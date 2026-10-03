/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.espetro.Espetro;

final class RoleIconResources {
    static final int TEXTURE_SIZE = 128;
    private static final Map<String, ResourceLocation> DISK_CACHE = new ConcurrentHashMap<String, ResourceLocation>();
    private static final Map<String, Boolean> DISK_FAILED = new ConcurrentHashMap<String, Boolean>();

    private RoleIconResources() {
    }

    static ResourceLocation resolve(String iconImagePath, String iconSlug) {
        ResourceLocation fromDisk = RoleIconResources.resolveDiskPath(iconImagePath);
        if (fromDisk != null) {
            return fromDisk;
        }
        return RoleIconResources.resolveSlug(iconSlug);
    }

    static ResourceLocation resolve(String icon) {
        return RoleIconResources.resolveSlug(icon);
    }

    static ResourceLocation resolveDiskOrSlug(String value) {
        return RoleIconResources.resolve(value, value);
    }

    static ResourceLocation resolveForScoreboard(String iconImage, String iconSlug, String classId) {
        ResourceLocation loc = RoleIconResources.resolve(iconImage, iconSlug);
        if (loc != null) {
            return loc;
        }
        if (iconImage != null && !iconImage.isBlank()) {
            String base = RoleIconResources.basenameSlug(iconImage);
            loc = RoleIconResources.resolveSlug(base);
            if (loc != null) {
                return loc;
            }
            loc = RoleIconResources.resolveSlug(RoleIconResources.toSnakeSlug(base));
            if (loc != null) {
                return loc;
            }
        }
        if (classId != null && !classId.isBlank()) {
            loc = RoleIconResources.resolveSlug(classId);
            if (loc != null) {
                return loc;
            }
            int idx = classId.lastIndexOf(95);
            if (idx > 0 && idx < classId.length() - 1) {
                for (int i = 0; i < classId.length(); ++i) {
                    if (classId.charAt(i) != '_' || i + 1 >= classId.length() || (loc = RoleIconResources.resolveSlug(classId.substring(i + 1))) == null) continue;
                    return loc;
                }
            }
        }
        return null;
    }

    private static String basenameSlug(String pathText) {
        String v = pathText.trim().replace('\\', '/');
        int slash = v.lastIndexOf(47);
        String name = slash >= 0 ? v.substring(slash + 1) : v;
        int dot = name.lastIndexOf(46);
        if (dot > 0) {
            name = name.substring(0, dot);
        }
        return name;
    }

    private static String toSnakeSlug(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }
        StringBuilder sb = new StringBuilder(name.length() + 4);
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
                continue;
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString().replaceAll("_+", "_");
    }

    static ResourceLocation resolveSlug(String icon) {
        if (icon == null || icon.isBlank() || icon.contains("..") || !icon.matches("[a-z0-9][a-z0-9_/-]*")) {
            return null;
        }
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)("textures/gui/roles/" + icon + ".png"));
        return Minecraft.m_91087_().m_91098_().m_213713_(location).isPresent() ? location : null;
    }

    static ResourceLocation resolveDiskPath(String pathText) {
        ResourceLocation resourceLocation;
        block13: {
            if (pathText == null || pathText.isBlank()) {
                return null;
            }
            String key = pathText.trim();
            if (DISK_FAILED.containsKey(key)) {
                return null;
            }
            ResourceLocation cached = DISK_CACHE.get(key);
            if (cached != null) {
                return cached;
            }
            Path path = Path.of(key, new String[0]);
            if (!path.isAbsolute()) {
                Path gameDir = Minecraft.m_91087_().f_91069_.toPath();
                path = gameDir.resolve(key).normalize();
            }
            if (!Files.isRegularFile(path, new LinkOption[0])) {
                DISK_FAILED.put(key, Boolean.TRUE);
                return null;
            }
            InputStream in = Files.newInputStream(path, new OpenOption[0]);
            try {
                NativeImage image = NativeImage.m_85058_(in);
                DynamicTexture texture = new DynamicTexture(image);
                String safe = Integer.toHexString(key.hashCode());
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)("dynamic/class_icon_" + safe));
                Minecraft.m_91087_().m_91097_().m_118495_(id, texture);
                DISK_CACHE.put(key, id);
                resourceLocation = id;
                if (in == null) break block13;
            }
            catch (Throwable throwable) {
                try {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    DISK_FAILED.put(key, Boolean.TRUE);
                    Espetro.LOGGER.debug("IconImage \u52a0\u8f7d\u5931\u8d25: {} ({})", (Object)key, (Object)e.toString());
                    return null;
                }
            }
            in.close();
        }
        return resourceLocation;
    }
}

