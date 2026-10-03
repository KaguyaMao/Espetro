/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.util;

import com.sighs.apricityui.dom.CommentNode;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class HtmlSerializer {
    private static final Set<String> VOID_ELEMENTS = Set.of("area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr");

    private HtmlSerializer() {
    }

    public static String serializeNode(Node node) {
        if (node == null) {
            return "";
        }
        if (node instanceof TextNode) {
            TextNode textNode = (TextNode)node;
            return HtmlSerializer.escapeHtml(textNode.getTextContent());
        }
        if (node instanceof CommentNode) {
            CommentNode commentNode = (CommentNode)node;
            return "<!--" + HtmlSerializer.escapeHtml(commentNode.getTextContent()) + "-->";
        }
        if (!(node instanceof Element)) {
            return "";
        }
        Element element = (Element)node;
        return HtmlSerializer.serializeHtml(element);
    }

    public static String serializeHtml(Element element) {
        if (element == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('<').append(element.tagName.toLowerCase(Locale.ROOT));
        for (Map.Entry<String, String> entry : element.getAttributes().entrySet()) {
            String key = entry.getKey();
            if (key == null || key.isBlank()) continue;
            builder.append(' ').append(key);
            String value = entry.getValue();
            if (value == null || value.isEmpty()) continue;
            builder.append("=\"").append(HtmlSerializer.escapeHtml(value)).append('\"');
        }
        builder.append('>');
        String tagName = element.tagName.toLowerCase(Locale.ROOT);
        if (VOID_ELEMENTS.contains(tagName)) {
            return builder.toString();
        }
        if (!element.childNodes.isEmpty()) {
            for (Node child : element.childNodes) {
                builder.append(HtmlSerializer.serializeNode(child));
            }
        } else if (!element.innerText.isEmpty()) {
            builder.append(HtmlSerializer.escapeHtml(element.innerText));
        }
        builder.append("</").append(tagName).append('>');
        return builder.toString();
    }

    public static String escapeHtml(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}

