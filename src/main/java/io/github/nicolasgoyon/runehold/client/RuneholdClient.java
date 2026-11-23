package io.github.nicolasgoyon.runehold.client;

import io.github.nicolasgoyon.runehold.Runehold;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** Client entry point of Runehold. The game loads this class only on the client. */
@Mod(value = Runehold.MOD_ID, dist = Dist.CLIENT)
public final class RuneholdClient {
    public RuneholdClient(IEventBus modEventBus, ModContainer container) {
        // Config screen in the mod list. Server values are editable only in a world.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
