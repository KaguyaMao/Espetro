package org.espetro.team;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.espetro.Espetro;
import org.espetro.bastion.BastionManager;
import org.espetro.config.GameConfig;
import org.espetro.network.NetworkManager;
import org.espetro.network.DeployScreenSync;
import org.espetro.network.FactionDisplay;
import org.espetro.network.MatchBroadcasts;
import org.espetro.network.PreMatchScreens;
import org.espetro.network.SquadSync;
import org.espetro.vehicle.VehicleManager;
import org.espetro.vehicle.VehicleEventHandler;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.mapconfig.ExternalConfigBootstrap;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.governance.CommanderGovernanceManager;
import org.espetro.dimension.BattlefieldWorldManager;
import org.espetro.logistics.SupplyManager;
import org.espetro.logistics.resupply.ResupplySessionManager;
import net.minecraftforge.common.MinecraftForge;
import org.espetro.api.event.GamePhaseChangedEvent;
import java.util.*;
import java.util.function.Consumer;

/**
 * 玩家在各场景下的强制状态（服务端权威）：
 * <ul>
 * <li>MatchHold：旁观 + 失明 + 锁位（主城高空 / 战场等待点）；</li>
 * <li>部署等待：冒险 + 失明 + 无敌，锁在原部署点；</li>
 * <li>主城：冒险模式与回城重置；</li>
 * <li>战场挖掘限制。</li>
 * </ul>
 * 从 {@link GameStateManager} 拆出；只读取当前阶段与观察者状态，不持有自己的状态。
 */
public final class PlayerHoldStates {

    private PlayerHoldStates() {
    }

    /**
     * 统一阶段等待态：SPECTATOR + BLINDNESS + 零速度 + Bastion 位置锁。
     * 从地图投票到部署落地前的禁锢路径应全部走此方法。
     */
    public enum HoldAnchor {
        /** 战场高空等待点（要求 BattlefieldContext 已 active）。 */
        BATTLEFIELD_WAIT,
        /** 主城出生点上方 waitingY。 */
        HUB_HIGH,
        /** 仅维持旁观/失明/零速，位置锁沿用已有 lock（若无则按 AUTO 解析）。 */
        CURRENT_LOCK,
        /** 战场已 active 则战场等待点，否则主城高空。 */
        AUTO
    }

    public static void applyMatchHoldState(ServerPlayer player, HoldAnchor anchor) {
        if (player == null || player.connection == null) {
            return;
        }
        HoldAnchor resolved = anchor == null ? HoldAnchor.AUTO : anchor;
        if (resolved == HoldAnchor.AUTO) {
            resolved = BattlefieldContext.isActive()
                ? HoldAnchor.BATTLEFIELD_WAIT : HoldAnchor.HUB_HIGH;
        }
        if (resolved == HoldAnchor.CURRENT_LOCK) {
            Vec3 existing = BastionManager.getInstance().getPlayerLockPosition(player.getUUID());
            if (existing == null) {
                resolved = BattlefieldContext.isActive()
                    ? HoldAnchor.BATTLEFIELD_WAIT : HoldAnchor.HUB_HIGH;
            } else {
                enforceSpectatorBlindness(player);
                player.setDeltaMovement(0, 0, 0);
                player.fallDistance = 0f;
                if (player.distanceToSqr(existing) > 0.01) {
                    player.teleportTo(player.serverLevel(),
                        existing.x, existing.y, existing.z, 0f, 0f);
                }
                return;
            }
        }

        enforceSpectatorBlindness(player);
        player.setDeltaMovement(0, 0, 0);
        player.fallDistance = 0f;

        if (resolved == HoldAnchor.BATTLEFIELD_WAIT) {
            if (!BattlefieldContext.isActive()) {
                resolved = HoldAnchor.HUB_HIGH;
            } else {
                ServerLevel battlefield = BattlefieldContext.requireBattlefield(player.server);
                Vec3 waitingPosition = getWaitingPosition();
                if (player.serverLevel() != battlefield
                    || player.distanceToSqr(waitingPosition) > 0.01) {
                    player.teleportTo(battlefield,
                        waitingPosition.x, waitingPosition.y, waitingPosition.z, 0f, 0f);
                }
                BastionManager.getInstance().lockPlayerPosition(player.getUUID(), waitingPosition);
                // 跨维 Respawn 后客户端丢药水：传送后再强制同步失明包。
                enforceSpectatorBlindness(player, true);
                return;
            }
        }

        // HUB_HIGH：主城出生点 XZ，高度用 waitingY（高空黑底）。
        ServerLevel hub = player.server.overworld();
        BlockPos spawn = hub.getSharedSpawnPos();
        double x = spawn.getX() + 0.5;
        double y = GameConfig.getWaitingY();
        double z = spawn.getZ() + 0.5;
        Vec3 hold = new Vec3(x, y, z);
        if (player.serverLevel() != hub || player.distanceToSqr(hold) > 0.25) {
            player.teleportTo(hub, x, y, z, 0f, 0f);
        }
        BastionManager.getInstance().lockPlayerPosition(player.getUUID(), hold);
        // 同维也可能需刷新；跨维到 hub 时同样必须重推失明。
        enforceSpectatorBlindness(player, true);
    }

