/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.InputEvent$Key
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.redabysslucia.dragonrise_reforge.events;

import com.redabysslucia.dragonrise_reforge.entities.utils.VariableEngineVehicle;
import com.redabysslucia.dragonrise_reforge.init.ModKeyMappings;
import com.redabysslucia.dragonrise_reforge.network.EngineChangeModeMessage;
import com.redabysslucia.dragonrise_reforge.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE, value={Dist.CLIENT})
public class ClickEvent {
    @SubscribeEvent
    public static void onKeyPressed(InputEvent.Key event) {
        Entity entity;
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null) {
            return;
        }
        if (player.m_5833_()) {
            return;
        }
        int key = event.getKey();
        if (key < 0) {
            return;
        }
        if (event.getAction() == 1 && (entity = player.m_20202_()) instanceof VariableEngineVehicle) {
            VariableEngineVehicle entity2 = (VariableEngineVehicle)entity;
            if (ModKeyMappings.ENGINE_CHANGE_MODE_TOGGLE.isPressed()) {
                ModNetwork.PACKET_HANDLER.sendToServer((Object)new EngineChangeModeMessage());
            }
        }
    }
}

