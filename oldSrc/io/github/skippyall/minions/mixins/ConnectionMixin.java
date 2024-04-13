package io.github.skippyall.minions.mixins;

import io.github.skippyall.minions.fakeplayer.ClientConnectionInterface;
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