    /**
     * MatchHold 强制旁观 + 失明。
     * <p>跨维 {@code teleportTo} 会发 Respawn 包，客户端重建 LocalPlayer 且
     * <b>不复制</b>药水效果；若服务端仍 {@code hasEffect(BLINDNESS)} 而只做
     * {@code addEffect}，可能不发更新包，客户端无失明、透明阶段 UI 会透出世界。
     * {@code forceClientResync=true} 时 remove 再 add 强制同步；tick 路径可 false 并节流。
     */
    public static void enforceSpectatorBlindness(ServerPlayer player) {
        enforceSpectatorBlindness(player, true);
    }

    /** 该观战者是否处于“已进入战局”的阶段（对战 / 回合结算）。 */
    public static boolean isObserverInMatch(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        GameStateManager manager = GameStateManager.getInstance();
        if (manager == null || !manager.isObserver(player.getUUID())) {
            return false;
        }
        GamePhase phase = manager.getCurrentPhase();
        return phase == GamePhase.BATTLE || phase == GamePhase.ROUND_END;
    }

    /**
     * 观战者进入战局：解除失明与位置锁，保持旁观模式自由观战。
     */
    public static void releaseObserverHold(ServerPlayer player) {
        if (player == null) {
            return;
        }
        player.setGameMode(GameType.SPECTATOR);
        player.removeEffect(MobEffects.BLINDNESS);
        BastionManager.getInstance().unlockPlayerPosition(player.getUUID());
        BastionManager.getInstance().clearWaiting(player.getUUID());
    }

