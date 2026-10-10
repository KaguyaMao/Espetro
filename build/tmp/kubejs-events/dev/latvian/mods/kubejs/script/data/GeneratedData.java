/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  dev.architectury.platform.Platform
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.IoSupplier
 *  org.jetbrains.annotations.NotNull
 */
package dev.latvian.mods.kubejs.script.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.Lazy;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;

public record GeneratedData(ResourceLocation id, Lazy<byte[]> data, boolean alwaysForget) implements IoSupplier<InputStream>
{
    public static final GeneratedData INTERNAL_RELOAD = new GeneratedData(KubeJS.id("__internal.reload"), Lazy.of(() -> new byte[0]), false);
    public static final GeneratedData PACK_META = new GeneratedData(KubeJS.id("pack.mcmeta"), Lazy.of(() -> {
        JsonObject json = new JsonObject();
        JsonObject pack = new JsonObject();
        pack.addProperty("description", "KubeJS Pack");
        pack.addProperty("pack_format", (Number)15);
        json.add("pack", (JsonElement)pack);
        return json.toString().getBytes(StandardCharsets.UTF_8);
    }), false);
    public static final GeneratedData PACK_ICON = new GeneratedData(KubeJS.id("textures/kubejs_logo.png"), Lazy.of(() -> {
        try {
            return Files.readAllBytes((Path)Platform.getMod((String)"kubejs").findResource(new String[]{"assets", "kubejs", "textures", "kubejs_logo.png"}).get());
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return new byte[0];
        }
    }), true);

    @NotNull
    public InputStream get() {
        ByteArrayInputStream in = new ByteArrayInputStream(this.data.get());
        if (this.alwaysForget) {
            this.data.forget();
        }
        return in;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GeneratedData)) return false;
        GeneratedData g = (GeneratedData)obj;
        if (!this.id.equals((Object)g.id)) return false;
        return true;
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}

