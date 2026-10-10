/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.typings.desc;

import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;

public record ArrayDescJS(TypeDescJS type) implements TypeDescJS
{
    @Override
    public void build(StringBuilder builder) {
        this.type.build(builder);
        builder.append('[');
        builder.append(']');
    }

    @Override
    public String toString() {
        return this.build();
    }
}

