/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.ClientGovernanceState;
import org.espetro.client.gui.ClientTacticalState;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.client.gui.RoleIconResources;
import org.espetro.network.GovernanceActionPacket;
import org.espetro.network.GovernanceStatePacket;
import org.espetro.network.MatchStatsActionPacket;
import org.espetro.network.MatchStatsSyncPacket;
import org.espetro.network.NetworkManager;

public final class MatchScoreboardScreen
extends EspetroMenuScreen {
    private static MatchStatsSyncPacket latestStats = new MatchStatsSyncPacket(List.of());
    private final Screen parent;
    private final List<HitRow> hitRows = new ArrayList<HitRow>();
    private final List<GovernanceHit> governanceHits = new ArrayList<GovernanceHit>();
    private MatchStatsSyncPacket.Row contextRow;
    private int contextX;
    private int contextY;

    public MatchScoreboardScreen(Screen parent) {
        super(Component.m_237113_("\u73a9\u5bb6\u5206\u6570\u677f"));
        this.parent = parent;
    }

    public Screen getParent() {
        return this.parent;
    }

    public static void updateStats(MatchStatsSyncPacket packet) {
        latestStats = packet == null ? new MatchStatsSyncPacket(List.of()) : packet;
        Minecraft mc = Minecraft.m_91087_();
        Screen screen = mc.f_91080_;
        if (screen instanceof MatchScoreboardScreen) {
            MatchScoreboardScreen screen2 = (MatchScoreboardScreen)screen;
            screen2.contextRow = null;
        }
    }

    public static void updateGovernance(GovernanceStatePacket packet) {
        ClientGovernanceState.update(packet);
    }

    public static String nameFor(UUID uuid) {
        if (uuid == null) {
            return "\u65e0";
        }
        for (MatchStatsSyncPacket.Row row : MatchScoreboardScreen.latestStats.rows) {
            if (!uuid.equals(row.uuid)) continue;
            return row.name;
        }
        return uuid.toString().substring(0, 8);
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        int top = 8;
        root.addChild(new ScoreboardCanvas(this.f_96543_, this.f_96544_));
        root.addChild(EspetroAuiWidgets.button(this.f_96543_ - 50, top, 42, 18, "\u8fd4\u56de", this::m_7379_));
    }

    private void renderTeamColumn(GuiGraphics graphics, String team, int x, int y, int w, int accent) {
        graphics.m_280509_(x, y, x + w, this.f_96544_ - 8, -1340597475);
        graphics.m_280637_(x, y, w, this.f_96544_ - 8 - y, accent);
        String title = EspetroAuiWidgets.teamPrefix(team) + EspetroAuiWidgets.teamName(team);
        graphics.m_280488_(this.f_96547_, title, x + 5, y + 5, 0xFFFFFF);
        graphics.m_280488_(this.f_96547_, "\u73a9\u5bb6", x + 5, y + 18, -4209723);
        graphics.m_280488_(this.f_96547_, "\u51fb\u6740", x + w - 92, y + 18, -4209723);
        graphics.m_280488_(this.f_96547_, "\u6b7b\u4ea1", x + w - 61, y + 18, -4209723);
        graphics.m_280488_(this.f_96547_, "\u804c\u4e1a", x + w - 30, y + 18, -4209723);
        List<MatchStatsSyncPacket.Row> rows = MatchScoreboardScreen.latestStats.rows.stream().filter(r -> team.equals(r.team)).sorted(Comparator.comparing(r -> !r.online || r.squadId < 0).thenComparing(r -> r.squadName == null ? "" : r.squadName, String.CASE_INSENSITIVE_ORDER).thenComparing(r -> r.name == null ? "" : r.name, String.CASE_INSENSITIVE_ORDER)).toList();
        int rowY = y + 31;
        String previousSquad = null;
        for (MatchStatsSyncPacket.Row row : rows) {
            int color;
            String group;
            if (rowY > this.f_96544_ - 78) break;
            String string = group = row.online && row.squadId >= 0 ? row.squadName : "\u672a\u7f16\u7ec4/\u79bb\u7ebf";
            if (!Objects.equals(group, previousSquad)) {
                graphics.m_280509_(x + 3, rowY, x + w - 3, rowY + 11, 1882602302);
                graphics.m_280488_(this.f_96547_, "\u00a76" + (group == null || group.isBlank() ? "\u5c0f\u961f" : group), x + 6, rowY + 2, 0xFFFFFF);
                rowY += 12;
                previousSquad = group;
            }
            int n = color = row.online ? -921103 : -8947849;
            if ((rowY / 12 & 1) == 0) {
                graphics.m_280509_(x + 3, rowY, x + w - 3, rowY + 12, 0x30111111);
            }
            String name = this.f_96547_.m_92834_(row.name, Math.max(20, w - 105));
            graphics.m_280488_(this.f_96547_, name, x + 6, rowY + 2, color);
            graphics.m_280488_(this.f_96547_, Integer.toString(row.kills), x + w - 83, rowY + 2, color);
            graphics.m_280488_(this.f_96547_, Integer.toString(row.deaths), x + w - 51, rowY + 2, color);
            ResourceLocation icon = RoleIconResources.resolveForScoreboard(row.classIconImage, row.classIcon, row.classId);
            if (icon != null) {
                int iconSize = 11;
                int iconX = x + w - 27;
                int iconY = rowY;
                graphics.m_280411_(icon, iconX, iconY, iconSize, iconSize, 0.0f, 0.0f, 128, 128, 128, 128);
            } else {
                graphics.m_280488_(this.f_96547_, row.classId == null || row.classId.isBlank() ? "-" : "\u25cf", x + w - 24, rowY + 2, color);
            }
            this.hitRows.add(new HitRow(x + 3, rowY, w - 6, 12, row));
            rowY += 12;
        }
    }

    private void renderGovernance(GuiGraphics graphics, int mouseX, int mouseY) {
        String myTeam = ClientGameState.getPlayerTeam();
        if (myTeam == null) {
            return;
        }
        GovernanceStatePacket.TeamState state = ClientGovernanceState.forTeam(myTeam);
        if (state == null || "IDLE".equals(state.state)) {
            return;
        }
        int barH = 62;
        int w = Math.min(420, this.f_96543_ - 24);
        int x = (this.f_96543_ - w) / 2;
        int y = this.f_96544_ - barH - 8;
        graphics.m_280509_(x, y, x + w, this.f_96544_ - 8, -266791148);
        graphics.m_280637_(x, y, w, this.f_96544_ - 8 - y, -18355);
        String stateLabel = switch (state.state) {
            case "IMPEACHMENT_VOTE" -> "\u5f39\u52be\u6295\u7968";
            case "VACANCY_VOLUNTEER" -> "\u6307\u6325\u5b98\u7a7a\u7f3a\u00b7\u5fd7\u613f";
            case "VACANCY_VOTE" -> "\u7a7a\u7f3a\u516c\u6295";
            default -> state.state;
        };
        graphics.m_280137_(this.f_96547_, "\u00a76" + stateLabel + " \u00a7e" + ClientGovernanceState.secondsLeft(state) + "s", this.f_96543_ / 2, y + 4, 0xFFFFFF);
        if ("VACANCY_VOLUNTEER".equals(state.state)) {
            graphics.m_280137_(this.f_96547_, "\u00a77\u5c0f\u961f\u957f\u8bf7\u6309 J \u6253\u5f00\u6218\u672f\u9762\u677f\u70b9\u51fb\u300c\u5fd7\u613f\u8865\u4f4d\u300d", this.f_96543_ / 2, y + 20, -2039584);
            graphics.m_280137_(this.f_96547_, "\u5fd7\u613f\u8005: " + state.volunteers.stream().map(MatchScoreboardScreen::nameFor).reduce((a, b) -> a + ", " + b).orElse("\u6682\u65e0"), this.f_96543_ / 2, y + 36, -5197648);
            return;
        }
        ArrayList<UUID> candidates = new ArrayList<UUID>();
        ArrayList<String> prefixes = new ArrayList<String>();
        if ("IMPEACHMENT_VOTE".equals(state.state)) {
            if (state.commander != null) {
                candidates.add(state.commander);
                prefixes.add("\u539f\u6307\u6325\u5b98");
            }
            if (state.challenger != null) {
                candidates.add(state.challenger);
                prefixes.add("\u6311\u6218\u8005");
            }
        } else if ("VACANCY_VOTE".equals(state.state)) {
            for (UUID v : state.volunteers) {
                candidates.add(v);
                prefixes.add("\u5fd7\u613f\u8005");
            }
        }
        if (candidates.isEmpty()) {
            graphics.m_280137_(this.f_96547_, "\u00a77\u7b49\u5f85\u5019\u9009\u4eba\u2026", this.f_96543_ / 2, y + 28, -6710887);
            return;
        }
        int slotW = Math.min(180, (w - 16) / candidates.size());
        int totalW = slotW * candidates.size();
        int startX = x + (w - totalW) / 2;
        int slotY = y + 18;
        int slotH = 34;
        GovernanceActionPacket.Action action = "IMPEACHMENT_VOTE".equals(state.state) ? GovernanceActionPacket.Action.VOTE_IMPEACHMENT : GovernanceActionPacket.Action.VOTE_VACANCY;
        for (int i = 0; i < candidates.size(); ++i) {
            int bg;
            boolean hovered;
            UUID candidate = (UUID)candidates.get(i);
            int sx = startX + i * slotW + 2;
            int sw = slotW - 4;
            boolean mine = ClientGovernanceState.isMyVote(state, candidate);
            boolean bl = hovered = mouseX >= sx && mouseX < sx + sw && mouseY >= slotY && mouseY < slotY + slotH;
            int n = mine ? -534099406 : (bg = hovered ? -532660152 : -534239188);
            int border = mine ? -9449577 : (hovered ? -18355 : -10066330);
            graphics.m_280509_(sx, slotY, sx + sw, slotY + slotH, bg);
            graphics.m_280637_(sx, slotY, sw, slotH, border);
            int votes = ClientGovernanceState.voteCount(state, candidate);
            String name = MatchScoreboardScreen.nameFor(candidate);
            String title = (mine ? "\u00a7a\u2713 " : "") + "\u00a7f" + (String)prefixes.get(i);
            graphics.m_280137_(this.f_96547_, title, sx + sw / 2, slotY + 4, 0xFFFFFF);
            graphics.m_280137_(this.f_96547_, "\u00a7e" + name, sx + sw / 2, slotY + 14, 0xFFFFFF);
            graphics.m_280137_(this.f_96547_, "\u00a7b" + votes + " \u7968", sx + sw / 2, slotY + 24, 0xFFFFFF);
            this.governanceHits.add(new GovernanceHit(sx, slotY, sw, slotH, action, candidate));
        }
    }

    private void renderContextMenu(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.contextRow == null) {
            return;
        }
        int menuW = 76;
        int menuH = 34;
        int x = Math.min(this.contextX, this.f_96543_ - menuW - 2);
        int y = Math.min(this.contextY, this.f_96544_ - menuH - 2);
        graphics.m_280509_(x, y, x + menuW, y + menuH, -266330080);
        graphics.m_280637_(x, y, menuW, menuH, -8947849);
        boolean canJoin = this.canForceJoin(this.contextRow);
        boolean canKick = this.canKick(this.contextRow);
        graphics.m_280488_(this.f_96547_, (canJoin ? "\u00a7a" : "\u00a78") + "\u62c9\u8fdb\u5c0f\u961f", x + 6, y + 5, 0xFFFFFF);
        graphics.m_280488_(this.f_96547_, (canKick ? "\u00a7c" : "\u00a78") + "\u8e22\u51fa\u5c0f\u961f", x + 6, y + 19, 0xFFFFFF);
    }

    @Override
    public boolean m_6375_(double mouseX, double mouseY, int button) {
        if (this.contextRow != null && button == 0) {
            int x = Math.min(this.contextX, this.f_96543_ - 78);
            int y = Math.min(this.contextY, this.f_96544_ - 36);
            if (mouseX >= (double)x && mouseX <= (double)(x + 76)) {
                if (mouseY >= (double)y && mouseY < (double)(y + 17) && this.canForceJoin(this.contextRow)) {
                    NetworkManager.sendMatchStatsAction(MatchStatsActionPacket.Action.FORCE_JOIN_SQUAD, this.contextRow.uuid);
                    this.contextRow = null;
                    return true;
                }
                if (mouseY >= (double)(y + 17) && mouseY <= (double)(y + 34) && this.canKick(this.contextRow)) {
                    NetworkManager.sendMatchStatsAction(MatchStatsActionPacket.Action.KICK_FROM_SQUAD, this.contextRow.uuid);
                    this.contextRow = null;
                    return true;
                }
            }
            this.contextRow = null;
        }
        if (button == 0 && this.handleGovernanceVote(mouseX, mouseY)) {
            return true;
        }
        if (button == 1) {
            for (HitRow hit : this.hitRows) {
                if (!hit.contains(mouseX, mouseY)) continue;
                this.contextRow = hit.row;
                this.contextX = (int)mouseX;
                this.contextY = (int)mouseY;
                return true;
            }
        }
        return super.m_6375_(mouseX, mouseY, button);
    }

    private boolean handleGovernanceVote(double mouseX, double mouseY) {
        for (GovernanceHit hit : this.governanceHits) {
            if (!hit.contains(mouseX, mouseY) || hit.candidate == null) continue;
            NetworkManager.sendGovernanceAction(hit.action, hit.candidate);
            return true;
        }
        return false;
    }

    private boolean canForceJoin(MatchStatsSyncPacket.Row row) {
        Minecraft mc = Minecraft.m_91087_();
        return mc.f_91074_ != null && row.online && !mc.f_91074_.m_20148_().equals(row.uuid) && Objects.equals(ClientGameState.getPlayerTeam(), row.team) && ClientTacticalState.isLocalSquadLeader(mc.f_91074_.m_7755_().getString()) && row.squadId < 0;
    }

    private boolean canKick(MatchStatsSyncPacket.Row row) {
        Minecraft mc = Minecraft.m_91087_();
        return mc.f_91074_ != null && row.online && !mc.f_91074_.m_20148_().equals(row.uuid) && Objects.equals(ClientGameState.getPlayerTeam(), row.team) && ClientTacticalState.isLocalSquadLeader(mc.f_91074_.m_7755_().getString()) && row.squadId >= 0 && row.squadId == ClientTacticalState.getMySquadId();
    }

    @Override
    public void m_7379_() {
        Minecraft.m_91087_().m_91152_(this.parent);
    }

    @Override
    public boolean m_7043_() {
        return false;
    }

    private final class ScoreboardCanvas
    extends GuiElement {
        private ScoreboardCanvas(int canvasWidth, int canvasHeight) {
            super(0, 0, canvasWidth, canvasHeight);
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int drawWidth, int drawHeight, int mouseX, int mouseY, float partialTick) {
            MatchScoreboardScreen.this.hitRows.clear();
            MatchScoreboardScreen.this.governanceHits.clear();
            graphics.m_280137_(MatchScoreboardScreen.this.f_96547_, "\u00a76\u00a7l\u672c\u56de\u5408\u73a9\u5bb6\u5206\u6570\u677f", MatchScoreboardScreen.this.f_96543_ / 2, 10, 0xFFFFFF);
            MatchScoreboardScreen.this.renderTeamColumn(graphics, "ATTACK", 6, 34, MatchScoreboardScreen.this.f_96543_ / 2 - 9, -2925744);
            MatchScoreboardScreen.this.renderTeamColumn(graphics, "DEFEND", MatchScoreboardScreen.this.f_96543_ / 2 + 3, 34, MatchScoreboardScreen.this.f_96543_ / 2 - 9, -11106873);
            MatchScoreboardScreen.this.renderGovernance(graphics, mouseX, mouseY);
            MatchScoreboardScreen.this.renderContextMenu(graphics, mouseX, mouseY);
            super.draw(graphics, x, y, drawWidth, drawHeight, mouseX, mouseY, partialTick);
        }
    }

    private record HitRow(int x, int y, int w, int h, MatchStatsSyncPacket.Row row) {
        boolean contains(double px, double py) {
            return px >= (double)this.x && px < (double)(this.x + this.w) && py >= (double)this.y && py < (double)(this.y + this.h);
        }
    }

    private record GovernanceHit(int x, int y, int w, int h, GovernanceActionPacket.Action action, UUID candidate) {
        boolean contains(double px, double py) {
            return px >= (double)this.x && px < (double)(this.x + this.w) && py >= (double)this.y && py < (double)(this.y + this.h);
        }
    }
}

