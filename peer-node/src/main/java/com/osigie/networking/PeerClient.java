package com.osigie.networking;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.function.Consumer;

public class PeerClient {

    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public void requestChunk(String host, int port, String songId, String chunkId, Consumer<byte[]> onComplete) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                        socketChannel.pipeline().addLast(new PeerClientHandler(songId, chunkId, onComplete));
                    }
                });

        bootstrap.connect(host, port).addListener((ChannelFuture future) -> {
            if (!future.isSuccess()) {
                System.out.println("Failed to connect to " + host + ":" + port);
                onComplete.accept(null);
            }
        });
    }

    public void shutdown() {
        workerGroup.shutdownGracefully();
    }
}
