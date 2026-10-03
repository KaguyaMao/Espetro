/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.InputEvent$Key
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.redabysslucia.dragonrise_reforge.client;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.redabysslucia.dragonrise_reforge.client.screen.FiringSolutionScreen;
import com.redabysslucia.dragonrise_reforge.entities.vehicle.IndirectFireVehicleBase;
import com.redabysslucia.dragonrise_reforge.init.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value={Dist.CLIENT}, modid="dragonrise_reforge", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class FireControlKeyHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() != 1) {
            return;
        }
        if (!ModKeyMappings.OPEN_FIRE_CONTROL.isClicked()) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        LocalPlayer player = minecraft.f_91074_;
        if (player == null || minecraft.f_91080_ != null) {
            return;
        }
        Entity entity = player.m_20202_();
        if (!(entity instanceof VehicleEntity)) {
            return;
        }
        VehicleEntity vehicle = (VehicleEntity)entity;
        if (!(vehicle instanceof IndirectFireVehicleBase)) {
            return;
        }
        if (vehicle.getSeatIndex((Entity)player) != vehicle.getTurretControllerIndex()) {
            return;
        }
        minecraft.m_91152_((Screen)new FiringSolutionScreen(vehicle, (Player)player));
    }
}

