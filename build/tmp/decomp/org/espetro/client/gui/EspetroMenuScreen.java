/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.AuiScreen;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.TutorialClientController;
import org.espetro.client.gui.TutorialHudOverlay;

abstract class EspetroMenuScreen
extends AuiScreen {
    protected boolean tutorialPreviewMode;
    private long lastBindingEpochSec = -1L;
    private final List<DynamicBinding> dynamicBindings = new ArrayList<DynamicBinding>();

    protected EspetroMenuScreen(Component title) {
        super(title);
    }

    public final void setTutorialPreviewMode(boolean tutorialPreviewMode) {
        this.tutorialPreviewMode = tutorialPreviewMode;
    }

    public final boolean isTutorialPreviewMode() {
        return this.tutorialPreviewMode;
    }

    protected final EspetroAuiWidgets.Text bindDynamic(EspetroAuiWidgets.Text widget, Supplier<String> supplier) {
        this.dynamicBindings.add(new DynamicBinding(widget, supplier));
        return widget;
    }

    @Override
    public void m_86600_() {
        long epochSec;
        super.m_86600_();
        if (!this.dynamicBindings.isEmpty() && (epochSec = System.currentTimeMillis() / 1000L) != this.lastBindingEpochSec) {
            this.lastBindingEpochSec = epochSec;
            for (DynamicBinding binding : this.dynamicBindings) {
                String next = binding.supplier.get();
                if (next == null) continue;
                binding.widget.setText(next);
            }
        }
    }

    @Override
    public boolean m_6375_(double mouseX, double mouseY, int button) {
        if (this.tutorialPreviewMode) {
            if (!TutorialHudOverlay.mouseClicked(mouseX, mouseY, button)) {
                // empty if block
            }
            return true;
        }
        return super.m_6375_(mouseX, mouseY, button);
    }

    @Override
    public boolean m_6348_(double mouseX, double mouseY, int button) {
        if (this.tutorialPreviewMode) {
            return true;
        }
        return super.m_6348_(mouseX, mouseY, button);
    }

    @Override
    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (this.tutorialPreviewMode) {
            return true;
        }
        return super.m_6050_(mouseX, mouseY, delta);
    }

    @Override
    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (TutorialClientController.handleKeyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.tutorialPreviewMode) {
            return true;
        }
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean m_7920_(int keyCode, int scanCode, int modifiers) {
        if (this.tutorialPreviewMode) {
            return true;
        }
        return super.m_7920_(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean m_5534_(char codePoint, int modifiers) {
        if (this.tutorialPreviewMode) {
            return true;
        }
        return super.m_5534_(codePoint, modifiers);
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.shadeWorld()) {
            EspetroAuiWidgets.drawScreenShade(graphics, this.f_96543_, this.f_96544_);
        }
    }

    private record DynamicBinding(EspetroAuiWidgets.Text widget, Supplier<String> supplier) {
    }
}

