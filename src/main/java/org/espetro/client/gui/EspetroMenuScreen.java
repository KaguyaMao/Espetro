package org.espetro.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.AuiScreen;
import org.espetro.client.aui.GuiElement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

abstract class EspetroMenuScreen extends AuiScreen {

    protected boolean tutorialPreviewMode;
    private long lastBindingEpochSec = -1;
    private final List<DynamicBinding> dynamicBindings = new ArrayList<>();

    private record DynamicBinding(EspetroAuiWidgets.Text widget, Supplier<String> supplier) {
    }

    protected EspetroMenuScreen(Component title) {
        super(title);
    }

    public final void setTutorialPreviewMode(boolean tutorialPreviewMode) {
        this.tutorialPreviewMode = tutorialPreviewMode;
    }

    public final boolean isTutorialPreviewMode() {
        return tutorialPreviewMode;
    }

    protected final EspetroAuiWidgets.Text bindDynamic(
            EspetroAuiWidgets.Text widget, Supplier<String> supplier) {
        dynamicBindings.add(new DynamicBinding(widget, supplier));
        return widget;
    }

    @Override
    public void tick() {
        super.tick();
        if (!dynamicBindings.isEmpty()) {
            long epochSec = System.currentTimeMillis() / 1000L;
            if (epochSec != lastBindingEpochSec) {
                lastBindingEpochSec = epochSec;
                for (DynamicBinding binding : dynamicBindings) {
                    String next = binding.supplier.get();
                    if (next != null) {
                        binding.widget.setText(next);
                    }
                }
            }
        }
    }

    /** 点到输入框时直接交给它：AUI 层会先消费点击，导致原版输入框无法聚焦、无法输入。 */
    private net.minecraft.client.gui.components.EditBox editBoxAt(double mouseX, double mouseY) {
        for (net.minecraft.client.gui.components.events.GuiEventListener child : this.children()) {
            if (child instanceof net.minecraft.client.gui.components.EditBox box
                && box.visible && box.isMouseOver(mouseX, mouseY)) {
                return box;
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        net.minecraft.client.gui.components.EditBox box = editBoxAt(mouseX, mouseY);
        if (box != null) {
            this.setFocused(box);
            box.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        if (tutorialPreviewMode) {
            return TutorialHudOverlay.mouseClicked(mouseX, mouseY, button) || true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (tutorialPreviewMode) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (tutorialPreviewMode) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 输入框获得焦点时优先处理（AUI 层会吞按键，导致打不了字）。
        if (this.getFocused() instanceof net.minecraft.client.gui.components.EditBox box) {
            if (box.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (keyCode == 256) { // ESC：先取消输入框焦点，不关闭界面
                this.setFocused(null);
                return true;
            }
        }
        if (TutorialClientController.handleKeyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (tutorialPreviewMode) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (tutorialPreviewMode) {
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.getFocused() instanceof net.minecraft.client.gui.components.EditBox box) {
            if (box.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        if (tutorialPreviewMode) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (shadeWorld()) {
            EspetroAuiWidgets.drawScreenShade(graphics, this.width, this.height);
        }
    }
}
