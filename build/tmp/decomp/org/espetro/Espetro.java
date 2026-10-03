/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.event.AddReloadListenerEvent
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.item.ItemTossEvent
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangeGameModeEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerRespawnEvent
 *  net.minecraftforge.event.server.ServerAboutToStartEvent
 *  net.minecraftforge.event.server.ServerStartingEvent
 *  net.minecraftforge.event.server.ServerStoppedEvent
 *  net.minecraftforge.event.server.ServerStoppingEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.config.IConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.server.ServerLifecycleHooks
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.espetro;

import com.mojang.brigadier.CommandDispatcher;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.espetro.bastion.BastionCommand;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.FobSupplyTracker;
import org.espetro.bastion.FortificationConfig;
import org.espetro.bastion.FortificationManager;
import org.espetro.command.EspetroCommand;
import org.espetro.command.OutpostCommand;
import org.espetro.dimension.BattlefieldWorldManager;
import org.espetro.governance.CommanderGovernanceManager;
import org.espetro.kubejs.EspetroKubeJSDefaultScripts;
import org.espetro.logistics.SupplyManager;
import org.espetro.logistics.SupplyType;
import org.espetro.logistics.resupply.ResupplySessionManager;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.mapconfig.ExternalConfigBootstrap;
import org.espetro.network.FortificationCatalogPacket;
import org.espetro.network.NetworkManager;
import org.espetro.network.VehicleSupplyActionPacket;
import org.espetro.ping.VehicleSeatPingCache;
import org.espetro.runtime.ServerRuntimeMaintenance;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.ClassEquipment;
import org.espetro.team.CommanderSkillManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.MapVoteManager;
import org.espetro.team.OutpostManager;
import org.espetro.team.PartyManager;
import org.espetro.team.SquadManager;
import org.espetro.team.TeamManager;
import org.espetro.team.TeamPackManager;
import org.espetro.team.TroopCountManager;
import org.espetro.tutorial.TutorialManager;
import org.espetro.vehicle.VehCommand;
import org.espetro.vehicle.VehicleCommand;
import org.espetro.vehicle.VehicleInteractionConfig;
import org.espetro.vehicle.VehicleManager;
import org.espetro.vehicle.VehicleSeatAccessPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value="espetro")
public class Espetro {
    public static final String MOD_ID = "espetro";
    public static final Logger LOGGER = LoggerFactory.getLogger(Espetro.class);
    private static MinecraftServer serverInstance;
    public static Object KEY_TEAM;
    public static Object KEY_CLASS;
    public static Object KEY_SKILL;
    public static Object KEY_RADIAL;

