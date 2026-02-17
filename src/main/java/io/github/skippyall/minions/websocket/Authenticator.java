package io.github.skippyall.minions.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.util.AttributeKey;

import java.util.UUID;

public class Authenticator extends ChannelInboundHandlerAdapter {
    public static final AttributeKey<Boolean> AUTHENTICATED = AttributeKey.newInstance("authenticated");
    public static final AttributeKey<UUID> MINION = AttributeKey.newInstance("minion");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!ctx.channel().hasAttr(AUTHENTICATED) && msg instanceof HttpMessage message) {
            String header = message.headers().get(HttpHeaderNames.AUTHORIZATION);
            if (header.startsWith("Bearer ")) {
                String key = header.substring("Bearer ".length()).trim();
                if (WebsocketServer.keyToMinion.containsKey(key)) {
                    ctx.channel().attr(AUTHENTICATED).set(true);
                    ctx.channel().attr(MINION).set(WebsocketServer.keyToMinion.get(key));
                } else {
                    ctx.channel().attr(AUTHENTICATED).set(false);
                }
            } else {
                ctx.channel().attr(AUTHENTICATED).set(false);
            }
        }

        if(ctx.channel().hasAttr(AUTHENTICATED) && ctx.channel().attr(AUTHENTICATED).get() == Boolean.TRUE) {
            super.channelRead(ctx, msg);
        } else {
            ctx.close();
        }
    }
}
