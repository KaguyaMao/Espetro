/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo
 *  net.minecraft.core.Registry
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.Bootstrap
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.stats.StatType
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.entity.ai.sensing.SensorType
 *  net.minecraft.world.entity.ai.village.poi.PoiType
 *  net.minecraft.world.entity.decoration.PaintingVariant
 *  net.minecraft.world.entity.npc.VillagerProfession
 *  net.minecraft.world.entity.schedule.Activity
 *  net.minecraft.world.entity.schedule.Schedule
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.alchemy.Potion
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.chunk.ChunkStatus
 *  net.minecraft.world.level.levelgen.carver.WorldCarver
 *  net.minecraft.world.level.levelgen.feature.Feature
 *  net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType
 *  net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType
 *  net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType
 *  net.minecraft.world.level.material.Fluid
 */
package net.minecraftforge.registries;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.holdersets.HolderSetType;

public class ForgeRegistries {
    public static final IForgeRegistry<Block> BLOCKS;
    public static final IForgeRegistry<Fluid> FLUIDS;
    public static final IForgeRegistry<Item> ITEMS;
    public static final IForgeRegistry<MobEffect> MOB_EFFECTS;
    public static final IForgeRegistry<SoundEvent> SOUND_EVENTS;
    public static final IForgeRegistry<Potion> POTIONS;
    public static final IForgeRegistry<Enchantment> ENCHANTMENTS;
    public static final IForgeRegistry<EntityType<?>> ENTITY_TYPES;
    public static final IForgeRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES;
    public static final IForgeRegistry<ParticleType<?>> PARTICLE_TYPES;
    public static final IForgeRegistry<MenuType<?>> MENU_TYPES;
    public static final IForgeRegistry<PaintingVariant> PAINTING_VARIANTS;
    public static final IForgeRegistry<RecipeType<?>> RECIPE_TYPES;
    public static final IForgeRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS;
    public static final IForgeRegistry<Attribute> ATTRIBUTES;
    public static final IForgeRegistry<StatType<?>> STAT_TYPES;
    public static final IForgeRegistry<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES;
    public static final IForgeRegistry<VillagerProfession> VILLAGER_PROFESSIONS;
    public static final IForgeRegistry<PoiType> POI_TYPES;
    public static final IForgeRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPES;
    public static final IForgeRegistry<SensorType<?>> SENSOR_TYPES;
    public static final IForgeRegistry<Schedule> SCHEDULES;
    public static final IForgeRegistry<Activity> ACTIVITIES;
    public static final IForgeRegistry<WorldCarver<?>> WORLD_CARVERS;
    public static final IForgeRegistry<Feature<?>> FEATURES;
    public static final IForgeRegistry<ChunkStatus> CHUNK_STATUS;
    public static final IForgeRegistry<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPES;
    public static final IForgeRegistry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES;
    public static final IForgeRegistry<TreeDecoratorType<?>> TREE_DECORATOR_TYPES;
    public static final IForgeRegistry<Biome> BIOMES;
    static final DeferredRegister<EntityDataSerializer<?>> DEFERRED_ENTITY_DATA_SERIALIZERS;
    public static final Supplier<IForgeRegistry<EntityDataSerializer<?>>> ENTITY_DATA_SERIALIZERS;
    static final DeferredRegister<Codec<? extends IGlobalLootModifier>> DEFERRED_GLOBAL_LOOT_MODIFIER_SERIALIZERS;
    public static final Supplier<IForgeRegistry<Codec<? extends IGlobalLootModifier>>> GLOBAL_LOOT_MODIFIER_SERIALIZERS;
    static final DeferredRegister<Codec<? extends BiomeModifier>> DEFERRED_BIOME_MODIFIER_SERIALIZERS;
    public static final Supplier<IForgeRegistry<Codec<? extends BiomeModifier>>> BIOME_MODIFIER_SERIALIZERS;
    static final DeferredRegister<Codec<? extends StructureModifier>> DEFERRED_STRUCTURE_MODIFIER_SERIALIZERS;
    public static final Supplier<IForgeRegistry<Codec<? extends StructureModifier>>> STRUCTURE_MODIFIER_SERIALIZERS;
    static final DeferredRegister<FluidType> DEFERRED_FLUID_TYPES;
    public static final Supplier<IForgeRegistry<FluidType>> FLUID_TYPES;
    static final DeferredRegister<HolderSetType> DEFERRED_HOLDER_SET_TYPES;
    public static final Supplier<IForgeRegistry<HolderSetType>> HOLDER_SET_TYPES;
    static final DeferredRegister<ItemDisplayContext> DEFERRED_DISPLAY_CONTEXTS;
    public static final Supplier<IForgeRegistry<ItemDisplayContext>> DISPLAY_CONTEXTS;

    private static void init() {
        Keys.init();
        GameData.init();
        Bootstrap.m_135870_();
        Tags.init();
    }

