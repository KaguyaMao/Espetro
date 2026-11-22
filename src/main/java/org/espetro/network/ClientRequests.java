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
 * 客户端 → 服务端请求的发送入口。只能在客户端调用；每个方法发送一个 C→S 包。
 */
public final class ClientRequests {

    private ClientRequests() {
    }

    public static void sendBuildFortification(String fortId) {
        NetworkManager.NET.sendToServer(new BuildFortificationPacket(fortId));
    }

    public static void sendFortificationPlacement(FortificationPlacementPacket.Action action,
                                                   UUID token,
                                                   net.minecraft.core.BlockPos anchor,
                                                   net.minecraft.core.Direction facing) {
        NetworkManager.NET.sendToServer(new FortificationPlacementPacket(action, token, anchor, facing));
    }

    public static void sendFortificationWork(net.minecraft.core.BlockPos target, boolean build) {
        NetworkManager.NET.sendToServer(FortificationWorkPacket.block(target, build));
    }

    public static void sendFortificationEntityWork(UUID target, boolean build) {
        NetworkManager.NET.sendToServer(FortificationWorkPacket.entity(target, build));
    }

    /** 固定武器：请求打开兑换轮盘（对普通载具会静默无响应）。 */
    public static void sendFixedWeaponOpen(UUID weaponEntityId) {
        if (weaponEntityId == null) {
            return;
        }
        NetworkManager.NET.sendToServer(new FixedWeaponOpenPacket(weaponEntityId));
    }

    /** 固定武器：请求用所在 FOB 的弹药兑换一次。 */
    public static void sendFixedWeaponExchange(UUID weaponEntityId) {
        if (weaponEntityId == null) {
            return;
        }
        NetworkManager.NET.sendToServer(new FixedWeaponExchangePacket(weaponEntityId));
    }

    public static void requestFortificationCatalog() {
        NetworkManager.NET.sendToServer(FortificationCatalogPacket.request());
    }

    public static void sendRadioOpen(net.minecraft.core.BlockPos pos) {
        NetworkManager.NET.sendToServer(RadioRadialPacket.openRequest(pos));
    }

    public static void sendRadioResupply(net.minecraft.core.BlockPos pos) {
        NetworkManager.NET.sendToServer(RadioRadialPacket.resupply(pos));
    }

    /**
     * 发送职业选择包
     */
    public static void sendClassSelect(String factionId, String classId) {
        NetworkManager.NET.sendToServer(new ClassSelectPacket(factionId, classId));
    }

    public static void sendClassSelect(String factionId, String classId, String variantId) {
        NetworkManager.NET.sendToServer(new ClassSelectPacket(factionId, classId, variantId));
    }

    public static void sendRadioClassSelect(String factionId, String classId, String variantId,
                                            net.minecraft.core.BlockPos radioPos) {
        NetworkManager.NET.sendToServer(ClassSelectPacket.fromRadio(
            factionId, classId, variantId, radioPos));
    }

    /**
     * 发送阵营选择包
     */
    public static void sendFactionSelect(String factionId) {
        NetworkManager.NET.sendToServer(new TeamSelectPacket(factionId));
    }

    /** 请求进入观战（中途加入面板按钮，C→S）。 */
    public static void sendSpectateRequest() {
        NetworkManager.NET.sendToServer(new SpectateRequestPacket());
    }

    /**
     * 请求打开职业选择界面（C→S），服务端会返回完整数据后自动打开GUI
     */
    public static void requestClassSelection(String factionId) {
        NetworkManager.NET.sendToServer(new RequestClassSelectionPacket(factionId));
    }

    /**
     * 请求职业人数同步
     */
    public static void requestClassCounts(String factionId) {
        NetworkManager.NET.sendToServer(new ClassCountSyncPacket(factionId));
    }

    /**
     * 请求当前游戏状态（C→S）
     */
    public static void requestGameState() {
        NetworkManager.NET.sendToServer(new RequestGameStatePacket());
    }

    public static void sendRadialAction(RadialActionPacket.Action action) {
        NetworkManager.NET.sendToServer(new RadialActionPacket(action));
    }

    /**
     * 创建班组小队。
     */
    public static void sendTutorialAction(byte action, String stepId) {
        try {
            NetworkManager.NET.sendToServer(new TutorialActionPacket(action, stepId == null ? "" : stepId));
        } catch (Exception e) {
            Espetro.LOGGER.error("发送教程操作包失败", e);
        }
    }

    public static void sendTutorialReopen() {
        try {
            NetworkManager.NET.sendToServer(TutorialActionPacket.reopen());
        } catch (Exception e) {
            Espetro.LOGGER.error("发送教程重开包失败", e);
        }
    }

    public static void sendTutorialNext(String stepId) {
        try {
            NetworkManager.NET.sendToServer(TutorialActionPacket.next(stepId));
        } catch (Exception e) {
            Espetro.LOGGER.error("发送教程下一步包失败", e);
        }
    }

    public static void createSquad(String squadName) {
        NetworkManager.NET.sendToServer(SquadActionPacket.create(squadName));
    }

