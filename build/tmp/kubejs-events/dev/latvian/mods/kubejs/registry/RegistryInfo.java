/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  dev.architectury.registry.registries.Registrar
 *  dev.architectury.registry.registries.RegistrarManager
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo
 *  net.minecraft.core.Registry
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.ChatType
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.stats.StatType
 *  net.minecraft.util.valueproviders.FloatProviderType
 *  net.minecraft.util.valueproviders.IntProviderType
 *  net.minecraft.world.damagesource.DamageType
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.entity.ai.sensing.SensorType
 *  net.minecraft.world.entity.ai.village.poi.PoiType
 *  net.minecraft.world.entity.animal.CatVariant
 *  net.minecraft.world.entity.animal.FrogVariant
 *  net.minecraft.world.entity.decoration.PaintingVariant
 *  net.minecraft.world.entity.npc.VillagerProfession
 *  net.minecraft.world.entity.npc.VillagerType
 *  net.minecraft.world.entity.schedule.Activity
 *  net.minecraft.world.entity.schedule.Schedule
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.Instrument
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.alchemy.Potion
 *  net.minecraft.world.item.armortrim.TrimMaterial
 *  net.minecraft.world.item.armortrim.TrimPattern
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BannerPattern
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.chunk.ChunkStatus
 *  net.minecraft.world.level.dimension.DimensionType
 *  net.minecraft.world.level.dimension.LevelStem
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.minecraft.world.level.gameevent.PositionSourceType
 *  net.minecraft.world.level.levelgen.DensityFunction
 *  net.minecraft.world.level.levelgen.NoiseGeneratorSettings
 *  net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType
 *  net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver
 *  net.minecraft.world.level.levelgen.carver.WorldCarver
 *  net.minecraft.world.level.levelgen.feature.ConfiguredFeature
 *  net.minecraft.world.level.levelgen.feature.Feature
 *  net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType
 *  net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType
 *  net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType
 *  net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType
 *  net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType
 *  net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType
 *  net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset
 *  net.minecraft.world.level.levelgen.heightproviders.HeightProviderType
 *  net.minecraft.world.level.levelgen.placement.PlacedFeature
 *  net.minecraft.world.level.levelgen.placement.PlacementModifierType
 *  net.minecraft.world.level.levelgen.presets.WorldPreset
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.level.levelgen.structure.StructureSet
 *  net.minecraft.world.level.levelgen.structure.StructureType
 *  net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType
 *  net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType
 *  net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType
 *  net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
 *  net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType
 *  net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType
 *  net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType
 *  net.minecraft.world.level.levelgen.synth.NormalNoise$NoiseParameters
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.storage.loot.entries.LootPoolEntryType
 *  net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
 *  net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
 *  net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType
 *  net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType
 *  net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.BuilderFactory;