    public Espetro() {
        Espetro.ensureKubeJSDefaultScriptsIfLoaded();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, (IConfigSpec)VehicleInteractionConfig.SPEC);
        DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> {
            try {
                Class.forName("org.espetro.EspetroClient").getMethod("init", new Class[0]).invoke(null, new Object[0]);
            }
            catch (Exception e) {
                LOGGER.error("Failed to initialize EspetroClient", (Throwable)e);
            }
        });
    }

    public static MinecraftServer getServer() {
        return serverInstance;
    }

    private static void ensureKubeJSDefaultScriptsIfLoaded() {
        if (ModList.get().isLoaded("kubejs")) {
            EspetroKubeJSDefaultScripts.ensureDefaultScripts();
        }
    }

    public static void reloadAllConfigs() {
        LOGGER.warn("Espetro \u5916\u90e8\u5730\u56fe\u3001\u7ef4\u5ea6\u3001\u7f16\u5236\u53ca\u6bcf\u5730\u56fe EsConfig \u4ec5\u5728\u542f\u52a8\u65f6\u52a0\u8f7d\uff1b\u672c\u6b21 reload \u672a\u91cd\u65b0\u8bfb\u53d6\u8fd9\u4e9b\u6587\u4ef6\uff0c\u8bf7\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u7aef\u3002");
        NetworkManager.syncSquadsToTeam("ATTACK");
        NetworkManager.syncSquadsToTeam("DEFEND");
    }

    private static void loadStartupConfigs(MinecraftServer server) {
        ExternalConfigBootstrap.bootstrapIfNeeded();
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        loader.loadExternalFrozen(ExternalConfigBootstrap.getFactionFiles());
        LOGGER.info("Espetro \u542f\u52a8\u914d\u7f6e\u5df2\u51bb\u7ed3\uff1a{} \u4e2a\u53ef\u7528\u5730\u56fe\uff0c{} \u4e2a EsFactions \u7f16\u5236\uff08\u5730\u56fe EsConfig \u5c06\u5728\u6218\u573a\u6fc0\u6d3b\u65f6\u5e94\u7528\uff09", (Object)ExternalConfigBootstrap.getUsableMaps().size(), (Object)loader.getFactionArray().length);
    }

    public static void broadcastToAll(String message) {
        MinecraftServer server = Espetro.getServer();
        if (server != null) {
            server.m_6846_().m_240416_(Component.m_237113_(message), false);
        }
    }

    public static void broadcastToTeam(String team, String message) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        ClassCountManager countManager = ClassCountManager.getInstance();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String playerTeam = Espetro.getPlayerTeam(player);
            if (!team.equals(playerTeam)) continue;
            player.m_213846_(Component.m_237113_(message));
        }
    }

    public static void sendToPlayer(ServerPlayer player, String message) {
        if (player != null) {
            player.m_213846_(Component.m_237113_(message));
        }
    }

    public static String getPlayerTeam(ServerPlayer player) {
        ClassCountManager countManager = ClassCountManager.getInstance();
        String storedTeam = countManager.getPlayerTeam(player.m_20148_());
        if (storedTeam != null) {
            return storedTeam;
        }
        String factionId = countManager.getPlayerFaction(player.m_20148_());
        if (factionId != null) {
            return GameStateManager.getTeamFromFactionStatic(factionId);
        }
        return null;
    }

    public static void broadcastClassSelection(String team, String classId, String message) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String playerTeam = Espetro.getPlayerTeam(player);
            if (!team.equals(playerTeam)) continue;
            player.m_213846_(Component.m_237113_(message));
        }
    }

    @Mod.EventBusSubscriber(modid="espetro")
    public static class ServerCommandHandler {
        private static final int FIXED_FOOD_LEVEL = 20;
        private static final float FIXED_SATURATION_LEVEL = 20.0f;

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            EspetroCommand.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
            BastionCommand.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
            VehicleCommand.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
            VehCommand.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
            event.getDispatcher().register(OutpostCommand.register());
        }

        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            Player player;
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                TeamManager.initTeams(server);
            }
            if ((player = event.getEntity()) instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                GameStateManager.getInstance().forcePlayerToHub(serverPlayer);
                ServerCommandHandler.clearPlayerInventory(serverPlayer);
                GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
                GameStateManager.getInstance().onPlayerJoin(serverPlayer);
                NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> serverPlayer), (Object)FortificationCatalogPacket.forPlayer(serverPlayer));
                if (phase == GamePhase.BATTLE || phase == GamePhase.DEPLOYING) {
                    CommanderGovernanceManager.getInstance().tryRestoreCommanderOnRejoin(serverPlayer);
                }
                LOGGER.info("\u73a9\u5bb6 {} \u5728{}\u9636\u6bb5\u52a0\u5165", (Object)serverPlayer.m_7755_().getString(), (Object)phase.getDisplayName());
            }
        }

        @SubscribeEvent
        public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
            Player player;
            Player player2 = event.getEntity();
            if (player2 instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player2;
                GameStateManager.getInstance().forcePlayerToHub(serverPlayer);
                VehicleSupplyActionPacket.clearPlayerRateLimit(serverPlayer.m_20148_());
                FortificationManager.getInstance().clearPlayer(serverPlayer.m_20148_());
                ResupplySessionManager.clearPlayer(serverPlayer.m_20148_());
                try {
                    TutorialManager.getInstance().onPlayerLeave(serverPlayer.m_20148_());
                }
                catch (Throwable t) {
                    LOGGER.error("\u6e05\u7406\u73a9\u5bb6\u6559\u7a0b\u4f1a\u8bdd\u5931\u8d25: {}", (Object)serverPlayer.m_20148_(), (Object)t);
                }
                ServerCommandHandler.clearPlayerInventory(serverPlayer);
            } else {
                ClassEquipment.clearEquipment(event.getEntity());
            }
            ClassCountManager countManager = ClassCountManager.getInstance();
            String classCountTeam = countManager.getEffectivePlayerTeam(event.getEntity().m_20148_());
            String classCountFaction = countManager.getPlayerFaction(event.getEntity().m_20148_());
            boolean wasLeader = SquadManager.getInstance().isSquadLeader(event.getEntity().m_20148_());
            if (GameStateManager.getInstance().getCurrentPhase() == GamePhase.BATTLE) {
                CommanderGovernanceManager.getInstance().onPlayerLeft(classCountTeam, event.getEntity().m_20148_());
            }
            if (wasLeader) {
                CommanderGovernanceManager.getInstance().onSquadLeaderLost(event.getEntity().m_20148_());
            }
            String squadTeam = SquadManager.getInstance().removePlayer(event.getEntity().m_20148_());
            countManager.removePlayer(event.getEntity());
            VehicleSeatPingCache.clear(event.getEntity().m_20148_());
            VehicleSeatAccessPolicy.clear(event.getEntity().m_20148_());
            NetworkManager.broadcastClassCounts(classCountTeam, classCountFaction);
            if (squadTeam != null) {
                TeamPackManager.getInstance().reconcileTeam(squadTeam);
                NetworkManager.syncSquadsToTeam(squadTeam);
            }
            if ((player = event.getEntity()) instanceof ServerPlayer) {
                ServerPlayer leavePlayer = (ServerPlayer)player;
                GameStateManager.getInstance().onPlayerLeave(leavePlayer);
            } else {
                GameStateManager.getInstance().onPlayerLeave(event.getEntity().m_20148_());
            }
        }

        @SubscribeEvent
        public static void onItemToss(ItemTossEvent event) {
            ServerPlayer player;
            Player player2 = event.getPlayer();
            if (player2 instanceof ServerPlayer && !(player = (ServerPlayer)player2).m_20310_(2)) {
                event.setCanceled(true);
                if (ClassEquipment.isEquipmentMutation(player)) {
                    return;
                }
                ServerCommandHandler.returnTossedItem(player, event.getEntity().m_32055_());
                player.m_213846_(Component.m_237113_("\u00a7c\u975e\u7ba1\u7406\u5458\u65e0\u6cd5\u4e22\u5f03\u7269\u54c1\uff01"));
            }
        }

        @SubscribeEvent
        public static void onLivingDrops(LivingDropsEvent event) {
            ServerPlayer player;
            LivingEntity livingEntity = event.getEntity();
            if (livingEntity instanceof ServerPlayer && !(player = (ServerPlayer)livingEntity).m_20310_(2)) {
                event.getDrops().clear();
            }
        }

        @SubscribeEvent
        public static void onHubPlayerHurt(LivingHurtEvent event) {
            ServerPlayer player;
            LivingEntity livingEntity = event.getEntity();
            if (livingEntity instanceof ServerPlayer && Level.f_46428_.equals((player = (ServerPlayer)livingEntity).m_284548_().m_46472_())) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
            ServerPlayer player;
            if (event.getLevel().m_5776_() || event.loadedFromDisk()) {
                return;
            }
            Entity entity = event.getEntity();
            if (!(entity instanceof ItemEntity)) {
                return;
            }
            ItemEntity itemEntity = (ItemEntity)entity;
            Entity owner = itemEntity.m_19749_();
            if (owner instanceof ServerPlayer && !(player = (ServerPlayer)owner).m_20310_(2)) {
                event.setCanceled(true);
                if (ClassEquipment.isEquipmentMutation(player)) {
                    return;
                }
                ServerCommandHandler.returnTossedItem(player, itemEntity.m_32055_());
                player.m_213846_(Component.m_237113_("\u00a7c\u975e\u7ba1\u7406\u5458\u65e0\u6cd5\u4e22\u5f03\u7269\u54c1\uff01"));
            }
        }

        private static void returnTossedItem(ServerPlayer player, ItemStack stack) {
            if (stack.m_41619_()) {
                return;
            }
            ItemStack copy = stack.m_41777_();
            player.m_150109_().m_36054_(copy);
            if (!copy.m_41619_()) {
                int room;
                int moved;
                ItemStack carried = player.f_36096_.m_142621_();
                if (carried.m_41619_()) {
                    player.f_36096_.m_142503_(copy.m_278832_());
                } else if (ItemStack.m_150942_(carried, copy) && (moved = Math.min(room = carried.m_41741_() - carried.m_41613_(), copy.m_41613_())) > 0) {
                    carried.m_41769_(moved);
                    copy.m_41774_(moved);
                }
            }
            player.m_150109_().m_6596_();
            player.f_36095_.m_38946_();
            player.f_36096_.m_38946_();
        }

        @SubscribeEvent
        public static void onServerAboutToStart(ServerAboutToStartEvent event) {
            BattlefieldWorldManager.StartupPreparationResult prepared = BattlefieldWorldManager.getInstance().prepareAtStartup(event.getServer());
            LOGGER.info("\u6218\u573a\u5730\u56fe\u542f\u52a8\u51c6\u5907: status={} prepared={} warnings={} error={}", new Object[]{prepared.status(), prepared.preparedCount(), prepared.warnings().size(), prepared.error()});
        }

        @SubscribeEvent
        public static void onServerStarting(ServerStartingEvent event) {
            serverInstance = event.getServer();
            ServerCommandHandler.disableNaturalRegeneration(event.getServer());
            ServerRuntimeMaintenance.getInstance().reset();
            FortificationConfig.loadServerConfig();
            FobSupplyTracker.clearAll();
            Espetro.loadStartupConfigs(event.getServer());
            ClassCountManager.getInstance().initializeAllClassScores();
            BastionManager.getInstance().reset();
            TeamPackManager.getInstance().reset();
            SupplyManager.getInstance().reset();
            FobSupplyTracker.clearAll();
            GameStateManager.getInstance().resetGame();
        }

        @SubscribeEvent
        public static void onServerStopping(ServerStoppingEvent event) {
            ResupplySessionManager.clearAll();
            ServerCommandHandler.clearAndSaveOnlinePlayerInventories(event.getServer());
            int removedBarrierBlocks = GameStateManager.getInstance().cleanupTemporaryBarriers(event.getServer());
            BastionManager.getInstance().reset();
            TeamPackManager.getInstance().reset();
            SupplyManager.getInstance().reset();
            FobSupplyTracker.clearAll();
            int removedVehicles = VehicleManager.getInstance().removeAllDeployedVehicles(event.getServer());
            LOGGER.info("\u505c\u670d\u6218\u5c40\u6e05\u7406\u5b8c\u6210: \u5df2\u5220\u9664{}\u8f86\u90e8\u7f72\u8f7d\u5177, \u5df2\u6062\u590d/\u5220\u9664{}\u4e2a\u5c4f\u969c\u65b9\u5757", (Object)removedVehicles, (Object)removedBarrierBlocks);
        }

        @SubscribeEvent
        public static void onServerStopped(ServerStoppedEvent event) {
            ResupplySessionManager.clearAll();
            BastionManager.getInstance().clearRuntimeState();
            TeamPackManager.getInstance().clearRuntimeState();
            SupplyManager.getInstance().reset();
            VehicleManager.getInstance().clearRuntimeState();
            ServerRuntimeMaintenance.getInstance().reset();
            BattlefieldWorldManager.getInstance().resetAfterServerStop();
            VehicleSupplyActionPacket.clearRateLimits();
            serverInstance = null;
        }

        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            Player player = event.getEntity();
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                GameStateManager.getInstance().applyHubAdventureOnEnter(serverPlayer);
                GameStateManager.getInstance().enforceHubAdventure(serverPlayer);
                GameStateManager.getInstance().applyBattlefieldMiningRestriction(serverPlayer);
            }
        }

        @SubscribeEvent
        public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            Player player = event.getEntity();
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                ResupplySessionManager.clearPlayer(serverPlayer.m_20148_());
                if (!BattlefieldWorldManager.getInstance().isStartupReady() && Espetro.MOD_ID.equals(event.getTo().m_135782_().m_135827_())) {
                    serverPlayer.m_213846_(Component.m_237113_("\u00a7c\u6218\u573a\u542f\u52a8\u91cd\u7f6e\u5931\u8d25\uff0c\u672c\u6b21\u4f1a\u8bdd\u5730\u56fe\u5df2\u7981\u7528\u3002"));
                    GameStateManager.getInstance().forcePlayerToHub(serverPlayer);
                    return;
                }
                if (Level.f_46428_.equals(event.getTo())) {
                    serverPlayer.m_21195_(MobEffects.f_19599_);
                    GameStateManager.getInstance().applyHubAdventureOnEnter(serverPlayer);
                } else if (BattlefieldContext.isActiveBattlefield(serverPlayer.m_284548_())) {
                    GameStateManager.getInstance().applyBattlefieldMiningRestriction(serverPlayer);
                } else {
                    serverPlayer.m_21195_(MobEffects.f_19599_);
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerChangeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
            Player player = event.getEntity();
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                if (GameStateManager.getInstance().shouldForceHubAdventure(serverPlayer) && event.getNewGameMode() != GameType.ADVENTURE) {
                    event.setNewGameMode(GameType.ADVENTURE);
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            Player player;
            if (event.phase == TickEvent.Phase.END && (player = event.player) instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                if (serverPlayer.f_19797_ % 20 == 0) {
                    ServerCommandHandler.maintainPlayerFood(serverPlayer);
                    GameStateManager.getInstance().enforceHubAdventure(serverPlayer);
                }
            }
        }

        private static void clearPlayerInventory(ServerPlayer player) {
            ClassEquipment.clearEquipment(player);
        }

        private static void clearAndSaveOnlinePlayerInventories(MinecraftServer server) {
            if (server == null) {
                return;
            }
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                ClassEquipment.clearEquipment(player);
            }
            server.m_6846_().m_11302_();
        }

        private static void maintainPlayerFood(ServerPlayer player) {
            FoodData foodData = player.m_36324_();
            if (foodData.m_38702_() != 20) {
                foodData.m_38705_(20);
            }
            if (foodData.m_38722_() != 20.0f) {
                foodData.m_38717_(20.0f);
            }
            if (foodData.m_150380_() != 0.0f) {
                foodData.m_150378_(0.0f);
            }
        }

        private static void disableNaturalRegeneration(MinecraftServer server) {
            if (server == null) {
                return;
            }
            GameRules.BooleanValue naturalRegeneration = server.m_129783_().m_46469_().m_46170_(GameRules.f_46139_);
            if (naturalRegeneration.m_46223_()) {
                naturalRegeneration.m_46246_(false, server);
            }
        }

        @SubscribeEvent
        public static void onAddReloadListener(AddReloadListenerEvent event) {
            event.addListener(new PreparableReloadListener(){

                @Override
                public CompletableFuture<Void> m_5540_(PreparableReloadListener.PreparationBarrier barrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                    return ((CompletableFuture)CompletableFuture.supplyAsync(() -> null, backgroundExecutor).thenCompose(barrier::m_6769_)).thenRunAsync(() -> Espetro.reloadAllConfigs(), gameExecutor);
                }

                @Override
                public String m_7812_() {
                    return "Espetro Data Reloader";
                }
            });
            LOGGER.info("Espetro \u5df2\u6ce8\u518c\u6570\u636e\u5305\u70ed\u91cd\u8f7d\u76d1\u542c\u5668");
        }

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                GameStateManager.getInstance().onServerTick();
                ServerRuntimeMaintenance.getInstance().onServerTick();
                BastionManager.getInstance().tickDerivedTacticalState(event.getServer());
                ResupplySessionManager.tick(event.getServer());
            }
        }
    }

    @Mod.EventBusSubscriber(modid="espetro", bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventHandlers {
        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                Espetro.ensureKubeJSDefaultScriptsIfLoaded();
                NetworkManager.registerNetwork();
                new ClassCountManager();
                GameStateManager.init();
                TroopCountManager.init();
                SquadManager.init();
                TeamPackManager.init();
                BastionManager.getInstance();
                FortificationConfig.loadDefaults();
                OutpostManager.init();
                CommanderSkillManager.init();
                MapVoteManager.init();
                PlayerMatchStatsManager.init();
                CommanderGovernanceManager.init();
                PartyManager.getInstance();
                TutorialManager.getInstance();
                ModEventHandlers.preloadCriticalClasses();
            });
        }

        private static void preloadCriticalClasses() {
            try {
                SupplyType.values();
                SupplyManager.getInstance();
                Class.forName("org.espetro.logistics.DeploySupplyStationPlacer");
                Class.forName("org.espetro.logistics.LogisticsConfig");
                Class.forName("org.espetro.logistics.SupplySourceBlock");
                Class.forName("org.espetro.logistics.SupplySourceBlockEntity");
                Class.forName("org.espetro.tutorial.TutorialStep");
                LOGGER.info("Espetro \u5173\u952e\u7c7b\u9884\u52a0\u8f7d\u5b8c\u6210");
            }
            catch (Throwable t) {
                LOGGER.error("Espetro \u5173\u952e\u7c7b\u9884\u52a0\u8f7d\u5931\u8d25\uff08\u8bf7\u786e\u8ba4 jar \u5b8c\u6574\u4e14\u52ff\u5728\u6e38\u620f\u8fd0\u884c\u65f6\u8986\u76d6\uff09", t);
            }
        }
    }
}

