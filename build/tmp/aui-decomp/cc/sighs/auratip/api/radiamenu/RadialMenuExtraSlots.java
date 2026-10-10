/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.api.radiamenu;

import cc.sighs.auratip.api.radiamenu.icon.IRadialIcon;
import cc.sighs.auratip.data.RadialMenuData;
import cc.sighs.auratip.data.action.Action;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class RadialMenuExtraSlots {
    private static final String OWNER_DEFAULT = "default";
    private static final String OWNER_KUBEJS = "kubejs";
    private static final Map<String, List<RadialMenuData.Slot>> BY_OWNER = new LinkedHashMap<String, List<RadialMenuData.Slot>>();
    private static final Map<String, Map<ResourceLocation, List<RadialMenuData.Slot>>> BY_OWNER_BY_MENU = new LinkedHashMap<String, Map<ResourceLocation, List<RadialMenuData.Slot>>>();
    private static volatile List<RadialMenuData.Slot> SNAPSHOT = Collections.emptyList();
    private static volatile Map<ResourceLocation, List<RadialMenuData.Slot>> SNAPSHOT_BY_MENU = Collections.emptyMap();

    private RadialMenuExtraSlots() {
    }

    public static synchronized void addSlot(RadialMenuData.Slot slot) {
        RadialMenuExtraSlots.addSlot(OWNER_DEFAULT, slot);
    }

    public static synchronized void addSlot(String owner, RadialMenuData.Slot slot) {
        if (slot == null) {
            return;
        }
        String key = RadialMenuExtraSlots.normalizeOwner(owner);
        List list = BY_OWNER.computeIfAbsent(key, k -> new ArrayList());
        list.add(slot);
        RadialMenuExtraSlots.rebuildSnapshot();
    }

    public static synchronized void addSlotForMenu(ResourceLocation menuId, RadialMenuData.Slot slot) {
        RadialMenuExtraSlots.addSlotForMenu(OWNER_DEFAULT, menuId, slot);
    }

    public static synchronized void addSlotForMenu(String owner, ResourceLocation menuId, RadialMenuData.Slot slot) {
        if (menuId == null || slot == null) {
            return;
        }
        String key = RadialMenuExtraSlots.normalizeOwner(owner);
        Map byMenu = BY_OWNER_BY_MENU.computeIfAbsent(key, k -> new LinkedHashMap());
        List list = byMenu.computeIfAbsent(menuId, k -> new ArrayList());
        list.add(slot);
        RadialMenuExtraSlots.rebuildSnapshot();
    }

    public static void addSlot(String name, IRadialIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor) {
        RadialMenuExtraSlots.addSlot(name, icon, action, text, highlightColor, true, null);
    }

    public static void addSlot(String name, IRadialIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction, @Nullable String baseColor) {
        if (name == null || name.isEmpty() || icon == null || action == null) {
            return;
        }
        RadialMenuExtraSlots.addSlot(new RadialMenuData.Slot(name, icon, action, Optional.ofNullable(text), Optional.ofNullable(highlightColor), closeAfterAction, Optional.ofNullable(baseColor)));
    }

    public static void addPersistentSlot(String name, IRadialIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor, @Nullable String baseColor) {
        RadialMenuExtraSlots.addSlot(name, icon, action, text, highlightColor, false, baseColor);
    }

    public static void addSlotForMenu(ResourceLocation menuId, String name, IRadialIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor) {
        RadialMenuExtraSlots.addSlotForMenu(menuId, name, icon, action, text, highlightColor, true, null);
    }

    public static void addSlotForMenu(ResourceLocation menuId, String name, IRadialIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction, @Nullable String baseColor) {
        if (menuId == null || name == null || name.isEmpty() || icon == null || action == null) {
            return;
        }
        RadialMenuExtraSlots.addSlotForMenu(menuId, new RadialMenuData.Slot(name, icon, action, Optional.ofNullable(text), Optional.ofNullable(highlightColor), closeAfterAction, Optional.ofNullable(baseColor)));
    }

    public static void addPersistentSlotForMenu(ResourceLocation menuId, String name, IRadialIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor, @Nullable String baseColor) {
        RadialMenuExtraSlots.addSlotForMenu(menuId, name, icon, action, text, highlightColor, false, baseColor);
    }

    public static synchronized void removeSlot(RadialMenuData.Slot slot) {
        RadialMenuExtraSlots.removeSlot(OWNER_DEFAULT, slot);
    }

    public static synchronized void removeSlot(String owner, RadialMenuData.Slot slot) {
        if (slot == null) {
            return;
        }
        String key = RadialMenuExtraSlots.normalizeOwner(owner);
        List<RadialMenuData.Slot> list = BY_OWNER.get(key);
        if (list != null && list.remove(slot)) {
            if (list.isEmpty()) {
                BY_OWNER.remove(key);
            }
            RadialMenuExtraSlots.rebuildSnapshot();
        }
    }

    public static synchronized void removeSlotForMenu(ResourceLocation menuId, RadialMenuData.Slot slot) {
        RadialMenuExtraSlots.removeSlotForMenu(OWNER_DEFAULT, menuId, slot);
    }

    public static synchronized void removeSlotForMenu(String owner, ResourceLocation menuId, RadialMenuData.Slot slot) {
        if (menuId == null || slot == null) {
            return;
        }
        String key = RadialMenuExtraSlots.normalizeOwner(owner);
        Map<ResourceLocation, List<RadialMenuData.Slot>> byMenu = BY_OWNER_BY_MENU.get(key);
        if (byMenu == null) {
            return;
        }
        List<RadialMenuData.Slot> list = byMenu.get(menuId);
        if (list != null && list.remove(slot)) {
            if (list.isEmpty()) {
                byMenu.remove(menuId);
            }
            if (byMenu.isEmpty()) {
                BY_OWNER_BY_MENU.remove(key);
            }
            RadialMenuExtraSlots.rebuildSnapshot();
        }
    }

    public static synchronized void removeSlot(String name) {
        RadialMenuExtraSlots.removeSlot(OWNER_DEFAULT, name);
    }

    public static synchronized void removeSlot(String owner, String name) {
        if (name == null || name.isEmpty()) {
            return;
        }
        String key = RadialMenuExtraSlots.normalizeOwner(owner);
        List<RadialMenuData.Slot> list = BY_OWNER.get(key);
        if (list == null || list.isEmpty()) {
            return;
        }
        boolean changed = list.removeIf(s -> name.equals(s.name()));
        if (changed) {
            if (list.isEmpty()) {
                BY_OWNER.remove(key);
            }
            RadialMenuExtraSlots.rebuildSnapshot();
        }
    }

    public static synchronized void removeSlotForMenu(ResourceLocation menuId, String name) {
        RadialMenuExtraSlots.removeSlotForMenu(OWNER_DEFAULT, menuId, name);
    }

    public static synchronized void removeSlotForMenu(String owner, ResourceLocation menuId, String name) {
        if (menuId == null || name == null || name.isEmpty()) {
            return;
        }
        String key = RadialMenuExtraSlots.normalizeOwner(owner);
        Map<ResourceLocation, List<RadialMenuData.Slot>> byMenu = BY_OWNER_BY_MENU.get(key);
        if (byMenu == null) {
            return;
        }
        List<RadialMenuData.Slot> list = byMenu.get(menuId);
        if (list == null || list.isEmpty()) {
            return;
        }
        boolean changed = list.removeIf(s -> name.equals(s.name()));
        if (changed) {
            if (list.isEmpty()) {
                byMenu.remove(menuId);
            }
            if (byMenu.isEmpty()) {
                BY_OWNER_BY_MENU.remove(key);
            }
            RadialMenuExtraSlots.rebuildSnapshot();
        }
    }

    public static synchronized List<RadialMenuData.Slot> getSlots() {
        return SNAPSHOT;
    }

    public static synchronized List<RadialMenuData.Slot> getSlotsForMenu(ResourceLocation menuId) {
        if (menuId == null) {
            return SNAPSHOT;
        }
        List<RadialMenuData.Slot> scoped = SNAPSHOT_BY_MENU.get(menuId);
        if (scoped == null || scoped.isEmpty()) {
            return SNAPSHOT;
        }
        if (SNAPSHOT.isEmpty()) {
            return scoped;
        }
        ArrayList<RadialMenuData.Slot> merged = new ArrayList<RadialMenuData.Slot>(SNAPSHOT.size() + scoped.size());
        merged.addAll(SNAPSHOT);
        merged.addAll(scoped);
        return List.copyOf(merged);
    }

    public static synchronized List<RadialMenuData.Slot> getSlots(String owner) {
        String key = RadialMenuExtraSlots.normalizeOwner(owner);
        List<RadialMenuData.Slot> list = BY_OWNER.get(key);
        return list == null ? List.of() : List.copyOf(list);
    }

    public static synchronized void clear() {
        RadialMenuExtraSlots.clear(OWNER_DEFAULT);
    }

    public static synchronized void clear(String owner) {
        String key = RadialMenuExtraSlots.normalizeOwner(owner);
        BY_OWNER.remove(key);
        BY_OWNER_BY_MENU.remove(key);
        RadialMenuExtraSlots.rebuildSnapshot();
    }

    public static synchronized void clearAll() {
        BY_OWNER.clear();
        BY_OWNER_BY_MENU.clear();
        RadialMenuExtraSlots.rebuildSnapshot();
    }

    public static String ownerKubejs() {
        return OWNER_KUBEJS;
    }

    private static String normalizeOwner(@Nullable String owner) {
        if (owner == null || owner.isBlank()) {
            return OWNER_DEFAULT;
        }
        return owner.trim();
    }

    private static void rebuildSnapshot() {
        if (BY_OWNER.isEmpty()) {
            SNAPSHOT = Collections.emptyList();
        } else {
            ArrayList<RadialMenuData.Slot> out = new ArrayList<RadialMenuData.Slot>();
            for (List<RadialMenuData.Slot> list : BY_OWNER.values()) {
                if (list == null || list.isEmpty()) continue;
                out.addAll(list);
            }
            List<Object> list = SNAPSHOT = out.isEmpty() ? Collections.emptyList() : List.copyOf(out);
        }
        if (BY_OWNER_BY_MENU.isEmpty()) {
            SNAPSHOT_BY_MENU = Collections.emptyMap();
            return;
        }
        LinkedHashMap merged = new LinkedHashMap();
        for (Map map : BY_OWNER_BY_MENU.values()) {
            if (map == null || map.isEmpty()) continue;
            for (Map.Entry entry : map.entrySet()) {
                ResourceLocation menuId = (ResourceLocation)entry.getKey();
                List list = (List)entry.getValue();
                if (menuId == null || list == null || list.isEmpty()) continue;
                List existing = (List)merged.get(menuId);
                if (existing == null) {
                    merged.put(menuId, List.copyOf(list));
                    continue;
                }
                ArrayList combined = new ArrayList(existing.size() + list.size());
                combined.addAll(existing);
                combined.addAll(list);
                merged.put(menuId, List.copyOf(combined));
            }
        }
        SNAPSHOT_BY_MENU = merged.isEmpty() ? Collections.emptyMap() : Map.copyOf(merged);
    }
}

