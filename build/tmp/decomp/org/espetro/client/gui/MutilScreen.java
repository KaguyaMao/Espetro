/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  se.mickelus.mutil.gui.GuiElement
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.espetro.client.gui.EspetroMutilWidgets;
import org.espetro.client.gui.TutorialClientController;
import org.espetro.client.gui.TutorialHudOverlay;
import se.mickelus.mutil.gui.GuiElement;

abstract class MutilScreen
extends Screen {
    protected GuiElement root;
    private boolean rootRebuildPending;
    private boolean rebuildingRoot;
    protected boolean tutorialPreviewMode;
    private Object structureSignature;
    private long lastThrottleEpochSec = -1L;
    private long lastBindingEpochSec = -1L;
    private final List<DynamicBinding> dynamicBindings = new ArrayList<DynamicBinding>();

    protected final boolean updateStructure(Object newSignature) {
        if (Objects.equals(this.structureSignature, newSignature)) {
            return false;
        }
        this.structureSignature = newSignature;
        this.rebuildMutilRoot();
        return true;
    }

    protected final Object getStructureSignature() {
        return this.structureSignature;
    }

    protected final boolean onceEverySecond() {
        long epochSec = System.currentTimeMillis() / 1000L;
        if (epochSec == this.lastThrottleEpochSec) {
            return false;
        }
        this.lastThrottleEpochSec = epochSec;
        return true;
    }

    protected final EspetroMutilWidgets.Text bindDynamic(EspetroMutilWidgets.Text widget, Supplier<String> supplier) {
        this.dynamicBindings.add(new DynamicBinding(widget, supplier));
        return widget;
    }

    private void refreshDynamicBindings() {
        for (DynamicBinding binding : this.dynamicBindings) {
            String next = binding.supplier.get();
            if (next == null) continue;
            binding.widget.setText(next);
        }
    }

    protected MutilScreen(Component title) {
        super(title);
    }

    public final void setTutorialPreviewMode(boolean tutorialPreviewMode) {
        this.tutorialPreviewMode = tutorialPreviewMode;
    }

    public final boolean isTutorialPreviewMode() {
        return this.tutorialPreviewMode;
    }

    @Override
    protected void m_7856_() {
        super.m_7856_();
        this.rebuildMutilRootNow();
    }

    protected final void rebuildMutilRoot() {
        this.rootRebuildPending = true;
    }

    private void rebuildMutilRootNow() {
        if (this.rebuildingRoot) {
            return;
        }
        this.rebuildingRoot = true;
        this.dynamicBindings.clear();
        GuiElement newRoot = new GuiElement(0, 0, this.f_96543_, this.f_96544_);
        this.buildMutilRoot(newRoot);
        this.root = newRoot;
        this.rebuildingRoot = false;
    }

    @Override
    public void m_86600_() {
        long epochSec;
        super.m_86600_();
        if (this.rootRebuildPending) {
            this.rootRebuildPending = false;
            this.rebuildMutilRootNow();
        }
        if (this.root != null) {
            this.root.updateAnimations();
        }
        if (!this.dynamicBindings.isEmpty() && (epochSec = System.currentTimeMillis() / 1000L) != this.lastBindingEpochSec) {
            this.lastBindingEpochSec = epochSec;
            this.refreshDynamicBindings();
        }
    }

    protected abstract void buildMutilRoot(GuiElement var1);

    protected void renderBeforeMutil(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EspetroMutilWidgets.drawScreenShade(graphics, this.f_96543_, this.f_96544_);
    }

    protected void renderAfterMutil(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.m_280262_();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.m_280168_().m_85836_();
        try {
            this.renderBeforeMutil(graphics, mouseX, mouseY, partialTick);
            if (this.root != null) {
                this.root.updateFocusState(0, 0, mouseX, mouseY);
                this.root.draw(graphics, 0, 0, this.f_96543_, this.f_96544_, mouseX, mouseY, partialTick);
                List tooltip = this.root.getTooltipLines();
                if (tooltip != null && !tooltip.isEmpty()) {
                    graphics.m_280666_(this.f_96547_, tooltip, mouseX, mouseY);
                }
            }
            this.renderAfterMutil(graphics, mouseX, mouseY, partialTick);
            graphics.m_280262_();
        }
        finally {
            graphics.m_280262_();
            graphics.m_280168_().m_85849_();
            graphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
    }

    @Override
    public boolean m_6375_(double mouseX, double mouseY, int button) {
        if (this.tutorialPreviewMode) {
            if (TutorialHudOverlay.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            return true;
        }
        if (this.root != null) {
            this.root.updateFocusState(0, 0, (int)mouseX, (int)mouseY);
            if (this.root.onMouseClick((int)mouseX, (int)mouseY, button)) {
                return true;
            }
        }
        return super.m_6375_(mouseX, mouseY, button);
    }

    @Override
    public boolean m_6348_(double mouseX, double mouseY, int button) {
        if (this.tutorialPreviewMode) {
            return true;
        }
        if (this.root != null) {
            this.root.onMouseRelease((int)mouseX, (int)mouseY, button);
        }
        return super.m_6348_(mouseX, mouseY, button);
    }

    @Override
    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (this.tutorialPreviewMode) {
            return true;
        }
        if (this.root != null && this.root.onMouseScroll(mouseX, mouseY, delta)) {
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
        if (this.root != null && this.root.onKeyPress(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean m_7920_(int keyCode, int scanCode, int modifiers) {
        if (this.tutorialPreviewMode) {
            return true;
        }
        if (this.root != null && this.root.onKeyRelease(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.m_7920_(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean m_5534_(char codePoint, int modifiers) {
        if (this.tutorialPreviewMode) {
            return true;
        }
        if (this.root != null && this.root.onCharType(codePoint, modifiers)) {
            return true;
        }
        return super.m_5534_(codePoint, modifiers);
    }

    private record DynamicBinding(EspetroMutilWidgets.Text widget, Supplier<String> supplier) {
    }
}

