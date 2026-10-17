package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.espetro.client.aui.GuiElement;
import org.espetro.client.support.FireSupportClient;
import org.espetro.support.FireRequestBoard.*;
import org.espetro.support.FireRequestNetwork;
import org.espetro.support.FireRequestNetwork.*;

import java.util.*;

/** Fire requests hosted by the same menu and widget tree as the deployment screen. */
public final class FireSupportScreen extends EspetroMenuScreen {
    private final Screen parent;
    private final FireSupportMap map = new FireSupportMap();
    private boolean form;
    private long session = Long.MIN_VALUE, messageUntil;
    private UUID selected;
    private String notice = "";
    private EditBox note;
    private int page, vw, vh, sidebar, mapX, mapY, mapW, mapH;
    private double scale = 1, x = Double.NaN, z = Double.NaN, endX = Double.NaN, endZ = Double.NaN;
    private EspetroAuiWidgets.Text startLabel, endLabel, footer;
    private EspetroAuiWidgets.ActionButton submit;

    public FireSupportScreen(Screen parent) {
        super(Component.literal("请求支援火力"));
        this.parent = parent;
        skipFadeIn();
    }

    private void layout() {
        scale = Math.min(1, Math.min(width / 560.0, height / 360.0));
        vw = (int) (width / scale);
        vh = (int) (height / scale);
        sidebar = Math.max(184, Math.min(230, vw / 3));
        mapX = sidebar + 20;
        mapW = vw - mapX - 12;
        mapY = form ? 48 : 108;
        mapH = vh - mapY - (form ? 44 : 72);
    }

    @Override
    protected void init() {
        String value = note == null ? "" : note.getValue();
        layout();
        super.init();
        note = new EditBox(font, 20, 220, sidebar - 28, 20, Component.literal("申请说明"));
        note.setMaxLength(96);
        note.setValue(value);
        note.setBordered(false);
        note.setTextColor(EspetroAuiWidgets.TEXT);
        note.visible = form;
        addRenderableWidget(note);
    }

    public void message(String message) {
        if (message != null && !message.isBlank()) {
            notice = message;
            messageUntil = System.currentTimeMillis() + 5000;
            rebuildMenuRoot();
        }
    }

    /** Small integration-entry labels also use the base game's font path. */
    public static void text(
            GuiGraphics g, String value, double x, double y, int color, double size, double max) {
        float factor = (float) Math.min(1, size / 9.0);
        String fit =
                Minecraft.getInstance()
                        .font
                        .plainSubstrByWidth(value, Math.max(1, (int) (max / factor)));
        EspetroAuiWidgets.drawScaledString(g, fit, (int) x, (int) y, color, factor);
    }

    public static void drawEntry(GuiGraphics g, int x, int mouseX, int mouseY, float partial) {
        var entry = EspetroAuiWidgets.button(x, 6, 83, 19, "请求支援火力 [U]", null).setTextScale(.75f);
        entry.updateFocusState(0, 0, mouseX, mouseY);
        entry.draw(g, 0, 0, 83, 19, mouseX, mouseY, partial);
    }

    private void label(GuiElement root, int x, int y, int width, String value, int color) {
        root.addChild(EspetroAuiWidgets.text(x, y, width, value, color));
    }

