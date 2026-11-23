package io.github.nicolasgoyon.runehold.gametest;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestListener;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Tools that the GameTests of Runehold share.
 */
final class TestSupport {
    /** The config values that each running test changed. The GameTests run on the server thread only. */
    private static final Map<GameTestInfo, Set<Object>> CHANGED_SETTINGS = new WeakHashMap<>();

    private TestSupport() {
    }

    /**
     * Sets a config value for one test. When the test ends (pass, failure or timeout), a listener sets the value that
     * the config had before the first change of the test: the next tests see this value also after a failure. Only
     * the first change of a value in a test adds a listener, so a test can change the same value more than one time.
     * The listeners of a test run in their order: a second listener for the same value would set the value of the
     * first change again.
     */
    static <T> void setConfigForTest(GameTestHelper helper, ModConfigSpec.ConfigValue<T> value, T newValue) {
        if (CHANGED_SETTINGS.computeIfAbsent(helper.testInfo, info -> new HashSet<>()).add(value)) {
            T old = value.get();
            helper.testInfo.addListener(new GameTestListener() {
                @Override
                public void testStructureLoaded(GameTestInfo info) {
                }

                @Override
                public void testPassed(GameTestInfo info, GameTestRunner runner) {
                    restore();
                }

                @Override
                public void testFailed(GameTestInfo info, GameTestRunner runner) {
                    restore();
                }

                @Override
                public void testAddedForRerun(GameTestInfo info, GameTestInfo rerun, GameTestRunner runner) {
                }

                private void restore() {
                    value.set(old);
                    CHANGED_SETTINGS.remove(helper.testInfo);
                }
            });
        }
        value.set(newValue);
    }

    /**
     * A mock player in survival mode at a position relative to the test. A test can remove it with
     * {@code getPlayerList().remove(player)}. At the end of the test, also after a failure, a listener removes the
     * player and its operator entry.
     */
    static ServerPlayer survivalPlayer(GameTestHelper helper, Vec3 position) {
        ServerLevel level = helper.getLevel();
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(
                new GameProfile(UUID.randomUUID(), "test-mock-player"), false);
        ServerPlayer player = new ServerPlayer(level.getServer(), level, cookie.gameProfile(),
                cookie.clientInformation());
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        level.getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
        // The default game mode of the GameTest server is creative.
        player.setGameMode(GameType.SURVIVAL);
        Vec3 absolute = helper.absoluteVec(position);
        player.moveTo(absolute.x, absolute.y, absolute.z);
        logOutAtEnd(helper, player);
        return player;
    }

    /**
     * Removes a mock player and its operator entry when the test passes or fails. A failed test does not get to its
     * own logout: without this listener, the player stays online for the next tests. The listener removes only this
     * player: a player of another test can have the same id.
     */
    private static void logOutAtEnd(GameTestHelper helper, ServerPlayer player) {
        helper.testInfo.addListener(new GameTestListener() {
            @Override
            public void testStructureLoaded(GameTestInfo info) {
            }

            @Override
            public void testPassed(GameTestInfo info, GameTestRunner runner) {
                logOut(player);
            }

            @Override
            public void testFailed(GameTestInfo info, GameTestRunner runner) {
                logOut(player);
            }

            @Override
            public void testAddedForRerun(GameTestInfo info, GameTestInfo rerun, GameTestRunner runner) {
            }
        });
    }

    private static void logOut(ServerPlayer player) {
        PlayerList players = player.server.getPlayerList();
        ServerPlayer online = players.getPlayer(player.getUUID());
        if (online != null && online != player) {
            return;
        }
        if (players.getOps().get(player.getGameProfile()) != null) {
            players.getOps().remove(player.getGameProfile());
        }
        if (online == player) {
            players.remove(player);
        }
    }
}
