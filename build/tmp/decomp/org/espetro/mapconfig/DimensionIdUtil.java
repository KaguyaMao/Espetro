/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.mapconfig;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import org.espetro.mapconfig.PathSafety;

public final class DimensionIdUtil {
    public static final String NAMESPACE = "espetro";

    private DimensionIdUtil() {
    }

    public static ResourceLocation generate(String mapFolderName) {
        Object slug = PathSafety.slugify(mapFolderName);
        if (((String)slug).isEmpty()) {
            slug = "map_" + PathSafety.stableShortHash(mapFolderName);
        }
        return ResourceLocation.fromNamespaceAndPath((String)NAMESPACE, (String)slug);
    }

    public static Optional<String> validateManualId(String raw, Set<String> alreadyUsed) {
        if (raw == null || raw.isBlank()) {
            return Optional.of("dimension_id \u4e3a\u7a7a");
        }
        String id = raw.trim().toLowerCase(Locale.ROOT);
        ResourceLocation rl = ResourceLocation.m_135820_(id);
        if (rl == null) {
            return Optional.of("dimension_id \u4e0d\u662f\u5408\u6cd5 ResourceLocation: " + raw);
        }
        String ns = rl.m_135827_();
        if ("minecraft".equals(ns) || "forge".equals(ns)) {
            return Optional.of("dimension_id \u4e0d\u5f97\u4f7f\u7528 minecraft/forge \u547d\u540d\u7a7a\u95f4: " + raw);
        }
        if (alreadyUsed != null && alreadyUsed.contains(rl.toString())) {
            return Optional.of("dimension_id \u51b2\u7a81: " + rl);
        }
        return Optional.empty();
    }

    public static ResourceLocation parseOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return ResourceLocation.m_135820_(raw.trim().toLowerCase(Locale.ROOT));
    }
}

