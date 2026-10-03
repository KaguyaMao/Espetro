/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.datafixers.util.Unit
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.codecs.BaseMapCodec
 */
package net.minecraftforge.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.BaseMapCodec;
import java.util.Map;
import java.util.Objects;

public class LenientUnboundedMapCodec<K, V>
implements BaseMapCodec<K, V>,
Codec<Map<K, V>> {
    private final Codec<K> keyCodec;
    private final Codec<V> elementCodec;

    public LenientUnboundedMapCodec(Codec<K> keyCodec, Codec<V> elementCodec) {
        this.keyCodec = keyCodec;
        this.elementCodec = elementCodec;
    }

    public Codec<K> keyCodec() {
        return this.keyCodec;
    }

    public Codec<V> elementCodec() {
        return this.elementCodec;
    }

    public <T> DataResult<Map<K, V>> decode(DynamicOps<T> ops, MapLike<T> input) {
        ImmutableMap.Builder read = ImmutableMap.builder();
        ImmutableList.Builder failed = ImmutableList.builder();
        DataResult result = input.entries().reduce(DataResult.success((Object)Unit.INSTANCE, (Lifecycle)Lifecycle.stable()), (r, pair) -> {
            DataResult k = this.keyCodec().parse(ops, pair.getFirst());
            DataResult v = this.elementCodec().parse(ops, pair.getSecond());
            DataResult entry = k.apply2stable(Pair::of, v);
            entry.error().ifPresent(e -> failed.add(pair));
            entry.result().ifPresent(e -> read.put(e.getFirst(), e.getSecond()));
            return r.apply2stable((u, p) -> u, entry);
        }, (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2));
        ImmutableMap elements = read.build();
        Object errors = ops.createMap(failed.build().stream());
        return result.map(arg_0 -> LenientUnboundedMapCodec.lambda$decode$6((Map)elements, arg_0)).setPartial((Object)elements).mapError(e -> e + " missed input: " + String.valueOf(errors));
    }

    public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> this.decode(ops, (Object)map)).map(r -> Pair.of((Object)r, (Object)input));
    }

    public <T> DataResult<T> encode(Map<K, V> input, DynamicOps<T> ops, T prefix) {
        return this.encode(input, ops, ops.mapBuilder()).build(prefix);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LenientUnboundedMapCodec that = (LenientUnboundedMapCodec)o;
        return Objects.equals(this.keyCodec, that.keyCodec) && Objects.equals(this.elementCodec, that.elementCodec);
    }

    public int hashCode() {
        return Objects.hash(this.keyCodec, this.elementCodec);
    }

    public String toString() {
        return "LenientUnboundedMapCodec[" + String.valueOf(this.keyCodec) + " -> " + String.valueOf(this.elementCodec) + "]";
    }

    private static /* synthetic */ Map lambda$decode$6(Map elements, Unit unit) {
        return elements;
    }
}

