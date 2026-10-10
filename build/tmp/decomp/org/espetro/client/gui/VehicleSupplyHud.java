/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.RenderGuiEvent$Post
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  org.joml.Matrix4f
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.HudRenderState;
import org.espetro.client.gui.VehicleWheelController;
import org.espetro.client.vehicle.VehicleInteractionState;
import org.espetro.network.VehicleSupplySyncPacket;
import org.joml.Matrix4f;

public final class VehicleSupplyHud {
    private static final int BAR_WIDTH = 200;
    private static final int BAR_HEIGHT = 6;
    private static final int PANEL_WIDTH = 220;
    private static final int PANEL_HEIGHT = 28;
    private static final int TOP_MARGIN = 4;
    private static final int ICON_SIZE = 10;
    private static final int ICON_TEXTURE_SIZE = 128;
    private static final ResourceLocation ICON_AMMO = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/ammo_supply.png");
    private static final ResourceLocation ICON_CONSTR = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/construction_supply.png");
    private static final int COLOR_AMMO_FILL = -3390396;
    private static final int COLOR_CONSTR_FILL = -3364352;
    private static final int COLOR_BAR_BG = -13421773;
    private static final int COLOR_PANEL_BG = -586084079;
    private static final int COLOR_PANEL_BORDER = -11184811;
    private static final int COLOR_HINT = -2245786;
    private static final double HINT_RANGE = 5.0;
    private static final int HINT_Y_OFFSET = 20;
    private static final int PROGRESS_RING_RADIUS = 38;
    private static final int PROGRESS_RING_THICKNESS = 3;
    private static final int PROGRESS_SEGMENTS = 64;
    private static boolean registered;

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        MinecraftForge.EVENT_BUS.register(VehicleSupplyHud.class);
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null || mc.f_91074_ == null) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        float interactionProgress = VehicleInteractionState.progress();
        if (VehicleWheelController.isWheelActive()) {
            VehicleSupplySyncPacket supply = VehicleWheelController.getCachedSupply();
            if (supply != null) {
                VehicleSupplyHud.drawCapacityBar(graphics, mc, supply);
            }
            if (interactionProgress >= 0.0f) {
                VehicleSupplyHud.drawInteractionCenterProgress(graphics, mc, interactionProgress);
            } else if (VehicleWheelController.isHolding()) {
                VehicleSupplyHud.drawProgressRing(graphics, mc);
            } else if (VehicleWheelController.isCenterHovered()) {
                VehicleSupplyHud.drawCenterMountHint(graphics, mc);
            }
            return;
        }
        if (interactionProgress >= 0.0f) {
            VehicleSupplyHud.drawInteractionCenterProgress(graphics, mc, interactionProgress);
            return;
        }
        VehicleSupplyHud.drawCrosshairHint(graphics, mc);
    }

    private static void drawCapacityBar(GuiGraphics graphics, Minecraft mc, VehicleSupplySyncPacket supply) {
        int ammoW;
        int max = supply.getMaxCapacity();
        if (max <= 0) {
            return;
        }
        int ammo = supply.getAmmo();
        int constr = supply.canCarryConstruction() ? supply.getConstruction() : 0;
        int screenW = mc.m_91268_().m_85445_();
        int px = (screenW - 220) / 2;
        int py = 4;
        graphics.m_280509_(px, py, px + 220, py + 28, -586084079);
        graphics.m_280509_(px, py, px + 220, py + 1, -11184811);
        graphics.m_280509_(px, py + 28 - 1, px + 220, py + 28, -11184811);
        graphics.m_280509_(px, py, px + 1, py + 28, -11184811);
        graphics.m_280509_(px + 220 - 1, py, px + 220, py + 28, -11184811);
        int barX = px + 10;
        int barY = py + 4;
        int barW = 200;
        int barH = 6;
        graphics.m_280509_(barX, barY, barX + barW, barY + barH, -13421773);
        if (ammo > 0) {
            ammoW = (int)((long)ammo * (long)barW / (long)max);
            graphics.m_280509_(barX, barY, barX + ammoW, barY + barH, -3390396);
        }
        if (constr > 0) {
            ammoW = ammo > 0 ? (int)((long)ammo * (long)barW / (long)max) : 0;
            int constrW = (int)((long)constr * (long)barW / (long)max);
            int constrEnd = Math.min(barX + ammoW + constrW, barX + barW);
            graphics.m_280509_(barX + ammoW, barY, constrEnd, barY + barH, -3364352);
        }
        int iconRowY = barY + barH + 2;
        int iconTextY = iconRowY + (10 - mc.f_91062_.f_92710_) / 2;
        if (supply.canCarryConstruction()) {
            int ammoIconX = px + 35;
            VehicleSupplyHud.drawSupplyIcon(graphics, ICON_AMMO, ammoIconX, iconRowY);
            graphics.m_280488_(mc.f_91062_, String.valueOf(ammo), ammoIconX + 10 + 3, iconTextY, 0xFFFFFF);
            String constrText = String.valueOf(constr);
            int constrTextW = mc.f_91062_.m_92895_(constrText);
            int constrIconX = px + 220 - 35 - 10 - 3 - constrTextW;
            VehicleSupplyHud.drawSupplyIcon(graphics, ICON_CONSTR, constrIconX, iconRowY);
            graphics.m_280488_(mc.f_91062_, constrText, constrIconX + 10 + 3, iconTextY, 0xFFFFFF);
        } else {
            String ammoText = String.valueOf(ammo);
            int totalW = 13 + mc.f_91062_.m_92895_(ammoText);
            int startX = px + (220 - totalW) / 2;
            VehicleSupplyHud.drawSupplyIcon(graphics, ICON_AMMO, startX, iconRowY);
            graphics.m_280488_(mc.f_91062_, ammoText, startX + 10 + 3, iconTextY, 0xFFFFFF);
        }
    }

    private static void drawSupplyIcon(GuiGraphics graphics, ResourceLocation icon, int x, int y) {
        graphics.m_280411_(icon, x, y, 10, 10, 0.0f, 0.0f, 128, 128, 128, 128);
    }

    private static void drawCrosshairHint(GuiGraphics graphics, Minecraft mc) {
        EntityHitResult entityHit;
        Entity target;
        HitResult hitResult = mc.f_91077_;
        if (hitResult instanceof EntityHitResult && VehicleSupplyHud.isFriendlyVehicle(target = (entityHit = (EntityHitResult)hitResult).m_82443_(), mc)) {
            int cx = mc.m_91268_().m_85445_() / 2;
            int cy = mc.m_91268_().m_85446_() / 2;
            String hint = "\u6309\u4f4f F \u6765\u4ea4\u4e92";
            graphics.m_280488_(mc.f_91062_, hint, cx - mc.f_91062_.m_92895_(hint) / 2, cy + 20, -2245786);
        }
    }

    private static boolean isFriendlyVehicle(Entity entity, Minecraft mc) {
        if (entity == null || mc.f_91074_ == null) {
            return false;
        }
        if ((double)entity.m_20270_(mc.f_91074_) > 5.0) {
            return false;
        }
        String playerTeam = ClientGameState.getPlayerTeam();
        if (playerTeam == null) {
            return false;
        }
        for (String tag : entity.m_19880_()) {
            if (!tag.startsWith("espetro_team_")) continue;
            return tag.substring("espetro_team_".length()).equals(playerTeam);
        }
        return false;
    }

    private static void drawInteractionCenterProgress(GuiGraphics graphics, Minecraft mc, float fill) {
        int cx = mc.m_91268_().m_85445_() / 2;
        int cy = mc.m_91268_().m_85446_() / 2;
        ResourceLocation icon = VehicleInteractionState.icon();
        int size = 18;
        graphics.m_280411_(icon, cx - size / 2, cy - size / 2 - 6, size, size, 0.0f, 0.0f, 128, 128, 128, 128);
        String label = VehicleInteractionState.label();
        String text = String.format("\u00a7e%s \u00a7f%.0f%%", label, Float.valueOf(fill * 100.0f));
        graphics.m_280488_(mc.f_91062_, text, cx - mc.f_91062_.m_92895_(text) / 2, cy + 16, 0xFFFFFF);
    }

    private static void drawCenterMountHint(GuiGraphics graphics, Minecraft mc) {
        int cx = mc.m_91268_().m_85445_() / 2;
        int cy = mc.m_91268_().m_85446_() / 2;
        ResourceLocation mountIcon = VehicleInteractionState.icon();
        graphics.m_280411_(mountIcon, cx - 9, cy - 15, 18, 18, 0.0f, 0.0f, 128, 128, 128, 128);
        String text = "\u00a77\u4e0a\u8f66";
        graphics.m_280488_(mc.f_91062_, text, cx - mc.f_91062_.m_92895_(text) / 2, cy + 16, 0xFFFFFF);
    }

    private static void drawProgressRing(GuiGraphics graphics, Minecraft mc) {
        int progress = VehicleWheelController.getHoldProgress();
        float fill = Math.min(1.0f, (float)progress / 20.0f);
        int color = VehicleWheelController.getHoldColor();
        int cx = mc.m_91268_().m_85445_() / 2;
        int cy = mc.m_91268_().m_85446_() / 2;
        VehicleSupplyHud.drawRingAt(graphics, cx, cy, fill, color);
    }

    private static void drawRingAt(GuiGraphics graphics, int cx, int cy, float fill, int color) {
        int r = 38;
        int thick = 3;
        HudRenderState.begin(graphics);
        RenderSystem.setShader(GameRenderer::m_172811_);
        Matrix4f matrix = graphics.m_280168_().m_85850_().m_252922_();
        BufferBuilder builder = Tesselator.m_85913_().m_85915_();
        builder.m_166779_(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.f_85815_);
        float a = color >> 24 & 0xFF;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        float alpha = a / 255.0f;
        int segments = 64;
        int filledSegments = (int)(fill * (float)segments);
        for (int i = 0; i <= filledSegments; ++i) {
            float angle = (float)(-1.5707963267948966 + Math.PI * 2 * (double)i / (double)segments);
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            float innerX = (float)cx + (float)(r - thick) * cos;
            float innerY = (float)cy + (float)(r - thick) * sin;
            float outerX = (float)cx + (float)(r + thick) * cos;
            float outerY = (float)cy + (float)(r + thick) * sin;
            builder.m_252986_(matrix, innerX, innerY, 0.0f).m_85950_(red, green, blue, alpha).m_5752_();
            builder.m_252986_(matrix, outerX, outerY, 0.0f).m_85950_(red, green, blue, alpha).m_5752_();
        }
        BufferUploader.m_231202_(builder.m_231175_());
        HudRenderState.restore(graphics);
    }
}

