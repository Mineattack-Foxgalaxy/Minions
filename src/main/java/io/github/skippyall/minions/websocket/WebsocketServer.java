package io.github.skippyall.minions.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WebsocketServer {
    public static final Map<String, UUID> keyToMinion = new HashMap<>();

    private final NioEventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;
    List<MessageHandler> handlers;

    private String host;
    private int port;
    private SslContext sslCtx;

    public void start() {
        channel = new ServerBootstrap()
                .group(group)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new WebSocketServerInitializer(sslCtx, this))
                .bind(host, port)
                .syncUninterruptibly()
                .channel();
    }

    public void stop() throws InterruptedException {
        if(channel != null) {
            channel.close().sync();
        }
        group.shutdownGracefully();
    }

    public static class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
        private static final int MAX_CONTENT_LENGTH = 65536;

        private final SslContext sslCtx;
        private final WebsocketServer server;

        public WebSocketServerInitializer(SslContext sslCtx, WebsocketServer server) {
            this.sslCtx = sslCtx;
            this.server = server;
        }

        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            if (sslCtx != null) {
                pipeline.addLast(sslCtx.newHandler(ch.alloc()));
            }
            pipeline.addLast(new HttpServerCodec())
                    .addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH))
                    .addLast(new Authenticator())
                    .addLast(new WebSocketServerCompressionHandler(MAX_CONTENT_LENGTH))
                    .addLast(new WebSocketServerProtocolHandler("/", null, true))
                    .addLast(new JsonDecoder())
                    .addLast(new JsonEncoder())
                    .addLast(new MessageHandler(server));
        }
    }
}
