package org.espetro.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.espetro.network.RoundReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Both series share one clock. Ordinary changes are sampled for drawing; tooltips use the original events. */
final class RoundEndDetails {
    private final RoundReport report;
    private final int topScore;
    private final List<Point> attack;
    private final List<Point> defend;
    private final Map<RoundReport.Reason, ResourceLocation> icons = new HashMap<>();
    private int hoverPage;
    private int hoverPages;
    private int hoverTick = -1;

    RoundEndDetails(RoundReport report) {
        this.report = report;
        int max = Math.max(report.initialAttack(), report.initialDefend());
        for (var event : report.tickets()) max = Math.max(max, Math.max(event.before(), event.after()));
        int interval = Math.max(1, (int) Math.ceil(max / 4d / 25d) * 25);
        topScore = interval * 4;
        attack = trend(report, true);
        defend = trend(report, false);
    }

    record Point(int tick, int value) {}

    static List<Point> trend(RoundReport report, boolean attack) {
        var points = new ArrayList<Point>();
        int span = Math.max(1, (int) Math.ceil(report.duration() / 32d));
        points.add(new Point(0, attack ? report.initialAttack() : report.initialDefend()));
        RoundReport.Ticket first = null;
        RoundReport.Ticket last = null;
        for (var event : report.tickets()) {
            if (event.attack() != attack) continue;
            boolean jump = marker(event);
            if (last != null && (jump || event.tick() / span != last.tick() / span)) {
                appendTrend(points, first, last, span);
                first = last = null;
            }
            if (jump) {
                var prior = points.get(points.size() - 1);
                if (prior.tick() != event.tick() || prior.value() != event.before()) {
                    points.add(new Point(event.tick(), event.before()));
                }
                points.add(new Point(event.tick(), event.after()));
            } else {
                if (first == null) first = event;
                last = event;
            }
        }
        if (last != null) appendTrend(points, first, last, span);
        var end = points.get(points.size() - 1);
        if (end.tick() < report.duration()) points.add(new Point(report.duration(), end.value()));
        return List.copyOf(points);
    }

    private static void appendTrend(List<Point> points, RoundReport.Ticket first, RoundReport.Ticket last, int span) {
        var prior = points.get(points.size() - 1);
        int rampStart = first.tick() - span;
        if (rampStart > prior.tick()) points.add(new Point(rampStart, prior.value()));
        points.add(new Point(last.tick(), last.after()));
    }

    private record Plot(int left, int top, int width, int height, int duration, int max) {
        float exactX(float tick) { return left + width * (tick / duration); }
        float exactY(float value) { return top + height - height * (value / max); }
        int x(int tick) { return Math.round(exactX(tick)); }
        int y(int value) { return Math.round(exactY(value)); }
    }

    void render(GuiGraphics g, Font font, int width, int bottom, int mx, int my, float progress) {
        UiPaint.glassPanel(g, 8, 29, width - 16, bottom - 29, UiTheme.GLASS_PANEL);
        g.drawCenteredString(font, "票数走势", width / 2, 13, UiTheme.TEXT);
        g.drawString(font, "进攻方", width - 92, 13, UiTheme.ATTACK, false);
        g.drawString(font, "防守方", width - 48, 13, UiTheme.DEFEND, false);
        if (report.duration() <= 0 || bottom < 99) {
            g.drawCenteredString(font, "暂无战局票数记录", width / 2, Math.max(38, bottom / 2), UiTheme.TEXT_SECOND);
            hoverPages = 0;
            return;
        }
        int left = 29 + font.width(Integer.toString(topScore));
        var p = new Plot(left, 53, Math.max(1, width - left - 25), Math.max(1, bottom - 78), report.duration(), topScore);
        for (int i = 0; i <= 4; i++) {
            int score = topScore * i / 4;
            int y = p.y(score);
            g.fill(p.left(), y, p.left() + p.width(), y + 1, UiTheme.HAIRLINE);
            String label = String.valueOf(score);
            g.drawString(font, label, p.left() - font.width(label) - 5, y - 4, UiTheme.TEXT_SECOND, false);
        }
        for (int i = 0; i <= 4; i++) {
            int tick = (int) (p.duration() * (i / 4d));
            int x = p.x(tick);
            g.fill(x, p.top(), x + 1, p.top() + p.height() + 3, UiTheme.HAIRLINE);
            g.drawCenteredString(font, time(tick), x, p.top() + p.height() + 8, UiTheme.TEXT_SECOND);
        }

        float reveal = p.duration() * progress;
        drawTrend(g, p, attack, reveal, UiTheme.ATTACK);
        drawTrend(g, p, defend, reveal, UiTheme.DEFEND);
        int nearestTick = -1, nearestDistance = Integer.MAX_VALUE;
        for (var event : report.tickets()) {
            if (event.tick() > reveal) break;
            int x = p.x(event.tick());
            int dx = Math.abs(mx - x);
            if (dx > 6) continue;
            int dy;
            if (marker(event)) {
                int top = p.y(Math.max(event.before(), event.after()));
                int base = p.y(Math.min(event.before(), event.after()));
                dy = Math.max(0, Math.max(top - 10 - my, my - base));
            } else {
                float value = valueAt(event.attack() ? attack : defend, event.tick());
                dy = Math.round(Math.abs(p.exactY(value) - my));
            }
            if (dx <= 6 && dy <= 6 && dx * dx + dy * dy < nearestDistance) {
                nearestDistance = dx * dx + dy * dy;
                nearestTick = event.tick();
            }
        }
        for (var event : report.tickets()) {
            if (event.tick() > reveal) break;
            if (marker(event)) drawMarker(g, event, p.x(event.tick()), p.y(Math.max(event.before(), event.after())) - 5);
        }
        drawNearby(g, font, width, bottom, mx, my, p, (int) reveal, nearestTick);
    }

