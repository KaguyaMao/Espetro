/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.render;

public final class DirtyFlags {
    private int flags = 0;

    public void add(int mask) {
        this.flags |= mask;
    }

    public boolean has(int mask) {
        return (this.flags & mask) != 0;
    }

    public void clear() {
        this.flags = 0;
    }
}

