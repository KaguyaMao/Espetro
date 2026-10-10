/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.element.SelectPopup;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.KeyEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.FontDrawer;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.style.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@ElementRegister(value="SELECT")
public class Select
extends Element {
    public static final String TAG_NAME = "SELECT";
    private static final long TYPEAHEAD_TIMEOUT_NS = 700000000L;
    private SelectPopup popup;
    private String typeahead = "";
    private long lastTypeaheadNs;

    public Select(Document document) {
        super(document, TAG_NAME);
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
                Base.offsetPaintDepth(poseStack, 0.16f);
                Text text = Text.of(this);
                text.content = this.selectedLabel();
                Position contentPosition = rectRenderer.getContentPosition();
                double contentHeight = Math.max(0.0, rectRenderer.box.innerSize().height());
                double drawY = contentPosition.y + (contentHeight - text.lineHeight) / 2.0;
                FontDrawer.drawFont(poseStack, text, new Position(contentPosition.x, drawY));
                if (!this.showsNativeArrow()) break;
                this.drawNativeArrow(poseStack, rectRenderer);
                break;
            }
            case BORDER: {
                rectRenderer.drawBorder(poseStack);
            }
        }
    }

    @Override
    public boolean canFocus() {
        return true;
    }

    public Size getIntrinsicSize() {
        Text text = Text.of(this);
        String label = this.selectedLabel();
        double nativeLineHeight = Text.calculateLineHeight(text.fontSize, "normal");
        return new Size(Size.measureText(this, label) + 20.0, Math.max(0.0, nativeLineHeight));
    }

    private void drawNativeArrow(PoseStack poseStack, Rect rectRenderer) {
        double right = rectRenderer.position.x + rectRenderer.box.elementSize().width() - rectRenderer.box.getBorderRight() - rectRenderer.box.getPaddingRight();
        double centerY = rectRenderer.position.y + rectRenderer.box.getMarginTop() + rectRenderer.box.elementSize().height() / 2.0;
        float x = (float)(right - 7.0);
        float y = (float)(centerY - 2.0);
        int color = new Color(this.isDisabled() ? "#797A7D" : "#D8D8D8").getValue();
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x, y, x + 7.0f, y + 1.0f, color);
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x + 1.0f, y + 1.0f, x + 6.0f, y + 2.0f, color);
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x + 2.0f, y + 2.0f, x + 5.0f, y + 3.0f, color);
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x + 3.0f, y + 3.0f, x + 4.0f, y + 4.0f, color);
    }

    boolean showsNativeArrow() {
        return !"none".equalsIgnoreCase(this.getComputedStyle().appearance) && !"false".equalsIgnoreCase(this.getAttribute("data-native-arrow"));
    }

    private String selectedLabel() {
        int selectedIndex = this.getSelectedIndex();
        List<Element> options = this.getOptions();
        return selectedIndex >= 0 && selectedIndex < options.size() ? options.get(selectedIndex).getOptionLabel() : "";
    }

    public boolean isPopupOpen() {
        return this.popup != null && this.popup.isOpen();
    }

    public void openPopup() {
        if (this.isDisabled() || this.getOptions().isEmpty() || this.isPopupOpen()) {
            return;
        }
        this.popup = SelectPopup.open(this);
    }

    public void closePopup() {
        if (this.popup != null) {
            this.popup.close();
        }
    }

    @Override
    public void handleClickDefault() {
        if (this.isDisabled()) {
            return;
        }
        if (this.isPopupOpen()) {
            this.closePopup();
        } else {
            this.openPopup();
        }
    }

    public boolean handleKeyDownDefault(KeyEvent event) {
        if (event == null || this.isDisabled()) {
            return false;
        }
        String key = event.key;
        if ("Tab".equals(key) && this.isPopupOpen()) {
            this.closePopup();
            return false;
        }
        if ("Escape".equals(key)) {
            if (!this.isPopupOpen()) {
                return false;
            }
            this.closePopup();
            return true;
        }
        if ("Enter".equals(key) || " ".equals(key)) {
            if (!this.isPopupOpen()) {
                this.openPopup();
                return true;
            }
            return this.popup.commitActive();
        }
        if ("ArrowDown".equals(key) || "ArrowUp".equals(key)) {
            int direction;
            int n = direction = "ArrowDown".equals(key) ? 1 : -1;
            if (event.altKey && !this.isPopupOpen()) {
                this.openPopup();
            } else if (this.isPopupOpen()) {
                this.popup.move(direction);
            } else {
                this.moveClosedSelection(direction, false);
            }
            return true;
        }
        if ("Home".equals(key) || "End".equals(key)) {
            boolean end = "End".equals(key);
            if (this.isPopupOpen()) {
                this.popup.moveToBoundary(end);
            } else {
                this.moveClosedSelectionToBoundary(end);
            }
            return true;
        }
        if ("PageUp".equals(key) || "PageDown".equals(key)) {
            int distance;
            int n = distance = "PageDown".equals(key) ? 10 : -10;
            if (this.isPopupOpen()) {
                this.popup.move(distance);
            } else {
                this.moveClosedSelection(distance, false);
            }
            return true;
        }
        if (!event.controlKey && !event.altKey && !event.metaKey && Select.isPrintableKey(key)) {
            return this.handleTypeahead(key);
        }
        return false;
    }

    void onPopupClosed(SelectPopup candidate) {
        if (this.popup == candidate) {
            this.popup = null;
        }
    }

    @Override
    public void onDisconnectedFromDocument() {
        this.closePopup();
    }

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        if (disabled) {
            this.closePopup();
        }
    }

    void commitUserSelection(int index) {
        List<Element> options = this.getOptions();
        if (index < 0 || index >= options.size()) {
            return;
        }
        Element option = options.get(index);
        if (option.isOptionEffectivelyDisabled()) {
            return;
        }
        List<Element> previous = this.selectedIdentitySnapshot();
        if (this.isMultiple()) {
            option.setSelected(!option.isSelected());
        } else {
            option.setSelected(true);
        }
        this.dispatchUserSelectionChangeEvents(previous);
    }

    private void moveClosedSelection(int distance, boolean wrap) {
        int next;
        List<Element> options = this.getOptions();
        if (options.isEmpty()) {
            return;
        }
        int direction = distance < 0 ? -1 : 1;
        int remaining = Math.max(1, Math.abs(distance));
        int index = this.getSelectedIndex();
        for (int step = 0; step < remaining && (next = Select.findEnabledOption(options, index, direction, wrap)) >= 0; ++step) {
            index = next;
        }
        if (index >= 0 && index != this.getSelectedIndex()) {
            this.commitUserSelection(index);
        }
    }

    private void moveClosedSelectionToBoundary(boolean end) {
        List<Element> options = this.getOptions();
        int index = Select.findEnabledOption(options, end ? options.size() : -1, end ? -1 : 1, false);
        if (index >= 0 && index != this.getSelectedIndex()) {
            this.commitUserSelection(index);
        }
    }

    private boolean handleTypeahead(String key) {
        int start;
        long now = System.nanoTime();
        if (now - this.lastTypeaheadNs > 700000000L) {
            this.typeahead = "";
        }
        this.lastTypeaheadNs = now;
        String token = key.toLowerCase(Locale.ROOT);
        this.typeahead = this.typeahead + token;
        List<Element> options = this.getOptions();
        int match = this.findPrefixMatch(options, this.typeahead, start = this.isPopupOpen() ? this.popup.getActiveIndex() : this.getSelectedIndex());
        if (match < 0 && this.typeahead.length() > 1) {
            this.typeahead = token;
            match = this.findPrefixMatch(options, this.typeahead, start);
        }
        if (match < 0) {
            return false;
        }
        if (this.isPopupOpen()) {
            this.popup.setActiveIndex(match, true);
        } else if (match != this.getSelectedIndex()) {
            this.commitUserSelection(match);
        }
        return true;
    }

    private int findPrefixMatch(List<Element> options, String prefix, int start) {
        if (options.isEmpty()) {
            return -1;
        }
        for (int offset = 1; offset <= options.size(); ++offset) {
            int index = Math.floorMod(start + offset, options.size());
            Element option = options.get(index);
            if (option.isOptionEffectivelyDisabled() || !option.getOptionLabel().toLowerCase(Locale.ROOT).startsWith(prefix)) continue;
            return index;
        }
        return -1;
    }

    private static int findEnabledOption(List<Element> options, int from, int direction, boolean wrap) {
        if (options.isEmpty()) {
            return -1;
        }
        int index = from;
        for (int checked = 0; checked < options.size(); ++checked) {
            index += direction;
            if (wrap) {
                index = Math.floorMod(index, options.size());
            } else if (index < 0 || index >= options.size()) {
                return -1;
            }
            if (options.get(index).isOptionEffectivelyDisabled()) continue;
            return index;
        }
        return -1;
    }

    private static boolean isPrintableKey(String key) {
        return key != null && key.codePointCount(0, key.length()) == 1 && !Character.isISOControl(key.codePointAt(0));
    }

    private List<Element> selectedIdentitySnapshot() {
        return new ArrayList<Element>(this.getSelectedOptions());
    }

    private void dispatchUserSelectionChangeEvents(List<Element> previousSelection) {
        if (Objects.equals(previousSelection, this.getSelectedOptions())) {
            return;
        }
        Event inputEvent = new Event(this, "input", true);
        Event.markTrustedFromCurrentDispatch(inputEvent);
        Event.tiggerEvent(inputEvent);
        Event changeEvent = new Event(this, "change", true);
        Event.markTrustedFromCurrentDispatch(changeEvent);
        Event.tiggerEvent(changeEvent);
    }
}

