/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.overlay.VehicleMainWeaponHudOverlay
 *  com.atsuishio.superbwarfare.client.overlay.weapon.AircraftHud
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.data.gun.GunProp
 *  com.atsuishio.superbwarfare.data.gun.ProjectileInfo
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.event.ClientEventHandler
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.redabysslucia.dragonrise_reforge.mixin;

import com.atsuishio.superbwarfare.client.overlay.VehicleMainWeaponHudOverlay;
import com.atsuishio.superbwarfare.client.overlay.weapon.AircraftHud;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.data.gun.ProjectileInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={AircraftHud.class}, remap=false)
public class AircraftHudMixin {
    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true)
    private void dragonrise$skipBombScope(VehicleEntity vehicle, Player player, ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight, CallbackInfo ci) {
        ProjectileInfo info;
        if (!ClientEventHandler.zoomVehicle) {
            return;
        }
        GunData data = vehicle.getGunData((Entity)player);
        if (data == null) {
            return;
        }
        if (!"@AirBomb".equals(data.get(GunProp.CROSSHAIR))) {
            return;
        }
        Object projectile = data.get(GunProp.PROJECTILE);
        if (projectile instanceof ProjectileInfo && (info = (ProjectileInfo)projectile).getItemId().startsWith("dragonrise_reforge:")) {
            ci.cancel();
        }
    }

    @Inject(method={"render"}, at={@At(value="RETURN")})
    private void dragonrise$rearSeatWeaponName(VehicleEntity vehicle, Player player, ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight, CallbackInfo ci) {
        if (player == vehicle.m_146895_()) {
            return;
        }
        if (!"dragonrise_reforge".equals(EntityType.m_20613_((EntityType)vehicle.m_6095_()).m_135827_())) {
            return;
        }
        if (ClientEventHandler.isNacelleCam((Player)player)) {
            return;
        }
        GunData data = vehicle.getGunData((Entity)player);
        if (data == null) {
            return;
        }
        PoseStack pose = guiGraphics.m_280168_();
        pose.m_85836_();
        pose.m_85837_((double)screenWidth / 2.0, (double)screenHeight / 2.0 + 50.0, 0.0);
        pose.m_85841_(0.75f, 0.75f, 1.0f);
        VehicleMainWeaponHudOverlay.renderWeaponInfoThirdAir((GuiGraphics)guiGraphics, (VehicleEntity)vehicle, (Player)player, (GunData)data, (Font)Minecraft.m_91087_().f_91062_);
        pose.m_85849_();
    }
}

