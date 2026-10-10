/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sighs.apricityui.init.Document
 *  com.sighs.apricityui.init.Element
 */
package org.espetro.client.aui;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class GuiElement {
    protected final Document document;
    protected final Element node;
    protected boolean hasFocus;
    private final List<GuiElement> children = new ArrayList<GuiElement>();
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean visible = true;
    private List<Component> tooltipLines = List.of();

    public GuiElement(int x, int y, int width, int height) {
        this(x, y, width, height, "aui-node");
    }

    public GuiElement(int x, int y, int width, int height, String cssClass) {
        this.document = null;
        this.x = x;
        this.y = y;
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
        if (this.document == null) {
            this.node = null;
            return;
        }
        this.node = this.document.createHTML("<div class=\"" + cssClass + "\" style=\"" + this.inlineStyle() + "\"></div>");
    }

    GuiElement(Document document, Element existing, int x, int y, int width, int height) {
        this.document = document;
        this.node = existing;
        this.x = x;
        this.y = y;
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
        this.syncStyle();
    }

    public final Element node() {
        return this.node;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setX(int x) {
        this.x = x;
        this.syncStyle();
    }

    public void setY(int y) {
        this.y = y;
        this.syncStyle();
    }

    public void setWidth(int width) {
        this.width = Math.max(0, width);
        this.syncStyle();
    }

    public void setHeight(int height) {
        this.height = Math.max(0, height);
        this.syncStyle();
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (this.node == null) {
            return;
        }
        if (visible) {
            this.node.removeAttribute("hidden");
            String cls = this.node.getClassName();
            if (cls != null && cls.contains("hidden")) {
                this.node.setClassName(cls.replace("hidden", "").trim());
            }
        } else {
            this.node.setAttribute("hidden", "true");
            String cls = this.node.getClassName();
            if (cls == null || !cls.contains("hidden")) {
                this.node.setClassName((cls == null ? "aui-node" : cls) + " hidden");
            }
        }
    }

    public boolean hasFocus() {
        return this.hasFocus;
    }

    public void addChild(GuiElement child) {
        if (child == null) {
            return;
        }
        this.children.add(child);
        if (this.node != null && child.node != null) {
            this.node.appendChild(child.node);
        }
    }

    public void clearChildren() {
        for (GuiElement child : new ArrayList<GuiElement>(this.children)) {
            if (child.node == null) continue;
            child.node.remove();
        }
        this.children.clear();
    }

    public List<GuiElement> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    public void setTooltip(List<Component> lines) {
        this.tooltipLines = lines == null ? List.of() : List.copyOf(lines);
    }

    public List<Component> getTooltipLines() {
        if (!this.tooltipLines.isEmpty() && this.hasFocus && this.visible) {
            return this.tooltipLines;
        }
        for (GuiElement child : this.children) {
            List<Component> nested = child.getTooltipLines();
            if (nested == null || nested.isEmpty()) continue;
            return nested;
        }
        return List.of();
    }

    public void updateAnimations() {
        for (GuiElement child : this.children) {
            child.updateAnimations();
        }
    }

    public void updateFocusState(int refX, int refY, int mouseX, int mouseY) {
        boolean next;
        boolean bl = next = this.visible && mouseX >= refX + this.x && mouseX < refX + this.x + this.width && mouseY >= refY + this.y && mouseY < refY + this.y + this.height;
        if (next != this.hasFocus) {
            this.hasFocus = next;
            if (this.hasFocus) {
                this.onFocus();
            } else {
                this.onBlur();
            }
        }
        for (GuiElement child : this.children) {
            if (!child.isVisible()) continue;
            child.updateFocusState(refX + this.x, refY + this.y, mouseX, mouseY);
        }
    }

    public boolean onMouseClick(int mouseX, int mouseY, int button) {
        for (int i = this.children.size() - 1; i >= 0; --i) {
            GuiElement child = this.children.get(i);
            if (!child.isVisible() || !child.onMouseClick(mouseX, mouseY, button)) continue;
            return true;
        }
        return false;
    }

    public void onMouseRelease(int mouseX, int mouseY, int button) {
        for (GuiElement child : this.children) {
            child.onMouseRelease(mouseX, mouseY, button);
        }
    }

    public boolean onMouseScroll(double mouseX, double mouseY, double delta) {
        for (int i = this.children.size() - 1; i >= 0; --i) {
            GuiElement child = this.children.get(i);
            if (!child.isVisible() || !child.onMouseScroll(mouseX, mouseY, delta)) continue;
            return true;
        }
        return false;
    }

    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        for (GuiElement child : this.children) {
            if (!child.onKeyPress(keyCode, scanCode, modifiers)) continue;
            return true;
        }
        return false;
    }

    public boolean onKeyRelease(int keyCode, int scanCode, int modifiers) {
        for (GuiElement child : this.children) {
            if (!child.onKeyRelease(keyCode, scanCode, modifiers)) continue;
            return true;
        }
        return false;
    }

    public boolean onCharType(char codePoint, int modifiers) {
        for (GuiElement child : this.children) {
            if (!child.onCharType(codePoint, modifiers)) continue;
            return true;
        }
        return false;
    }

    public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) {
            return;
        }
        this.drawChildren(graphics, x + this.x, y + this.y, width, height, mouseX, mouseY, partialTick);
    }

    protected void drawChildren(GuiGraphics graphics, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
        for (GuiElement child : this.children) {
            if (!child.isVisible()) continue;
            child.draw(graphics, refX, refY, screenWidth, screenHeight, mouseX, mouseY, opacity);
        }
    }

    protected void onFocus() {
    }

    protected void onBlur() {
    }

    protected final void syncStyle() {
        if (this.node == null) {
            return;
        }
        this.node.setAttribute("style", this.inlineStyle());
    }

    private String inlineStyle() {
        return "left:" + this.x + "px;top:" + this.y + "px;width:" + this.width + "px;height:" + this.height + "px;";
    }
}

