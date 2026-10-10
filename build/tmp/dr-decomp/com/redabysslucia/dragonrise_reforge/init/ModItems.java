/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraftforge.common.ForgeSpawnEggItem
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.redabysslucia.dragonrise_reforge.init;

import com.redabysslucia.dragonrise_reforge.init.ModEntities;
import com.redabysslucia.dragonrise_reforge.item.AmmoSupplyStationDeployer;
import com.redabysslucia.dragonrise_reforge.item.AttackDroneDeployerItem;
import com.redabysslucia.dragonrise_reforge.item.ClusterChargeItem;
import com.redabysslucia.dragonrise_reforge.item.DSHKDeployer;
import com.redabysslucia.dragonrise_reforge.item.GeneratorDeployer;
import com.redabysslucia.dragonrise_reforge.item.HJ8Deployer;
import com.redabysslucia.dragonrise_reforge.item.KV1Deployer;
import com.redabysslucia.dragonrise_reforge.item.M2Deployer;
import com.redabysslucia.dragonrise_reforge.item.MK19Deployer;
import com.redabysslucia.dragonrise_reforge.item.Pak40Deployer;
import com.redabysslucia.dragonrise_reforge.item.R6DroneDeployerItem;
import com.redabysslucia.dragonrise_reforge.item.R9M133Deployer;
import com.redabysslucia.dragonrise_reforge.item.SHIELDDeployer;
import com.redabysslucia.dragonrise_reforge.item.ZU23Deployer;
import com.redabysslucia.dragonrise_reforge.item.armor.AljinHelmet;
import com.redabysslucia.dragonrise_reforge.item.armor.CN21;
import com.redabysslucia.dragonrise_reforge.item.armor.CNchest;
import com.redabysslucia.dragonrise_reforge.item.armor.CNfast;
import com.redabysslucia.dragonrise_reforge.item.armor.Desert07Chest;
import com.redabysslucia.dragonrise_reforge.item.armor.Desert07Helmet;
import com.redabysslucia.dragonrise_reforge.item.armor.Desert07Pants;
import com.redabysslucia.dragonrise_reforge.item.armor.FASTHelmet;
import com.redabysslucia.dragonrise_reforge.item.armor.Gorka3;
import com.redabysslucia.dragonrise_reforge.item.armor.Gorka3Leggings;
import com.redabysslucia.dragonrise_reforge.item.armor.KR06Chest;
import com.redabysslucia.dragonrise_reforge.item.armor.KR06Helmet;
import com.redabysslucia.dragonrise_reforge.item.armor.KR06Pants;
import com.redabysslucia.dragonrise_reforge.item.armor.MED21Chest;
import com.redabysslucia.dragonrise_reforge.item.armor.MSVChest;
import com.redabysslucia.dragonrise_reforge.item.armor.MSVPants;
import com.redabysslucia.dragonrise_reforge.item.armor.Ocean07Chest;
import com.redabysslucia.dragonrise_reforge.item.armor.Ocean07Helmet;
import com.redabysslucia.dragonrise_reforge.item.armor.Ocean07Pants;
import com.redabysslucia.dragonrise_reforge.item.armor.Pants21;
import com.redabysslucia.dragonrise_reforge.item.armor.Sniper21Helmet;
import com.redabysslucia.dragonrise_reforge.item.armor.T21Helmet;
import com.redabysslucia.dragonrise_reforge.item.armor.UNHelmet;
import com.redabysslucia.dragonrise_reforge.item.qjz89Deployer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"dragonrise_reforge");
    public static final RegistryObject<Item> TAB_ICON = REGISTRY.register("tab_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SF_TAB_ICON = REGISTRY.register("sf_tab_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SOC_TAB_ICON = REGISTRY.register("soc_tab_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WW2_TAB_ICON = REGISTRY.register("ww2_tab_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MISC_ICON = REGISTRY.register("misc_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MK19_DEPLOYER = REGISTRY.register("mk19", MK19Deployer::new);
    public static final RegistryObject<Item> HJ8_DEPLOYER = REGISTRY.register("hj8", HJ8Deployer::new);
    public static final RegistryObject<Item> R9M133_DEPLOYER = REGISTRY.register("9m133", R9M133Deployer::new);
    public static final RegistryObject<Item> ZU23_DEPLOYER = REGISTRY.register("zu23", ZU23Deployer::new);
    public static final RegistryObject<Item> qjz89_DEPLOYER = REGISTRY.register("qjz89", qjz89Deployer::new);
    public static final RegistryObject<Item> DSHK_DEPLOYER = REGISTRY.register("dshk", DSHKDeployer::new);
    public static final RegistryObject<Item> M2_DEPLOYER = REGISTRY.register("m2", M2Deployer::new);
    public static final RegistryObject<Item> SHIELD_DEPLOYER = REGISTRY.register("shield", SHIELDDeployer::new);
    public static final RegistryObject<Item> AMMO_SUPPLY_STATION_DEPLOYER = REGISTRY.register("ammo_supply_station", AmmoSupplyStationDeployer::new);
    public static final RegistryObject<Item> GENERATOR_DEPLOYER = REGISTRY.register("generator_deployer", GeneratorDeployer::new);
    public static final RegistryObject<Item> CN21 = REGISTRY.register("cn21", CN21::new);
    public static final RegistryObject<Item> CNCHEST = REGISTRY.register("cnchest", CNchest::new);
    public static final RegistryObject<Item> CNFAST = REGISTRY.register("cnfast", CNfast::new);
    public static final RegistryObject<Item> MSV_CHEST = REGISTRY.register("msv_chest", MSVChest::new);
    public static final RegistryObject<Item> GORKA3 = REGISTRY.register("gorka3", Gorka3::new);
    public static final RegistryObject<Item> ALJIN_HELMET = REGISTRY.register("aljin_helmet", AljinHelmet::new);
    public static final RegistryObject<Item> GORKA3_LEGGINGS = REGISTRY.register("gorka3_leggings", Gorka3Leggings::new);
    public static final RegistryObject<Item> MED21_CHEST = REGISTRY.register("med21_chest", MED21Chest::new);
    public static final RegistryObject<Item> T21_HELMET = REGISTRY.register("t21_helmet", T21Helmet::new);
    public static final RegistryObject<Item> FAST_HELMET = REGISTRY.register("fast_helmet", FASTHelmet::new);
    public static final RegistryObject<Item> SNIPER21_HELMET = REGISTRY.register("sniper21_helmet", Sniper21Helmet::new);
    public static final RegistryObject<Item> PANTS21 = REGISTRY.register("pants21", Pants21::new);
    public static final RegistryObject<Item> MSV_PANTS = REGISTRY.register("msv_pants", MSVPants::new);
    public static final RegistryObject<Item> DESERT07_HELMET = REGISTRY.register("desert07_helmet", Desert07Helmet::new);
    public static final RegistryObject<Item> DESERT07_CHEST = REGISTRY.register("desert07_chest", Desert07Chest::new);
    public static final RegistryObject<Item> DESERT07_PANTS = REGISTRY.register("desert07_pants", Desert07Pants::new);
    public static final RegistryObject<Item> OCEAN07_HELMET = REGISTRY.register("ocean07_helmet", Ocean07Helmet::new);
    public static final RegistryObject<Item> OCEAN07_CHEST = REGISTRY.register("ocean07_chest", Ocean07Chest::new);
    public static final RegistryObject<Item> OCEAN07_PANTS = REGISTRY.register("ocean07_pants", Ocean07Pants::new);
    public static final RegistryObject<Item> KR06_HELMET = REGISTRY.register("kr06_helmet", KR06Helmet::new);
    public static final RegistryObject<Item> KR06_CHEST = REGISTRY.register("kr06_chest", KR06Chest::new);
    public static final RegistryObject<Item> KR06_PANTS = REGISTRY.register("kr06_pants", KR06Pants::new);
    public static final RegistryObject<Item> TERRORIST_SPAWN_EGG = REGISTRY.register("terrorist_spawn_egg", () -> new ForgeSpawnEggItem(() -> (EntityType)ModEntities.TERRORIST.get(), -11584987, -14014413, new Item.Properties()));
    public static final RegistryObject<Item> UN_HELMET = REGISTRY.register("un_helmet", UNHelmet::new);
    public static final RegistryObject<Item> SPRAY_CAN = REGISTRY.register("spray_can", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CLUSTER_CHARGE = REGISTRY.register("cluster_charge", () -> new ClusterChargeItem(new Item.Properties()));
    public static final RegistryObject<Item> R6_DRONE = REGISTRY.register("r6_drone", () -> new R6DroneDeployerItem(new Item.Properties()));
    public static final RegistryObject<Item> ATTACK_DRONE = REGISTRY.register("attack_drone", () -> new AttackDroneDeployerItem(new Item.Properties()));
    public static final RegistryObject<Item> KEVLAR = REGISTRY.register("kevlar", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> KV1_DEPLOYER = REGISTRY.register("kv1", KV1Deployer::new);
    public static final RegistryObject<Item> PAK40_DEPLOYER = REGISTRY.register("pak40", Pak40Deployer::new);

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}

