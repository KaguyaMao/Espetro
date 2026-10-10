/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.flag.FeatureFlags
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.MenuType$MenuSupplier
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTab$DisplayItemsGenerator
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.fml.ModContainer
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.ModLoader
 *  net.minecraftforge.fml.loading.moddiscovery.ModInfo
 *  net.minecraftforge.forgespi.language.IModInfo
 *  net.minecraftforge.network.IContainerFactory
 */
package dev.latvian.mods.kubejs.platform.forge;

import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.network.IContainerFactory;

public class MiscForgeHelper
implements MiscPlatformHelper {
    @Override
    public void setModName(PlatformWrapper.ModInfo info, String name) {
        try {
            IModInfo iModInfo;
            Optional mc = ModList.get().getModContainerById(info.getId());
            if (mc.isPresent() && (iModInfo = ((ModContainer)mc.get()).getModInfo()) instanceof ModInfo) {
                ModInfo i = (ModInfo)iModInfo;
                Field field = ModInfo.class.getDeclaredField("displayName");
                field.setAccessible(true);
                field.set(i, name);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public MobCategory getMobCategory(String name) {
        return MobCategory.byName((String)name);
    }

    @Override
    public boolean isDataGen() {
        return ModLoader.isDataGenRunning();
    }

    @Override
    public long ingotFluidAmount() {
        return 90L;
    }

    @Override
    public long bottleFluidAmount() {
        return 250L;
    }

    @Override
    public CreativeModeTab creativeModeTab(Component name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator content) {
        return CreativeModeTab.builder().m_257941_(name).m_257737_(icon).m_257501_(content).m_257652_();
    }

    @Override
    public MenuType<KubeJSMenu> createMenuType() {
        return new MenuType((MenuType.MenuSupplier)((IContainerFactory)KubeJSMenu::new), FeatureFlags.f_244377_);
    }
}

