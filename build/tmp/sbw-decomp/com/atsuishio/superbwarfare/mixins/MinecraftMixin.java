/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.atsuishio.superbwarfare.mixins;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.init.ModKeyMappings;
import com.atsuishio.superbwarfare.network.message.send.ChangeVehicleSeatMessage;
import com.atsuishio.superbwarfare.network.message.send.SwitchVehicleWeaponMessage;
import com.atsuishio.superbwarfare.tools.MinecraftUtil;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Minecraft.class})
public class MinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer f_91074_;
    @Shadow
    @Final
    public Options f_91066_;

    @Inject(method={"handleKeybinds()V"}, at={@At(value="HEAD")}, cancellable=true)
    private void handleKeybinds(CallbackInfo ci) {
        Entity entity;
        if (this.f_91074_ == null || !((entity = this.f_91074_.m_20202_()) instanceof VehicleEntity)) {
            return;
        }
        VehicleEntity vehicle = (VehicleEntity)entity;
        int index = -1;
        for (int i = 0; i < 9; ++i) {
            if (!this.f_91066_.f_92056_[i].m_90857_()) continue;
            index = i;
            break;
        }
        if (index == -1) {
            return;
        }
        if (vehicle.getMaxPassengers() > 1 && ModKeyMappings.CHANGE_SEAT.m_90857_() && index < vehicle.getMaxPassengers() && vehicle.getNthEntity(index) == null) {
            ci.cancel();
            this.f_91066_.f_92056_[index].m_90859_();
            MinecraftUtil.sendPacketToServer(new ChangeVehicleSeatMessage(index));
            vehicle.changeSeat((Entity)this.f_91074_, index);
            return;
        }
        int seatIndex = vehicle.getSeatIndex((Entity)this.f_91074_);
        if (vehicle.banHand((LivingEntity)this.f_91074_)) {
            ci.cancel();
            this.f_91066_.f_92056_[index].m_90859_();
            if (!ModKeyMappings.CHANGE_SEAT.m_90857_() && vehicle.hasWeapon(seatIndex) && vehicle.getWeaponIndex(seatIndex) != index && ClientEventHandler.switchVehicleWeaponCooldown <= 0) {
                MinecraftUtil.sendPacketToServer(new SwitchVehicleWeaponMessage(seatIndex, index, false));
                ClientEventHandler.switchVehicleWeaponCooldown = 3;
            }
        }
    }
}
