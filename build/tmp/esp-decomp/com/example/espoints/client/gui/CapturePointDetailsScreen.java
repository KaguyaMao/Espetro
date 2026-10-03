/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  org.espetro.client.aui.GuiElement
 */
package com.example.espoints.client.gui;

import com.example.espoints.capturepoint.CapturePoint;
import com.example.espoints.capturepoint.DisplayState;
import com.example.espoints.client.ClientBattleState;
import com.example.espoints.client.gui.EspetroMenuScreen;
import com.example.espoints.client.gui.HcrAuiWidgets;
import com.example.espoints.client.gui.ScrollableList;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.network.RequestCapturePointOverviewMessage;
import com.example.espoints.util.EspetroTeamBridge;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;

public class CapturePointDetailsScreen
extends EspetroMenuScreen {
    private static final int MARGIN = 18;
    private static final int PANEL_MIN_W = 340;
    private static final int PANEL_MIN_H = 220;
    private static final int HEADER_H = 48;
    private static final int TABLE_HEADER_H = 34;
    private static final int ROW_H = 28;
    private static final int ROW_GAP = 3;
    private static final int SCROLLBAR_RESERVED_W = 8;
    private static final int ROW_BG = -1441917146;
    private static final int ROW_BG_ALT = -1441719251;
    private static final List<CapturePoint> overviewPoints = new ArrayList<CapturePoint>();
    private int lastDataHash;
    private int lastWidth;
    private int lastHeight;

    public CapturePointDetailsScreen() {
        super((Component)Component.m_237113_((String)"\u636e\u70b9\u5360\u9886\u60c5\u51b5"));
    }

    public static void syncOverviewFromServer(List<CapturePoint.SerializableCapturePoint> serializedPoints) {
        CapturePointDetailsScreen.updateOverview(serializedPoints);
        Minecraft mc = Minecraft.m_91087_();
        Screen screen = mc.f_91080_;
        if (screen instanceof CapturePointDetailsScreen) {
            CapturePointDetailsScreen screen2 = (CapturePointDetailsScreen)screen;
            screen2.rebuildForData();
        }
    }

    public static void openFromServer(List<CapturePoint.SerializableCapturePoint> serializedPoints) {
        CapturePointDetailsScreen.updateOverview(serializedPoints);
        Minecraft mc = Minecraft.m_91087_();
        Screen screen = mc.f_91080_;
        if (screen instanceof CapturePointDetailsScreen) {
            CapturePointDetailsScreen screen2 = (CapturePointDetailsScreen)screen;
            screen2.rebuildForData();
        } else if (mc.f_91080_ == null) {
            mc.m_91152_((Screen)new CapturePointDetailsScreen());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void updateOverview(List<CapturePoint.SerializableCapturePoint> serializedPoints) {
        ArrayList<CapturePoint> points = new ArrayList<CapturePoint>();
        for (CapturePoint.SerializableCapturePoint sp : serializedPoints) {
            CapturePoint point = new CapturePoint(sp.name, sp.pos1, sp.pos2, sp.batch);
            point.restoreFromSerializable(sp);
            points.add(point);
        }
        points.sort(Comparator.comparingInt(CapturePoint::getBatch).thenComparing(CapturePoint::getName));
        List<CapturePoint> list = overviewPoints;
        synchronized (list) {
            overviewPoints.clear();
            overviewPoints.addAll(points);
        }
    }

    public void m_86600_() {
        super.m_86600_();
        int dataHash = this.getDataHash();
        if (dataHash != this.lastDataHash || this.f_96543_ != this.lastWidth || this.f_96544_ != this.lastHeight) {
            this.rebuildForData();
        }
    }

    private void rebuildForData() {
        if (this.f_96541_ != null && this.f_96541_.f_91080_ == this && this.f_96543_ > 0 && this.f_96544_ > 0) {
            this.rebuildMenuRoot();
        }
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        this.lastWidth = this.f_96543_;
        this.lastHeight = this.f_96544_;
        this.lastDataHash = this.getDataHash();
        List<CapturePoint> points = this.getOverviewPoints();
        int pageX = 18;
        int pageY = 18;
        int pageW = Math.max(340, this.f_96543_ - 36);
        int pageH = Math.max(220, this.f_96544_ - 36);
        root.addChild((GuiElement)HcrAuiWidgets.panel(pageX, pageY, pageW, pageH, -535225314, -1604623776));
        this.buildHeader(root, pageX, pageY, pageW);
        int contentX = pageX + 18;
        int contentY = pageY + 48 + 12;
        int contentW = pageW - 36;
        int contentH = pageY + pageH - contentY - 18;
        this.buildTable(root, points, contentX, contentY, contentW, contentH);
    }

    private void buildHeader(GuiElement root, int pageX, int pageY, int pageW) {
        root.addChild((GuiElement)HcrAuiWidgets.text(pageX + 18, pageY + 13, "\u636e\u70b9\u5360\u9886\u60c5\u51b5", -790040));
        root.addChild((GuiElement)HcrAuiWidgets.text(pageX + 18, pageY + 29, "\u4ec5\u663e\u793a\u636e\u70b9\u5360\u9886\u72b6\u6001", -5722440));
        HcrAuiWidgets.ActionButton refresh = HcrAuiWidgets.button(pageX + pageW - 126, pageY + 14, 50, 18, "\u5237\u65b0", this::requestOverview).setColors(0, 1077033802, 1345990688).setBorderColor(-1604623776).setTextColor(-14490);
        root.addChild((GuiElement)refresh);
        HcrAuiWidgets.ActionButton close = HcrAuiWidgets.button(pageX + pageW - 68, pageY + 14, 50, 18, "\u5173\u95ed", () -> ((CapturePointDetailsScreen)this).m_7379_()).setColors(0, 1077033802, 1345990688).setBorderColor(-1604623776).setTextColor(-5722440);
        root.addChild((GuiElement)close);
        root.addChild((GuiElement)HcrAuiWidgets.rect(pageX + 18, pageY + 48, pageW - 36, 1, 0x35FFFFFF));
    }

    private void buildTable(GuiElement root, List<CapturePoint> points, int x, int y, int width, int height) {
        root.addChild((GuiElement)HcrAuiWidgets.panel(x, y, width, height, -1340005081, -1604623776));
        int rowW = width - 24 - 8;
        boolean narrow = rowW < 520;
        int listX = x + 12;
        int listY = y + 34;
        int listW = width - 24;
        int listH = Math.max(28, height - 34 - 12);
        this.addTableHeaders(root, x + 12, y + 12, rowW, narrow);
        root.addChild((GuiElement)HcrAuiWidgets.rect(x + 12, y + 29, width - 24, 1, -1604623776));
        ScrollableList list = new ScrollableList(listX, listY, listW, listH).setScrollStep(31).setAlwaysShowScrollbar(true);
        root.addChild((GuiElement)list);
        if (points.isEmpty()) {
            list.addChild(HcrAuiWidgets.text(10, 10, "\u6682\u65e0\u636e\u70b9\u6570\u636e", -5722440));
            return;
        }
        int yOffset = 0;
        for (int i = 0; i < points.size(); ++i) {
            CapturePoint point = points.get(i);
            this.addPointRow(list, point, 0, yOffset, rowW, 28, i % 2 == 0, narrow);
            yOffset += 31;
        }
    }

    private void addTableHeaders(GuiElement root, int x, int y, int rowW, boolean narrow) {
        int[] cols = this.getColumns(rowW, narrow);
        root.addChild((GuiElement)HcrAuiWidgets.text(x + cols[0], y, "\u636e\u70b9", -5722440));
        root.addChild((GuiElement)HcrAuiWidgets.text(x + cols[1], y, "\u5360\u9886\u60c5\u51b5", -5722440));
        if (!narrow) {
            root.addChild((GuiElement)HcrAuiWidgets.text(x + cols[2], y, "\u5360\u9886\u65b9", -5722440));
        }
        root.addChild((GuiElement)HcrAuiWidgets.text(x + cols[3], y, "\u8fdb\u5ea6", -5722440));
    }

    private void addPointRow(GuiElement parent, CapturePoint point, int x, int y, int w, int h, boolean even, boolean narrow) {
        int[] cols = this.getColumns(w, narrow);
        int stateColor = this.getStateColor(point);
        parent.addChild((GuiElement)HcrAuiWidgets.rect(x, y, w, h, even ? -1441917146 : -1441719251));
        parent.addChild((GuiElement)HcrAuiWidgets.rect(x, y, 3, h, stateColor));
        parent.addChild((GuiElement)HcrAuiWidgets.text(x + cols[0], y + 6, HcrAuiWidgets.trimToWidth(point.getName(), cols[1] - cols[0] - 8), -790040));
        String statusText = narrow ? this.getOccupancyText(point) : this.getStatusText(point);
        int statusMaxW = (narrow ? cols[3] : cols[2]) - cols[1] - 8;
        parent.addChild((GuiElement)HcrAuiWidgets.text(x + cols[1], y + 6, HcrAuiWidgets.trimToWidth(statusText, statusMaxW), stateColor));
        if (!narrow) {
            parent.addChild((GuiElement)HcrAuiWidgets.text(x + cols[2], y + 6, HcrAuiWidgets.trimToWidth(this.getCaptorText(point), Math.max(48, cols[3] - cols[2] - 8)), this.getCaptorColor(point)));
        }
        String progressText = point.getProgress() + "%";
        int progressTextW = Minecraft.m_91087_().f_91062_.m_92895_(progressText);
        int barX = x + cols[3];
        int barY = y + 9;
        int barW = Math.max(42, w - cols[3] - progressTextW - 8);
        parent.addChild((GuiElement)HcrAuiWidgets.rect(barX, barY, barW, 8, -14207413));
        int progressW = point.getProgress() * barW / 100;
        if (progressW > 0) {
            parent.addChild((GuiElement)HcrAuiWidgets.rect(barX, barY, progressW, 8, stateColor));
        }
        parent.addChild((GuiElement)HcrAuiWidgets.text(barX + barW + 5, y + 6, progressText, -5722440));
    }

    private int[] getColumns(int rowW, boolean narrow) {
        if (narrow) {
            return new int[]{10, Math.max(82, rowW * 30 / 100), 0, Math.max(180, rowW - 96)};
        }
        return new int[]{10, Math.max(110, rowW * 22 / 100), Math.max(240, rowW * 45 / 100), Math.max(380, rowW - 120)};
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<CapturePoint> getOverviewPoints() {
        List<CapturePoint> list = overviewPoints;
        synchronized (list) {
            if (!overviewPoints.isEmpty()) {
                return new ArrayList<CapturePoint>(overviewPoints);
            }
        }
        ArrayList<CapturePoint> fallback = new ArrayList<CapturePoint>(ClientBattleState.get().points());
        fallback.sort(Comparator.comparingInt(CapturePoint::getBatch).thenComparing(CapturePoint::getName));
        return fallback;
    }

    private int getDataHash() {
        List<CapturePoint> points = this.getOverviewPoints();
        int hash = points.size();
        for (CapturePoint point : points) {
            hash = 31 * hash + point.getName().hashCode();
            hash = 31 * hash + point.getBatch();
            hash = 31 * hash + point.getDisplayState().hashCode();
            hash = 31 * hash + point.getProgress();
            hash = 31 * hash + Objects.hashCode(point.getCaptorName());
        }
        ClientBattleState state = ClientBattleState.get();
        hash = 31 * hash + state.currentBatch();
        hash = 31 * hash + state.totalBatches();
        return hash;
    }

    private void requestOverview() {
        if (Minecraft.m_91087_().m_91403_() != null) {
            NetworkHandler.INSTANCE.sendToServer((Object)new RequestCapturePointOverviewMessage());
        }
    }

    private boolean isContested(CapturePoint point) {
        return point.getDisplayState() == DisplayState.CAPTURING_FLAG_SINGLE || point.getDisplayState() == DisplayState.CAPTURING_CONTESTED_MULTI || point.getDisplayState() == DisplayState.CONTESTED_MULTI || point.getDisplayState() == DisplayState.CAPTURING_DOWN;
    }

    private String getStatusText(CapturePoint point) {
        switch (point.getDisplayState()) {
            case CAPTURED: {
                return "\u5df2\u5360\u9886";
            }
            case CAPTURING_FLAG_SINGLE: {
                return "\u5347\u65d7\u4e2d";
            }
            case CAPTURING_CONTESTED_MULTI: 
            case CONTESTED_MULTI: {
                return "\u4e89\u593a\u4e2d";
            }
            case CAPTURING_DOWN: {
                return "\u964d\u65d7\u4e2d";
            }
        }
        return "\u4e2d\u7acb";
    }

    private int getStateColor(CapturePoint point) {
        if (point.getDisplayState() == DisplayState.CAPTURED) {
            return this.getCaptorColor(point);
        }
        if (this.isContested(point)) {
            return -19380;
        }
        return -5722440;
    }

    private String getCaptorText(CapturePoint point) {
        String captor = point.getCaptorName();
        if (captor == null || captor.isEmpty()) {
            return "-";
        }
        String displayName = EspetroTeamBridge.displayName(captor);
        return displayName == null || displayName.isEmpty() ? "-" : displayName;
    }

    private String getOccupancyText(CapturePoint point) {
        String captor = this.getCaptorText(point);
        if ("-".equals(captor)) {
            return this.getStatusText(point);
        }
        return this.getStatusText(point) + " - " + captor;
    }

    private int getCaptorColor(CapturePoint point) {
        String canonicalTeam = EspetroTeamBridge.canonicalizeTeamName(point.getCaptorName());
        if ("ATTACK".equals(canonicalTeam)) {
            return -41386;
        }
        if ("DEFEND".equals(canonicalTeam)) {
            return -10514945;
        }
        return -790040;
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 82) {
            this.requestOverview();
            return true;
        }
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    public boolean m_7043_() {
        return false;
    }
}

