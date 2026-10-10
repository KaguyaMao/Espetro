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
 *  net.minecraftforge.event.BuildCreativeModeTabContentsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.RegistryObject
 */
package tech.vvp.vvp.init;

import com.atsuishio.superbwarfare.item.container.ContainerBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import tech.vvp.vvp.init.ModEntities;
import tech.vvp.vvp.init.ModItems;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create((ResourceKey)Registries.f_279569_, (String)"vvp");
    public static final RegistryObject<CreativeModeTab> NATO_VEHICLE_TAB = TABS.register("nato_tab", () -> CreativeModeTab.builder().m_257737_(() -> new ItemStack((ItemLike)ModItems.NATO_TAB_ICON.get())).m_257941_((Component)Component.m_237115_((String)"natotab.vvp_natovehicle_tab")).m_257501_((parameters, output) -> {
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.PUMA.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BRADLEY.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.AJAX.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.CV_90.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.CENTAURO.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.STRYKER.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.STRYKER_M1296.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.M1A2_HV.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.M1A2_SEP_II.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.OPLOT.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.LEOPARD_2A7V.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.LEOPARD_2A4.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.VARTA.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.VARTA_PTRK.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.FMTV.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BUSHMASTER.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.AH_64.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.COBRA.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.M142_HIMARS.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.HUMVEE_MK19.get())));
    }).m_257652_());
    public static final RegistryObject<CreativeModeTab> RU_VEHICLE_TAB = TABS.register("ru_tab", () -> CreativeModeTab.builder().m_257941_((Component)Component.m_237115_((String)"rutab.vvp_ruvehicle_tab")).m_257737_(() -> new ItemStack((ItemLike)ModItems.RU_TAB_ICON.get())).m_257501_((parameters, output) -> {
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMP_2.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMP_2M.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMP_2_BAKHCHA.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMP_3.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.T72_B3M.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.T90_M.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.TERMINATOR.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMPT_3K.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.URAL.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.PANTSIR_S1.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.PAUTINA.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.GAZ_TIGR.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.MI_28.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.MI_24.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.MI_8.get())));
    }).m_257652_());
    public static final RegistryObject<CreativeModeTab> ARMOR_TAB = TABS.register("armor_tab", () -> CreativeModeTab.builder().m_257941_((Component)Component.m_237115_((String)"itemGroup.vvp_armor_tab")).m_257737_(() -> new ItemStack((ItemLike)ModItems.ARMOR_ICON.get())).m_257501_((parameters, output) -> {
        output.m_246326_((ItemLike)ModItems.PANAMA.get());
        output.m_246326_((ItemLike)ModItems.KEPKA.get());
        output.m_246326_((ItemLike)ModItems.BERETA.get());
        output.m_246326_((ItemLike)ModItems.MI_28_HELMET.get());
        output.m_246326_((ItemLike)ModItems.MI_28_CHEST.get());
        output.m_246326_((ItemLike)ModItems.AGS_30_ITEM.get());
        output.m_246326_((ItemLike)ModItems.KORNET_ITEM.get());
        output.m_246326_((ItemLike)ModItems.ITEM_40_MM.get());
        output.m_246326_((ItemLike)ModItems.ITEM_30MM.get());
        output.m_246326_((ItemLike)ModItems.ITEM_7_62MM.get());
        output.m_246326_((ItemLike)ModItems.ITEM_12_7MM.get());
        output.m_246326_((ItemLike)ModItems.ITEM_AP_SHELL.get());
        output.m_246326_((ItemLike)ModItems.ITEM_HE_SHELL.get());
        output.m_246326_((ItemLike)ModItems.AGM.get());
        output.m_246326_((ItemLike)ModItems.AAM.get());
        output.m_246326_((ItemLike)ModItems.GMLRS_M31.get());
        output.m_246326_((ItemLike)ModItems.GMLRS_M30A1.get());
        output.m_246326_((ItemLike)ModItems.SPRAY.get());
    }).m_257652_());

    @Mod.EventBusSubscriber(modid="vvp", bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration {
        @SubscribeEvent
        public static void register(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == NATO_VEHICLE_TAB.getKey() || event.getTabKey() == RU_VEHICLE_TAB.getKey()) {
                // empty if block
            }
        }
    }
}

