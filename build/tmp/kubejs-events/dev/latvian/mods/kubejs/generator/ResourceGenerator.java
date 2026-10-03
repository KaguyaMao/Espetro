/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.generator;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.Lazy;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class ResourceGenerator {
    private final ConsoleJS console;
    private final Map<ResourceLocation, GeneratedData> map;

    public ResourceGenerator(ConsoleJS c, Map<ResourceLocation, GeneratedData> m) {
        this.console = c;
        this.map = m;
    }

    public void add(ResourceLocation id, Supplier<byte[]> data, boolean alwaysForget) {
        this.map.put(id, new GeneratedData(id, Lazy.of(data), alwaysForget));
    }

    public void add(ResourceLocation id, Supplier<byte[]> data) {
        this.add(id, data, false);
    }

    public void json(ResourceLocation id, JsonElement json) {
        this.add(new ResourceLocation(id.m_135827_(), id.m_135815_() + ".json"), () -> json.toString().getBytes(StandardCharsets.UTF_8));
        if (this.console.getDebugEnabled() || this.console == ConsoleJS.SERVER && DevProperties.get().dataPackOutput) {
            this.console.info("Generated " + String.valueOf(id) + ": " + String.valueOf(json));
        }
    }
}

