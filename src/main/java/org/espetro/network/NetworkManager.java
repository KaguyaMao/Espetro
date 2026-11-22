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
 * 网络管理器
 * 注册所有数据包
 */
public class NetworkManager {
    public static final String PROTOCOL_VERSION = "1.35";

    public static final SimpleChannel NET = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(Espetro.MOD_ID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static void registerNetwork() {
        // 方向由 Forge 校验：toClient 只接受服务端发出的包，toServer 只接受客户端发出的包。
        // 注册顺序即包 ID，调整顺序或增删包时需提升 PROTOCOL_VERSION。
        PacketRegistrar packets = new PacketRegistrar(NET);
        // 阵营选择包
        packets.toServer(TeamSelectPacket.class, TeamSelectPacket::write, TeamSelectPacket::read, TeamSelectPacket::handle);

        // 观战请求包（中途加入面板"进入观战"）
        packets.toServer(SpectateRequestPacket.class, SpectateRequestPacket::write, SpectateRequestPacket::read, SpectateRequestPacket::handle);

        // 职业选择包（包含结果）
        packets.toServer(ClassSelectPacket.class, ClassSelectPacket::write, ClassSelectPacket::read, ClassSelectPacket::handle);

        // 职业人数请求/同步包
        packets.bidirectional(ClassCountSyncPacket.class, ClassCountSyncPacket::write, ClassCountSyncPacket::read, ClassCountSyncPacket::handle);

        // 等待状态消息包
        packets.toClient(WaitingStatusPacket.class, WaitingStatusPacket::write, WaitingStatusPacket::read, WaitingStatusPacket::handle);

        // 指挥官投票包
        packets.toClient(CommanderVotePacket.class, CommanderVotePacket::write, CommanderVotePacket::read, CommanderVotePacket::handle);

        // 投票数据同步包
        packets.toClient(VoteDataPacket.class, VoteDataPacket::write, VoteDataPacket::read, VoteDataPacket::handle);

        // 投票操作包
        packets.toServer(CastVotePacket.class, CastVotePacket::write, CastVotePacket::read, CastVotePacket::handle);

        // 游戏阶段同步包
        packets.toClient(GamePhaseSyncPacket.class, GamePhaseSyncPacket::write, GamePhaseSyncPacket::read, GamePhaseSyncPacket::handle);

        // 兵力统计同步包
        packets.toClient(TroopCountSyncPacket.class, TroopCountSyncPacket::write, TroopCountSyncPacket::read, TroopCountSyncPacket::handle);

        // 强制打开攻防方选择界面包（S→C）
        packets.toClient(OpenFactionScreenPacket.class, OpenFactionScreenPacket::write, OpenFactionScreenPacket::read, OpenFactionScreenPacket::handle);

        // 编制选择界面包（S→C）
        packets.toClient(ClassSelectScreenPacket.class, ClassSelectScreenPacket::write, ClassSelectScreenPacket::read, ClassSelectScreenPacket::handle);

        // 双方编制揭示界面包（S→C）
        packets.toClient(FactionRevealPacket.class, FactionRevealPacket::write, FactionRevealPacket::read, FactionRevealPacket::handle);

        // 队伍分配提示页包（S→C）
        packets.toClient(TeamAssignPacket.class, TeamAssignPacket::write, TeamAssignPacket::read, TeamAssignPacket::handle);

        // 地图揭晓页包（S→C）
        packets.toClient(MapRevealPacket.class, MapRevealPacket::write, MapRevealPacket::read, MapRevealPacket::handle);

        // 职业选择界面包（S→C）
        packets.toClient(OpenClassSelectionPacket.class, OpenClassSelectionPacket::write, OpenClassSelectionPacket::read, OpenClassSelectionPacket::handle);

        // 请求职业选择界面包（C→S）：客户端请求 → 服务端回传完整数据
        packets.toServer(RequestClassSelectionPacket.class, RequestClassSelectionPacket::write, RequestClassSelectionPacket::read, RequestClassSelectionPacket::handle);

        // 客户端补加实体后请求重同步位置/数据（方案 A：修复开局载具错位）
        packets.toServer(EntityResyncRequestPacket.class, EntityResyncRequestPacket::write, EntityResyncRequestPacket::read, EntityResyncRequestPacket::handle);

        // 请求游戏状态包（C→S）
        packets.toServer(RequestGameStatePacket.class, RequestGameStatePacket::write, RequestGameStatePacket::read, RequestGameStatePacket::handle);

        // 游戏状态响应包（S→C）
        packets.toClient(GameStateResponsePacket.class, GameStateResponsePacket::write, GameStateResponsePacket::read, GameStateResponsePacket::handle);

        // 载具部署界面包（S→C）
        packets.toClient(VehicleDeployScreenPacket.class, VehicleDeployScreenPacket::write, VehicleDeployScreenPacket::read, VehicleDeployScreenPacket::handle);

        // 载具信息请求包（C→S）
        packets.toServer(RequestVehicleInfoPacket.class, RequestVehicleInfoPacket::write, RequestVehicleInfoPacket::read, RequestVehicleInfoPacket::handle);

        // 主动重新部署请求包（C→S）：立刻阵亡，对战阶段扣兵力、部署阶段不扣
        packets.toServer(RedeployRequestPacket.class, RedeployRequestPacket::write, RedeployRequestPacket::read, RedeployRequestPacket::handle);

        // 复活点选择界面包（S→C）
        packets.toClient(DeployPointSelectPacket.class, DeployPointSelectPacket::write, DeployPointSelectPacket::read, DeployPointSelectPacket::handle);

        // 统一部署主界面包（S→C）
        packets.toClient(UnifiedDeployScreenPacket.class, UnifiedDeployScreenPacket::write, UnifiedDeployScreenPacket::read, UnifiedDeployScreenPacket::handle);

        // 班组小队操作包（C→S）
        packets.toServer(SquadActionPacket.class, SquadActionPacket::write, SquadActionPacket::read, SquadActionPacket::handle);

        // 班组小队同步包（S→C）
        packets.toClient(SquadSyncPacket.class, SquadSyncPacket::write, SquadSyncPacket::read, SquadSyncPacket::handle);

        // 指挥官技能请求包（C→S）
        packets.toServer(CommanderSkillPacket.class, CommanderSkillPacket::write, CommanderSkillPacket::read, CommanderSkillPacket::handle);

        // 指挥官技能同步包（S→C）
        packets.toClient(CommanderSkillSyncPacket.class, CommanderSkillSyncPacket::write, CommanderSkillSyncPacket::read, CommanderSkillSyncPacket::handle);

        packets.toServer(RadialActionPacket.class, RadialActionPacket::write, RadialActionPacket::read, RadialActionPacket::handle);

        // ===== multi-dimension battlefield packets (protocol 1.11) =====
        packets.toClient(MapVoteStatePacket.class, MapVoteStatePacket::write, MapVoteStatePacket::read, MapVoteStatePacket::handle);
        packets.toServer(MapVoteCastPacket.class, MapVoteCastPacket::write, MapVoteCastPacket::read, MapVoteCastPacket::handle);
        packets.toClient(OpenMapVoteScreenPacket.class, OpenMapVoteScreenPacket::write, OpenMapVoteScreenPacket::read, OpenMapVoteScreenPacket::handle);
        packets.toClient(TeamSelectStatePacket.class, TeamSelectStatePacket::write, TeamSelectStatePacket::read, TeamSelectStatePacket::handle);
        packets.toClient(MatchStatsSyncPacket.class, MatchStatsSyncPacket::write, MatchStatsSyncPacket::read, MatchStatsSyncPacket::handle);
        packets.toServer(MatchStatsActionPacket.class, MatchStatsActionPacket::write, MatchStatsActionPacket::read, MatchStatsActionPacket::handle);
        packets.toClient(GovernanceStatePacket.class, GovernanceStatePacket::write, GovernanceStatePacket::read, GovernanceStatePacket::handle);
        packets.toServer(GovernanceActionPacket.class, GovernanceActionPacket::write, GovernanceActionPacket::read, GovernanceActionPacket::handle);
        packets.toServer(SquadCreateWithCategoryPacket.class, SquadCreateWithCategoryPacket::write, SquadCreateWithCategoryPacket::read, SquadCreateWithCategoryPacket::handle);
        packets.toClient(OpenHubScreenPacket.class, OpenHubScreenPacket::write, OpenHubScreenPacket::read, OpenHubScreenPacket::handle);
        packets.toClient(RoundEndPacket.class, RoundEndPacket::write, RoundEndPacket::read, RoundEndPacket::handle);
        // 新手教程（此前仅有 send API，未 register → 点击教程直接 Invalid message 崩溃）
        packets.toClient(TutorialSyncPacket.class, TutorialSyncPacket::write, TutorialSyncPacket::read, TutorialSyncPacket::handle);
        packets.toServer(TutorialActionPacket.class, TutorialActionPacket::write, TutorialActionPacket::read, TutorialActionPacket::handle);
        // 编制选择倒计时轻量包（避免每秒全量 ClassSelectScreenPacket）
        packets.toClient(ClassSelectTimerPacket.class, ClassSelectTimerPacket::write, ClassSelectTimerPacket::read, ClassSelectTimerPacket::handle);
        packets.toClient(EquipZoneSyncPacket.class, EquipZoneSyncPacket::write, EquipZoneSyncPacket::read, EquipZoneSyncPacket::handle);
        packets.bidirectional(RadioRadialPacket.class, RadioRadialPacket::write, RadioRadialPacket::read, RadioRadialPacket::handle);
        // 战局倒计时包
        packets.toClient(BattleTimerPacket.class, BattleTimerPacket::write, BattleTimerPacket::read, BattleTimerPacket::handle);
        // 组队匹配
        packets.toServer(PartyActionPacket.class, PartyActionPacket::write, PartyActionPacket::read, PartyActionPacket::handle);
        packets.toClient(PartyListPacket.class, PartyListPacket::write, PartyListPacket::read, PartyListPacket::handle);
        packets.toServer(VehicleSupplyActionPacket.class, VehicleSupplyActionPacket::write, VehicleSupplyActionPacket::read, VehicleSupplyActionPacket::handle);
        packets.bidirectional(VehicleSupplySyncPacket.class, VehicleSupplySyncPacket::write, VehicleSupplySyncPacket::read, VehicleSupplySyncPacket::handle);
        packets.toClient(FobSupplySyncPacket.class, FobSupplySyncPacket::write, FobSupplySyncPacket::read, FobSupplySyncPacket::handle);
        packets.toClient(OutpostSupplySyncPacket.class, OutpostSupplySyncPacket::write, OutpostSupplySyncPacket::read, OutpostSupplySyncPacket::handle);
        packets.toServer(BuildFortificationPacket.class, BuildFortificationPacket::write, BuildFortificationPacket::read, BuildFortificationPacket::handle);
        packets.bidirectional(FortificationCatalogPacket.class, FortificationCatalogPacket::write, FortificationCatalogPacket::read, FortificationCatalogPacket::handle);
        packets.toClient(FortificationPreviewPacket.class, FortificationPreviewPacket::write, FortificationPreviewPacket::read, FortificationPreviewPacket::handle);
        packets.toServer(FortificationPlacementPacket.class, FortificationPlacementPacket::write, FortificationPlacementPacket::read, FortificationPlacementPacket::handle);
        packets.toServer(FortificationWorkPacket.class, FortificationWorkPacket::write, FortificationWorkPacket::read, FortificationWorkPacket::handle);
        packets.toClient(FortificationProgressPacket.class, FortificationProgressPacket::write, FortificationProgressPacket::read, FortificationProgressPacket::handle);
        packets.toClient(FortificationWandPacket.class, FortificationWandPacket::write, FortificationWandPacket::read, FortificationWandPacket::handle);
        packets.toClient(AudioCuePacket.class, AudioCuePacket::write, AudioCuePacket::read, AudioCuePacket::handle);
        packets.toClient(TaczGunPackSyncChunkPacket.class, TaczGunPackSyncChunkPacket::write, TaczGunPackSyncChunkPacket::read, TaczGunPackSyncChunkPacket::handle);
        packets.toClient(DeployPointSyncPacket.class, DeployPointSyncPacket::write, DeployPointSyncPacket::read, DeployPointSyncPacket::handle);

        packets.toServer(RequestResupplyCatalogPacket.class, RequestResupplyCatalogPacket::write, RequestResupplyCatalogPacket::read, RequestResupplyCatalogPacket::handle);
        packets.toClient(ResupplyCatalogPacket.class, ResupplyCatalogPacket::write, ResupplyCatalogPacket::read, ResupplyCatalogPacket::handle);
        packets.toServer(SelectResupplyEntryPacket.class, SelectResupplyEntryPacket::write, SelectResupplyEntryPacket::read, SelectResupplyEntryPacket::handle);
        packets.toClient(ResupplyEntryDeltaPacket.class, ResupplyEntryDeltaPacket::write, ResupplyEntryDeltaPacket::read, ResupplyEntryDeltaPacket::handle);
        packets.toServer(CloseResupplySessionPacket.class, CloseResupplySessionPacket::write, CloseResupplySessionPacket::read, CloseResupplySessionPacket::handle);

        packets.toServer(MountRequestPacket.class, MountRequestPacket::write, MountRequestPacket::read, MountRequestPacket::handle);
        packets.toClient(MountProgressPacket.class, MountProgressPacket::write, MountProgressPacket::read, MountProgressPacket::handle);
        packets.toServer(DismountRequestPacket.class, DismountRequestPacket::write, DismountRequestPacket::read, DismountRequestPacket::handle);
        packets.toServer(SeatSwitchReadyPacket.class, SeatSwitchReadyPacket::write, SeatSwitchReadyPacket::read, SeatSwitchReadyPacket::handle);
        // 服务端→客户端：碰撞体积显示策略
        packets.toClient(HitboxPolicyPacket.class, HitboxPolicyPacket::write, HitboxPolicyPacket::read, HitboxPolicyPacket::handle);
        // 服务端→客户端：强制关闭对局内模组界面（设为观察者等移出对局场景）
        packets.toClient(CloseModScreensPacket.class, CloseModScreensPacket::write, CloseModScreensPacket::read, CloseModScreensPacket::handle);
        // 固定武器：F 键用 FOB 弹药兑换（协议 1.12）
        packets.toServer(FixedWeaponExchangePacket.class, FixedWeaponExchangePacket::write, FixedWeaponExchangePacket::read, FixedWeaponExchangePacket::handle);
        // 固定武器：F 打开兑换轮盘（协议 1.13）
        packets.toServer(FixedWeaponOpenPacket.class, FixedWeaponOpenPacket::write, FixedWeaponOpenPacket::read, FixedWeaponOpenPacket::handle);
        packets.toClient(FixedWeaponWheelPacket.class, FixedWeaponWheelPacket::write, FixedWeaponWheelPacket::read, FixedWeaponWheelPacket::handle);
    }

    /**
     * 通用方法：发送网络包给指定玩家（S→C）
     */
    public static <T> void sendToPlayer(ServerPlayer player, T packet) {
        NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
