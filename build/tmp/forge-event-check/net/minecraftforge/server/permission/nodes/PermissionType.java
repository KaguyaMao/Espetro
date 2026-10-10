/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.server.permission.nodes;

import java.util.Objects;

public final class PermissionType<T> {
    private final Class<T> typeToken;
    private final String typeName;

    PermissionType(Class<T> typeToken, String typeName) {
        this.typeToken = typeToken;
        this.typeName = typeName;
    }

    public Class<T> typeToken() {
        return this.typeToken;
    }

    public String typeName() {
        return this.typeName;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PermissionType)) {
            return false;
        }
        PermissionType otherType = (PermissionType)obj;
        return Objects.equals(this.typeToken, otherType.typeToken) && Objects.equals(this.typeName, otherType.typeName);
    }

    public int hashCode() {
        return Objects.hash(this.typeToken, this.typeName);
    }

    public String toString() {
        return "PermissionType[typeToken=" + String.valueOf(this.typeToken) + ", typeName=" + this.typeName + "]";
    }
}

