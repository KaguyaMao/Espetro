/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package com.sighs.apricityui.dev.debug;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sighs.apricityui.dev.debug.DebugProtocolException;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.style.Interaction;

final class DebugInput {
    private DebugInput() {
    }

    static JsonObject hover(Document document, Element element) {
        Position screen = DebugInput.actionableCenter(document, element, false);
        MouseEvent.tiggerEvent(DebugInput.mouseEvent("mousemove", screen, -1, 0), document);
        return DebugInput.pointResult(screen);
    }

    static JsonObject click(Document document, Element element) {
        Position screen = DebugInput.actionableCenter(document, element, true);
        MouseEvent.tiggerEvent(DebugInput.mouseEvent("mousemove", screen, -1, 0), document);
        MouseEvent.tiggerEvent(DebugInput.mouseEvent("mousedown", screen, 0, 1), document);
        MouseEvent.tiggerEvent(DebugInput.mouseEvent("mouseup", screen, 0, 0), document);
        return DebugInput.pointResult(screen);
    }

    static JsonObject fill(Element element, String value) {
        AbstractText text;
        if (!(element instanceof AbstractText) || !(text = (AbstractText)element).canEditText()) {
            throw new DebugProtocolException(-32003, "Element is not an editable input or textarea");
        }
        if (element.isDisabled()) {
            throw new DebugProtocolException(-32003, "Element is disabled");
        }
        text.focus();
        text.selectAll();
        text.replaceSelection(value == null ? "" : value);
        JsonObject result = new JsonObject();
        result.addProperty("value", text.getValue());
        return result;
    }

    private static Position actionableCenter(Document document, Element element, boolean requirePointerTarget) {
        if (!(Interaction.isDisplayed(element) && Interaction.isVisible(element) && element.isVisible)) {
            throw new DebugProtocolException(-32003, "Element is not visible");
        }
        if (requirePointerTarget && !element.isPointerEnabled) {
            throw new DebugProtocolException(-32003, "Element does not accept pointer events");
        }
        Element.DOMRect rect = element.getBoundingClientRect();
        if (rect.width <= 0.0 || rect.height <= 0.0) {
            throw new DebugProtocolException(-32003, "Element has no rendered size");
        }
        Position documentPosition = new Position(rect.x + rect.width / 2.0, rect.y + rect.height / 2.0);
        Element hit = document.hitTest(documentPosition);
        if (requirePointerTarget && (hit == null || !element.contains(hit))) {
            throw new DebugProtocolException(-32003, "Element center is covered or outside the hit-test region");
        }
        return document.documentToScreenPosition(documentPosition);
    }

    private static MouseEvent mouseEvent(String type, Position position, int button, int buttons) {
        MouseEvent event = new MouseEvent(type, position, button, false);
        event.buttons = buttons;
        return event;
    }

    private static JsonObject pointResult(Position position) {
        JsonObject point = new JsonObject();
        point.addProperty("x", (Number)position.x);
        point.addProperty("y", (Number)position.y);
        JsonObject result = new JsonObject();
        result.add("point", (JsonElement)point);
        return result;
    }
}

