/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.element;

import com.sighs.apricityui.element.Select;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.task.FrameTaskScheduler;
import com.sighs.apricityui.ui.Tooltip;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

final class SelectPopup {
    private static final double VIEWPORT_GAP = 4.0;
    private static final double MIN_POPUP_WIDTH = 80.0;
    private static final double ROW_HEIGHT = 28.0;
    private static final int MAX_VISIBLE_ROWS = 12;
    private static final int Z_INDEX = 10000;
    private static final String PANEL_STYLE = "position:fixed;z-index:10000;box-sizing:border-box;background:#ffffff;border:1px solid #767676;border-radius:2px;box-shadow:0 2px 6px rgba(0,0,0,0.28);overflow-x:hidden;overflow-y:auto;padding:2px 0;color:#000000;pointer-events:auto;";
    private static final String ROW_STYLE = "box-sizing:border-box;display:flex;align-items:center;width:100%;min-height:28px;padding:4px 8px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;cursor:default;user-select:none;";
    private static final String GROUP_STYLE = "box-sizing:border-box;width:100%;min-height:24px;padding:5px 8px 3px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;color:#555555;background:#f5f5f5;font-weight:600;user-select:none;";
    private static SelectPopup activePopup;
    private final Select select;
    private final Document document;
    private final List<Element> options;
    private final List<Element> rows = new ArrayList<Element>();
    private Element panel;
    private final Consumer<Event> outsideMouseListener = this::handleOutsidePointer;
    private final Consumer<Event> outsideContextMenuListener = this::handleOutsidePointer;
    private int activeIndex;
    private boolean closed;

    private SelectPopup(Select select) {
        this.select = select;
        this.document = select.document;
        this.options = List.copyOf(select.getOptions());
        this.activeIndex = this.initialActiveIndex();
    }

    static synchronized SelectPopup open(Select select) {
        if (activePopup != null) {
            if (SelectPopup.activePopup.select == select && activePopup.isOpen()) {
                return activePopup;
            }
            activePopup.close();
        }
        SelectPopup popup = new SelectPopup(select);
        try (Document.ContextScope ignored = Document.withContext(popup.document);){
            popup.mount();
        }
        if (popup.isOpen()) {
            activePopup = popup;
        }
        return popup;
    }

    boolean isOpen() {
        return !this.closed && this.panel != null && this.panel.isConnected() && this.select.isConnected();
    }

