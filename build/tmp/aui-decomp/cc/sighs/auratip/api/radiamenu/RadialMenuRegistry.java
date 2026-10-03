/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.data.DataManager
 *  javax.annotation.Nullable
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.api.radiamenu;

import cc.sighs.auratip.data.RadialMenuData;
import cc.sighs.oelib.data.DataManager;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public final class RadialMenuRegistry {
    private static final String OWNER_DEFAULT = "default";
    private static final String OWNER_KUBEJS = "kubejs";
    private static final Map<String, Map<ResourceLocation, RadialMenuData>> BY_OWNER = new LinkedHashMap<String, Map<ResourceLocation, RadialMenuData>>();
    private static volatile Map<ResourceLocation, RadialMenuData> SNAPSHOT_BY_ID = Collections.emptyMap();

    private RadialMenuRegistry() {
    }

    public static String ownerKubejs() {
        return OWNER_KUBEJS;
    }

    public static Collection<RadialMenuData> getAllRuntimeMenus() {
        return SNAPSHOT_BY_ID.values();
    }

    public static synchronized Collection<RadialMenuData> getMenus(@Nullable String owner) {
        String key = RadialMenuRegistry.normalizeOwner(owner);
        Map<ResourceLocation, RadialMenuData> map = BY_OWNER.get(key);
        if (map == null || map.isEmpty()) {
            return List.of();
        }
        return map.values();
    }

    @Nullable
    public static RadialMenuData getRuntimeMenu(ResourceLocation id) {
        if (id == null) {
            return null;
        }
        return SNAPSHOT_BY_ID.get(id);
    }

    @Nullable
    public static RadialMenuData getDataMenu(ResourceLocation id) {
        if (id == null) {
            return null;
        }
        return RadialMenuRegistry.getDataMenusById().get(id);
    }

    public static synchronized void setMenus(@Nullable String owner, @Nullable Collection<RadialMenuData> menus) {
        String key = RadialMenuRegistry.normalizeOwner(owner);
        if (menus == null || menus.isEmpty()) {
            BY_OWNER.remove(key);
            RadialMenuRegistry.rebuildSnapshot();
            return;
        }
        LinkedHashMap<ResourceLocation, RadialMenuData> map = new LinkedHashMap<ResourceLocation, RadialMenuData>();
        for (RadialMenuData menu : menus) {
            if (menu == null) {
                throw new IllegalStateException("RadialMenuRegistry.setMenus: menu is null (owner='" + key + "')");
            }
            ResourceLocation id = Objects.requireNonNull(menu.id(), "menu.id()");
            if (map.containsKey(id)) {
                throw new IllegalStateException("Duplicate radial menu id '" + String.valueOf(id) + "' within owner '" + key + "'");
            }
            map.put(id, menu);
        }
        Map<ResourceLocation, RadialMenuData> data = RadialMenuRegistry.getDataMenusById();
        for (ResourceLocation id : map.keySet()) {
            if (!data.containsKey(id)) continue;
            throw new IllegalStateException("Duplicate radial menu id '" + String.valueOf(id) + "' detected between runtime and datapack menus.");
        }
        BY_OWNER.put(key, Map.copyOf(map));
        RadialMenuRegistry.rebuildSnapshot();
    }

    public static synchronized void clear(@Nullable String owner) {
        RadialMenuRegistry.setMenus(owner, null);
    }

    public static synchronized void clearAll() {
        BY_OWNER.clear();
        RadialMenuRegistry.rebuildSnapshot();
    }

    @Nullable
    public static RadialMenuData resolveMenuToOpen(@Nullable ResourceLocation menuId) {
        int dataCount;
        if (menuId != null) {
            RadialMenuData runtime = RadialMenuRegistry.getRuntimeMenu(menuId);
            if (runtime != null) {
                return runtime;
            }
            return RadialMenuRegistry.getDataMenu(menuId);
        }
        Map<ResourceLocation, RadialMenuData> data = RadialMenuRegistry.getDataMenusById();
        int runtimeCount = SNAPSHOT_BY_ID.size();
        int total = runtimeCount + (dataCount = data.size());
        if (total == 0) {
            return null;
        }
        if (total == 1) {
            if (runtimeCount == 1) {
                return SNAPSHOT_BY_ID.values().iterator().next();
            }
            return data.values().iterator().next();
        }
        return null;
    }

    private static String normalizeOwner(@Nullable String owner) {
        if (owner == null || owner.isBlank()) {
            return OWNER_DEFAULT;
        }
        return owner.trim();
    }

    private static void rebuildSnapshot() {
        if (BY_OWNER.isEmpty()) {
            SNAPSHOT_BY_ID = Collections.emptyMap();
            return;
        }
        LinkedHashMap<ResourceLocation, RadialMenuData> merged = new LinkedHashMap<ResourceLocation, RadialMenuData>();
        for (Map.Entry<String, Map<ResourceLocation, RadialMenuData>> ownerEntry : BY_OWNER.entrySet()) {
            String owner = ownerEntry.getKey();
            Map<ResourceLocation, RadialMenuData> map = ownerEntry.getValue();
            if (map == null || map.isEmpty()) continue;
            for (Map.Entry<ResourceLocation, RadialMenuData> entry : map.entrySet()) {
                ResourceLocation id = entry.getKey();
                if (merged.containsKey(id)) {
                    throw new IllegalStateException("Duplicate radial menu id '" + String.valueOf(id) + "' detected across runtime owners (at least '" + owner + "').");
                }
                merged.put(id, entry.getValue());
            }
        }
        SNAPSHOT_BY_ID = merged.isEmpty() ? Collections.emptyMap() : Map.copyOf(merged);
    }

    private static Map<ResourceLocation, RadialMenuData> getDataMenusById() {
        List data = DataManager.getDataList(RadialMenuData.class);
        if (data == null || data.isEmpty()) {
            return Map.of();
        }
        LinkedHashMap<ResourceLocation, RadialMenuData> out = new LinkedHashMap<ResourceLocation, RadialMenuData>();
        for (RadialMenuData menu : data) {
            if (menu == null) continue;
            ResourceLocation id = Objects.requireNonNull(menu.id(), "datapack radial menu id");
            if (out.containsKey(id)) {
                throw new IllegalStateException("Duplicate datapack radial menu id '" + String.valueOf(id) + "' detected.");
            }
            out.put(id, menu);
        }
        return out;
    }
}

