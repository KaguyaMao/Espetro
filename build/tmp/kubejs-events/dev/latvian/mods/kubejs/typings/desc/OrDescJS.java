/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.typings.desc;

import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;

public record OrDescJS(TypeDescJS[] types) implements TypeDescJS
{
    @Override
    public void build(StringBuilder builder) {
        for (int i = 0; i < this.types.length; ++i) {
            if (i > 0) {
                builder.append(" | ");
            }
            this.types[i].build(builder);
        }
    }

    @Override
    public TypeDescJS or(TypeDescJS type) {
        if (type instanceof OrDescJS) {
            OrDescJS t = (OrDescJS)type;
            TypeDescJS[] types1 = new TypeDescJS[this.types.length + t.types.length];
            System.arraycopy(this.types, 0, types1, 0, this.types.length);
            System.arraycopy(t.types, 0, types1, this.types.length, t.types.length);
            return new OrDescJS(types1);
        }
        TypeDescJS[] types1 = new TypeDescJS[this.types.length + 1];
        System.arraycopy(this.types, 0, types1, 0, this.types.length);
        types1[this.types.length] = type;
        return new OrDescJS(types1);
    }

    @Override
    public String toString() {
        return this.build();
    }
}

