//code from https://github.com/gnembon/fabric-carpet
package io.github.skippyall.minions.minion.fakeplayer;

import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.state.NetworkState;
import net.minecraft.network.listener.PacketListener;

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
    }

    @Override
    public void setInitialPacketListener(PacketListener packetListener)
    {
    }

    @Override
    public <T extends PacketListener> void transitionInbound(NetworkState<T> state, T packetListener) {
    }
}
