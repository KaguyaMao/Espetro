/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.resources.ResourceLocation
 */
package frontline.combat.fcp.entity.vehicle.Trailers;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record TrailerTowedData(double towX, double towY, double towZ, List<ResourceLocation> allowedDrivers, boolean allowAnyDriver, float maxArticulation, boolean terrainFollow, double attachSearchRadius) {
    public static final Codec<TrailerTowedData> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Codec.DOUBLE.optionalFieldOf("tow_x", (Object)0.0).forGetter(TrailerTowedData::towX), (App)Codec.DOUBLE.optionalFieldOf("tow_y", (Object)0.5).forGetter(TrailerTowedData::towY), (App)Codec.DOUBLE.fieldOf("tow_z").forGetter(TrailerTowedData::towZ), (App)ResourceLocation.f_135803_.listOf().optionalFieldOf("allowed_drivers", List.of()).forGetter(TrailerTowedData::allowedDrivers), (App)Codec.BOOL.optionalFieldOf("allow_any_driver", (Object)false).forGetter(TrailerTowedData::allowAnyDriver), (App)Codec.FLOAT.optionalFieldOf("max_articulation", (Object)Float.valueOf(110.0f)).forGetter(TrailerTowedData::maxArticulation), (App)Codec.BOOL.optionalFieldOf("terrain_follow", (Object)false).forGetter(TrailerTowedData::terrainFollow), (App)Codec.DOUBLE.optionalFieldOf("attach_search_radius", (Object)6.0).forGetter(TrailerTowedData::attachSearchRadius)).apply((Applicative)inst, TrailerTowedData::new));

    public boolean canBeTowedBy(ResourceLocation driverId) {
        return this.allowAnyDriver || this.allowedDrivers.contains(driverId);
    }
}

