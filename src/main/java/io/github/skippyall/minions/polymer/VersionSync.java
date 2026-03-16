package io.github.skippyall.minions.polymer;

import eu.pb4.polymer.networking.api.PolymerNetworking;
import eu.pb4.polymer.networking.api.server.PolymerServerNetworking;
import io.github.skippyall.minions.Minions;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import xyz.nucleoid.packettweaker.PacketContext;

public class VersionSync {
    public static final int NETWORK_VERSION = 1;

    public static boolean isOnClient(PacketContext context) {
        if(context.getPlayer() != null) {
            return isOnClient(context.getPlayer().networkHandler);
        }
        return false;
    }

    public static boolean isOnClient(ServerPlayNetworkHandler player) {
        return PolymerServerNetworking.getSupportedVersion(player, VersionSyncPayload.PACKET_ID.id()) == NETWORK_VERSION;
    }

    public static class VersionSyncPayload implements CustomPayload {

        public static final CustomPayload.Id<VersionSyncPayload> PACKET_ID = new CustomPayload.Id<>(Identifier.of(Minions.MOD_ID, "version_sync"));

        @Override
        public Id<? extends CustomPayload> getId() {
            return PACKET_ID;
        }
    }

    public static void register() {
        PolymerNetworking.registerS2CVersioned(VersionSyncPayload.PACKET_ID, NETWORK_VERSION, PacketCodec.unit(null));
    }
}
