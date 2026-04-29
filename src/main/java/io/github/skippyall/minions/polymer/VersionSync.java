package io.github.skippyall.minions.polymer;

import eu.pb4.polymer.networking.api.PolymerNetworking;
import eu.pb4.polymer.networking.api.server.PolymerServerNetworking;
import io.github.skippyall.minions.Minions;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import xyz.nucleoid.packettweaker.PacketContext;

public class VersionSync {
    public static final int NETWORK_VERSION = 1;

    public static boolean isOnClient(PacketContext context) {
        if(context.getPlayer() != null) {
            return isOnClient(context.getPlayer().connection);
        }
        return false;
    }

    public static boolean isOnClient(ServerGamePacketListenerImpl player) {
        return PolymerServerNetworking.getSupportedVersion(player, VersionSyncPayload.PACKET_ID.id()) == NETWORK_VERSION;
    }

    public static class VersionSyncPayload implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<VersionSyncPayload> PACKET_ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Minions.MOD_ID, "version_sync"));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return PACKET_ID;
        }
    }

    public static void register() {
        PolymerNetworking.registerS2CVersioned(VersionSyncPayload.PACKET_ID, NETWORK_VERSION, StreamCodec.unit(null));
    }
}
