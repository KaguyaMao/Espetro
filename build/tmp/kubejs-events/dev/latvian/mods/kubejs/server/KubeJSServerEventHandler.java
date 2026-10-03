/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  dev.architectury.event.EventResult
 *  dev.architectury.event.events.common.CommandPerformEvent
 *  dev.architectury.event.events.common.CommandRegistrationEvent
 *  dev.architectury.event.events.common.LifecycleEvent
 *  net.minecraft.Util
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands$CommandSelection
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.storage.LevelResource
 */
package dev.latvian.mods.kubejs.server;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.CommandPerformEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.latvian.mods.kubejs.bindings.event.LevelEvents;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.command.CommandRegistryEventJS;
import dev.latvian.mods.kubejs.command.KubeJSCommands;
import dev.latvian.mods.kubejs.level.SimpleLevelEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.CommandEventJS;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

public class KubeJSServerEventHandler {
    private static final LevelResource PERSISTENT_DATA = new LevelResource("kubejs_persistent_data.nbt");

    public static void init() {
        LifecycleEvent.SERVER_BEFORE_START.register(KubeJSServerEventHandler::serverBeforeStart);
        CommandRegistrationEvent.EVENT.register(KubeJSServerEventHandler::registerCommands);
        LifecycleEvent.SERVER_STARTING.register(KubeJSServerEventHandler::serverStarting);
        LifecycleEvent.SERVER_STOPPING.register(KubeJSServerEventHandler::serverStopping);
        LifecycleEvent.SERVER_STOPPED.register(KubeJSServerEventHandler::serverStopped);
        LifecycleEvent.SERVER_LEVEL_SAVE.register(KubeJSServerEventHandler::serverLevelSaved);
        LifecycleEvent.SERVER_LEVEL_LOAD.register(KubeJSServerEventHandler::serverLevelLoaded);
        CommandPerformEvent.EVENT.register(KubeJSServerEventHandler::command);
    }

    public static void serverBeforeStart(MinecraftServer server) {
        UtilsJS.staticServer = server;
        UtilsJS.staticRegistryAccess = server.m_206579_();
        Path p = server.m_129843_(PERSISTENT_DATA);
        if (Files.exists(p, new LinkOption[0])) {
            try {
                CompoundTag tag = NbtIo.m_128937_((File)p.toFile());
                if (tag != null) {
                    CompoundTag t = tag.m_128469_("__restore_inventories");
                    if (!t.m_128456_()) {
                        tag.m_128473_("__restore_inventories");
                        Map playerMap = server.kjs$restoreInventories();
                        for (String key : t.m_128431_()) {
                            ListTag list = t.m_128437_(key, 10);
                            Map map = playerMap.computeIfAbsent(UUID.fromString(key), k -> new HashMap());
                            for (Tag tag2 : list) {
                                short slot = ((CompoundTag)tag2).m_128448_("Slot");
                                ItemStack stack = ItemStack.m_41712_((CompoundTag)((CompoundTag)tag2));
                                map.put(Integer.valueOf(slot), stack);
                            }
                        }
                    }
                    server.kjs$getPersistentData().m_128391_(tag);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {
        KubeJSCommands.register(dispatcher);
        if (ServerEvents.COMMAND_REGISTRY.hasListeners()) {
            ServerEvents.COMMAND_REGISTRY.post(ScriptType.SERVER, new CommandRegistryEventJS(dispatcher, registry, selection));
        }
    }

    private static void serverStarting(MinecraftServer server) {
        ServerEvents.LOADED.post(ScriptType.SERVER, new ServerEventJS(server));
    }

    private static void serverStopping(MinecraftServer server) {
        ServerEvents.UNLOADED.post(ScriptType.SERVER, new ServerEventJS(server));
    }

    private static void serverStopped(MinecraftServer server) {
        UtilsJS.staticServer = null;
        UtilsJS.staticRegistryAccess = RegistryAccess.f_243945_;
    }

    private static void serverLevelLoaded(ServerLevel level) {
        if (LevelEvents.LOADED.hasListeners()) {
            LevelEvents.LOADED.post(new SimpleLevelEventJS((Level)level), level.m_46472_().m_135782_());
        }
    }

    private static void serverLevelSaved(ServerLevel level) {
        if (level.m_46472_() == Level.f_46428_) {
            CompoundTag serverData = level.m_7654_().kjs$getPersistentData().m_6426_();
            Path p = level.m_7654_().m_129843_(PERSISTENT_DATA);
            Map playerMap = level.m_7654_().kjs$restoreInventories();
            if (!playerMap.isEmpty()) {
                CompoundTag nbt = new CompoundTag();
                for (Map.Entry entry : playerMap.entrySet()) {
                    ListTag list = new ListTag();
                    for (Map.Entry entry2 : ((Map)entry.getValue()).entrySet()) {
                        CompoundTag tag = new CompoundTag();
                        tag.m_128376_("Slot", ((Integer)entry2.getKey()).shortValue());
                        ((ItemStack)entry2.getValue()).m_41739_(tag);
                        list.add((Object)tag);
                    }
                    nbt.m_128365_(((UUID)entry.getKey()).toString(), (Tag)list);
                }
                serverData.m_128365_("__restore_inventories", (Tag)nbt);
            }
            Util.m_183992_().execute(() -> {
                try {
                    NbtIo.m_128944_((CompoundTag)serverData, (File)p.toFile());
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    public static EventResult command(CommandPerformEvent event) {
        if (ServerEvents.COMMAND.hasListeners()) {
            CommandEventJS e = new CommandEventJS(event);
            return ServerEvents.COMMAND.post(e, e.getCommandName()).arch();
        }
        return EventResult.pass();
    }
}

