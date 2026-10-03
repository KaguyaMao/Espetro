/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.typings.desc;

import dev.latvian.mods.kubejs.typings.desc.PrimitiveDescJS;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import java.util.Collection;
import java.util.Map;

public interface DescriptionContext {
    public static final DescriptionContext DEFAULT = new DescriptionContext(){};
    public static final DescriptionContext DISPLAY = new DescriptionContext(){

        @Override
        public String typeName(Class<?> type) {
            String n = type.getName();
            int i = n.lastIndexOf(46);
            return i != -1 ? n.substring(i + 1) : n;
        }
    };

    default public TypeDescJS javaType(Class<?> type) {
        if (type == null) {
            return TypeDescJS.NULL;
        }
        if (type == Object.class) {
            return TypeDescJS.ANY;
        }
        if (Number.class.isAssignableFrom(type) || type == Byte.TYPE || type == Short.TYPE || type == Integer.TYPE || type == Long.TYPE || type == Float.TYPE || type == Double.TYPE) {
            return TypeDescJS.NUMBER;
        }
        if (CharSequence.class.isAssignableFrom(type) || type == Character.class || type == Character.TYPE) {
            return TypeDescJS.STRING;
        }
        if (type == Boolean.class || type == Boolean.TYPE) {
            return TypeDescJS.BOOLEAN;
        }
        if (Map.class.isAssignableFrom(type)) {
            return TypeDescJS.ANY.asMap(TypeDescJS.ANY);
        }
        if (type.isArray() || Collection.class.isAssignableFrom(type)) {
            return TypeDescJS.ANY.asArray();
        }
        return new PrimitiveDescJS(this.typeName(type));
    }

    default public String typeName(Class<?> type) {
        return type.getName();
    }
}

