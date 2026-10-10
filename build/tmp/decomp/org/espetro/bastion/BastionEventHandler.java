/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.EntityLeaveLevelEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$BreakSpeed
 *  net.minecraftforge.event.entity.player.PlayerEvent$Clone
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerRespawnEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteractSpecific
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$LeftClickBlock
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.event.level.BlockEvent$BreakEvent
 *  net.minecraftforge.event.level.BlockEvent$EntityPlaceEvent
 *  net.minecraftforge.event.level.ExplosionEvent$Detonate
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package org.espetro.bastion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.Espetro;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionItems;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.DeployActions;
import org.espetro.bastion.FortificationManager;
import org.espetro.bastion.RadioLossPolicy;
import org.espetro.logistics.SupplyManager;
import org.espetro.logistics.resupply.ResupplySessionManager;
import org.espetro.logistics.resupply.ResupplySourceRef;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.network.NetworkManager;
import org.espetro.network.RadioRadialPacket;
import org.espetro.team.ClassCountManager;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.OutpostManager;
import org.espetro.team.SpawnPointConfig;
import org.espetro.team.SquadManager;
import org.espetro.team.TeamPackManager;
import org.espetro.team.VoteManager;

@Mod.EventBusSubscriber(modid="espetro")
public class BastionEventHandler {
    private static final double RADIO_DISMANTLE_DISTANCE_SQR = 36.0;
    private static final Map<UUID, RadioDismantleAttempt> RADIO_DISMANTLE_ATTEMPTS = new HashMap<UUID, RadioDismantleAttempt>();
    private static int radioDismantleTickCounter;

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        String factionId;
        Object bastion;
        ArmorStand armorStand;
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof ArmorStand && BastionEventHandler.isBastionCore(armorStand = (ArmorStand)livingEntity)) {
            if (!armorStand.m_9236_().f_46443_ && (bastion = BastionManager.getInstance().findBastionByArmorStand(armorStand.m_20148_())) != null && ((BastionData)bastion).isActive()) {
                BastionManager.getInstance().onCoreArmorStandDestroyed((BastionData)bastion, event.getSource().m_7639_());
            }
            return;
        }
        bastion = event.getEntity();
        if (!(bastion instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)bastion;
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.BATTLE && phase != GamePhase.DEPLOYING) {
            return;
        }
        ServerLevel battlefield = player.m_284548_();
        if (!BattlefieldContext.isActiveBattlefield(battlefield)) {
            return;
        }
        if (phase == GamePhase.DEPLOYING) {
            OutpostManager.getInstance().prepareDeployTargets(battlefield);
        }
        if ((factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_())) == null) {
            return;
        }
        String team = Espetro.getPlayerTeam(player);
        if (team != null) {
            SpawnPointConfig.SpawnPoint spawnPoint = SpawnPointConfig.getSpawnPoint(team);
            BastionManager bastionManager = BastionManager.getInstance();
            bastionManager.savePlayerDeployPoint(player, new BlockPos((int)spawnPoint.x, (int)spawnPoint.y, (int)spawnPoint.z), battlefield);
        }
        BastionManager.getInstance().onPlayerDeath(battlefield, player.m_20148_());
        TeamPackManager.getInstance().onPlayerDeath(player.m_20148_());
        Espetro.LOGGER.info("\u73a9\u5bb6 {} \u6b7b\u4ea1\uff0c\u8fdb\u5165\u5175\u7ad9\u9009\u62e9\u72b6\u6001", (Object)player.m_7755_().getString());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        TeamPackManager.getInstance().syncTeamPackItem(player2);
        if (BastionManager.getInstance().isWaitingForBastion(player2.m_20148_())) {
            BastionEventHandler.applyWaitingDeployState(player2);
            ServerPlayer finalPlayer = player2;
            player2.f_8924_.execute(() -> {
                if (BastionManager.getInstance().isWaitingForBastion(finalPlayer.m_20148_())) {
                    int remaining = GameStateManager.getInstance().getCurrentPhase() == GamePhase.DEPLOYING ? GameStateManager.getInstance().getDeployTimeRemainingSeconds() : -1;
                    NetworkManager.sendUnifiedDeployScreen(finalPlayer, remaining);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        TeamPackManager.getInstance().syncTeamPackItem(player2);
        if (BastionManager.getInstance().isWaitingForBastion(player2.m_20148_())) {
            BastionEventHandler.applyWaitingDeployState(player2);
            player2.f_8924_.execute(() -> {
                if (BastionManager.getInstance().isWaitingForBastion(player2.m_20148_())) {
                    int remaining = GameStateManager.getInstance().getCurrentPhase() == GamePhase.DEPLOYING ? GameStateManager.getInstance().getDeployTimeRemainingSeconds() : -1;
                    NetworkManager.sendUnifiedDeployScreen(player2, remaining);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onTeamPackBreak(BlockEvent.BreakEvent event) {
        boolean isLeaderOfThisSquad;
        boolean isEnemy;
        if (event.getLevel().m_5776_()) {
            return;
        }
        TeamPackManager.TeamPackData teamPack = TeamPackManager.getInstance().findByPos(event.getPos());
        if (teamPack == null) {
            return;
        }
        event.setCanceled(true);
        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        String playerTeam = Espetro.getPlayerTeam(player2);
        boolean bl = isEnemy = playerTeam != null && !playerTeam.equals(teamPack.team);
        if (isEnemy) {
            TeamPackManager.getInstance().damageTeamPack(teamPack, player2, 1, true);
            return;
        }
        boolean bl2 = isLeaderOfThisSquad = SquadManager.getInstance().isSquadLeader(player2.m_20148_()) && SquadManager.getInstance().getPlayerSquadId(player2.m_20148_()) == teamPack.squadId;
        if (!isLeaderOfThisSquad) {
            player2.m_213846_(Component.m_237113_("\u00a7c\u53ea\u6709\u672c\u5c0f\u961f\u961f\u957f\u624d\u80fd\u62c6\u9664\u961f\u5305\uff01"));
            return;
        }
        TeamPackManager.getInstance().destroyTeamPack(teamPack, player2, true, false);
    }

    @SubscribeEvent
    public static void onTeamPackEnemyLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        boolean isEnemy;
        Player player;
        if (event.getLevel().m_5776_() || !((player = event.getEntity()) instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        TeamPackManager.TeamPackData teamPack = TeamPackManager.getInstance().findByPos(event.getPos());
        if (teamPack == null) {
            return;
        }
        String playerTeam = Espetro.getPlayerTeam(player2);
        boolean bl = isEnemy = playerTeam != null && !playerTeam.equals(teamPack.team);
        if (isEnemy) {
            event.setCanceled(true);
            TeamPackManager.getInstance().damageTeamPack(teamPack, player2, 1, true);
        }
    }

    @SubscribeEvent
    public static void onTeamPackRightClick(PlayerInteractEvent.RightClickBlock event) {
        boolean isEnemy;
        Player player;
        if (event.getLevel().m_5776_() || !((player = event.getEntity()) instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        TeamPackManager.TeamPackData teamPack = TeamPackManager.getInstance().findByPos(event.getPos());
        if (teamPack == null) {
            return;
        }
        event.setCanceled(true);
        String playerTeam = Espetro.getPlayerTeam(player2);
        boolean bl = isEnemy = playerTeam != null && !playerTeam.equals(teamPack.team);
        if (isEnemy) {
            TeamPackManager.getInstance().damageTeamPack(teamPack, player2, 1, true);
        }
    }

    @SubscribeEvent
    public static void onTeamPackBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getEntity().m_9236_().m_5776_()) {
            return;
        }
        BlockPos pos = event.getPosition().orElse(null);
        if (pos == null || TeamPackManager.getInstance().findByPos(pos) == null) {
            return;
        }
        float multiplier = TeamPackManager.getInstance().getBreakSpeedMultiplier();
        event.setNewSpeed(event.getNewSpeed() * multiplier);
    }

    @SubscribeEvent
    public static void onExplosionDetonateTeamPack(ExplosionEvent.Detonate event) {
        if (event.getLevel().m_5776_()) {
            return;
        }
        for (BlockPos pos : event.getAffectedBlocks()) {
            TeamPackManager.TeamPackData teamPack = TeamPackManager.getInstance().findByPos(pos);
            if (teamPack == null) continue;
            TeamPackManager.getInstance().destroyTeamPackByExplosion(teamPack);
        }
    }

    @SubscribeEvent
    public static void onBastionCoreInteract(PlayerInteractEvent.EntityInteract event) {
        BastionEventHandler.cancelBastionCoreInteract((PlayerInteractEvent)event, event.getTarget());
    }

    @SubscribeEvent
    public static void onBastionCoreInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        BastionEventHandler.cancelBastionCoreInteract((PlayerInteractEvent)event, event.getTarget());
    }

    private static void cancelBastionCoreInteract(PlayerInteractEvent event, Entity target) {
        ServerPlayer player;
        ArmorStand armorStand;
        block6: {
            block5: {
                if (!(target instanceof ArmorStand) || !BastionEventHandler.isBastionCore(armorStand = (ArmorStand)target)) {
                    return;
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                Player player2 = event.getEntity();
                if (!(player2 instanceof ServerPlayer)) break block5;
                player = (ServerPlayer)player2;
                if (!player.m_9236_().f_46443_) break block6;
            }
            return;
        }
        BastionData bastion = BastionManager.getInstance().findBastionByArmorStand(armorStand.m_20148_());
        if (bastion != null && !bastion.isRadio()) {
            player.m_213846_(Component.m_237113_("\u00a77\u8fd9\u662f\u5175\u7ad9\u6838\u5fc3\uff0c\u8bf7\u5411\u5df1\u65b9 Radio \u65b9\u5757\u5b58\u53d6\u8865\u7ed9\u3002"));
        }
    }

    @SubscribeEvent
    public static void onRallyItemPlace(BlockEvent.EntityPlaceEvent event) {
        boolean fromRallyItem;
        ServerPlayer player;
        Object object;
        block7: {
            block6: {
                if (event.getLevel().m_5776_() || !event.getPlacedBlock().m_60713_(Blocks.f_50273_) || !((object = event.getEntity()) instanceof ServerPlayer)) break block6;
                player = (ServerPlayer)object;
                object = event.getLevel();
                if (object instanceof ServerLevel) break block7;
            }
            return;
        }
        ServerLevel level = (ServerLevel)object;
        TeamPackManager manager = TeamPackManager.getInstance();
        boolean bl = fromRallyItem = manager.isTeamPackItem(player.m_21205_()) || manager.isTeamPackItem(player.m_21206_());
        if (!fromRallyItem) {
            return;
        }
        BlockPos pos = event.getPos();
        String precheck = manager.canPlaceTeamPack(player, level, pos);
        if (precheck != null) {
            event.setCanceled(true);
            player.m_213846_(Component.m_237113_(precheck));
            return;
        }
        String error = manager.placeTeamPack(player, level, pos);
        if (error != null) {
            event.setCanceled(true);
            player.m_213846_(Component.m_237113_(error));
        }
    }

    @SubscribeEvent
    public static void onRadioBlockPlace(BlockEvent.EntityPlaceEvent event) {
        ServerPlayer player;
        Object object;
        block5: {
            block4: {
                if (event.getLevel().m_5776_() || BastionItems.RADIO_BLOCK == null || !event.getPlacedBlock().m_60713_(BastionItems.RADIO_BLOCK)) {
                    return;
                }
                object = event.getEntity();
                if (!(object instanceof ServerPlayer)) break block4;
                player = (ServerPlayer)object;
                object = event.getLevel();
                if (object instanceof ServerLevel) break block5;
            }
            event.setCanceled(true);
            return;
        }
        ServerLevel level = (ServerLevel)object;
        event.setCanceled(true);
        player.m_5661_(Component.m_237113_("\u00a7e\u8bf7\u4ece\u5efa\u9020\u5de5\u4e8b\u8f6e\u76d8\u9009\u62e9 Radio\uff0c\u5e76\u5de6\u952e\u786e\u8ba4\u4f4d\u7f6e\u3002"), true);
    }

    @SubscribeEvent
    public static void onRadioRetrieve(PlayerInteractEvent.LeftClickBlock event) {
        ServerLevel level;
        ServerPlayer player;
        block15: {
            block14: {
                Object object;
                if (event.getLevel().m_5776_() || !((object = event.getEntity()) instanceof ServerPlayer)) break block14;
                player = (ServerPlayer)object;
                if (BastionItems.RADIO_BLOCK != null && (object = event.getLevel()) instanceof ServerLevel && (level = (ServerLevel)object).m_8055_(event.getPos()).m_60713_(BastionItems.RADIO_BLOCK)) break block15;
            }
            return;
        }
        BastionData bastion = BastionManager.getInstance().findRadioByBlockPos(event.getPos());
        if (bastion == null) {
            return;
        }
        if (FortificationManager.getInstance().contains(level, event.getPos())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            if (player.m_21205_().m_41720_() != Items.f_42384_) {
                player.m_5661_(Component.m_237113_("\u00a7e\u4f7f\u7528\u5de5\u5175\u94f2\u53f3\u952e\u62c6\u9664\uff0c\u5de6\u952e\u4fee\u5efa\u6216\u4fee\u590d\u3002"), true);
            }
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            player.m_5661_(Component.m_237113_("\u00a7c\u5c1a\u672a\u52a0\u5165\u9635\u8425\uff0c\u65e0\u6cd5\u62c6\u9664 Radio\u3002"), true);
            return;
        }
        if (!team.equals(bastion.getTeam())) {
            if (!player.m_6144_()) {
                player.m_5661_(Component.m_237113_("\u00a7e\u6f5c\u884c\u5e76\u5de6\u952e\u70b9\u51fb\u53ef\u5f00\u59cb\u62c6\u9664\u654c\u65b9 Radio\u3002"), true);
                return;
            }
            BastionEventHandler.startRadioDismantle(player, bastion);
            return;
        }
        if (!player.m_6144_()) {
            player.m_5661_(Component.m_237113_("\u00a7e\u5c0f\u961f\u957f\u6216\u6307\u6325\u5b98\u53ef\u6f5c\u884c\u5de6\u952e\u6536\u8d77\u5df1\u65b9 Radio\u3002"), true);
            return;
        }
        boolean commander = VoteManager.getInstance().isCommander(player.m_20148_());
        boolean squadLeader = SquadManager.getInstance().isSquadLeader(player.m_20148_());
        if (!commander && !squadLeader) {
            player.m_5661_(Component.m_237113_("\u00a7c\u53ea\u6709\u5c0f\u961f\u957f\u6216\u6307\u6325\u5b98\u624d\u80fd\u6536\u8d77\u5df1\u65b9 Radio\u3002"), true);
            return;
        }
        int lostConstruction = bastion.getConstructionSupplies();
        int lostAmmunition = bastion.getAmmunitionSupplies();
        level.m_7731_(event.getPos(), Blocks.f_50016_.m_49966_(), 3);
        BastionManager.getInstance().retrieveRadio(bastion, player.m_20148_());
        if (BastionItems.RADIO_BLOCK_ITEM != null) {
            ItemStack stack = new ItemStack(BastionItems.RADIO_BLOCK_ITEM);
            if (DeployActions.hasRadioItem(player) || !player.m_150109_().m_36054_(stack)) {
                player.m_36176_(stack, false);
            }
        }
        player.m_213846_(Component.m_237113_("\u00a7a\u5df2\u6536\u8d77 Radio \u00a7e" + bastion.getName() + "\u00a7a\uff0c\u53ef\u91cd\u65b0\u653e\u7f6e\u3002"));
        if (lostConstruction > 0 || lostAmmunition > 0) {
            player.m_213846_(Component.m_237113_("\u00a77\u5e93\u5b58 \u00a76" + lostConstruction + " \u5efa\u6750\u00a77 / \u00a7b" + lostAmmunition + " \u5f39\u836f\u00a77 \u5df2\u4e22\u5931\u3002"));
        }
    }

    @SubscribeEvent
    public static void onRadioBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().m_5776_()) {
            return;
        }
        BastionData bastion = BastionManager.getInstance().findRadioByBlockPos(event.getPos());
        if (bastion == null) {
            return;
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRadioBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (BastionItems.RADIO_BLOCK == null || !event.getState().m_60713_(BastionItems.RADIO_BLOCK)) {
            return;
        }
        event.setNewSpeed(0.0f);
    }

    private static void startRadioDismantle(ServerPlayer player, BastionData bastion) {
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.DEPLOYING && phase != GamePhase.BATTLE || !BattlefieldContext.isActiveBattlefield(player.m_284548_()) || player.m_5833_() || !player.m_6084_()) {
            player.m_5661_(Component.m_237113_("\u00a7c\u5f53\u524d\u65e0\u6cd5\u62c6\u9664 Radio\u3002"), true);
            return;
        }
        if (player.m_20238_(Vec3.m_82512_(bastion.getPosition())) > 36.0) {
            player.m_5661_(Component.m_237113_("\u00a7c\u8bf7\u9760\u8fd1 Radio \u540e\u518d\u5f00\u59cb\u62c6\u9664\u3002"), true);
            return;
        }
        long now = System.currentTimeMillis();
        RadioDismantleAttempt existing = RADIO_DISMANTLE_ATTEMPTS.get(player.m_20148_());
        if (existing != null && existing.radioId().equals(bastion.getBastionId())) {
            long remaining = Math.max(1L, (existing.completesAtMillis() - now + 999L) / 1000L);
            player.m_5661_(Component.m_237113_("\u00a7e\u6b63\u5728\u62c6\u9664 Radio\uff0c\u5269\u4f59 \u00a7c" + remaining + " \u00a7e\u79d2\u3002"), true);
            return;
        }
        RADIO_DISMANTLE_ATTEMPTS.put(player.m_20148_(), new RadioDismantleAttempt(bastion.getBastionId(), player.m_284548_().m_46472_(), bastion.getPosition().m_7949_(), now + 30000L));
        player.m_213846_(Component.m_237113_("\u00a7e\u5f00\u59cb\u62c6\u9664\u654c\u65b9 Radio\u3002\u8bf7\u5728\u9644\u8fd1\u7b49\u5f85 \u00a7c30 \u00a7e\u79d2\u3002"));
        player.m_5661_(Component.m_237113_("\u00a7e\u62c6\u9664 Radio\uff1a\u00a7c30 \u79d2"), true);
    }

    @SubscribeEvent
    public static void onRadioDismantleServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || RADIO_DISMANTLE_ATTEMPTS.isEmpty()) {
            return;
        }
        if (++radioDismantleTickCounter < 20) {
            return;
        }
        radioDismantleTickCounter = 0;
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            BastionEventHandler.clearRadioDismantleAttempts();
            return;
        }
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, RadioDismantleAttempt>> iterator = RADIO_DISMANTLE_ATTEMPTS.entrySet().iterator();
        while (iterator.hasNext()) {
            RadioDismantleAttempt attempt;
            BastionData bastion;
            ServerLevel level;
            Map.Entry<UUID, RadioDismantleAttempt> entry = iterator.next();
            ServerPlayer player = server.m_6846_().m_11259_(entry.getKey());
            String cancelReason = BastionEventHandler.validateRadioDismantle(player, level, bastion = (level = server.m_129880_((attempt = entry.getValue()).dimension())) == null ? null : BastionManager.getInstance().findRadioByBlockPos(attempt.pos()), attempt);
            if (cancelReason != null) {
                iterator.remove();
                if (player == null) continue;
                player.m_5661_(Component.m_237113_("\u00a7cRadio \u62c6\u9664\u5df2\u53d6\u6d88\uff1a" + cancelReason), true);
                continue;
            }
            long remainingMillis = attempt.completesAtMillis() - now;
            if (remainingMillis > 0L) {
                long remainingSeconds = Math.max(1L, (remainingMillis + 999L) / 1000L);
                player.m_5661_(Component.m_237113_("\u00a7e\u62c6\u9664 Radio\uff1a\u00a7c" + remainingSeconds + " \u79d2"), true);
                continue;
            }
            iterator.remove();
            level.m_7731_(attempt.pos(), Blocks.f_50016_.m_49966_(), 3);
            BastionManager.getInstance().destroyBastionWithManpower(bastion, player, true);
            player.m_213846_(Component.m_237113_("\u00a7a\u654c\u65b9 Radio \u5df2\u62c6\u9664\u3002"));
        }
    }

    @Nullable
    private static String validateRadioDismantle(@Nullable ServerPlayer player, @Nullable ServerLevel level, @Nullable BastionData bastion, RadioDismantleAttempt attempt) {
        if (player == null || level == null || bastion == null || !bastion.isActive() || !bastion.getBastionId().equals(attempt.radioId())) {
            return "\u76ee\u6807\u5df2\u4e0d\u5b58\u5728";
        }
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.DEPLOYING && phase != GamePhase.BATTLE || player.m_284548_() != level || player.m_5833_() || !player.m_6084_()) {
            return "\u5f53\u524d\u72b6\u6001\u4e0d\u5141\u8bb8\u62c6\u9664";
        }
        String playerTeam = Espetro.getPlayerTeam(player);
        if (playerTeam == null || playerTeam.equals(bastion.getTeam())) {
            return "\u9635\u8425\u72b6\u6001\u5df2\u6539\u53d8";
        }
        if (player.m_20238_(Vec3.m_82512_(attempt.pos())) > 36.0) {
            return "\u79bb\u5f00\u4e86 Radio \u9644\u8fd1";
        }
        return null;
    }

    public static void clearRadioDismantleAttempts() {
        RADIO_DISMANTLE_ATTEMPTS.clear();
        radioDismantleTickCounter = 0;
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void onExplosionDetonateRadio(ExplosionEvent.Detonate event) {
        Level level;
        if (event.getLevel().m_5776_() || !((level = event.getLevel()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        Explosion explosion = event.getExplosion();
        Entity attacker = explosion.m_252906_();
        if (attacker == null) {
            attacker = explosion.getExploder();
        }
        for (BlockPos pos : event.getAffectedBlocks()) {
            BastionData bastion = BastionManager.getInstance().findRadioByBlockPos(pos);
            boolean indexed = FortificationManager.getInstance().contains(level2, pos);
            if (bastion == null || !RadioLossPolicy.explosionScoresRadioLoss(true, indexed)) continue;
            BastionManager.getInstance().destroyBastionWithManpower(bastion, attacker, true);
        }
        Entity lossAttacker = attacker;
        level2.m_7654_().execute(() -> BastionManager.getInstance().destroyRadiosMissingCoreInArea(level2, null, Double.POSITIVE_INFINITY, lossAttacker));
    }

    @SubscribeEvent
    public static void onRadioBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        ServerPlayer player;
        block7: {
            block6: {
                Player player2;
                if (event.getLevel().m_5776_() || !((player2 = event.getEntity()) instanceof ServerPlayer)) break block6;
                player = (ServerPlayer)player2;
                if (BastionItems.RADIO_BLOCK != null) break block7;
            }
            return;
        }
        ServerLevel level = (ServerLevel)event.getLevel();
        if (!level.m_8055_(event.getPos()).m_60713_(BastionItems.RADIO_BLOCK)) {
            return;
        }
        BastionData bastion = BastionManager.getInstance().findRadioByBlockPos(event.getPos());
        if (bastion == null) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        String team = Espetro.getPlayerTeam(player);
        if (team == null || !team.equals(bastion.getTeam())) {
            return;
        }
    }

    @SubscribeEvent
    public static void onSupplySourceInteract(PlayerInteractEvent.RightClickBlock event) {
        ServerPlayer player;
        Object object;
        block9: {
            block8: {
                if (event.getLevel().m_5776_() || !((object = event.getEntity()) instanceof ServerPlayer)) break block8;
                player = (ServerPlayer)object;
                object = event.getLevel();
                if (object instanceof ServerLevel) break block9;
            }
            return;
        }
        ServerLevel level = (ServerLevel)object;
        if (player.m_21205_().m_41720_() == Items.f_42384_ && FortificationManager.getInstance().contains(level, event.getPos())) {
            return;
        }
        try {
            if (SupplyManager.getInstance().handleSourceInteraction(player, level, event.getPos())) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
        catch (Throwable t) {
            Espetro.LOGGER.error("\u8865\u7ed9\u7ad9\u4ea4\u4e92\u5931\u8d25 at {}", (Object)event.getPos(), (Object)t);
        }
    }

    @SubscribeEvent
    public static void onShulkerBoxInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        if (event.getLevel().m_5776_()) {
            return;
        }
        ServerLevel level = (ServerLevel)event.getLevel();
        BlockPos clickedPos = event.getPos();
        if (player2.m_21205_().m_41720_() == Items.f_42384_ && FortificationManager.getInstance().contains(level, clickedPos)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }
        BlockState state = level.m_8055_(clickedPos);
        boolean legacyShulker = state.m_60713_(Blocks.f_50456_) || state.m_60713_(Blocks.f_50524_) || state.m_60713_(Blocks.f_50521_);
        String team = Espetro.getPlayerTeam(player2);
        if (team == null) {
            return;
        }
        BastionData radio = FortificationManager.getInstance().findRadioForAmmoCrate(level, clickedPos, team);
        boolean registeredCrate = FortificationManager.getInstance().isAmmoCrateAt(level, clickedPos, team);
        if (radio == null && !registeredCrate && !legacyShulker) {
            return;
        }
        if (radio == null) {
            if (registeredCrate || BastionManager.getInstance().findBastionByShulkerPos(clickedPos) != null) {
                event.setCanceled(true);
            }
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (player2.m_6144_()) {
            ResupplySessionManager.open(player2, ResupplySourceRef.radio(clickedPos));
        } else {
            RadioRadialPacket.openClassMenuAt(player2, clickedPos);
        }
    }

    private static boolean isBastionCore(ArmorStand armorStand) {
        return armorStand.m_19880_().contains("bastion_armor_stand");
    }

    @SubscribeEvent
    public static void onBastionCoreLeaveLevel(EntityLeaveLevelEvent event) {
        ArmorStand armorStand;
        if (event.getLevel().m_5776_()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof ArmorStand) || !BastionEventHandler.isBastionCore(armorStand = (ArmorStand)entity)) {
            return;
        }
        Entity.RemovalReason reason = entity.m_146911_();
        if (reason != Entity.RemovalReason.KILLED) {
            return;
        }
        BastionData bastion = BastionManager.getInstance().findBastionByArmorStand(armorStand.m_20148_());
        if (bastion != null && bastion.isActive()) {
            BastionManager.getInstance().onCoreArmorStandDestroyed(bastion, null);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        boolean waiting;
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        BastionManager bastionManager = BastionManager.getInstance();
        UUID playerId = player2.m_20148_();
        Vec3 lock = bastionManager.getPlayerLockPosition(playerId);
        boolean adventureDeployWaiting = waiting = bastionManager.isWaitingForBastion(playerId);
        if (lock == null && !waiting) {
            return;
        }
        if (lock == null) {
            if (player2.f_19797_ % 20 == 0) {
                BastionEventHandler.applyWaitingDeployState(player2);
            } else {
                BastionEventHandler.maintainHoldWithoutTeleport(player2, false, adventureDeployWaiting);
            }
            return;
        }
        int shard = Math.floorMod(playerId.hashCode(), 20);
        if (Math.floorMod(player2.f_19797_, 20) != shard) {
            return;
        }
        boolean needModeFix = adventureDeployWaiting ? player2.f_8941_.m_9290_() != GameType.ADVENTURE : !player2.m_5833_();
        boolean teleported = BastionEventHandler.enforceLockedPosition(player2, lock);
        boolean forceBlind = teleported || needModeFix || Math.floorMod(player2.f_19797_, 60) == shard;
        BastionEventHandler.maintainHoldWithoutTeleport(player2, forceBlind, adventureDeployWaiting);
    }

    private static boolean enforceLockedPosition(ServerPlayer player, Vec3 lock) {
        player.m_20334_(0.0, 0.0, 0.0);
        player.f_19789_ = 0.0f;
        if (player.m_20238_(lock) > 0.25) {
            player.m_8999_(player.m_284548_(), lock.f_82479_, lock.f_82480_, lock.f_82481_, player.m_146908_(), player.m_146909_());
            player.m_20334_(0.0, 0.0, 0.0);
            return true;
        }
        return false;
    }

    private static void maintainHoldWithoutTeleport(ServerPlayer player, boolean forceBlindResync, boolean adventureDeployWaiting) {
        if (adventureDeployWaiting) {
            GameStateManager.enforceAdventureBlindness(player, forceBlindResync);
        } else {
            GameStateManager.enforceSpectatorBlindness(player, forceBlindResync);
        }
    }

    private static void applyWaitingDeployState(ServerPlayer player) {
        GameStateManager.getInstance().applyDeploymentWaitingState(player);
    }

    private record RadioDismantleAttempt(UUID radioId, ResourceKey<Level> dimension, BlockPos pos, long completesAtMillis) {
    }
}

