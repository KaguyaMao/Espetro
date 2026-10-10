/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.slf4j.Logger
 */
package com.redabysslucia.dragonrise_reforge;

import com.mojang.logging.LogUtils;
import com.redabysslucia.dragonrise_reforge.client.outline.render.OutlineRenderer;
import com.redabysslucia.dragonrise_reforge.init.ModEntities;
import com.redabysslucia.dragonrise_reforge.init.ModItems;
import com.redabysslucia.dragonrise_reforge.init.ModSounds;
import com.redabysslucia.dragonrise_reforge.init.ModTabs;
import com.redabysslucia.dragonrise_reforge.network.ModNetwork;
import com.rhythm.dragon_vehicle_deployer.DragonVehicleDeployer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod(value="dragonrise_reforge")
public class Dragonrise_reforge {
    public static final String MODID = "dragonrise_reforge";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Dragonrise_reforge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(bus);
        ModEntities.REGISTRY.register(bus);
        ModTabs.TABS.register(bus);
        ModSounds.REGISTRY.register(bus);
        DragonVehicleDeployer.register(bus);
        bus.addListener(this::commonSetup);
        bus.addListener(this::setupClient);
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", (Object)ForgeRegistries.BLOCKS.getKey((Object)Blocks.f_50493_));
        ModNetwork.register();
    }

    public void dragonrise_reforge() {
        MinecraftForge.EVENT_BUS.register((Object)this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    }

    private void setupClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                OutlineRenderer.init();
                OutlineRenderer.register();
                LOGGER.info("Outline rendering system initialized");
            }
            catch (Exception e) {
                LOGGER.error("Failed to initialize outline rendering system", (Throwable)e);
            }
        });
    }
}

