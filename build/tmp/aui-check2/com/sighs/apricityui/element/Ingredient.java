/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.sighs.apricityui.element;

import com.sighs.apricityui.dom.SlotContentRules;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.element.Item;
import com.sighs.apricityui.element.MinecraftElement;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.render.BodyRenderNodeProvider;
import com.sighs.apricityui.render.RenderNode;
import com.sighs.apricityui.slot.IngredientDisplaySpec;
import com.sighs.apricityui.slot.IngredientExpressionCompiler;
import com.sighs.apricityui.slot.ItemStackExpressionCompiler;
import java.util.List;
import java.util.Locale;
import net.minecraft.world.item.ItemStack;

@ElementRegister(value="INGREDIENT")
public class Ingredient
extends MinecraftElement
implements BodyRenderNodeProvider {
    public static final String TAG_NAME = "INGREDIENT";
    private String compiledSignature = "";
    private IngredientDisplaySpec displaySpec = IngredientDisplaySpec.EMPTY;
    private int candidateIndex;
    private long nextRotateAtMillis;

    public Ingredient(Document document) {
        super(document, TAG_NAME);
    }

    @Override
    public List<RenderNode> createBodyRenderNodes() {
        return List.of(new RenderNode.ElementBackgroundNode(this));
    }

    @Override
    public void tick() {
        super.tick();
        this.refreshIfNeeded();
        Item item = SlotContentRules.ensureControlledItem(this);
        if (item == null) {
            return;
        }
        if (!this.displaySpec.hasCandidates()) {
            item.setIngredientStack(ItemStack.f_41583_);
            this.updateControlledItemText(item, "minecraft:air");
            return;
        }
        int size = this.displaySpec.candidates().size();
        if (this.candidateIndex < 0 || this.candidateIndex >= size) {
            this.candidateIndex = 0;
        }
        long now = System.currentTimeMillis();
        if (this.displaySpec.cycleEnabled() && size > 1 && !this.isHover && !item.isHover) {
            if (this.nextRotateAtMillis <= 0L) {
                this.nextRotateAtMillis = now + this.displaySpec.cycleIntervalMs();
            } else if (now >= this.nextRotateAtMillis) {
                this.candidateIndex = (this.candidateIndex + 1) % size;
                this.nextRotateAtMillis = now + this.displaySpec.cycleIntervalMs();
            }
        }
        ItemStack selected = this.displaySpec.candidates().get(this.candidateIndex).m_41777_();
        item.setIngredientStack(selected);
        this.updateControlledItemText(item, ItemStackExpressionCompiler.serialize(selected));
    }

    public String getCandidateExpression() {
        StringBuilder builder = new StringBuilder();
        for (Node child : this.childNodes) {
            if (!(child instanceof TextNode)) continue;
            TextNode textNode = (TextNode)child;
            builder.append(textNode.getTextContent());
        }
        return builder.isEmpty() ? (this.innerText == null ? "" : this.innerText) : builder.toString();
    }

    private void refreshIfNeeded() {
        long cycleInterval;
        boolean cycleEnabled;
        String expression = this.getCandidateExpression();
        String signature = expression + "|cycle=" + (cycleEnabled = this.resolveCycleEnabled()) + "|interval=" + (cycleInterval = this.resolveCycleIntervalMs());
        if (signature.equals(this.compiledSignature)) {
            return;
        }
        this.compiledSignature = signature;
        this.displaySpec = IngredientExpressionCompiler.compile(expression, cycleEnabled, cycleInterval);
        this.candidateIndex = 0;
        this.nextRotateAtMillis = 0L;
    }

    private boolean resolveCycleEnabled() {
        Boolean cssFlag = Ingredient.parseBooleanLike(this.getCustomPropertyInherit("--aui-ingredient-cycle"));
        if (cssFlag == null) {
            cssFlag = Ingredient.parseBooleanLike(this.getCustomPropertyInherit("--aui-slot-cycle"));
        }
        if (cssFlag != null) {
            return cssFlag;
        }
        Boolean attrFlag = Ingredient.parseBooleanLike(this.getAttribute("cycle"));
        return attrFlag == null || attrFlag != false;
    }

    private long resolveCycleIntervalMs() {
        Long cssInterval = Ingredient.parsePositiveLong(this.getCustomPropertyInherit("--aui-ingredient-cycle-interval"));
        if (cssInterval == null) {
            cssInterval = Ingredient.parsePositiveLong(this.getCustomPropertyInherit("--aui-slot-cycle-interval"));
        }
        if (cssInterval != null) {
            return Math.max(200L, cssInterval);
        }
        Long attrInterval = Ingredient.parsePositiveLong(this.getFirstNonBlankAttribute("cycle-interval", "rotate-interval"));
        if (attrInterval != null) {
            return Math.max(200L, attrInterval);
        }
        return 1000L;
    }

    private void updateControlledItemText(Item item, String value) {
        if (item == null || value == null || value.equals(item.getTextContent())) {
            return;
        }
        item.setTextContent(value);
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

    private static Long parsePositiveLong(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            long parsed = Long.parseLong(raw.trim());
            return parsed > 0L ? Long.valueOf(parsed) : null;
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    static {
        Element.register(TAG_NAME, (document, tagName) -> new Ingredient((Document)document));
    }
}

