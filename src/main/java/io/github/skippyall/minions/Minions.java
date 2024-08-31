package io.github.skippyall.minions;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import io.github.skippyall.minions.minion.MinionItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Minions implements ModInitializer {
    public static final String MOD_ID = "minions";
    public static final MinionItem MINION_ITEM = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "minion"), new MinionItem(false));

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        PolymerEntityUtils.registerType();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {

        });
    }
}
