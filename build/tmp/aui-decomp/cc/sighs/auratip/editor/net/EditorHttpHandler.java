/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.util.CharsetUtil
 *  io.netty.util.concurrent.GenericFutureListener
 */
package cc.sighs.auratip.editor.net;

import cc.sighs.auratip.AuraTip;
import cc.sighs.auratip.editor.net.EditorHttpRequest;
import cc.sighs.auratip.editor.net.EditorHttpRequestDecoder;
import cc.sighs.auratip.editor.net.EditorWsFrameDecoder;
import cc.sighs.auratip.editor.net.EditorWsFrameEncoder;
import cc.sighs.auratip.editor.net.EditorWsHandler;
import cc.sighs.auratip.editor.net.EditorWsHub;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

final class EditorHttpHandler
extends SimpleChannelInboundHandler<EditorHttpRequest> {
    private static final String EDITOR_PATH = "/assets/auratip/web/editor.html";
    private static final String WEB_ROOT = "/assets/auratip/web";
    private static final String TOKEN_WS_PORT = "__AURATIP_WS_PORT__";
    private static final String WS_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private final EditorWsHub wsHub;

    EditorHttpHandler(EditorWsHub wsHub) {
        this.wsHub = wsHub;
    }

    protected void channelRead0(ChannelHandlerContext ctx, EditorHttpRequest req) {
        String method = req.method();
        if (!"GET".equalsIgnoreCase(method)) {
            EditorHttpHandler.sendStatus(ctx, 405, "Method Not Allowed");
            return;
        }
        String path = req.path();
        if (path == null || path.isEmpty() || "/".equals(path)) {
            EditorHttpHandler.sendRedirect(ctx, "/editor.html");
            return;
        }
        if ("/ws".equals(path) && EditorHttpHandler.isWebSocketUpgrade(req)) {
            this.upgradeToWebSocket(ctx, req);
            return;
        }
        if ("/editor.html".equals(path)) {
            EditorHttpHandler.sendEditorHtml(ctx);
            return;
        }
        if (path.startsWith("/lang/") && path.endsWith(".json")) {
            EditorHttpHandler.sendWebResource(ctx, path, "application/json; charset=utf-8");
            return;
        }
        if (path.startsWith("/") && !path.contains("..") && path.length() > 1) {
            String mime = "application/octet-stream";
            if (path.endsWith(".js")) {
                mime = "text/javascript; charset=utf-8";
            } else if (path.endsWith(".json")) {
                mime = "application/json; charset=utf-8";
            } else if (path.endsWith(".html")) {
                mime = "text/html; charset=utf-8";
            } else if (path.endsWith(".css")) {
                mime = "text/css; charset=utf-8";
            }
            EditorHttpHandler.sendWebResource(ctx, path, mime);
            return;
        }
        EditorHttpHandler.sendStatus(ctx, 404, "Not Found");
    }

    private static boolean isWebSocketUpgrade(EditorHttpRequest req) {
        String upgrade = req.headerLower("upgrade");
        if (!"websocket".equalsIgnoreCase(upgrade)) {
            return false;
        }
        String connection = req.headerLower("connection");
        return connection.toLowerCase().contains("upgrade");
    }

    private void upgradeToWebSocket(ChannelHandlerContext ctx, EditorHttpRequest req) {
        String accept;
        String key = req.headerLower("sec-websocket-key");
        if (key == null || key.isBlank()) {
            EditorHttpHandler.sendStatus(ctx, 400, "Bad Request");
            return;
        }
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hash = sha1.digest((key.trim() + WS_GUID).getBytes(StandardCharsets.US_ASCII));
            accept = Base64.getEncoder().encodeToString(hash);
        }
        catch (Exception e) {
            AuraTip.LOGGER.warn("WS accept hash failed", (Throwable)e);
            EditorHttpHandler.sendStatus(ctx, 500, "Internal Server Error");
            return;
        }
        String response = "HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: " + accept + "\r\n\r\n";
        ctx.writeAndFlush((Object)Unpooled.copiedBuffer((CharSequence)response, (Charset)CharsetUtil.US_ASCII)).addListener((GenericFutureListener)((ChannelFutureListener)future -> {
            if (!future.isSuccess()) {
                ctx.close();
                return;
            }
            ChannelPipeline p = ctx.pipeline();
            if (p.get(EditorHttpRequestDecoder.class) != null) {
                p.remove(EditorHttpRequestDecoder.class);
            }
            p.remove((ChannelHandler)this);
            p.addLast(new ChannelHandler[]{new EditorWsFrameDecoder()});
            p.addLast(new ChannelHandler[]{new EditorWsFrameEncoder()});
            p.addLast(new ChannelHandler[]{new EditorWsHandler(this.wsHub)});
        }));
    }

    private static void sendEditorHtml(ChannelHandlerContext ctx) {
        try (InputStream in = AuraTip.class.getResourceAsStream(EDITOR_PATH);){
            if (in == null) {
                EditorHttpHandler.sendStatus(ctx, 404, "Not Found");
                return;
            }
            byte[] bytes = in.readAllBytes();
            String html = new String(bytes, StandardCharsets.UTF_8);
            int port = ((InetSocketAddress)ctx.channel().localAddress()).getPort();
            html = html.replace(TOKEN_WS_PORT, String.valueOf(port));
            byte[] out = html.getBytes(StandardCharsets.UTF_8);
            String header = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: " + out.length + "\r\nCache-Control: no-store\r\nConnection: close\r\n\r\n";
            ByteBuf buf = Unpooled.buffer((int)(header.length() + out.length));
            buf.writeCharSequence((CharSequence)header, CharsetUtil.US_ASCII);
            buf.writeBytes(out);
            ctx.writeAndFlush((Object)buf).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
        }
        catch (Exception e) {
            AuraTip.LOGGER.warn("Failed to serve editor.html", (Throwable)e);
            EditorHttpHandler.sendStatus(ctx, 500, "Internal Server Error");
        }
    }

    private static void sendWebResource(ChannelHandlerContext ctx, String requestPath, String contentType) {
        if (requestPath == null || requestPath.isBlank()) {
            EditorHttpHandler.sendStatus(ctx, 404, "Not Found");
            return;
        }
        if (requestPath.contains("..") || requestPath.contains("\\") || requestPath.contains("%")) {
            EditorHttpHandler.sendStatus(ctx, 400, "Bad Request");
            return;
        }
        String resourcePath = WEB_ROOT + requestPath;
        try (InputStream in = AuraTip.class.getResourceAsStream(resourcePath);){
            if (in == null) {
                EditorHttpHandler.sendStatus(ctx, 404, "Not Found");
                return;
            }
            byte[] out = in.readAllBytes();
            String header = "HTTP/1.1 200 OK\r\nContent-Type: " + contentType + "\r\nContent-Length: " + out.length + "\r\nCache-Control: no-store\r\nConnection: close\r\n\r\n";
            ByteBuf buf = Unpooled.buffer((int)(header.length() + out.length));
            buf.writeCharSequence((CharSequence)header, CharsetUtil.US_ASCII);
            buf.writeBytes(out);
            ctx.writeAndFlush((Object)buf).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
        }
        catch (Exception e) {
            AuraTip.LOGGER.warn("Failed to serve web resource {}", (Object)requestPath, (Object)e);
            EditorHttpHandler.sendStatus(ctx, 500, "Internal Server Error");
        }
    }

    private static void sendRedirect(ChannelHandlerContext ctx, String location) {
        String response = "HTTP/1.1 302 Found\r\nLocation: " + location + "\r\nConnection: close\r\n\r\n";
        ctx.writeAndFlush((Object)Unpooled.copiedBuffer((CharSequence)response, (Charset)CharsetUtil.US_ASCII)).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
    }

    private static void sendStatus(ChannelHandlerContext ctx, int code, String text) {
        byte[] bytes = (code + " " + text).getBytes(StandardCharsets.UTF_8);
        String header = "HTTP/1.1 " + code + " " + text + "\r\nContent-Type: text/plain; charset=utf-8\r\nContent-Length: " + bytes.length + "\r\nConnection: close\r\n\r\n";
        ByteBuf buf = Unpooled.buffer((int)(header.length() + bytes.length));
        buf.writeCharSequence((CharSequence)header, CharsetUtil.US_ASCII);
        buf.writeBytes(bytes);
        ctx.writeAndFlush((Object)buf).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
    }
}

