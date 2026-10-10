/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package frontline.combat.fcp.init;

import frontline.combat.fcp.init.ModFoods;
import frontline.combat.fcp.item.varies.SprayItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"fcp");
    public static final RegistryObject<Item> TERRORIST_TAB_ICON = REGISTRY.register("terrorist_tab_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RUSSIAN_TAB_ICON = REGISTRY.register("russian_tab_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> AMERICAN_TAB_ICON = REGISTRY.register("american_tab_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DELICIOUS_SNACK = REGISTRY.register("delicious_snack", () -> new Item(new Item.Properties().m_41489_(ModFoods.DELICIOUS_SNACK)));
    public static final RegistryObject<Item> REDBULL = REGISTRY.register("redbull", () -> new Item(new Item.Properties().m_41489_(ModFoods.REDBULL).m_41487_(1)));
    public static final RegistryObject<Item> SPRAY = REGISTRY.register("spray", () -> new SprayItem());
}

