/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 */
package cc.sighs.auratip.editor.net;

import cc.sighs.auratip.AuraTip;
import cc.sighs.auratip.editor.net.EditorHttpRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

final class EditorHttpRequestDecoder
extends ByteToMessageDecoder {
    private static final int MAX_HEADER_BYTES = 32768;

    EditorHttpRequestDecoder() {
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int readable = in.readableBytes();
        if (readable <= 0) {
            return;
        }
        if (readable > 32768) {
            AuraTip.LOGGER.warn("Editor HTTP header too large: {} bytes", (Object)readable);
            ctx.close();
            return;
        }
        int end = EditorHttpRequestDecoder.indexOfHeaderEnd(in);
        if (end < 0) {
            return;
        }
        int len = end - in.readerIndex();
        byte[] bytes = new byte[len];
        in.readBytes(bytes);
        in.skipBytes(4);
        String headerText = new String(bytes, StandardCharsets.US_ASCII);
        String[] lines = headerText.split("\\r\\n");
        if (lines.length == 0) {
            return;
        }
        String first = lines[0].trim();
        String[] firstParts = first.split("\\s+");
        if (firstParts.length < 2) {
            return;
        }
        String method = firstParts[0];
        String uri = firstParts[1];
        Map<String, String> headers = EditorHttpRequest.headersLowerMutable();
        for (int i = 1; i < lines.length; ++i) {
            String line = lines[i];
            int colon = line.indexOf(58);
            if (colon <= 0) continue;
            String key = line.substring(0, colon).trim().toLowerCase();
            String value = line.substring(colon + 1).trim();
            if (key.isEmpty()) continue;
            headers.put(key, value);
        }
        out.add(new EditorHttpRequest(method, uri, headers));
    }

    private static int indexOfHeaderEnd(ByteBuf in) {
        int start = in.readerIndex();
        int end = in.writerIndex();
        int i = start;
        while (i + 3 < end) {
            if (in.getByte(i) == 13 && in.getByte(i + 1) == 10 && in.getByte(i + 2) == 13 && in.getByte(i + 3) == 10) {
                return i;
            }
            ++i;
        }
        return -1;
    }
}

