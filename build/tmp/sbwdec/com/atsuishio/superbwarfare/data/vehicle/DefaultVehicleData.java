/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  kotlin.Deprecated
 *  kotlin.DeprecationLevel
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlinx.serialization.DeserializationStrategy
 *  kotlinx.serialization.KSerializer
 *  kotlinx.serialization.SerialName
 *  kotlinx.serialization.Serializable
 *  kotlinx.serialization.SerializationStrategy
 *  kotlinx.serialization.Transient
 *  kotlinx.serialization.UnknownFieldException
 *  kotlinx.serialization.builtins.BuiltinSerializersKt
 *  kotlinx.serialization.descriptors.SerialDescriptor
 *  kotlinx.serialization.encoding.CompositeDecoder
 *  kotlinx.serialization.encoding.CompositeEncoder
 *  kotlinx.serialization.encoding.Decoder
 *  kotlinx.serialization.encoding.Encoder
 *  kotlinx.serialization.internal.ArrayListSerializer
 *  kotlinx.serialization.internal.BooleanSerializer
 *  kotlinx.serialization.internal.DoubleSerializer
 *  kotlinx.serialization.internal.FloatSerializer
 *  kotlinx.serialization.internal.GeneratedSerializer
 *  kotlinx.serialization.internal.GeneratedSerializer$DefaultImpls
 *  kotlinx.serialization.internal.IntSerializer
 *  kotlinx.serialization.internal.LinkedHashMapSerializer
 *  kotlinx.serialization.internal.PluginExceptionsKt
 *  kotlinx.serialization.internal.PluginGeneratedSerialDescriptor
 *  kotlinx.serialization.internal.SerializationConstructorMarker
 *  kotlinx.serialization.internal.StringSerializer
 *  kotlinx.serialization.json.Json
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.phys.Vec2
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.ForgeConfigSpec$ConfigValue
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atsuishio.superbwarfare.data.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.annotation.ServerOnly;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.data.DataLoader;
import com.atsuishio.superbwarfare.data.IDBasedData;
import com.atsuishio.superbwarfare.data.ModColor;
import com.atsuishio.superbwarfare.data.ModColorSerializer;
import com.atsuishio.superbwarfare.data.ObjectToList;
import com.atsuishio.superbwarfare.data.StringToObject;
import com.atsuishio.superbwarfare.data.gun.DefaultGunData;
import com.atsuishio.superbwarfare.data.vehicle.subdata.CollisionLevel;
import com.atsuishio.superbwarfare.data.vehicle.subdata.DestroyInfo;
import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType;
import com.atsuishio.superbwarfare.data.vehicle.subdata.OBBInfo;
import com.atsuishio.superbwarfare.data.vehicle.subdata.PartHealth;
import com.atsuishio.superbwarfare.data.vehicle.subdata.RadarInfo;
import com.atsuishio.superbwarfare.data.vehicle.subdata.SeatInfo;
import com.atsuishio.superbwarfare.data.vehicle.subdata.SeekInfo;
import com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleContainerType;
import com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModify;
import com.atsuishio.superbwarfare.serialization.kserializer.GsonObjectSerializer;
import com.atsuishio.superbwarfare.serialization.kserializer.ResourceLocationSerializer;
import com.atsuishio.superbwarfare.serialization.kserializer.SoundEventSerializer;
import com.atsuishio.superbwarfare.serialization.kserializer.Vec2Serializer;
import com.atsuishio.superbwarfare.serialization.kserializer.Vec3Serializer;
import com.atsuishio.superbwarfare.tools.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.SerialName;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.Transient;
import kotlinx.serialization.UnknownFieldException;
import kotlinx.serialization.builtins.BuiltinSerializersKt;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeDecoder;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.encoding.Decoder;
import kotlinx.serialization.encoding.Encoder;
import kotlinx.serialization.internal.ArrayListSerializer;
import kotlinx.serialization.internal.BooleanSerializer;
import kotlinx.serialization.internal.DoubleSerializer;
import kotlinx.serialization.internal.FloatSerializer;
import kotlinx.serialization.internal.GeneratedSerializer;
import kotlinx.serialization.internal.IntSerializer;
import kotlinx.serialization.internal.LinkedHashMapSerializer;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import kotlinx.serialization.internal.StringSerializer;
import kotlinx.serialization.json.Json;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Serializable
@Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000\u0090\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001e\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\bB\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u0000 \u00df\u00022\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0004\u00df\u0002\u00e0\u0002B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003B\u009b\u0005\b\u0010\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u0005\u0012\u0006\u0010\u000b\u001a\u00020\t\u0012\u0006\u0010\f\u001a\u00020\t\u0012\u0006\u0010\r\u001a\u00020\t\u0012\u0006\u0010\u000e\u001a\u00020\u0005\u0012\u000e\u0010\u000f\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u0010\u0012\u000e\u0010\u0012\u001a\n\u0012\u0004\u0012\u00020\u0014\u0018\u00010\u0013\u0012\u000e\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0016\u0018\u00010\u0013\u0012\u0006\u0010\u0017\u001a\u00020\t\u0012\u0006\u0010\u0018\u001a\u00020\u0019\u0012\u0006\u0010\u001a\u001a\u00020\u001b\u0012\u0006\u0010\u001c\u001a\u00020\u0019\u0012\u0006\u0010\u001d\u001a\u00020\t\u0012\u0006\u0010\u001e\u001a\u00020\u001b\u0012\u0006\u0010\u001f\u001a\u00020\u001b\u0012\u0006\u0010 \u001a\u00020\u001b\u0012\u0006\u0010!\u001a\u00020\u001b\u0012\u0006\u0010\"\u001a\u00020\u001b\u0012\u0014\u0010#\u001a\u0010\u0012\n\u0012\b\u0012\u0004\u0012\u00020%0$\u0018\u00010\u0013\u0012\u0006\u0010&\u001a\u00020\t\u0012\u0006\u0010'\u001a\u00020\t\u0012\u0006\u0010(\u001a\u00020\u0005\u0012\u0006\u0010)\u001a\u00020\u0005\u0012\b\u0010*\u001a\u0004\u0018\u00010+\u0012\b\u0010,\u001a\u0004\u0018\u00010-\u0012\b\u0010.\u001a\u0004\u0018\u00010/\u0012\u0006\u00100\u001a\u00020\u001b\u0012\b\u00101\u001a\u0004\u0018\u000102\u0012\b\u00103\u001a\u0004\u0018\u000102\u0012\b\u00104\u001a\u0004\u0018\u000105\u0012\b\u00106\u001a\u0004\u0018\u000105\u0012\u0006\u00107\u001a\u00020\t\u0012\b\u00108\u001a\u0004\u0018\u000109\u0012\b\u0010:\u001a\u0004\u0018\u00010;\u0012\b\u0010<\u001a\u0004\u0018\u00010=\u0012\b\u0010>\u001a\u0004\u0018\u00010?\u0012\b\u0010@\u001a\u0004\u0018\u00010?\u0012\b\u0010A\u001a\u0004\u0018\u00010B\u0012\u0006\u0010C\u001a\u00020\u001b\u0012\u0006\u0010D\u001a\u00020\u001b\u0012\u0006\u0010E\u001a\u00020\t\u0012\u0014\u0010F\u001a\u0010\u0012\u0004\u0012\u00020H\u0012\u0004\u0012\u00020=\u0018\u00010G\u0012\b\u0010I\u001a\u0004\u0018\u00010J\u0012\b\u0010K\u001a\u0004\u0018\u00010B\u0012\b\u0010L\u001a\u0004\u0018\u00010M\u0012\b\u0010N\u001a\u0004\u0018\u00010M\u0012\b\u0010O\u001a\u0004\u0018\u00010M\u0012\u0006\u0010P\u001a\u00020\u0005\u0012\u0006\u0010Q\u001a\u00020\t\u0012\b\u0010R\u001a\u0004\u0018\u00010H\u0012\b\u0010S\u001a\u0004\u0018\u00010B\u0012\b\u0010T\u001a\u0004\u0018\u00010B\u0012\b\u0010U\u001a\u0004\u0018\u00010B\u0012\b\u0010V\u001a\u0004\u0018\u00010M\u0012\b\u0010W\u001a\u0004\u0018\u00010M\u0012\b\u0010X\u001a\u0004\u0018\u00010M\u0012\u0006\u0010Y\u001a\u00020\u0005\u0012\u0006\u0010Z\u001a\u00020\u001b\u0012\u0006\u0010[\u001a\u00020\u0019\u0012\u000e\u0010\\\u001a\n\u0012\u0004\u0012\u00020B\u0018\u00010\u0010\u0012\u0006\u0010]\u001a\u00020\t\u0012\u0006\u0010^\u001a\u00020\t\u0012\b\u0010_\u001a\u0004\u0018\u00010`\u0012\b\u0010a\u001a\u0004\u0018\u00010b\u00a2\u0006\u0004\b\u0002\u0010cJ\b\u0010h\u001a\u00020HH\u0016J\u0010\u0010i\u001a\u00020j2\u0006\u0010d\u001a\u00020HH\u0016J\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u0010J\u0013\u0010F\u001a\u000f\u0012\u0004\u0012\u00020H\u0012\u0005\u0012\u00030\u008e\u00020GJ\t\u0010\u00d7\u0002\u001a\u00020jH\u0016J,\u0010\u00d8\u0002\u001a\u00020j2\u0007\u0010\u00d9\u0002\u001a\u00020\u00002\b\u0010\u00da\u0002\u001a\u00030\u00db\u00022\b\u0010\u00dc\u0002\u001a\u00030\u00dd\u0002H\u0001\u00a2\u0006\u0003\b\u00de\u0002R\u0018\u0010d\u001a\u00020H8\u0002@\u0002X\u0083\u000e\u00a2\u0006\b\n\u0000\u0012\u0004\be\u0010\u0003R\u0018\u0010f\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\b\n\u0000\u0012\u0004\bg\u0010\u0003R$\u0010\b\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0014\n\u0000\u0012\u0004\bk\u0010\u0003\u001a\u0004\bl\u0010m\"\u0004\bn\u0010oR$\u0010\n\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0014\n\u0000\u0012\u0004\bp\u0010\u0003\u001a\u0004\bq\u0010r\"\u0004\bs\u0010tR$\u0010\u000b\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0014\n\u0000\u0012\u0004\bu\u0010\u0003\u001a\u0004\bv\u0010m\"\u0004\bw\u0010oR$\u0010\f\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0014\n\u0000\u0012\u0004\bx\u0010\u0003\u001a\u0004\by\u0010m\"\u0004\bz\u0010oR$\u0010\r\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0014\n\u0000\u0012\u0004\b{\u0010\u0003\u001a\u0004\b|\u0010m\"\u0004\b}\u0010oR%\u0010\u000e\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0015\n\u0000\u0012\u0004\b~\u0010\u0003\u001a\u0004\b\u007f\u0010r\"\u0005\b\u0080\u0001\u0010tR/\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u00108\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u0081\u0001\u0010\u0003\u001a\u0006\b\u0082\u0001\u0010\u0083\u0001\"\u0006\b\u0084\u0001\u0010\u0085\u0001R!\u0010\u0012\u001a\n\u0012\u0004\u0012\u00020\u0014\u0018\u00010\u00138\u0002@\u0002X\u0083\u000e\u00a2\u0006\t\n\u0000\u0012\u0005\b\u0086\u0001\u0010\u0003R1\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0016\u0018\u00010\u00138\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u0087\u0001\u0010\u0003\u001a\u0006\b\u0088\u0001\u0010\u0089\u0001\"\u0006\b\u008a\u0001\u0010\u008b\u0001R'\u0010\u0017\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0017\n\u0000\u0012\u0005\b\u008c\u0001\u0010\u0003\u001a\u0005\b\u008d\u0001\u0010m\"\u0005\b\u008e\u0001\u0010oR\u0019\u0010\u0018\u001a\u00020\u00198\u0006@\u0006X\u0087\u000e\u00a2\u0006\t\n\u0000\u0012\u0005\b\u008f\u0001\u0010\u0003R)\u0010\u001a\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u0090\u0001\u0010\u0003\u001a\u0006\b\u0091\u0001\u0010\u0092\u0001\"\u0006\b\u0093\u0001\u0010\u0094\u0001R)\u0010\u001c\u001a\u00020\u00198\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u0095\u0001\u0010\u0003\u001a\u0006\b\u0096\u0001\u0010\u0097\u0001\"\u0006\b\u0098\u0001\u0010\u0099\u0001R'\u0010\u001d\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0017\n\u0000\u0012\u0005\b\u009a\u0001\u0010\u0003\u001a\u0005\b\u009b\u0001\u0010m\"\u0005\b\u009c\u0001\u0010oR)\u0010\u001e\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u009d\u0001\u0010\u0003\u001a\u0006\b\u009e\u0001\u0010\u0092\u0001\"\u0006\b\u009f\u0001\u0010\u0094\u0001R)\u0010\u001f\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00a0\u0001\u0010\u0003\u001a\u0006\b\u00a1\u0001\u0010\u0092\u0001\"\u0006\b\u00a2\u0001\u0010\u0094\u0001R)\u0010 \u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00a3\u0001\u0010\u0003\u001a\u0006\b\u00a4\u0001\u0010\u0092\u0001\"\u0006\b\u00a5\u0001\u0010\u0094\u0001R\u0019\u0010!\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\t\n\u0000\u0012\u0005\b\u00a6\u0001\u0010\u0003R)\u0010\"\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00a7\u0001\u0010\u0003\u001a\u0006\b\u00a8\u0001\u0010\u0092\u0001\"\u0006\b\u00a9\u0001\u0010\u0094\u0001R%\u0010#\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020%0$0\u00138\u0006@\u0006X\u0087\u000e\u00a2\u0006\t\n\u0000\u0012\u0005\b\u00aa\u0001\u0010\u0003R'\u0010&\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0017\n\u0000\u0012\u0005\b\u00ab\u0001\u0010\u0003\u001a\u0005\b\u00ac\u0001\u0010m\"\u0005\b\u00ad\u0001\u0010oR'\u0010'\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0017\n\u0000\u0012\u0005\b\u00ae\u0001\u0010\u0003\u001a\u0005\b\u00af\u0001\u0010m\"\u0005\b\u00b0\u0001\u0010oR'\u0010(\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0017\n\u0000\u0012\u0005\b\u00b1\u0001\u0010\u0003\u001a\u0005\b\u00b2\u0001\u0010r\"\u0005\b\u00b3\u0001\u0010tR'\u0010)\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0017\n\u0000\u0012\u0005\b\u00b4\u0001\u0010\u0003\u001a\u0005\b\u00b5\u0001\u0010r\"\u0005\b\u00b6\u0001\u0010tR)\u0010*\u001a\u00020+8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00b7\u0001\u0010\u0003\u001a\u0006\b\u00b8\u0001\u0010\u00b9\u0001\"\u0006\b\u00ba\u0001\u0010\u00bb\u0001R+\u0010,\u001a\u0004\u0018\u00010-8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00bc\u0001\u0010\u0003\u001a\u0006\b\u00bd\u0001\u0010\u00be\u0001\"\u0006\b\u00bf\u0001\u0010\u00c0\u0001R)\u0010.\u001a\u00020/8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00c1\u0001\u0010\u0003\u001a\u0006\b\u00c2\u0001\u0010\u00c3\u0001\"\u0006\b\u00c4\u0001\u0010\u00c5\u0001R)\u00100\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00c6\u0001\u0010\u0003\u001a\u0006\b\u00c7\u0001\u0010\u0092\u0001\"\u0006\b\u00c8\u0001\u0010\u0094\u0001RC\u00101\u001a\u001902j\u0003`\u00c9\u0001\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u00d2\u0001\u0012\u0005\b\u00cd\u0001\u0010\u0003\u001a\u0006\b\u00ce\u0001\u0010\u00cf\u0001\"\u0006\b\u00d0\u0001\u0010\u00d1\u0001RG\u00103\u001a\u001d\u0018\u000102j\u0005\u0018\u0001`\u00c9\u0001\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u00d2\u0001\u0012\u0005\b\u00d3\u0001\u0010\u0003\u001a\u0006\b\u00d4\u0001\u0010\u00cf\u0001\"\u0006\b\u00d5\u0001\u0010\u00d1\u0001R)\u00104\u001a\u0002058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00d6\u0001\u0010\u0003\u001a\u0006\b\u00d7\u0001\u0010\u00d8\u0001\"\u0006\b\u00d9\u0001\u0010\u00da\u0001R)\u00106\u001a\u0002058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00db\u0001\u0010\u0003\u001a\u0006\b\u00dc\u0001\u0010\u00d8\u0001\"\u0006\b\u00dd\u0001\u0010\u00da\u0001R'\u00107\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0017\n\u0000\u0012\u0005\b\u00de\u0001\u0010\u0003\u001a\u0005\b\u00df\u0001\u0010m\"\u0005\b\u00e0\u0001\u0010oR)\u00108\u001a\u0002098\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00e1\u0001\u0010\u0003\u001a\u0006\b\u00e2\u0001\u0010\u00e3\u0001\"\u0006\b\u00e4\u0001\u0010\u00e5\u0001R)\u0010:\u001a\u00020;8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00e6\u0001\u0010\u0003\u001a\u0006\b\u00e7\u0001\u0010\u00e8\u0001\"\u0006\b\u00e9\u0001\u0010\u00ea\u0001RC\u0010<\u001a\u00190=j\u0003`\u00eb\u0001\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u00f1\u0001\u0012\u0005\b\u00ec\u0001\u0010\u0003\u001a\u0006\b\u00ed\u0001\u0010\u00ee\u0001\"\u0006\b\u00ef\u0001\u0010\u00f0\u0001RC\u0010>\u001a\u00190?j\u0003`\u00f2\u0001\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u00f8\u0001\u0012\u0005\b\u00f3\u0001\u0010\u0003\u001a\u0006\b\u00f4\u0001\u0010\u00f5\u0001\"\u0006\b\u00f6\u0001\u0010\u00f7\u0001RC\u0010@\u001a\u00190?j\u0003`\u00f2\u0001\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u00f8\u0001\u0012\u0005\b\u00f9\u0001\u0010\u0003\u001a\u0006\b\u00fa\u0001\u0010\u00f5\u0001\"\u0006\b\u00fb\u0001\u0010\u00f7\u0001RC\u0010A\u001a\u00190Bj\u0003`\u00fc\u0001\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u0082\u0002\u0012\u0005\b\u00fd\u0001\u0010\u0003\u001a\u0006\b\u00fe\u0001\u0010\u00ff\u0001\"\u0006\b\u0080\u0002\u0010\u0081\u0002R)\u0010C\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u0083\u0002\u0010\u0003\u001a\u0006\b\u0084\u0002\u0010\u0092\u0001\"\u0006\b\u0085\u0002\u0010\u0094\u0001R)\u0010D\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u0086\u0002\u0010\u0003\u001a\u0006\b\u0087\u0002\u0010\u0092\u0001\"\u0006\b\u0088\u0002\u0010\u0094\u0001R'\u0010E\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0017\n\u0000\u0012\u0005\b\u0089\u0002\u0010\u0003\u001a\u0005\b\u008a\u0002\u0010m\"\u0005\b\u008b\u0002\u0010oR<\u0010F\u001a%\u0012\u0004\u0012\u00020H\u0012\u001b\u0012\u00190=j\u0003`\u00eb\u0001\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00010G8\u0002@\u0002X\u0083\u000e\u00a2\u0006\t\n\u0000\u0012\u0005\b\u008c\u0002\u0010\u0003R)\u0010\u008d\u0002\u001a\u0011\u0012\u0004\u0012\u00020H\u0012\u0005\u0012\u00030\u008e\u0002\u0018\u00010G8\u0002@\u0002X\u0083\u000e\u00a2\u0006\t\n\u0000\u0012\u0005\b\u008f\u0002\u0010\u0003R)\u0010I\u001a\u00020J8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u0090\u0002\u0010\u0003\u001a\u0006\b\u0091\u0002\u0010\u0092\u0002\"\u0006\b\u0093\u0002\u0010\u0094\u0002RG\u0010K\u001a\u001d\u0018\u00010Bj\u0005\u0018\u0001`\u00fc\u0001\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u0082\u0002\u0012\u0005\b\u0095\u0002\u0010\u0003\u001a\u0006\b\u0096\u0002\u0010\u00ff\u0001\"\u0006\b\u0097\u0002\u0010\u0081\u0002RC\u0010L\u001a\u00190Mj\u0003`\u0098\u0002\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u009e\u0002\u0012\u0005\b\u0099\u0002\u0010\u0003\u001a\u0006\b\u009a\u0002\u0010\u009b\u0002\"\u0006\b\u009c\u0002\u0010\u009d\u0002RC\u0010N\u001a\u00190Mj\u0003`\u0098\u0002\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u009e\u0002\u0012\u0005\b\u009f\u0002\u0010\u0003\u001a\u0006\b\u00a0\u0002\u0010\u009b\u0002\"\u0006\b\u00a1\u0002\u0010\u009d\u0002RC\u0010O\u001a\u00190Mj\u0003`\u0098\u0002\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u009e\u0002\u0012\u0005\b\u00a2\u0002\u0010\u0003\u001a\u0006\b\u00a3\u0002\u0010\u009b\u0002\"\u0006\b\u00a4\u0002\u0010\u009d\u0002R'\u0010P\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0017\n\u0000\u0012\u0005\b\u00a5\u0002\u0010\u0003\u001a\u0005\b\u00a6\u0002\u0010r\"\u0005\b\u00a7\u0002\u0010tR'\u0010Q\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0017\n\u0000\u0012\u0005\b\u00a8\u0002\u0010\u0003\u001a\u0005\b\u00a9\u0002\u0010m\"\u0005\b\u00aa\u0002\u0010oR)\u0010R\u001a\u00020H8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00ab\u0002\u0010\u0003\u001a\u0006\b\u00ac\u0002\u0010\u00ad\u0002\"\u0006\b\u00ae\u0002\u0010\u00af\u0002RC\u0010S\u001a\u00190Bj\u0003`\u00fc\u0001\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u0082\u0002\u0012\u0005\b\u00b0\u0002\u0010\u0003\u001a\u0006\b\u00b1\u0002\u0010\u00ff\u0001\"\u0006\b\u00b2\u0002\u0010\u0081\u0002RG\u0010T\u001a\u001d\u0018\u00010Bj\u0005\u0018\u0001`\u00fc\u0001\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u0082\u0002\u0012\u0005\b\u00b3\u0002\u0010\u0003\u001a\u0006\b\u00b4\u0002\u0010\u00ff\u0001\"\u0006\b\u00b5\u0002\u0010\u0081\u0002RC\u0010U\u001a\u00190Bj\u0003`\u00fc\u0001\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u0082\u0002\u0012\u0005\b\u00b6\u0002\u0010\u0003\u001a\u0006\b\u00b7\u0002\u0010\u00ff\u0001\"\u0006\b\u00b8\u0002\u0010\u0081\u0002RC\u0010V\u001a\u00190Mj\u0003`\u0098\u0002\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u009e\u0002\u0012\u0005\b\u00b9\u0002\u0010\u0003\u001a\u0006\b\u00ba\u0002\u0010\u009b\u0002\"\u0006\b\u00bb\u0002\u0010\u009d\u0002RC\u0010W\u001a\u00190Mj\u0003`\u0098\u0002\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u009e\u0002\u0012\u0005\b\u00bc\u0002\u0010\u0003\u001a\u0006\b\u00bd\u0002\u0010\u009b\u0002\"\u0006\b\u00be\u0002\u0010\u009d\u0002RC\u0010X\u001a\u00190Mj\u0003`\u0098\u0002\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u001c\n\u0003\u0010\u009e\u0002\u0012\u0005\b\u00bf\u0002\u0010\u0003\u001a\u0006\b\u00c0\u0002\u0010\u009b\u0002\"\u0006\b\u00c1\u0002\u0010\u009d\u0002R'\u0010Y\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0017\n\u0000\u0012\u0005\b\u00c2\u0002\u0010\u0003\u001a\u0005\b\u00c3\u0002\u0010r\"\u0005\b\u00c4\u0002\u0010tR\u0019\u0010Z\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\t\n\u0000\u0012\u0005\b\u00c5\u0002\u0010\u0003R)\u0010[\u001a\u00020\u00198\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00c6\u0002\u0010\u0003\u001a\u0006\b\u00c7\u0002\u0010\u0097\u0001\"\u0006\b\u00c8\u0002\u0010\u0099\u0001RF\u0010\\\u001a\u001f\u0012\u001b\u0012\u00190Bj\u0003`\u00fc\u0001\u00a2\u0006\u000f\b\u00ca\u0001\u0012\n\b\u00cb\u0001\u0012\u0005\b\t0\u00cc\u00010\u00108\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00c9\u0002\u0010\u0003\u001a\u0006\b\u00ca\u0002\u0010\u0083\u0001\"\u0006\b\u00cb\u0002\u0010\u0085\u0001R'\u0010]\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0017\n\u0000\u0012\u0005\b\u00cc\u0002\u0010\u0003\u001a\u0005\b\u00cd\u0002\u0010m\"\u0005\b\u00ce\u0002\u0010oR'\u0010^\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0017\n\u0000\u0012\u0005\b\u00cf\u0002\u0010\u0003\u001a\u0005\b\u00d0\u0002\u0010m\"\u0005\b\u00d1\u0002\u0010oR)\u0010_\u001a\u00020`8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00d2\u0002\u0010\u0003\u001a\u0006\b\u00d3\u0002\u0010\u00d4\u0002\"\u0006\b\u00d5\u0002\u0010\u00d6\u0002\u00a8\u0006\u00e1\u0002"}, d2={"Lcom/atsuishio/superbwarfare/data/vehicle/DefaultVehicleData;", "Lcom/atsuishio/superbwarfare/data/IDBasedData;", "<init>", "()V", "seen0", "", "seen1", "seen2", "maxHealth", "", "repairCooldown", "repairAmount", "selfHurtPercent", "selfHurtAmount", "maxEnergy", "obb", "", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/OBBInfo;", "seats", "Lcom/atsuishio/superbwarfare/data/ObjectToList;", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/SeatInfo;", "radar", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/RadarInfo;", "upStep", "trackDistanceMultiply", "", "keepChunkLoaded", "", "mouseSensitivity", "passengerRenderScale", "allowFreeCam", "hasDecoy", "smokeDecoy", "applyDefaultDamageModifiers", "sendHitParticles", "damageModifiers", "Lcom/atsuishio/superbwarfare/data/StringToObject;", "Lcom/atsuishio/superbwarfare/entity/vehicle/damage/DamageModify;", "mass", "towForceFactor", "decoyMagazineSize", "decoyReloadTime", "destroyInfo", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/DestroyInfo;", "seekInfo", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/SeekInfo;", "vehicleContainerType", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/VehicleContainerType;", "hasUpgradeSlots", "vehicleIcon", "Lnet/minecraft/resources/ResourceLocation;", "containerIcon", "hudColor", "Lcom/atsuishio/superbwarfare/data/ModColor;", "laserColor", "laserScale", "type", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/VehicleType;", "engineType", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/EngineType;", "engineInfo", "Lcom/google/gson/JsonObject;", "engineSound", "Lnet/minecraft/sounds/SoundEvent;", "hornSound", "thirdPersonCameraPos", "Lnet/minecraft/world/phys/Vec3;", "hasLowHealthWarning", "forwardTowed", "rotateOffsetHeight", "weapons", "", "", "collisionLevel", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/CollisionLevel;", "turretPos", "turretTurnSpeed", "Lnet/minecraft/world/phys/Vec2;", "turretYawRange", "turretPitchRange", "turretControllerIndex", "turretCustomPitch", "hudType", "barrelPos", "passengerWeaponStationPos", "passengerWeaponStationBarrelPos", "passengerWeaponStationTurnSpeed", "passengerWeaponStationYawRange", "passengerWeaponStationPitchRange", "passengerWeaponStationControllerIndex", "usePassengerCreativeAmmoBox", "gravity", "terrainCompat", "terrainCompatRotateRate", "inertiaRotateRate", "partHealth", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/PartHealth;", "serializationConstructorMarker", "Lkotlinx/serialization/internal/SerializationConstructorMarker;", "(IIIFIFFFILjava/util/List;Lcom/atsuishio/superbwarfare/data/ObjectToList;Lcom/atsuishio/superbwarfare/data/ObjectToList;FDZDFZZZZZLcom/atsuishio/superbwarfare/data/ObjectToList;FFIILcom/atsuishio/superbwarfare/data/vehicle/subdata/DestroyInfo;Lcom/atsuishio/superbwarfare/data/vehicle/subdata/SeekInfo;Lcom/atsuishio/superbwarfare/data/vehicle/subdata/VehicleContainerType;ZLnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lcom/atsuishio/superbwarfare/data/ModColor;Lcom/atsuishio/superbwarfare/data/ModColor;FLcom/atsuishio/superbwarfare/data/vehicle/subdata/VehicleType;Lcom/atsuishio/superbwarfare/data/vehicle/subdata/EngineType;Lcom/google/gson/JsonObject;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/world/phys/Vec3;ZZFLjava/util/Map;Lcom/atsuishio/superbwarfare/data/vehicle/subdata/CollisionLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec2;Lnet/minecraft/world/phys/Vec2;Lnet/minecraft/world/phys/Vec2;IFLjava/lang/String;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec2;Lnet/minecraft/world/phys/Vec2;Lnet/minecraft/world/phys/Vec2;IZDLjava/util/List;FFLcom/atsuishio/superbwarfare/data/vehicle/subdata/PartHealth;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V", "id", "getId$annotations", "isDefaultData", "isDefaultData$annotations", "getId", "setId", "", "getMaxHealth$annotations", "getMaxHealth", "()F", "setMaxHealth", "(F)V", "getRepairCooldown$annotations", "getRepairCooldown", "()I", "setRepairCooldown", "(I)V", "getRepairAmount$annotations", "getRepairAmount", "setRepairAmount", "getSelfHurtPercent$annotations", "getSelfHurtPercent", "setSelfHurtPercent", "getSelfHurtAmount$annotations", "getSelfHurtAmount", "setSelfHurtAmount", "getMaxEnergy$annotations", "getMaxEnergy", "setMaxEnergy", "getObb$annotations", "getObb", "()Ljava/util/List;", "setObb", "(Ljava/util/List;)V", "getSeats$annotations", "getRadar$annotations", "getRadar", "()Lcom/atsuishio/superbwarfare/data/ObjectToList;", "setRadar", "(Lcom/atsuishio/superbwarfare/data/ObjectToList;)V", "getUpStep$annotations", "getUpStep", "setUpStep", "getTrackDistanceMultiply$annotations", "getKeepChunkLoaded$annotations", "getKeepChunkLoaded", "()Z", "setKeepChunkLoaded", "(Z)V", "getMouseSensitivity$annotations", "getMouseSensitivity", "()D", "setMouseSensitivity", "(D)V", "getPassengerRenderScale$annotations", "getPassengerRenderScale", "setPassengerRenderScale", "getAllowFreeCam$annotations", "getAllowFreeCam", "setAllowFreeCam", "getHasDecoy$annotations", "getHasDecoy", "setHasDecoy", "getSmokeDecoy$annotations", "getSmokeDecoy", "setSmokeDecoy", "getApplyDefaultDamageModifiers$annotations", "getSendHitParticles$annotations", "getSendHitParticles", "setSendHitParticles", "getDamageModifiers$annotations", "getMass$annotations", "getMass", "setMass", "getTowForceFactor$annotations", "getTowForceFactor", "setTowForceFactor", "getDecoyMagazineSize$annotations", "getDecoyMagazineSize", "setDecoyMagazineSize", "getDecoyReloadTime$annotations", "getDecoyReloadTime", "setDecoyReloadTime", "getDestroyInfo$annotations", "getDestroyInfo", "()Lcom/atsuishio/superbwarfare/data/vehicle/subdata/DestroyInfo;", "setDestroyInfo", "(Lcom/atsuishio/superbwarfare/data/vehicle/subdata/DestroyInfo;)V", "getSeekInfo$annotations", "getSeekInfo", "()Lcom/atsuishio/superbwarfare/data/vehicle/subdata/SeekInfo;", "setSeekInfo", "(Lcom/atsuishio/superbwarfare/data/vehicle/subdata/SeekInfo;)V", "getVehicleContainerType$annotations", "getVehicleContainerType", "()Lcom/atsuishio/superbwarfare/data/vehicle/subdata/VehicleContainerType;", "setVehicleContainerType", "(Lcom/atsuishio/superbwarfare/data/vehicle/subdata/VehicleContainerType;)V", "getHasUpgradeSlots$annotations", "getHasUpgradeSlots", "setHasUpgradeSlots", "Lcom/atsuishio/superbwarfare/serialization/kserializer/SerializedResourceLocation;", "Lkotlinx/serialization/Serializable;", "with", "Lkotlin/reflect/KClass;", "getVehicleIcon$annotations", "getVehicleIcon", "()Lnet/minecraft/resources/ResourceLocation;", "setVehicleIcon", "(Lnet/minecraft/resources/ResourceLocation;)V", "Lnet/minecraft/resources/ResourceLocation;", "getContainerIcon$annotations", "getContainerIcon", "setContainerIcon", "getHudColor$annotations", "getHudColor", "()Lcom/atsuishio/superbwarfare/data/ModColor;", "setHudColor", "(Lcom/atsuishio/superbwarfare/data/ModColor;)V", "getLaserColor$annotations", "getLaserColor", "setLaserColor", "getLaserScale$annotations", "getLaserScale", "setLaserScale", "getType$annotations", "getType", "()Lcom/atsuishio/superbwarfare/data/vehicle/subdata/VehicleType;", "setType", "(Lcom/atsuishio/superbwarfare/data/vehicle/subdata/VehicleType;)V", "getEngineType$annotations", "getEngineType", "()Lcom/atsuishio/superbwarfare/data/vehicle/subdata/EngineType;", "setEngineType", "(Lcom/atsuishio/superbwarfare/data/vehicle/subdata/EngineType;)V", "Lcom/atsuishio/superbwarfare/serialization/kserializer/SerializedGsonObject;", "getEngineInfo$annotations", "getEngineInfo", "()Lcom/google/gson/JsonObject;", "setEngineInfo", "(Lcom/google/gson/JsonObject;)V", "Lcom/google/gson/JsonObject;", "Lcom/atsuishio/superbwarfare/serialization/kserializer/SerializedSoundEvent;", "getEngineSound$annotations", "getEngineSound", "()Lnet/minecraft/sounds/SoundEvent;", "setEngineSound", "(Lnet/minecraft/sounds/SoundEvent;)V", "Lnet/minecraft/sounds/SoundEvent;", "getHornSound$annotations", "getHornSound", "setHornSound", "Lcom/atsuishio/superbwarfare/serialization/kserializer/SerializedVec3;", "getThirdPersonCameraPos$annotations", "getThirdPersonCameraPos", "()Lnet/minecraft/world/phys/Vec3;", "setThirdPersonCameraPos", "(Lnet/minecraft/world/phys/Vec3;)V", "Lnet/minecraft/world/phys/Vec3;", "getHasLowHealthWarning$annotations", "getHasLowHealthWarning", "setHasLowHealthWarning", "getForwardTowed$annotations", "getForwardTowed", "setForwardTowed", "getRotateOffsetHeight$annotations", "getRotateOffsetHeight", "setRotateOffsetHeight", "getWeapons$annotations", "processedWeapons", "Lcom/atsuishio/superbwarfare/data/gun/DefaultGunData;", "getProcessedWeapons$annotations", "getCollisionLevel$annotations", "getCollisionLevel", "()Lcom/atsuishio/superbwarfare/data/vehicle/subdata/CollisionLevel;", "setCollisionLevel", "(Lcom/atsuishio/superbwarfare/data/vehicle/subdata/CollisionLevel;)V", "getTurretPos$annotations", "getTurretPos", "setTurretPos", "Lcom/atsuishio/superbwarfare/serialization/kserializer/SerializedVec2;", "getTurretTurnSpeed$annotations", "getTurretTurnSpeed", "()Lnet/minecraft/world/phys/Vec2;", "setTurretTurnSpeed", "(Lnet/minecraft/world/phys/Vec2;)V", "Lnet/minecraft/world/phys/Vec2;", "getTurretYawRange$annotations", "getTurretYawRange", "setTurretYawRange", "getTurretPitchRange$annotations", "getTurretPitchRange", "setTurretPitchRange", "getTurretControllerIndex$annotations", "getTurretControllerIndex", "setTurretControllerIndex", "getTurretCustomPitch$annotations", "getTurretCustomPitch", "setTurretCustomPitch", "getHudType$annotations", "getHudType", "()Ljava/lang/String;", "setHudType", "(Ljava/lang/String;)V", "getBarrelPos$annotations", "getBarrelPos", "setBarrelPos", "getPassengerWeaponStationPos$annotations", "getPassengerWeaponStationPos", "setPassengerWeaponStationPos", "getPassengerWeaponStationBarrelPos$annotations", "getPassengerWeaponStationBarrelPos", "setPassengerWeaponStationBarrelPos", "getPassengerWeaponStationTurnSpeed$annotations", "getPassengerWeaponStationTurnSpeed", "setPassengerWeaponStationTurnSpeed", "getPassengerWeaponStationYawRange$annotations", "getPassengerWeaponStationYawRange", "setPassengerWeaponStationYawRange", "getPassengerWeaponStationPitchRange$annotations", "getPassengerWeaponStationPitchRange", "setPassengerWeaponStationPitchRange", "getPassengerWeaponStationControllerIndex$annotations", "getPassengerWeaponStationControllerIndex", "setPassengerWeaponStationControllerIndex", "getUsePassengerCreativeAmmoBox$annotations", "getGravity$annotations", "getGravity", "setGravity", "getTerrainCompat$annotations", "getTerrainCompat", "setTerrainCompat", "getTerrainCompatRotateRate$annotations", "getTerrainCompatRotateRate", "setTerrainCompatRotateRate", "getInertiaRotateRate$annotations", "getInertiaRotateRate", "setInertiaRotateRate", "getPartHealth$annotations", "getPartHealth", "()Lcom/atsuishio/superbwarfare/data/vehicle/subdata/PartHealth;", "setPartHealth", "(Lcom/atsuishio/superbwarfare/data/vehicle/subdata/PartHealth;)V", "limit", "write$Self", "self", "output", "Lkotlinx/serialization/encoding/CompositeEncoder;", "serialDesc", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "write$Self$superbwarfare", "Companion", "$serializer", "superbwarfare"})
@SourceDebugExtension(value={"SMAP\nDefaultVehicleData.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DefaultVehicleData.kt\ncom/atsuishio/superbwarfare/data/vehicle/DefaultVehicleData\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,336:1\n1557#2:337\n1628#2,3:338\n*S KotlinDebug\n*F\n+ 1 DefaultVehicleData.kt\ncom/atsuishio/superbwarfare/data/vehicle/DefaultVehicleData\n*L\n318#1:337\n318#1:338,3\n*E\n"})
public final class DefaultVehicleData
implements IDBasedData<DefaultVehicleData> {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private transient String id;
    @JvmField
    public transient boolean isDefaultData;
    private float maxHealth;
    @ServerOnly
    private int repairCooldown;
    @ServerOnly
    private float repairAmount;
    @ServerOnly
    private float selfHurtPercent;
    @ServerOnly
    private float selfHurtAmount;
    private int maxEnergy;
    @NotNull
    private List<OBBInfo> obb;
    @Nullable
    private ObjectToList<SeatInfo> seats;
    @Nullable
    private ObjectToList<RadarInfo> radar;
    private float upStep;
    @JvmField
    public double trackDistanceMultiply;
    private boolean keepChunkLoaded;
    private double mouseSensitivity;
    private float passengerRenderScale;
    private boolean allowFreeCam;
    private boolean hasDecoy;
    private boolean smokeDecoy;
    @ServerOnly
    @JvmField
    public boolean applyDefaultDamageModifiers;
    @ServerOnly
    private boolean sendHitParticles;
    @ServerOnly
    @JvmField
    @NotNull
    public ObjectToList<StringToObject<DamageModify>> damageModifiers;
    @ServerOnly
    private float mass;
    @ServerOnly
    private float towForceFactor;
    @ServerOnly
    private int decoyMagazineSize;
    @ServerOnly
    private int decoyReloadTime;
    @ServerOnly
    @NotNull
    private DestroyInfo destroyInfo;
    @Nullable
    private SeekInfo seekInfo;
    @NotNull
    private VehicleContainerType vehicleContainerType;
    private boolean hasUpgradeSlots;
    @NotNull
    private ResourceLocation vehicleIcon;
    @Nullable
    private ResourceLocation containerIcon;
    @NotNull
    private ModColor hudColor;
    @NotNull
    private ModColor laserColor;
    private float laserScale;
    @NotNull
    private VehicleType type;
    @NotNull
    private EngineType engineType;
    @NotNull
    private JsonObject engineInfo;
    @NotNull
    private SoundEvent engineSound;
    @NotNull
    private SoundEvent hornSound;
    @NotNull
    private Vec3 thirdPersonCameraPos;
    private boolean hasLowHealthWarning;
    private boolean forwardTowed;
    private float rotateOffsetHeight;
    @NotNull
    private Map<String, JsonObject> weapons;
    @Nullable
    private transient Map<String, DefaultGunData> processedWeapons;
    @NotNull
    private CollisionLevel collisionLevel;
    @Nullable
    private Vec3 turretPos;
    @NotNull
    private Vec2 turretTurnSpeed;
    @NotNull
    private Vec2 turretYawRange;
    @NotNull
    private Vec2 turretPitchRange;
    private int turretControllerIndex;
    private float turretCustomPitch;
    @NotNull
    private String hudType;
    @NotNull
    private Vec3 barrelPos;
    @Nullable
    private Vec3 passengerWeaponStationPos;
    @NotNull
    private Vec3 passengerWeaponStationBarrelPos;
    @NotNull
    private Vec2 passengerWeaponStationTurnSpeed;
    @NotNull
    private Vec2 passengerWeaponStationYawRange;
    @NotNull
    private Vec2 passengerWeaponStationPitchRange;
    private int passengerWeaponStationControllerIndex;
    @JvmField
    public boolean usePassengerCreativeAmmoBox;
    private double gravity;
    @NotNull
    private List<Vec3> terrainCompat;
    private float terrainCompatRotateRate;
    private float inertiaRotateRate;
    @NotNull
    private PartHealth partHealth;
    @JvmField
    @NotNull
    private static final KSerializer<Object>[] $childSerializers;

    public DefaultVehicleData() {
        this.id = "";
        this.isDefaultData = true;
        this.maxHealth = 50.0f;
        Object object = DefaultVehicleData.Companion.getConfigOrDefault((ForgeConfigSpec.ConfigValue)VehicleConfig.REPAIR_COOLDOWN);
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"access$getConfigOrDefault(...)");
        this.repairCooldown = ((Number)object).intValue();
        this.repairAmount = (float)((Number)DefaultVehicleData.Companion.getConfigOrDefault((ForgeConfigSpec.ConfigValue)VehicleConfig.REPAIR_AMOUNT)).doubleValue();
        this.selfHurtPercent = 0.1f;
        this.selfHurtAmount = 0.1f;
        this.maxEnergy = Integer.MAX_VALUE;
        this.obb = new ArrayList();
        this.seats = new ObjectToList<SeatInfo>(new SeatInfo[0]);
        this.radar = new ObjectToList<RadarInfo>(new RadarInfo[0]);
        this.trackDistanceMultiply = 1.0;
        this.keepChunkLoaded = true;
        this.mouseSensitivity = 0.4;
        this.passengerRenderScale = 1.0f;
        this.smokeDecoy = true;
        this.applyDefaultDamageModifiers = true;
        this.sendHitParticles = true;
        this.damageModifiers = new ObjectToList<StringToObject>(new StringToObject[0]);
        this.mass = 1.0f;
        this.towForceFactor = 1.0f;
        this.decoyMagazineSize = 8;
        this.decoyReloadTime = 500;
        this.destroyInfo = new DestroyInfo();
        this.vehicleContainerType = VehicleContainerType.MEDIUM;
        this.vehicleIcon = Mod.Companion.loc("textures/gun_icon/default_icon.png");
        this.hudColor = new ModColor(0x66FF00);
        this.laserColor = new ModColor(0xFF0000);
        this.laserScale = 0.035f;
        this.type = VehicleType.EMPTY;
        this.engineType = EngineType.EMPTY;
        this.engineInfo = new JsonObject();
        SoundEvent soundEvent = SoundEvents.f_271165_;
        Intrinsics.checkNotNullExpressionValue((Object)soundEvent, (String)"EMPTY");
        this.engineSound = soundEvent;
        SoundEvent soundEvent2 = SoundEvents.f_271165_;
        Intrinsics.checkNotNullExpressionValue((Object)soundEvent2, (String)"EMPTY");
        this.hornSound = soundEvent2;
        this.thirdPersonCameraPos = new Vec3(0.0, 1.0, 3.0);
        this.hasLowHealthWarning = true;
        this.forwardTowed = true;
        this.weapons = new LinkedHashMap();
        this.collisionLevel = new CollisionLevel();
        this.turretTurnSpeed = new Vec2(5.0f, 5.0f);
        this.turretYawRange = new Vec2(-514.0f, 514.0f);
        this.turretPitchRange = new Vec2(-10.0f, 30.0f);
        this.hudType = "@Empty";
        Vec3 vec3 = Vec3.f_82478_;
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"ZERO");
        this.barrelPos = vec3;
        Vec3 vec32 = Vec3.f_82478_;
        Intrinsics.checkNotNullExpressionValue((Object)vec32, (String)"ZERO");
        this.passengerWeaponStationBarrelPos = vec32;
        this.passengerWeaponStationTurnSpeed = new Vec2(5.0f, 5.0f);
        this.passengerWeaponStationYawRange = new Vec2(-514.0f, 514.0f);
        this.passengerWeaponStationPitchRange = new Vec2(-10.0f, 30.0f);
        this.passengerWeaponStationControllerIndex = 1;
        this.usePassengerCreativeAmmoBox = true;
        this.gravity = 0.06;
        this.terrainCompat = new ArrayList();
        this.terrainCompatRotateRate = 1.0f;
        this.partHealth = new PartHealth();
    }

    @Transient
    private static /* synthetic */ void getId$annotations() {
    }

    @Transient
    public static /* synthetic */ void isDefaultData$annotations() {
    }

    @Override
    @NotNull
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(@NotNull String id) {
        Intrinsics.checkNotNullParameter((Object)id, (String)"id");
        this.id = id;
    }

    public final float getMaxHealth() {
        return this.maxHealth;
    }

    public final void setMaxHealth(float f) {
        this.maxHealth = f;
    }

    @SerialName(value="MaxHealth")
    public static /* synthetic */ void getMaxHealth$annotations() {
    }

    public final int getRepairCooldown() {
        return this.repairCooldown;
    }

    public final void setRepairCooldown(int n) {
        this.repairCooldown = n;
    }

    @SerialName(value="RepairCooldown")
    public static /* synthetic */ void getRepairCooldown$annotations() {
    }

    public final float getRepairAmount() {
        return this.repairAmount;
    }

    public final void setRepairAmount(float f) {
        this.repairAmount = f;
    }

    @SerialName(value="RepairAmount")
    public static /* synthetic */ void getRepairAmount$annotations() {
    }

    public final float getSelfHurtPercent() {
        return this.selfHurtPercent;
    }

    public final void setSelfHurtPercent(float f) {
        this.selfHurtPercent = f;
    }

    @SerialName(value="SelfHurtPercent")
    public static /* synthetic */ void getSelfHurtPercent$annotations() {
    }

    public final float getSelfHurtAmount() {
        return this.selfHurtAmount;
    }

    public final void setSelfHurtAmount(float f) {
        this.selfHurtAmount = f;
    }

    @SerialName(value="SelfHurtAmount")
    public static /* synthetic */ void getSelfHurtAmount$annotations() {
    }

    public final int getMaxEnergy() {
        return this.maxEnergy;
    }

    public final void setMaxEnergy(int n) {
        this.maxEnergy = n;
    }

    @SerialName(value="MaxEnergy")
    public static /* synthetic */ void getMaxEnergy$annotations() {
    }

    @NotNull
    public final List<OBBInfo> getObb() {
        return this.obb;
    }

    public final void setObb(@NotNull List<OBBInfo> list) {
        Intrinsics.checkNotNullParameter(list, (String)"<set-?>");
        this.obb = list;
    }

    @SerialName(value="OBB")
    public static /* synthetic */ void getObb$annotations() {
    }

    @SerialName(value="Seats")
    private static /* synthetic */ void getSeats$annotations() {
    }

    @Nullable
    public final ObjectToList<RadarInfo> getRadar() {
        return this.radar;
    }

    public final void setRadar(@Nullable ObjectToList<RadarInfo> objectToList) {
        this.radar = objectToList;
    }

    @SerialName(value="Radar")
    public static /* synthetic */ void getRadar$annotations() {
    }

    @NotNull
    public final List<SeatInfo> seats() {
        if (this.seats == null) {
            return new ArrayList();
        }
        ObjectToList<SeatInfo> objectToList = this.seats;
        Intrinsics.checkNotNull(objectToList);
        List<SeatInfo> list = Collections.unmodifiableList(objectToList.list);
        Intrinsics.checkNotNullExpressionValue(list, (String)"unmodifiableList(...)");
        return list;
    }

    public final float getUpStep() {
        return this.upStep;
    }

    public final void setUpStep(float f) {
        this.upStep = f;
    }

    @SerialName(value="UpStep")
    public static /* synthetic */ void getUpStep$annotations() {
    }

    @SerialName(value="TrackDistanceMultiply")
    public static /* synthetic */ void getTrackDistanceMultiply$annotations() {
    }

    public final boolean getKeepChunkLoaded() {
        return this.keepChunkLoaded;
    }

    public final void setKeepChunkLoaded(boolean bl) {
        this.keepChunkLoaded = bl;
    }

    @SerialName(value="KeepChunkLoaded")
    public static /* synthetic */ void getKeepChunkLoaded$annotations() {
    }

    public final double getMouseSensitivity() {
        return this.mouseSensitivity;
    }

    public final void setMouseSensitivity(double d) {
        this.mouseSensitivity = d;
    }

    @SerialName(value="MouseSensitivity")
    public static /* synthetic */ void getMouseSensitivity$annotations() {
    }

    public final float getPassengerRenderScale() {
        return this.passengerRenderScale;
    }

    public final void setPassengerRenderScale(float f) {
        this.passengerRenderScale = f;
    }

    @SerialName(value="PassengerRenderScale")
    public static /* synthetic */ void getPassengerRenderScale$annotations() {
    }

    public final boolean getAllowFreeCam() {
        return this.allowFreeCam;
    }

    public final void setAllowFreeCam(boolean bl) {
        this.allowFreeCam = bl;
    }

    @SerialName(value="AllowFreeCam")
    public static /* synthetic */ void getAllowFreeCam$annotations() {
    }

    public final boolean getHasDecoy() {
        return this.hasDecoy;
    }

    public final void setHasDecoy(boolean bl) {
        this.hasDecoy = bl;
    }

    @SerialName(value="HasDecoy")
    public static /* synthetic */ void getHasDecoy$annotations() {
    }

    public final boolean getSmokeDecoy() {
        return this.smokeDecoy;
    }

    public final void setSmokeDecoy(boolean bl) {
        this.smokeDecoy = bl;
    }

    @SerialName(value="SmokeDecoy")
    public static /* synthetic */ void getSmokeDecoy$annotations() {
    }

    @SerialName(value="ApplyDefaultDamageModifiers")
    public static /* synthetic */ void getApplyDefaultDamageModifiers$annotations() {
    }

    public final boolean getSendHitParticles() {
        return this.sendHitParticles;
    }

    public final void setSendHitParticles(boolean bl) {
        this.sendHitParticles = bl;
    }

    @SerialName(value="SendHitParticles")
    public static /* synthetic */ void getSendHitParticles$annotations() {
    }

    @SerialName(value="DamageModifiers")
    public static /* synthetic */ void getDamageModifiers$annotations() {
    }

    public final float getMass() {
        return this.mass;
    }

    public final void setMass(float f) {
        this.mass = f;
    }

    @SerialName(value="Mass")
    public static /* synthetic */ void getMass$annotations() {
    }

    public final float getTowForceFactor() {
        return this.towForceFactor;
    }

    public final void setTowForceFactor(float f) {
        this.towForceFactor = f;
    }

    @SerialName(value="TowForceFactor")
    public static /* synthetic */ void getTowForceFactor$annotations() {
    }

    public final int getDecoyMagazineSize() {
        return this.decoyMagazineSize;
    }

    public final void setDecoyMagazineSize(int n) {
        this.decoyMagazineSize = n;
    }

    @SerialName(value="DecoyMagazineSize")
    public static /* synthetic */ void getDecoyMagazineSize$annotations() {
    }

    public final int getDecoyReloadTime() {
        return this.decoyReloadTime;
    }

    public final void setDecoyReloadTime(int n) {
        this.decoyReloadTime = n;
    }

    @SerialName(value="DecoyReloadTime")
    public static /* synthetic */ void getDecoyReloadTime$annotations() {
    }

    @NotNull
    public final DestroyInfo getDestroyInfo() {
        return this.destroyInfo;
    }

    public final void setDestroyInfo(@NotNull DestroyInfo destroyInfo) {
        Intrinsics.checkNotNullParameter((Object)destroyInfo, (String)"<set-?>");
        this.destroyInfo = destroyInfo;
    }

    @SerialName(value="DestroyInfo")
    public static /* synthetic */ void getDestroyInfo$annotations() {
    }

    @Nullable
    public final SeekInfo getSeekInfo() {
        return this.seekInfo;
    }

    public final void setSeekInfo(@Nullable SeekInfo seekInfo) {
        this.seekInfo = seekInfo;
    }

    @SerialName(value="SeekInfo")
    public static /* synthetic */ void getSeekInfo$annotations() {
    }

    @NotNull
    public final VehicleContainerType getVehicleContainerType() {
        return this.vehicleContainerType;
    }

    public final void setVehicleContainerType(@NotNull VehicleContainerType vehicleContainerType) {
        Intrinsics.checkNotNullParameter((Object)((Object)vehicleContainerType), (String)"<set-?>");
        this.vehicleContainerType = vehicleContainerType;
    }

    @SerialName(value="VehicleContainerType")
    public static /* synthetic */ void getVehicleContainerType$annotations() {
    }

    public final boolean getHasUpgradeSlots() {
        return this.hasUpgradeSlots;
    }

    public final void setHasUpgradeSlots(boolean bl) {
        this.hasUpgradeSlots = bl;
    }

    @SerialName(value="HasUpgradeSlots")
    public static /* synthetic */ void getHasUpgradeSlots$annotations() {
    }

    @NotNull
    public final ResourceLocation getVehicleIcon() {
        return this.vehicleIcon;
    }

    public final void setVehicleIcon(@NotNull ResourceLocation resourceLocation) {
        Intrinsics.checkNotNullParameter((Object)resourceLocation, (String)"<set-?>");
        this.vehicleIcon = resourceLocation;
    }

    @SerialName(value="VehicleIcon")
    public static /* synthetic */ void getVehicleIcon$annotations() {
    }

    @Nullable
    public final ResourceLocation getContainerIcon() {
        return this.containerIcon;
    }

    public final void setContainerIcon(@Nullable ResourceLocation resourceLocation) {
        this.containerIcon = resourceLocation;
    }

    @SerialName(value="ContainerIcon")
    public static /* synthetic */ void getContainerIcon$annotations() {
    }

    @NotNull
    public final ModColor getHudColor() {
        return this.hudColor;
    }

    public final void setHudColor(@NotNull ModColor modColor) {
        Intrinsics.checkNotNullParameter((Object)modColor, (String)"<set-?>");
        this.hudColor = modColor;
    }

    @SerialName(value="HUDColor")
    public static /* synthetic */ void getHudColor$annotations() {
    }

    @NotNull
    public final ModColor getLaserColor() {
        return this.laserColor;
    }

    public final void setLaserColor(@NotNull ModColor modColor) {
        Intrinsics.checkNotNullParameter((Object)modColor, (String)"<set-?>");
        this.laserColor = modColor;
    }

    @SerialName(value="LaserColor")
    public static /* synthetic */ void getLaserColor$annotations() {
    }

    public final float getLaserScale() {
        return this.laserScale;
    }

    public final void setLaserScale(float f) {
        this.laserScale = f;
    }

    @SerialName(value="LaserScale")
    public static /* synthetic */ void getLaserScale$annotations() {
    }

    @NotNull
    public final VehicleType getType() {
        return this.type;
    }

    public final void setType(@NotNull VehicleType vehicleType) {
        Intrinsics.checkNotNullParameter((Object)((Object)vehicleType), (String)"<set-?>");
        this.type = vehicleType;
    }

    @SerialName(value="Type")
    public static /* synthetic */ void getType$annotations() {
    }

    @NotNull
    public final EngineType getEngineType() {
        return this.engineType;
    }

    public final void setEngineType(@NotNull EngineType engineType) {
        Intrinsics.checkNotNullParameter((Object)((Object)engineType), (String)"<set-?>");
        this.engineType = engineType;
    }

    @SerialName(value="EngineType")
    public static /* synthetic */ void getEngineType$annotations() {
    }

    @NotNull
    public final JsonObject getEngineInfo() {
        return this.engineInfo;
    }

    public final void setEngineInfo(@NotNull JsonObject jsonObject) {
        Intrinsics.checkNotNullParameter((Object)jsonObject, (String)"<set-?>");
        this.engineInfo = jsonObject;
    }

    @SerialName(value="EngineInfo")
    public static /* synthetic */ void getEngineInfo$annotations() {
    }

    @NotNull
    public final SoundEvent getEngineSound() {
        return this.engineSound;
    }

    public final void setEngineSound(@NotNull SoundEvent soundEvent) {
        Intrinsics.checkNotNullParameter((Object)soundEvent, (String)"<set-?>");
        this.engineSound = soundEvent;
    }

    @SerialName(value="EngineSound")
    public static /* synthetic */ void getEngineSound$annotations() {
    }

    @NotNull
    public final SoundEvent getHornSound() {
        return this.hornSound;
    }

    public final void setHornSound(@NotNull SoundEvent soundEvent) {
        Intrinsics.checkNotNullParameter((Object)soundEvent, (String)"<set-?>");
        this.hornSound = soundEvent;
    }

    @SerialName(value="HornSound")
    public static /* synthetic */ void getHornSound$annotations() {
    }

    @NotNull
    public final Vec3 getThirdPersonCameraPos() {
        return this.thirdPersonCameraPos;
    }

    public final void setThirdPersonCameraPos(@NotNull Vec3 vec3) {
        Intrinsics.checkNotNullParameter((Object)vec3, (String)"<set-?>");
        this.thirdPersonCameraPos = vec3;
    }

    @SerialName(value="ThirdPersonCameraPos")
    public static /* synthetic */ void getThirdPersonCameraPos$annotations() {
    }

    public final boolean getHasLowHealthWarning() {
        return this.hasLowHealthWarning;
    }

    public final void setHasLowHealthWarning(boolean bl) {
        this.hasLowHealthWarning = bl;
    }

    @SerialName(value="HasLowHealthWarning")
    public static /* synthetic */ void getHasLowHealthWarning$annotations() {
    }

    public final boolean getForwardTowed() {
        return this.forwardTowed;
    }

    public final void setForwardTowed(boolean bl) {
        this.forwardTowed = bl;
    }

    @SerialName(value="ForwardTowed")
    public static /* synthetic */ void getForwardTowed$annotations() {
    }

    public final float getRotateOffsetHeight() {
        return this.rotateOffsetHeight;
    }

    public final void setRotateOffsetHeight(float f) {
        this.rotateOffsetHeight = f;
    }

    @SerialName(value="RotateOffsetHeight")
    public static /* synthetic */ void getRotateOffsetHeight$annotations() {
    }

    @SerialName(value="Weapons")
    private static /* synthetic */ void getWeapons$annotations() {
    }

    @Transient
    private static /* synthetic */ void getProcessedWeapons$annotations() {
    }

    @NotNull
    public final Map<String, DefaultGunData> weapons() {
        if (this.processedWeapons != null) {
            Map<String, DefaultGunData> map = this.processedWeapons;
            Intrinsics.checkNotNull(map);
            return map;
        }
        HashMap map = new HashMap();
        for (Map.Entry<String, JsonObject> entry : this.weapons.entrySet()) {
            JsonObject value = entry.getValue();
            JsonElement primitive = (value = value.deepCopy()).get("Template");
            if (primitive instanceof JsonPrimitive && ((JsonPrimitive)primitive).isString()) {
                value.remove("Template");
                JsonObject templateValue = this.weapons.get(((JsonPrimitive)primitive).getAsString());
                if (templateValue != null) {
                    JsonObject newValue = templateValue.deepCopy();
                    for (Map.Entry kv : value.entrySet()) {
                        newValue.add((String)kv.getKey(), (JsonElement)kv.getValue());
                    }
                    value = newValue;
                }
            }
            Map map2 = map;
            String string = entry.getKey();
            Json json = DataLoader.INSTANCE.getJSON();
            DeserializationStrategy deserializationStrategy = (DeserializationStrategy)DefaultGunData.Companion.serializer();
            JsonObject jsonObject = value.getAsJsonObject();
            Intrinsics.checkNotNullExpressionValue((Object)jsonObject, (String)"getAsJsonObject(...)");
            Iterator iterator = json.decodeFromJsonElement(deserializationStrategy, JsonUtil.toKxJson((JsonElement)jsonObject));
            map2.put(string, iterator);
        }
        Map<String, DefaultGunData> map3 = this.processedWeapons = Collections.unmodifiableMap(map);
        Intrinsics.checkNotNull(map3);
        return map3;
    }

    @NotNull
    public final CollisionLevel getCollisionLevel() {
        return this.collisionLevel;
    }

    public final void setCollisionLevel(@NotNull CollisionLevel collisionLevel) {
        Intrinsics.checkNotNullParameter((Object)collisionLevel, (String)"<set-?>");
        this.collisionLevel = collisionLevel;
    }

    @SerialName(value="CollisionLevel")
    public static /* synthetic */ void getCollisionLevel$annotations() {
    }

    @Nullable
    public final Vec3 getTurretPos() {
        return this.turretPos;
    }

    public final void setTurretPos(@Nullable Vec3 vec3) {
        this.turretPos = vec3;
    }

    @SerialName(value="TurretPos")
    public static /* synthetic */ void getTurretPos$annotations() {
    }

    @NotNull
    public final Vec2 getTurretTurnSpeed() {
        return this.turretTurnSpeed;
    }

    public final void setTurretTurnSpeed(@NotNull Vec2 vec2) {
        Intrinsics.checkNotNullParameter((Object)vec2, (String)"<set-?>");
        this.turretTurnSpeed = vec2;
    }

    @SerialName(value="TurretTurnSpeed")
    public static /* synthetic */ void getTurretTurnSpeed$annotations() {
    }

    @NotNull
    public final Vec2 getTurretYawRange() {
        return this.turretYawRange;
    }

    public final void setTurretYawRange(@NotNull Vec2 vec2) {
        Intrinsics.checkNotNullParameter((Object)vec2, (String)"<set-?>");
        this.turretYawRange = vec2;
    }

    @SerialName(value="TurretYawRange")
    public static /* synthetic */ void getTurretYawRange$annotations() {
    }

    @NotNull
    public final Vec2 getTurretPitchRange() {
        return this.turretPitchRange;
    }

    public final void setTurretPitchRange(@NotNull Vec2 vec2) {
        Intrinsics.checkNotNullParameter((Object)vec2, (String)"<set-?>");
        this.turretPitchRange = vec2;
    }

    @SerialName(value="TurretPitchRange")
    public static /* synthetic */ void getTurretPitchRange$annotations() {
    }

    public final int getTurretControllerIndex() {
        return this.turretControllerIndex;
    }

    public final void setTurretControllerIndex(int n) {
        this.turretControllerIndex = n;
    }

    @SerialName(value="TurretControllerIndex")
    public static /* synthetic */ void getTurretControllerIndex$annotations() {
    }

    public final float getTurretCustomPitch() {
        return this.turretCustomPitch;
    }

    public final void setTurretCustomPitch(float f) {
        this.turretCustomPitch = f;
    }

    @SerialName(value="TurretCustomPitch")
    public static /* synthetic */ void getTurretCustomPitch$annotations() {
    }

    @NotNull
    public final String getHudType() {
        return this.hudType;
    }

    public final void setHudType(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        this.hudType = string;
    }

    @SerialName(value="HudType")
    public static /* synthetic */ void getHudType$annotations() {
    }

    @NotNull
    public final Vec3 getBarrelPos() {
        return this.barrelPos;
    }

    public final void setBarrelPos(@NotNull Vec3 vec3) {
        Intrinsics.checkNotNullParameter((Object)vec3, (String)"<set-?>");
        this.barrelPos = vec3;
    }

    @SerialName(value="BarrelPos")
    public static /* synthetic */ void getBarrelPos$annotations() {
    }

    @Nullable
    public final Vec3 getPassengerWeaponStationPos() {
        return this.passengerWeaponStationPos;
    }

    public final void setPassengerWeaponStationPos(@Nullable Vec3 vec3) {
        this.passengerWeaponStationPos = vec3;
    }

    @SerialName(value="PassengerWeaponStationPos")
    public static /* synthetic */ void getPassengerWeaponStationPos$annotations() {
    }

    @NotNull
    public final Vec3 getPassengerWeaponStationBarrelPos() {
        return this.passengerWeaponStationBarrelPos;
    }

    public final void setPassengerWeaponStationBarrelPos(@NotNull Vec3 vec3) {
        Intrinsics.checkNotNullParameter((Object)vec3, (String)"<set-?>");
        this.passengerWeaponStationBarrelPos = vec3;
    }

    @SerialName(value="PassengerWeaponStationBarrelPos")
    public static /* synthetic */ void getPassengerWeaponStationBarrelPos$annotations() {
    }

    @NotNull
    public final Vec2 getPassengerWeaponStationTurnSpeed() {
        return this.passengerWeaponStationTurnSpeed;
    }

    public final void setPassengerWeaponStationTurnSpeed(@NotNull Vec2 vec2) {
        Intrinsics.checkNotNullParameter((Object)vec2, (String)"<set-?>");
        this.passengerWeaponStationTurnSpeed = vec2;
    }

    @SerialName(value="PassengerWeaponStationTurnSpeed")
    public static /* synthetic */ void getPassengerWeaponStationTurnSpeed$annotations() {
    }

    @NotNull
    public final Vec2 getPassengerWeaponStationYawRange() {
        return this.passengerWeaponStationYawRange;
    }

    public final void setPassengerWeaponStationYawRange(@NotNull Vec2 vec2) {
        Intrinsics.checkNotNullParameter((Object)vec2, (String)"<set-?>");
        this.passengerWeaponStationYawRange = vec2;
    }

    @SerialName(value="PassengerWeaponStationYawRange")
    public static /* synthetic */ void getPassengerWeaponStationYawRange$annotations() {
    }

    @NotNull
    public final Vec2 getPassengerWeaponStationPitchRange() {
        return this.passengerWeaponStationPitchRange;
    }

    public final void setPassengerWeaponStationPitchRange(@NotNull Vec2 vec2) {
        Intrinsics.checkNotNullParameter((Object)vec2, (String)"<set-?>");
        this.passengerWeaponStationPitchRange = vec2;
    }

    @SerialName(value="PassengerWeaponStationPitchRange")
    public static /* synthetic */ void getPassengerWeaponStationPitchRange$annotations() {
    }

    public final int getPassengerWeaponStationControllerIndex() {
        return this.passengerWeaponStationControllerIndex;
    }

    public final void setPassengerWeaponStationControllerIndex(int n) {
        this.passengerWeaponStationControllerIndex = n;
    }

    @SerialName(value="PassengerWeaponStationControllerIndex")
    public static /* synthetic */ void getPassengerWeaponStationControllerIndex$annotations() {
    }

    @SerialName(value="UsePassengerCreativeAmmoBox")
    public static /* synthetic */ void getUsePassengerCreativeAmmoBox$annotations() {
    }

    public final double getGravity() {
        return this.gravity;
    }

    public final void setGravity(double d) {
        this.gravity = d;
    }

    @SerialName(value="Gravity")
    public static /* synthetic */ void getGravity$annotations() {
    }

    @NotNull
    public final List<Vec3> getTerrainCompat() {
        return this.terrainCompat;
    }

    public final void setTerrainCompat(@NotNull List<Vec3> list) {
        Intrinsics.checkNotNullParameter(list, (String)"<set-?>");
        this.terrainCompat = list;
    }

    @SerialName(value="TerrainCompat")
    public static /* synthetic */ void getTerrainCompat$annotations() {
    }

    public final float getTerrainCompatRotateRate() {
        return this.terrainCompatRotateRate;
    }

    public final void setTerrainCompatRotateRate(float f) {
        this.terrainCompatRotateRate = f;
    }

    @SerialName(value="TerrainCompatRotateRate")
    public static /* synthetic */ void getTerrainCompatRotateRate$annotations() {
    }

    public final float getInertiaRotateRate() {
        return this.inertiaRotateRate;
    }

    public final void setInertiaRotateRate(float f) {
        this.inertiaRotateRate = f;
    }

    @SerialName(value="InertiaRotateRate")
    public static /* synthetic */ void getInertiaRotateRate$annotations() {
    }

    @NotNull
    public final PartHealth getPartHealth() {
        return this.partHealth;
    }

    public final void setPartHealth(@NotNull PartHealth partHealth) {
        Intrinsics.checkNotNullParameter((Object)partHealth, (String)"<set-?>");
        this.partHealth = partHealth;
    }

    @SerialName(value="PartHealth")
    public static /* synthetic */ void getPartHealth$annotations() {
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void limit() {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        this.maxHealth = Math.max(this.maxHealth, 0.0f);
        this.repairCooldown = Math.max(this.repairCooldown, 0);
        this.maxEnergy = Math.max(this.maxEnergy, 0);
        Iterable iterable = this.obb;
        DefaultVehicleData defaultVehicleData = this;
        boolean $i$f$map = false;
        void var3_4 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            OBBInfo oBBInfo = (OBBInfo)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            it.limit();
            collection.add(it);
        }
        defaultVehicleData.obb = CollectionsKt.toMutableList((Collection)((List)destination$iv$iv));
        this.collisionLevel.setLevel(RangesKt.coerceIn((int)this.collisionLevel.getLevel(), (int)0, (int)4));
    }

    @JvmStatic
    public static final /* synthetic */ void write$Self$superbwarfare(DefaultVehicleData self, CompositeEncoder output, SerialDescriptor serialDesc) {
        boolean bl;
        boolean bl2;
        boolean bl3;
        boolean bl4;
        boolean bl5;
        KSerializer<Object>[] kSerializerArray = $childSerializers;
        if (output.shouldEncodeElementDefault(serialDesc, 0) ? true : Float.compare(self.maxHealth, 50.0f) != 0) {
            output.encodeFloatElement(serialDesc, 0, self.maxHealth);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 1)) {
            bl5 = true;
        } else {
            int n = self.repairCooldown;
            Object object = DefaultVehicleData.Companion.getConfigOrDefault((ForgeConfigSpec.ConfigValue)VehicleConfig.REPAIR_COOLDOWN);
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"access$getConfigOrDefault(...)");
            bl5 = n != ((Number)object).intValue();
        }
        if (bl5) {
            output.encodeIntElement(serialDesc, 1, self.repairCooldown);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 2) ? true : Float.compare(self.repairAmount, (float)((Number)DefaultVehicleData.Companion.getConfigOrDefault((ForgeConfigSpec.ConfigValue)VehicleConfig.REPAIR_AMOUNT)).doubleValue()) != 0) {
            output.encodeFloatElement(serialDesc, 2, self.repairAmount);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 3) ? true : Float.compare(self.selfHurtPercent, 0.1f) != 0) {
            output.encodeFloatElement(serialDesc, 3, self.selfHurtPercent);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 4) ? true : Float.compare(self.selfHurtAmount, 0.1f) != 0) {
            output.encodeFloatElement(serialDesc, 4, self.selfHurtAmount);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 5) ? true : self.maxEnergy != Integer.MAX_VALUE) {
            output.encodeIntElement(serialDesc, 5, self.maxEnergy);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 6) ? true : !Intrinsics.areEqual(self.obb, (Object)new ArrayList())) {
            output.encodeSerializableElement(serialDesc, 6, (SerializationStrategy)kSerializerArray[6], self.obb);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 7) ? true : !Intrinsics.areEqual(self.seats, new ObjectToList<SeatInfo>(new SeatInfo[0]))) {
            output.encodeNullableSerializableElement(serialDesc, 7, (SerializationStrategy)kSerializerArray[7], self.seats);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 8) ? true : !Intrinsics.areEqual(self.radar, new ObjectToList<RadarInfo>(new RadarInfo[0]))) {
            output.encodeNullableSerializableElement(serialDesc, 8, (SerializationStrategy)kSerializerArray[8], self.radar);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 9) ? true : Float.compare(self.upStep, 0.0f) != 0) {
            output.encodeFloatElement(serialDesc, 9, self.upStep);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 10) ? true : Double.compare(self.trackDistanceMultiply, 1.0) != 0) {
            output.encodeDoubleElement(serialDesc, 10, self.trackDistanceMultiply);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 11) ? true : !self.keepChunkLoaded) {
            output.encodeBooleanElement(serialDesc, 11, self.keepChunkLoaded);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 12) ? true : Double.compare(self.mouseSensitivity, 0.4) != 0) {
            output.encodeDoubleElement(serialDesc, 12, self.mouseSensitivity);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 13) ? true : Float.compare(self.passengerRenderScale, 1.0f) != 0) {
            output.encodeFloatElement(serialDesc, 13, self.passengerRenderScale);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 14) ? true : self.allowFreeCam) {
            output.encodeBooleanElement(serialDesc, 14, self.allowFreeCam);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 15) ? true : self.hasDecoy) {
            output.encodeBooleanElement(serialDesc, 15, self.hasDecoy);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 16) ? true : !self.smokeDecoy) {
            output.encodeBooleanElement(serialDesc, 16, self.smokeDecoy);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 17) ? true : !self.applyDefaultDamageModifiers) {
            output.encodeBooleanElement(serialDesc, 17, self.applyDefaultDamageModifiers);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 18) ? true : !self.sendHitParticles) {
            output.encodeBooleanElement(serialDesc, 18, self.sendHitParticles);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 19) ? true : !Intrinsics.areEqual(self.damageModifiers, new ObjectToList<StringToObject>(new StringToObject[0]))) {
            output.encodeSerializableElement(serialDesc, 19, (SerializationStrategy)kSerializerArray[19], self.damageModifiers);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 20) ? true : Float.compare(self.mass, 1.0f) != 0) {
            output.encodeFloatElement(serialDesc, 20, self.mass);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 21) ? true : Float.compare(self.towForceFactor, 1.0f) != 0) {
            output.encodeFloatElement(serialDesc, 21, self.towForceFactor);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 22) ? true : self.decoyMagazineSize != 8) {
            output.encodeIntElement(serialDesc, 22, self.decoyMagazineSize);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 23) ? true : self.decoyReloadTime != 500) {
            output.encodeIntElement(serialDesc, 23, self.decoyReloadTime);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 24) ? true : !Intrinsics.areEqual((Object)self.destroyInfo, (Object)new DestroyInfo())) {
            output.encodeSerializableElement(serialDesc, 24, (SerializationStrategy)DestroyInfo.$serializer.INSTANCE, (Object)self.destroyInfo);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 25) ? true : self.seekInfo != null) {
            output.encodeNullableSerializableElement(serialDesc, 25, (SerializationStrategy)SeekInfo.$serializer.INSTANCE, (Object)self.seekInfo);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 26) ? true : self.vehicleContainerType != VehicleContainerType.MEDIUM) {
            output.encodeSerializableElement(serialDesc, 26, (SerializationStrategy)kSerializerArray[26], (Object)self.vehicleContainerType);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 27) ? true : self.hasUpgradeSlots) {
            output.encodeBooleanElement(serialDesc, 27, self.hasUpgradeSlots);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 28) ? true : !Intrinsics.areEqual((Object)self.vehicleIcon, (Object)Mod.Companion.loc("textures/gun_icon/default_icon.png"))) {
            output.encodeSerializableElement(serialDesc, 28, (SerializationStrategy)ResourceLocationSerializer.INSTANCE, (Object)self.vehicleIcon);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 29) ? true : self.containerIcon != null) {
            output.encodeNullableSerializableElement(serialDesc, 29, (SerializationStrategy)ResourceLocationSerializer.INSTANCE, (Object)self.containerIcon);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 30) ? true : !Intrinsics.areEqual((Object)self.hudColor, (Object)new ModColor(0x66FF00))) {
            output.encodeSerializableElement(serialDesc, 30, (SerializationStrategy)ModColorSerializer.INSTANCE, (Object)self.hudColor);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 31) ? true : !Intrinsics.areEqual((Object)self.laserColor, (Object)new ModColor(0xFF0000))) {
            output.encodeSerializableElement(serialDesc, 31, (SerializationStrategy)ModColorSerializer.INSTANCE, (Object)self.laserColor);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 32) ? true : Float.compare(self.laserScale, 0.035f) != 0) {
            output.encodeFloatElement(serialDesc, 32, self.laserScale);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 33) ? true : self.type != VehicleType.EMPTY) {
            output.encodeSerializableElement(serialDesc, 33, (SerializationStrategy)kSerializerArray[33], (Object)self.type);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 34) ? true : self.engineType != EngineType.EMPTY) {
            output.encodeSerializableElement(serialDesc, 34, (SerializationStrategy)kSerializerArray[34], (Object)self.engineType);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 35) ? true : !Intrinsics.areEqual((Object)self.engineInfo, (Object)new JsonObject())) {
            output.encodeSerializableElement(serialDesc, 35, (SerializationStrategy)GsonObjectSerializer.INSTANCE, (Object)self.engineInfo);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 36)) {
            bl4 = true;
        } else {
            SoundEvent soundEvent = self.engineSound;
            SoundEvent soundEvent2 = SoundEvents.f_271165_;
            Intrinsics.checkNotNullExpressionValue((Object)soundEvent2, (String)"EMPTY");
            bl4 = !Intrinsics.areEqual((Object)soundEvent, (Object)soundEvent2);
        }
        if (bl4) {
            output.encodeSerializableElement(serialDesc, 36, (SerializationStrategy)SoundEventSerializer.INSTANCE, (Object)self.engineSound);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 37)) {
            bl3 = true;
        } else {
            SoundEvent soundEvent = self.hornSound;
            SoundEvent soundEvent3 = SoundEvents.f_271165_;
            Intrinsics.checkNotNullExpressionValue((Object)soundEvent3, (String)"EMPTY");
            bl3 = !Intrinsics.areEqual((Object)soundEvent, (Object)soundEvent3);
        }
        if (bl3) {
            output.encodeSerializableElement(serialDesc, 37, (SerializationStrategy)SoundEventSerializer.INSTANCE, (Object)self.hornSound);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 38) ? true : !Intrinsics.areEqual((Object)self.thirdPersonCameraPos, (Object)new Vec3(0.0, 1.0, 3.0))) {
            output.encodeSerializableElement(serialDesc, 38, (SerializationStrategy)Vec3Serializer.INSTANCE, (Object)self.thirdPersonCameraPos);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 39) ? true : !self.hasLowHealthWarning) {
            output.encodeBooleanElement(serialDesc, 39, self.hasLowHealthWarning);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 40) ? true : !self.forwardTowed) {
            output.encodeBooleanElement(serialDesc, 40, self.forwardTowed);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 41) ? true : Float.compare(self.rotateOffsetHeight, 0.0f) != 0) {
            output.encodeFloatElement(serialDesc, 41, self.rotateOffsetHeight);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 42) ? true : !Intrinsics.areEqual(self.weapons, (Object)new LinkedHashMap())) {
            output.encodeSerializableElement(serialDesc, 42, (SerializationStrategy)kSerializerArray[42], self.weapons);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 43) ? true : !Intrinsics.areEqual((Object)self.collisionLevel, (Object)new CollisionLevel())) {
            output.encodeSerializableElement(serialDesc, 43, (SerializationStrategy)CollisionLevel.$serializer.INSTANCE, (Object)self.collisionLevel);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 44) ? true : self.turretPos != null) {
            output.encodeNullableSerializableElement(serialDesc, 44, (SerializationStrategy)Vec3Serializer.INSTANCE, (Object)self.turretPos);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 45) ? true : !Intrinsics.areEqual((Object)self.turretTurnSpeed, (Object)new Vec2(5.0f, 5.0f))) {
            output.encodeSerializableElement(serialDesc, 45, (SerializationStrategy)Vec2Serializer.INSTANCE, (Object)self.turretTurnSpeed);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 46) ? true : !Intrinsics.areEqual((Object)self.turretYawRange, (Object)new Vec2(-514.0f, 514.0f))) {
            output.encodeSerializableElement(serialDesc, 46, (SerializationStrategy)Vec2Serializer.INSTANCE, (Object)self.turretYawRange);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 47) ? true : !Intrinsics.areEqual((Object)self.turretPitchRange, (Object)new Vec2(-10.0f, 30.0f))) {
            output.encodeSerializableElement(serialDesc, 47, (SerializationStrategy)Vec2Serializer.INSTANCE, (Object)self.turretPitchRange);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 48) ? true : self.turretControllerIndex != 0) {
            output.encodeIntElement(serialDesc, 48, self.turretControllerIndex);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 49) ? true : Float.compare(self.turretCustomPitch, 0.0f) != 0) {
            output.encodeFloatElement(serialDesc, 49, self.turretCustomPitch);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 50) ? true : !Intrinsics.areEqual((Object)self.hudType, (Object)"@Empty")) {
            output.encodeStringElement(serialDesc, 50, self.hudType);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 51)) {
            bl2 = true;
        } else {
            Vec3 vec3 = self.barrelPos;
            Vec3 vec32 = Vec3.f_82478_;
            Intrinsics.checkNotNullExpressionValue((Object)vec32, (String)"ZERO");
            bl2 = !Intrinsics.areEqual((Object)vec3, (Object)vec32);
        }
        if (bl2) {
            output.encodeSerializableElement(serialDesc, 51, (SerializationStrategy)Vec3Serializer.INSTANCE, (Object)self.barrelPos);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 52) ? true : self.passengerWeaponStationPos != null) {
            output.encodeNullableSerializableElement(serialDesc, 52, (SerializationStrategy)Vec3Serializer.INSTANCE, (Object)self.passengerWeaponStationPos);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 53)) {
            bl = true;
        } else {
            Vec3 vec3 = self.passengerWeaponStationBarrelPos;
            Vec3 vec33 = Vec3.f_82478_;
            Intrinsics.checkNotNullExpressionValue((Object)vec33, (String)"ZERO");
            bl = !Intrinsics.areEqual((Object)vec3, (Object)vec33);
        }
        if (bl) {
            output.encodeSerializableElement(serialDesc, 53, (SerializationStrategy)Vec3Serializer.INSTANCE, (Object)self.passengerWeaponStationBarrelPos);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 54) ? true : !Intrinsics.areEqual((Object)self.passengerWeaponStationTurnSpeed, (Object)new Vec2(5.0f, 5.0f))) {
            output.encodeSerializableElement(serialDesc, 54, (SerializationStrategy)Vec2Serializer.INSTANCE, (Object)self.passengerWeaponStationTurnSpeed);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 55) ? true : !Intrinsics.areEqual((Object)self.passengerWeaponStationYawRange, (Object)new Vec2(-514.0f, 514.0f))) {
            output.encodeSerializableElement(serialDesc, 55, (SerializationStrategy)Vec2Serializer.INSTANCE, (Object)self.passengerWeaponStationYawRange);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 56) ? true : !Intrinsics.areEqual((Object)self.passengerWeaponStationPitchRange, (Object)new Vec2(-10.0f, 30.0f))) {
            output.encodeSerializableElement(serialDesc, 56, (SerializationStrategy)Vec2Serializer.INSTANCE, (Object)self.passengerWeaponStationPitchRange);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 57) ? true : self.passengerWeaponStationControllerIndex != 1) {
            output.encodeIntElement(serialDesc, 57, self.passengerWeaponStationControllerIndex);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 58) ? true : !self.usePassengerCreativeAmmoBox) {
            output.encodeBooleanElement(serialDesc, 58, self.usePassengerCreativeAmmoBox);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 59) ? true : Double.compare(self.gravity, 0.06) != 0) {
            output.encodeDoubleElement(serialDesc, 59, self.gravity);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 60) ? true : !Intrinsics.areEqual(self.terrainCompat, (Object)new ArrayList())) {
            output.encodeSerializableElement(serialDesc, 60, (SerializationStrategy)kSerializerArray[60], self.terrainCompat);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 61) ? true : Float.compare(self.terrainCompatRotateRate, 1.0f) != 0) {
            output.encodeFloatElement(serialDesc, 61, self.terrainCompatRotateRate);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 62) ? true : Float.compare(self.inertiaRotateRate, 0.0f) != 0) {
            output.encodeFloatElement(serialDesc, 62, self.inertiaRotateRate);
        }
        if (output.shouldEncodeElementDefault(serialDesc, 63) ? true : !Intrinsics.areEqual((Object)self.partHealth, (Object)new PartHealth())) {
            output.encodeSerializableElement(serialDesc, 63, (SerializationStrategy)PartHealth.$serializer.INSTANCE, (Object)self.partHealth);
        }
    }

    public /* synthetic */ DefaultVehicleData(int seen0, int seen1, int seen2, float maxHealth, int repairCooldown, float repairAmount, float selfHurtPercent, float selfHurtAmount, int maxEnergy, List obb, ObjectToList seats, ObjectToList radar, float upStep, double trackDistanceMultiply, boolean keepChunkLoaded, double mouseSensitivity, float passengerRenderScale, boolean allowFreeCam, boolean hasDecoy, boolean smokeDecoy, boolean applyDefaultDamageModifiers, boolean sendHitParticles, ObjectToList damageModifiers, float mass, float towForceFactor, int decoyMagazineSize, int decoyReloadTime, DestroyInfo destroyInfo, SeekInfo seekInfo, VehicleContainerType vehicleContainerType, boolean hasUpgradeSlots, ResourceLocation vehicleIcon, ResourceLocation containerIcon, ModColor hudColor, ModColor laserColor, float laserScale, VehicleType type, EngineType engineType, JsonObject engineInfo, SoundEvent engineSound, SoundEvent hornSound, Vec3 thirdPersonCameraPos, boolean hasLowHealthWarning, boolean forwardTowed, float rotateOffsetHeight, Map weapons, CollisionLevel collisionLevel, Vec3 turretPos, Vec2 turretTurnSpeed, Vec2 turretYawRange, Vec2 turretPitchRange, int turretControllerIndex, float turretCustomPitch, String hudType, Vec3 barrelPos, Vec3 passengerWeaponStationPos, Vec3 passengerWeaponStationBarrelPos, Vec2 passengerWeaponStationTurnSpeed, Vec2 passengerWeaponStationYawRange, Vec2 passengerWeaponStationPitchRange, int passengerWeaponStationControllerIndex, boolean usePassengerCreativeAmmoBox, double gravity, List terrainCompat, float terrainCompatRotateRate, float inertiaRotateRate, PartHealth partHealth, SerializationConstructorMarker serializationConstructorMarker) {
        if ((0 & seen0) != 0 | (0 & seen1) != 0 | (0 & seen2) != 0) {
            int[] nArray = new int[]{seen0, seen1, seen2};
            int[] nArray2 = nArray;
            nArray = new int[]{0, 0, 0};
            PluginExceptionsKt.throwArrayMissingFieldException((int[])nArray2, (int[])nArray, (SerialDescriptor)$serializer.INSTANCE.getDescriptor());
        }
        this.id = "";
        this.isDefaultData = true;
        this.maxHealth = (seen0 & 1) == 0 ? 50.0f : maxHealth;
        if ((seen0 & 2) == 0) {
            Object object = DefaultVehicleData.Companion.getConfigOrDefault((ForgeConfigSpec.ConfigValue)VehicleConfig.REPAIR_COOLDOWN);
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"access$getConfigOrDefault(...)");
            this.repairCooldown = ((Number)object).intValue();
        } else {
            this.repairCooldown = repairCooldown;
        }
        this.repairAmount = (seen0 & 4) == 0 ? (float)((Number)DefaultVehicleData.Companion.getConfigOrDefault((ForgeConfigSpec.ConfigValue)VehicleConfig.REPAIR_AMOUNT)).doubleValue() : repairAmount;
        this.selfHurtPercent = (seen0 & 8) == 0 ? 0.1f : selfHurtPercent;
        this.selfHurtAmount = (seen0 & 0x10) == 0 ? 0.1f : selfHurtAmount;
        this.maxEnergy = (seen0 & 0x20) == 0 ? Integer.MAX_VALUE : maxEnergy;
        this.obb = (seen0 & 0x40) == 0 ? (List)new ArrayList() : obb;
        this.seats = (seen0 & 0x80) == 0 ? new ObjectToList<SeatInfo>(new SeatInfo[0]) : seats;
        this.radar = (seen0 & 0x100) == 0 ? new ObjectToList<RadarInfo>(new RadarInfo[0]) : radar;
        this.upStep = (seen0 & 0x200) == 0 ? 0.0f : upStep;
        this.trackDistanceMultiply = (seen0 & 0x400) == 0 ? 1.0 : trackDistanceMultiply;
        this.keepChunkLoaded = (seen0 & 0x800) == 0 ? true : keepChunkLoaded;
        this.mouseSensitivity = (seen0 & 0x1000) == 0 ? 0.4 : mouseSensitivity;
        this.passengerRenderScale = (seen0 & 0x2000) == 0 ? 1.0f : passengerRenderScale;
        this.allowFreeCam = (seen0 & 0x4000) == 0 ? false : allowFreeCam;
        this.hasDecoy = (seen0 & 0x8000) == 0 ? false : hasDecoy;
        this.smokeDecoy = (seen0 & 0x10000) == 0 ? true : smokeDecoy;
        this.applyDefaultDamageModifiers = (seen0 & 0x20000) == 0 ? true : applyDefaultDamageModifiers;
        this.sendHitParticles = (seen0 & 0x40000) == 0 ? true : sendHitParticles;
        this.damageModifiers = (seen0 & 0x80000) == 0 ? new ObjectToList<StringToObject>(new StringToObject[0]) : damageModifiers;
        this.mass = (seen0 & 0x100000) == 0 ? 1.0f : mass;
        this.towForceFactor = (seen0 & 0x200000) == 0 ? 1.0f : towForceFactor;
        this.decoyMagazineSize = (seen0 & 0x400000) == 0 ? 8 : decoyMagazineSize;
        this.decoyReloadTime = (seen0 & 0x800000) == 0 ? 500 : decoyReloadTime;
        this.destroyInfo = (seen0 & 0x1000000) == 0 ? new DestroyInfo() : destroyInfo;
        this.seekInfo = (seen0 & 0x2000000) == 0 ? null : seekInfo;
        this.vehicleContainerType = (seen0 & 0x4000000) == 0 ? VehicleContainerType.MEDIUM : vehicleContainerType;
        this.hasUpgradeSlots = (seen0 & 0x8000000) == 0 ? false : hasUpgradeSlots;
        this.vehicleIcon = (seen0 & 0x10000000) == 0 ? Mod.Companion.loc("textures/gun_icon/default_icon.png") : vehicleIcon;
        this.containerIcon = (seen0 & 0x20000000) == 0 ? null : containerIcon;
        this.hudColor = (seen0 & 0x40000000) == 0 ? new ModColor(0x66FF00) : hudColor;
        this.laserColor = (seen0 & Integer.MIN_VALUE) == 0 ? new ModColor(0xFF0000) : laserColor;
        this.laserScale = (seen1 & 1) == 0 ? 0.035f : laserScale;
        this.type = (seen1 & 2) == 0 ? VehicleType.EMPTY : type;
        this.engineType = (seen1 & 4) == 0 ? EngineType.EMPTY : engineType;
        this.engineInfo = (seen1 & 8) == 0 ? new JsonObject() : engineInfo;
        if ((seen1 & 0x10) == 0) {
            SoundEvent soundEvent = SoundEvents.f_271165_;
            Intrinsics.checkNotNullExpressionValue((Object)soundEvent, (String)"EMPTY");
            this.engineSound = soundEvent;
        } else {
            this.engineSound = engineSound;
        }
        if ((seen1 & 0x20) == 0) {
            SoundEvent soundEvent = SoundEvents.f_271165_;
            Intrinsics.checkNotNullExpressionValue((Object)soundEvent, (String)"EMPTY");
            this.hornSound = soundEvent;
        } else {
            this.hornSound = hornSound;
        }
        this.thirdPersonCameraPos = (seen1 & 0x40) == 0 ? new Vec3(0.0, 1.0, 3.0) : thirdPersonCameraPos;
        this.hasLowHealthWarning = (seen1 & 0x80) == 0 ? true : hasLowHealthWarning;
        this.forwardTowed = (seen1 & 0x100) == 0 ? true : forwardTowed;
        this.rotateOffsetHeight = (seen1 & 0x200) == 0 ? 0.0f : rotateOffsetHeight;
        this.weapons = (seen1 & 0x400) == 0 ? (Map)new LinkedHashMap() : weapons;
        this.processedWeapons = null;
        this.collisionLevel = (seen1 & 0x800) == 0 ? new CollisionLevel() : collisionLevel;
        this.turretPos = (seen1 & 0x1000) == 0 ? null : turretPos;
        this.turretTurnSpeed = (seen1 & 0x2000) == 0 ? new Vec2(5.0f, 5.0f) : turretTurnSpeed;
        this.turretYawRange = (seen1 & 0x4000) == 0 ? new Vec2(-514.0f, 514.0f) : turretYawRange;
        this.turretPitchRange = (seen1 & 0x8000) == 0 ? new Vec2(-10.0f, 30.0f) : turretPitchRange;
        this.turretControllerIndex = (seen1 & 0x10000) == 0 ? 0 : turretControllerIndex;
        this.turretCustomPitch = (seen1 & 0x20000) == 0 ? 0.0f : turretCustomPitch;
        this.hudType = (seen1 & 0x40000) == 0 ? "@Empty" : hudType;
        if ((seen1 & 0x80000) == 0) {
            Vec3 vec3 = Vec3.f_82478_;
            Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"ZERO");
            this.barrelPos = vec3;
        } else {
            this.barrelPos = barrelPos;
        }
        this.passengerWeaponStationPos = (seen1 & 0x100000) == 0 ? null : passengerWeaponStationPos;
        if ((seen1 & 0x200000) == 0) {
            Vec3 vec3 = Vec3.f_82478_;
            Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"ZERO");
            this.passengerWeaponStationBarrelPos = vec3;
        } else {
            this.passengerWeaponStationBarrelPos = passengerWeaponStationBarrelPos;
        }
        this.passengerWeaponStationTurnSpeed = (seen1 & 0x400000) == 0 ? new Vec2(5.0f, 5.0f) : passengerWeaponStationTurnSpeed;
        this.passengerWeaponStationYawRange = (seen1 & 0x800000) == 0 ? new Vec2(-514.0f, 514.0f) : passengerWeaponStationYawRange;
        this.passengerWeaponStationPitchRange = (seen1 & 0x1000000) == 0 ? new Vec2(-10.0f, 30.0f) : passengerWeaponStationPitchRange;
        this.passengerWeaponStationControllerIndex = (seen1 & 0x2000000) == 0 ? 1 : passengerWeaponStationControllerIndex;
        this.usePassengerCreativeAmmoBox = (seen1 & 0x4000000) == 0 ? true : usePassengerCreativeAmmoBox;
        this.gravity = (seen1 & 0x8000000) == 0 ? 0.06 : gravity;
        this.terrainCompat = (seen1 & 0x10000000) == 0 ? (List)new ArrayList() : terrainCompat;
        this.terrainCompatRotateRate = (seen1 & 0x20000000) == 0 ? 1.0f : terrainCompatRotateRate;
        this.inertiaRotateRate = (seen1 & 0x40000000) == 0 ? 0.0f : inertiaRotateRate;
        this.partHealth = (seen1 & Integer.MIN_VALUE) == 0 ? new PartHealth() : partHealth;
    }

    static {
        KSerializer[] kSerializerArray = new KSerializer[]{null, null, null, null, null, null, new ArrayListSerializer((KSerializer)OBBInfo.$serializer.INSTANCE), ObjectToList.Companion.serializer((KSerializer)SeatInfo.$serializer.INSTANCE), ObjectToList.Companion.serializer((KSerializer)RadarInfo.$serializer.INSTANCE), null, null, null, null, null, null, null, null, null, null, ObjectToList.Companion.serializer(StringToObject.Companion.serializer((KSerializer)DamageModify.$serializer.INSTANCE)), null, null, null, null, null, null, VehicleContainerType.Companion.serializer(), null, null, null, null, null, null, VehicleType.Companion.serializer(), EngineType.Companion.serializer(), null, null, null, null, null, null, null, new LinkedHashMapSerializer((KSerializer)StringSerializer.INSTANCE, (KSerializer)GsonObjectSerializer.INSTANCE), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, new ArrayListSerializer((KSerializer)Vec3Serializer.INSTANCE), null, null, null};
        $childSerializers = kSerializerArray;
    }

    @Deprecated(message="This synthesized declaration should not be used directly", level=DeprecationLevel.HIDDEN)
    @Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c7\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0015\u0010\u0005\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00070\u0006\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\t\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u000bJ\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0002R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006\u0015"}, d2={"com/atsuishio/superbwarfare/data/vehicle/DefaultVehicleData.$serializer", "Lkotlinx/serialization/internal/GeneratedSerializer;", "Lcom/atsuishio/superbwarfare/data/vehicle/DefaultVehicleData;", "<init>", "()V", "childSerializers", "", "Lkotlinx/serialization/KSerializer;", "()[Lkotlinx/serialization/KSerializer;", "deserialize", "decoder", "Lkotlinx/serialization/encoding/Decoder;", "serialize", "", "encoder", "Lkotlinx/serialization/encoding/Encoder;", "value", "descriptor", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "getDescriptor", "()Lkotlinx/serialization/descriptors/SerialDescriptor;", "superbwarfare"})
    public final class $serializer
    implements GeneratedSerializer<DefaultVehicleData> {
        @NotNull
        public static final $serializer INSTANCE = new $serializer();
        @NotNull
        private static final SerialDescriptor descriptor;

        private $serializer() {
        }

        public final void serialize(@NotNull Encoder encoder, @NotNull DefaultVehicleData value) {
            Intrinsics.checkNotNullParameter((Object)encoder, (String)"encoder");
            Intrinsics.checkNotNullParameter((Object)value, (String)"value");
            SerialDescriptor serialDescriptor = descriptor;
            CompositeEncoder compositeEncoder = encoder.beginStructure(serialDescriptor);
            DefaultVehicleData.write$Self$superbwarfare(value, compositeEncoder, serialDescriptor);
            compositeEncoder.endStructure(serialDescriptor);
        }

        @NotNull
        public final DefaultVehicleData deserialize(@NotNull Decoder decoder) {
            Intrinsics.checkNotNullParameter((Object)decoder, (String)"decoder");
            SerialDescriptor serialDescriptor = descriptor;
            boolean bl = true;
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            float f = 0.0f;
            int n4 = 0;
            float f2 = 0.0f;
            float f3 = 0.0f;
            float f4 = 0.0f;
            int n5 = 0;
            List list = null;
            ObjectToList objectToList = null;
            ObjectToList objectToList2 = null;
            float f5 = 0.0f;
            double d = 0.0;
            boolean bl2 = false;
            double d2 = 0.0;
            float f6 = 0.0f;
            boolean bl3 = false;
            boolean bl4 = false;
            boolean bl5 = false;
            boolean bl6 = false;
            boolean bl7 = false;
            ObjectToList objectToList3 = null;
            float f7 = 0.0f;
            float f8 = 0.0f;
            int n6 = 0;
            int n7 = 0;
            DestroyInfo destroyInfo = null;
            SeekInfo seekInfo = null;
            VehicleContainerType vehicleContainerType = null;
            boolean bl8 = false;
            ResourceLocation resourceLocation = null;
            ResourceLocation resourceLocation2 = null;
            ModColor modColor = null;
            ModColor modColor2 = null;
            float f9 = 0.0f;
            VehicleType vehicleType = null;
            EngineType engineType = null;
            JsonObject jsonObject = null;
            SoundEvent soundEvent = null;
            SoundEvent soundEvent2 = null;
            Vec3 vec3 = null;
            boolean bl9 = false;
            boolean bl10 = false;
            float f10 = 0.0f;
            Map map = null;
            CollisionLevel collisionLevel = null;
            Vec3 vec32 = null;
            Vec2 vec2 = null;
            Vec2 vec22 = null;
            Vec2 vec23 = null;
            int n8 = 0;
            float f11 = 0.0f;
            String string = null;
            Vec3 vec33 = null;
            Vec3 vec34 = null;
            Vec3 vec35 = null;
            Vec2 vec24 = null;
            Vec2 vec25 = null;
            Vec2 vec26 = null;
            int n9 = 0;
            boolean bl11 = false;
            double d3 = 0.0;
            List list2 = null;
            float f12 = 0.0f;
            float f13 = 0.0f;
            PartHealth partHealth = null;
            CompositeDecoder compositeDecoder = decoder.beginStructure(serialDescriptor);
            KSerializer[] kSerializerArray = $childSerializers;
            if (compositeDecoder.decodeSequentially()) {
                f = compositeDecoder.decodeFloatElement(serialDescriptor, 0);
                n |= 1;
                n4 = compositeDecoder.decodeIntElement(serialDescriptor, 1);
                n |= 2;
                f2 = compositeDecoder.decodeFloatElement(serialDescriptor, 2);
                n |= 4;
                f3 = compositeDecoder.decodeFloatElement(serialDescriptor, 3);
                n |= 8;
                f4 = compositeDecoder.decodeFloatElement(serialDescriptor, 4);
                n |= 0x10;
                n5 = compositeDecoder.decodeIntElement(serialDescriptor, 5);
                n |= 0x20;
                list = (List)compositeDecoder.decodeSerializableElement(serialDescriptor, 6, (DeserializationStrategy)kSerializerArray[6], (Object)list);
                n |= 0x40;
                objectToList = (ObjectToList)compositeDecoder.decodeNullableSerializableElement(serialDescriptor, 7, (DeserializationStrategy)kSerializerArray[7], (Object)objectToList);
                n |= 0x80;
                objectToList2 = (ObjectToList)compositeDecoder.decodeNullableSerializableElement(serialDescriptor, 8, (DeserializationStrategy)kSerializerArray[8], (Object)objectToList2);
                n |= 0x100;
                f5 = compositeDecoder.decodeFloatElement(serialDescriptor, 9);
                n |= 0x200;
                d = compositeDecoder.decodeDoubleElement(serialDescriptor, 10);
                n |= 0x400;
                bl2 = compositeDecoder.decodeBooleanElement(serialDescriptor, 11);
                n |= 0x800;
                d2 = compositeDecoder.decodeDoubleElement(serialDescriptor, 12);
                n |= 0x1000;
                f6 = compositeDecoder.decodeFloatElement(serialDescriptor, 13);
                n |= 0x2000;
                bl3 = compositeDecoder.decodeBooleanElement(serialDescriptor, 14);
                n |= 0x4000;
                bl4 = compositeDecoder.decodeBooleanElement(serialDescriptor, 15);
                n |= 0x8000;
                bl5 = compositeDecoder.decodeBooleanElement(serialDescriptor, 16);
                n |= 0x10000;
                bl6 = compositeDecoder.decodeBooleanElement(serialDescriptor, 17);
                n |= 0x20000;
                bl7 = compositeDecoder.decodeBooleanElement(serialDescriptor, 18);
                n |= 0x40000;
                objectToList3 = (ObjectToList)compositeDecoder.decodeSerializableElement(serialDescriptor, 19, (DeserializationStrategy)kSerializerArray[19], (Object)objectToList3);
                n |= 0x80000;
                f7 = compositeDecoder.decodeFloatElement(serialDescriptor, 20);
                n |= 0x100000;
                f8 = compositeDecoder.decodeFloatElement(serialDescriptor, 21);
                n |= 0x200000;
                n6 = compositeDecoder.decodeIntElement(serialDescriptor, 22);
                n |= 0x400000;
                n7 = compositeDecoder.decodeIntElement(serialDescriptor, 23);
                n |= 0x800000;
                destroyInfo = (DestroyInfo)compositeDecoder.decodeSerializableElement(serialDescriptor, 24, (DeserializationStrategy)DestroyInfo.$serializer.INSTANCE, (Object)destroyInfo);
                n |= 0x1000000;
                seekInfo = (SeekInfo)compositeDecoder.decodeNullableSerializableElement(serialDescriptor, 25, (DeserializationStrategy)SeekInfo.$serializer.INSTANCE, (Object)seekInfo);
                n |= 0x2000000;
                vehicleContainerType = (VehicleContainerType)((Object)compositeDecoder.decodeSerializableElement(serialDescriptor, 26, (DeserializationStrategy)kSerializerArray[26], (Object)vehicleContainerType));
                n |= 0x4000000;
                bl8 = compositeDecoder.decodeBooleanElement(serialDescriptor, 27);
                n |= 0x8000000;
                resourceLocation = (ResourceLocation)compositeDecoder.decodeSerializableElement(serialDescriptor, 28, (DeserializationStrategy)ResourceLocationSerializer.INSTANCE, (Object)resourceLocation);
                n |= 0x10000000;
                resourceLocation2 = (ResourceLocation)compositeDecoder.decodeNullableSerializableElement(serialDescriptor, 29, (DeserializationStrategy)ResourceLocationSerializer.INSTANCE, (Object)resourceLocation2);
                n |= 0x20000000;
                modColor = (ModColor)compositeDecoder.decodeSerializableElement(serialDescriptor, 30, (DeserializationStrategy)ModColorSerializer.INSTANCE, (Object)modColor);
                n |= 0x40000000;
                modColor2 = (ModColor)compositeDecoder.decodeSerializableElement(serialDescriptor, 31, (DeserializationStrategy)ModColorSerializer.INSTANCE, (Object)modColor2);
                n |= Integer.MIN_VALUE;
                f9 = compositeDecoder.decodeFloatElement(serialDescriptor, 32);
                n2 |= 1;
                vehicleType = (VehicleType)((Object)compositeDecoder.decodeSerializableElement(serialDescriptor, 33, (DeserializationStrategy)kSerializerArray[33], (Object)vehicleType));
                n2 |= 2;
                engineType = (EngineType)((Object)compositeDecoder.decodeSerializableElement(serialDescriptor, 34, (DeserializationStrategy)kSerializerArray[34], (Object)engineType));
                n2 |= 4;
                jsonObject = (JsonObject)compositeDecoder.decodeSerializableElement(serialDescriptor, 35, (DeserializationStrategy)GsonObjectSerializer.INSTANCE, (Object)jsonObject);
                n2 |= 8;
                soundEvent = (SoundEvent)compositeDecoder.decodeSerializableElement(serialDescriptor, 36, (DeserializationStrategy)SoundEventSerializer.INSTANCE, (Object)soundEvent);
                n2 |= 0x10;
                soundEvent2 = (SoundEvent)compositeDecoder.decodeSerializableElement(serialDescriptor, 37, (DeserializationStrategy)SoundEventSerializer.INSTANCE, (Object)soundEvent2);
                n2 |= 0x20;
                vec3 = (Vec3)compositeDecoder.decodeSerializableElement(serialDescriptor, 38, (DeserializationStrategy)Vec3Serializer.INSTANCE, (Object)vec3);
                n2 |= 0x40;
                bl9 = compositeDecoder.decodeBooleanElement(serialDescriptor, 39);
                n2 |= 0x80;
                bl10 = compositeDecoder.decodeBooleanElement(serialDescriptor, 40);
                n2 |= 0x100;
                f10 = compositeDecoder.decodeFloatElement(serialDescriptor, 41);
                n2 |= 0x200;
                map = (Map)compositeDecoder.decodeSerializableElement(serialDescriptor, 42, (DeserializationStrategy)kSerializerArray[42], (Object)map);
                n2 |= 0x400;
                collisionLevel = (CollisionLevel)compositeDecoder.decodeSerializableElement(serialDescriptor, 43, (DeserializationStrategy)CollisionLevel.$serializer.INSTANCE, (Object)collisionLevel);
                n2 |= 0x800;
                vec32 = (Vec3)compositeDecoder.decodeNullableSerializableElement(serialDescriptor, 44, (DeserializationStrategy)Vec3Serializer.INSTANCE, (Object)vec32);
                n2 |= 0x1000;
                vec2 = (Vec2)compositeDecoder.decodeSerializableElement(serialDescriptor, 45, (DeserializationStrategy)Vec2Serializer.INSTANCE, (Object)vec2);
                n2 |= 0x2000;
                vec22 = (Vec2)compositeDecoder.decodeSerializableElement(serialDescriptor, 46, (DeserializationStrategy)Vec2Serializer.INSTANCE, (Object)vec22);
                n2 |= 0x4000;
                vec23 = (Vec2)compositeDecoder.decodeSerializableElement(serialDescriptor, 47, (DeserializationStrategy)Vec2Serializer.INSTANCE, (Object)vec23);
                n2 |= 0x8000;
                n8 = compositeDecoder.decodeIntElement(serialDescriptor, 48);
                n2 |= 0x10000;
                f11 = compositeDecoder.decodeFloatElement(serialDescriptor, 49);
                n2 |= 0x20000;
                string = compositeDecoder.decodeStringElement(serialDescriptor, 50);
                n2 |= 0x40000;
                vec33 = (Vec3)compositeDecoder.decodeSerializableElement(serialDescriptor, 51, (DeserializationStrategy)Vec3Serializer.INSTANCE, (Object)vec33);
                n2 |= 0x80000;
                vec34 = (Vec3)compositeDecoder.decodeNullableSerializableElement(serialDescriptor, 52, (DeserializationStrategy)Vec3Serializer.INSTANCE, (Object)vec34);
                n2 |= 0x100000;
                vec35 = (Vec3)compositeDecoder.decodeSerializableElement(serialDescriptor, 53, (DeserializationStrategy)Vec3Serializer.INSTANCE, (Object)vec35);
                n2 |= 0x200000;
                vec24 = (Vec2)compositeDecoder.decodeSerializableElement(serialDescriptor, 54, (DeserializationStrategy)Vec2Serializer.INSTANCE, (Object)vec24);
                n2 |= 0x400000;
                vec25 = (Vec2)compositeDecoder.decodeSerializableElement(serialDescriptor, 55, (DeserializationStrategy)Vec2Serializer.INSTANCE, (Object)vec25);
                n2 |= 0x800000;
                vec26 = (Vec2)compositeDecoder.decodeSerializableElement(serialDescriptor, 56, (DeserializationStrategy)Vec2Serializer.INSTANCE, (Object)vec26);
                n2 |= 0x1000000;
                n9 = compositeDecoder.decodeIntElement(serialDescriptor, 57);
                n2 |= 0x2000000;
                bl11 = compositeDecoder.decodeBooleanElement(serialDescriptor, 58);
                n2 |= 0x4000000;
                d3 = compositeDecoder.decodeDoubleElement(serialDescriptor, 59);
                n2 |= 0x8000000;
                list2 = (List)compositeDecoder.decodeSerializableElement(serialDescriptor, 60, (DeserializationStrategy)kSerializerArray[60], (Object)list2);
                n2 |= 0x10000000;
                f12 = compositeDecoder.decodeFloatElement(serialDescriptor, 61);
                n2 |= 0x20000000;
                f13 = compositeDecoder.decodeFloatElement(serialDescriptor, 62);
                n2 |= 0x40000000;
                partHealth = (PartHealth)compositeDecoder.decodeSerializableElement(serialDescriptor, 63, (DeserializationStrategy)PartHealth.$serializer.INSTANCE, (Object)partHealth);
                n2 |= Integer.MIN_VALUE;
            } else {
                while (bl) {
                    int n10 = compositeDecoder.decodeElementIndex(serialDescriptor);
                    switch (n10) {
                        case -1: {
                            bl = false;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 0: {
                            f = compositeDecoder.decodeFloatElement(serialDescriptor, 0);
                            n |= 1;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 1: {
                            n4 = compositeDecoder.decodeIntElement(serialDescriptor, 1);
                            n |= 2;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 2: {
                            f2 = compositeDecoder.decodeFloatElement(serialDescriptor, 2);
                            n |= 4;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 3: {
                            f3 = compositeDecoder.decodeFloatElement(serialDescriptor, 3);
                            n |= 8;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 4: {
                            f4 = compositeDecoder.decodeFloatElement(serialDescriptor, 4);
                            n |= 0x10;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 5: {
                            n5 = compositeDecoder.decodeIntElement(serialDescriptor, 5);
                            n |= 0x20;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 6: {
                            list = (List)compositeDecoder.decodeSerializableElement(serialDescriptor, 6, (DeserializationStrategy)kSerializerArray[6], list);
                            n |= 0x40;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 7: {
                            objectToList = (ObjectToList)compositeDecoder.decodeNullableSerializableElement(serialDescriptor, 7, (DeserializationStrategy)kSerializerArray[7], objectToList);
                            n |= 0x80;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 8: {
                            objectToList2 = (ObjectToList)compositeDecoder.decodeNullableSerializableElement(serialDescriptor, 8, (DeserializationStrategy)kSerializerArray[8], objectToList2);
                            n |= 0x100;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 9: {
                            f5 = compositeDecoder.decodeFloatElement(serialDescriptor, 9);
                            n |= 0x200;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 10: {
                            d = compositeDecoder.decodeDoubleElement(serialDescriptor, 10);
                            n |= 0x400;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 11: {
                            bl2 = compositeDecoder.decodeBooleanElement(serialDescriptor, 11);
                            n |= 0x800;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 12: {
                            d2 = compositeDecoder.decodeDoubleElement(serialDescriptor, 12);
                            n |= 0x1000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 13: {
                            f6 = compositeDecoder.decodeFloatElement(serialDescriptor, 13);
                            n |= 0x2000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 14: {
                            bl3 = compositeDecoder.decodeBooleanElement(serialDescriptor, 14);
                            n |= 0x4000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 15: {
                            bl4 = compositeDecoder.decodeBooleanElement(serialDescriptor, 15);
                            n |= 0x8000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 16: {
                            bl5 = compositeDecoder.decodeBooleanElement(serialDescriptor, 16);
                            n |= 0x10000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 17: {
                            bl6 = compositeDecoder.decodeBooleanElement(serialDescriptor, 17);
                            n |= 0x20000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 18: {
                            bl7 = compositeDecoder.decodeBooleanElement(serialDescriptor, 18);
                            n |= 0x40000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 19: {
                            objectToList3 = (ObjectToList)compositeDecoder.decodeSerializableElement(serialDescriptor, 19, (DeserializationStrategy)kSerializerArray[19], objectToList3);
                            n |= 0x80000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 20: {
                            f7 = compositeDecoder.decodeFloatElement(serialDescriptor, 20);
                            n |= 0x100000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 21: {
                            f8 = compositeDecoder.decodeFloatElement(serialDescriptor, 21);
                            n |= 0x200000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 22: {
                            n6 = compositeDecoder.decodeIntElement(serialDescriptor, 22);
                            n |= 0x400000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 23: {
                            n7 = compositeDecoder.decodeIntElement(serialDescriptor, 23);
                            n |= 0x800000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 24: {
                            destroyInfo = (DestroyInfo)compositeDecoder.decodeSerializableElement(serialDescriptor, 24, (DeserializationStrategy)DestroyInfo.$serializer.INSTANCE, destroyInfo);
                            n |= 0x1000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 25: {
                            seekInfo = (SeekInfo)compositeDecoder.decodeNullableSerializableElement(serialDescriptor, 25, (DeserializationStrategy)SeekInfo.$serializer.INSTANCE, seekInfo);
                            n |= 0x2000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 26: {
                            vehicleContainerType = (VehicleContainerType)((Object)compositeDecoder.decodeSerializableElement(serialDescriptor, 26, (DeserializationStrategy)kSerializerArray[26], vehicleContainerType));
                            n |= 0x4000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 27: {
                            bl8 = compositeDecoder.decodeBooleanElement(serialDescriptor, 27);
                            n |= 0x8000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 28: {
                            resourceLocation = (ResourceLocation)compositeDecoder.decodeSerializableElement(serialDescriptor, 28, (DeserializationStrategy)ResourceLocationSerializer.INSTANCE, resourceLocation);
                            n |= 0x10000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 29: {
                            resourceLocation2 = (ResourceLocation)compositeDecoder.decodeNullableSerializableElement(serialDescriptor, 29, (DeserializationStrategy)ResourceLocationSerializer.INSTANCE, resourceLocation2);
                            n |= 0x20000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 30: {
                            modColor = (ModColor)compositeDecoder.decodeSerializableElement(serialDescriptor, 30, (DeserializationStrategy)ModColorSerializer.INSTANCE, modColor);
                            n |= 0x40000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 31: {
                            modColor2 = (ModColor)compositeDecoder.decodeSerializableElement(serialDescriptor, 31, (DeserializationStrategy)ModColorSerializer.INSTANCE, modColor2);
                            n |= Integer.MIN_VALUE;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 32: {
                            f9 = compositeDecoder.decodeFloatElement(serialDescriptor, 32);
                            n2 |= 1;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 33: {
                            vehicleType = (VehicleType)((Object)compositeDecoder.decodeSerializableElement(serialDescriptor, 33, (DeserializationStrategy)kSerializerArray[33], vehicleType));
                            n2 |= 2;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 34: {
                            engineType = (EngineType)((Object)compositeDecoder.decodeSerializableElement(serialDescriptor, 34, (DeserializationStrategy)kSerializerArray[34], engineType));
                            n2 |= 4;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 35: {
                            jsonObject = (JsonObject)compositeDecoder.decodeSerializableElement(serialDescriptor, 35, (DeserializationStrategy)GsonObjectSerializer.INSTANCE, jsonObject);
                            n2 |= 8;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 36: {
                            soundEvent = (SoundEvent)compositeDecoder.decodeSerializableElement(serialDescriptor, 36, (DeserializationStrategy)SoundEventSerializer.INSTANCE, soundEvent);
                            n2 |= 0x10;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 37: {
                            soundEvent2 = (SoundEvent)compositeDecoder.decodeSerializableElement(serialDescriptor, 37, (DeserializationStrategy)SoundEventSerializer.INSTANCE, soundEvent2);
                            n2 |= 0x20;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 38: {
                            vec3 = (Vec3)compositeDecoder.decodeSerializableElement(serialDescriptor, 38, (DeserializationStrategy)Vec3Serializer.INSTANCE, vec3);
                            n2 |= 0x40;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 39: {
                            bl9 = compositeDecoder.decodeBooleanElement(serialDescriptor, 39);
                            n2 |= 0x80;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 40: {
                            bl10 = compositeDecoder.decodeBooleanElement(serialDescriptor, 40);
                            n2 |= 0x100;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 41: {
                            f10 = compositeDecoder.decodeFloatElement(serialDescriptor, 41);
                            n2 |= 0x200;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 42: {
                            map = (Map)compositeDecoder.decodeSerializableElement(serialDescriptor, 42, (DeserializationStrategy)kSerializerArray[42], map);
                            n2 |= 0x400;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 43: {
                            collisionLevel = (CollisionLevel)compositeDecoder.decodeSerializableElement(serialDescriptor, 43, (DeserializationStrategy)CollisionLevel.$serializer.INSTANCE, collisionLevel);
                            n2 |= 0x800;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 44: {
                            vec32 = (Vec3)compositeDecoder.decodeNullableSerializableElement(serialDescriptor, 44, (DeserializationStrategy)Vec3Serializer.INSTANCE, vec32);
                            n2 |= 0x1000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 45: {
                            vec2 = (Vec2)compositeDecoder.decodeSerializableElement(serialDescriptor, 45, (DeserializationStrategy)Vec2Serializer.INSTANCE, vec2);
                            n2 |= 0x2000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 46: {
                            vec22 = (Vec2)compositeDecoder.decodeSerializableElement(serialDescriptor, 46, (DeserializationStrategy)Vec2Serializer.INSTANCE, vec22);
                            n2 |= 0x4000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 47: {
                            vec23 = (Vec2)compositeDecoder.decodeSerializableElement(serialDescriptor, 47, (DeserializationStrategy)Vec2Serializer.INSTANCE, vec23);
                            n2 |= 0x8000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 48: {
                            n8 = compositeDecoder.decodeIntElement(serialDescriptor, 48);
                            n2 |= 0x10000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 49: {
                            f11 = compositeDecoder.decodeFloatElement(serialDescriptor, 49);
                            n2 |= 0x20000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 50: {
                            string = compositeDecoder.decodeStringElement(serialDescriptor, 50);
                            n2 |= 0x40000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 51: {
                            vec33 = (Vec3)compositeDecoder.decodeSerializableElement(serialDescriptor, 51, (DeserializationStrategy)Vec3Serializer.INSTANCE, vec33);
                            n2 |= 0x80000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 52: {
                            vec34 = (Vec3)compositeDecoder.decodeNullableSerializableElement(serialDescriptor, 52, (DeserializationStrategy)Vec3Serializer.INSTANCE, vec34);
                            n2 |= 0x100000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 53: {
                            vec35 = (Vec3)compositeDecoder.decodeSerializableElement(serialDescriptor, 53, (DeserializationStrategy)Vec3Serializer.INSTANCE, vec35);
                            n2 |= 0x200000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 54: {
                            vec24 = (Vec2)compositeDecoder.decodeSerializableElement(serialDescriptor, 54, (DeserializationStrategy)Vec2Serializer.INSTANCE, vec24);
                            n2 |= 0x400000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 55: {
                            vec25 = (Vec2)compositeDecoder.decodeSerializableElement(serialDescriptor, 55, (DeserializationStrategy)Vec2Serializer.INSTANCE, vec25);
                            n2 |= 0x800000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 56: {
                            vec26 = (Vec2)compositeDecoder.decodeSerializableElement(serialDescriptor, 56, (DeserializationStrategy)Vec2Serializer.INSTANCE, vec26);
                            n2 |= 0x1000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 57: {
                            n9 = compositeDecoder.decodeIntElement(serialDescriptor, 57);
                            n2 |= 0x2000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 58: {
                            bl11 = compositeDecoder.decodeBooleanElement(serialDescriptor, 58);
                            n2 |= 0x4000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 59: {
                            d3 = compositeDecoder.decodeDoubleElement(serialDescriptor, 59);
                            n2 |= 0x8000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 60: {
                            list2 = (List)compositeDecoder.decodeSerializableElement(serialDescriptor, 60, (DeserializationStrategy)kSerializerArray[60], list2);
                            n2 |= 0x10000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 61: {
                            f12 = compositeDecoder.decodeFloatElement(serialDescriptor, 61);
                            n2 |= 0x20000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 62: {
                            f13 = compositeDecoder.decodeFloatElement(serialDescriptor, 62);
                            n2 |= 0x40000000;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        case 63: {
                            partHealth = (PartHealth)compositeDecoder.decodeSerializableElement(serialDescriptor, 63, (DeserializationStrategy)PartHealth.$serializer.INSTANCE, partHealth);
                            n2 |= Integer.MIN_VALUE;
                            Unit unit = Unit.INSTANCE;
                            break;
                        }
                        default: {
                            throw new UnknownFieldException(n10);
                        }
                    }
                }
            }
            compositeDecoder.endStructure(serialDescriptor);
            return new DefaultVehicleData(n, n2, n3, f, n4, f2, f3, f4, n5, list, objectToList, objectToList2, f5, d, bl2, d2, f6, bl3, bl4, bl5, bl6, bl7, objectToList3, f7, f8, n6, n7, destroyInfo, seekInfo, vehicleContainerType, bl8, resourceLocation, resourceLocation2, modColor, modColor2, f9, vehicleType, engineType, jsonObject, soundEvent, soundEvent2, vec3, bl9, bl10, f10, map, collisionLevel, vec32, vec2, vec22, vec23, n8, f11, string, vec33, vec34, vec35, vec24, vec25, vec26, n9, bl11, d3, list2, f12, f13, partHealth, null);
        }

        @NotNull
        public final SerialDescriptor getDescriptor() {
            return descriptor;
        }

        @NotNull
        public final KSerializer<?>[] childSerializers() {
            KSerializer[] kSerializerArray = $childSerializers;
            KSerializer[] kSerializerArray2 = new KSerializer[]{FloatSerializer.INSTANCE, IntSerializer.INSTANCE, FloatSerializer.INSTANCE, FloatSerializer.INSTANCE, FloatSerializer.INSTANCE, IntSerializer.INSTANCE, kSerializerArray[6], BuiltinSerializersKt.getNullable((KSerializer)kSerializerArray[7]), BuiltinSerializersKt.getNullable((KSerializer)kSerializerArray[8]), FloatSerializer.INSTANCE, DoubleSerializer.INSTANCE, BooleanSerializer.INSTANCE, DoubleSerializer.INSTANCE, FloatSerializer.INSTANCE, BooleanSerializer.INSTANCE, BooleanSerializer.INSTANCE, BooleanSerializer.INSTANCE, BooleanSerializer.INSTANCE, BooleanSerializer.INSTANCE, kSerializerArray[19], FloatSerializer.INSTANCE, FloatSerializer.INSTANCE, IntSerializer.INSTANCE, IntSerializer.INSTANCE, DestroyInfo.$serializer.INSTANCE, BuiltinSerializersKt.getNullable((KSerializer)((KSerializer)SeekInfo.$serializer.INSTANCE)), kSerializerArray[26], BooleanSerializer.INSTANCE, ResourceLocationSerializer.INSTANCE, BuiltinSerializersKt.getNullable((KSerializer)ResourceLocationSerializer.INSTANCE), ModColorSerializer.INSTANCE, ModColorSerializer.INSTANCE, FloatSerializer.INSTANCE, kSerializerArray[33], kSerializerArray[34], GsonObjectSerializer.INSTANCE, SoundEventSerializer.INSTANCE, SoundEventSerializer.INSTANCE, Vec3Serializer.INSTANCE, BooleanSerializer.INSTANCE, BooleanSerializer.INSTANCE, FloatSerializer.INSTANCE, kSerializerArray[42], CollisionLevel.$serializer.INSTANCE, BuiltinSerializersKt.getNullable((KSerializer)Vec3Serializer.INSTANCE), Vec2Serializer.INSTANCE, Vec2Serializer.INSTANCE, Vec2Serializer.INSTANCE, IntSerializer.INSTANCE, FloatSerializer.INSTANCE, StringSerializer.INSTANCE, Vec3Serializer.INSTANCE, BuiltinSerializersKt.getNullable((KSerializer)Vec3Serializer.INSTANCE), Vec3Serializer.INSTANCE, Vec2Serializer.INSTANCE, Vec2Serializer.INSTANCE, Vec2Serializer.INSTANCE, IntSerializer.INSTANCE, BooleanSerializer.INSTANCE, DoubleSerializer.INSTANCE, kSerializerArray[60], FloatSerializer.INSTANCE, FloatSerializer.INSTANCE, PartHealth.$serializer.INSTANCE};
            return kSerializerArray2;
        }

        @NotNull
        public KSerializer<?>[] typeParametersSerializers() {
            return GeneratedSerializer.DefaultImpls.typeParametersSerializers((GeneratedSerializer)this);
        }

        static {
            PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("com.atsuishio.superbwarfare.data.vehicle.DefaultVehicleData", (GeneratedSerializer)INSTANCE, 64);
            pluginGeneratedSerialDescriptor.addElement("MaxHealth", true);
            pluginGeneratedSerialDescriptor.addElement("RepairCooldown", true);
            pluginGeneratedSerialDescriptor.addElement("RepairAmount", true);
            pluginGeneratedSerialDescriptor.addElement("SelfHurtPercent", true);
            pluginGeneratedSerialDescriptor.addElement("SelfHurtAmount", true);
            pluginGeneratedSerialDescriptor.addElement("MaxEnergy", true);
            pluginGeneratedSerialDescriptor.addElement("OBB", true);
            pluginGeneratedSerialDescriptor.addElement("Seats", true);
            pluginGeneratedSerialDescriptor.addElement("Radar", true);
            pluginGeneratedSerialDescriptor.addElement("UpStep", true);
            pluginGeneratedSerialDescriptor.addElement("TrackDistanceMultiply", true);
            pluginGeneratedSerialDescriptor.addElement("KeepChunkLoaded", true);
            pluginGeneratedSerialDescriptor.addElement("MouseSensitivity", true);
            pluginGeneratedSerialDescriptor.addElement("PassengerRenderScale", true);
            pluginGeneratedSerialDescriptor.addElement("AllowFreeCam", true);
            pluginGeneratedSerialDescriptor.addElement("HasDecoy", true);
            pluginGeneratedSerialDescriptor.addElement("SmokeDecoy", true);
            pluginGeneratedSerialDescriptor.addElement("ApplyDefaultDamageModifiers", true);
            pluginGeneratedSerialDescriptor.addElement("SendHitParticles", true);
            pluginGeneratedSerialDescriptor.addElement("DamageModifiers", true);
            pluginGeneratedSerialDescriptor.addElement("Mass", true);
            pluginGeneratedSerialDescriptor.addElement("TowForceFactor", true);
            pluginGeneratedSerialDescriptor.addElement("DecoyMagazineSize", true);
            pluginGeneratedSerialDescriptor.addElement("DecoyReloadTime", true);
            pluginGeneratedSerialDescriptor.addElement("DestroyInfo", true);
            pluginGeneratedSerialDescriptor.addElement("SeekInfo", true);
            pluginGeneratedSerialDescriptor.addElement("VehicleContainerType", true);
            pluginGeneratedSerialDescriptor.addElement("HasUpgradeSlots", true);
            pluginGeneratedSerialDescriptor.addElement("VehicleIcon", true);
            pluginGeneratedSerialDescriptor.addElement("ContainerIcon", true);
            pluginGeneratedSerialDescriptor.addElement("HUDColor", true);
            pluginGeneratedSerialDescriptor.addElement("LaserColor", true);
            pluginGeneratedSerialDescriptor.addElement("LaserScale", true);
            pluginGeneratedSerialDescriptor.addElement("Type", true);
            pluginGeneratedSerialDescriptor.addElement("EngineType", true);
            pluginGeneratedSerialDescriptor.addElement("EngineInfo", true);
            pluginGeneratedSerialDescriptor.addElement("EngineSound", true);
            pluginGeneratedSerialDescriptor.addElement("HornSound", true);
            pluginGeneratedSerialDescriptor.addElement("ThirdPersonCameraPos", true);
            pluginGeneratedSerialDescriptor.addElement("HasLowHealthWarning", true);
            pluginGeneratedSerialDescriptor.addElement("ForwardTowed", true);
            pluginGeneratedSerialDescriptor.addElement("RotateOffsetHeight", true);
            pluginGeneratedSerialDescriptor.addElement("Weapons", true);
            pluginGeneratedSerialDescriptor.addElement("CollisionLevel", true);
            pluginGeneratedSerialDescriptor.addElement("TurretPos", true);
            pluginGeneratedSerialDescriptor.addElement("TurretTurnSpeed", true);
            pluginGeneratedSerialDescriptor.addElement("TurretYawRange", true);
            pluginGeneratedSerialDescriptor.addElement("TurretPitchRange", true);
            pluginGeneratedSerialDescriptor.addElement("TurretControllerIndex", true);
            pluginGeneratedSerialDescriptor.addElement("TurretCustomPitch", true);
            pluginGeneratedSerialDescriptor.addElement("HudType", true);
            pluginGeneratedSerialDescriptor.addElement("BarrelPos", true);
            pluginGeneratedSerialDescriptor.addElement("PassengerWeaponStationPos", true);
            pluginGeneratedSerialDescriptor.addElement("PassengerWeaponStationBarrelPos", true);
            pluginGeneratedSerialDescriptor.addElement("PassengerWeaponStationTurnSpeed", true);
            pluginGeneratedSerialDescriptor.addElement("PassengerWeaponStationYawRange", true);
            pluginGeneratedSerialDescriptor.addElement("PassengerWeaponStationPitchRange", true);
            pluginGeneratedSerialDescriptor.addElement("PassengerWeaponStationControllerIndex", true);
            pluginGeneratedSerialDescriptor.addElement("UsePassengerCreativeAmmoBox", true);
            pluginGeneratedSerialDescriptor.addElement("Gravity", true);
            pluginGeneratedSerialDescriptor.addElement("TerrainCompat", true);
            pluginGeneratedSerialDescriptor.addElement("TerrainCompatRotateRate", true);
            pluginGeneratedSerialDescriptor.addElement("InertiaRotateRate", true);
            pluginGeneratedSerialDescriptor.addElement("PartHealth", true);
            descriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
        }
    }

    @Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J!\u0010\u0004\u001a\u0002H\u0005\"\u0004\b\u0000\u0010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u0002H\u00050\u0007H\u0002\u00a2\u0006\u0002\u0010\bJ\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a8\u0006\f"}, d2={"Lcom/atsuishio/superbwarfare/data/vehicle/DefaultVehicleData$Companion;", "", "<init>", "()V", "getConfigOrDefault", "T", "config", "Lnet/minecraftforge/common/ForgeConfigSpec$ConfigValue;", "(Lnet/minecraftforge/common/ForgeConfigSpec$ConfigValue;)Ljava/lang/Object;", "serializer", "Lkotlinx/serialization/KSerializer;", "Lcom/atsuishio/superbwarfare/data/vehicle/DefaultVehicleData;", "superbwarfare"})
    public static final class Companion {
        private Companion() {
        }

        private final <T> T getConfigOrDefault(ForgeConfigSpec.ConfigValue<T> config) {
            Object object;
            try {
                object = config.get();
            }
            catch (Exception exception) {
                object = config.getDefault();
            }
            return (T)object;
        }

        @NotNull
        public final KSerializer<DefaultVehicleData> serializer() {
            return (KSerializer)$serializer.INSTANCE;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
