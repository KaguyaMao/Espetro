/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  net.minecraft.client.Minecraft
 */
package com.sighs.apricityui.dev.debug;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.dev.debug.DebugProtocolException;
import com.sighs.apricityui.dev.debug.DebugProtocolSession;
import com.sighs.apricityui.dev.debug.DebugWebSocketConnection;
import com.sighs.apricityui.spi.AuiServices;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;

public final class ExternalDebugServer {
    public static final int PROTOCOL_VERSION = 1;
    public static final int PORT = 25321;
    public static final String ENDPOINT = "ws://127.0.0.1:25321/apricity";
    private static final int MAX_QUEUED_COMMANDS = 4096;
    private static final int MAX_COMMANDS_PER_TICK = 256;
    private static final long COMMAND_BUDGET_NS = 4000000L;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ConcurrentLinkedQueue<PendingCommand> COMMANDS = new ConcurrentLinkedQueue();
    private static final Set<DebugWebSocketConnection> CONNECTIONS = ConcurrentHashMap.newKeySet();
    private static final AtomicInteger QUEUED_COMMANDS = new AtomicInteger();
    private static final AtomicInteger CONNECTION_SEQUENCE = new AtomicInteger();
    private static final AtomicBoolean STARTED = new AtomicBoolean();
    private static final AtomicBoolean SHUTDOWN_HOOK_INSTALLED = new AtomicBoolean();
    private static volatile ServerSocket serverSocket;
    private static volatile Path discoveryFile;
    private static volatile String token;

    private ExternalDebugServer() {
    }

