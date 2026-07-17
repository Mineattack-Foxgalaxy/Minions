//code from https://github.com/gnembon/fabric-carpet/blob/master/src/main/java/carpet/mixins/Connection_packetCounterMixin.java and https://github.com/gnembon/fabric-carpet/blob/master/src/main/java/carpet/fakes/ClientConnectionInterface.java
package io.github.skippyall.minions.mixins;

import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Connection.class)
public interface ClientConnectionInterface {
    @Accessor //Compat with adventure-platform-fabric
    void setChannel(Channel channel);
}
