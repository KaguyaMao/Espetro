/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dev.resource;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.loader.ClientLoader;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.resource.Font;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class ResourceFontAsset {
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("ttf", "otf");
    private static final String FAMILY_PREFIX = "aui-resource-font-";

    private ResourceFontAsset() {
    }

    public static boolean isFont(Loader.StaticResourceEntry entry) {
        return entry != null && SUPPORTED_EXTENSIONS.contains(ResourceFontAsset.safe(entry.extension()).toLowerCase(Locale.ROOT));
    }

    public static String familyName(Loader.StaticResourceEntry entry) {
        String path = entry == null ? "" : ResourceFontAsset.safe(entry.path());
        UUID id = UUID.nameUUIDFromBytes(path.getBytes(StandardCharsets.UTF_8));
        return FAMILY_PREFIX + id;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static boolean ensureLoaded(Loader.StaticResourceEntry entry) {
        if (!ResourceFontAsset.isFont(entry)) {
            return false;
        }
        String family = ResourceFontAsset.familyName(entry);
        if (Font.isRegistered(family)) {
            return true;
        }
        String path = ResourceFontAsset.safe(entry.path());
        try (InputStream stream = ClientLoader.getResourceStream(path);){
            if (stream == null) {
                ApricityUI.LOGGER.warn("[AUI Font] preview font resource is missing path={}", (Object)path);
                boolean bl2 = false;
                return bl2;
            }
            boolean loaded = Font.registerFont(family, stream);
            if (!loaded) {
                ApricityUI.LOGGER.error("[AUI Font] preview font registration failed family={} path={}", (Object)family, (Object)path);
            }
            boolean bl = loaded;
            return bl;
        }
        catch (IOException exception) {
            ApricityUI.LOGGER.error("[AUI Font] preview font read failed family={} path={}", new Object[]{family, path, exception});
            return false;
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}

