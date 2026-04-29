package io.github.skippyall.minions.client;

import eu.pb4.polymer.networking.api.client.PolymerClientNetworking;
import io.github.skippyall.minions.polymer.VersionSync;
import net.fabricmc.api.ClientModInitializer;

public class MinionsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PolymerClientNetworking.registerCommonHandler(VersionSync.VersionSyncPayload.class, (client, handler, payload) -> {});
    }
}
