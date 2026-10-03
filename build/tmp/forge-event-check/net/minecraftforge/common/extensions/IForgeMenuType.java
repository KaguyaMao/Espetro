/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.flag.FeatureFlags
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 */
package net.minecraftforge.common.extensions;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;

public interface IForgeMenuType<T> {
    public static <T extends AbstractContainerMenu> MenuType<T> create(IContainerFactory<T> factory) {
        return new MenuType(factory, FeatureFlags.f_244332_);
    }

    public T create(int var1, Inventory var2, FriendlyByteBuf var3);
}

