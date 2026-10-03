/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Deprecated
 *  kotlin.DeprecationLevel
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlinx.serialization.KSerializer
 *  kotlinx.serialization.Serializable
 *  kotlinx.serialization.UnknownFieldException
 *  kotlinx.serialization.descriptors.SerialDescriptor
 *  kotlinx.serialization.encoding.CompositeDecoder
 *  kotlinx.serialization.encoding.CompositeEncoder
 *  kotlinx.serialization.encoding.Decoder
 *  kotlinx.serialization.encoding.Encoder
 *  kotlinx.serialization.internal.GeneratedSerializer
 *  kotlinx.serialization.internal.GeneratedSerializer$DefaultImpls
 *  kotlinx.serialization.internal.IntSerializer
 *  kotlinx.serialization.internal.PluginExceptionsKt
 *  kotlinx.serialization.internal.PluginGeneratedSerialDescriptor
 *  kotlinx.serialization.internal.SerializationConstructorMarker
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.network.NetworkEvent$Context
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atsuishio.superbwarfare.network.message.send;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.network.ServerPacketPayload;
import java.util.function.Supplier;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.UnknownFieldException;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeDecoder;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.encoding.Decoder;
import kotlinx.serialization.encoding.Encoder;
import kotlinx.serialization.internal.GeneratedSerializer;
import kotlinx.serialization.internal.IntSerializer;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Serializable
@Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 #2\u00020\u0001:\u0002\"#B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005B#\b\u0010\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\u0004\b\u0004\u0010\tJ\u001b\u0010\f\u001a\u00020\r*\f\u0012\u0004\u0012\u00020\u00100\u000fj\u0002`\u000eH\u0016\u00a2\u0006\u0002\u0010\u0011J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001J%\u0010\u001b\u001a\u00020\r2\u0006\u0010\u001c\u001a\u00020\u00002\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H\u0001\u00a2\u0006\u0002\b!R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006$"}, d2={"Lcom/atsuishio/superbwarfare/network/message/send/ChangeVehicleSeatMessage;", "Lcom/atsuishio/superbwarfare/network/ServerPacketPayload;", "index", "", "<init>", "(I)V", "seen0", "serializationConstructorMarker", "Lkotlinx/serialization/internal/SerializationConstructorMarker;", "(IILkotlinx/serialization/internal/SerializationConstructorMarker;)V", "getIndex", "()I", "handler", "", "Lcom/atsuishio/superbwarfare/network/PayloadContext;", "Ljava/util/function/Supplier;", "Lnet/minecraftforge/network/NetworkEvent$Context;", "(Ljava/util/function/Supplier;)V", "component1", "copy", "equals", "", "other", "", "hashCode", "toString", "", "write$Self", "self", "output", "Lkotlinx/serialization/encoding/CompositeEncoder;", "serialDesc", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "write$Self$superbwarfare", "$serializer", "Companion", "superbwarfare"})
public final class ChangeVehicleSeatMessage
extends ServerPacketPayload {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private final int index;

    public ChangeVehicleSeatMessage(int index) {
        this.index = index;
    }

    public final int getIndex() {
        return this.index;
    }

    @Override
    public void handler(@NotNull Supplier<NetworkEvent.Context> $this$handler) {
        Intrinsics.checkNotNullParameter($this$handler, (String)"<this>");
        ServerPlayer player = this.sender($this$handler);
        Entity entity = player.m_20202_();
        VehicleEntity vehicleEntity = entity instanceof VehicleEntity ? (VehicleEntity)entity : null;
        if (vehicleEntity == null) {
            return;
        }
        VehicleEntity vehicle = vehicleEntity;
        vehicle.changeSeat((Entity)player, this.index);
    }

    public final int component1() {
        return this.index;
    }

    @NotNull
    public final ChangeVehicleSeatMessage copy(int index) {
        return new ChangeVehicleSeatMessage(index);
    }

    public static /* synthetic */ ChangeVehicleSeatMessage copy$default(ChangeVehicleSeatMessage changeVehicleSeatMessage, int n, int n2, Object object) {
        if ((n2 & 1) != 0) {
            n = changeVehicleSeatMessage.index;
        }
        return changeVehicleSeatMessage.copy(n);
    }

    @NotNull
    public String toString() {
        return "ChangeVehicleSeatMessage(index=" + this.index + ")";
    }

    public int hashCode() {
        return Integer.hashCode(this.index);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ChangeVehicleSeatMessage)) {
            return false;
        }
        ChangeVehicleSeatMessage changeVehicleSeatMessage = (ChangeVehicleSeatMessage)other;
        return this.index == changeVehicleSeatMessage.index;
    }

    public /* synthetic */ ChangeVehicleSeatMessage(int seen0, int index, SerializationConstructorMarker serializationConstructorMarker) {
        if (1 != (1 & seen0)) {
            PluginExceptionsKt.throwMissingFieldException((int)seen0, (int)1, (SerialDescriptor)$serializer.INSTANCE.getDescriptor());
        }
        this.index = index;
    }

    @Deprecated(message="This synthesized declaration should not be used directly", level=DeprecationLevel.HIDDEN)
    @Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c7\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0015\u0010\u0005\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00070\u0006\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\t\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u000bJ\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0002R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006\u0015"}, d2={"com/atsuishio/superbwarfare/network/message/send/ChangeVehicleSeatMessage.$serializer", "Lkotlinx/serialization/internal/GeneratedSerializer;", "Lcom/atsuishio/superbwarfare/network/message/send/ChangeVehicleSeatMessage;", "<init>", "()V", "childSerializers", "", "Lkotlinx/serialization/KSerializer;", "()[Lkotlinx/serialization/KSerializer;", "deserialize", "decoder", "Lkotlinx/serialization/encoding/Decoder;", "serialize", "", "encoder", "Lkotlinx/serialization/encoding/Encoder;", "value", "descriptor", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "getDescriptor", "()Lkotlinx/serialization/descriptors/SerialDescriptor;", "superbwarfare"})
    public final class $serializer
    implements GeneratedSerializer<ChangeVehicleSeatMessage> {
        @NotNull
        public static final $serializer INSTANCE = new $serializer();
        @NotNull
        private static final SerialDescriptor descriptor;

        private $serializer() {
        }

        public final void serialize(@NotNull Encoder encoder, @NotNull ChangeVehicleSeatMessage value) {
            Intrinsics.checkNotNullParameter((Object)encoder, (String)"encoder");
            Intrinsics.checkNotNullParameter((Object)value, (String)"value");
            SerialDescriptor serialDescriptor = descriptor;
            CompositeEncoder compositeEncoder = encoder.beginStructure(serialDescriptor);
            compositeEncoder.encodeIntElement(serialDescriptor, 0, value.index);
            compositeEncoder.endStructure(serialDescriptor);
        }

        @NotNull
        public final ChangeVehicleSeatMessage deserialize(@NotNull Decoder decoder) {
            Intrinsics.checkNotNullParameter((Object)decoder, (String)"decoder");
            SerialDescriptor serialDescriptor = descriptor;
            boolean bl = true;
            int n = 0;
            int n2 = 0;
            CompositeDecoder compositeDecoder = decoder.beginStructure(serialDescriptor);
            if (compositeDecoder.decodeSequentially()) {
                n2 = compositeDecoder.decodeIntElement(serialDescriptor, 0);
                n |= 1;
            } else {
                block4: while (bl) {
                    int n3 = compositeDecoder.decodeElementIndex(serialDescriptor);
                    switch (n3) {
                        case -1: {
                            bl = false;
                            continue block4;
                        }
                        case 0: {
                            n2 = compositeDecoder.decodeIntElement(serialDescriptor, 0);
                            n |= 1;
                            continue block4;
                        }
                    }
                    throw new UnknownFieldException(n3);
                }
            }
            compositeDecoder.endStructure(serialDescriptor);
            return new ChangeVehicleSeatMessage(n, n2, null);
        }

        @NotNull
        public final SerialDescriptor getDescriptor() {
            return descriptor;
        }

        @NotNull
        public final KSerializer<?>[] childSerializers() {
            KSerializer[] kSerializerArray = new KSerializer[]{IntSerializer.INSTANCE};
            return kSerializerArray;
        }

        @NotNull
        public KSerializer<?>[] typeParametersSerializers() {
            return GeneratedSerializer.DefaultImpls.typeParametersSerializers((GeneratedSerializer)this);
        }

        static {
            PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("com.atsuishio.superbwarfare.network.message.send.ChangeVehicleSeatMessage", (GeneratedSerializer)INSTANCE, 1);
            pluginGeneratedSerialDescriptor.addElement("index", false);
            descriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
        }
    }

    @Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a8\u0006\u0007"}, d2={"Lcom/atsuishio/superbwarfare/network/message/send/ChangeVehicleSeatMessage$Companion;", "", "<init>", "()V", "serializer", "Lkotlinx/serialization/KSerializer;", "Lcom/atsuishio/superbwarfare/network/message/send/ChangeVehicleSeatMessage;", "superbwarfare"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final KSerializer<ChangeVehicleSeatMessage> serializer() {
            return (KSerializer)$serializer.INSTANCE;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