    /**
     * 加入班组小队。
     */
    public static void joinSquad(int squadId) {
        NetworkManager.NET.sendToServer(SquadActionPacket.join(squadId));
    }

    /**
     * 退出当前班组小队。
     */
    public static void leaveSquad() {
        NetworkManager.NET.sendToServer(SquadActionPacket.leave());
    }

    /**
     * 删除当前队长管理的小队。
     */
    public static void deleteSquad(int squadId) {
        NetworkManager.NET.sendToServer(SquadActionPacket.delete(squadId));
    }

    public static void transferSquadLeader(java.util.UUID targetUuid) {
        if (targetUuid != null) {
            NetworkManager.NET.sendToServer(SquadActionPacket.transferSquadLeader(targetUuid));
        }
    }

    public static void transferFireteamLeader(java.util.UUID targetUuid) {
        if (targetUuid != null) {
            NetworkManager.NET.sendToServer(SquadActionPacket.transferFireteamLeader(targetUuid));
        }
    }

    public static void appointFireteamLeader(java.util.UUID targetUuid, org.espetro.team.Fireteam fireteam) {
        if (targetUuid != null && fireteam != null && fireteam != org.espetro.team.Fireteam.A) {
            NetworkManager.NET.sendToServer(SquadActionPacket.appointFireteamLeader(targetUuid, fireteam));
        }
    }

    public static void assignFireteam(java.util.UUID targetUuid, org.espetro.team.Fireteam fireteam) {
        if (targetUuid != null && fireteam != null) {
            NetworkManager.NET.sendToServer(SquadActionPacket.assignFireteam(targetUuid, fireteam));
        }
    }

    public static void lockSquad() {
        NetworkManager.NET.sendToServer(SquadActionPacket.lock());
    }

    public static void unlockSquad() {
        NetworkManager.NET.sendToServer(SquadActionPacket.unlock());
    }

    /**
     * 发送投票
     */
    public static void sendCastVote(String targetPlayerName) {
        NetworkManager.NET.sendToServer(new CastVotePacket(targetPlayerName));
    }

    /** 客户端请求打开只读载具信息面板。 */
    public static void requestVehicleInfo() {
        NetworkManager.NET.sendToServer(new RequestVehicleInfoPacket());
    }

    /** 客户端请求重新部署（立刻阵亡；对战阶段扣兵力，部署阶段不扣）。 */
    public static void requestRedeploy() {
        NetworkManager.NET.sendToServer(new RedeployRequestPacket());
    }

    /**
     * 请求指挥官技能同步（C→S），服务端返回 CommanderSkillSyncPacket
     */
    public static void requestCommanderSkillSync() {
        NetworkManager.NET.sendToServer(CommanderSkillPacket.query());
    }

    /**
     * 发送指挥官技能激活请求（C→S）
     */
    public static void sendCommanderSkillActivate(org.espetro.team.CommanderSkillType type) {
        NetworkManager.NET.sendToServer(CommanderSkillPacket.activate(type));
    }

    public static void sendCommanderSkillActivate(String skillId) {

        NetworkManager.NET.sendToServer(new CommanderSkillPacket(skillId));
    }

    public static void sendMapVoteCast(String mapFolder) {
        NetworkManager.NET.sendToServer(new MapVoteCastPacket(mapFolder));
    }

    public static void sendGovernanceAction(GovernanceActionPacket.Action action, java.util.UUID candidate) {
        NetworkManager.NET.sendToServer(new GovernanceActionPacket(action, candidate));
    }

    public static void sendSquadCreateWithCategory(String name, String categoryId) {
        NetworkManager.NET.sendToServer(new SquadCreateWithCategoryPacket(name, categoryId));
    }

    public static void sendMatchStatsAction(MatchStatsActionPacket.Action action, java.util.UUID target) {
        NetworkManager.NET.sendToServer(new MatchStatsActionPacket(action, target));
    }

    // ==================== 组队匹配 ====================

    public static void sendPartyCreate(String password) {
        NetworkManager.NET.sendToServer(PartyActionPacket.create(password));
    }

    public static void sendPartyJoin(java.util.UUID partyId, String password) {
        NetworkManager.NET.sendToServer(PartyActionPacket.join(partyId, password));
    }

    public static void sendPartySetPassword(java.util.UUID partyId, String password) {
        NetworkManager.NET.sendToServer(PartyActionPacket.setPassword(partyId, password));
    }

    public static void sendPartyLeave() {
        NetworkManager.NET.sendToServer(PartyActionPacket.leave());
    }

    public static void sendPartyKick(java.util.UUID partyId, java.util.UUID targetId) {
        NetworkManager.NET.sendToServer(PartyActionPacket.kick(partyId, targetId));
    }

    public static void sendPartyToggleLock(java.util.UUID partyId) {
        NetworkManager.NET.sendToServer(PartyActionPacket.toggleLock(partyId));
    }

    public static void sendPartyDisband(java.util.UUID partyId) {
        NetworkManager.NET.sendToServer(PartyActionPacket.disband(partyId));
    }

    /** 客户端请求最新的组队列表。 */
    public static void requestPartyList() {
        NetworkManager.NET.sendToServer(PartyActionPacket.requestList());
    }
}
