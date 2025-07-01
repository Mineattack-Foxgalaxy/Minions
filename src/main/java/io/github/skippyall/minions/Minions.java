package io.github.skippyall.minions;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import io.github.skippyall.minions.command.MinionsCommand;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.minion.MinionPersistentState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Minions implements ModInitializer {
    public static final String MOD_ID = "minions";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        MinionData.register();
        PolymerEntityUtils.registerType();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.error("Initializing Minion data");
            MinionPersistentState.create(server);
            MinionPersistentState.INSTANCE.getMinionData().forEach((uuid, data) -> {
                if(data.isSpawned()) {
                    MinionFakePlayer.spawnMinion(data, server.getOverworld(), null, null, true);
                }
            });
        });

        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            MinionsCommand.register(commandDispatcher);
        });

        MinionItems.register();
        MinionCreativeTab.registerGroup();
    }
}
