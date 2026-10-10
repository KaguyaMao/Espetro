/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  net.minecraft.util.StringRepresentable
 */
package net.minecraftforge.common;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.StringRepresentable;

public interface IExtensibleEnum {
    @Deprecated
    default public void init() {
    }

    public static <E extends Enum<E>> Codec<E> createCodecForExtensibleEnum(Supplier<E[]> valuesSupplier, Function<? super String, ? extends E> enumValueFromNameFunction) {
        return Codec.either((Codec)Codec.STRING, (Codec)Codec.INT).comapFlatMap(either -> (DataResult)either.map(str -> {
            Enum val = (Enum)enumValueFromNameFunction.apply((String)str);
            return val != null ? DataResult.success((Object)val) : DataResult.error(() -> "Unknown enum value name: " + str);
        }, arg_0 -> IExtensibleEnum.lambda$createCodecForExtensibleEnum$3((Supplier)valuesSupplier, arg_0)), value -> Either.left((Object)((StringRepresentable)value).m_7912_()));
    }

    private static /* synthetic */ DataResult lambda$createCodecForExtensibleEnum$3(Supplier valuesSupplier, Integer num) {
        Enum[] values = (Enum[])valuesSupplier.get();
        return num >= 0 && num < values.length ? DataResult.success((Object)values[num]) : DataResult.error(() -> "Unknown enum id: " + num);
    }
}