    public static void enforceSpectatorBlindness(ServerPlayer player, boolean forceClientResync) {
        if (player == null) {
            return;
        }
        // 观战者进入战局（对战/结算）后不再失明，也不再被锁位，可自由飞行观战；
        // 投票与准备阶段仍然保持隐藏（只显示"当前正在投票，请等待"）。
        if (isObserverInMatch(player)) {
            player.setGameMode(GameType.SPECTATOR);
            player.removeEffect(MobEffects.BLINDNESS);
            return;
        }
        if (!player.isSpectator()) {
            player.setGameMode(GameType.SPECTATOR);
        }
        if (forceClientResync) {
            player.removeEffect(MobEffects.BLINDNESS);
            player.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS, Integer.MAX_VALUE, 0, false, false, false));
        } else if (!player.hasEffect(MobEffects.BLINDNESS)) {
            player.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS, Integer.MAX_VALUE, 0, false, false, false));
        }
        player.setDeltaMovement(0, 0, 0);
        player.fallDistance = 0f;
    }

    /**
     * @deprecated 使用 {@link #applyMatchHoldState(ServerPlayer, HoldAnchor)}；保留兼容旧调用。
     */
    @Deprecated
    public static void applyMatchLoadingHoldState(ServerPlayer player) {
        applyMatchHoldState(player, HoldAnchor.AUTO);
    }

    public static Vec3 getWaitingPosition() {
        return new Vec3(0.5, GameConfig.getWaitingY(), 0.5);
    }

    /** 强制玩家处于统一的高空等待点、旁观模式和失明状态。 */
    public static void applyWaitingState(ServerPlayer player) {
        applyMatchHoldState(player, HoldAnchor.BATTLEFIELD_WAIT);
    }

    public static void applyBattlefieldWaitingState(ServerPlayer player) {
        applyMatchHoldState(player, HoldAnchor.BATTLEFIELD_WAIT);
    }

    /**
     * 主世界主城：持久维度，不随战场卸载重置。
     * 强制玩家回到 overworld 出生点（维度或坐标不对都会传送）。
     * 合法解除 MatchHold：解锁 + 去失明；非管理员固定为冒险模式。
     */
    public static void applyHubState(ServerPlayer player) {
        forcePlayerToHub(player);
    }

    /**
     * 非管理员位于主世界主城时强制冒险模式（不可自行切生存/创造）。
     * 管理员不强制锁定，但进主城时仍会先设为冒险（见 {@link #applyHubAdventureOnEnter}）。
     */
    public static boolean shouldForceHubAdventure(ServerPlayer player) {
        return player != null
            && player.connection != null
            && !player.hasPermissions(2)
            && Level.OVERWORLD.equals(player.serverLevel().dimension());
    }

    /**
     * 进入主城时：所有人（含管理员）设为冒险模式。
     * 之后仅非管理员被 {@link #shouldForceHubAdventure} 持续锁定。
     */
    public static void applyHubAdventureOnEnter(ServerPlayer player) {
        if (player == null || player.connection == null) {
            return;
        }
        if (!Level.OVERWORLD.equals(player.serverLevel().dimension())) {
            return;
        }
        if (player.gameMode.getGameModeForPlayer() != GameType.ADVENTURE) {
            player.setGameMode(GameType.ADVENTURE);
        }
    }

    /**
     * 持续收口：非管理员在主城若不是冒险则改回冒险。
     * 管理员不在此强制，可自行切换创造等。
     */
    public static void enforceHubAdventure(ServerPlayer player) {
        if (shouldForceHubAdventure(player)
                && player.gameMode.getGameModeForPlayer() != GameType.ADVENTURE) {
            player.setGameMode(GameType.ADVENTURE);
        }
    }

    /**
     * 已实际部署到战场的玩家：通过挖掘等级/可收获判定禁止挖方块，
     * 不再使用挖掘疲劳。真正拦截在 {@link #shouldRestrictBattlefieldMining} 驱动的事件里完成。
     * 此方法只清理旧版疲劳效果，便于从疲劳方案平滑迁移。
     */
    public static void applyBattlefieldMiningRestriction(ServerPlayer player) {
        if (player == null || player.connection == null) {
            return;
        }
        // 清除历史「战场挖掘疲劳」残留（旧版本施加的永久 DIG_SLOWDOWN）。
        player.removeEffect(MobEffects.DIG_SLOWDOWN);
    }

    /** @deprecated use {@link #applyBattlefieldMiningRestriction(ServerPlayer)} */
    @Deprecated
    public static void applyBattlefieldMiningFatigue(ServerPlayer player) {
        applyBattlefieldMiningRestriction(player);
    }

    /**
     * 战场部署/战斗中的非创造、非旁观玩家：视为挖掘等级不足，无法开采任何方块。
     * 服务端权威；客户端用于 BreakSpeed 预判（避免裂纹动画）。
     */
    public static boolean shouldRestrictBattlefieldMining(net.minecraft.world.entity.player.Player player) {
        if (player == null || !player.isAlive()) {
            return false;
        }
        if (player.isSpectator() || player.getAbilities().instabuild) {
            return false;
        }
        if (!isDeployOrBattlePhaseForMining(player)) {
            return false;
        }
        if (player.level() instanceof ServerLevel serverLevel) {
            return BattlefieldContext.isActiveBattlefield(serverLevel);
        }
        // 客户端：若已有 ACTIVE 配置则严格对维度；否则用「非主城」近似（避免裂纹误显）。
        if (BattlefieldContext.isActive()) {
            return BattlefieldContext.isActiveBattlefield(player.level().dimension());
        }
        return !Level.OVERWORLD.equals(player.level().dimension());
    }

    private static boolean isDeployOrBattlePhaseForMining(net.minecraft.world.entity.player.Player player) {
        if (!player.level().isClientSide()) {
            GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
            return phase == GamePhase.DEPLOYING || phase == GamePhase.BATTLE;
        }
        GamePhase phase = net.minecraftforge.fml.DistExecutor.unsafeCallWhenOn(
            net.minecraftforge.api.distmarker.Dist.CLIENT,
            () -> org.espetro.client.gui.ClientGameState::getCurrentPhase);
        return phase == GamePhase.DEPLOYING || phase == GamePhase.BATTLE;
    }

    /**
     * 进服/退服/回城共用：强制进入主城维度出生点，不销毁或重载主城世界。
     */
    public static void forcePlayerToHub(ServerPlayer player) {
        if (player == null || player.connection == null) {
            return;
        }
        player.removeEffect(MobEffects.BLINDNESS);
        player.removeEffect(MobEffects.DIG_SLOWDOWN);
        BastionManager.getInstance().unlockPlayerPosition(player.getUUID());
        BastionManager.getInstance().clearWaiting(player.getUUID());
        // Natural regeneration is disabled globally by Espetro. Without an
        // explicit hub reset, damage saved in playerdata survives reconnects
        // and leaves players permanently below full health in the lobby.
        if (player.isAlive()) {
            player.setHealth(player.getMaxHealth());
            player.clearFire();
            player.setAirSupply(player.getMaxAirSupply());
        }
        ServerLevel hub = player.server.overworld();
        BlockPos spawn = hub.getSharedSpawnPos();
        double x = spawn.getX() + 0.5;
        double y = spawn.getY();
        double z = spawn.getZ() + 0.5;
        // 始终写回重生点，避免下次登录落在已卸载的战场维度。
        player.setRespawnPosition(Level.OVERWORLD, spawn, 0f, true, false);
        boolean wrongDimension = player.serverLevel() != hub;
        boolean farFromSpawn = player.distanceToSqr(x, y, z) > 2.0 * 2.0;
        if (wrongDimension || farFromSpawn) {
            player.teleportTo(hub, x, y, z, 0f, 0f);
        }
        // 回主城：所有人先进冒险；非管理员之后由 changeGameMode 事件持续锁定
        applyHubAdventureOnEnter(player);
        player.setDeltaMovement(0, 0, 0);
        player.fallDistance = 0f;
    }

    /**
     * 等待部署点选择：只要已经选择阵营，就统一以冒险模式、失明并锁在
     * 本方原部署点。首次部署、死亡重新部署与主动重新部署使用同一规则。
     * 尚未选择阵营时仍使用战场高空旁观等待态。
     */
    public static void applyDeploymentWaitingState(ServerPlayer player) {
        applyAdventureDeploymentWaitingState(player);
    }

    /**
     * 首次/死亡/主动重新部署的临时落点。这里只传送和锁定，绝不
     * clearWaiting，因而玩家站在原部署点坐标并不等于已经选择
     * “原部署点”。
     */
    public static void applyAdventureDeploymentWaitingState(ServerPlayer player) {
        if (player == null || player.connection == null) {
            return;
        }
        String team = Espetro.getPlayerTeam(player);
        if (team == null || !BattlefieldContext.isActive()) {
            applyMatchHoldState(player, HoldAnchor.BATTLEFIELD_WAIT);
            return;
        }
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint(team);
        ServerLevel battlefield = BattlefieldContext.requireBattlefield(player.server);
        BastionManager.DeployPoint original =
            BastionManager.getInstance().getPlayerDeployPoint(player.getUUID());
        BlockPos originalPos = original != null && original.pos != null
            ? original.pos
            : new BlockPos((int) spawn.x, (int) spawn.y, (int) spawn.z);
        Vec3 hold = new Vec3(
            originalPos.getX() + 0.5,
            originalPos.getY() + 0.1,
            originalPos.getZ() + 0.5);
        if (player.serverLevel() != battlefield || player.distanceToSqr(hold) > 0.01) {
            player.teleportTo(
                battlefield, hold.x, hold.y, hold.z, spawn.yaw, 0f);
        }
        BastionManager.getInstance().lockPlayerPosition(player.getUUID(), hold);
        enforceAdventureBlindness(player, true);
    }

    /** 已选阵营的部署等待态：冒险、失明、无敌和零速度；不会切换为旁观者。 */
    public static void enforceAdventureBlindness(
            ServerPlayer player, boolean forceClientResync) {
        if (player == null) {
            return;
        }
        if (player.gameMode.getGameModeForPlayer() != GameType.ADVENTURE) {
            player.setGameMode(GameType.ADVENTURE);
        }
        if (forceClientResync) {
            player.removeEffect(MobEffects.BLINDNESS);
            player.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS, Integer.MAX_VALUE, 0, false, false, false));
        } else if (!player.hasEffect(MobEffects.BLINDNESS)) {
            player.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS, Integer.MAX_VALUE, 0, false, false, false));
        }
        if (!player.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
            player.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 127,
                false, false, false));
        }
        player.setDeltaMovement(0, 0, 0);
        player.fallDistance = 0f;
    }
}
