/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.latvian.mods.rhino.util.HideFromJS
 */
package com.sighs.apricityui.init;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.behavior.ScrollModel;
import com.sighs.apricityui.behavior.SelectModel;
import com.sighs.apricityui.behavior.SelectionUnits;
import com.sighs.apricityui.behavior.TextSelection;
import com.sighs.apricityui.dom.CommentNode;
import com.sighs.apricityui.dom.InnerText;
import com.sighs.apricityui.dom.NodeTree;
import com.sighs.apricityui.dom.RenderElement;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.element.ContentEditable;
import com.sighs.apricityui.element.Div;
import com.sighs.apricityui.element.RichText;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.form.ConstraintValidator;
import com.sighs.apricityui.form.FormData;
import com.sighs.apricityui.form.FormDataEntry;
import com.sighs.apricityui.form.ValidityState;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Flex;
import com.sighs.apricityui.layout.Layout;
import com.sighs.apricityui.layout.NormalFlow;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.parser.CSS;
import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.parser.CssString;
import com.sighs.apricityui.parser.Selector;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.DirtyFlags;
import com.sighs.apricityui.render.FontDrawer;
import com.sighs.apricityui.render.GeometryQueryScope;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.style.Animation;
import com.sighs.apricityui.style.Background;
import com.sighs.apricityui.style.ConstraintText;
import com.sighs.apricityui.style.InlineStyleDeclaration;
import com.sighs.apricityui.style.Interaction;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.style.StyleFrameCache;
import com.sighs.apricityui.style.Text;
import com.sighs.apricityui.style.Transition;
import com.sighs.apricityui.util.HtmlSerializer;
import com.sighs.apricityui.util.TextMetrics;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.IsoFields;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Element
extends Node {
    private HashMap<String, String> attributes = new HashMap();
    private final Map<String, Object> runtimeCaches = new HashMap<String, Object>();
    public String tagName;
    public String innerText = "";
    private String lastInnerText = "";
    public boolean isLoaded = false;
    public HashMap<String, CSS.Declaration> cssCache = new HashMap();
    private boolean cssCacheMatched = false;
    public Element parentElement = null;
    public ArrayList<Element> children = new ArrayList();
    private Element beforePseudoElement = null;
    private Element afterPseudoElement = null;
    private List<Node> renderChildNodesCache = null;
    private TextNode legacyRenderTextNode = null;
    private HashMap<String, CSS.Declaration> beforePseudoStyles = null;
    private HashMap<String, CSS.Declaration> afterPseudoStyles = null;
    private boolean beforePseudoResolved = false;
    private boolean afterPseudoResolved = false;
    private boolean pseudoElement = false;
    private Selector.PseudoElement pseudoElementKind = null;
    private Element pseudoElementHost = null;
    private Style pseudoElementPreviousStyle = null;
    public boolean isPointerEnabled = true;
    public boolean isVisible = true;
    public String id = null;
    public String value = null;
    private boolean valueDirty = false;
    private Boolean checkedState = null;
    private boolean checkedDirty = false;
    public Boolean selectedState = null;
    private boolean selectedDirty = false;
    private String customValidityMessage = "";
    private final ArrayList<String> fileList = new ArrayList();
    public boolean isHover = false;
    public boolean isActive = false;
    public boolean isFocus = false;
    public double scrollWidth = 0.0;
    public double scrollHeight = 0.0;
    public double scrollLeft = 0.0;
    public double scrollTop = 0.0;
    public double targetScrollLeft = 0.0;
    public double targetScrollTop = 0.0;
    public Set<String> classNames = Collections.emptySet();
    private RenderElement renderElement = new RenderElement(this);
    private final DirtyFlags dirty = new DirtyFlags();
    private final NodeTree node = new NodeTree(this);
    private final ScrollModel scroll = new ScrollModel(this);
    private final TextSelection textSelection = new TextSelection(this);
    private final DOMTokenList classList = new DOMTokenList(this);
    private final DOMStringMap dataset = new DOMStringMap(this);
    private boolean domInitHookInvoked = false;
    private boolean inlineEventHandlersInstalled = false;
    private boolean topLayer = false;
    private final Style inlineStyle = Style.createInlineDeclarationStyle(this);
    private Style inlineStyleSnapshot = this.inlineStyle.clone();
    private List<InlineStyleDeclaration.Entry> inlineDeclarations = new ArrayList<InlineStyleDeclaration.Entry>();
    private boolean inlineStyleInitialized = false;
    private boolean mutableInlineStyleExposed = false;
    private boolean syncingInlineStyle = false;
    private String inlineStyleAttributeSnapshot = null;
    private static final Map<String, BiFunction<Document, String, ? extends Element>> REGISTRY = new HashMap<String, BiFunction<Document, String, ? extends Element>>();

    public final Object getRuntimeCache(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        return this.runtimeCaches.get(key);
    }

    public final void putRuntimeCache(String key, Object value) {
        if (key == null || key.isBlank()) {
            return;
        }
        if (value == null) {
            this.runtimeCaches.remove(key);
            return;
        }
        this.runtimeCaches.put(key, value);
    }

    public final Object computeRuntimeCacheIfAbsent(String key, Supplier<Object> factory) {
        if (key == null || key.isBlank() || factory == null) {
            return null;
        }
        return this.runtimeCaches.computeIfAbsent(key, ignored -> factory.get());
    }

    public final void removeRuntimeCache(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        this.runtimeCaches.remove(key);
    }

    public final void clearRuntimeCaches() {
        this.runtimeCaches.clear();
    }

    public Element(Document document, String tagName) {
        super(document);
        this.tagName = tagName.toUpperCase();
        this.textSelection.addEventListeners();
    }

    protected static Set<String> parseClassNames(String value) {
        return CssString.parseClassNames(value);
    }

    protected final void invalidateStyleCaches() {
        this.renderElement.computedStyle.clear();
        this.clearPseudoElementCaches();
        StyleFrameCache.invalidate(this);
        if (this.document != null) {
            this.document.bumpSelectionCache();
        }
    }

    public final void invalidateStyle() {
        this.invalidateStyleCaches();
        if (this.isConnected()) {
            this.requestStyleRecalc();
        }
    }

    public ArrayList<Element> getRoute() {
        return this.node.getRoute();
    }

    public Element[] getRouteArray() {
        return this.node.getRouteArray();
    }

    public void forEachRoute(Consumer<Element> consumer) {
        this.node.forEachRoute(consumer);
    }

    public Style getStyle() {
        this.ensureInlineStyleInitialized();
        this.mutableInlineStyleExposed = true;
        if (this.document != null) {
            this.document.trackMutableInlineStyle(this);
        }
        return this.inlineStyle;
    }

    void commitPendingInlineStyleMutations() {
        this.syncLegacyInlineStyleMutations();
    }

    public void setInlineStyleProperty(String name, String value) {
        this.setInlineStyleProperty(name, value, "");
    }

    public void setInlineStyleProperty(String name, String value, String priority) {
        String normalizedPriority;
        this.syncLegacyInlineStyleMutations();
        String property = InlineStyleDeclaration.normalizeProperty(name);
        if (property.isBlank()) {
            return;
        }
        String normalizedValue = value == null ? "" : value.trim();
        String string = normalizedPriority = priority == null ? "" : priority.trim().toLowerCase(Locale.ROOT);
        if (!normalizedPriority.isEmpty() && !"important".equals(normalizedPriority)) {
            return;
        }
        InlineStyleDeclaration.removeAll(this.inlineDeclarations, property);
        if (!normalizedValue.isEmpty()) {
            this.inlineDeclarations.add(new InlineStyleDeclaration.Entry(property, normalizedValue + ("important".equals(normalizedPriority) ? " !important" : "")));
        }
        this.commitInlineDeclarations();
    }

    public String removeInlineStyleProperty(String name) {
        this.syncLegacyInlineStyleMutations();
        String property = InlineStyleDeclaration.normalizeProperty(name);
        String previous = InlineStyleDeclaration.lastValue(this.inlineDeclarations, property);
        if (previous != null) {
            InlineStyleDeclaration.removeAll(this.inlineDeclarations, property);
            this.commitInlineDeclarations();
        }
        return InlineStyleDeclaration.valueWithoutPriority(previous);
    }

    public String getInlineStylePropertyValue(String name) {
        this.syncLegacyInlineStyleMutations();
        String property = InlineStyleDeclaration.normalizeProperty(name);
        String value = InlineStyleDeclaration.lastValue(this.inlineDeclarations, property);
        if (value != null) {
            return InlineStyleDeclaration.valueWithoutPriority(value);
        }
        String expanded = this.inlineStyle.get(property);
        return expanded == null || "unset".equalsIgnoreCase(expanded) ? "" : expanded;
    }

    public String getInlineStylePropertyPriority(String name) {
        this.syncLegacyInlineStyleMutations();
        return InlineStyleDeclaration.priorityOf(InlineStyleDeclaration.lastValue(this.inlineDeclarations, InlineStyleDeclaration.normalizeProperty(name)));
    }

    public String getInlineStyleCssText() {
        this.syncLegacyInlineStyleMutations();
        return InlineStyleDeclaration.serialize(this.inlineDeclarations);
    }

    public void setInlineStyleCssText(String value) {
        this.syncLegacyInlineStyleMutations();
        this.setAttribute("style", value == null ? "" : value);
    }

    public String[] getInlineStylePropertyNames() {
        this.syncLegacyInlineStyleMutations();
        String[] names = new String[this.inlineDeclarations.size()];
        for (int index = 0; index < names.length; ++index) {
            names[index] = this.inlineDeclarations.get(index).property();
        }
        return names;
    }

    public String[] getSupportedInlineStylePropertyNames() {
        return Style.getSupportedPropertyNames();
    }

    public String getCustomProperty(String name) {
        return this.getRawComputedStyle().getCustomProperty(name);
    }

    public String getRawCustomProperty(String name) {
        Style cached = this.renderElement.computedStyle.get();
        if (cached != null) {
            return cached.getCustomProperty(name);
        }
        this.ensureCssCacheReady();
        Style rawStyle = new Style();
        rawStyle.mergeCascade(this.cssCache, this.getAttribute("style"));
        return rawStyle.getCustomProperty(name);
    }

    public String getCustomPropertyInherit(String name) {
        Element current = this;
        while (current != null) {
            String value = current.getCustomProperty(name);
            if (value != null && !value.isBlank()) {
                return value;
            }
            current = current.parentElement;
        }
        return null;
    }

    public HashMap<String, String> getAttributes() {
        return this.attributes;
    }

    public String getAttribute(String name) {
        if (name == null) {
            return null;
        }
        if ("style".equals(name)) {
            this.syncLegacyInlineStyleMutations();
        }
        if ("id".equals(name)) {
            String attrId = this.attributes.get("id");
            if (this.id == null) {
                this.id = attrId;
            } else if (!this.id.equals(attrId)) {
                this.attributes.put("id", this.id);
                this.requestStyleRecalc();
            }
            return this.attributes.get("id");
        }
        return this.attributes.get(name);
    }

    public void setAttribute(String name, String value) {
        String oldValue = this.attributes.get(name);
        String oldId = "id".equals(name) ? this.id : null;
        this.attributes.put(name, value);
        if (name.equals("style")) {
            this.updateInlineStyle();
        }
        if (name.equals("value")) {
            if (!this.valueDirty || this.value == null) {
                this.value = value;
            }
            this.getRenderer().text.clear();
            this.getRenderer().wrappedText.clear();
        }
        if (name.equals("id")) {
            if (oldId != null && !oldId.isBlank() && this.document != null && !oldId.equals(value)) {
                this.document.removeID(oldId, this);
            }
            this.id = value;
            if (this.document != null) {
                this.document.recordID(this);
            }
        }
        if (name.equals("class")) {
            this.classNames = Element.parseClassNames(value);
        }
        this.syncAttributeState(name);
        this.invalidateStyle();
        if (this.document != null && name != null && !Objects.equals(oldValue, value)) {
            this.document.queueMutation(Document.MutationRecord.attributes(this, name, oldValue));
        }
    }

    public void removeAttribute(String name) {
        String oldValue = this.attributes.get(name);
        String oldId = "id".equals(name) ? this.id : null;
        this.attributes.remove(name);
        if (name.equals("style")) {
            this.updateInlineStyle();
        }
        if (name.equals("value")) {
            if (!this.valueDirty) {
                this.value = null;
            }
            this.getRenderer().text.clear();
            this.getRenderer().wrappedText.clear();
        }
        if (name.equals("id")) {
            if (oldId != null && !oldId.isBlank() && this.document != null) {
                this.document.removeID(oldId, this);
            }
            this.id = null;
        }
        if (name.equals("class")) {
            this.classNames = Collections.emptySet();
        }
        this.syncAttributeState(name);
        this.invalidateStyle();
        if (this.document != null && name != null && oldValue != null) {
            this.document.queueMutation(Document.MutationRecord.attributes(this, name, oldValue));
        }
    }

    public boolean hasAttribute(String name) {
        return this.attributes.containsKey(name);
    }

    public boolean toggleAttribute(String name) {
        return this.toggleAttribute(name, null);
    }

    public boolean toggleAttribute(String name, Boolean force) {
        boolean shouldContain;
        if (name == null || name.isBlank()) {
            return false;
        }
        String normalized = name.trim();
        boolean present = this.hasAttribute(normalized);
        boolean bl = force == null ? !present : (shouldContain = force.booleanValue());
        if (shouldContain) {
            if (!present) {
                this.setAttribute(normalized, "");
            }
        } else if (present) {
            this.removeAttribute(normalized);
        }
        return shouldContain;
    }

    public Set<String> getClassNames() {
        return this.classNames == null ? Collections.emptySet() : this.classNames;
    }

    protected final void requestStyleRecalc() {
        if (this.document != null) {
            this.document.requestStyleRecalc(this);
        }
    }

    public void setTopLayer(boolean topLayer) {
        if (this.topLayer == topLayer) {
            return;
        }
        this.topLayer = topLayer;
        if (this.document != null && this.isConnected()) {
            Div root;
            Div div = root = this.document.documentElement != null ? this.document.documentElement : this.document.body;
            if (root != null) {
                this.document.markDirty(root, 11);
            }
        }
    }

    public boolean isTopLayer() {
        return this.topLayer;
    }

    protected final void requestPseudoStyleRecalc(String pseudoName) {
        if (this.document != null) {
            this.document.requestPseudoStyleRecalc(this, pseudoName);
        }
    }

    public boolean recomputeStyleSelf() {
        Style originStyle = this.getComputedStyle();
        this.cssCache = this.pseudoElement ? Selector.matchPseudoElementCSS(this.pseudoElementHost, this.pseudoElementKind) : Selector.matchCSS(this);
        this.cssCacheMatched = true;
        this.invalidateStyleCaches();
        Style currentStyle = this.getRawComputedStyle();
        if (this.document != null) {
            this.document.setHasAnimationSpec(this, Animation.hasAnimationSpec(currentStyle));
        }
        RenderElement.observeStyle(this, originStyle, currentStyle);
        Transition.create(this, originStyle, currentStyle);
        this.syncGeneratedPseudoElementsForStyleRecalc();
        return currentStyle.affectsDescendantComputedStyleComparedTo(originStyle);
    }

    public Style getComputedStyle() {
        this.syncLegacyInlineStyleMutations();
        Style cached = StyleFrameCache.get(this);
        if (cached != null) {
            return cached;
        }
        Style computedStyle = this.getRawComputedStyle();
        if (StyleFrameCache.isActive()) {
            StyleFrameCache.put(this, computedStyle);
        }
        return computedStyle;
    }

    public Style getRawComputedStyle() {
        Style computedStyle;
        this.syncLegacyInlineStyleMutations();
        Style cache = this.renderElement.computedStyle.get();
        if (cache != null) {
            computedStyle = cache;
        } else {
            this.ensureCssCacheReady();
            computedStyle = new Style();
            computedStyle.applyUserAgentDefaults(this);
            computedStyle.mergeCascade(this.cssCache, this.getAttribute("style"));
            this.renderElement.computedStyle.set(computedStyle);
            computedStyle.resolveVarReferences(this);
            computedStyle.finalizeComputedValues(this);
            this.isPointerEnabled = computedStyle.pointerEvents.equals("auto");
            this.isVisible = Interaction.isVisible(this);
        }
        return computedStyle;
    }

    private void ensureCssCacheReady() {
        if (this.pseudoElement) {
            this.cssCache = Selector.matchPseudoElementCSS(this.pseudoElementHost, this.pseudoElementKind);
            return;
        }
        if (this.document == null || this.cssCacheMatched) {
            return;
        }
        this.cssCache = Selector.matchCSS(this);
        this.cssCacheMatched = true;
    }

    public void updateInlineStyle() {
        String rawAttributeValue = this.attributes.get("style");
        String attributeValue = rawAttributeValue == null ? "" : rawAttributeValue;
        this.inlineDeclarations = InlineStyleDeclaration.parseEntries(attributeValue);
        Style previousStyle = this.inlineStyleSnapshot;
        Style newStyle = Style.createInlineDeclarationStyle();
        for (InlineStyleDeclaration.Entry entry : this.inlineDeclarations) {
            newStyle.update(entry.property(), InlineStyleDeclaration.valueWithoutPriority(entry.value()));
        }
        this.inlineStyle.copyFrom(newStyle);
        if (this.inlineStyleInitialized && this.isConnected()) {
            RenderElement.observeStyle(this, previousStyle, this.inlineStyle);
        }
        this.inlineStyleSnapshot = this.inlineStyle.clone();
        this.inlineStyleAttributeSnapshot = rawAttributeValue;
        this.inlineStyleInitialized = true;
    }

    private void ensureInlineStyleInitialized() {
        if (!this.inlineStyleInitialized) {
            this.updateInlineStyle();
        }
    }

    private void syncLegacyInlineStyleMutations() {
        if (this.syncingInlineStyle) {
            return;
        }
        this.ensureInlineStyleInitialized();
        String attributeValue = this.attributes.get("style");
        if (!Objects.equals(attributeValue, this.inlineStyleAttributeSnapshot)) {
            String oldValue = this.inlineStyleAttributeSnapshot;
            this.updateInlineStyle();
            this.invalidateStyle();
            if (this.document != null) {
                this.document.queueMutation(Document.MutationRecord.attributes(this, "style", oldValue));
            }
        }
        if (!this.mutableInlineStyleExposed) {
            return;
        }
        Map<String, String> changes = this.inlineStyle.changesComparedTo(this.inlineStyleSnapshot);
        if (changes.isEmpty()) {
            return;
        }
        changes.forEach((property, value) -> {
            InlineStyleDeclaration.removeAll(this.inlineDeclarations, property);
            if (value != null && !value.isBlank() && !"unset".equalsIgnoreCase(value.trim())) {
                this.inlineDeclarations.add(new InlineStyleDeclaration.Entry((String)property, value.trim()));
            }
        });
        this.commitInlineDeclarations();
    }

    private void commitInlineDeclarations() {
        String serialized = InlineStyleDeclaration.serialize(this.inlineDeclarations);
        String oldValue = this.attributes.get("style");
        this.syncingInlineStyle = true;
        try {
            this.attributes.put("style", serialized);
            this.updateInlineStyle();
            if (!Objects.equals(oldValue, serialized)) {
                this.invalidateStyle();
            }
            if (this.document != null && !Objects.equals(oldValue, serialized)) {
                this.document.queueMutation(Document.MutationRecord.attributes(this, "style", oldValue));
            }
        }
        finally {
            this.syncingInlineStyle = false;
        }
    }

    public void setHover(boolean hover) {
        if (this.isHover == hover) {
            return;
        }
        this.isHover = hover;
        this.requestPseudoStyleRecalc("hover");
    }

    public void setActive(boolean active) {
        if (this.isActive == active) {
            return;
        }
        this.isActive = active;
        this.requestPseudoStyleRecalc("active");
    }

    public void setFocus(boolean value) {
        if (this.isFocus == value) {
            return;
        }
        this.isFocus = value;
        this.requestPseudoStyleRecalc("focus");
        Element ancestor = this.parentElement;
        while (ancestor != null) {
            ancestor.requestPseudoStyleRecalc("focus-within");
            ancestor = ancestor.parentElement;
        }
    }

    public boolean canFocus() {
        return this.canSelectInnerText();
    }

    public static boolean isElementFocusing(Element element) {
        if (element == null || element.document == null) {
            return false;
        }
        Element currentFocus = element.document.getFocusedElement();
        return currentFocus != null && element.uuid.equals(currentFocus.uuid);
    }

    public void setScrollLeft(double value) {
        double before = this.getTargetScrollLeft();
        this.scroll.setScrollLeft(value);
        if (this.document != null && Double.compare(before, this.getTargetScrollLeft()) != 0) {
            this.document.registerActiveScroll(this);
        }
    }

    public void setScrollTop(double value) {
        double before = this.getTargetScrollTop();
        this.scroll.setScrollTop(value);
        if (this.document != null && Double.compare(before, this.getTargetScrollTop()) != 0) {
            this.document.registerActiveScroll(this);
        }
    }

    public double getScrollLeft() {
        return this.scroll.getScrollLeft();
    }

    public double getScrollTop() {
        return this.scroll.getScrollTop();
    }

    public double getTargetScrollLeft() {
        return this.scroll.getTargetScrollLeft();
    }

    public double getTargetScrollTop() {
        return this.scroll.getTargetScrollTop();
    }

    public boolean canScroll() {
        return this.scroll.canScroll();
    }

    public boolean canScrollVertically() {
        return this.scroll.canScrollVertically();
    }

    public boolean canScrollHorizontally() {
        return this.scroll.canScrollHorizontally();
    }

    public boolean hasVerticalScrollRange() {
        return this.scroll.hasVerticalScrollRange();
    }

    public boolean hasHorizontalScrollRange() {
        return this.scroll.hasHorizontalScrollRange();
    }

    public double getVerticalScrollbarGutter() {
        return this.scroll.getVerticalScrollbarGutter();
    }

    public double getHorizontalScrollbarGutter() {
        return this.scroll.getHorizontalScrollbarGutter();
    }

    @HideFromJS
    public void commitScrollMetricsFromLayout() {
        this.scroll.commitLayoutMetrics();
    }

    public String getDefaultValue() {
        return this.attributes.getOrDefault("value", "");
    }

    public void setDefaultValue(String value) {
        String normalized = value == null ? "" : value;
        this.attributes.put("value", normalized);
        if (!this.valueDirty || this.value == null) {
            this.value = normalized;
            this.getRenderer().text.clear();
            this.getRenderer().wrappedText.clear();
        }
        this.invalidateStyle();
    }

    public String getValue() {
        if ("SELECT".equalsIgnoreCase(this.tagName)) {
            for (Element option : this.getOptionChildren()) {
                if (!option.currentSelectedness()) continue;
                return option.getOptionValue();
            }
            return "";
        }
        if ("OPTION".equalsIgnoreCase(this.tagName)) {
            return this.getOptionValue();
        }
        return this.value == null ? this.getDefaultValue() : this.value;
    }

    public void setValue(String value) {
        String normalized;
        String string = normalized = value == null ? "" : value;
        if ("SELECT".equalsIgnoreCase(this.tagName)) {
            boolean matched = false;
            for (Element option : this.getOptionChildren()) {
                boolean selected = !matched && Objects.equals(normalized, option.getOptionValue());
                option.selectedState = selected;
                option.selectedDirty = true;
                matched |= selected;
            }
            this.value = normalized;
            this.valueDirty = true;
            this.getRenderer().text.clear();
            this.getRenderer().wrappedText.clear();
            this.invalidateStyle();
            return;
        }
        this.value = normalized;
        this.valueDirty = true;
        this.getRenderer().text.clear();
        this.getRenderer().wrappedText.clear();
        this.invalidateStyle();
    }

    public String getPlaceholder() {
        String placeholder = this.getAttribute("placeholder");
        return placeholder == null ? "" : placeholder;
    }

    public void setPlaceholder(String value) {
        this.setAttribute("placeholder", value == null ? "" : value);
    }

    public String getName() {
        String name = this.getAttribute("name");
        return name == null ? "" : name;
    }

    public void setName(String value) {
        this.setAttribute("name", value == null ? "" : value);
    }

    public String getType() {
        if ("SELECT".equalsIgnoreCase(this.tagName)) {
            return this.isMultiple() ? "select-multiple" : "select-one";
        }
        String type = this.getAttribute("type");
        if (type == null || type.isBlank()) {
            if ("INPUT".equalsIgnoreCase(this.tagName)) {
                return "text";
            }
            if ("BUTTON".equalsIgnoreCase(this.tagName)) {
                return "submit";
            }
        }
        if ("INPUT".equalsIgnoreCase(this.tagName)) {
            String normalized;
            return switch (normalized = type.toLowerCase(Locale.ROOT)) {
                case "button", "checkbox", "color", "date", "datetime-local", "email", "file", "hidden", "image", "month", "number", "password", "radio", "range", "reset", "search", "submit", "tel", "text", "time", "url", "week" -> normalized;
                default -> "text";
            };
        }
        return type;
    }

    public void setType(String value) {
        this.setAttribute("type", value == null ? "" : value);
    }

    public boolean isMultiple() {
        return this.hasBooleanAttribute("multiple");
    }

    public void setMultiple(boolean multiple) {
        this.setBooleanAttribute("multiple", multiple);
    }

    public boolean isDisabled() {
        if (this.hasBooleanAttribute("disabled")) {
            return true;
        }
        if (!(this.isFormControl() || "OPTION".equalsIgnoreCase(this.tagName) || "OPTGROUP".equalsIgnoreCase(this.tagName))) {
            return false;
        }
        Element ancestor = this.parentElement;
        while (ancestor != null) {
            if ("FIELDSET".equalsIgnoreCase(ancestor.tagName) && ancestor.hasBooleanAttribute("disabled") && !this.isInsideFirstLegend(ancestor)) {
                return true;
            }
            ancestor = ancestor.parentElement;
        }
        return "OPTION".equalsIgnoreCase(this.tagName) && this.parentElement != null && "OPTGROUP".equalsIgnoreCase(this.parentElement.tagName) && this.parentElement.isDisabled();
    }

    public void setDisabled(boolean disabled) {
        this.setBooleanAttribute("disabled", disabled);
    }

    public Element getFormOwner() {
        if ("FORM".equalsIgnoreCase(this.tagName)) {
            return null;
        }
        if (!this.isFormControl()) {
            return null;
        }
        if (this.hasAttribute("form")) {
            String id = this.getAttribute("form");
            if (id == null || id.isBlank() || this.document == null) {
                return null;
            }
            Element candidate = this.document.getElementById(id.trim());
            if (candidate == null) {
                Div root = this.document.documentElement != null ? this.document.documentElement : this.document.body;
                candidate = Element.findElementById(root, id.trim());
            }
            return candidate != null && "FORM".equalsIgnoreCase(candidate.tagName) ? candidate : null;
        }
        Element current = this.parentElement;
        while (current != null) {
            if ("FORM".equalsIgnoreCase(current.tagName)) {
                return current;
            }
            current = current.parentElement;
        }
        return null;
    }

    public Element getForm() {
        return this.getFormOwner();
    }

    public boolean isFormAssociated() {
        return this.isFormControl();
    }

    public List<Element> getFormControls() {
        if (!"FORM".equalsIgnoreCase(this.tagName)) {
            return List.of();
        }
        ArrayList<Element> result = new ArrayList<Element>();
        ArrayList<Element> candidates = new ArrayList<Element>();
        if (this.document != null) {
            candidates.addAll(this.document.getElements());
        }
        ArrayList<Element> local = new ArrayList<Element>();
        ConstraintValidator.collectElements(this, local);
        for (Element candidate : local) {
            if (candidates.contains(candidate)) continue;
            candidates.add(candidate);
        }
        for (Element candidate : candidates) {
            if (candidate == null || candidate == this || !candidate.isFormControl() || candidate.getFormOwner() != this) continue;
            result.add(candidate);
        }
        return Collections.unmodifiableList(result);
    }

    private static Element findElementById(Element root, String id) {
        if (root == null || id == null) {
            return null;
        }
        if (id.equals(root.id) || id.equals(root.getAttribute("id"))) {
            return root;
        }
        for (Element child : root.children) {
            Element match = Element.findElementById(child, id);
            if (match == null) continue;
            return match;
        }
        return null;
    }

    private boolean isInsideFirstLegend(Element fieldset) {
        Element firstLegend = null;
        for (Element child : fieldset.children) {
            if (!"LEGEND".equalsIgnoreCase(child.tagName)) continue;
            firstLegend = child;
            break;
        }
        return firstLegend != null && firstLegend.contains(this);
    }

    private boolean isFormControl() {
        if (this.tagName == null) {
            return false;
        }
        return switch (this.tagName.toUpperCase(Locale.ROOT)) {
            case "INPUT", "SELECT", "TEXTAREA", "BUTTON", "OUTPUT", "FIELDSET" -> true;
            default -> false;
        };
    }

    private boolean isLabelableControl() {
        if (!this.isFormControl()) {
            return false;
        }
        return !"FIELDSET".equalsIgnoreCase(this.tagName);
    }

    public boolean isChecked() {
        return this.checkedState != null ? this.checkedState.booleanValue() : this.hasRawBooleanAttribute("checked");
    }

    public void setChecked(boolean checked) {
        boolean changed = this.isChecked() != checked;
        this.checkedState = checked;
        this.checkedDirty = true;
        if ("INPUT".equalsIgnoreCase(this.tagName) && checked && "radio".equalsIgnoreCase(this.getAttribute("type")) && this.document != null) {
            this.enforceRadioGroupChecked();
        }
        this.invalidateStyle();
        if (changed && this.document != null && this.document.documentElement != null) {
            this.document.requestStyleRecalc(this.document.documentElement);
        }
    }

    public boolean isDefaultChecked() {
        return this.hasRawBooleanAttribute("checked");
    }

    public void setDefaultChecked(boolean checked) {
        this.setRawBooleanAttribute("checked", checked);
        if (!this.checkedDirty) {
            this.checkedState = checked;
        }
        if ("INPUT".equalsIgnoreCase(this.tagName) && checked && "radio".equalsIgnoreCase(this.getAttribute("type")) && this.document != null) {
            this.enforceRadioGroupChecked();
        }
        this.invalidateStyle();
    }

    public boolean isSelected() {
        return this.currentSelectedness();
    }

    public void setSelected(boolean selected) {
        this.selectedState = selected;
        this.selectedDirty = true;
        Element select = this.getOwnerSelect();
        if (select != null) {
            if (selected && !select.isMultiple()) {
                for (Element option : select.getOptionChildren()) {
                    if (option == this) continue;
                    option.selectedState = false;
                }
            }
            select.invalidateSelectPresentation();
        }
        this.invalidateStyle();
    }

    public boolean isDefaultSelected() {
        return this.hasRawBooleanAttribute("selected");
    }

    public void setDefaultSelected(boolean selected) {
        Element select;
        this.setRawBooleanAttribute("selected", selected);
        if (!this.selectedDirty) {
            this.selectedState = selected;
        }
        if ((select = this.getOwnerSelect()) != null && !this.selectedDirty) {
            select.normalizeSelectSelection(false);
            select.invalidateSelectPresentation();
        }
        this.invalidateStyle();
    }

    public int getSelectedIndex() {
        if (!"SELECT".equalsIgnoreCase(this.tagName)) {
            return -1;
        }
        List<Element> options = this.getOptionChildren();
        for (int i = 0; i < options.size(); ++i) {
            if (!options.get(i).currentSelectedness()) continue;
            return i;
        }
        return -1;
    }

    public void setSelectedIndex(int index) {
        if (!"SELECT".equalsIgnoreCase(this.tagName)) {
            return;
        }
        List<Element> options = this.getOptionChildren();
        for (int i = 0; i < options.size(); ++i) {
            Element option = options.get(i);
            option.selectedState = i == index && index >= 0 && index < options.size();
            option.selectedDirty = true;
        }
        this.invalidateSelectPresentation();
    }

    public void drawPhase(PoseStack poseStack, Base.RenderPhase phase) {
        if (NormalFlow.isInlineTextPaintedByAncestor(this)) {
            return;
        }
        Rect rectRenderer = Rect.of(this);
        switch (phase) {
            case SHADOW: {
                rectRenderer.drawShadow(poseStack);
                break;
            }
            case BODY: {
                rectRenderer.drawBody(poseStack);
                this.drawChildTextRuns(poseStack, rectRenderer);
                this.textSelection.drawInnerTextSelection(poseStack, rectRenderer);
                this.textSelection.drawInnerText(poseStack, rectRenderer);
                break;
            }
            case BORDER: {
                rectRenderer.drawBorder(poseStack);
            }
        }
    }

    public void drawBackgroundOnly(PoseStack poseStack) {
        if (NormalFlow.isInlineTextPaintedByAncestor(this)) {
            return;
        }
        Rect.of(this).drawBody(poseStack);
    }

    public void drawContentOnly(PoseStack poseStack) {
        if (NormalFlow.isInlineTextPaintedByAncestor(this)) {
            return;
        }
        Rect rectRenderer = Rect.of(this);
        this.drawChildTextRuns(poseStack, rectRenderer);
        this.textSelection.drawInnerTextSelection(poseStack, rectRenderer);
        this.textSelection.drawInnerText(poseStack, rectRenderer);
    }

    @HideFromJS
    public void drawScrollbar(PoseStack poseStack, Rect rectRenderer) {
        this.scroll.drawScrollbar(poseStack, rectRenderer);
    }

    @HideFromJS
    public boolean mayRenderScrollbar() {
        return this.scroll.mayRenderScrollbar();
    }

    @HideFromJS
    public boolean handleScrollbarMouseDown(MouseEvent event) {
        return this.scroll.handleMouseDown(event);
    }

    @HideFromJS
    public boolean handleScrollbarMouseMove(MouseEvent event) {
        return this.scroll.handleMouseMove(event);
    }

    @HideFromJS
    public boolean handleScrollbarMouseUp(MouseEvent event) {
        return this.scroll.handleMouseUp(event);
    }

    @HideFromJS
    public boolean isScrollbarInteractionActive() {
        return this.scroll.isScrollbarInteractionActive();
    }

    protected void onInitFromDom(Element origin) {
    }

    public final void runInitFromDomOnce(Element origin) {
        if (this.domInitHookInvoked) {
            return;
        }
        this.domInitHookInvoked = true;
        String attrId = this.attributes.getOrDefault("id", null);
        if ((this.id == null || this.id.isEmpty()) && attrId != null && !attrId.isEmpty()) {
            this.id = attrId;
        }
        if (this.document != null && this.id != null && !this.id.isBlank()) {
            this.document.recordID(this);
        }
        String attrValue = this.attributes.getOrDefault("value", null);
        if (this.value == null && attrValue != null) {
            this.value = attrValue;
        }
        String attrClass = this.attributes.getOrDefault("class", null);
        if ((this.classNames == null || this.classNames.isEmpty()) && attrClass != null && !attrClass.isEmpty()) {
            this.classNames = Element.parseClassNames(attrClass);
        }
        this.onInitFromDom(origin);
        this.applyDomStateFromAttributes();
        this.installInlineEventHandlers();
    }

    private void installInlineEventHandlers() {
        if (this.inlineEventHandlersInstalled || this.attributes == null || this.attributes.isEmpty()) {
            return;
        }
        this.inlineEventHandlersInstalled = true;
        for (Map.Entry<String, String> entry : new ArrayList<Map.Entry<String, String>>(this.attributes.entrySet())) {
            String type;
            String name = entry.getKey();
            String code = entry.getValue();
            if (name == null || code == null || code.isBlank() || name.length() <= 2 || !name.startsWith("on") || (type = name.substring(2).trim().toLowerCase(Locale.ROOT)).isEmpty()) continue;
            String source = this.document == null ? "<inline-event>" : this.document.getPath() + "#" + type + "@" + this.tagName;
            this.addEventListener(type, event -> AuiServices.script().eval(code, (Event)event, source));
        }
    }

    public static void register(String tagName, BiFunction<Document, String, ? extends Element> creator) {
        if (tagName == null || creator == null) {
            return;
        }
        REGISTRY.put(tagName.toUpperCase(Locale.ROOT), creator);
    }

    public static Element init(Element origin) {
        if (!origin.getClass().equals(Element.class)) {
            origin.runInitFromDomOnce(origin);
            return origin;
        }
        BiFunction<Document, String, ? extends Element> creator = REGISTRY.get(origin.tagName);
        if (Element.hasContentEditableAttribute(origin)) {
            BiFunction<Document, String, Element> biFunction = creator = Element.isPlainTextOnly(origin) ? ContentEditable::new : RichText::new;
        }
        if (creator != null) {
            Element element = creator.apply(origin.document, origin.tagName);
            element.id = origin.id;
            element.uuid = origin.uuid;
            element.innerText = origin.innerText;
            element.attributes = origin.attributes;
            element.parentNode = origin.parentNode;
            element.parentElement = origin.parentElement;
            element.value = origin.value;
            element.classNames = origin.classNames;
            origin.childNodes.forEach(node -> {
                node.parentNode = element;
                if (node instanceof Element) {
                    Element childElement = (Element)node;
                    childElement.parentElement = element;
                }
            });
            element.childNodes.addAll(origin.childNodes);
            element.children = new ArrayList<Element>(origin.children);
            element.updateInlineStyle();
            for (Event.ListenerRecord eventListener : origin.EventListener) {
                if (eventListener.internal()) continue;
                element.EventListener.add(eventListener);
            }
            origin.document.updateElement(element);
            element.runInitFromDomOnce(origin);
            return element;
        }
        origin.runInitFromDomOnce(origin);
        return origin;
    }

    private static boolean hasContentEditableAttribute(Element origin) {
        return origin.attributes != null && origin.attributes.containsKey("contenteditable");
    }

    private static boolean isPlainTextOnly(Element origin) {
        String value = origin.attributes == null ? null : origin.attributes.get("contenteditable");
        return value != null && "plaintext-only".equalsIgnoreCase(value.trim());
    }

    public List<Element> querySelectorAll(String selector) {
        return this.node.querySelectorAll(selector);
    }

    public Element querySelector(String selector) {
        return this.node.querySelector(selector);
    }

    public void prepend(Element element) {
        this.node.prepend(element);
    }

    public void append(Element element) {
        this.node.append(element);
    }

    public Element appendChild(Element element) {
        return this.node.appendChild(element);
    }

    public void replaceChildren(Node ... children) {
        this.clearChildren();
        if (children != null) {
            for (Node child : children) {
                if (child == null) continue;
                this.appendChild(child);
            }
        }
    }

    public Element removeChild(Element element) {
        return this.node.removeChild(element);
    }

    public Element insertBefore(Element newElement, Element referenceElement) {
        return this.node.insertBefore(newElement, referenceElement);
    }

    public Element replaceChild(Element newElement, Element oldElement) {
        return this.node.replaceChild(newElement, oldElement);
    }

    public Element getFirstElementChild() {
        return this.children.isEmpty() ? null : this.children.get(0);
    }

    public Element getLastElementChild() {
        return this.children.isEmpty() ? null : this.children.get(this.children.size() - 1);
    }

    public int getChildElementCount() {
        return this.children.size();
    }

    @Override
    public boolean hasChildNodes() {
        return !this.childNodes.isEmpty();
    }

    public List<Element> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    public List<Element> getRenderChildren() {
        if ("SELECT".equalsIgnoreCase(this.tagName)) {
            return List.of();
        }
        if (this.children.isEmpty() && !this.hasGeneratedPseudoElement(Selector.PseudoElement.BEFORE) && !this.hasGeneratedPseudoElement(Selector.PseudoElement.AFTER)) {
            return this.children;
        }
        ArrayList<Element> result = new ArrayList<Element>(this.children.size() + 2);
        Element before = this.getGeneratedPseudoElement(Selector.PseudoElement.BEFORE);
        if (before != null) {
            result.add(before);
        }
        result.addAll(this.children);
        Element after = this.getGeneratedPseudoElement(Selector.PseudoElement.AFTER);
        if (after != null) {
            result.add(after);
        }
        return result;
    }

    public List<Element> getExistingLayoutChildren() {
        if (this.beforePseudoElement == null && this.afterPseudoElement == null) {
            return this.children;
        }
        ArrayList<Element> result = new ArrayList<Element>(this.children.size() + 2);
        if (this.beforePseudoElement != null) {
            result.add(this.beforePseudoElement);
        }
        result.addAll(this.children);
        if (this.afterPseudoElement != null) {
            result.add(this.afterPseudoElement);
        }
        return result;
    }

    public List<Node> getRenderChildNodes() {
        TextNode legacyText;
        if ("SELECT".equalsIgnoreCase(this.tagName)) {
            return List.of();
        }
        Element before = this.getGeneratedPseudoElement(Selector.PseudoElement.BEFORE);
        Element after = this.getGeneratedPseudoElement(Selector.PseudoElement.AFTER);
        if (this.childNodes.isEmpty() && before == null && after == null) {
            this.renderChildNodesCache = null;
            return this.childNodes;
        }
        boolean includeLegacyText = this.childNodes.isEmpty() && this.innerText != null && !this.innerText.isEmpty();
        TextNode textNode = legacyText = includeLegacyText ? this.getLegacyRenderTextNode() : null;
        if (this.matchesRenderChildNodesCache(before, legacyText, after)) {
            return this.renderChildNodesCache;
        }
        ArrayList<Node> result = new ArrayList<Node>(this.childNodes.size() + (includeLegacyText ? 3 : 2));
        if (before != null) {
            result.add(before);
        }
        if (legacyText != null) {
            result.add(legacyText);
        }
        result.addAll(this.childNodes);
        if (after != null) {
            result.add(after);
        }
        this.renderChildNodesCache = Collections.unmodifiableList(result);
        return this.renderChildNodesCache;
    }

    private boolean matchesRenderChildNodesCache(Element before, TextNode legacyText, Element after) {
        if (this.renderChildNodesCache == null) {
            return false;
        }
        int expectedSize = this.childNodes.size() + (before == null ? 0 : 1) + (legacyText == null ? 0 : 1) + (after == null ? 0 : 1);
        if (this.renderChildNodesCache.size() != expectedSize) {
            return false;
        }
        int index = 0;
        if (before != null && this.renderChildNodesCache.get(index++) != before) {
            return false;
        }
        if (legacyText != null && this.renderChildNodesCache.get(index++) != legacyText) {
            return false;
        }
        for (Node child : this.childNodes) {
            if (this.renderChildNodesCache.get(index++) == child) continue;
            return false;
        }
        return after == null || this.renderChildNodesCache.get(index) == after;
    }

    private TextNode getLegacyRenderTextNode() {
        if (this.legacyRenderTextNode == null || this.legacyRenderTextNode.document != this.document || !Objects.equals(this.legacyRenderTextNode.getTextContent(), this.innerText)) {
            this.legacyRenderTextNode = new TextNode(this.document, this.innerText);
        }
        this.legacyRenderTextNode.parentNode = this;
        this.legacyRenderTextNode.depth = this.depth + 1;
        return this.legacyRenderTextNode;
    }

    public boolean isPseudoElement() {
        return this.pseudoElement;
    }

    public Element getPseudoElementHost() {
        return this.pseudoElementHost;
    }

    private boolean hasGeneratedPseudoElement(Selector.PseudoElement kind) {
        return this.getGeneratedPseudoElement(kind) != null;
    }

    private void syncGeneratedPseudoElementsForStyleRecalc() {
        boolean hasAfter;
        if (this.pseudoElement) {
            return;
        }
        boolean hadBefore = this.beforePseudoElement != null && this.beforePseudoElement.wasPseudoContentGenerated();
        boolean hadAfter = this.afterPseudoElement != null && this.afterPseudoElement.wasPseudoContentGenerated();
        boolean hasBefore = this.getGeneratedPseudoElement(Selector.PseudoElement.BEFORE) != null;
        boolean bl = hasAfter = this.getGeneratedPseudoElement(Selector.PseudoElement.AFTER) != null;
        if ((hadBefore != hasBefore || hadAfter != hasAfter) && this.document != null) {
            this.invalidatePseudoElementHostLayout();
        }
    }

    private Element getGeneratedPseudoElement(Selector.PseudoElement kind) {
        Element pseudo;
        if (kind == null || this.pseudoElement) {
            return null;
        }
        HashMap<String, CSS.Declaration> styles = this.resolvePseudoElementStyles(kind);
        CSS.Declaration content = styles == null ? null : styles.get("content");
        if (!CssString.isGeneratedPseudoContent(content == null ? null : content.value())) {
            return null;
        }
        Element element = pseudo = kind == Selector.PseudoElement.BEFORE ? this.beforePseudoElement : this.afterPseudoElement;
        if (pseudo == null) {
            pseudo = this.createPseudoElement(kind);
            if (kind == Selector.PseudoElement.BEFORE) {
                this.beforePseudoElement = pseudo;
            } else {
                this.afterPseudoElement = pseudo;
            }
        }
        pseudo.syncPseudoElement(styles);
        return pseudo.isPseudoContentGenerated() ? pseudo : null;
    }

    private Element createPseudoElement(Selector.PseudoElement kind) {
        Element pseudo = new Element(this.document, kind == Selector.PseudoElement.BEFORE ? "::before" : "::after");
        pseudo.pseudoElement = true;
        pseudo.pseudoElementKind = kind;
        pseudo.pseudoElementHost = this;
        pseudo.parentNode = this;
        pseudo.parentElement = this;
        pseudo.depth = this.depth + 1;
        pseudo.isPointerEnabled = false;
        return pseudo;
    }

    private void syncPseudoElement(HashMap<String, CSS.Declaration> styles) {
        if (!this.pseudoElement || this.pseudoElementHost == null) {
            return;
        }
        this.document = this.pseudoElementHost.document;
        this.parentNode = this.pseudoElementHost;
        this.parentElement = this.pseudoElementHost;
        this.depth = this.pseudoElementHost.depth + 1;
        if (Element.samePseudoStyles(this.cssCache, styles) && this.renderElement.computedStyle.get() != null) {
            this.isPointerEnabled = false;
            return;
        }
        Style originStyle = this.pseudoElementPreviousStyle;
        if (originStyle == null) {
            Style cached = this.renderElement.computedStyle.get();
            originStyle = cached == null ? this.getRawComputedStyle() : cached;
        }
        this.cssCache = styles == null ? new HashMap() : new HashMap<String, CSS.Declaration>(styles);
        this.getRenderer().computedStyle.clear();
        Style style = this.getRawComputedStyle();
        if (this.document != null) {
            this.document.setHasAnimationSpec(this, Animation.hasAnimationSpec(style));
        }
        RenderElement.observeStyle(this, originStyle, style);
        Transition.create(this, originStyle, style);
        this.pseudoElementPreviousStyle = style.clone();
        this.innerText = CssString.parsePseudoContentText(style.content);
        if (this.document != null) {
            this.document.bumpSelectionCache();
        }
        this.isPointerEnabled = false;
        this.invalidatePseudoElementHostLayout();
    }

    private void invalidatePseudoElementHostLayout() {
        Element host;
        Element element = host = this.pseudoElement ? this.pseudoElementHost : this;
        if (host == null) {
            return;
        }
        host.getRenderer().invalidateLayoutSubtree();
        if (host.document != null) {
            host.document.markDirty(host, 7);
        }
    }

    private static boolean samePseudoStyles(HashMap<String, CSS.Declaration> current, HashMap<String, CSS.Declaration> next) {
        if (current == null || current.isEmpty()) {
            return next == null || next.isEmpty();
        }
        return current.equals(next);
    }

    private boolean isPseudoContentGenerated() {
        if (!this.pseudoElement) {
            return false;
        }
        Style style = this.getRawComputedStyle();
        return CssString.isGeneratedPseudoContent(style.content);
    }

    private boolean wasPseudoContentGenerated() {
        if (!this.pseudoElement) {
            return false;
        }
        Style previous = this.pseudoElementPreviousStyle;
        return previous != null && CssString.isGeneratedPseudoContent(previous.content);
    }

    private HashMap<String, CSS.Declaration> resolvePseudoElementStyles(Selector.PseudoElement kind) {
        if (kind == Selector.PseudoElement.BEFORE) {
            if (!this.beforePseudoResolved) {
                this.beforePseudoStyles = Selector.matchPseudoElementCSS(this, kind);
                this.beforePseudoResolved = true;
            }
            return this.beforePseudoStyles;
        }
        if (!this.afterPseudoResolved) {
            this.afterPseudoStyles = Selector.matchPseudoElementCSS(this, kind);
            this.afterPseudoResolved = true;
        }
        return this.afterPseudoStyles;
    }

    private void clearPseudoElementCaches() {
        this.beforePseudoResolved = false;
        this.afterPseudoResolved = false;
        this.beforePseudoStyles = null;
        this.afterPseudoStyles = null;
        if (this.beforePseudoElement != null) {
            this.beforePseudoElement.clearPseudoElementSelfCaches();
        }
        if (this.afterPseudoElement != null) {
            this.afterPseudoElement.clearPseudoElementSelfCaches();
        }
    }

    private void clearPseudoElementSelfCaches() {
        Style cachedStyle = this.renderElement.computedStyle.get();
        if (cachedStyle != null) {
            this.pseudoElementPreviousStyle = cachedStyle.clone();
        }
        this.cssCache.clear();
        this.renderElement.computedStyle.clear();
        this.renderElement.text.clear();
        this.renderElement.wrappedText.clear();
        this.renderElement.size.clear();
        this.renderElement.box.clear();
        this.renderElement.position.clear();
        StyleFrameCache.invalidate(this);
        if (this.document != null) {
            this.document.bumpSelectionCache();
        }
    }

    @Override
    public List<Node> getChildNodes() {
        return super.getChildNodes();
    }

    public List<Element> getOptions() {
        if (!"SELECT".equalsIgnoreCase(this.tagName)) {
            return List.of();
        }
        return Collections.unmodifiableList(this.getOptionChildren());
    }

    public List<Element> getSelectedOptions() {
        if (!"SELECT".equalsIgnoreCase(this.tagName)) {
            return List.of();
        }
        ArrayList<Element> selected = new ArrayList<Element>();
        for (Element option : this.getOptions()) {
            if (option == null || !option.isSelected()) continue;
            selected.add(option);
        }
        return Collections.unmodifiableList(selected);
    }

    public Element getNextElementSibling() {
        if (this.parentElement == null) {
            return null;
        }
        int index = this.parentElement.children.indexOf(this);
        if (index < 0 || index + 1 >= this.parentElement.children.size()) {
            return null;
        }
        return this.parentElement.children.get(index + 1);
    }

    public Element getPreviousElementSibling() {
        if (this.parentElement == null) {
            return null;
        }
        int index = this.parentElement.children.indexOf(this);
        if (index <= 0) {
            return null;
        }
        return this.parentElement.children.get(index - 1);
    }

    @Override
    public Node getParentNode() {
        return this.parentNode;
    }

    @Override
    public short getNodeType() {
        return 1;
    }

    @Override
    public String getNodeName() {
        return this.tagName;
    }

    @Override
    public String getTextContent() {
        if (this.childNodes.isEmpty()) {
            return this.innerText;
        }
        StringBuilder builder = new StringBuilder();
        for (Node child : this.childNodes) {
            String text;
            if (child == null || (text = child.getTextContent()) == null) continue;
            builder.append(text);
        }
        return builder.toString();
    }

    public String getInnerText() {
        return InnerText.get(this);
    }

    public void setInnerText(String value) {
        InnerText.set(this, value);
    }

    @Override
    public void setTextContent(String value) {
        String normalized;
        String oldValue = this.getTextContent();
        String string = normalized = value == null ? "" : ConstraintText.normalizeNumericText(value);
        if (!this.childNodes.isEmpty()) {
            ArrayList snapshot = new ArrayList(this.childNodes);
            for (Node child : snapshot) {
                this.removeChild(child);
            }
        }
        this.innerText = normalized;
        if (this.document != null) {
            this.document.bumpSelectionCache();
        }
        this.legacyRenderTextNode = null;
        this.getRenderer().text.clear();
        this.getRenderer().wrappedText.clear();
        this.getRenderer().size.clear();
        if (this.document != null && !Objects.equals(oldValue, normalized)) {
            this.getRenderer().invalidateLayoutSubtree();
            this.forEachRoute(element -> {
                RenderElement renderer = element.getRenderer();
                renderer.invalidateLayoutVersion();
                renderer.size.clear();
                renderer.box.clear();
            });
            if (this.parentElement != null) {
                this.parentElement.children.forEach(sibling -> sibling.getRenderer().position.clear());
            }
            this.document.markDirty(this, 15);
            this.document.queueMutation(Document.MutationRecord.characterData(this, oldValue));
        }
    }

    @Override
    public Element cloneNode(boolean deep) {
        this.syncLegacyInlineStyleMutations();
        Element cloned = Element.init(new Element(this.document, this.tagName));
        cloned.innerText = this.innerText;
        cloned.id = this.id;
        cloned.value = this.value;
        cloned.checkedState = this.checkedState;
        cloned.checkedDirty = this.checkedDirty;
        cloned.selectedState = this.selectedState;
        cloned.selectedDirty = this.selectedDirty;
        cloned.valueDirty = this.valueDirty;
        cloned.attributes.putAll(this.attributes);
        cloned.updateInlineStyle();
        Set<Object> set = cloned.classNames = this.classNames == null ? Collections.emptySet() : this.classNames;
        if (deep) {
            for (Node child : this.childNodes) {
                Node copy = child.cloneNode(true);
                if (copy == null) continue;
                cloned.appendChild(copy);
            }
        }
        return cloned;
    }

    public String getInnerHTML() {
        if (this.childNodes.isEmpty()) {
            return HtmlSerializer.escapeHtml(this.innerText);
        }
        StringBuilder builder = new StringBuilder();
        for (Node child : this.childNodes) {
            if (child == null) continue;
            builder.append(HtmlSerializer.serializeNode(child));
        }
        return builder.toString();
    }

    public void setInnerHTML(String html) {
        ArrayList snapshot = new ArrayList(this.childNodes);
        for (Node child : snapshot) {
            this.removeChild(child);
        }
        this.innerText = "";
        if (this.document != null) {
            this.document.bumpSelectionCache();
        }
        if (this.document == null || html == null || html.isEmpty()) {
            return;
        }
        Element wrapper = this.document.createHTML("<div>" + html + "</div>");
        if (wrapper == null) {
            return;
        }
        ArrayList newChildren = new ArrayList(wrapper.childNodes);
        for (Node child : newChildren) {
            this.appendChild(child);
        }
    }

    public String getOuterHTML() {
        return HtmlSerializer.serializeNode(this);
    }

    public void setOuterHTML(String html) {
        if (this.document == null) {
            return;
        }
        if (this.parentElement == null) {
            this.setInnerHTML(html);
            return;
        }
        Element wrapper = this.document.createHTML("<div>" + (html == null ? "" : html) + "</div>");
        if (wrapper == null) {
            this.remove();
            return;
        }
        ArrayList replacements = new ArrayList(wrapper.childNodes);
        Element insertionParent = this.parentElement;
        Element anchor = this;
        for (Node replacement : replacements) {
            insertionParent.insertBefore(replacement, (Node)anchor);
        }
        this.remove();
    }

    public String getClassName() {
        String className = this.getAttribute("class");
        return className == null ? "" : className;
    }

    public void setClassName(String value) {
        this.setAttribute("class", value == null ? "" : value);
    }

    public DOMTokenList getClassList() {
        return this.classList;
    }

    public DOMStringMap getDataset() {
        return this.dataset;
    }

    public boolean matches(String selector) {
        return Selector.matches(this, selector);
    }

    public Element closest(String selector) {
        Element current = this;
        while (current != null) {
            if (current.matches(selector)) {
                return current;
            }
            current = current.parentElement;
        }
        return null;
    }

    public boolean contains(Element element) {
        Element current = element;
        while (current != null) {
            if (current == this) {
                return true;
            }
            current = current.parentElement;
        }
        return false;
    }

    public List<Element> getElementsByClassName(String className) {
        String normalized;
        String string = normalized = className == null ? "" : className.trim();
        if (normalized.isEmpty()) {
            return List.of();
        }
        String selector = "." + String.join((CharSequence)".", normalized.split("\\s+"));
        return this.querySelectorAll(selector);
    }

    public List<Element> getElementsByTagName(String tagName) {
        String normalized;
        String string = normalized = tagName == null ? "" : tagName.trim();
        if (normalized.isEmpty()) {
            return List.of();
        }
        return this.querySelectorAll(normalized);
    }

    public List<Element> getElementsByName(String name) {
        String normalized;
        String string = normalized = name == null ? "" : name.trim();
        if (normalized.isEmpty()) {
            return List.of();
        }
        return this.querySelectorAll("[name=\"" + normalized + "\"]");
    }

    public void focus() {
        if (this.document == null || this.isDisabled()) {
            return;
        }
        if ("INPUT".equalsIgnoreCase(this.tagName) && !this.canFocus()) {
            return;
        }
        this.document.setFocusedElement(this);
    }

    public void blur() {
        if (this.document == null) {
            return;
        }
        if (this.document.getFocusedElement() == this) {
            this.document.clearFocus();
        }
    }

    public boolean submit() {
        if (!"FORM".equalsIgnoreCase(this.tagName)) {
            return false;
        }
        return this.dispatchSubmitEvent(null);
    }

    public boolean requestSubmit() {
        return this.requestSubmit(null);
    }

    public boolean requestSubmit(Element submitter) {
        boolean skipValidation;
        if (!"FORM".equalsIgnoreCase(this.tagName)) {
            return false;
        }
        if (submitter != null && (!ConstraintText.isSubmitButton(submitter) || submitter.getFormOwner() != this || submitter.isDisabled())) {
            return false;
        }
        boolean bl = skipValidation = this.hasAttribute("novalidate") || submitter != null && submitter.hasAttribute("formnovalidate");
        if (!skipValidation && !this.checkValidity()) {
            return false;
        }
        return this.dispatchSubmitEvent(submitter);
    }

    private boolean dispatchSubmitEvent(Element submitter) {
        Event event = new Event(this, "submit", null, false);
        event.bubbles = true;
        event.cancelable = true;
        event.submitter = submitter;
        Event.tiggerEvent(event);
        if (event.defaultPrevented) {
            return false;
        }
        Event formDataEvent = new Event(this, "formdata", false);
        formDataEvent.formData = this.getFormData(submitter);
        Event.tiggerEvent(formDataEvent);
        return true;
    }

    public boolean reset() {
        if (!"FORM".equalsIgnoreCase(this.tagName)) {
            return false;
        }
        Event event = new Event(this, "reset", null, false);
        event.bubbles = true;
        event.cancelable = true;
        Event.tiggerEvent(event);
        if (event.defaultPrevented) {
            return false;
        }
        List<Element> controls = this.getFormControls();
        for (Element control : controls) {
            control.resetFormControl();
        }
        for (Element control : controls) {
            if (!"INPUT".equalsIgnoreCase(control.tagName) || !"radio".equals(ConstraintText.normalizedInputType(control)) || !control.isChecked()) continue;
            control.enforceRadioGroupChecked();
        }
        return true;
    }

    public List<FormDataEntry> getFormDataEntries() {
        return this.getFormDataEntries(null);
    }

    public List<FormDataEntry> getFormDataEntries(Element submitter) {
        if (!"FORM".equalsIgnoreCase(this.tagName)) {
            return List.of();
        }
        ArrayList<FormDataEntry> entries = new ArrayList<FormDataEntry>();
        for (Element control : this.getFormControls()) {
            Element.appendFormDataEntries(entries, control, submitter);
        }
        if (submitter != null && submitter.getFormOwner() == this && !this.getFormControls().contains(submitter)) {
            Element.appendFormDataEntries(entries, submitter, submitter);
        }
        return Collections.unmodifiableList(entries);
    }

    public FormData getFormData() {
        return new FormData(this.getFormDataEntries());
    }

    public FormData getFormData(Element submitter) {
        return new FormData(this.getFormDataEntries(submitter));
    }

    private static void appendFormDataEntries(List<FormDataEntry> entries, Element control, Element submitter) {
        String tag;
        if (control == null || !control.hasAttribute("name") || control.isDisabled()) {
            return;
        }
        String name = control.getAttribute("name");
        String string = tag = control.tagName == null ? "" : control.tagName.toUpperCase(Locale.ROOT);
        if ("OUTPUT".equals(tag) || "FIELDSET".equals(tag)) {
            return;
        }
        if ("SELECT".equals(tag)) {
            for (Element option : control.getSelectedOptions()) {
                if (option.isOptionEffectivelyDisabled()) continue;
                entries.add(new FormDataEntry(name, option.getOptionValue()));
            }
            return;
        }
        if ("INPUT".equals(tag)) {
            String type = ConstraintText.normalizedInputType(control);
            if ("checkbox".equals(type) || "radio".equals(type)) {
                if (!control.isChecked()) {
                    return;
                }
                entries.add(new FormDataEntry(name, control.hasAttribute("value") ? control.getValue() : "on"));
                return;
            }
            if ("submit".equals(type) || "image".equals(type)) {
                if (control != submitter) {
                    return;
                }
                if ("image".equals(type)) {
                    entries.add(new FormDataEntry(name + ".x", "0"));
                    entries.add(new FormDataEntry(name + ".y", "0"));
                    return;
                }
            }
            if ("button".equals(type) || "reset".equals(type)) {
                return;
            }
            if ("file".equals(type)) {
                List<String> files = control.getFileList();
                if (files.isEmpty()) {
                    entries.add(new FormDataEntry(name, "", ""));
                } else {
                    for (String file : files) {
                        entries.add(new FormDataEntry(name, file, ConstraintText.fileName(file)));
                    }
                }
                return;
            }
        } else if ("BUTTON".equals(tag)) {
            String type = control.getAttribute("type");
            if (type == null || type.isBlank()) {
                type = "submit";
            }
            if (!"submit".equalsIgnoreCase(type) && !"image".equalsIgnoreCase(type)) {
                return;
            }
            if (control != submitter) {
                return;
            }
        }
        entries.add(new FormDataEntry(name, control.getValue()));
    }

    public boolean isWillValidate() {
        String type;
        if (!this.isFormControl() || this.isDisabled()) {
            return false;
        }
        if ("OUTPUT".equalsIgnoreCase(this.tagName) || "FIELDSET".equalsIgnoreCase(this.tagName)) {
            return false;
        }
        if ("INPUT".equalsIgnoreCase(this.tagName) && ("hidden".equals(type = ConstraintText.normalizedInputType(this)) || "button".equals(type) || "reset".equals(type) || "submit".equals(type) || "image".equals(type))) {
            return false;
        }
        if ("BUTTON".equalsIgnoreCase(this.tagName)) {
            return false;
        }
        return !"INPUT".equalsIgnoreCase(this.tagName) && !"TEXTAREA".equalsIgnoreCase(this.tagName) || !this.hasAttribute("readonly");
    }

    public ValidityState getValidity() {
        ConstraintValidator.ValidationResult result;
        if ("FORM".equalsIgnoreCase(this.tagName)) {
            result = new ConstraintValidator.ValidationResult();
            for (Element control : this.getFormControls()) {
                result.merge(ConstraintValidator.state(control, control.customValidityMessage));
            }
        } else {
            result = ConstraintValidator.state(this, this.customValidityMessage);
        }
        return result.toState();
    }

    public boolean isValid() {
        return this.getValidity().valid;
    }

    public boolean checkValidity() {
        if ("FORM".equalsIgnoreCase(this.tagName)) {
            boolean valid = true;
            for (Element control : this.getFormControls()) {
                if (control.checkValidity()) continue;
                valid = false;
            }
            return valid;
        }
        if (!this.isWillValidate()) {
            return true;
        }
        if (!this.isValid()) {
            Event invalid = new Event(this, "invalid", false);
            invalid.cancelable = true;
            Event.tiggerEvent(invalid);
            return false;
        }
        return true;
    }

    public boolean reportValidity() {
        return this.checkValidity();
    }

    public void setCustomValidity(String message) {
        this.customValidityMessage = message == null ? "" : message;
        this.invalidateStyle();
    }

    public String getValidationMessage() {
        if (!this.isWillValidate() || this.isValid()) {
            return "";
        }
        if (!this.customValidityMessage.isBlank()) {
            return this.customValidityMessage;
        }
        ValidityState state = this.getValidity();
        if (state.valueMissing) {
            return "Please fill out this field.";
        }
        if (state.typeMismatch) {
            return "Please enter a valid value.";
        }
        if (state.badInput) {
            return "Please enter a number.";
        }
        if (state.rangeUnderflow) {
            return "Value is too small.";
        }
        if (state.rangeOverflow) {
            return "Value is too large.";
        }
        if (state.stepMismatch) {
            return "Please enter a valid value.";
        }
        if (state.patternMismatch) {
            return "Please match the requested format.";
        }
        if (state.tooShort) {
            return "Value is too short.";
        }
        if (state.tooLong) {
            return "Value is too long.";
        }
        return "Please enter a valid value.";
    }

    public double getValueAsNumber() {
        Double parsed = ConstraintText.parseConstraintNumber(ConstraintText.normalizedInputType(this), this.getValue());
        if (parsed == null) {
            return Double.NaN;
        }
        return switch (ConstraintText.normalizedInputType(this)) {
            case "date" -> parsed * 8.64E7;
            case "datetime-local" -> parsed * 1000.0;
            case "time" -> parsed * 1000.0;
            case "week" -> parsed * 8.64E7;
            case "month" -> {
                long monthIndex = Math.round(parsed);
                int year = (int)Math.floorDiv(monthIndex, 12);
                int month = Math.floorMod(monthIndex, 12) + 1;
                yield YearMonth.of(year, month).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
            }
            default -> parsed;
        };
    }

    public void setValueAsNumber(double number) {
        double internal;
        String type;
        if (!Double.isFinite(number)) {
            this.setValue("");
            return;
        }
        switch (type = ConstraintText.normalizedInputType(this)) {
            case "date": 
            case "week": {
                double d = number / 8.64E7;
                break;
            }
            case "datetime-local": {
                double d = number / 1000.0;
                break;
            }
            case "time": {
                double d = number / 1000.0;
                break;
            }
            default: {
                double d = internal = number;
            }
        }
        if ("date".equals(type)) {
            this.setValue(LocalDate.ofEpochDay(Math.round(internal)).toString());
        } else if ("time".equals(type)) {
            this.setValue(ConstraintText.formatTime(internal));
        } else if ("datetime-local".equals(type)) {
            this.setValue(Instant.ofEpochMilli(Math.round(number)).atZone(ZoneOffset.UTC).toLocalDateTime().toString());
        } else if ("month".equals(type)) {
            Instant instant = Instant.ofEpochMilli(Math.round(number));
            this.setValue(YearMonth.from(instant.atZone(ZoneOffset.UTC)).toString());
        } else if ("week".equals(type)) {
            LocalDate date = LocalDate.ofEpochDay(Math.round(internal));
            this.setValue(String.format(Locale.ROOT, "%04d-W%02d", date.get(IsoFields.WEEK_BASED_YEAR), date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
        } else {
            this.setValue(ConstraintText.serializeNumberValue(number));
        }
    }

    public void stepUp() {
        this.stepBy(1);
    }

    public void stepUp(int count) {
        this.stepBy(Math.max(0, count));
    }

    public void stepDown() {
        this.stepBy(-1);
    }

    public void stepDown(int count) {
        this.stepBy(-Math.max(0, count));
    }

    private void stepBy(int count) {
        double step;
        double current;
        String type = ConstraintText.normalizedInputType(this);
        if (!ConstraintText.isNumericType(type)) {
            return;
        }
        Double currentValue = ConstraintText.parseConstraintNumber(type, this.getValue());
        Double minimum = ConstraintText.parseConstraintNumber(type, this.getAttribute("min"));
        double d = currentValue == null ? (minimum == null ? 0.0 : minimum) : (current = currentValue.doubleValue());
        double d2 = ConstraintText.parseConstraintNumber("number", this.getAttribute("step")) == null ? ("time".equals(type) || "datetime-local".equals(type) ? 60.0 : ("week".equals(type) ? 7.0 : 1.0)) : (step = ConstraintText.parseConstraintNumber("number", this.getAttribute("step")).doubleValue());
        if (step <= 0.0 || !Double.isFinite(step)) {
            return;
        }
        double next = current + (double)count * step;
        double exposed = switch (type) {
            case "date", "week" -> next * 8.64E7;
            case "datetime-local" -> next * 1000.0;
            case "time" -> next * 1000.0;
            case "month" -> {
                int year = (int)Math.floorDiv(Math.round(next), 12);
                int month = Math.floorMod(Math.round(next), 12) + 1;
                yield YearMonth.of(year, month).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
            }
            default -> next;
        };
        this.setValueAsNumber(exposed);
    }

    private void resetFormControl() {
        if ("INPUT".equalsIgnoreCase(this.tagName) || "TEXTAREA".equalsIgnoreCase(this.tagName)) {
            this.restoreFormValue(this.getDefaultValue());
        }
        if ("INPUT".equalsIgnoreCase(this.tagName) && "file".equals(ConstraintText.normalizedInputType(this))) {
            this.fileList.clear();
            this.value = "";
            this.valueDirty = false;
        }
        if ("INPUT".equalsIgnoreCase(this.tagName) && ("checkbox".equals(ConstraintText.normalizedInputType(this)) || "radio".equals(ConstraintText.normalizedInputType(this)))) {
            this.checkedState = this.isDefaultChecked();
            this.checkedDirty = false;
            this.invalidateStyle();
        }
        if ("OPTION".equalsIgnoreCase(this.tagName)) {
            this.selectedState = this.isDefaultSelected();
            this.selectedDirty = false;
            this.invalidateStyle();
        }
        if ("SELECT".equalsIgnoreCase(this.tagName)) {
            for (Element option : this.getOptionChildren()) {
                option.selectedState = option.isDefaultSelected();
                option.selectedDirty = false;
            }
            this.normalizeSelectSelection(true);
            this.invalidateSelectPresentation();
        }
    }

    protected void restoreFormValue(String restored) {
        this.value = restored == null ? "" : restored;
        this.valueDirty = false;
        this.getRenderer().text.clear();
        this.getRenderer().wrappedText.clear();
        this.invalidateStyle();
    }

    public List<String> getFileList() {
        return Collections.unmodifiableList(this.fileList);
    }

    public int getFileCount() {
        return this.fileList.size();
    }

    public String getFile(int index) {
        return index >= 0 && index < this.fileList.size() ? this.fileList.get(index) : "";
    }

    public void setFileList(List<String> files) {
        this.fileList.clear();
        if (files != null) {
            for (String file : files) {
                if (file == null || file.isBlank()) continue;
                this.fileList.add(file);
            }
        }
        if (!this.isMultiple() && this.fileList.size() > 1) {
            this.fileList.subList(1, this.fileList.size()).clear();
        }
        if ("INPUT".equalsIgnoreCase(this.tagName) && "file".equals(ConstraintText.normalizedInputType(this))) {
            this.value = this.fileList.isEmpty() ? "" : this.fileList.get(0);
            this.valueDirty = true;
            this.getRenderer().text.clear();
        }
    }

    public void scrollTo(double x, double y) {
        double beforeLeft = this.getTargetScrollLeft();
        double beforeTop = this.getTargetScrollTop();
        this.setScrollLeft(x);
        this.setScrollTop(y);
        this.dispatchScrollEventIfChanged(beforeLeft, beforeTop);
    }

    public void scrollBy(double x, double y) {
        double beforeLeft = this.getTargetScrollLeft();
        double beforeTop = this.getTargetScrollTop();
        this.setScrollLeft(this.getTargetScrollLeft() + x);
        this.setScrollTop(this.getTargetScrollTop() + y);
        this.dispatchScrollEventIfChanged(beforeLeft, beforeTop);
    }

    public DOMRect getBoundingClientRect() {
        try (GeometryQueryScope geometryScope = GeometryQueryScope.open();){
            Rect rect = Rect.of(this);
            Box box = rect.box;
            double x = rect.position.x + box.getMarginLeft();
            double y = rect.position.y + box.getMarginTop();
            Size elementSize = rect.getElementSize();
            double width = elementSize.width();
            double height = elementSize.height();
            DOMRect dOMRect = new DOMRect(x, y, width, height);
            return dOMRect;
        }
    }

    public void before(Element element) {
        if (this.parentElement == null || element == null) {
            return;
        }
        this.parentElement.insertBefore(element, this);
    }

    public void after(Element element) {
        if (this.parentElement == null || element == null) {
            return;
        }
        Element nextSibling = this.getNextElementSibling();
        this.parentElement.insertBefore(element, nextSibling);
    }

    public void replaceWith(Element element) {
        if (this.parentElement == null || element == null) {
            return;
        }
        this.parentElement.replaceChild(element, this);
    }

    @Override
    public boolean dispatchEvent(Object event) {
        if (!(event instanceof Event)) {
            return false;
        }
        Event targetEvent = (Event)event;
        if (targetEvent.target == null) {
            targetEvent.target = this;
        }
        if (targetEvent.currentTarget == null) {
            targetEvent.currentTarget = this;
        }
        Event.tiggerEvent(targetEvent);
        return !targetEvent.defaultPrevented;
    }

    public void click() {
        Element activationTarget;
        if (this.isDisabled()) {
            return;
        }
        Event clickEvent = new Event(this, "click", null, false);
        clickEvent.cancelable = true;
        Event.tiggerEvent(clickEvent);
        if (!clickEvent.defaultPrevented && (activationTarget = this.resolveClickActivationTarget()) != null && !activationTarget.isDisabled()) {
            activationTarget.handleClickDefault();
        }
    }

    public Element resolveClickActivationTarget() {
        Element current = this;
        while (current != null) {
            if (current.hasClickActivationBehavior()) {
                return current;
            }
            current = current.parentElement;
        }
        return null;
    }

    protected boolean hasClickActivationBehavior() {
        if (this.tagName == null) {
            return false;
        }
        return switch (this.tagName.trim().toUpperCase()) {
            case "LABEL", "INPUT", "SELECT", "BUTTON" -> true;
            default -> false;
        };
    }

    public void handleClickDefault() {
        if (this.document == null) {
            return;
        }
        if ("BUTTON".equalsIgnoreCase(this.tagName)) {
            Element form;
            String type = this.getAttribute("type");
            if (type == null || type.isBlank() || "submit".equalsIgnoreCase(type)) {
                Element form2 = this.getFormOwner();
                if (form2 != null) {
                    form2.requestSubmit(this);
                }
            } else if ("reset".equalsIgnoreCase(type) && (form = this.getFormOwner()) != null) {
                form.reset();
            }
            return;
        }
        if (!"LABEL".equalsIgnoreCase(this.tagName)) {
            return;
        }
        Element control = this.getLabeledControl();
        if (control != null && control != this && !control.isDisabled()) {
            control.click();
        }
    }

    public Element getLabeledControl() {
        if (!"LABEL".equalsIgnoreCase(this.tagName)) {
            return null;
        }
        String forId = this.getAttribute("for");
        if (this.hasAttribute("for")) {
            Element candidate;
            if (forId == null || forId.isBlank()) {
                return null;
            }
            Element element = candidate = this.document == null ? null : this.document.getElementById(forId.trim());
            if (candidate == null) {
                Element root = this;
                while (root.parentElement != null) {
                    root = root.parentElement;
                }
                candidate = Element.findElementById(root, forId.trim());
            }
            return candidate != null && candidate.isLabelableControl() ? candidate : null;
        }
        return this.querySelector("input, select, textarea, button, output");
    }

    public List<Element> getLabels() {
        if (!this.isLabelableControl()) {
            return List.of();
        }
        ArrayList<Element> labels = new ArrayList<Element>();
        ArrayList<Element> candidates = new ArrayList<Element>();
        if (this.document != null) {
            candidates.addAll(this.document.getElements());
        }
        Element root = this;
        while (root.parentElement != null) {
            root = root.parentElement;
        }
        ArrayList<Element> local = new ArrayList<Element>();
        ConstraintValidator.collectElements(root, local);
        for (Element candidate : local) {
            if (candidates.contains(candidate)) continue;
            candidates.add(candidate);
        }
        for (Element candidate : candidates) {
            if (!"LABEL".equalsIgnoreCase(candidate.tagName) || candidate.getLabeledControl() != this && (candidate.hasAttribute("for") || !candidate.contains(this))) continue;
            labels.add(candidate);
        }
        return Collections.unmodifiableList(labels);
    }

    public Element findEnclosingForm() {
        Element owner = this.getFormOwner();
        if (owner != null) {
            return owner;
        }
        Element current = this;
        while (current != null) {
            if ("FORM".equalsIgnoreCase(current.tagName)) {
                return current;
            }
            current = current.parentElement;
        }
        return null;
    }

    public boolean submitEnclosingForm() {
        Element form = this.findEnclosingForm();
        return form != null && form.requestSubmit(ConstraintText.isSubmitButton(this) ? this : null);
    }

    public boolean dispatchScrollEventIfChanged(double previousLeft, double previousTop) {
        if (Double.compare(previousLeft, this.getTargetScrollLeft()) == 0 && Double.compare(previousTop, this.getTargetScrollTop()) == 0) {
            return false;
        }
        Event event = new Event(this, "scroll", null, false);
        event.bubbles = false;
        Event.markTrustedFromCurrentDispatch(event);
        return Event.tiggerEvent(event);
    }

    public void addDirtyFlags(int mask) {
        this.dirty.add(mask);
    }

    public boolean hasDirtyFlag(int mask) {
        return this.dirty.has(mask);
    }

    public void clearDirtyFlags() {
        this.dirty.clear();
    }

    public int getDepth() {
        return this.node.getDepth();
    }

    public Element getParentStackContext() {
        return this.node.getParentStackContext();
    }

    public boolean isStackContext() {
        return this.node.isStackContext();
    }

    public void tick() {
        this.scroll.tick();
        if (!this.innerText.equals(this.lastInnerText)) {
            this.getRenderer().text.clear();
            this.getRenderer().wrappedText.clear();
            this.getRenderer().size.clear();
            this.lastInnerText = this.innerText;
            if (this.document != null) {
                this.document.bumpSelectionCache();
                this.document.markDirty(this, 5);
                if (this.parentElement != null) {
                    this.parentElement.getRenderer().size.clear();
                    this.document.markDirty(this.parentElement, 5);
                }
            }
        }
    }

    boolean stepScrollRender() {
        return this.scroll.stepRender();
    }

    boolean needsScrollRenderStep() {
        return this.scroll.needsRenderStep();
    }

    @Override
    public void addEventListener(String type, Consumer<Event> listener) {
        super.addEventListener(type, listener);
    }

    @Override
    public void addEventListener(String type, Consumer<Event> listener, boolean useCapture) {
        super.addEventListener(type, listener, useCapture);
    }

    @Override
    public void addEventListener(String type, Consumer<Event> listener, boolean useCapture, boolean once) {
        super.addEventListener(type, listener, useCapture, once);
    }

    @Override
    public void addInternalEventListener(String type, Consumer<Event> listener) {
        super.addInternalEventListener(type, listener);
    }

    @Override
    public void addInternalEventListener(String type, Consumer<Event> listener, boolean useCapture) {
        super.addInternalEventListener(type, listener, useCapture);
    }

    @Override
    public void removeEventListener(String type, Consumer<Event> listener, boolean useCapture) {
        super.removeEventListener(type, listener, useCapture);
    }

    @Override
    public void triggerEvent(Consumer<Event.ListenerRecord> handler) {
        super.triggerEvent(handler);
    }

    @Override
    public void setEventListeners(CopyOnWriteArrayList<Event.ListenerRecord> listeners) {
        super.setEventListeners(listeners);
    }

    public RenderElement getRenderer() {
        return this.renderElement;
    }

    public void resetRenderer() {
        this.renderElement = new RenderElement(this);
    }

    @Override
    public void remove() {
        this.document.removeElement(this);
    }

    public boolean hasInnerTextSelection() {
        return this.textSelection.hasInnerTextSelection();
    }

    public String getSelectedInnerText() {
        return this.textSelection.getSelectedInnerText();
    }

    public void selectAllInnerText() {
        this.textSelection.selectAllInnerText();
    }

    public void clearTextSelection() {
        this.textSelection.clearTextSelection();
    }

    public boolean canSelectInnerText() {
        return this.textSelection.canSelectInnerText();
    }

    protected final boolean hasBooleanAttribute(String name) {
        return this.hasAttribute(name);
    }

    protected final void setBooleanAttribute(String name, boolean enabled) {
        if (enabled) {
            this.setAttribute(name, "");
        } else {
            this.removeAttribute(name);
        }
    }

    public final boolean hasRawBooleanAttribute(String name) {
        return this.attributes.containsKey(name);
    }

    protected final void setRawBooleanAttribute(String name, boolean enabled) {
        if (enabled) {
            this.attributes.put(name, "");
        } else {
            this.attributes.remove(name);
        }
    }

    private List<Element> getOptionChildren() {
        return SelectModel.getOptionChildren(this);
    }

    public String getOptionValue() {
        return SelectModel.getOptionValue(this);
    }

    public String getOptionLabel() {
        return SelectModel.getOptionLabel(this);
    }

    public void setOptionLabel(String label) {
        SelectModel.setOptionLabel(this, label);
    }

    public String getOptionText() {
        return SelectModel.getOptionText(this);
    }

    public void setOptionText(String text) {
        SelectModel.setOptionText(this, text);
    }

    public int getOptionIndex() {
        return SelectModel.getOptionIndex(this);
    }

    public int getSelectLength() {
        return SelectModel.getSelectLength(this);
    }

    public int getFormLength() {
        return "FORM".equalsIgnoreCase(this.tagName) ? this.getFormControls().size() : 0;
    }

    public int getSelectSize() {
        return SelectModel.getSelectSize(this);
    }

    public void setSelectSize(int size) {
        SelectModel.setSelectSize(this, size);
    }

    public Element getOwnerSelect() {
        return SelectModel.getOwnerSelect(this);
    }

    public boolean isOptionEffectivelyDisabled() {
        return SelectModel.isOptionEffectivelyDisabled(this);
    }

    private boolean currentSelectedness() {
        return SelectModel.currentSelectedness(this);
    }

    private void normalizeSelectSelection(boolean allowDefaultSelection) {
        SelectModel.normalizeSelectSelection(this, allowDefaultSelection);
    }

    private int getSelectDisplaySize() {
        return SelectModel.getSelectDisplaySize(this);
    }

    private void invalidateSelectPresentation() {
        SelectModel.invalidateSelectPresentation(this);
    }

    private void syncAttributeState(String name) {
        if (name == null || name.isBlank()) {
            return;
        }
        if ("value".equals(name) && "SELECT".equalsIgnoreCase(this.tagName)) {
            return;
        }
        if (("multiple".equals(name) || "size".equals(name)) && "SELECT".equalsIgnoreCase(this.tagName)) {
            this.normalizeSelectSelection(true);
            this.invalidateSelectPresentation();
            return;
        }
        if ("value".equals(name)) {
            if (!this.valueDirty || this.value == null) {
                this.value = this.getDefaultValue();
            }
            return;
        }
        if ("checked".equals(name)) {
            if (!this.checkedDirty) {
                this.checkedState = this.hasRawBooleanAttribute("checked");
            }
            if ("INPUT".equalsIgnoreCase(this.tagName) && "radio".equalsIgnoreCase(this.getAttribute("type")) && this.isChecked()) {
                this.enforceRadioGroupChecked();
            }
            return;
        }
        if ("selected".equals(name) && "OPTION".equalsIgnoreCase(this.tagName)) {
            Element select;
            if (!this.selectedDirty) {
                this.selectedState = this.hasRawBooleanAttribute("selected");
            }
            if ((select = this.getOwnerSelect()) != null && !this.selectedDirty) {
                select.normalizeSelectSelection(false);
                select.invalidateSelectPresentation();
            }
            return;
        }
        if ("selected".equals(name) && !this.selectedDirty) {
            this.selectedState = this.hasRawBooleanAttribute("selected");
        }
    }

    private void applyDomStateFromAttributes() {
        if ("SELECT".equalsIgnoreCase(this.tagName)) {
            this.normalizeSelectSelection(true);
            return;
        }
        if ("OPTION".equalsIgnoreCase(this.tagName)) {
            Element select;
            if (!this.selectedDirty) {
                this.selectedState = this.hasRawBooleanAttribute("selected");
            }
            if ((select = this.getOwnerSelect()) != null) {
                select.normalizeSelectSelection(true);
                select.invalidateSelectPresentation();
            }
            return;
        }
        if (this.value == null) {
            this.value = this.getDefaultValue();
        }
        if (!this.checkedDirty) {
            this.checkedState = this.hasRawBooleanAttribute("checked");
        }
        if (!this.selectedDirty) {
            this.selectedState = this.hasRawBooleanAttribute("selected");
        }
        if ("INPUT".equalsIgnoreCase(this.tagName) && "radio".equalsIgnoreCase(this.getAttribute("type")) && this.isChecked()) {
            this.enforceRadioGroupChecked();
        }
    }

    public void syncDomStateAfterAttach() {
        this.applyDomStateFromAttributes();
        for (Element child : this.children) {
            if (child == null) continue;
            child.syncDomStateAfterAttach();
        }
        if ("SELECT".equalsIgnoreCase(this.tagName)) {
            this.normalizeSelectSelection(true);
        }
    }

    public void syncSelectStateAfterChildrenChanged() {
        if ("SELECT".equalsIgnoreCase(this.tagName)) {
            this.normalizeSelectSelection(true);
            this.invalidateSelectPresentation();
            return;
        }
        if ("OPTGROUP".equalsIgnoreCase(this.tagName) && this.parentElement != null && "SELECT".equalsIgnoreCase(this.parentElement.tagName)) {
            this.parentElement.normalizeSelectSelection(true);
            this.parentElement.invalidateSelectPresentation();
        }
    }

    public void onDisconnectedFromDocument() {
    }

    public void invalidateSubtreeAfterAttach() {
        this.invalidateStyleCaches();
        this.renderElement.route.clear();
        this.renderElement.transform.clear();
        this.renderElement.opacity.clear();
        this.renderElement.text.clear();
        this.renderElement.wrappedText.clear();
        this.renderElement.size.clear();
        this.renderElement.box.clear();
        this.renderElement.position.clear();
        this.renderElement.background.clear();
        this.renderElement.cursor.clear();
        this.renderElement.filter.clear();
        this.renderElement.backdropFilter.clear();
        for (Element child : this.children) {
            if (child == null) continue;
            child.invalidateSubtreeAfterAttach();
        }
    }

    void refreshElementChildrenFromChildNodes() {
        ArrayList<Element> elementChildren = new ArrayList<Element>();
        for (Node child : this.childNodes) {
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            childElement.parentElement = this;
            elementChildren.add(childElement);
        }
        this.children = elementChildren;
    }

    private void enforceRadioGroupChecked() {
        List<Element> candidates;
        String group = this.getAttribute("name");
        if (group == null || group.isBlank()) {
            return;
        }
        Element owner = this.getFormOwner();
        if (owner != null) {
            candidates = owner.getFormControls();
        } else if (this.document != null) {
            candidates = this.document.getElements();
        } else {
            Element root = this;
            while (root.parentElement != null) {
                root = root.parentElement;
            }
            ArrayList<Element> local = new ArrayList<Element>();
            ConstraintValidator.collectElements(root, local);
            candidates = local;
        }
        for (Element element : candidates) {
            if (element == this || element.getFormOwner() != owner || !"INPUT".equalsIgnoreCase(element.tagName) || !"radio".equalsIgnoreCase(ConstraintText.normalizedInputType(element)) || !group.equals(element.getAttribute("name"))) continue;
            element.checkedState = false;
            element.checkedDirty = true;
            element.invalidateStyle();
        }
    }

    protected void drawStaticText(PoseStack poseStack, Rect rectRenderer, Text text) {
        double contentHeight;
        if (text == null || text.content == null || text.content.isEmpty()) {
            return;
        }
        Position contentPos = rectRenderer.getContentPosition();
        double contentWidth = Box.of(this).innerSize().width();
        List<String> renderLines = this.resolveRenderedLines(text, contentWidth, contentHeight = Box.of(this).innerSize().height());
        if (renderLines.isEmpty()) {
            return;
        }
        double textHeight = Math.max(text.lineHeight, (double)renderLines.size() * text.lineHeight);
        double drawY = contentPos.y + TextMetrics.computeVerticalOffset(text, contentHeight, textHeight);
        for (int i = 0; i < renderLines.size(); ++i) {
            String line = renderLines.get(i);
            double lineTop = (double)i * text.lineHeight;
            if (lineTop >= contentHeight) break;
            double lineWidth = Text.measureLine(text, line);
            double drawX = contentPos.x + TextMetrics.computeAlignedX(text, contentWidth, lineWidth, i == 0);
            Text lineText = TextMetrics.cloneTextForSegment(text, line, Color.BLACK);
            FontDrawer.drawFont(poseStack, lineText, new Position(drawX - this.scrollLeft, drawY + lineTop));
        }
    }

    public Position getFlexTextOffset() {
        String content;
        Text text = Text.of(this);
        String string = content = text == null ? "" : text.content;
        if (content == null || content.isEmpty()) {
            return Position.ZERO;
        }
        List<String> lines = Text.splitLines(content);
        String firstLine = lines.isEmpty() ? "" : lines.get(0);
        double contentWidth = Box.of(this).innerSize().width();
        double contentHeight = Box.of(this).innerSize().height();
        double lineWidth = Text.measureLine(text, firstLine);
        double x = TextMetrics.computeFlexTextAlignedX(this, text, contentWidth, lineWidth);
        double y = TextMetrics.computeFlexTextAlignedY(this, text, contentHeight);
        return new Position(x, y);
    }

    private void drawChildTextRuns(PoseStack poseStack, Rect rectRenderer) {
        List<Node> renderChildNodes = this.getRenderChildNodes();
        if (renderChildNodes.isEmpty()) {
            return;
        }
        if (this instanceof AbstractText) {
            return;
        }
        if (Layout.isFlexDisplay(this.getComputedStyle().display)) {
            this.drawFlexDirectTextRuns(poseStack);
            return;
        }
        if (this.getRenderChildren().isEmpty()) {
            for (Node child : renderChildNodes) {
                TextNode textNode;
                if (!(child instanceof TextNode) || (textNode = (TextNode)child).getTextContent().isEmpty()) continue;
                return;
            }
        }
        if (Layout.isGridDisplay(this.getComputedStyle().display)) {
            return;
        }
        Position contentPos = rectRenderer.getContentPosition();
        boolean alignDirectTextRuns = this.shouldAlignDirectNormalFlowTextRuns();
        double contentWidth = alignDirectTextRuns ? Box.of(this).innerSize().width() : 0.0;
        List<NormalFlow.TextRunLayout> textRuns = NormalFlow.computeTextRuns(this);
        boolean[] baselineAnchors = Element.resolveRunBaselineAnchors(textRuns);
        int[] selectionRange = null;
        if (this.document != null && SelectionUnits.isSelectionUnit(this)) {
            selectionRange = this.document.resolveUnitSelectionRange(this);
        }
        for (int r = 0; r < textRuns.size(); ++r) {
            NormalFlow.TextRunLayout run = textRuns.get(r);
            if (run == null || run.text() == null || run.lines() == null) continue;
            int runBase = 0;
            if (selectionRange != null) {
                TextNode runNode = run.node();
                runBase = SelectionUnits.baseOffsetOfDescendant(this, runNode != null ? runNode : run.owner());
            }
            Position drawPos = new Position(0.0, 0.0);
            for (int i = 0; i < run.lines().size(); ++i) {
                String line = run.lines().get(i);
                if (line == null || line.isEmpty()) continue;
                double lineWidth = Text.measureLine(run.text(), line);
                double alignOffset = alignDirectTextRuns && run.owner() == this ? TextMetrics.computeAlignedX(run.text(), contentWidth, lineWidth, i == 0) : 0.0;
                drawPos.x = contentPos.x + (i == 0 ? run.x() : 0.0) + alignOffset - this.scrollLeft;
                drawPos.y = contentPos.y + run.y() + (double)i * run.text().lineHeight;
                this.drawInlineFragmentBackground(poseStack, run.owner(), drawPos, lineWidth, run.text().lineHeight);
                if (selectionRange != null) {
                    int segEnd;
                    int globalStart = runBase + SelectionUnits.runLineStart(run, i);
                    int globalEnd = globalStart + line.length();
                    int segStart = Math.max(selectionRange[0], globalStart);
                    if (segStart < (segEnd = Math.min(selectionRange[1], globalEnd))) {
                        double highlightX0 = drawPos.x + Element.measureRunSegment(run, line.substring(0, segStart - globalStart));
                        double highlightX1 = drawPos.x + Element.measureRunSegment(run, line.substring(0, segEnd - globalStart));
                        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), (float)highlightX0, (float)drawPos.y, (float)highlightX1, (float)(drawPos.y + run.text().lineHeight), Text.getSelectionColor(this));
                    }
                }
                Text lineText = TextMetrics.cloneTextForSegment(run.text(), line, Color.BLACK);
                if (baselineAnchors[r]) {
                    FontDrawer.drawFontOnBaseline(poseStack, lineText, drawPos, Text.renderedBaselineOffset(lineText));
                    continue;
                }
                FontDrawer.drawFont(poseStack, lineText, drawPos);
            }
        }
    }

    private static double measureRunSegment(NormalFlow.TextRunLayout run, String segment) {
        if (segment == null || segment.isEmpty()) {
            return 0.0;
        }
        Text copy = TextMetrics.cloneTextForSegment(run.text(), segment, Color.BLACK);
        return Text.measureLine(copy, segment);
    }

    public static boolean[] resolveRunBaselineAnchors(List<NormalFlow.TextRunLayout> runs) {
        boolean[] flags = new boolean[runs.size()];
        HashMap<Long, Integer> backendMasks = new HashMap<Long, Integer>();
        HashMap<Long, List> lineMembers = new HashMap<Long, List>();
        for (int r = 0; r < runs.size(); ++r) {
            NormalFlow.TextRunLayout run = runs.get(r);
            if (run == null || run.text() == null || run.lines() == null) continue;
            int backend = Element.usesDefaultFontBackend(run.text()) ? 1 : 2;
            double baselineOffset = Text.renderedBaselineOffset(run.text());
            for (int i = 0; i < run.lines().size(); ++i) {
                String line = run.lines().get(i);
                if (line == null || line.isBlank()) continue;
                long lineKey = Math.round((run.y() + (double)i * run.text().lineHeight + baselineOffset) * 1000.0);
                backendMasks.merge(lineKey, backend, (a, b) -> a | b);
                lineMembers.computeIfAbsent(lineKey, key -> new ArrayList()).add(r);
            }
        }
        for (Map.Entry entry : lineMembers.entrySet()) {
            if ((Integer)backendMasks.get(entry.getKey()) != 3) continue;
            Iterator iterator = ((List)entry.getValue()).iterator();
            while (iterator.hasNext()) {
                int r = (Integer)iterator.next();
                flags[r] = true;
            }
        }
        return flags;
    }

    private static boolean usesDefaultFontBackend(Text text) {
        return text.fontFamily == null || text.fontFamily.equals("unset");
    }

    private boolean shouldAlignDirectNormalFlowTextRuns() {
        boolean hasText = false;
        for (Node child : this.getRenderChildNodes()) {
            Element element;
            if (child instanceof CommentNode) continue;
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode)child;
                hasText |= textNode.getTextContent() != null && !textNode.getTextContent().isEmpty();
                continue;
            }
            if (child instanceof Element && !Layout.isInFlow((element = (Element)child).getComputedStyle())) continue;
            return false;
        }
        return hasText;
    }

    private void drawInlineFragmentBackground(PoseStack poseStack, Element owner, Position drawPos, double width, double height) {
        if (owner == null || owner == this || width <= 0.0 || height <= 0.0) {
            return;
        }
        Style style = owner.getComputedStyle();
        if (!"inline".equalsIgnoreCase(style.display)) {
            return;
        }
        Background background = Background.of(owner);
        if (background == null || background.color == null || "unset".equals(background.color)) {
            return;
        }
        int color = new Color(background.color).getValue();
        if (color >>> 24 == 0) {
            return;
        }
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), (float)drawPos.x, (float)drawPos.y, (float)(drawPos.x + width), (float)(drawPos.y + height), color);
    }

    private void drawFlexDirectTextRuns(PoseStack poseStack) {
        int[] selectionRange = this.document != null && this.document.getDocumentSelection().isActive() && SelectionUnits.isSelectionUnit(this) ? this.document.getDocumentSelection().localRangeForUnit(this) : null;
        List<String> fragments = selectionRange == null ? null : SelectionUnits.flexTextFragments(this);
        int fragmentIndex = 0;
        int accumulatedBase = 0;
        for (Flex.DirectTextLayout layout : Flex.computeDirectTextLayouts(this)) {
            if (layout == null || layout.text() == null || layout.position() == null) continue;
            Text text = layout.text();
            if (text.content == null || text.content.isEmpty()) continue;
            if (selectionRange == null || fragments == null) {
                FontDrawer.drawFont(poseStack, TextMetrics.cloneTextForSegment(text, text.content, Color.BLACK), this.getFlexDirectTextPaintPosition(layout));
                continue;
            }
            int base = accumulatedBase;
            if (fragmentIndex < fragments.size() && fragments.get(fragmentIndex).equals(text.content)) {
                accumulatedBase += text.content.length();
                ++fragmentIndex;
            } else {
                fragmentIndex = fragments.size();
            }
            int segStart = Math.max(selectionRange[0], base);
            int segEnd = Math.min(selectionRange[1], base + text.content.length());
            Position paintPos = this.getFlexDirectTextPaintPosition(layout);
            if (segStart >= segEnd) {
                FontDrawer.drawFont(poseStack, TextMetrics.cloneTextForSegment(text, text.content, Color.BLACK), paintPos);
                continue;
            }
            double x0 = paintPos.x + Element.measureTextSegment(text, text.content.substring(0, segStart - base));
            double x1 = paintPos.x + Element.measureTextSegment(text, text.content.substring(0, segEnd - base));
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), (float)x0, (float)paintPos.y, (float)x1, (float)(paintPos.y + text.lineHeight), Text.getSelectionColor(this));
            FontDrawer.drawFont(poseStack, TextMetrics.cloneTextForSegment(text, text.content, Color.BLACK), paintPos);
        }
    }

    private static double measureTextSegment(Text text, String segment) {
        if (segment == null || segment.isEmpty()) {
            return 0.0;
        }
        Text copy = TextMetrics.cloneTextForSegment(text, segment, Color.BLACK);
        return Text.measureLine(copy, segment);
    }

    Position getFlexDirectTextPaintPosition(Flex.DirectTextLayout layout) {
        Position origin = Position.forRender(this);
        Box box = Box.of(this);
        double originX = origin.x + box.getMarginLeft();
        double originY = origin.y + box.getMarginTop();
        if (layout == null || layout.position() == null) {
            return new Position(originX, originY);
        }
        return new Position(originX + layout.position().x - this.scrollLeft, originY + layout.position().y - this.scrollTop);
    }

    private boolean hasMixedDirectTextAndElementChildren() {
        if (this.getRenderChildren().isEmpty() || this.getRenderChildNodes().isEmpty()) {
            return false;
        }
        for (Node child : this.getRenderChildNodes()) {
            TextNode textNode;
            if (!(child instanceof TextNode) || (textNode = (TextNode)child).getTextContent().isBlank()) continue;
            return true;
        }
        return false;
    }

    public List<String> resolveRenderedLines(Text text, double contentWidth, double contentHeight) {
        boolean truncated;
        Text.WrappedText wrapped = Text.wrap(this, text);
        ArrayList<String> lines = new ArrayList<String>(wrapped.lines());
        if (lines.isEmpty()) {
            return lines;
        }
        int heightLineCount = Math.max(1, (int)Math.floor(contentHeight / Math.max(1.0, text.lineHeight)));
        int lineClamp = Text.resolveLineClamp(this);
        int visibleLineCount = lineClamp > 0 ? Math.min(heightLineCount, lineClamp) : heightLineCount;
        boolean bl = truncated = visibleLineCount < lines.size();
        if (visibleLineCount < lines.size()) {
            lines = new ArrayList(lines.subList(0, visibleLineCount));
        }
        if (this.shouldApplyClampedEllipsis(contentWidth, lineClamp, truncated)) {
            int last = lines.size() - 1;
            lines.set(last, TextMetrics.ellipsize(text, (String)lines.get(last), contentWidth, true));
        } else if (this.shouldApplyEllipsis(text, contentWidth)) {
            String line = (String)lines.get(0);
            lines.set(0, TextMetrics.ellipsize(text, line, Math.max(0.0, contentWidth - Math.abs(text.textIndent)), false));
            if (lines.size() > 1) {
                lines = new ArrayList(lines.subList(0, 1));
            }
        }
        return lines;
    }

    private boolean shouldApplyEllipsis(Text text, double contentWidth) {
        if (contentWidth <= 0.0) {
            return false;
        }
        String overflow = this.getComputedStyle().overflow;
        String textOverflow = this.getComputedStyle().textOverflow;
        if (!Interaction.clipsOverflow(overflow)) {
            return false;
        }
        if (!"ellipsis".equalsIgnoreCase(textOverflow)) {
            return false;
        }
        return !Text.allowsSoftWrap(text.whiteSpace);
    }

    private boolean shouldApplyClampedEllipsis(double contentWidth, int lineClamp, boolean truncated) {
        if (contentWidth <= 0.0 || lineClamp <= 0 || !truncated) {
            return false;
        }
        Style style = this.getComputedStyle();
        return Interaction.clipsOverflow(style.overflow) && "ellipsis".equalsIgnoreCase(style.textOverflow);
    }

    public String toString() {
        return "<" + this.tagName + ">";
    }

    public static final class DOMTokenList {
        private final Element owner;

        DOMTokenList(Element owner) {
            this.owner = owner;
        }

        public int getLength() {
            return this.owner.getClassNames().size();
        }

        public boolean contains(String token) {
            if (token == null || token.isBlank()) {
                return false;
            }
            return this.owner.getClassNames().contains(token.trim());
        }

        public void add(String ... tokens) {
            this.updateTokens(true, tokens);
        }

        public void remove(String ... tokens) {
            this.updateTokens(false, tokens);
        }

        public boolean toggle(String token) {
            return this.toggle(token, null);
        }

        public boolean toggle(String token, Boolean force) {
            boolean shouldContain;
            if (token == null || token.isBlank()) {
                return false;
            }
            String normalized = token.trim();
            LinkedHashSet<String> values = new LinkedHashSet<String>(this.owner.getClassNames());
            boolean present = values.contains(normalized);
            boolean bl = force == null ? !present : (shouldContain = force.booleanValue());
            if (shouldContain) {
                values.add(normalized);
            } else {
                values.remove(normalized);
            }
            this.owner.setClassName(String.join((CharSequence)" ", values));
            return shouldContain;
        }

        public String item(int index) {
            if (index < 0 || index >= this.owner.getClassNames().size()) {
                return null;
            }
            return new ArrayList<String>(this.owner.getClassNames()).get(index);
        }

        public String toString() {
            return this.owner.getClassName();
        }

        private void updateTokens(boolean add, String ... tokens) {
            if (tokens == null || tokens.length == 0) {
                return;
            }
            LinkedHashSet<String> values = new LinkedHashSet<String>(this.owner.getClassNames());
            boolean changed = false;
            for (String token : tokens) {
                if (token == null || token.isBlank()) continue;
                String normalized = token.trim();
                changed |= add ? values.add(normalized) : values.remove(normalized);
            }
            if (changed) {
                this.owner.setClassName(String.join((CharSequence)" ", values));
            }
        }
    }

    public static final class DOMStringMap
    implements Map {
        private final Element owner;

        DOMStringMap(Element owner) {
            this.owner = owner;
        }

        public String get(Object key) {
            if (key == null) {
                return "";
            }
            String name = String.valueOf(key);
            if (name.isBlank()) {
                return "";
            }
            return this.owner.getAttribute(DOMStringMap.toDataAttributeName(name));
        }

        public void set(String key, String value) {
            if (key == null || key.isBlank()) {
                return;
            }
            this.owner.setAttribute(DOMStringMap.toDataAttributeName(key), value == null ? "" : value);
        }

        public boolean has(String key) {
            if (key == null || key.isBlank()) {
                return false;
            }
            return this.owner.hasAttribute(DOMStringMap.toDataAttributeName(key));
        }

        public void delete(String key) {
            if (key == null || key.isBlank()) {
                return;
            }
            this.owner.removeAttribute(DOMStringMap.toDataAttributeName(key));
        }

        public Set<String> keys() {
            LinkedHashSet<String> keys = new LinkedHashSet<String>();
            for (String attrName : this.owner.getAttributes().keySet()) {
                if (!attrName.startsWith("data-") || attrName.length() <= 5) continue;
                keys.add(DOMStringMap.fromDataAttributeName(attrName));
            }
            return Collections.unmodifiableSet(keys);
        }

        @Override
        public int size() {
            return this.keys().size();
        }

        @Override
        public boolean isEmpty() {
            return this.keys().isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return key != null && this.has(String.valueOf(key));
        }

        @Override
        public boolean containsValue(Object value) {
            for (String key : this.keys()) {
                if (!Objects.equals(this.get(key), value)) continue;
                return true;
            }
            return false;
        }

        public String put(Object key, Object value) {
            if (key == null) {
                return null;
            }
            String name = String.valueOf(key);
            String old = this.get(name);
            this.set(name, DOMStringMap.jsValueToString(value));
            return old;
        }

        private static String jsValueToString(Object value) {
            double d;
            if (value == null) {
                return null;
            }
            if (value instanceof Double && !Double.isInfinite(d = ((Double)value).doubleValue()) && !Double.isNaN(d) && d == Math.rint(d) && d >= -9.223372036854776E18 && d <= 9.223372036854776E18) {
                return String.valueOf((long)d);
            }
            return String.valueOf(value);
        }

        public String remove(Object key) {
            if (key == null) {
                return null;
            }
            String name = String.valueOf(key);
            String old = this.get(name);
            this.delete(name);
            return old;
        }

        public void putAll(Map m) {
            if (m == null) {
                return;
            }
            Iterator iterator = m.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry eo;
                Map.Entry e = eo = iterator.next();
                this.set((String)e.getKey(), (String)e.getValue());
            }
        }

        @Override
        public void clear() {
            for (String key : new ArrayList<String>(this.keys())) {
                this.delete(key);
            }
        }

        public Set<String> keySet() {
            return this.keys();
        }

        public Collection<String> values() {
            ArrayList<String> values = new ArrayList<String>();
            for (String key : this.keys()) {
                values.add(this.get(key));
            }
            return Collections.unmodifiableList(values);
        }

        public Set<Map.Entry<String, String>> entrySet() {
            LinkedHashSet<AbstractMap.SimpleEntry<String, String>> entries = new LinkedHashSet<AbstractMap.SimpleEntry<String, String>>();
            for (String key : this.keys()) {
                entries.add(new AbstractMap.SimpleEntry<String, String>(key, this.get(key)));
            }
            return Collections.unmodifiableSet(entries);
        }

        public String toString() {
            return this.keys().toString();
        }

        private static String toDataAttributeName(String key) {
            String trimmed = key.trim();
            StringBuilder result = new StringBuilder("data-");
            for (int i = 0; i < trimmed.length(); ++i) {
                char c = trimmed.charAt(i);
                if (Character.isUpperCase(c)) {
                    result.append('-').append(Character.toLowerCase(c));
                    continue;
                }
                if (c == '_') {
                    result.append('-');
                    continue;
                }
                result.append(Character.toLowerCase(c));
            }
            return result.toString();
        }

        private static String fromDataAttributeName(String attrName) {
            String raw = attrName.substring(5);
            StringBuilder result = new StringBuilder();
            boolean upperNext = false;
            for (int i = 0; i < raw.length(); ++i) {
                char c = raw.charAt(i);
                if (c == '-') {
                    upperNext = true;
                    continue;
                }
                result.append(upperNext ? Character.toUpperCase(c) : c);
                upperNext = false;
            }
            return result.toString();
        }
    }

    public static final class DOMRect {
        public final double x;
        public final double y;
        public final double width;
        public final double height;
        public final double left;
        public final double top;
        public final double right;
        public final double bottom;

        public DOMRect(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.left = x;
            this.top = y;
            this.right = x + width;
            this.bottom = y + height;
        }
    }
}

