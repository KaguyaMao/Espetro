/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.Level
 */
package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import tech.vvp.vvp.entity.vehicle.ICamoVehicle;
import tech.vvp.vvp.entity.vehicle.VvpVehicleBase;
import tech.vvp.vvp.init.ModItems;
import tech.vvp.vvp.init.ModSounds;

public abstract class CamoVehicleBase
extends VvpVehicleBase
implements ICamoVehicle {
    private static final EntityDataAccessor<Integer> CAMO_TYPE = SynchedEntityData.m_135353_(CamoVehicleBase.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);

    public CamoVehicleBase(EntityType<? extends GeoVehicleEntity> type, Level world) {
        super(type, world);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(CAMO_TYPE, (Object)0);
    }

    @Override
    public int getCamoType() {
        return (Integer)this.f_19804_.m_135370_(CAMO_TYPE);
    }

    @Override
    public void setCamoType(int camoType) {
        this.f_19804_.m_135381_(CAMO_TYPE, (Object)camoType);
    }

    @Override
    public void cycleCamo() {
        int current = this.getCamoType();
        this.setCamoType((current + 1) % this.getCamoTextures().length);
    }

    public InteractionResult m_6096_(Player player, InteractionHand hand) {
        if (player.m_21120_(hand).m_150930_((Item)ModItems.SPRAY.get())) {
            if (!this.m_9236_().f_46443_) {
                this.cycleCamo();
                String[] camoNames = this.getCamoNames();
                int camoType = this.getCamoType();
                String camoName = camoType >= 0 && camoType < camoNames.length ? camoNames[camoType] : "Unknown";
                player.m_5661_((Component)Component.m_237110_((String)"message.vvp.camo_changed", (Object[])new Object[]{camoName}).m_130940_(ChatFormatting.GREEN), true);
                this.m_9236_().m_5594_(null, this.m_20183_(), (SoundEvent)ModSounds.SPRAY.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            player.m_6674_(hand);
            return InteractionResult.SUCCESS;
        }
        return super.m_6096_(player, hand);
    }

    public void m_7380_(CompoundTag compound) {
        super.m_7380_(compound);
        compound.m_128405_("CamoType", this.getCamoType());
    }

    public void m_7378_(CompoundTag compound) {
        super.m_7378_(compound);
        if (compound.m_128441_("CamoType")) {
            this.setCamoType(compound.m_128451_("CamoType"));
        }
    }

    @Override
    public abstract ResourceLocation[] getCamoTextures();

    @Override
    public abstract String[] getCamoNames();
}

