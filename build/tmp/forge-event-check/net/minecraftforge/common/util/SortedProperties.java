/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.common.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class SortedProperties
extends Properties {
    private static final long serialVersionUID = -8913480931455982442L;

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        TreeSet<Map.Entry<Object, Object>> ret = new TreeSet<Map.Entry<Object, Object>>((left, right) -> left.getKey().toString().compareTo(right.getKey().toString()));
        ret.addAll(super.entrySet());
        return ret;
    }

    @Override
    public Set<Object> keySet() {
        return new TreeSet<Object>(super.keySet());
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
    }

    public static void store(Properties props, Writer stream, String comment) throws IOException {
        SortedProperties sorted = new SortedProperties();
        sorted.putAll((Map<?, ?>)props);
        sorted.store(stream, comment);
    }
}