    private static float valueAt(List<Point> points, int tick) {
        int low = 1, high = points.size();
        while (low < high) {
            int mid = (low + high) >>> 1;
            if (points.get(mid).tick() <= tick) low = mid + 1;
            else high = mid;
        }
        var before = points.get(low - 1);
        return low == points.size() ? before.value() : interpolate(before, points.get(low), tick);
    }

    private static float interpolate(Point before, Point after, float tick) {
        float fraction = (tick - before.tick()) / (after.tick() - (float) before.tick());
        return before.value() + (after.value() - (float) before.value()) * fraction;
    }

    private static void drawTrend(GuiGraphics g, Plot plot, List<Point> points, float reveal, int color) {
        for (int i = 1; i < points.size(); i++) {
            var before = points.get(i - 1);
            if (before.tick() > reveal) break;
            var after = points.get(i);
            boolean partial = after.tick() > reveal;
            float endTick = partial ? reveal : after.tick();
            float endScore = partial ? interpolate(before, after, reveal) : after.value();
            drawSegment(g, plot.exactX(before.tick()), plot.exactY(before.value()),
                plot.exactX(endTick), plot.exactY(endScore), color);
            if (partial) break;
        }
    }

    private static void drawSegment(GuiGraphics g, float x1, float y1, float x2, float y2, int color) {
        float dx = x2 - x1, dy = y2 - y1;
        float length = (float) Math.hypot(dx, dy);
        if (length < 0.01f) return;
        g.pose().pushPose();
        g.pose().translate(x1, y1, 0);
        g.pose().mulPose(Axis.ZP.rotation((float) Math.atan2(dy, dx)));
        g.pose().scale(length + 0.35f, 0.75f, 1f);
        g.fill(0, -1, 1, 1, color);
        g.pose().popPose();
    }

    private static boolean marker(RoundReport.Ticket event) {
        return switch (event.reason().cause()) {
            case VEHICLE, FOB, CAPTURE -> true;
            default -> false;
        };
    }

    private void drawMarker(GuiGraphics g, RoundReport.Ticket event, int x, int y) {
        var icon = icons.computeIfAbsent(event.reason(), RoundEndDetails::resolveIcon);
        int filter = UiTheme.lerp(UiTheme.TEXTURE_WHITE, teamColor(event.attack()), 0.54f);
        UiPaint.tint(g, filter);
        try {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            g.blit(icon, x - 5, y - 5, 10, 10, 0, 0, 10, 10, 10, 10);
        } finally {
            RenderSystem.disableBlend();
            UiPaint.tint(g, UiTheme.TEXTURE_WHITE);
        }
    }

    private static ResourceLocation resolveIcon(RoundReport.Reason reason) {
        if (reason.cause() == RoundReport.Cause.CAPTURE || reason.cause() == RoundReport.Cause.FOB) {
            var mapIcon = ResourceLocation.fromNamespaceAndPath("espoints", "textures/gui/map/"
                + (reason.cause() == RoundReport.Cause.CAPTURE ? "rally.png" : "radio.png"));
            if (Minecraft.getInstance().getResourceManager().getResource(mapIcon).isPresent()) return mapIcon;
        }
        if (reason.cause() == RoundReport.Cause.VEHICLE) {
            String[] spec = reason.icon().split("\\|", 2);
            try {
                var resolved = (ResourceLocation) Class.forName("com.example.espoints.tactical.FriendlyVehicleIcons")
                    .getMethod("texture", String.class, String.class)
                    .invoke(null, spec[0], spec.length == 2 ? spec[1] : "");
                if (resolved != null && Minecraft.getInstance().getResourceManager().getResource(resolved).isPresent()) return resolved;
            } catch (ReflectiveOperationException | LinkageError absent) {
                // The integration is optional.
            }
        }
        String fallback = switch (reason.cause()) {
            case FOB -> "map/radio.png";
            case CAPTURE -> "squad/rally.png";
            default -> "squad/vehicle_deploy.png";
        };
        return ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/" + fallback);
    }

