package com.osigie.networking;

import com.osigie.service.ChunkStore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class PeerServerHandler extends ChannelInboundHandlerAdapter {
    private final ChunkStore chunkStore;
    private final String peerId;

    public PeerServerHandler(ChunkStore chunkStore, String peerId) {
        this.chunkStore = chunkStore;
        this.peerId = peerId;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String request = (String) msg;

        String[] parts = request.split("\\|");

        if (!parts[0].equals("GET_CHUNK")) {
            return;
        }

        String songId = parts[1];
        String chunkId = parts[2];

        byte[] chunk = this.chunkStore.loadChunk(songId, chunkId);

        if (chunk == null) {
            ctx.writeAndFlush(Unpooled.copiedBuffer("NOT_FOUND\n".getBytes()));
        } else {
            int size = chunk.length;

            String header = "CHUNK|" + songId + "|" + chunkId + "|" + size + "\n";
            ctx.writeAndFlush(Unpooled.copiedBuffer(header, StandardCharsets.UTF_8));
            ctx.writeAndFlush(Unpooled.wrappedBuffer(chunk));
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
