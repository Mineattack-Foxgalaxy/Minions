package io.github.skippyall.minions.websocket;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class JsonEncoder extends MessageToMessageEncoder<JsonObject> {
    @Override
    protected void encode(ChannelHandlerContext ctx, JsonObject msg, List<Object> out) {
        out.add(msg.toString());
    }
}
