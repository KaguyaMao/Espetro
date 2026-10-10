/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.Fluid
 */
package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class Tags {
    public static TagKey<Item> item(ResourceLocation id) {
        return Tags.generic(id, Registries.f_256913_);
    }

    public static TagKey<Block> block(ResourceLocation id) {
        return Tags.generic(id, Registries.f_256747_);
    }

    public static TagKey<Fluid> fluid(ResourceLocation id) {
        return Tags.generic(id, Registries.f_256808_);
    }

    public static TagKey<EntityType<?>> entityType(ResourceLocation id) {
        return Tags.generic(id, Registries.f_256939_);
    }

    public static TagKey<Biome> biome(ResourceLocation id) {
        return Tags.generic(id, Registries.f_256952_);
    }

    public static Stream<TagKey<Item>> byItemStack(ItemStack stack) {
        return Tags.byItem(stack.m_41720_());
    }

    public static Stream<TagKey<Item>> byItem(Item item) {
        return Tags.forHolder(item.m_204114_());
    }

    public static Stream<TagKey<Block>> byBlockState(BlockState state) {
        return Tags.byBlock(state.m_60734_());
    }

    public static Stream<TagKey<Block>> byBlock(Block block) {
        return Tags.forHolder(block.m_204297_());
    }

    public static Stream<TagKey<Fluid>> byFluid(Fluid fluid) {
        return Tags.forHolder(fluid.m_205069_());
    }

    public static Stream<TagKey<EntityType<?>>> byEntity(Entity entity) {
        return Tags.byEntityType(entity.m_6095_());
    }

    public static Stream<TagKey<EntityType<?>>> byEntityType(EntityType<?> entityType) {
        return Tags.forHolder(entityType.m_204041_());
    }

    public static <T> Stream<TagKey<T>> forType(T object, Registry<T> registry) {
        Tags.warnIfUnbound();
        return registry.m_7854_(object).flatMap(arg_0 -> registry.m_203636_(arg_0)).stream().flatMap(Holder::m_203616_);
    }

    private static <T> TagKey<T> generic(ResourceLocation id, ResourceKey<Registry<T>> registry) {
        return TagKey.m_203882_(registry, (ResourceLocation)id);
    }

    private static <T> Stream<TagKey<T>> forHolder(Holder.Reference<T> registryHolder) {
        Tags.warnIfUnbound();
        return registryHolder.m_203616_();
    }

    private static void warnIfUnbound() {
        if (!((TagContext)TagContext.INSTANCE.getValue()).areTagsBound()) {
            ConsoleJS.getCurrent(ConsoleJS.STARTUP).warn("Tags have not been bound to registry yet! The values returned by this method may be outdated!", new Throwable());
        }
    }
}

