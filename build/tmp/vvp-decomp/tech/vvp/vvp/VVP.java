/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package tech.vvp.vvp;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import tech.vvp.vvp.init.ModEntities;
import tech.vvp.vvp.init.ModItems;
import tech.vvp.vvp.init.ModParticleTypes;
import tech.vvp.vvp.init.ModSounds;
import tech.vvp.vvp.init.ModTabs;
import tech.vvp.vvp.network.VVPNetwork;

@Mod(value="vvp")
public class VVP {
    public static final String MOD_ID = "vvp";
    public static final SimpleChannel PACKET_HANDLER = VVPNetwork.VVP_HANDLER;

    public VVP() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
        ModSounds.REGISTRY.register(modEventBus);
        ModTabs.TABS.register(modEventBus);
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    private void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(VVPNetwork::register);
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}

