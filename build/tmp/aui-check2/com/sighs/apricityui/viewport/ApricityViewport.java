/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWVidMode
 */
package com.sighs.apricityui.viewport;

import com.mojang.blaze3d.platform.Window;
import com.sighs.apricityui.parser.HTML;
import com.sighs.apricityui.spi.AuiServices;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

public record ApricityViewport(int layoutWidth, int layoutHeight, float renderScale, double scissorScale, double zoom) {
    private static final double MAX_DOCUMENT_GUI_SCALE = 5.0;
    private static final int DEFAULT_FIXED_WIDTH = 427;
    private static final int DEFAULT_FIXED_HEIGHT = 249;
    private static final int DEFAULT_BROWSER_WIDTH = 1920;
    private static final int DEFAULT_BROWSER_HEIGHT = 1080;
    private static final String META_NAME = "aui-viewport";
    private static final Object ZOOM_STORE_LOCK = new Object();
    private static final Map<String, State> STATES = new ConcurrentHashMap<String, State>();
    private static final Properties STORED_ZOOMS = new Properties();
    private static volatile boolean zoomStoreLoaded = false;

    public ApricityViewport(int layoutWidth, int layoutHeight, float renderScale, double scissorScale) {
        this(layoutWidth, layoutHeight, renderScale, scissorScale, 1.0);
    }

    public static ApricityViewport resolve(String templatePath, Window window) {
        Spec spec = ApricityViewport.spec(templatePath);
        return spec.resolve(window, spec.initialZoom());
    }

    public static Spec spec(String templatePath) {
        String raw = HTML.findMetaContent(templatePath, META_NAME);
        Map<String, String> options = ApricityViewport.parseOptions(raw);
        String mode = options.getOrDefault("mode", options.getOrDefault("type", "gui")).trim().toLowerCase(Locale.ROOT);
        double initialZoom = ApricityViewport.parseDouble(options.get("zoom"), 1.0);
        double minZoom = ApricityViewport.parseDouble(options.get("min-zoom"), 0.5);
        double maxZoom = ApricityViewport.parseDouble(options.get("max-zoom"), 3.0);
        double zoomStep = ApricityViewport.parseDouble(options.get("zoom-step"), 0.1);
        boolean userScalable = ApricityViewport.parseBoolean(options.get("user-scalable"), true);
        return new Spec(mode, options, initialZoom, minZoom, maxZoom, zoomStep, userScalable);
    }

    private static ApricityViewport resolveBase(String mode, Map<String, String> options, Window window) {
        double actualGuiScale = Math.max(1.0, window.m_85449_());
        return switch (mode) {
            case "window", "native", "screen", "fullscreen" -> ApricityViewport.browser(window, actualGuiScale, options);
            case "browser", "css", "web" -> ApricityViewport.windowViewport(window, actualGuiScale, options);
            case "fixed" -> ApricityViewport.fixed(window, actualGuiScale, options);
            case "gui", "mc", "default", "" -> ApricityViewport.gui(window, actualGuiScale);
            default -> ApricityViewport.gui(window, actualGuiScale);
        };
    }

    private static ApricityViewport applyZoom(ApricityViewport base, double zoom) {
        double safeZoom = zoom > 0.0 && Double.isFinite(zoom) ? zoom : 1.0;
        int width = Math.max(1, (int)Math.round((double)base.layoutWidth() / safeZoom));
        int height = Math.max(1, (int)Math.round((double)base.layoutHeight() / safeZoom));
        return new ApricityViewport(width, height, (float)((double)base.renderScale() * safeZoom), base.scissorScale() * safeZoom, safeZoom);
    }

    private static ApricityViewport gui(Window window, double actualGuiScale) {
        double documentGuiScale = Math.min(actualGuiScale, 5.0);
        float renderScale = (float)(documentGuiScale / actualGuiScale);
        int width = Math.max(1, (int)Math.round((double)window.m_85443_() / documentGuiScale));
        int height = Math.max(1, (int)Math.round((double)window.m_85444_() / documentGuiScale));
        return new ApricityViewport(width, height, renderScale, documentGuiScale);
    }

    private static ApricityViewport browserReference(Window window, double actualGuiScale, Map<String, String> options) {
        GLFWVidMode videoMode = ApricityViewport.resolveVideoMode(window);
        double cssScale = ApricityViewport.browserCssScale(window);
        int physicalWidth = videoMode == null ? 1920 : videoMode.width();
        int physicalHeight = videoMode == null ? 1080 : videoMode.height();
        int width = Math.max(1, ApricityViewport.parseInt(options.get("width"), (int)Math.round((double)physicalWidth / cssScale)));
        int height = Math.max(1, ApricityViewport.parseInt(options.get("height"), (int)Math.round((double)physicalHeight / cssScale)));
        float renderScale = (float)Math.max(1.0E-4, cssScale / actualGuiScale);
        return new ApricityViewport(width, height, renderScale, cssScale);
    }

