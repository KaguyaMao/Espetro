/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2LongMap
 *  it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.GlobalPos
 *  net.minecraft.core.Registry
 *  net.minecraft.core.SectionPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.phys.BlockHitResult
 *  org.apache.commons.lang3.tuple.Pair
 *  org.apache.commons.lang3.tuple.Triple
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package com.sighs.apricityui.network.serialization;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.sighs.apricityui.network.codec.StreamCodec;
import com.sighs.apricityui.network.serialization.NetworkSerialization;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.PublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Quaternionf;
import org.joml.Vector3f;

final class ComponentIO {
    static final int MAX_DEPTH = 64;

    private ComponentIO() {
    }

    static Object decodeWithPlan(FriendlyByteBuf buf, ComponentPlan plan, int depth, String name, Class<?> owner) {
        if (depth > 64) {
            throw new IllegalStateException("Decoding depth exceeded for record " + owner.getName() + "#" + name);
        }
        if (plan.readHandle != null) {
            try {
                return plan.readHandle.invoke(buf);
            }
            catch (Throwable t) {
                throw new IllegalStateException("Failed to decode component for record " + owner.getName() + "#" + name, t);
            }
        }
        switch (plan.kind) {
            case INT: {
                return buf.m_130242_();
            }
            case LONG: {
                return buf.m_130258_();
            }
            case BOOLEAN: {
                return buf.readBoolean();
            }
            case FLOAT: {
                return Float.valueOf(buf.readFloat());
            }
            case DOUBLE: {
                return buf.readDouble();
            }
            case BYTE: {
                return buf.readByte();
            }
            case SHORT: {
                return buf.readShort();
            }
            case STRING: {
                return buf.m_130277_();
            }
            case UUID: {
                return buf.m_130259_();
            }
            case BYTE_ARRAY: {
                return buf.m_130052_();
            }
            case INT_ARRAY: {
                return buf.m_130100_();
            }
            case LONG_ARRAY: {
                return buf.m_178381_();
            }
            case DATE: {
                return buf.m_130282_();
            }
            case INSTANT: {
                return buf.m_236873_();
            }
            case BITSET: {
                return buf.m_178384_();
            }
            case PUBLIC_KEY: {
                return buf.m_236874_();
            }
            case INT_LIST: {
                return buf.m_178338_();
            }
            case BLOCK_POS: {
                return buf.m_130135_();
            }
            case CHUNK_POS: {
                return buf.m_178383_();
            }
            case SECTION_POS: {
                return buf.m_130157_();
            }
            case GLOBAL_POS: {
                return buf.m_236872_();
            }
            case VECTOR3F: {
                return buf.m_269394_();
            }
            case QUATERNIONF: {
                return buf.m_269131_();
            }
            case RESOURCE_LOCATION: {
                return buf.m_130281_();
            }
            case BLOCK_HIT_RESULT: {
                return buf.m_130283_();
            }
            case COMPOUND_TAG: {
                return buf.m_130260_();
            }
            case ITEM: {
                return buf.m_130267_();
            }
            case COMPONENT: {
                return buf.m_130238_();
            }
            case GAME_PROFILE: {
                return buf.m_236875_();
            }
            case PROPERTY: {
                return buf.m_236876_();
            }
            case PROPERTY_MAP: {
                return buf.m_246981_();
            }
            case ENUM: {
                return buf.m_130066_(plan.enumClass);
            }
            case RECORD: {
                return plan.codec.decode(buf);
            }
            case PAIR: {
                Object left = ComponentIO.decodeWithPlan(buf, plan.key, depth + 1, name, owner);
                Object right = ComponentIO.decodeWithPlan(buf, plan.value, depth + 1, name, owner);
                if (plan.pairRawClass == com.mojang.datafixers.util.Pair.class) {
                    return com.mojang.datafixers.util.Pair.of((Object)left, (Object)right);
                }
                return Pair.of((Object)left, (Object)right);
            }
            case EITHER: {
                boolean isLeft = buf.readBoolean();
                if (isLeft) {
                    Object left = ComponentIO.decodeWithPlan(buf, plan.key, depth + 1, name, owner);
                    return Either.left((Object)left);
                }
                Object right = ComponentIO.decodeWithPlan(buf, plan.value, depth + 1, name, owner);
                return Either.right((Object)right);
            }
            case TRIPLE: {
                Object left = ComponentIO.decodeWithPlan(buf, plan.key, depth + 1, name, owner);
                Object middle = ComponentIO.decodeWithPlan(buf, plan.middle, depth + 1, name, owner);
                Object right = ComponentIO.decodeWithPlan(buf, plan.value, depth + 1, name, owner);
                return Triple.of((Object)left, (Object)middle, (Object)right);
            }
            case OPTIONAL: {
                boolean present = buf.readBoolean();
                if (!present) {
                    return Optional.empty();
                }
                Object v = ComponentIO.decodeWithPlan(buf, plan.element, depth + 1, name, owner);
                return Optional.of(v);
            }
            case LIST: {
                return ComponentIO.readListGeneric(buf, plan, depth, name, owner);
            }
            case SET: {
                return ComponentIO.readSetGeneric(buf, plan, depth, name, owner);
            }
            case MAP: {
                return ComponentIO.readMapGeneric(buf, plan, depth, name, owner);
            }
            case ENUM_SET: {
                return ComponentIO.readEnumSetGeneric(buf, plan.enumClass);
            }
        }
        throw new IllegalStateException("Unsupported plan for record " + owner.getName() + "#" + name);
    }

