/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.tools.FormatTool
 *  com.atsuishio.superbwarfare.tools.RangeTool
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 */
package com.redabysslucia.dragonrise_reforge.client.overlay;

import com.atsuishio.superbwarfare.tools.FormatTool;
import com.atsuishio.superbwarfare.tools.RangeTool;
import com.redabysslucia.dragonrise_reforge.entities.M270Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(value=Dist.CLIENT)
public class M270BallisticOverlay
implements IGuiOverlay {
    public static final String ID = "superbwarfare_m270_ballistic";

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        LocalPlayer player = mc.f_91074_;
        if (player == null) {
            return;
        }
        Entity entity = player.m_20202_();
        if (!(entity instanceof M270Entity)) {
            return;
        }
        M270Entity m270 = (M270Entity)entity;
        double elevation = -m270.getTurretXRot();
        Vec3 lookVec = m270.m_20154_();
        float vehicleYaw = (float)Math.toDegrees(Math.atan2(-lookVec.f_82479_, lookVec.f_82481_));
        float turretWorldYaw = vehicleYaw + m270.getTurretYRot();
        float worldAzimuth = (turretWorldYaw + 180.0f) % 360.0f;
        if (worldAzimuth < 0.0f) {
            worldAzimuth += 360.0f;
        }
        double velocity = m270.getProjectileVelocity(0);
        double gravity = m270.getProjectileGravity(0);
        double range = gravity > 0.0 && velocity > 0.0 && elevation > 0.0 ? RangeTool.getRange((double)elevation, (double)velocity, (double)gravity) : 0.0;
        int x = 5;
        int y = screenHeight / 2 - 20;
        int color = -22016;
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)("\u4ef0\u89d2: " + FormatTool.format1D((double)elevation, (String)"\u00b0"))), x, y, color, false);
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)String.format("\u65b9\u4f4d: %.1f\u00b0", Float.valueOf(worldAzimuth))), x, y + 12, color, false);
        String rangeStr = range > 0.0 ? FormatTool.format0D((double)Math.max(0, (int)range), (String)"m") : "---m";
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)("\u8ddd\u79bb: " + rangeStr)), x, y + 24, color, false);
    }
}

