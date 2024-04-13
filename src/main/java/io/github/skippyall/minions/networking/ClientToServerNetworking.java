package io.github.skippyall.minions.networking;

import io.github.skippyall.minions.Minions;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.util.Identifier;

public class ClientToServerNetworking {
    public static final Identifier RL = new Identifier(Minions.MOD_ID, "network");
    @Environment(EnvType.CLIENT)
    public static void sendJoinPacket(PlayerEntity player) {
        PacketByteBuf pbf = new PacketByteBuf(Unpooled.buffer());
        pbf.writeString("BN|Init|V0.1");
        ClientConfigurationNetworking.send(RL, pbf);
    }

    @Environment(EnvType.CLIENT)
    public static void onConfigurationInit(ClientConfigurationNetworkHandler handler, MinecraftClient client) {
        sendJoinPacket(client.player);
    }

    @Environment(EnvType.SERVER)
    public static void receive(MinecraftServer server, ServerConfigurationNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        String message = buf.readString();
        if (!message.startsWith("BN|")) {
            Minions.LOGGER.debug("Message with wrong format: " + message);
        }
        String[] parts = message.split("|");

    }
}
