package org.espetro.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.ClientTacticalState;
import org.espetro.client.gui.UiTheme;
import org.joml.Matrix4f;

/**
 * 头顶标识渲染器（指挥官/小队长/火力组长/普通成员）。
 * 仿 vanilla EntityRenderer.renderNameTag 的 PoseStack 推栈方式，
 * 修复此前自建 Matrix4f 导致的位移/旋转顺序错误。
 *
 * <p>按需求：所有标识**全距离可见**（含本队），仅车内规则不同——车内只保留
 * 指挥官/小队长/火力组长，不画普通成员标识。普通成员标识为队长标识的 1/2。</p>
 *
 * <p>可见性规则：火力组长的彩色标识与组员的彩色标识**只对本小队可见**；
 * 其它小队一律显示同一张"小蓝色标"（squad_leader.png，小号、不画数字）。
 * 小队长标内的队伍编号为黑色，保证在绿色/蓝色圆标上都看得清。</p>
 */
public final class LeaderOverheadRenderer {

    /** 队长类标识半宽（PNG 画框按需求已缩小为原来 1/2）。 */
    private static final float HALF = 0.14f;
    /** 普通成员标识半宽 = 队长标识的 1/2。 */
    private static final float HALF_MEMBER = HALF / 2f;
    /**
     * 普通成员标识相对原版 nametag 顶边的抬高量。
     * nametag 顶边 = {@code Entity#getNameTagOffsetY()}（= bbHeight + 0.5，文本从锚点向下 0.225），
     * 图标中心 = 顶边 + 抬高量 + 图标半高 ⇒ 图标底边 = 顶边 + 抬高量。
     */
    private static final double MEMBER_LIFT = 0.05;
    private static final float NUMBER_MAX_SCALE = 0.022f;
    private static final float NUMBER_MAX_WIDTH = HALF * 1.45f;
    private static final float NUMBER_FORWARD_OFFSET = 0.02f;

