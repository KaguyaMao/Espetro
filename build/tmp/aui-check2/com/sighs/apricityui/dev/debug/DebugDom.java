/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 */
package com.sighs.apricityui.dev.debug;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.sighs.apricityui.dev.debug.DebugProtocolException;
import com.sighs.apricityui.element.Div;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.TreeMap;

final class DebugDom {
    static final int DEFAULT_MAX_DEPTH = 32;
    static final int DEFAULT_MAX_NODES = 5000;
    static final int MAX_DEPTH = 128;
    static final int MAX_NODES = 20000;

    private DebugDom() {
    }

    static JsonObject query(Document document, String selector) {
        Element element;
        try {
            element = document.querySelector(selector);
        }
        catch (RuntimeException invalidSelector) {
            throw DebugDom.invalidSelector(selector);
        }
        JsonObject result = new JsonObject();
        if (element == null) {
            result.add("nodeId", (JsonElement)JsonNull.INSTANCE);
        } else {
            result.addProperty("nodeId", element.uuid.toString());
        }
        return result;
    }

    static JsonObject queryAll(Document document, String selector) {
        JsonArray ids = new JsonArray();
        try {
            for (Element element : document.querySelectorAll(selector)) {
                ids.add(element.uuid.toString());
            }
        }
        catch (RuntimeException invalidSelector) {
            throw DebugDom.invalidSelector(selector);
        }
        JsonObject result = new JsonObject();
        result.add("nodeIds", (JsonElement)ids);
        return result;
    }

    static JsonObject snapshot(Document document, int maxDepth, int maxNodes) {
        Div root = document.documentElement != null ? document.documentElement : document.body;
        JsonObject result = new JsonObject();
        if (root == null) {
            result.add("root", (JsonElement)JsonNull.INSTANCE);
            result.addProperty("nodeCount", (Number)0);
            return result;
        }
        Counter counter = new Counter(maxNodes);
        result.add("root", (JsonElement)DebugDom.snapshotNode(root, 0, maxDepth, counter));
        result.addProperty("nodeCount", (Number)counter.count);
        return result;
    }

    static JsonObject attributes(Element element) {
        JsonObject attributes = new JsonObject();
        for (Map.Entry<String, String> entry : new TreeMap<String, String>(element.getAttributes()).entrySet()) {
            attributes.addProperty(entry.getKey(), entry.getValue());
        }
        JsonObject result = new JsonObject();
        result.add("attributes", (JsonElement)attributes);
        return result;
    }

    static JsonObject text(Node node) {
        JsonObject result = new JsonObject();
        result.addProperty("text", node.getTextContent());
        return result;
    }

    static JsonObject computedStyle(Element element) {
        JsonObject result = new JsonObject();
        result.addProperty("cssText", element.getComputedStyle().toCss());
        return result;
    }

    static JsonObject boxModel(Document document, Element element) {
        Element.DOMRect border = element.getBoundingClientRect();
        Box box = Box.of(element);
        JsonObject result = new JsonObject();
        result.add("margin", (JsonElement)DebugDom.screenRect(document, border.x - box.getMarginLeft(), border.y - box.getMarginTop(), border.width + box.getMarginHorizontal(), border.height + box.getMarginVertical()));
        result.add("border", (JsonElement)DebugDom.screenRect(document, border.x, border.y, border.width, border.height));
        double paddingX = border.x + box.getBorderLeft();
        double paddingY = border.y + box.getBorderTop();
        double paddingWidth = Math.max(0.0, border.width - box.getBorderHorizontal());
        double paddingHeight = Math.max(0.0, border.height - box.getBorderVertical());
        result.add("padding", (JsonElement)DebugDom.screenRect(document, paddingX, paddingY, paddingWidth, paddingHeight));
        result.add("content", (JsonElement)DebugDom.screenRect(document, paddingX + box.getPaddingLeft(), paddingY + box.getPaddingTop(), Math.max(0.0, paddingWidth - box.getPaddingHorizontal()), Math.max(0.0, paddingHeight - box.getPaddingVertical())));
        return result;
    }

    static Element requireElement(Document document, String nodeId) {
        Node node = DebugDom.requireNode(document, nodeId);
        if (!(node instanceof Element)) {
            throw new DebugProtocolException(-32002, "Node is detached");
        }
        Element element = (Element)node;
        return element;
    }

    static Node requireNode(Document document, String nodeId) {
        if (nodeId == null || nodeId.isBlank()) {
            throw new DebugProtocolException(-32602, "nodeId is required");
        }
        Node node = DebugDom.findNode(document, nodeId);
        if (node == null || node.document != document || !node.isConnected()) {
            throw new DebugProtocolException(-32002, "Node is detached");
        }
        return node;
    }

    private static Node findNode(Document document, String nodeId) {
        Div root;
        Div div = root = document.documentElement != null ? document.documentElement : document.body;
        if (root == null) {
            return null;
        }
        ArrayDeque<Node> pending = new ArrayDeque<Node>();
        pending.push(root);
        while (!pending.isEmpty()) {
            Node node = (Node)pending.pop();
            if (node.uuid.toString().equals(nodeId)) {
                return node;
            }
            for (int index = node.childNodes.size() - 1; index >= 0; --index) {
                Node child = node.childNodes.get(index);
                if (child == null) continue;
                pending.push(child);
            }
        }
        return null;
    }

    private static DebugProtocolException invalidSelector(String selector) {
        return new DebugProtocolException(-32602, "Invalid selector: " + selector);
    }

    private static JsonObject snapshotNode(Node node, int depth, int maxDepth, Counter counter) {
        counter.increment();
        JsonObject result = new JsonObject();
        result.addProperty("nodeId", node.uuid.toString());
        result.addProperty("nodeType", (Number)node.getNodeType());
        result.addProperty("nodeName", node.getNodeName());
        if (node instanceof Element) {
            Element element = (Element)node;
            JsonObject attributes = new JsonObject();
            for (Map.Entry<String, String> entry : new TreeMap<String, String>(element.getAttributes()).entrySet()) {
                attributes.addProperty(entry.getKey(), entry.getValue());
            }
            result.add("attributes", (JsonElement)attributes);
        } else {
            result.addProperty("text", node.getTextContent());
        }
        JsonArray children = new JsonArray();
        if (depth < maxDepth) {
            for (Node child : node.childNodes) {
                if (child == null) continue;
                children.add((JsonElement)DebugDom.snapshotNode(child, depth + 1, maxDepth, counter));
            }
        }
        result.add("children", (JsonElement)children);
        return result;
    }

    private static JsonObject screenRect(Document document, double x, double y, double width, double height) {
        Position position = document.documentToScreenPosition(new Position(x, y));
        JsonObject rect = new JsonObject();
        rect.addProperty("x", (Number)position.x);
        rect.addProperty("y", (Number)position.y);
        rect.addProperty("width", (Number)(width * document.getViewportScaleX()));
        rect.addProperty("height", (Number)(height * document.getViewportScaleY()));
        rect.addProperty("left", (Number)position.x);
        rect.addProperty("top", (Number)position.y);
        rect.addProperty("right", (Number)(position.x + width * document.getViewportScaleX()));
        rect.addProperty("bottom", (Number)(position.y + height * document.getViewportScaleY()));
        return rect;
    }

    private static final class Counter {
        private final int limit;
        private int count;

        private Counter(int limit) {
            this.limit = limit;
        }

        private void increment() {
            ++this.count;
            if (this.count > this.limit) {
                throw new DebugProtocolException(-32004, "DOM snapshot exceeds maxNodes=" + this.limit);
            }
        }
    }
}

