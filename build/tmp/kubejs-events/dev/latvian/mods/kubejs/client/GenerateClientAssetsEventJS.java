/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.Util
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import java.io.IOException;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public class GenerateClientAssetsEventJS
extends EventJS {
    public final AssetJsonGenerator generator;

    public GenerateClientAssetsEventJS(AssetJsonGenerator gen) {
        this.generator = gen;
    }

    public void addLang(String key, String value) {
        ConsoleJS.CLIENT.error("Use ClientEvents.lang('en_us', event => { event.add(key, value) }) instead!");
    }

    public void add(ResourceLocation location, JsonElement json) {
        this.generator.json(location, json);
    }

    public void addModel(String type, ResourceLocation id, Consumer<ModelGenerator> consumer) {
        ModelGenerator gen = (ModelGenerator)Util.m_137469_((Object)new ModelGenerator(), consumer);
        this.add(new ResourceLocation(id.m_135827_(), "models/%s/%s".formatted(type, id.m_135815_())), (JsonElement)gen.toJson());
    }

    public void addBlockState(ResourceLocation id, Consumer<VariantBlockStateGenerator> consumer) {
        VariantBlockStateGenerator gen = (VariantBlockStateGenerator)Util.m_137469_((Object)new VariantBlockStateGenerator(), consumer);
        this.add(new ResourceLocation(id.m_135827_(), "blockstates/" + id.m_135815_()), (JsonElement)gen.toJson());
    }

    public void addMultipartBlockState(ResourceLocation id, Consumer<MultipartBlockStateGenerator> consumer) {
        MultipartBlockStateGenerator gen = (MultipartBlockStateGenerator)Util.m_137469_((Object)new MultipartBlockStateGenerator(), consumer);
        this.add(new ResourceLocation(id.m_135827_(), "blockstates/" + id.m_135815_()), (JsonElement)gen.toJson());
    }

    public void stencil(ResourceLocation target, String stencil, JsonObject colors) throws IOException {
        this.generator.stencil(target, stencil, colors);
    }

    public void defaultItemModel(ResourceLocation id) {
        this.addModel("item", id, model -> {
            model.parent("minecraft:item/generated");
            model.texture("layer0", id.m_135827_() + ":item/" + id.m_135815_());
        });
    }

    public void defaultHandheldItemModel(ResourceLocation id) {
        this.addModel("item", id, model -> {
            model.parent("minecraft:item/handheld");
            model.texture("layer0", id.m_135827_() + ":item/" + id.m_135815_());
        });
    }
}

