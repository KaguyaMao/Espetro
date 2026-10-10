/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Either
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.tags.PoiTypeTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.ai.village.poi.PoiType
 *  net.minecraft.world.entity.npc.VillagerProfession
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.misc;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class VillagerProfessionBuilder
extends BuilderBase<VillagerProfession> {
    public transient Either<ResourceKey<PoiType>, TagKey<PoiType>> poiType = Either.right((Object)PoiTypeTags.f_215875_);
    public transient ImmutableSet<Item> requestedItems = ImmutableSet.of();
    public transient ImmutableSet<Block> secondaryPoi = ImmutableSet.of();
    @Nullable
    public transient SoundEvent workSound = null;

    public VillagerProfessionBuilder(ResourceLocation i) {
        super(i);
    }

    @Override
    public final RegistryInfo getRegistryType() {
        return RegistryInfo.VILLAGER_PROFESSION;
    }

    @Override
    public VillagerProfession createObject() {
        Predicate<Holder> validPois = holder -> (Boolean)this.poiType.map(arg_0 -> ((Holder)holder).m_203565_(arg_0), arg_0 -> ((Holder)holder).m_203656_(arg_0));
        return new VillagerProfession(this.id.m_135815_(), validPois, validPois, this.requestedItems, this.secondaryPoi, this.workSound);
    }

    public VillagerProfessionBuilder poiType(ResourceLocation t) {
        this.poiType = Either.left((Object)ResourceKey.m_135785_((ResourceKey)Registries.f_256805_, (ResourceLocation)t));
        return this;
    }

    public VillagerProfessionBuilder poiTypeTag(ResourceLocation t) {
        this.poiType = Either.right((Object)TagKey.m_203882_((ResourceKey)Registries.f_256805_, (ResourceLocation)t));
        return this;
    }

    public VillagerProfessionBuilder requestedItems(Item[] t) {
        this.requestedItems = ImmutableSet.copyOf((Object[])t);
        return this;
    }

    public VillagerProfessionBuilder secondaryPoi(Block[] t) {
        this.secondaryPoi = ImmutableSet.copyOf((Object[])t);
        return this;
    }

    public VillagerProfessionBuilder workSound(@Nullable SoundEvent t) {
        this.workSound = t;
        return this;
    }
}

