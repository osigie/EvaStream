package com.osigie.networking;

import com.osigie.service.ChunkStore;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.StandardCharsets;

public class PeerServer {
    private final int port;
    private final ChunkStore chunkStore;
    private final String peerId;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public PeerServer(int port, ChunkStore chunkStore, String peerId) {
        this.port = port;
        this.chunkStore = chunkStore;
        this.peerId = peerId;
    }

    public void start() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                        socketChannel.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
                        socketChannel.pipeline().addLast(new PeerServerHandler(chunkStore, peerId));
                    }
                });

        bootstrap.bind(port).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                System.out.println("Server started on port " + port);
            } else {
                System.out.println("Failed to start server on port " + port);
            }
        });
    }

    public void shutdown() {
        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();
    }
}
