/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraftforge.common.extensions.IForgeMenuType
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.rhythm.dragon_vehicle_deployer.menu;

import com.rhythm.dragon_vehicle_deployer.menu.DeployerConfigMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.MENU_TYPES, (String)"dragonrise_reforge");
    public static final RegistryObject<MenuType<DeployerConfigMenu>> DEPLOYER_CONFIG_MENU = REGISTRY.register("deployer_config_menu", () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.m_130135_();
        return new DeployerConfigMenu(windowId, inv, pos);
    }));
}

