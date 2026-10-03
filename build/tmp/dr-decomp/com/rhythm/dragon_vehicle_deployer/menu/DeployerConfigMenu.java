/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ContainerData
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.SimpleContainerData
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Block
 */
package com.rhythm.dragon_vehicle_deployer.menu;

import com.rhythm.dragon_vehicle_deployer.DragonVehicleDeployer;
import com.rhythm.dragon_vehicle_deployer.menu.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class DeployerConfigMenu
extends AbstractContainerMenu {
    private final BlockPos pos;
    private final ContainerData data;

    public DeployerConfigMenu(int windowId, Inventory inv, BlockPos pos) {
        this(windowId, inv, pos, (ContainerData)new SimpleContainerData(3));
    }

    public DeployerConfigMenu(int windowId, Inventory inv, BlockPos pos, ContainerData data) {
        super((MenuType)ModMenuTypes.DEPLOYER_CONFIG_MENU.get(), windowId);
        this.pos = pos;
        this.data = data;
        this.m_38884_(data);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getSpawnIntervalSeconds() {
        return this.data.m_6413_(0);
    }

    public boolean isAutoSpawnEnabled() {
        return this.data.m_6413_(1) != 0;
    }

    public int getIdleClearTimeoutSeconds() {
        return this.data.m_6413_(2);
    }

    public ItemStack m_7648_(Player player, int index) {
        return ItemStack.f_41583_;
    }

    public boolean m_6875_(Player player) {
        return player.m_20275_((double)this.pos.m_123341_() + 0.5, (double)this.pos.m_123342_() + 0.5, (double)this.pos.m_123343_() + 0.5) <= 64.0 && player.m_9236_().m_8055_(this.pos).m_60713_((Block)DragonVehicleDeployer.VEHICLE_DEPLOYER_BLOCK.get());
    }
}

