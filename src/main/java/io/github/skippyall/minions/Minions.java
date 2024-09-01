package io.github.skippyall.minions;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.minion.MinionItem;
import io.github.skippyall.minions.minion.MinionPersistentState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Minions implements ModInitializer {
    public static final String MOD_ID = "minions";
    public static final MinionItem MINION_ITEM = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "minion"), new MinionItem(false));

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final List<Runnable> executeOnNextTick = new ArrayList<>();

    @Override
    public void onInitialize() {
        LOGGER.debug("Add Customthing");
        PolymerEntityUtils.registerType();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            MinionPersistentState.create(server);
            MinionPersistentState.INSTANCE.getMinionData().forEach(data -> {
                System.out.println("spawn Minion " + data.name);
                MinionFakePlayer.spawnMinionAt(data, server.getOverworld(), null, null);
            });
        });
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            exec(() -> {
                for (Runnable run:executeOnNextTick) {
                    run.run();
                }
                executeOnNextTick.clear();
            });
        });
    }

    private static synchronized void exec(Runnable run) {
        run.run();
    }

    public static void addExecuteOnNextTick(Runnable run) {
        exec(() -> {
            executeOnNextTick.add(run);
        });
    }
}
