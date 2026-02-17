package io.github.skippyall.minions.websocket;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.UUID;

public class MessageHandler extends SimpleChannelInboundHandler<JsonObject> {
    private final WebsocketServer server;

    public MessageHandler(WebsocketServer server) {
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        server.handlers.add(this);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        server.handlers.remove(this);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonObject msg) throws Exception {
        UUID minion = ctx.channel().attr(Authenticator.MINION).get();
        MinionWebsocketManager.get(minion).handleMessage(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {

        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
