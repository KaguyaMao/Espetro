/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.Connection
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ContainerData
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Math
 */
package com.rhythm.dragon_vehicle_deployer.block.entity;

import com.rhythm.dragon_vehicle_deployer.Config;
import com.rhythm.dragon_vehicle_deployer.DragonVehicleDeployer;
import com.rhythm.dragon_vehicle_deployer.block.VehicleDeployerBlock;
import com.rhythm.dragon_vehicle_deployer.menu.DeployerConfigMenu;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Math;

public class VehicleDeployerBlockEntity
extends BlockEntity
implements MenuProvider {
    public CompoundTag entityData = new CompoundTag();
    public int spawnIntervalSeconds = 5;
    public boolean autoSpawnEnabled = true;
    public int idleClearTimeoutSeconds = 300;
    private int tickCounter = 0;
    @Nullable
    private UUID spawnedVehicleUUID = null;
    private boolean vehicleWasOccupied = false;
    private long lastOccupiedGameTime = 0L;
    private boolean idleClearTriggered = false;
    private final ContainerData dataAccess = new ContainerData(){

        public int m_6413_(int index) {
            return switch (index) {
                case 0 -> VehicleDeployerBlockEntity.this.spawnIntervalSeconds;
                case 1 -> {
                    if (VehicleDeployerBlockEntity.this.autoSpawnEnabled) {
                        yield 1;
                    }
                    yield 0;
                }
                case 2 -> VehicleDeployerBlockEntity.this.idleClearTimeoutSeconds;
                default -> 0;
            };
        }

        public void m_8050_(int index, int value) {
            switch (index) {
                case 0: {
                    VehicleDeployerBlockEntity.this.spawnIntervalSeconds = value;
                    break;
                }
                case 1: {
                    VehicleDeployerBlockEntity.this.autoSpawnEnabled = value != 0;
                    break;
                }
                case 2: {
                    VehicleDeployerBlockEntity.this.idleClearTimeoutSeconds = value;
                }
            }
            VehicleDeployerBlockEntity.this.m_6596_();
        }

        public int m_6499_() {
            return 3;
        }
    };

    public VehicleDeployerBlockEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)DragonVehicleDeployer.VEHICLE_DEPLOYER_BLOCK_ENTITY.get(), pos, state);
        try {
            this.spawnIntervalSeconds = (Integer)Config.DEFAULT_SPAWN_INTERVAL.get();
            this.idleClearTimeoutSeconds = (Integer)Config.IDLE_CLEAR_TIMEOUT_SECONDS.get();
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
    }

    public void m_142466_(CompoundTag tag) {
        super.m_142466_(tag);
        this.entityData = new CompoundTag();
        if (tag.m_128441_("EntityType")) {
            this.entityData.m_128359_("EntityType", tag.m_128461_("EntityType"));
        }
        if (tag.m_128441_("Entity")) {
            this.entityData.m_128365_("Entity", (Tag)tag.m_128469_("Entity"));
        }
        this.spawnIntervalSeconds = tag.m_128441_("SpawnIntervalSeconds") ? tag.m_128451_("SpawnIntervalSeconds") : ((Integer)Config.DEFAULT_SPAWN_INTERVAL.get()).intValue();
        this.autoSpawnEnabled = tag.m_128441_("AutoSpawnEnabled") ? tag.m_128471_("AutoSpawnEnabled") : true;
        this.idleClearTimeoutSeconds = tag.m_128441_("IdleClearTimeoutSeconds") ? tag.m_128451_("IdleClearTimeoutSeconds") : ((Integer)Config.IDLE_CLEAR_TIMEOUT_SECONDS.get()).intValue();
        this.spawnedVehicleUUID = tag.m_128403_("SpawnedVehicleUUID") ? tag.m_128342_("SpawnedVehicleUUID") : null;
        this.vehicleWasOccupied = tag.m_128471_("VehicleWasOccupied");
        this.lastOccupiedGameTime = tag.m_128454_("LastOccupiedGameTime");
    }

    protected void m_183515_(CompoundTag tag) {
        super.m_183515_(tag);
        if (this.entityData.m_128441_("EntityType")) {
            tag.m_128359_("EntityType", this.entityData.m_128461_("EntityType"));
        }
        if (this.entityData.m_128441_("Entity")) {
            tag.m_128365_("Entity", (Tag)this.entityData.m_128469_("Entity"));
        }
        tag.m_128405_("SpawnIntervalSeconds", this.spawnIntervalSeconds);
        tag.m_128379_("AutoSpawnEnabled", this.autoSpawnEnabled);
        tag.m_128405_("IdleClearTimeoutSeconds", this.idleClearTimeoutSeconds);
        if (this.spawnedVehicleUUID != null) {
            tag.m_128362_("SpawnedVehicleUUID", this.spawnedVehicleUUID);
        }
        tag.m_128379_("VehicleWasOccupied", this.vehicleWasOccupied);
        tag.m_128356_("LastOccupiedGameTime", this.lastOccupiedGameTime);
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.m_195640_((BlockEntity)this);
    }

    public CompoundTag m_5995_() {
        CompoundTag tag = new CompoundTag();
        this.m_183515_(tag);
        return tag;
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.m_142466_(pkt.m_131708_());
    }

    public void writeEntityInfo(ItemStack stack) {
        CompoundTag tag = BlockItem.m_186336_((ItemStack)stack);
        if (tag == null) {
            return;
        }
        this.entityData = tag.m_6426_();
        this.spawnedVehicleUUID = null;
        this.vehicleWasOccupied = false;
        this.lastOccupiedGameTime = 0L;
        this.idleClearTriggered = false;
        this.m_6596_();
    }

    public void deploy(BlockState state) {
        if (this.f_58857_ == null) {
            return;
        }
        if (this.entityData.m_128441_("EntityType")) {
            EntityType entityType = EntityType.m_20632_((String)this.entityData.m_128461_("EntityType")).orElse(null);
            if (entityType == null) {
                return;
            }
            Entity entity = entityType.m_20615_(this.f_58857_);
            if (entity == null) {
                return;
            }
            if (this.entityData.m_128441_("Entity")) {
                CompoundTag entityTag = this.entityData.m_128469_("Entity").m_6426_();
                entityTag.m_128473_("UUID");
                entity.m_20258_(entityTag);
            }
            Direction direction = (Direction)state.m_61143_((Property)VehicleDeployerBlock.FACING);
            UUID newUUID = UUID.randomUUID();
            entity.m_20084_(newUUID);
            entity.m_6034_((double)this.m_58899_().m_123341_() + 0.5 + (2.0 * Math.random() - 1.0) * (double)0.1f, (double)this.m_58899_().m_123342_() + 1.5 + (2.0 * Math.random() - 1.0) * (double)0.1f, (double)this.m_58899_().m_123343_() + 0.5 + (2.0 * Math.random() - 1.0) * (double)0.1f);
            entity.m_146922_(direction.m_122435_());
            this.f_58857_.m_7967_(entity);
            this.spawnedVehicleUUID = newUUID;
            this.m_6596_();
        }
    }

    private boolean isSpawnedVehicleAlive() {
        Level level = this.f_58857_;
        if (!(level instanceof ServerLevel)) {
            return false;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        if (this.spawnedVehicleUUID == null) {
            return false;
        }
        Entity entity = serverLevel.m_8791_(this.spawnedVehicleUUID);
        return entity != null && entity.m_6084_();
    }

    private void trackOccupancy(Level world) {
        long idleTicks;
        boolean hasPassengers;
        int timeout = this.idleClearTimeoutSeconds;
        if (timeout <= 0) {
            return;
        }
        if (!(world instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)world;
        if (this.spawnedVehicleUUID == null) {
            this.vehicleWasOccupied = false;
            this.lastOccupiedGameTime = 0L;
            return;
        }
        Entity entity = serverLevel.m_8791_(this.spawnedVehicleUUID);
        if (entity == null || !entity.m_6084_()) {
            this.vehicleWasOccupied = false;
            this.lastOccupiedGameTime = 0L;
            this.spawnedVehicleUUID = null;
            return;
        }
        boolean bl = hasPassengers = !entity.m_20197_().isEmpty();
        if (hasPassengers) {
            this.vehicleWasOccupied = true;
            this.lastOccupiedGameTime = world.m_46467_();
        } else if (this.vehicleWasOccupied && (idleTicks = world.m_46467_() - this.lastOccupiedGameTime) >= (long)timeout * 20L) {
            entity.m_146870_();
            this.spawnedVehicleUUID = null;
            this.vehicleWasOccupied = false;
            this.lastOccupiedGameTime = 0L;
            this.idleClearTriggered = true;
            this.tickCounter = 0;
            this.m_6596_();
        }
    }

    public static void tick(Level world, BlockPos pos, BlockState state, VehicleDeployerBlockEntity blockEntity) {
        if (world.f_46443_) {
            return;
        }
        if (!blockEntity.autoSpawnEnabled) {
            return;
        }
        if (!blockEntity.entityData.m_128441_("EntityType")) {
            return;
        }
        blockEntity.trackOccupancy(world);
        if (blockEntity.idleClearTriggered) {
            blockEntity.idleClearTriggered = false;
            blockEntity.deploy(state);
            return;
        }
        ++blockEntity.tickCounter;
        if (blockEntity.tickCounter < blockEntity.spawnIntervalSeconds * 20) {
            return;
        }
        blockEntity.tickCounter = 0;
        if (blockEntity.isSpawnedVehicleAlive()) {
            return;
        }
        blockEntity.deploy(state);
    }

    public Component m_5446_() {
        return Component.m_237115_((String)"container.dragonrise_reforge.deployer_config");
    }

    @Nullable
    public AbstractContainerMenu m_7208_(int containerId, Inventory playerInventory, Player player) {
        return new DeployerConfigMenu(containerId, playerInventory, this.m_58899_(), this.dataAccess);
    }
}

