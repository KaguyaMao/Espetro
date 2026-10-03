/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ChestMenu
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.player.InventoryEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

@Info(value="Invoked when a player opens a chest.\n\nSame as `PlayerEvents.inventoryOpened`, but only for chests.\n")
public class ChestEventJS
extends InventoryEventJS {
    public ChestEventJS(Player player, AbstractContainerMenu menu) {
        super(player, menu);
    }

    @Info(value="Gets the chest inventory.")
    public Container getInventory() {
        return ((ChestMenu)this.getInventoryContainer()).m_39261_();
    }

    @Info(value="Gets the chest block.")
    @Nullable
    public BlockContainerJS getBlock() {
        Container container = this.getInventory();
        if (container instanceof BlockEntity) {
            BlockEntity be = (BlockEntity)container;
            return this.getLevel().kjs$getBlock(be);
        }
        return null;
    }
}

