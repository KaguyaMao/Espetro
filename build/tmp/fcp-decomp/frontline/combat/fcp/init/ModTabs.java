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
package frontline.combat.fcp.init;

import com.atsuishio.superbwarfare.item.container.ContainerBlockItem;
import frontline.combat.fcp.init.ModEntities;
import frontline.combat.fcp.init.ModItems;
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

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create((ResourceKey)Registries.f_279569_, (String)"fcp");
    public static final RegistryObject<CreativeModeTab> TERRORIST_VEHICLE_TAB = TABS.register("terrorist_tab", () -> CreativeModeTab.builder().m_257737_(() -> new ItemStack((ItemLike)ModItems.TERRORIST_TAB_ICON.get())).m_257941_((Component)Component.m_237115_((String)"terroristtab.fc_terrorist_tab")).m_257501_((parameters, output) -> {
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.TOYOTA_HILUX.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.TOYOTA_HILUX_ROCKET_POD.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.TOYOTA_HILUX_BMP.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.TOYOTA_HILUX_SPG9.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.TOYOTA_HILUX_MORTAR.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMP1.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMP1U.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMP1AM.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMP2.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMP1P.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMP2D.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMP2M.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BMP2_NOATGM.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.T72AV.get())));
    }).m_257652_());
    public static final RegistryObject<CreativeModeTab> RUSSIAN_VEHICLE_TAB = TABS.register("russian_tab", () -> CreativeModeTab.builder().m_257737_(() -> new ItemStack((ItemLike)ModItems.RUSSIAN_TAB_ICON.get())).m_257941_((Component)Component.m_237115_((String)"russiantab.fc_russian_tab")).m_257501_((parameters, output) -> {
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.UAZ.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.UAZ_DSHKA.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.URAL.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.URAL_GRAD.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.KAMAZ.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.GAZ_TIGR.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.GAZ_TIGR_RWS.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.GAZ_TIGR_MG.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.GAZ_TIGR_GL.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.NOVATOR.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BTR3E.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BTR4MV1.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BTR82.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BTR80.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BTR80_COPE.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.BTR82_COPE.get())));
    }).m_257652_());
    public static final RegistryObject<CreativeModeTab> AMERICAN_VEHICLE_TAB = TABS.register("american_tab", () -> CreativeModeTab.builder().m_257737_(() -> new ItemStack((ItemLike)ModItems.AMERICAN_TAB_ICON.get())).m_257941_((Component)Component.m_237115_((String)"americantab.fc_american_tab")).m_257501_((parameters, output) -> {
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.STRYKER_MGS.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.STRYKER_M2.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.STRYKER_DRAGOON.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.STRYKER_MK19.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.STRYKER_TOW.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.STRYKER_MORTAR.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.LITTLEBIRD.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.LITTLEBIRD_ARMED.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.LAV25.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.VIPER.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.HUEY.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.HUEY_ROCKETS.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.HUEY_DOOR_GUNNER_M60.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.HUEY_DOOR_GUNNER_M134.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.VENOM.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.MATV.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.MATV_CROW.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.MATV_TOW.get())));
        output.m_246342_(ContainerBlockItem.createInstance((EntityType)((EntityType)ModEntities.AAVP.get())));
    }).m_257652_());

    @Mod.EventBusSubscriber(modid="fcp", bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration {
        @SubscribeEvent
        public static void register(BuildCreativeModeTabContentsEvent event) {
        }
    }
}

