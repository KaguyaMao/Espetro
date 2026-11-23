package org.espetro.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import org.espetro.network.MatchStatsSyncPacket;
import org.espetro.network.UnifiedDeployScreenPacket.SquadMemberInfo;
import org.espetro.team.GamePhase;
import org.espetro.vehicle.SbwVehicleSeatResolver;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** In-match squad roster, heard speakers, compass and vanilla chat placement. */
public final class TacticalHud {
    public static final KeyMapping TOGGLE = new KeyMapping(
        "key.espetro.squad_hud", GLFW.GLFW_KEY_Y, "key.categories.espetro");
    private static final int PREVIOUS_MAX_ROSTER_WIDTH = 122;
    private static final long ROSTER_IDLE_NANOS = java.util.concurrent.TimeUnit.SECONDS.toNanos(10);
    private static final UiMotion.Value ROSTER_OPACITY = new UiMotion.Value(1f);
    private static long lastRosterUpdate = System.nanoTime();
    private static float compassMultiplier = 1f;
    private static final int ROW = 12;
    private static final int MAX_CHAT_LINES = 6;
    private static final String[] DIRECTIONS = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
    private static boolean expanded = true;
    private static List<Speaker> speakers = List.of();

    private TacticalHud() { }

    /** Client-only handoff from EsVoice's existing listener-specific speaker list. */
    public record Speaker(UUID id, String name, int channel) { }

    public static boolean inMatch() {
        Minecraft mc = Minecraft.getInstance();
        GamePhase phase = ClientGameState.getCurrentPhase();
        return mc != null && mc.level != null && mc.player != null
            && (phase == GamePhase.DEPLOYING || phase == GamePhase.BATTLE);
    }

    public static void updateSpeakers(List<Speaker> active) {
        speakers = List.copyOf(active);
    }

    public static void tick(Minecraft mc) {
        while (TOGGLE.consumeClick()) {
            if (mc.screen == null && inMatch()) expanded = !expanded;
        }
    }

    public static void reset() {
        expanded = true;
        speakers = List.of();
        rosterUpdated();
        ROSTER_OPACITY.snap(1f);
    }

    static void rosterUpdated() {
        lastRosterUpdate = System.nanoTime();
        ROSTER_OPACITY.to(1f, 180);
    }

    static void setCompassMultiplier(float multiplier) {
        compassMultiplier = multiplier;
    }

    public static boolean moveChat() {
        Minecraft mc = Minecraft.getInstance();
        // Focused history retains vanilla click and scroll coordinates.
        return inMatch() && !(mc.screen instanceof ChatScreen);
    }

    public static int chatLineLimit(int vanillaLines) {
        if (!moveChat()) return vanillaLines;
        Minecraft mc = Minecraft.getInstance();
        int height = mc.getWindow().getGuiScaledHeight();
        int availableHeight = height / 2 - chatTop(height);
        int lines = Math.max(1, availableHeight / chatLinePixels(mc));
        return Math.min(vanillaLines, Math.min(MAX_CHAT_LINES, lines));
    }

    public static void positionChat(CustomizeGuiOverlayEvent.Chat event) {
        if (!moveChat()) return;
        Minecraft mc = Minecraft.getInstance();
        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();
        double scale = mc.gui.getChat().getScale();
        int baseline = height / 2;
        // Forge divides its outer pose translation by chat scale.
        int vanillaBaseline = height - 40;
        event.setPosY(vanillaBaseline + (int) Math.round((baseline - vanillaBaseline) * scale));
        event.setPosX(Math.max(2, (int) Math.round(width * 0.032 - 4 * scale)));
    }

    private static int chatTop(int height) {
        if (OutpostSupplyHud.isShowing()) return OutpostSupplyHud.bottomEdge() + 6;
        int naturalTop = Math.round(height * 0.12f);
        return FobSupplyHud.isShowing() ? Math.max(naturalTop, FobSupplyHud.bottomEdge(height) + 6) : naturalTop;
    }

