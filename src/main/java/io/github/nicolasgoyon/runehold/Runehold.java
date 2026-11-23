package io.github.nicolasgoyon.runehold;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/** The Runehold mod: balance of the enchantments, with an anvil that has no level cap. */
@Mod(Runehold.MOD_ID)
public final class Runehold {
    /** The id of the mod. */
    public static final String MOD_ID = "runehold";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Runehold(IEventBus modEventBus, ModContainer container) {
        LOGGER.debug("{} {} starts", MOD_ID, container.getModInfo().getVersion());
    }
}
