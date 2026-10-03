/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.EntityType
 */
package tech.vvp.vvp.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class ModTags {
    private static TagKey<EntityType<?>> modEntityTag(String name) {
        return TagKey.m_203882_((ResourceKey)Registries.f_256939_, (ResourceLocation)new ResourceLocation("vvp", name));
    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> PANTSIR_AIR_TARGET = ModTags.modEntityTag("pantsir_air_target");
        public static final TagKey<EntityType<?>> PANTSIR_GROUND_TARGET = ModTags.modEntityTag("pantsir_ground_target");
        public static final TagKey<EntityType<?>> PANTSIR_MISSILE_TARGET = ModTags.modEntityTag("pantsir_missile_target");
    }
}

