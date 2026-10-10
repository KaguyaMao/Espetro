/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.init.ModDamageTypes
 *  com.atsuishio.superbwarfare.init.ModSounds
 *  com.atsuishio.superbwarfare.tools.FormatTool
 *  com.atsuishio.superbwarfare.tools.ParticleTool
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.entity.EntityTypeTest
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Math
 */
package com.redabysslucia.dragonrise_reforge.entities.atmg;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.FormatTool;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.redabysslucia.dragonrise_reforge.init.ModItems;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

public class HJ8Entity
extends VehicleEntity {
    public static final EntityDataAccessor<Boolean> LOADED = SynchedEntityData.m_135353_(HJ8Entity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    public static final EntityDataAccessor<Integer> RELOAD_COOLDOWN = SynchedEntityData.m_135353_(HJ8Entity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);

    public HJ8Entity(EntityType<HJ8Entity> type, Level world) {
        super(type, world);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(LOADED, (Object)false);
        this.f_19804_.m_135372_(RELOAD_COOLDOWN, (Object)0);
    }

    public void m_7380_(CompoundTag compound) {
        super.m_7380_(compound);
        compound.m_128379_("State", ((Boolean)this.f_19804_.m_135370_(LOADED)).booleanValue());
        compound.m_128405_("ReloadCoolDown", ((Integer)this.f_19804_.m_135370_(RELOAD_COOLDOWN)).intValue());
    }

    protected void m_7378_(CompoundTag compound) {
        super.m_7378_(compound);
        this.f_19804_.m_135381_(LOADED, (Object)compound.m_128471_("State"));
        this.f_19804_.m_135381_(RELOAD_COOLDOWN, (Object)compound.m_128451_("ReloadCoolDown"));
    }

    /*
     * Enabled aggressive block sorting
     */
    @NotNull
    public InteractionResult m_6096_(Player player, @NotNull InteractionHand hand) {
        GunData gunData = this.getGunData(0);
        if (gunData == null) {
            return InteractionResult.SUCCESS;
        }
        int coolDown = (int)Math.ceil((float)(20.0f / ((float)this.vehicleWeaponRpm(0) / 60.0f)));
        ItemStack stack = player.m_21205_();
        if (gunData.hasEnoughAmmoToShoot((Entity)player)) {
            this.f_19804_.m_135381_(LOADED, (Object)true);
            return super.m_6096_(player, hand);
        }
        if ((Boolean)this.f_19804_.m_135370_(LOADED) != false) return InteractionResult.SUCCESS;
        if (!gunData.selectedAmmoConsumer().isAmmoItem(stack)) {
            return super.m_6096_(player, hand);
        }
        Level level = this.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            if ((Integer)this.f_19804_.m_135370_(RELOAD_COOLDOWN) == 0) {
                this.modifyGunData(0, data -> data.reloadAmmo((Entity)player));
                this.f_19804_.m_135381_(LOADED, (Object)true);
                serverLevel.m_5594_(null, this.m_20097_(), (SoundEvent)ModSounds.TYPE_63_RELOAD.get(), SoundSource.PLAYERS, 1.0f, this.f_19796_.m_188501_() * 0.1f + 0.9f);
                return InteractionResult.SUCCESS;
            }
        }
        player.m_5661_((Component)Component.m_237113_((String)(FormatTool.format1DZ((double)((double)(coolDown - (Integer)this.f_19804_.m_135370_(RELOAD_COOLDOWN)) / 20.0)) + " / " + FormatTool.format1DZ((double)((double)coolDown / 20.0)))), true);
        return InteractionResult.SUCCESS;
    }

    public void m_6075_() {
        super.m_6075_();
        if ((Integer)this.f_19804_.m_135370_(RELOAD_COOLDOWN) > 0) {
            this.f_19804_.m_135381_(RELOAD_COOLDOWN, (Object)((Integer)this.f_19804_.m_135370_(RELOAD_COOLDOWN) - 1));
        }
    }

    @NotNull
    public List<ItemStack> getRetrieveItems() {
        ItemStack stack;
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack((ItemLike)ModItems.HJ8_DEPLOYER.get()));
        GunData data = this.getGunData(0);
        if (((Boolean)this.f_19804_.m_135370_(LOADED)).booleanValue() && data != null && !(stack = data.selectedAmmoConsumer().stack().m_255036_(data.withdrawAmmoCount())).m_41619_()) {
            list.add(stack.m_41777_());
        }
        return list;
    }

    public void vehicleShoot(LivingEntity living, UUID uuid, Vec3 targetPos) {
        super.vehicleShoot(living, uuid, targetPos);
        this.f_19804_.m_135381_(LOADED, (Object)false);
        Vec3 barrelVector = this.getBarrelVector(1.0f);
        Vec3 pos = this.getShootPos((Entity)living, 1.0f).m_82549_(barrelVector.m_82490_(-0.5));
        AABB ab = new AABB(pos, pos).m_82400_(0.75).m_82383_(barrelVector.m_82490_(-2.0)).m_82369_(barrelVector.m_82490_(-5.0));
        int coolDown = (int)Math.ceil((float)(20.0f / ((float)this.vehicleWeaponRpm(0) / 60.0f)));
        this.f_19804_.m_135381_(RELOAD_COOLDOWN, (Object)coolDown);
        for (Entity entity : this.m_9236_().m_142425_(EntityTypeTest.m_156916_(Entity.class), ab, target -> target != this && target != this.m_146895_() && target.m_20202_() == null)) {
            entity.m_6469_(ModDamageTypes.causeBurnDamage((RegistryAccess)entity.m_9236_().m_9598_(), (Entity)living), 30.0f - 2.0f * entity.m_20270_((Entity)this));
            double force = 4.0 - 0.7 * (double)entity.m_20270_((Entity)this);
            entity.m_5997_(-force * barrelVector.f_82479_, -force * barrelVector.f_82480_, -force * barrelVector.f_82481_);
        }
        Level level = this.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            ParticleTool.spawnMediumCannonMuzzleParticles((Vec3)barrelVector.m_82490_(-1.0), (Vec3)pos, (ServerLevel)serverLevel, (Entity)this);
            ParticleTool.spawnMediumCannonMuzzleParticles((Vec3)barrelVector, (Vec3)pos, (ServerLevel)serverLevel, (Entity)this);
        }
    }
}

