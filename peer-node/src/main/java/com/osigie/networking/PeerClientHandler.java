package com.osigie.networking;

import com.osigie.domain.NetworkType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import utils.NetworkUtil;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class PeerClientHandler extends ChannelInboundHandlerAdapter {
    private final String songId;
    private final String chunkId;
    private final Consumer<byte[]> onComplete;

    public PeerClientHandler(String songId, String chunkId, Consumer<byte[]> onComplete) {
        this.songId = songId;
        this.chunkId = chunkId;
        this.onComplete = onComplete;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeByte(NetworkType.GET_CHUNK.getValue());
        NetworkUtil.writeString(buf, songId);
        NetworkUtil.writeString(buf, chunkId);
        ctx.writeAndFlush(buf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        byte type = buf.readByte();

        if (type == NetworkType.CHUNK_DATA.getValue()) {
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            onComplete.accept(data);
        } else if (type == NetworkType.NOT_FOUND.getValue()) {
            onComplete.accept(null);
        }
        buf.release();
        ctx.close();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Peer client exception caught");
        cause.printStackTrace();
        ctx.close();
    }
}
