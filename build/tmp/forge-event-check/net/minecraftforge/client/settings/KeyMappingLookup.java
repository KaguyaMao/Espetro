/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.KeyMapping
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.settings;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyModifier;
import org.jetbrains.annotations.Nullable;

public class KeyMappingLookup {
    private static final EnumMap<KeyModifier, Map<InputConstants.Key, List<KeyMapping>>> map = new EnumMap(KeyModifier.class);

    @Deprecated(forRemoval=true, since="1.20.1")
    @Nullable
    public KeyMapping get(InputConstants.Key keyCode) {
        KeyMapping binding;
        KeyModifier activeModifier = KeyModifier.getActiveModifier();
        if (!activeModifier.matches(keyCode) && (binding = this.get(keyCode, activeModifier)) != null) {
            return binding;
        }
        return this.get(keyCode, KeyModifier.NONE);
    }

    @Deprecated(forRemoval=true, since="1.20.1")
    @Nullable
    private KeyMapping get(InputConstants.Key keyCode, KeyModifier keyModifier) {
        List<KeyMapping> bindings = map.get((Object)keyModifier).get(keyCode);
        if (bindings != null) {
            for (KeyMapping binding : bindings) {
                if (!binding.isActiveAndMatches(keyCode)) continue;
                return binding;
            }
        }
        return null;
    }

    public List<KeyMapping> getAll(InputConstants.Key keyCode) {
        ArrayList<KeyMapping> ret = new ArrayList<KeyMapping>();
        for (KeyModifier modifier : KeyModifier.getValues(false)) {
            if (!modifier.isActive(null) || modifier.matches(keyCode)) continue;
            for (KeyMapping binding : this.get(modifier, keyCode)) {
                if (!binding.isActiveAndMatches(keyCode)) continue;
                ret.add(binding);
            }
        }
        if (!ret.isEmpty()) {
            return ret;
        }
        for (KeyMapping binding : this.get(KeyModifier.NONE, keyCode)) {
            if (!binding.isActiveAndMatches(keyCode)) continue;
            ret.add(binding);
        }
        return ret;
    }

    private List<KeyMapping> get(KeyModifier modifier, InputConstants.Key keyCode) {
        List<KeyMapping> bindings = map.get((Object)modifier).get(keyCode);
        return bindings == null ? Collections.emptyList() : bindings;
    }

    public void put(InputConstants.Key keyCode, KeyMapping keyBinding) {
        Map<InputConstants.Key, List<KeyMapping>> bindingsMap = map.get((Object)keyBinding.getKeyModifier());
        List bindingsForKey = bindingsMap.computeIfAbsent(keyCode, k -> new ArrayList());
        bindingsForKey.add(keyBinding);
    }

    public void remove(KeyMapping keyBinding) {
        InputConstants.Key keyCode = keyBinding.getKey();
        Map<InputConstants.Key, List<KeyMapping>> bindingsMap = map.get((Object)keyBinding.getKeyModifier());
        List<KeyMapping> bindingsForKey = bindingsMap.get(keyCode);
        if (bindingsForKey != null) {
            bindingsForKey.remove(keyBinding);
            if (bindingsForKey.isEmpty()) {
                bindingsMap.remove(keyCode);
            }
        }
    }

    public void clear() {
        map.values().forEach(Map::clear);
    }

    static {
        for (KeyModifier modifier : KeyModifier.values()) {
            map.put(modifier, new HashMap());
        }
    }
}