    private static int chatLinePixels(Minecraft mc) {
        return Math.max(1, (int) Math.ceil(9 * (1 + mc.options.chatLineSpacing().get()) * mc.gui.getChat().getScale()));
    }

    static void render(GuiGraphics graphics, Minecraft mc, float partialTick) {
        if (!inMatch() || mc.player.isSpectator()) return;
        drawCompass(graphics, mc, partialTick);
        drawRoster(graphics, mc);
    }

    private static void drawRoster(GuiGraphics g, Minecraft mc) {
        var squad = ClientTacticalState.localSquad();
        List<SquadMemberInfo> members = squad == null ? List.of() : squad.members;
        List<Speaker> active = speakers;
        int inset = Math.max(5, Math.round(g.guiWidth() * 0.015f));
        boolean mounted = SbwVehicleSeatResolver.isSupportedVehicle(mc.player.getRootVehicle());
        int bottomInset = Math.max(7, Math.round(g.guiHeight() * 0.025f));
        if (mounted) bottomInset = Math.max(bottomInset, 31);
        else if (mc.gameMode != null && mc.gameMode.canHurtPlayer()
                && mc.player.getHealth() < mc.player.getMaxHealth()) bottomInset = Math.max(bottomInset, 28);
        int bottom = g.guiHeight() - bottomInset;
        int maxWidth = Math.max(52, Math.min(PREVIOUS_MAX_ROSTER_WIDTH * 3 / 2, g.guiWidth() - inset * 2));
        Component hint = Component.translatable(expanded ? "hud.espetro.squad.hide" : "hud.espetro.squad.show",
            TOGGLE.getTranslatedKeyMessage());
        String heading = squad == null ? "" : "[" + squad.displayId + "] " + squad.name;
        int width = Math.max(52, mc.font.width(hint) + 10);
        if (expanded) {
            width = Math.max(width, mc.font.width(heading) + 10);
            for (SquadMemberInfo member : members) width = Math.max(width, mc.font.width(member.playerName) + 39);
        }
        width = Math.min(Math.round(Math.min(width, PREVIOUS_MAX_ROSTER_WIDTH) * 1.5f), maxWidth);
        int x = anchoredX(g.guiWidth(), width, inset, mounted);
        int y = bottom;
        int visibleRows = 0;
        if (squad != null) {
            if (System.nanoTime() - lastRosterUpdate >= ROSTER_IDLE_NANOS) ROSTER_OPACITY.to(0.6f, 250);
            float opacity = ROSTER_OPACITY.get();
            visibleRows = expanded ? Math.min(members.size(), Math.max(0, (bottom - 54) / ROW)) : 0;
            y = bottom - ROW * (visibleRows + (expanded ? 2 : 1));
            g.fill(x, y, x + width, bottom, UiTheme.alpha(UiTheme.HUD_SURFACE, opacity));
            if (expanded) {
                drawLabel(g, mc.font, heading,
                    x + 5, y + 2, width - 10, UiTheme.alpha(UiTheme.WORLD_POSITIVE, opacity));
                Map<UUID, Speaker> byPlayer = new java.util.HashMap<>();
                for (Speaker speaker : active) byPlayer.put(speaker.id(), speaker);
                for (int i = 0; i < visibleRows; i++) {
                    SquadMemberInfo member = members.get(i);
                    int rowY = y + ROW * (i + 1);
                    int group = fireteamColor(member.fireteam);
                    if (group != 0) g.fill(x + 3, rowY + 1, x + 5, rowY + ROW, UiTheme.alpha(group, opacity));
                    Speaker speaking = byPlayer.get(member.uuid);
                    int color = UiTheme.alpha(UiTheme.WORLD_TEXT, opacity);
                    drawHead(g, mc, member.uuid, x + 8, rowY + 2, opacity);
                    drawRole(g, member.uuid, member.className, x + 19, rowY + 1, color);
                    drawLabel(g, mc.font, member.playerName, x + 32, rowY + 2, width - 50, color);
                    if (speaking != null) drawSpeaker(g, x + width - 14, rowY + 2,
                        UiTheme.alpha(voiceColor(speaking.channel()), opacity));
                }
            }
            drawLabel(g, mc.font, hint.getString(), x + 5, bottom - ROW + 2, width - 10,
                UiTheme.alpha(UiTheme.WORLD_MUTED, opacity));
        }

        int voiceWidth = Math.min(maxWidth, Math.max(width, active.stream()
            .mapToInt(speaker -> mc.font.width(speaker.name()) + 61).max().orElse(0)));
        int voiceX = anchoredX(g.guiWidth(), voiceWidth, inset, mounted);
        int voiceY = y - 3;
        for (int i = active.size() - 1; i >= 0 && voiceY >= 54 + ROW; i--) {
            Speaker speaker = active.get(i);
            voiceY -= ROW;
            int color = voiceColor(speaker.channel());
            Integer squadNumber = ClientTacticalState.squadNumber(speaker.id());
            if (squadNumber == null) squadNumber = MatchScoreboardScreen.squadNumberFor(speaker.id());
            String badge = squadNumber == null ? "–" : Integer.toString(squadNumber);
            g.fill(voiceX, voiceY, voiceX + voiceWidth, voiceY + ROW - 1, UiTheme.HUD_SURFACE);
            drawLabel(g, mc.font, badge, voiceX + 3, voiceY + 2, 15, color);
            drawHead(g, mc, speaker.id(), voiceX + 19, voiceY + 2, 1f);
            drawRole(g, speaker.id(), ClientTacticalState.className(speaker.id()), voiceX + 30, voiceY + 1, color);
            drawLabel(g, mc.font, speaker.name(), voiceX + 43, voiceY + 2, voiceWidth - 61, color);
            drawSpeaker(g, voiceX + voiceWidth - 14, voiceY + 2, color);
        }
    }

