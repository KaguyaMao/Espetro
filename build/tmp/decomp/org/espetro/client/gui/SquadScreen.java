/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.ClientTacticalState;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.client.gui.ScrollableList;
import org.espetro.client.gui.SquadNameField;
import org.espetro.client.gui.UnifiedDeployScreen;
import org.espetro.network.MatchStatsActionPacket;
import org.espetro.network.NetworkManager;
import org.espetro.network.UnifiedDeployScreenPacket;

public class SquadScreen
extends EspetroMenuScreen {
    private static final int NO_SQUAD = -1;
    private static final int BUTTON_H = 12;
    private static final int ROW_H = 12;
    private static final int GAP = 1;
    private static final float TEXT_SCALE = 0.72f;
    private static final float MEMBER_TEXT_SCALE = 0.68f;
    private static final int SCREEN_SHADE = -15592169;
    private static final int PANEL_BG = -15131618;
    private static final int PANEL_SOFT_BG = -14605017;
    private final List<UnifiedDeployScreenPacket.SquadInfo> squads = new ArrayList<UnifiedDeployScreenPacket.SquadInfo>();
    private final String team;
    private final List<UnifiedDeployScreenPacket.SquadCategoryInfo> categories = new ArrayList<UnifiedDeployScreenPacket.SquadCategoryInfo>();
    private final Screen parent;
    private int mySquadId;
    private int selectedSquadId;
    private SquadNameField nameField;
    private String pendingSquadName = "";
    private GuiElement categoryPopup;
    private GuiElement detailContainer;
    private int detailX;
    private int detailY;
    private int detailW;
    private int detailH;
    private final Map<Integer, EspetroAuiWidgets.ActionButton> rowJoinButtons = new HashMap<Integer, EspetroAuiWidgets.ActionButton>();
    private final Map<Integer, EspetroAuiWidgets.ActionButton> rowDetailButtons = new HashMap<Integer, EspetroAuiWidgets.ActionButton>();

    public SquadScreen(List<UnifiedDeployScreenPacket.SquadInfo> squads, int mySquadId, String team, List<UnifiedDeployScreenPacket.SquadCategoryInfo> categories, Screen parent) {
        super(Component.m_237113_("\u73ed\u7ec4\u5c0f\u961f"));
        if (squads != null) {
            this.squads.addAll(squads);
        }
        this.mySquadId = mySquadId;
        this.team = team;
        if (categories != null) {
            this.categories.addAll(categories);
        }
        this.parent = parent;
        this.selectedSquadId = -1;
    }

    public void updateSquads(List<UnifiedDeployScreenPacket.SquadInfo> updatedSquads, int updatedMySquadId) {
        Screen screen;
        List<UnifiedDeployScreenPacket.SquadInfo> nextSquads;
        List<Object> list = nextSquads = updatedSquads == null ? List.of() : updatedSquads;
        if (this.mySquadId == updatedMySquadId && this.squads.equals(nextSquads)) {
            return;
        }
        boolean structureChanged = this.mySquadId != updatedMySquadId || !SquadScreen.squadStructureSignature(this.squads).equals(SquadScreen.squadStructureSignature(nextSquads));
        this.squads.clear();
        this.squads.addAll(nextSquads);
        this.mySquadId = updatedMySquadId;
        if (this.findSquad(this.selectedSquadId) == null) {
            this.selectedSquadId = -1;
        }
        if ((screen = this.parent) instanceof UnifiedDeployScreen) {
            UnifiedDeployScreen deployScreen = (UnifiedDeployScreen)screen;
            deployScreen.updateSquads(updatedSquads, updatedMySquadId);
        }
        if (this.root == null) {
            return;
        }
        if (structureChanged) {
            this.rebuildMenuRoot();
        } else {
            this.refreshSquadRowLabels();
            this.rebuildDetailContainer();
        }
    }

    private static List<Object> squadStructureSignature(List<UnifiedDeployScreenPacket.SquadInfo> list) {
        ArrayList<Object> signature = new ArrayList<Object>();
        for (UnifiedDeployScreenPacket.SquadInfo squad : list) {
            signature.add(squad.id);
            for (UnifiedDeployScreenPacket.SquadMemberInfo member : squad.members) {
                signature.add(member.uuid);
                signature.add(member.leader);
            }
        }
        return signature;
    }

    public void updateFromDeployPacket(UnifiedDeployScreenPacket packet) {
        this.updateSquads(packet.getSquads(), packet.getMySquadId());
        Screen screen = this.parent;
        if (screen instanceof UnifiedDeployScreen) {
            UnifiedDeployScreen deployScreen = (UnifiedDeployScreen)screen;
            deployScreen.updateClasses(packet.getClasses(), packet.getClassCounts(), packet.getVariantCounts());
            deployScreen.updateTimeRemaining(packet.getDeployTimeRemaining());
            deployScreen.updateDeploymentState(packet.isWaitingForDeploySelection(), packet.getOutpostRedeployCooldownRemaining());
            deployScreen.updateClassSwitchCooldown(packet.getClassSwitchCooldownRemaining());
        }
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.m_280509_(0, 0, this.f_96543_, this.f_96544_, -15592169);
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        this.rowJoinButtons.clear();
        this.rowDetailButtons.clear();
        int panelW = Math.max(250, Math.min(520, this.f_96543_ - 12));
        int panelH = Math.max(154, Math.min(210, this.f_96544_ - 12));
        int panelX = (this.f_96543_ - panelW) / 2;
        int panelY = Math.max(4, (this.f_96544_ - panelH) / 2);
        root.addChild(EspetroAuiWidgets.panel(panelX, panelY, panelW, panelH, -15131618, -10788256));
        root.addChild(SquadScreen.compactText(panelX + 5, panelY + 5, "\u00a76\u00a7l\u73ed\u7ec4\u5c0f\u961f", -14490));
        root.addChild(SquadScreen.compactText(panelX + 5, panelY + 14, EspetroAuiWidgets.teamPrefix(this.team) + EspetroAuiWidgets.teamName(this.team), EspetroAuiWidgets.teamColor(this.team)));
        EspetroAuiWidgets.ActionButton closeButton = SquadScreen.compactButton(panelX + panelW - 35, panelY + 4, 30, 12, "\u5173\u95ed", this::returnToParent);
        root.addChild(closeButton);
        int inputY = panelY + 25;
        int createW = 32;
        int inputW = Math.min(150, panelW / 2 - createW - 10);
        this.nameField = new SquadNameField(panelX + 5, inputY, inputW, 12, "\u5c0f\u961f\u540d\u79f0", this::createSquad);
        root.addChild(this.nameField);
        root.addChild(SquadScreen.compactButton(panelX + 5 + inputW + 3, inputY, createW, 12, "\u521b\u5efa", this::createSquad).setTextColor(-9054838));
        root.addChild(SquadScreen.compactButton(panelX + 5 + inputW + createW + 6, inputY, 32, 12, "\u9000\u51fa", NetworkManager::leaveSquad).setEnabled(this.mySquadId != -1).setTextColor(-19380));
        int contentY = panelY + 40;
        int contentH = panelH - 45;
        this.detailW = Math.max(112, Math.min(210, (panelW - 14) / 2));
        int listW = panelW - this.detailW - 13;
        int listX = panelX + 5;
        this.detailX = listX + listW + 3;
        this.detailY = contentY;
        this.detailH = contentH;
        this.buildSquadList(root, listX, contentY, listW, contentH);
        root.addChild(EspetroAuiWidgets.panel(this.detailX, this.detailY, this.detailW, this.detailH, -14605017, -10788256));
        this.detailContainer = new GuiElement(0, 0, this.f_96543_, this.f_96544_);
        root.addChild(this.detailContainer);
        this.rebuildDetailContainer();
        this.categoryPopup = this.buildCategoryPopup(panelX, panelY, panelW, panelH);
        this.categoryPopup.setVisible(false);
        root.addChild(this.categoryPopup);
    }

    private void buildSquadList(GuiElement root, int x, int y, int width, int height) {
        root.addChild(EspetroAuiWidgets.panel(x, y, width, height, -14605017, -10788256));
        ScrollableList list = new ScrollableList(x + 3, y + 3, width - 6, height - 6).setScrollStep(13).setAlwaysShowScrollbar(true);
        root.addChild(list);
        if (this.squads.isEmpty()) {
            list.addChild(SquadScreen.compactCenteredText(0, 3, width - 10, "\u6682\u65e0\u5c0f\u961f", -2828064));
            return;
        }
        int rowY = 0;
        int buttonW = list.getWidth() - 18;
        for (UnifiedDeployScreenPacket.SquadInfo squad : this.squads) {
            int squadId = squad.id;
            EspetroAuiWidgets.ActionButton joinButton = SquadScreen.compactButton(0, rowY, buttonW, 12, this.squadRowLabel(squad), () -> NetworkManager.joinSquad(squadId));
            list.addChild(joinButton);
            this.rowJoinButtons.put(squadId, joinButton);
            EspetroAuiWidgets.ActionButton detailButton = SquadScreen.compactButton(buttonW + 2, rowY, 12, 12, "\u25b6", () -> this.toggleDetail(squadId)).setTextColor(-14490);
            list.addChild(detailButton);
            this.rowDetailButtons.put(squadId, detailButton);
            rowY += 13;
        }
        this.refreshSquadRowLabels();
    }

    private String squadRowLabel(UnifiedDeployScreenPacket.SquadInfo squad) {
        boolean joined = squad.id == this.mySquadId;
        boolean full = squad.memberCount >= squad.maxMembers && !joined;
        String count = "\u00a77[" + squad.memberCount + "/" + squad.maxMembers + "]";
        String lockIcon = squad.isLocked ? " \u00a7c\ud83d\udd12" : "";
        String marker = SquadScreen.firstCodePoint(squad.categoryId, squad.categoryDisplayName);
        return (joined ? "\u00a7a" : (full ? "\u00a7c" : "\u00a7f")) + squad.displayId + ". " + squad.name + " " + count + lockIcon + (String)(marker.isEmpty() ? "" : " \u00a76[" + marker + "]");
    }

    private void refreshSquadRowLabels() {
        for (UnifiedDeployScreenPacket.SquadInfo squad : this.squads) {
            EspetroAuiWidgets.ActionButton detail;
            EspetroAuiWidgets.ActionButton join = this.rowJoinButtons.get(squad.id);
            if (join != null) {
                boolean joined = squad.id == this.mySquadId;
                boolean full = squad.memberCount >= squad.maxMembers && !joined;
                boolean blockedByLock = squad.isLocked && !joined;
                join.setLabel(this.squadRowLabel(squad)).setSelected(joined).setEnabled(!full && !blockedByLock).setTextColor(full ? -5327681 : -1);
            }
            if ((detail = this.rowDetailButtons.get(squad.id)) == null) continue;
            detail.setLabel(squad.id == this.selectedSquadId ? "\u25bc" : "\u25b6").setSelected(squad.id == this.selectedSquadId);
        }
    }

    private void toggleDetail(int squadId) {
        this.selectedSquadId = this.selectedSquadId == squadId ? -1 : squadId;
        this.refreshSquadRowLabels();
        this.rebuildDetailContainer();
    }

    private void rebuildDetailContainer() {
        if (this.detailContainer == null) {
            return;
        }
        this.detailContainer.clearChildren();
        UnifiedDeployScreenPacket.SquadInfo squad = this.findSquad(this.selectedSquadId);
        if (squad == null) {
            return;
        }
        int x = this.detailX;
        int y = this.detailY;
        int width = this.detailW;
        int height = this.detailH;
        this.detailContainer.addChild(SquadScreen.compactText(x + 4, y + 4, width - 8, "\u00a76\u00a7l" + squad.name, -14490, 0.72f));
        this.detailContainer.addChild(SquadScreen.compactText(x + 4, y + 13, width - 43, "\u00a77\u6210\u5458 " + squad.memberCount + "/" + squad.maxMembers, -2828064, 0.72f));
        if (this.isLocalPlayerLeader(squad)) {
            String lockLabel = squad.isLocked ? "\u89e3\u9501" : "\u9501\u5b9a";
            int lockColor = squad.isLocked ? -9054838 : -19380;
            this.detailContainer.addChild(SquadScreen.compactButton(x + width - 70, y + 11, 32, 12, lockLabel, () -> {
                if (squad.isLocked) {
                    NetworkManager.unlockSquad();
                } else {
                    NetworkManager.lockSquad();
                }
            }).setTextColor(lockColor));
            this.detailContainer.addChild(SquadScreen.compactButton(x + width - 36, y + 11, 32, 12, "\u5220\u9664", () -> NetworkManager.deleteSquad(squad.id)).setTextColor(-39322));
        } else if (squad.isLocked) {
            this.detailContainer.addChild(SquadScreen.compactText(x + width - 70, y + 13, 66, "\u00a7c\ud83d\udd12 \u5df2\u9501\u5b9a", -2828064, 0.72f));
        }
        ScrollableList detailList = new ScrollableList(x + 4, y + 25, width - 8, height - 29).setScrollStep(8).setAlwaysShowScrollbar(true);
        this.detailContainer.addChild(detailList);
        if (squad.members.isEmpty()) {
            detailList.addChild(SquadScreen.compactText(0, 0, "\u6682\u65e0\u6210\u5458", -2828064));
            return;
        }
        int lineY = 0;
        boolean localLeader = this.isLocalPlayerLeader(squad);
        UUID localId = Minecraft.m_91087_().f_91074_ != null ? Minecraft.m_91087_().f_91074_.m_20148_() : null;
        for (UnifiedDeployScreenPacket.SquadMemberInfo member : squad.members) {
            String label = member.leader ? "[\u961f\u957f] " + member.playerName + " - " + member.className : member.playerName + " - " + member.className;
            int textWidth = localLeader && !member.leader ? detailList.getWidth() - 39 : detailList.getWidth() - 6;
            detailList.addChild(SquadScreen.compactText(0, lineY, textWidth, label, ClientTacticalState.getSquadMemberColor(squad.id, member), 0.68f));
            if (localLeader && !member.leader && member.uuid != null && !member.uuid.equals(new UUID(0L, 0L)) && !member.uuid.equals(localId)) {
                detailList.addChild(SquadScreen.compactButton(detailList.getWidth() - 35, lineY - 1, 29, 9, "\u8e22\u51fa", () -> NetworkManager.sendMatchStatsAction(MatchStatsActionPacket.Action.KICK_FROM_SQUAD, member.uuid)).setTextColor(-39322));
            }
            lineY += 8;
        }
    }

    private GuiElement buildCategoryPopup(int panelX, int panelY, int panelW, int panelH) {
        GuiElement popup = new GuiElement(0, 0, this.f_96543_, this.f_96544_);
        List<UnifiedDeployScreenPacket.SquadCategoryInfo> options = this.categories.isEmpty() ? List.of(new UnifiedDeployScreenPacket.SquadCategoryInfo("none", "\u65e0")) : this.categories;
        int rowH = 14;
        int visibleRows = Math.max(1, Math.min(6, options.size()));
        int popupW = Math.min(180, panelW - 12);
        int popupH = 22 + visibleRows * rowH + 5;
        int x = panelX + 5;
        int y = Math.min(panelY + panelH - popupH - 5, panelY + 39);
        final int clickX = x;
        final int clickY = y;
        final int clickW = popupW;
        final int clickH = popupH;
        popup.addChild(new GuiElement(clickX, clickY, clickW, clickH){

            @Override
            public boolean onMouseClick(int mouseX, int mouseY, int button) {
                return mouseX >= clickX && mouseX < clickX + clickW && mouseY >= clickY && mouseY < clickY + clickH;
            }
        });
        popup.addChild(EspetroAuiWidgets.panel(x, y, popupW, popupH, -266856424, -1525668));
        popup.addChild(SquadScreen.compactText(x + 6, y + 6, "\u00a76\u00a7l\u9009\u62e9\u5c0f\u961f\u7c7b\u522b", -14490));
        popup.addChild(SquadScreen.compactButton(x + popupW - 34, y + 4, 28, 12, "\u53d6\u6d88", this::cancelCategoryPopup));
        ScrollableList list = new ScrollableList(x + 6, y + 22, popupW - 12, popupH - 28).setScrollStep(14).setAlwaysShowScrollbar(options.size() > visibleRows);
        popup.addChild(list);
        int rowY = 0;
        for (UnifiedDeployScreenPacket.SquadCategoryInfo category : options) {
            list.addChild(SquadScreen.compactButton(0, rowY, list.getWidth() - 12, 12, category.displayName, () -> this.finishCreate(category.id)));
            rowY += 14;
        }
        return popup;
    }

    private void cancelCategoryPopup() {
        this.pendingSquadName = "";
        if (this.categoryPopup != null) {
            this.categoryPopup.setVisible(false);
        }
    }

    private static String firstCodePoint(String categoryId, String displayName) {
        if ("none".equals(categoryId) || displayName == null || displayName.isEmpty()) {
            return "";
        }
        int cp = displayName.codePointAt(0);
        return new String(Character.toChars(cp));
    }

    private static EspetroAuiWidgets.Text compactText(int x, int y, String value, int color) {
        return EspetroAuiWidgets.text(x, y, value, color).setTextScale(0.72f);
    }

    private static EspetroAuiWidgets.Text compactText(int x, int y, int width, String value, int color, float scale) {
        return EspetroAuiWidgets.text(x, y, width, value, color).setTextScale(scale);
    }

    private static EspetroAuiWidgets.Text compactCenteredText(int x, int y, int width, String value, int color) {
        return EspetroAuiWidgets.centeredText(x, y, width, value, color).setTextScale(0.72f);
    }

    private static EspetroAuiWidgets.ActionButton compactButton(int x, int y, int width, int height, String label, Runnable action) {
        return EspetroAuiWidgets.button(x, y, width, height, label, action).setTextScale(0.72f);
    }

    private void createSquad() {
        String name;
        this.pendingSquadName = name = this.nameField != null ? this.nameField.getValue() : "";
        if (this.categoryPopup != null) {
            this.categoryPopup.setVisible(true);
        }
    }

    private void finishCreate(String categoryId) {
        NetworkManager.sendSquadCreateWithCategory(this.pendingSquadName, categoryId);
        this.pendingSquadName = "";
        if (this.categoryPopup != null) {
            this.categoryPopup.setVisible(false);
        }
        if (this.nameField != null) {
            this.nameField.clear();
        }
    }

    private boolean isCategoryPopupOpen() {
        return this.categoryPopup != null && this.categoryPopup.isVisible();
    }

    private UnifiedDeployScreenPacket.SquadInfo findSquad(int id) {
        for (UnifiedDeployScreenPacket.SquadInfo squad : this.squads) {
            if (squad.id != id) continue;
            return squad;
        }
        return null;
    }

    private boolean isLocalPlayerLeader(UnifiedDeployScreenPacket.SquadInfo squad) {
        if (squad.id != this.mySquadId || Minecraft.m_91087_().f_91074_ == null) {
            return false;
        }
        String localName = Minecraft.m_91087_().f_91074_.m_7755_().getString();
        for (UnifiedDeployScreenPacket.SquadMemberInfo member : squad.members) {
            if (!member.leader || !localName.equals(member.playerName)) continue;
            return true;
        }
        return false;
    }

    private void returnToParent() {
        if (this.parent != null) {
            Minecraft.m_91087_().m_91152_(this.parent);
        } else {
            Minecraft.m_91087_().m_91152_(null);
        }
    }

    @Override
    public void m_7379_() {
        if (this.isCategoryPopupOpen()) {
            this.cancelCategoryPopup();
            return;
        }
        this.returnToParent();
    }

    @Override
    public boolean m_7043_() {
        return false;
    }
}

