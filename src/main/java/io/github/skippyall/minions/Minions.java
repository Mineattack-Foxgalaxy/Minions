package io.github.skippyall.minions;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import io.github.skippyall.minions.command.MinionsCommand;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.minion.MinionPersistentState;
import io.github.skippyall.minions.mixins.PlayerListEntryS2CPacket$EntryMixin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
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
            MinionPersistentState.create(server);
            MinionPersistentState.INSTANCE.getMinionData().forEach((uuid, data) -> {
                if(data.isSpawned()) {
                    MinionFakePlayer.spawnMinion(data, server.getOverworld(), null, null, true);
                }
            });
        });
        PlayerEntity
        ClientPlayNetworkHandler

        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            MinionsCommand.register(commandDispatcher);
        });

        MinionItems.register();
        MinionCreativeTab.registerGroup();
    }
}
