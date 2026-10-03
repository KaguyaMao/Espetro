/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package frontline.combat.fcp.entity.vehicle.Trailers;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TrailerDriverData(double hitchX, double hitchY, double hitchZ) {
    public static final Codec<TrailerDriverData> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Codec.DOUBLE.optionalFieldOf("hitch_x", (Object)0.0).forGetter(TrailerDriverData::hitchX), (App)Codec.DOUBLE.optionalFieldOf("hitch_y", (Object)0.5).forGetter(TrailerDriverData::hitchY), (App)Codec.DOUBLE.fieldOf("hitch_z").forGetter(TrailerDriverData::hitchZ)).apply((Applicative)inst, TrailerDriverData::new));
}

