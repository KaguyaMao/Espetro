package org.espetro.vehicle;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.Espetro;
import org.espetro.team.SquadManager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 载具事件处理器
 */
@Mod.EventBusSubscriber(modid = Espetro.MOD_ID)
public class VehicleEventHandler {

    /**
     * 队员的载具认领申请：**按载具排队**（key = 载具 UUID，value = 该载具的申请队列，先申请先处理）。
     * 同一名队员重复申请同一台车只刷新时限，不会重复入队。
     */
    static final Map<UUID, ArrayDeque<PendingClaim>> PENDING_CLAIMS = new ConcurrentHashMap<>();
    private static final long CLAIM_TIMEOUT_MS = 60_000L;
    /** 单台载具最多排队的申请数（防止刷屏/内存膨胀）。 */
    private static final int MAX_CLAIMS_PER_VEHICLE = 8;
    /** 申请序号，用于"先申请先处理"的 FIFO 排序。 */
    private static final AtomicLong CLAIM_SEQ = new AtomicLong();
    private static final Map<UUID, Long> LAST_DENIAL_MESSAGE = new ConcurrentHashMap<>();
    private static final long DENIAL_MESSAGE_INTERVAL_MS = 1_000L;

    record PendingClaim(UUID memberUuid, UUID vehicleUuid, int squadId, String team,
                        long seq, long expiryMs) {}

    /**
     * 提交（或刷新）一条认领申请，并给该小队队长发带可点击按钮的提示。
     */
    private static void submitClaim(ServerPlayer member, Entity vehicle, int squadId,
                                    String team, SquadManager sm) {
        UUID vehicleId = vehicle.getUUID();
        ArrayDeque<PendingClaim> queue = PENDING_CLAIMS.computeIfAbsent(vehicleId, k -> new ArrayDeque<>());
        long expiry = System.currentTimeMillis() + CLAIM_TIMEOUT_MS;
        PendingClaim claim = new PendingClaim(member.getUUID(), vehicleId, squadId, team,
            CLAIM_SEQ.incrementAndGet(), expiry);
        synchronized (queue) {
            queue.removeIf(c -> c.memberUuid().equals(member.getUUID()));   // 同一人重复申请 → 只刷新
            queue.addLast(claim);
            while (queue.size() > MAX_CLAIMS_PER_VEHICLE) queue.pollFirst();
        }

        member.sendSystemMessage(Component.literal("§a已向队长申请认领该载具"));
        notifyLeaderClaim(sm, member, vehicle, vehicleId, team, squadId);
    }

