/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.Util
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.generator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.client.StencilTexture;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.ResourceGenerator;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public class AssetJsonGenerator
extends ResourceGenerator {
    private final Map<String, StencilTexture> stencils = new HashMap<String, StencilTexture>();

    public AssetJsonGenerator(Map<ResourceLocation, GeneratedData> m) {
        super(ConsoleJS.CLIENT, m);
    }

    public void blockState(ResourceLocation id, Consumer<VariantBlockStateGenerator> consumer) {
        VariantBlockStateGenerator gen = (VariantBlockStateGenerator)Util.m_137469_((Object)new VariantBlockStateGenerator(), consumer);
        this.json(new ResourceLocation(id.m_135827_(), "blockstates/" + id.m_135815_()), (JsonElement)gen.toJson());
    }

    public void multipartState(ResourceLocation id, Consumer<MultipartBlockStateGenerator> consumer) {
        MultipartBlockStateGenerator gen = (MultipartBlockStateGenerator)Util.m_137469_((Object)new MultipartBlockStateGenerator(), consumer);
        this.json(new ResourceLocation(id.m_135827_(), "blockstates/" + id.m_135815_()), (JsonElement)gen.toJson());
    }

    public void blockModel(ResourceLocation id, Consumer<ModelGenerator> consumer) {
        ModelGenerator gen = (ModelGenerator)Util.m_137469_((Object)new ModelGenerator(), consumer);
        this.json(new ResourceLocation(id.m_135827_(), "models/block/" + id.m_135815_()), (JsonElement)gen.toJson());
    }

    public void itemModel(ResourceLocation id, Consumer<ModelGenerator> consumer) {
        ModelGenerator gen = (ModelGenerator)Util.m_137469_((Object)new ModelGenerator(), consumer);
        this.json(AssetJsonGenerator.asItemModelLocation(id), (JsonElement)gen.toJson());
    }

    public static ResourceLocation asItemModelLocation(ResourceLocation id) {
        return new ResourceLocation(id.m_135827_(), "models/item/" + id.m_135815_());
    }

    public void stencil(ResourceLocation target, String stencil, JsonObject colors) throws IOException {
        StencilTexture st = this.stencils.get(stencil);
        if (st == null) {
            Path path = KubeJSPaths.ASSETS.resolve("kubejs/textures/stencil/" + stencil + ".png");
            if (Files.notExists(path, new LinkOption[0])) {
                throw new IllegalArgumentException("Stencil file 'kubejs/assets/kubejs/textures/stencil/'" + stencil + ".png' not found!");
            }
            try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(path, new OpenOption[0]));){
                Path metaPath = KubeJSPaths.ASSETS.resolve("kubejs/textures/stencil/" + stencil + ".png.mcmeta");
                byte[] meta = null;
                if (Files.exists(metaPath, new LinkOption[0])) {
                    meta = Files.readAllBytes(metaPath);
                }
                st = new StencilTexture(ImageIO.read(in), meta);
                this.stencils.put(stencil, st);
            }
        }
        StencilTexture st1 = st;
        this.add(new ResourceLocation(target.m_135827_(), "textures/" + target.m_135815_() + ".png"), () -> st1.create(colors), true);
        if (st.mcmeta != null) {
            this.add(new ResourceLocation(target.m_135827_(), "textures/" + target.m_135815_() + ".png.mcmeta"), () -> st1.mcmeta, false);
        }
    }
}

