/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.typings.desc;

import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;

public record PrimitiveDescJS(String type) implements TypeDescJS
{
    @Override
    public void build(StringBuilder builder) {
        builder.append(this.type);
    }

    @Override
    public String build() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}

