/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.behavior.richtext;

import com.sighs.apricityui.behavior.richtext.RangeBridge;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import java.util.List;

public class TreeWalkerBridge {
    public static final int SHOW_ALL = -1;
    public static final int SHOW_TEXT = 4;
    private final List<TextNode> nodes;
    private int index = -1;

    public TreeWalkerBridge(Element root, int whatToShow) {
        this.nodes = RangeBridge.collectTextNodes(root, whatToShow);
    }

    public Node nextNode() {
        ++this.index;
        return this.index < this.nodes.size() ? (Node)this.nodes.get(this.index) : null;
    }

    public Node getCurrentNode() {
        return this.index >= 0 && this.index < this.nodes.size() ? (Node)this.nodes.get(this.index) : null;
    }

    public String getNodeValue() {
        Node current = this.getCurrentNode();
        return current == null ? null : current.getNodeValue();
    }
}

