/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.TickTask
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.util.thread.BlockableEventLoop
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.LogicalSide
 */
package net.minecraftforge.common;

import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.WorldWorkerManager;
import net.minecraftforge.common.loot.LootModifierManager;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.server.command.ConfigCommand;
import net.minecraftforge.server.command.ForgeCommand;

public class ForgeInternalHandler {
    private static LootModifierManager INSTANCE;

    @SubscribeEvent(priority=EventPriority.HIGH)
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity newEntity;
        ItemStack stack;
        Item item;
        Entity entity = event.getEntity();
        if (entity.getClass().equals(ItemEntity.class) && (item = (stack = ((ItemEntity)entity).m_32055_()).m_41720_()).hasCustomEntity(stack) && (newEntity = item.createEntity(event.getLevel(), entity, stack)) != null) {
            entity.m_146870_();
            event.setCanceled(true);
            BlockableEventLoop<? super TickTask> executor = LogicalSidedProvider.WORKQUEUE.get(event.getLevel().f_46443_ ? LogicalSide.CLIENT : LogicalSide.SERVER);
            executor.m_6937_((Runnable)new TickTask(0, () -> event.getLevel().m_7967_(newEntity)));
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onDimensionUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel) {
            FakePlayerFactory.unloadLevel((ServerLevel)event.getLevel());
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        WorldWorkerManager.tick(event.phase == TickEvent.Phase.START);
    }

    @SubscribeEvent
    public void checkSettings(TickEvent.ClientTickEvent event) {
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (!event.getLevel().m_5776_()) {
            FarmlandWaterManager.removeTickets(event.getChunk());
        }
    }

    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        UsernameCache.setUsername(event.getEntity().m_20148_(), event.getEntity().m_36316_().getName());
    }

    @SubscribeEvent
    public void tagsUpdated(TagsUpdatedEvent event) {
        if (event.shouldUpdateStaticData()) {
            ForgeHooks.updateBurns();
        }
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        new ForgeCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onResourceReload(AddReloadListenerEvent event) {
        INSTANCE = new LootModifierManager();
        event.addListener((PreparableReloadListener)INSTANCE);
    }

    static LootModifierManager getLootModifierManager() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Can not retrieve LootModifierManager until resources have loaded once.");
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void resourceReloadListeners(AddReloadListenerEvent event) {
        event.addListener(TierSortingRegistry.getReloadListener());
        event.addListener(CreativeModeTabRegistry.getReloadListener());
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void builtinMobSpawnBlocker(EntityJoinLevelEvent event) {
        Mob mob;
        Entity entity = event.getEntity();
        if (entity instanceof Mob && (mob = (Mob)entity).isSpawnCancelled()) {
            event.setCanceled(true);
        }
    }
}

