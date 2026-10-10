/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.init.ModItems
 *  net.minecraft.client.Camera
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.redabysslucia.dragonrise_reforge.mixin;

import com.atsuishio.superbwarfare.init.ModItems;
import com.redabysslucia.dragonrise_reforge.entities.special.R6DroneEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Camera.class})
public abstract class R6DroneCameraMixin {
    @Shadow(aliases={"Lnet/minecraft/client/Camera;setRotation(FF)V"})
    protected abstract void m_90572_(float var1, float var2);

    @Shadow(aliases={"Lnet/minecraft/client/Camera;setPosition(DDD)V"})
    protected abstract void m_90584_(double var1, double var3, double var5);

    @Inject(at={@At(value="INVOKE", target="Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal=0)}, method={"setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"}, cancellable=true)
    private void dr$onSetup(BlockGetter level, Entity entity, boolean detached, boolean mirrored, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null) {
            return;
        }
        ItemStack stack = player.m_21205_();
        if (!stack.m_150930_((Item)ModItems.MONITOR.get())) {
            return;
        }
        CompoundTag tag = stack.m_41784_();
        if (!tag.m_128471_("Using") || !tag.m_128471_("Linked")) {
            return;
        }
        R6DroneEntity drone = R6DroneEntity.findDrone(player.m_9236_(), tag.m_128461_("LinkedDrone"));
        if (drone == null) {
            return;
        }
        this.m_90572_(player.m_146908_(), player.m_146909_());
        CameraType cameraType = mc.f_91066_.m_92176_();
        Vec3 dronePos = drone.advanceSmoothPosition(partialTicks);
        if (cameraType == CameraType.FIRST_PERSON || cameraType == CameraType.THIRD_PERSON_BACK) {
            double yawRad = Math.toRadians(player.m_146908_());
            Vec3 flatLook = new Vec3((double)(-Mth.m_14031_((float)((float)yawRad))), 0.0, (double)Mth.m_14089_((float)((float)yawRad)));
            Vec3 camPos = dronePos.m_82549_(flatLook.m_82490_(0.05)).m_82520_(0.0, 0.15, 0.0);
            this.m_90584_(camPos.f_82479_, camPos.f_82480_, camPos.f_82481_);
        } else {
            Vec3 look = player.m_20154_();
            Vec3 camPos = dronePos.m_82549_(look.m_82490_(-2.5)).m_82520_(0.0, 0.6, 0.0);
            this.m_90584_(camPos.f_82479_, camPos.f_82480_, camPos.f_82481_);
        }
        ci.cancel();
    }
}