    private static int anchoredX(int screenWidth, int width, int inset, boolean right) {
        return right ? screenWidth - inset - width : inset;
    }

    static void drawHead(GuiGraphics g, Minecraft mc, UUID player, int x, int y, float opacity) {
        var connection = mc.getConnection();
        var info = connection == null ? null : connection.getPlayerInfo(player);
        ResourceLocation skin = info == null ? DefaultPlayerSkin.getDefaultSkin(player) : info.getSkinLocation();
        g.setColor(1f, 1f, 1f, opacity);
        try {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            PlayerFaceRenderer.draw(g, skin, x, y, 8, true, false);
        } finally {
            RenderSystem.disableBlend();
            UiPaint.tint(g, UiTheme.TEXTURE_WHITE);
        }
    }

    static void drawRole(GuiGraphics g, UUID player, String className, int x, int y, int color) {
        MatchStatsSyncPacket.Row stats = MatchScoreboardScreen.statsFor(player);
        ResourceLocation icon = stats == null ? null
            : RoleIconResources.resolveForScoreboard(stats.classIconImage, stats.classIcon, stats.classId);
        if (icon == null) icon = RoleIconResources.resolve(className);
        drawRoleIcon(g, icon, x, y, color);
    }

    static void drawRoleIcon(GuiGraphics g, ResourceLocation icon, int x, int y, int color) {
        if (icon != null) {
            UiPaint.tint(g, color);
            int size = RoleIconResources.TEXTURE_SIZE;
            try {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                g.blit(icon, x, y, 10, 10, 0, 0, size, size, size, size);
            } finally {
                RenderSystem.disableBlend();
                UiPaint.tint(g, UiTheme.TEXTURE_WHITE);
            }
        } else {
            g.fill(x + 4, y + 1, x + 6, y + 9, color);
            g.fill(x + 2, y + 3, x + 8, y + 7, color);
        }
    }

