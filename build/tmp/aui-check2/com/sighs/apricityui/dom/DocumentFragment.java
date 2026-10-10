/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dom;

import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Node;
import java.util.ArrayList;

public class DocumentFragment
extends Node {
    public static final short DOCUMENT_FRAGMENT_NODE = 11;

    public DocumentFragment(Document document) {
        super(document);
    }

    @Override
    public short getNodeType() {
        return 11;
    }

    @Override
    public String getNodeName() {
        return "#document-fragment";
    }

    @Override
    public String getTextContent() {
        StringBuilder builder = new StringBuilder();
        for (Node child : this.childNodes) {
            String text;
            if (child == null || (text = child.getTextContent()) == null) continue;
            builder.append(text);
        }
        return builder.toString();
    }

    @Override
    public void setTextContent(String value) {
        for (Node child : new ArrayList(this.childNodes)) {
            this.removeChild(child);
        }
        if (value == null || value.isEmpty()) {
            return;
        }
        this.appendChild(new TextNode(this.document, value));
    }
}

