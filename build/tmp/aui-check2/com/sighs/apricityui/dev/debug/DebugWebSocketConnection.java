/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonParser
 */
package com.sighs.apricityui.dev.debug;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.sighs.apricityui.dev.debug.DebugProtocolSession;
import com.sighs.apricityui.dev.debug.ExternalDebugServer;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

final class DebugWebSocketConnection
implements Runnable {
    private static final String PATH = "/apricity";
    private static final String WEB_SOCKET_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private static final int MAX_HTTP_HEADER = 65536;
    private static final int MAX_FRAME_PAYLOAD = 0x100000;
    private final Socket socket;
    private final String token;
    private final DebugProtocolSession session = new DebugProtocolSession();
    private final AtomicBoolean open = new AtomicBoolean(true);
    private final Object outputLock = new Object();
    private volatile OutputStream output;

    DebugWebSocketConnection(Socket socket, String token) {
        this.socket = socket;
        this.token = token;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try (Socket socket = this.socket;
             BufferedInputStream input = new BufferedInputStream(this.socket.getInputStream());){
            this.socket.setSoTimeout(10000);
            this.output = this.socket.getOutputStream();
            if (!this.handshake(input)) {
                return;
            }
            this.socket.setSoTimeout(0);
            this.readFrames(input);
        }
        catch (IOException iOException) {
        }
        finally {
            this.open.set(false);
            ExternalDebugServer.connectionClosed(this, this.session);
        }
    }

    boolean isOpen() {
        return this.open.get() && !this.socket.isClosed();
    }

    void sendText(String text) {
        if (!this.isOpen() || this.output == null) {
            return;
        }
        this.sendFrame(1, text.getBytes(StandardCharsets.UTF_8));
    }

    void close() {
        if (!this.open.compareAndSet(true, false)) {
            return;
        }
        try {
            this.socket.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private boolean handshake(InputStream input) throws IOException {
        URI uri;
        String headerText = DebugWebSocketConnection.readHttpHeader(input);
        if (headerText == null) {
            return false;
        }
        String[] lines = headerText.split("\\r\\n");
        if (lines.length == 0) {
            return this.reject(400, "Bad Request");
        }
        String[] requestLine = lines[0].split(" ", 3);
        if (requestLine.length < 2 || !"GET".equals(requestLine[0])) {
            return this.reject(400, "Bad Request");
        }
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        for (int index = 1; index < lines.length; ++index) {
            int separator = lines[index].indexOf(58);
            if (separator <= 0) continue;
            headers.put(lines[index].substring(0, separator).trim().toLowerCase(Locale.ROOT), lines[index].substring(separator + 1).trim());
        }
        try {
            uri = URI.create(requestLine[1]);
        }
        catch (IllegalArgumentException invalidUri) {
            return this.reject(400, "Bad Request");
        }
        String suppliedToken = DebugWebSocketConnection.queryParameter(uri.getRawQuery(), "token");
        if (suppliedToken == null) {
            suppliedToken = DebugWebSocketConnection.bearerToken((String)headers.get("authorization"));
        }
        if (!PATH.equals(uri.getPath()) || !ExternalDebugServer.tokenMatches(this.token, suppliedToken)) {
            return this.reject(401, "Unauthorized");
        }
        String key = (String)headers.get("sec-websocket-key");
        if (!(key != null && "13".equals(headers.get("sec-websocket-version")) && "websocket".equalsIgnoreCase((String)headers.get("upgrade")) && DebugWebSocketConnection.hasHeaderToken((String)headers.get("connection"), "upgrade"))) {
            return this.reject(400, "Bad WebSocket Handshake");
        }
        String accept = DebugWebSocketConnection.webSocketAccept(key);
        String response = "HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: " + accept + "\r\n\r\n";
        this.output.write(response.getBytes(StandardCharsets.US_ASCII));
        this.output.flush();
        return true;
    }

    private void readFrames(InputStream input) throws IOException {
        OutputStream fragmented = null;
        int fragmentedOpcode = 0;
        block7: while (this.open.get()) {
            Frame frame = this.readFrame(input);
            if (frame == null) {
                return;
            }
            switch (frame.opcode) {
                case 0: {
                    if (fragmented == null) {
                        throw new IOException("Unexpected continuation frame");
                    }
                    fragmented.write(frame.payload);
                    if (((ByteArrayOutputStream)fragmented).size() > 0x100000) {
                        throw new IOException("Message is too large");
                    }
                    if (!frame.fin) continue block7;
                    this.dispatchMessage(fragmentedOpcode, ((ByteArrayOutputStream)fragmented).toByteArray());
                    fragmented = null;
                    fragmentedOpcode = 0;
                    continue block7;
                }
                case 1: {
                    if (fragmented != null) {
                        throw new IOException("Interleaved fragmented message");
                    }
                    if (frame.fin) {
                        this.dispatchMessage(frame.opcode, frame.payload);
                        continue block7;
                    }
                    fragmented = new ByteArrayOutputStream();
                    fragmented.write(frame.payload);
                    fragmentedOpcode = frame.opcode;
                    continue block7;
                }
                case 8: {
                    this.sendFrame(8, frame.payload.length <= 125 ? frame.payload : new byte[]{});
                    return;
                }
                case 9: {
                    this.sendFrame(10, frame.payload);
                    continue block7;
                }
                case 10: {
                    continue block7;
                }
            }
            throw new IOException("Unsupported WebSocket opcode");
        }
    }

    private void dispatchMessage(int opcode, byte[] payload) {
        if (opcode != 1) {
            return;
        }
        try {
            JsonElement parsed = JsonParser.parseString((String)new String(payload, StandardCharsets.UTF_8));
            if (!parsed.isJsonObject()) {
                ExternalDebugServer.writeError(this, (JsonElement)JsonNull.INSTANCE, -32600, "JSON-RPC request must be an object");
                return;
            }
            ExternalDebugServer.enqueue(this.session, this, parsed.getAsJsonObject());
        }
        catch (JsonParseException parseError) {
            ExternalDebugServer.writeError(this, (JsonElement)JsonNull.INSTANCE, -32700, "Parse error");
        }
    }

    private Frame readFrame(InputStream input) throws IOException {
        boolean masked;
        int first = input.read();
        if (first < 0) {
            return null;
        }
        int second = DebugWebSocketConnection.readByte(input);
        boolean fin = (first & 0x80) != 0;
        int opcode = first & 0xF;
        boolean bl = masked = (second & 0x80) != 0;
        if (!masked) {
            throw new IOException("Client WebSocket frames must be masked");
        }
        long length = second & 0x7F;
        if (length == 126L) {
            length = DebugWebSocketConnection.readUnsignedShort(input);
        } else if (length == 127L) {
            length = DebugWebSocketConnection.readLong(input);
        }
        if (length < 0L || length > 0x100000L) {
            throw new IOException("Frame is too large");
        }
        if (!(opcode < 8 || fin && length <= 125L)) {
            throw new IOException("Invalid control frame");
        }
        byte[] mask = DebugWebSocketConnection.readExactly(input, 4);
        byte[] payload = DebugWebSocketConnection.readExactly(input, (int)length);
        for (int index = 0; index < payload.length; ++index) {
            int n = index;
            payload[n] = (byte)(payload[n] ^ mask[index & 3]);
        }
        return new Frame(fin, opcode, payload);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendFrame(int opcode, byte[] payload) {
        OutputStream target = this.output;
        if (target == null || !this.isOpen()) {
            return;
        }
        Object object = this.outputLock;
        synchronized (object) {
            try {
                target.write(0x80 | opcode & 0xF);
                if (payload.length <= 125) {
                    target.write(payload.length);
                } else if (payload.length <= 65535) {
                    target.write(126);
                    target.write(payload.length >>> 8 & 0xFF);
                    target.write(payload.length & 0xFF);
                } else {
                    target.write(127);
                    target.write(ByteBuffer.allocate(8).putLong(payload.length).array());
                }
                target.write(payload);
                target.flush();
            }
            catch (IOException failure) {
                this.close();
            }
        }
    }

    private boolean reject(int status, String message) throws IOException {
        byte[] body = message.getBytes(StandardCharsets.UTF_8);
        String response = "HTTP/1.1 " + status + " " + message + "\r\nContent-Type: text/plain; charset=utf-8\r\nContent-Length: " + body.length + "\r\nConnection: close\r\n\r\n";
        this.output.write(response.getBytes(StandardCharsets.US_ASCII));
        this.output.write(body);
        this.output.flush();
        return false;
    }

    private static String readHttpHeader(InputStream input) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int matched = 0;
        while (bytes.size() < 65536) {
            int value = input.read();
            if (value < 0) {
                return null;
            }
            bytes.write(value);
            if ((matched = (switch (matched) {
                case 0 -> {
                    if (value == 13) {
                        yield 1;
                    }
                    yield 0;
                }
                case 1 -> {
                    if (value == 10) {
                        yield 2;
                    }
                    if (value == 13) {
                        yield 1;
                    }
                    yield 0;
                }
                case 2 -> {
                    if (value == 13) {
                        yield 3;
                    }
                    yield 0;
                }
                case 3 -> {
                    if (value == 10) {
                        yield 4;
                    }
                    yield 0;
                }
                default -> 4;
            })) != 4) continue;
            return bytes.toString(StandardCharsets.US_ASCII);
        }
        throw new IOException("HTTP header is too large");
    }

    private static String queryParameter(String query, String name) {
        if (query == null || query.isEmpty()) {
            return null;
        }
        for (String part : query.split("&")) {
            String rawName;
            int separator = part.indexOf(61);
            String string = rawName = separator < 0 ? part : part.substring(0, separator);
            if (!name.equals(URLDecoder.decode(rawName, StandardCharsets.UTF_8))) continue;
            String value = separator < 0 ? "" : part.substring(separator + 1);
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        }
        return null;
    }

    private static String bearerToken(String authorization) {
        if (authorization == null || !authorization.toLowerCase(Locale.ROOT).startsWith("bearer ")) {
            return null;
        }
        return authorization.substring(7).trim();
    }

    private static boolean hasHeaderToken(String header, String expected) {
        if (header == null) {
            return false;
        }
        for (String token : header.split(",")) {
            if (!expected.equalsIgnoreCase(token.trim())) continue;
            return true;
        }
        return false;
    }

    private static String webSocketAccept(String key) throws IOException {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-1").digest((key.trim() + WEB_SOCKET_GUID).getBytes(StandardCharsets.US_ASCII));
            return Base64.getEncoder().encodeToString(digest);
        }
        catch (NoSuchAlgorithmException impossible) {
            throw new IOException("SHA-1 is unavailable", impossible);
        }
    }

    private static int readByte(InputStream input) throws IOException {
        int value = input.read();
        if (value < 0) {
            throw new EOFException();
        }
        return value;
    }

    private static int readUnsignedShort(InputStream input) throws IOException {
        return DebugWebSocketConnection.readByte(input) << 8 | DebugWebSocketConnection.readByte(input);
    }

    private static long readLong(InputStream input) throws IOException {
        long value = 0L;
        for (int index = 0; index < 8; ++index) {
            value = value << 8 | (long)DebugWebSocketConnection.readByte(input);
        }
        return value;
    }

    private static byte[] readExactly(InputStream input, int length) throws IOException {
        int read;
        byte[] result = new byte[length];
        for (int offset = 0; offset < length; offset += read) {
            read = input.read(result, offset, length - offset);
            if (read >= 0) continue;
            throw new EOFException();
        }
        return result;
    }

    private record Frame(boolean fin, int opcode, byte[] payload) {
    }
}

