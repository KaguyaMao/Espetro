/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 */
package org.espetro.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.espetro.client.ClientEquipZones;
import org.espetro.client.gui.ClientGameState;
import org.espetro.network.EquipZoneSyncPacket;
import org.espetro.team.GamePhase;

public final class EquipZoneRenderer {
    private EquipZoneRenderer() {
    }

    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null || mc.f_91073_ == null || mc.f_91066_.f_92062_) {
            return;
        }
        String team = ClientGameState.getPlayerTeam();
        if (team == null || team.isBlank()) {
            return;
        }
        GamePhase phase = ClientGameState.getCurrentPhase();
        if (phase != GamePhase.DEPLOYING && phase != GamePhase.BATTLE && phase != GamePhase.FACTION_REVEAL && ClientEquipZones.getZones().isEmpty()) {
            return;
        }
        List<EquipZoneSyncPacket.Zone> zones = ClientEquipZones.getZones();
        if (zones.isEmpty()) {
            return;
        }
        PoseStack pose = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.m_91269_().m_110104_();
        VertexConsumer lines = buffers.m_6299_(RenderType.m_110504_());
        double camX = event.getCamera().m_90583_().f_82479_;
        double camY = event.getCamera().m_90583_().f_82480_;
        double camZ = event.getCamera().m_90583_().f_82481_;
        float r = 1.0f;
        float g = 0.85f;
        float b = 0.1f;
        float a = 1.0f;
        pose.m_85836_();
        pose.m_85837_(-camX, -camY, -camZ);
        for (EquipZoneSyncPacket.Zone zone : zones) {
            double range = zone.range() > 0.0 ? zone.range() : 6.0;
            AABB box = new AABB(zone.x() - range, zone.y() - 0.05, zone.z() - range, zone.x() + range, zone.y() + range * 2.0, zone.z() + range);
            LevelRenderer.m_109646_(pose, lines, box, r, g, b, a);
        }
        pose.m_85849_();
        buffers.m_109912_(RenderType.m_110504_());
    }
}

