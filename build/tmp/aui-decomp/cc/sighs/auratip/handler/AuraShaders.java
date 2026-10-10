/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.registry.extra.ShaderRegister
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.handler;

import cc.sighs.oelib.registry.extra.ShaderRegister;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public class AuraShaders {
    private static ShaderInstance radialRing;

    public static ShaderInstance getRadialRing() {
        return radialRing;
    }

    public static void register() {
        ShaderRegister.register((ResourceLocation)new ResourceLocation("auratip", "radial_ring"), (VertexFormat)DefaultVertexFormat.f_85817_, shader -> {
            radialRing = shader;
        });
    }
}

