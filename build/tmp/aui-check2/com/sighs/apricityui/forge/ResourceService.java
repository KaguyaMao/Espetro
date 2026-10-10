/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 */
package com.sighs.apricityui.forge;

import com.sighs.apricityui.render.SmoothRenderType;
import com.sighs.apricityui.spi.AuiResourceService;
import com.sighs.apricityui.spi.RenderHandle;
import com.sighs.apricityui.spi.TextureKey;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public final class ResourceService
implements AuiResourceService {
    public static final ResourceService INSTANCE = new ResourceService();

    private ResourceService() {
    }

    @Override
    public Optional<InputStream> openResource(String path) {
        ResourceManager manager = ResourceService.resourceManager();
        if (manager == null) {
            return Optional.empty();
        }
        ResourceLocation location = ResourceService.parseLocation(path);
        if (location == null) {
            return Optional.empty();
        }
        try {
            Optional resource = manager.m_213713_(location);
            return resource.map(r -> {
                try {
                    return r.m_215507_();
                }
                catch (IOException ignored) {
                    return null;
                }
            });
        }
        catch (Exception ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Map<String, String> listResourcePaths(String path, String suffix) {
        ResourceManager manager = ResourceService.resourceManager();
        if (manager == null) {
            return Map.of();
        }
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        try {
            Map resources = manager.m_214159_(path, location -> suffix == null || suffix.isEmpty() || location.m_135815_().endsWith(suffix));
            for (Map.Entry entry : resources.entrySet()) {
                String fullPath;
                String relative = fullPath = ((ResourceLocation)entry.getKey()).m_135815_();
                String prefix = path;
                if (prefix != null && !prefix.isEmpty() && relative.startsWith(prefix + "/")) {
                    relative = relative.substring(prefix.length() + 1);
                }
                if (relative.isBlank()) continue;
                String sourcePack = ((Resource)entry.getValue()).m_215506_();
                result.put(relative, sourcePack == null ? "" : sourcePack);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return result;
    }

    @Override
    public TextureKey locationOf(String key) {
        if (key == null) {
            return null;
        }
        String sanitizedPath = key.toLowerCase().replaceAll("[^a-z0-9/._-]", "_");
        int hash = Math.floorMod(key.hashCode(), 0x1000000);
        return TextureKey.of("dynamic/" + sanitizedPath + "-" + Integer.toHexString(hash));
    }

    @Override
    public TextureKey tryParseTextureKey(String src) {
        if (src == null || src.isBlank()) {
            return null;
        }
        return ResourceLocation.m_135820_((String)src) == null ? null : TextureKey.of(src);
    }

    @Override
    public Object textureLocation(TextureKey key) {
        if (key == null) {
            return null;
        }
        return ResourceService.parseLocation(key.value());
    }

    @Override
    public RenderHandle smoothRenderType(TextureKey key, boolean blur, boolean depthTest) {
        return RenderHandle.of(SmoothRenderType.createSmooth(ResourceService.parseLocation(key.value()), blur, depthTest));
    }

    private static ResourceLocation parseLocation(String value) {
        int colon = value.indexOf(58);
        if (colon >= 0) {
            return ResourceLocation.m_135820_((String)value);
        }
        return new ResourceLocation("apricityui", value);
    }

    private static ResourceManager resourceManager() {
        try {
            Minecraft minecraft = Minecraft.m_91087_();
            if (minecraft == null) {
                return null;
            }
            return minecraft.m_91098_();
        }
        catch (LinkageError | RuntimeException ignored) {
            return null;
        }
    }
}

