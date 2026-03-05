package com.osigie.networking;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class PeerClientHandler extends ChannelInboundHandlerAdapter {
    private final String songId;
    private final String chunkId;
    private final Runnable onComplete;

    private int expectedSize;
    private ByteBuf buffer;

    public PeerClientHandler(String songId, String chunkId, Runnable onComplete) {
        this.songId = songId;
        this.chunkId = chunkId;
        this.onComplete = onComplete;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String request = "GET_CHUNK|" + songId + "|" + chunkId + "\n";
        ByteBuf buf = Unpooled.copiedBuffer(request, StandardCharsets.UTF_8);
        ctx.channel().writeAndFlush(buf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof String header) {
            String[] parts = header.trim().split("\\|");

            if (parts[0].equals("CHUNK")) {
                expectedSize = Integer.parseInt(parts[3]);
                buffer = ctx.alloc().buffer(expectedSize);
            }
        } else if (msg instanceof ByteBuf data) {
            buffer.writeBytes(data);

            if (buffer.readableBytes() >= expectedSize) {
                byte[] chunkData = new byte[expectedSize];
                buffer.readBytes(chunkData);
                System.out.println("Chunk received: " + chunkData.length + " bytes");
                onComplete.run();
                ctx.close();
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
