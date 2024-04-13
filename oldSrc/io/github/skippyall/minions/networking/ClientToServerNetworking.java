package io.github.skippyall.minions.networking;

import io.github.skippyall.minions.Minions;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.world.entity.player.Player;

public class ClientToServerNetworking {
    public static final ResourceLocation RL = new ResourceLocation(Minions.MOD_ID, "network");
    @Environment(EnvType.CLIENT)
    public static void sendJoinPacket(Player player) {
        FriendlyByteBuf pbf = new FriendlyByteBuf(Unpooled.buffer());
        pbf.writeUtf("BN|Init|V0.1");
        ClientConfigurationNetworking.send(RL, pbf);
    }

    @Environment(EnvType.CLIENT)
    public static void onConfigurationInit(ClientConfigurationPacketListenerImpl handler, Minecraft client) {
        sendJoinPacket(client.player);
    }

    @Environment(EnvType.SERVER)
    public static void receive(MinecraftServer server, ServerConfigurationPacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        String message = buf.readUtf();
        if (!message.startsWith("BN|")) {
            Minions.LOGGER.debug("Message with wrong format: " + message);
        }
        String[] parts = message.split("|");

    }
}
