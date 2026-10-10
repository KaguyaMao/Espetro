/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.DetectedVersion
 *  net.minecraft.WorldVersion
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BiomeColors
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo
 *  net.minecraft.commands.synchronization.ArgumentTypeInfos
 *  net.minecraft.commands.synchronization.SingletonArgumentInfo
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.RegistryCodecs
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.metadata.PackMetadataGenerator
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.metadata.pack.PackMetadataSection
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.ExtraCodecs$EitherCodec
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.RangedAttribute
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData
 *  net.minecraft.world.level.levelgen.GenerationStep$Decoration
 *  net.minecraft.world.level.levelgen.placement.PlacedFeature
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.pathfinder.BlockPathTypes
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.fml.CrashReportCallables
 *  net.minecraftforge.fml.IExtensionPoint$DisplayTest
 *  net.minecraftforge.fml.StartupMessageManager
 *  net.minecraftforge.fml.VersionChecker
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.config.IConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.DetectedVersion;
import net.minecraft.WorldVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.VillagerTradingManager;
import net.minecraftforge.common.WorldWorkerManager;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.FalseCondition;
import net.minecraftforge.common.crafting.conditions.ItemExistsCondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBiomeTagsProvider;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.data.ForgeEntityTypeTagsProvider;
import net.minecraftforge.common.data.ForgeFluidTagsProvider;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.common.data.ForgeLootTableProvider;
import net.minecraftforge.common.data.ForgeRecipeProvider;
import net.minecraftforge.common.data.ForgeSpriteSourceProvider;
import net.minecraftforge.common.data.VanillaSoundDefinitionsProvider;
import net.minecraftforge.common.loot.CanToolPerformAction;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.common.world.NoneBiomeModifier;
import net.minecraftforge.common.world.NoneStructureModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.CrashReportCallables;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.snapshots.ForgeSnapshotsMod;
import net.minecraftforge.network.filters.VanillaPacketSplitter;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeDeferredRegistriesSetup;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.IdMappingEvent;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.holdersets.AndHolderSet;
import net.minecraftforge.registries.holdersets.AnyHolderSet;
import net.minecraftforge.registries.holdersets.HolderSetType;
import net.minecraftforge.registries.holdersets.NotHolderSet;
import net.minecraftforge.registries.holdersets.OrHolderSet;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContextKey;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