    /** 给队长发：§a队员申请使用&lt;载具名&gt;，[通过][否决]（按钮点击直接执行命令）。 */
    private static void notifyLeaderClaim(SquadManager sm, ServerPlayer member, Entity vehicle,
                                          UUID vehicleId, String team, int squadId) {
        UUID leaderUuid = sm.getSquadLeaderUuid(team, squadId);
        if (leaderUuid == null) return;
        ServerPlayer leader = member.serverLevel().getServer().getPlayerList().getPlayer(leaderUuid);
        if (leader == null) return;

        String vehicleName = getVehicleDisplayName(vehicle);
        Component passButton = Component.literal("[通过]").withStyle(Style.EMPTY
            .withColor(ChatFormatting.GREEN)
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/veh pass " + vehicleId))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                Component.literal("点击通过：" + vehicleName))));
        Component denyButton = Component.literal("[否决]").withStyle(Style.EMPTY
            .withColor(ChatFormatting.RED)
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/veh passno " + vehicleId))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                Component.literal("点击否决：" + vehicleName))));

        leader.sendSystemMessage(Component.literal("§a队员申请使用" + vehicleName + "，")
            .append(passButton)
            .append(Component.literal(" "))
            .append(denyButton));
    }

    /**
     * 取出队首（最早申请、未过期、属于该小队）的认领申请并移除。
     *
     * @param vehicleId 指定载具；传 null 表示该小队名下最早的一条申请（任意载具）
     */
    static PendingClaim pollClaim(@javax.annotation.Nullable UUID vehicleId, int squadId) {
        long now = System.currentTimeMillis();
        List<UUID> candidates = vehicleId != null
            ? List.of(vehicleId) : new ArrayList<>(PENDING_CLAIMS.keySet());

        PendingClaim best = null;
        UUID bestVehicle = null;
        for (UUID vid : candidates) {
            ArrayDeque<PendingClaim> queue = PENDING_CLAIMS.get(vid);
            if (queue == null) continue;
            synchronized (queue) {
                queue.removeIf(c -> c.expiryMs() < now);   // 顺手清理过期申请
                for (PendingClaim c : queue) {
                    if (c.squadId() != squadId) continue;
                    if (best == null || c.seq() < best.seq()) {
                        best = c;
                        bestVehicle = vid;
                    }
                    break;   // 该载具只需要最早的一条
                }
                if (queue.isEmpty()) PENDING_CLAIMS.remove(vid);
            }
            if (vehicleId != null) break;
        }
        if (best == null || bestVehicle == null) return null;

        ArrayDeque<PendingClaim> queue = PENDING_CLAIMS.get(bestVehicle);
        if (queue != null) {
            synchronized (queue) {
                queue.remove(best);
                if (queue.isEmpty()) PENDING_CLAIMS.remove(bestVehicle);
            }
        }
        return best;
    }

    /**
     * 清除某名玩家的全部待处理认领申请（跳边 / 观战 / 离队 / 对局重置时调用）。
     * 否则队长换队后仍会收到该队员在旧队伍的认领按钮，点"通过"会把载具
     * 归属给一个已经不存在的编制。
     */
    public static void clearClaimsFor(UUID memberUuid) {
        if (memberUuid == null) return;
        for (Map.Entry<UUID, ArrayDeque<PendingClaim>> entry : PENDING_CLAIMS.entrySet()) {
            ArrayDeque<PendingClaim> queue = entry.getValue();
            synchronized (queue) {
                queue.removeIf(c -> c.memberUuid().equals(memberUuid));
                if (queue.isEmpty()) PENDING_CLAIMS.remove(entry.getKey(), queue);
            }
        }
    }

    /** 清空全部认领申请（对局重置 / 地图切换）。 */
    public static void clearAllClaims() {
        PENDING_CLAIMS.clear();
    }

    /**
     * 载具死亡时从追踪中移除
     */
    @SubscribeEvent
    public static void onVehicleDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();

        if (entity.getTags().contains("espetro_vehicle")) {
            VehicleManager.getInstance().onVehicleDeath(entity.getUUID());
            Espetro.LOGGER.debug("载具 {} 已死亡，移除追踪", entity.getUUID());
        }

    }

    @SubscribeEvent
    public static void onVehicleLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        Entity entity = event.getEntity();
        if (VehicleManager.isMappedSupplyStation(entity)
                && (entity.getRemovalReason() == Entity.RemovalReason.KILLED
                    || entity.getRemovalReason() == Entity.RemovalReason.DISCARDED)) {
            VehicleManager.getInstance().unregisterMappedSupplyStation(entity.getUUID());
            org.espetro.bastion.FortificationManager.getInstance()
                .removeEntity(entity.getUUID());
        }
        if (entity.getTags().contains("espetro_vehicle")) {
            VehicleManager.getInstance().updateVehicleLocation(entity);
            Espetro.LOGGER.info("载具 {} 离开已加载世界（reason={}），追踪保留", entity.getUUID(), entity.getRemovalReason());
            if (entity.getRemovalReason() == Entity.RemovalReason.KILLED) {
                VehicleManager.getInstance().onVehicleDeath(entity.getUUID());
                Espetro.LOGGER.debug("载具 {} 已被杀毁，移除追踪并处理兵力扣除", entity.getUUID());
            } else if (entity.getRemovalReason() == Entity.RemovalReason.DISCARDED) {
                if (isDestroyedSbwVehicle(entity)) {
                    // SBW destroys vehicles by entering wreck state and finally calling discard().
                    // This is a fallback for the destroy() mixin and remains idempotent when the
                    // mixin has already registered the loss.
                    VehicleManager.getInstance().onVehicleDeath(entity.getUUID());
                    Espetro.LOGGER.debug("载具 {} 残骸已移除，确保自动刷新已登记", entity.getUUID());
                } else {
                    VehicleManager.getInstance().onVehicleRemoved(entity.getUUID());
                    Espetro.LOGGER.info("载具 {} 被 DISCARDED 移除（reason={}）—— 会清除追踪且【不排重生】", entity.getUUID(), entity.getRemovalReason());
                }
            } else {
                Espetro.LOGGER.debug("载具 {} 暂时离开已加载世界，保留停服清理追踪", entity.getUUID());
            }
        }

    }

