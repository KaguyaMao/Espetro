/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTab$DisplayItemsGenerator
 *  net.minecraft.world.item.ItemStack
 */
package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.util.Lazy;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public interface MiscPlatformHelper {
    public static final Lazy<MiscPlatformHelper> INSTANCE = Lazy.serviceLoader(MiscPlatformHelper.class);

    public static MiscPlatformHelper get() {
        return INSTANCE.get();
    }

    public void setModName(PlatformWrapper.ModInfo var1, String var2);

    public MobCategory getMobCategory(String var1);

    public boolean isDataGen();

    public long ingotFluidAmount();

    public long bottleFluidAmount();

    public CreativeModeTab creativeModeTab(Component var1, Supplier<ItemStack> var2, CreativeModeTab.DisplayItemsGenerator var3);

    public MenuType<KubeJSMenu> createMenuType();
}

