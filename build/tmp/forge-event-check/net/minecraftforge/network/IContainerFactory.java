/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType$MenuSupplier
 */
package net.minecraftforge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public interface IContainerFactory<T extends AbstractContainerMenu>
extends MenuType.MenuSupplier<T> {
    public T create(int var1, Inventory var2, FriendlyByteBuf var3);

    default public T m_39994_(int p_create_1_, Inventory p_create_2_) {
        return this.create(p_create_1_, p_create_2_, null);
    }
}

