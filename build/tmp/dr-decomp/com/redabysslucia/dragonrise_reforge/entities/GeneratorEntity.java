/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.inventory.menu.ChargingStationMenu
 *  com.atsuishio.superbwarfare.network.dataslot.ContainerEnergyData
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.Container
 *  net.minecraft.world.ContainerHelper
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.AABB
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.common.util.LazyOptional
 *  net.minecraftforge.energy.EnergyStorage
 *  net.minecraftforge.items.ItemStackHandler
 *  org.jetbrains.annotations.NotNull
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.inventory.menu.ChargingStationMenu;
import com.atsuishio.superbwarfare.network.dataslot.ContainerEnergyData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class GeneratorEntity
extends VehicleEntity
implements MenuProvider {
    private static final int SLOT_FUEL = 0;
    private static final int SLOT_CHARGE = 1;
    private static final int MAX_DATA_COUNT = 4;
    public static final int MAX_ENERGY = 4000000;
    public static final int DEFAULT_FUEL_TIME = 1600;
    public static final int CHARGE_SPEED = 128;
    public static final int CHARGE_OTHER_SPEED = 100000;
    public static final int CHARGE_RADIUS = 8;
    private final NonNullList<ItemStack> generatorItems = NonNullList.m_122780_((int)2, (Object)ItemStack.f_41583_);
    private LazyOptional<EnergyStorage> energyHandler;
    private final ItemStackHandler itemHandler = new ItemStackHandler(2);
    public int fuelTick = 0;
    public int maxFuelTick = 1600;
    public boolean showRange = false;
    protected final ContainerEnergyData dataAccess = new ContainerEnergyData(){

        public long get(int index) {
            return switch (index) {
                case 0 -> GeneratorEntity.this.fuelTick;
                case 1 -> GeneratorEntity.this.maxFuelTick;
                case 2 -> {
                    int[] energy = new int[]{0};
                    GeneratorEntity.this.getCapability(ForgeCapabilities.ENERGY).ifPresent(e -> {
                        energy[0] = e.getEnergyStored();
                    });
                    yield energy[0];
                }
                case 3 -> {
                    if (GeneratorEntity.this.showRange) {
                        yield 1L;
                    }
                    yield 0L;
                }
                default -> 0L;
            };
        }

        public void set(int index, long value) {
            switch (index) {
                case 0: {
                    GeneratorEntity.this.fuelTick = (int)value;
                    break;
                }
                case 1: {
                    GeneratorEntity.this.maxFuelTick = (int)value;
                    break;
                }
                case 2: {
                    GeneratorEntity.this.getCapability(ForgeCapabilities.ENERGY).ifPresent(e -> e.receiveEnergy((int)value, false));
                    break;
                }
                case 3: {
                    GeneratorEntity.this.showRange = value == 1L;
                }
            }
        }

        public int getCount() {
            return 4;
        }
    };

    public GeneratorEntity(EntityType<? extends GeneratorEntity> type, Level level) {
        super(type, level);
        this.energyHandler = LazyOptional.of(() -> new EnergyStorage(4000000));
    }

    protected void m_8097_() {
        super.m_8097_();
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

    private void chargeEntity(EnergyStorage handler) {
        if (this.m_9236_() == null) {
            return;
        }
        if (this.m_9236_().m_46467_() % 20L != 0L) {
            return;
        }
        AABB searchBox = this.m_20191_().m_82400_(8.0);
        this.m_9236_().m_45976_(Entity.class, searchBox).forEach(entity -> entity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
            if (cap.canReceive()) {
                int charged = cap.receiveEnergy(Math.min(handler.getEnergyStored(), 2000000), false);
                handler.extractEnergy(charged, false);
            }
        }));
        this.setChanged();
    }

    private void chargeItemStack(EnergyStorage handler) {
        ItemStack stack = (ItemStack)this.generatorItems.get(1);
        if (stack.m_41619_()) {
            return;
        }
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
            if (cap.getEnergyStored() < cap.getMaxEnergyStored()) {
                int charged = cap.receiveEnergy(Math.min(100000, handler.getEnergyStored()), false);
                handler.extractEnergy(Math.min(charged, handler.getEnergyStored()), false);
            }
        });
        this.setChanged();
    }

    private void chargeBlock(EnergyStorage handler) {
        if (this.m_9236_() == null) {
            return;
        }
        BlockPos pos = new BlockPos((int)this.m_20185_(), (int)this.m_20186_(), (int)this.m_20189_());
        for (Direction dir : Direction.values()) {
            BlockEntity be = this.m_9236_().m_7702_(pos.m_121945_(dir));
            if (be == null || !be.getCapability(ForgeCapabilities.ENERGY).isPresent()) continue;
            be.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
                if (cap.canReceive() && cap.getEnergyStored() < cap.getMaxEnergyStored()) {
                    int received = cap.receiveEnergy(Math.min(handler.getEnergyStored(), 100000), false);
                    handler.extractEnergy(received, false);
                    be.m_6596_();
                    this.setChanged();
                }
            });
        }
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.m_9236_().m_5776_()) {
            return;
        }
        this.m_20256_(this.m_20184_().m_82490_(0.85));
        this.energyHandler.ifPresent(handler -> {
            if (handler.getEnergyStored() > 0) {
                this.chargeEntity((EnergyStorage)handler);
            }
            if (handler.getEnergyStored() > 0) {
                this.chargeItemStack((EnergyStorage)handler);
            }
            if (handler.getEnergyStored() > 0) {
                this.chargeBlock((EnergyStorage)handler);
            }
        });
        if (this.fuelTick > 0) {
            --this.fuelTick;
            this.energyHandler.ifPresent(handler -> {
                if (handler.getEnergyStored() < handler.getMaxEnergyStored()) {
                    handler.receiveEnergy(128, false);
                }
            });
        } else if (!((ItemStack)this.generatorItems.get(0)).m_41619_()) {
            int[] flag = new int[]{0};
            this.energyHandler.ifPresent(handler -> {
                if (handler.getEnergyStored() >= handler.getMaxEnergyStored()) {
                    flag[0] = 1;
                }
            });
            if (flag[0] == 1) {
                return;
            }
            ItemStack fuel = (ItemStack)this.generatorItems.get(0);
            int burnTime = ForgeHooks.getBurnTime((ItemStack)fuel, (RecipeType)RecipeType.f_44108_);
            if (fuel.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
                fuel.getCapability(ForgeCapabilities.ENERGY).ifPresent(itemEnergy -> this.energyHandler.ifPresent(handler -> {
                    int toExtract = Math.min(100000, handler.getMaxEnergyStored() - handler.getEnergyStored());
                    if (itemEnergy.canExtract() && handler.canReceive()) {
                        handler.receiveEnergy(itemEnergy.extractEnergy(toExtract, false), false);
                    }
                }));
                this.setChanged();
            } else if (burnTime > 0) {
                this.fuelTick = burnTime;
                this.maxFuelTick = burnTime;
                if (fuel.hasCraftingRemainingItem()) {
                    if (fuel.m_41613_() <= 1) {
                        this.generatorItems.set(0, (Object)fuel.getCraftingRemainingItem());
                    } else {
                        ItemStack copy = fuel.getCraftingRemainingItem().m_41777_();
                        copy.m_41764_(1);
                        ItemEntity itemEntity = new ItemEntity(this.m_9236_(), this.m_20185_() + 0.5, this.m_20186_() + 0.2, this.m_20189_() + 0.5, copy);
                        this.m_9236_().m_7967_((Entity)itemEntity);
                        fuel.m_41774_(1);
                    }
                } else {
                    fuel.m_41774_(1);
                }
                this.setChanged();
            } else if (fuel.m_41720_().m_41472_()) {
                FoodProperties properties = fuel.getFoodProperties(null);
                if (properties == null) {
                    return;
                }
                int nutrition = properties.m_38744_();
                float saturation = properties.m_38745_() * 2.0f * (float)nutrition;
                int tick = nutrition * 80 + (int)(saturation * 200.0f);
                if (fuel.hasCraftingRemainingItem()) {
                    tick += 400;
                }
                fuel.m_41774_(1);
                this.fuelTick = tick;
                this.maxFuelTick = tick;
                this.setChanged();
            }
        }
    }

    protected void m_7378_(@NotNull CompoundTag compound) {
        super.m_7378_(compound);
        if (compound.m_128441_("Energy")) {
            this.getCapability(ForgeCapabilities.ENERGY).ifPresent(e -> ((EnergyStorage)e).deserializeNBT(compound.m_128423_("Energy")));
        }
        this.fuelTick = compound.m_128451_("FuelTick");
        this.maxFuelTick = compound.m_128451_("MaxFuelTick");
        this.showRange = compound.m_128471_("ShowRange");
        this.generatorItems.clear();
        for (int i = 0; i < 2; ++i) {
            this.generatorItems.add((Object)ItemStack.m_41712_((CompoundTag)compound.m_128469_("Item" + i)));
        }
    }

    public void m_7380_(@NotNull CompoundTag compound) {
        super.m_7380_(compound);
        this.getCapability(ForgeCapabilities.ENERGY).ifPresent(e -> compound.m_128365_("Energy", ((EnergyStorage)e).serializeNBT()));
        compound.m_128405_("FuelTick", this.fuelTick);
        compound.m_128405_("MaxFuelTick", this.maxFuelTick);
        compound.m_128379_("ShowRange", this.showRange);
        for (int i = 0; i < this.generatorItems.size(); ++i) {
            compound.m_128365_("Item" + i, (Tag)((ItemStack)this.generatorItems.get(i)).serializeNBT());
        }
    }

    public int getGeneratorContainerSize() {
        return this.generatorItems.size();
    }

    public boolean isGeneratorEmpty() {
        for (ItemStack stack : this.generatorItems) {
            if (stack.m_41619_()) continue;
            return false;
        }
        return true;
    }

    public ItemStack getGeneratorItem(int slot) {
        return (ItemStack)this.generatorItems.get(slot);
    }

    public ItemStack removeGeneratorItem(int slot, int amount) {
        return ContainerHelper.m_18969_(this.generatorItems, (int)slot, (int)amount);
    }

    public ItemStack removeGeneratorItemNoUpdate(int slot) {
        return ContainerHelper.m_18966_(this.generatorItems, (int)slot);
    }

    public void setGeneratorItem(int slot, ItemStack stack) {
        ItemStack itemstack = (ItemStack)this.generatorItems.get(slot);
        boolean flag = !stack.m_41619_() && ItemStack.m_150942_((ItemStack)itemstack, (ItemStack)stack);
        this.generatorItems.set(slot, (Object)stack);
        if (stack.m_41613_() > this.getMaxStackSize()) {
            stack.m_41764_(this.getMaxStackSize());
        }
        if (slot == 0 && !flag) {
            this.setChanged();
        }
    }

    public void clearGeneratorContent() {
        this.generatorItems.clear();
    }

    public Component m_5446_() {
        return Component.m_237115_((String)"container.superbwarfare.charging_station");
    }

    public AbstractContainerMenu m_7208_(int containerId, Inventory playerInventory, Player player) {
        return new ChargingStationMenu(containerId, playerInventory, (Container)new GeneratorContainerBridge(this), this.dataAccess);
    }

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyHandler.cast();
        }
        if (!this.m_213877_() && cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(() -> this.itemHandler).cast();
        }
        return super.getCapability(cap, side);
    }

    public void invalidateCaps() {
        super.invalidateCaps();
        this.energyHandler.invalidate();
    }

    public void reviveCaps() {
        super.reviveCaps();
        this.energyHandler = LazyOptional.of(() -> new EnergyStorage(4000000));
    }

    public static class GeneratorContainerBridge
    implements Container {
        private final GeneratorEntity entity;

        public GeneratorContainerBridge(GeneratorEntity entity) {
            this.entity = entity;
        }

        public int m_6643_() {
            return this.entity.getGeneratorContainerSize();
        }

        public boolean m_7983_() {
            return this.entity.isGeneratorEmpty();
        }

        public ItemStack m_8020_(int slot) {
            return this.entity.getGeneratorItem(slot);
        }

        public ItemStack m_7407_(int slot, int amount) {
            return this.entity.removeGeneratorItem(slot, amount);
        }

        public ItemStack m_8016_(int slot) {
            return this.entity.removeGeneratorItemNoUpdate(slot);
        }

        public void m_6836_(int slot, ItemStack stack) {
            this.entity.setGeneratorItem(slot, stack);
        }

        public boolean m_6542_(Player player) {
            return player.m_20280_((Entity)this.entity) <= 64.0;
        }

        public void m_6211_() {
            this.entity.clearGeneratorContent();
        }

        public void m_6596_() {
            this.entity.setChanged();
        }
    }
}

