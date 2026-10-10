/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.redabysslucia.dragonrise_reforge.entities.utils.IVehicleBackground;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ZTZ99AEntity
extends VehicleEntity
implements IVehicleBackground {
    private final Float[][] PitchAdjustments = new Float[][]{{Float.valueOf(180.0f), Float.valueOf(180.0f), Float.valueOf(180.0f), Float.valueOf(4.0f), Float.valueOf(-4.0f)}, {Float.valueOf(-180.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f), Float.valueOf(4.0f), Float.valueOf(-4.0f)}};

    public ZTZ99AEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.3f) * damage);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ResourceLocation getBackgroundTexture() {
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null) {
            return null;
        }
        int seatIndex = this.getSeatIndex((Entity)player);
        if (seatIndex == this.getTurretControllerIndex()) {
            return new ResourceLocation("dragonrise_reforge", "textures/overlay/vehicle/hud/testcroos3.png");
        }
        if (seatIndex == 1) {
            return new ResourceLocation("dragonrise_reforge", "textures/overlay/vehicle/hud/testcroos2.png");
        }
        return null;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean shouldRenderBackground() {
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null) {
            return false;
        }
        int seatIndex = this.getSeatIndex((Entity)player);
        return seatIndex == this.getTurretControllerIndex() || seatIndex == 1;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public float getBackgroundAlpha() {
        return 1.0f;
    }
}

