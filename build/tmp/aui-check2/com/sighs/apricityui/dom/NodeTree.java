/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dom;

import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.parser.Selector;
import com.sighs.apricityui.style.Style;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class NodeTree {
    private final Element owner;

    public NodeTree(Element owner) {
        this.owner = owner;
    }

    public ArrayList<Element> getRoute() {
        ArrayList<Element> result = new ArrayList<Element>();
        Node current = this.owner;
        while (current != null) {
            if (current instanceof Element) {
                Element element = current;
                result.add(element);
            }
            current = current.parentNode;
        }
        return result;
    }

    public Element[] getRouteArray() {
        Element[] cache = this.owner.getRenderer().route.get();
        if (cache != null) {
            return cache;
        }
        int count = 0;
        Node cur = this.owner;
        while (cur != null) {
            if (cur instanceof Element) {
                ++count;
            }
            cur = cur.parentNode;
        }
        Element[] route = new Element[count];
        cur = this.owner;
        int index = 0;
        while (cur != null) {
            if (cur instanceof Element) {
                Node element = cur;
                route[index++] = element;
            }
            cur = cur.parentNode;
        }
        this.owner.getRenderer().route.set(route);
        return route;
    }

    public void forEachRoute(Consumer<Element> consumer) {
        if (consumer == null) {
            return;
        }
        Node cur = this.owner;
        while (cur != null) {
            if (cur instanceof Element) {
                Element element = cur;
                consumer.accept(element);
            }
            cur = cur.parentNode;
        }
    }

    public List<Element> querySelectorAll(String selector) {
        return Selector.querySelectorAll(this.owner, selector);
    }

    public Element querySelector(String selector) {
        return Selector.querySelector(this.owner, selector);
    }

    public void prepend(Element element) {
        this.owner.document.createRelation(Element.init(element), this.owner, true);
    }

    public void append(Element element) {
        this.owner.document.createRelation(Element.init(element), this.owner, false);
    }

    public Element appendChild(Element element) {
        Element child = Element.init(element);
        this.owner.document.createRelation(child, this.owner, false);
        return child;
    }

    public Element removeChild(Element element) {
        if (element == null || element.parentNode != this.owner) {
            return null;
        }
        this.owner.document.removeElement(element);
        return element;
    }

    public Element insertBefore(Element newElement, Element referenceElement) {
        Element child = Element.init(newElement);
        this.owner.document.getTree().insertBefore(child, this.owner, referenceElement);
        return child;
    }

    public Element replaceChild(Element newElement, Element oldElement) {
        if (oldElement == null || oldElement.parentNode != this.owner) {
            return null;
        }
        Element child = Element.init(newElement);
        this.owner.document.getTree().replaceChild(this.owner, child, oldElement);
        return oldElement;
    }

    public int getDepth() {
        return this.owner.depth;
    }

    public Element getParentStackContext() {
        Node current = this.owner.parentNode;
        while (current != null) {
            Element parent;
            if (current instanceof Element && (parent = (Element)current).isStackContext()) {
                return parent;
            }
            current = current.parentNode;
        }
        return this.owner.document.body;
    }

    public boolean isStackContext() {
        Style style = this.owner.getComputedStyle();
        return !style.position.equals("static") || !style.zIndex.equals("auto");
    }
}

