package org.espetro.client.support;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.espetro.support.FireRequestBoard.*;
import org.espetro.support.FireRequestNetwork;
import org.espetro.support.FireRequestNetwork.*;
import org.esradial.client.RadialUiText;

import java.util.*;

public final class FireSupportScreen extends Screen {
    private final Screen parent;
    private final FireSupportMap map = new FireSupportMap();

    private record Hit(int x, int y, int w, int h, Runnable action) {
        boolean contains(double a, double b) {
            return a >= x && a < x + w && b >= y && b < y + h;
        }
    }

    private final List<Hit> hits = new ArrayList<>();
    private int left, top, w, h, split, page;
    private double scale = 1, x = Double.NaN, z = Double.NaN, endX = Double.NaN, endZ = Double.NaN;
    private boolean form, line;
    private long session = Long.MIN_VALUE, messageUntil;
    private UUID selected;
    private String notice = "";
    private EditBox note;

    public FireSupportScreen(Screen parent) {
        super(Component.literal("火力支援"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        String value = note == null ? "" : note.getValue();
        scale = Math.min(1, Math.min(width / 820.0, height / 480.0));
        int vw = (int) (width / scale), vh = (int) (height / scale);
        w = Math.min(vw - 16, 1000);
        h = Math.min(vh - 16, 540);
        left = (vw - w) / 2;
        top = (vh - h) / 2;
        split = left + (int) (w * .32);
        note =
                new EditBox(
                        font,
                        left + 18,
                        top + 249,
                        split - left - 36,
                        22,
                        Component.literal("任务说明"));
        note.setMaxLength(96);
        note.setValue(value);
        note.setTextColor(0xffe6ede2);
        addRenderableWidget(note);
        note.visible = form;
    }

    public void message(String message) {
        if (message != null && !message.isBlank()) {
            notice = message;
            messageUntil = System.currentTimeMillis() + 5000;
        }
    }

    public static void text(
            GuiGraphics g, String text, double x, double y, int color, double size, double max) {
        g.flush();
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
        RadialUiText.draw(g, RadialUiText.fit(text, max, size, 400), x, y, color, size, 400);
        com.sighs.apricityui.render.ImageDrawer.flushBatch();
    }

    private void t(GuiGraphics g, String s, int x, int y, int color, int size, int max) {
        text(g, s, x, y, color, size, max);
    }

    private void button(
            GuiGraphics g,
            String s,
            int x,
            int y,
            int width,
            boolean enabled,
            boolean primary,
            Runnable action,
            int mx,
            int my) {
        boolean hover = mx >= x && mx < x + width && my >= y && my < y + 25;
        g.fill(
                x,
                y,
                x + width,
                y + 25,
                !enabled ? 0xff28372e : primary ? 0xffdec573 : hover ? 0xff476052 : 0xff30483c);
        t(
                g,
                s,
                x + 9,
                y + 6,
                !enabled ? 0xff7e9582 : primary ? 0xff1b2a20 : 0xffe5ebdf,
                11,
                width - 18);
        if (enabled) hits.add(new Hit(x, y, width, 25, action));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float p) {
        g.pose().pushPose();
        g.pose().scale((float) scale, (float) scale, 1);
        try {
            draw(g, (int) (mx / scale), (int) (my / scale), p);
        } finally {
            g.flush();
            g.pose().popPose();
        }
    }

    private void draw(GuiGraphics g, int mx, int my, float p) {
        hits.clear();
        RadialUiText.beginFrame();
        g.fill(0, 0, (int) (width / scale), (int) (height / scale), 0xd00d1915);
        g.fill(left, top, left + w, top + h, 0xf81b2a26);
        g.fill(left, top, left + w, top + 2, 0xffe2c777);
        t(g, "ESPETRO / 火力支援", left + 18, top + 14, 0xffe8ecdf, 16, w - 120);
        button(g, "关闭", left + w - 72, top + 10, 54, true, false, this::onClose, mx, my);
        Snapshot data = FireSupportClient.snapshot;
        if (data == null) {
            t(g, "正在同步本方火力申请…", left + 18, top + 70, 0xffb4c7b6, 12, w - 36);
            return;
        }
        if (session != data.session()) {
            session = data.session();
            selected = null;
            page = 0;
            x = z = endX = endZ = Double.NaN;
        }
        boolean canCreate = data.battle() && data.actor().requester() && data.skillReady();
        button(
                g,
                "本方申请",
                left + 18,
                top + 50,
                100,
                true,
                !form,
                () -> {
                    form = false;
                    note.visible = false;
                },
                mx,
                my);
        button(
                g,
                "+ 发起申请",
                left + 128,
                top + 50,
                120,
                canCreate,
                form,
                () -> {
                    form = true;
                    note.visible = true;
                },
                mx,
                my);
        String status =
                !data.battle()
                        ? "非战斗阶段"
                        : !data.skillReady()
                                ? "火炮技能未配置"
                                : data.cooldown() > 0
                                        ? "技能冷却 " + data.cooldown() + " 秒"
                                        : "155 火炮就绪";
        t(g, status, split + 18, top + 57, 0xffddc478, 10, w - (split - left) - 38);
        g.fill(split, top + 91, split + 1, top + h - 40, 0xff40564a);
        if (form) form(g, data, mx, my, p, canCreate);
        else list(g, data, mx, my, p);
        g.fill(left + 18, top + h - 34, left + w - 18, top + h - 33, 0xff40564a);
        String footer =
                System.currentTimeMillis() < messageUntil
                        ? notice
                        : !data.actor().member()
                                ? "加入阵营后可查看本方申请"
                                : form ? "提交后等待指挥官审批 · 左键选择目标 · 滚轮缩放地图" : "本方可见 · 批准后执行并消耗原有技能冷却";
        t(g, footer, left + 18, top + h - 23, 0xffb8c9b5, 10, w - 36);
        super.render(g, mx, my, p);
    }

    private void form(GuiGraphics g, Snapshot d, int mx, int my, float p, boolean enabled) {
        int lx = left + 18, lw = split - left - 36, rx = split + 18, rw = left + w - rx - 18;
        t(g, "申请火力支援", lx, top + 104, 0xffeef0e5, 15, lw);
        int half = (lw - 6) / 2;
        button(
                g,
                "定点炮击",
                lx,
                top + 135,
                half,
                true,
                !line,
                () -> {
                    line = false;
                    clearTarget();
                },
                mx,
                my);
        button(
                g,
                "徐进弹幕",
                lx + half + 6,
                top + 135,
                half,
                true,
                line,
                () -> {
                    line = true;
                    clearTarget();
                },
                mx,
                my);
        t(g, "起点：" + coordinate(x, z), lx, top + 177, 0xffcbd6c4, 11, lw);
        if (line) t(g, "终点：" + coordinate(endX, endZ), lx, top + 201, 0xffcbd6c4, 11, lw);
        t(g, "任务说明（可选）", lx, top + 231, 0xff9eb2a3, 10, lw);
        t(g, "仅本方可见", lx, top + 289, 0xffa6bba7, 10, lw);
        t(g, "提交不会立即开火。", lx, top + 314, 0xffe2c777, 11, lw);
        button(
                g,
                "提交审批",
                lx,
                top + h - 78,
                lw,
                enabled && Double.isFinite(x) && (!line || Double.isFinite(endX)),
                true,
                () -> {
                    send(
                            new Action(
                                    line ? Type.CREATE_LINE : Type.CREATE_POINT,
                                    d.session(),
                                    Action.NONE,
                                    x,
                                    z,
                                    endX,
                                    endZ,
                                    note.getValue()));
                },
                mx,
                my);
        map.render(g, rx, top + 104, rw, h - 188, p, scale, d.bounds());
        map.targets(g, x, z, line, endX, endZ, 0xffe2c777);
        double[] hover = map.pick(mx, my);
        if (hover != null)
            t(
                    g,
                    "悬停：" + coordinate(hover[0], hover[1]),
                    rx + 8,
                    top + h - 105,
                    0xffe2c777,
                    10,
                    rw - 20);
        button(g, "清除选点", rx + rw - 112, top + h - 78, 112, true, false, this::clearTarget, mx, my);
    }

    private void list(GuiGraphics g, Snapshot d, int mx, int my, float p) {
        int lx = left + 18, lw = split - left - 36, rx = split + 18, rw = left + w - rx - 18;
        List<Request> all =
                d.requests().stream()
                        .sorted(Comparator.comparing(Request::created).reversed())
                        .toList();
        int rows = Math.max(1, (h - 179) / 62), pages = Math.max(1, (all.size() + rows - 1) / rows);
        page = Math.min(page, pages - 1);
        for (int i = page * rows; i < Math.min(all.size(), (page + 1) * rows); i++) {
            Request r = all.get(i);
            int yy = top + 103 + (i - page * rows) * 62;
            g.fill(lx, yy, lx + lw, yy + 55, r.id().equals(selected) ? 0xff344d41 : 0xff233730);
            t(
                    g,
                    r.note().isBlank() ? r.title() : r.note(),
                    lx + 9,
                    yy + 7,
                    0xffeef0e5,
                    12,
                    lw - 18);
            t(
                    g,
                    state(r.state()) + " · " + r.requesterName(),
                    lx + 9,
                    yy + 31,
                    0xffb0c2ad,
                    10,
                    lw - 18);
            hits.add(new Hit(lx, yy, lw, 55, () -> selected = r.id()));
        }
        button(g, "‹", lx, top + h - 77, 30, page > 0, false, () -> page--, mx, my);
        t(g, (page + 1) + " / " + pages, lx + 41, top + h - 70, 0xffaebfac, 10, 70);
        button(
                g,
                "›",
                lx + lw - 30,
                top + h - 77,
                30,
                page + 1 < pages,
                false,
                () -> page++,
                mx,
                my);
        if (all.isEmpty()) t(g, "暂无火力申请", lx + 8, top + 119, 0xffaebfac, 12, lw - 16);
        Request r = all.stream().filter(v -> v.id().equals(selected)).findFirst().orElse(null);
        if (r == null) {
            t(g, "选择一条申请查看目标", rx, top + 113, 0xffe6ecde, 14, rw);
            t(g, "只有本方已部署的指挥官可审批。", rx, top + 145, 0xff9eb2a3, 11, rw);
            return;
        }
        t(g, r.title() + " · " + state(r.state()), rx, top + 104, 0xffe8eddf, 14, rw);
        t(g, "申请：" + r.requesterName() + " / 小队 " + r.squad(), rx, top + 131, 0xffacbfa9, 10, rw);
        map.render(g, rx, top + 158, rw, h - 318, p, scale, d.bounds());
        map.targets(g, r.x(), r.z(), r.line(), r.endX(), r.endZ(), 0xffe2c777);
        t(
                g,
                coordinate(r.x(), r.z()) + (r.line() ? " → " + coordinate(r.endX(), r.endZ()) : ""),
                rx,
                top + h - 148,
                0xffe2c777,
                10,
                rw);
        t(g, r.note(), rx, top + h - 127, 0xffc4d1be, 11, rw);
        t(g, r.result(), rx, top + h - 106, 0xffa6bba7, 10, rw);
        int yy = top + h - 78, third = (rw - 12) / 3;
        boolean pending = r.state() == State.PENDING && d.battle();
        button(
                g,
                "驳回申请",
                rx,
                yy,
                third,
                pending && d.actor().commander() && d.actor().deployed(),
                false,
                () -> simple(Type.REJECT, r),
                mx,
                my);
        button(
                g,
                "取消申请",
                rx + third + 6,
                yy,
                third,
                pending && (d.actor().commander() || d.actor().id().equals(r.requester())),
                false,
                () -> simple(Type.CANCEL, r),
                mx,
                my);
        button(
                g,
                "批准并执行",
                rx + 2 * (third + 6),
                yy,
                third,
                pending
                        && d.actor().commander()
                        && d.actor().deployed()
                        && d.cooldown() == 0
                        && d.skillReady(),
                true,
                () -> simple(Type.APPROVE, r),
                mx,
                my);
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

    private void simple(Type type, Request request) {
        send(Action.simple(type, request.session(), request.id()));
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
        for (Hit hit : List.copyOf(hits))
            if (hit.contains(mx, my)) {
                hit.action().run();
                return true;
            }
        if (form) {
            double[] target = map.pick(mx, my);
            if (target != null) {
                if (!Double.isFinite(x) || !line || Double.isFinite(endX)) {
                    x = target[0];
                    z = target[1];
                    endX = endZ = Double.NaN;
                } else {
                    endX = target[0];
                    endZ = target[1];
                }
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (map.pick(x / scale, y / scale) != null) map.zoom(delta);
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
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}
