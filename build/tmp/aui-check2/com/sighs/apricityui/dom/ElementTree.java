/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dom;

import com.sighs.apricityui.dom.DocumentFragment;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.spi.AuiServices;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class ElementTree {
    private final Document owner;
    private final ArrayList<Node> nodes = new ArrayList();
    private final ArrayList<Element> elements = new ArrayList();
    private final HashMap<String, Element> idMap = new HashMap();

    public ElementTree(Document owner) {
        this.owner = owner;
    }

    public ArrayList<Element> getElements() {
        return this.elements;
    }

    public ArrayList<Node> getNodes() {
        return this.nodes;
    }

    public void clear() {
        this.nodes.clear();
        this.elements.clear();
        this.idMap.clear();
    }

    public void rebuildFromRoot(Element root) {
        this.nodes.clear();
        this.elements.clear();
        this.idMap.clear();
        if (root == null) {
            return;
        }
        root.parentNode = null;
        root.parentElement = null;
        root.depth = 0;
        ArrayDeque<Node> stack = new ArrayDeque<Node>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node current = (Node)stack.pop();
            this.nodes.add(current);
            if (current instanceof Element) {
                Element element = (Element)current;
                this.syncElementChildView(element);
                this.elements.add(element);
                element.runInitFromDomOnce(element);
                if (element.id != null && !element.id.isBlank()) {
                    this.idMap.put(element.id, element);
                }
            }
            ArrayList<Node> children = current.childNodes;
            for (int i = children.size() - 1; i >= 0; --i) {
                Node child = (Node)children.get(i);
                if (child == null) continue;
                child.parentNode = current;
                child.depth = current.depth + 1;
                if (child instanceof Element) {
                    Element parentElement;
                    Element childElement = (Element)child;
                    childElement.parentElement = current instanceof Element ? (parentElement = (Element)current) : null;
                }
                stack.push(child);
            }
        }
    }

    public void updateElement(Element element) {
        int index = -1;
        for (Element e : this.elements) {
            if (!e.uuid.equals(element.uuid)) continue;
            index = this.elements.indexOf(e);
        }
        if (index == -1) {
            return;
        }
        this.elements.set(index, element);
    }

    public void createRelation(Node child, Node parent, boolean head) {
        if (child == null || parent == null) {
            return;
        }
        this.moveSubtree(child, parent, head ? 0 : parent.childNodes.size());
    }

    public void insertBefore(Node newChild, Node parent, Node referenceChild) {
        int index;
        if (newChild == null || parent == null) {
            return;
        }
        int n = index = referenceChild == null ? parent.childNodes.size() : parent.childNodes.indexOf(referenceChild);
        if (index < 0) {
            index = parent.childNodes.size();
        }
        this.moveSubtree(newChild, parent, index);
    }

    public Node insertFragment(DocumentFragment fragment, Node parent, Node referenceChild) {
        int index;
        if (fragment == null || parent == null || fragment.childNodes.isEmpty()) {
            return null;
        }
        int n = index = referenceChild == null ? parent.childNodes.size() : parent.childNodes.indexOf(referenceChild);
        if (index < 0) {
            index = parent.childNodes.size();
        }
        return this.moveFragmentChildren(fragment, parent, index);
    }

    public Node insertBeforeAndReturn(Node newChild, Node parent, Node referenceChild) {
        this.insertBefore(newChild, parent, referenceChild);
        return newChild;
    }

    public void replaceChild(Node parent, Node newChild, Node oldChild) {
        if (parent == null || newChild == null || oldChild == null) {
            return;
        }
        int index = parent.childNodes.indexOf(oldChild);
        if (index < 0) {
            return;
        }
        this.removeNode(oldChild);
        this.moveSubtree(newChild, parent, Math.min(index, parent.childNodes.size()));
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }
        this.detachSubtree(node);
    }

    public void clearChildren(Node parent) {
        if (parent == null || parent.childNodes.isEmpty()) {
            return;
        }
        if (this.owner != null) {
            this.owner.bumpSelectionCache();
        }
        if (this.owner != null) {
            this.owner.markHitTestDirtyAll();
        }
        ArrayList<Node> removedRoots = new ArrayList<Node>(parent.childNodes);
        Node previousSibling = null;
        Node nextSibling = null;
        parent.childNodes.clear();
        this.syncElementChildView(parent);
        ArrayList<Node> removedNodes = new ArrayList<Node>();
        ArrayList<Element> removedElements = new ArrayList<Element>();
        for (Node child : removedRoots) {
            List<Node> subtree = this.flattenSubtree(child);
            removedNodes.addAll(subtree);
            removedElements.addAll(this.flattenElements(subtree));
            child.parentNode = null;
            if (!(child instanceof Element)) continue;
            Element element = (Element)child;
            element.parentElement = null;
        }
        this.nodes.removeAll(removedNodes);
        this.elements.removeAll(removedElements);
        for (Element element : removedElements) {
            element.onDisconnectedFromDocument();
            if (element.id != null && !element.id.isBlank()) {
                this.removeId(element.id, element);
            }
            element.getRenderer().route.clear();
        }
        this.clearRemovedFocusState(removedElements);
        if (parent instanceof Element) {
            Element parentElement = (Element)parent;
            ElementTree.clearTextCaches(parentElement);
            ElementTree.clearLayoutChain(parentElement);
            this.owner.markDirty(parentElement, 6);
        }
        AuiServices.expander().restoreRequiredContent(this.owner, parent);
        this.owner.queueMutation(Document.MutationRecord.childList(parent, List.of(), removedRoots, previousSibling, nextSibling));
    }

    public void removeId(String id, Element element) {
        if (id == null || id.isBlank()) {
            return;
        }
        Element current = this.idMap.get(id);
        if (current == element) {
            this.idMap.remove(id);
        }
    }

    public void recordId(Element element) {
        if (element == null || element.id == null || element.id.isBlank()) {
            return;
        }
        this.idMap.put(element.id, element);
    }

    public Element getElementById(String id) {
        return this.idMap.get(id);
    }

    private void moveSubtree(Node child, Node parent, int childIndex) {
        if (this.owner != null) {
            this.owner.bumpSelectionCache();
        }
        AuiServices.expander().validateRuntimeInsertion(this.owner, parent, child);
        this.detachSubtree(child);
        AuiServices.expander().validateRuntimeInsertion(this.owner, parent, child);
        int safeIndex = Math.max(0, Math.min(childIndex, parent.childNodes.size()));
        parent.childNodes.add(safeIndex, child);
        this.syncElementChildView(parent);
        Node previousSibling = safeIndex > 0 ? parent.childNodes.get(safeIndex - 1) : null;
        Node nextSibling = safeIndex + 1 < parent.childNodes.size() ? parent.childNodes.get(safeIndex + 1) : null;
        this.updateSubtree(child, parent, parent.depth + 1, this.owner);
        if (child instanceof Element) {
            Element childElement = (Element)child;
            childElement.syncDomStateAfterAttach();
            childElement.invalidateSubtreeAfterAttach();
            childElement.invalidateStyle();
            this.owner.markDirty(childElement, 7);
        }
        int insertIndex = safeIndex == 0 ? this.nodes.indexOf(parent) + 1 : this.findSubtreeEndExclusive(parent.childNodes.get(safeIndex - 1));
        List<Node> subtreeNodes = this.flattenSubtree(child);
        this.nodes.addAll(insertIndex, subtreeNodes);
        this.elements.addAll(this.resolveInsertIndexForElements(insertIndex), this.flattenElements(subtreeNodes));
        if (parent instanceof Element) {
            Element parentElement = (Element)parent;
            ElementTree.clearTextCaches(parentElement);
            ElementTree.clearLayoutChain(parentElement);
            this.owner.markDirty(parentElement, 6);
        }
        this.owner.queueMutation(Document.MutationRecord.childList(parent, List.of(child), List.of(), previousSibling, nextSibling));
    }

    private Node moveFragmentChildren(DocumentFragment fragment, Node parent, int childIndex) {
        ArrayList<Node> roots = new ArrayList<Node>(fragment.childNodes);
        if (roots.isEmpty()) {
            return null;
        }
        if (this.owner != null) {
            this.owner.bumpSelectionCache();
        }
        for (Node child : roots) {
            AuiServices.expander().validateRuntimeInsertion(this.owner, parent, child);
        }
        int safeIndex = Math.max(0, Math.min(childIndex, parent.childNodes.size()));
        Node previousSibling = safeIndex > 0 ? parent.childNodes.get(safeIndex - 1) : null;
        Node nextSibling = safeIndex < parent.childNodes.size() ? parent.childNodes.get(safeIndex) : null;
        fragment.childNodes.clear();
        parent.childNodes.addAll(safeIndex, roots);
        this.syncElementChildView(parent);
        if (this.owner != null) {
            this.owner.markHitTestDirtyAll();
        }
        ArrayList<Node> insertedNodes = new ArrayList<Node>();
        ArrayList<Element> insertedElements = new ArrayList<Element>();
        for (Node child : roots) {
            this.updateSubtree(child, parent, parent.depth + 1, this.owner);
            if (child instanceof Element) {
                Element childElement = (Element)child;
                childElement.syncDomStateAfterAttach();
                childElement.invalidateSubtreeAfterAttach();
                childElement.invalidateStyle();
                this.owner.markDirty(childElement, 7);
            }
            List<Node> subtreeNodes = this.flattenSubtree(child);
            insertedNodes.addAll(subtreeNodes);
            insertedElements.addAll(this.flattenElements(subtreeNodes));
        }
        int insertIndex = safeIndex == 0 ? this.nodes.indexOf(parent) + 1 : this.findSubtreeEndExclusive(parent.childNodes.get(safeIndex - 1));
        this.nodes.addAll(insertIndex, insertedNodes);
        this.elements.addAll(this.resolveInsertIndexForElements(insertIndex), insertedElements);
        if (parent instanceof Element) {
            Element parentElement = (Element)parent;
            ElementTree.clearTextCaches(parentElement);
            ElementTree.clearLayoutChain(parentElement);
            this.owner.markDirty(parentElement, 6);
        }
        AuiServices.expander().normalizeRuntimeChildren(this.owner, parent);
        this.owner.queueMutation(Document.MutationRecord.childList(parent, roots, List.of(), previousSibling, nextSibling));
        return roots.get(roots.size() - 1);
    }

    private void detachSubtree(Node node) {
        Node oldParent;
        if (this.owner != null) {
            this.owner.bumpSelectionCache();
        }
        if ((oldParent = node.parentNode) != null) {
            int index = oldParent.childNodes.indexOf(node);
            Node previousSibling = index > 0 ? oldParent.childNodes.get(index - 1) : null;
            Iterator<Element> nextSibling = index >= 0 && index + 1 < oldParent.childNodes.size() ? oldParent.childNodes.get(index + 1) : null;
            oldParent.childNodes.removeIf(candidate -> node.uuid.equals(candidate.uuid));
            this.syncElementChildView(oldParent);
            if (oldParent instanceof Element) {
                Element oldParentElement = (Element)oldParent;
                ElementTree.clearTextCaches(oldParentElement);
                ElementTree.clearLayoutChain(oldParentElement);
                this.owner.markDirty(oldParentElement, 6);
            }
            this.owner.queueMutation(Document.MutationRecord.childList(oldParent, List.of(), List.of(node), previousSibling, nextSibling));
        }
        List<Node> subtree = this.flattenSubtree(node);
        this.nodes.removeAll(subtree);
        List<Element> subtreeElements = this.flattenElements(subtree);
        this.elements.removeAll(subtreeElements);
        for (Element element : subtreeElements) {
            element.onDisconnectedFromDocument();
            if (element.id != null && !element.id.isBlank()) {
                this.removeId(element.id, element);
            }
            element.getRenderer().route.clear();
        }
        this.clearRemovedFocusState(subtreeElements);
        node.parentNode = null;
        if (node instanceof Element) {
            Element element = (Element)node;
            element.parentElement = null;
        }
        if (oldParent != null) {
            AuiServices.expander().restoreRequiredContent(this.owner, oldParent);
        }
    }

    private static void clearTextCaches(Element element) {
        if (element == null) {
            return;
        }
        element.getRenderer().text.clear();
        element.getRenderer().wrappedText.clear();
        element.getRenderer().size.clear();
    }

    private static void clearLayoutChain(Element element) {
        Element current = element;
        while (current != null) {
            current.getRenderer().size.clear();
            current.getRenderer().box.clear();
            current.getRenderer().position.clear();
            current = current.parentElement;
        }
    }

    private void updateSubtree(Node root, Node parent, int depth, Document document) {
        root.parentNode = parent;
        root.depth = depth;
        root.document = document;
        if (root instanceof Element) {
            Element parentElement;
            Element element = (Element)root;
            element.parentElement = parent instanceof Element ? (parentElement = (Element)parent) : null;
            this.syncElementChildView(element);
            element.getRenderer().route.clear();
            if (element.id != null && !element.id.isBlank()) {
                this.idMap.put(element.id, element);
            }
        }
        for (Node child : root.childNodes) {
            this.updateSubtree(child, root, depth + 1, document);
        }
    }

    private List<Node> flattenSubtree(Node root) {
        ArrayList<Node> subtree = new ArrayList<Node>();
        ArrayDeque<Node> stack = new ArrayDeque<Node>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node current = (Node)stack.pop();
            subtree.add(current);
            ArrayList<Node> children = current.childNodes;
            for (int i = children.size() - 1; i >= 0; --i) {
                Node child = (Node)children.get(i);
                if (child == null) continue;
                stack.push(child);
            }
        }
        return subtree;
    }

    private int findSubtreeEndExclusive(Node node) {
        int end;
        int start = this.nodes.indexOf(node);
        if (start < 0) {
            return this.nodes.size();
        }
        for (end = start + 1; end < this.nodes.size() && this.nodes.get((int)end).depth > node.depth; ++end) {
        }
        return end;
    }

    private void clearRemovedFocusState(List<Element> subtree) {
        Element previousCursor;
        Element active;
        Element focused = this.owner.getFocusedElement();
        if (focused != null && subtree.contains(focused)) {
            this.owner.clearFocus();
        }
        if ((active = this.owner.getPressedElement()) != null && subtree.contains(active)) {
            this.owner.setPressedElement(null);
        }
        if ((previousCursor = this.owner.getPreviousCursorElement()) != null && subtree.contains(previousCursor)) {
            this.owner.setPreviousCursorElement(null);
        }
    }

    private void syncElementChildView(Node node) {
        if (!(node instanceof Element)) {
            return;
        }
        Element element = (Element)node;
        ArrayList<Element> elementChildren = new ArrayList<Element>();
        for (Node child : element.childNodes) {
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            childElement.parentElement = element;
            elementChildren.add(childElement);
        }
        element.children = elementChildren;
        element.syncSelectStateAfterChildrenChanged();
    }

    private List<Element> flattenElements(List<Node> source) {
        ArrayList<Element> out = new ArrayList<Element>();
        for (Node node : source) {
            if (!(node instanceof Element)) continue;
            Element element = (Element)node;
            out.add(element);
        }
        return out;
    }

    private int resolveInsertIndexForElements(int nodeInsertIndex) {
        int elementIndex = 0;
        for (int i = 0; i < Math.min(nodeInsertIndex, this.nodes.size()); ++i) {
            if (!(this.nodes.get(i) instanceof Element)) continue;
            ++elementIndex;
        }
        return elementIndex;
    }
}

