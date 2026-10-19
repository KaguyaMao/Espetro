package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.network.GovernanceActionPacket;
import org.espetro.network.GovernanceStatePacket;
import org.espetro.network.MatchStatsActionPacket;
import org.espetro.network.MatchStatsSyncPacket;
import org.espetro.network.NetworkManager;
import org.espetro.client.aui.GuiElement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Two-column per-round scoreboard. Rows are grouped by squad; unsquadded and
 * offline players are sorted alphabetically at the bottom. Right-click opens
 * the server-authoritative squad context menu.
 */
public final class MatchScoreboardScreen extends EspetroMenuScreen {

    private static MatchStatsSyncPacket latestStats = new MatchStatsSyncPacket(List.of());

    private final Screen parent;
    private final List<HitRow> hitRows = new ArrayList<>();
    private final List<GovernanceHit> governanceHits = new ArrayList<>();
    private MatchStatsSyncPacket.Row contextRow;
    private int contextX;
    private int contextY;

    public MatchScoreboardScreen(Screen parent) {
        super(Component.literal("玩家分数板"));
        this.parent = parent;
    }

    public Screen getParent() {
        return parent;
    }

    public static void updateStats(MatchStatsSyncPacket packet) {
        latestStats = packet == null ? new MatchStatsSyncPacket(List.of()) : packet;
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof MatchScoreboardScreen screen) {
            screen.contextRow = null;
        }
    }

    /** Kept for callers; state lives in {@link ClientGovernanceState}. */
    public static void updateGovernance(GovernanceStatePacket packet) {
        ClientGovernanceState.update(packet);
    }

    public static String nameFor(UUID uuid) {
        if (uuid == null) return "无";
        for (MatchStatsSyncPacket.Row row : latestStats.rows) {
            if (uuid.equals(row.uuid)) return row.name;
        }
        return uuid.toString().substring(0, 8);
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        int top = 8;
        root.addChild(new ScoreboardCanvas(width, height));
        root.addChild(EspetroAuiWidgets.button(width - 50, top, 42, 18,
            "返回", this::onClose));
    }

    private final class ScoreboardCanvas extends GuiElement {
        private ScoreboardCanvas(int canvasWidth, int canvasHeight) {
            super(0, 0, canvasWidth, canvasHeight);
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int drawWidth, int drawHeight,
                         int mouseX, int mouseY, float partialTick) {
            hitRows.clear();
            governanceHits.clear();
            UiPaint.textCentered(graphics, "本回合玩家分数板", 0, 8, width,
                UiTheme.GOLD, UiTheme.TYPE_TITLE, true);
            renderTeamColumn(graphics, "ATTACK", 6, 34, width / 2 - 9,
                UiTheme.ATTACK, mouseX, mouseY);
            renderTeamColumn(graphics, "DEFEND", width / 2 + 3, 34,
                width / 2 - 9, UiTheme.DEFEND, mouseX, mouseY);
            renderGovernance(graphics, mouseX, mouseY);
            renderContextMenu(graphics, mouseX, mouseY);
            super.draw(graphics, x, y, drawWidth, drawHeight, mouseX, mouseY, partialTick);
        }
    }

    /** 微标签：小号、第三级文字色（中文不加字距）。 */
    private static void microAt(GuiGraphics graphics, String value, int x, int y) {
        UiPaint.textScaled(graphics, value, x, y, UiTheme.TEXT_THIRD, UiTheme.TYPE_MICRO, false);
    }

    /** 右对齐微标签：给定列的右边界。 */
    private static void microRight(GuiGraphics graphics, String value, int right, int y) {
        int textWidth = Math.round(Minecraft.getInstance().font.width(value) * UiTheme.TYPE_MICRO);
        microAt(graphics, value, right - textWidth, y);
    }

    private static String localPlayerName() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player == null ? null : mc.player.getName().getString();
    }

    private static int teamWash(String team) {
        return "ATTACK".equals(team) ? UiTheme.ATTACK_SOFT : UiTheme.DEFEND_SOFT;
    }

    private void renderTeamColumn(GuiGraphics graphics, String team, int x, int y, int w, int accent,
                                  int mouseX, int mouseY) {
        int panelH = height - 8 - y;
        if (panelH <= 0 || w <= 0) {
            return;
        }
        UiPaint.card(graphics, x, y, w, panelH, UiTheme.RADIUS_CARD,
            UiTheme.CARD_TOP, UiTheme.CARD_BOTTOM);
        UiPaint.accentBarBottom(graphics, x + 3, y + panelH - 3, w - 6, accent);

        // 阵营标题 + 表头（微标签）
        UiPaint.textShadowed(graphics, EspetroAuiWidgets.teamName(team), x + 8, y + 7, accent);
        int headerY = y + 21;
        microAt(graphics, "玩家", x + 8, headerY);
        microRight(graphics, "击杀", x + w - 69, headerY);
        microRight(graphics, "死亡", x + w - 38, headerY);
        microRight(graphics, "职业", x + w - 16, headerY);
        UiPaint.divider(graphics, x + 6, y + 30, w - 12);

        List<MatchStatsSyncPacket.Row> rows = latestStats.rows.stream()
            .filter(r -> team.equals(r.team))
            .sorted(Comparator
                .comparing((MatchStatsSyncPacket.Row r) -> !r.online || r.squadId < 0)
                .thenComparing(r -> r.squadName == null ? "" : r.squadName,
                    String.CASE_INSENSITIVE_ORDER)
                .thenComparing(r -> r.name == null ? "" : r.name,
                    String.CASE_INSENSITIVE_ORDER))
            .toList();

        String selfName = localPlayerName();
        int rowY = y + 33;
        String previousSquad = null;
        for (MatchStatsSyncPacket.Row row : rows) {
            if (rowY > height - 78) break;
            String group = row.online && row.squadId >= 0 ? row.squadName : "未编组/离线";
            if (!Objects.equals(group, previousSquad)) {
                UiPaint.roundRect(graphics, x + 4, rowY, w - 8, 11,
                    UiTheme.RADIUS_INNER, UiTheme.SURFACE_2);
                UiPaint.textScaled(graphics, group == null || group.isBlank() ? "小队" : group,
                    x + 8, rowY + 2, UiTheme.GOLD, UiTheme.TYPE_MICRO, false);
                rowY += 12;
                previousSquad = group;
            }

            boolean self = selfName != null && selfName.equals(row.name);
            boolean hovered = mouseX >= x + 4 && mouseX < x + w - 4
                && mouseY >= rowY && mouseY < rowY + 12;
            int rowFill = self ? teamWash(team) : (hovered ? UiTheme.SURFACE_2 : 0);
            if (rowFill != 0) {
                UiPaint.roundRect(graphics, x + 4, rowY, w - 8, 12,
                    UiTheme.RADIUS_INNER, rowFill);
                if (self) {
                    UiPaint.accentBarLeft(graphics, x + 5, rowY + 3, 6, accent);
                }
            }

            int color = row.online ? UiTheme.TEXT : UiTheme.TEXT_DISABLED;
            String name = font.plainSubstrByWidth(row.name, Math.max(20, w - 105));
            UiPaint.textShadowed(graphics, name, x + 8, rowY + 2, color);
            UiPaint.textShadowed(graphics, Integer.toString(row.kills),
                x + w - 83, rowY + 2, row.online ? UiTheme.TEXT_SECOND : UiTheme.TEXT_DISABLED);
            UiPaint.textShadowed(graphics, Integer.toString(row.deaths),
                x + w - 51, rowY + 2, row.online ? UiTheme.TEXT_SECOND : UiTheme.TEXT_DISABLED);
            // Prefer path (IconImage) then jar slug (icon), then classId fallback.
            ResourceLocation icon = RoleIconResources.resolveForScoreboard(
                row.classIconImage, row.classIcon, row.classId);
            if (icon != null) {
                // Full 128×128 UV → 11×11 screen (same as deploy class buttons).
                int iconSize = 11;
                int iconX = x + w - 27;
                int iconY = rowY;
                graphics.blit(icon, iconX, iconY, iconSize, iconSize,
                    0.0f, 0.0f,
                    RoleIconResources.TEXTURE_SIZE, RoleIconResources.TEXTURE_SIZE,
                    RoleIconResources.TEXTURE_SIZE, RoleIconResources.TEXTURE_SIZE);
            } else {
                UiPaint.textShadowed(graphics,
                    row.classId == null || row.classId.isBlank() ? "-" : "●",
                    x + w - 24, rowY + 2, UiTheme.TEXT_THIRD);
            }
            UiPaint.hairline(graphics, x + 8, rowY + 12, Math.max(0, w - 16), UiTheme.HAIRLINE);
            hitRows.add(new HitRow(x + 4, rowY, w - 8, 12, row));
            rowY += 12;
        }
    }

    private void renderGovernance(GuiGraphics graphics, int mouseX, int mouseY) {
        String myTeam = ClientGameState.getPlayerTeam();
        if (myTeam == null) return;
        GovernanceStatePacket.TeamState state = ClientGovernanceState.forTeam(myTeam);
        if (state == null || "IDLE".equals(state.state)) return;

        int barH = 62;
        int w = Math.min(420, width - 24);
        int x = (width - w) / 2;
        int y = height - barH - 8;
        UiPaint.card(graphics, x, y, w, barH, UiTheme.RADIUS_CARD,
            UiTheme.CARD_TOP, UiTheme.CARD_BOTTOM);
        UiPaint.accentBarBottom(graphics, x + 3, y + barH - 3, w - 6, UiTheme.WARNING);

        String stateLabel = switch (state.state) {
            case "IMPEACHMENT_VOTE" -> "弹劾投票";
            case "VACANCY_VOLUNTEER" -> "指挥官空缺·志愿";
            case "VACANCY_VOTE" -> "空缺公投";
            default -> state.state;
        };
        UiPaint.textCentered(graphics, stateLabel, x, y + 6, w,
            UiTheme.GOLD, UiTheme.TYPE_HEADING, true);
        String seconds = ClientGovernanceState.secondsLeft(state) + "s";
        UiPaint.textShadowed(graphics, seconds,
            x + w - font.width(seconds) - 12, y + 7, UiTheme.WARNING);

        if ("VACANCY_VOLUNTEER".equals(state.state)) {
            UiPaint.textCentered(graphics, "小队长请按 J 打开战术面板点击「志愿补位」",
                x, y + 24, w, UiTheme.TEXT_SECOND, UiTheme.TYPE_BODY, true);
            String volunteers = "志愿者: " + state.volunteers.stream()
                .map(MatchScoreboardScreen::nameFor).reduce((a, b) -> a + ", " + b).orElse("暂无");
            UiPaint.textCentered(graphics, volunteers, x, y + 38, w,
                UiTheme.TEXT_THIRD, UiTheme.TYPE_BODY, true);
            return;
        }

        List<UUID> candidates = new ArrayList<>();
        List<String> prefixes = new ArrayList<>();
        if ("IMPEACHMENT_VOTE".equals(state.state)) {
            if (state.commander != null) {
                candidates.add(state.commander);
                prefixes.add("原指挥官");
            }
            if (state.challenger != null) {
                candidates.add(state.challenger);
                prefixes.add("挑战者");
            }
        } else if ("VACANCY_VOTE".equals(state.state)) {
            for (UUID v : state.volunteers) {
                candidates.add(v);
                prefixes.add("志愿者");
            }
        }

        if (candidates.isEmpty()) {
            UiPaint.textCentered(graphics, "等待候选人…", x, y + 28, w,
                UiTheme.TEXT_THIRD, UiTheme.TYPE_BODY, true);
            return;
        }

        int slotW = Math.min(180, (w - 16) / candidates.size());
        int totalW = slotW * candidates.size();
        int startX = x + (w - totalW) / 2;
        int slotY = y + 18;
        int slotH = 34;

        GovernanceActionPacket.Action action = "IMPEACHMENT_VOTE".equals(state.state)
            ? GovernanceActionPacket.Action.VOTE_IMPEACHMENT
            : GovernanceActionPacket.Action.VOTE_VACANCY;

        for (int i = 0; i < candidates.size(); i++) {
            UUID candidate = candidates.get(i);
            int sx = startX + i * slotW + 2;
            int sw = slotW - 4;
            boolean mine = ClientGovernanceState.isMyVote(state, candidate);
            boolean hovered = mouseX >= sx && mouseX < sx + sw
                && mouseY >= slotY && mouseY < slotY + slotH;
            if (mine) {
                UiPaint.roundRect(graphics, sx, slotY, sw, slotH,
                    UiTheme.RADIUS_CONTROL, UiTheme.POSITIVE_SOFT);
                UiPaint.glow(graphics, sx, slotY, sw, slotH, UiTheme.RADIUS_CONTROL,
                    UiTheme.POSITIVE);
                UiPaint.strokeRoundRect(graphics, sx, slotY, sw, slotH,
                    UiTheme.RADIUS_CONTROL, UiTheme.POSITIVE);
            } else {
                UiPaint.roundRect(graphics, sx, slotY, sw, slotH,
                    UiTheme.RADIUS_CONTROL, UiTheme.CARD_TOP);
                UiPaint.strokeRoundRect(graphics, sx, slotY, sw, slotH,
                    UiTheme.RADIUS_CONTROL, hovered ? UiTheme.ACCENT_LINE : UiTheme.HAIRLINE);
            }

            int votes = ClientGovernanceState.voteCount(state, candidate);
            String name = nameFor(candidate);
            String title = (mine ? "✓ " : "") + prefixes.get(i);
            UiPaint.textCentered(graphics, title, sx, slotY + 3, sw,
                mine ? UiTheme.POSITIVE : UiTheme.TEXT_SECOND, UiTheme.TYPE_BODY, true);
            UiPaint.textCentered(graphics, name, sx, slotY + 13, sw,
                UiTheme.TEXT, UiTheme.TYPE_BODY, true);
            UiPaint.textCentered(graphics, votes + " 票", sx, slotY + 21, sw,
                UiTheme.GOLD, UiTheme.TYPE_HEADING, true);

            governanceHits.add(new GovernanceHit(sx, slotY, sw, slotH, action, candidate));
        }
    }

    private void renderContextMenu(GuiGraphics graphics, int mouseX, int mouseY) {
        if (contextRow == null) return;
        int menuW = 76;
        int menuH = 34;
        int x = Math.min(contextX, width - menuW - 2);
        int y = Math.min(contextY, height - menuH - 2);
        UiPaint.card(graphics, x, y, menuW, menuH, UiTheme.RADIUS_CONTROL,
            UiTheme.CARD_TOP, UiTheme.CARD_BOTTOM);
        boolean canJoin = canForceJoin(contextRow);
        boolean canKick = canKick(contextRow);
        UiPaint.textShadowed(graphics, "拉进小队", x + 7, y + 5,
            canJoin ? UiTheme.POSITIVE : UiTheme.TEXT_DISABLED);
        UiPaint.textShadowed(graphics, "踢出小队", x + 7, y + 19,
            canKick ? UiTheme.NEGATIVE : UiTheme.TEXT_DISABLED);
        UiPaint.hairline(graphics, x + 5, y + 17, menuW - 10, UiTheme.HAIRLINE);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (contextRow != null && button == 0) {
            int x = Math.min(contextX, width - 78);
            int y = Math.min(contextY, height - 36);
            if (mouseX >= x && mouseX <= x + 76) {
                if (mouseY >= y && mouseY < y + 17 && canForceJoin(contextRow)) {
                    NetworkManager.sendMatchStatsAction(MatchStatsActionPacket.Action.FORCE_JOIN_SQUAD,
                        contextRow.uuid);
                    contextRow = null;
                    return true;
                }
                if (mouseY >= y + 17 && mouseY <= y + 34 && canKick(contextRow)) {
                    NetworkManager.sendMatchStatsAction(MatchStatsActionPacket.Action.KICK_FROM_SQUAD,
                        contextRow.uuid);
                    contextRow = null;
                    return true;
                }
            }
            contextRow = null;
        }
        if (button == 0 && handleGovernanceVote(mouseX, mouseY)) return true;
        if (button == 1) {
            for (HitRow hit : hitRows) {
                if (hit.contains(mouseX, mouseY)) {
                    contextRow = hit.row;
                    contextX = (int) mouseX;
                    contextY = (int) mouseY;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleGovernanceVote(double mouseX, double mouseY) {
        for (GovernanceHit hit : governanceHits) {
            if (hit.contains(mouseX, mouseY) && hit.candidate != null) {
                NetworkManager.sendGovernanceAction(hit.action, hit.candidate);
                return true;
            }
        }
        return false;
    }

    private boolean canForceJoin(MatchStatsSyncPacket.Row row) {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && row.online
            && !mc.player.getUUID().equals(row.uuid)
            && Objects.equals(ClientGameState.getPlayerTeam(), row.team)
            && ClientTacticalState.isLocalSquadLeader(mc.player.getName().getString())
            && row.squadId < 0;
    }

    private boolean canKick(MatchStatsSyncPacket.Row row) {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && row.online
            && !mc.player.getUUID().equals(row.uuid)
            && Objects.equals(ClientGameState.getPlayerTeam(), row.team)
            && ClientTacticalState.isLocalSquadLeader(mc.player.getName().getString())
            && row.squadId >= 0 && row.squadId == ClientTacticalState.getMySquadId();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record HitRow(int x, int y, int w, int h, MatchStatsSyncPacket.Row row) {
        boolean contains(double px, double py) {
            return px >= x && px < x + w && py >= y && py < y + h;
        }
    }

    private record GovernanceHit(int x, int y, int w, int h,
                                 GovernanceActionPacket.Action action, UUID candidate) {
        boolean contains(double px, double py) {
            return px >= x && px < x + w && py >= y && py < y + h;
        }
    }
}