    public static void startIfEnabled() {
        if (!ExternalDebugServer.isEnabled() || !STARTED.compareAndSet(false, true)) {
            return;
        }
        token = ExternalDebugServer.configuredToken();
        try {
            ServerSocket socket = new ServerSocket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 25321));
            serverSocket = socket;
            ExternalDebugServer.writeDiscoveryFile();
            ExternalDebugServer.installShutdownHook();
            Thread acceptThread = new Thread(ExternalDebugServer::acceptConnections, "Apricity-Debug-Accept");
            acceptThread.setDaemon(true);
            acceptThread.start();
            ApricityUI.LOGGER.info("Apricity external debugger listening at {}", (Object)ENDPOINT);
        }
        catch (Throwable failure) {
            STARTED.set(false);
            ExternalDebugServer.closeServerSocket();
            ApricityUI.LOGGER.error("Unable to start Apricity external debugger on 127.0.0.1:{}", (Object)25321, (Object)failure);
        }
    }

    public static void reconcileConfiguration() {
        if (ExternalDebugServer.isEnabled()) {
            ExternalDebugServer.startIfEnabled();
        } else {
            ExternalDebugServer.stop();
        }
    }

    public static void tick() {
        PendingCommand command;
        if (!STARTED.get()) {
            return;
        }
        long deadline = System.nanoTime() + 4000000L;
        for (int processed = 0; processed < 256 && (command = COMMANDS.poll()) != null; ++processed) {
            QUEUED_COMMANDS.decrementAndGet();
            command.execute();
            if (System.nanoTime() < deadline) continue;
            break;
        }
    }

    public static void stop() {
        PendingCommand command;
        if (!STARTED.compareAndSet(true, false)) {
            return;
        }
        ExternalDebugServer.closeServerSocket();
        for (DebugWebSocketConnection connection : CONNECTIONS) {
            connection.close();
        }
        CONNECTIONS.clear();
        while ((command = COMMANDS.poll()) != null) {
            QUEUED_COMMANDS.decrementAndGet();
            command.failServerStopped();
        }
        ExternalDebugServer.deleteDiscoveryFile();
    }

    static boolean tokenMatches(String expected, String candidate) {
        if (expected == null || candidate == null) {
            return false;
        }
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), candidate.getBytes(StandardCharsets.UTF_8));
    }

    static void enqueue(DebugProtocolSession session, DebugWebSocketConnection connection, JsonObject request) {
        if (QUEUED_COMMANDS.incrementAndGet() > 4096) {
            QUEUED_COMMANDS.decrementAndGet();
            if (request.has("id")) {
                ExternalDebugServer.writeError(connection, ExternalDebugServer.requestId(request), -32000, "Debug command queue is full");
            }
            return;
        }
        COMMANDS.add(new PendingCommand(session, connection, request));
    }

    static void connectionClosed(DebugWebSocketConnection connection, DebugProtocolSession session) {
        CONNECTIONS.remove(connection);
        if (!STARTED.get()) {
            session.close();
            return;
        }
        if (QUEUED_COMMANDS.incrementAndGet() > 4096) {
            QUEUED_COMMANDS.decrementAndGet();
            return;
        }
        COMMANDS.add(new PendingCommand(session, null, null));
    }

    static void writeError(DebugWebSocketConnection connection, JsonElement id, int code, String message) {
        if (connection == null || !connection.isOpen()) {
            return;
        }
        JsonObject error = new JsonObject();
        error.addProperty("code", (Number)code);
        error.addProperty("message", message == null ? "Unknown error" : message);
        JsonObject response = new JsonObject();
        response.addProperty("jsonrpc", "2.0");
        response.add("id", (JsonElement)(id == null ? JsonNull.INSTANCE : id));
        response.add("error", (JsonElement)error);
        connection.sendText(GSON.toJson((JsonElement)response));
    }

    private static void acceptConnections() {
        while (STARTED.get()) {
            try {
                Socket socket = serverSocket.accept();
                if (CONNECTIONS.size() >= 64) {
                    socket.close();
                    continue;
                }
                socket.setTcpNoDelay(true);
                DebugWebSocketConnection connection = new DebugWebSocketConnection(socket, token);
                CONNECTIONS.add(connection);
                Thread thread = new Thread((Runnable)connection, "Apricity-Debug-Connection-" + CONNECTION_SEQUENCE.incrementAndGet());
                thread.setDaemon(true);
                thread.start();
            }
            catch (IOException exception) {
                if (!STARTED.get()) continue;
                ApricityUI.LOGGER.debug("Apricity debugger accept failed", (Throwable)exception);
            }
        }
    }

    private static boolean isEnabled() {
        String override = System.getProperty("apricityui.debug.enabled");
        if (override != null) {
            return Boolean.parseBoolean(override);
        }
        try {
            return AuiServices.config().remoteDebug();
        }
        catch (IllegalStateException unavailableConfig) {
            return !AuiServices.client().isProduction();
        }
    }

    private static String configuredToken() {
        String configured = System.getProperty("apricityui.debug.token");
        if (configured != null && !configured.isBlank()) {
            return configured;
        }
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static void installShutdownHook() {
        if (!SHUTDOWN_HOOK_INSTALLED.compareAndSet(false, true)) {
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(ExternalDebugServer::stop, "Apricity-Debug-Shutdown"));
    }

    private static void writeDiscoveryFile() throws IOException {
        Path file = ExternalDebugServer.resolveDiscoveryFile();
        Files.createDirectories(file.getParent(), new FileAttribute[0]);
        JsonObject discovery = new JsonObject();
        discovery.addProperty("protocolVersion", (Number)1);
        discovery.addProperty("endpoint", ENDPOINT);
        discovery.addProperty("token", token);
        discovery.addProperty("pid", (Number)ProcessHandle.current().pid());
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        Files.writeString(temporary, (CharSequence)GSON.toJson((JsonElement)discovery), StandardCharsets.UTF_8, new OpenOption[0]);
        try {
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (IOException unsupportedAtomicMove) {
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
        }
        discoveryFile = file;
    }

    private static Path resolveDiscoveryFile() {
        try {
            Minecraft minecraft = Minecraft.m_91087_();
            if (minecraft != null && minecraft.f_91069_ != null) {
                return minecraft.f_91069_.toPath().resolve("apricity").resolve("debug.json");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return Path.of("run", "apricity", "debug.json").toAbsolutePath();
    }

    private static void closeServerSocket() {
        ServerSocket socket = serverSocket;
        serverSocket = null;
        if (socket == null) {
            return;
        }
        try {
            socket.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static void deleteDiscoveryFile() {
        Path file = discoveryFile;
        discoveryFile = null;
        if (file == null) {
            return;
        }
        try {
            Files.deleteIfExists(file);
        }
        catch (IOException exception) {
            ApricityUI.LOGGER.debug("Unable to remove Apricity debugger discovery file {}", (Object)file, (Object)exception);
        }
    }

    private static JsonElement requestId(JsonObject request) {
        if (request == null || !request.has("id")) {
            return JsonNull.INSTANCE;
        }
        JsonElement id = request.get("id");
        return id == null ? JsonNull.INSTANCE : id.deepCopy();
    }

    private static void writeResult(DebugWebSocketConnection connection, JsonElement id, JsonElement result) {
        if (connection == null || id == null || !connection.isOpen()) {
            return;
        }
        JsonObject response = new JsonObject();
        response.addProperty("jsonrpc", "2.0");
        response.add("id", id);
        response.add("result", (JsonElement)(result == null ? JsonNull.INSTANCE : result));
        connection.sendText(GSON.toJson((JsonElement)response));
    }

    private record PendingCommand(DebugProtocolSession session, DebugWebSocketConnection connection, JsonObject request) {
        void execute() {
            block6: {
                if (this.request == null) {
                    this.session.close();
                    return;
                }
                JsonElement id = ExternalDebugServer.requestId(this.request);
                boolean notification = !this.request.has("id");
                try {
                    JsonElement result = this.session.handle(this.request);
                    if (!notification) {
                        ExternalDebugServer.writeResult(this.connection, id, result);
                    }
                }
                catch (DebugProtocolException protocolError) {
                    if (!notification) {
                        ExternalDebugServer.writeError(this.connection, id, protocolError.code(), protocolError.getMessage());
                    }
                }
                catch (Throwable failure) {
                    ApricityUI.LOGGER.error("External debugger command failed", failure);
                    if (notification) break block6;
                    ExternalDebugServer.writeError(this.connection, id, -32603, "Internal error");
                }
            }
        }

        void failServerStopped() {
            if (this.request != null && this.request.has("id")) {
                ExternalDebugServer.writeError(this.connection, ExternalDebugServer.requestId(this.request), -32000, "Debug server stopped");
            }
            this.session.close();
        }
    }
}

