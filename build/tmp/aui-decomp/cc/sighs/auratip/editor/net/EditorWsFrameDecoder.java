/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 */
package cc.sighs.auratip.editor.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

final class EditorWsFrameDecoder
extends ByteToMessageDecoder {
    private static final int MAX_PAYLOAD = 0x100000;

    EditorWsFrameDecoder() {
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int maskLen;
        if (in.readableBytes() < 2) {
            return;
        }
        in.markReaderIndex();
        short b0 = in.readUnsignedByte();
        short b1 = in.readUnsignedByte();
        int opcode = b0 & 0xF;
        boolean masked = (b1 & 0x80) != 0;
        long len = b1 & 0x7F;
        if (len == 126L) {
            if (in.readableBytes() < 2) {
                in.resetReaderIndex();
                return;
            }
            len = in.readUnsignedShort();
        } else if (len == 127L) {
            if (in.readableBytes() < 8) {
                in.resetReaderIndex();
                return;
            }
            len = in.readLong();
        }
        if (len < 0L || len > 0x100000L) {
            ctx.close();
            return;
        }
        int n = maskLen = masked ? 4 : 0;
        if (in.readableBytes() < maskLen + (int)len) {
            in.resetReaderIndex();
            return;
        }
        byte[] mask = null;
        if (masked) {
            mask = new byte[4];
            in.readBytes(mask);
        }
        byte[] payload = new byte[(int)len];
        in.readBytes(payload);
        if (masked && mask != null) {
            for (int i = 0; i < payload.length; ++i) {
                payload[i] = (byte)(payload[i] ^ mask[i & 3]);
            }
        }
        if (opcode == 8) {
            ctx.close();
            return;
        }
        if (opcode == 1) {
            out.add(new String(payload, StandardCharsets.UTF_8));
        }
    }
}

