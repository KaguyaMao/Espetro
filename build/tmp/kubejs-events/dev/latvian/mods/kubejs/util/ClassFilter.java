/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap
 */
package dev.latvian.mods.kubejs.util;

import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassFilter {
    private static final byte V_DEF = -1;
    private static final byte V_DENY = 0;
    private static final byte V_ALLOW = 1;
    private final Set<String> denyStrong = new HashSet<String>();
    private final List<String> denyWeak = new ArrayList<String>();
    private final Set<String> allowStrong = new HashSet<String>();
    private final List<String> allowWeak = new ArrayList<String>();
    private final Object2ByteOpenHashMap<String> cache = new Object2ByteOpenHashMap();

    public ClassFilter() {
        this.cache.defaultReturnValue((byte)-1);
    }

    public void deny(String s) {
        if ((s = s.trim()).isEmpty()) {
            return;
        }
        this.denyStrong.add(s);
        if (!this.denyWeak.contains(s)) {
            this.denyWeak.add(s);
        }
    }

    public void deny(Class<?> c) {
        this.deny(c.getName());
    }

    public void allow(String s) {
        if ((s = s.trim()).isEmpty()) {
            return;
        }
        this.allowStrong.add(s);
        if (!this.allowWeak.contains(s)) {
            this.allowWeak.add(s);
        }
    }

    public void allow(Class<?> c) {
        this.allow(c.getName());
    }

    private byte isAllowed0(String s) {
        if (this.denyStrong.contains(s)) {
            return 0;
        }
        if (this.allowStrong.contains(s)) {
            return 1;
        }
        for (String s1 : this.denyWeak) {
            if (!s.startsWith(s1)) continue;
            return 0;
        }
        return 1;
    }

    public boolean isAllowed(String s) {
        byte b = this.cache.getByte((Object)s);
        if (b == -1) {
            b = this.isAllowed0(s);
            this.cache.put((Object)s, b);
        }
        return b == 1;
    }
}

