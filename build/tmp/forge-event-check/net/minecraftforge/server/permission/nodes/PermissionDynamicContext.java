/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.server.permission.nodes;

import java.util.Objects;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContextKey;

public final class PermissionDynamicContext<T> {
    private PermissionDynamicContextKey<T> dynamic;
    private T value;

    PermissionDynamicContext(PermissionDynamicContextKey<T> dynamic, T value) {
        this.dynamic = dynamic;
        this.value = value;
    }

    public PermissionDynamicContextKey<T> getDynamic() {
        return this.dynamic;
    }

    public T getValue() {
        return this.value;
    }

    public String getSerializedValue() {
        return this.dynamic.serializer().apply(this.value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermissionDynamicContext)) {
            return false;
        }
        PermissionDynamicContext otherContext = (PermissionDynamicContext)o;
        return this.dynamic.equals(otherContext.dynamic) && this.value.equals(otherContext.value);
    }

    public int hashCode() {
        return Objects.hash(this.dynamic, this.value);
    }
}

