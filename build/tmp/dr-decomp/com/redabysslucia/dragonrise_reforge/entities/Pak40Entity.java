/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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

import com.redabysslucia.dragonrise_reforge.entities.utils.IVehicleBackground;
import com.redabysslucia.dragonrise_reforge.entities.utils.SyncCameraVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Pak40Entity
extends SyncCameraVehicle
implements IVehicleBackground {
    public Pak40Entity(EntityType<Pak40Entity> type, Level world) {
        super(type, world);
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
        if (seatIndex == 1) {
            return new ResourceLocation("dragonrise_reforge", "textures/overlay/vehicle/hud/zf3x8.png");
        }
        return null;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean scaleByHeight() {
        return true;
    }
}

