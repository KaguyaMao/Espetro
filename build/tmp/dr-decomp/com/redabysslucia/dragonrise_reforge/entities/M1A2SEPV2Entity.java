/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  javax.annotation.Nonnull
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class M1A2SEPV2Entity
extends VehicleEntity {
    public static final EntityDataAccessor<Boolean> TUSK_INSTALLED = SynchedEntityData.m_135353_(M1A2SEPV2Entity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    private int tuskClickCount = 0;
    private final Float[][] PitchAdjustments = new Float[][]{{Float.valueOf(150.0f), Float.valueOf(180.0f), Float.valueOf(17.0f), Float.valueOf(0.0f), Float.valueOf(-10.0f)}, {Float.valueOf(-150.0f), Float.valueOf(-180.0f), Float.valueOf(17.0f), Float.valueOf(0.0f), Float.valueOf(-10.0f)}};

    public M1A2SEPV2Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(TUSK_INSTALLED, (Object)false);
    }

    public void m_7380_(@Nonnull CompoundTag compound) {
        super.m_7380_(compound);
        compound.m_128379_("TuskInstalled", this.isTuskInstalled());
    }

    public void m_7378_(@Nonnull CompoundTag compound) {
        super.m_7378_(compound);
        if (compound.m_128441_("TuskInstalled")) {
            this.setTuskInstalled(compound.m_128471_("TuskInstalled"));
        }
    }

    public boolean m_6469_(DamageSource source, float amount) {
        Entity entity;
        if (!this.m_9236_().m_5776_() && (entity = source.m_7639_()) instanceof Player) {
            Player player = (Player)entity;
            if (source.m_7640_() == source.m_7639_()) {
                ++this.tuskClickCount;
                if (this.tuskClickCount >= 10) {
                    this.tuskClickCount = 0;
                    boolean installed = !this.isTuskInstalled();
                    this.setTuskInstalled(installed);
                    player.m_5661_((Component)Component.m_237115_((String)(installed ? "message.dragonrise_reforge.m1a2sepv2.tusk_installed" : "message.dragonrise_reforge.m1a2sepv2.tusk_removed")), true);
                    this.m_9236_().m_5594_(null, this.m_20183_(), SoundEvents.f_12016_, this.m_5720_(), 0.8f, 1.2f);
                }
                return false;
            }
        }
        return super.m_6469_(source, amount);
    }

    public boolean isTuskInstalled() {
        return (Boolean)this.f_19804_.m_135370_(TUSK_INSTALLED);
    }

    public void setTuskInstalled(boolean installed) {
        this.f_19804_.m_135381_(TUSK_INSTALLED, (Object)installed);
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.3f) * damage);
    }

    public int getTrackAnimationLength() {
        return 80;
    }

    public float getTurretMaxHealth() {
        return 100.0f;
    }

    public float getWheelMaxHealth() {
        return 100.0f;
    }

    public float getEngineMaxHealth() {
        return 150.0f;
    }
}

