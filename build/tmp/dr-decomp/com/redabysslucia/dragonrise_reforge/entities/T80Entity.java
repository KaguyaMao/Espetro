/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  com.atsuishio.superbwarfare.tools.ParticleTool
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.redabysslucia.dragonrise_reforge.entities.utils.IVehicleBackground;
import com.redabysslucia.dragonrise_reforge.utils.GeoBasedParticleUtil;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class T80Entity
extends VehicleEntity
implements IVehicleBackground {
    public T80Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.3f) * damage);
    }

    public void vehicleShoot(LivingEntity living, UUID uuid, Vec3 targetPos) {
        Level level;
        if (living != null && (level = living.m_9236_()) instanceof ServerLevel && living == this.m_146895_() && this.getWeaponIndex(0) == 0) {
            ParticleTool.spawnBigCannonMuzzleParticles((Vec3)this.getShootVec((Entity)living, 1.0f), (Vec3)this.getShootPos((Entity)living, 1.0f), (ServerLevel)((ServerLevel)level), (Entity)this);
        }
        super.vehicleShoot(living, uuid, targetPos);
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.f_19797_ % 1 == 0 && this.hasPlayerOperator()) {
            GeoBasedParticleUtil.spawnParticlesFromManualPosition((Entity)this, 0.0, 21.4995, 64.7884);
        }
    }

    private boolean hasPlayerOperator() {
        if (!this.m_20197_().isEmpty()) {
            return this.m_20197_().get(0) instanceof Player;
        }
        return false;
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
        if (seatIndex == 0) {
            return new ResourceLocation("dragonrise_reforge", "textures/overlay/vehicle/hud/testcroos1.png");
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
        return seatIndex == 0 || seatIndex == 1;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public float getBackgroundAlpha() {
        return 1.0f;
    }
}