    static {
        ForgeRegistries.init();
        BLOCKS = RegistryManager.ACTIVE.getRegistry(Keys.BLOCKS);
        FLUIDS = RegistryManager.ACTIVE.getRegistry(Keys.FLUIDS);
        ITEMS = RegistryManager.ACTIVE.getRegistry(Keys.ITEMS);
        MOB_EFFECTS = RegistryManager.ACTIVE.getRegistry(Keys.MOB_EFFECTS);
        SOUND_EVENTS = RegistryManager.ACTIVE.getRegistry(Keys.SOUND_EVENTS);
        POTIONS = RegistryManager.ACTIVE.getRegistry(Keys.POTIONS);
        ENCHANTMENTS = RegistryManager.ACTIVE.getRegistry(Keys.ENCHANTMENTS);
        ENTITY_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.ENTITY_TYPES);
        BLOCK_ENTITY_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.BLOCK_ENTITY_TYPES);
        PARTICLE_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.PARTICLE_TYPES);
        MENU_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.MENU_TYPES);
        PAINTING_VARIANTS = RegistryManager.ACTIVE.getRegistry(Keys.PAINTING_VARIANTS);
        RECIPE_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.RECIPE_TYPES);
        RECIPE_SERIALIZERS = RegistryManager.ACTIVE.getRegistry(Keys.RECIPE_SERIALIZERS);
        ATTRIBUTES = RegistryManager.ACTIVE.getRegistry(Keys.ATTRIBUTES);
        STAT_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.STAT_TYPES);
        COMMAND_ARGUMENT_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.COMMAND_ARGUMENT_TYPES);
        VILLAGER_PROFESSIONS = RegistryManager.ACTIVE.getRegistry(Keys.VILLAGER_PROFESSIONS);
        POI_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.POI_TYPES);
        MEMORY_MODULE_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.MEMORY_MODULE_TYPES);
        SENSOR_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.SENSOR_TYPES);
        SCHEDULES = RegistryManager.ACTIVE.getRegistry(Keys.SCHEDULES);
        ACTIVITIES = RegistryManager.ACTIVE.getRegistry(Keys.ACTIVITIES);
        WORLD_CARVERS = RegistryManager.ACTIVE.getRegistry(Keys.WORLD_CARVERS);
        FEATURES = RegistryManager.ACTIVE.getRegistry(Keys.FEATURES);
        CHUNK_STATUS = RegistryManager.ACTIVE.getRegistry(Keys.CHUNK_STATUS);
        BLOCK_STATE_PROVIDER_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.BLOCK_STATE_PROVIDER_TYPES);
        FOLIAGE_PLACER_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.FOLIAGE_PLACER_TYPES);
        TREE_DECORATOR_TYPES = RegistryManager.ACTIVE.getRegistry(Keys.TREE_DECORATOR_TYPES);
        BIOMES = RegistryManager.ACTIVE.getRegistry(Keys.BIOMES);
        DEFERRED_ENTITY_DATA_SERIALIZERS = DeferredRegister.create(Keys.ENTITY_DATA_SERIALIZERS, Keys.ENTITY_DATA_SERIALIZERS.m_135782_().m_135827_());
        ENTITY_DATA_SERIALIZERS = DEFERRED_ENTITY_DATA_SERIALIZERS.makeRegistry(GameData::getDataSerializersRegistryBuilder);
        DEFERRED_GLOBAL_LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS.m_135782_().m_135827_());
        GLOBAL_LOOT_MODIFIER_SERIALIZERS = DEFERRED_GLOBAL_LOOT_MODIFIER_SERIALIZERS.makeRegistry(GameData::getGLMSerializersRegistryBuilder);
        DEFERRED_BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(Keys.BIOME_MODIFIER_SERIALIZERS, Keys.BIOME_MODIFIER_SERIALIZERS.m_135782_().m_135827_());
        BIOME_MODIFIER_SERIALIZERS = DEFERRED_BIOME_MODIFIER_SERIALIZERS.makeRegistry(GameData::getBiomeModifierSerializersRegistryBuilder);
        DEFERRED_STRUCTURE_MODIFIER_SERIALIZERS = DeferredRegister.create(Keys.STRUCTURE_MODIFIER_SERIALIZERS, Keys.STRUCTURE_MODIFIER_SERIALIZERS.m_135782_().m_135827_());
        STRUCTURE_MODIFIER_SERIALIZERS = DEFERRED_STRUCTURE_MODIFIER_SERIALIZERS.makeRegistry(GameData::getStructureModifierSerializersRegistryBuilder);
        DEFERRED_FLUID_TYPES = DeferredRegister.create(Keys.FLUID_TYPES, Keys.FLUID_TYPES.m_135782_().m_135827_());
        FLUID_TYPES = DEFERRED_FLUID_TYPES.makeRegistry(GameData::getFluidTypeRegistryBuilder);
        DEFERRED_HOLDER_SET_TYPES = DeferredRegister.create(Keys.HOLDER_SET_TYPES, "forge");
        HOLDER_SET_TYPES = DEFERRED_HOLDER_SET_TYPES.makeRegistry(GameData::makeUnsavedAndUnsynced);
        DEFERRED_DISPLAY_CONTEXTS = DeferredRegister.create(Keys.DISPLAY_CONTEXTS, "forge");
        DISPLAY_CONTEXTS = DEFERRED_DISPLAY_CONTEXTS.makeRegistry(GameData::getItemDisplayContextRegistryBuilder);
    }

    public static final class Keys {
        public static final ResourceKey<Registry<Block>> BLOCKS = Keys.key("block");
        public static final ResourceKey<Registry<Fluid>> FLUIDS = Keys.key("fluid");
        public static final ResourceKey<Registry<Item>> ITEMS = Keys.key("item");
        public static final ResourceKey<Registry<MobEffect>> MOB_EFFECTS = Keys.key("mob_effect");
        public static final ResourceKey<Registry<Potion>> POTIONS = Keys.key("potion");
        public static final ResourceKey<Registry<Attribute>> ATTRIBUTES = Keys.key("attribute");
        public static final ResourceKey<Registry<StatType<?>>> STAT_TYPES = Keys.key("stat_type");
        public static final ResourceKey<Registry<ArgumentTypeInfo<?, ?>>> COMMAND_ARGUMENT_TYPES = Keys.key("command_argument_type");
        public static final ResourceKey<Registry<SoundEvent>> SOUND_EVENTS = Keys.key("sound_event");
        public static final ResourceKey<Registry<Enchantment>> ENCHANTMENTS = Keys.key("enchantment");
        public static final ResourceKey<Registry<EntityType<?>>> ENTITY_TYPES = Keys.key("entity_type");
        public static final ResourceKey<Registry<PaintingVariant>> PAINTING_VARIANTS = Keys.key("painting_variant");
        public static final ResourceKey<Registry<ParticleType<?>>> PARTICLE_TYPES = Keys.key("particle_type");
        public static final ResourceKey<Registry<MenuType<?>>> MENU_TYPES = Keys.key("menu");
        public static final ResourceKey<Registry<BlockEntityType<?>>> BLOCK_ENTITY_TYPES = Keys.key("block_entity_type");
        public static final ResourceKey<Registry<RecipeType<?>>> RECIPE_TYPES = Keys.key("recipe_type");
        public static final ResourceKey<Registry<RecipeSerializer<?>>> RECIPE_SERIALIZERS = Keys.key("recipe_serializer");
        public static final ResourceKey<Registry<VillagerProfession>> VILLAGER_PROFESSIONS = Keys.key("villager_profession");
        public static final ResourceKey<Registry<PoiType>> POI_TYPES = Keys.key("point_of_interest_type");
        public static final ResourceKey<Registry<MemoryModuleType<?>>> MEMORY_MODULE_TYPES = Keys.key("memory_module_type");
        public static final ResourceKey<Registry<SensorType<?>>> SENSOR_TYPES = Keys.key("sensor_type");
        public static final ResourceKey<Registry<Schedule>> SCHEDULES = Keys.key("schedule");
        public static final ResourceKey<Registry<Activity>> ACTIVITIES = Keys.key("activity");
        public static final ResourceKey<Registry<WorldCarver<?>>> WORLD_CARVERS = Keys.key("worldgen/carver");
        public static final ResourceKey<Registry<Feature<?>>> FEATURES = Keys.key("worldgen/feature");
        public static final ResourceKey<Registry<ChunkStatus>> CHUNK_STATUS = Keys.key("chunk_status");
        public static final ResourceKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPES = Keys.key("worldgen/block_state_provider_type");
        public static final ResourceKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPES = Keys.key("worldgen/foliage_placer_type");
        public static final ResourceKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPES = Keys.key("worldgen/tree_decorator_type");
        public static final ResourceKey<Registry<Biome>> BIOMES = Keys.key("worldgen/biome");
        public static final ResourceKey<Registry<EntityDataSerializer<?>>> ENTITY_DATA_SERIALIZERS = Keys.key("forge:entity_data_serializers");
        public static final ResourceKey<Registry<Codec<? extends IGlobalLootModifier>>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = Keys.key("forge:global_loot_modifier_serializers");
        public static final ResourceKey<Registry<Codec<? extends BiomeModifier>>> BIOME_MODIFIER_SERIALIZERS = Keys.key("forge:biome_modifier_serializers");
        public static final ResourceKey<Registry<Codec<? extends StructureModifier>>> STRUCTURE_MODIFIER_SERIALIZERS = Keys.key("forge:structure_modifier_serializers");
        public static final ResourceKey<Registry<FluidType>> FLUID_TYPES = Keys.key("forge:fluid_type");
        public static final ResourceKey<Registry<HolderSetType>> HOLDER_SET_TYPES = Keys.key("forge:holder_set_type");
        public static final ResourceKey<Registry<ItemDisplayContext>> DISPLAY_CONTEXTS = Keys.key("forge:display_contexts");
        public static final ResourceKey<Registry<BiomeModifier>> BIOME_MODIFIERS = Keys.key("forge:biome_modifier");
        public static final ResourceKey<Registry<StructureModifier>> STRUCTURE_MODIFIERS = Keys.key("forge:structure_modifier");

        private static <T> ResourceKey<Registry<T>> key(String name) {
            return ResourceKey.m_135788_((ResourceLocation)new ResourceLocation(name));
        }

        private static void init() {
        }
    }
}

