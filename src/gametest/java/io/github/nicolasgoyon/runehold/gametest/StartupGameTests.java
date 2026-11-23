package io.github.nicolasgoyon.runehold.gametest;

import io.github.nicolasgoyon.runehold.Runehold;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** In-game tests for the start of the mod. */
@GameTestHolder(Runehold.MOD_ID)
@PrefixGameTestTemplate(false)
public final class StartupGameTests {
    private StartupGameTests() {
    }

    /** The server loads the mod. */
    @GameTest(template = "empty", timeoutTicks = 20)
    public static void modIsLoaded(GameTestHelper helper) {
        helper.assertTrue(ModList.get().isLoaded(Runehold.MOD_ID), "The mod must be loaded");
        helper.succeed();
    }
}
