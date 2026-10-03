/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dom;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Node;
import java.util.Objects;

public class CommentNode
extends Node {
    private String data;

    public CommentNode(Document document, String data) {
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
        return 8;
    }

    @Override
    public String getNodeName() {
        return "#comment";
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
        if (this.document != null && !Objects.equals(oldValue, normalized)) {
            this.document.queueMutation(Document.MutationRecord.characterData(this, oldValue));
        }
    }

    public String toString() {
        return "<!--" + this.data + "-->";
    }
}

