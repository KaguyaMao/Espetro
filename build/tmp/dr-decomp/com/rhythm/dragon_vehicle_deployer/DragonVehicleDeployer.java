/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.MenuScreens
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.BlockEntityType$Builder
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterGuiOverlaysEvent
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.BuildCreativeModeTabContentsEvent
 *  net.minecraftforge.event.server.ServerStartingEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.config.IConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 *  org.slf4j.Logger
 */
package com.rhythm.dragon_vehicle_deployer;

import com.mojang.logging.LogUtils;
import com.redabysslucia.dragonrise_reforge.client.overlay.HJ8Overlay;
import com.rhythm.dragon_vehicle_deployer.Config;
import com.rhythm.dragon_vehicle_deployer.block.VehicleDeployerBlock;
import com.rhythm.dragon_vehicle_deployer.block.entity.VehicleDeployerBlockEntity;
import com.rhythm.dragon_vehicle_deployer.client.screen.DeployerConfigScreen;
import com.rhythm.dragon_vehicle_deployer.menu.ModMenuTypes;
import com.rhythm.dragon_vehicle_deployer.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

public class DragonVehicleDeployer {
    public static final String MODID = "dragonrise_reforge";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"dragonrise_reforge");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"dragonrise_reforge");
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create((ResourceKey)Registries.f_279569_, (String)"dragonrise_reforge");
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCK_ENTITY_TYPES, (String)"dragonrise_reforge");
    public static final RegistryObject<Block> VEHICLE_DEPLOYER_BLOCK = BLOCKS.register("vehicle_deployer", VehicleDeployerBlock::new);
    public static final RegistryObject<Item> VEHICLE_DEPLOYER_BLOCK_ITEM = ITEMS.register("vehicle_deployer", () -> new BlockItem((Block)VEHICLE_DEPLOYER_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<VehicleDeployerBlockEntity>> VEHICLE_DEPLOYER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("vehicle_deployer", () -> BlockEntityType.Builder.m_155273_(VehicleDeployerBlockEntity::new, (Block[])new Block[]{(Block)VEHICLE_DEPLOYER_BLOCK.get()}).m_58966_(null));

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(DragonVehicleDeployer::commonSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, (IConfigSpec)Config.SPEC);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        ModMenuTypes.REGISTRY.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(DragonVehicleDeployer.class);
        modEventBus.addListener(DragonVehicleDeployer::addCreative);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("DRAGON VEHICLE DEPLOYER: COMMON SETUP");
        ModNetwork.register(event);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.f_256788_) {
            event.accept(VEHICLE_DEPLOYER_BLOCK_ITEM);
        }
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("DRAGON VEHICLE DEPLOYER: Server starting");
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelowAll("superbwarfare_hj8", (IGuiOverlay)new HJ8Overlay());
    }

    @Mod.EventBusSubscriber(modid="dragonrise_reforge", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("DRAGON VEHICLE DEPLOYER: CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", (Object)Minecraft.m_91087_().m_91094_().m_92546_());
            event.enqueueWork(() -> MenuScreens.m_96206_((MenuType)((MenuType)ModMenuTypes.DEPLOYER_CONFIG_MENU.get()), DeployerConfigScreen::new));
        }
    }
}

