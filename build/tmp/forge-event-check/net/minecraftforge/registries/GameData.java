/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Multimap
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  net.minecraft.core.IdMapper
 *  net.minecraft.core.MappedRegistry
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.SpawnPlacements
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.DefaultAttributes
 *  net.minecraft.world.entity.ai.village.poi.PoiType
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition
 *  net.minecraft.world.level.levelgen.DebugLevelSource
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoader
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.StartupMessageManager
 *  net.minecraftforge.fml.util.EnhancedRuntimeException
 *  net.minecraftforge.fml.util.EnhancedRuntimeException$WrappedPrintStream
 *  net.minecraftforge.fml.util.thread.EffectiveSide
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.registries;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.core.IdMapper;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.util.LogMessageAdapter;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.util.EnhancedRuntimeException;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.ILockableRegistry;
import net.minecraftforge.registries.IdMappingEvent;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.NamespacedDefaultedWrapper;
import net.minecraftforge.registries.NamespacedWrapper;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class GameData {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker REGISTRIES = ForgeRegistry.REGISTRIES;
    private static final int MAX_VARINT = 0x7FFFFFFE;
    private static final ResourceLocation BLOCK_TO_ITEM = new ResourceLocation("minecraft:blocktoitemmap");
    private static final ResourceLocation BLOCKSTATE_TO_ID = new ResourceLocation("minecraft:blockstatetoid");
    private static final ResourceLocation BLOCKSTATE_TO_POINT_OF_INTEREST_TYPE = new ResourceLocation("minecraft:blockstatetopointofinteresttype");
    private static boolean hasInit = false;
    private static final boolean DISABLE_VANILLA_REGISTRIES = Boolean.parseBoolean(System.getProperty("forge.disableVanillaGameData", "false"));
    private static final BiConsumer<ResourceLocation, ForgeRegistry<?>> LOCK_VANILLA = (name, reg) -> reg.slaves.values().stream().filter(o -> o instanceof ILockableRegistry).forEach(o -> ((ILockableRegistry)o).lock());

    public static void init() {
        if (DISABLE_VANILLA_REGISTRIES) {
            LOGGER.warn(REGISTRIES, "DISABLING VANILLA REGISTRY CREATION AS PER SYSTEM VARIABLE SETTING! forge.disableVanillaGameData");
            return;
        }
        if (hasInit) {
            return;
        }
        hasInit = true;
        GameData.makeRegistry(ForgeRegistries.Keys.BLOCKS, "air").addCallback(BlockCallbacks.INSTANCE).legacyName("blocks").intrusiveHolderCallback(Block::m_204297_).create();
        GameData.makeRegistry(ForgeRegistries.Keys.FLUIDS, "empty").intrusiveHolderCallback(Fluid::m_205069_).create();
        GameData.makeRegistry(ForgeRegistries.Keys.ITEMS, "air").addCallback(ItemCallbacks.INSTANCE).legacyName("items").intrusiveHolderCallback(Item::m_204114_).create();
        GameData.makeRegistry(ForgeRegistries.Keys.MOB_EFFECTS).legacyName("potions").create();
        GameData.makeRegistry(ForgeRegistries.Keys.SOUND_EVENTS).legacyName("soundevents").create();
        GameData.makeRegistry(ForgeRegistries.Keys.POTIONS, "empty").legacyName("potiontypes").create();
        GameData.makeRegistry(ForgeRegistries.Keys.ENCHANTMENTS).legacyName("enchantments").create();
        GameData.makeRegistry(ForgeRegistries.Keys.ENTITY_TYPES, "pig").legacyName("entities").intrusiveHolderCallback(EntityType::m_204041_).create();
        GameData.makeRegistry(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES).disableSaving().legacyName("blockentities").create();
        GameData.makeRegistry(ForgeRegistries.Keys.PARTICLE_TYPES).disableSaving().create();
        GameData.makeRegistry(ForgeRegistries.Keys.MENU_TYPES).disableSaving().create();
        GameData.makeRegistry(ForgeRegistries.Keys.PAINTING_VARIANTS, "kebab").create();
        GameData.makeRegistry(ForgeRegistries.Keys.RECIPE_TYPES).disableSaving().disableSync().create();
        GameData.makeRegistry(ForgeRegistries.Keys.RECIPE_SERIALIZERS).disableSaving().create();
        GameData.makeRegistry(ForgeRegistries.Keys.ATTRIBUTES).onValidate(AttributeCallbacks.INSTANCE).disableSaving().disableSync().create();
        GameData.makeRegistry(ForgeRegistries.Keys.STAT_TYPES).create();
        GameData.makeRegistry(ForgeRegistries.Keys.COMMAND_ARGUMENT_TYPES).disableSaving().create();
        GameData.makeRegistry(ForgeRegistries.Keys.VILLAGER_PROFESSIONS, "none").create();
        GameData.makeRegistry(ForgeRegistries.Keys.POI_TYPES).addCallback(PointOfInterestTypeCallbacks.INSTANCE).disableSync().create();
        GameData.makeRegistry(ForgeRegistries.Keys.MEMORY_MODULE_TYPES, "dummy").disableSync().create();
        GameData.makeRegistry(ForgeRegistries.Keys.SENSOR_TYPES, "dummy").disableSaving().disableSync().create();
        GameData.makeRegistry(ForgeRegistries.Keys.SCHEDULES).disableSaving().disableSync().create();
        GameData.makeRegistry(ForgeRegistries.Keys.ACTIVITIES).disableSaving().disableSync().create();
        GameData.makeRegistry(ForgeRegistries.Keys.WORLD_CARVERS).disableSaving().disableSync().create();
        GameData.makeRegistry(ForgeRegistries.Keys.FEATURES).disableSaving().disableSync().create();
        GameData.makeRegistry(ForgeRegistries.Keys.CHUNK_STATUS, "empty").disableSaving().disableSync().create();
        GameData.makeRegistry(ForgeRegistries.Keys.BLOCK_STATE_PROVIDER_TYPES).disableSaving().disableSync().create();
        GameData.makeRegistry(ForgeRegistries.Keys.FOLIAGE_PLACER_TYPES).disableSaving().disableSync().create();
        GameData.makeRegistry(ForgeRegistries.Keys.TREE_DECORATOR_TYPES).disableSaving().disableSync().create();
        GameData.makeRegistry(ForgeRegistries.Keys.BIOMES).disableSync().create();
    }

    static RegistryBuilder<EntityDataSerializer<?>> getDataSerializersRegistryBuilder() {
        return GameData.makeRegistry(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, 256, 0x7FFFFFFE).disableSaving().disableOverrides();
    }

    static RegistryBuilder<Codec<? extends IGlobalLootModifier>> getGLMSerializersRegistryBuilder() {
        return GameData.makeRegistry(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS).disableSaving().disableSync();
    }

    static RegistryBuilder<Codec<? extends BiomeModifier>> getBiomeModifierSerializersRegistryBuilder() {
        return new RegistryBuilder().disableSaving().disableSync();
    }

    static RegistryBuilder<Codec<? extends StructureModifier>> getStructureModifierSerializersRegistryBuilder() {
        return new RegistryBuilder().disableSaving().disableSync();
    }

    static RegistryBuilder<FluidType> getFluidTypeRegistryBuilder() {
        return GameData.makeRegistry(ForgeRegistries.Keys.FLUID_TYPES).disableSaving();
    }

    static <T> RegistryBuilder<T> makeUnsavedAndUnsynced() {
        return RegistryBuilder.of().disableSaving().disableSync();
    }

    static RegistryBuilder<ItemDisplayContext> getItemDisplayContextRegistryBuilder() {
        return new RegistryBuilder().setMaxID(256).disableOverrides().disableSaving().setDefaultKey(new ResourceLocation("minecraft:none")).onAdd(ItemDisplayContext.ADD_CALLBACK);
    }

    private static <T> RegistryBuilder<T> makeRegistry(ResourceKey<? extends Registry<T>> key) {
        return new RegistryBuilder().setName(key.m_135782_()).setMaxID(0x7FFFFFFE).hasWrapper();
    }

    private static <T> RegistryBuilder<T> makeRegistry(ResourceKey<? extends Registry<T>> key, int min, int max) {
        return new RegistryBuilder().setName(key.m_135782_()).setIDRange(min, max).hasWrapper();
    }

    private static <T> RegistryBuilder<T> makeRegistry(ResourceKey<? extends Registry<T>> key, String _default) {
        return new RegistryBuilder().setName(key.m_135782_()).setMaxID(0x7FFFFFFE).hasWrapper().setDefaultKey(new ResourceLocation(_default));
    }

    public static <T> MappedRegistry<T> getWrapper(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle) {
        ForgeRegistry reg = RegistryManager.ACTIVE.getRegistry(key);
        Validate.notNull(reg, (String)("Attempted to get vanilla wrapper for unknown registry: " + key.toString()), (Object[])new Object[0]);
        MappedRegistry ret = reg.getSlaveMap(NamespacedWrapper.Factory.ID, NamespacedWrapper.class);
        Validate.notNull((Object)ret, (String)("Attempted to get vanilla wrapper for registry created incorrectly: " + key.toString()), (Object[])new Object[0]);
        return ret;
    }

    public static <T> MappedRegistry<T> getWrapper(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, String defKey) {
        ForgeRegistry reg = RegistryManager.ACTIVE.getRegistry(key);
        Validate.notNull(reg, (String)("Attempted to get vanilla wrapper for unknown registry: " + key.toString()), (Object[])new Object[0]);
        MappedRegistry ret = reg.getSlaveMap(NamespacedDefaultedWrapper.Factory.ID, NamespacedDefaultedWrapper.class);
        Validate.notNull((Object)ret, (String)("Attempted to get vanilla wrapper for registry created incorrectly: " + key.toString()), (Object[])new Object[0]);
        return ret;
    }

    public static Map<Block, Item> getBlockItemMap() {
        return RegistryManager.ACTIVE.getRegistry(ForgeRegistries.Keys.ITEMS).getSlaveMap(BLOCK_TO_ITEM, Map.class);
    }

    public static IdMapper<BlockState> getBlockStateIDMap() {
        return RegistryManager.ACTIVE.getRegistry(ForgeRegistries.Keys.BLOCKS).getSlaveMap(BLOCKSTATE_TO_ID, IdMapper.class);
    }

    public static Map<BlockState, PoiType> getBlockStatePointOfInterestTypeMap() {
        return RegistryManager.ACTIVE.getRegistry(ForgeRegistries.Keys.POI_TYPES).getSlaveMap(BLOCKSTATE_TO_POINT_OF_INTEREST_TYPE, Map.class);
    }

    public static void vanillaSnapshot() {
        LOGGER.debug(REGISTRIES, "Creating vanilla freeze snapshot");
        for (Map.Entry r : RegistryManager.ACTIVE.registries.entrySet()) {
            GameData.loadRegistry((ResourceLocation)r.getKey(), RegistryManager.ACTIVE, RegistryManager.VANILLA, true);
        }
        RegistryManager.VANILLA.registries.forEach((name, reg) -> {
            reg.validateContent((ResourceLocation)name);
            reg.freeze();
        });
        RegistryManager.VANILLA.registries.forEach(LOCK_VANILLA);
        RegistryManager.ACTIVE.registries.forEach(LOCK_VANILLA);
        LOGGER.debug(REGISTRIES, "Vanilla freeze snapshot created");
    }

    public static void unfreezeData() {
        LOGGER.debug(REGISTRIES, "Unfreezing vanilla registries");
        BuiltInRegistries.f_257047_.m_123024_().filter(r -> r instanceof MappedRegistry).forEach(r -> ((MappedRegistry)r).unfreeze());
    }

    public static void freezeData() {
        LOGGER.debug(REGISTRIES, "Freezing registries");
        BuiltInRegistries.f_257047_.m_123024_().filter(r -> r instanceof MappedRegistry).forEach(r -> ((MappedRegistry)r).m_203521_());
        for (Map.Entry r2 : RegistryManager.ACTIVE.registries.entrySet()) {
            GameData.loadRegistry((ResourceLocation)r2.getKey(), RegistryManager.ACTIVE, RegistryManager.FROZEN, true);
        }
        RegistryManager.FROZEN.registries.forEach((name, reg) -> {
            reg.validateContent((ResourceLocation)name);
            reg.freeze();
        });
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
            reg.freeze();
            reg.bake();
            reg.dump((ResourceLocation)name);
        });
        GameData.fireRemapEvent((Map<ResourceLocation, Map<ResourceLocation, IdMappingEvent.IdRemapping>>)ImmutableMap.of(), true);
        LOGGER.debug(REGISTRIES, "All registries frozen");
    }

    public static void revertToFrozen() {
        GameData.revertTo(RegistryManager.FROZEN, true);
    }

    public static void revertTo(RegistryManager target, boolean fireEvents) {
        if (target.registries.isEmpty()) {
            LOGGER.warn(REGISTRIES, "Can't revert to {} GameData state without a valid snapshot.", (Object)target.getName());
            return;
        }
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.resetDelegates());
        LOGGER.debug(REGISTRIES, "Reverting to {} data state.", (Object)target.getName());
        for (Map.Entry r : RegistryManager.ACTIVE.registries.entrySet()) {
            GameData.loadRegistry((ResourceLocation)r.getKey(), target, RegistryManager.ACTIVE, true);
        }
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.bake());
        if (fireEvents) {
            GameData.fireRemapEvent((Map<ResourceLocation, Map<ResourceLocation, IdMappingEvent.IdRemapping>>)ImmutableMap.of(), true);
            ObjectHolderRegistry.applyObjectHolders();
        }
        LOGGER.debug(REGISTRIES, "{} state restored.", (Object)target.getName());
    }

    public static void revert(RegistryManager state, ResourceLocation registry, boolean lock) {
        LOGGER.debug(REGISTRIES, "Reverting {} to {}", (Object)registry, (Object)state.getName());
        GameData.loadRegistry(registry, state, RegistryManager.ACTIVE, lock);
        LOGGER.debug(REGISTRIES, "Reverting complete");
    }

    public static void postRegisterEvents() {
        HashSet<ResourceLocation> keySet = new HashSet<ResourceLocation>(RegistryManager.ACTIVE.registries.keySet());
        keySet.addAll(RegistryManager.getVanillaRegistryKeys());
        LinkedHashSet ordered = new LinkedHashSet(MappedRegistry.getKnownRegistries());
        ordered.retainAll(keySet);
        ordered.addAll(keySet.stream().sorted(ResourceLocation::compareNamespaced).toList());
        RuntimeException aggregate = new RuntimeException();
        for (ResourceLocation rootRegistryName : ordered) {
            try {
                ResourceKey registryKey = ResourceKey.m_135788_((ResourceLocation)rootRegistryName);
                ForgeRegistry forgeRegistry = RegistryManager.ACTIVE.getRegistry(rootRegistryName);
                Registry vanillaRegistry = (Registry)BuiltInRegistries.f_257047_.m_7745_(rootRegistryName);
                RegisterEvent registerEvent = new RegisterEvent(registryKey, forgeRegistry, vanillaRegistry);
                StartupMessageManager.modLoaderConsumer().ifPresent(s -> s.accept("REGISTERING " + String.valueOf(registryKey.m_135782_())));
                if (forgeRegistry != null) {
                    forgeRegistry.unfreeze();
                }
                ModLoader.get().postEventWrapContainerInModOrder((Event)registerEvent);
                if (forgeRegistry != null) {
                    forgeRegistry.freeze();
                }
                LOGGER.debug(REGISTRIES, "Applying holder lookups: {}", (Object)registryKey.m_135782_());
                ObjectHolderRegistry.applyObjectHolders(arg_0 -> ((ResourceLocation)registryKey.m_135782_()).equals(arg_0));
                LOGGER.debug(REGISTRIES, "Holder lookups applied: {}", (Object)registryKey.m_135782_());
            }
            catch (Throwable t) {
                aggregate.addSuppressed(t);
            }
        }
        if (aggregate.getSuppressed().length > 0) {
            LOGGER.fatal("Failed to register some entries, see suppressed exceptions for details", (Throwable)aggregate);
            LOGGER.fatal("Detected errors during registry event dispatch, rolling back to VANILLA state");
            GameData.revertTo(RegistryManager.VANILLA, false);
            LOGGER.fatal("Detected errors during registry event dispatch, roll back to VANILLA complete");
            throw aggregate;
        }
        ForgeHooks.modifyAttributes();
        SpawnPlacements.fireSpawnPlacementEvent();
        CreativeModeTabRegistry.sortTabs();
    }

    private static <T> void loadRegistry(final ResourceLocation registryName, final RegistryManager from, final RegistryManager to, boolean freeze) {
        ForgeRegistry fromRegistry = from.getRegistry(registryName);
        if (fromRegistry == null) {
            ForgeRegistry toRegistry = to.getRegistry(registryName);
            if (toRegistry == null) {
                throw new EnhancedRuntimeException("Could not find registry to load: " + String.valueOf(registryName)){
                    private static final long serialVersionUID = 1L;

                    protected void printStackTrace(EnhancedRuntimeException.WrappedPrintStream stream) {
                        stream.println("Looking For: " + String.valueOf(registryName));
                        stream.println("Found From:");
                        for (ResourceLocation name : from.registries.keySet()) {
                            stream.println("  " + String.valueOf(name));
                        }
                        stream.println("Found To:");
                        for (ResourceLocation name : to.registries.keySet()) {
                            stream.println("  " + String.valueOf(name));
                        }
                    }
                };
            }
        } else {
            ForgeRegistry toRegistry = to.getRegistry(registryName, from);
            toRegistry.sync(registryName, fromRegistry);
            if (freeze) {
                toRegistry.isFrozen = true;
            }
        }
    }

    public static Multimap<ResourceLocation, ResourceLocation> injectSnapshot(Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot, boolean injectFrozenData, boolean isLocalWorld) {
        ResourceLocation[] missingRegs;
        LOGGER.info(REGISTRIES, "Injecting existing registry data into this {} instance", (Object)EffectiveSide.get());
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.validateContent((ResourceLocation)name));
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.dump((ResourceLocation)name));
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.resetDelegates());
        snapshot = snapshot.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(e -> RegistryManager.ACTIVE.updateLegacyName((ResourceLocation)e.getKey()), Map.Entry::getValue, (k1, k2) -> k1, LinkedHashMap::new));
        if (isLocalWorld && (missingRegs = (ResourceLocation[])snapshot.keySet().stream().filter(name -> !RegistryManager.ACTIVE.registries.containsKey(name)).toArray(ResourceLocation[]::new)).length > 0) {
            String header = "Forge Mod Loader detected missing/unknown registrie(s).\n\nThere are " + missingRegs.length + " missing registries in this save.\nIf you continue the missing registries will get removed.\nThis may cause issues, it is advised that you create a world backup before continuing.\n\n";
            StringBuilder text = new StringBuilder("Missing Registries:\n");
            for (ResourceLocation s : missingRegs) {
                text.append(s).append("\n");
            }
            LOGGER.warn(REGISTRIES, header);
            LOGGER.warn(REGISTRIES, text.toString());
        }
        RegistryManager STAGING = new RegistryManager();
        HashMap<ResourceLocation, Map<ResourceLocation, IdMappingEvent.IdRemapping>> remaps = new HashMap<ResourceLocation, Map<ResourceLocation, IdMappingEvent.IdRemapping>>();
        LinkedHashMap missing = new LinkedHashMap();
        snapshot.forEach((key, value) -> {
            remaps.put((ResourceLocation)key, new LinkedHashMap());
            missing.put(key, new Object2IntLinkedOpenHashMap());
            GameData.loadPersistentDataToStagingRegistry(RegistryManager.ACTIVE, STAGING, (Map)remaps.get(key), (Object2IntMap<ResourceLocation>)((Object2IntMap)missing.get(key)), key, value);
        });
        int count = missing.values().stream().mapToInt(Map::size).sum();
        if (count > 0) {
            LOGGER.debug(REGISTRIES, "There are {} mappings missing - attempting a mod remap", (Object)count);
            ArrayListMultimap defaulted = ArrayListMultimap.create();
            ArrayListMultimap failed = ArrayListMultimap.create();
            missing.entrySet().stream().filter(e -> !((Object2IntMap)e.getValue()).isEmpty()).forEach(arg_0 -> GameData.lambda$injectSnapshot$28(STAGING, (Multimap)failed, remaps, (Multimap)defaulted, isLocalWorld, arg_0));
            if (!defaulted.isEmpty() && !isLocalWorld) {
                return defaulted;
            }
            if (!defaulted.isEmpty()) {
                String header = "Forge Mod Loader detected missing registry entries.\n\nThere are " + defaulted.size() + " missing entries in this save.\nIf you continue the missing entries will get removed.\nA world backup will be automatically created in your saves directory.\n\n";
                StringBuilder buf = new StringBuilder();
                defaulted.asMap().forEach((name, entries) -> {
                    buf.append("Missing ").append(name).append(":\n");
                    entries.stream().sorted(ResourceLocation::compareNamespaced).forEach(rl -> buf.append("    ").append(rl).append("\n"));
                    buf.append("\n");
                });
                LOGGER.warn(REGISTRIES, header);
                LOGGER.warn(REGISTRIES, buf.toString());
            }
            if (!defaulted.isEmpty() && isLocalWorld) {
                LOGGER.error(REGISTRIES, "There are unidentified mappings in this world - we are going to attempt to process anyway");
            }
        }
        if (injectFrozenData) {
            RegistryManager.ACTIVE.registries.forEach((name, reg) -> GameData.loadFrozenDataToStagingRegistry(STAGING, name, (Map)remaps.get(name)));
        }
        STAGING.registries.forEach((name, reg) -> reg.validateContent((ResourceLocation)name));
        RegistryManager.ACTIVE.registries.forEach((key, value) -> GameData.loadRegistry(key, STAGING, RegistryManager.ACTIVE, true));
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
            reg.bake();
            reg.dump((ResourceLocation)name);
        });
        GameData.fireRemapEvent(remaps, false);
        ObjectHolderRegistry.applyObjectHolders();
        return ArrayListMultimap.create();
    }

    private static void fireRemapEvent(Map<ResourceLocation, Map<ResourceLocation, IdMappingEvent.IdRemapping>> remaps, boolean isFreezing) {
        MinecraftForge.EVENT_BUS.post((Event)new IdMappingEvent(remaps, isFreezing));
    }

    private static <T> void loadPersistentDataToStagingRegistry(RegistryManager pool, RegistryManager to, Map<ResourceLocation, IdMappingEvent.IdRemapping> remaps, Object2IntMap<ResourceLocation> missing, ResourceLocation name, ForgeRegistry.Snapshot snap) {
        ForgeRegistry active = pool.getRegistry(name);
        if (active == null) {
            return;
        }
        ForgeRegistry _new = to.getRegistry(name, RegistryManager.ACTIVE);
        snap.aliases.forEach(_new::addAlias);
        snap.blocked.forEach(_new::block);
        _new.loadIds(snap.ids, snap.overrides, missing, remaps, active, name);
    }

    private static <T> void processMissing(ResourceLocation name, RegistryManager STAGING, MissingMappingsEvent e, Object2IntMap<ResourceLocation> missing, Map<ResourceLocation, IdMappingEvent.IdRemapping> remaps, Collection<ResourceLocation> defaulted, Collection<ResourceLocation> failed, boolean injectNetworkDummies) {
        List mappings = e.getAllMappings(ResourceKey.m_135788_((ResourceLocation)name));
        ForgeRegistry active = RegistryManager.ACTIVE.getRegistry(name);
        ForgeRegistry staging = STAGING.getRegistry(name);
        staging.processMissingEvent(name, active, mappings, missing, remaps, defaulted, failed, injectNetworkDummies);
    }

    private static <T> void loadFrozenDataToStagingRegistry(RegistryManager STAGING, ResourceLocation name, Map<ResourceLocation, IdMappingEvent.IdRemapping> remaps) {
        ForgeRegistry frozen = RegistryManager.FROZEN.getRegistry(name);
        ForgeRegistry newRegistry = STAGING.getRegistry(name, RegistryManager.FROZEN);
        Object2IntLinkedOpenHashMap _new = new Object2IntLinkedOpenHashMap();
        frozen.getKeys().stream().filter(key -> !newRegistry.containsKey((ResourceLocation)key)).forEach(arg_0 -> GameData.lambda$loadFrozenDataToStagingRegistry$36((Object2IntMap)_new, frozen, arg_0));
        newRegistry.loadIds((Object2IntMap<ResourceLocation>)_new, frozen.getOverrideOwners(), (Object2IntMap<ResourceLocation>)new Object2IntLinkedOpenHashMap(), remaps, frozen, name);
    }

    public static ResourceLocation checkPrefix(String name, boolean warnOverrides) {
        int index = name.lastIndexOf(58);
        String oldPrefix = index == -1 ? "" : name.substring(0, index).toLowerCase(Locale.ROOT);
        name = index == -1 ? name : name.substring(index + 1);
        String prefix = ModLoadingContext.get().getActiveNamespace();
        if (warnOverrides && !oldPrefix.equals(prefix) && !oldPrefix.isEmpty()) {
            LogManager.getLogger().debug("Mod `{}` attempting to register `{}` to the namespace `{}`. This could be intended, but likely means an EventBusSubscriber without a modid.", (Object)prefix, (Object)name, (Object)oldPrefix);
            prefix = oldPrefix;
        }
        return new ResourceLocation(prefix, name);
    }

    private static /* synthetic */ void lambda$loadFrozenDataToStagingRegistry$36(Object2IntMap _new, ForgeRegistry frozen, ResourceLocation key) {
        _new.put((Object)key, frozen.getID(key));
    }

    private static /* synthetic */ void lambda$injectSnapshot$28(RegistryManager STAGING, Multimap failed, Map remaps, Multimap defaulted, boolean isLocalWorld, Map.Entry m) {
        ResourceLocation name = (ResourceLocation)m.getKey();
        ForgeRegistry reg = STAGING.getRegistry(name);
        Object2IntMap missingIds = (Object2IntMap)m.getValue();
        MissingMappingsEvent event = reg.getMissingEvent(name, (Object2IntMap<ResourceLocation>)missingIds);
        MinecraftForge.EVENT_BUS.post((Event)event);
        List lst = event.getAllMappings(reg.getRegistryKey()).stream().filter(e -> e.action == MissingMappingsEvent.Action.DEFAULT).sorted(Comparator.comparing(Object::toString)).collect(Collectors.toList());
        if (!lst.isEmpty()) {
            LOGGER.error(REGISTRIES, () -> LogMessageAdapter.adapt(sb -> {
                sb.append("Unidentified mapping from registry ").append(name).append('\n');
                lst.stream().sorted().forEach(map -> sb.append('\t').append(map.key).append(": ").append(map.id).append('\n'));
            }));
        }
        event.getAllMappings(reg.getRegistryKey()).stream().filter(e -> e.action == MissingMappingsEvent.Action.FAIL).forEach(fail -> failed.put((Object)name, (Object)fail.key));
        GameData.processMissing(name, STAGING, event, (Object2IntMap<ResourceLocation>)missingIds, (Map)remaps.get(name), defaulted.get((Object)name), failed.get((Object)name), !isLocalWorld);
    }

    static {
        GameData.init();
    }

    private static class BlockCallbacks
    implements IForgeRegistry.AddCallback<Block>,
    IForgeRegistry.ClearCallback<Block>,
    IForgeRegistry.BakeCallback<Block>,
    IForgeRegistry.CreateCallback<Block> {
        static final BlockCallbacks INSTANCE = new BlockCallbacks();

        private BlockCallbacks() {
        }

        @Override
        public void onAdd(IForgeRegistryInternal<Block> owner, RegistryManager stage, int id, ResourceKey<Block> key, Block block, @Nullable Block oldBlock) {
            if (oldBlock != null) {
                StateDefinition oldContainer = oldBlock.m_49965_();
                StateDefinition newContainer = block.m_49965_();
                if (key.m_135782_().m_135827_().equals("minecraft") && !oldContainer.m_61092_().equals(newContainer.m_61092_())) {
                    String oldSequence = oldContainer.m_61092_().stream().map(s -> String.format(Locale.ENGLISH, "%s={%s}", s.m_61708_(), s.m_6908_().stream().map(Object::toString).collect(Collectors.joining(",")))).collect(Collectors.joining(";"));
                    String newSequence = newContainer.m_61092_().stream().map(s -> String.format(Locale.ENGLISH, "%s={%s}", s.m_61708_(), s.m_6908_().stream().map(Object::toString).collect(Collectors.joining(",")))).collect(Collectors.joining(";"));
                    LOGGER.error(REGISTRIES, () -> LogMessageAdapter.adapt(sb -> {
                        sb.append("Registry replacements for vanilla block '").append(key.m_135782_()).append("' must not change the number or order of blockstates.\n");
                        sb.append("\tOld: ").append(oldSequence).append('\n');
                        sb.append("\tNew: ").append(newSequence);
                    }));
                    throw new RuntimeException("Invalid vanilla replacement. See log for details.");
                }
            }
        }

        @Override
        public void onClear(IForgeRegistryInternal<Block> owner, RegistryManager stage) {
            owner.getSlaveMap(BLOCKSTATE_TO_ID, ClearableObjectIntIdentityMap.class).clear();
        }

        @Override
        public void onCreate(IForgeRegistryInternal<Block> owner, RegistryManager stage) {
            ClearableObjectIntIdentityMap<BlockState> idMap = new ClearableObjectIntIdentityMap<BlockState>(){

                public int getId(BlockState key) {
                    return this.f_122654_.containsKey((Object)key) ? this.f_122654_.getInt((Object)key) : -1;
                }
            };
            owner.setSlaveMap(BLOCKSTATE_TO_ID, idMap);
            owner.setSlaveMap(BLOCK_TO_ITEM, new HashMap());
        }

        @Override
        public void onBake(IForgeRegistryInternal<Block> owner, RegistryManager stage) {
            ClearableObjectIntIdentityMap blockstateMap = owner.getSlaveMap(BLOCKSTATE_TO_ID, ClearableObjectIntIdentityMap.class);
            for (Block block : owner) {
                for (BlockState state : block.m_49965_().m_61056_()) {
                    blockstateMap.m_122667_(state);
                    state.m_60611_();
                }
                block.m_60589_();
            }
            DebugLevelSource.initValidStates();
        }
    }

    private static class ItemCallbacks
    implements IForgeRegistry.AddCallback<Item>,
    IForgeRegistry.ClearCallback<Item>,
    IForgeRegistry.CreateCallback<Item> {
        static final ItemCallbacks INSTANCE = new ItemCallbacks();

        private ItemCallbacks() {
        }

        @Override
        public void onAdd(IForgeRegistryInternal<Item> owner, RegistryManager stage, int id, ResourceKey<Item> key, Item item, @Nullable Item oldItem) {
            Map blockToItem;
            if (oldItem instanceof BlockItem) {
                blockToItem = owner.getSlaveMap(BLOCK_TO_ITEM, Map.class);
                ((BlockItem)oldItem).removeFromBlockToItemMap(blockToItem, item);
            }
            if (item instanceof BlockItem) {
                blockToItem = owner.getSlaveMap(BLOCK_TO_ITEM, Map.class);
                ((BlockItem)item).m_6192_(blockToItem, item);
            }
        }

        @Override
        public void onClear(IForgeRegistryInternal<Item> owner, RegistryManager stage) {
            owner.getSlaveMap(BLOCK_TO_ITEM, Map.class).clear();
        }

        @Override
        public void onCreate(IForgeRegistryInternal<Item> owner, RegistryManager stage) {
            Map map = stage.getRegistry(ForgeRegistries.Keys.BLOCKS).getSlaveMap(BLOCK_TO_ITEM, Map.class);
            owner.setSlaveMap(BLOCK_TO_ITEM, map);
        }
    }

    private static class AttributeCallbacks
    implements IForgeRegistry.ValidateCallback<Attribute> {
        static final AttributeCallbacks INSTANCE = new AttributeCallbacks();

        private AttributeCallbacks() {
        }

        @Override
        public void onValidate(IForgeRegistryInternal<Attribute> owner, RegistryManager stage, int id, ResourceLocation key, Attribute obj) {
            if (stage != RegistryManager.VANILLA) {
                DefaultAttributes.m_22296_();
            }
        }
    }

    private static class PointOfInterestTypeCallbacks
    implements IForgeRegistry.AddCallback<PoiType>,
    IForgeRegistry.ClearCallback<PoiType>,
    IForgeRegistry.CreateCallback<PoiType> {
        static final PointOfInterestTypeCallbacks INSTANCE = new PointOfInterestTypeCallbacks();

        private PointOfInterestTypeCallbacks() {
        }

        @Override
        public void onAdd(IForgeRegistryInternal<PoiType> owner, RegistryManager stage, int id, ResourceKey<PoiType> key, PoiType obj, @Nullable PoiType oldObj) {
            Map map = owner.getSlaveMap(BLOCKSTATE_TO_POINT_OF_INTEREST_TYPE, Map.class);
            if (oldObj != null) {
                oldObj.f_27325_().forEach(map::remove);
            }
            obj.f_27325_().forEach(state -> {
                PoiType oldType = map.put(state, obj);
                if (oldType != null) {
                    throw new IllegalStateException(String.format(Locale.ENGLISH, "Point of interest types %s and %s both list %s in their blockstates, this is not allowed. Blockstates can only have one point of interest type each.", oldType, obj, state));
                }
            });
        }

        @Override
        public void onClear(IForgeRegistryInternal<PoiType> owner, RegistryManager stage) {
            owner.getSlaveMap(BLOCKSTATE_TO_POINT_OF_INTEREST_TYPE, Map.class).clear();
        }

        @Override
        public void onCreate(IForgeRegistryInternal<PoiType> owner, RegistryManager stage) {
            owner.setSlaveMap(BLOCKSTATE_TO_POINT_OF_INTEREST_TYPE, new HashMap());
        }
    }

    private static class ClearableObjectIntIdentityMap<I>
    extends IdMapper<I> {
        private ClearableObjectIntIdentityMap() {
        }

        void clear() {
            this.f_122654_.clear();
            this.f_122655_.clear();
            this.f_122653_ = 0;
        }

        void remove(I key) {
            boolean hadId = this.f_122654_.containsKey(key);
            int prev = this.f_122654_.removeInt(key);
            if (hadId) {
                this.f_122655_.set(prev, null);
            }
        }
    }
}

