/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.food.FoodProperties$Builder
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.material.MapColor
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.BuildCreativeModeTabContentsEvent
 *  net.minecraftforge.event.server.ServerStartingEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.config.IConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 *  org.slf4j.Logger
 */
package LOL_141.vehicle_addition;

import LOL_141.vehicle_addition.Config;
import LOL_141.vehicle_addition.client.audio.SuperbwarfateSoundEvents;
import LOL_141.vehicle_addition.compat.SeatStabilizerData;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(value="vehicle_addition")
public class Vehicle_addition {
    public static final String MODID = "vehicle_addition";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"vehicle_addition");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"vehicle_addition");
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create((ResourceKey)Registries.f_279569_, (String)"vehicle_addition");
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.m_284310_().m_284180_(MapColor.f_283947_)));
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem((Block)EXAMPLE_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().m_41489_(new FoodProperties.Builder().m_38765_().m_38760_(1).m_38758_(2.0f).m_38767_())));
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder().withTabsBefore(new ResourceKey[]{CreativeModeTabs.f_256797_}).m_257737_(() -> ((Item)EXAMPLE_ITEM.get()).m_7968_()).m_257501_((parameters, output) -> output.m_246326_((ItemLike)EXAMPLE_ITEM.get())).m_257652_());

    public Vehicle_addition() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        SuperbwarfateSoundEvents.SOUND_EVENTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register((Object)this);
        if (ModList.get().isLoaded("superbwarfare")) {
            MinecraftForge.EVENT_BUS.register(SeatStabilizerData.class);
        }
        modEventBus.addListener(this::addCreative);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, (IConfigSpec)Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", (Object)ForgeRegistries.BLOCKS.getKey((Object)Blocks.f_50493_));
        if (Config.logDirtBlock) {
            LOGGER.info("DIRT BLOCK >> {}", (Object)ForgeRegistries.BLOCKS.getKey((Object)Blocks.f_50493_));
        }
        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
        Config.items.forEach(item -> LOGGER.info("ITEM >> {}", (Object)item.toString()));
        Vehicle_addition.logAsmCoremodState();
    }

    private static void logAsmCoremodState() {
        try {
            Class<?> seat = Class.forName("com.atsuishio.superbwarfare.data.vehicle.subdata.SeatInfo");
            try {
                seat.getField("stabilizer");
                LOGGER.info("[VehicleTerrain] ASM coremod ACTIVE (SeatInfo.stabilizer injected)");
            }
            catch (NoSuchFieldException e) {
                LOGGER.warn("[VehicleTerrain] ASM coremod NOT active: SeatInfo.stabilizer missing (dev \u73af\u5883 services \u4e0d\u53d1\u73b0\uff1b\u751f\u4ea7\u73af\u5883 jar \u653e mods \u6587\u4ef6\u5939\u624d\u751f\u6548)");
            }
        }
        catch (Throwable t) {
            LOGGER.info("[VehicleTerrain] SBW SeatInfo not available: {}", (Object)t.toString());
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.f_256788_) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid="vehicle_addition", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", (Object)Minecraft.m_91087_().m_91094_().m_92546_());
        }
    }
}

