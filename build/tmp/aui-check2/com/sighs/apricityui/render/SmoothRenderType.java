/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.renderer.RenderStateShard$EmptyTextureStateShard
 *  net.minecraft.client.renderer.RenderStateShard$TextureStateShard
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.resources.ResourceLocation
 */
package com.sighs.apricityui.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class SmoothRenderType
extends RenderType {
    private SmoothRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType createSmooth(ResourceLocation location, boolean blur, boolean depthTest) {
        return SmoothRenderType.m_173215_((String)"apricity_image", (VertexFormat)DefaultVertexFormat.f_85820_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)true, (RenderType.CompositeState)RenderType.CompositeState.m_110628_().m_173290_((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(location, blur, false)).m_173292_(f_173103_).m_110663_(depthTest ? f_110113_ : f_110111_).m_110685_(f_110139_).m_110687_(f_110115_).m_110691_(false));
    }
}

