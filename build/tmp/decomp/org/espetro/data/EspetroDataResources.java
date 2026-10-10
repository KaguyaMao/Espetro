/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public final class EspetroDataResources {
    private EspetroDataResources() {
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)path);
    }

    public static Optional<Resource> getPreferred(ResourceManager resourceManager, ResourceLocation location) {
        List<Resource> stack = resourceManager.m_213829_(location);
        return Optional.ofNullable(EspetroDataResources.selectPreferred(stack));
    }

    public static Map<ResourceLocation, Resource> listPreferred(ResourceManager resourceManager, String path, Predicate<ResourceLocation> filter) {
        Map<ResourceLocation, List<Resource>> stacks = resourceManager.m_214160_(path, filter);
        LinkedHashMap<ResourceLocation, Resource> result = new LinkedHashMap<ResourceLocation, Resource>();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : stacks.entrySet()) {
            Resource resource = EspetroDataResources.selectPreferred(entry.getValue());
            if (resource == null) continue;
            result.put(entry.getKey(), resource);
        }
        return result;
    }

    public static String readUtf8(Resource resource) throws IOException {
        try (InputStream inputStream = resource.m_215507_();){
            String string = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return string;
        }
    }

    public static String describeSource(Resource resource) {
        return resource.m_215506_() + (resource.m_247137_() ? " (builtin)" : " (datapack)");
    }

    @Nullable
    private static Resource selectPreferred(List<Resource> stack) {
        Resource resource;
        int i;
        for (i = stack.size() - 1; i >= 0; --i) {
            resource = stack.get(i);
            if (resource.m_247137_()) continue;
            return resource;
        }
        for (i = stack.size() - 1; i >= 0; --i) {
            resource = stack.get(i);
            if (!resource.m_247137_()) continue;
            return resource;
        }
        return null;
    }
}

