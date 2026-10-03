/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.resources.FallbackResourceManager
 *  net.minecraft.server.packs.resources.MultiPackResourceManager
 */
package dev.latvian.mods.kubejs.script.data;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.mods.kubejs.util.JsonIO;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;

public class DataPackEventJS
extends EventJS {
    private final VirtualKubeJSDataPack virtualDataPack;
    private final MultiPackResourceManager wrappedManager;

    public DataPackEventJS(VirtualKubeJSDataPack d, MultiPackResourceManager rm) {
        this.virtualDataPack = d;
        this.wrappedManager = rm;
    }

    public void add(ResourceLocation id, String content) {
        this.virtualDataPack.addData(id, content);
    }

    public void addJson(ResourceLocation id, JsonElement json) {
        if (json != null) {
            id = id.m_135815_().endsWith(".json") ? id : new ResourceLocation(id.m_135827_(), id.m_135815_() + ".json");
            this.add(id, JsonIO.toString(json));
        }
    }

    @Override
    protected void afterPosted(EventResult result) {
        for (String namespace : this.virtualDataPack.m_5698_(PackType.SERVER_DATA)) {
            this.wrappedManager.f_203794_.computeIfAbsent(namespace, ns -> new FallbackResourceManager(PackType.SERVER_DATA, ns)).m_215377_((PackResources)this.virtualDataPack);
        }
    }
}

