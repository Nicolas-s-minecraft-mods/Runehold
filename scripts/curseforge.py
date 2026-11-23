#!/usr/bin/env python3
"""Publish Runehold to CurseForge.

    CURSEFORGE_TOKEN=... python3 scripts/curseforge.py publish --version 1.2.3 --notes notes.md

The project comes from curseforge_project_id in gradle.properties. With no project (an empty value), the script
publishes nothing and stops with no error: the release goes on without CurseForge.

`publish` sends the jar of build/libs with the release notes (Markdown). The game versions come from the API of
CurseForge: the Minecraft version of gradle.properties, the loader NeoForge, and the environments Client and Server.
`--type` selects the release type: `release`, `beta` or `alpha`. The token is an API token of CurseForge
(https://legacy.curseforge.com/account/api-tokens).
"""
import argparse
import json
import os
import pathlib
import sys
import urllib.error
import urllib.request
import uuid

API = "https://minecraft.curseforge.com/api"
USER_AGENT = "Nicolas-s-minecraft-mods/Runehold (release workflow)"
TYPES = ("release", "beta", "alpha")
LOADER = "NeoForge"
ENVIRONMENTS = ("Client", "Server")


def properties(path):
    """The properties of a gradle.properties file."""
    values = {}
    for line in pathlib.Path(path).read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if line and not line.startswith("#") and "=" in line:
            key, value = line.split("=", 1)
            values[key.strip()] = value.strip()
    return values


def game_versions(versions, types, minecraft):
    """The IDs of the game versions of a file: the Minecraft version, the loader and the environments.

    versions: the list of /api/game/versions (id, gameVersionTypeID, name).
    types: the list of /api/game/version-types (id, slug).
    """
    slugs = {kind["id"]: kind["slug"] for kind in types}
    ids = []
    for version in versions:
        slug = slugs.get(version["gameVersionTypeID"], "")
        name = version["name"]
        if (slug.startswith("minecraft-") and name == minecraft) or (slug == "modloader" and name == LOADER) \
                or (slug == "environment" and name in ENVIRONMENTS):
            ids.append(version["id"])
    found = {version["name"] for version in versions if version["id"] in ids}
    missing = [name for name in (minecraft, LOADER) if name not in found]
    if missing:
        raise ValueError(f"CurseForge has no game version {', '.join(missing)}")
    return sorted(set(ids))


def metadata(version, notes, props, ids, release_type="release"):
    """The JSON of the metadata of a new file."""
    return {
        "changelog": notes,
        "changelogType": "markdown",
        "displayName": f"{props['mod_name']} {version}",
        "gameVersions": ids,
        "releaseType": release_type,
    }


def multipart(boundary, data, path):
    """The body of a multipart request with the metadata and the file."""
    body = bytearray()
    prefix = f"--{boundary}\r\n".encode()
    body += prefix
    body += b'Content-Disposition: form-data; name="metadata"\r\nContent-Type: application/json\r\n\r\n'
    body += json.dumps(data).encode() + b"\r\n"
    body += prefix
    body += (f'Content-Disposition: form-data; name="file"; filename="{path.name}"\r\n'
             "Content-Type: application/java-archive\r\n\r\n").encode()
    body += path.read_bytes() + b"\r\n"
    body += f"--{boundary}--\r\n".encode()
    return bytes(body)


def get(path, token):
    request = urllib.request.Request(f"{API}{path}", headers={"X-Api-Token": token, "User-Agent": USER_AGENT})
    with urllib.request.urlopen(request) as response:
        return json.loads(response.read().decode())


def publish(args, project, token):
    """Sends a new file."""
    props = properties("gradle.properties")
    path = pathlib.Path(args.libs) / f"{props['mod_id']}-{args.version}.jar"
    if not path.is_file():
        print(f"No file: {path}", file=sys.stderr)
        return 1
    try:
        ids = game_versions(get("/game/versions", token), get("/game/version-types", token),
                            props["minecraft_version"])
    except urllib.error.HTTPError as error:
        print(f"CurseForge refused the game versions: {error.code} {error.read().decode()}", file=sys.stderr)
        return 1
    except ValueError as error:
        print(error, file=sys.stderr)
        return 1
    notes = pathlib.Path(args.notes).read_text(encoding="utf-8")
    boundary = uuid.uuid4().hex
    data = metadata(args.version, notes, props, ids, args.type)
    request = urllib.request.Request(f"{API}/projects/{project}/upload-file", data=multipart(boundary, data, path),
                                     method="POST", headers={
                                         "X-Api-Token": token,
                                         "Content-Type": f"multipart/form-data; boundary={boundary}",
                                         "User-Agent": USER_AGENT,
                                     })
    try:
        with urllib.request.urlopen(request) as response:
            file_id = json.loads(response.read().decode()).get("id")
    except urllib.error.HTTPError as error:
        print(f"CurseForge refused {request.full_url}: {error.code} {error.read().decode()}", file=sys.stderr)
        return 1
    print(f"CurseForge has the new file {file_id}: {args.version} ({args.type}).")
    return 0


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    commands = parser.add_subparsers(dest="command", required=True)
    version = commands.add_parser("publish", help="send a new file")
    version.add_argument("--version", required=True, help="the version")
    version.add_argument("--notes", required=True, help="the file of the notes of the version")
    version.add_argument("--type", choices=TYPES, default="release", help="the release type")
    version.add_argument("--libs", default="build/libs", help="the folder of the jars")
    version.set_defaults(run=publish)
    args = parser.parse_args(argv)
    project = properties("gradle.properties").get("curseforge_project_id", "")
    if not project:
        print("No curseforge_project_id in gradle.properties: no publication to CurseForge.")
        return 0
    token = os.environ.get("CURSEFORGE_TOKEN")
    if not token:
        print("No CURSEFORGE_TOKEN: the publication to CurseForge needs an API token.", file=sys.stderr)
        return 1
    return args.run(args, project, token)


if __name__ == "__main__":
    sys.exit(main())
