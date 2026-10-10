/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonArray;
import dev.latvian.mods.kubejs.util.JsonIO;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public interface ListJS {
    @Nullable
    public static List<?> of(@Nullable Object o) {
        if (o instanceof List) {
            List l = (List)o;
            return l;
        }
        if (o instanceof Iterable) {
            int n;
            Iterable itr = (Iterable)o;
            if (itr instanceof Collection) {
                Collection c = (Collection)itr;
                n = c.size();
            } else {
                n = 4;
            }
            ArrayList list = new ArrayList(n);
            for (Object o1 : itr) {
                list.add(o1);
            }
            return list;
        }
        return ListJS.ofArray(o);
    }

    public static List<?> orEmpty(@Nullable Object o) {
        List<?> l = ListJS.of(o);
        return l == null ? List.of() : l;
    }

    public static List<?> orSelf(@Nullable Object o) {
        List<?> l = ListJS.of(o);
        if (l != null) {
            return l;
        }
        ArrayList<Object> list = new ArrayList<Object>(1);
        if (o != null) {
            list.add(o);
        }
        return list;
    }

    @Nullable
    public static List<?> ofArray(@Nullable Object array) {
        if (array instanceof Object[]) {
            return new ArrayList<Object>(Arrays.asList((Object[])array));
        }
        if (array instanceof int[]) {
            return ListJS.of((int[])array);
        }
        if (array instanceof byte[]) {
            return ListJS.of((byte[])array);
        }
        if (array instanceof short[]) {
            return ListJS.of((short[])array);
        }
        if (array instanceof long[]) {
            return ListJS.of((long[])array);
        }
        if (array instanceof float[]) {
            return ListJS.of((float[])array);
        }
        if (array instanceof double[]) {
            return ListJS.of((double[])array);
        }
        if (array instanceof char[]) {
            return ListJS.of((char[])array);
        }
        if (array != null && array.getClass().isArray()) {
            int length = Array.getLength(array);
            ArrayList<Object> list = new ArrayList<Object>(length);
            for (int i = 0; i < length; ++i) {
                list.add(Array.get(array, i));
            }
            return list;
        }
        return null;
    }

    public static List<Byte> of(byte[] array) {
        ArrayList<Byte> list = new ArrayList<Byte>(array.length);
        for (byte v : array) {
            list.add(v);
        }
        return list;
    }

    public static List<Short> of(short[] array) {
        ArrayList<Short> list = new ArrayList<Short>(array.length);
        for (short v : array) {
            list.add(v);
        }
        return list;
    }

    public static List<Integer> of(int[] array) {
        ArrayList<Integer> list = new ArrayList<Integer>(array.length);
        for (int v : array) {
            list.add(v);
        }
        return list;
    }

    public static List<Long> of(long[] array) {
        ArrayList<Long> list = new ArrayList<Long>(array.length);
        for (long v : array) {
            list.add(v);
        }
        return list;
    }

    public static List<Float> of(float[] array) {
        ArrayList<Float> list = new ArrayList<Float>(array.length);
        for (float v : array) {
            list.add(Float.valueOf(v));
        }
        return list;
    }

    public static List<Double> of(double[] array) {
        ArrayList<Double> list = new ArrayList<Double>(array.length);
        for (double v : array) {
            list.add(v);
        }
        return list;
    }

    public static List<Character> of(char[] array) {
        ArrayList<Character> list = new ArrayList<Character>(array.length);
        for (char v : array) {
            list.add(Character.valueOf(v));
        }
        return list;
    }

    @Nullable
    public static Set<?> ofSet(@Nullable Object o) {
        if (o instanceof Set) {
            Set s = (Set)o;
            return s;
        }
        if (o instanceof Collection) {
            Collection c = (Collection)o;
            return new LinkedHashSet(c);
        }
        if (o instanceof Iterable) {
            Iterable itr = (Iterable)o;
            HashSet set = new HashSet();
            for (Object o1 : itr) {
                set.add(o1);
            }
            return set;
        }
        List<?> list = ListJS.of(o);
        return list == null ? null : new LinkedHashSet(list);
    }

    @Nullable
    public static JsonArray json(@Nullable Object array) {
        if (array instanceof JsonArray) {
            JsonArray arr = (JsonArray)array;
            return arr;
        }
        if (array instanceof CharSequence) {
            try {
                return (JsonArray)JsonIO.GSON.fromJson(array.toString(), JsonArray.class);
            }
            catch (Exception ex) {
                return null;
            }
        }
        if (array instanceof Iterable) {
            Iterable itr = (Iterable)array;
            JsonArray json = new JsonArray();
            for (Object o1 : itr) {
                json.add(JsonIO.of(o1));
            }
            return json;
        }
        return null;
    }
}

