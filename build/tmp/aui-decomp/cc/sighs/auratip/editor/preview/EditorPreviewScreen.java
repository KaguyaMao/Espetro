/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package cc.sighs.auratip.editor.preview;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class EditorPreviewScreen
extends Screen {
    private final Runnable onClose;

    public EditorPreviewScreen(Runnable onClose) {
        super((Component)Component.m_237113_((String)"AuraTip Editor Preview"));
        this.onClose = onClose == null ? () -> {} : onClose;
    }

    public boolean m_7043_() {
        return true;
    }

    public boolean m_6913_() {
        return true;
    }

    public void m_7379_() {
        super.m_7379_();
        this.onClose.run();
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    public void m_280273_(GuiGraphics graphics) {
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        return super.m_6375_(mouseX, mouseY, button);
    }

    public boolean m_6348_(double mouseX, double mouseY, int button) {
        return super.m_6348_(mouseX, mouseY, button);
    }

    public boolean m_7979_(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.m_7979_(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        return super.m_6050_(mouseX, mouseY, delta);
    }
}

