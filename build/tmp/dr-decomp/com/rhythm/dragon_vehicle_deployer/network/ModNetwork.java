/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package com.rhythm.dragon_vehicle_deployer.network;

import com.rhythm.dragon_vehicle_deployer.network.DeployerSettingsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation("dragonrise_reforge", "main"), () -> "1", "1"::equals, "1"::equals);
    private static int id = 0;

    public static void register(FMLCommonSetupEvent event) {
        CHANNEL.registerMessage(id++, DeployerSettingsPacket.class, DeployerSettingsPacket::encode, DeployerSettingsPacket::decode, DeployerSettingsPacket::handle);
    }
}

