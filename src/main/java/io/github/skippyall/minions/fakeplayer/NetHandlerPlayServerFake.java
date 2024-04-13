package io.github.skippyall.minions.fakeplayer;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import java.util.Set;

public class NetHandlerPlayServerFake extends ServerPlayNetworkHandler
{
    public NetHandlerPlayServerFake(final MinecraftServer minecraftServer, final ClientConnection connection, final ServerPlayerEntity serverPlayer, final ConnectedClientData i)
    {
        super(minecraftServer, connection, serverPlayer, i);
    }

    @Override
    public void sendPacket(final Packet<?> packetIn)
    {
    }

    @Override
    public void disconnect(Text message)
    {
        if (message.getContent() instanceof TranslatableTextContent text && (text.getKey().equals("multiplayer.disconnect.idling") || text.getKey().equals("multiplayer.disconnect.duplicate_login")))
        {
            ((MinionFakePlayer) player).kill(message);
        }
    }

    @Override
    public void requestTeleport(double d, double e, double f, float g, float h, Set<PositionFlag> set)
    {
        super.requestTeleport(d, e, f, g, h, set);
        if (player.getServerWorld().getPlayerByUuid(player.getUuid()) != null) {
            syncWithPlayerPosition();
            player.getServerWorld().getChunkManager().updatePosition(player);
        }
    }

}