    private EspetroAuiWidgets.ActionButton button(
            GuiElement root,
            int x,
            int y,
            int w,
            String title,
            boolean enabled,
            boolean selected,
            Runnable action) {
        var button =
                EspetroAuiWidgets.button(x, y, w, 20, title, action)
                        .setEnabled(enabled)
                        .setSelected(selected)
                        .setTextScale(.9f);
        root.addChild(button);
        return button;
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        layout();
        label(root, 12, 10, vw - 100, "请求支援火力", EspetroAuiWidgets.GOLD);
        label(root, 12, 27, vw - 210, "选择范围 · 提交申请 · 指挥官审批", EspetroAuiWidgets.DIM);
        button(root, vw - 64, 9, 52, "关闭", true, false, this::onClose);
        root.addChild(EspetroAuiWidgets.rect(12, 40, vw - 24, 1, 0x405b6260));
        Snapshot d = FireSupportClient.snapshot;
        startLabel = endLabel = footer = null;
        submit = null;
        if (d == null) {
            label(root, 16, 64, vw - 32, "正在同步本方申请…", EspetroAuiWidgets.MUTED);
            return;
        }
        if (session != d.session()) {
            session = d.session();
            selected = null;
            page = 0;
            clearTarget();
        }
        String readiness =
                !d.battle()
                        ? "非战斗阶段"
                        : !d.skillReady()
                                ? "支援暂不可用"
                                : d.cooldown() > 0 ? "支援冷却 " + d.cooldown() + " 秒" : "支援就绪";
        label(root, vw - 210, 27, 198, readiness, EspetroAuiWidgets.GOLD);
        boolean canCreate = d.battle() && d.actor().requester() && d.skillReady();
        int half = (sidebar - 12) / 2;
        button(root, 12, 48, half, "本方申请", true, !form, () -> mode(false));
        button(root, 16 + half, 48, half - 4, "+ 发起申请", canCreate, form, () -> mode(true));
        root.addChild(EspetroAuiWidgets.panel(12, 78, sidebar - 4, vh - 110));
        if (form) buildForm(root, d, canCreate);
        else buildList(root, d);
        footer =
                EspetroAuiWidgets.text(12, vh - 19, vw - 24, "", EspetroAuiWidgets.DIM)
                        .setTextScale(.85f);
        root.addChild(footer);
        refreshLabels();
    }

    private void buildForm(GuiElement root, Snapshot d, boolean canCreate) {
        int contentW = sidebar - 28;
        label(root, 22, 91, contentW, "申请范围", EspetroAuiWidgets.TEXT);
        label(root, 22, 111, contentW, "左键依次选择两个端点", EspetroAuiWidgets.DIM);
        label(root, 22, 129, contentW, "移动鼠标可预览矩形范围", EspetroAuiWidgets.DIM);
        startLabel = EspetroAuiWidgets.text(22, 158, contentW, "", EspetroAuiWidgets.MUTED);
        endLabel = EspetroAuiWidgets.text(22, 179, contentW, "", EspetroAuiWidgets.MUTED);
        root.addChild(startLabel);
        root.addChild(endLabel);
        label(root, 22, 204, contentW, "申请说明（可选）", EspetroAuiWidgets.DIM);
        root.addChild(
                EspetroAuiWidgets.panel(
                        20, 217, sidebar - 28, 24, 0x30212527, EspetroAuiWidgets.BORDER));
        label(root, 22, 258, contentW, "仅本方可见，提交不会立即开火。", EspetroAuiWidgets.MUTED);
        button(
                root,
                22,
                vh - 92,
                contentW,
                "重选范围",
                true,
                false,
                () -> {
                    clearTarget();
                    refreshLabels();
                });
        submit =
                button(
                        root,
                        22,
                        vh - 62,
                        contentW,
                        "提交审批",
                        canCreate && validRange(),
                        true,
                        () ->
                                send(
                                        new Action(
                                                Type.CREATE,
                                                d.session(),
                                                Action.NONE,
                                                x,
                                                z,
                                                endX,
                                                endZ,
                                                note.getValue())));
        root.addChild(EspetroAuiWidgets.panel(mapX, mapY, mapW, mapH));
    }

    private List<Request> requests() {
        var d = FireSupportClient.snapshot;
        return d == null
                ? List.of()
                : d.requests().stream()
                        .sorted(Comparator.comparing(Request::created).reversed())
                        .toList();
    }

    private Request selection() {
        return requests().stream().filter(r -> r.id().equals(selected)).findFirst().orElse(null);
    }