    static void encodeWithPlan(FriendlyByteBuf buf, ComponentPlan plan, Object value, int depth, String name, Class<?> owner) {
        if (depth > 64) {
            throw new IllegalStateException("Encoding depth exceeded for record " + owner.getName() + "#" + name);
        }
        if (plan.writeHandle != null) {
            try {
                plan.writeHandle.invoke(buf, value);
                return;
            }
            catch (Throwable t) {
                throw new IllegalStateException("Failed to encode component for record " + owner.getName() + "#" + name, t);
            }
        }
        switch (plan.kind) {
            case INT: {
                buf.m_130130_(((Integer)value).intValue());
                break;
            }
            case LONG: {
                buf.m_130103_(((Long)value).longValue());
                break;
            }
            case BOOLEAN: {
                buf.writeBoolean(((Boolean)value).booleanValue());
                break;
            }
            case FLOAT: {
                buf.writeFloat(((Float)value).floatValue());
                break;
            }
            case DOUBLE: {
                buf.writeDouble(((Double)value).doubleValue());
                break;
            }
            case BYTE: {
                buf.writeByte((int)((Byte)value).byteValue());
                break;
            }
            case SHORT: {
                buf.writeShort((int)((Short)value).shortValue());
                break;
            }
            case STRING: {
                buf.m_130070_((String)value);
                break;
            }
            case UUID: {
                buf.m_130077_((UUID)value);
                break;
            }
            case BYTE_ARRAY: {
                buf.m_130087_((byte[])value);
                break;
            }
            case INT_ARRAY: {
                buf.m_130089_((int[])value);
                break;
            }
            case LONG_ARRAY: {
                buf.m_130091_((long[])value);
                break;
            }
            case DATE: {
                buf.m_130075_((Date)value);
                break;
            }
            case INSTANT: {
                buf.m_236826_((Instant)value);
                break;
            }
            case BITSET: {
                buf.m_178350_((BitSet)value);
                break;
            }
            case PUBLIC_KEY: {
                buf.m_236824_((PublicKey)value);
                break;
            }
            case INT_LIST: {
                buf.m_178345_((IntList)value);
                break;
            }
            case BLOCK_POS: {
                buf.m_130064_((BlockPos)value);
                break;
            }
            case CHUNK_POS: {
                buf.m_178341_((ChunkPos)value);
                break;
            }
            case SECTION_POS: {
                buf.m_178343_((SectionPos)value);
                break;
            }
            case GLOBAL_POS: {
                buf.m_236814_((GlobalPos)value);
                break;
            }
            case VECTOR3F: {
                buf.m_269582_((Vector3f)value);
                break;
            }
            case QUATERNIONF: {
                buf.m_269101_((Quaternionf)value);
                break;
            }
            case RESOURCE_LOCATION: {
                buf.m_130085_((ResourceLocation)value);
                break;
            }
            case BLOCK_HIT_RESULT: {
                buf.m_130062_((BlockHitResult)value);
                break;
            }
            case COMPOUND_TAG: {
                buf.m_130079_((CompoundTag)value);
                break;
            }
            case ITEM: {
                buf.m_130055_((ItemStack)value);
                break;
            }
            case COMPONENT: {
                buf.m_130083_((Component)value);
                break;
            }
            case GAME_PROFILE: {
                buf.m_236803_((GameProfile)value);
                break;
            }
            case PROPERTY: {
                buf.m_236805_((Property)value);
                break;
            }
            case PROPERTY_MAP: {
                buf.m_246636_((PropertyMap)value);
                break;
            }
            case ENUM: {
                buf.m_130068_((Enum)value);
                break;
            }
            case RECORD: {
                plan.codec.encode(buf, value);
                break;
            }
            case PAIR: {
                Object right;
                Object left;
                if (value instanceof com.mojang.datafixers.util.Pair) {
                    com.mojang.datafixers.util.Pair p = (com.mojang.datafixers.util.Pair)value;
                    left = p.getFirst();
                    right = p.getSecond();
                } else if (value instanceof Pair) {
                    Pair p = (Pair)value;
                    left = p.getLeft();
                    right = p.getRight();
                } else {
                    throw new IllegalStateException("Pair value type not supported: " + value.getClass().getName());
                }
                ComponentIO.encodeWithPlan(buf, plan.key, left, depth + 1, name, owner);
                ComponentIO.encodeWithPlan(buf, plan.value, right, depth + 1, name, owner);
                break;
            }
            case EITHER: {
                Either e = (Either)value;
                if (e.left().isPresent()) {
                    buf.writeBoolean(true);
                    ComponentIO.encodeWithPlan(buf, plan.key, e.left().get(), depth + 1, name, owner);
                    break;
                }
                buf.writeBoolean(false);
                ComponentIO.encodeWithPlan(buf, plan.value, e.right().orElse(null), depth + 1, name, owner);
                break;
            }
            case TRIPLE: {
                Triple t = (Triple)value;
                ComponentIO.encodeWithPlan(buf, plan.key, t.getLeft(), depth + 1, name, owner);
                ComponentIO.encodeWithPlan(buf, plan.middle, t.getMiddle(), depth + 1, name, owner);
                ComponentIO.encodeWithPlan(buf, plan.value, t.getRight(), depth + 1, name, owner);
                break;
            }
            case OPTIONAL: {
                Optional opt = (Optional)value;
                boolean present = opt != null && opt.isPresent();
                buf.writeBoolean(present);
                if (!present) break;
                ComponentIO.encodeWithPlan(buf, plan.element, opt.get(), depth + 1, name, owner);
                break;
            }
            case LIST: 
            case SET: {
                ComponentIO.writeCollectionGeneric(buf, (Collection)value, plan.element, depth, name, owner);
                break;
            }
            case MAP: {
                ComponentIO.writeMapGeneric(buf, (Map)value, plan.key, plan.value, depth, name, owner);
                break;
            }
            case ENUM_SET: {
                ComponentIO.writeEnumSetGeneric(buf, (EnumSet)value, plan.enumClass);
                break;
            }
            default: {
                throw new IllegalStateException("Unsupported plan for record " + owner.getName() + "#" + name);
            }
        }
    }

