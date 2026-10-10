/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  javax.annotation.Nullable
 *  net.minecraft.util.ExtraCodecs$EitherCodec
 */
package net.minecraftforge.client.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.ExtraCodecs;

public record ForgeFaceData(int color, int blockLight, int skyLight, boolean ambientOcclusion, boolean calculateNormals) {
    public static final ForgeFaceData DEFAULT = new ForgeFaceData(-1, 0, 0, true, false);
    public static final Codec<Integer> COLOR = new ExtraCodecs.EitherCodec((Codec)Codec.INT, (Codec)Codec.STRING).xmap(either -> (Integer)either.map(Function.identity(), str -> (int)Long.parseLong(str, 16)), color -> Either.right((Object)Integer.toHexString(color)));
    public static final Codec<ForgeFaceData> CODEC = RecordCodecBuilder.create(builder -> builder.group((App)COLOR.optionalFieldOf("color", (Object)-1).forGetter(ForgeFaceData::color), (App)Codec.intRange((int)0, (int)15).optionalFieldOf("block_light", (Object)0).forGetter(ForgeFaceData::blockLight), (App)Codec.intRange((int)0, (int)15).optionalFieldOf("sky_light", (Object)0).forGetter(ForgeFaceData::skyLight), (App)Codec.BOOL.optionalFieldOf("ambient_occlusion", (Object)true).forGetter(ForgeFaceData::ambientOcclusion), (App)Codec.BOOL.optionalFieldOf("calculate_normals", (Object)false).forGetter(ForgeFaceData::calculateNormals)).apply((Applicative)builder, ForgeFaceData::new));

    public ForgeFaceData(int color, int blockLight, int skyLight, boolean ambientOcclusion) {
        this(color, blockLight, skyLight, ambientOcclusion, false);
    }

    @Nullable
    public static ForgeFaceData read(@Nullable JsonElement obj, @Nullable ForgeFaceData fallback) throws JsonParseException {
        if (obj == null) {
            return fallback;
        }
        return (ForgeFaceData)CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)obj).getOrThrow(false, JsonParseException::new);
    }
}

