/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dom;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import java.util.Objects;

public class TextNode
extends Node {
    private String data;

    public TextNode(Document document, String data) {
        super(document);
        this.data = data == null ? "" : data;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String value) {
        this.setTextContent(value);
    }

    @Override
    public short getNodeType() {
        return 3;
    }

    @Override
    public String getNodeName() {
        return "#text";
    }

    @Override
    public String getNodeValue() {
        return this.data;
    }

    @Override
    public String getTextContent() {
        return this.data;
    }

    @Override
    public void setTextContent(String value) {
        String normalized = value == null ? "" : value;
        String oldValue = this.data;
        this.data = normalized;
        Node node = this.parentNode;
        if (node instanceof Element) {
            Element parentElement = (Element)node;
            parentElement.getRenderer().text.clear();
            parentElement.getRenderer().wrappedText.clear();
            parentElement.getRenderer().size.clear();
            if (this.document != null) {
                this.document.markDirty(parentElement, 5);
            }
        }
        if (this.document != null) {
            this.document.bumpSelectionCache();
        }
        if (this.document != null && !Objects.equals(oldValue, normalized)) {
            this.document.queueMutation(Document.MutationRecord.characterData(this, oldValue));
        }
    }

    public void replaceData(int offset, int count, String replacement) {
        int len = this.data.length();
        int start = Math.max(0, Math.min(offset, len));
        int end = Math.max(start, Math.min(start + Math.max(0, count), len));
        String next = this.data.substring(0, start) + (replacement == null ? "" : replacement) + this.data.substring(end);
        this.setTextContent(next);
    }

    public TextNode splitText(int offset) {
        int len = this.data.length();
        int split = Math.max(0, Math.min(offset, len));
        String head = this.data.substring(0, split);
        String tail = this.data.substring(split);
        this.setTextContent(head);
        TextNode tailNode = new TextNode(this.document, tail);
        if (this.parentNode != null) {
            this.parentNode.insertBefore(tailNode, this.getNextSibling());
        }
        return tailNode;
    }

    public String toString() {
        return this.data;
    }
}

