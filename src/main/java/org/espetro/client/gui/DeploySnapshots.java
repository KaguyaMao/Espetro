package org.espetro.client.gui;

import org.espetro.client.aui.GuiElement;
import org.espetro.client.aui.GuiRect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.HcrTacticalMapBridge;
import org.espetro.client.ClientEquipZones;
import org.espetro.network.ClientRequests;
import org.espetro.network.UnifiedDeployScreenPacket;
import org.espetro.network.GovernanceStatePacket;
import org.espetro.network.GovernanceActionPacket;
import org.espetro.team.CommanderSkillManager;
import org.espetro.team.Fireteam;
import org.espetro.team.GamePhase;
import org.lwjgl.glfw.GLFW;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 统一部署界面的纯数据逻辑：服务端快照的等价比较（决定是否需要重建界面）、
 * 文本格式化、坐标解析与免费解锁规则。不依赖界面状态，可直接单元测试。
 */
public final class DeploySnapshots {

    private DeploySnapshots() {
    }

    /** 服务端下发的"装备完全解锁模式"状态。 */
    private static volatile boolean freeUnlockMode;

    private static final Pattern COORDINATE_PATTERN =
        Pattern.compile("[-+]?\\d+(?:\\.\\d+)?");

    /**
     * 行集合是否变化（类型/名称/上限/刷新周期）：
     * 只有它变了才需要重建行按钮；仅有 current 变化时原地改文案即可。
     */
    static boolean vehicleRowSignatureEqual(List<UnifiedDeployScreenPacket.VehicleInfo> a,
                                                    List<UnifiedDeployScreenPacket.VehicleInfo> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            UnifiedDeployScreenPacket.VehicleInfo x = a.get(i);
            UnifiedDeployScreenPacket.VehicleInfo y = b.get(i);
            if (!Objects.equals(x.type, y.type)
                || !Objects.equals(x.displayName, y.displayName)
                || x.max != y.max
                || x.respawnMinutes != y.respawnMinutes) {
                return false;
            }
        }
        return true;
    }

    static boolean vehicleInfosEqual(List<UnifiedDeployScreenPacket.VehicleInfo> a,
                                             List<UnifiedDeployScreenPacket.VehicleInfo> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            UnifiedDeployScreenPacket.VehicleInfo x = a.get(i);
            UnifiedDeployScreenPacket.VehicleInfo y = b.get(i);
            if (!Objects.equals(x.type, y.type)
                || !Objects.equals(x.displayName, y.displayName)
                || x.max != y.max
                || x.current != y.current
                || x.respawnMinutes != y.respawnMinutes) {
                return false;
            }
        }
        return true;
    }

    static boolean variantCountsEqual(Map<String, Map<String, Integer>> a,
                                              Map<String, Map<String, Integer>> b) {
        if (a == b) return true;
        if (a == null || b == null || a.size() != b.size()) return false;
        for (Map.Entry<String, Map<String, Integer>> e : a.entrySet()) {
            Map<String, Integer> other = b.get(e.getKey());
            if (other == null || !other.equals(e.getValue())) {
                return false;
            }
        }
        return true;
    }

    static String normalizeSelectedClassId(String classId) {
        return classId == null || classId.isBlank() ? null : classId;
    }

    /** 职业交互结构（职业及其变体的数量、ID 与顺序）。 */
    static boolean classGridEquals(
            List<UnifiedDeployScreenPacket.ClassInfo> a,
            List<UnifiedDeployScreenPacket.ClassInfo> b) {
        if (a == b) return true;
        if (a == null || b == null || a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            UnifiedDeployScreenPacket.ClassInfo x = a.get(i);
            UnifiedDeployScreenPacket.ClassInfo y = b.get(i);
            if (!Objects.equals(x.classId, y.classId)
                || x.variants.size() != y.variants.size()) {
                return false;
            }
            for (int v = 0; v < x.variants.size(); v++) {
                if (!Objects.equals(
                    x.variants.get(v).variantId, y.variants.get(v).variantId)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 影响按钮 label/enabled 的展示态是否一致（含小队当前人数、上限、图标名）。
     */
    static boolean classDisplayStateEquals(
            List<UnifiedDeployScreenPacket.ClassInfo> a,
            List<UnifiedDeployScreenPacket.ClassInfo> b) {
        if (a == b) return true;
        if (a == null || b == null || a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            UnifiedDeployScreenPacket.ClassInfo x = a.get(i);
            UnifiedDeployScreenPacket.ClassInfo y = b.get(i);
            if (x == y) continue;
            if (x == null || y == null) return false;
            if (x.maxPlayers != y.maxPlayers
                || x.strictCount != y.strictCount
                || x.teamCount != y.teamCount
                || x.maxPerSquad != y.maxPerSquad
                || x.squadCurrentCount != y.squadCurrentCount
                || !Objects.equals(x.classId, y.classId)
                || !Objects.equals(x.name, y.name)
                || !Objects.equals(x.icon, y.icon)
                || !Objects.equals(x.iconImage, y.iconImage)
                || x.variants.size() != y.variants.size()) {
                return false;
            }
            for (int v = 0; v < x.variants.size(); v++) {
                UnifiedDeployScreenPacket.VariantInfo vx = x.variants.get(v);
                UnifiedDeployScreenPacket.VariantInfo vy = y.variants.get(v);
                if (!Objects.equals(vx.variantId, vy.variantId)
                    || vx.maxPlayers != vy.maxPlayers
                    || vx.currentCount != vy.currentCount
                    || !Objects.equals(vx.name, vy.name)) {
                    return false;
                }
            }
        }
        return true;
    }

    static String votePrefixFor(GovernanceStatePacket.TeamState state, UUID candidate) {
        if (state == null || candidate == null) {
            return "候选人";
        }
        if ("IMPEACHMENT_VOTE".equals(state.state)) {
            if (candidate.equals(state.commander)) {
                return "原指挥官";
            }
            if (candidate.equals(state.challenger)) {
                return "挑战者";
            }
        }
        return "志愿者";
    }

    static String buildVoteButtonLabel(GovernanceStatePacket.TeamState state,
                                              UUID candidate, String prefix) {
        String name = MatchScoreboardScreen.nameFor(candidate);
        int votes = ClientGovernanceState.voteCount(state, candidate);
        boolean mine = ClientGovernanceState.isMyVote(state, candidate);
        String mark = mine ? "\u00a7a\u2713 " : "\u00a7f";
        return mark + prefix + "\u00a7f\uff1a\u00a7e" + name + "  \u00a7b[" + votes + "\u7968]";
    }

    static boolean sameGovernanceLayout(GovernanceStatePacket.TeamState a,
                                                GovernanceStatePacket.TeamState b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return Objects.equals(a.state, b.state)
            && Objects.equals(a.commander, b.commander)
            && Objects.equals(a.challenger, b.challenger)
            && Objects.equals(a.volunteers, b.volunteers);
    }

    /**
     * 冷却文案统一格式：分钟:秒（{@code m:ss}），秒数不足两位补零
     * （例如 {@code 0:05}、{@code 12:05}）。技能按钮与载具行共用这一份实现。
     */
    static String formatCooldown(int seconds) {
        int safe = Math.max(0, seconds);
        int minutes = safe / 60;
        int rest = safe % 60;
        return minutes + ":" + (rest < 10 ? "0" : "") + rest;
    }

    static boolean sameSquadCategories(
            List<UnifiedDeployScreenPacket.SquadCategoryInfo> a,
            List<UnifiedDeployScreenPacket.SquadCategoryInfo> b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null || a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!Objects.equals(a.get(i).id, b.get(i).id)
                || !Objects.equals(a.get(i).displayName, b.get(i).displayName)) {
                return false;
            }
        }
        return true;
    }

    static double[] parseDeploymentCoordinates(String positionText) {
        if (positionText == null || positionText.isBlank()) {
            return null;
        }

        Matcher matcher = COORDINATE_PATTERN.matcher(positionText);
        double[] values = new double[3];
        int count = 0;
        while (matcher.find() && count < values.length) {
            try {
                values[count++] = Double.parseDouble(matcher.group());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return count >= 3 ? new double[] {values[0], values[2]} : null;
    }

    /**
     * 更新完全解锁状态。
     *
     * @return 状态是否发生变化（变化时调用方应刷新职业按钮）
     */
    public static boolean applyFreeUnlock(boolean value) {
        if (freeUnlockMode == value) return false;
        freeUnlockMode = value;
        return true;
    }

    public static boolean isFreeUnlockMode() {
        return freeUnlockMode;
    }

    static boolean inside(int x, int y, int left, int top, int width, int height) {
        return x >= left && x < left + width && y >= top && y < top + height;
    }
}
