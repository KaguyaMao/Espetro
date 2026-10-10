/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.multiplayer.ServerData
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.core.MinecraftEnvironmentKJS;
import dev.latvian.mods.kubejs.item.ItemClickedEventJS;
import dev.latvian.mods.kubejs.net.FirstClickMessage;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface MinecraftClientKJS
extends MinecraftEnvironmentKJS {
    default public Minecraft kjs$self() {
        return (Minecraft)this;
    }

    @Override
    default public Component kjs$getName() {
        return Component.m_237113_((String)this.kjs$self().m_7326_());
    }

    @Override
    default public void kjs$tell(Component message) {
        this.kjs$self().f_91074_.kjs$tell(message);
    }

    @Override
    default public void kjs$setStatusMessage(Component message) {
        this.kjs$self().f_91074_.kjs$setStatusMessage(message);
    }

    @Override
    default public int kjs$runCommand(String command) {
        this.kjs$self().f_91074_.f_108617_.m_246623_(command);
        return 0;
    }

    @Override
    default public int kjs$runCommandSilent(String command) {
        this.kjs$self().f_91074_.f_108617_.m_246623_(command);
        return 0;
    }

    @Nullable
    default public Screen kjs$getCurrentScreen() {
        return this.kjs$self().f_91080_;
    }

    default public void kjs$setCurrentScreen(Screen gui) {
        this.kjs$self().m_91152_(gui);
    }

    default public void kjs$setTitle(String t) {
        ClientProperties.get().title = t.trim();
        this.kjs$self().m_91341_();
    }

    default public String kjs$getCurrentWorldName() {
        ServerData server = this.kjs$self().m_91089_();
        return server == null ? "Singleplayer" : server.f_105362_;
    }

    default public boolean kjs$isKeyDown(int key) {
        return InputConstants.m_84830_((long)this.kjs$self().m_91268_().m_85439_(), (int)key);
    }

    default public boolean kjs$isShiftDown() {
        return Screen.m_96638_();
    }

    default public boolean kjs$isCtrlDown() {
        return Screen.m_96637_();
    }

    default public boolean kjs$isAltDown() {
        return Screen.m_96639_();
    }

    @HideFromJS
    default public void kjs$startAttack0() {
        if (ItemEvents.FIRST_LEFT_CLICKED.hasListeners()) {
            LocalPlayer player = this.kjs$self().f_91074_;
            ItemStack stack = player.m_21120_(InteractionHand.MAIN_HAND);
            ItemEvents.FIRST_LEFT_CLICKED.post((ScriptTypeHolder)ScriptType.CLIENT, (Object)stack.m_41720_(), new ItemClickedEventJS((Player)player, InteractionHand.MAIN_HAND, stack));
        }
        new FirstClickMessage(0).sendToServer();
    }

    @HideFromJS
    default public void kjs$startUseItem0() {
        if (ItemEvents.FIRST_RIGHT_CLICKED.hasListeners()) {
            LocalPlayer player = this.kjs$self().f_91074_;
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = player.m_21120_(hand);
                ItemEvents.FIRST_RIGHT_CLICKED.post((ScriptTypeHolder)ScriptType.CLIENT, (Object)stack.m_41720_(), new ItemClickedEventJS((Player)player, hand, stack));
            }
        }
        new FirstClickMessage(1).sendToServer();
    }

    @HideFromJS
    default public void kjs$afterResourcesLoaded(boolean reload) {
        ConsoleJS.CLIENT.setCapturingErrors(false);
        ConsoleJS.CLIENT.info("Client resource reload complete!");
    }
}

