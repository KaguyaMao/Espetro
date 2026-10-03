/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dom;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.element.Ingredient;
import com.sighs.apricityui.element.Item;
import com.sighs.apricityui.element.Slot;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import java.util.ArrayList;

public final class SlotContentRules {
    private static boolean restoring;

    private SlotContentRules() {
    }

    public static void validateRuntimeInsertion(Node parent, Node child) {
        if (parent instanceof Slot) {
            Slot slot = (Slot)parent;
            if (!(child instanceof Item) && !(child instanceof Ingredient)) {
                throw SlotContentRules.hierarchy(parent, child);
            }
            SlotContentRules.replaceSlotContent(slot, child);
            return;
        }
        if (parent instanceof Item) {
            if (!(child instanceof TextNode)) {
                throw SlotContentRules.hierarchy(parent, child);
            }
            return;
        }
        if (parent instanceof Ingredient) {
            Ingredient ingredient = (Ingredient)parent;
            if (!(child instanceof Item) && !(child instanceof TextNode)) {
                throw SlotContentRules.hierarchy(parent, child);
            }
            if (child instanceof Item) {
                SlotContentRules.replaceControlledItem(ingredient, child);
            }
        }
    }

    public static void normalizeTemplate(Document document) {
        if (document == null) {
            return;
        }
        for (Element element : new ArrayList<Element>(document.getElements())) {
            if (element instanceof Slot) {
                Slot slot = (Slot)element;
                SlotContentRules.normalizeSlot(slot, document);
                continue;
            }
            if (element instanceof Ingredient) {
                Ingredient ingredient = (Ingredient)element;
                SlotContentRules.normalizeIngredient(ingredient, document);
                continue;
            }
            if (!(element instanceof Item)) continue;
            Item item = (Item)element;
            SlotContentRules.normalizeItem(item, document);
        }
    }

    public static Item ensureDirectItem(Slot slot) {
        if (slot == null) {
            return null;
        }
        for (Node child : slot.childNodes) {
            if (!(child instanceof Item)) continue;
            Item item = (Item)child;
            return item;
        }
        Item item = new Item(slot.document);
        item.setTextContent("minecraft:air");
        slot.appendChild(item);
        return item;
    }

    public static Item ensureControlledItem(Ingredient ingredient) {
        if (ingredient == null) {
            return null;
        }
        Item existing = SlotContentRules.findControlledItem(ingredient);
        if (existing != null) {
            return existing;
        }
        Item item = new Item(ingredient.document);
        item.setTextContent("minecraft:air");
        ingredient.appendChild(item);
        return item;
    }

    public static Element getSlotContent(Slot slot) {
        if (slot == null) {
            return null;
        }
        for (Node child : slot.childNodes) {
            if (!(child instanceof Item) && !(child instanceof Ingredient)) continue;
            return (Element)child;
        }
        return null;
    }

    public static Item getDisplayItem(Slot slot) {
        Element content = SlotContentRules.getSlotContent(slot);
        if (content instanceof Item) {
            Item item = (Item)content;
            return item;
        }
        if (content instanceof Ingredient) {
            Ingredient ingredient = (Ingredient)content;
            return SlotContentRules.findControlledItem(ingredient);
        }
        return null;
    }

    public static void normalizeRuntimeChildren(Node parent) {
        if (parent instanceof Slot) {
            Slot slot = (Slot)parent;
            SlotContentRules.normalizeSlot(slot, slot.document);
        } else if (parent instanceof Ingredient) {
            Ingredient ingredient = (Ingredient)parent;
            SlotContentRules.normalizeIngredient(ingredient, ingredient.document);
        } else if (parent instanceof Item) {
            Item item = (Item)parent;
            SlotContentRules.normalizeItem(item, item.document);
        }
    }

    public static void restoreRequiredContent(Node parent) {
        if (restoring) {
            return;
        }
        try {
            Ingredient ingredient;
            Slot slot;
            restoring = true;
            if (parent instanceof Slot && SlotContentRules.getSlotContent(slot = (Slot)parent) == null) {
                SlotContentRules.ensureDirectItem(slot);
            } else if (parent instanceof Ingredient && SlotContentRules.findControlledItem(ingredient = (Ingredient)parent) == null) {
                SlotContentRules.ensureControlledItem(ingredient);
            }
        }
        finally {
            restoring = false;
        }
    }

    private static void normalizeSlot(Slot slot, Document document) {
        Element keep = SlotContentRules.getSlotContent(slot);
        for (Node child : new ArrayList(slot.childNodes)) {
            if (child == keep) continue;
            SlotContentRules.warn(document, slot, child);
            child.remove();
        }
        if (keep == null) {
            SlotContentRules.ensureDirectItem(slot);
        }
    }

    private static void normalizeIngredient(Ingredient ingredient, Document document) {
        Item keep = SlotContentRules.findControlledItem(ingredient);
        if (keep == null) {
            keep = SlotContentRules.ensureControlledItem(ingredient);
        }
        boolean sourceTextPresent = false;
        for (Node child : new ArrayList(ingredient.childNodes)) {
            if (child == keep) continue;
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode)child;
                if (!sourceTextPresent && !textNode.getTextContent().isBlank()) {
                    sourceTextPresent = true;
                    continue;
                }
            }
            SlotContentRules.warn(document, ingredient, child);
            child.remove();
        }
        if (!(sourceTextPresent || keep.getTextContent().isBlank() || "minecraft:air".equals(keep.getTextContent().trim()))) {
            ingredient.innerText = keep.getTextContent();
            keep.setTextContent("minecraft:air");
        }
    }

    private static void normalizeItem(Item item, Document document) {
        boolean textFound = false;
        for (Node child : new ArrayList(item.childNodes)) {
            if (child instanceof TextNode && !textFound) {
                textFound = true;
                continue;
            }
            SlotContentRules.warn(document, item, child);
            child.remove();
        }
    }

    private static Item findControlledItem(Ingredient ingredient) {
        for (Node child : ingredient.childNodes) {
            if (!(child instanceof Item)) continue;
            Item item = (Item)child;
            return item;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void replaceSlotContent(Slot slot, Node incoming) {
        boolean previous = restoring;
        restoring = true;
        try {
            for (Node child : new ArrayList(slot.childNodes)) {
                if (child == incoming) continue;
                child.remove();
            }
        }
        finally {
            restoring = previous;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void replaceControlledItem(Ingredient ingredient, Node incoming) {
        boolean previous = restoring;
        restoring = true;
        try {
            for (Node child : new ArrayList(ingredient.childNodes)) {
                if (!(child instanceof Item) || child == incoming) continue;
                child.remove();
            }
        }
        finally {
            restoring = previous;
        }
    }

    private static IllegalArgumentException hierarchy(Node parent, Node child) {
        String string;
        String parentName;
        if (parent instanceof Element) {
            Element element = (Element)parent;
            v0 = element.tagName;
        } else {
            v0 = parentName = parent.getNodeName();
        }
        if (child instanceof Element) {
            Element element = (Element)child;
            string = element.tagName;
        } else {
            string = child == null ? "null" : child.getNodeName();
        }
        String childName = string;
        return new IllegalArgumentException("HierarchyRequestError: " + parentName + " cannot contain " + childName);
    }

    private static void warn(Document document, Element parent, Node child) {
        ApricityUI.LOGGER.warn("Discarded invalid Slot/Item/Ingredient template child, template={}, parent={}, child={}", new Object[]{document == null ? "" : document.getPath(), parent.tagName, child == null ? "null" : child.getNodeName()});
    }
}