    private static final ResourceLocation COMMANDER_TEX = texture("commander.png");
    private static final ResourceLocation SQUAD_LEADER_TEX = texture("squad_leader.png");
    private static final ResourceLocation SELF_SQUAD_LEADER_TEX = texture("self_squad_leader.png");
    private static final ResourceLocation FIRETEAM_B_TEX = texture("fireteam_b.png");
    private static final ResourceLocation FIRETEAM_C_TEX = texture("fireteam_c.png");
    /** 本队 B / C 组普通成员（普通玩家标识）。 */
    private static final ResourceLocation FIRETEAMMATE_B_TEX = texture("fireteammate_b.png");
    private static final ResourceLocation FIRETEAMMATE_C_TEX = texture("fireteammate_c.png");
    /** 医疗兵视角：倒地队友的伤员标识（红色 75% 半透明，本文件 assets/espetro/textures/gui/overhead/wounded.png）。 */
    private static final ResourceLocation WOUNDED_TEX = texture("wounded.png");
    /** 伤员标识颜色：纯红 + 75% 透明度（0.75*255 = 191 = 0xBF）。 */
    private static final int WOUNDED_COLOR = 0xBFFF0000;
    /** 伤员标识尺寸 = 队长标识的 2 倍。 */
    private static final float WOUNDED_HALF = HALF * 2f;
    /** 伤员标识高度再抬 1/3 个自身高度，避免和队长标识重叠。 */
    private static final double WOUNDED_LIFT = WOUNDED_HALF * 2f / 3.0;

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || !ClientGameState.getCurrentPhase().isMatchActive()) return;
        if (mc.options.hideGui || mc.player.isSpectator()) return;

        PoseStack ps = event.getPoseStack();
        Camera camera = event.getCamera();
        MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
        float pt = event.getPartialTick();

        java.util.Map<Integer, VehicleEntry> veh = new java.util.HashMap<>();

        for (Player p : mc.level.players()) {
            if (p == mc.player || p.isSpectator() || p.isInvisible() || !p.isAlive()) continue;
            OverheadInfo info = resolve(p);
            if (info == null) continue;

            Entity rv = p.getVehicle();
            if (rv != null) {
                // 车内只保留指挥官/小队长/火力组长，不画普通成员标识
                if (info.type == T.MEMBER) continue;
                int vid = rv.getId();
                VehicleEntry e = veh.get(vid);
                int nr = info.rankOrdinal();
                if (e == null || nr < e.rank || (nr == e.rank && info.squadId < e.info.squadId))
                    veh.put(vid, new VehicleEntry(info, nr));
            } else {
                render(ps, buf, p, pt, info, camera);
            }
        }
        for (var e : veh.entrySet()) {
            Entity v = mc.level.getEntity(e.getKey());
            if (v != null) render(ps, buf, v, pt, e.getValue().info, camera);
        }
        // 医疗兵：倒地队友头顶的伤员标识（全距离可见）
        renderWoundedAllies(ps, buf, pt, camera);
        buf.endBatch();
    }

    private static OverheadInfo resolve(Player p) {
        String n = p.getName().getString();
        ClientTacticalState.MarkerInfo m = ClientTacticalState.getMarker(n);
        if (m == null) return null;
        boolean cmd = m.commander() || ClientTacticalState.isCommander(n);
        int sid = m.squadId();
        int displayId = m.displayId();
        byte ft = m.fireteam();
        if (cmd) return new OverheadInfo(sid, displayId, ft, T.COMMANDER);
        if (m.leader() && sid > 0) return new OverheadInfo(sid, displayId, ft, T.SQUAD_LEADER);
        // A 组组长即小队长（已在上一条返回）；这里只剩 B/C 组组长
        if (m.fireteamLeader() && (ft == 1 || ft == 2)) return new OverheadInfo(sid, displayId, ft, T.FIRETEAM_LEADER);
        // 其余有编制的小队成员 → 普通成员标识（全距离可见）
        if (sid > 0) return new OverheadInfo(sid, displayId, ft, T.MEMBER);
        return null;
    }

    /** 仿 vanilla renderNameTag：push → translate → mulPose(cameraOrientation) → scale → draw → pop */
    private static void render(PoseStack ps, MultiBufferSource.BufferSource buf,
                               Entity entity, float pt, OverheadInfo info, Camera camera) {
        double x = entity.xo + (entity.getX() - entity.xo) * pt;
        double baseY = entity.yo + (entity.getY() - entity.yo) * pt;
        // 其它小队的火力组长/组员统一显示"小蓝色标"：小一号，并放到普通成员高度
        boolean smallAllyMarker = info.squadId != ClientTacticalState.getMySquadId()
            && (info.type == T.FIRETEAM_LEADER || info.type == T.MEMBER);
        // 队长类：实体高度 + 1.0（保持原样）；普通成员/小蓝标：位于 nametag 顶边上方
        double y = baseY + (info.type == T.MEMBER || smallAllyMarker
            ? entity.getNameTagOffsetY() + MEMBER_LIFT + HALF_MEMBER
            : entity.getBbHeight() + 1.0);
        double z = entity.zo + (entity.getZ() - entity.zo) * pt;

        ResourceLocation icon = textureFor(info);
        ps.pushPose();
        ps.translate(x - camera.getPosition().x, y - camera.getPosition().y, z - camera.getPosition().z);
        ps.mulPose(camera.rotation());

        // == PNG 画框 ==
        ps.pushPose();
        // 保持正向缩放以维持顶点绕序；通过 UV 水平翻转修正镜像，避免被背面剔除。
        RenderType iconLayer = RenderType.textSeeThrough(icon);
        // 普通成员标识与其它小队的小蓝标都是小号（队长标识的一半）
        float half = (info.type == T.MEMBER || smallAllyMarker) ? HALF_MEMBER : HALF;
        ps.scale(half, half, 1f);
        quadFlippedX(buf.getBuffer(iconLayer), ps.last().pose(), UiTheme.TEXTURE_WHITE);
        ps.popPose();
        // 先提交不透明图标，再把数字写入字体缓冲，确保数字最终覆盖在图标上。
        buf.endBatch(iconLayer);

        // == 小队编号：居中叠在队长画框前方，按位数缩放且始终略小于画框 ==
        if (info.type == T.SQUAD_LEADER) {
            renderSquadNumber(ps, buf, info.displayId);
            // 关键：字体与图标是不同 RenderType，而 BufferSource.endBatch()（无参）只 flush
            // lastState；这里必须立刻 flush 一次（此刻 lastState 正是字体层），否则后续任何
            // 标识 getBuffer(图标层) 都会把 lastState 顶掉，字体层永远不落盘 → 数字不可见。
            buf.endBatch();
        }
        ps.popPose();
    }

    /**
     * 医疗兵专属：倒地队友头顶最高图层画伤员标识（全距离可见）。
     * 只有本地玩家职业带 {@code "med": true} 时才绘制；倒地者自己与普通玩家看不到。
     */
    private static void renderWoundedAllies(PoseStack ps, MultiBufferSource.BufferSource buf,
                                            float pt, Camera camera) {
        if (!ClientMedicState.isMedic()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        Team myTeam = mc.player.getTeam();
        for (Player p : mc.level.players()) {
            if (p == mc.player || !p.isAlive() || p.isSpectator()) continue;
            // 倒地判据：隐身。realgrisly 倒地时除了 setForcedPose(SWIMMING)（最终由
            // Player.updatePlayerPose() 转成 DATA_POSE），还会 setInvisible(true)；
            // 姿态可能被其它逻辑或同步时序吃掉，隐身是同一 tick 内必定生效的标记，
            // 因此用隐身判定，避免医疗兵头顶标识时有时无。
            if (!p.isInvisible()) continue;
            if (myTeam != null && p.getTeam() != myTeam) continue;

            double x = p.xo + (p.getX() - p.xo) * pt;
            double baseY = p.yo + (p.getY() - p.yo) * pt;
            double y = baseY + p.getBbHeight() + 1.0 + WOUNDED_LIFT;
            double z = p.zo + (p.getZ() - p.zo) * pt;

            RenderType layer = RenderType.textSeeThrough(WOUNDED_TEX);
            ps.pushPose();
            ps.translate(x - camera.getPosition().x, y - camera.getPosition().y, z - camera.getPosition().z);
            ps.mulPose(camera.rotation());
            ps.pushPose();
            ps.scale(WOUNDED_HALF, WOUNDED_HALF, 1f);
            quadFlippedX(buf.getBuffer(layer), ps.last().pose(), WOUNDED_COLOR);
            ps.popPose();
            buf.endBatch(layer);
            ps.popPose();
        }
    }

    private static ResourceLocation textureFor(OverheadInfo info) {
        boolean mySquad = info.squadId == ClientTacticalState.getMySquadId();
        return switch (info.type) {
            case COMMANDER -> COMMANDER_TEX;
            case SQUAD_LEADER -> mySquad ? SELF_SQUAD_LEADER_TEX : SQUAD_LEADER_TEX;
            // 火力组长标识只对本小队可见；其它小队一律小蓝色标（尺寸见 render 的 smallAllyMarker）
            case FIRETEAM_LEADER -> !mySquad ? SQUAD_LEADER_TEX
                : info.fireteam == 1 ? FIRETEAM_B_TEX : FIRETEAM_C_TEX;
            // 普通成员：本队 A/B/C 组各用一张彩色标（不画数字）；其它小队用小蓝色标（不画数字）
            case MEMBER -> {
                if (!mySquad) yield SQUAD_LEADER_TEX;
                yield switch (info.fireteam) {
                    case 1 -> FIRETEAMMATE_B_TEX;   // B 组
                    case 2 -> FIRETEAMMATE_C_TEX;   // C 组
                    default -> SELF_SQUAD_LEADER_TEX;   // A 组（0）
                };
            }
        };
    }

    private static void renderSquadNumber(PoseStack ps, MultiBufferSource.BufferSource buf, int squadId) {
        String label = String.valueOf(squadId);
        var font = Minecraft.getInstance().font;
        int textWidth = Math.max(1, font.width(label));
        float scale = Math.min(NUMBER_MAX_SCALE, NUMBER_MAX_WIDTH / textWidth);

        ps.pushPose();
        ps.translate(0f, 0f, NUMBER_FORWARD_OFFSET);
        // 字体坐标 X/Y 都要反转，和 vanilla 名牌渲染保持一致。
        ps.scale(-scale, -scale, scale);
        font.drawInBatch(label,
            -textWidth / 2f,
            -font.lineHeight / 2f,
            UiTheme.WORLD_INK, false, ps.last().pose(), buf,
            net.minecraft.client.gui.Font.DisplayMode.SEE_THROUGH, 0, 0xF000F0);
        ps.popPose();
    }

    private static void quadFlippedX(VertexConsumer vc, Matrix4f m, int rgba) {
        int r = (rgba >> 16) & 0xFF, g = (rgba >> 8) & 0xFF, b = rgba & 0xFF, a = (rgba >> 24) & 0xFF;
        int light = 0xF000F0;
        vc.vertex(m, -1f,  1f, 0f).color(r, g, b, a).uv(1, 0).uv2(light).endVertex();
        vc.vertex(m,  1f,  1f, 0f).color(r, g, b, a).uv(0, 0).uv2(light).endVertex();
        vc.vertex(m,  1f, -1f, 0f).color(r, g, b, a).uv(0, 1).uv2(light).endVertex();
        vc.vertex(m, -1f, -1f, 0f).color(r, g, b, a).uv(1, 1).uv2(light).endVertex();
    }

    private static ResourceLocation texture(String fileName) {
        return ResourceLocation.fromNamespaceAndPath(
            "espetro", "textures/gui/overhead/" + fileName);
    }

    private enum T { COMMANDER, SQUAD_LEADER, FIRETEAM_LEADER, MEMBER }

    private static class OverheadInfo {
        final int squadId; final int displayId; final byte fireteam; final T type;
        OverheadInfo(int s, int d, byte f, T t) { squadId = s; displayId = d; fireteam = f; type = t; }
        int rankOrdinal() {
            return switch (type) {
                case COMMANDER -> 0;
                case SQUAD_LEADER -> 1;
                case FIRETEAM_LEADER -> 2;
                case MEMBER -> 3;
            };
        }
    }

    private static class VehicleEntry {
        final OverheadInfo info; final int rank;
        VehicleEntry(OverheadInfo i, int r) { info = i; rank = r; }
    }
}
