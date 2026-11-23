package io.github.nicolasgoyon.runehold;

import com.mojang.logging.LogUtils;
import io.github.nicolasgoyon.runehold.anvil.AnvilRules;
import io.github.nicolasgoyon.runehold.config.RuneholdConfig;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

/** The Runehold mod: balance of the enchantments, with an anvil that has no level cap. */
@Mod(Runehold.MOD_ID)
public final class Runehold {
    /** The id of the mod. */
    public static final String MOD_ID = "runehold";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Runehold(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, RuneholdConfig.SPEC);
        modEventBus.addListener(AnvilRules::onConfigLoad);
        modEventBus.addListener(AnvilRules::onConfigReload);
        // Lowest priority: the anvil recipes of other mods set their output first.
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, AnvilRules::onAnvilUpdate);
        LOGGER.debug("{} {} starts", MOD_ID, container.getModInfo().getVersion());
    }
}
