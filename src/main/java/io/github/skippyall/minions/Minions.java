package io.github.skippyall.minions;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import io.github.skippyall.minions.command.MinionsCommand;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.minion.MinionPersistentState;
import io.github.skippyall.minions.minion.skin.SkinProviders;
import io.github.skippyall.minions.module.MinionModule;
import io.github.skippyall.minions.program.instruction.Instructions;
import io.github.skippyall.minions.program.supplier.ValueSuppliers;
import io.github.skippyall.minions.program.value.ValueTypes;
import io.github.skippyall.minions.util.PolymerUtil;
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
        MinionRegistries.register();

        Instructions.register();
        ValueSuppliers.register();
        ValueTypes.register();
        SkinProviders.register();
        GuiDisplay.register();

        MinionData.register();
        MinionModule.register();

        MinionBlocks.register();
        MinionItems.register();
        MinionCreativeTab.registerGroup();

        PolymerUtil.register();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
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

        PolymerResourcePackUtils.addModAssets(Minions.MOD_ID);
    }
}
