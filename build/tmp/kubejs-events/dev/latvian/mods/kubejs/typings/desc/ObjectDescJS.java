/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.typings.desc;

import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import java.util.List;
import java.util.regex.Pattern;

public record ObjectDescJS(List<Entry> types) implements TypeDescJS
{
    public ObjectDescJS add(String key, TypeDescJS value) {
        this.types.add(new Entry(key, value, false));
        return this;
    }

    public ObjectDescJS add(String key, TypeDescJS value, boolean optional) {
        this.types.add(new Entry(key, value, optional));
        return this;
    }

    @Override
    public void build(StringBuilder builder) {
        builder.append('{');
        for (int i = 0; i < this.types.size(); ++i) {
            if (i > 0) {
                builder.append(',');
                builder.append(' ');
            }
            if (this.types.get((int)i).wrap) {
                builder.append('\"');
            }
            builder.append(this.types.get((int)i).key);
            if (this.types.get((int)i).wrap) {
                builder.append('\"');
            }
            if (this.types.get((int)i).optional) {
                builder.append('?');
            }
            builder.append(':');
            builder.append(' ');
            this.types.get((int)i).value.build(builder);
        }
        builder.append('}');
    }

    @Override
    public String toString() {
        return this.build();
    }

    public record Entry(String key, TypeDescJS value, boolean optional, boolean wrap) {
        private static final Pattern ILLEGAL_KEY_PATTERN = Pattern.compile("[^a-zA-Z0-9_$]");

        public Entry(String key, TypeDescJS value, boolean optional) {
            this(key, value, optional, ILLEGAL_KEY_PATTERN.matcher(key).find());
        }
    }
}

