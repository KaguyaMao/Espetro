/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 */
package com.redabysslucia.dragonrise_reforge.client.outline.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class EntityMaskRenderer {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void renderEntityMask(Entity entity, double lerpX, double lerpY, double lerpZ, float partialTick, PoseStack poseStack, Matrix4f projectionMatrix, MultiBufferSource.BufferSource maskBufferSource) {
        Minecraft mc = Minecraft.m_91087_();
        EntityRenderDispatcher dispatcher = mc.m_91290_();
        EntityRenderer renderer = dispatcher.m_114382_(entity);
        if (renderer != null) {
            System.out.println("Rendering entity mask for " + entity.m_7755_() + ", renderer: " + renderer.getClass().getName());
            Vec3 cameraPos = mc.f_91063_.m_109153_().m_90583_();
            double x = lerpX - cameraPos.f_82479_;
            double y = lerpY - cameraPos.f_82480_;
            double z = lerpZ - cameraPos.f_82481_;
            poseStack.m_85836_();
            poseStack.m_85837_(x, y, z);
            try {
                float lerpYaw = entity.f_19859_ + (entity.m_146908_() - entity.f_19859_) * partialTick;
                System.out.println("Calling renderer.render for " + entity.m_7755_());
                renderer.m_7392_(entity, lerpYaw, partialTick, poseStack, (MultiBufferSource)maskBufferSource, 0xF000F0);
                System.out.println("Successfully rendered entity mask for " + entity.m_7755_());
            }
            catch (Exception e) {
                System.out.println("Error rendering entity mask for " + entity.m_7755_() + ": " + e.getMessage());
                e.printStackTrace();
            }
            finally {
                poseStack.m_85849_();
            }
        } else {
            System.out.println("No renderer found for entity: " + entity.m_7755_());
        }
    }
}

