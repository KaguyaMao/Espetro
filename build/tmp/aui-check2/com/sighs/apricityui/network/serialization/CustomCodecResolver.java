/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 */
package com.sighs.apricityui.network.serialization;

import com.mojang.serialization.Codec;
import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.network.NetworkPlatform;
import com.sighs.apricityui.network.codec.StreamCodec;
import com.sighs.apricityui.network.serialization.JsonCodec;
import com.sighs.apricityui.network.serialization.NetFieldCodec;
import com.sighs.apricityui.network.serialization.NetworkSerialization;
import com.sighs.apricityui.network.serialization.RegistryCodec;
import com.sighs.apricityui.network.util.ReflectionUtil;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

final class CustomCodecResolver {
    private static final ConcurrentHashMap<CodecCacheKey, StreamCodec<FriendlyByteBuf, Object>> CACHE = new ConcurrentHashMap();
    private static final ConcurrentHashMap<RegistryCacheKey, StreamCodec<FriendlyByteBuf, Object>> REGISTRY_CACHE = new ConcurrentHashMap();
    private static final ConcurrentHashMap<JsonCacheKey, StreamCodec<FriendlyByteBuf, Object>> JSON_CACHE = new ConcurrentHashMap();

    private CustomCodecResolver() {
    }

    static StreamCodec<FriendlyByteBuf, Object> resolve(Class<?> recordClass, RecordComponent component) {
        NetFieldCodec net = component.getAnnotation(NetFieldCodec.class);
        if (net != null) {
            CodecCacheKey key = new CodecCacheKey(net.holder(), component.getGenericType());
            return CACHE.computeIfAbsent(key, k -> CustomCodecResolver.resolveInternal(recordClass, component, net));
        }
        RegistryCodec reg = component.getAnnotation(RegistryCodec.class);
        if (reg != null) {
            RegistryCacheKey key = new RegistryCacheKey(reg.value(), component.getGenericType());
            return REGISTRY_CACHE.computeIfAbsent(key, k -> CustomCodecResolver.resolveRegistryCodec(component, reg));
        }
        JsonCodec json = component.getAnnotation(JsonCodec.class);
        if (json != null) {
            JsonCacheKey key = new JsonCacheKey(json.holder(), json.field(), component.getGenericType());
            return JSON_CACHE.computeIfAbsent(key, k -> CustomCodecResolver.resolveJsonCodec(component, json));
        }
        return null;
    }

    private static StreamCodec<FriendlyByteBuf, Object> resolveInternal(Class<?> recordClass, RecordComponent component, NetFieldCodec meta) {
        StreamCodec<FriendlyByteBuf, Object> direct;
        Class<?> holder = meta.holder();
        String fieldName = meta.field();
        if (fieldName != null && !fieldName.isEmpty() && (direct = CustomCodecResolver.tryResolveByName(recordClass, holder, fieldName)) != null) {
            return direct;
        }
        StreamCodec<FriendlyByteBuf, Object> scanned = CustomCodecResolver.resolveBySemanticScan(recordClass, holder, component);
        if (scanned != null) {
            return scanned;
        }
        throw new IllegalStateException("Failed to resolve codec for component " + component.getName() + " of record " + recordClass.getName() + " using holder " + holder.getName());
    }

    private static StreamCodec<FriendlyByteBuf, Object> tryResolveByName(Class<?> recordClass, Class<?> holder, String fieldName) {
        try {
            Field field = holder.getDeclaredField(fieldName);
            if (!Modifier.isStatic(field.getModifiers()) || !StreamCodec.class.isAssignableFrom(field.getType())) {
                return null;
            }
            StreamCodec<FriendlyByteBuf, Object> codec = ReflectionUtil.getStaticFieldAsCodec(recordClass, holder, field);
            ApricityUI.LOGGER.debug("NetFieldCodec: Resolved codec for {} via {}.{}", new Object[]{recordClass.getSimpleName(), holder.getSimpleName(), fieldName});
            return codec;
        }
        catch (NoSuchFieldException e) {
            ApricityUI.LOGGER.warn("NetFieldCodec: Field {}.{} not found", (Object)holder.getSimpleName(), (Object)fieldName);
            return null;
        }
    }

