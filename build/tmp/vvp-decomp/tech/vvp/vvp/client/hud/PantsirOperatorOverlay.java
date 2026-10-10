/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.init.ModKeyMappings
 *  com.atsuishio.superbwarfare.init.ModSounds
 *  com.atsuishio.superbwarfare.tools.VectorToolKt
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.event.RenderGuiEvent$Post
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  org.joml.Matrix4f
 *  org.lwjgl.glfw.GLFW
 */
package tech.vvp.vvp.client.hud;

import com.atsuishio.superbwarfare.init.ModKeyMappings;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.VectorToolKt;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import tech.vvp.vvp.client.PantsirClientHandler;
import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;
import tech.vvp.vvp.network.VVPNetwork;
import tech.vvp.vvp.network.message.PantsirLockRequestMessage;

@OnlyIn(value=Dist.CLIENT)
@Mod.EventBusSubscriber(modid="vvp", value={Dist.CLIENT})
public class PantsirOperatorOverlay {
    private static final int COLOR_RADAR_GREEN = -16711936;
    private static final int COLOR_RADAR_DARK = -16764160;
    private static final int COLOR_RADAR_BG = -536866560;
    private static final int COLOR_TARGET_YELLOW = -256;
    private static final int COLOR_TARGET_RED = -52429;
    private static final int COLOR_LOCKED = -16711936;
    private static final int COLOR_TARGET_BLIP = -39424;
    private static final int COLOR_TURRET_BEAM = -256;
    private static final int COLOR_SSC_SECTOR = 0x40FFFF00;
    private static final int COLOR_PANEL_BG = -1073741824;
    private static final int COLOR_MISSILE = -16711681;
    private static final int RADAR_RADIUS = 60;
    private static final int RADAR_SEGMENTS = 32;
    private static final int MARKER_SIZE = 24;
    private static final int MARKER_HALF = 12;
    private static final double RADAR_RANGE = 1100.0;
    private static final float SSC_HALF_ANGLE = 3.0f;
    private static boolean wasLockKeyPressed = false;
    private static int lastRadarState = -1;
    private static long lastLockingSoundTime = 0L;
    private static boolean wasNextKeyPressed = false;
    private static boolean wasPrevKeyPressed = false;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null || mc.f_91066_.f_92062_) {
            return;
        }
        Entity vehicle = player.m_20202_();
        if (!(vehicle instanceof PantsirS1Entity)) {
            PantsirClientHandler.reset();
            return;
        }
        PantsirS1Entity pantsir = (PantsirS1Entity)vehicle;
        int seatIndex = pantsir.getSeatIndex((Entity)player);
        if (seatIndex != 1) {
            return;
        }
        int vehicleId = pantsir.m_19879_();
        PantsirOperatorOverlay.handleLockKeyInput((Player)player);
        PantsirOperatorOverlay.handleTargetSwitchInput((Player)player);
        PantsirOperatorOverlay.handleLockSounds(vehicleId);
        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.m_91268_().m_85445_();
        int screenHeight = mc.m_91268_().m_85446_();
        PoseStack poseStack = guiGraphics.m_280168_();
        poseStack.m_85836_();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        PantsirOperatorOverlay.renderRadarDisplay(guiGraphics, poseStack, screenWidth, screenHeight, (Player)player, vehicleId);
        PantsirOperatorOverlay.renderStatusPanel(guiGraphics, screenWidth, screenHeight, vehicleId);
        PantsirOperatorOverlay.renderTargetMarker(guiGraphics, screenWidth, screenHeight, vehicleId);
        PantsirOperatorOverlay.renderControlHint(guiGraphics, screenWidth, screenHeight, vehicleId);
        poseStack.m_85849_();
    }

    private static void handleLockKeyInput(Player player) {
        Entity vehicle = player.m_20202_();
        if (!(vehicle instanceof PantsirS1Entity)) {
            return;
        }
        PantsirS1Entity pantsir = (PantsirS1Entity)vehicle;
        PantsirClientHandler.PantsirRadarData data = PantsirClientHandler.getRadarData(pantsir.m_19879_());
        if (data == null) {
            return;
        }
        boolean isPressed = ModKeyMappings.VEHICLE_SEEK.m_90857_();
        if (isPressed && !wasLockKeyPressed) {
            int state = data.radarState;
            if (state == 1) {
                VVPNetwork.VVP_HANDLER.sendToServer((Object)new PantsirLockRequestMessage(0));
            } else if (state == 2 || state == 3) {
                VVPNetwork.VVP_HANDLER.sendToServer((Object)new PantsirLockRequestMessage(1));
            }
        }
        wasLockKeyPressed = isPressed;
    }

    private static void handleTargetSwitchInput(Player player) {
        boolean rightPressed;
        boolean leftPressed;
        Entity vehicle = player.m_20202_();
        if (!(vehicle instanceof PantsirS1Entity)) {
            return;
        }
        PantsirS1Entity pantsir = (PantsirS1Entity)vehicle;
        PantsirClientHandler.PantsirRadarData data = PantsirClientHandler.getRadarData(pantsir.m_19879_());
        if (data == null) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        long window = mc.m_91268_().m_85439_();
        boolean bl = leftPressed = GLFW.glfwGetKey((long)window, (int)263) == 1;
        if (leftPressed && !wasPrevKeyPressed && data.radarState != 2 && data.radarState != 3) {
            VVPNetwork.VVP_HANDLER.sendToServer((Object)new PantsirLockRequestMessage(3));
        }
        wasPrevKeyPressed = leftPressed;
        boolean bl2 = rightPressed = GLFW.glfwGetKey((long)window, (int)262) == 1;
        if (rightPressed && !wasNextKeyPressed && data.radarState != 2 && data.radarState != 3) {
            VVPNetwork.VVP_HANDLER.sendToServer((Object)new PantsirLockRequestMessage(2));
        }
        wasNextKeyPressed = rightPressed;
    }

    private static void handleLockSounds(int vehicleId) {
        PantsirClientHandler.PantsirRadarData data = PantsirClientHandler.getRadarData(vehicleId);
        if (data == null) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null) {
            return;
        }
        int currentState = data.radarState;
        long now = System.currentTimeMillis();
        if (currentState == 2 && lastRadarState != 2) {
            player.m_5496_((SoundEvent)ModSounds.MISSILE_LOCKING.get(), 2.0f, 1.0f);
            lastLockingSoundTime = now;
        }
        if (currentState == 3 && now - lastLockingSoundTime > 500L) {
            player.m_5496_((SoundEvent)ModSounds.MISSILE_LOCKED.get(), 2.0f, 1.0f);
            lastLockingSoundTime = now;
        }
        lastRadarState = currentState;
    }

    private static void renderRadarDisplay(GuiGraphics guiGraphics, PoseStack poseStack, int screenWidth, int screenHeight, Player player, int vehicleId) {
        PantsirClientHandler.PantsirRadarData data = PantsirClientHandler.getRadarData(vehicleId);
        if (data == null) {
            return;
        }
        int centerX = screenWidth - 60 - 25;
        int centerY = 100;
        PantsirOperatorOverlay.drawFilledCircle(poseStack, centerX + 2, centerY + 2, 62, Integer.MIN_VALUE);
        PantsirOperatorOverlay.drawFilledCircle(poseStack, centerX, centerY, 60, -536866560);
        int gridColor = 0x40003300;
        int diag = 42;
        PantsirOperatorOverlay.drawLine(poseStack, centerX - diag, centerY - diag, centerX + diag, centerY + diag, gridColor, 1);
        PantsirOperatorOverlay.drawLine(poseStack, centerX - diag, centerY + diag, centerX + diag, centerY - diag, gridColor, 1);
        PantsirOperatorOverlay.drawCircleOutline(poseStack, centerX, centerY, 15.0f, -16764160);
        PantsirOperatorOverlay.drawCircleOutline(poseStack, centerX, centerY, 30.0f, -16764160);
        PantsirOperatorOverlay.drawCircleOutline(poseStack, centerX, centerY, 45.0f, -16764160);
        PantsirOperatorOverlay.drawCircleOutline(poseStack, centerX, centerY, 58.0f, -16711936);
        PantsirOperatorOverlay.drawCircleOutline(poseStack, centerX, centerY, 59.0f, -2147418368);
        PantsirOperatorOverlay.drawCircleOutline(poseStack, centerX, centerY, 60.0f, 0x4000FF00);
        guiGraphics.m_280509_(centerX - 1, centerY - 10, centerX + 1, centerY + 10, -16764160);
        guiGraphics.m_280509_(centerX - 10, centerY - 1, centerX + 10, centerY + 1, -16764160);
        PantsirOperatorOverlay.drawFilledCircle(poseStack, centerX, centerY, 3, 0x6000FF00);
        guiGraphics.m_280509_(centerX - 2, centerY - 2, centerX + 2, centerY + 2, -16711936);
        PantsirOperatorOverlay.drawRadarSweep(poseStack, centerX, centerY, vehicleId, data);
        PantsirOperatorOverlay.drawTurretBeam(poseStack, centerX, centerY, data);
        PantsirOperatorOverlay.drawAllTargetBlips(guiGraphics, poseStack, centerX, centerY, player, data);
        PantsirOperatorOverlay.drawMissiles(guiGraphics, poseStack, centerX, centerY, player, data);
        Minecraft mc = Minecraft.m_91087_();
        guiGraphics.m_280056_(mc.f_91062_, "N", centerX - 3, centerY - 60 - 12, -16711936, true);
        guiGraphics.m_280056_(mc.f_91062_, "S", centerX - 3, centerY + 60 + 4, -16711936, true);
        guiGraphics.m_280056_(mc.f_91062_, "W", centerX - 60 - 10, centerY - 4, -16711936, true);
        guiGraphics.m_280056_(mc.f_91062_, "E", centerX + 60 + 4, centerY - 4, -16711936, true);
        String rangeText = "1100m";
        int rangeWidth = mc.f_91062_.m_92895_(rangeText);
        guiGraphics.m_280509_(centerX + 60 - rangeWidth - 6, centerY - 60 + 1, centerX + 60, centerY - 60 + 11, -1073741824);
        guiGraphics.m_280056_(mc.f_91062_, rangeText, centerX + 60 - rangeWidth - 4, centerY - 60 + 3, -16711936, false);
        int targetCount = data.allTargets.size();
        if (targetCount > 0) {
            String tgtText = "\u25ce " + targetCount;
            int tgtWidth = mc.f_91062_.m_92895_(tgtText);
            guiGraphics.m_280509_(centerX - 60 + 1, centerY - 60 + 1, centerX - 60 + tgtWidth + 9, centerY - 60 + 11, -1073741824);
            guiGraphics.m_280056_(mc.f_91062_, tgtText, centerX - 60 + 5, centerY - 60 + 3, -256, false);
        }
    }

    private static void drawRadarSweep(PoseStack poseStack, int centerX, int centerY, int vehicleId, PantsirClientHandler.PantsirRadarData data) {
        float radarAngle = PantsirClientHandler.getInterpolatedRadarAngle(vehicleId);
        double radians = Math.toRadians(radarAngle);
        int beamLength = 55;
        int endX = centerX + (int)(Math.sin(radians) * (double)beamLength);
        int endY = centerY + (int)(Math.cos(radians) * (double)beamLength);
        int beamColor = switch (data.radarState) {
            case 3 -> -16711936;
            case 1, 2 -> -256;
            default -> -16711936;
        };
        float sectorAngle = 15.0f;
        int trailSegments = 8;
        for (int i = 0; i < trailSegments; ++i) {
            float trailAngle = radarAngle - (float)i * 3.0f;
            double trailRad = Math.toRadians(trailAngle);
            double leftRad = trailRad - Math.toRadians(sectorAngle);
            double rightRad = trailRad + Math.toRadians(sectorAngle);
            int leftX = centerX + (int)(Math.sin(leftRad) * (double)beamLength);
            int leftY = centerY + (int)(Math.cos(leftRad) * (double)beamLength);
            int rightX = centerX + (int)(Math.sin(rightRad) * (double)beamLength);
            int rightY = centerY + (int)(Math.cos(rightRad) * (double)beamLength);
            float alpha = (1.0f - (float)i / (float)trailSegments) * 0.15f;
            int trailColor = beamColor & 0xFFFFFF | (int)(alpha * 255.0f) << 24;
            PantsirOperatorOverlay.drawTriangle(poseStack, centerX, centerY, leftX, leftY, rightX, rightY, trailColor);
        }
        PantsirOperatorOverlay.drawLine(poseStack, centerX, centerY, endX, endY, beamColor, 3);
        int glowSize = 3;
        PantsirOperatorOverlay.drawFilledCircle(poseStack, endX, endY, glowSize, beamColor);
        double leftRad = radians - Math.toRadians(sectorAngle);
        double rightRad = radians + Math.toRadians(sectorAngle);
        int leftX = centerX + (int)(Math.sin(leftRad) * (double)beamLength);
        int leftY = centerY + (int)(Math.cos(leftRad) * (double)beamLength);
        int rightX = centerX + (int)(Math.sin(rightRad) * (double)beamLength);
        int rightY = centerY + (int)(Math.cos(rightRad) * (double)beamLength);
        int sectorAlpha = beamColor & 0xFFFFFF | 0x30000000;
        PantsirOperatorOverlay.drawTriangle(poseStack, centerX, centerY, leftX, leftY, rightX, rightY, sectorAlpha);
    }

    private static void drawTurretBeam(PoseStack poseStack, int centerX, int centerY, PantsirClientHandler.PantsirRadarData data) {
        float turretAngle = data.turretAngle;
        double radians = Math.toRadians(turretAngle);
        int beamLength = 55;
        int endX = centerX - (int)(Math.sin(radians) * (double)beamLength);
        int endY = centerY + (int)(Math.cos(radians) * (double)beamLength);
        PantsirOperatorOverlay.drawLine(poseStack, centerX, centerY, endX, endY, -256, 2);
        double leftRad = radians - Math.toRadians(3.0);
        double rightRad = radians + Math.toRadians(3.0);
        int leftX = centerX - (int)(Math.sin(leftRad) * (double)beamLength);
        int leftY = centerY + (int)(Math.cos(leftRad) * (double)beamLength);
        int rightX = centerX - (int)(Math.sin(rightRad) * (double)beamLength);
        int rightY = centerY + (int)(Math.cos(rightRad) * (double)beamLength);
        PantsirOperatorOverlay.drawTriangle(poseStack, centerX, centerY, leftX, leftY, rightX, rightY, 0x40FFFF00);
    }

    private static void drawAllTargetBlips(GuiGraphics guiGraphics, PoseStack poseStack, int centerX, int centerY, Player player, PantsirClientHandler.PantsirRadarData data) {
        Entity vehicle = player.m_20202_();
        if (vehicle == null) {
            return;
        }
        Vec3 radarPos = vehicle.m_20182_();
        for (PantsirClientHandler.RadarTarget target : data.allTargets) {
            long time;
            int blipColor;
            boolean isMainTarget;
            Vec3 targetPos = new Vec3(target.x, target.y, target.z);
            Vec3 toTarget = targetPos.m_82546_(radarPos);
            double distance = toTarget.m_165924_();
            double normalizedDist = Math.min(distance / 1100.0, 1.0);
            int radarDist = (int)(normalizedDist * 52.0);
            int blipX = centerX + (int)(toTarget.f_82479_ / distance * (double)radarDist);
            int blipY = centerY + (int)(toTarget.f_82481_ / distance * (double)radarDist);
            boolean bl = isMainTarget = target.entityId == data.targetEntityId;
            if (target.isAlly) {
                blipColor = -16711936;
            } else if (isMainTarget) {
                blipColor = switch (data.radarState) {
                    case 3 -> -16711936;
                    case 2 -> -256;
                    default -> -52429;
                };
            } else {
                blipColor = target.targetType == 3 ? -52429 : -39424;
            }
            PantsirOperatorOverlay.drawTargetIcon(guiGraphics, poseStack, blipX, blipY, target.targetType, blipColor, isMainTarget);
            if (isMainTarget && data.radarState == 3 && (time = System.currentTimeMillis()) / 250L % 2L == 0L) {
                int size = 6;
                int thickness = 2;
                guiGraphics.m_280509_(blipX - size - 1, blipY - size - 1, blipX - size + 3, blipY - size - 1 + thickness, blipColor);
                guiGraphics.m_280509_(blipX - size - 1, blipY - size - 1, blipX - size - 1 + thickness, blipY - size + 3, blipColor);
                guiGraphics.m_280509_(blipX + size - 2, blipY - size - 1, blipX + size + 1, blipY - size - 1 + thickness, blipColor);
                guiGraphics.m_280509_(blipX + size - 1, blipY - size - 1, blipX + size + 1, blipY - size + 3, blipColor);
                guiGraphics.m_280509_(blipX - size - 1, blipY + size - 1, blipX - size + 3, blipY + size + 1, blipColor);
                guiGraphics.m_280509_(blipX - size - 1, blipY + size - 2, blipX - size - 1 + thickness, blipY + size + 1, blipColor);
                guiGraphics.m_280509_(blipX + size - 2, blipY + size - 1, blipX + size + 1, blipY + size + 1, blipColor);
                guiGraphics.m_280509_(blipX + size - 1, blipY + size - 2, blipX + size + 1, blipY + size + 1, blipColor);
            }
            if (!isMainTarget || data.radarState != 2) continue;
            time = System.currentTimeMillis();
            float progress = (float)data.lockProgress / 100.0f;
            int ringSize = (int)(8.0 + Math.sin((double)time / 100.0) * 2.0);
            int ringAlpha = (int)((0.5 + Math.sin((double)time / 150.0) * (double)0.3f) * 255.0);
            int ringColor = blipColor & 0xFFFFFF | ringAlpha << 24;
            PantsirOperatorOverlay.drawCircleOutline(poseStack, blipX, blipY, (float)ringSize * progress, ringColor);
        }
    }

    private static void drawTargetIcon(GuiGraphics guiGraphics, PoseStack poseStack, int x, int y, int targetType, int color, boolean isMain) {
        int size;
        int n = size = isMain ? 3 : 2;
        if (isMain) {
            long time = System.currentTimeMillis();
            float pulse = (float)(Math.sin((double)time / 200.0) * 0.3 + 0.7);
            int pulseAlpha = (int)(pulse * 255.0f);
            int glowColor = color & 0xFFFFFF | pulseAlpha << 24;
            int glowSize = size + 2;
            PantsirOperatorOverlay.drawFilledCircle(poseStack, x, y, glowSize, glowColor);
        }
        switch (targetType) {
            case 1: {
                guiGraphics.m_280509_(x - size, y - 1, x + size, y + 1, color);
                guiGraphics.m_280509_(x - 1, y - size, x + 1, y + size, color);
                guiGraphics.m_280509_(x - 2, y - 2, x + 2, y + 2, color);
                if (!isMain) break;
                guiGraphics.m_280509_(x - size - 1, y - 1, x - size, y + 1, color);
                guiGraphics.m_280509_(x + size, y - 1, x + size + 1, y + 1, color);
                guiGraphics.m_280509_(x - 1, y - size - 1, x + 1, y - size, color);
                guiGraphics.m_280509_(x - 1, y + size, x + 1, y + size + 1, color);
                break;
            }
            case 2: {
                PantsirOperatorOverlay.drawTriangleUp(poseStack, x, y - size, x - size, y + size, x + size, y + size, color);
                guiGraphics.m_280509_(x - size - 1, y, x + size + 1, y + 1, color);
                if (!isMain) break;
                PantsirOperatorOverlay.drawLine(poseStack, x, y - size - 1, x - size - 1, y + size, color, 1);
                PantsirOperatorOverlay.drawLine(poseStack, x, y - size - 1, x + size + 1, y + size, color, 1);
                break;
            }
            case 3: {
                PantsirOperatorOverlay.drawDiamond(poseStack, x, y, size, color);
                guiGraphics.m_280509_(x - 1, y + size, x + 1, y + size + 3, color);
                guiGraphics.m_280509_(x - 1, y - 1, x + 1, y + 1, color);
                break;
            }
            default: {
                guiGraphics.m_280509_(x - size, y - size, x + size, y + size, color);
                guiGraphics.m_280509_(x - 1, y - size + 1, x + 1, y + size - 1, color & 0xFFFFFF | Integer.MIN_VALUE);
                guiGraphics.m_280509_(x - size + 1, y - 1, x + size - 1, y + 1, color & 0xFFFFFF | Integer.MIN_VALUE);
            }
        }
    }

    private static void drawTriangleUp(PoseStack poseStack, int topX, int topY, int leftX, int leftY, int rightX, int rightY, int color) {
        PantsirOperatorOverlay.drawTriangle(poseStack, topX, topY, leftX, leftY, rightX, rightY, color);
    }

    private static void drawDiamond(PoseStack poseStack, int centerX, int centerY, int size, int color) {
        PantsirOperatorOverlay.drawTriangle(poseStack, centerX, centerY - size, centerX - size, centerY, centerX, centerY + size, color);
        PantsirOperatorOverlay.drawTriangle(poseStack, centerX, centerY - size, centerX + size, centerY, centerX, centerY + size, color);
    }

    private static void drawMissiles(GuiGraphics guiGraphics, PoseStack poseStack, int centerX, int centerY, Player player, PantsirClientHandler.PantsirRadarData data) {
        Entity vehicle = player.m_20202_();
        if (vehicle == null) {
            return;
        }
        Vec3 radarPos = vehicle.m_20182_();
        for (PantsirClientHandler.MissilePosition missile : data.missiles) {
            Vec3 missilePos = new Vec3(missile.x, missile.y, missile.z);
            Vec3 toMissile = missilePos.m_82546_(radarPos);
            double distance = toMissile.m_165924_();
            if (distance < 1.0) continue;
            double normalizedDist = Math.min(distance / 1100.0, 1.0);
            int radarDist = (int)(normalizedDist * 52.0);
            int blipX = centerX + (int)(toMissile.f_82479_ / distance * (double)radarDist);
            int blipY = centerY + (int)(toMissile.f_82481_ / distance * (double)radarDist);
            PantsirOperatorOverlay.drawDashedLine(poseStack, centerX, centerY, blipX, blipY, -16711681, 4, 3);
            guiGraphics.m_280509_(blipX - 2, blipY - 2, blipX + 2, blipY + 2, -16711681);
        }
    }

    private static void renderStatusPanel(GuiGraphics guiGraphics, int screenWidth, int screenHeight, int vehicleId) {
        Object stateText;
        PantsirClientHandler.PantsirRadarData data = PantsirClientHandler.getRadarData(vehicleId);
        if (data == null) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        int x = 15;
        int y = 40;
        int panelWidth = 110;
        int panelHeight = 65;
        guiGraphics.m_280509_(x - 5, y - 5, x + panelWidth, y + panelHeight, -1073741824);
        guiGraphics.m_280509_(x - 6, y - 6, x + panelWidth + 1, y - 5, -16711936);
        guiGraphics.m_280509_(x - 6, y + panelHeight, x + panelWidth + 1, y + panelHeight + 1, -16711936);
        guiGraphics.m_280509_(x - 6, y - 5, x - 5, y + panelHeight, -16711936);
        guiGraphics.m_280509_(x + panelWidth, y - 5, x + panelWidth + 1, y + panelHeight, -16711936);
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)"\u258c PANTSIR-S1"), x, y, -16711936, true);
        guiGraphics.m_280509_(x, y += 14, x + 95, y + 1, -16711936);
        y += 6;
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)stateText), x, y, switch (data.radarState) {
            case 0 -> {
                stateText = "\u25c9 SCANNING";
                yield -16711936;
            }
            case 1 -> {
                stateText = "\u25ce DETECTED";
                yield -256;
            }
            case 2 -> {
                stateText = "\u25d0 LOCKING " + data.lockProgress + "%";
                yield -256;
            }
            case 3 -> {
                stateText = "\u25c6 LOCKED";
                yield -16711936;
            }
            case 4 -> {
                stateText = "\u25cc LOST";
                yield -52429;
            }
            default -> {
                stateText = "---";
                yield -16764160;
            }
        }, true);
        y += 12;
        if (data.radarState == 2) {
            int barWidth = 90;
            int barHeight = 6;
            int progress = data.lockProgress;
            int filledWidth = (int)((double)(barWidth * progress) / 100.0);
            guiGraphics.m_280509_(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, -16711936);
            guiGraphics.m_280509_(x, y, x + barWidth, y + barHeight, -16764160);
            long time = System.currentTimeMillis();
            float pulse = (float)(Math.sin((double)time / 100.0) * 0.2 + 0.8);
            int pulseAlpha = (int)(pulse * 255.0f);
            int barColor = 0xFFFF00 | pulseAlpha << 24;
            guiGraphics.m_280509_(x, y, x + filledWidth, y + barHeight, barColor);
            y += 10;
        }
        if (data.targetEntityId != -1 && (data.radarState == 1 || data.radarState == 2 || data.radarState == 3)) {
            String distText = String.format("RNG: %.0fm", data.targetDistance);
            guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)distText), x, y, -16711936, true);
        }
    }

    private static void renderTargetMarker(GuiGraphics guiGraphics, int screenWidth, int screenHeight, int vehicleId) {
        int color;
        Vec3 targetPos;
        Entity targetEntity;
        PantsirClientHandler.PantsirRadarData data = PantsirClientHandler.getRadarData(vehicleId);
        if (data == null) {
            return;
        }
        if (data.radarState != 2 && data.radarState != 3) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null || mc.f_91073_ == null) {
            return;
        }
        Entity vehicle = player.m_20202_();
        if (!(vehicle instanceof PantsirS1Entity)) {
            return;
        }
        boolean isSignalLost = data.uiLostSignal;
        Entity entity = targetEntity = data.targetEntityId >= 0 ? mc.f_91073_.m_6815_(data.targetEntityId) : null;
        if (targetEntity != null && targetEntity.m_6084_()) {
            float partialTick = mc.m_91296_();
            targetPos = new Vec3(Mth.m_14139_((double)partialTick, (double)targetEntity.f_19790_, (double)targetEntity.m_20185_()), Mth.m_14139_((double)partialTick, (double)targetEntity.f_19791_, (double)targetEntity.m_20186_()) + (double)targetEntity.m_20206_() * 0.5, Mth.m_14139_((double)partialTick, (double)targetEntity.f_19792_, (double)targetEntity.m_20189_()));
        } else {
            targetPos = isSignalLost && data.uiLastTargetPos != null ? data.uiLastTargetPos : new Vec3(data.targetX, data.targetY, data.targetZ);
        }
        if (targetPos.equals((Object)Vec3.f_82478_)) {
            return;
        }
        Vec3 screenPos = VectorToolKt.worldToScreen((Vec3)targetPos);
        if (screenPos.f_82481_ <= 0.0 || !VectorToolKt.canSee((Vec3)targetPos)) {
            return;
        }
        int x = (int)screenPos.f_82479_;
        int y = (int)screenPos.f_82480_;
        int n = color = data.radarState == 2 ? -256 : -16711936;
        if (isSignalLost) {
            long time = System.currentTimeMillis();
            float alpha = (float)(Math.sin((double)time / 200.0) * 0.4 + 0.6);
            int alphaInt = (int)(alpha * 255.0f);
            color = 0xFF3333 | alphaInt << 24;
            if (time / 300L % 2L != 0L) {
                return;
            }
        }
        PantsirOperatorOverlay.drawTargetBrackets(guiGraphics, x, y, color);
        if (data.radarState == 3) {
            String distText = String.format("%.0fm", data.targetDistance);
            guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)distText), x + 12 + 5, y - 12, color, false);
            double speed = Math.sqrt(data.targetVelX * data.targetVelX + data.targetVelY * data.targetVelY + data.targetVelZ * data.targetVelZ) * 20.0;
            guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)String.format("%.0f m/s", speed)), x + 12 + 5, y, color, false);
            double timeToIntercept = data.targetDistance / 160.0;
            guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)String.format("ETA: %.1fs", timeToIntercept)), x + 12 + 5, y + 12, color, false);
            String status = isSignalLost ? "LOST" : "TRK";
            guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)status), x + 12 + 5, y + 24, color, false);
        } else {
            guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)(data.lockProgress + "%")), x + 12 + 5, y - 12, color, false);
        }
    }

    private static void drawTargetBrackets(GuiGraphics guiGraphics, int cx, int cy, int color) {
        int half = 12;
        int corner = 8;
        int thick = 2;
        guiGraphics.m_280509_(cx - half, cy - half, cx - half + corner, cy - half + thick, color);
        guiGraphics.m_280509_(cx - half, cy - half, cx - half + thick, cy - half + corner, color);
        guiGraphics.m_280509_(cx + half - corner, cy - half, cx + half, cy - half + thick, color);
        guiGraphics.m_280509_(cx + half - thick, cy - half, cx + half, cy - half + corner, color);
        guiGraphics.m_280509_(cx - half, cy + half - thick, cx - half + corner, cy + half, color);
        guiGraphics.m_280509_(cx - half, cy + half - corner, cx - half + thick, cy + half, color);
        guiGraphics.m_280509_(cx + half - corner, cy + half - thick, cx + half, cy + half, color);
        guiGraphics.m_280509_(cx + half - thick, cy + half - corner, cx + half, cy + half, color);
    }

    private static void renderControlHint(GuiGraphics guiGraphics, int screenWidth, int screenHeight, int vehicleId) {
        PantsirClientHandler.PantsirRadarData data = PantsirClientHandler.getRadarData(vehicleId);
        if (data == null) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        PoseStack poseStack = guiGraphics.m_280168_();
        String keyName = ModKeyMappings.VEHICLE_SEEK.getKey().m_84875_().getString();
        String fireKey = ModKeyMappings.FIRE.getKey().m_84875_().getString();
        Object hint = switch (data.radarState) {
            case 0 -> {
                if (data.allTargets.isEmpty()) {
                    yield "Radar scanning...";
                }
                yield "[\u2190/\u2192] Select target";
            }
            case 1 -> "[" + keyName + "] Lock | [\u2190/\u2192] Switch";
            case 2 -> "[" + keyName + "] Cancel";
            case 3 -> "\u25c6 [" + fireKey + "] FIRE | [" + keyName + "] Release";
            case 4 -> "Target lost";
            default -> "";
        };
        int color = switch (data.radarState) {
            case 3 -> -16711936;
            case 1, 2 -> -256;
            default -> -16711936;
        };
        float scale = 0.71f;
        int textWidth = (int)((float)mc.f_91062_.m_92895_((String)hint) * scale);
        int textHeight = (int)(9.0f * scale);
        int hintX = (screenWidth - textWidth) / 2;
        int hintY = screenHeight - 40;
        guiGraphics.m_280509_(hintX - 3, hintY - 2, hintX + textWidth + 3, hintY + textHeight + 2, -1073741824);
        poseStack.m_85836_();
        poseStack.m_252880_((float)hintX, (float)hintY, 0.0f);
        poseStack.m_85841_(scale, scale, 1.0f);
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)hint), 0, 0, color, false);
        poseStack.m_85849_();
    }

    private static void drawFilledCircle(PoseStack poseStack, int cx, int cy, int radius, int color) {
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        RenderSystem.setShader(GameRenderer::m_172811_);
        BufferBuilder buffer = Tesselator.m_85913_().m_85915_();
        Matrix4f matrix = poseStack.m_85850_().m_252922_();
        buffer.m_166779_(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.f_85815_);
        buffer.m_252986_(matrix, (float)cx, (float)cy, 0.0f).m_85950_(r, g, b, a).m_5752_();
        for (int i = 0; i <= 32; ++i) {
            double angle = Math.PI * 2 * (double)i / 32.0;
            float x = (float)cx + (float)(Math.cos(angle) * (double)radius);
            float y = (float)cy + (float)(Math.sin(angle) * (double)radius);
            buffer.m_252986_(matrix, x, y, 0.0f).m_85950_(r, g, b, a).m_5752_();
        }
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)buffer.m_231175_());
    }

    private static void drawCircleOutline(PoseStack poseStack, int cx, int cy, float radius, int color) {
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        RenderSystem.setShader(GameRenderer::m_172811_);
        BufferBuilder buffer = Tesselator.m_85913_().m_85915_();
        Matrix4f matrix = poseStack.m_85850_().m_252922_();
        buffer.m_166779_(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.f_85815_);
        for (int i = 0; i <= 32; ++i) {
            double angle = Math.PI * 2 * (double)i / 32.0;
            float x = (float)cx + (float)(Math.cos(angle) * (double)radius);
            float y = (float)cy + (float)(Math.sin(angle) * (double)radius);
            buffer.m_252986_(matrix, x, y, 0.0f).m_85950_(r, g, b, a).m_5752_();
        }
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)buffer.m_231175_());
    }

    private static void drawLine(PoseStack poseStack, int x1, int y1, int x2, int y2, int color, int width) {
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        RenderSystem.setShader(GameRenderer::m_172811_);
        RenderSystem.lineWidth((float)width);
        BufferBuilder buffer = Tesselator.m_85913_().m_85915_();
        Matrix4f matrix = poseStack.m_85850_().m_252922_();
        buffer.m_166779_(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.f_85815_);
        buffer.m_252986_(matrix, (float)x1, (float)y1, 0.0f).m_85950_(r, g, b, a).m_5752_();
        buffer.m_252986_(matrix, (float)x2, (float)y2, 0.0f).m_85950_(r, g, b, a).m_5752_();
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)buffer.m_231175_());
    }

    private static void drawTriangle(PoseStack poseStack, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        RenderSystem.setShader(GameRenderer::m_172811_);
        BufferBuilder buffer = Tesselator.m_85913_().m_85915_();
        Matrix4f matrix = poseStack.m_85850_().m_252922_();
        buffer.m_166779_(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.f_85815_);
        buffer.m_252986_(matrix, (float)x1, (float)y1, 0.0f).m_85950_(r, g, b, a).m_5752_();
        buffer.m_252986_(matrix, (float)x2, (float)y2, 0.0f).m_85950_(r, g, b, a).m_5752_();
        buffer.m_252986_(matrix, (float)x3, (float)y3, 0.0f).m_85950_(r, g, b, a).m_5752_();
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)buffer.m_231175_());
    }

    private static void drawDashedLine(PoseStack poseStack, int x1, int y1, int x2, int y2, int color, int dashLength, int gapLength) {
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length < 1.0) {
            return;
        }
        double unitX = dx / length;
        double unitY = dy / length;
        RenderSystem.setShader(GameRenderer::m_172811_);
        RenderSystem.lineWidth((float)2.0f);
        BufferBuilder buffer = Tesselator.m_85913_().m_85915_();
        Matrix4f matrix = poseStack.m_85850_().m_252922_();
        buffer.m_166779_(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.f_85815_);
        double pos = 0.0;
        boolean drawing = true;
        while (pos < length) {
            if (drawing) {
                double startX = (double)x1 + unitX * pos;
                double startY = (double)y1 + unitY * pos;
                double endPos = Math.min(pos + (double)dashLength, length);
                double endX = (double)x1 + unitX * endPos;
                double endY = (double)y1 + unitY * endPos;
                buffer.m_252986_(matrix, (float)startX, (float)startY, 0.0f).m_85950_(r, g, b, a).m_5752_();
                buffer.m_252986_(matrix, (float)endX, (float)endY, 0.0f).m_85950_(r, g, b, a).m_5752_();
                pos = endPos;
            } else {
                pos += (double)gapLength;
            }
            drawing = !drawing;
        }
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)buffer.m_231175_());
    }
}

