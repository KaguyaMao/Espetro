/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.init.ModItems
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.redabysslucia.dragonrise_reforge.mixin;

import com.atsuishio.superbwarfare.init.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redabysslucia.dragonrise_reforge.entities.special.R6DroneEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={GameRenderer.class})
public class GameRendererMixin {
    @Inject(at={@At(value="HEAD")}, method={"getNightVisionScale(Lnet/minecraft/world/entity/LivingEntity;F)F"}, cancellable=true)
    private static void getNightVisionScale(LivingEntity living, float num, CallbackInfoReturnable<Float> callback) {
        callback.setReturnValue((Object)Float.valueOf(0.0f));
    }

    @Inject(method={"renderItemInHand"}, at={@At(value="HEAD")}, cancellable=true)
    private void dr$hideHeldItemForR6Drone(PoseStack poseStack, Camera camera, float partialTick, CallbackInfo ci) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        ItemStack stack = mc.f_91074_.m_21205_();
        if (!stack.m_150930_((Item)ModItems.MONITOR.get())) {
            return;
        }
        CompoundTag tag = stack.m_41784_();
        if (!tag.m_128471_("Using") || !tag.m_128471_("Linked")) {
            return;
        }
        if (R6DroneEntity.findDrone(mc.f_91074_.m_9236_(), tag.m_128461_("LinkedDrone")) != null) {
            ci.cancel();
        }
    }
}

