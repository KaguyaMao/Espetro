/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.world.item.ItemStack
 */
package com.sighs.apricityui.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.dom.SlotContentRules;
import com.sighs.apricityui.element.Item;
import com.sighs.apricityui.element.MinecraftElement;
import com.sighs.apricityui.element.Recipe;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.BodyRenderNodeProvider;
import com.sighs.apricityui.render.ForegroundRenderNodeProvider;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.RenderNode;
import com.sighs.apricityui.style.Background;
import com.sighs.apricityui.style.Interaction;
import java.util.Collections;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.item.ItemStack;

@ElementRegister(value="SLOT")
public class Slot
extends MinecraftElement
implements BodyRenderNodeProvider,
ForegroundRenderNodeProvider {
    public static final String TAG_NAME = "SLOT";
    private static final ThreadLocal<Set<Slot>> INTERACTIVE_RESOLUTION;
    private boolean bound;
    private boolean boundDisabled;
    private boolean boundHidden;
    private boolean boundGhost;

    public Slot(Document document) {
        super(document, TAG_NAME);
    }

    public boolean isBound() {
        return this.bound;
    }

    public void bindToMenuSlot(boolean initialDisabled) {
        this.bound = true;
        this.boundDisabled = initialDisabled;
        this.boundHidden = false;
        this.boundGhost = false;
    }

    public void updateBoundMenuState(boolean nextDisabled, boolean nextHidden, boolean nextGhost) {
        this.boundDisabled = nextDisabled;
        this.boundHidden = nextHidden;
        this.boundGhost = nextGhost;
    }

    public void updateBoundMenuState(boolean nextDisabled, boolean nextGhost) {
        this.updateBoundMenuState(nextDisabled, false, nextGhost);
    }

    public void updateBoundMenuState(boolean nextDisabled) {
        this.updateBoundMenuState(nextDisabled, false, false);
    }

    public void clearMenuSlotBinding() {
        this.bound = false;
        this.boundDisabled = false;
        this.boundHidden = false;
        this.boundGhost = false;
        this.setHover(false);
    }

    public boolean isExplicitlyDisabled() {
        if (!this.hasAttribute("disabled")) {
            return false;
        }
        Boolean disabledAttribute = Slot.parseBooleanLike(this.getAttribute("disabled"));
        return disabledAttribute == null || disabledAttribute != false;
    }

    @Override
    public boolean isDisabled() {
        return this.boundDisabled || this.isExplicitlyDisabled();
    }

    public boolean canShowItemTooltip() {
        return this.resolveInteractionCapabilities().contains((Object)InteractionCapability.TOOLTIP);
    }

    public boolean canOperateBoundMenuSlot() {
        return this.bound && !this.isDisabled() && this.resolveInteractionCapabilities().contains((Object)InteractionCapability.SLOT);
    }

    public boolean canReceiveSlotFocus() {
        return this.canOperateBoundMenuSlot();
    }

    public boolean shouldAcceptPointer() {
        return this.canOperateBoundMenuSlot();
    }

    public int getRepeatCount() {
        Integer parsed = Slot.parsePositiveInt(this.getAttribute("repeat"));
        return parsed == null ? 1 : parsed;
    }

    public int getSlotIndex() {
        Integer parsed = Slot.parseInt(this.getFirstNonBlankAttribute("slot-index", "index"));
        return parsed == null ? -1 : parsed;
    }

    public int resolveSlotSizeHint(int fallback) {
        int height;
        Integer cssSize = Slot.parsePositiveInt(this.getCustomPropertyInherit("--aui-slot-size"));
        if (cssSize != null) {
            return cssSize;
        }
        int width = Size.parse(this.getComputedStyle().width);
        int styleSize = Math.max(width, height = Size.parse(this.getComputedStyle().height));
        if (styleSize > 0) {
            return styleSize;
        }
        Integer attrSize = Slot.parsePositiveInt(this.getFirstNonBlankAttribute("size", "slot-size"));
        if (attrSize != null) {
            return attrSize;
        }
        return Math.max(1, fallback);
    }

    public boolean containsSlotPoint(double mouseX, double mouseY) {
        Position position = Position.of(this);
        int size = this.resolveSlotSizeHint(16);
        return mouseX >= position.x && mouseX < position.x + (double)size && mouseY >= position.y && mouseY < position.y + (double)size;
    }

    public boolean shouldRenderBackground() {
        Boolean cssFlag = Slot.parseBooleanLike(this.getCustomPropertyInherit("--aui-slot-render-bg"));
        if (cssFlag != null) {
            return cssFlag;
        }
        Boolean attrFlag = Slot.parseBooleanLike(this.getAttribute("render-bg"));
        if (attrFlag != null) {
            return attrFlag;
        }
        return switch (Slot.normalizeToken(this.getAttribute("render"))) {
            case "item", "none" -> false;
            default -> true;
        };
    }

    public boolean shouldRenderItem() {
        Boolean cssFlag = Slot.parseBooleanLike(this.getCustomPropertyInherit("--aui-slot-render-item"));
        if (cssFlag != null) {
            return cssFlag;
        }
        Boolean attrFlag = Slot.parseBooleanLike(this.getAttribute("render-item"));
        if (attrFlag != null) {
            return attrFlag;
        }
        return switch (Slot.normalizeToken(this.getAttribute("render"))) {
            case "bg", "none" -> false;
            default -> true;
        };
    }

    public float resolveIconScale(float fallback) {
        Float cssScale = Slot.parsePositiveFloat(this.getCustomPropertyInherit("--aui-slot-icon-scale"));
        if (cssScale != null) {
            return cssScale.floatValue();
        }
        Float attrScale = Slot.parsePositiveFloat(this.getAttribute("iconScale"));
        if (attrScale != null) {
            return attrScale.floatValue();
        }
        return Math.max(0.01f, fallback);
    }

    public int resolveZIndex(int fallback) {
        Integer cssZ = Slot.parseInt(this.getCustomPropertyInherit("--aui-slot-z"));
        if (cssZ != null) {
            return cssZ;
        }
        Integer attrZ = Slot.parseInt(this.getFirstNonBlankAttribute("zIndex", "z"));
        return attrZ == null ? fallback : attrZ;
    }

    @Override
    public boolean canFocus() {
        return this.canReceiveSlotFocus();
    }

    public String getBackgroundImageCandidate() {
        String rawPath;
        Background background = Background.of(this);
        String string = rawPath = background == null ? null : background.imagePath;
        if (rawPath == null || rawPath.isBlank() || "unset".equals(rawPath)) {
            return null;
        }
        return rawPath;
    }

    @Override
    public List<RenderNode> createBodyRenderNodes() {
        return List.of(new RenderNode.ElementBackgroundNode(this));
    }

    @Override
    public List<RenderNode> createForegroundRenderNodes() {
        return List.of(new RenderNode.ElementForegroundNode(this, this::drawForegroundMask));
    }

    public boolean shouldRenderForegroundMask() {
        return !(!this.canOperateBoundMenuSlot() || this.boundHidden || !this.shouldRenderBackground() && !this.shouldRenderItem() || !this.isVisible || !Interaction.isDisplayed(this) || !this.isHover && !this.boundGhost);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void drawForegroundMask(PoseStack poseStack) {
        if (!this.shouldRenderForegroundMask()) {
            return;
        }
        Position position = Position.of(this);
        int size = this.resolveSlotSizeHint(16);
        poseStack.m_252880_(0.0f, 0.0f, Base.getGuiItemForegroundZ());
        Graph.beginLayeredBatch();
        try {
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), (float)position.x, (float)position.y, (float)(position.x + (double)size), (float)(position.y + (double)size), -2130706433);
        }
        finally {
            Graph.endBatch();
        }
    }

    @Override
    public void drawBackgroundOnly(PoseStack poseStack) {
        if (!this.shouldRenderBackground()) {
            return;
        }
        super.drawBackgroundOnly(poseStack);
    }

    @Override
    public void drawPhase(PoseStack poseStack, Base.RenderPhase phase) {
        if (!(this.shouldRenderBackground() || phase != Base.RenderPhase.SHADOW && phase != Base.RenderPhase.BODY && phase != Base.RenderPhase.BORDER)) {
            return;
        }
        super.drawPhase(poseStack, phase);
    }

    @Override
    public ItemStack getTooltipStack() {
        if (!this.canShowItemTooltip() || !this.shouldRenderItem()) {
            return ItemStack.f_41583_;
        }
        Item item = SlotContentRules.getDisplayItem(this);
        return item == null ? ItemStack.f_41583_ : item.getTooltipStack();
    }

    public void applyRecipeSlotMeta(String className, String generatedTag) {
        this.setAttributesBatch(Map.of("class", className == null ? "" : className, "data-generated", generatedTag == null ? "" : generatedTag), true);
    }

    private boolean isRecipeSlot() {
        String generatedTag = this.getAttribute("data-generated");
        if (generatedTag != null && generatedTag.startsWith("recipe")) {
            return true;
        }
        return this.hasAncestor(Recipe.class);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private EnumSet<InteractionCapability> resolveInteractionCapabilities() {
        Set<Slot> resolving = INTERACTIVE_RESOLUTION.get();
        if (!resolving.add(this)) {
            return this.resolveInteractionCapabilitiesWithoutCss();
        }
        try {
            if (this.isRecipeSlot()) {
                EnumSet<InteractionCapability> enumSet = EnumSet.noneOf(InteractionCapability.class);
                return enumSet;
            }
            EnumSet<InteractionCapability> attributeCapabilities = Slot.parseInteractionCapabilities(this.getAttribute("interactive"));
            if (attributeCapabilities != null) {
                EnumSet<InteractionCapability> enumSet = attributeCapabilities;
                return enumSet;
            }
            EnumSet<InteractionCapability> pointerCapabilities = Slot.parseInteractionCapabilities(this.getAttribute("pointer"));
            if (pointerCapabilities != null) {
                EnumSet<InteractionCapability> enumSet = pointerCapabilities;
                return enumSet;
            }
            EnumSet<InteractionCapability> cssCapabilities = Slot.parseInteractionCapabilities(this.getCustomPropertyInherit("--aui-slot-interactive"));
            if (cssCapabilities != null) {
                EnumSet<InteractionCapability> enumSet = cssCapabilities;
                return enumSet;
            }
            EnumSet<InteractionCapability> enumSet = this.resolveInteractionCapabilitiesWithoutCss();
            return enumSet;
        }
        finally {
            resolving.remove(this);
            if (resolving.isEmpty()) {
                INTERACTIVE_RESOLUTION.remove();
            }
        }
    }

    private EnumSet<InteractionCapability> resolveInteractionCapabilitiesWithoutCss() {
        if (this.isRecipeSlot()) {
            return EnumSet.noneOf(InteractionCapability.class);
        }
        EnumSet<InteractionCapability> attributeCapabilities = Slot.parseInteractionCapabilities(this.getAttribute("interactive"));
        if (attributeCapabilities != null) {
            return attributeCapabilities;
        }
        EnumSet<InteractionCapability> pointerCapabilities = Slot.parseInteractionCapabilities(this.getAttribute("pointer"));
        if (pointerCapabilities != null) {
            return pointerCapabilities;
        }
        return this.bound ? EnumSet.of(InteractionCapability.TOOLTIP, InteractionCapability.SLOT) : EnumSet.of(InteractionCapability.TOOLTIP);
    }

    private String getFirstNonBlankAttribute(String ... keys) {
        if (keys == null) {
            return null;
        }
        for (String key : keys) {
            String value;
            if (key == null || key.isBlank() || (value = this.getAttribute(key)) == null || value.isBlank()) continue;
            return value;
        }
        return null;
    }

    private static String normalizeToken(String raw) {
        return raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
    }

    private static Integer parseInt(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(raw.trim());
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static Integer parsePositiveInt(String raw) {
        Integer parsed = Slot.parseInt(raw);
        return parsed != null && parsed > 0 ? parsed : null;
    }

    private static Float parsePositiveFloat(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            float parsed = Float.parseFloat(raw.trim());
            return parsed > 0.0f ? Float.valueOf(parsed) : null;
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static EnumSet<InteractionCapability> parseInteractionCapabilities(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (normalized.isBlank() || "unset".equals(normalized) || "auto".equals(normalized)) {
            return null;
        }
        EnumSet<InteractionCapability> result = EnumSet.noneOf(InteractionCapability.class);
        boolean hasKnownCapability = false;
        String[] stringArray = normalized.split("[\\s,]+");
        int n = stringArray.length;
        block21: for (int i = 0; i < n; ++i) {
            String token;
            switch (token = stringArray[i]) {
                case "1": 
                case "true": 
                case "yes": 
                case "on": 
                case "enabled": {
                    result.add(InteractionCapability.TOOLTIP);
                    result.add(InteractionCapability.SLOT);
                    hasKnownCapability = true;
                    continue block21;
                }
                case "0": 
                case "false": 
                case "no": 
                case "off": 
                case "disabled": 
                case "none": {
                    return EnumSet.noneOf(InteractionCapability.class);
                }
                case "tooltip": {
                    result.add(InteractionCapability.TOOLTIP);
                    hasKnownCapability = true;
                    continue block21;
                }
                case "slot": {
                    result.add(InteractionCapability.SLOT);
                    hasKnownCapability = true;
                    continue block21;
                }
            }
        }
        return hasKnownCapability ? result : null;
    }

    private static Boolean parseBooleanLike(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (normalized.isBlank() || "unset".equals(normalized) || "auto".equals(normalized)) {
            return null;
        }
        return switch (normalized) {
            case "1", "true", "yes", "on", "enabled" -> true;
            case "0", "false", "no", "off", "disabled", "none" -> false;
            default -> null;
        };
    }

    static {
        Element.register(TAG_NAME, (document, tagName) -> new Slot((Document)document));
        INTERACTIVE_RESOLUTION = ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap()));
    }

    private static enum InteractionCapability {
        TOOLTIP,
        SLOT;

    }
}

