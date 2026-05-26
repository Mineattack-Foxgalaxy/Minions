package io.github.skippyall.minions.polymer;

import eu.pb4.polymer.networking.api.PolymerNetworking;
import eu.pb4.polymer.networking.api.server.PolymerServerNetworking;
import io.github.skippyall.minions.Minions;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jspecify.annotations.Nullable;

public class VersionSync {
    public static final int NETWORK_VERSION = 1;

    public static boolean isOnClient(@Nullable PacketContext context) {
        if(context != null && context.get(PacketContext.CONNECTION).getPacketListener() instanceof ServerGamePacketListenerImpl gamePacketListener) {
            return isOnClient(gamePacketListener);
        }
        return false;
    }

    public static boolean isOnClient(ServerGamePacketListenerImpl player) {
        return PolymerServerNetworking.getSupportedVersion(player, VersionSyncPayload.PACKET_ID.id()) == NETWORK_VERSION;
    }

    public static class VersionSyncPayload implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<VersionSyncPayload> PACKET_ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Minions.MOD_ID, "version_sync"));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return PACKET_ID;
        }
    }

    public static void register() {
        PolymerNetworking.registerClientboundVersioned(VersionSyncPayload.PACKET_ID, NETWORK_VERSION, StreamCodec.unit(null));
    }
}
