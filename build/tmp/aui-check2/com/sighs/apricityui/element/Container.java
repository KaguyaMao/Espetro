/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.element;

import com.sighs.apricityui.container.bind.ContainerBindType;
import com.sighs.apricityui.element.ContainerDeclaration;
import com.sighs.apricityui.element.MinecraftElement;
import com.sighs.apricityui.element.Recipe;
import com.sighs.apricityui.element.Slot;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.util.common.NormalizeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ElementRegister(value="CONTAINER")
public class Container
extends MinecraftElement {
    public static final String TAG_NAME = "CONTAINER";

    public Container(Document document) {
        super(document, TAG_NAME);
    }

    public static List<ContainerDeclaration> extractDeclarations(Document document) {
        if (document == null) {
            return List.of();
        }
        ArrayList<ContainerDeclaration> declarations = new ArrayList<ContainerDeclaration>();
        int[] topLevelIndex = new int[]{0};
        Container.collectDeclarations(document, declarations, topLevelIndex);
        return List.copyOf(declarations);
    }

    private static void collectDeclarations(Document document, List<ContainerDeclaration> declarations, int[] topLevelIndex) {
        for (Element element : document.getElements()) {
            Container container;
            if (!(element instanceof Container) || (container = (Container)element).findAncestor(Container.class) != null) continue;
            String rawId = container.getAttribute("id");
            String containerId = Container.resolveContainerId(rawId, topLevelIndex[0]);
            String rawBind = container.getAttribute("bind");
            ContainerBindType bindType = Container.resolveBindType(rawBind);
            boolean primary = Container.parseBooleanLike(container.getAttribute("primary"));
            int capacity = Container.resolveCapacity(document, container);
            declarations.add(new ContainerDeclaration(containerId, bindType, capacity, primary));
            topLevelIndex[0] = topLevelIndex[0] + 1;
        }
    }

    private static int resolveCapacity(Document document, Container container) {
        int declaredSize = Container.parsePositiveInt(container.getAttribute("size"), 0);
        int maxSlotIndex = -1;
        int nextImplicit = 0;
        for (Element element : document.getElements()) {
            int candidate;
            Container owner;
            Slot slot;
            if (!(element instanceof Slot) || (slot = (Slot)element).findAncestor(Recipe.class) != null || (owner = slot.findAncestor(Container.class)) != container) continue;
            int repeat = Math.max(1, slot.getRepeatCount());
            int parsedIndex = slot.getSlotIndex();
            int start = parsedIndex < 0 ? nextImplicit : parsedIndex;
            int endIndex = start + repeat - 1;
            if (endIndex > maxSlotIndex) {
                maxSlotIndex = endIndex;
            }
            if ((candidate = start + repeat) <= nextImplicit) continue;
            nextImplicit = candidate;
        }
        int slotDerivedCapacity = maxSlotIndex + 1;
        ContainerBindType bindType = Container.resolveBindType(container.getAttribute("bind"));
        int playerCapacity = bindType == ContainerBindType.PLAYER ? 36 : 0;
        return Math.max(Math.max(declaredSize, slotDerivedCapacity), playerCapacity);
    }

    private static String resolveContainerId(String rawId, int index) {
        String normalized = NormalizeUtil.normalizeContainerId(rawId);
        if (normalized != null && !normalized.isBlank() && normalized.matches("^[a-z0-9_./-]+$")) {
            return normalized;
        }
        return "c" + Math.max(0, index);
    }

    private static ContainerBindType resolveBindType(String rawBindType) {
        if (rawBindType == null || rawBindType.isBlank()) {
            return ContainerBindType.PLAYER;
        }
        ContainerBindType bindType = ContainerBindType.fromRaw(rawBindType);
        return bindType != null ? bindType : ContainerBindType.PLAYER;
    }

    private static int parsePositiveInt(String raw, int fallback) {
        if (raw == null || raw.isBlank()) {
            return Math.max(0, fallback);
        }
        try {
            int parsed = Integer.parseInt(raw.trim());
            return parsed > 0 ? parsed : Math.max(0, fallback);
        }
        catch (NumberFormatException ignored) {
            return Math.max(0, fallback);
        }
    }

    private static boolean parseBooleanLike(String raw) {
        String normalized;
        if (raw == null) {
            return false;
        }
        return switch (normalized = raw.trim().toLowerCase(Locale.ROOT)) {
            case "1", "true", "yes", "on", "enabled" -> true;
            default -> false;
        };
    }

    public int resolveSlotSizePx(int fallback) {
        int safeFallback = Math.max(1, fallback);
        String rawSlotSize = this.getAttribute("slot-size");
        int parsedSize = Size.parse(rawSlotSize);
        return parsedSize > 0 ? parsedSize : safeFallback;
    }
}