    private static ApricityViewport browser(Window window, double actualGuiScale, Map<String, String> options) {
        ApricityViewport reference = ApricityViewport.browserReference(window, actualGuiScale, options);
        if (options.containsKey("height")) {
            return reference;
        }
        double cssScale = ApricityViewport.browserCssScale(window);
        int height = reference.layoutHeight();
        if (window != null) {
            try {
                int physicalHeight = window.m_85444_();
                if (physicalHeight > 0) {
                    height = Math.max(1, (int)Math.round((double)physicalHeight / cssScale));
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return new ApricityViewport(reference.layoutWidth(), height, reference.renderScale(), reference.scissorScale());
    }

    private static ApricityViewport windowViewport(Window window, double actualGuiScale, Map<String, String> options) {
        ApricityViewport browserViewport = ApricityViewport.browserReference(window, actualGuiScale, options);
        double guiWidth = Math.max(1.0, (double)window.m_85445_());
        double guiHeight = Math.max(1.0, (double)window.m_85446_());
        float renderScale = (float)Math.max(1.0E-4, guiWidth / Math.max(1.0, (double)browserViewport.layoutWidth()));
        int layoutHeight = Math.max(1, (int)Math.round(guiHeight / (double)renderScale));
        return new ApricityViewport(browserViewport.layoutWidth(), layoutHeight, renderScale, actualGuiScale * (double)renderScale);
    }

    private static double browserCssScale(Window window) {
        if (window == null || AuiServices.client().getWindowHandle() == 0L) {
            return 1.0;
        }
        float[] xScale = new float[1];
        float[] yScale = new float[1];
        try {
            GLFW.glfwGetWindowContentScale((long)AuiServices.client().getWindowHandle(), (float[])xScale, (float[])yScale);
            double scale = Math.max(xScale[0], yScale[0]);
            return scale > 0.0 && Double.isFinite(scale) ? scale : 1.0;
        }
        catch (Throwable ignored) {
            return 1.0;
        }
    }

    private static ApricityViewport fixed(Window window, double actualGuiScale, Map<String, String> options) {
        float renderScale;
        int width = ApricityViewport.parseInt(options.get("width"), 427);
        int height = ApricityViewport.parseInt(options.get("height"), 249);
        width = Math.max(1, width);
        height = Math.max(1, height);
        String scaleOption = options.getOrDefault("scale", "1").trim().toLowerCase(Locale.ROOT);
        if ("fit".equals(scaleOption) || "contain".equals(scaleOption)) {
            double guiWidth = (double)window.m_85443_() / actualGuiScale;
            double guiHeight = (double)window.m_85444_() / actualGuiScale;
            renderScale = (float)Math.max(1.0E-4, Math.min(guiWidth / (double)width, guiHeight / (double)height));
        } else {
            renderScale = "window".equals(scaleOption) || "native".equals(scaleOption) ? (float)(1.0 / actualGuiScale) : ("gui".equals(scaleOption) || "mc".equals(scaleOption) ? 1.0f : (float)Math.max(1.0E-4, ApricityViewport.parseDouble(scaleOption, 1.0)));
        }
        return new ApricityViewport(width, height, renderScale, actualGuiScale * (double)renderScale);
    }

    private static GLFWVidMode resolveVideoMode(Window window) {
        if (window == null || AuiServices.client().getWindowHandle() == 0L) {
            return null;
        }
        try {
            long monitor = GLFW.glfwGetWindowMonitor((long)AuiServices.client().getWindowHandle());
            if (monitor == 0L) {
                monitor = GLFW.glfwGetPrimaryMonitor();
            }
            return monitor == 0L ? null : GLFW.glfwGetVideoMode((long)monitor);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static Map<String, String> parseOptions(String raw) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        if (raw == null || raw.isBlank()) {
            return options;
        }
        for (String token : raw.split("[,;]")) {
            String trimmed;
            if (token == null || (trimmed = token.trim()).isEmpty()) continue;
            int equals = trimmed.indexOf(61);
            if (equals < 0) {
                options.putIfAbsent("mode", trimmed);
                continue;
            }
            String key = trimmed.substring(0, equals).trim().toLowerCase(Locale.ROOT);
            String value = trimmed.substring(equals + 1).trim();
            if (key.isEmpty()) continue;
            options.put(key, value);
        }
        return options;
    }

    private static int parseInt(String raw, int fallback) {
        return (int)Math.round(ApricityViewport.parseDouble(raw, fallback));
    }

    private static double parseDouble(String raw, double fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            double value = Double.parseDouble(raw.trim());
            return Double.isFinite(value) ? value : fallback;
        }
        catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static boolean parseBoolean(String raw, boolean fallback) {
        String normalized;
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        return switch (normalized = raw.trim().toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1", "on" -> true;
            case "false", "no", "0", "off" -> false;
            default -> fallback;
        };
    }

    private static String normalizeTemplatePath(String templatePath) {
        return templatePath == null ? "" : templatePath.trim().replace('\\', '/');
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static double readStoredZoom(String templatePath, double fallback) {
        ApricityViewport.ensureZoomStoreLoaded();
        Object object = ZOOM_STORE_LOCK;
        synchronized (object) {
            return ApricityViewport.parseDouble(STORED_ZOOMS.getProperty(templatePath), fallback);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void writeStoredZoom(String templatePath, double zoom) {
        if (templatePath == null || templatePath.isBlank()) {
            return;
        }
        ApricityViewport.ensureZoomStoreLoaded();
        Object object = ZOOM_STORE_LOCK;
        synchronized (object) {
            STORED_ZOOMS.setProperty(templatePath, String.format(Locale.ROOT, "%.6f", zoom));
            Path file = ApricityViewport.zoomStorePath();
            try {
                Files.createDirectories(file.getParent(), new FileAttribute[0]);
                try (OutputStream out = Files.newOutputStream(file, new OpenOption[0]);){
                    STORED_ZOOMS.store(out, "ApricityUI viewport zoom values");
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void ensureZoomStoreLoaded() {
        if (zoomStoreLoaded) {
            return;
        }
        Object object = ZOOM_STORE_LOCK;
        synchronized (object) {
            if (zoomStoreLoaded) {
                return;
            }
            Path file = ApricityViewport.zoomStorePath();
            if (Files.exists(file, new LinkOption[0]) && Files.isRegularFile(file, new LinkOption[0])) {
                try (InputStream in = Files.newInputStream(file, new OpenOption[0]);){
                    STORED_ZOOMS.load(in);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
            zoomStoreLoaded = true;
        }
    }

    private static Path zoomStorePath() {
        return ApricityViewport.configDir().resolve("apricityui").resolve("viewport-zoom.properties");
    }

    private static Path configDir() {
        Path dir = AuiServices.client().getConfigDirectory();
        return dir != null ? dir : Path.of("config", new String[0]).toAbsolutePath().normalize();
    }

    private static double sanitizeZoom(double value, double fallback) {
        return value > 0.0 && Double.isFinite(value) ? value : fallback;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public record Spec(String mode, Map<String, String> options, double initialZoom, double minZoom, double maxZoom, double zoomStep, boolean userScalable) {
        public Spec {
            mode = mode == null || mode.isBlank() ? "gui" : mode.trim().toLowerCase(Locale.ROOT);
            options = options == null ? Map.of() : Map.copyOf(options);
            double low = ApricityViewport.sanitizeZoom(minZoom, 0.1);
            double high = ApricityViewport.sanitizeZoom(maxZoom, 10.0);
            if (high < low) {
                double temp = low;
                low = high;
                high = temp;
            }
            minZoom = low;
            maxZoom = high;
            initialZoom = ApricityViewport.clamp(ApricityViewport.sanitizeZoom(initialZoom, 1.0), minZoom, maxZoom);
            zoomStep = ApricityViewport.clamp(ApricityViewport.sanitizeZoom(zoomStep, 0.1), 0.01, 5.0);
        }

        public ApricityViewport resolve(Window window, double zoom) {
            return ApricityViewport.applyZoom(ApricityViewport.resolveBase(this.mode, this.options, window), ApricityViewport.clamp(ApricityViewport.sanitizeZoom(zoom, this.initialZoom), this.minZoom, this.maxZoom));
        }

        public ApricityViewport resolveHeadless(int availableWidth, int availableHeight, double zoom) {
            ApricityViewport base;
            int fallbackWidth = Math.max(1, availableWidth);
            int fallbackHeight = Math.max(1, availableHeight);
            if ("fixed".equals(this.mode)) {
                String scaleOption;
                int width = Math.max(1, ApricityViewport.parseInt(this.options.get("width"), 427));
                int height = Math.max(1, ApricityViewport.parseInt(this.options.get("height"), 249));
                double scale = switch (scaleOption = this.options.getOrDefault("scale", "1").trim().toLowerCase(Locale.ROOT)) {
                    case "fit", "contain" -> Math.min((double)fallbackWidth / (double)width, (double)fallbackHeight / (double)height);
                    case "window", "native", "gui", "mc" -> 1.0;
                    default -> Math.max(1.0E-4, ApricityViewport.parseDouble(scaleOption, 1.0));
                };
                base = new ApricityViewport(width, height, (float)scale, scale);
            } else if (Spec.isWindowMode(this.mode)) {
                int width = Math.max(1, ApricityViewport.parseInt(this.options.get("width"), 1920));
                int height = Math.max(1, ApricityViewport.parseInt(this.options.get("height"), fallbackHeight));
                base = new ApricityViewport(width, height, 1.0f, 1.0);
            } else if (Spec.isBrowserMode(this.mode)) {
                int width = Math.max(1, ApricityViewport.parseInt(this.options.get("width"), 1920));
                double scale = Math.max(1.0E-4, (double)fallbackWidth / (double)width);
                int height = Math.max(1, (int)Math.round((double)fallbackHeight / scale));
                base = new ApricityViewport(width, height, (float)scale, scale);
            } else {
                base = new ApricityViewport(fallbackWidth, fallbackHeight, 1.0f, 1.0);
            }
            return ApricityViewport.applyZoom(base, ApricityViewport.clamp(ApricityViewport.sanitizeZoom(zoom, this.initialZoom), this.minZoom, this.maxZoom));
        }

        private static boolean isBrowserMode(String mode) {
            return "browser".equals(mode) || "css".equals(mode) || "web".equals(mode);
        }

        private static boolean isWindowMode(String mode) {
            return "window".equals(mode) || "native".equals(mode) || "screen".equals(mode) || "fullscreen".equals(mode);
        }

        public State createState() {
            return new State(this);
        }

        public State createState(String templatePath) {
            String key = ApricityViewport.normalizeTemplatePath(templatePath);
            return STATES.compute(key, (ignored, existing) -> {
                if (existing == null) {
                    return new State(key, this);
                }
                existing.updateSpec(this);
                return existing;
            });
        }
    }

    public static final class State {
        private final String templatePath;
        private volatile Spec spec;
        private double zoom;

        private State(Spec spec) {
            this("", spec);
        }

        private State(String templatePath, Spec spec) {
            this.templatePath = ApricityViewport.normalizeTemplatePath(templatePath);
            this.spec = spec;
            this.zoom = ApricityViewport.readStoredZoom(this.templatePath, spec.initialZoom());
            this.zoom = ApricityViewport.clamp(ApricityViewport.sanitizeZoom(this.zoom, spec.initialZoom()), spec.minZoom(), spec.maxZoom());
        }

        public ApricityViewport resolve(Window window) {
            return this.spec.resolve(window, this.zoom);
        }

        public ApricityViewport resolveHeadless(int availableWidth, int availableHeight) {
            return this.spec.resolveHeadless(availableWidth, availableHeight, this.zoom);
        }

        public synchronized boolean zoomIn() {
            if (!this.spec.userScalable()) {
                return false;
            }
            return this.setZoom(this.zoom + this.spec.zoomStep());
        }

        public synchronized boolean zoomOut() {
            if (!this.spec.userScalable()) {
                return false;
            }
            return this.setZoom(this.zoom - this.spec.zoomStep());
        }

        public synchronized boolean resetZoom() {
            if (!this.spec.userScalable()) {
                return false;
            }
            return this.setZoom(this.spec.initialZoom());
        }

        public boolean canUserScale() {
            return this.spec.userScalable();
        }

        public synchronized double zoom() {
            return this.zoom;
        }

        private synchronized void updateSpec(Spec nextSpec) {
            if (nextSpec == null) {
                return;
            }
            this.spec = nextSpec;
            double clamped = ApricityViewport.clamp(ApricityViewport.sanitizeZoom(this.zoom, nextSpec.initialZoom()), nextSpec.minZoom(), nextSpec.maxZoom());
            if (Math.abs(clamped - this.zoom) >= 1.0E-6) {
                this.zoom = clamped;
                ApricityViewport.writeStoredZoom(this.templatePath, this.zoom);
            }
        }

        public synchronized boolean setZoom(double nextZoom) {
            double clamped = ApricityViewport.clamp(ApricityViewport.sanitizeZoom(nextZoom, this.zoom), this.spec.minZoom(), this.spec.maxZoom());
            if (Math.abs(clamped - this.zoom) < 1.0E-6) {
                return false;
            }
            this.zoom = clamped;
            ApricityViewport.writeStoredZoom(this.templatePath, this.zoom);
            return true;
        }
    }
}

