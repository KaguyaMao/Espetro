/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.typings.desc;

import dev.latvian.mods.kubejs.typings.desc.ArrayDescJS;
import dev.latvian.mods.kubejs.typings.desc.FixedArrayDescJS;
import dev.latvian.mods.kubejs.typings.desc.GenericDescJS;
import dev.latvian.mods.kubejs.typings.desc.ObjectDescJS;
import dev.latvian.mods.kubejs.typings.desc.OrDescJS;
import dev.latvian.mods.kubejs.typings.desc.PrimitiveDescJS;
import java.util.ArrayList;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public interface TypeDescJS {
    public static final TypeDescJS ANY = new PrimitiveDescJS("any");
    public static final TypeDescJS NULL = new PrimitiveDescJS("null");
    public static final TypeDescJS STRING = new PrimitiveDescJS("string");
    public static final TypeDescJS NUMBER = new PrimitiveDescJS("number");
    public static final TypeDescJS BOOLEAN = new PrimitiveDescJS("boolean");
    public static final TypeDescJS MAP = new PrimitiveDescJS("Map");
    public static final TypeDescJS ANY_MAP = new GenericDescJS(MAP, STRING, ANY);

    public static TypeDescJS fixedArray(TypeDescJS ... types) {
        return new FixedArrayDescJS(types);
    }

    public static TypeDescJS any(TypeDescJS ... types) {
        return new OrDescJS(types);
    }

    public static ObjectDescJS object() {
        return new ObjectDescJS(new ArrayList<ObjectDescJS.Entry>());
    }

    public static ObjectDescJS object(int init) {
        return new ObjectDescJS(new ArrayList<ObjectDescJS.Entry>(init));
    }

    public void build(StringBuilder var1);

    default public String build() {
        StringBuilder builder = new StringBuilder();
        this.build(builder);
        return builder.toString();
    }

    default public TypeDescJS asArray() {
        return new ArrayDescJS(this);
    }

    default public TypeDescJS asMap() {
        return this.asMap(STRING);
    }

    default public TypeDescJS asMap(TypeDescJS key) {
        if (key == STRING && this == ANY) {
            return ANY_MAP;
        }
        return new GenericDescJS(MAP, key, this);
    }

    default public TypeDescJS or(TypeDescJS type) {
        return new OrDescJS(new TypeDescJS[]{this, type});
    }

    default public TypeDescJS withGenerics(TypeDescJS ... types) {
        return new GenericDescJS(this, types);
    }
}

