/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.ChatFormatting
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.MinecraftServer$ReloadableResources
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.core.DataSenderKJS;
import dev.latvian.mods.kubejs.core.MinecraftEnvironmentKJS;
import dev.latvian.mods.kubejs.core.NoMixinException;
import dev.latvian.mods.kubejs.core.PlayerSelector;
import dev.latvian.mods.kubejs.core.WithAttachedData;
import dev.latvian.mods.kubejs.core.WithPersistentData;
import dev.latvian.mods.kubejs.net.SendDataFromServerMessage;
import dev.latvian.mods.kubejs.player.AdvancementJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface MinecraftServerKJS
extends WithAttachedData<MinecraftServer>,
WithPersistentData,
DataSenderKJS,
MinecraftEnvironmentKJS {
    default public MinecraftServer kjs$self() {
        return (MinecraftServer)this;
    }

    public MinecraftServer.ReloadableResources kjs$getReloadableResources();

    public ServerLevel kjs$getOverworld();

    @Override
    default public Component kjs$getName() {
        return Component.m_237113_((String)this.kjs$self().m_7326_());
    }

    @Override
    default public void kjs$tell(Component message) {
        this.kjs$self().m_213846_(message);
        for (ServerPlayer player : this.kjs$self().m_6846_().m_11314_()) {
            player.kjs$tell(message);
        }
    }

    @Override
    default public void kjs$setStatusMessage(Component message) {
        for (ServerPlayer player : this.kjs$self().m_6846_().m_11314_()) {
            player.kjs$setStatusMessage(message);
        }
    }

    @Override
    default public int kjs$runCommand(String command) {
        return this.kjs$self().m_129892_().m_230957_(this.kjs$self().m_129893_(), command);
    }

    @Override
    default public int kjs$runCommandSilent(String command) {
        return this.kjs$self().m_129892_().m_230957_(this.kjs$self().m_129893_().m_81324_(), command);
    }

    default public ServerLevel kjs$getLevel(ResourceLocation dimension) {
        return this.kjs$self().m_129880_(ResourceKey.m_135785_((ResourceKey)Registries.f_256858_, (ResourceLocation)dimension));
    }

    @Nullable
    default public ServerPlayer kjs$getPlayer(PlayerSelector selector) {
        return selector.getPlayer(this.kjs$self());
    }

    default public EntityArrayList kjs$getPlayers() {
        return new EntityArrayList((Level)this.kjs$self().m_129783_(), this.kjs$self().m_6846_().m_11314_());
    }

    default public EntityArrayList kjs$getEntities() {
        EntityArrayList list = new EntityArrayList((Level)this.kjs$self().m_129783_(), 10);
        for (ServerLevel level : this.kjs$self().m_129785_()) {
            list.addAllIterable(level.m_8583_());
        }
        return list;
    }

    @Nullable
    default public AdvancementJS kjs$getAdvancement(ResourceLocation id) {
        Advancement a = this.kjs$self().m_129889_().m_136041_(id);
        return a == null ? null : new AdvancementJS(a);
    }

    @Override
    default public void kjs$sendData(String channel, @Nullable CompoundTag data) {
        new SendDataFromServerMessage(channel, data).sendToAll(this.kjs$self());
    }

    @HideFromJS
    default public void kjs$afterResourcesLoaded(boolean reload) {
        KubeJSReloadListener.postAfterRecipes();
        if (reload) {
            DataExport.exportData();
        }
        if (reload && CommonProperties.get().announceReload && !CommonProperties.get().hideServerScriptErrors) {
            if (ConsoleJS.SERVER.errors.isEmpty()) {
                this.kjs$tell((Component)Component.m_237113_((String)"Reloaded with no KubeJS errors!").m_130940_(ChatFormatting.GREEN));
            } else {
                this.kjs$tell(ConsoleJS.SERVER.errorsComponent("/kubejs errors server"));
            }
        }
        ConsoleJS.SERVER.setCapturingErrors(false);
        ConsoleJS.SERVER.info("Server resource reload complete!");
    }

    default public Map<UUID, Map<Integer, ItemStack>> kjs$restoreInventories() {
        throw new NoMixinException();
    }
}

