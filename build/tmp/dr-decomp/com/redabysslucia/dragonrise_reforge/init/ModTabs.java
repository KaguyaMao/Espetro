/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.item.container.ContainerBlockItem
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.RegistryObject
 */
package com.redabysslucia.dragonrise_reforge.init;

import com.atsuishio.superbwarfare.item.container.ContainerBlockItem;
import com.redabysslucia.dragonrise_reforge.init.ModEntities;
import com.redabysslucia.dragonrise_reforge.init.ModItems;
import com.rhythm.dragon_vehicle_deployer.DragonVehicleDeployer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create((ResourceKey)Registries.f_279569_, (String)"dragonrise_reforge");
    public static final RegistryObject<CreativeModeTab> MBT_TAB = TABS.register("dragonrise_reforge", () -> CreativeModeTab.builder().m_257941_((Component)Component.m_237115_((String)"item_group.dragonrise_reforge.title")).m_257737_(() -> new ItemStack((ItemLike)ModItems.TAB_ICON.get())).m_257501_((param, output) -> {
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.ZTZ99A.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.ZTZ99AH.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.ZTQ15.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.VT4A1.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.VT4B.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.ZTZ59A.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.TYPE100.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.ZTD05.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.ZTZ96A.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.ZBD05.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.ZBD04A.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.ZBL08.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.ZLT_11.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.CSK181.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.CM34.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.AA625E.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.SX1.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.MV3.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.MV3_ARMED.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.MV3_SUPPLY.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.Z10A.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.Z10ME.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.Z20.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.Z9.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.J20.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.J20VTOL.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.J35.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.J16.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.J15T.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.J11.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.J10C.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.J10.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.JF17.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.J8.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.Q5.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.T90MH.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.T80.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.T80B.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.T72B3.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMPT72.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMP3.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMD4M.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.R2S25M.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.PROJECT640.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.AKM.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BRDM2.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.URAL4320_ZU23.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.URAL4320.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.URAL4320_SUPPLY.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.S2S38.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.TUNGUSKA.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.ZSU234.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.KA50.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.M1A2SEPV2.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.M1A1HC.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.M10BOOKER.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.M3A3.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.M113.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.M270.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.HUMVEE.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.HUMVEETOW.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.FAVA.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.AH64.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.AH1F.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.UH60.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.AC130.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.F14.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.F15E.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.F16C.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.AV8B.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.FA18E.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.AMX56.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.EC665.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.REFALE.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.REFALEAA.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.L1A2.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.LEOPARD2A4.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.FLARAKPZ1.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.STRV103.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.CV90.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.JAS39E.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.NH90.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.TJGC.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.HYR0.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.CYBORG_TANK.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.WLHGZU23.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.WLSC.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.PZBJY.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.TOYOTASEIKI.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.motuo.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.NPDS114.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.NPDS514.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.NPDS810.get())));
    }).m_257652_());
    public static final RegistryObject<CreativeModeTab> WW2_TAB = TABS.register("ww2_tab", () -> CreativeModeTab.builder().m_257941_((Component)Component.m_237115_((String)"item_group.dragonrise_reforge.ww2title")).m_257737_(() -> new ItemStack((ItemLike)ModItems.WW2_TAB_ICON.get())).m_257501_((param, output) -> {
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.IS2.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.T3485.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.KV1.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.T3476.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.MAUS.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.TIGER.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.PANZER4.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.PERSHING.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.M4A2105.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.M4A2.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.M3Stuart.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.LVT.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.AAV7A1.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.AAVC7C1.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.F4U.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.COMET.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.CHURCHILL_VII.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.MARKV.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.CHALLENGER_DS.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.CAMEL.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.type97Q.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.type97.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.type3.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.DARKBEAR.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.MI24V.get())));
    }).m_257652_());
    public static final RegistryObject<CreativeModeTab> MISC_TAB = TABS.register("misc_tab", () -> CreativeModeTab.builder().m_257941_((Component)Component.m_237115_((String)"item_group.dragonrise_reforge.misc_tab.title")).m_257737_(() -> new ItemStack((ItemLike)ModItems.MISC_ICON.get())).m_257501_((param, output) -> {
        output.m_246326_((ItemLike)ModItems.MK19_DEPLOYER.get());
        output.m_246326_((ItemLike)ModItems.ZU23_DEPLOYER.get());
        output.m_246326_((ItemLike)ModItems.HJ8_DEPLOYER.get());
        output.m_246326_((ItemLike)ModItems.R9M133_DEPLOYER.get());
        output.m_246326_((ItemLike)ModItems.qjz89_DEPLOYER.get());
        output.m_246326_((ItemLike)ModItems.DSHK_DEPLOYER.get());
        output.m_246326_((ItemLike)ModItems.M2_DEPLOYER.get());
        output.m_246326_((ItemLike)ModItems.SHIELD_DEPLOYER.get());
        output.m_246326_((ItemLike)ModItems.AMMO_SUPPLY_STATION_DEPLOYER.get());
        output.m_246326_((ItemLike)DragonVehicleDeployer.VEHICLE_DEPLOYER_BLOCK_ITEM.get());
        output.m_246326_((ItemLike)ModItems.CNCHEST.get());
        output.m_246326_((ItemLike)ModItems.CN21.get());
        output.m_246326_((ItemLike)ModItems.CNFAST.get());
        output.m_246326_((ItemLike)ModItems.MSV_CHEST.get());
        output.m_246326_((ItemLike)ModItems.ALJIN_HELMET.get());
        output.m_246326_((ItemLike)ModItems.GORKA3.get());
        output.m_246326_((ItemLike)ModItems.GORKA3_LEGGINGS.get());
        output.m_246326_((ItemLike)ModItems.MED21_CHEST.get());
        output.m_246326_((ItemLike)ModItems.T21_HELMET.get());
        output.m_246326_((ItemLike)ModItems.FAST_HELMET.get());
        output.m_246326_((ItemLike)ModItems.SNIPER21_HELMET.get());
        output.m_246326_((ItemLike)ModItems.PANTS21.get());
        output.m_246326_((ItemLike)ModItems.MSV_PANTS.get());
        output.m_246326_((ItemLike)ModItems.DESERT07_HELMET.get());
        output.m_246326_((ItemLike)ModItems.DESERT07_CHEST.get());
        output.m_246326_((ItemLike)ModItems.DESERT07_PANTS.get());
        output.m_246326_((ItemLike)ModItems.OCEAN07_HELMET.get());
        output.m_246326_((ItemLike)ModItems.OCEAN07_CHEST.get());
        output.m_246326_((ItemLike)ModItems.OCEAN07_PANTS.get());
        output.m_246326_((ItemLike)ModItems.UN_HELMET.get());
        output.m_246326_((ItemLike)ModItems.KR06_HELMET.get());
        output.m_246326_((ItemLike)ModItems.KR06_CHEST.get());
        output.m_246326_((ItemLike)ModItems.KR06_PANTS.get());
        output.m_246326_((ItemLike)ModItems.SPRAY_CAN.get());
        output.m_246326_((ItemLike)ModItems.CLUSTER_CHARGE.get());
        output.m_246326_((ItemLike)ModItems.R6_DRONE.get());
        output.m_246326_((ItemLike)ModItems.ATTACK_DRONE.get());
        output.m_246326_((ItemLike)ModItems.KEVLAR.get());
        output.m_246326_((ItemLike)ModItems.PAK40_DEPLOYER.get());
    }).m_257652_());
}

