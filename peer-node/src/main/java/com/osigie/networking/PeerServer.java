package com.osigie.networking;

import com.osigie.service.ChunkStore;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class PeerServer {

    private final int port;
    private final ChunkStore chunkStore;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public PeerServer(int port, ChunkStore chunkStore) {
        this.port = port;
        this.chunkStore = chunkStore;
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
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel
                                .pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        socketChannel
                                .pipeline()
                                .addLast(new LengthFieldPrepender(4));
                        socketChannel.pipeline().addLast(new PeerServerHandler(chunkStore));
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

}
