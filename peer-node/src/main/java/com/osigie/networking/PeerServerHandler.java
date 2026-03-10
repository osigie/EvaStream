package com.osigie.networking;

import com.osigie.domain.NetworkType;
import com.osigie.service.ChunkStore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import utils.NetworkUtil;

import java.nio.charset.StandardCharsets;

public class PeerServerHandler extends ChannelInboundHandlerAdapter {
    private final ChunkStore chunkStore;

    public PeerServerHandler(ChunkStore chunkStore) {
        this.chunkStore = chunkStore;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;

        byte type = buf.readByte();

        if (type == NetworkType.GET_CHUNK.getValue()) {
            String songId = NetworkUtil.readString(buf);
            String chunkId = NetworkUtil.readString(buf);
            byte[] chunk = chunkStore.loadChunk(songId, chunkId);

            ByteBuf response = ctx.alloc().buffer();
            if (chunk == null) {
                response.writeByte(NetworkType.NOT_FOUND.getValue());
            } else {
                response.writeByte(NetworkType.CHUNK_DATA.getValue());
                response.writeBytes(chunk);
            }
            ctx.writeAndFlush(response)
                    .addListener(ChannelFutureListener.CLOSE);
        }
        buf.release();

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
