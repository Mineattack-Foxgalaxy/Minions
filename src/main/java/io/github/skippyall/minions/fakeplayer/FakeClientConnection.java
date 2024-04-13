package io.github.skippyall.minions.fakeplayer;

import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.text.Text;

public class FakeClientConnection extends ClientConnection {
    public FakeClientConnection(NetworkSide p)
    {
        super(p);
        // compat with adventure-platform-fabric. This does NOT trigger other vanilla handlers for establishing a channel
        // also makes #isOpen return true, allowing enderpearls to teleport fake players
        ((ClientConnectionInterface)this).setChannel(new EmbeddedChannel());
    }

    @Override
    public void tryDisableAutoRead()
    {
    }

    @Override
    public void handleDisconnection() {
        getPacketListener().onDisconnected(getDisconnectReason()==null ? Text.literal("Disconnected"): getDisconnectReason());
    }

    @Override
    public void setInitialPacketListener(PacketListener packetListener)
    {
    }

    @Override
    public void setPacketListener(PacketListener packetListener) {

    }
}
