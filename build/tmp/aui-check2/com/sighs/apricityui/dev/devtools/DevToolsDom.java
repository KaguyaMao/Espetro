/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dev.devtools;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import java.util.ArrayList;

final class DevToolsDom {
    private DevToolsDom() {
    }

    static Element element(Document document, String tag, String className) {
        Element element = Element.init(document.createElement(tag));
        if (className != null && !className.isBlank()) {
            element.setAttribute("class", className);
        }
        return element;
    }

    static Element text(Document document, String tag, String className, String value) {
        Element element = DevToolsDom.element(document, tag, className);
        element.setTextContent(value == null ? "" : value);
        return element;
    }

    static Element input(Document document, String className, String value, String placeholder) {
        Element input = DevToolsDom.element(document, "INPUT", className);
        input.setAttribute("type", "text");
        input.value = value == null ? "" : value;
        input.setAttribute("value", input.value);
        if (placeholder != null && !placeholder.isBlank()) {
            input.setAttribute("placeholder", placeholder);
        }
        return input;
    }

    static void clear(Element element) {
        if (element == null) {
            return;
        }
        new ArrayList<Element>(element.children).forEach(Element::remove);
        element.setTextContent("");
    }

    static void setClass(Element element, String className) {
        if (element != null) {
            element.setAttribute("class", className == null ? "" : className);
        }
    }

    static void markDirty(Document document) {
        if (document == null || document.body == null) {
            return;
        }
        document.markDirty(document.body, 15);
    }

    static String value(Element input) {
        if (input == null) {
            return "";
        }
        return input.value == null ? input.getAttribute("value") : input.value;
    }
}

