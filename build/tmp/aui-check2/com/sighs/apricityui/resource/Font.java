/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.resource;

import com.sighs.apricityui.ApricityUI;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.CallSite;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class Font {
    private static final float BASE_FONT_SIZE = 48.0f;
    private static final Map<String, java.awt.Font> FONTS = Collections.synchronizedMap(new HashMap());
    private static final String DEFAULT_KEY = "default";
    private static final int SINGLE_FAMILY_CACHE_LIMIT = 128;
    private static final int BASE_FONT_CHAIN_CACHE_LIMIT = 64;
    private static final int RUN_PLAN_CACHE_LIMIT = 512;
    private static final Map<String, Optional<java.awt.Font>> SINGLE_FAMILY_CACHE = Font.createLruCache(128);
    private static final Map<String, List<java.awt.Font>> BASE_FONT_CHAIN_CACHE = Font.createLruCache(64);
    private static final Map<String, List<java.awt.Font>> SINGLE_FAMILY_CHAIN_CACHE = Font.createLruCache(64);
    private static final Map<DerivedFontKey, java.awt.Font> DERIVED_FONT_CACHE = new ConcurrentHashMap<DerivedFontKey, java.awt.Font>();
    private static final Map<RunPlanKey, List<FontRun>> RUN_PLAN_CACHE = Font.createLruCache(512);
    private static final Set<String> UNAVAILABLE_FAMILIES = ConcurrentHashMap.newKeySet();
    private static final AtomicLong METRICS_REVISION = new AtomicLong(1L);
    private static final Map<String, String> GENERIC_FAMILY_MAPPING = Map.ofEntries(Map.entry("serif", "Serif"), Map.entry("sans-serif", "SansSerif"), Map.entry("monospace", "Monospaced"), Map.entry("ui-serif", "Serif"), Map.entry("ui-sans-serif", "SansSerif"), Map.entry("ui-monospace", "Monospaced"), Map.entry("ui-rounded", "SansSerif"), Map.entry("cursive", "SansSerif"), Map.entry("fantasy", "SansSerif"), Map.entry("system-ui", "Dialog"), Map.entry("emoji", "Dialog"), Map.entry("math", "Serif"), Map.entry("fangsong", "Serif"));

    public static boolean registerFont(String key, InputStream stream) {
        if (key == null || key.isBlank() || stream == null) {
            ApricityUI.LOGGER.warn("[AUI Font] invalid font registration request family={} streamPresent={}", (Object)key, (Object)(stream != null ? 1 : 0));
            return false;
        }
        try {
            java.awt.Font base = java.awt.Font.createFont(0, stream);
            java.awt.Font derived = base.deriveFont(0, 48.0f);
            Font.registerResolvedFont(key, derived);
            return true;
        }
        catch (FontFormatException | IOException e) {
            ApricityUI.LOGGER.error("[AUI Font] failed to decode font family={}", (Object)key, (Object)e);
            return false;
        }
    }

    public static boolean registerFont(String key, File fontFile) {
        if (key == null || key.isBlank() || fontFile == null || !fontFile.exists()) {
            ApricityUI.LOGGER.warn("[AUI Font] font file is missing family={} file={}", (Object)key, (Object)fontFile);
            return false;
        }
        try {
            java.awt.Font base = java.awt.Font.createFont(0, fontFile);
            java.awt.Font derived = base.deriveFont(0, 48.0f);
            Font.registerResolvedFont(key, derived);
            return true;
        }
        catch (Exception e) {
            ApricityUI.LOGGER.error("[AUI Font] failed to load font family={} file={}", new Object[]{key, fontFile, e});
            return false;
        }
    }

    public static boolean registerFont(String key, Path path) {
        if (key == null || key.isBlank() || path == null || !Files.exists(path, new LinkOption[0])) {
            ApricityUI.LOGGER.warn("[AUI Font] font path is missing family={} path={}", (Object)key, (Object)path);
            return false;
        }
        return Font.registerFont(key, path.toFile());
    }

    public static java.awt.Font getBaseFont(String key) {
        if (key == null || key.isBlank()) {
            return FONTS.get(DEFAULT_KEY);
        }
        java.awt.Font font = FONTS.get(key);
        if (font != null) {
            return font;
        }
        String cleanKey = Font.cleanFamilyName(key);
        font = FONTS.get(cleanKey);
        if (font != null) {
            return font;
        }
        font = FONTS.get(Font.toLookupKey(cleanKey));
        return font != null ? font : FONTS.get(DEFAULT_KEY);
    }

    public static boolean isRegistered(String key) {
        String cleanKey = Font.cleanFamilyName(key);
        return !cleanKey.isEmpty() && (FONTS.containsKey(cleanKey) || FONTS.containsKey(Font.toLookupKey(cleanKey)));
    }

    public static java.awt.Font resolveBaseFont(String rawFamilyChain) {
        List<java.awt.Font> chain = Font.resolveBaseFontChain(rawFamilyChain);
        return chain.isEmpty() ? FONTS.get(DEFAULT_KEY) : chain.get(0);
    }

    public static List<java.awt.Font> resolveBaseFontChain(String rawFamilyChain) {
        String key;
        String cacheKey = Font.normalizeFamilyChain(rawFamilyChain);
        List<java.awt.Font> cached = BASE_FONT_CHAIN_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        ArrayList<java.awt.Font> result = new ArrayList<java.awt.Font>();
        LinkedHashSet<CallSite> seen = new LinkedHashSet<CallSite>();
        for (String family : Font.parseFontFamilies(rawFamilyChain)) {
            for (java.awt.Font font : Font.resolveSingleFamilyChain(family)) {
                String key2;
                if (font == null || !seen.add((CallSite)((Object)(key2 = font.getFontName(Locale.ROOT) + "|" + font.getFamily(Locale.ROOT))))) continue;
                result.add(font);
            }
        }
        java.awt.Font fallback = FONTS.get(DEFAULT_KEY);
        if (fallback != null && seen.add((CallSite)((Object)(key = fallback.getFontName(Locale.ROOT) + "|" + fallback.getFamily(Locale.ROOT))))) {
            result.add(fallback);
        }
        List<java.awt.Font> immutable = List.copyOf(result);
        BASE_FONT_CHAIN_CACHE.put(cacheKey, immutable);
        return immutable;
    }

    public static List<FontRun> planFontRuns(String rawFamilyChain, int fontStyle, float size, String content) {
        int cp;
        if (content == null || content.isEmpty()) {
            return List.of();
        }
        RunPlanKey cacheKey = new RunPlanKey(Font.normalizeFamilyChain(rawFamilyChain), fontStyle, Float.floatToIntBits(size), content);
        List<FontRun> cached = RUN_PLAN_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        List<java.awt.Font> baseFonts = Font.resolveBaseFontChain(rawFamilyChain);
        if (baseFonts.isEmpty()) {
            return List.of();
        }
        ArrayList<java.awt.Font> fonts = new ArrayList<java.awt.Font>(baseFonts.size());
        for (java.awt.Font font : baseFonts) {
            fonts.add(Font.deriveCachedFont(font, fontStyle, size));
        }
        ArrayList<FontRun> runs = new ArrayList<FontRun>();
        StringBuilder current = new StringBuilder();
        java.awt.Font currentFont = null;
        HashMap<Integer, java.awt.Font> codePointFontCache = new HashMap<Integer, java.awt.Font>();
        for (int i = 0; i < content.length(); i += Character.charCount(cp)) {
            cp = content.codePointAt(i);
            java.awt.Font font = codePointFontCache.computeIfAbsent(cp, key -> Font.pickDisplayFont(fonts, key));
            String glyph = new String(Character.toChars(cp));
            if (currentFont != null && currentFont.equals(font)) {
                current.append(glyph);
                continue;
            }
            if (currentFont != null && current.length() > 0) {
                runs.add(new FontRun(currentFont, current.toString()));
            }
            current.setLength(0);
            current.append(glyph);
            currentFont = font;
        }
        if (currentFont != null && current.length() > 0) {
            runs.add(new FontRun(currentFont, current.toString()));
        }
        List<FontRun> immutable = List.copyOf(runs);
        RUN_PLAN_CACHE.put(cacheKey, immutable);
        return immutable;
    }

    public static List<String> parseFontFamilies(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        ArrayList<String> result = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        char quote = '\u0000';
        for (int i = 0; i < raw.length(); ++i) {
            char c = raw.charAt(i);
            if (quote != '\u0000') {
                if (c == quote) {
                    quote = '\u0000';
                    continue;
                }
                current.append(c);
                continue;
            }
            if (c == '\'' || c == '\"') {
                quote = c;
                continue;
            }
            if (c == ',') {
                Font.appendFamily(result, current);
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        Font.appendFamily(result, current);
        return result;
    }

    public static float getBaseFontSize() {
        return 48.0f;
    }

    public static long getMetricsRevision() {
        return METRICS_REVISION.get();
    }

    public static void clear() {
        FONTS.clear();
        UNAVAILABLE_FAMILIES.clear();
        Font.clearResolutionCaches();
        FONTS.put(DEFAULT_KEY, new java.awt.Font("Microsoft YaHei", 0, 48));
        METRICS_REVISION.incrementAndGet();
    }

    public static void prepareReload() {
        UNAVAILABLE_FAMILIES.clear();
        Font.clearResolutionCaches();
        if (!FONTS.containsKey(DEFAULT_KEY)) {
            FONTS.put(DEFAULT_KEY, new java.awt.Font("Microsoft YaHei", 0, 48));
        }
        METRICS_REVISION.incrementAndGet();
    }

    private static java.awt.Font resolveSingleFamily(String family) {
        if (family == null || family.isBlank()) {
            return null;
        }
        String cleanFamily = Font.cleanFamilyName(family);
        Optional<java.awt.Font> cached = SINGLE_FAMILY_CACHE.get(cleanFamily);
        if (cached != null) {
            return cached.orElse(null);
        }
        java.awt.Font alias = Font.resolveKnownWebFontAlias(cleanFamily);
        if (alias != null) {
            SINGLE_FAMILY_CACHE.put(cleanFamily, Optional.of(alias));
            return alias;
        }
        java.awt.Font registered = FONTS.get(cleanFamily);
        if (registered == null) {
            registered = FONTS.get(Font.toLookupKey(cleanFamily));
        }
        if (registered != null) {
            SINGLE_FAMILY_CACHE.put(cleanFamily, Optional.of(registered));
            return registered;
        }
        java.awt.Font genericResolved = Font.resolveGenericFamily(cleanFamily);
        if (genericResolved != null) {
            SINGLE_FAMILY_CACHE.put(cleanFamily, Optional.of(genericResolved));
            return genericResolved;
        }
        String genericMapped = GENERIC_FAMILY_MAPPING.get(cleanFamily.toLowerCase(Locale.ROOT));
        if (genericMapped != null) {
            java.awt.Font resolved = new java.awt.Font(genericMapped, 0, 48);
            SINGLE_FAMILY_CACHE.put(cleanFamily, Optional.of(resolved));
            return resolved;
        }
        java.awt.Font systemFont = new java.awt.Font(cleanFamily, 0, 48);
        if (!"Dialog".equalsIgnoreCase(systemFont.getFamily(Locale.ROOT)) || Font.isDialogFamily(cleanFamily)) {
            SINGLE_FAMILY_CACHE.put(cleanFamily, Optional.of(systemFont));
            return systemFont;
        }
        SINGLE_FAMILY_CACHE.put(cleanFamily, Optional.empty());
        if (UNAVAILABLE_FAMILIES.add(cleanFamily)) {
            ApricityUI.LOGGER.warn("[AUI Font] font family unavailable, using fallback family={}", (Object)cleanFamily);
        }
        return null;
    }

    private static java.awt.Font resolveKnownWebFontAlias(String family) {
        String normalized = Font.cleanFamilyName(family).toLowerCase(Locale.ROOT);
        if ("chakra petch".equals(normalized)) {
            return Font.resolveInstalledFont("Chakra Petch");
        }
        if ("rajdhani".equals(normalized)) {
            return Font.resolveInstalledFont("Rajdhani");
        }
        return null;
    }

    private static List<java.awt.Font> resolveSingleFamilyChain(String family) {
        if (family == null || family.isBlank()) {
            return List.of();
        }
        String cleanFamily = Font.cleanFamilyName(family);
        List<java.awt.Font> cached = SINGLE_FAMILY_CHAIN_CACHE.get(cleanFamily);
        if (cached != null) {
            return cached;
        }
        java.awt.Font primary = Font.resolveSingleFamily(cleanFamily);
        if (primary == null) {
            SINGLE_FAMILY_CHAIN_CACHE.put(cleanFamily, List.of());
            return List.of();
        }
        ArrayList<java.awt.Font> chain = new ArrayList<java.awt.Font>();
        chain.add(primary);
        if (Font.isSansGeneric(cleanFamily)) {
            Font.addInstalledFont(chain, "Noto Sans SC");
            Font.addInstalledFont(chain, "Segoe UI Symbol");
            Font.addInstalledFont(chain, "Segoe UI Emoji");
        }
        List<java.awt.Font> immutable = List.copyOf(chain);
        SINGLE_FAMILY_CHAIN_CACHE.put(cleanFamily, immutable);
        return immutable;
    }

    public static double measureFontRuns(List<FontRun> runs, Function<java.awt.Font, FontMetrics> metricsProvider, double letterSpacing, boolean includeTrailingSpacing) {
        if (runs == null || runs.isEmpty() || metricsProvider == null) {
            return 0.0;
        }
        double width = 0.0;
        int glyphCount = 0;
        boolean spaced = Math.abs(letterSpacing) > 1.0E-6;
        for (FontRun run : runs) {
            int codePoint;
            FontMetrics metrics;
            if (run == null || run.font() == null || run.text() == null || run.text().isEmpty() || (metrics = metricsProvider.apply(run.font())) == null) continue;
            if (!spaced) {
                width += (double)metrics.stringWidth(run.text());
                continue;
            }
            for (int offset = 0; offset < run.text().length(); offset += Character.charCount(codePoint)) {
                codePoint = run.text().codePointAt(offset);
                width += (double)metrics.stringWidth(new String(Character.toChars(codePoint)));
                ++glyphCount;
            }
        }
        if (spaced && glyphCount > 0) {
            int spacingCount = includeTrailingSpacing ? glyphCount : glyphCount - 1;
            width += letterSpacing * (double)Math.max(0, spacingCount);
        }
        return Math.max(0.0, width);
    }

    public static double measureFontRuns(List<FontRun> runs, FontRenderContext renderContext, double letterSpacing, boolean includeTrailingSpacing) {
        if (runs == null || runs.isEmpty() || renderContext == null) {
            return 0.0;
        }
        double width = 0.0;
        int glyphCount = 0;
        boolean spaced = Math.abs(letterSpacing) > 1.0E-6;
        for (FontRun run : runs) {
            int codePoint;
            if (run == null || run.font() == null || run.text() == null || run.text().isEmpty()) continue;
            if (!spaced) {
                width += run.font().getStringBounds(run.text(), renderContext).getWidth();
                continue;
            }
            for (int offset = 0; offset < run.text().length(); offset += Character.charCount(codePoint)) {
                codePoint = run.text().codePointAt(offset);
                String glyph = new String(Character.toChars(codePoint));
                width += run.font().getStringBounds(glyph, renderContext).getWidth();
                ++glyphCount;
            }
        }
        if (spaced && glyphCount > 0) {
            int spacingCount = includeTrailingSpacing ? glyphCount : glyphCount - 1;
            width += letterSpacing * (double)Math.max(0, spacingCount);
        }
        return Math.max(0.0, width);
    }

    private static java.awt.Font resolveGenericFamily(String family) {
        String normalized = Font.cleanFamilyName(family).toLowerCase(Locale.ROOT);
        if (!Font.isSansGeneric(normalized)) {
            return null;
        }
        java.awt.Font browserSans = Font.resolveInstalledFont("Sans Serif Collection");
        if (browserSans != null) {
            return browserSans;
        }
        java.awt.Font arial = Font.resolveInstalledFont("Arial");
        if (arial != null) {
            return arial;
        }
        return new java.awt.Font("SansSerif", 0, 48);
    }

    private static boolean isSansGeneric(String family) {
        String normalized = Font.cleanFamilyName(family).toLowerCase(Locale.ROOT);
        return normalized.equals("sans-serif") || normalized.equals("ui-sans-serif") || normalized.equals("ui-rounded") || normalized.equals("cursive") || normalized.equals("fantasy");
    }

    private static java.awt.Font resolveInstalledFont(String family) {
        if (family == null || family.isBlank()) {
            return null;
        }
        java.awt.Font font = new java.awt.Font(family, 0, 48);
        return "Dialog".equalsIgnoreCase(font.getFamily(Locale.ROOT)) ? null : font;
    }

    private static void addInstalledFont(List<java.awt.Font> fonts, String family) {
        java.awt.Font font = Font.resolveInstalledFont(family);
        if (font == null) {
            return;
        }
        String key = font.getFontName(Locale.ROOT) + "|" + font.getFamily(Locale.ROOT);
        for (java.awt.Font existing : fonts) {
            String existingKey;
            if (existing == null || !(existingKey = existing.getFontName(Locale.ROOT) + "|" + existing.getFamily(Locale.ROOT)).equals(key)) continue;
            return;
        }
        fonts.add(font);
    }

    private static java.awt.Font pickDisplayFont(List<java.awt.Font> fonts, int codePoint) {
        if (fonts == null || fonts.isEmpty()) {
            return FONTS.get(DEFAULT_KEY);
        }
        for (java.awt.Font font : fonts) {
            if (font == null || !font.canDisplay(codePoint)) continue;
            return font;
        }
        java.awt.Font fallback = fonts.get(fonts.size() - 1);
        return fallback != null ? fallback : FONTS.get(DEFAULT_KEY);
    }

    private static void registerResolvedFont(String key, java.awt.Font derived) {
        String cleanKey = Font.cleanFamilyName(key);
        if (cleanKey.isEmpty() || derived == null) {
            return;
        }
        FONTS.put(cleanKey, derived);
        FONTS.put(Font.toLookupKey(cleanKey), derived);
        Font.clearResolutionCaches();
        METRICS_REVISION.incrementAndGet();
    }

    private static boolean isDialogFamily(String family) {
        String normalized = Font.cleanFamilyName(family).toLowerCase(Locale.ROOT);
        return normalized.equals("dialog") || normalized.equals("dialoginput");
    }

    private static void appendFamily(List<String> result, StringBuilder current) {
        String family = Font.cleanFamilyName(current == null ? null : current.toString());
        if (!family.isEmpty()) {
            result.add(family);
        }
    }

    private static String cleanFamilyName(String key) {
        if (key == null) {
            return "";
        }
        String clean = key.trim();
        if (clean.length() >= 2) {
            char first = clean.charAt(0);
            char last = clean.charAt(clean.length() - 1);
            if (first == '\'' && last == '\'' || first == '\"' && last == '\"') {
                clean = clean.substring(1, clean.length() - 1).trim();
            }
        }
        return clean.replace("\"", "").replace("'", "").trim();
    }

    private static String toLookupKey(String family) {
        return Font.cleanFamilyName(family).toLowerCase(Locale.ROOT);
    }

    private static java.awt.Font deriveCachedFont(java.awt.Font font, int fontStyle, float size) {
        if (font == null) {
            return null;
        }
        DerivedFontKey key = new DerivedFontKey(font.getFontName(Locale.ROOT), font.getFamily(Locale.ROOT), fontStyle, Float.floatToIntBits(size));
        return DERIVED_FONT_CACHE.computeIfAbsent(key, unused -> font.deriveFont(fontStyle, size));
    }

    private static void clearResolutionCaches() {
        SINGLE_FAMILY_CACHE.clear();
        SINGLE_FAMILY_CHAIN_CACHE.clear();
        BASE_FONT_CHAIN_CACHE.clear();
        DERIVED_FONT_CACHE.clear();
        RUN_PLAN_CACHE.clear();
    }

    private static String normalizeFamilyChain(String rawFamilyChain) {
        List<String> families = Font.parseFontFamilies(rawFamilyChain);
        return families.isEmpty() ? "" : String.join((CharSequence)",", families);
    }

    private static <K, V> Map<K, V> createLruCache(final int maxSize) {
        return Collections.synchronizedMap(new LinkedHashMap<K, V>(16, 0.75f, true){

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return this.size() > maxSize;
            }
        });
    }

    static {
        FONTS.put(DEFAULT_KEY, new java.awt.Font("Microsoft YaHei", 0, 48));
    }

    private record RunPlanKey(String familyChain, int style, int sizeBits, String content) {
    }

    public record FontRun(java.awt.Font font, String text) {
    }

    private record DerivedFontKey(String fontName, String family, int style, int sizeBits) {
    }
}

