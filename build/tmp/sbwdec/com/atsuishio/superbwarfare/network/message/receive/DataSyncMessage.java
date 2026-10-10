/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Deprecated
 *  kotlin.DeprecationLevel
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlinx.serialization.DeserializationStrategy
 *  kotlinx.serialization.KSerializer
 *  kotlinx.serialization.Serializable
 *  kotlinx.serialization.SerializationStrategy
 *  kotlinx.serialization.SerializersKt
 *  kotlinx.serialization.UnknownFieldException
 *  kotlinx.serialization.descriptors.SerialDescriptor
 *  kotlinx.serialization.encoding.CompositeDecoder
 *  kotlinx.serialization.encoding.CompositeEncoder
 *  kotlinx.serialization.encoding.Decoder
 *  kotlinx.serialization.encoding.Encoder
 *  kotlinx.serialization.internal.GeneratedSerializer
 *  kotlinx.serialization.internal.GeneratedSerializer$DefaultImpls
 *  kotlinx.serialization.internal.PluginExceptionsKt
 *  kotlinx.serialization.internal.PluginGeneratedSerialDescriptor
 *  kotlinx.serialization.internal.SerializationConstructorMarker
 *  kotlinx.serialization.internal.StringSerializer
 *  kotlinx.serialization.json.Json
 *  net.minecraftforge.network.NetworkEvent$Context
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atsuishio.superbwarfare.network.message.receive;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.data.DataLoader;
import com.atsuishio.superbwarfare.network.ClientPacketPayload;
import com.atsuishio.superbwarfare.serialization.kserializer.CompressedStringSerializer;
import com.atsuishio.superbwarfare.tools.JavaUtilKt;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.SerializersKt;
import kotlinx.serialization.UnknownFieldException;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeDecoder;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.encoding.Decoder;
import kotlinx.serialization.encoding.Encoder;
import kotlinx.serialization.internal.GeneratedSerializer;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import kotlinx.serialization.internal.StringSerializer;
import kotlinx.serialization.json.Json;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Serializable
@Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 ,2\u00020\u0001:\u0002+,B*\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0019\u0010\u0004\u001a\u00150\u0003j\u0002`\u0005\u00a2\u0006\f\b\u0006\u0012\b\b\u0007\u0012\u0004\b\t0\b\u00a2\u0006\u0004\b\t\u0010\nB/\b\u0010\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\u0004\b\t\u0010\u000fJ\u001b\u0010\u0014\u001a\u00020\u0015*\f\u0012\u0004\u0012\u00020\u00180\u0017j\u0002`\u0016H\u0016\u00a2\u0006\u0002\u0010\u0019J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J!\u0010\u001b\u001a\u00150\u0003j\u0002`\u0005\u00a2\u0006\f\b\u0006\u0012\b\b\u0007\u0012\u0004\b\t0\bH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0011J5\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u001b\b\u0002\u0010\u0004\u001a\u00150\u0003j\u0002`\u0005\u00a2\u0006\f\b\u0006\u0012\b\b\u0007\u0012\u0004\b\t0\bH\u00c6\u0001\u00a2\u0006\u0002\u0010\u001dJ\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010!H\u00d6\u0003J\t\u0010\"\u001a\u00020\fH\u00d6\u0001J\t\u0010#\u001a\u00020\u0003H\u00d6\u0001J%\u0010$\u001a\u00020\u00152\u0006\u0010%\u001a\u00020\u00002\u0006\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020)H\u0001\u00a2\u0006\u0002\b*R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R&\u0010\u0004\u001a\u00150\u0003j\u0002`\u0005\u00a2\u0006\f\b\u0006\u0012\b\b\u0007\u0012\u0004\b\t0\b\u00a2\u0006\n\n\u0002\u0010\u0013\u001a\u0004\b\u0012\u0010\u0011\u00a8\u0006-"}, d2={"Lcom/atsuishio/superbwarfare/network/message/receive/DataSyncMessage;", "Lcom/atsuishio/superbwarfare/network/ClientPacketPayload;", "path", "", "jsonData", "Lcom/atsuishio/superbwarfare/serialization/kserializer/CompressedString;", "Lkotlinx/serialization/Serializable;", "with", "Lcom/atsuishio/superbwarfare/serialization/kserializer/CompressedStringSerializer;", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", "seen0", "", "serializationConstructorMarker", "Lkotlinx/serialization/internal/SerializationConstructorMarker;", "(ILjava/lang/String;Ljava/lang/String;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V", "getPath", "()Ljava/lang/String;", "getJsonData", "Ljava/lang/String;", "handler", "", "Lcom/atsuishio/superbwarfare/network/PayloadContext;", "Ljava/util/function/Supplier;", "Lnet/minecraftforge/network/NetworkEvent$Context;", "(Ljava/util/function/Supplier;)V", "component1", "component2", "copy", "(Ljava/lang/String;Ljava/lang/String;)Lcom/atsuishio/superbwarfare/network/message/receive/DataSyncMessage;", "equals", "", "other", "", "hashCode", "toString", "write$Self", "self", "output", "Lkotlinx/serialization/encoding/CompositeEncoder;", "serialDesc", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "write$Self$superbwarfare", "$serializer", "Companion", "superbwarfare"})
public final class DataSyncMessage
extends ClientPacketPayload {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final String path;
    @NotNull
    private final String jsonData;

    public DataSyncMessage(@NotNull String path, @NotNull String jsonData) {
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        Intrinsics.checkNotNullParameter((Object)jsonData, (String)"jsonData");
        this.path = path;
        this.jsonData = jsonData;
    }

    @NotNull
    public final String getPath() {
        return this.path;
    }

    @NotNull
    public final String getJsonData() {
        return this.jsonData;
    }

    @Override
    public void handler(@NotNull Supplier<NetworkEvent.Context> $this$handler) {
        block3: {
            Object object;
            Intrinsics.checkNotNullParameter($this$handler, (String)"<this>");
            DataLoader.GeneralData<?> generalData = DataLoader.INSTANCE.getLOADED_DATA().get(this.path);
            if (generalData == null) {
                Supplier<NetworkEvent.Context> $this$handler_u24lambda_u240 = $this$handler;
                boolean bl = false;
                Mod.LOGGER.error("unknown data path " + this.path + "!");
                return;
            }
            DataLoader.GeneralData<?> data = generalData;
            if (data.isKtData()) {
                Json json = DataLoader.INSTANCE.getJSON();
                Type type = data.getMapType().getType();
                Intrinsics.checkNotNullExpressionValue((Object)type, (String)"getType(...)");
                object = json.decodeFromString((DeserializationStrategy)SerializersKt.serializer((Type)type), this.jsonData);
            } else {
                object = DataLoader.GSON.fromJson(this.jsonData, data.getMapType());
            }
            Object object2 = object;
            Intrinsics.checkNotNull((Object)object2, (String)"null cannot be cast to non-null type kotlin.collections.Map<kotlin.String, kotlin.Any>");
            Map map = (Map)object2;
            data.getDataMap().clear();
            data.getDataMap().putAll(map);
            Consumer<Map<String, Object>> consumer = data.getOnReload();
            if (consumer == null) break block3;
            JavaUtilKt.invoke(consumer, map);
        }
    }

    @NotNull
    public final String component1() {
        return this.path;
    }

    @NotNull
    public final String component2() {
        return this.jsonData;
    }

    @NotNull
    public final DataSyncMessage copy(@NotNull String path, @NotNull String jsonData) {
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        Intrinsics.checkNotNullParameter((Object)jsonData, (String)"jsonData");
        return new DataSyncMessage(path, jsonData);
    }

    public static /* synthetic */ DataSyncMessage copy$default(DataSyncMessage dataSyncMessage, String string, String string2, int n, Object object) {
        if ((n & 1) != 0) {
            string = dataSyncMessage.path;
        }
        if ((n & 2) != 0) {
            string2 = dataSyncMessage.jsonData;
        }
        return dataSyncMessage.copy(string, string2);
    }

    @NotNull
    public String toString() {
        return "DataSyncMessage(path=" + this.path + ", jsonData=" + this.jsonData + ")";
    }

    public int hashCode() {
        int result = this.path.hashCode();
        result = result * 31 + this.jsonData.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DataSyncMessage)) {
            return false;
        }
        DataSyncMessage dataSyncMessage = (DataSyncMessage)other;
        if (!Intrinsics.areEqual((Object)this.path, (Object)dataSyncMessage.path)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.jsonData, (Object)dataSyncMessage.jsonData);
    }

    @JvmStatic
    public static final /* synthetic */ void write$Self$superbwarfare(DataSyncMessage self, CompositeEncoder output, SerialDescriptor serialDesc) {
        output.encodeStringElement(serialDesc, 0, self.path);
        output.encodeSerializableElement(serialDesc, 1, (SerializationStrategy)CompressedStringSerializer.INSTANCE, (Object)self.jsonData);
    }

    public /* synthetic */ DataSyncMessage(int seen0, String path, String jsonData, SerializationConstructorMarker serializationConstructorMarker) {
        if (3 != (3 & seen0)) {
            PluginExceptionsKt.throwMissingFieldException((int)seen0, (int)3, (SerialDescriptor)$serializer.INSTANCE.getDescriptor());
        }
        this.path = path;
        this.jsonData = jsonData;
    }

    @Deprecated(message="This synthesized declaration should not be used directly", level=DeprecationLevel.HIDDEN)
    @Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c7\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0015\u0010\u0005\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00070\u0006\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\t\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u000bJ\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0002R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006\u0015"}, d2={"com/atsuishio/superbwarfare/network/message/receive/DataSyncMessage.$serializer", "Lkotlinx/serialization/internal/GeneratedSerializer;", "Lcom/atsuishio/superbwarfare/network/message/receive/DataSyncMessage;", "<init>", "()V", "childSerializers", "", "Lkotlinx/serialization/KSerializer;", "()[Lkotlinx/serialization/KSerializer;", "deserialize", "decoder", "Lkotlinx/serialization/encoding/Decoder;", "serialize", "", "encoder", "Lkotlinx/serialization/encoding/Encoder;", "value", "descriptor", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "getDescriptor", "()Lkotlinx/serialization/descriptors/SerialDescriptor;", "superbwarfare"})
    public final class $serializer
    implements GeneratedSerializer<DataSyncMessage> {
        @NotNull
        public static final $serializer INSTANCE = new $serializer();
        @NotNull
        private static final SerialDescriptor descriptor;

        private $serializer() {
        }

        public final void serialize(@NotNull Encoder encoder, @NotNull DataSyncMessage value) {
            Intrinsics.checkNotNullParameter((Object)encoder, (String)"encoder");
            Intrinsics.checkNotNullParameter((Object)value, (String)"value");
            SerialDescriptor serialDescriptor = descriptor;
            CompositeEncoder compositeEncoder = encoder.beginStructure(serialDescriptor);
            DataSyncMessage.write$Self$superbwarfare(value, compositeEncoder, serialDescriptor);
            compositeEncoder.endStructure(serialDescriptor);
        }

        @NotNull
        public final DataSyncMessage deserialize(@NotNull Decoder decoder) {
            Intrinsics.checkNotNullParameter((Object)decoder, (String)"decoder");
            SerialDescriptor serialDescriptor = descriptor;
            boolean bl = true;
            int n = 0;
            String string = null;
            String string2 = null;
            CompositeDecoder compositeDecoder = decoder.beginStructure(serialDescriptor);
            if (compositeDecoder.decodeSequentially()) {
                string = compositeDecoder.decodeStringElement(serialDescriptor, 0);
                n |= 1;
                string2 = (String)compositeDecoder.decodeSerializableElement(serialDescriptor, 1, (DeserializationStrategy)CompressedStringSerializer.INSTANCE, string2);
                n |= 2;
            } else {
                block5: while (bl) {
                    int n2 = compositeDecoder.decodeElementIndex(serialDescriptor);
                    switch (n2) {
                        case -1: {
                            bl = false;
                            continue block5;
                        }
                        case 0: {
                            string = compositeDecoder.decodeStringElement(serialDescriptor, 0);
                            n |= 1;
                            continue block5;
                        }
                        case 1: {
                            string2 = (String)compositeDecoder.decodeSerializableElement(serialDescriptor, 1, (DeserializationStrategy)CompressedStringSerializer.INSTANCE, string2);
                            n |= 2;
                            continue block5;
                        }
                    }
                    throw new UnknownFieldException(n2);
                }
            }
            compositeDecoder.endStructure(serialDescriptor);
            return new DataSyncMessage(n, string, string2, null);
        }

        @NotNull
        public final SerialDescriptor getDescriptor() {
            return descriptor;
        }

        @NotNull
        public final KSerializer<?>[] childSerializers() {
            KSerializer[] kSerializerArray = new KSerializer[]{StringSerializer.INSTANCE, CompressedStringSerializer.INSTANCE};
            return kSerializerArray;
        }

        @NotNull
        public KSerializer<?>[] typeParametersSerializers() {
            return GeneratedSerializer.DefaultImpls.typeParametersSerializers((GeneratedSerializer)this);
        }

        static {
            PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("com.atsuishio.superbwarfare.network.message.receive.DataSyncMessage", (GeneratedSerializer)INSTANCE, 2);
            pluginGeneratedSerialDescriptor.addElement("path", false);
            pluginGeneratedSerialDescriptor.addElement("jsonData", false);
            descriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
        }
    }

    @Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a8\u0006\u0007"}, d2={"Lcom/atsuishio/superbwarfare/network/message/receive/DataSyncMessage$Companion;", "", "<init>", "()V", "serializer", "Lkotlinx/serialization/KSerializer;", "Lcom/atsuishio/superbwarfare/network/message/receive/DataSyncMessage;", "superbwarfare"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final KSerializer<DataSyncMessage> serializer() {
            return (KSerializer)$serializer.INSTANCE;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
