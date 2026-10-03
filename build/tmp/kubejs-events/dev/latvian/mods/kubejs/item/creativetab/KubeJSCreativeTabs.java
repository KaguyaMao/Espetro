/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.registry.registries.DeferredRegister
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 */
package dev.latvian.mods.kubejs.item.creativetab;

import dev.architectury.registry.registries.DeferredRegister;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class KubeJSCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create((String)"kubejs", (ResourceKey)Registries.f_279569_);

    public static void init() {
        if (!CommonProperties.get().serverOnly) {
            REGISTER.register("tab", () -> MiscPlatformHelper.get().creativeModeTab((Component)Component.m_237113_((String)"KubeJS"), () -> {
                ItemStack is = ItemStackJS.of(CommonProperties.get().creativeModeTabIcon);
                return is.m_41619_() ? Items.f_42493_.m_7968_() : is;
            }, (params, output) -> {
                for (BuilderBase<Item> builderBase : RegistryInfo.ITEM) {
                    output.m_246342_(builderBase.get().m_7968_());
                }
            }));
            REGISTER.register();
        }
    }
}

