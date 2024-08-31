package io.github.skippyall.minions.client;

import io.github.skippyall.minions.networking.ClientToServerNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;

public class MinionsClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        //ClientConfigurationConnectionEvents.INIT.register(ClientToServerNetworking::onConfigurationInit);
    }
}
