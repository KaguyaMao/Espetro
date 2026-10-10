/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package com.sighs.apricityui.screen;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.client.Client;
import com.sighs.apricityui.dev.resource.ResourcePreviewDialog;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.loader.ClientLoader;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.FrameTimingHud;
import com.sighs.apricityui.render.Mask;
import com.sighs.apricityui.screen.AuiLinkedScreen;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.style.Cursor;
import com.sighs.apricityui.viewport.ApricityViewport;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ApricityScreen
extends Screen
implements AuiLinkedScreen {
    private final String templatePath;
    private boolean pauseGame;
    private boolean showDefaultBackground;
    private Document linkedDocument;
    private boolean loggedInitState = false;
    private boolean loggedRenderState = false;

    public ApricityScreen(String templatePath) {
        super((Component)Component.m_237119_());
        this.templatePath = templatePath;
    }

    public ApricityScreen setPauseGame(boolean pauseGame) {
        this.pauseGame = pauseGame;
        return this;
    }

    public ApricityScreen setShowDefaultBackground(boolean showDefaultBackground) {
        this.showDefaultBackground = showDefaultBackground;
        return this;
    }

    public boolean isPauseGame() {
        return this.pauseGame;
    }

    public boolean isShowDefaultBackground() {
        return this.showDefaultBackground;
    }

    @Override
    public Document getLinkedDocument() {
        return this.linkedDocument;
    }

    protected void m_7856_() {
        super.m_7856_();
        if (this.linkedDocument != null) {
            this.linkedDocument.remove();
            this.linkedDocument = null;
        }
        this.linkedDocument = Document.create(this.templatePath);
        if (this.linkedDocument != null) {
            this.linkedDocument.applyViewport(false);
        }
        if (!this.loggedInitState) {
            this.loggedInitState = true;
            ApricityViewport viewport = this.currentViewport();
            ApricityUI.LOGGER.info("[AUI Screen] init path={} viewport={}x{} doc={} body={} paintList={}", new Object[]{this.templatePath, viewport.layoutWidth(), viewport.layoutHeight(), this.linkedDocument == null ? "<null>" : this.linkedDocument.getUuid(), this.linkedDocument == null || this.linkedDocument.body == null ? "<null>" : this.linkedDocument.body.tagName, this.linkedDocument == null ? -1 : this.linkedDocument.getPaintList().size()});
        }
    }

    public void m_6574_(@Nonnull Minecraft minecraft, int width, int height) {
        super.m_6574_(minecraft, width, height);
        if (this.linkedDocument != null) {
            this.linkedDocument.applyViewport(true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void m_88315_(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        FrameTimingHud.beginFrame();
        try {
            if (this.showDefaultBackground) {
                this.m_280273_(guiGraphics);
            }
            if (this.linkedDocument != null) {
                if (!this.loggedRenderState) {
                    this.loggedRenderState = true;
                    ApricityUI.LOGGER.info("[AUI Screen] render path={} doc={} body={} paintList={} dirty={}", new Object[]{this.templatePath, this.linkedDocument.getUuid(), this.linkedDocument.body == null ? "<null>" : this.linkedDocument.body.tagName, this.linkedDocument.getPaintList().size(), this.linkedDocument.getDirtyElements().size()});
                }
                ApricityViewport viewport = this.currentViewport();
                guiGraphics.m_280168_().m_85836_();
                Mask.pushScissorScale(viewport.scissorScale());
                try {
                    guiGraphics.m_280168_().m_85841_(viewport.renderScale(), viewport.renderScale(), 1.0f);
                    Base.drawScreenDocument(guiGraphics.m_280168_(), this.linkedDocument);
                }
                finally {
                    Mask.popScissorScale();
                    guiGraphics.m_280168_().m_85849_();
                }
                Minecraft.m_91087_().m_91269_().m_110104_().m_109911_();
            }
            ResourcePreviewDialog.draw(guiGraphics.m_280168_(), this.linkedDocument);
            Client.drawPersistentScreenDocuments(guiGraphics, this.linkedDocument);
            guiGraphics.m_280262_();
            Cursor.drawPseudoCursor(guiGraphics.m_280168_());
            guiGraphics.m_280262_();
        }
        finally {
            FrameTimingHud.endFrame();
            Client.drawFrameTimingHud(guiGraphics);
        }
    }

    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (ApricityScreen.m_96637_() && this.handleViewportZoom(delta > 0.0)) {
            return true;
        }
        return super.m_6050_(mouseX, mouseY, delta);
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (keyCode == AuiServices.keys().reloadKey()) {
            ClientLoader.reload();
            return true;
        }
        if (ApricityScreen.isControlModifier(modifiers)) {
            if (keyCode == 61 || keyCode == 334) {
                return this.handleViewportZoom(true);
            }
            if (keyCode == 45 || keyCode == 333) {
                return this.handleViewportZoom(false);
            }
            if (keyCode == 48 || keyCode == 320) {
                return this.resetViewportZoom();
            }
        }
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    public void m_7379_() {
        if (this.linkedDocument != null) {
            if (this.linkedDocument.body != null) {
                Event.triggerSingle(new Event(this.linkedDocument.body, "unload", false));
            }
            this.linkedDocument.remove();
        }
        Size.clearViewportOverride();
        Cursor.resetToDefault();
        super.m_7379_();
    }

    public void m_7861_() {
        if (this.linkedDocument != null) {
            this.linkedDocument.remove();
        }
        Size.clearViewportOverride();
        super.m_7861_();
    }

    public boolean m_7043_() {
        return this.pauseGame;
    }

    public boolean handleViewportZoom(boolean zoomIn) {
        return this.linkedDocument != null && this.linkedDocument.handleViewportZoom(zoomIn);
    }

    public boolean resetViewportZoom() {
        return this.linkedDocument != null && this.linkedDocument.resetViewportZoom();
    }

    private ApricityViewport currentViewport() {
        return this.linkedDocument == null ? new ApricityViewport(1, 1, 1.0f, 1.0) : this.linkedDocument.getViewport();
    }

    private static boolean isControlModifier(int modifiers) {
        return (modifiers & 2) != 0;
    }
}

