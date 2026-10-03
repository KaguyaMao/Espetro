/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.network.PacketDistributor
 *  org.slf4j.Logger
 */
package com.redabysslucia.dragonrise_reforge.events;

import com.redabysslucia.dragonrise_reforge.Dragonrise_reforge;
import com.redabysslucia.dragonrise_reforge.entities.projectile.GuidedBombEntity;
import com.redabysslucia.dragonrise_reforge.network.ModNetwork;
import com.redabysslucia.dragonrise_reforge.network.message.OwnBombMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvent {
    private static final Logger LOGGER = Dragonrise_reforge.LOGGER;

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().m_5776_()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof GuidedBombEntity)) {
            return;
        }
        GuidedBombEntity bomb = (GuidedBombEntity)entity;
        Entity entity2 = bomb.m_19749_();
        if (entity2 instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)entity2;
            LOGGER.info("[BombHud] server: notify player {} bomb {}", (Object)sp.m_36316_().getName(), (Object)bomb.m_20148_());
            ModNetwork.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> sp), (Object)new OwnBombMessage(bomb.m_20148_()));
        }
    }
}

