# Runehold

<img src="docs/runehold_logo.png" alt="Logo of Runehold" width="128">

NeoForge mod: balance of the enchantments, with an anvil that has no level cap.

[![Build](https://img.shields.io/github/actions/workflow/status/Nicolas-s-minecraft-mods/Runehold/build.yml?branch=develop&label=build)](https://github.com/Nicolas-s-minecraft-mods/Runehold/actions/workflows/build.yml)
[![CurseForge](https://img.shields.io/curseforge/dt/1825645?logo=curseforge&label=curseforge)](https://www.curseforge.com/projects/1825645)
![Minecraft 1.21.1](https://img.shields.io/badge/Minecraft-1.21.1-62B47A)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1-E68C37)
[![License: MIT](https://img.shields.io/github/license/Nicolas-s-minecraft-mods/Runehold)](LICENSE)

## State

The anvil has no level cap and no "Too Expensive!" limit:

- 2 books of the maximum level give a book of the next level. Each level costs double the level before it.
- A repair has no penalty for the work before it. A repair of the same item always has the same cost.
- The client shows the cost of the anvil and the levels above X as numbers, for example "Efficiency 20".

The mod needs no other mod. The rules and the configuration are in [docs/design.md](docs/design.md).

## Worlds of Folkhold Balance

A world of Folkhold Balance can use Runehold for the anvil rules. The world loses nothing. The config file has a new name (`runehold-server.toml`). To keep changed values, copy the section `[anvil]` to the new file. The steps are in [docs/design.md](docs/design.md#worlds-of-folkhold-balance).

## Contribute

Read [CONTRIBUTING.md](CONTRIBUTING.md) for the workflow, the build, the tests and the versions.

## License

[MIT](LICENSE)
