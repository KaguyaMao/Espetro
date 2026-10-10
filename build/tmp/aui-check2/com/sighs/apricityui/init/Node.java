/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.init;

import com.sighs.apricityui.dom.CommentNode;
import com.sighs.apricityui.dom.DocumentFragment;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.EventRegistry;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class Node {
    public static final short ELEMENT_NODE = 1;
    public static final short TEXT_NODE = 3;
    public static final short COMMENT_NODE = 8;
    public static final short DOCUMENT_FRAGMENT_NODE = 11;
    public UUID uuid = UUID.randomUUID();
    public Document document;
    public Node parentNode = null;
    public final ArrayList<Node> childNodes = new ArrayList();
    public int depth = 0;
    private long subtreeMutationVersion = 1L;
    private final EventRegistry events = new EventRegistry(this);
    public CopyOnWriteArrayList<Event.ListenerRecord> EventListener = this.events.listeners();

    protected Node(Document document) {
        this.document = document;
    }

    public Document getOwnerDocument() {
        return this.document;
    }

    public Node getParentNode() {
        return this.parentNode;
    }

    public Element getParentElement() {
        Element parent;
        Node node = this.parentNode;
        return node instanceof Element ? (parent = (Element)node) : null;
    }

    public List<Node> getChildNodes() {
        return Collections.unmodifiableList(this.childNodes);
    }

    public long getSubtreeMutationVersion() {
        return this.subtreeMutationVersion;
    }

    public void invalidateSubtreeMutationVersion() {
        Node current = this;
        while (current != null) {
            ++current.subtreeMutationVersion;
            current = current.parentNode;
        }
    }

    public Node getFirstChild() {
        return this.childNodes.isEmpty() ? null : this.childNodes.get(0);
    }

    public Node getLastChild() {
        return this.childNodes.isEmpty() ? null : this.childNodes.get(this.childNodes.size() - 1);
    }

    public boolean hasChildNodes() {
        return !this.childNodes.isEmpty();
    }

    public Node getNextSibling() {
        if (this.parentNode == null) {
            return null;
        }
        int index = this.parentNode.childNodes.indexOf(this);
        if (index < 0 || index + 1 >= this.parentNode.childNodes.size()) {
            return null;
        }
        return this.parentNode.childNodes.get(index + 1);
    }

    public Node getPreviousSibling() {
        if (this.parentNode == null) {
            return null;
        }
        int index = this.parentNode.childNodes.indexOf(this);
        if (index <= 0) {
            return null;
        }
        return this.parentNode.childNodes.get(index - 1);
    }

    public ArrayList<Node> getRouteNodes() {
        ArrayList<Node> result = new ArrayList<Node>();
        Node current = this;
        while (current != null) {
            result.add(current);
            current = current.parentNode;
        }
        return result;
    }

    public boolean contains(Node node) {
        if (node == null) {
            return false;
        }
        Node current = node;
        while (current != null) {
            if (current == this) {
                return true;
            }
            current = current.parentNode;
        }
        return false;
    }

    public boolean isConnected() {
        Node current = this;
        while (current != null) {
            block3: {
                Element root;
                block5: {
                    block4: {
                        if (current.parentNode != null) break block3;
                        if (!(current instanceof Element)) break block4;
                        root = (Element)current;
                        if (root.document != null) break block5;
                    }
                    return false;
                }
                return root.document.documentElement == root || root.document.body == root;
            }
            current = current.parentNode;
        }
        return false;
    }

    public Node appendChild(Node node) {
        if (this.document == null || node == null) {
            return null;
        }
        if (node instanceof DocumentFragment) {
            DocumentFragment fragment = (DocumentFragment)node;
            if (this.isConnected()) {
                return this.document.getTree().insertFragment(fragment, this, null);
            }
            Node last = null;
            ArrayList snapshot = new ArrayList(fragment.childNodes);
            for (Node child : snapshot) {
                last = this.appendSingleChild(this.prepareForInsertion(child));
            }
            return last;
        }
        return this.appendSingleChild(this.prepareForInsertion(node));
    }

    public Node removeChild(Node node) {
        if (this.document == null || node == null || node.parentNode != this) {
            return null;
        }
        this.document.removeNode(node);
        return node;
    }

    public void clearChildren() {
        if (this.document == null || this.childNodes.isEmpty()) {
            return;
        }
        if (this.isConnected()) {
            this.document.getTree().clearChildren(this);
            return;
        }
        ArrayList<Node> snapshot = new ArrayList<Node>(this.childNodes);
        for (Node child : snapshot) {
            Node.detachLocalChild(child);
        }
    }

    public Node insertBefore(Node newNode, Node referenceNode) {
        if (this.document == null || newNode == null) {
            return null;
        }
        if (newNode instanceof DocumentFragment) {
            DocumentFragment fragment = (DocumentFragment)newNode;
            if (this.isConnected()) {
                return this.document.getTree().insertFragment(fragment, this, referenceNode);
            }
            Node last = null;
            ArrayList snapshot = new ArrayList(fragment.childNodes);
            for (Node child : snapshot) {
                last = this.insertSingleChildBefore(this.prepareForInsertion(child), referenceNode);
            }
            return last;
        }
        return this.insertSingleChildBefore(this.prepareForInsertion(newNode), referenceNode);
    }

    public Node replaceChild(Node newNode, Node oldNode) {
        if (this.document == null || newNode == null || oldNode == null || oldNode.parentNode != this) {
            return null;
        }
        if (newNode instanceof DocumentFragment) {
            DocumentFragment fragment = (DocumentFragment)newNode;
            Node nextSibling = oldNode.getNextSibling();
            this.document.removeNode(oldNode);
            if (this.isConnected()) {
                this.document.getTree().insertFragment(fragment, this, nextSibling);
                return oldNode;
            }
            ArrayList snapshot = new ArrayList(fragment.childNodes);
            for (Node child : snapshot) {
                this.insertSingleChildBefore(this.prepareForInsertion(child), nextSibling);
            }
            return oldNode;
        }
        Node inserted = this.prepareForInsertion(newNode);
        if (this.isConnected()) {
            this.document.getTree().replaceChild(this, inserted, oldNode);
        } else {
            int index = this.childNodes.indexOf(oldNode);
            if (index < 0) {
                return null;
            }
            Node.detachLocalChild(oldNode);
            this.attachLocalChild(inserted, index);
        }
        return oldNode;
    }

    private Node appendSingleChild(Node node) {
        if (node == null) {
            return null;
        }
        if (this.isConnected()) {
            this.document.createRelation(node, this, false);
        } else {
            this.attachLocalChild(node, this.childNodes.size());
        }
        return node;
    }

    private Node insertSingleChildBefore(Node node, Node referenceNode) {
        if (node == null) {
            return null;
        }
        if (this.isConnected()) {
            this.document.getTree().insertBefore(node, this, referenceNode);
        } else {
            int index;
            int n = index = referenceNode == null ? this.childNodes.size() : this.childNodes.indexOf(referenceNode);
            if (index < 0) {
                index = this.childNodes.size();
            }
            this.attachLocalChild(node, index);
        }
        return node;
    }

    private void attachLocalChild(Node node, int index) {
        if (node == null) {
            return;
        }
        if (this.document != null) {
            this.document.bumpSelectionCache();
        }
        Node.detachLocalChild(node);
        int safeIndex = Math.max(0, Math.min(index, this.childNodes.size()));
        this.childNodes.add(safeIndex, node);
        node.parentNode = this;
        node.document = this.document;
        node.depth = this.depth + 1;
        Node node2 = this;
        if (node2 instanceof Element) {
            Element parentElement = (Element)node2;
            if (node instanceof Element) {
                Element childElement = (Element)node;
                childElement.parentElement = parentElement;
                childElement.syncDomStateAfterAttach();
            }
            parentElement.refreshElementChildrenFromChildNodes();
        } else if (node instanceof Element) {
            Element childElement = (Element)node;
            childElement.parentElement = null;
            childElement.syncDomStateAfterAttach();
        }
    }

    private static void detachLocalChild(Node node) {
        if (node == null) {
            return;
        }
        Node oldParent = node.parentNode;
        if (oldParent == null) {
            return;
        }
        if (oldParent.document != null) {
            oldParent.document.bumpSelectionCache();
        }
        oldParent.childNodes.remove(node);
        if (oldParent instanceof Element) {
            Element oldParentElement = (Element)oldParent;
            oldParentElement.refreshElementChildrenFromChildNodes();
        }
        node.parentNode = null;
        if (node instanceof Element) {
            Element childElement = (Element)node;
            childElement.parentElement = null;
        }
    }

    private Node prepareForInsertion(Node node) {
        if (node instanceof Element) {
            Element element = (Element)node;
            return Element.init(element);
        }
        return node;
    }

    public Node cloneNode() {
        return this.cloneNode(false);
    }

    public Node cloneNode(boolean deep) {
        Node node = this;
        if (node instanceof TextNode) {
            TextNode textNode = (TextNode)node;
            return new TextNode(this.document, textNode.getTextContent());
        }
        node = this;
        if (node instanceof CommentNode) {
            CommentNode commentNode = (CommentNode)node;
            return new CommentNode(this.document, commentNode.getTextContent());
        }
        node = this;
        if (node instanceof DocumentFragment) {
            DocumentFragment fragment = (DocumentFragment)node;
            DocumentFragment copy = new DocumentFragment(this.document);
            if (deep) {
                for (Node child : fragment.childNodes) {
                    copy.appendChild(child.cloneNode(true));
                }
            }
            return copy;
        }
        node = this;
        if (node instanceof Element) {
            Element element = (Element)node;
            return element.cloneNode(deep);
        }
        return null;
    }

    public void before(Node node) {
        if (this.parentNode == null || node == null) {
            return;
        }
        this.parentNode.insertBefore(node, this);
    }

    public void after(Node node) {
        if (this.parentNode == null || node == null) {
            return;
        }
        this.parentNode.insertBefore(node, this.getNextSibling());
    }

    public void replaceWith(Node node) {
        if (this.parentNode == null || node == null) {
            return;
        }
        this.parentNode.replaceChild(node, this);
    }

    public void remove() {
        if (this.document == null) {
            return;
        }
        this.document.removeNode(this);
    }

    public boolean dispatchEvent(Object event) {
        if (!(event instanceof Event)) {
            return false;
        }
        Event targetEvent = (Event)event;
        if (targetEvent.target == null) {
            targetEvent.target = this;
        }
        if (targetEvent.currentTarget == null) {
            targetEvent.currentTarget = this;
        }
        Event.tiggerEvent(targetEvent);
        return !targetEvent.defaultPrevented;
    }

    public void addEventListener(String type, Consumer<Event> listener) {
        this.events.addEventListener(type, listener);
    }

    public void addEventListener(String type, Consumer<Event> listener, boolean useCapture) {
        this.events.addEventListener(type, listener, useCapture);
    }

    public void addEventListener(String type, Consumer<Event> listener, boolean useCapture, boolean once) {
        this.events.addEventListener(type, listener, useCapture, once);
    }

    protected void addInternalEventListener(String type, Consumer<Event> listener) {
        this.events.addInternalEventListener(type, listener);
    }

    protected void addInternalEventListener(String type, Consumer<Event> listener, boolean useCapture) {
        this.events.addInternalEventListener(type, listener, useCapture);
    }

    public void removeEventListener(String type, Consumer<Event> listener) {
        this.removeEventListener(type, listener, false);
    }

    public void removeEventListener(String type, Consumer<Event> listener, boolean useCapture) {
        this.events.removeEventListener(type, listener, useCapture);
    }

    public void triggerEvent(Consumer<Event.ListenerRecord> handler) {
        this.events.triggerEvent(handler);
    }

    public void setEventListeners(CopyOnWriteArrayList<Event.ListenerRecord> listeners) {
        this.events.setListeners(listeners);
        this.EventListener = this.events.listeners();
    }

    public abstract short getNodeType();

    public abstract String getNodeName();

    public String getNodeValue() {
        return null;
    }

    public abstract String getTextContent();

    public abstract void setTextContent(String var1);
}