    int getActiveIndex() {
        return this.activeIndex;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void close() {
        try (Document.ContextScope ignored = Document.withContext(this.document);){
            if (this.closed) {
                return;
            }
            this.closed = true;
            this.document.removeEventListener("mousedown", this.outsideMouseListener, true);
            this.document.removeEventListener("contextmenu", this.outsideContextMenuListener, true);
            Tooltip.hide(this.document);
            if (this.panel != null) {
                this.panel.remove();
            }
            this.panel = null;
            this.rows.clear();
            Class<SelectPopup> clazz = SelectPopup.class;
            synchronized (SelectPopup.class) {
                if (activePopup == this) {
                    activePopup = null;
                }
                // ** MonitorExit[var2_2] (shouldn't be in output)
                this.markDirty();
                this.select.onPopupClosed(this);
            }
        }
        {
            return;
        }
    }

    void move(int delta) {
        int candidate;
        if (this.options.isEmpty()) {
            return;
        }
        int direction = delta < 0 ? -1 : 1;
        int count = Math.max(1, Math.abs(delta));
        int next = this.activeIndex;
        for (int step = 0; step < count && (candidate = this.findEnabled(next, direction, false)) >= 0; ++step) {
            next = candidate;
        }
        this.setActiveIndex(next, true);
    }

    void moveToBoundary(boolean end) {
        int index = this.findEnabled(end ? this.options.size() : -1, end ? -1 : 1, false);
        if (index >= 0) {
            this.setActiveIndex(index, true);
        }
    }

    void setActiveIndex(int index, boolean reveal) {
        try (Document.ContextScope ignored = Document.withContext(this.document);){
            if (index < 0 || index >= this.options.size() || this.options.get(index).isOptionEffectivelyDisabled()) {
                return;
            }
            int previous = this.activeIndex;
            this.activeIndex = index;
            if (previous >= 0 && previous < this.rows.size()) {
                this.applyRowStyle(previous);
            }
            if (this.activeIndex < this.rows.size()) {
                this.applyRowStyle(this.activeIndex);
            }
            if (reveal) {
                FrameTaskScheduler.scheduleAfterFrames(1, deadlineNs -> {
                    try (Document.ContextScope callbackContext = Document.withContext(this.document);){
                        if (this.isOpen()) {
                            this.revealActiveRow();
                        }
                        boolean bl = true;
                        return bl;
                    }
                });
            }
        }
    }

    boolean commitActive() {
        try (Document.ContextScope ignored = Document.withContext(this.document);){
            if (this.activeIndex < 0 || this.activeIndex >= this.options.size()) {
                boolean bl = false;
                return bl;
            }
            if (this.options.get(this.activeIndex).isOptionEffectivelyDisabled()) {
                boolean bl = false;
                return bl;
            }
            this.select.commitUserSelection(this.activeIndex);
            if (!this.select.isMultiple()) {
                this.close();
            } else {
                this.refreshRows();
            }
            boolean bl = true;
            return bl;
        }
    }

    private void mount() {
        if (this.document == null || this.document.body == null || this.options.isEmpty()) {
            return;
        }
        Tooltip.hide(this.document);
        Element.DOMRect initialAnchor = this.readAnchorRect();
        this.panel = this.element("DIV", "aui-select-popup");
        this.panel.setTopLayer(true);
        this.panel.setAttribute("role", "listbox");
        this.panel.setAttribute("aria-multiselectable", Boolean.toString(this.select.isMultiple()));
        this.panel.addEventListener("mousedown", event -> {
            event.stopPropagation();
            this.document.setFocusedElement(this.select);
        });
        this.panel.addEventListener("click", Event::stopPropagation);
        this.panel.addEventListener("contextmenu", event -> {
            event.preventDefault();
            event.stopPropagation();
        });
        Element previousGroup = null;
        for (int i = 0; i < this.options.size(); ++i) {
            Element group = SelectPopup.optionGroup(this.options.get(i));
            if (group != null && group != previousGroup) {
                this.appendGroup(group);
            }
            this.appendOption(i);
            previousGroup = group;
        }
        this.document.body.append(this.panel);
        this.document.addEventListener("mousedown", this.outsideMouseListener, true);
        this.document.addEventListener("contextmenu", this.outsideContextMenuListener, true);
        this.positionPanel(initialAnchor);
        this.markDirty();
        FrameTaskScheduler.scheduleAfterFrames(1, deadlineNs -> {
            try (Document.ContextScope callbackContext = Document.withContext(this.document);){
                if (!this.isOpen()) {
                    boolean bl = true;
                    return bl;
                }
                this.positionPanel();
                this.revealActiveRow();
                this.markDirty();
                boolean bl = true;
                return bl;
            }
        });
    }

    private void handleOutsidePointer(Event event) {
        Element target;
        if (!this.isOpen() || event == null) {
            return;
        }
        if (event.target == this.panel || event.target == this.select) {
            return;
        }
        Object object = event.target;
        if (object instanceof Element && (this.panel.contains(target = (Element)object) || this.select.contains(target))) {
            return;
        }
        this.close();
    }

    private void appendOption(int index) {
        Element option = this.options.get(index);
        Element row = this.element("DIV", "aui-select-option");
        row.setAttribute("role", "option");
        row.setAttribute("data-option-index", Integer.toString(index));
        row.setAttribute("aria-selected", Boolean.toString(option.isSelected()));
        row.setAttribute("aria-disabled", Boolean.toString(option.isOptionEffectivelyDisabled()));
        row.setTextContent(option.getOptionLabel());
        String tooltipKey = option.getAttribute("data-tooltip-key");
        if (tooltipKey != null && !tooltipKey.isBlank()) {
            row.setAttribute("data-tooltip-key", tooltipKey);
            Tooltip.bindTranslation(row, tooltipKey);
        }
        row.addEventListener("mouseenter", event -> this.setActiveIndex(index, false));
        row.addEventListener("mousedown", event -> {
            event.preventDefault();
            event.stopPropagation();
            this.document.setFocusedElement(this.select);
        });
        if (!option.isOptionEffectivelyDisabled()) {
            row.addEventListener("click", event -> {
                event.preventDefault();
                event.stopPropagation();
                this.setActiveIndex(index, false);
                this.commitActive();
            });
        }
        this.rows.add(row);
        this.applyRowStyle(index);
        this.panel.append(row);
    }

    private void appendGroup(Element group) {
        Element header = this.element("DIV", "aui-select-optgroup");
        header.setAttribute("role", "presentation");
        header.setTextContent(group.getAttribute("label"));
        header.setAttribute("style", GROUP_STYLE + SelectPopup.inheritedFontStyle(group));
        this.panel.append(header);
    }

    private void refreshRows() {
        for (int i = 0; i < this.rows.size(); ++i) {
            this.rows.get(i).setAttribute("aria-selected", Boolean.toString(this.options.get(i).isSelected()));
            this.applyRowStyle(i);
        }
    }

    private void applyRowStyle(int index) {
        String foreground;
        if (index < 0 || index >= this.rows.size()) {
            return;
        }
        Element option = this.options.get(index);
        boolean disabled = option.isOptionEffectivelyDisabled();
        boolean highlighted = index == this.activeIndex;
        boolean selected = option.isSelected();
        Style optionStyle = option.getComputedStyle();
        String string = highlighted ? "#ffffff" : (foreground = SelectPopup.hasAuthorOptionColor(option) ? SelectPopup.inherited(optionStyle.color, "#000000") : "#000000");
        String background = highlighted ? "#1967d2" : (selected ? "#e8f0fe" : SelectPopup.inherited(optionStyle.backgroundColor, "transparent"));
        String state = "color:" + foreground + ";background-color:" + background + ";" + (disabled ? "opacity:0.45;" : "opacity:1;");
        String groupIndent = SelectPopup.optionGroup(option) == null ? "" : "padding-left:20px;";
        this.rows.get(index).setAttribute("style", ROW_STYLE + groupIndent + SelectPopup.inheritedFontStyle(option) + state);
    }

    private static boolean hasAuthorOptionColor(Element option) {
        if (option == null) {
            return false;
        }
        if (option.cssCache.containsKey("color")) {
            return true;
        }
        String inlineColor = option.getInlineStylePropertyValue("color");
        return inlineColor != null && !inlineColor.isBlank() && !"unset".equalsIgnoreCase(inlineColor);
    }

    private void positionPanel() {
        if (this.panel == null) {
            return;
        }
        this.positionPanel(this.readAnchorRect());
    }

    private Element.DOMRect readAnchorRect() {
        if (this.document != null && this.document.isActive()) {
            this.document.commitPendingStyleRecalcForRender();
            if (!this.document.getPaintList().isEmpty() && this.document.hasPendingRenderState()) {
                this.document.commitRenderState();
            }
        }
        return this.select.getBoundingClientRect();
    }

    private void positionPanel(Element.DOMRect anchor) {
        if (this.panel == null || anchor == null) {
            return;
        }
        double viewportWidth = this.document.getViewport().layoutWidth();
        double viewportHeight = this.document.getViewport().layoutHeight();
        double width = Math.max(80.0, anchor.width);
        width = Math.min(width, Math.max(1.0, viewportWidth - 8.0));
        double desiredHeight = (double)Math.min(this.options.size(), 12) * 28.0 + 6.0;
        double below = Math.max(0.0, viewportHeight - anchor.bottom - 4.0);
        double above = Math.max(0.0, anchor.top - 4.0);
        boolean openBelow = below >= Math.min(desiredHeight, 96.0) || below >= above;
        double available = Math.max(30.0, openBelow ? below : above);
        double maxHeight = Math.min(desiredHeight, available);
        double actualHeight = Math.min(desiredHeight, maxHeight);
        double left = SelectPopup.clamp(anchor.left, 4.0, Math.max(4.0, viewportWidth - width - 4.0));
        double top = openBelow ? anchor.bottom : anchor.top - actualHeight;
        top = SelectPopup.clamp(top, 4.0, Math.max(4.0, viewportHeight - actualHeight - 4.0));
        this.panel.setAttribute("style", "position:fixed;z-index:10000;box-sizing:border-box;background:#ffffff;border:1px solid #767676;border-radius:2px;box-shadow:0 2px 6px rgba(0,0,0,0.28);overflow-x:hidden;overflow-y:auto;padding:2px 0;color:#000000;pointer-events:auto;left:" + SelectPopup.px(left) + ";top:" + SelectPopup.px(top) + ";width:" + SelectPopup.px(width) + ";max-height:" + SelectPopup.px(maxHeight) + ";" + SelectPopup.inheritedFontStyle(this.select));
    }

    private void revealActiveRow() {
        if (this.panel == null || this.activeIndex < 0 || this.activeIndex >= this.rows.size()) {
            return;
        }
        Element.DOMRect panelRect = this.panel.getBoundingClientRect();
        Element.DOMRect rowRect = this.rows.get(this.activeIndex).getBoundingClientRect();
        double nextScroll = this.panel.getTargetScrollTop();
        if (rowRect.top < panelRect.top) {
            nextScroll -= panelRect.top - rowRect.top;
        } else if (rowRect.bottom > panelRect.bottom) {
            nextScroll += rowRect.bottom - panelRect.bottom;
        }
        if (Double.compare(nextScroll, this.panel.getTargetScrollTop()) != 0) {
            this.panel.setScrollTop(nextScroll);
        }
    }

    private int initialActiveIndex() {
        int selected = this.select.getSelectedIndex();
        if (selected >= 0 && selected < this.options.size() && !this.options.get(selected).isOptionEffectivelyDisabled()) {
            return selected;
        }
        return this.findEnabled(-1, 1, false);
    }

    private int findEnabled(int from, int direction, boolean wrap) {
        if (this.options.isEmpty()) {
            return -1;
        }
        int index = from;
        for (int checked = 0; checked < this.options.size(); ++checked) {
            index += direction;
            if (wrap) {
                if (index < 0) {
                    index = this.options.size() - 1;
                }
                if (index >= this.options.size()) {
                    index = 0;
                }
            } else if (index < 0 || index >= this.options.size()) {
                return -1;
            }
            if (this.options.get(index).isOptionEffectivelyDisabled()) continue;
            return index;
        }
        return -1;
    }

    private Element element(String tag, String classes) {
        Element element = Element.init(this.document.createElement(tag));
        if (classes != null && !classes.isBlank()) {
            element.setAttribute("class", classes);
        }
        return element;
    }

    private static Element optionGroup(Element option) {
        Element parent = option == null ? null : option.parentElement;
        return parent != null && "OPTGROUP".equalsIgnoreCase(parent.tagName) ? parent : null;
    }

    private void markDirty() {
        if (this.document != null && this.document.body != null) {
            this.document.markDirty(this.document.body, 15);
        }
    }

    private static String inheritedFontStyle(Element element) {
        Style style = element.getComputedStyle();
        return SelectPopup.css("font-family", style.fontFamily) + SelectPopup.css("font-size", style.fontSize) + SelectPopup.css("font-weight", style.fontWeight) + SelectPopup.css("font-style", style.fontStyle) + SelectPopup.css("line-height", style.lineHeight) + SelectPopup.css("letter-spacing", style.letterSpacing);
    }

    private static String css(String property, String value) {
        if (value == null || value.isBlank() || "unset".equalsIgnoreCase(value)) {
            return "";
        }
        return property + ":" + value + ";";
    }

    private static String inherited(String value, String fallback) {
        return value == null || value.isBlank() || "unset".equalsIgnoreCase(value) ? fallback : value;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    private static String px(double value) {
        return String.format(Locale.ROOT, "%.2fpx", value);
    }
}

