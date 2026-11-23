"""Tests of the publication to CurseForge: python3 -m unittest discover -s scripts -p 'test_*.py'."""
import json
import os
import pathlib
import tempfile
import unittest
from unittest import mock

import curseforge

TYPES = [{"id": 1, "slug": "minecraft-1-21"}, {"id": 2, "slug": "modloader"}, {"id": 3, "slug": "environment"},
         {"id": 4, "slug": "java"}, {"id": 5, "slug": "minecraft-1-20"}]
VERSIONS = [{"id": 10, "gameVersionTypeID": 1, "name": "1.21.1"}, {"id": 11, "gameVersionTypeID": 1, "name": "1.21"},
            {"id": 20, "gameVersionTypeID": 2, "name": "NeoForge"}, {"id": 21, "gameVersionTypeID": 2, "name": "Forge"},
            {"id": 30, "gameVersionTypeID": 3, "name": "Client"}, {"id": 31, "gameVersionTypeID": 3, "name": "Server"},
            {"id": 40, "gameVersionTypeID": 4, "name": "Java 21"},
            {"id": 50, "gameVersionTypeID": 5, "name": "1.20.1"}]


class CurseForgeTest(unittest.TestCase):
    def test_game_versions_are_the_minecraft_version_the_loader_and_the_environments(self):
        self.assertEqual([10, 20, 30, 31], curseforge.game_versions(VERSIONS, TYPES, "1.21.1"))

    def test_an_unknown_minecraft_version_is_an_error(self):
        with self.assertRaises(ValueError):
            curseforge.game_versions(VERSIONS, TYPES, "1.99")

    def test_metadata_has_the_notes_and_the_versions(self):
        data = curseforge.metadata("1.2.3", "Notes", {"mod_name": "Runehold"}, [10, 20], "beta")

        self.assertEqual("Runehold 1.2.3", data["displayName"])
        self.assertEqual("markdown", data["changelogType"])
        self.assertEqual([10, 20], data["gameVersions"])
        self.assertEqual("beta", data["releaseType"])

    def test_multipart_has_the_metadata_and_the_file(self):
        with tempfile.TemporaryDirectory() as folder:
            path = pathlib.Path(folder) / "runehold-1.2.3.jar"
            path.write_bytes(b"JAR")

            body = curseforge.multipart("b", {"a": 1}, path)

        self.assertIn(b'name="metadata"', body)
        self.assertIn(json.dumps({"a": 1}).encode(), body)
        self.assertIn(b'filename="runehold-1.2.3.jar"', body)
        self.assertTrue(body.endswith(b"--b--\r\n"))

    def test_no_project_publishes_nothing(self):
        with tempfile.TemporaryDirectory() as folder:
            pathlib.Path(folder, "gradle.properties").write_text("mod_id=runehold\ncurseforge_project_id=\n")
            cwd = os.getcwd()
            os.chdir(folder)
            try:
                with mock.patch.object(curseforge, "publish") as publish:
                    result = curseforge.main(["publish", "--version", "1.2.3", "--notes", "notes.md"])
            finally:
                os.chdir(cwd)

        self.assertEqual(0, result)
        publish.assert_not_called()

    def test_a_project_with_no_token_is_an_error(self):
        with tempfile.TemporaryDirectory() as folder:
            pathlib.Path(folder, "gradle.properties").write_text("curseforge_project_id=123\n")
            cwd = os.getcwd()
            os.chdir(folder)
            try:
                with mock.patch.dict(os.environ, {}, clear=True):
                    result = curseforge.main(["publish", "--version", "1.2.3", "--notes", "notes.md"])
            finally:
                os.chdir(cwd)

        self.assertEqual(1, result)


if __name__ == "__main__":
    unittest.main()
