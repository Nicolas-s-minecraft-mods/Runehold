# Runehold design

Runehold is a mod for Minecraft 1.21.1 (NeoForge). It changes the balance of the enchantments, with an anvil that has no level cap. The mod needs no other mod.

## Anvil

Classes: `AnvilRules`, `AnvilCost`, `KeyedValues`, `MergeRule`.

- No level cap and no "Too Expensive!" limit. An enchantment level such as Efficiency 20 is possible, but very expensive. All costs are in levels.
- **Merge**: the vanilla rule with no max level. Two equal levels give the level + 1, two different levels give the higher level. Thus 2 books of the max level give a book of the next level. Efficiency 20 needs 2¹⁵ books of Efficiency V.
- The config key `levelCaps` can set a cap for each enchantment. By default there is no cap. The game stores a maximum level of 255.
- The **enchantment cost** is exponential in the level of the result: `operationCost + ceil(k × Σ w × (2^b − 2^a))`. a is the level on the left item (0 if none), and b is the level of the result. w is the weight of the enchantment: its `anvil_cost`, or the config key `weights`.
- Level 0 has the value 0, not 2⁰. Each level costs double the level before it, as the vanilla prior work penalty. The default k is 0.5: the vanilla max levels stay near the vanilla costs, and each level above the vanilla max is much more expensive.
- A book is not cheaper than an item. A level built in steps is never cheaper than the same level in one operation. Each additional step costs one more `operationCost`, and the rounding up of each step can add 1 level.
- The **repair cost** is separate. A full repair costs `base + ceil(r × Σ w × level)` for the current enchantments of the item, curses excluded. The base comes from the repair material.
- A repair with n units of material costs `ceil(full × min(n, 4) / 4)`. The anvil uses the material units of the vanilla loop: each unit repairs a quarter of the maximum durability, rounded down. Thus an item can use a fifth unit when its maximum durability is not a multiple of 4. An example is an iron pickaxe at 249 damage of 250.
- A repair with a second item of the same type counts the repaired durability in quarters, rounded up. The vanilla durability rule stays (sum of the two items + 12 %).
- There is **no prior work penalty**: the result has a `repair_cost` of 0, and the anvil ignores the `repair_cost` of the inputs. A repair of the same item thus always has the same cost.
- A rename adds 1 level. Each incompatible enchantment adds 1 level. A book with only incompatible enchantments gives no result. An operation that changes nothing gives no result. The anvil still takes damage.
- The anvil does not enchant a stack of more than one item. A cost above 32,767 levels gives no result: the menu sends the cost to the client as a short.
- The client shows the cost in place of "Too Expensive!" (red when the player has too few levels). It shows a level with no translation as a number, for example "Efficiency 20".
- The anvil recipes of other mods have priority: the listener of Runehold runs last, and does nothing when another mod sets an output.
- With `enabled = false`, the anvil is vanilla, and the levels above X have no number.
- The listener runs on the server and on the client: the menu calculates its result on the two sides.

Example with the default factors (k = 0.5, r = 1) and the weights of the vanilla `anvil_cost`. These weights are: Efficiency, Sharpness and Protection 1, Unbreaking 2, Fortune, Looting and Mending 4, Thorns 8. The vanilla column is a book on an item with no prior work penalty. The vanilla anvil also adds 1, 3, 7, 15... levels for 1, 2, 3, 4... earlier operations on each input. It shows "Too Expensive!" at 40 levels or more.

| Action | Cost (levels) | Vanilla |
| --- | --- | --- |
| Book Efficiency V or Sharpness V on an item with no enchantments | 17 | 5 |
| Book Protection IV / Unbreaking III / Fortune III / Mending | 9 / 9 / 17 / 5 | 4 / 3 / 6 / 2 |
| Book Thorns III | 33 | 12 |
| Two books Efficiency IV, Unbreaking II or Protection III (the level + 1) | 9 / 5 / 5 | 5 / 3 / 4 |
| One book Efficiency V + Unbreaking III + Fortune III + Mending on a pickaxe | 45 | 16 |
| One book Protection IV + Unbreaking III + Mending on a chestplate | 21 | 9 |
| Book Efficiency III / V on Efficiency II | 3 / 15 | 3 / 5 |
| Efficiency VI / VII / VIII / X, from the level before it | 17 / 33 / 65 / 257 | not possible |
| Book Efficiency X on Efficiency V / five steps V + V, VI + VI, ... | 497 / 501 | not possible |
| Efficiency XV from no enchantment / Efficiency XVI | 16,385 / no result (32,769) | not possible |
| Book Unbreaking IV / V on Unbreaking III | 9 / 25 | not possible |
| Full repair: Eff II + Unb III + Mending / Eff II + Unb III | 16 / 12 |
| Full repair: Eff V + Unb III / Eff X + Unb III / Eff XX + Unb III | 15 / 20 / 30 |
| Iron pickaxe, full repair: Eff II / Eff II + Unb III | 5 / 11 |

## Configuration

Section `anvil` of `runehold-server.toml`, in the `serverconfig` folder of each world. The config screen of the mod list shows the same values, in English and in French.

| Value | Default | Meaning |
| --- | --- | --- |
| `enabled` | true | Use these rules. False gives the vanilla anvil |
| `mergeRule` | `VANILLA` | `VANILLA` or `SUM` (the levels add together) |
| `enchantCostFactor` | 0.5 | Factor k of the enchantment cost |
| `operationCost` | 1 | Cost of each operation that raises a level |
| `weights` | `[]` | `"id=weight"` or `"#tag=weight"`. Other enchantments use their `anvil_cost` |
| `repairEnchantFactor` | 1.0 | Factor r of the repair cost |
| `repairBaseCost` | planks 1, leather 1, stone tool materials 2, gold 2, iron 3, turtle scute 3, diamond 4, netherite 5 | `"material=cost"` or `"#tag=cost"`. Chainmail uses iron |
| `repairDefaultBaseCost` | 3 | Base for an item with no material in `repairBaseCost` |
| `priorWorkPenalty` | false | Add the vanilla prior work penalty |
| `levelCaps` | `[]` | `"id=level"` or `"#tag=level"` |
| `absoluteMaxLevel` | 255 | Largest level of all enchantments |

## Worlds of Folkhold Balance

Runehold has no items, no entities and no saved data. A world of Folkhold Balance loses nothing. The enchantments of high levels and the `repair_cost` of the items stay on the items. The rules have the same section and the same values, in a config file with a new name. To keep changed values on a server:

1. Stop the server.
2. Open `serverconfig/folkhold_balance-server.toml` of the world.
3. Copy the values of the section `[anvil]` to the same section of `serverconfig/runehold-server.toml`. If this file does not exist, start the server one time with Runehold to make it.
4. Start the server.

Do not use Runehold and the anvil rules of Folkhold Balance in the same game. Only one of the two listeners then sets the result of the anvil.

## Tests

- JUnit (`src/test`): the costs (`AnvilCostTest`), the config entries `id=value` (`KeyedValuesTest`) and the merge rules (`MergeRuleTest`).
- GameTest (`src/gametest`, template `runehold:bench`): `AnvilGameTests`. A mock player uses an anvil menu and takes the result as a click does. The tests check the costs, the levels above the vanilla max, the repairs, the rename and the limits. They also check the recipes of other mods and the vanilla anvil with `enabled = false`.
