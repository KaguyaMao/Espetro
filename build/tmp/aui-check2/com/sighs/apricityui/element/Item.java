/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.sighs.apricityui.element;

import com.sighs.apricityui.element.MinecraftElement;
import com.sighs.apricityui.element.Slot;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.render.BodyRenderNodeProvider;
import com.sighs.apricityui.render.RenderNode;
import com.sighs.apricityui.slot.ItemStackExpressionCompiler;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;

@ElementRegister(value="ITEM")
public class Item
extends MinecraftElement
implements BodyRenderNodeProvider {
    public static final String TAG_NAME = "ITEM";
    private Source source = Source.NONE;
    private Optional<ItemStack> drivenStack = Optional.empty();
    private String overlayText;
    private boolean hidden;
    private boolean menuDisabled;
    private String parsedSource;
    private Optional<ItemStack> parsedStack = Optional.empty();

    public Item(Document document) {
        super(document, TAG_NAME);
    }

    public void setDrivenState(ItemStack stack, String nextOverlayText, boolean nextHidden, boolean nextMenuDisabled, Source nextSource) {
        this.drivenStack = Item.copyStack(stack);
        this.overlayText = nextOverlayText;
        this.hidden = nextHidden;
        this.menuDisabled = nextMenuDisabled;
        this.source = nextSource == null ? Source.NONE : nextSource;
        this.requestRepaint();
    }

    public void setIngredientStack(ItemStack stack) {
        this.setDrivenState(stack, null, false, false, Source.INGREDIENT);
    }

    public void clearDrivenState(Source expectedSource) {
        if (expectedSource != null && this.source != expectedSource) {
            return;
        }
        this.source = Source.NONE;
        this.drivenStack = Optional.empty();
        this.overlayText = null;
        this.hidden = false;
        this.menuDisabled = false;
        this.requestRepaint();
    }

    public boolean isMenuBound() {
        return this.source == Source.MENU;
    }

    public boolean canOperateBoundMenuSlot() {
        Slot slot = this.findAncestor(Slot.class);
        return this.isMenuBound() && slot != null && slot.canOperateBoundMenuSlot();
    }

    public boolean canShowItemTooltip() {
        Slot slot = this.findAncestor(Slot.class);
        if (slot != null) {
            return slot.canShowItemTooltip();
        }
        return this.resolveStandaloneInteraction().contains((Object)InteractionCapability.TOOLTIP);
    }

    public boolean shouldPaintItem() {
        if (this.hidden || this.source == Source.MENU && this.menuDisabled) {
            return false;
        }
        Slot slot = this.findAncestor(Slot.class);
        return slot == null || slot.shouldRenderItem() && !slot.isDisabled();
    }

    public ItemStack resolveDisplayStack() {
        if (!this.shouldPaintItem()) {
            return ItemStack.f_41583_;
        }
        return this.currentStack().map(ItemStack::m_41777_).orElse(ItemStack.f_41583_);
    }

    public String resolveOverlayText() {
        return this.shouldPaintItem() ? this.overlayText : null;
    }

    @Override
    public ItemStack getTooltipStack() {
        if (!this.canShowItemTooltip()) {
            return ItemStack.f_41583_;
        }
        return this.resolveDisplayStack();
    }

    @Override
    public List<RenderNode> createBodyRenderNodes() {
        return List.of(new RenderNode.ElementBackgroundNode(this), new RenderNode.ItemNode(this, this::resolveDisplayStack, this::shouldPaintItem, this::resolveIconScale, this::resolveZIndex, true, this::resolveOverlayText, () -> 0.0));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.source == Source.NONE) {
            this.refreshParsedStack();
        }
    }

    private Optional<ItemStack> currentStack() {
        if (this.source != Source.NONE) {
            return this.drivenStack;
        }
        this.refreshParsedStack();
        return this.parsedStack;
    }

    private void refreshParsedStack() {
        String expression = this.getTextContent();
        if (expression == null) {
            expression = "";
        }
        if (expression.equals(this.parsedSource)) {
            return;
        }
        this.parsedSource = expression;
        this.parsedStack = Item.copyStack(ItemStackExpressionCompiler.parse(expression));
        this.requestRepaint();
    }

    private double resolveIconScale() {
        Slot slot = this.findAncestor(Slot.class);
        return slot == null ? 1.0 : (double)slot.resolveIconScale(1.0f);
    }

    private int resolveZIndex() {
        Slot slot = this.findAncestor(Slot.class);
        return slot == null ? 0 : slot.resolveZIndex(0);
    }

    private EnumSet<InteractionCapability> resolveStandaloneInteraction() {
        String raw = this.getAttribute("interactive");
        if (raw == null || raw.isBlank()) {
            return EnumSet.of(InteractionCapability.TOOLTIP);
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        EnumSet<InteractionCapability> result = EnumSet.noneOf(InteractionCapability.class);
        String[] stringArray = normalized.split("[\\s,]+");
        int n = stringArray.length;
        block22: for (int i = 0; i < n; ++i) {
            String token;
            switch (token = stringArray[i]) {
                case "1": 
                case "true": 
                case "yes": 
                case "on": 
                case "enabled": 
                case "all": {
                    result.add(InteractionCapability.TOOLTIP);
                    result.add(InteractionCapability.SLOT);
                    continue block22;
                }
                case "tooltip": {
                    result.add(InteractionCapability.TOOLTIP);
                    continue block22;
                }
                case "slot": {
                    result.add(InteractionCapability.SLOT);
                    continue block22;
                }
                case "0": 
                case "false": 
                case "no": 
                case "off": 
                case "disabled": 
                case "none": {
                    return EnumSet.noneOf(InteractionCapability.class);
                }
            }
        }
        return result;
    }

    private static Optional<ItemStack> copyStack(ItemStack stack) {
        return stack == null || stack.m_41619_() ? Optional.empty() : Optional.of(stack.m_41777_());
    }

    static {
        Element.register(TAG_NAME, (document, tagName) -> new Item((Document)document));
    }

    public static enum Source {
        NONE,
        INGREDIENT,
        MENU;

    }

    private static enum InteractionCapability {
        TOOLTIP,
        SLOT;

    }
}