    private void buildList(GuiElement root, Snapshot d) {
        List<Request> all = requests();
        int rows = Math.max(1, (vh - 158) / 49),
                pages = Math.max(1, (all.size() + rows - 1) / rows);
        page = Math.min(page, pages - 1);
        if (all.isEmpty()) label(root, 24, 100, sidebar - 28, "暂无火力申请", EspetroAuiWidgets.DIM);
        for (int i = page * rows; i < Math.min(all.size(), (page + 1) * rows); i++) {
            Request r = all.get(i);
            int yy = 88 + (i - page * rows) * 49;
            var row =
                    EspetroAuiWidgets.button(
                                    20,
                                    yy,
                                    sidebar - 20,
                                    42,
                                    "",
                                    () -> {
                                        selected = r.id();
                                        rebuildMenuRoot();
                                    })
                            .setSelected(r.id().equals(selected));
            root.addChild(row);
            label(
                    root,
                    28,
                    yy + 6,
                    sidebar - 36,
                    r.note().isBlank() ? "请求支援火力" : r.note(),
                    EspetroAuiWidgets.TEXT);
            label(
                    root,
                    28,
                    yy + 24,
                    sidebar - 36,
                    state(r.state()) + " · " + r.requesterName(),
                    EspetroAuiWidgets.DIM);
        }
        button(
                root,
                22,
                vh - 62,
                28,
                "<",
                page > 0,
                false,
                () -> {
                    page--;
                    rebuildMenuRoot();
                });
        label(root, 63, vh - 55, 70, (page + 1) + " / " + pages, EspetroAuiWidgets.DIM);
        button(
                root,
                sidebar - 34,
                vh - 62,
                28,
                ">",
                page + 1 < pages,
                false,
                () -> {
                    page++;
                    rebuildMenuRoot();
                });
        Request r = selection();
        if (r == null) {
            label(root, mapX + 2, 57, mapW, "选择一条申请查看范围", EspetroAuiWidgets.TEXT);
            label(root, mapX + 2, 78, mapW, "只有本方已部署的指挥官可审批。", EspetroAuiWidgets.DIM);
            root.addChild(EspetroAuiWidgets.panel(mapX, mapY, mapW, mapH));
            return;
        }
        label(
                root,
                mapX + 2,
                53,
                mapW,
                state(r.state()) + " · " + r.requesterName() + " / 小队 " + r.squad(),
                EspetroAuiWidgets.GOLD);
        label(
                root,
                mapX + 2,
                73,
                mapW,
                r.note().isBlank() ? "未填写申请说明" : r.note(),
                EspetroAuiWidgets.MUTED);
        label(
                root,
                mapX + 2,
                91,
                mapW,
                coordinate(r.x(), r.z()) + " → " + coordinate(r.endX(), r.endZ()),
                EspetroAuiWidgets.DIM);
        root.addChild(EspetroAuiWidgets.panel(mapX, mapY, mapW, mapH));
        label(root, mapX + 2, vh - 63, mapW, r.result(), EspetroAuiWidgets.DIM);
        boolean pending = r.state() == State.PENDING && d.battle();
        boolean commander = d.actor().commander() && d.actor().deployed();
        int third = (mapW - 12) / 3;
        button(
                root,
                mapX,
                vh - 48,
                third,
                "驳回申请",
                pending && commander,
                false,
                () -> simple(Type.REJECT, r));
        button(
                root,
                mapX + third + 6,
                vh - 48,
                third,
                "取消申请",
                pending && (d.actor().commander() || d.actor().id().equals(r.requester())),
                false,
                () -> simple(Type.CANCEL, r));
        button(
                root,
                mapX + (third + 6) * 2,
                vh - 48,
                third,
                "批准并执行",
                pending && commander && d.cooldown() == 0 && d.skillReady(),
                true,
                () -> simple(Type.APPROVE, r));
    }

    private void mode(boolean form) {
        this.form = form;
        note.visible = form;
        if (!form) setFocused(null);
        rebuildMenuRoot();
    }

    private void refreshLabels() {
        Snapshot d = FireSupportClient.snapshot;
        if (note != null) note.visible = form;
        if (startLabel != null) startLabel.setText("起点：" + coordinate(x, z));
        if (endLabel != null) endLabel.setText("终点：" + coordinate(endX, endZ));
        if (submit != null)
            submit.setEnabled(
                    d != null
                            && d.battle()
                            && d.actor().requester()
                            && d.skillReady()
                            && validRange());
        if (footer != null)
            footer.setText(
                    System.currentTimeMillis() < messageUntil
                            ? notice
                            : form ? "左键选择范围 · 滚轮缩放 · 提交后等待审批" : "仅本方可见 · 批准后执行并进入支援冷却");
    }

