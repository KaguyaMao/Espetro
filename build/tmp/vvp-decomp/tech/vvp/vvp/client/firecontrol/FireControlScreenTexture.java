/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.mojang.blaze3d.platform.NativeImage
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.Vec3
 */
package tech.vvp.vvp.client.firecontrol;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import tech.vvp.vvp.client.firecontrol.FireControlClientState;
import tech.vvp.vvp.client.firecontrol.FireControlMapMath;
import tech.vvp.vvp.client.firecontrol.TabletFontRasterizer;
import tech.vvp.vvp.client.firecontrol.XaeroMapSampler;
import tech.vvp.vvp.entity.vehicle.HimarsEntity;
import tech.vvp.vvp.firecontrol.HimarsBallisticsUtil;

public class FireControlScreenTexture
extends DynamicTexture {
    public static final int WIDTH = 256;
    public static final int HEIGHT = 128;
    private static final int COLOR_BLACK = XaeroMapSampler.color(0, 0, 0, 255);
    private static final int COLOR_GREEN = -5193616;
    private static final int COLOR_AMBER = -2570072;
    private static final int COLOR_DIM = -7570846;
    private static final int COLOR_RED = -3387312;
    private static final int COLOR_CYAN = -11874049;
    private int tickCount;
    private final Long2IntOpenHashMap blockColorCache = new Long2IntOpenHashMap(65536);
    private String lastDimension = "";
    private static final int HUD_X = 20;
    private static final int HUD_Y = 6;
    private static final int HUD_LINE = 8;

    public FireControlScreenTexture() {
        super(256, 128, true);
    }

    public void update() {
        NativeImage image = this.m_117991_();
        if (image == null) {
            return;
        }
        FireControlClientState.TabletPower power = FireControlClientState.getPower();
        ++this.tickCount;
        switch (power) {
            case OFF: {
                FireControlScreenTexture.fill(image, COLOR_BLACK);
                break;
            }
            case BOOTING: {
                this.drawBootGraphics(image);
                this.drawBootHud(image);
                break;
            }
            case SHUTTING_DOWN: {
                this.drawShutdownGraphics(image);
                this.drawShutdownHud(image);
                break;
            }
            case ONLINE: {
                this.drawOnlineMap(image);
                this.drawOnlineHud(image);
            }
        }
        if (power != FireControlClientState.TabletPower.OFF) {
            FireControlScreenTexture.rotate180(image);
        }
        this.m_117985_();
    }

    private static void rotate180(NativeImage image) {
        int ox;
        int x;
        int w = image.m_84982_();
        int h = image.m_85084_();
        for (int y = 0; y < h / 2; ++y) {
            for (x = 0; x < w; ++x) {
                ox = w - 1 - x;
                int oy = h - 1 - y;
                int top = image.m_84985_(x, y);
                int bottom = image.m_84985_(ox, oy);
                image.m_84988_(x, y, bottom);
                image.m_84988_(ox, oy, top);
            }
        }
        if ((h & 1) == 1) {
            int mid = h / 2;
            for (x = 0; x < w / 2; ++x) {
                ox = w - 1 - x;
                int left = image.m_84985_(x, mid);
                int right = image.m_84985_(ox, mid);
                image.m_84988_(x, mid, right);
                image.m_84988_(ox, mid, left);
            }
        }
    }

    private void drawOnlineMap(NativeImage image) {
        Minecraft mc = Minecraft.m_91087_();
        ClientLevel level = mc.f_91073_;
        if (level == null) {
            FireControlScreenTexture.fill(image, COLOR_BLACK);
            return;
        }
        HimarsEntity himars = FireControlClientState.getBoundHimars();
        double centerX = FireControlClientState.getMapCenterX();
        double centerZ = FireControlClientState.getMapCenterZ();
        double zoom = FireControlClientState.getZoom();
        double radius = FireControlMapMath.viewRadius(zoom);
        XaeroMapSampler.beginFrame(level);
        XaeroMapSampler.prepareView(level, centerX, centerZ, radius);
        String currentDim = level.m_46472_().m_135782_().toString();
        if (!currentDim.equals(this.lastDimension)) {
            this.blockColorCache.clear();
            this.lastDimension = currentDim;
        }
        if (this.blockColorCache.size() > 524288) {
            this.blockColorCache.clear();
        }
        int step = FireControlMapMath.mapPixelStep(zoom);
        for (int x = 0; x < 256; x += step) {
            for (int y = 0; y < 128; y += step) {
                double wx = FireControlMapMath.screenToWorldX(x, centerX, zoom);
                double wz = FireControlMapMath.screenToWorldZ(y, centerZ, zoom);
                int blockX = (int)Math.floor(wx);
                int blockZ = (int)Math.floor(wz);
                long cacheKey = (long)blockX << 32 | (long)blockZ & 0xFFFFFFFFL;
                int color = XaeroMapSampler.sampleColorFast(blockX, blockZ);
                if (color != 0) {
                    this.blockColorCache.put(cacheKey, color);
                } else {
                    color = this.blockColorCache.get(cacheKey);
                }
                int finalColor = color != 0 ? color : XaeroMapSampler.color(12, 16, 24, 255);
                for (int ddx = 0; ddx < step && x + ddx < 256; ++ddx) {
                    for (int ddy = 0; ddy < step && y + ddy < 128; ++ddy) {
                        image.m_84988_(x + ddx, y + ddy, finalColor);
                    }
                }
            }
        }
        if (himars != null) {
            FireControlScreenTexture.drawVehicleMarkerWithTurret(image, himars, centerX, centerZ, zoom, XaeroMapSampler.color(74, 144, 217, 255));
        }
        if (FireControlClientState.hasTarget()) {
            FireControlScreenTexture.drawTargetMarker(image, (double)FireControlClientState.getTargetX() + 0.5, (double)FireControlClientState.getTargetZ() + 0.5, centerX, centerZ, zoom);
        }
        if (himars != null && FireControlClientState.hasTarget()) {
            this.drawBallisticArc(image, himars, centerX, centerZ, zoom);
        }
        FireControlScreenTexture.drawCrosshair(image, 128, 64);
        this.drawScanline(image);
    }

    private void drawXpFlag(NativeImage image, int startX, int startY) {
        int[] trailColors = new int[]{XaeroMapSampler.color(0, 100, 180, 160), XaeroMapSampler.color(210, 50, 35, 160), XaeroMapSampler.color(80, 170, 40, 160)};
        for (int t = 0; t < 3; ++t) {
            int txStart = startX - 8 - t * 4;
            int tyStart = startY + 17 + t * 4;
            for (int j = 0; j < 12; ++j) {
                int px = txStart + j;
                int py = tyStart - (int)((double)j * 0.3) + (int)(Math.sin((double)j * 0.4) * 1.0);
                if (px < 0 || px >= 256 || py < 0 || py >= 128) continue;
                image.m_84988_(px, py, trailColors[t]);
            }
        }
        for (int dx = 0; dx < 36; ++dx) {
            double centerY = (double)startY + 11.0 - (double)dx * 0.25 + Math.sin((double)dx * 0.16) * 1.8;
            int yStart = (int)Math.round(centerY - 12.0);
            int yEnd = (int)Math.round(centerY + 12.0);
            for (int y = yStart; y <= yEnd; ++y) {
                int factor;
                boolean isTop;
                int px = startX + dx;
                int py = y;
                if (px < 0 || px >= 256 || py < 0 || py >= 128) continue;
                if (dx == 0 || dx == 35 || y == yStart || y == yEnd || dx == 17 || dx == 18 || y == (int)Math.round(centerY) || y == (int)Math.round(centerY) - 1) {
                    image.m_84988_(px, py, XaeroMapSampler.color(0, 0, 0, 255));
                    continue;
                }
                boolean isLeft = dx < 17;
                boolean bl = isTop = (double)y < centerY;
                if (isLeft && isTop) {
                    factor = dx * 4;
                    image.m_84988_(px, py, XaeroMapSampler.color(240 - factor, 60 - factor / 3, 40 - factor / 4, 255));
                    continue;
                }
                if (!isLeft && isTop) {
                    factor = (dx - 18) * 4;
                    image.m_84988_(px, py, XaeroMapSampler.color(110 - factor / 2, 205 - factor, 50 - factor / 3, 255));
                    continue;
                }
                if (isLeft && !isTop) {
                    factor = dx * 4;
                    image.m_84988_(px, py, XaeroMapSampler.color(0, 120 - factor, 210 - factor / 2, 255));
                    continue;
                }
                factor = (dx - 18) * 4;
                image.m_84988_(px, py, XaeroMapSampler.color(245 - factor, 190 - factor, 10, 255));
            }
        }
    }

    private void drawBootGraphics(NativeImage image) {
        FireControlScreenTexture.fill(image, COLOR_BLACK);
        this.drawXpFlag(image, 110, 25);
        int trackX1 = 93;
        int trackX2 = 163;
        int trackY1 = 95;
        int trackY2 = 101;
        int borderColor = XaeroMapSampler.color(80, 80, 80, 255);
        for (int x = trackX1; x <= trackX2; ++x) {
            image.m_84988_(x, trackY1, borderColor);
            image.m_84988_(x, trackY2, borderColor);
        }
        for (int y = trackY1; y <= trackY2; ++y) {
            image.m_84988_(trackX1, y, borderColor);
            image.m_84988_(trackX2, y, borderColor);
        }
        int blockWidth = 4;
        int blockSpacing = 2;
        int trackInnerWidth = trackX2 - 1 - (trackX1 + 1);
        int cycleWidth = trackInnerWidth + 16;
        int animOffset = this.tickCount * 2 % cycleWidth - 8;
        for (int b = 0; b < 3; ++b) {
            int bx = trackX1 + 2 + animOffset - b * (blockWidth + blockSpacing);
            if (bx < trackX1 + 2 || bx + blockWidth > trackX2 - 2) continue;
            for (int x = bx; x < bx + blockWidth; ++x) {
                for (int y = trackY1 + 1; y <= trackY2 - 1; ++y) {
                    image.m_84988_(x, y, XaeroMapSampler.color(0, 162, 232, 255));
                }
            }
        }
    }

    private void drawBootHud(NativeImage image) {
        TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)"WINDOWS"), 94, 65, XaeroMapSampler.color(255, 255, 255, 255));
        TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)"XP"), 142, 65, XaeroMapSampler.color(220, 60, 45, 255));
        TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)"VVP CORPORATION"), 83, 115, XaeroMapSampler.color(120, 120, 120, 255));
    }

    private void drawShutdownGraphics(NativeImage image) {
        int x;
        int y;
        for (y = 0; y <= 18; ++y) {
            for (x = 0; x < 256; ++x) {
                image.m_84988_(x, y, XaeroMapSampler.color(0, 45, 150, 255));
            }
        }
        for (y = 19; y <= 108; ++y) {
            for (x = 0; x < 256; ++x) {
                double dx = x - 30;
                double dy = y - 40;
                double dist = Math.sqrt(dx * dx + dy * dy);
                int r = (int)Math.max(90.0, 170.0 - dist * 0.4);
                int g = (int)Math.max(126.0, 200.0 - dist * 0.4);
                int b = (int)Math.max(220.0, 245.0 - dist * 0.15);
                image.m_84988_(x, y, XaeroMapSampler.color(r, g, b, 255));
            }
        }
        for (int x2 = 0; x2 < 256; ++x2) {
            image.m_84988_(x2, 109, XaeroMapSampler.color(255, 160, 0, 255));
        }
        for (y = 110; y < 128; ++y) {
            for (x = 0; x < 256; ++x) {
                image.m_84988_(x, y, XaeroMapSampler.color(0, 45, 150, 255));
            }
        }
        this.drawXpFlag(image, 110, 25);
    }

    private void drawShutdownHud(NativeImage image) {
        TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)"WINDOWS"), 94, 65, XaeroMapSampler.color(255, 255, 255, 255));
        TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)"XP"), 142, 65, XaeroMapSampler.color(220, 60, 45, 255));
        TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)"WINDOWS IS SHUTTING DOWN..."), 53, 78, XaeroMapSampler.color(255, 255, 255, 255));
        TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)"VVP CORPORATION"), 8, 115, XaeroMapSampler.color(255, 255, 255, 255));
        TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)"VVP"), 232, 115, XaeroMapSampler.color(255, 255, 255, 255));
    }

    private void drawOnlineHud(NativeImage image) {
        int fc;
        String fs;
        HimarsEntity himars = FireControlClientState.getBoundHimars();
        if (himars == null) {
            return;
        }
        if (!FireControlClientState.hasTarget() && himars.isFdcTargetDesignated()) {
            int tx = himars.getFdcTargetX();
            int ty = himars.getFdcTargetY();
            int tz = himars.getFdcTargetZ();
            Vec3 origin = HimarsBallisticsUtil.resolveShootOrigin(himars);
            HimarsBallisticsUtil.FireSolution sol = HimarsBallisticsUtil.solve(himars, origin, (double)tx + 0.5, ty, (double)tz + 0.5);
            FireControlClientState.setTarget(tx, ty, tz, sol);
        }
        int BAR = XaeroMapSampler.color(3, 9, 3, 255);
        int SEP = XaeroMapSampler.color(0, 110, 45, 255);
        for (int y = 0; y < 12; ++y) {
            for (int x = 0; x < 256; ++x) {
                image.m_84988_(x, y, BAR);
            }
        }
        FireControlScreenTexture.drawHLine(image, 0, 255, 11, SEP);
        TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)("X" + himars.m_146903_() + " Z" + himars.m_146907_())), 8, 8, -7570846);
        boolean hold = himars.isStationaryForFire();
        TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)(hold ? "HOLD" : "MOVE")), 113, 8, hold ? -5193616 : -3387312);
        int hdg = Math.floorMod((int)himars.m_146908_() + 180, 360);
        TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)("HDG " + hdg)), 199, 8, -7570846);
        for (int y = 113; y < 128; ++y) {
            for (int x = 0; x < 256; ++x) {
                image.m_84988_(x, y, BAR);
            }
        }
        FireControlScreenTexture.drawHLine(image, 0, 255, 113, SEP);
        int TY = 118;
        if (FireControlClientState.hasTarget()) {
            HimarsBallisticsUtil.FireSolution solution = FireControlClientState.getSolution();
            if (solution != null) {
                boolean oor;
                double dx = (double)FireControlClientState.getTargetX() + 0.5 - himars.m_20185_();
                double dz = (double)FireControlClientState.getTargetZ() + 0.5 - himars.m_20189_();
                double range = Math.sqrt(dx * dx + dz * dz);
                double minArc = HimarsBallisticsUtil.getMinRange(himars);
                double maxArc = HimarsBallisticsUtil.getMaxRange(himars);
                boolean bl = oor = range < minArc || range > maxArc || !solution.inArc();
                String rngVal = range < minArc ? "<" + (int)minArc + "M" : (range > maxArc ? ">" + (int)maxArc + "M" : (int)Math.round(range) + "M");
                int el = (int)(-himars.getTurretXRot());
                int az = (int)Mth.m_14177_((float)(himars.getTurretYRot() - himars.m_146908_()));
                int tof = (int)solution.timeOfFlight();
                TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)"RNG"), 8, 118, -7570846);
                TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)rngVal), 27, 118, oor ? -3387312 : -2570072);
                TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)"AZ"), 65, 118, -7570846);
                TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)String.valueOf(az)), 77, 118, -2570072);
                TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)"EL"), 101, 118, -7570846);
                TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)String.valueOf(el)), 113, 118, -2570072);
                TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)"TOF"), 135, 118, -7570846);
                TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)(tof + "s")), 155, 118, -2570072);
            }
        } else {
            TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)"SPACE:MARK   ENTER:FIRE"), 8, 118, -7570846);
        }
        FireControlScreenTexture.drawVLine(image, 168, 114, 127, SEP);
        if (himars.isFdcAwaitingFire()) {
            if (himars.isFdcBarrelAligned()) {
                fs = "FIRE RDY";
                fc = -5193616;
            } else {
                fs = "SLEWING";
                fc = -11874049;
            }
        } else if (himars.isFdcSlewing()) {
            fs = "SLEWING";
            fc = -11874049;
        } else if (himars.isFdcTargetDesignated()) {
            fs = "TGT OK";
            fc = -5193616;
        } else {
            HimarsBallisticsUtil.FireSolution s2 = FireControlClientState.getSolution();
            boolean ia = s2 != null && s2.inArc();
            fs = ia ? "READY" : "NO ARC";
            fc = ia ? -5193616 : -3387312;
        }
        TabletFontRasterizer.drawString(image, (Component)Component.m_237113_((String)fs), 178, 118, fc);
        GunData gunData = himars.getGunData("GMLRS");
        if (gunData != null) {
            int ammo = Math.max(0, gunData.ammo.get());
            int typeIdx = gunData.selectedAmmoType.get();
            int ammoCol = typeIdx == 0 ? -5193616 : -2570072;
            for (int i = 0; i < 6; ++i) {
                int ix = 222 + i * 6;
                int iy = 116;
                if (i < ammo) {
                    int r;
                    for (r = 0; r < 5; ++r) {
                        FireControlScreenTexture.setPixelSafe(image, ix, iy + r, ammoCol);
                    }
                    for (r = 1; r < 4; ++r) {
                        FireControlScreenTexture.setPixelSafe(image, ix + 1, iy + r, ammoCol);
                    }
                    continue;
                }
                FireControlScreenTexture.setPixelSafe(image, ix, iy, -7570846);
                FireControlScreenTexture.setPixelSafe(image, ix + 1, iy, -7570846);
                FireControlScreenTexture.setPixelSafe(image, ix, iy + 4, -7570846);
                FireControlScreenTexture.setPixelSafe(image, ix + 1, iy + 4, -7570846);
            }
        }
    }

    private void drawBallisticArc(NativeImage image, HimarsEntity himars, double centerX, double centerZ, double zoom) {
        if (!FireControlClientState.hasTarget()) {
            return;
        }
        HimarsBallisticsUtil.FireSolution solution = FireControlClientState.getSolution();
        if (solution == null) {
            return;
        }
        int hx = FireControlMapMath.worldToScreenX(himars.m_20185_(), centerX, zoom);
        int hy = FireControlMapMath.worldToScreenY(himars.m_20189_(), centerZ, zoom);
        int tx = FireControlMapMath.worldToScreenX(solution.targetX(), centerX, zoom);
        int ty = FireControlMapMath.worldToScreenY(solution.targetZ(), centerZ, zoom);
        int lineColor = solution.inArc() ? XaeroMapSampler.color(255, 220, 50, 255) : XaeroMapSampler.color(200, 80, 60, 255);
        int dashOffset = this.tickCount / 2 % 6;
        double dist = Math.sqrt((double)(tx - hx) * (double)(tx - hx) + (double)(ty - hy) * (double)(ty - hy));
        if (dist < 1.0) {
            return;
        }
        int steps = (int)dist;
        for (int i = 0; i <= steps; ++i) {
            if ((i + dashOffset) / 3 % 2 != 0) continue;
            float t = (float)i / (float)steps;
            FireControlScreenTexture.setPixelSafe(image, hx + (int)((float)(tx - hx) * t), hy + (int)((float)(ty - hy) * t), lineColor);
        }
        int circleColor = XaeroMapSampler.color(255, 60, 40, 255);
        for (int a = 0; a < 360; a += 8) {
            if (a / 8 % 2 == 0) continue;
            double rad = Math.toRadians(a);
            FireControlScreenTexture.setPixelSafe(image, tx + (int)(Math.cos(rad) * 12.0), ty + (int)(Math.sin(rad) * 12.0), circleColor);
        }
    }

    private static void setPixelSafe(NativeImage image, int x, int y, int color) {
        if (x >= 0 && x < 256 && y >= 0 && y < 128) {
            image.m_84988_(x, y, color);
        }
    }

    private void drawScanline(NativeImage image) {
        int scanY = this.tickCount * 2 % 128;
        for (int dist = -4; dist <= 4; ++dist) {
            int y = scanY + dist;
            if (y < 0 || y >= 128) continue;
            int abs = Math.abs(dist);
            int addG = abs == 0 ? 90 : (abs == 1 ? 55 : (abs == 2 ? 28 : (abs == 3 ? 12 : 5)));
            int addR = addG / 5;
            for (int x = 0; x < 256; ++x) {
                int col = image.m_84985_(x, y);
                int r = Math.min(255, (col & 0xFF) + addR);
                int g = Math.min(255, (col >> 8 & 0xFF) + addG);
                int b = col >> 16 & 0xFF;
                image.m_84988_(x, y, XaeroMapSampler.color(r, g, b, 255));
            }
        }
    }

    private static void fill(NativeImage image, int color) {
        for (int x = 0; x < 256; ++x) {
            for (int y = 0; y < 128; ++y) {
                image.m_84988_(x, y, color);
            }
        }
    }

    private static void drawVehicleMarkerWithTurret(NativeImage image, HimarsEntity himars, double centerX, double centerZ, double zoom, int bodyColor) {
        double wx = himars.m_20185_();
        double wz = himars.m_20189_();
        int px = FireControlMapMath.worldToScreenX(wx, centerX, zoom);
        int py = FireControlMapMath.worldToScreenY(wz, centerZ, zoom);
        FireControlScreenTexture.fillSquare(image, px, py, 2, bodyColor);
        Vec3 barrelDir = himars.getBarrelVector(1.0f);
        double worldTurretYaw = Math.toDegrees(-Math.atan2(barrelDir.f_82479_, barrelDir.f_82481_));
        double rad = Math.toRadians(worldTurretYaw);
        int dx = (int)Math.round(-Math.sin(rad) * 6.0);
        int dz = (int)Math.round(Math.cos(rad) * 6.0);
        FireControlScreenTexture.drawArrow(image, px, py, px + dx, py + dz, XaeroMapSampler.color(220, 100, 60, 255));
    }

    private static void drawVehicleMarker(NativeImage image, double wx, double wz, double centerX, double centerZ, double zoom, int color) {
        int px = FireControlMapMath.worldToScreenX(wx, centerX, zoom);
        int py = FireControlMapMath.worldToScreenY(wz, centerZ, zoom);
        FireControlScreenTexture.fillSquare(image, px, py, 2, color);
    }

    private static void drawTargetMarker(NativeImage image, double wx, double wz, double centerX, double centerZ, double zoom) {
        int px = FireControlMapMath.worldToScreenX(wx, centerX, zoom);
        int py = FireControlMapMath.worldToScreenY(wz, centerZ, zoom);
        int color = XaeroMapSampler.color(255, 60, 40, 255);
        FireControlScreenTexture.drawHLine(image, px - 5, px + 5, py, color);
        FireControlScreenTexture.drawVLine(image, px, py - 5, py + 5, color);
        FireControlScreenTexture.fillSquare(image, px, py, 1, XaeroMapSampler.color(255, 220, 80, 255));
    }

    private static void drawCrosshair(NativeImage image, int cx, int cy) {
        int color = XaeroMapSampler.color(80, 255, 120, 255);
        int size = 7;
        FireControlScreenTexture.drawHLine(image, cx - size, cx - 2, cy, color);
        FireControlScreenTexture.drawHLine(image, cx + 2, cx + size, cy, color);
        FireControlScreenTexture.drawVLine(image, cx, cy - size, cy - 2, color);
        FireControlScreenTexture.drawVLine(image, cx, cy + 2, cy + size, color);
    }

    private static void fillSquare(NativeImage image, int cx, int cy, int radius, int color) {
        for (int ox = -radius; ox <= radius; ++ox) {
            for (int oy = -radius; oy <= radius; ++oy) {
                int px = cx + ox;
                int py = cy + oy;
                if (px < 0 || px >= 256 || py < 0 || py >= 128) continue;
                image.m_84988_(px, py, color);
            }
        }
    }

    private static void drawArrow(NativeImage image, int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            if (x0 >= 0 && x0 < 256 && y0 >= 0 && y0 < 128) {
                image.m_84988_(x0, y0, color);
            }
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 >= dx) continue;
            err += dx;
            y0 += sy;
        }
        double angle = Math.atan2(y1 - (y0 - (y1 - y0)), x1 - (x0 - (x1 - x0)));
        int[] ahx = new int[]{x1 + (int)(Math.cos(angle + 2.5) * 3.0), x1 + (int)(Math.cos(angle - 2.5) * 3.0)};
        int[] ahy = new int[]{y1 + (int)(Math.sin(angle + 2.5) * 3.0), y1 + (int)(Math.sin(angle - 2.5) * 3.0)};
        for (int i = 0; i < 2; ++i) {
            if (ahx[i] < 0 || ahx[i] >= 256 || ahy[i] < 0 || ahy[i] >= 128) continue;
            image.m_84988_(ahx[i], ahy[i], color);
        }
    }

    private static void drawHLine(NativeImage image, int x1, int x2, int y, int color) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); ++x) {
            if (x < 0 || x >= 256 || y < 0 || y >= 128) continue;
            image.m_84988_(x, y, color);
        }
    }

    private static void drawVLine(NativeImage image, int x, int y1, int y2, int color) {
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); ++y) {
            if (x < 0 || x >= 256 || y < 0 || y >= 128) continue;
            image.m_84988_(x, y, color);
        }
    }

    public static int resolveTargetSurfaceY(ClientLevel level, int blockX, int blockZ) {
        return level.m_6924_(Heightmap.Types.WORLD_SURFACE, blockX, blockZ);
    }
}

