/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToByteEncoder
 */
package cc.sighs.auratip.editor.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.charset.StandardCharsets;

final class EditorWsFrameEncoder
extends MessageToByteEncoder<String> {
    EditorWsFrameEncoder() {
    }

    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) {
        if (msg == null) {
            msg = "";
        }
        byte[] payload = msg.getBytes(StandardCharsets.UTF_8);
        int len = payload.length;
        out.writeByte(129);
        if (len <= 125) {
            out.writeByte(len);
        } else if (len <= 65535) {
            out.writeByte(126);
            out.writeShort(len);
        } else {
            out.writeByte(127);
            out.writeLong((long)len);
        }
        out.writeBytes(payload);
    }
}