    private static void drawSpeaker(GuiGraphics g, int x, int y, int color) {
        g.fill(x, y + 3, x + 3, y + 7, color);
        g.fill(x + 3, y + 2, x + 4, y + 8, color);
        g.fill(x + 4, y + 1, x + 5, y + 9, color);
        g.fill(x + 6, y + 3, x + 7, y + 7, color);
        g.fill(x + 8, y + 2, x + 9, y + 8, color);
    }

    private static void drawLabel(GuiGraphics g, Font font, String text, int x, int y, int width, int color) {
        int ellipsisWidth = font.width("…");
        if (width < ellipsisWidth) return;
        String label = font.width(text) <= width ? text : font.plainSubstrByWidth(text, width - ellipsisWidth) + "…";
        g.drawString(font, label, x, y, color, true);
    }

    static int fireteamColor(byte team) {
        return switch (team) {
            case 0 -> UiTheme.WORLD_POSITIVE;
            case 1 -> UiTheme.HUD_FIRETEAM_PURPLE;
            case 2 -> UiTheme.HUD_CYAN;
            default -> UiTheme.TRANSPARENT;
        };
    }

    private static int voiceColor(int channel) {
        return switch (channel) {
            case 1 -> UiTheme.WORLD_POSITIVE;
            case 2 -> UiTheme.WORLD_WARNING;
            default -> UiTheme.HUD_VOICE_BLUE;
        };
    }

    private static void drawCompass(GuiGraphics g, Minecraft mc, float partialTick) {
        float heading = Mth.positiveModulo(mc.player.getViewYRot(partialTick) + 180f, 360f);
        int center = g.guiWidth() / 2;
        int halfWidth = Math.min(128, Math.max(56, g.guiWidth() / 5));
        int y = g.guiHeight() - 26;
        String bearing = Math.floorMod(Math.round(heading), 360) + "°";
        int bearingHalf = (mc.font.width(bearing) + 9) / 2;
        float scale = 0.5f * compassMultiplier;
        int bottom = y + 22;
        g.pose().pushPose();
        g.pose().translate(center, bottom, 0);
        g.pose().scale(scale, scale, 1f);
        g.pose().translate(-center, -bottom, 0);
        try {
            int first = Mth.floor((heading - 60) / 15) * 15;
            for (int angle = first; angle <= heading + 60; angle += 15) {
                int x = center + Math.round((angle - heading) * halfWidth / 60);
                if (x < center - halfWidth || x >= center + halfWidth) continue;
                int azimuth = Math.floorMod(angle, 360);
                boolean cardinal = azimuth % 45 == 0;
                g.fill(x, y + (cardinal ? 3 : 6), x + 1, y + 10, UiTheme.WORLD_MUTED);
                String label = cardinal ? DIRECTIONS[azimuth / 45] : Integer.toString(azimuth);
                int labelHalf = (mc.font.width(label) + 1) / 2;
                int distance = Math.abs(x - center);
                if (distance >= bearingHalf + labelHalf + 2 && distance + labelHalf < halfWidth) {
                    g.drawCenteredString(mc.font, label, x, y + 12, UiTheme.WORLD_MUTED);
                }
            }
            g.fill(center - 3, y + 1, center + 4, y + 3, UiTheme.WORLD_TEXT);
            g.fill(center - 2, y + 3, center + 3, y + 5, UiTheme.WORLD_TEXT);
            g.fill(center - 1, y + 5, center + 2, y + 7, UiTheme.WORLD_TEXT);
            g.fill(center, y + 7, center + 1, y + 9, UiTheme.WORLD_TEXT);
            g.drawCenteredString(mc.font, bearing, center, y + 12, UiTheme.WORLD_TEXT);
        } finally {
            g.pose().popPose();
        }
    }
}
