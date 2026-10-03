/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.network.FriendlyByteBuf
 */
package com.sighs.apricityui.network.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.network.codec.StreamCodec;
import com.sighs.apricityui.network.serialization.NetworkRecordCodecBuilder;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.FriendlyByteBuf;

public final class NetworkSerialization {
    private static final ConcurrentHashMap<Class<?>, StreamCodec<FriendlyByteBuf, ?>> RECORD_CODEC_CACHE = new ConcurrentHashMap();

    private NetworkSerialization() {
    }

    public static <T> StreamCodec<FriendlyByteBuf, T> jsonCodec(final Codec<T> codec) {
        return new StreamCodec<FriendlyByteBuf, T>(){

            @Override
            public T decode(FriendlyByteBuf buf) {
                String json = buf.m_130277_();
                JsonElement element = JsonParser.parseString((String)json);
                DataResult result = codec.parse((DynamicOps)JsonOps.INSTANCE, (Object)element);
                if (result.error().isPresent()) {
                    String message = ((DataResult.PartialResult)result.error().get()).message();
                    ApricityUI.LOGGER.error("Failed to decode json payload: {}", (Object)message);
                    throw new IllegalStateException("Failed to decode json payload: " + message);
                }
                return result.result().orElseThrow(() -> new IllegalStateException("Failed to decode json payload: empty result"));
            }

            @Override
            public void encode(FriendlyByteBuf buf, T value) {
                DataResult result = codec.encodeStart((DynamicOps)JsonOps.INSTANCE, value);
                if (result.error().isPresent()) {
                    String message = ((DataResult.PartialResult)result.error().get()).message();
                    ApricityUI.LOGGER.error("Failed to encode json payload: {}", (Object)message);
                    throw new IllegalStateException("Failed to encode json payload: " + message);
                }
                JsonElement element = (JsonElement)result.result().orElseThrow(() -> new IllegalStateException("Failed to encode json payload: empty result"));
                buf.m_130070_(element.toString());
            }
        };
    }

    public static <T> StreamCodec<FriendlyByteBuf, T> autoCodec(Class<T> recordClass) {
        if (!recordClass.isRecord()) {
            throw new IllegalArgumentException("autoCodec only supports record types: " + recordClass.getName());
        }
        StreamCodec<FriendlyByteBuf, ?> existing = RECORD_CODEC_CACHE.get(recordClass);
        if (existing != null) {
            return existing;
        }
        StreamCodec<FriendlyByteBuf, T> built = NetworkRecordCodecBuilder.build(recordClass);
        StreamCodec<FriendlyByteBuf, T> prev = RECORD_CODEC_CACHE.putIfAbsent(recordClass, built);
        return prev != null ? prev : built;
    }
}