@Mod(value="forge")
public class ForgeMod {
    public static final String VERSION_CHECK_CAT = "version_checking";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FORGEMOD = MarkerManager.getMarker((String)"FORGEMOD");
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.Keys.ATTRIBUTES, "forge");
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.f_256982_, "forge");
    private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, "forge");
    private static final DeferredRegister<Codec<? extends StructureModifier>> STRUCTURE_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, "forge");
    private static final DeferredRegister<HolderSetType> HOLDER_SET_TYPES = DeferredRegister.create(ForgeRegistries.Keys.HOLDER_SET_TYPES, "forge");
    private static final RegistryObject<EnumArgument.Info> ENUM_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("enum", () -> (EnumArgument.Info)ArgumentTypeInfos.registerByClass(EnumArgument.class, new EnumArgument.Info()));
    private static final RegistryObject<SingletonArgumentInfo<ModIdArgument>> MODID_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("modid", () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(ModIdArgument.class, (ArgumentTypeInfo)SingletonArgumentInfo.m_235451_(ModIdArgument::modIdArgument)));
    public static final RegistryObject<Attribute> SWIM_SPEED = ATTRIBUTES.register("swim_speed", () -> new RangedAttribute("forge.swim_speed", 1.0, 0.0, 1024.0).m_22084_(true));
    public static final RegistryObject<Attribute> NAMETAG_DISTANCE = ATTRIBUTES.register("nametag_distance", () -> new RangedAttribute("forge.name_tag_distance", 64.0, 0.0, 64.0).m_22084_(true));
    public static final RegistryObject<Attribute> ENTITY_GRAVITY = ATTRIBUTES.register("entity_gravity", () -> new RangedAttribute("forge.entity_gravity", 0.08, -8.0, 8.0).m_22084_(true));
    public static final RegistryObject<Attribute> BLOCK_REACH = ATTRIBUTES.register("block_reach", () -> new RangedAttribute("forge.block_reach", 4.5, 0.0, 1024.0).m_22084_(true));
    public static final RegistryObject<Attribute> ENTITY_REACH = ATTRIBUTES.register("entity_reach", () -> new RangedAttribute("forge.entity_reach", 3.0, 0.0, 1024.0).m_22084_(true));
    public static final RegistryObject<Attribute> STEP_HEIGHT_ADDITION = ATTRIBUTES.register("step_height_addition", () -> new RangedAttribute("forge.step_height", 0.0, -512.0, 512.0).m_22084_(true));
    public static final RegistryObject<Codec<NoneBiomeModifier>> NONE_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("none", () -> Codec.unit((Object)NoneBiomeModifier.INSTANCE));
    public static final RegistryObject<Codec<ForgeBiomeModifiers.AddFeaturesBiomeModifier>> ADD_FEATURES_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("add_features", () -> RecordCodecBuilder.create(builder -> builder.group((App)Biome.f_47432_.fieldOf("biomes").forGetter(ForgeBiomeModifiers.AddFeaturesBiomeModifier::biomes), (App)PlacedFeature.f_191774_.fieldOf("features").forGetter(ForgeBiomeModifiers.AddFeaturesBiomeModifier::features), (App)GenerationStep.Decoration.f_224188_.fieldOf("step").forGetter(ForgeBiomeModifiers.AddFeaturesBiomeModifier::step)).apply((Applicative)builder, ForgeBiomeModifiers.AddFeaturesBiomeModifier::new)));
    public static final RegistryObject<Codec<ForgeBiomeModifiers.RemoveFeaturesBiomeModifier>> REMOVE_FEATURES_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("remove_features", () -> RecordCodecBuilder.create(builder -> builder.group((App)Biome.f_47432_.fieldOf("biomes").forGetter(ForgeBiomeModifiers.RemoveFeaturesBiomeModifier::biomes), (App)PlacedFeature.f_191774_.fieldOf("features").forGetter(ForgeBiomeModifiers.RemoveFeaturesBiomeModifier::features), (App)new ExtraCodecs.EitherCodec(GenerationStep.Decoration.f_224188_.listOf(), GenerationStep.Decoration.f_224188_).xmap(either -> (Set)either.map(Set::copyOf, Set::of), set -> set.size() == 1 ? Either.right((Object)((GenerationStep.Decoration[])set.toArray(GenerationStep.Decoration[]::new))[0]) : Either.left(List.copyOf(set))).optionalFieldOf("steps", EnumSet.allOf(GenerationStep.Decoration.class)).forGetter(ForgeBiomeModifiers.RemoveFeaturesBiomeModifier::steps)).apply((Applicative)builder, ForgeBiomeModifiers.RemoveFeaturesBiomeModifier::new)));
    public static final RegistryObject<Codec<ForgeBiomeModifiers.AddSpawnsBiomeModifier>> ADD_SPAWNS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("add_spawns", () -> RecordCodecBuilder.create(builder -> builder.group((App)Biome.f_47432_.fieldOf("biomes").forGetter(ForgeBiomeModifiers.AddSpawnsBiomeModifier::biomes), (App)new ExtraCodecs.EitherCodec(MobSpawnSettings.SpawnerData.f_48403_.listOf(), MobSpawnSettings.SpawnerData.f_48403_).xmap(either -> (List)either.map(Function.identity(), List::of), list -> list.size() == 1 ? Either.right((Object)((MobSpawnSettings.SpawnerData)list.get(0))) : Either.left((Object)list)).fieldOf("spawners").forGetter(ForgeBiomeModifiers.AddSpawnsBiomeModifier::spawners)).apply((Applicative)builder, ForgeBiomeModifiers.AddSpawnsBiomeModifier::new)));
    public static final RegistryObject<Codec<ForgeBiomeModifiers.RemoveSpawnsBiomeModifier>> REMOVE_SPAWNS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("remove_spawns", () -> RecordCodecBuilder.create(builder -> builder.group((App)Biome.f_47432_.fieldOf("biomes").forGetter(ForgeBiomeModifiers.RemoveSpawnsBiomeModifier::biomes), (App)RegistryCodecs.m_206277_(ForgeRegistries.Keys.ENTITY_TYPES).fieldOf("entity_types").forGetter(ForgeBiomeModifiers.RemoveSpawnsBiomeModifier::entityTypes)).apply((Applicative)builder, ForgeBiomeModifiers.RemoveSpawnsBiomeModifier::new)));
    public static final RegistryObject<Codec<NoneStructureModifier>> NONE_STRUCTURE_MODIFIER_TYPE = STRUCTURE_MODIFIER_SERIALIZERS.register("none", () -> Codec.unit((Object)NoneStructureModifier.INSTANCE));
    public static final RegistryObject<HolderSetType> ANY_HOLDER_SET = HOLDER_SET_TYPES.register("any", () -> AnyHolderSet::codec);
    public static final RegistryObject<HolderSetType> AND_HOLDER_SET = HOLDER_SET_TYPES.register("and", () -> AndHolderSet::codec);
    public static final RegistryObject<HolderSetType> OR_HOLDER_SET = HOLDER_SET_TYPES.register("or", () -> OrHolderSet::codec);
    public static final RegistryObject<HolderSetType> NOT_HOLDER_SET = HOLDER_SET_TYPES.register("not", () -> NotHolderSet::codec);
    private static final DeferredRegister<FluidType> VANILLA_FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, "minecraft");
    public static final RegistryObject<FluidType> EMPTY_TYPE = VANILLA_FLUID_TYPES.register("empty", () -> new FluidType(FluidType.Properties.create().descriptionId("block.minecraft.air").motionScale(1.0).canPushEntity(false).canSwim(false).canDrown(false).fallDistanceModifier(1.0f).pathType(null).adjacentPathType(null).density(0).temperature(0).viscosity(0)){

        @Override
        public void setItemMovement(ItemEntity entity) {
            if (!entity.m_20068_()) {
                entity.m_20256_(entity.m_20184_().m_82520_(0.0, -0.04, 0.0));
            }
        }
    });
    public static final RegistryObject<FluidType> WATER_TYPE = VANILLA_FLUID_TYPES.register("water", () -> new FluidType(FluidType.Properties.create().descriptionId("block.minecraft.water").fallDistanceModifier(0.0f).canExtinguish(true).canConvertToSource(true).supportsBoating(true).sound(SoundActions.BUCKET_FILL, SoundEvents.f_11781_).sound(SoundActions.BUCKET_EMPTY, SoundEvents.f_11778_).sound(SoundActions.FLUID_VAPORIZE, SoundEvents.f_11937_).canHydrate(true)){

        @Override
        @Nullable
        public BlockPathTypes getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog) {
            return canFluidLog ? super.getBlockPathType(state, level, pos, mob, true) : null;
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions(){
                private static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");
                private static final ResourceLocation WATER_STILL = new ResourceLocation("block/water_still");
                private static final ResourceLocation WATER_FLOW = new ResourceLocation("block/water_flow");
                private static final ResourceLocation WATER_OVERLAY = new ResourceLocation("block/water_overlay");

                @Override
                public ResourceLocation getStillTexture() {
                    return WATER_STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return WATER_FLOW;
                }

                @Override
                @Nullable
                public ResourceLocation getOverlayTexture() {
                    return WATER_OVERLAY;
                }

                @Override
                public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                    return UNDERWATER_LOCATION;
                }

                @Override
                public int getTintColor() {
                    return -12618012;
                }

                @Override
                public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                    return BiomeColors.m_108811_((BlockAndTintGetter)getter, (BlockPos)pos) | 0xFF000000;
                }
            });
        }
    });
    public static final RegistryObject<FluidType> LAVA_TYPE = VANILLA_FLUID_TYPES.register("lava", () -> new FluidType(FluidType.Properties.create().descriptionId("block.minecraft.lava").canSwim(false).canDrown(false).pathType(BlockPathTypes.LAVA).adjacentPathType(null).sound(SoundActions.BUCKET_FILL, SoundEvents.f_11783_).sound(SoundActions.BUCKET_EMPTY, SoundEvents.f_11780_).lightLevel(15).density(3000).viscosity(6000).temperature(1300)){

        @Override
        public double motionScale(Entity entity) {
            return entity.m_9236_().m_6042_().f_63857_() ? 0.007 : 0.0023333333333333335;
        }

        @Override
        public void setItemMovement(ItemEntity entity) {
            Vec3 vec3 = entity.m_20184_();
            entity.m_20334_(vec3.f_82479_ * (double)0.95f, vec3.f_82480_ + (double)(vec3.f_82480_ < (double)0.06f ? 5.0E-4f : 0.0f), vec3.f_82481_ * (double)0.95f);
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions(){
                private static final ResourceLocation LAVA_STILL = new ResourceLocation("block/lava_still");
                private static final ResourceLocation LAVA_FLOW = new ResourceLocation("block/lava_flow");

                @Override
                public ResourceLocation getStillTexture() {
                    return LAVA_STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return LAVA_FLOW;
                }
            });
        }
    });
    private static boolean enableMilkFluid = false;
    public static final RegistryObject<SoundEvent> BUCKET_EMPTY_MILK = RegistryObject.create(new ResourceLocation("item.bucket.empty_milk"), ForgeRegistries.SOUND_EVENTS);
    public static final RegistryObject<SoundEvent> BUCKET_FILL_MILK = RegistryObject.create(new ResourceLocation("item.bucket.fill_milk"), ForgeRegistries.SOUND_EVENTS);
    public static final RegistryObject<FluidType> MILK_TYPE = RegistryObject.createOptional(new ResourceLocation("milk"), ForgeRegistries.Keys.FLUID_TYPES.m_135782_(), "minecraft");
    public static final RegistryObject<Fluid> MILK = RegistryObject.create(new ResourceLocation("milk"), ForgeRegistries.FLUIDS);
    public static final RegistryObject<Fluid> FLOWING_MILK = RegistryObject.create(new ResourceLocation("flowing_milk"), ForgeRegistries.FLUIDS);
    private static ForgeMod INSTANCE;
    public static final PermissionNode<Boolean> USE_SELECTORS_PERMISSION;

    public static ForgeMod getInstance() {
        return INSTANCE;
    }

    public static void enableMilkFluid() {
        enableMilkFluid = true;
    }

    public ForgeMod(FMLJavaModLoadingContext context) {
        LOGGER.info(FORGEMOD, "Forge mod loading, version {}, for MC {} with MCP {}", (Object)ForgeVersion.getVersion(), (Object)MCPVersion.getMCVersion(), (Object)MCPVersion.getMCPVersion());
        ForgeSnapshotsMod.logStartupWarning();
        INSTANCE = this;
        MinecraftForge.initialize();
        CrashReportCallables.registerCrashCallable((String)"Crash Report UUID", () -> {
            UUID uuid = UUID.randomUUID();
            LOGGER.fatal("Preparing crash report with UUID {}", (Object)uuid);
            return uuid.toString();
        });
        CrashReportCallables.registerCrashCallable((String)"FML", ForgeVersion::getSpec);
        CrashReportCallables.registerCrashCallable((String)"Forge", () -> ForgeVersion.getGroup() + ":" + ForgeVersion.getVersion());
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(event -> {
            event.dataPackRegistry(ForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifier.DIRECT_CODEC);
            event.dataPackRegistry(ForgeRegistries.Keys.STRUCTURE_MODIFIERS, StructureModifier.DIRECT_CODEC);
        });
        modEventBus.addListener(this::preInit);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(this::registerFluids);
        modEventBus.addListener(this::registerVanillaDisplayContexts);
        modEventBus.addListener(this::registerRecipeSerializers);
        modEventBus.addListener(this::registerLootData);
        modEventBus.register((Object)this);
        ATTRIBUTES.register(modEventBus);
        COMMAND_ARGUMENT_TYPES.register(modEventBus);
        BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
        STRUCTURE_MODIFIER_SERIALIZERS.register(modEventBus);
        HOLDER_SET_TYPES.register(modEventBus);
        VANILLA_FLUID_TYPES.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
        MinecraftForge.EVENT_BUS.addListener(this::missingSoundMapping);
        context.registerConfig(ModConfig.Type.CLIENT, (IConfigSpec)ForgeConfig.clientSpec);
        context.registerConfig(ModConfig.Type.SERVER, (IConfigSpec)ForgeConfig.serverSpec);
        context.registerConfig(ModConfig.Type.COMMON, (IConfigSpec)ForgeConfig.commonSpec);
        modEventBus.register(ForgeConfig.class);
        ForgeDeferredRegistriesSetup.setup(modEventBus);
        context.registerDisplayTest(IExtensionPoint.DisplayTest.IGNORE_ALL_VERSION);
        StartupMessageManager.addModMessage((String)("Forge version " + ForgeVersion.getVersion()));
        MinecraftForge.EVENT_BUS.addListener(VillagerTradingManager::loadTrades);
        MinecraftForge.EVENT_BUS.register((Object)MinecraftForge.INTERNAL_HANDLER);
        MinecraftForge.EVENT_BUS.addListener(this::mappingChanged);
        MinecraftForge.EVENT_BUS.addListener(this::registerPermissionNodes);
        ForgeRegistries.ITEMS.tags().addOptionalTagDefaults(Tags.Items.ENCHANTING_FUELS, Set.of(ForgeRegistries.ITEMS.getDelegateOrThrow(Items.f_42534_)));
        ForgeMod.addAlias(ForgeRegistries.ATTRIBUTES, new ResourceLocation("forge", "reach_distance"), new ResourceLocation("forge", "block_reach"));
        ForgeMod.addAlias(ForgeRegistries.ATTRIBUTES, new ResourceLocation("forge", "attack_range"), new ResourceLocation("forge", "entity_reach"));
    }

    public void preInit(FMLCommonSetupEvent evt) {
        VersionChecker.startVersionCheck();
        VanillaPacketSplitter.register();
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
    }

    public void serverStopping(ServerStoppingEvent evt) {
        WorldWorkerManager.clear();
    }

    public void mappingChanged(IdMappingEvent evt) {
        Ingredient.invalidateAll();
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        gen.addProvider(true, (DataProvider)new PackMetadataGenerator(packOutput).m_247300_(PackMetadataSection.f_243696_, (Object)new PackMetadataSection((Component)Component.m_237115_((String)"pack.forge.description"), DetectedVersion.f_132476_.m_264084_(PackType.CLIENT_RESOURCES), Arrays.stream(PackType.values()).collect(Collectors.toMap(Function.identity(), arg_0 -> ((WorldVersion)DetectedVersion.f_132476_).m_264084_(arg_0))))));
        ForgeBlockTagsProvider blockTags = new ForgeBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        gen.addProvider(event.includeServer(), (DataProvider)blockTags);
        gen.addProvider(event.includeServer(), (DataProvider)new ForgeItemTagsProvider(packOutput, lookupProvider, blockTags.m_274426_(), existingFileHelper));
        gen.addProvider(event.includeServer(), (DataProvider)new ForgeEntityTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), (DataProvider)new ForgeFluidTagsProvider(packOutput, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), (DataProvider)new ForgeRecipeProvider(packOutput));
        gen.addProvider(event.includeServer(), (DataProvider)new ForgeLootTableProvider(packOutput));
        gen.addProvider(event.includeServer(), (DataProvider)new ForgeBiomeTagsProvider(packOutput, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeClient(), (DataProvider)new ForgeSpriteSourceProvider(packOutput, existingFileHelper));
        gen.addProvider(event.includeClient(), (DataProvider)new VanillaSoundDefinitionsProvider(packOutput, existingFileHelper));
    }

    public void missingSoundMapping(MissingMappingsEvent event) {
        if (event.getKey() != ForgeRegistries.Keys.SOUND_EVENTS) {
            return;
        }
        List<String> removedSounds = Arrays.asList("entity.parrot.imitate.panda", "entity.parrot.imitate.zombie_pigman", "entity.parrot.imitate.enderman", "entity.parrot.imitate.polar_bear", "entity.parrot.imitate.wolf");
        for (MissingMappingsEvent.Mapping<SoundEvent> mapping : event.getAllMappings(ForgeRegistries.Keys.SOUND_EVENTS)) {
            ResourceLocation regName = mapping.getKey();
            if (regName == null || !regName.m_135827_().equals("minecraft")) continue;
            String path = regName.m_135815_();
            if (!removedSounds.stream().anyMatch(s -> s.equals(path))) continue;
            LOGGER.info("Ignoring removed minecraft sound {}", (Object)regName);
            mapping.ignore();
        }
    }

    public void registerFluids(RegisterEvent event) {
        if (enableMilkFluid) {
            event.register(ForgeRegistries.Keys.SOUND_EVENTS, helper -> {
                helper.register(BUCKET_EMPTY_MILK.getId(), SoundEvent.m_262824_((ResourceLocation)BUCKET_EMPTY_MILK.getId()));
                helper.register(BUCKET_FILL_MILK.getId(), SoundEvent.m_262824_((ResourceLocation)BUCKET_FILL_MILK.getId()));
            });
            event.register(ForgeRegistries.Keys.FLUID_TYPES, helper -> helper.register(MILK_TYPE.getId(), new FluidType(FluidType.Properties.create().density(1024).viscosity(1024).sound(SoundActions.BUCKET_FILL, BUCKET_FILL_MILK.get()).sound(SoundActions.BUCKET_EMPTY, BUCKET_EMPTY_MILK.get())){

                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions(){
                        private static final ResourceLocation MILK_STILL = new ResourceLocation("forge", "block/milk_still");
                        private static final ResourceLocation MILK_FLOW = new ResourceLocation("forge", "block/milk_flowing");

                        @Override
                        public ResourceLocation getStillTexture() {
                            return MILK_STILL;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return MILK_FLOW;
                        }
                    });
                }
            }));
            event.register(ForgeRegistries.Keys.FLUIDS, helper -> {
                ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(MILK_TYPE, MILK, FLOWING_MILK).bucket(() -> Items.f_42455_);
                helper.register(MILK.getId(), new ForgeFlowingFluid.Source(properties));
                helper.register(FLOWING_MILK.getId(), new ForgeFlowingFluid.Flowing(properties));
            });
        }
    }

    public void registerVanillaDisplayContexts(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.DISPLAY_CONTEXTS)) {
            IForgeRegistryInternal forgeRegistry = (IForgeRegistryInternal)event.getForgeRegistry();
            if (forgeRegistry == null) {
                throw new IllegalStateException("Item display context was not a forge registry, wtf???");
            }
            Arrays.stream(ItemDisplayContext.values()).filter(Predicate.not(ItemDisplayContext::isModded)).forEach(ctx -> forgeRegistry.register(ctx.m_269462_(), new ResourceLocation("minecraft", ctx.m_7912_()), ctx));
        }
    }

    public void registerRecipeSerializers(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
            CraftingHelper.register(AndCondition.Serializer.INSTANCE);
            CraftingHelper.register(FalseCondition.Serializer.INSTANCE);
            CraftingHelper.register(ItemExistsCondition.Serializer.INSTANCE);
            CraftingHelper.register(ModLoadedCondition.Serializer.INSTANCE);
            CraftingHelper.register(NotCondition.Serializer.INSTANCE);
            CraftingHelper.register(OrCondition.Serializer.INSTANCE);
            CraftingHelper.register(TrueCondition.Serializer.INSTANCE);
            CraftingHelper.register(TagEmptyCondition.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "compound"), CompoundIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "nbt"), StrictNBTIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "partial_nbt"), PartialNBTIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "difference"), DifferenceIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "intersection"), IntersectionIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("minecraft", "item"), VanillaIngredientSerializer.INSTANCE);
            event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, new ResourceLocation("forge", "conditional"), ConditionalRecipe.Serializer::new);
        }
    }

    public void registerLootData(RegisterEvent event) {
        if (!event.getRegistryKey().equals((Object)Registries.f_256976_)) {
            return;
        }
        event.register(Registries.f_256976_, new ResourceLocation("forge:loot_table_id"), () -> LootTableIdCondition.LOOT_TABLE_ID);
        event.register(Registries.f_256976_, new ResourceLocation("forge:can_tool_perform_action"), () -> CanToolPerformAction.LOOT_CONDITION_TYPE);
    }

    public void registerPermissionNodes(PermissionGatherEvent.Nodes event) {
        event.addNodes(USE_SELECTORS_PERMISSION);
    }

    @Deprecated(forRemoval=true, since="1.20")
    private static <T> void addAlias(IForgeRegistry<T> registry, ResourceLocation from, ResourceLocation to) {
        ForgeRegistry fReg = (ForgeRegistry)registry;
        fReg.addAlias(from, to);
    }

    static {
        USE_SELECTORS_PERMISSION = new PermissionNode<Boolean>("forge", "use_entity_selectors", PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.m_20310_(2), new PermissionDynamicContextKey[0]);
    }
}