    @Override
    public void tick() {
        var d = FireSupportClient.snapshot;
        updateStructure(
                Arrays.asList(
                        d == null ? 0 : d.session(),
                        d == null ? 0 : d.revision(),
                        d == null ? null : d.actor(),
                        d != null && d.battle(),
                        d != null && d.skillReady(),
                        d == null ? 0 : d.cooldown(),
                        form,
                        selected,
                        page));
        super.tick();
        refreshLabels();
        if (note != null) note.tick();
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        g.pose().pushPose();
        g.pose().scale((float) scale, (float) scale, 1);
        try {
            super.render(g, (int) (mx / scale), (int) (my / scale), partial);
        } finally {
            g.flush();
            g.pose().popPose();
        }
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics g, int mx, int my, float partial) {
        EspetroAuiWidgets.drawScreenShade(g, vw, vh);
        var d = FireSupportClient.snapshot;
        if (d == null) return;
        map.render(g, mapX, mapY, mapW, mapH, partial, scale, d.bounds());
        if (form) {
            double[] hover = map.pick(mx, my);
            if (Double.isFinite(x) && !Double.isFinite(endX) && hover != null)
                map.range(g, x, z, hover[0], hover[1]);
            else map.range(g, x, z, endX, endZ);
        } else {
            var r = selection();
            if (r != null) map.range(g, r.x(), r.z(), r.endX(), r.endZ());
        }
    }

    @Override
    protected void renderAfterMenu(GuiGraphics g, int mx, int my, float partial) {
        if (note != null && form) note.render(g, mx, my, partial);
        if (form) {
            double[] hover = map.pick(mx, my);
            if (hover != null)
                text(
                        g,
                        "坐标：" + coordinate(hover[0], hover[1]),
                        mapX + 3,
                        vh - 33,
                        EspetroAuiWidgets.GOLD,
                        9,
                        mapW - 6);
        }
    }

    private boolean validRange() {
        return Double.isFinite(x)
                && Double.isFinite(z)
                && Double.isFinite(endX)
                && Double.isFinite(endZ)
                && Math.hypot(endX - x, endZ - z) >= 1;
    }

    private void clearTarget() {
        x = z = endX = endZ = Double.NaN;
    }

    private String coordinate(double x, double z) {
        if (!Double.isFinite(x) || !Double.isFinite(z)) return "尚未选择";
        var d = FireSupportClient.snapshot;
        return d != null && d.bounds() != null
                ? d.bounds().label(x, z)
                : String.format(Locale.ROOT, "X %.1f / Z %.1f", x, z);
    }

    private void send(Action action) {
        FireRequestNetwork.send(action);
        message("已提交，等待服务器确认…");
    }

    private void simple(Type type, Request r) {
        send(Action.simple(type, r.session(), r.id()));
    }

    private static String state(State s) {
        return switch (s) {
            case PENDING -> "待审批";
            case EXECUTING -> "下达中";
            case APPROVED -> "已下达";
            case REJECTED -> "已驳回";
            case CANCELLED -> "已取消";
            case EXPIRED -> "已过期";
        };
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        mx /= scale;
        my /= scale;
        if (button != 0) return true;
        if (note != null && note.visible && note.isMouseOver(mx, my))
            return super.mouseClicked(mx, my, button);
        if (form) {
            double[] target = map.pick(mx, my);
            if (target != null) {
                if (!Double.isFinite(x) || Double.isFinite(endX)) {
                    x = target[0];
                    z = target[1];
                    endX = endZ = Double.NaN;
                } else {
                    endX = target[0];
                    endZ = target[1];
                }
                refreshLabels();
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        if (map.pick(mx / scale, my / scale) != null) map.zoom(delta);
        return true;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        return super.mouseReleased(x / scale, y / scale, button);
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        return super.mouseDragged(x / scale, y / scale, button, dx / scale, dy / scale);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}