    @SubscribeEvent
    public static void onVehicleJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        Entity entity = event.getEntity();
        if (entity.getTags().contains("espetro_vehicle")) {
            VehicleManager.getInstance().updateVehicleLocation(entity);
        }
    }

    /**
     * 新生成的 ammo_supply_station（非读档、尚未标记）→ 绑定放置者队伍，
     * 使 ESPoints SyncBastions / 战术地图显示该载具补给站。
     */
    @SubscribeEvent
    public static void onSupplyStationJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getLevel() instanceof ServerLevel)) return;

        Entity entity = event.getEntity();
        if (!VehicleManager.isAmmoSupplyStationEntity(entity)) return;
        // 仅 Espetro“建造工事”产生、带持久映射标签的实体可进入战术地图。
        if (VehicleManager.isMappedSupplyStation(entity)) {
            VehicleManager.getInstance().registerMappedSupplyStation(entity);
        }
    }

    // 载具部署木棍已移除；载具部署入口在 Alt 轮盘（DEPLOY_VEHICLE）。

    // ======================== 载具小队归属 ========================

    /** 检查实体是否为 SBW VehicleEntity（通过类名判断，不直接依赖 superbwarfare 模组）。 */
    private static boolean isSbwVehicle(Entity entity) {
        Class<?> clazz = entity.getClass();
        while (clazz != null) {
            if ("com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity".equals(clazz.getName())) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    /** SBW uses its own wreck flag instead of vanilla living-entity death state. */
    private static boolean isDestroyedSbwVehicle(Entity entity) {
        if (!isSbwVehicle(entity)) return false;
        try {
            return Boolean.TRUE.equals(entity.getClass().getMethod("isWreck").invoke(entity));
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return false;
        }
    }

    private static String getVehicleDisplayName(Entity vehicle) {
        Component custom = vehicle.getCustomName();
        return custom != null ? custom.getString() : vehicle.getType().getDescription().getString();
    }

    /**
     * 玩家右键载具时检查小队归属规则。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onVehicleEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Entity target = event.getTarget();
        if (!isSbwVehicle(target)) return;

        // 白名单载具：不拦截、不取消，让 SBW 原生 VehicleEntity.interact 全功能回归
        // （上车/开集装箱/撬棍回收/命名牌/C4）。
        if (VehicleNativeWhitelist.isNative(target)) return;

        if (!isMountAllowed(player, target)) {
            event.setCanceled(true);
        }
    }

    /**
     * 载具小队归属准入检查（对局中生效）。
     * <p>同时被右键交互（{@link #onVehicleEntityInteract}）与读条上车通道
     * （{@code VehicleMountServer.tryMount}）调用，防止非队长成员绕过交互拦截
     * 直接上未认领/非本队的载具。主城阶段放行（原版 SBW 交互不受限制）。</p>
     *
     * @return true = 允许上车；false = 拒绝（已向队长发起认领申请）
     */
    public static boolean isMountAllowed(ServerPlayer player, Entity vehicle) {
        // 白名单载具：完全豁免本模组的上车准入（敌对阵营/小队归属/组员座位等）。
        if (VehicleNativeWhitelist.isNative(vehicle)) return true;
        if (player == null || !isSbwVehicle(vehicle)) return true;
        // 主城阶段放行：使用原版 SBW 上车，不限制。
        if (org.espetro.team.GameStateManager.getInstance().getCurrentPhase().isLobbyLike()) {
            return true;
        }

        String team = Espetro.getPlayerTeam(player);
        if (team == null) return true; // 不在阵营中的玩家不受限制

        team = team.toUpperCase();

        // 0) 敌对阵营：无论如何都不能登（车上有人也不行、不能抢占、不能改成自己小队）
        if (isEnemyVehicle(player, vehicle)) {
            notifyEnemyVehicle(player);
            return false;
        }

        SquadManager sm = SquadManager.getInstance();
        int playerSquad = sm.getPlayerSquadId(player.getUUID());
        boolean isLeader = sm.isSquadLeader(player.getUUID());

        int vehicleSquad = VehicleSquadOwnership.getSquadId(vehicle);
        boolean vehicleOwned = vehicleSquad != -1;
        boolean vehicleHasPassengers = !vehicle.getPassengers().isEmpty();

        // 1) 载具上有人 → 同阵营任何人可登（敌方已在上面拦掉）
        if (vehicleHasPassengers) {
            return true;
        }

        // 2) 载具无人 → 按归属规则判断
        if (!vehicleOwned) {
            // 无归属：只有小队长可登（自动归属），小队员触发申请
            if (isLeader) {
                return true; // 允许交互，挂载时自动归属
            }
            if (playerSquad != SquadManager.NO_SQUAD) {
                submitClaim(player, vehicle, playerSquad, team, sm);
            }
            return false;
        }

        // 已归属且无人
        if (playerSquad == vehicleSquad) {
            // 本队队员可直接登（包含队长）
            return true;
        }

        if (isLeader) {
            // 别队队长登空载具 → 允许交互，挂载时重新归属
            return true;
        }

        if (playerSquad != SquadManager.NO_SQUAD) {
            // 别队小队员 → 触发申请
            submitClaim(player, vehicle, playerSquad, team, sm);
        }
        return false;
    }

    /**
     * 载具是否与玩家敌对（用于锁车：敌人无论如何都不能登上/操作己方载具）。
     * 载具阵营无法判定时视为中立，返回 false。
     */
    public static boolean isEnemyVehicle(ServerPlayer player, Entity vehicle) {
        if (player == null || vehicle == null) return false;
        String playerTeam = Espetro.getPlayerTeam(player);
        if (playerTeam == null) return false;
        String vehicleTeam = resolveVehicleTeam(vehicle);
        return vehicleTeam != null && !vehicleTeam.equalsIgnoreCase(playerTeam.trim());
    }

    /**
     * 解析载具所属阵营（用于敌我锁车）：优先小队归属阵营，其次生成时写入的持久标签，
     * 最后回退到运行期追踪表。无法判定时返回 null（按"中立"处理）。
     */
    private static String resolveVehicleTeam(Entity vehicle) {
        String owned = VehicleSquadOwnership.getSquadTeam(vehicle);
        if (owned != null && !owned.isBlank()) return owned.toUpperCase();
        var data = vehicle.getPersistentData();
        if (data.contains(VehicleManager.VEHICLE_TEAM_KEY, net.minecraft.nbt.Tag.TAG_STRING)) {
            String tagged = data.getString(VehicleManager.VEHICLE_TEAM_KEY);
            if (!tagged.isBlank()) return tagged.toUpperCase();
        }
        String tracked = VehicleManager.getInstance().getVehicleTeam(vehicle.getUUID());
        return tracked != null ? tracked.toUpperCase() : null;
    }

    /** 敌方载具提示（1 秒限流，避免连点刷屏）。 */
    public static void notifyEnemyVehicle(ServerPlayer player) {
        long now = System.currentTimeMillis();
        Long previous = LAST_DENIAL_MESSAGE.put(player.getUUID(), now);
        if (previous != null && now - previous < DENIAL_MESSAGE_INTERVAL_MS) return;
        player.displayClientMessage(Component.literal("§c这是敌方载具，无法登上。"), true);
    }

    /**
     * 小队长登上载具时自动归属 / 重新归属。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onVehicleMount(EntityMountEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!event.isMounting()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Entity vehicle = event.getEntityBeingMounted();
        if (!isSbwVehicle(vehicle)) return;

        // 主城阶段不进行小队归属：主城使用原版 SBW 上车（测试/自由使用），
        // 不应污染对局中的载具归属状态。
        if (org.espetro.team.GameStateManager.getInstance().getCurrentPhase().isLobbyLike()) {
            return;
        }

        SquadManager sm = SquadManager.getInstance();
        if (!sm.isSquadLeader(player.getUUID())) return;

        int playerSquad = sm.getPlayerSquadId(player.getUUID());
        if (playerSquad == SquadManager.NO_SQUAD) return;

        String team = Espetro.getPlayerTeam(player);
        if (team == null) return;
        team = team.toUpperCase();

        int vehicleSquad = VehicleSquadOwnership.getSquadId(vehicle);
        String vehicleSquadTeam = VehicleSquadOwnership.getSquadTeam(vehicle);
        if (vehicleSquad == -1) {
            // 无归属 → 自动归属
            VehicleSquadOwnership.setOwner(vehicle, playerSquad, team);
            return;
        }

        // 已归属但属于其他小队 → 重新归属（仅当载具无人时才会走到这里，
        // 因为有人时的交互已在 onVehicleEntityInteract 中放行且不触发重归属）
        if (vehicleSquad != playerSquad) {
            // 通知原队长
            if (vehicleSquadTeam != null) {
                UUID oldLeaderUuid = sm.getSquadLeaderUuid(vehicleSquadTeam, vehicleSquad);
                if (oldLeaderUuid != null) {
                    ServerPlayer oldLeader = player.serverLevel().getServer().getPlayerList()
                        .getPlayer(oldLeaderUuid);
                    if (oldLeader != null) {
                        String vehicleName = getVehicleDisplayName(vehicle);
                        String newSquadName = sm.getSquadName(team, playerSquad);
                        if (newSquadName == null) newSquadName = "未知小队";
                        oldLeader.sendSystemMessage(Component.literal(
                            "§c您空闲的载具" + vehicleName + "已被" + newSquadName + "认领"));
                    }
                }
            }
            VehicleSquadOwnership.setOwner(vehicle, playerSquad, team);
        }
    }
}
