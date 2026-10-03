/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 */
package com.example.espoints.hud;

import java.lang.reflect.Method;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class AreaInfoHUD
implements IGuiOverlay {
    private static final int HUD_MARGIN = 10;
    private static final int TEXT_SIZE = 12;
    private static final int TEXT_COLOR = -1;
    private static final int BACKGROUND_COLOR = Integer.MIN_VALUE;

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        String areaInfo = this.getCurrentAreaInfo((Player)mc.f_91074_);
        if (areaInfo == null) {
            return;
        }
        int textWidth = mc.f_91062_.m_92895_(areaInfo);
        int hudStartX = (screenWidth - textWidth) / 2;
        int hudStartY = 10;
        guiGraphics.m_280056_(mc.f_91062_, areaInfo, hudStartX, hudStartY, -1, false);
    }

    private String getCurrentAreaInfo(Player player) {
        try {
            Class<?> apiClass = Class.forName("hcr.batui.api.HCRBATUIApi");
            Method getPlayerAreaMethod = apiClass.getMethod("getPlayerArea", Player.class);
            Object playerArea = getPlayerAreaMethod.invoke(null, player);
            Class<?> optionalClass = Class.forName("java.util.Optional");
            Method isPresentMethod = optionalClass.getMethod("isPresent", new Class[0]);
            boolean isPresent = (Boolean)isPresentMethod.invoke(playerArea, new Object[0]);
            if (isPresent) {
                Method getMethod = optionalClass.getMethod("get", new Class[0]);
                Object area = getMethod.invoke(playerArea, new Object[0]);
                Class<?> areaClass = Class.forName("hcr.batui.data.Area");
                Method getNameMethod = areaClass.getMethod("getName", new Class[0]);
                String areaName = (String)getNameMethod.invoke(area, new Object[0]);
                Method getTypeMethod = areaClass.getMethod("getType", new Class[0]);
                Object areaType = getTypeMethod.invoke(area, new Object[0]);
                return String.format("\u5f53\u524d\u533a\u57df: %s (%s)", areaName, areaType);
            }
            return "\u5f53\u524d\u533a\u57df: \u65e0";
        }
        catch (ClassNotFoundException e) {
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

