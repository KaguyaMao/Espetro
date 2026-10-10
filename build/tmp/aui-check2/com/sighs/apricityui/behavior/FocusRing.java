/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.behavior;

import com.sighs.apricityui.behavior.DocumentSelection;
import com.sighs.apricityui.behavior.SelectionUnits;
import com.sighs.apricityui.behavior.richtext.RichTextSelection;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public final class FocusRing {
    private final Document owner;
    private Element previousCursorElement = null;
    private Element activeElement = null;
    private Element focusedElement = null;

    public FocusRing(Document owner) {
        this.owner = owner;
    }

    public Element getPreviousCursorElement() {
        return this.previousCursorElement;
    }

    public void setPreviousCursorElement(Element element) {
        this.previousCursorElement = element;
    }

    public Element getPressedElement() {
        return this.activeElement;
    }

    public void setPressedElement(Element element) {
        if (this.activeElement == element) {
            return;
        }
        List<Element> oldChain = this.activeElement != null ? this.activeElement.getRoute() : Collections.emptyList();
        List<Element> newChain = element != null ? element.getRoute() : Collections.emptyList();
        Set oldSet = Collections.newSetFromMap(new IdentityHashMap());
        oldSet.addAll(oldChain);
        Set newSet = Collections.newSetFromMap(new IdentityHashMap());
        newSet.addAll(newChain);
        for (Element e : oldChain) {
            if (newSet.contains(e)) continue;
            e.setActive(false);
        }
        for (Element e : newChain) {
            if (oldSet.contains(e)) continue;
            e.setActive(true);
        }
        this.activeElement = element;
    }

    public Element getFocusedElement() {
        return this.focusedElement;
    }

    public void setFocusedElement(Element element) {
        if (this.focusedElement != null && this.focusedElement != element) {
            Element previous = this.focusedElement;
            previous.setFocus(false);
            FocusRing.dispatchFocusEvent(previous, "blur");
        }
        this.focusedElement = element;
        if (element != null) {
            element.setFocus(true);
            FocusRing.dispatchFocusEvent(element, "focus");
        }
    }

    private static void dispatchFocusEvent(Element element, String type) {
        if (element == null || type == null || type.isBlank()) {
            return;
        }
        Event event = new Event(element, type, null, false);
        event.bubbles = false;
        Event.markTrustedFromCurrentDispatch(event);
        Event.triggerSingle(event);
    }

    public boolean hasAnyTextSelection() {
        if (this.owner.getDocumentSelection().isActive()) {
            return true;
        }
        for (Element element : this.owner.getElements()) {
            AbstractText textElement;
            if (!(element instanceof AbstractText) || !(textElement = (AbstractText)element).hasSelection()) continue;
            return true;
        }
        return false;
    }

    public void clearAllTextSelections() {
        this.clearAllTextSelectionsExcept(null);
    }

    public void clearAllTextSelectionsExcept(Element keep) {
        RichTextSelection rich;
        Element keepUnit;
        DocumentSelection selection = this.owner.getDocumentSelection();
        Element element = keepUnit = keep == null ? null : SelectionUnits.resolveUnit(keep);
        if (keepUnit == null || selection.getAnchorUnit() != keepUnit || selection.getEndUnit() != keepUnit) {
            selection.clear();
        }
        if (!(rich = this.owner.getRichTextSelection()).isActive() || keepUnit == null || !rich.coversUnit(keepUnit)) {
            rich.clear();
        }
        for (Element element2 : this.owner.getElements()) {
            AbstractText textElement;
            if (element2 == keep || !(element2 instanceof AbstractText) || !(textElement = (AbstractText)element2).hasSelection()) continue;
            textElement.clearSelection();
        }
    }

    public void clearFocus() {
        this.setFocusedElement(null);
    }
}

