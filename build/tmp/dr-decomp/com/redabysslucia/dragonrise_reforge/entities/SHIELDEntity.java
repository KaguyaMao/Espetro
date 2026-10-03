/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.redabysslucia.dragonrise_reforge.entities.utils.FireLightVisionVehicle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SHIELDEntity
extends FireLightVisionVehicle {
    private static final EntityDataAccessor<Float> TARGET_YAW = SynchedEntityData.m_135353_(SHIELDEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> TARGET_PITCH = SynchedEntityData.m_135353_(SHIELDEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);

    public SHIELDEntity(EntityType<SHIELDEntity> type, Level world) {
        super(type, world);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(TARGET_YAW, (Object)Float.valueOf(this.m_146908_()));
        this.f_19804_.m_135372_(TARGET_PITCH, (Object)Float.valueOf(this.m_146909_()));
    }

    public void m_7380_(@NotNull CompoundTag compound) {
        super.m_7380_(compound);
        compound.m_128350_("TargetPitch", ((Float)this.f_19804_.m_135370_(TARGET_PITCH)).floatValue());
        compound.m_128350_("TargetYaw", ((Float)this.f_19804_.m_135370_(TARGET_YAW)).floatValue());
    }

    public void m_7378_(@NotNull CompoundTag compound) {
        super.m_7378_(compound);
        if (compound.m_128441_("TargetPitch")) {
            this.f_19804_.m_135381_(TARGET_PITCH, (Object)Float.valueOf(compound.m_128457_("TargetPitch")));
        }
        if (compound.m_128441_("TargetYaw")) {
            this.f_19804_.m_135381_(TARGET_YAW, (Object)Float.valueOf(compound.m_128457_("TargetYaw")));
        }
    }

    @NotNull
    public InteractionResult m_6096_(@NotNull Player player, @NotNull InteractionHand hand) {
        InteractionResult result = super.m_6096_(player, hand);
        if (result != InteractionResult.PASS) {
            return result;
        }
        if (player.m_6144_()) {
            this.f_19804_.m_135381_(TARGET_YAW, (Object)Float.valueOf(player.m_146908_()));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public void travel() {
        super.travel();
        float diffY = Mth.m_14177_((float)(((Float)this.f_19804_.m_135370_(TARGET_YAW)).floatValue() - this.m_146908_()));
        float diffX = Mth.m_14177_((float)(((Float)this.f_19804_.m_135370_(TARGET_PITCH)).floatValue() - this.m_146909_()));
        this.m_146922_(this.m_146908_() + Mth.m_14036_((float)(0.5f * diffY), (float)-20.0f, (float)20.0f));
        this.m_146926_(this.m_146909_() + Mth.m_14036_((float)(0.5f * diffX), (float)-20.0f, (float)20.0f));
    }
}

