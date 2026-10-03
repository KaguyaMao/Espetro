/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnegative
 */
package net.minecraftforge.common.util;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnegative;

public final class Size2i {
    @Nonnegative
    public final int width;
    @Nonnegative
    public final int height;

    public Size2i(@Nonnegative int width, @Nonnegative int height) {
        Preconditions.checkArgument((width >= 0 ? 1 : 0) != 0, (Object)"width must be greater or equal 0");
        Preconditions.checkArgument((height >= 0 ? 1 : 0) != 0, (Object)"height must be greater or equal 0");
        this.width = width;
        this.height = height;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Size2i) {
            Size2i other = (Size2i)obj;
            return this.width == other.width && this.height == other.height;
        }
        return false;
    }

    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + this.width;
        hash = hash * 31 + this.height;
        return hash;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("width", this.width).add("height", this.height).toString();
    }
}

