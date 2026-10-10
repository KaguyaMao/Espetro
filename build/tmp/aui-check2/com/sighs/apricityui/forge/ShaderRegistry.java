/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  net.minecraftforge.client.event.RegisterShadersEvent
 */
package com.sighs.apricityui.forge;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.client.event.RegisterShadersEvent;

public class ShaderRegistry {
    private static ShaderInstance filterShader;
    private static ShaderInstance filterBlurShader;

    public static void register(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation("apricityui", "filter"), DefaultVertexFormat.f_85817_), instance -> {
            filterShader = instance;
        });
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation("apricityui", "filter_blur"), DefaultVertexFormat.f_85817_), instance -> {
            filterBlurShader = instance;
        });
    }

    public static void init(ResourceManager resourceManager) throws IOException {
        filterShader = new ShaderInstance((ResourceProvider)resourceManager, new ResourceLocation("apricityui", "filter"), DefaultVertexFormat.f_85817_);
        filterBlurShader = new ShaderInstance((ResourceProvider)resourceManager, new ResourceLocation("apricityui", "filter_blur"), DefaultVertexFormat.f_85817_);
    }

    public static ShaderInstance getFilterShader() {
        return filterShader;
    }

    public static ShaderInstance getFilterBlurShader() {
        return filterBlurShader;
    }
}

