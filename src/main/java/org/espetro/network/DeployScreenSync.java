package org.espetro.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.espetro.Espetro;
import org.espetro.config.GameConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.team.*;
import org.espetro.network.core.PacketRegistrar;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 部署阶段的服务端同步：统一部署界面（含分帧发送队列）、复活点、载具部署、
 * 装备区与职业人数。
 */
public final class DeployScreenSync {

    private DeployScreenSync() {
    }

    private static final int FULL_DEPLOY_PACKETS_PER_TICK = 8;

    private static final Map<UUID, QueuedDeployScreen> QUEUED_FULL_DEPLOY_SCREENS = new LinkedHashMap<>();

    public static void queueUnifiedDeployScreen(ServerPlayer player, int deployTimeRemaining) {
        queueUnifiedDeployScreen(player, deployTimeRemaining, false);
    }

    public static void queueUnifiedDeployScreen(ServerPlayer player, int deployTimeRemaining,
                                                 boolean playEntryAudio) {
        if (player != null) {
            QUEUED_FULL_DEPLOY_SCREENS.put(player.getUUID(),
                new QueuedDeployScreen(deployTimeRemaining, playEntryAudio));
        }
    }

    public static void drainQueuedFullScreens() {
        MinecraftServer server = Espetro.getServer();
        if (server == null || QUEUED_FULL_DEPLOY_SCREENS.isEmpty()) {
            return;
        }
        int sent = 0;
        var iterator = QUEUED_FULL_DEPLOY_SCREENS.entrySet().iterator();
        while (iterator.hasNext() && sent < FULL_DEPLOY_PACKETS_PER_TICK) {
            Map.Entry<UUID, QueuedDeployScreen> entry = iterator.next();
            iterator.remove();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player != null) {
                QueuedDeployScreen queued = entry.getValue();
                sendUnifiedDeployScreen(player, queued.deployTimeRemaining());
                if (queued.playEntryAudio()) {
                    org.espetro.audio.FactionAudioCoordinator.sendEntry(player);
                }
                sent++;
            }
        }
    }

    public static void clearQueuedFullScreens() {
        QUEUED_FULL_DEPLOY_SCREENS.clear();
    }

    private record QueuedDeployScreen(int deployTimeRemaining, boolean playEntryAudio) {
    }

    /** 同步同阵营换装黄框区域。 */
    public static void sendEquipZones(ServerPlayer player) {
        if (player == null) return;
        List<EquipZoneSyncPacket.Zone> zones =
            org.espetro.team.ClassEquipmentZones.collectForPlayer(player);
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), new EquipZoneSyncPacket(zones));
    }

    /** 重发部署界面（不打开面板），用于开关状态变化后让界面立刻按新规则重算。 */
    public static void resendDeployScreen(ServerPlayer player) {
        if (player == null) return;
        sendUnifiedDeployScreen(player, -1, false);
    }

    /** 向玩家发送载具职业选择（不依赖 Radio，战斗载具专用） */
    public static void sendVehicleClassSelect(ServerPlayer player, String factionId) {
        sendVehicleClassSelect(player, factionId, false);
    }

    /**
     * 向玩家发送载具职业选择。
     *
     * @param crewOnly true 时只下发载具组员（驾驶员）与载具队长，用于补给载具与无标签载具；
     *                 显式 {@code fightveh: true} 的步兵战斗载具传 false，保持完整职业列表。
     *                 只是过滤同一个 {@link RadioRadialPacket#classList} 的元素，协议字段不变。
     */
    public static void sendVehicleClassSelect(ServerPlayer player, String factionId, boolean crewOnly) {
        if (factionId == null) return;
        var loader = org.espetro.team.FactionDataProvider.getOrCreateLoader();
        var server = player.getServer();
        if (server != null) loader.ensureLoaded(server.getResourceManager());
        var kits = loader.getClassesForFaction(factionId);
        var counts = org.espetro.team.ClassCountManager.getInstance();
        String team = counts.getEffectivePlayerTeam(player.getUUID());
        int squadId = org.espetro.team.SquadManager.getInstance().getPlayerSquadId(player.getUUID());
        boolean inSquad = squadId != org.espetro.team.SquadManager.NO_SQUAD;
        int squadSize = inSquad
            ? org.espetro.team.SquadManager.getInstance().getSquadMemberUuids(team, squadId).size()
            : 0;
        int cooldown = counts.getClassSwitchCooldownRemaining(player.getUUID());
        boolean freeUnlock = org.espetro.team.FreeUnlockManager.isEnabled();
        var list = new java.util.ArrayList<RadioRadialPacket.ClassEntry>();
        if (kits != null) {
            for (var kit : kits) {
                if (kit == null) continue;
                if (crewOnly
                    && !org.espetro.vehicle.VehicleClassChangePolicy.isVehicleCrewClass(kit)) {
                    continue;
                }
                int squadCount = counts.getSquadClassCountForViewer(
                    player.getUUID(), team, kit.id);
                int maxCount = kit.teamCount
                    ? Math.max(1, kit.maxPlayers)
                    : kit.maxPerSquad > 0 ? kit.maxPerSquad : Math.max(1, kit.maxPlayers);
                int teamCount = counts.getCount(team, kit.id);
                String denial = "";
                boolean cooldownBlocked = cooldown > 0;
                if (cooldownBlocked) {
                    denial = "职业切换冷却中，还需等待 " + cooldown + " 秒。";
                } else if (!inSquad) {
                    denial = "请先加入班组小队后再选择职业。";
                } else if (!freeUnlock && kit.teammatesNeed > 0 && squadSize < kit.teammatesNeed) {
                    denial = "小队达到 " + kit.teammatesNeed + " 人后才能选择该职业。";
                } else if (!freeUnlock && kit.teamCount && squadCount >= kit.maxPlayers) {
                    denial = "本小队该职业人数已满（" + squadCount + "/" + kit.maxPlayers + "）。";
                } else if (!freeUnlock && !kit.teamCount && teamCount >= kit.maxPlayers) {
                    denial = "该职业全队人数已满（" + teamCount + "/" + kit.maxPlayers + "）。";
                } else if (!freeUnlock && !kit.teamCount && kit.maxPerSquad > 0
                    && squadCount >= kit.maxPerSquad) {
                    denial = "本小队该职业人数已满（" + squadCount + "/" + kit.maxPerSquad + "）。";
                }
                boolean enabled = denial.isEmpty();
                var variants = new java.util.ArrayList<RadioRadialPacket.VariantEntry>();
                String defaultVariantId = "";
                if (kit.variants != null) {
                    var defaultVariant = kit.variants.get("default");
                    if (defaultVariant != null) defaultVariantId = defaultVariant.id;
                    else if (!kit.variants.isEmpty()) defaultVariantId = kit.variants.values().iterator().next().id;
                    for (var variant : kit.variants.values()) {
                        int variantCount = kit.teamCount
                            ? counts.countVariantInSquad(team, squadId, kit.id, variant.id)
                            : counts.getVariantCount(team, kit.id, variant.id);
                        boolean variantEnabled = enabled
                            && (freeUnlock || !kit.strictCount || variantCount < variant.maxPlayers);
                        String variantDenial = denial;
                        if (enabled && !variantEnabled)
                            variantDenial = "该装备变体人数已满（" + variantCount + "/" + variant.maxPlayers + "）。";
                        variants.add(new RadioRadialPacket.VariantEntry(
                            variant.id, variant.name, variantCount, variant.maxPlayers,
                            kit.strictCount, variantEnabled, variantDenial));
                    }
                }
                String icon = kit.icon != null ? kit.icon : "";
                String iconImage = kit.iconImage != null ? kit.iconImage : "";
                list.add(new RadioRadialPacket.ClassEntry(
                    kit.id, kit.name, icon, iconImage,
                    defaultVariantId, squadCount, maxCount,
                    true, enabled, cooldownBlocked, denial, variants));
            }
        }
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), RadioRadialPacket.classList(list));
    }

    private static void sendRadioClassList(ServerPlayer player, net.minecraft.core.BlockPos radioPos) {
        // 打开客户端 RadioRadialController，复用其职业选择逻辑
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
            RadioRadialPacket.openRequest(radioPos != null ? radioPos : net.minecraft.core.BlockPos.ZERO));
    }

    public static void broadcastEquipZonesForTeam(String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || team == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (team.equals(Espetro.getPlayerTeam(player))) {
                sendEquipZones(player);
            }
        }
    }

    /**
     * 向同队在线玩家刷新部署面板数据（openScreen=false，不强制弹窗）。
     * <p>
     * 必须带上每人的 {@code squadCurrentCount} 等小队作用域字段，否则 J 面板灰显/人数错误。
     * 装备预览走 {@link ClassLoadoutPreviewResolver} 缓存，避免旧版「N×ItemParser」尖峰；
     * 仍比无缓存的 full 风暴轻一个数量级。打开面板仍用 {@link #sendUnifiedDeployScreen}。
     */
    public static void refreshUnifiedDeployScreensForTeam(String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || team == null) {
            return;
        }
        int remaining = -1;
        if (org.espetro.team.GameStateManager.getInstance().getCurrentPhase()
            == org.espetro.team.GamePhase.DEPLOYING) {
            remaining = org.espetro.team.GameStateManager.getInstance()
                .getDeployTimeRemainingSeconds();
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (team.equals(Espetro.getPlayerTeam(player))) {
                // 预览已缓存：主要成本是组包；保证小队人数/满员态正确
                syncUnifiedDeployScreen(player, remaining);
            }
        }
    }

    public static void broadcastClassCounts(String team, String factionId) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || team == null || factionId == null || factionId.isBlank()) return;

        ClassCountManager countManager = ClassCountManager.getInstance();
        java.util.Map<String, Integer> counts = countManager.getCountsForFaction(team, factionId);
        java.util.Map<Integer, ClassCountSyncPacket> packetsBySquad = new java.util.HashMap<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (team.equals(countManager.getEffectivePlayerTeam(player.getUUID()))) {
                int squadId = SquadManager.getInstance().getPlayerSquadId(player.getUUID());
                ClassCountSyncPacket packet = packetsBySquad.computeIfAbsent(squadId, ignored ->
                    new ClassCountSyncPacket(
                        counts,
                        countManager.getSquadCountsForViewer(player.getUUID(), team, factionId),
                        countManager.getVariantCountsForViewer(player.getUUID(), team, factionId),
                        factionId));
                NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
            }
        }
    }

    /**
     * 向玩家发送只读载具信息界面
     */
    public static void sendVehicleDeployScreen(ServerPlayer player, String factionId) {
        sendVehicleDeployScreen(player, factionId, true);
    }

    public static void syncVehicleDeployScreen(ServerPlayer player, String factionId) {
        sendVehicleDeployScreen(player, factionId, false);
    }

    private static void sendVehicleDeployScreen(ServerPlayer player, String factionId,
                                                boolean openScreen) {
        java.util.Map<String, org.espetro.vehicle.VehicleConfig.VehicleTypeConfig> configs =
            org.espetro.vehicle.VehicleConfig.getFactionVehicles(factionId);
        java.util.List<VehicleDeployScreenPacket.VehicleInfo> list = new java.util.ArrayList<>();

        org.espetro.vehicle.VehicleManager vm = org.espetro.vehicle.VehicleManager.getInstance();
        for (java.util.Map.Entry<String, org.espetro.vehicle.VehicleConfig.VehicleTypeConfig> entry : configs.entrySet()) {
            String type = entry.getKey();
            org.espetro.vehicle.VehicleConfig.VehicleTypeConfig cfg = entry.getValue();
            String team = Espetro.getPlayerTeam(player);
            int current = team == null ? vm.getActiveCount(factionId, type)
                : vm.getActiveCount(team, factionId, type);
            long cooldown = vm.getCooldownRemaining(team, factionId, type);
            String displayName = org.espetro.vehicle.VehicleManager.getDisplayName(factionId, type);

            list.add(new VehicleDeployScreenPacket.VehicleInfo(
                type, displayName, cfg.max, current,
                System.currentTimeMillis() + cooldown, cfg.respawnMinutes));
        }

        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
            new VehicleDeployScreenPacket(openScreen, list));
    }

    /**
     * 旧复活点选择入口的兼容转发：统一改为发送 mutil 部署面板。
     */
    public static void sendDeployPointSelectScreen(ServerPlayer player) {
        sendUnifiedDeployScreen(player, -1);
    }

    /**
     * 发送统一部署主界面给指定玩家
     * 集成：职业选择、复活点选择、载具部署、小队选择、地图
     */
    public static void sendUnifiedDeployScreen(ServerPlayer player, int deployTimeRemaining) {
        sendUnifiedDeployScreen(player, deployTimeRemaining, true);
    }

    /**
     * 静默同步统一部署数据。面板已经打开时就地刷新；面板关闭时不得主动弹出。
     */
    public static void syncUnifiedDeployScreen(ServerPlayer player, int deployTimeRemaining) {
        sendUnifiedDeployScreen(player, deployTimeRemaining, false);
    }

    private static void sendUnifiedDeployScreen(
            ServerPlayer player, int deployTimeRemaining, boolean openScreen) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        // The same packet backs the main GUI in both deployment and battle. Always
        // attach the current authoritative phase timer so opening J mid-battle does
        // not render an empty timer until a later BattleTimerPacket arrives.
        GameStateManager gameState = GameStateManager.getInstance();
        int phaseTimeRemaining = switch (gameState.getCurrentPhase()) {
            case DEPLOYING -> gameState.getDeployTimeRemainingSeconds();
            case BATTLE -> gameState.getBattleTimeRemainingSeconds();
            default -> deployTimeRemaining;
        };

        String team = Espetro.getPlayerTeam(player);
        if (team == null) return;

        VoteManager voteManager = VoteManager.getInstance();
        ClassSelectManager selectManager = ClassSelectManager.getInstance();
        org.espetro.bastion.BastionManager bm = org.espetro.bastion.BastionManager.getInstance();
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();

        String factionId = "ATTACK".equals(team)
            ? selectManager.getFinalAttackClass()
            : selectManager.getFinalDefendClass();
        if (factionId == null) factionId = team;

        // === 职业数据 ===
        FactionDataLoader.FactionData factionData = loader.getFaction(factionId);
        String factionName = factionData != null ? factionData.name : factionId;
        String factionDesc = factionData != null ? factionData.description : "";
        String factionIcon = factionData != null ? (factionData.icon != null ? factionData.icon : "") : "";

        java.util.List<UnifiedDeployScreenPacket.ClassInfo> classList = new java.util.ArrayList<>();
        java.util.Map<String, Integer> classCountMap = new java.util.HashMap<>();
        FactionDataLoader.ClassKitData[] kits = loader.getClassesForFaction(factionId);
        if (kits != null) {
            ClassCountManager counts = ClassCountManager.getInstance();
            boolean isLeader = SquadManager.getInstance().isSquadLeader(player.getUUID());
            for (FactionDataLoader.ClassKitData kit : kits) {
                // leaderOnly: 非队长不显示，后续向前补位
                if (kit.leaderOnly && !isLeader) continue;
                int count = counts.getEffectiveClassCountForViewer(player.getUUID(), team, kit.id);
                int squadCount = counts.getSquadClassCountForViewer(player.getUUID(), team, kit.id);
                java.util.List<UnifiedDeployScreenPacket.VariantInfo> variants = new java.util.ArrayList<>();
                if (kit.variants != null) {
                    for (FactionDataLoader.ClassVariantData variant : kit.variants.values()) {
                        int vCount = kit.teamCount
                            ? counts.countVariantInSquad(team,
                                org.espetro.team.SquadManager.getInstance().getPlayerSquadId(player.getUUID()),
                                kit.id, variant.id)
                            : counts.getVariantCount(team, kit.id, variant.id);
                        // 服务端权威装备预览：解析装备/命令配置为 6 槽位 ItemStack，
                        // 客户端无需解析命令即可渲染人物模型。
                        ClassLoadoutPreviewResolver.Preview preview =
                            ClassLoadoutPreviewResolver.resolve(server, kit, variant);
                        variants.add(new UnifiedDeployScreenPacket.VariantInfo(
                            variant.id, variant.name, variant.description, variant.maxPlayers, vCount,
                            new UnifiedDeployScreenPacket.LoadoutPreview(
                                preview.head, preview.chest, preview.legs, preview.feet,
                                preview.mainHand, preview.offHand)));
                    }
                }
                classList.add(new UnifiedDeployScreenPacket.ClassInfo(
                    kit.id, kit.name, kit.description, kit.role, kit.icon, kit.iconImage,
                    kit.maxPlayers, kit.strictCount, count, kit.troopValue, kit.healthBonus, kit.speedBonus,
                    kit.teamCount, kit.maxPerSquad, squadCount,
                    Math.max(0, kit.teammatesNeed), kit.row, kit.unlockPerN, kit.unlockMinSquad, kit.leaderOnly,
                    variants
                ));
                classCountMap.put(kit.id, count);
            }
        }

        // === 复活点 / 部署点数据 ===
        boolean hasDeploy = false;
        String deployPos = "";
        org.espetro.bastion.BastionManager.DeployPoint dp = bm.getPlayerDeployPoint(player.getUUID());
        if (dp != null && dp.pos != null) {
            hasDeploy = true;
            deployPos = dp.pos.getX() + ", " + dp.pos.getY() + ", " + dp.pos.getZ();
        }

        java.util.List<UnifiedDeployScreenPacket.BastionItem> bastionList = new java.util.ArrayList<>();
        // 列出全部己方 HAB（含启用倒计时/无覆盖等），状态文案供 UI 展示；选择时再 isHabOperational
        for (org.espetro.bastion.BastionData bd : bm.getTeamBastions(team)) {
            net.minecraft.core.BlockPos armorStandPos = bm.getRecordedArmorStandPosition(bd);
            if (armorStandPos == null) {
                continue;
            }
            bastionList.add(new UnifiedDeployScreenPacket.BastionItem(
                bd.getBastionId(), bd.getName(),
                armorStandPos.getX() + ", " + armorStandPos.getY() + ", " + armorStandPos.getZ(),
                UnifiedDeployScreenPacket.BastionItem.TYPE_HAB,
                bm.getFobStatus(bd),
                0L, 0,
                bd.getHabAvailableAt(),
                org.espetro.logistics.LogisticsConfig.get().habActivationSeconds
            ));
        }
        bastionList.addAll(org.espetro.team.TeamPackManager.getInstance().getDeployItemsForPlayer(player));

        // AAS 防守方在部署阶段可使用前哨基地；RAAS 双方都不可用。
        if (org.espetro.team.OutpostManager.getInstance().canListFor(team)) {
            var outposts = org.espetro.team.OutpostManager.getInstance().getOutposts();
            for (int i = 0; i < outposts.size(); i++) {
                var op = outposts.get(i);
                // 用特殊 UUID 标记前哨基地：MSB=0, LSB=index+1
                bastionList.add(new UnifiedDeployScreenPacket.BastionItem(
                    new java.util.UUID(0L, i + 1L),
                    "§d前哨: " + op.name,
                    op.getPosString()
                ));
            }
        }

        // 载具运行时状态：面板内「载具」上拉菜单的数据源。
        // 与 sendVehicleDeployScreen 使用同一套 API；cooldownRemaining 沿用该字段的
        // 历史单位（秒），不改协议字段的顺序与含义。
        java.util.List<UnifiedDeployScreenPacket.VehicleInfo> vehicleList = new java.util.ArrayList<>();
        org.espetro.vehicle.VehicleManager vehicleManager =
            org.espetro.vehicle.VehicleManager.getInstance();
        java.util.Map<String, org.espetro.vehicle.VehicleConfig.VehicleTypeConfig> vehicleConfigs =
            org.espetro.vehicle.VehicleConfig.getFactionVehicles(factionId);
        for (java.util.Map.Entry<String, org.espetro.vehicle.VehicleConfig.VehicleTypeConfig> vehicleEntry
                : vehicleConfigs.entrySet()) {
            String vehicleType = vehicleEntry.getKey();
            org.espetro.vehicle.VehicleConfig.VehicleTypeConfig vehicleCfg = vehicleEntry.getValue();
            int vehicleCurrent = team == null
                ? vehicleManager.getActiveCount(factionId, vehicleType)
                : vehicleManager.getActiveCount(team, factionId, vehicleType);
            long vehicleCooldownMs = vehicleManager.getCooldownRemaining(team, factionId, vehicleType);
            vehicleList.add(new UnifiedDeployScreenPacket.VehicleInfo(
                vehicleType,
                org.espetro.vehicle.VehicleManager.getDisplayName(factionId, vehicleType),
                vehicleCfg.max,
                vehicleCurrent,
                (int) (vehicleCooldownMs / 1000L),
                vehicleCfg.respawnMinutes));
        }

        boolean isCmd = voteManager.isCommanderOf(player.getUUID(), team);

        // === 小队数据 ===
        java.util.List<UnifiedDeployScreenPacket.SquadInfo> squadList = SquadSync.buildSquadInfoList(team);
        java.util.List<String> commanderNames = SquadSync.getCommanderNames(team);
        int mySquadId = SquadManager.getInstance().getPlayerSquadId(player.getUUID());
        java.util.List<UnifiedDeployScreenPacket.SquadCategoryInfo> squadCategories =
            new java.util.ArrayList<>();
        org.espetro.mapconfig.ActiveMapConfig activeMap =
            org.espetro.mapconfig.BattlefieldContext.getOrNull();
        org.espetro.mapconfig.SquadTypesSnapshot types = activeMap != null
            ? activeMap.squadTypes : org.espetro.mapconfig.SquadTypesSnapshot.defaults();
        for (org.espetro.mapconfig.SquadTypesSnapshot.Category category : types.categories) {
            squadCategories.add(new UnifiedDeployScreenPacket.SquadCategoryInfo(
                category.id(), category.displayName()));
        }

        UnifiedDeployScreenPacket packet = new UnifiedDeployScreenPacket(
            factionId, factionName, factionDesc, factionIcon,
            classList, classCountMap,
            hasDeploy, deployPos, bastionList,
            isCmd, vehicleList,
            squadList, mySquadId,
            phaseTimeRemaining, team,
            commanderNames, GameConfig.getTeammateNameTagDistance(),
            bm.isWaitingForBastion(player.getUUID()),
            org.espetro.team.OutpostManager.getInstance()
                .getRedeployCooldownRemaining(player.getUUID()),
            squadCategories,
            ClassCountManager.getInstance()
                .getClassSwitchCooldownRemaining(player.getUUID()),
            openScreen,
            java.util.Objects.toString(
                ClassCountManager.getInstance().getPlayerClass(player.getUUID()), ""),
            org.espetro.team.FreeUnlockManager.isEnabled()
        );

        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
            MatchStatsSyncPacket.from(org.espetro.stats.PlayerMatchStatsManager.getInstance()));
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
            GovernanceStatePacket.from(
                org.espetro.governance.CommanderGovernanceManager.getInstance(),
                player.getUUID()));
        sendEquipZones(player);
    }

    public static void sendDeployPointSync(ServerPlayer player) {
        if (player == null || player.connection == null) return;
        String team = Espetro.getPlayerTeam(player);
        if (team == null) return;
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
            new DeployPointSyncPacket(buildDeployPointItems(player, team)));
    }

    /** 对仍处于部署等待状态的玩家进行低成本兜底同步。 */
    public static void refreshWaitingDeployPoints() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.DEPLOYING && phase != GamePhase.BATTLE) return;
        org.espetro.bastion.BastionManager bm = org.espetro.bastion.BastionManager.getInstance();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (bm.isWaitingForBastion(player.getUUID())) {
                sendDeployPointSync(player);
            }
        }
    }

    /**
     * HAB/Radio 生命周期变化后立即刷新该阵营的部署点列表。
     *
     * <p>不再只刷新"等待复活"的玩家：玩家可能正开着部署界面（J 菜单）看兵站列表，
     * 兵站被摧毁后必须立刻把新列表推给他，否则按钮会残留、点击必然失败。</p>
     */
    public static void refreshDeployPointsForTeam(String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || team == null) return;
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.DEPLOYING && phase != GamePhase.BATTLE) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (team.equals(Espetro.getPlayerTeam(player))) {
                sendDeployPointSync(player);
            }
        }
    }

    private static java.util.List<UnifiedDeployScreenPacket.BastionItem> buildDeployPointItems(
            ServerPlayer player, String team) {
        org.espetro.bastion.BastionManager bm = org.espetro.bastion.BastionManager.getInstance();
        java.util.List<UnifiedDeployScreenPacket.BastionItem> items = new java.util.ArrayList<>();
        for (org.espetro.bastion.BastionData bd : bm.getTeamBastions(team)) {
            // 已被摧毁的兵站不再出现在部署列表里（否则旧按钮会残留、点了必然失败）
            if (!bd.isActive()) continue;
            net.minecraft.core.BlockPos armorStandPos = bm.getRecordedArmorStandPosition(bd);
            if (armorStandPos == null) continue;
            items.add(new UnifiedDeployScreenPacket.BastionItem(
                bd.getBastionId(), bd.getName(),
                armorStandPos.getX() + ", " + armorStandPos.getY() + ", " + armorStandPos.getZ(),
                UnifiedDeployScreenPacket.BastionItem.TYPE_HAB,
                bm.getFobStatus(bd), 0L, 0,
                bd.getHabAvailableAt(),
                org.espetro.logistics.LogisticsConfig.get().habActivationSeconds));
        }
        items.addAll(org.espetro.team.TeamPackManager.getInstance().getDeployItemsForPlayer(player));

        if (org.espetro.team.OutpostManager.getInstance().canListFor(team)) {
            var outposts = org.espetro.team.OutpostManager.getInstance().getOutposts();
            for (int i = 0; i < outposts.size(); i++) {
                var op = outposts.get(i);
                items.add(new UnifiedDeployScreenPacket.BastionItem(
                    new java.util.UUID(0L, i + 1L), "§d前哨: " + op.name, op.getPosString()));
            }
        }
        return items;
    }
}
