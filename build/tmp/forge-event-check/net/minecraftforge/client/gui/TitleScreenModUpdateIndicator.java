/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.fml.VersionChecker$Status
 *  net.minecraftforge.fml.loading.FMLConfig
 *  net.minecraftforge.fml.loading.FMLConfig$ConfigValue
 */
package net.minecraftforge.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.loading.ClientModLoader;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.FMLConfig;

public class TitleScreenModUpdateIndicator
extends Screen {
    private static final ResourceLocation VERSION_CHECK_ICONS = new ResourceLocation("forge", "textures/gui/version_check_icons.png");
    private final Button modButton;
    private VersionChecker.Status showNotification = null;
    private boolean hasCheckedForUpdates = false;

    public TitleScreenModUpdateIndicator(Button modButton) {
        super((Component)Component.m_237115_((String)"forge.menu.updatescreen.title"));
        this.modButton = modButton;
    }

    public void m_7856_() {
        if (!this.hasCheckedForUpdates) {
            if (this.modButton != null) {
                this.showNotification = ClientModLoader.checkForUpdates();
            }
            this.hasCheckedForUpdates = true;
        }
    }

    public void m_88315_(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.showNotification == null || !this.showNotification.shouldDraw() || !FMLConfig.getBoolConfigValue((FMLConfig.ConfigValue)FMLConfig.ConfigValue.VERSION_CHECK)) {
            return;
        }
        int x = this.modButton.m_252754_();
        int y = this.modButton.m_252907_();
        int w = this.modButton.m_5711_();
        int h = this.modButton.m_93694_();
        guiGraphics.m_280163_(VERSION_CHECK_ICONS, x + w - (h / 2 + 4), y + (h / 2 - 4), (float)(this.showNotification.getSheetOffset() * 8), this.showNotification.isAnimated() && (System.currentTimeMillis() / 800L & 1L) == 1L ? 8.0f : 0.0f, 8, 8, 64, 16);
    }

    public static TitleScreenModUpdateIndicator init(TitleScreen guiMainMenu, Button modButton) {
        TitleScreenModUpdateIndicator titleScreenModUpdateIndicator = new TitleScreenModUpdateIndicator(modButton);
        titleScreenModUpdateIndicator.m_6574_(guiMainMenu.getMinecraft(), guiMainMenu.f_96543_, guiMainMenu.f_96544_);
        titleScreenModUpdateIndicator.m_7856_();
        return titleScreenModUpdateIndicator;
    }
}

