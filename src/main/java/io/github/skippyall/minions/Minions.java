package io.github.skippyall.minions;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.skippyall.minions.command.MinionsCommand;
import io.github.skippyall.minions.docs.DocsManager;
import io.github.skippyall.minions.minion.MinionPersistentState;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.polymer.VersionSync;
import io.github.skippyall.minions.registration.MinionRegistration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
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
                    MinionFakePlayer.spawnMinion(data, server.overworld(), null, null, true);
                }
            });
        });

        CommandRegistrationCallback.EVENT.register(MinionsCommand::register);

        PolymerResourcePackUtils.addModAssets(Minions.MOD_ID);

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new DocsManager());
    }
}
