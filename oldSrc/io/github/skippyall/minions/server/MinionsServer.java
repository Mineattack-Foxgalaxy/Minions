package io.github.skippyall.minions.server;

import io.github.skippyall.minions.networking.ClientToServerNetworking;
import io.github.skippyall.minions.networking.VersionChecker;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

public class MinionsServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerConfigurationNetworking.registerGlobalReceiver(ClientToServerNetworking.RL, ClientToServerNetworking::receive);
        ServerConfigurationConnectionEvents.CONFIGURE.register(new ServerConfigurationConnectionEvents.Configure() {
            @Override
            public void onSendConfiguration(ServerConfigurationPacketListenerImpl handler, MinecraftServer server) {
                VersionChecker.resetPlayer(handler.getOwner().getId());
            }
        });
    }
}
