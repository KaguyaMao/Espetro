/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.sighs.apricityui.client;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.parser.HTML;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="apricityui", value={Dist.CLIENT})
public final class ClientWptSnapshotRunner {
    private static final String INPUT = "AUI_WPT_CLIENT_INPUT";
    private static final String OUTPUT = "AUI_WPT_CLIENT_OUTPUT";
    private static final String EXIT = "AUI_WPT_CLIENT_EXIT_ON_FINISH";
    private static final String TIMEOUT_SECONDS = "AUI_WPT_CLIENT_TIMEOUT_SECONDS";
    private static final String STALL_TIMEOUT_SECONDS = "AUI_WPT_CLIENT_STALL_TIMEOUT_SECONDS";
    private static final ArrayDeque<Case> CASES = new ArrayDeque();
    private static boolean initialized;
    private static boolean completed;
    private static long ticks;
    private static volatile int totalCases;
    private static volatile int processedCases;
    private static volatile long activeCaseStartedAt;
    private static volatile String activeCaseId;
    private static BufferedWriter output;

    private ClientWptSnapshotRunner() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || completed || !ClientWptSnapshotRunner.enabled() || Minecraft.m_91087_() == null) {
            return;
        }
        if (!initialized) {
            if (++ticks < 10L) {
                return;
            }
            ClientWptSnapshotRunner.initialize();
            return;
        }
        for (int index = 0; index < 10 && !CASES.isEmpty(); ++index) {
            Case testCase = CASES.removeFirst();
            activeCaseId = testCase.id;
            activeCaseStartedAt = System.nanoTime();
            ClientWptSnapshotRunner.capture(testCase);
            activeCaseStartedAt = 0L;
            activeCaseId = null;
            if (++processedCases % 500 != 0 && !CASES.isEmpty()) continue;
            ApricityUI.LOGGER.info("[AUI WPT] progress processed={} total={} remaining={}", new Object[]{processedCases, totalCases, CASES.size()});
        }
        if (CASES.isEmpty()) {
            ClientWptSnapshotRunner.finish();
        }
    }

    private static boolean enabled() {
        return System.getenv(INPUT) != null && System.getenv(OUTPUT) != null;
    }

    private static void initialize() {
        initialized = true;
        try {
            Size.setViewportOverride(ClientWptSnapshotRunner.environmentInt("AUI_WPT_VIEWPORT_WIDTH", 800), ClientWptSnapshotRunner.environmentInt("AUI_WPT_VIEWPORT_HEIGHT", 600));
            for (String line : Files.readAllLines(Path.of(System.getenv(INPUT), new String[0]), StandardCharsets.UTF_8)) {
                int tab = line.indexOf(9);
                if (tab <= 0) continue;
                CASES.add(new Case(line.substring(0, tab), Path.of(line.substring(tab + 1), new String[0])));
            }
            totalCases = CASES.size();
            Path target = Path.of(System.getenv(OUTPUT), new String[0]);
            Files.createDirectories(target.getParent(), new FileAttribute[0]);
            output = Files.newBufferedWriter(target, StandardCharsets.UTF_8, new OpenOption[0]);
            ApricityUI.LOGGER.info("[AUI WPT] client batch started: {} cases", (Object)CASES.size());
            ClientWptSnapshotRunner.startWatchdog();
        }
        catch (Exception exception) {
            ApricityUI.LOGGER.error("[AUI WPT] client batch initialization failed", (Throwable)exception);
            ClientWptSnapshotRunner.finish();
        }
    }

    private static void startWatchdog() {
        int timeoutSeconds = ClientWptSnapshotRunner.environmentInt(TIMEOUT_SECONDS, 900);
        int stallTimeoutSeconds = ClientWptSnapshotRunner.environmentInt(STALL_TIMEOUT_SECONDS, 15);
        Thread watchdog = new Thread(() -> {
            long batchStartedAt = System.nanoTime();
            String reason = null;
            while (!completed && reason == null) {
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException ignored) {
                    return;
                }
                long now = System.nanoTime();
                if (activeCaseStartedAt != 0L && now - activeCaseStartedAt >= (long)stallTimeoutSeconds * 1000000000L) {
                    reason = "case stalled for " + stallTimeoutSeconds + " seconds: " + activeCaseId;
                    continue;
                }
                if (now - batchStartedAt < (long)timeoutSeconds * 1000000000L) continue;
                reason = "batch timed out after " + timeoutSeconds + " seconds";
            }
            if (completed) {
                return;
            }
            ApricityUI.LOGGER.error("[AUI WPT] client watchdog stopped at {}/{} cases: {}", new Object[]{processedCases, totalCases, reason});
            try {
                if (output != null) {
                    output.flush();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            Runtime.getRuntime().halt(124);
        }, "aui-wpt-watchdog");
        watchdog.setDaemon(true);
        watchdog.start();
    }

    private static void capture(Case testCase) {
        try {
            String path = "wpt-client/" + testCase.id;
            HTML.putTemple(path, Files.readString(testCase.source, StandardCharsets.UTF_8));
            Document document = new Document(path, false);
            document.refresh();
            String snapshot = document == null ? "{\"nodes\":[]}" : ClientWptSnapshotRunner.snapshot(document);
            ClientWptSnapshotRunner.write(testCase.id, "pass", Base64.getUrlEncoder().withoutPadding().encodeToString(snapshot.getBytes(StandardCharsets.UTF_8)));
        }
        catch (Throwable throwable) {
            ClientWptSnapshotRunner.write(testCase.id, "aui-runtime-unsupported", throwable.getClass().getSimpleName());
        }
    }

    private static String snapshot(Document document) {
        StringBuilder value = new StringBuilder("{\"nodes\":[");
        boolean first = true;
        for (Element element : document.querySelectorAll("*")) {
            if (!first) {
                value.append(',');
            }
            first = false;
            Element.DOMRect rect = element.getBoundingClientRect();
            value.append("{\"tag\":\"").append(ClientWptSnapshotRunner.escape(element.getNodeName().toLowerCase())).append("\",\"id\":\"").append(ClientWptSnapshotRunner.escape(element.getAttribute("id"))).append("\",\"rect\":[").append(ClientWptSnapshotRunner.number(rect.x)).append(',').append(ClientWptSnapshotRunner.number(rect.y)).append(',').append(ClientWptSnapshotRunner.number(rect.width)).append(',').append(ClientWptSnapshotRunner.number(rect.height)).append("]}");
        }
        return value.append("]}").toString();
    }

    private static String number(double value) {
        return String.format(Locale.ROOT, "%.4f", value);
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static void write(String id, String status, String detail) {
        try {
            if (output == null) {
                return;
            }
            output.write(id.replace('\t', ' '));
            output.write(9);
            output.write(status);
            output.write(9);
            output.write(detail == null ? "" : detail.replace('\t', ' ').replace('\n', ' '));
            output.newLine();
            output.flush();
        }
        catch (IOException exception) {
            ApricityUI.LOGGER.error("[AUI WPT] result write failed", (Throwable)exception);
        }
    }

    private static void finish() {
        if (completed) {
            return;
        }
        completed = true;
        try {
            if (output != null) {
                output.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        Size.clearViewportOverride();
        ApricityUI.LOGGER.info("[AUI WPT] client batch complete");
        if (Boolean.parseBoolean(System.getenv(EXIT)) && Minecraft.m_91087_() != null) {
            Minecraft.m_91087_().m_91395_();
        }
    }

    private static int environmentInt(String name, int fallback) {
        try {
            String value = System.getenv(name);
            return value == null || value.isBlank() ? fallback : Integer.parseInt(value);
        }
        catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private record Case(String id, Path source) {
    }
}

