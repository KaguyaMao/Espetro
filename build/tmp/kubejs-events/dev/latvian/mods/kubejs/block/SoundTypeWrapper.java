/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.Undefined
 *  dev.latvian.mods.rhino.mod.util.RemappingHelper
 *  dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory
 *  net.minecraft.world.level.block.SoundType
 */
package dev.latvian.mods.kubejs.block;

import com.google.gson.JsonElement;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.level.block.SoundType;

public class SoundTypeWrapper
implements TypeWrapperFactory<SoundType> {
    public static final SoundTypeWrapper INSTANCE = new SoundTypeWrapper();
    private Map<String, SoundType> map;

    public Map<String, SoundType> getMap() {
        if (this.map == null) {
            this.map = new LinkedHashMap<String, SoundType>();
            this.map.put("empty", SoundType.f_279557_);
            try {
                for (Field field : SoundType.class.getFields()) {
                    if (field.getType() != SoundType.class || !Modifier.isPublic(field.getModifiers()) || !Modifier.isStatic(field.getModifiers())) continue;
                    try {
                        String r = RemappingHelper.getMinecraftRemapper().getMappedField(SoundType.class, field);
                        this.map.put((r.isBlank() ? field.getName() : r).toLowerCase(), (SoundType)field.get(null));
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return this.map;
    }

    public SoundType wrap(Context cx, Object o) {
        String string;
        if (o instanceof SoundType) {
            SoundType t = (SoundType)o;
            return t;
        }
        if (o == null || Undefined.isUndefined((Object)o)) {
            return SoundType.f_279557_;
        }
        Map<String, SoundType> map = this.getMap();
        if (o instanceof JsonElement) {
            JsonElement j = (JsonElement)o;
            string = j.getAsString();
        } else {
            string = o.toString();
        }
        return map.getOrDefault(string.toLowerCase(), SoundType.f_279557_);
    }
}

