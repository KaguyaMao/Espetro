/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  org.slf4j.Logger
 */
package com.rhythm.dragon_vehicle_deployer.event;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid="dragonrise_reforge", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerLoginHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static Class<?> vehicleEntityClass = null;
    private static boolean classLookupDone = false;

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        Level level = serverPlayer.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        String playerUuid = serverPlayer.m_20149_();
        if (!classLookupDone) {
            classLookupDone = true;
            try {
                vehicleEntityClass = Class.forName("com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity");
            }
            catch (ClassNotFoundException e) {
                LOGGER.warn("SuperbWarfare VehicleEntity class not found, vehicle cleanup on login disabled");
            }
        }
        if (vehicleEntityClass == null) {
            return;
        }
        ArrayList<Entity> toRemove = new ArrayList<Entity>();
        for (Entity entity : serverLevel.m_8583_()) {
            if (!entity.m_6084_() || !vehicleEntityClass.isInstance(entity)) continue;
            CompoundTag tag = new CompoundTag();
            entity.m_20240_(tag);
            String lastDriver = tag.m_128461_("LastDriver");
            if (!playerUuid.equals(lastDriver)) continue;
            toRemove.add(entity);
        }
        for (Entity entity : toRemove) {
            LOGGER.info("Removing vehicle {} (type={}) owned by player {} on login", new Object[]{entity.m_20149_(), entity.m_6095_().m_204041_().m_205785_().m_135782_(), serverPlayer.m_7755_().getString()});
            entity.m_146870_();
        }
    }
}

