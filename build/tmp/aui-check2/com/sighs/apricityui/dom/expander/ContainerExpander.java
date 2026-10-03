/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dom.expander;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.element.Container;
import com.sighs.apricityui.element.Item;
import com.sighs.apricityui.element.Recipe;
import com.sighs.apricityui.element.Slot;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public final class ContainerExpander {
    private static final String GENERATED_CONTAINER_AUTO = "container-auto";
    private static final String GENERATED_CONTAINER_REPEAT = "container-repeat";
    private static final int PLAYER_AUTO_SLOT_COUNT = 36;

    public static void expand(Document document) {
        if (document == null) {
            return;
        }
        ArrayList<Element> snapshot = new ArrayList<Element>(document.getElements());
        for (Element element : snapshot) {
            Container container;
            if (!(element instanceof Container) || !ContainerExpander.isBindableContainer(container = (Container)element)) continue;
            ContainerExpander.expandSingleContainer(document, container);
        }
    }

    private static void expandSingleContainer(Document document, Container container) {
        List<Slot> ownedSlots = ContainerExpander.collectOwnedSlots(document, container);
        ContainerExpander.materializeRepeatedSlots(ownedSlots);
        ownedSlots = ContainerExpander.collectOwnedSlots(document, container);
        if (ownedSlots.isEmpty()) {
            Integer declaredSize = ContainerExpander.parsePositiveInt(container.getAttribute("size"));
            String bindType = ContainerExpander.normalize(container.getAttribute("bind"));
            if (declaredSize != null) {
                ContainerExpander.appendAutoSlots(document, container, declaredSize, false);
            } else if ("player".equals(bindType)) {
                ContainerExpander.appendAutoSlots(document, container, 36, true);
            }
            ownedSlots = ContainerExpander.collectOwnedSlots(document, container);
        }
        ContainerExpander.normalizeSlotIndices(ownedSlots, document, container);
        ContainerExpander.injectDefaultColumnsIfNeeded(container, ownedSlots.size());
    }

    private static void materializeRepeatedSlots(List<Slot> slots) {
        if (slots == null || slots.isEmpty()) {
            return;
        }
        int nextImplicitIndex = 0;
        for (Slot source : new ArrayList<Slot>(slots)) {
            int repeatCount = source.getRepeatCount();
            int requestedIndex = source.getSlotIndex();
            int startIndex = requestedIndex >= 0 ? requestedIndex : nextImplicitIndex;
            nextImplicitIndex = Math.max(nextImplicitIndex, startIndex + repeatCount);
            if (repeatCount <= 1) continue;
            ContainerExpander.ensureIndexAttributes(source, startIndex);
            source.removeAttribute("repeat");
            Slot insertionPoint = source;
            for (int offset = 1; offset < repeatCount; ++offset) {
                Element clonedElement = source.cloneNode(true);
                if (!(clonedElement instanceof Slot)) {
                    throw new IllegalStateException("Repeated container slot clone is not a Slot");
                }
                Slot clone = (Slot)clonedElement;
                clone.removeAttribute("id");
                ContainerExpander.ensureIndexAttributes(clone, startIndex + offset);
                clone.setAttribute("data-generated", GENERATED_CONTAINER_REPEAT);
                insertionPoint.after(clone);
                insertionPoint = clone;
            }
        }
    }

    private static void appendAutoSlots(Document document, Container container, int count, boolean playerAuto) {
        int safeCount = Math.max(0, count);
        for (int index = 0; index < safeCount; ++index) {
            Slot slot = new Slot(document);
            LinkedHashMap<String, String> attrs = new LinkedHashMap<String, String>();
            attrs.put("index", String.valueOf(index));
            attrs.put("slot-index", String.valueOf(index));
            attrs.put("data-generated", GENERATED_CONTAINER_AUTO);
            if (playerAuto) {
                attrs.put("part", index < 27 ? "inv" : "hotbar");
            }
            slot.setAttributesBatch(attrs, true);
            Item item = new Item(document);
            item.setTextContent("minecraft:air");
            slot.appendChild(item);
            container.append(slot);
        }
    }

    private static List<Slot> collectOwnedSlots(Document document, Container container) {
        ArrayList<Slot> result = new ArrayList<Slot>();
        if (document == null || container == null) {
            return result;
        }
        for (Element element : document.getElements()) {
            Container owner;
            Slot slot;
            if (!(element instanceof Slot) || (slot = (Slot)element).findAncestor(Recipe.class) != null || (owner = slot.findAncestor(Container.class)) != container) continue;
            result.add(slot);
        }
        return result;
    }

    private static void normalizeSlotIndices(List<Slot> slots, Document document, Container container) {
        if (slots == null || slots.isEmpty()) {
            return;
        }
        HashSet<Integer> usedIndices = new HashSet<Integer>();
        ArrayList<Slot> missing = new ArrayList<Slot>();
        for (Slot slot : slots) {
            int slotIndex = slot.getSlotIndex();
            if (slotIndex >= 0) {
                usedIndices.add(slotIndex);
                ContainerExpander.ensureIndexAttributes(slot, slotIndex);
                continue;
            }
            missing.add(slot);
        }
        int nextIndex = 0;
        for (Slot slot : missing) {
            while (usedIndices.contains(nextIndex)) {
                ++nextIndex;
            }
            int assigned = nextIndex;
            usedIndices.add(assigned);
            ContainerExpander.ensureIndexAttributes(slot, assigned);
            ApricityUI.LOGGER.warn("ContainerExpander assigned implicit slot index, template={}, containerId={}, assignedIndex={}", new Object[]{document == null ? "" : document.getPath(), container == null ? "" : container.getAttribute("id"), assigned});
        }
    }

    private static void ensureIndexAttributes(Slot slot, int index) {
        if (slot == null || index < 0) {
            return;
        }
        String target = String.valueOf(index);
        if (!target.equals(slot.getAttribute("index"))) {
            slot.setAttribute("index", target);
        }
        if (!target.equals(slot.getAttribute("slot-index"))) {
            slot.setAttribute("slot-index", target);
        }
    }

    private static void injectDefaultColumnsIfNeeded(Container container, int slotCount) {
        if (container == null || slotCount <= 0) {
            return;
        }
        boolean hasGridTemplateColumns = ContainerExpander.hasCustomValue(container.getComputedStyle().gridTemplateColumns);
        boolean hasContainerColumns = ContainerExpander.hasCustomValue(container.getCustomProperty("--aui-container-columns"));
        if (hasGridTemplateColumns || hasContainerColumns) {
            return;
        }
        int effectiveColumns = Math.max(1, Math.min(9, slotCount));
        String mergedStyle = ContainerExpander.appendStyle(container.getAttribute("style"), "--aui-container-columns-effective:%d;grid-template-columns:%d;".formatted(effectiveColumns, effectiveColumns));
        container.setAttribute("style", mergedStyle);
    }

    private static boolean isBindableContainer(Container container) {
        if (container == null) {
            return false;
        }
        String bind = ContainerExpander.normalize(container.getAttribute("bind"));
        if (!bind.isBlank()) {
            return true;
        }
        Boolean primary = ContainerExpander.parseBooleanLike(container.getAttribute("primary"));
        return primary != null && primary != false;
    }

    private static String appendStyle(String inlineStyle, String declaration) {
        Object patch;
        Object base = inlineStyle == null ? "" : inlineStyle.trim();
        Object object = patch = declaration == null ? "" : declaration.trim();
        if (((String)patch).isBlank()) {
            return base;
        }
        if (!((String)patch).endsWith(";")) {
            patch = (String)patch + ";";
        }
        if (((String)base).isBlank()) {
            return patch;
        }
        if (!((String)base).endsWith(";")) {
            base = (String)base + ";";
        }
        return (String)base + (String)patch;
    }

    private static boolean hasCustomValue(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return !normalized.isBlank() && !"unset".equals(normalized) && !"auto".equals(normalized);
    }

    private static Integer parsePositiveInt(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(raw.trim());
            return parsed > 0 ? Integer.valueOf(parsed) : null;
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().toLowerCase(Locale.ROOT);
    }

    private static Boolean parseBooleanLike(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (normalized.isBlank() || "unset".equals(normalized) || "auto".equals(normalized)) {
            return null;
        }
        return switch (normalized) {
            case "1", "true", "yes", "on", "enabled" -> true;
            case "0", "false", "no", "off", "disabled", "none" -> false;
            default -> null;
        };
    }
}

