# Runehold design

Runehold is a mod for Minecraft 1.21.1 (NeoForge). It changes the balance of the enchantments, with an anvil that has no level cap.

## State

The mod loads in the game. It has no function yet. All the sections after this one are **planned work**: they come from the open issues. The rules move from the module `balance` of Folkhold (#3), with the same behavior.

## Planned rules

- **Anvil with no level cap**: the anvil accepts a work of any level cost.
- **Books of the next level**: 2 books of the maximum level of an enchantment give a book of the next level.
- **Repair with no penalty**: a repair does not cost more for the work done on the item before it.

## Planned compatibility

Runehold has its own mod id, so the config file, the registry names and the saved data have new names. A world of Folkhold Balance must keep its items. To do this, the mod reads the old names (data fixer or aliases), or the release notes give the steps and say what is lost.
