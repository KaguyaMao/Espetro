/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
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
package frontline.combat.fcp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import frontline.combat.fcp.entity.vehicle.ICamoVehicle;
import frontline.combat.fcp.init.ModItems;
import frontline.combat.fcp.init.ModSounds;
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

public abstract class CamoVehicleBase
extends GeoVehicleEntity
implements ICamoVehicle {
    private static final EntityDataAccessor<Integer> CAMO_TYPE = SynchedEntityData.m_135353_(CamoVehicleBase.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);

    public CamoVehicleBase(EntityType<? extends VehicleEntity> type, Level world) {
        super(type, world);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(CAMO_TYPE, (Object)0);
    }

    public ResourceLocation getCurrentTexture() {
        ResourceLocation[] textures = this.getCamoTextures();
        int total = textures.length;
        int camoCount = (int)Math.ceil((double)total / 2.0);
        int index = this.getCamoType();
        if (index < 0 || index >= camoCount) {
            index = 0;
        }
        if (this.isWreck()) {
            int wreckedIndex = index + camoCount;
            if (wreckedIndex >= total) {
                wreckedIndex = total - 1;
            }
            return textures[wreckedIndex];
        }
        return textures[index];
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
        int total = this.getCamoTextures().length;
        int camoCount = (int)Math.ceil((double)total / 2.0);
        int current = this.getCamoType();
        this.setCamoType((current + 1) % camoCount);
    }

    public InteractionResult m_6096_(Player player, InteractionHand hand) {
        if (player.m_21120_(hand).m_150930_((Item)ModItems.SPRAY.get())) {
            if (!this.m_9236_().f_46443_) {
                this.cycleCamo();
                String[] camoNames = this.getCamoNames();
                int camoType = this.getCamoType();
                String camoName = camoType >= 0 && camoType < camoNames.length ? camoNames[camoType] : "Unknown";
                player.m_5661_((Component)Component.m_237110_((String)"message.fcp.camo_changed", (Object[])new Object[]{camoName}).m_130940_(ChatFormatting.GREEN), true);
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