    static ComponentPlan planOf(Class<?> rawType, Type genericType) {
        if (rawType.isRecord()) {
            ComponentPlan p = new ComponentPlan(Kind.RECORD);
            p.codec = NetworkSerialization.autoCodec(rawType);
            return p;
        }
        if (rawType == Integer.TYPE || rawType == Integer.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.INT), "readVarIntW", Integer.TYPE, "writeVarIntW", Integer.TYPE);
        }
        if (rawType == Long.TYPE || rawType == Long.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.LONG), "readVarLongW", Long.TYPE, "writeVarLongW", Long.TYPE);
        }
        if (rawType == Boolean.TYPE || rawType == Boolean.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.BOOLEAN), "readBooleanW", Boolean.TYPE, "writeBooleanW", Boolean.TYPE);
        }
        if (rawType == Float.TYPE || rawType == Float.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.FLOAT), "readFloatW", Float.TYPE, "writeFloatW", Float.TYPE);
        }
        if (rawType == Double.TYPE || rawType == Double.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.DOUBLE), "readDoubleW", Double.TYPE, "writeDoubleW", Double.TYPE);
        }
        if (rawType == Byte.TYPE || rawType == Byte.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.BYTE), "readByteW", Byte.TYPE, "writeByteW", Byte.TYPE);
        }
        if (rawType == Short.TYPE || rawType == Short.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.SHORT), "readShortW", Short.TYPE, "writeShortW", Short.TYPE);
        }
        if (rawType == String.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.STRING), "readUtfW", String.class, "writeUtfW", String.class);
        }
        if (rawType == UUID.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.UUID), "readUUIDW", UUID.class, "writeUUIDW", UUID.class);
        }
        if (rawType == byte[].class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.BYTE_ARRAY), "readByteArrayW", byte[].class, "writeByteArrayW", byte[].class);
        }
        if (rawType == int[].class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.INT_ARRAY), "readVarIntArrayW", int[].class, "writeVarIntArrayW", int[].class);
        }
        if (rawType == long[].class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.LONG_ARRAY), "readLongArrayW", long[].class, "writeLongArrayW", long[].class);
        }
        if (rawType == Date.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.DATE), "readDateW", Date.class, "writeDateW", Date.class);
        }
        if (rawType == Instant.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.INSTANT), "readInstantW", Instant.class, "writeInstantW", Instant.class);
        }
        if (rawType == BitSet.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.BITSET), "readBitSetW", BitSet.class, "writeBitSetW", BitSet.class);
        }
        if (rawType == PublicKey.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.PUBLIC_KEY), "readPublicKeyW", PublicKey.class, "writePublicKeyW", PublicKey.class);
        }
        if (rawType == IntList.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.INT_LIST), "readIntIdListW", IntList.class, "writeIntIdListW", IntList.class);
        }
        if (rawType == ResourceKey.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.RESOURCE_KEY), "readRegistryKeyW", ResourceKey.class, "writeResourceKeyW", ResourceKey.class);
        }
        if (rawType == BlockPos.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.BLOCK_POS), "readBlockPosW", BlockPos.class, "writeBlockPosW", BlockPos.class);
        }
        if (rawType == ChunkPos.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.CHUNK_POS), "readChunkPosW", ChunkPos.class, "writeChunkPosW", ChunkPos.class);
        }
        if (rawType == SectionPos.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.SECTION_POS), "readSectionPosW", SectionPos.class, "writeSectionPosW", SectionPos.class);
        }
        if (rawType == GlobalPos.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.GLOBAL_POS), "readGlobalPosW", GlobalPos.class, "writeGlobalPosW", GlobalPos.class);
        }
        if (rawType == Vector3f.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.VECTOR3F), "readVector3fW", Vector3f.class, "writeVector3fW", Vector3f.class);
        }
        if (rawType == Quaternionf.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.QUATERNIONF), "readQuaternionW", Quaternionf.class, "writeQuaternionW", Quaternionf.class);
        }
        if (rawType == ResourceLocation.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.RESOURCE_LOCATION), "readResourceLocationW", ResourceLocation.class, "writeResourceLocationW", ResourceLocation.class);
        }
        if (rawType == BlockHitResult.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.BLOCK_HIT_RESULT), "readBlockHitResultW", BlockHitResult.class, "writeBlockHitResultW", BlockHitResult.class);
        }
        if (rawType == CompoundTag.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.COMPOUND_TAG), "readCompoundTagW", CompoundTag.class, "writeCompoundTagW", CompoundTag.class);
        }
        if (rawType == ItemStack.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.ITEM), "readItemW", ItemStack.class, "writeItemW", ItemStack.class);
        }
        if (rawType == Component.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.COMPONENT), "readComponentW", Component.class, "writeComponentW", Component.class);
        }
        if (rawType == GameProfile.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.GAME_PROFILE), "readGameProfileW", GameProfile.class, "writeGameProfileW", GameProfile.class);
        }
        if (rawType == Property.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.GAME_PROFILE), "readPropertyW", Property.class, "writePropertyW", Property.class);
        }
        if (rawType == PropertyMap.class) {
            return ComponentIO.bindStatic(new ComponentPlan(Kind.PROPERTY_MAP), "readGameProfileProperties", PropertyMap.class, "writeGameProfileProperties", PropertyMap.class);
        }
        if (rawType.isEnum()) {
            ComponentPlan p = new ComponentPlan(Kind.ENUM);
            p.enumClass = rawType;
            try {
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                MethodHandle r = lookup.findStatic(ComponentIO.class, "readEnumW", MethodType.methodType(Enum.class, FriendlyByteBuf.class, Class.class));
                p.readHandle = MethodHandles.insertArguments(r, 1, p.enumClass);
                p.writeHandle = lookup.findStatic(ComponentIO.class, "writeEnumW", MethodType.methodType(Void.TYPE, FriendlyByteBuf.class, Enum.class));
            }
            catch (IllegalAccessException | NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
            return p;
        }
        if (rawType == IntSet.class) {
            ComponentPlan p = new ComponentPlan(Kind.SET);
            p.rawType = IntSet.class;
            p.element = ComponentIO.planOf(Integer.class, Integer.class);
            return p;
        }
        if (rawType == LongSet.class) {
            ComponentPlan p = new ComponentPlan(Kind.SET);
            p.rawType = LongSet.class;
            p.element = ComponentIO.planOf(Long.class, Long.class);
            return p;
        }
        if (rawType == Int2IntMap.class) {
            ComponentPlan p = new ComponentPlan(Kind.MAP);
            p.rawType = Int2IntMap.class;
            p.key = ComponentIO.planOf(Integer.class, Integer.class);
            p.value = ComponentIO.planOf(Integer.class, Integer.class);
            return p;
        }
        if (rawType == Object2IntMap.class) {
            return ComponentIO.planMap(rawType, genericType, String.class, Integer.class);
        }
        if (rawType == Object2LongMap.class) {
            return ComponentIO.planMap(rawType, genericType, String.class, Long.class);
        }
        if (rawType == Object2ObjectMap.class) {
            return ComponentIO.planMap(rawType, genericType, String.class, String.class);
        }
        if (rawType == Int2ObjectMap.class) {
            return ComponentIO.planMap(rawType, genericType, Integer.class, String.class);
        }
        if (rawType == Long2ObjectMap.class) {
            return ComponentIO.planMap(rawType, genericType, Long.class, String.class);
        }
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)genericType;
            Type raw = pt.getRawType();
            if (raw == Optional.class) {
                Type arg = pt.getActualTypeArguments()[0];
                Class<?> argRaw = ComponentIO.erasureOf(arg);
                ComponentPlan p = new ComponentPlan(Kind.OPTIONAL);
                p.element = ComponentIO.planOf(argRaw, arg);
                return p;
            }
            if (raw instanceof Class) {
                Type[] args;
                Class rawClass = (Class)raw;
                if (rawClass == com.mojang.datafixers.util.Pair.class || rawClass == Pair.class) {
                    Type[] args2 = pt.getActualTypeArguments();
                    Type leftType = args2[0];
                    Type rightType = args2[1];
                    ComponentPlan p = new ComponentPlan(Kind.PAIR);
                    p.key = ComponentIO.planOf(ComponentIO.erasureOf(leftType), leftType);
                    p.value = ComponentIO.planOf(ComponentIO.erasureOf(rightType), rightType);
                    p.pairRawClass = rawClass;
                    return p;
                }
                if (rawClass == Either.class) {
                    Type[] args3 = pt.getActualTypeArguments();
                    Type leftType = args3[0];
                    Type rightType = args3[1];
                    ComponentPlan p = new ComponentPlan(Kind.EITHER);
                    p.key = ComponentIO.planOf(ComponentIO.erasureOf(leftType), leftType);
                    p.value = ComponentIO.planOf(ComponentIO.erasureOf(rightType), rightType);
                    return p;
                }
                if (rawClass == Triple.class) {
                    Type[] args4 = pt.getActualTypeArguments();
                    Type leftType = args4[0];
                    Type midType = args4[1];
                    Type rightType = args4[2];
                    ComponentPlan p = new ComponentPlan(Kind.TRIPLE);
                    p.key = ComponentIO.planOf(ComponentIO.erasureOf(leftType), leftType);
                    p.middle = ComponentIO.planOf(ComponentIO.erasureOf(midType), midType);
                    p.value = ComponentIO.planOf(ComponentIO.erasureOf(rightType), rightType);
                    return p;
                }
                if (List.class.isAssignableFrom(rawClass)) {
                    Type elemType = pt.getActualTypeArguments()[0];
                    Class<?> elemRaw = ComponentIO.erasureOf(elemType);
                    ComponentPlan p = new ComponentPlan(Kind.LIST);
                    p.rawType = rawClass;
                    p.collectionFactory = rawClass == ObjectList.class ? ObjectArrayList::new : ArrayList::new;
                    p.element = ComponentIO.planOf(elemRaw, elemType);
                    return p;
                }
                if (rawClass == EnumSet.class) {
                    Type arg = pt.getActualTypeArguments()[0];
                    Class<?> argRaw = ComponentIO.erasureOf(arg);
                    if (!Enum.class.isAssignableFrom(argRaw)) {
                        throw new IllegalStateException("EnumSet element type must be an enum");
                    }
                    ComponentPlan p = new ComponentPlan(Kind.ENUM_SET);
                    p.enumClass = argRaw;
                    return p;
                }
                if (Set.class.isAssignableFrom(rawClass)) {
                    Type elemType = pt.getActualTypeArguments()[0];
                    Class<?> elemRaw = ComponentIO.erasureOf(elemType);
                    ComponentPlan p = new ComponentPlan(Kind.SET);
                    p.rawType = rawClass;
                    p.collectionFactory = rawClass == ObjectSet.class ? ObjectOpenHashSet::new : (rawClass == IntSet.class ? IntOpenHashSet::new : (rawClass == LongSet.class ? LongOpenHashSet::new : LinkedHashSet::new));
                    p.element = ComponentIO.planOf(elemRaw, elemType);
                    return p;
                }
                if (Map.class.isAssignableFrom(rawClass) && (args = pt.getActualTypeArguments()).length == 2) {
                    Type keyType = args[0];
                    Type valueType = args[1];
                    Class<?> keyRaw = ComponentIO.erasureOf(keyType);
                    Class<?> valueRaw = ComponentIO.erasureOf(valueType);
                    ComponentPlan p = new ComponentPlan(Kind.MAP);
                    p.rawType = rawClass;
                    p.key = ComponentIO.planOf(keyRaw, keyType);
                    p.value = ComponentIO.planOf(valueRaw, valueType);
                    return p;
                }
            }
        }
        throw new IllegalStateException("Unsupported type " + rawType.getName());
    }

    private static ComponentPlan planMap(Class<?> rawType, Type genericType, Class<?> defaultKey, Class<?> defaultValue) {
        Type[] typeArgs;
        ComponentPlan p = new ComponentPlan(Kind.MAP);
        p.rawType = rawType;
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)genericType;
            v0 = pt.getActualTypeArguments();
        } else {
            v0 = typeArgs = new Type[]{};
        }
        if (rawType == Int2ObjectMap.class || rawType == Int2IntMap.class) {
            p.key = ComponentIO.planOf(Integer.class, Integer.class);
        } else if (rawType == Long2ObjectMap.class) {
            p.key = ComponentIO.planOf(Long.class, Long.class);
        } else {
            Class<?> keyType = typeArgs.length >= 1 ? typeArgs[0] : defaultKey;
            p.key = ComponentIO.planOf(ComponentIO.erasureOf(keyType), keyType);
        }
        if (rawType == Object2IntMap.class) {
            p.value = ComponentIO.planOf(Integer.class, Integer.class);
        } else if (rawType == Object2LongMap.class) {
            p.value = ComponentIO.planOf(Long.class, Long.class);
        } else {
            int valIdx = rawType == Object2ObjectMap.class ? 1 : 0;
            Class<?> valType = typeArgs.length > valIdx ? typeArgs[valIdx] : defaultValue;
            p.value = ComponentIO.planOf(ComponentIO.erasureOf(valType), valType);
        }
        return p;
    }

    static Class<?> erasureOf(Type type) {
        ParameterizedType pt;
        Type type2;
        if (type instanceof Class) {
            Class c = (Class)type;
            return c;
        }
        if (type instanceof ParameterizedType && (type2 = (pt = (ParameterizedType)type).getRawType()) instanceof Class) {
            Class c = (Class)type2;
            return c;
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    static <E extends Enum<E>> EnumSet<E> readEnumSetGeneric(FriendlyByteBuf buf, Class<? extends Enum> enumClassRaw) {
        Class<? extends Enum> ec = enumClassRaw;
        return buf.m_247336_(ec);
    }

    static <E extends Enum<E>> void writeEnumSetGeneric(FriendlyByteBuf buf, EnumSet<?> setRaw, Class<? extends Enum> enumClassRaw) {
        EnumSet<?> set = setRaw;
        Class<? extends Enum> ec = enumClassRaw;
        buf.m_245616_(set, ec);
    }

    static <T> List<T> readListGeneric(FriendlyByteBuf buf, ComponentPlan containerPlan, int depth, String name, Class<?> owner) {
        List base = buf.m_236845_(b -> ComponentIO.decodeWithPlan(b, containerPlan.element, depth + 1, name, owner));
        return ComponentIO.convertListToRawType(base, containerPlan.rawType);
    }

    static <T> Set<T> readSetGeneric(FriendlyByteBuf buf, ComponentPlan containerPlan, int depth, String name, Class<?> owner) {
        Set base = (Set)buf.m_236838_(LinkedHashSet::new, b -> ComponentIO.decodeWithPlan(b, containerPlan.element, depth + 1, name, owner));
        return ComponentIO.convertSetToRawType(base, containerPlan.rawType);
    }

    static <K, V> Map<K, V> readMapGeneric(FriendlyByteBuf buf, ComponentPlan containerPlan, int depth, String name, Class<?> owner) {
        Map base = buf.m_236847_(b -> ComponentIO.decodeWithPlan(b, containerPlan.key, depth + 1, name, owner), b -> ComponentIO.decodeWithPlan(b, containerPlan.value, depth + 1, name, owner));
        return ComponentIO.convertMapToRawType(base, containerPlan.rawType);
    }

    static void writeCollectionGeneric(FriendlyByteBuf buf, Collection<?> collection, ComponentPlan elemPlan, int depth, String name, Class<?> owner) {
        buf.m_236828_(collection, (b, e) -> ComponentIO.encodeWithPlan(b, elemPlan, e, depth + 1, name, owner));
    }

    static void writeMapGeneric(FriendlyByteBuf buf, Map<?, ?> map, ComponentPlan keyPlan, ComponentPlan valuePlan, int depth, String name, Class<?> owner) {
        buf.m_236831_(map, (b, k) -> ComponentIO.encodeWithPlan(b, keyPlan, k, depth + 1, name, owner), (b, v) -> ComponentIO.encodeWithPlan(b, valuePlan, v, depth + 1, name, owner));
    }

    static Map<?, ?> convertMapToRawType(Map<?, ?> base, Class<?> rawType) {
        if (rawType == null) {
            return base;
        }
        if (rawType == Object2IntMap.class) {
            Object2IntOpenHashMap m = new Object2IntOpenHashMap();
            for (Map.Entry<?, ?> e : base.entrySet()) {
                m.put(e.getKey(), ((Number)e.getValue()).intValue());
            }
            return m;
        }
        if (rawType == Object2LongMap.class) {
            Object2LongOpenHashMap m = new Object2LongOpenHashMap();
            for (Map.Entry<?, ?> e : base.entrySet()) {
                m.put(e.getKey(), ((Number)e.getValue()).longValue());
            }
            return m;
        }
        if (rawType == Object2ObjectMap.class) {
            Object2ObjectOpenHashMap m = new Object2ObjectOpenHashMap();
            m.putAll(base);
            return m;
        }
        if (rawType == Int2ObjectMap.class) {
            Int2ObjectOpenHashMap m = new Int2ObjectOpenHashMap();
            for (Map.Entry<?, ?> e : base.entrySet()) {
                m.put(((Number)e.getKey()).intValue(), e.getValue());
            }
            return m;
        }
        if (rawType == Int2IntMap.class) {
            Int2IntOpenHashMap m = new Int2IntOpenHashMap();
            for (Map.Entry<?, ?> e : base.entrySet()) {
                m.put(((Number)e.getKey()).intValue(), ((Number)e.getValue()).intValue());
            }
            return m;
        }
        if (rawType == Long2ObjectMap.class) {
            Long2ObjectOpenHashMap m = new Long2ObjectOpenHashMap();
            for (Map.Entry<?, ?> e : base.entrySet()) {
                m.put(((Number)e.getKey()).longValue(), e.getValue());
            }
            return m;
        }
        return base;
    }

    static List<?> convertListToRawType(List<?> base, Class<?> rawType) {
        if (rawType == null) {
            return base;
        }
        if (rawType == ObjectList.class) {
            return new ObjectArrayList(base);
        }
        return base;
    }

    static Set<?> convertSetToRawType(Set<?> base, Class<?> rawType) {
        if (rawType == null) {
            return base;
        }
        if (rawType == ObjectSet.class) {
            return new ObjectOpenHashSet(base);
        }
        if (rawType == IntSet.class) {
            IntOpenHashSet set = new IntOpenHashSet();
            for (Object e : base) {
                set.add(((Number)e).intValue());
            }
            return set;
        }
        if (rawType == LongSet.class) {
            LongOpenHashSet set = new LongOpenHashSet();
            for (Object e : base) {
                set.add(((Number)e).longValue());
            }
            return set;
        }
        return base;
    }

    private static ComponentPlan bindStatic(ComponentPlan p, String readName, Class<?> readType, String writeName, Class<?> writeArgType) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            p.readHandle = lookup.findStatic(ComponentIO.class, readName, MethodType.methodType(readType, FriendlyByteBuf.class));
            p.writeHandle = lookup.findStatic(ComponentIO.class, writeName, MethodType.methodType(Void.TYPE, FriendlyByteBuf.class, writeArgType));
            return p;
        }
        catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    static int readVarIntW(FriendlyByteBuf buf) {
        return buf.m_130242_();
    }

    static long readVarLongW(FriendlyByteBuf buf) {
        return buf.m_130258_();
    }

    static boolean readBooleanW(FriendlyByteBuf buf) {
        return buf.readBoolean();
    }

    static float readFloatW(FriendlyByteBuf buf) {
        return buf.readFloat();
    }

    static double readDoubleW(FriendlyByteBuf buf) {
        return buf.readDouble();
    }

    static byte readByteW(FriendlyByteBuf buf) {
        return buf.readByte();
    }

    static short readShortW(FriendlyByteBuf buf) {
        return buf.readShort();
    }

    static String readUtfW(FriendlyByteBuf buf) {
        return buf.m_130277_();
    }

    static UUID readUUIDW(FriendlyByteBuf buf) {
        return buf.m_130259_();
    }

    static byte[] readByteArrayW(FriendlyByteBuf buf) {
        return buf.m_130052_();
    }

    static int[] readVarIntArrayW(FriendlyByteBuf buf) {
        return buf.m_130100_();
    }

    static long[] readLongArrayW(FriendlyByteBuf buf) {
        return buf.m_178381_();
    }

    static Date readDateW(FriendlyByteBuf buf) {
        return buf.m_130282_();
    }

    static Instant readInstantW(FriendlyByteBuf buf) {
        return buf.m_236873_();
    }

    static BitSet readBitSetW(FriendlyByteBuf buf) {
        return buf.m_178384_();
    }

    static PublicKey readPublicKeyW(FriendlyByteBuf buf) {
        return buf.m_236874_();
    }

    static BlockPos readBlockPosW(FriendlyByteBuf buf) {
        return buf.m_130135_();
    }

    static ChunkPos readChunkPosW(FriendlyByteBuf buf) {
        return buf.m_178383_();
    }

    static SectionPos readSectionPosW(FriendlyByteBuf buf) {
        return buf.m_130157_();
    }

    static GlobalPos readGlobalPosW(FriendlyByteBuf buf) {
        return buf.m_236872_();
    }

    static Vector3f readVector3fW(FriendlyByteBuf buf) {
        return buf.m_269394_();
    }

    static Quaternionf readQuaternionW(FriendlyByteBuf buf) {
        return buf.m_269131_();
    }

    static ResourceLocation readResourceLocationW(FriendlyByteBuf buf) {
        return buf.m_130281_();
    }

    static BlockHitResult readBlockHitResultW(FriendlyByteBuf buf) {
        return buf.m_130283_();
    }

    static CompoundTag readCompoundTagW(FriendlyByteBuf buf) {
        return buf.m_130260_();
    }

    static ItemStack readItemW(FriendlyByteBuf buf) {
        return buf.m_130267_();
    }

    static Component readComponentW(FriendlyByteBuf buf) {
        return buf.m_130238_();
    }

    static GameProfile readGameProfileW(FriendlyByteBuf buf) {
        return buf.m_236875_();
    }

    static Property readPropertyW(FriendlyByteBuf buf) {
        return buf.m_236876_();
    }

    static PropertyMap readGameProfileProperties(FriendlyByteBuf buf) {
        return buf.m_246981_();
    }

    static Enum<?> readEnumW(FriendlyByteBuf buf, Class<? extends Enum<?>> enumClass) {
        return buf.m_130066_(enumClass);
    }

    static IntList readIntIdListW(FriendlyByteBuf buf) {
        return buf.m_178338_();
    }

    static <T> ResourceKey<T> readResourceKeyW(FriendlyByteBuf buf, ResourceKey<? extends Registry<T>> resourceKeyClass) {
        return buf.m_236801_(resourceKeyClass);
    }

    static ResourceKey<? extends Registry<?>> readRegistryKeyW(FriendlyByteBuf buf) {
        return ResourceKey.m_135788_((ResourceLocation)buf.m_130281_());
    }

    static void writeVarIntW(FriendlyByteBuf buf, int v) {
        buf.m_130130_(v);
    }

    static void writeVarLongW(FriendlyByteBuf buf, long v) {
        buf.m_130103_(v);
    }

    static void writeBooleanW(FriendlyByteBuf buf, boolean v) {
        buf.writeBoolean(v);
    }

    static void writeFloatW(FriendlyByteBuf buf, float v) {
        buf.writeFloat(v);
    }

    static void writeDoubleW(FriendlyByteBuf buf, double v) {
        buf.writeDouble(v);
    }

    static void writeByteW(FriendlyByteBuf buf, byte v) {
        buf.writeByte((int)v);
    }

    static void writeShortW(FriendlyByteBuf buf, short v) {
        buf.writeShort((int)v);
    }

    static void writeUtfW(FriendlyByteBuf buf, String v) {
        buf.m_130070_(v);
    }

    static void writeUUIDW(FriendlyByteBuf buf, UUID v) {
        buf.m_130077_(v);
    }

    static void writeByteArrayW(FriendlyByteBuf buf, byte[] v) {
        buf.m_130087_(v);
    }

    static void writeVarIntArrayW(FriendlyByteBuf buf, int[] v) {
        buf.m_130089_(v);
    }

    static void writeLongArrayW(FriendlyByteBuf buf, long[] v) {
        buf.m_130091_(v);
    }

    static void writeDateW(FriendlyByteBuf buf, Date v) {
        buf.m_130075_(v);
    }

    static void writeInstantW(FriendlyByteBuf buf, Instant v) {
        buf.m_236826_(v);
    }

    static void writeBitSetW(FriendlyByteBuf buf, BitSet v) {
        buf.m_178350_(v);
    }

    static void writePublicKeyW(FriendlyByteBuf buf, PublicKey v) {
        buf.m_236824_(v);
    }

    static void writeBlockPosW(FriendlyByteBuf buf, BlockPos v) {
        buf.m_130064_(v);
    }

    static void writeChunkPosW(FriendlyByteBuf buf, ChunkPos v) {
        buf.m_178341_(v);
    }

    static void writeSectionPosW(FriendlyByteBuf buf, SectionPos v) {
        buf.m_178343_(v);
    }

    static void writeGlobalPosW(FriendlyByteBuf buf, GlobalPos v) {
        buf.m_236814_(v);
    }

    static void writeVector3fW(FriendlyByteBuf buf, Vector3f v) {
        buf.m_269582_(v);
    }

    static void writeQuaternionW(FriendlyByteBuf buf, Quaternionf v) {
        buf.m_269101_(v);
    }

    static void writeResourceLocationW(FriendlyByteBuf buf, ResourceLocation v) {
        buf.m_130085_(v);
    }

    static void writeBlockHitResultW(FriendlyByteBuf buf, BlockHitResult v) {
        buf.m_130062_(v);
    }

    static void writeCompoundTagW(FriendlyByteBuf buf, CompoundTag v) {
        buf.m_130079_(v);
    }

    static void writeItemW(FriendlyByteBuf buf, ItemStack v) {
        buf.m_130055_(v);
    }

    static void writeComponentW(FriendlyByteBuf buf, Component v) {
        buf.m_130083_(v);
    }

    static void writeGameProfileW(FriendlyByteBuf buf, GameProfile v) {
        buf.m_236803_(v);
    }

    static void writePropertyW(FriendlyByteBuf buf, Property v) {
        buf.m_236805_(v);
    }

    static void writeGameProfileProperties(FriendlyByteBuf buf, PropertyMap v) {
        buf.m_246636_(v);
    }

    static void writeEnumW(FriendlyByteBuf buf, Enum<?> v) {
        buf.m_130068_(v);
    }

    static void writeIntIdListW(FriendlyByteBuf buf, IntList v) {
        buf.m_178345_(v);
    }

    static void writeResourceKeyW(FriendlyByteBuf buf, ResourceKey<?> v) {
        buf.m_236858_(v);
    }

    static final class ComponentPlan {
        final Kind kind;
        ComponentPlan element;
        ComponentPlan key;
        ComponentPlan value;
        ComponentPlan middle;
        Class<? extends Enum> enumClass;
        MethodHandle readHandle;
        MethodHandle writeHandle;
        StreamCodec<FriendlyByteBuf, Object> codec;
        Class<?> pairRawClass;
        Class<?> rawType;
        Supplier<Collection<?>> collectionFactory;

        ComponentPlan(Kind k) {
            this.kind = k;
        }
    }

    static enum Kind {
        INT,
        LONG,
        BOOLEAN,
        FLOAT,
        DOUBLE,
        BYTE,
        SHORT,
        STRING,
        UUID,
        BYTE_ARRAY,
        INT_ARRAY,
        LONG_ARRAY,
        DATE,
        INSTANT,
        BITSET,
        PUBLIC_KEY,
        BLOCK_POS,
        CHUNK_POS,
        SECTION_POS,
        GLOBAL_POS,
        VECTOR3F,
        QUATERNIONF,
        RESOURCE_LOCATION,
        BLOCK_HIT_RESULT,
        COMPOUND_TAG,
        ITEM,
        COMPONENT,
        GAME_PROFILE,
        PROPERTY,
        PROPERTY_MAP,
        ENUM,
        RECORD,
        PAIR,
        EITHER,
        TRIPLE,
        OPTIONAL,
        LIST,
        SET,
        MAP,
        ENUM_SET,
        INT_LIST,
        RESOURCE_KEY;

    }
}