import dev.latvian.mods.kubejs.registry.BuilderType;
import dev.latvian.mods.kubejs.registry.RegistryCallback;
import dev.latvian.mods.kubejs.registry.RegistryEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RegistryInfo<T>
implements Iterable<BuilderBase<? extends T>>,
TypeWrapperFactory<T> {
    public static final Map<ResourceKey<? extends Registry<?>>, RegistryInfo<?>> MAP = Collections.synchronizedMap(new LinkedHashMap());
    public static final List<BuilderBase<?>> ALL_BUILDERS = new LinkedList();
    @Info(value="Platform-agnostic wrapper of minecraft registries, can be used to register content or get objects from the registry")
    private static final RegistrarManager REGISTRIES = RegistrarManager.get((String)"kubejs");
    public static final RegistryInfo<SoundEvent> SOUND_EVENT = RegistryInfo.of(Registries.f_256840_, SoundEvent.class);
    public static final RegistryInfo<Fluid> FLUID = RegistryInfo.of(Registries.f_256808_, Fluid.class);
    public static final RegistryInfo<MobEffect> MOB_EFFECT = RegistryInfo.of(Registries.f_256929_, MobEffect.class).languageKeyPrefix("effect");
    public static final RegistryInfo<Block> BLOCK = RegistryInfo.of(Registries.f_256747_, Block.class);
    public static final RegistryInfo<Enchantment> ENCHANTMENT = RegistryInfo.of(Registries.f_256762_, Enchantment.class);
    public static final RegistryInfo<EntityType> ENTITY_TYPE = RegistryInfo.of(Registries.f_256939_, EntityType.class);
    public static final RegistryInfo<Item> ITEM = RegistryInfo.of(Registries.f_256913_, Item.class).noAutoWrap();
    public static final RegistryInfo<Potion> POTION = RegistryInfo.of(Registries.f_256973_, Potion.class);
    public static final RegistryInfo<ParticleType> PARTICLE_TYPE = RegistryInfo.of(Registries.f_256890_, ParticleType.class);
    public static final RegistryInfo<BlockEntityType> BLOCK_ENTITY_TYPE = RegistryInfo.of(Registries.f_256922_, BlockEntityType.class);
    public static final RegistryInfo<PaintingVariant> PAINTING_VARIANT = RegistryInfo.of(Registries.f_256836_, PaintingVariant.class);
    public static final RegistryInfo<ResourceLocation> CUSTOM_STAT = RegistryInfo.of(Registries.f_256887_, ResourceLocation.class);
    public static final RegistryInfo<ChunkStatus> CHUNK_STATUS = RegistryInfo.of(Registries.f_256755_, ChunkStatus.class);
    public static final RegistryInfo<RuleTestType> RULE_TEST = RegistryInfo.of(Registries.f_256947_, RuleTestType.class);
    public static final RegistryInfo<PosRuleTestType> POS_RULE_TEST = RegistryInfo.of(Registries.f_257009_, PosRuleTestType.class);
    public static final RegistryInfo<MenuType> MENU = RegistryInfo.of(Registries.f_256798_, MenuType.class);
    public static final RegistryInfo<RecipeType> RECIPE_TYPE = RegistryInfo.of(Registries.f_256954_, RecipeType.class);
    public static final RegistryInfo<RecipeSerializer> RECIPE_SERIALIZER = RegistryInfo.of(Registries.f_256764_, RecipeSerializer.class);
    public static final RegistryInfo<Attribute> ATTRIBUTE = RegistryInfo.of(Registries.f_256728_, Attribute.class);
    public static final RegistryInfo<GameEvent> GAME_EVENT = RegistryInfo.of(Registries.f_256827_, GameEvent.class);
    public static final RegistryInfo<PositionSourceType> POSITION_SOURCE_TYPE = RegistryInfo.of(Registries.f_256792_, PositionSourceType.class);
    public static final RegistryInfo<StatType> STAT_TYPE = RegistryInfo.of(Registries.f_256849_, StatType.class);
    public static final RegistryInfo<VillagerType> VILLAGER_TYPE = RegistryInfo.of(Registries.f_257019_, VillagerType.class);
    public static final RegistryInfo<VillagerProfession> VILLAGER_PROFESSION = RegistryInfo.of(Registries.f_256749_, VillagerProfession.class);
    public static final RegistryInfo<PoiType> POINT_OF_INTEREST_TYPE = RegistryInfo.of(Registries.f_256805_, PoiType.class);
    public static final RegistryInfo<MemoryModuleType> MEMORY_MODULE_TYPE = RegistryInfo.of(Registries.f_257023_, MemoryModuleType.class);
    public static final RegistryInfo<SensorType> SENSOR_TYPE = RegistryInfo.of(Registries.f_256937_, SensorType.class);
    public static final RegistryInfo<Schedule> SCHEDULE = RegistryInfo.of(Registries.f_256723_, Schedule.class);
    public static final RegistryInfo<Activity> ACTIVITY = RegistryInfo.of(Registries.f_257025_, Activity.class);
    public static final RegistryInfo<LootPoolEntryType> LOOT_ENTRY = RegistryInfo.of(Registries.f_257032_, LootPoolEntryType.class);
    public static final RegistryInfo<LootItemFunctionType> LOOT_FUNCTION = RegistryInfo.of(Registries.f_257015_, LootItemFunctionType.class);
    public static final RegistryInfo<LootItemConditionType> LOOT_ITEM = RegistryInfo.of(Registries.f_256976_, LootItemConditionType.class);
    public static final RegistryInfo<LootNumberProviderType> LOOT_NUMBER_PROVIDER = RegistryInfo.of(Registries.f_256829_, LootNumberProviderType.class);
    public static final RegistryInfo<LootNbtProviderType> LOOT_NBT_PROVIDER = RegistryInfo.of(Registries.f_256871_, LootNbtProviderType.class);
    public static final RegistryInfo<LootScoreProviderType> LOOT_SCORE_PROVIDER = RegistryInfo.of(Registries.f_256924_, LootScoreProviderType.class);
    public static final RegistryInfo<ArgumentTypeInfo> COMMAND_ARGUMENT_TYPE = RegistryInfo.of(Registries.f_256982_, ArgumentTypeInfo.class);
    public static final RegistryInfo<DimensionType> DIMENSION_TYPE = RegistryInfo.of(Registries.f_256787_, DimensionType.class);
    public static final RegistryInfo<Level> DIMENSION = RegistryInfo.of(Registries.f_256858_, Level.class);
    public static final RegistryInfo<LevelStem> LEVEL_STEM = RegistryInfo.of(Registries.f_256862_, LevelStem.class);
    public static final RegistryInfo<FloatProviderType> FLOAT_PROVIDER_TYPE = RegistryInfo.of(Registries.f_256892_, FloatProviderType.class);
    public static final RegistryInfo<IntProviderType> INT_PROVIDER_TYPE = RegistryInfo.of(Registries.f_256949_, IntProviderType.class);
    public static final RegistryInfo<HeightProviderType> HEIGHT_PROVIDER_TYPE = RegistryInfo.of(Registries.f_256757_, HeightProviderType.class);
    public static final RegistryInfo<BlockPredicateType> BLOCK_PREDICATE_TYPE = RegistryInfo.of(Registries.f_256774_, BlockPredicateType.class);
    public static final RegistryInfo<NoiseGeneratorSettings> NOISE_GENERATOR_SETTINGS = RegistryInfo.of(Registries.f_256932_, NoiseGeneratorSettings.class);
    public static final RegistryInfo<ConfiguredWorldCarver> CONFIGURED_CARVER = RegistryInfo.of(Registries.f_257003_, ConfiguredWorldCarver.class);
    public static final RegistryInfo<ConfiguredFeature> CONFIGURED_FEATURE = RegistryInfo.of(Registries.f_256911_, ConfiguredFeature.class);
    public static final RegistryInfo<PlacedFeature> PLACED_FEATURE = RegistryInfo.of(Registries.f_256988_, PlacedFeature.class);
    public static final RegistryInfo<Structure> STRUCTURE = RegistryInfo.of(Registries.f_256944_, Structure.class);
    public static final RegistryInfo<StructureSet> STRUCTURE_SET = RegistryInfo.of(Registries.f_256998_, StructureSet.class);
    public static final RegistryInfo<StructureProcessorList> PROCESSOR_LIST = RegistryInfo.of(Registries.f_257011_, StructureProcessorList.class);
    public static final RegistryInfo<StructureTemplatePool> TEMPLATE_POOL = RegistryInfo.of(Registries.f_256948_, StructureTemplatePool.class);
    public static final RegistryInfo<Biome> BIOME = RegistryInfo.of(Registries.f_256952_, Biome.class);
    public static final RegistryInfo<NormalNoise.NoiseParameters> NOISE = RegistryInfo.of(Registries.f_256865_, NormalNoise.NoiseParameters.class);
    public static final RegistryInfo<DensityFunction> DENSITY_FUNCTION = RegistryInfo.of(Registries.f_257040_, DensityFunction.class);
    public static final RegistryInfo<WorldPreset> WORLD_PRESET = RegistryInfo.of(Registries.f_256729_, WorldPreset.class);
    public static final RegistryInfo<FlatLevelGeneratorPreset> FLAT_LEVEL_GENERATOR_PRESET = RegistryInfo.of(Registries.f_256724_, FlatLevelGeneratorPreset.class);
    public static final RegistryInfo<WorldCarver> CARVER = RegistryInfo.of(Registries.f_256812_, WorldCarver.class);
    public static final RegistryInfo<Feature> FEATURE = RegistryInfo.of(Registries.f_256833_, Feature.class);
    public static final RegistryInfo<StructurePlacementType> STRUCTURE_PLACEMENT_TYPE = RegistryInfo.of(Registries.f_256888_, StructurePlacementType.class);
    public static final RegistryInfo<StructurePieceType> STRUCTURE_PIECE = RegistryInfo.of(Registries.f_256786_, StructurePieceType.class);
    public static final RegistryInfo<StructureType> STRUCTURE_TYPE = RegistryInfo.of(Registries.f_256938_, StructureType.class);
    public static final RegistryInfo<PlacementModifierType> PLACEMENT_MODIFIER = RegistryInfo.of(Registries.f_256843_, PlacementModifierType.class);
    public static final RegistryInfo<BlockStateProviderType> BLOCK_STATE_PROVIDER_TYPE = RegistryInfo.of(Registries.f_256891_, BlockStateProviderType.class);
    public static final RegistryInfo<FoliagePlacerType> FOLIAGE_PLACER_TYPE = RegistryInfo.of(Registries.f_256905_, FoliagePlacerType.class);
    public static final RegistryInfo<TrunkPlacerType> TRUNK_PLACER_TYPE = RegistryInfo.of(Registries.f_256963_, TrunkPlacerType.class);
    public static final RegistryInfo<TreeDecoratorType> TREE_DECORATOR_TYPE = RegistryInfo.of(Registries.f_256845_, TreeDecoratorType.class);
    public static final RegistryInfo<RootPlacerType> ROOT_PLACER_TYPE = RegistryInfo.of(Registries.f_256768_, RootPlacerType.class);
    public static final RegistryInfo<FeatureSizeType> FEATURE_SIZE_TYPE = RegistryInfo.of(Registries.f_256720_, FeatureSizeType.class);
    public static final RegistryInfo<Codec> BIOME_SOURCE = RegistryInfo.of(Registries.f_256826_, Codec.class);
    public static final RegistryInfo<Codec> CHUNK_GENERATOR = RegistryInfo.of(Registries.f_256783_, Codec.class);
    public static final RegistryInfo<Codec> CONDITION = RegistryInfo.of(Registries.f_256793_, Codec.class);
    public static final RegistryInfo<Codec> RULE = RegistryInfo.of(Registries.f_256815_, Codec.class);
    public static final RegistryInfo<Codec> DENSITY_FUNCTION_TYPE = RegistryInfo.of(Registries.f_256746_, Codec.class);
    public static final RegistryInfo<StructureProcessorType> STRUCTURE_PROCESSOR = RegistryInfo.of(Registries.f_256983_, StructureProcessorType.class);
    public static final RegistryInfo<StructurePoolElementType> STRUCTURE_POOL_ELEMENT = RegistryInfo.of(Registries.f_257024_, StructurePoolElementType.class);
    public static final RegistryInfo<ChatType> CHAT_TYPE = RegistryInfo.of(Registries.f_256873_, ChatType.class);
    public static final RegistryInfo<CatVariant> CAT_VARIANT = RegistryInfo.of(Registries.f_257006_, CatVariant.class);
    public static final RegistryInfo<FrogVariant> FROG_VARIANT = RegistryInfo.of(Registries.f_256732_, FrogVariant.class);
    public static final RegistryInfo<BannerPattern> BANNER_PATTERN = RegistryInfo.of(Registries.f_256969_, BannerPattern.class);
    public static final RegistryInfo<Instrument> INSTRUMENT = RegistryInfo.of(Registries.f_257010_, Instrument.class);
    public static final RegistryInfo<TrimMaterial> TRIM_MATERIAL = RegistryInfo.of(Registries.f_266076_, TrimMaterial.class);
    public static final RegistryInfo<TrimPattern> TRIM_PATTERN = RegistryInfo.of(Registries.f_266063_, TrimPattern.class);
    public static final RegistryInfo<CreativeModeTab> CREATIVE_MODE_TAB = RegistryInfo.of(Registries.f_279569_, CreativeModeTab.class);
    public static final RegistryInfo<DamageType> DAMAGE_TYPE = RegistryInfo.of(Registries.f_268580_, DamageType.class);
    public static final RegistryInfo<LootItemConditionType> LOOT_CONDITION_TYPE = RegistryInfo.of(Registries.f_256976_, LootItemConditionType.class);
    public static final RegistryInfo<LootItemFunctionType> LOOT_FUNCTION_TYPE = RegistryInfo.of(Registries.f_257015_, LootItemFunctionType.class);
    public static final RegistryInfo<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = RegistryInfo.of(Registries.f_256871_, LootNbtProviderType.class);
    public static final RegistryInfo<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = RegistryInfo.of(Registries.f_256829_, LootNumberProviderType.class);
    public static final RegistryInfo<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = RegistryInfo.of(Registries.f_257032_, LootPoolEntryType.class);
    public static final RegistryInfo<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = RegistryInfo.of(Registries.f_256924_, LootScoreProviderType.class);
    public static final RegistryInfo<Codec> MATERIAL_CONDITION = RegistryInfo.of(Registries.f_256793_, Codec.class);
    public static final RegistryInfo<Codec> MATERIAL_RULE = RegistryInfo.of(Registries.f_256815_, Codec.class);
    public static final RegistryInfo<PlacementModifierType> PLACEMENT_MODIFIER_TYPE = RegistryInfo.of(Registries.f_256843_, PlacementModifierType.class);
    public static final RegistryInfo<RuleBlockEntityModifierType> RULE_BLOCK_ENTITY_MODIFIER = RegistryInfo.of(Registries.f_276428_, RuleBlockEntityModifierType.class);
    public static final RegistryInfo<StructurePlacementType> STRUCTURE_PLACEMENT = RegistryInfo.of(Registries.f_256888_, StructurePlacementType.class);
    public static final RegistryInfo<String> DECORATED_POT_PATTERNS = RegistryInfo.of(Registries.f_271200_, String.class);
    public static final RegistryInfo<NoiseGeneratorSettings> NOISE_SETTINGS = RegistryInfo.of(Registries.f_256932_, NoiseGeneratorSettings.class);
    public static final RegistryInfo<MultiNoiseBiomeSourceParameterList> MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = RegistryInfo.of(Registries.f_273919_, MultiNoiseBiomeSourceParameterList.class);
    public static final LinkedList<RegistryInfo<?>> AFTER_VANILLA = new LinkedList();
    public final ResourceKey<? extends Registry<T>> key;
    public final Class<?> objectBaseClass;
    public final Map<String, BuilderType<T>> types;
    public final Map<ResourceLocation, BuilderBase<? extends T>> objects;
    public boolean hasDefaultTags = false;
    private BuilderType<T> defaultType;
    public boolean bypassServerOnly;
    public boolean autoWrap;
    private Registrar<T> architecturyRegistrar;
    public String languageKeyPrefix;

    public static <T> RegistryInfo<T> of(ResourceKey<? extends Registry<?>> key, Class<T> type) {
        RegistryInfo<?> r = MAP.get(key);
        if (r == null) {
            RegistryInfo<T> reg = new RegistryInfo<T>((ResourceKey)UtilsJS.cast(key), type);
            MAP.put(key, reg);
            return reg;
        }
        return r;
    }

    public static RegistryInfo<?> of(ResourceKey<? extends Registry<?>> key) {
        return RegistryInfo.of((ResourceKey)UtilsJS.cast(key), Object.class);
    }

    private RegistryInfo(ResourceKey<? extends Registry<T>> key, Class<T> objectBaseClass) {
        this.key = key;
        this.objectBaseClass = objectBaseClass;
        this.types = new LinkedHashMap<String, BuilderType<T>>();
        this.objects = new LinkedHashMap<ResourceLocation, BuilderBase<? extends T>>();
        this.bypassServerOnly = false;
        this.autoWrap = objectBaseClass != Codec.class && objectBaseClass != ResourceLocation.class && objectBaseClass != String.class;
        this.languageKeyPrefix = key.m_135782_().m_135815_().replace('/', '.');
    }

    public RegistryInfo<T> bypassServerOnly() {
        this.bypassServerOnly = true;
        return this;
    }

    public RegistryInfo<T> noAutoWrap() {
        this.autoWrap = false;
        return this;
    }

    public RegistryInfo<T> languageKeyPrefix(String prefix) {
        this.languageKeyPrefix = prefix;
        return this;
    }

    public void addType(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory, boolean isDefault) {
        BuilderType b = new BuilderType(type, builderType, factory);
        this.types.put(type, b);
        if (isDefault) {
            if (this.defaultType != null) {
                ConsoleJS.STARTUP.warn("Previous default type '" + this.defaultType.type() + "' for registry '" + String.valueOf(this.key.m_135782_()) + "' replaced with '" + type + "'!");
            }
            this.defaultType = b;
        }
    }

    public void addType(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
        this.addType(type, builderType, factory, type.equals("basic"));
    }

    public void addBuilder(BuilderBase<? extends T> builder) {
        if (builder == null) {
            throw new IllegalArgumentException("Can't add null builder in registry '" + String.valueOf(this.key.m_135782_()) + "'!");
        }
        if (DevProperties.get().debugInfo) {
            ConsoleJS.STARTUP.info("~ " + String.valueOf(this.key.m_135782_()) + " | " + String.valueOf(builder.id));
        }
        if (this.objects.containsKey(builder.id)) {
            throw new IllegalArgumentException("Duplicate key '" + String.valueOf(builder.id) + "' in registry '" + String.valueOf(this.key.m_135782_()) + "'!");
        }
        this.objects.put(builder.id, builder);
        ALL_BUILDERS.add(builder);
    }

    @Nullable
    public BuilderType getDefaultType() {
        if (this.types.isEmpty()) {
            return null;
        }
        if (this.defaultType == null) {
            this.defaultType = this.types.values().iterator().next();
        }
        return this.defaultType;
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof RegistryInfo)) return false;
        RegistryInfo ri = (RegistryInfo)obj;
        if (!this.key.equals(ri.key)) return false;
        return true;
    }

    public String toString() {
        return this.key.m_135782_().toString();
    }

    public int registerObjects(RegistryCallback<T> function) {
        if (DevProperties.get().debugInfo) {
            if (this.objects.isEmpty()) {
                KubeJS.LOGGER.info("Skipping " + String.valueOf(this) + " registry");
            } else {
                KubeJS.LOGGER.info("Building " + this.objects.size() + " objects of " + String.valueOf(this) + " registry");
            }
        }
        if (this.objects.isEmpty()) {
            return 0;
        }
        int added = 0;
        for (BuilderBase<T> builder : this) {
            if (builder.dummyBuilder) continue;
            if (!builder.getRegistryType().bypassServerOnly) {
                if (CommonProperties.get().serverOnly) continue;
            }
            function.accept(builder.id, builder::createTransformedObject);
            if (DevProperties.get().debugInfo) {
                ConsoleJS.STARTUP.info("+ " + String.valueOf(this) + " | " + String.valueOf(builder.id));
            }
            ++added;
        }
        if (!this.objects.isEmpty() && DevProperties.get().debugInfo) {
            KubeJS.LOGGER.info("Registered " + added + "/" + this.objects.size() + " objects of " + String.valueOf(this));
        }
        return added;
    }

    @Override
    @NotNull
    public Iterator<BuilderBase<? extends T>> iterator() {
        return this.objects.values().iterator();
    }

    public Registrar<T> getArchitecturyRegistrar() {
        if (this.architecturyRegistrar == null) {
            this.architecturyRegistrar = REGISTRIES.get(this.key);
        }
        return this.architecturyRegistrar;
    }

    public Registry<T> getVanillaRegistry() {
        return (Registry)BuiltInRegistries.f_257047_.m_6246_(this.key);
    }

    public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
        return this.getArchitecturyRegistrar().entrySet();
    }

    public ResourceLocation getId(T value) {
        return this.getArchitecturyRegistrar().getId(value);
    }

    public T getValue(ResourceLocation id) {
        return (T)this.getArchitecturyRegistrar().get(id);
    }

    public boolean hasValue(ResourceLocation id) {
        return this.getArchitecturyRegistrar().contains(id);
    }

    public T wrap(Context cx, Object o) {
        if (o == null) {
            return null;
        }
        if (this.objectBaseClass.isInstance(o)) {
            return (T)o;
        }
        ResourceLocation id = UtilsJS.getMCID(cx, o);
        T value = this.getValue(id);
        if (value == null) {
            NullPointerException npe = new NullPointerException("No such element with id %s in registry %s!".formatted(id, this));
            ConsoleJS.getCurrent(cx).error("Error while wrapping registry element type!", npe);
            throw npe;
        }
        return value;
    }

    public void fireRegistryEvent() {
        RegistryEventJS event = new RegistryEventJS(this);
        StartupEvents.REGISTRY.post(event, this.key);
        event.created.forEach(BuilderBase::createAdditionalObjects);
    }
}

