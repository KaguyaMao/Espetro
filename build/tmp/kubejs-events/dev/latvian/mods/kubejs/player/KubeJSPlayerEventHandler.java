/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.event.EventResult
 *  dev.architectury.event.events.common.ChatEvent
 *  dev.architectury.event.events.common.ChatEvent$ChatComponent
 *  dev.architectury.event.events.common.PlayerEvent
 *  dev.architectury.event.events.common.TickEvent
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ChestMenu
 *  net.minecraft.world.inventory.ContainerListener
 *  net.minecraft.world.inventory.InventoryMenu
 */
package dev.latvian.mods.kubejs.player;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.bindings.event.PlayerEvents;
import dev.latvian.mods.kubejs.player.ChestEventJS;
import dev.latvian.mods.kubejs.player.InventoryEventJS;
import dev.latvian.mods.kubejs.player.PlayerAdvancementEventJS;
import dev.latvian.mods.kubejs.player.PlayerChatDecorateEventJS;
import dev.latvian.mods.kubejs.player.PlayerChatReceivedEventJS;
import dev.latvian.mods.kubejs.player.PlayerRespawnedEventJS;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.InventoryMenu;

public class KubeJSPlayerEventHandler {
    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(KubeJSPlayerEventHandler::loggedIn);
        PlayerEvent.PLAYER_QUIT.register(KubeJSPlayerEventHandler::loggedOut);
        TickEvent.PLAYER_POST.register(KubeJSPlayerEventHandler::tick);
        ChatEvent.DECORATE.register(KubeJSPlayerEventHandler::chatDecorate);
        ChatEvent.RECEIVED.register(KubeJSPlayerEventHandler::chatReceived);
        PlayerEvent.PLAYER_ADVANCEMENT.register(KubeJSPlayerEventHandler::advancement);
        PlayerEvent.OPEN_MENU.register(KubeJSPlayerEventHandler::inventoryOpened);
        PlayerEvent.CLOSE_MENU.register(KubeJSPlayerEventHandler::inventoryClosed);
    }

    public static void loggedIn(ServerPlayer player) {
        PlayerEvents.LOGGED_IN.post(ScriptType.SERVER, new SimplePlayerEventJS((Player)player));
        player.f_36095_.m_38893_((ContainerListener)player.kjs$getInventoryChangeListener());
        if (!ConsoleJS.SERVER.errors.isEmpty() && !CommonProperties.get().hideServerScriptErrors) {
            player.m_5661_(ConsoleJS.SERVER.errorsComponent("/kubejs errors server"), false);
        }
        player.kjs$getStages().sync();
    }

    public static void respawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean keepData) {
        newPlayer.kjs$setRawPersistentData(oldPlayer.kjs$getRawPersistentData());
        newPlayer.f_36095_.m_38893_((ContainerListener)newPlayer.kjs$getInventoryChangeListener());
        PlayerEvents.RESPAWNED.post(ScriptType.SERVER, new PlayerRespawnedEventJS(newPlayer, oldPlayer, keepData));
        newPlayer.kjs$getStages().sync();
    }

    public static void loggedOut(ServerPlayer player) {
        PlayerEvents.LOGGED_OUT.post(ScriptType.SERVER, new SimplePlayerEventJS((Player)player));
    }

    public static void tick(Player player) {
        if (PlayerEvents.TICK.hasListeners()) {
            PlayerEvents.TICK.post((ScriptTypeHolder)player, new SimplePlayerEventJS(player));
        }
    }

    public static void chatDecorate(ServerPlayer player, ChatEvent.ChatComponent component) {
        PlayerEvents.DECORATE_CHAT.post(ScriptType.SERVER, new PlayerChatDecorateEventJS(player, component));
    }

    public static EventResult chatReceived(ServerPlayer player, Component component) {
        return PlayerEvents.CHAT.hasListeners() ? PlayerEvents.CHAT.post(ScriptType.SERVER, new PlayerChatReceivedEventJS(player, component)).arch() : EventResult.pass();
    }

    public static void advancement(ServerPlayer player, Advancement advancement) {
        if (PlayerEvents.ADVANCEMENT.hasListeners()) {
            PlayerEvents.ADVANCEMENT.post(new PlayerAdvancementEventJS(player, advancement), advancement.m_138327_());
        }
    }

    public static void inventoryOpened(Player player, AbstractContainerMenu menu) {
        if (!(menu instanceof InventoryMenu)) {
            menu.m_38893_((ContainerListener)player.kjs$getInventoryChangeListener());
        }
        if (PlayerEvents.INVENTORY_OPENED.hasListeners()) {
            PlayerEvents.INVENTORY_OPENED.post((ScriptTypeHolder)player, (Object)menu, new InventoryEventJS(player, menu));
        }
        if (menu instanceof ChestMenu && PlayerEvents.CHEST_OPENED.hasListeners()) {
            PlayerEvents.CHEST_OPENED.post((ScriptTypeHolder)player, (Object)menu, new ChestEventJS(player, menu));
        }
    }

    public static void inventoryClosed(Player player, AbstractContainerMenu menu) {
        if (PlayerEvents.INVENTORY_CLOSED.hasListeners()) {
            PlayerEvents.INVENTORY_CLOSED.post((ScriptTypeHolder)player, (Object)menu, new InventoryEventJS(player, menu));
        }
        if (menu instanceof ChestMenu && PlayerEvents.CHEST_CLOSED.hasListeners()) {
            PlayerEvents.CHEST_CLOSED.post((ScriptTypeHolder)player, (Object)menu, new ChestEventJS(player, menu));
        }
    }
}

