/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  org.lwjgl.glfw.GLFW
 */
package com.redabysslucia.dragonrise_reforge.init;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.BiConsumer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public enum ModKeyMappings {
    ENGINE_CHANGE_MODE_TOGGLE("enginechangekey", 86, "Toggle Engine Change Mode"),
    OPEN_FIRE_CONTROL("openfirecontrolkey", 80, "Open Fire Control Panel");

    private final String description;
    private final String translation;
    private final int key;
    private final boolean modifiable;
    private KeyMapping keybind;

    private ModKeyMappings(int defaultKey) {
        this("", defaultKey, "");
    }

    private ModKeyMappings(String description, int defaultKey, String translation) {
        this.description = "dragonrise_reforge.keyinfo." + description;
        this.key = defaultKey;
        this.modifiable = !description.isEmpty();
        this.translation = translation;
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (ModKeyMappings key : ModKeyMappings.values()) {
            if (!key.modifiable) continue;
            consumer.accept(key.description, key.translation);
        }
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        for (ModKeyMappings key : ModKeyMappings.values()) {
            key.keybind = new KeyMapping(key.description, key.key, "dragonrise_reforge");
            if (!key.modifiable) continue;
            event.register(key.keybind);
        }
    }

    public static boolean isKeyDown(int key) {
        return InputConstants.m_84830_((long)Minecraft.m_91087_().m_91268_().m_85439_(), (int)key);
    }

    public static boolean isMouseButtonDown(int button) {
        return GLFW.glfwGetMouseButton((long)Minecraft.m_91087_().m_91268_().m_85439_(), (int)button) == 1;
    }

    public static boolean ctrlDown() {
        return Screen.m_96637_();
    }

    public static boolean shiftDown() {
        return Screen.m_96638_();
    }

    public static boolean altDown() {
        return Screen.m_96639_();
    }

    public KeyMapping getKeybind() {
        return this.keybind;
    }

    public boolean isClicked() {
        return this.keybind.m_90859_();
    }

    public boolean isPressed() {
        if (!this.modifiable) {
            return ModKeyMappings.isKeyDown(this.key);
        }
        return this.keybind.m_90857_();
    }

    public String getBoundKey() {
        return this.keybind.m_90863_().getString().toUpperCase();
    }

    public int getBoundCode() {
        return this.keybind.getKey().m_84873_();
    }
}