    private static StreamCodec<FriendlyByteBuf, Object> resolveBySemanticScan(Class<?> recordClass, Class<?> holder, RecordComponent component) {
        Field[] fields = holder.getDeclaredFields();
        if (fields.length == 0) {
            return null;
        }
        Type componentType = component.getGenericType();
        boolean isList = CustomCodecResolver.isListType(componentType);
        boolean isOptional = CustomCodecResolver.isOptionalType(componentType);
        StreamCodec<FriendlyByteBuf, Object> bestCodec = null;
        int bestScore = Integer.MIN_VALUE;
        for (Field field : fields) {
            int score;
            int mods = field.getModifiers();
            if (!Modifier.isPublic(mods) || !Modifier.isStatic(mods) || !Modifier.isFinal(mods) || !StreamCodec.class.isAssignableFrom(field.getType()) || (score = CustomCodecResolver.scoreCodecField(field, componentType, isList, isOptional)) <= bestScore) continue;
            bestScore = score;
            bestCodec = ReflectionUtil.getStaticFieldAsCodec(recordClass, holder, field);
        }
        if (bestCodec != null) {
            ApricityUI.LOGGER.debug("NetFieldCodec: Resolved codec for {} via semantic scan in {}", (Object)recordClass.getSimpleName(), (Object)holder.getSimpleName());
        }
        return bestCodec;
    }

    private static int scoreCodecField(Field field, Type componentType, boolean componentIsList, boolean componentIsOptional) {
        ParameterizedType pt;
        int score = 0;
        Type genericType = field.getGenericType();
        Type bufferType = null;
        Type valueType = null;
        if (genericType instanceof ParameterizedType && (pt = (ParameterizedType)genericType).getActualTypeArguments().length == 2) {
            bufferType = pt.getActualTypeArguments()[0];
            valueType = pt.getActualTypeArguments()[1];
        }
        if (CustomCodecResolver.isSameType(valueType, componentType)) {
            score += 100;
        }
        String name = field.getName().toUpperCase(Locale.ROOT);
        if (componentIsList && name.contains("LIST")) {
            score += 40;
        }
        if (componentIsOptional && name.contains("OPTIONAL")) {
            score += 40;
        }
        if (bufferType == FriendlyByteBuf.class) {
            score += 20;
        }
        if (bufferType == ByteBuf.class) {
            score -= 50;
        }
        return score;
    }

    private static boolean isListType(Type type) {
        if (type instanceof ParameterizedType) {
            Class c;
            ParameterizedType pt = (ParameterizedType)type;
            Type raw = pt.getRawType();
            if (raw instanceof Class && List.class.isAssignableFrom(c = (Class)raw)) {
                return true;
            }
            if (CustomCodecResolver.isOptionalType(type)) {
                Type[] args = pt.getActualTypeArguments();
                return args.length == 1 && CustomCodecResolver.isListType(args[0]);
            }
        }
        return false;
    }

