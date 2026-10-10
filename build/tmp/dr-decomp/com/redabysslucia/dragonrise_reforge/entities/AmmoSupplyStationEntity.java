/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.AmmoConsumer
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.data.gun.GunProp
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.SeatInfo
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.tools.InventoryTool
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.jetbrains.annotations.NotNull
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.data.gun.AmmoConsumer;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.data.vehicle.subdata.SeatInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.InventoryTool;
import com.redabysslucia.dragonrise_reforge.config.SupplyStationConfig;
import com.redabysslucia.dragonrise_reforge.config.SupplyStationDataLoader;
import com.redabysslucia.dragonrise_reforge.init.ModSounds;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class AmmoSupplyStationEntity
extends VehicleEntity {
    private static final EntityDataAccessor<Float> SUPPLY_RANGE = SynchedEntityData.m_135353_(AmmoSupplyStationEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Integer> NON_MAGAZINE_FILL_AMOUNT = SynchedEntityData.m_135353_(AmmoSupplyStationEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> SUPPLY_INTERVAL = SynchedEntityData.m_135353_(AmmoSupplyStationEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> SUPPLY_TIME = SynchedEntityData.m_135353_(AmmoSupplyStationEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.m_135353_(AmmoSupplyStationEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    private static final EntityDataAccessor<Float> SUPPLY_PROGRESS = SynchedEntityData.m_135353_(AmmoSupplyStationEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final float DEFAULT_SUPPLY_RANGE = 24.0f;
    private static final int DEFAULT_NON_MAGAZINE_FILL = 100;
    private static final int DEFAULT_SUPPLY_INTERVAL = 20;
    private static final int DEFAULT_SUPPLY_TIME = 160;
    private int tickCounter = 0;
    private int chargeTick = 0;
    private int chargeCooldown = 0;
    private int chargingSoundTimer = 0;
    private final Map<UUID, Float> trackedVehicleHealth = new HashMap<UUID, Float>();
    private static final int INTERRUPT_COOLDOWN = 10;
    private static final int CHARGING_SOUND_INTERVAL = 20;

    public AmmoSupplyStationEntity(EntityType<? extends AmmoSupplyStationEntity> type, Level level) {
        super(type, level);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(SUPPLY_RANGE, (Object)Float.valueOf(24.0f));
        this.f_19804_.m_135372_(NON_MAGAZINE_FILL_AMOUNT, (Object)100);
        this.f_19804_.m_135372_(SUPPLY_INTERVAL, (Object)20);
        this.f_19804_.m_135372_(SUPPLY_TIME, (Object)160);
        this.f_19804_.m_135372_(ACTIVE, (Object)true);
        this.f_19804_.m_135372_(SUPPLY_PROGRESS, (Object)Float.valueOf(0.0f));
    }

    public boolean m_6094_() {
        return false;
    }

    public void m_7334_(Entity entity) {
    }

    public float getMaxHealth() {
        return 2100.0f;
    }

    public float getMass() {
        return 1.14514189E9f;
    }

    public float getSupplyRange() {
        return ((Float)this.f_19804_.m_135370_(SUPPLY_RANGE)).floatValue();
    }

    public void setSupplyRange(float range) {
        this.f_19804_.m_135381_(SUPPLY_RANGE, (Object)Float.valueOf(Math.max(1.0f, range)));
    }

    public int getNonMagazineFillAmount() {
        return (Integer)this.f_19804_.m_135370_(NON_MAGAZINE_FILL_AMOUNT);
    }

    public void setNonMagazineFillAmount(int amount) {
        this.f_19804_.m_135381_(NON_MAGAZINE_FILL_AMOUNT, (Object)Math.max(1, amount));
    }

    public int getSupplyInterval() {
        return (Integer)this.f_19804_.m_135370_(SUPPLY_INTERVAL);
    }

    public void setSupplyInterval(int interval) {
        this.f_19804_.m_135381_(SUPPLY_INTERVAL, (Object)Math.max(1, interval));
    }

    public int getSupplyTime() {
        return (Integer)this.f_19804_.m_135370_(SUPPLY_TIME);
    }

    public void setSupplyTime(int time) {
        this.f_19804_.m_135381_(SUPPLY_TIME, (Object)Math.max(1, time));
    }

    public boolean isActive() {
        return (Boolean)this.f_19804_.m_135370_(ACTIVE);
    }

    public void setActive(boolean active) {
        this.f_19804_.m_135381_(ACTIVE, (Object)active);
    }

    public float getSupplyProgress() {
        return ((Float)this.f_19804_.m_135370_(SUPPLY_PROGRESS)).floatValue();
    }

    private void setSupplyProgress(float progress) {
        this.f_19804_.m_135381_(SUPPLY_PROGRESS, (Object)Float.valueOf(Math.max(0.0f, Math.min(1.0f, progress))));
    }

    public boolean isCharging() {
        return this.getSupplyProgress() > 0.0f && this.getSupplyProgress() < 1.0f;
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.m_9236_().m_5776_()) {
            return;
        }
        Vec3 vel = this.m_20184_();
        this.m_20334_(vel.f_82479_ * 0.85, vel.f_82480_, vel.f_82481_ * 0.85);
        if (!this.isActive()) {
            return;
        }
        if (this.isCharging()) {
            this.tickCharge();
            return;
        }
        if (this.chargeCooldown > 0) {
            --this.chargeCooldown;
            return;
        }
        ++this.tickCounter;
        int interval = this.getSupplyInterval();
        if (interval <= 0) {
            interval = 20;
        }
        if (this.tickCounter >= interval) {
            this.tickCounter = 0;
            this.startCharge();
        }
    }

    private void tickCharge() {
        int totalTime = this.getSupplyTime();
        if (totalTime <= 0) {
            totalTime = 160;
        }
        ++this.chargeTick;
        float progress = Math.min(1.0f, (float)this.chargeTick / (float)totalTime);
        this.setSupplyProgress(progress);
        --this.chargingSoundTimer;
        if (this.chargingSoundTimer <= 0) {
            this.m_9236_().m_5594_(null, this.m_20183_(), (SoundEvent)ModSounds.SUPPLY_STATION_CHARGING.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            this.chargingSoundTimer = 20;
        }
        if (this.checkVehicleDamage() || !this.checkTrackedVehiclesHavePlayer()) {
            this.cancelCharge();
            return;
        }
        if (progress >= 1.0f) {
            this.chargeTick = 0;
            this.trackedVehicleHealth.clear();
            this.chargingSoundTimer = 0;
            this.performSupply();
            this.setSupplyProgress(0.0f);
        }
    }

    private boolean checkVehicleDamage() {
        for (Map.Entry<UUID, Float> entry : this.trackedVehicleHealth.entrySet()) {
            VehicleEntity vehicle = this.findTrackedVehicle(entry.getKey());
            if (vehicle == null || !vehicle.m_6084_()) {
                return true;
            }
            if (!(vehicle.getHealth() < entry.getValue().floatValue())) continue;
            return true;
        }
        return false;
    }

    private boolean checkTrackedVehiclesHavePlayer() {
        for (UUID uuid : this.trackedVehicleHealth.keySet()) {
            VehicleEntity vehicle = this.findTrackedVehicle(uuid);
            if (vehicle == null) {
                return false;
            }
            if (!vehicle.m_20197_().stream().noneMatch(p -> p instanceof Player)) continue;
            return false;
        }
        return true;
    }

    private VehicleEntity findTrackedVehicle(UUID uuid) {
        float range = this.getSupplyRange();
        AABB searchBox = this.m_20191_().m_82400_((double)range);
        List vehicles = this.m_9236_().m_6443_(VehicleEntity.class, searchBox, v -> v.m_6084_() && v.m_20148_().equals(uuid));
        return vehicles.isEmpty() ? null : (VehicleEntity)vehicles.get(0);
    }

    private void cancelCharge() {
        this.chargeTick = 0;
        this.trackedVehicleHealth.clear();
        this.chargingSoundTimer = 0;
        this.setSupplyProgress(0.0f);
        this.chargeCooldown = 10;
    }

    private void startCharge() {
        List<VehicleEntity> vehicles = this.findNearbyVehicles();
        boolean anyNeedsAction = false;
        this.trackedVehicleHealth.clear();
        for (VehicleEntity vehicle : vehicles) {
            boolean needsSupply = this.vehicleNeedsSupply(vehicle);
            boolean needsHeal = this.vehicleNeedsHealing(vehicle);
            boolean needsBonus = this.vehicleNeedsBonusItem(vehicle);
            if (!needsSupply && !needsHeal && !needsBonus) continue;
            anyNeedsAction = true;
            this.trackedVehicleHealth.put(vehicle.m_20148_(), Float.valueOf(vehicle.getHealth()));
        }
        if (anyNeedsAction) {
            this.chargeTick = 0;
            this.setSupplyProgress(0.001f);
        }
    }

    private List<VehicleEntity> findNearbyVehicles() {
        float range = this.getSupplyRange();
        AABB searchBox = this.m_20191_().m_82400_((double)range);
        return this.m_9236_().m_6443_(VehicleEntity.class, searchBox, v -> v.m_6084_() && v.m_20280_((Entity)this) <= (double)(range * range) && v.m_20197_().stream().anyMatch(p -> p instanceof Player));
    }

    private boolean vehicleNeedsSupply(VehicleEntity vehicle) {
        SupplyStationConfig config = SupplyStationDataLoader.getConfig();
        String vehicleId = this.getVehicleId(vehicle);
        SupplyStationConfig.ResupplyRule vehicleRule = config.getRuleForVehicle(vehicleId);
        for (int seat = 0; seat < vehicle.getMaxPassengers(); ++seat) {
            List weaponNames;
            SeatInfo seatInfo = vehicle.getSeat(seat);
            if (seatInfo == null || (weaponNames = seatInfo.weapons()) == null || weaponNames.isEmpty()) continue;
            for (int weaponIdx = 0; weaponIdx < weaponNames.size(); ++weaponIdx) {
                List consumers;
                GunData gunData = vehicle.getGunData(seat, weaponIdx);
                if (gunData == null || gunData.hasInfiniteBackupAmmo((Entity)vehicle) || (consumers = (List)gunData.get(GunProp.AMMO_CONSUMER)) == null || consumers.isEmpty()) continue;
                for (AmmoConsumer consumer : consumers) {
                    if (!this.checkWeaponNeedsSupply(vehicle, gunData, config, vehicleRule, consumer)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean vehicleNeedsHealing(VehicleEntity vehicle) {
        SupplyStationConfig config = SupplyStationDataLoader.getConfig();
        String vehicleId = this.getVehicleId(vehicle);
        SupplyStationConfig.ResupplyRule vehicleRule = config.getRuleForVehicle(vehicleId);
        float healPercent = vehicleRule.healPercent;
        if (healPercent <= 0.0f && vehicleRule != config.defaultRule) {
            healPercent = config.defaultRule.healPercent;
        }
        if (healPercent <= 0.0f) {
            return false;
        }
        return vehicle.getHealth() < vehicle.getMaxHealth();
    }

    private boolean vehicleNeedsBonusItem(VehicleEntity vehicle) {
        String vehicleId;
        SupplyStationConfig.ResupplyRule vehicleRule;
        SupplyStationConfig config = SupplyStationDataLoader.getConfig();
        String bonusItemId = config.getEffectiveBonusItem(vehicleRule = config.getRuleForVehicle(vehicleId = this.getVehicleId(vehicle)));
        if (bonusItemId.isEmpty()) {
            return false;
        }
        ResourceLocation itemLocation = ResourceLocation.m_135820_((String)bonusItemId);
        if (itemLocation == null) {
            return false;
        }
        Item bonusItem = (Item)ForgeRegistries.ITEMS.getValue(itemLocation);
        if (bonusItem == null) {
            return false;
        }
        int target = config.getEffectiveBonusItemCount(vehicleRule);
        int current = this.countBonusItem(vehicle, bonusItem);
        return current < target;
    }

    private int countBonusItem(VehicleEntity vehicle, Item bonusItem) {
        Optional handlerOpt = vehicle.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (handlerOpt.isEmpty()) {
            return 0;
        }
        IItemHandler handler = (IItemHandler)handlerOpt.get();
        int total = 0;
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.m_41720_() != bonusItem) continue;
            total += stack.m_41613_();
        }
        return total;
    }

    private boolean checkWeaponNeedsSupply(VehicleEntity vehicle, GunData gunData, SupplyStationConfig config, SupplyStationConfig.ResupplyRule vehicleRule, AmmoConsumer consumer) {
        String ammoKey = this.getAmmoKey(consumer);
        if (ammoKey.isEmpty()) {
            return false;
        }
        if (config.isPackageBasedMode(vehicleRule, ammoKey)) {
            int target;
            SupplyStationConfig.AmmoTypeRule ammoRule = config.getAmmoRule(vehicleRule, ammoKey);
            String customItemId = ammoRule.customItem;
            if (customItemId == null || customItemId.isEmpty()) {
                return false;
            }
            ResourceLocation itemLocation = ResourceLocation.m_135820_((String)customItemId);
            if (itemLocation == null) {
                return false;
            }
            Item customItem = (Item)ForgeRegistries.ITEMS.getValue(itemLocation);
            if (customItem == null) {
                return false;
            }
            int current = this.countBonusItem(vehicle, customItem);
            return current < (target = Math.max(1, ammoRule.customItemCount));
        }
        if (config.isMagazineMode(vehicleRule, ammoKey) && (Integer)gunData.get(GunProp.MAGAZINE) > 0) {
            int backupAmmo;
            int magazine = (Integer)gunData.get(GunProp.MAGAZINE);
            int currentAmmo = gunData.ammo.get();
            return currentAmmo + (backupAmmo = this.countBackupAmmoForConsumer(vehicle, gunData, consumer)) < magazine;
        }
        SupplyStationConfig.AmmoTypeRule ammoRule = config.getAmmoRule(vehicleRule, ammoKey);
        int target = ammoRule.fixedAmount > 0 ? ammoRule.fixedAmount : this.getNonMagazineFillAmount();
        int currentBackup = this.countBackupAmmoForConsumer(vehicle, gunData, consumer);
        boolean isSelected = consumer == gunData.selectedAmmoConsumer();
        int currentLoaded = isSelected ? gunData.ammo.get() : 0;
        return currentLoaded + currentBackup < target;
    }

    private void performSupply() {
        SupplyStationConfig config = SupplyStationDataLoader.getConfig();
        List<VehicleEntity> vehicles = this.findNearbyVehicles();
        for (VehicleEntity vehicle : vehicles) {
            this.resupplyVehicle(vehicle);
            this.healVehicle(vehicle, config);
            this.supplyBonusItem(vehicle, config);
        }
        this.m_9236_().m_5594_(null, this.m_20183_(), (SoundEvent)ModSounds.SUPPLY_STATION_COMPLETE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    private void healVehicle(VehicleEntity vehicle, SupplyStationConfig config) {
        String vehicleId = this.getVehicleId(vehicle);
        SupplyStationConfig.ResupplyRule vehicleRule = config.getRuleForVehicle(vehicleId);
        float healPercent = vehicleRule.healPercent;
        if (healPercent <= 0.0f && vehicleRule != config.defaultRule) {
            healPercent = config.defaultRule.healPercent;
        }
        if (healPercent <= 0.0f) {
            return;
        }
        float maxHealth = vehicle.getMaxHealth();
        float currentHealth = vehicle.getHealth();
        if (currentHealth >= maxHealth) {
            return;
        }
        float healAmount = maxHealth * healPercent / 100.0f;
        float newHealth = Math.min(maxHealth, currentHealth + healAmount);
        vehicle.setHealth(newHealth);
    }

    private void supplyBonusItem(VehicleEntity vehicle, SupplyStationConfig config) {
        int current;
        String vehicleId = this.getVehicleId(vehicle);
        SupplyStationConfig.ResupplyRule vehicleRule = config.getRuleForVehicle(vehicleId);
        String bonusItemId = config.getEffectiveBonusItem(vehicleRule);
        if (bonusItemId.isEmpty()) {
            return;
        }
        ResourceLocation itemLocation = ResourceLocation.m_135820_((String)bonusItemId);
        if (itemLocation == null) {
            return;
        }
        Item bonusItem = (Item)ForgeRegistries.ITEMS.getValue(itemLocation);
        if (bonusItem == null) {
            return;
        }
        int target = config.getEffectiveBonusItemCount(vehicleRule);
        int toAdd = target - (current = this.countBonusItem(vehicle, bonusItem));
        if (toAdd <= 0) {
            return;
        }
        Optional handlerOpt = vehicle.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (handlerOpt.isEmpty()) {
            return;
        }
        IItemHandler handler = (IItemHandler)handlerOpt.get();
        ItemStack stack = new ItemStack((ItemLike)bonusItem, toAdd);
        InventoryTool.insertItem((IItemHandler)handler, (ItemStack)stack, (int)toAdd);
    }

    private String getVehicleId(VehicleEntity vehicle) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey((Object)vehicle.m_6095_());
        return key != null ? key.toString() : "";
    }

    private String getAmmoKey(AmmoConsumer consumer) {
        if (consumer == null) {
            return "";
        }
        ItemStack stack = consumer.stack();
        if (stack.m_41619_()) {
            return "";
        }
        ResourceLocation key = ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_());
        return key != null ? key.toString() : "";
    }

    private int countBackupAmmoForConsumer(VehicleEntity vehicle, GunData gunData, AmmoConsumer consumer) {
        Optional handlerOpt = vehicle.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (handlerOpt.isEmpty()) {
            return 0;
        }
        IItemHandler handler = (IItemHandler)handlerOpt.get();
        int itemCount = consumer.count(gunData, handler);
        int loadAmount = consumer.getLoadAmount();
        return itemCount * loadAmount;
    }

    private boolean resupplyVehicle(VehicleEntity vehicle) {
        SupplyStationConfig config = SupplyStationDataLoader.getConfig();
        String vehicleId = this.getVehicleId(vehicle);
        SupplyStationConfig.ResupplyRule vehicleRule = config.getRuleForVehicle(vehicleId);
        int globalFallbackFill = this.getNonMagazineFillAmount();
        boolean anyResupplied = false;
        for (int seat = 0; seat < vehicle.getMaxPassengers(); ++seat) {
            List weaponNames;
            SeatInfo seatInfo = vehicle.getSeat(seat);
            if (seatInfo == null || (weaponNames = seatInfo.weapons()) == null || weaponNames.isEmpty()) continue;
            for (int weaponIdx = 0; weaponIdx < weaponNames.size(); ++weaponIdx) {
                List consumers;
                GunData gunData = vehicle.getGunData(seat, weaponIdx);
                if (gunData == null || gunData.hasInfiniteBackupAmmo((Entity)vehicle) || (consumers = (List)gunData.get(GunProp.AMMO_CONSUMER)) == null || consumers.isEmpty()) continue;
                for (AmmoConsumer consumer : consumers) {
                    if (!this.resupplyWeapon(vehicle, gunData, config, vehicleRule, globalFallbackFill, consumer)) continue;
                    anyResupplied = true;
                }
            }
        }
        return anyResupplied;
    }

    private boolean resupplyWeapon(VehicleEntity vehicle, GunData gunData, SupplyStationConfig config, SupplyStationConfig.ResupplyRule vehicleRule, int globalFallbackFill, AmmoConsumer consumer) {
        String ammoKey = this.getAmmoKey(consumer);
        if (ammoKey.isEmpty()) {
            return false;
        }
        if (config.isPackageBasedMode(vehicleRule, ammoKey)) {
            return this.resupplyPackageWeapon(vehicle, gunData, config.getAmmoRule(vehicleRule, ammoKey));
        }
        if (config.isMagazineMode(vehicleRule, ammoKey) && (Integer)gunData.get(GunProp.MAGAZINE) > 0) {
            return this.resupplyMagazineWeapon(vehicle, gunData, ammoKey, consumer);
        }
        return this.resupplyFixedWeapon(vehicle, gunData, config, vehicleRule, ammoKey, globalFallbackFill, consumer);
    }

    private boolean resupplyMagazineWeapon(VehicleEntity vehicle, GunData gunData, String ammoKey, AmmoConsumer consumer) {
        int backupAmmo;
        int magazine = (Integer)gunData.get(GunProp.MAGAZINE);
        int currentAmmo = gunData.ammo.get();
        int totalAmmo = currentAmmo + (backupAmmo = this.countBackupAmmoForConsumer(vehicle, gunData, consumer));
        if (totalAmmo >= magazine) {
            return false;
        }
        int ammoNeeded = magazine - totalAmmo;
        if (ammoNeeded <= 0) {
            return false;
        }
        this.supplyBackupAmmoToVehicle(vehicle, ammoNeeded, consumer);
        return true;
    }

    private boolean resupplyFixedWeapon(VehicleEntity vehicle, GunData gunData, SupplyStationConfig config, SupplyStationConfig.ResupplyRule vehicleRule, String ammoKey, int globalFallbackFill, AmmoConsumer consumer) {
        boolean isSelected;
        int currentLoaded;
        SupplyStationConfig.AmmoTypeRule ammoRule = config.getAmmoRule(vehicleRule, ammoKey);
        int fillAmount = ammoRule.fixedAmount > 0 ? ammoRule.fixedAmount : globalFallbackFill;
        int currentBackup = this.countBackupAmmoForConsumer(vehicle, gunData, consumer);
        int totalAmmo = currentBackup + (currentLoaded = (isSelected = consumer == gunData.selectedAmmoConsumer()) ? gunData.ammo.get() : 0);
        if (totalAmmo >= fillAmount) {
            return false;
        }
        int ammoToAdd = fillAmount - totalAmmo;
        this.supplyBackupAmmoToVehicle(vehicle, ammoToAdd, consumer);
        return true;
    }

    private boolean resupplyPackageWeapon(VehicleEntity vehicle, GunData gunData, SupplyStationConfig.AmmoTypeRule ammoRule) {
        ItemStack stack;
        int current;
        String customItemId = ammoRule.customItem;
        if (customItemId == null || customItemId.isEmpty()) {
            return false;
        }
        ResourceLocation itemLocation = ResourceLocation.m_135820_((String)customItemId);
        if (itemLocation == null) {
            return false;
        }
        Item customItem = (Item)ForgeRegistries.ITEMS.getValue(itemLocation);
        if (customItem == null) {
            return false;
        }
        int target = Math.max(1, ammoRule.customItemCount);
        int toAdd = target - (current = this.countBonusItem(vehicle, customItem));
        if (toAdd <= 0) {
            return false;
        }
        Optional handlerOpt = vehicle.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (handlerOpt.isEmpty()) {
            return false;
        }
        IItemHandler handler = (IItemHandler)handlerOpt.get();
        int inserted = InventoryTool.insertItem((IItemHandler)handler, (ItemStack)(stack = new ItemStack((ItemLike)customItem, toAdd)), (int)toAdd);
        if (inserted < toAdd) {
            int remaining = toAdd - inserted;
            stack.m_41764_(remaining);
            InventoryTool.insertItem((IItemHandler)handler, (ItemStack)stack, (int)remaining);
        }
        return inserted > 0;
    }

    private void supplyBackupAmmoToVehicle(VehicleEntity vehicle, int ammoAmount, AmmoConsumer consumer) {
        int itemsNeeded;
        int loadAmount = consumer.getLoadAmount();
        if (loadAmount <= 0) {
            loadAmount = 1;
        }
        if ((itemsNeeded = (ammoAmount + loadAmount - 1) / loadAmount) <= 0) {
            return;
        }
        Optional handlerOpt = vehicle.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (handlerOpt.isEmpty()) {
            return;
        }
        IItemHandler handler = (IItemHandler)handlerOpt.get();
        ItemStack ammoStack = consumer.stack().m_41777_();
        if (ammoStack.m_41619_()) {
            return;
        }
        int inserted = InventoryTool.insertItem((IItemHandler)handler, (ItemStack)ammoStack, (int)itemsNeeded);
        if (inserted < itemsNeeded) {
            int remaining = itemsNeeded - inserted;
            ammoStack.m_41764_(remaining);
            InventoryTool.insertItem((IItemHandler)handler, (ItemStack)ammoStack, (int)remaining);
        }
    }

    protected void m_7378_(@NotNull CompoundTag compound) {
        super.m_7378_(compound);
        if (compound.m_128441_("SupplyRange")) {
            this.setSupplyRange(compound.m_128457_("SupplyRange"));
        }
        if (compound.m_128441_("NonMagazineFillAmount")) {
            this.setNonMagazineFillAmount(compound.m_128451_("NonMagazineFillAmount"));
        }
        if (compound.m_128441_("SupplyInterval")) {
            this.setSupplyInterval(compound.m_128451_("SupplyInterval"));
        }
        if (compound.m_128441_("SupplyTime")) {
            this.setSupplyTime(compound.m_128451_("SupplyTime"));
        }
        if (compound.m_128441_("Active")) {
            this.setActive(compound.m_128471_("Active"));
        }
        if (compound.m_128441_("TickCounter")) {
            this.tickCounter = compound.m_128451_("TickCounter");
        }
        if (compound.m_128441_("ChargeTick")) {
            this.chargeTick = compound.m_128451_("ChargeTick");
        }
        if (compound.m_128441_("ChargeCooldown")) {
            this.chargeCooldown = compound.m_128451_("ChargeCooldown");
        }
    }

    public void m_7380_(@NotNull CompoundTag compound) {
        super.m_7380_(compound);
        compound.m_128350_("SupplyRange", this.getSupplyRange());
        compound.m_128405_("NonMagazineFillAmount", this.getNonMagazineFillAmount());
        compound.m_128405_("SupplyInterval", this.getSupplyInterval());
        compound.m_128405_("SupplyTime", this.getSupplyTime());
        compound.m_128379_("Active", this.isActive());
        compound.m_128405_("TickCounter", this.tickCounter);
        compound.m_128405_("ChargeTick", this.chargeTick);
        compound.m_128405_("ChargeCooldown", this.chargeCooldown);
    }
}

