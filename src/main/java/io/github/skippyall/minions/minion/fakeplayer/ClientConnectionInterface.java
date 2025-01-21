//code from https://github.com/gnembon/fabric-carpet
package io.github.skippyall.minions.minion.fakeplayer;

import io.netty.channel.Channel;

public interface ClientConnectionInterface {
    void setChannel(Channel channel);
}
