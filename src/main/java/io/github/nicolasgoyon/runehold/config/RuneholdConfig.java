package io.github.nicolasgoyon.runehold.config;

import io.github.nicolasgoyon.runehold.anvil.KeyedValues;
import io.github.nicolasgoyon.runehold.anvil.MergeRule;
import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Server configuration of Runehold: file {@code runehold-server.toml} in the {@code serverconfig} folder of each
 * world.
 */
public final class RuneholdConfig {
    static final String KEY_PREFIX = "runehold.configuration.";

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue ANVIL_ENABLED;
    public static final ModConfigSpec.EnumValue<MergeRule> ANVIL_MERGE_RULE;
    public static final ModConfigSpec.DoubleValue ANVIL_ENCHANT_COST_FACTOR;
    public static final ModConfigSpec.IntValue ANVIL_OPERATION_COST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ANVIL_WEIGHTS;
    public static final ModConfigSpec.DoubleValue ANVIL_REPAIR_ENCHANT_FACTOR;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ANVIL_REPAIR_BASE_COST;
    public static final ModConfigSpec.IntValue ANVIL_REPAIR_DEFAULT_BASE_COST;
    public static final ModConfigSpec.BooleanValue ANVIL_PRIOR_WORK_PENALTY;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ANVIL_LEVEL_CAPS;
    public static final ModConfigSpec.IntValue ANVIL_ABSOLUTE_MAX_LEVEL;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Anvil: no level cap, no \"Too Expensive!\" limit, an exponential enchantment cost and a repair",
                        "cost with no prior work penalty. All costs are in levels.")
                .translation(KEY_PREFIX + "anvil")
                .push("anvil");
        ANVIL_ENABLED = builder
                .comment("Use the anvil rules of this section. False gives the vanilla anvil.")
                .translation(KEY_PREFIX + "anvilEnabled")
                .define("enabled", true);
        ANVIL_MERGE_RULE = builder
                .comment("Level of an enchantment when the anvil combines two levels. VANILLA: two equal levels give",
                        "the level + 1, two different levels give the higher level. SUM: the levels add together.",
                        "There is no cap at the max level of the enchantment: see levelCaps.")
                .translation(KEY_PREFIX + "mergeRule")
                .defineEnum("mergeRule", MergeRule.VANILLA);
        ANVIL_ENCHANT_COST_FACTOR = builder
                .comment("Factor k of the enchantment cost: operationCost + ceil(k × sum of weight × (2^b - 2^a)).",
                        "a is the level on the left item (0 if none), b is the level of the result. Level 0 has the",
                        "value 0. Each level costs double the level before it. With k = 0.5: Efficiency V costs 17,",
                        "Efficiency VI on Efficiency V costs 17, Efficiency VIII on Efficiency VII costs 65.",
                        "A cost above 32,767 levels gives no result.")
                .translation(KEY_PREFIX + "enchantCostFactor")
                .defineInRange("enchantCostFactor", 0.5, 0.0, 1000.0);
        ANVIL_OPERATION_COST = builder
                .comment("Fixed cost of an operation that raises the level of an enchantment.")
                .translation(KEY_PREFIX + "operationCost")
                .defineInRange("operationCost", 1, 0, 1000);
        ANVIL_WEIGHTS = builder
                .comment("Weight of an enchantment, as \"id=weight\" or \"#tag=weight\". An enchantment that is not",
                        "in the list uses its anvil_cost.")
                .translation(KEY_PREFIX + "weights")
                .defineListAllowEmpty("weights", List.of(), () -> "minecraft:efficiency=1",
                        KeyedValues::isValidEntry);
        ANVIL_REPAIR_ENCHANT_FACTOR = builder
                .comment("Factor r of the repair cost: base + ceil(r × sum of weight × level) for a full repair. Curses",
                        "do not count. A repair of a quarter of the durability costs a quarter of a full repair,",
                        "rounded up.")
                .translation(KEY_PREFIX + "repairEnchantFactor")
                .defineInRange("repairEnchantFactor", 1.0, 0.0, 1000.0);
        ANVIL_REPAIR_BASE_COST = builder
                .comment("Base cost of a repair, as \"material=cost\" or \"#material_tag=cost\". The first material",
                        "that repairs the item gives the base, ids before tags.")
                .translation(KEY_PREFIX + "repairBaseCost")
                .defineListAllowEmpty("repairBaseCost", List.of("#minecraft:planks=1", "minecraft:leather=1",
                        "#minecraft:stone_tool_materials=2", "minecraft:gold_ingot=2", "minecraft:iron_ingot=3",
                        "minecraft:turtle_scute=3", "minecraft:diamond=4", "minecraft:netherite_ingot=5"),
                        () -> "minecraft:iron_ingot=3", KeyedValues::isValidEntry);
        ANVIL_REPAIR_DEFAULT_BASE_COST = builder
                .comment("Base cost of a repair for an item with no material in repairBaseCost.")
                .translation(KEY_PREFIX + "repairDefaultBaseCost")
                .defineInRange("repairDefaultBaseCost", 3, 0, 1000);
        ANVIL_PRIOR_WORK_PENALTY = builder
                .comment("Add the vanilla prior work penalty to the cost. False: the result has no penalty.")
                .translation(KEY_PREFIX + "priorWorkPenalty")
                .define("priorWorkPenalty", false);
        ANVIL_LEVEL_CAPS = builder
                .comment("Largest level of an enchantment in the anvil, as \"id=level\" or \"#tag=level\".",
                        "An enchantment that is not in the list has no cap below absoluteMaxLevel.")
                .translation(KEY_PREFIX + "levelCaps")
                .defineListAllowEmpty("levelCaps", List.of(), () -> "minecraft:fortune=5", KeyedValues::isValidEntry);
        ANVIL_ABSOLUTE_MAX_LEVEL = builder
                .comment("Largest level of all enchantments in the anvil. The game stores a maximum of 255.")
                .translation(KEY_PREFIX + "absoluteMaxLevel")
                .defineInRange("absoluteMaxLevel", 255, 1, 255);
        builder.pop();

        SPEC = builder.build();
    }

    private RuneholdConfig() {
    }
}
