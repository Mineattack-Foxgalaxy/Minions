//code from https://github.com/gnembon/fabric-carpet
package io.github.skippyall.minions.mixins;

import io.github.skippyall.minions.minion.fakeplayer.ClientConnectionInterface;
import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Connection.class)
public abstract class ConnectionMixin implements ClientConnectionInterface {
    @Override
    @Accessor //Compat with adventure-platform-fabric
    public abstract void setChannel(Channel channel);
}
