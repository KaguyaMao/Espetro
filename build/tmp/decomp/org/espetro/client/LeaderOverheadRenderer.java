/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  org.joml.Matrix4f
 */
package org.espetro.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.ClientTacticalState;
import org.joml.Matrix4f;

public final class LeaderOverheadRenderer {
    private static final float HALF = 0.14f;
    private static final float NUMBER_MAX_SCALE = 0.018f;
    private static final float NUMBER_MAX_WIDTH = 0.20300001f;
    private static final float NUMBER_FORWARD_OFFSET = 0.02f;
    private static final ResourceLocation COMMANDER_TEX = LeaderOverheadRenderer.texture("commander.png");
    private static final ResourceLocation SQUAD_LEADER_TEX = LeaderOverheadRenderer.texture("squad_leader.png");
    private static final ResourceLocation SELF_SQUAD_LEADER_TEX = LeaderOverheadRenderer.texture("self_squad_leader.png");
    private static final ResourceLocation FIRETEAM_B_TEX = LeaderOverheadRenderer.texture("fireteam_b.png");
    private static final ResourceLocation FIRETEAM_C_TEX = LeaderOverheadRenderer.texture("fireteam_c.png");
    private static final double R_CMD = 200.0;
    private static final double R_SL_OTHER = 50.0;
    private static final double R_FT = 50.0;

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null || mc.f_91073_ == null || !ClientGameState.getCurrentPhase().isMatchActive()) {
            return;
        }
        if (mc.f_91066_.f_92062_ || mc.f_91074_.m_5833_()) {
            return;
        }
        PoseStack ps = event.getPoseStack();
        Camera camera = event.getCamera();
        MultiBufferSource.BufferSource buf = mc.m_91269_().m_110104_();
        float pt = event.getPartialTick();
        HashMap<Integer, VehicleEntry> veh = new HashMap<Integer, VehicleEntry>();
        for (Player player : mc.f_91073_.m_6907_()) {
            double distSq;
            OverheadInfo info;
            if (player == mc.f_91074_ || player.m_5833_() || player.m_20145_() || !player.m_6084_() || (info = LeaderOverheadRenderer.resolve(player)) == null || !LeaderOverheadRenderer.inRange(info, distSq = camera.m_90583_().m_82531_(player.m_20185_(), player.m_20186_(), player.m_20189_()))) continue;
            Entity rv = player.m_20202_();
            if (rv != null) {
                int vid = rv.m_19879_();
                VehicleEntry e = (VehicleEntry)veh.get(vid);
                int nr = info.rankOrdinal();
                if (e != null && nr >= e.rank && (nr != e.rank || info.squadId >= e.info.squadId)) continue;
                veh.put(vid, new VehicleEntry(info, nr));
                continue;
            }
            LeaderOverheadRenderer.render(ps, buf, player, pt, info, camera);
        }
        for (Map.Entry entry : veh.entrySet()) {
            Entity v = mc.f_91073_.m_6815_((Integer)entry.getKey());
            if (v == null) continue;
            LeaderOverheadRenderer.render(ps, buf, v, pt, ((VehicleEntry)entry.getValue()).info, camera);
        }
        buf.m_109911_();
    }

    private static OverheadInfo resolve(Player p) {
        String n = p.m_7755_().getString();
        ClientTacticalState.MarkerInfo m = ClientTacticalState.getMarker(n);
        if (m == null) {
            return null;
        }
        boolean cmd = m.commander() || ClientTacticalState.isCommander(n);
        int sid = m.squadId();
        int displayId = m.displayId();
        byte ft = m.fireteam();
        if (cmd) {
            return new OverheadInfo(sid, displayId, ft, T.COMMANDER);
        }
        if (m.leader() && sid > 0) {
            return new OverheadInfo(sid, displayId, ft, T.SQUAD_LEADER);
        }
        if (m.fireteamLeader() && (ft == 1 || ft == 2)) {
            return new OverheadInfo(sid, displayId, ft, T.FIRETEAM_LEADER);
        }
        return null;
    }

    private static boolean inRange(OverheadInfo info, double distSq) {
        return switch (info.type) {
            default -> throw new IncompatibleClassChangeError();
            case T.COMMANDER -> {
                if (distSq <= 40000.0) {
                    yield true;
                }
                yield false;
            }
            case T.SQUAD_LEADER -> {
                if (info.squadId == ClientTacticalState.getMySquadId()) {
                    yield true;
                }
                if (distSq <= 2500.0) {
                    yield true;
                }
                yield false;
            }
            case T.FIRETEAM_LEADER -> info.squadId != ClientTacticalState.getMySquadId() ? false : (ClientTacticalState.isLocalSquadLeader(Minecraft.m_91087_().f_91074_.m_7755_().getString()) ? true : (info.fireteam != ClientTacticalState.getMyFireteam() ? false : distSq <= 2500.0));
        };
    }

    private static void render(PoseStack ps, MultiBufferSource.BufferSource buf, Entity entity, float pt, OverheadInfo info, Camera camera) {
        double x = entity.f_19854_ + (entity.m_20185_() - entity.f_19854_) * (double)pt;
        double y = entity.f_19855_ + (entity.m_20186_() - entity.f_19855_) * (double)pt + (double)entity.m_20206_() + 1.0;
        double z = entity.f_19856_ + (entity.m_20189_() - entity.f_19856_) * (double)pt;
        ResourceLocation icon = LeaderOverheadRenderer.textureFor(info);
        ps.m_85836_();
        ps.m_85837_(x - camera.m_90583_().f_82479_, y - camera.m_90583_().f_82480_, z - camera.m_90583_().f_82481_);
        ps.m_252781_(camera.m_253121_());
        ps.m_85836_();
        RenderType iconLayer = RenderType.m_110500_(icon);
        ps.m_85841_(0.14f, 0.14f, 1.0f);
        LeaderOverheadRenderer.quadFlippedX(buf.m_6299_(iconLayer), ps.m_85850_().m_252922_(), -1);
        ps.m_85849_();
        buf.m_109912_(iconLayer);
        if (info.type == T.SQUAD_LEADER) {
            LeaderOverheadRenderer.renderSquadNumber(ps, buf, info.displayId);
        }
        ps.m_85849_();
    }

    private static ResourceLocation textureFor(OverheadInfo info) {
        return switch (info.type) {
            default -> throw new IncompatibleClassChangeError();
            case T.COMMANDER -> COMMANDER_TEX;
            case T.SQUAD_LEADER -> {
                if (info.squadId == ClientTacticalState.getMySquadId()) {
                    yield SELF_SQUAD_LEADER_TEX;
                }
                yield SQUAD_LEADER_TEX;
            }
            case T.FIRETEAM_LEADER -> info.fireteam == 1 ? FIRETEAM_B_TEX : FIRETEAM_C_TEX;
        };
    }

    private static void renderSquadNumber(PoseStack ps, MultiBufferSource.BufferSource buf, int squadId) {
        String label = String.valueOf(squadId);
        Font font = Minecraft.m_91087_().f_91062_;
        int textWidth = Math.max(1, font.m_92895_(label));
        float scale = Math.min(0.018f, 0.20300001f / (float)textWidth);
        ps.m_85836_();
        ps.m_252880_(0.0f, 0.0f, 0.02f);
        ps.m_85841_(-scale, -scale, scale);
        font.m_271703_(label, (float)(-textWidth) / 2.0f, (float)(-font.f_92710_) / 2.0f, -1, false, ps.m_85850_().m_252922_(), buf, Font.DisplayMode.SEE_THROUGH, 0, 0xF000F0);
        ps.m_85849_();
    }

    private static void quadFlippedX(VertexConsumer vc, Matrix4f m, int rgba) {
        int r = rgba >> 16 & 0xFF;
        int g = rgba >> 8 & 0xFF;
        int b = rgba & 0xFF;
        int a = rgba >> 24 & 0xFF;
        int light = 0xF000F0;
        vc.m_252986_(m, -1.0f, 1.0f, 0.0f).m_6122_(r, g, b, a).m_7421_(1.0f, 0.0f).m_85969_(light).m_5752_();
        vc.m_252986_(m, 1.0f, 1.0f, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_85969_(light).m_5752_();
        vc.m_252986_(m, 1.0f, -1.0f, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 1.0f).m_85969_(light).m_5752_();
        vc.m_252986_(m, -1.0f, -1.0f, 0.0f).m_6122_(r, g, b, a).m_7421_(1.0f, 1.0f).m_85969_(light).m_5752_();
    }

    private static ResourceLocation texture(String fileName) {
        return ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)("textures/gui/overhead/" + fileName));
    }

    private static class OverheadInfo {
        final int squadId;
        final int displayId;
        final byte fireteam;
        final T type;

        OverheadInfo(int s, int d, byte f, T t) {
            this.squadId = s;
            this.displayId = d;
            this.fireteam = f;
            this.type = t;
        }

        int rankOrdinal() {
            return this.type == T.COMMANDER ? 0 : (this.type == T.SQUAD_LEADER ? 1 : 2);
        }
    }

    private static class VehicleEntry {
        final OverheadInfo info;
        final int rank;

        VehicleEntry(OverheadInfo i, int r) {
            this.info = i;
            this.rank = r;
        }
    }

    private static enum T {
        COMMANDER,
        SQUAD_LEADER,
        FIRETEAM_LEADER;

    }
}

