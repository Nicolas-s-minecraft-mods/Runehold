# Contributing

## Language

- Write all code, comments, documentation, commit messages, issues and pull requests in English.
- Use the style of ASD-STE100 (Simplified Technical English): short sentences, active voice, one instruction for each sentence, no semicolons.
- Documentation and comments describe the current state of the code and of the project. Do not refer to earlier versions, to the origin of a document or to the reason for a change. Put this information in commit messages and pull requests.
- The mod supports English (`en_us`) and French (`fr_fr`). Each text that the player sees has a key in the two files of `src/main/resources/assets/runehold/lang/`. The build fails when a key is missing in one file (`checkTranslations`).

## Workflow

1. Open an issue, or select an existing issue.
2. Create a branch from `develop`. Use the format `<type>/<issue>-<short-name>`, for example `feat/3-move-anvil-rules`. Types: `feat`, `fix`, `docs`, `build`, `ci`, `refactor`, `test`, `chore`.
3. Make small commits. Use [Conventional Commits](https://www.conventionalcommits.org/), for example `feat(anvil): remove the level cap of the anvil`. In a pull request with one commit, put the bump marker in the title of the commit too. GitHub can use this title for the squash. Scopes: `anvil`, `books`, `repair`, `config`, `ci`, `docs`.
4. Open a pull request to `develop`. Write `Closes #<issue>` in the description. The title follows Conventional Commits and ends with a bump marker (see [Versions and releases](#versions-and-releases)), for example `feat(anvil): remove the level cap of the anvil [minor]`.
5. The CI must pass before the merge. The check **Title and version** refuses a title with no bump marker, and its summary gives the version after the merge.
6. Use "Squash and merge": the title of the pull request becomes the title of the commit on `develop`. Delete the branch after the merge.
7. A pull request that changes a function of the mod also updates `docs/design.md`.

## Issues and labels

- The type labels: `bug`, `type: feature`, `type: docs`, `type: chore`, `type: ci` and `type: design`.
- The size labels come from the `Estimate:` line of the issue: `size: S` (1 hour), `size: M` (2 hours), `size: L` (4 hours) and `size: XL` (6 hours or more).
- The label `needs discussion` marks an issue that needs a decision of the owner before the work starts.

## Build and tests

- `./gradlew build`: compiles, checks the format (Spotless) and the translations, runs Error Prone and the unit tests (JUnit).
- `./gradlew spotlessApply`: fixes the format of the files.
- `./gradlew runGameTestServer`: runs the in-game tests (GameTest) in a new world, then stops.
- `./gradlew runClient` and `./gradlew runServer`: a client and a dedicated server with the mod.
- The pure logic (level costs, book levels) has JUnit tests. The behavior in the game has GameTests.

## Versions and releases

- `develop` gets the pull requests. `main` gets only the merges of `develop`, and each merge makes a release.
- Each commit of `develop` has a bump marker at the end of its title:
  - `[patch]`: a fix or a small change. 1.2.3 becomes 1.2.4.
  - `[minor]`: a new feature. 1.2.3 becomes 1.3.0.
  - `[major]`: a change that breaks the worlds or the API. 1.2.3 becomes 2.0.0.
- The version starts from the last release tag (`vX.Y.Z`). The commits after the tag apply their bump in order, the oldest first. A commit with no marker keeps the version. A commit of Dependabot counts as `[patch]`.
- `python3 scripts/version.py version --rev <commit>` gives the version of a commit.
- The workflow **Build** has one job for each task. The jobs are: the version, the tests of the scripts, the build (compilation, format and translations), the unit tests and the GameTests.
- The workflow **Build** gives each build a version. A build of `develop` has the version of the commit with `-dev.<commit>`. A pull request has the version after the merge with `-pr.<number>.<commit>`.
- The workflow **Release** runs on each push on `main`. It tags the commit of `develop` with `vX.Y.Z`, creates a GitHub Release with the jar and the release notes, and publishes the jars to GitHub Packages. When `gradle.properties` has `curseforge_project_id`, the workflow also publishes the jar to CurseForge, with the secret `CURSEFORGE_TOKEN` (an API token of CurseForge). With an empty value, it skips CurseForge. If the tag exists, it makes no release.
- The workflow **CurseForge** ("Run workflow" with a version) sends an existing GitHub Release to CurseForge.
- To make a release, open a pull request from `develop` to `main`. Merge it with a merge commit (not a squash), so that `develop` contains the tag.

## Release notes

The release notes come from the titles of the commits on `develop`: each title with a bump marker gives one line, with its version. Thus the title of a pull request must tell the change in words that a player understands.
