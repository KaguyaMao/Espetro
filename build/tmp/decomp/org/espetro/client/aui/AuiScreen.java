/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.aui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;

public abstract class AuiScreen
extends Screen {
    public static final String HOST_PATH = "screens/host.html";
    protected GuiElement root;
    private boolean rootRebuildPending;
    private boolean rebuildingRoot;
    private Object structureSignature;
    private long lastThrottleEpochSec = -1L;

    protected AuiScreen(Component title) {
        super(title);
    }

    public static void runWithDocument(Object ignored, Runnable action) {
        if (action != null) {
            action.run();
        }
    }

    protected boolean shadeWorld() {
        return true;
    }

    protected final boolean updateStructure(Object newSignature) {
        if (Objects.equals(this.structureSignature, newSignature)) {
            return false;
        }
        this.structureSignature = newSignature;
        this.rebuildMenuRoot();
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

    protected final void rebuildMenuRoot() {
        this.rootRebuildPending = true;
    }

    @Override
    protected void m_7856_() {
        super.m_7856_();
        this.rebuildMenuRootNow();
    }

    private void rebuildMenuRootNow() {
        if (this.rebuildingRoot) {
            return;
        }
        this.rebuildingRoot = true;
        try {
            GuiElement next = new GuiElement(0, 0, this.f_96543_, this.f_96544_);
            this.buildMenuRoot(next);
            this.root = next;
        }
        finally {
            this.rebuildingRoot = false;
        }
    }

    @Override
    public void m_86600_() {
        super.m_86600_();
        if (this.rootRebuildPending) {
            this.rootRebuildPending = false;
            this.rebuildMenuRootNow();
        }
        if (this.root != null) {
            this.root.updateAnimations();
        }
    }

    protected abstract void buildMenuRoot(GuiElement var1);

    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    protected void renderAfterMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
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
            this.renderBeforeMenu(graphics, mouseX, mouseY, partialTick);
            if (this.root != null) {
                this.root.updateFocusState(0, 0, mouseX, mouseY);
                this.root.draw(graphics, 0, 0, this.f_96543_, this.f_96544_, mouseX, mouseY, partialTick);
                List<Component> tooltip = this.root.getTooltipLines();
                if (tooltip != null && !tooltip.isEmpty()) {
                    graphics.m_280666_(this.f_96547_, tooltip, mouseX, mouseY);
                }
            }
            this.renderAfterMenu(graphics, mouseX, mouseY, partialTick);
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
        if (this.root != null) {
            this.root.onMouseRelease((int)mouseX, (int)mouseY, button);
        }
        return super.m_6348_(mouseX, mouseY, button);
    }

    @Override
    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (this.root != null && this.root.onMouseScroll(mouseX, mouseY, delta)) {
            return true;
        }
        return super.m_6050_(mouseX, mouseY, delta);
    }

    @Override
    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (this.root != null && this.root.onKeyPress(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean m_7920_(int keyCode, int scanCode, int modifiers) {
        if (this.root != null && this.root.onKeyRelease(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.m_7920_(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean m_5534_(char codePoint, int modifiers) {
        if (this.root != null && this.root.onCharType(codePoint, modifiers)) {
            return true;
        }
        return super.m_5534_(codePoint, modifiers);
    }

    @Override
    public boolean m_7043_() {
        return false;
    }
}