    private static boolean isOptionalType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            Type raw = pt.getRawType();
            return raw == Optional.class;
        }
        return false;
    }

    private static boolean isSameType(Type a, Type b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.equals(b)) {
            return true;
        }
        if (a instanceof Class) {
            Class ca = (Class)a;
            if (b instanceof Class) {
                Class cb = (Class)b;
                return ca.isAssignableFrom(cb) || cb.isAssignableFrom(ca);
            }
        }
        if (a instanceof ParameterizedType) {
            ParameterizedType pa = (ParameterizedType)a;
            if (b instanceof ParameterizedType) {
                ParameterizedType pb = (ParameterizedType)b;
                return pa.getRawType().equals(pb.getRawType());
            }
        }
        return false;
    }

    private static StreamCodec<FriendlyByteBuf, Object> resolveRegistryCodec(RecordComponent component, RegistryCodec meta) {
        final ResourceLocation regId = new ResourceLocation(meta.value());
        final ResourceKey key = ResourceKey.m_135788_((ResourceLocation)regId);
        return new StreamCodec<FriendlyByteBuf, Object>(){

            @Override
            public Object decode(FriendlyByteBuf buf) {
                Registry<?> registry = this.getRegistry(regId, key);
                int id = buf.m_130242_();
                return registry.m_203300_(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID " + id + " for registry " + regId));
            }

            @Override
            public void encode(FriendlyByteBuf buf, Object value) {
                Registry<?> registry = this.getRegistry(regId, key);
                if (value instanceof Holder) {
                    Holder holder = (Holder)value;
                    this.writeId(buf, registry, holder.m_203334_());
                } else {
                    this.writeId(buf, registry, value);
                }
            }

            private Registry<?> getRegistry(ResourceLocation regId2, ResourceKey<? extends Registry<?>> key2) {
                MinecraftServer server;
                Registry registry = (Registry)BuiltInRegistries.f_257047_.m_7745_(regId2);
                if (registry == null && (server = NetworkPlatform.currentServer()) != null) {
                    registry = server.m_206579_().m_175515_(key2);
                }
                if (registry == null) {
                    throw new IllegalStateException("Could not find registry: " + regId2);
                }
                return registry;
            }

            private void writeId(FriendlyByteBuf buf, Registry<?> registry, Object value) {
                Registry<?> rawRegistry = registry;
                int id = rawRegistry.m_7447_(value);
                if (id == -1) {
                    throw new IllegalArgumentException("Value " + value + " is not registered in " + regId);
                }
                buf.m_130130_(id);
            }
        };
    }

    private static StreamCodec<FriendlyByteBuf, Object> resolveJsonCodec(RecordComponent component, JsonCodec meta) {
        StreamCodec<FriendlyByteBuf, Object> direct;
        Class<?> searchIn = meta.holder() == Void.class ? component.getType() : meta.holder();
        String fieldName = meta.field();
        if (fieldName != null && !fieldName.isEmpty() && (direct = CustomCodecResolver.tryResolveJsonByName(component.getDeclaringRecord(), searchIn, fieldName)) != null) {
            return direct;
        }
        StreamCodec<FriendlyByteBuf, Object> scanned = CustomCodecResolver.resolveJsonBySemanticScan(component.getDeclaringRecord(), searchIn, component);
        if (scanned != null) {
            return scanned;
        }
        throw new IllegalStateException("Failed to resolve JSON Codec for component " + component.getName() + " using holder/type " + searchIn.getName());
    }

    private static StreamCodec<FriendlyByteBuf, Object> tryResolveJsonByName(Class<?> recordClass, Class<?> holder, String fieldName) {
        try {
            Field f = holder.getDeclaredField(fieldName);
            int mods = f.getModifiers();
            if (!Modifier.isStatic(mods) || !Codec.class.isAssignableFrom(f.getType())) {
                return null;
            }
            Codec codec = ReflectionUtil.getStaticField(recordClass, holder, f, Codec.class);
            ApricityUI.LOGGER.debug("JsonCodec: Resolved JSON codec for {} via {}.{}", new Object[]{recordClass.getSimpleName(), holder.getSimpleName(), fieldName});
            return NetworkSerialization.jsonCodec(codec);
        }
        catch (NoSuchFieldException e) {
            ApricityUI.LOGGER.warn("JsonCodec: Field {}.{} not found", (Object)holder.getSimpleName(), (Object)fieldName);
            return null;
        }
    }

    private static StreamCodec<FriendlyByteBuf, Object> resolveJsonBySemanticScan(Class<?> recordClass, Class<?> holder, RecordComponent component) {
        Field[] fields = holder.getDeclaredFields();
        if (fields.length == 0) {
            return null;
        }
        Type componentType = component.getGenericType();
        StreamCodec best = null;
        int bestScore = Integer.MIN_VALUE;
        for (Field f : fields) {
            String name;
            ParameterizedType pt;
            int mods = f.getModifiers();
            if (!Modifier.isPublic(mods) || !Modifier.isStatic(mods) || !Modifier.isFinal(mods) || !Codec.class.isAssignableFrom(f.getType())) continue;
            int score = 0;
            Type valueType = null;
            Type type = f.getGenericType();
            if (type instanceof ParameterizedType && (pt = (ParameterizedType)type).getActualTypeArguments().length == 1) {
                valueType = pt.getActualTypeArguments()[0];
            }
            if (CustomCodecResolver.isSameType(valueType, componentType)) {
                score += 100;
            }
            if ((name = f.getName().toUpperCase(Locale.ROOT)).contains("CODEC")) {
                score += 20;
            }
            if (score <= bestScore) continue;
            bestScore = score;
            Codec codec = ReflectionUtil.getStaticField(recordClass, holder, f, Codec.class);
            best = NetworkSerialization.jsonCodec(codec);
        }
        if (best != null) {
            ApricityUI.LOGGER.debug("JsonCodec: Resolved JSON codec for {} via semantic scan in {}", (Object)recordClass.getSimpleName(), (Object)holder.getSimpleName());
        }
        return best;
    }

    private record CodecCacheKey(Class<?> holder, Type componentType) {
    }

    private record RegistryCacheKey(String registryId, Type componentType) {
    }

    private record JsonCacheKey(Class<?> holder, String field, Type componentType) {
    }
}