    private void drawNearby(GuiGraphics g, Font font, int width, int bottom, int mx, int my,
                            Plot plot, int reveal, int nearestTick) {
        if (nearestTick < 0) {
            hoverTick = -1;
            hoverPages = 0;
            return;
        }
        if (hoverTick != nearestTick) hoverPage = 0;
        hoverTick = nearestTick;
        var near = new ArrayList<RoundReport.Ticket>();
        for (var event : report.tickets()) {
            if (event.tick() > reveal) break;
            if (Math.abs(plot.x(event.tick()) - plot.x(nearestTick)) <= 6) near.add(event);
        }
        int cardWidth = Math.min(180, Math.max(1, width - 12));
        int crewLines = near.stream().filter(e -> e.reason().cause() == RoundReport.Cause.VEHICLE)
            .mapToInt(e -> crewLines(font, e, cardWidth).size()).max().orElse(0);
        int maxLines = Math.max(1, (bottom - 88) / 10);
        int cardHeight = Math.max(44, 43 + Math.min(crewLines, maxLines) * 10);
        int rows = Math.max(1, (bottom - 43) / cardHeight);
        int capacity = rows;
        hoverPages = (near.size() + capacity - 1) / capacity;
        hoverPage = Math.min(hoverPage, hoverPages - 1);
        List<RoundReport.Ticket> visible = near.subList(hoverPage * capacity, Math.min(near.size(), (hoverPage + 1) * capacity));
        int panelWidth = cardWidth;
        int panelHeight = visible.size() * cardHeight + (hoverPages > 1 ? 12 : 0);
        int x = mx + 10 + panelWidth <= width - 4 ? mx + 10 : Math.max(4, mx - panelWidth - 10);
        int y = Math.max(29, Math.min(my + 8, bottom - panelHeight - 2));
        for (int i = 0; i < visible.size(); i++) {
            var event = visible.get(i);
            var reason = event.reason();
            int cx = x, cy = y + i * cardHeight;
            int color = teamColor(event.attack());
            g.fill(cx, cy, cx + cardWidth - 2, cy + cardHeight - 2, UiTheme.withAlpha(UiTheme.GLASS_PANEL, 247));
            g.fill(cx, cy, cx + cardWidth - 2, cy + 1, color);
            draw(g, font, reason.label(), cx + 5, cy + 5, cardWidth - 12, UiTheme.TEXT);
            String delta = String.format(Locale.ROOT, "%+d 票", event.after() - event.before());
            draw(g, font, time(event.tick()) + "  " + delta, cx + 5, cy + 17, cardWidth - 12, color);
            if (reason.cause() == RoundReport.Cause.VEHICLE) {
                draw(g, font, reason.squad().isBlank() ? "未分配小队" : reason.squad(), cx + 5, cy + 29, cardWidth - 12, UiTheme.TEXT_SECOND);
                var crew = crewLines(font, event, cardWidth);
                for (int n = 0; n < Math.min(maxLines, crew.size()); n++) {
                    g.drawString(font, crew.get(n), cx + 5, cy + 41 + n * 10, UiTheme.TEXT_SECOND, false);
                }
            }
        }
        if (hoverPages > 1) draw(g, font, "滚轮 " + (hoverPage + 1) + "/" + hoverPages,
            x + 5, y + panelHeight - 11, panelWidth, UiTheme.TEXT);
    }

    private static List<net.minecraft.util.FormattedCharSequence> crewLines(Font font, RoundReport.Ticket event, int width) {
        var occupants = event.reason().occupants();
        return font.split(net.minecraft.network.chat.Component.literal(occupants.isEmpty()
            ? "车上无人" : "车上：" + String.join("、", occupants)), Math.max(1, width - 12));
    }

    boolean scroll(double delta) {
        if (hoverPages <= 1 || delta == 0) return false;
        hoverPage = Math.max(0, Math.min(hoverPages - 1, hoverPage + (delta < 0 ? 1 : -1)));
        return true;
    }

    static void draw(GuiGraphics g, Font font, String text, int x, int y, int width, int color) {
        if (width > 0) g.drawString(font, font.plainSubstrByWidth(text, width), x, y, color, false);
    }

    static int teamColor(boolean attack) { return attack ? UiTheme.ATTACK : UiTheme.DEFEND; }
    static String time(int ticks) {
        int seconds = ticks / 20;
        return String.format(Locale.ROOT, "%02d:%02d", seconds / 60, seconds % 60);
    }
}
