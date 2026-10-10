/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package cc.sighs.auratip.api.tip;

import cc.sighs.auratip.data.TipData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class TipRegistry {
    private static final String OWNER_KUBEJS = "kubejs";
    private static final Map<String, List<TipData>> BY_OWNER = new LinkedHashMap<String, List<TipData>>();
    private static volatile List<TipData> SNAPSHOT = Collections.emptyList();

    private TipRegistry() {
    }

    public static List<TipData> getTips() {
        return SNAPSHOT;
    }

    public static synchronized List<TipData> getTips(@Nullable String owner) {
        String key = TipRegistry.normalizeOwner(owner);
        List<TipData> list = BY_OWNER.get(key);
        return list == null ? List.of() : list;
    }

    public static synchronized void setTips(@Nullable String owner, @Nullable Collection<TipData> newTips) {
        String key = TipRegistry.normalizeOwner(owner);
        if (newTips == null || newTips.isEmpty()) {
            BY_OWNER.remove(key);
        } else {
            LinkedHashMap<String, TipData> byId = new LinkedHashMap<String, TipData>();
            for (TipData tip : newTips) {
                if (tip == null) {
                    throw new IllegalStateException("TipRegistry.setTips: tip is null (owner='" + key + "')");
                }
                if (tip.id() == null) {
                    throw new IllegalStateException("TipRegistry.setTips: tip.id is null (owner='" + key + "')");
                }
                String id = tip.id().toString();
                TipData previous = byId.put(id, tip);
                if (previous == null) continue;
                throw new IllegalStateException("Duplicate TipData id '" + id + "' inside owner '" + key + "'");
            }
            BY_OWNER.put(key, List.copyOf(byId.values()));
        }
        TipRegistry.rebuildSnapshot();
    }

    public static synchronized void clear(@Nullable String owner) {
        String key = TipRegistry.normalizeOwner(owner);
        BY_OWNER.remove(key);
        TipRegistry.rebuildSnapshot();
    }

    public static synchronized void clearAll() {
        BY_OWNER.clear();
        TipRegistry.rebuildSnapshot();
    }

    public static String ownerKubejs() {
        return OWNER_KUBEJS;
    }

    private static String normalizeOwner(@Nullable String owner) {
        return owner == null ? "" : owner.trim();
    }

    private static void rebuildSnapshot() {
        if (BY_OWNER.isEmpty()) {
            SNAPSHOT = Collections.emptyList();
            return;
        }
        ArrayList<TipData> out = new ArrayList<TipData>();
        LinkedHashMap<String, String> ownerByTipId = new LinkedHashMap<String, String>();
        for (Map.Entry<String, List<TipData>> entry : BY_OWNER.entrySet()) {
            String owner = entry.getKey();
            List<TipData> list = entry.getValue();
            if (list == null || list.isEmpty()) continue;
            for (TipData tip : list) {
                if (tip == null || tip.id() == null) continue;
                String id = tip.id().toString();
                String previousOwner = ownerByTipId.putIfAbsent(id, owner);
                if (previousOwner != null) {
                    throw new IllegalStateException("Duplicate TipData id '" + id + "' detected across owners. owner=" + previousOwner + " and owner=" + owner);
                }
                out.add(tip);
            }
        }
        SNAPSHOT = out.isEmpty() ? Collections.emptyList() : List.copyOf(out);
    }
}

