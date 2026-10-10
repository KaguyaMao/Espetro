/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.init.ModItems
 *  com.atsuishio.superbwarfare.init.ModKeyMappings
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.event.InputEvent$MouseButton$Pre
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.redabysslucia.dragonrise_reforge.client;

import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModKeyMappings;
import com.redabysslucia.dragonrise_reforge.client.sound.R6DroneLoopSoundInstance;
import com.redabysslucia.dragonrise_reforge.entities.special.R6DroneEntity;
import com.redabysslucia.dragonrise_reforge.network.ModNetwork;
import com.redabysslucia.dragonrise_reforge.network.message.R6DroneControlMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(value=Dist.CLIENT)
@Mod.EventBusSubscriber(modid="dragonrise_reforge", bus=Mod.EventBusSubscriber.Bus.FORGE, value={Dist.CLIENT})
public class R6DroneClientHandler {
    private static short lastKeys = (short)-1;
    private static R6DroneLoopSoundInstance playingLoop;
    private static boolean attackHeld;

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null || mc.f_91073_ == null) {
            return;
        }
        ItemStack stack = mc.f_91074_.m_21205_();
        if (!stack.m_150930_((Item)ModItems.MONITOR.get())) {
            return;
        }
        CompoundTag tag = stack.m_41783_();
        if (tag == null || !tag.m_128471_("Using") || !tag.m_128471_("Linked")) {
            return;
        }
        if (R6DroneEntity.findDrone((Level)mc.f_91073_, tag.m_128461_("LinkedDrone")) == null) {
            return;
        }
        if (event.getButton() == 0) {
            attackHeld = event.getAction() != 0;
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null || mc.f_91073_ == null) {
            R6DroneClientHandler.stopLoop(mc);
            return;
        }
        ItemStack stack = player.m_21205_();
        if (!stack.m_150930_((Item)ModItems.MONITOR.get())) {
            lastKeys = (short)-1;
            R6DroneClientHandler.stopLoop(mc);
            return;
        }
        CompoundTag tag = stack.m_41783_();
        if (tag == null || !tag.m_128471_("Using") || !tag.m_128471_("Linked")) {
            lastKeys = (short)-1;
            R6DroneClientHandler.stopLoop(mc);
            return;
        }
        R6DroneEntity drone = R6DroneEntity.findDrone((Level)mc.f_91073_, tag.m_128461_("LinkedDrone"));
        if (drone == null) {
            lastKeys = (short)-1;
            R6DroneClientHandler.stopLoop(mc);
            return;
        }
        short keys = 0;
        if (ModKeyMappings.MOVE_LEFT.m_90857_()) {
            keys = (short)(keys | 1);
        }
        if (ModKeyMappings.MOVE_RIGHT.m_90857_()) {
            keys = (short)(keys | 2);
        }
        if (ModKeyMappings.MOVE_FORWARD.m_90857_()) {
            keys = (short)(keys | 4);
        }
        if (ModKeyMappings.MOVE_BACKWARD.m_90857_()) {
            keys = (short)(keys | 8);
        }
        if (ModKeyMappings.MOVE_SPACE.m_90857_()) {
            keys = (short)(keys | 0x10);
        }
        if (attackHeld) {
            keys = (short)(keys | 0x20);
        }
        if (ModKeyMappings.MOVE_CTRL.m_90857_()) {
            keys = (short)(keys | 0x100);
        }
        if (keys != lastKeys) {
            lastKeys = keys;
            ModNetwork.PACKET_HANDLER.sendToServer((Object)new R6DroneControlMessage(keys));
        }
        boolean moving = (keys & 0xF) != 0;
        boolean sprinting = (keys & 0x100) != 0;
        R6DroneClientHandler.updateLoop(mc, drone, moving, sprinting);
    }

    private static void updateLoop(Minecraft mc, R6DroneEntity drone, boolean moving, boolean sprinting) {
        SoundManager sm = mc.m_91106_();
        boolean shouldPlay = moving;
        boolean wantFast = sprinting;
        if (!shouldPlay) {
            R6DroneClientHandler.stopLoop(mc);
            return;
        }
        if (wantFast && (playingLoop == null || !playingLoop.isFast())) {
            R6DroneClientHandler.stopLoop(mc);
            playingLoop = new R6DroneLoopSoundInstance(drone, mc, true);
            sm.m_120367_((SoundInstance)playingLoop);
        } else if (!wantFast && (playingLoop == null || playingLoop.isFast())) {
            R6DroneClientHandler.stopLoop(mc);
            playingLoop = new R6DroneLoopSoundInstance(drone, mc, false);
            sm.m_120367_((SoundInstance)playingLoop);
        }
    }

    private static void stopLoop(Minecraft mc) {
        if (playingLoop != null) {
            mc.m_91106_().m_120399_((SoundInstance)playingLoop);
            playingLoop = null;
        }
    }
}

