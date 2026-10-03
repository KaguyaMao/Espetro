package org.espetro.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.ClientTacticalState;
import org.joml.Matrix4f;

/**
 * 头顶标识渲染器（指挥官/小队长/火力组长/普通成员）。
 * 仿 vanilla EntityRenderer.renderNameTag 的 PoseStack 推栈方式，
 * 修复此前自建 Matrix4f 导致的位移/旋转顺序错误。
 *
 * <p>按需求：所有标识**全距离可见**（含本队），仅车内规则不同——车内只保留
 * 指挥官/小队长/火力组长，不画普通成员标识。普通成员标识为队长标识的 1/2。</p>
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
    private static final float NUMBER_MAX_SCALE = 0.018f;
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
        // 队长类：实体高度 + 1.0（保持原样）；普通成员：位于 nametag 顶边上方
        double y = baseY + (info.type == T.MEMBER
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
        // 普通成员标识为队长标识的 1/5
        float half = info.type == T.MEMBER ? HALF_MEMBER : HALF;
        ps.scale(half, half, 1f);
        quadFlippedX(buf.getBuffer(iconLayer), ps.last().pose(), 0xFFFFFFFF);
        ps.popPose();
        // 先提交不透明图标，再把数字写入字体缓冲，确保数字最终覆盖在图标上。
        buf.endBatch(iconLayer);

        // == 小队编号：居中叠在队长画框前方，按位数缩放且始终略小于画框 ==
        if (info.type == T.SQUAD_LEADER) {
            renderSquadNumber(ps, buf, info.displayId);
        }
        ps.popPose();
    }

    private static ResourceLocation textureFor(OverheadInfo info) {
        return switch (info.type) {
            case COMMANDER -> COMMANDER_TEX;
            case SQUAD_LEADER -> info.squadId == ClientTacticalState.getMySquadId()
                ? SELF_SQUAD_LEADER_TEX : SQUAD_LEADER_TEX;
            case FIRETEAM_LEADER -> info.fireteam == 1 ? FIRETEAM_B_TEX : FIRETEAM_C_TEX;
            // 普通成员：其它小队用 squad_leader.png（不画数字）；本队 A/B/C 组各用一张（不画数字）
            case MEMBER -> {
                if (info.squadId != ClientTacticalState.getMySquadId()) yield SQUAD_LEADER_TEX;
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
            0xFFFFFFFF, false, ps.last().pose(), buf,
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
