/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.RenderNameTagEvent
 *  net.minecraftforge.eventbus.api.Event$Result
 */
package org.espetro.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.Event;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.ClientTacticalState;

public final class TeammateNameTagRenderer {
    private static final int HUB_NAME_COLOR = 0xFFFFFF;

    private TeammateNameTagRenderer() {
    }

    public static void onRenderNameTag(RenderNameTagEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player target = (Player)entity;
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer viewer = mc.f_91074_;
        if (viewer == null || target == viewer) {
            event.setResult(Event.Result.DENY);
            return;
        }
        if (ClientGameState.getCurrentPhase().isLobbyLike()) {
            String name = target.m_7755_().getString();
            event.setContent((Component)Component.m_237113_(name).m_6270_(Style.f_131099_.m_131148_(TextColor.m_131266_(0xFFFFFF))));
            event.setResult(Event.Result.ALLOW);
            return;
        }
        if (!TeammateNameTagRenderer.isFriendlyTeammate(viewer, target) || !TeammateNameTagRenderer.isLookedAtWithinDistance(viewer, target, event.getPartialTick())) {
            event.setResult(Event.Result.DENY);
            return;
        }
        String name = target.m_7755_().getString();
        int color = ClientTacticalState.getNameColor(name) & 0xFFFFFF;
        event.setContent((Component)Component.m_237113_(name).m_6270_(Style.f_131099_.m_131148_(TextColor.m_131266_(color))));
        event.setResult(Event.Result.ALLOW);
    }

    private static boolean isFriendlyTeammate(Player viewer, Player target) {
        String targetTeamId;
        Team viewerTeam = viewer.m_5647_();
        Team targetTeam = target.m_5647_();
        String viewerTeamId = viewerTeam != null ? viewerTeam.m_5758_() : null;
        String string = targetTeamId = targetTeam != null ? targetTeam.m_5758_() : null;
        if (TeammateNameTagRenderer.isEspetroTeam(viewerTeamId)) {
            return viewerTeamId.equals(targetTeamId);
        }
        String localTeam = ClientGameState.getPlayerTeam();
        if (localTeam == null || targetTeam == null) {
            return false;
        }
        return "ATTACK".equals(localTeam) && "espetro_attack".equals(targetTeam.m_5758_()) || "DEFEND".equals(localTeam) && "espetro_defend".equals(targetTeam.m_5758_());
    }

    private static boolean isEspetroTeam(String teamId) {
        return "espetro_attack".equals(teamId) || "espetro_defend".equals(teamId);
    }

    private static boolean isLookedAtWithinDistance(Player viewer, Player target, float partialTick) {
        double maxDistance = ClientTacticalState.getTeammateNameTagDistance();
        if (viewer.m_20280_(target) > maxDistance * maxDistance) {
            return false;
        }
        if (!viewer.m_142582_(target)) {
            return false;
        }
        Vec3 eye = viewer.m_20299_(partialTick);
        Vec3 look = viewer.m_20252_(partialTick).m_82541_();
        Vec3 end = eye.m_82549_(look.m_82490_(maxDistance));
        AABB targetBox = target.m_20191_().m_82400_(0.45);
        return targetBox.m_82371_(eye, end).isPresent();
    }
}

