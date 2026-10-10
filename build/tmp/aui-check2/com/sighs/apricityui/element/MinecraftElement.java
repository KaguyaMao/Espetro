/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.item.ItemStack
 */
package com.sighs.apricityui.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.Rect;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public abstract class MinecraftElement
extends Element {
    private static final Map<String, Object> GLOBAL_RUNTIME_CACHES = new ConcurrentHashMap<String, Object>();
    private int attributeBatchDepth = 0;
    private boolean pendingCssUpdate = false;

    protected MinecraftElement(Document document, String tagName) {
        super(document, tagName);
    }

    protected static Object getGlobalCache(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        return GLOBAL_RUNTIME_CACHES.get(key);
    }

    protected static Object computeGlobalCacheIfAbsent(String key, Supplier<Object> factory) {
        if (key == null || key.isBlank() || factory == null) {
            return null;
        }
        return GLOBAL_RUNTIME_CACHES.computeIfAbsent(key, ignored -> factory.get());
    }

    protected static void clearGlobalCache(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        GLOBAL_RUNTIME_CACHES.remove(key);
    }

    protected static void clearAllGlobalCaches() {
        GLOBAL_RUNTIME_CACHES.clear();
    }

    public final <T extends Element> T findAncestor(Class<T> type) {
        if (type == null) {
            return null;
        }
        Element current = this.parentElement;
        while (current != null) {
            if (type.isInstance(current)) {
                return (T)((Element)type.cast(current));
            }
            current = current.parentElement;
        }
        return null;
    }

    public final boolean hasAncestor(Class<? extends Element> type) {
        return this.findAncestor(type) != null;
    }

    public final void beginAttributeBatch() {
        ++this.attributeBatchDepth;
    }

    public final void endAttributeBatch(boolean updateCssOnce) {
        if (this.attributeBatchDepth <= 0) {
            return;
        }
        --this.attributeBatchDepth;
        if (updateCssOnce) {
            this.pendingCssUpdate = true;
        }
        if (this.attributeBatchDepth == 0 && this.pendingCssUpdate) {
            this.pendingCssUpdate = false;
            this.invalidateStyle();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void setAttributesBatch(Map<String, String> attributes, boolean updateCssOnce) {
        if (attributes == null || attributes.isEmpty()) {
            return;
        }
        this.beginAttributeBatch();
        try {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                this.putAttributeSilently(entry.getKey(), entry.getValue());
            }
        }
        finally {
            this.endAttributeBatch(updateCssOnce);
        }
    }

    protected final void putAttributeSilently(String name, String value) {
        if (name == null || name.isBlank()) {
            return;
        }
        HashMap<String, String> attributes = this.getAttributes();
        String safeValue = value == null ? "" : value;
        attributes.put(name, safeValue);
        if ("style".equals(name)) {
            this.updateInlineStyle();
        }
        if ("value".equals(name)) {
            this.value = safeValue;
        }
        if ("id".equals(name)) {
            this.id = safeValue;
            if (this.document != null) {
                this.document.recordID(this);
            }
        }
        if ("class".equals(name)) {
            this.classNames = MinecraftElement.parseClassNames(safeValue);
        }
        if (this.attributeBatchDepth == 0) {
            this.invalidateStyle();
        }
    }

    public final void requestRepaint() {
        if (this.document != null) {
            this.document.markDirty(this, 1);
        }
    }

    public final void requestRelayout() {
        this.getRenderer().position.clear();
        this.getRenderer().size.clear();
        this.getRenderer().box.clear();
        if (this.document != null) {
            this.document.markDirty(this, 4);
        }
    }

    public ItemStack getTooltipStack() {
        return ItemStack.f_41583_;
    }

    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        ItemStack stack = this.getTooltipStack();
        if (stack.m_41619_()) {
            return;
        }
        guiGraphics.m_280153_(Minecraft.m_91087_().f_91062_, stack, mouseX, mouseY);
    }

    @Override
    public void drawPhase(PoseStack poseStack, Base.RenderPhase phase) {
        Rect rectRenderer = Rect.of(this);
        switch (phase) {
            case SHADOW: {
                rectRenderer.drawShadow(poseStack);
                break;
            }
            case BODY: {
                rectRenderer.drawBody(poseStack);
                break;
            }
            case BORDER: {
                rectRenderer.drawBorder(poseStack);
            }
        }
    }
}

