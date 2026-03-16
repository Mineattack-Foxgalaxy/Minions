package io.github.skippyall.minions;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.skippyall.minions.command.MinionsCommand;
import io.github.skippyall.minions.docs.DocsManager;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.minion.MinionPersistentState;
import io.github.skippyall.minions.registration.MinionRegistration;
import io.github.skippyall.minions.polymer.VersionSync;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Minions implements ModInitializer {
    public static final String MOD_ID = "minions";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        MinionsConfig.get();

        MinionRegistration.register();

        VersionSync.register();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            MinionPersistentState.get(server).getMinionData().forEach((uuid, data) -> {
                if(data.isSpawned()) {
                    MinionFakePlayer.spawnMinion(data, server.getOverworld(), null, null, true);
                }
            });
        });

        CommandRegistrationCallback.EVENT.register(MinionsCommand::register);

        /*ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
            if(blockEntity instanceof MinionTriggerBlockEntity) {
                world.updateComparators(blockEntity.getPos(), MinionBlocks.MINION_TRIGGER_BLOCK);
            }
        });*/

        PolymerResourcePackUtils.addModAssets(Minions.MOD_ID);

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DocsManager());
    }
}
