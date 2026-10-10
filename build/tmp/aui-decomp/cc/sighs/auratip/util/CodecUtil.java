/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 */
package cc.sighs.auratip.util;

import cc.sighs.auratip.util.ColorUtil;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public final class CodecUtil {
    private CodecUtil() {
    }

    public static <T> Codec<T> stringOrIntList(Function<String, T> fromString, Function<List<Integer>, T> fromList, Function<T, String> toString, Function<T, List<Integer>> toList) {
        return Codec.either((Codec)Codec.STRING, (Codec)Codec.INT.listOf()).xmap(either -> either.map(fromString, fromList), value -> {
            String s = (String)toString.apply(value);
            if (s != null) {
                return Either.left((Object)s);
            }
            return Either.right((Object)((List)toList.apply(value)));
        });
    }

    public static <T> Codec<T> intOrList(Function<Integer, T> fromInt, Function<List<Integer>, T> fromList, Function<T, Integer> toInt, Function<T, List<Integer>> toList) {
        return Codec.either((Codec)Codec.INT, (Codec)Codec.INT.listOf()).xmap(either -> either.map(fromInt, fromList), value -> {
            int v = (Integer)toInt.apply(value);
            if (v != 0) {
                return Either.left((Object)v);
            }
            return Either.right((Object)((List)toList.apply(value)));
        });
    }

    public static <T> Codec<T> intOrListOrObject(Function<Integer, T> fromInt, Function<List<Integer>, T> fromList, Function<T, Integer> toInt, Function<T, List<Integer>> toList, Codec<T> objectCodec) {
        return Codec.either((Codec)Codec.INT, (Codec)Codec.either((Codec)Codec.INT.listOf(), objectCodec)).xmap(either -> {
            if (either.left().isPresent()) {
                return fromInt.apply((Integer)either.left().get());
            }
            Either inner = (Either)either.right().get();
            return inner.map(fromList, Function.identity());
        }, value -> {
            int v = (Integer)toInt.apply(value);
            if (v != 0) {
                return Either.left((Object)v);
            }
            return Either.right((Object)Either.left((Object)((List)toList.apply(value))));
        });
    }

    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> enumClass) {
        return Codec.STRING.xmap(value -> {
            for (Enum c : (Enum[])enumClass.getEnumConstants()) {
                if (!c.name().equalsIgnoreCase((String)value)) continue;
                return c;
            }
            return Enum.valueOf(enumClass, value.toUpperCase(Locale.ROOT));
        }, e -> e.name().toLowerCase(Locale.ROOT));
    }

    public static Codec<Integer> argbOrInt() {
        return Codec.either((Codec)Codec.STRING, (Codec)Codec.INT).xmap(either -> (Integer)either.map(ColorUtil::parseArgb, Function.identity()), Either::right);
    }
}

