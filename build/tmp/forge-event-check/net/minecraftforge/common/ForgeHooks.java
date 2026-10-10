/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.Lifecycle$Deprecated
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.ChatFormatting
 *  net.minecraft.ResourceLocationException
 *  net.minecraft.Util
 *  net.minecraft.commands.CommandSource
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.SharedSuggestionProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderLookup$RegistryLookup
 *  net.minecraft.core.HolderLookup$RegistryLookup$Delegate
 *  net.minecraft.core.HolderOwner
 *  net.minecraft.core.HolderSet
 *  net.minecraft.core.HolderSet$Named
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.ChatDecorator
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentContents
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.TextColor
 *  net.minecraft.network.chat.contents.LiteralContents
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
 *  net.minecraft.network.protocol.game.ServerboundPlayerActionPacket$Action
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.metadata.pack.PackMetadataSection
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.stats.Stats
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.TagEntry
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.util.Mth
 *  net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix$Conversion
 *  net.minecraft.world.Container
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectUtil
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.ExperienceOrb
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.SlotAccess
 *  net.minecraft.world.entity.ai.Brain
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.DefaultAttributes
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.monster.EnderMan
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AnvilMenu
 *  net.minecraft.world.inventory.ClickAction
 *  net.minecraft.world.inventory.ContainerLevelAccess
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.BucketItem
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTab$DisplayItemsGenerator
 *  net.minecraft.world.item.CreativeModeTab$ItemDisplayParameters
 *  net.minecraft.world.item.CreativeModeTab$Output
 *  net.minecraft.world.item.CreativeModeTab$TabVisibility
 *  net.minecraft.world.item.EnchantedBookItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ItemStackLinkedSet
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.PotionItem
 *  net.minecraft.world.item.SpawnEggItem
 *  net.minecraft.world.item.Tiers
 *  net.minecraft.world.item.TippedArrowItem
 *  net.minecraft.world.item.alchemy.Potion
 *  net.minecraft.world.item.alchemy.PotionUtils
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biome$ClimateSettings
 *  net.minecraft.world.level.biome.BiomeGenerationSettings
 *  net.minecraft.world.level.biome.BiomeSpecialEffects
 *  net.minecraft.world.level.biome.MobSpawnSettings
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.FurnaceBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.pattern.BlockInWorld
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.minecraft.world.level.gameevent.GameEvent$Context
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.level.storage.LevelStorageSource$LevelDirectory
 *  net.minecraft.world.level.storage.WorldData
 *  net.minecraft.world.level.storage.loot.LootContext
 *  net.minecraft.world.level.storage.loot.LootTable
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.eventbus.api.Event$Result
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.ModLoader
 *  org.apache.commons.lang3.function.TriFunction
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 *  org.apache.maven.artifact.versioning.ArtifactVersion
 *  org.apache.maven.artifact.versioning.DefaultArtifactVersion
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeInternalHandler;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifierManager;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.BrainBuilder;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.DifficultyChangeEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.ModMismatchEvent;
import net.minecraftforge.event.RegisterStructureConversionsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.EnderManAngerEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingDrownEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingMakeBrainEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.resource.ResourcePackLoader;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgeHooks {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FORGEHOOKS = MarkerManager.getMarker((String)"FORGEHOOKS");
    private static final Marker WORLDPERSISTENCE = MarkerManager.getMarker((String)"WP");
    static final Pattern URL_PATTERN = Pattern.compile("((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00a7 \n]|$))", 2);
    private static ThreadLocal<Player> craftingPlayer = new ThreadLocal();
    private static final ThreadLocal<Deque<LootTableContext>> lootContext = new ThreadLocal();
    private static final Map<Holder.Reference<Item>, Integer> VANILLA_BURNS = new HashMap<Holder.Reference<Item>, Integer>();
    private static final Set<String> VANILLA_DIMS = Sets.newHashSet((Object[])new String[]{"minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"});
    private static final String DIMENSIONS_KEY = "dimensions";
    private static final String SEED_KEY = "seed";
    private static final Map<EntityType<? extends LivingEntity>, AttributeSupplier> FORGE_ATTRIBUTES = new HashMap<EntityType<? extends LivingEntity>, AttributeSupplier>();
    private static final Lazy<Map<String, StructuresBecomeConfiguredFix.Conversion>> FORGE_CONVERSION_MAP = Lazy.concurrentOf(() -> {
        HashMap<String, StructuresBecomeConfiguredFix.Conversion> map = new HashMap<String, StructuresBecomeConfiguredFix.Conversion>();
        MinecraftForge.EVENT_BUS.post((Event)new RegisterStructureConversionsEvent(map));
        return ImmutableMap.copyOf(map);
    });

    public static boolean canContinueUsing(@NotNull ItemStack from, @NotNull ItemStack to) {
        if (!from.m_41619_() && !to.m_41619_()) {
            return from.m_41720_().canContinueUsing(from, to);
        }
        return false;
    }

    public static boolean isCorrectToolForDrops(@NotNull BlockState state, @NotNull Player player) {
        if (!state.m_60834_()) {
            return ForgeEventFactory.doPlayerHarvestCheck(player, state, true);
        }
        return player.m_36298_(state);
    }

    public static boolean onItemStackedOn(ItemStack carriedItem, ItemStack stackedOnItem, Slot slot, ClickAction action, Player player, SlotAccess carriedSlotAccess) {
        return MinecraftForge.EVENT_BUS.post((Event)new ItemStackedOnOtherEvent(carriedItem, stackedOnItem, slot, action, player, carriedSlotAccess));
    }

    public static void onDifficultyChange(Difficulty difficulty, Difficulty oldDifficulty) {
        MinecraftForge.EVENT_BUS.post((Event)new DifficultyChangeEvent(difficulty, oldDifficulty));
    }

    public static LivingChangeTargetEvent onLivingChangeTarget(LivingEntity entity, LivingEntity originalTarget, LivingChangeTargetEvent.ILivingTargetType targetType) {
        LivingChangeTargetEvent event = new LivingChangeTargetEvent(entity, originalTarget, targetType);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static Brain<?> onLivingMakeBrain(LivingEntity entity, Brain<?> originalBrain, Dynamic<?> dynamic) {
        BrainBuilder brainBuilder = originalBrain.createBuilder();
        LivingMakeBrainEvent event = new LivingMakeBrainEvent(entity, brainBuilder);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return brainBuilder.makeBrain(dynamic);
    }

    public static boolean onLivingTick(LivingEntity entity) {
        return MinecraftForge.EVENT_BUS.post((Event)new LivingEvent.LivingTickEvent(entity));
    }

    public static boolean onLivingAttack(LivingEntity entity, DamageSource src, float amount) {
        return entity instanceof Player || !MinecraftForge.EVENT_BUS.post((Event)new LivingAttackEvent(entity, src, amount));
    }

    public static boolean onPlayerAttack(LivingEntity entity, DamageSource src, float amount) {
        return !MinecraftForge.EVENT_BUS.post((Event)new LivingAttackEvent(entity, src, amount));
    }

    public static LivingKnockBackEvent onLivingKnockBack(LivingEntity target, float strength, double ratioX, double ratioZ) {
        LivingKnockBackEvent event = new LivingKnockBackEvent(target, strength, ratioX, ratioZ);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static boolean onLivingUseTotem(LivingEntity entity, DamageSource damageSource, ItemStack totem, InteractionHand hand) {
        return !MinecraftForge.EVENT_BUS.post((Event)new LivingUseTotemEvent(entity, damageSource, totem, hand));
    }

    public static float onLivingHurt(LivingEntity entity, DamageSource src, float amount) {
        LivingHurtEvent event = new LivingHurtEvent(entity, src, amount);
        return MinecraftForge.EVENT_BUS.post((Event)event) ? 0.0f : event.getAmount();
    }

    public static float onLivingDamage(LivingEntity entity, DamageSource src, float amount) {
        LivingDamageEvent event = new LivingDamageEvent(entity, src, amount);
        return MinecraftForge.EVENT_BUS.post((Event)event) ? 0.0f : event.getAmount();
    }

    public static boolean onLivingDeath(LivingEntity entity, DamageSource src) {
        return MinecraftForge.EVENT_BUS.post((Event)new LivingDeathEvent(entity, src));
    }

    public static boolean onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit) {
        return MinecraftForge.EVENT_BUS.post((Event)new LivingDropsEvent(entity, source, drops, lootingLevel, recentlyHit));
    }

    @Nullable
    public static float[] onLivingFall(LivingEntity entity, float distance, float damageMultiplier) {
        float[] fArray;
        LivingFallEvent event = new LivingFallEvent(entity, distance, damageMultiplier);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            fArray = null;
        } else {
            float[] fArray2 = new float[2];
            fArray2[0] = event.getDistance();
            fArray = fArray2;
            fArray2[1] = event.getDamageMultiplier();
        }
        return fArray;
    }

    public static int getLootingLevel(Entity target, @Nullable Entity killer, @Nullable DamageSource cause) {
        int looting = 0;
        if (killer instanceof LivingEntity) {
            looting = EnchantmentHelper.m_44930_((LivingEntity)((LivingEntity)killer));
        }
        if (target instanceof LivingEntity) {
            looting = ForgeHooks.getLootingLevel((LivingEntity)target, cause, looting);
        }
        return looting;
    }

    public static int getLootingLevel(LivingEntity target, @Nullable DamageSource cause, int level) {
        LootingLevelEvent event = new LootingLevelEvent(target, cause, level);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getLootingLevel();
    }

    public static double getEntityVisibilityMultiplier(LivingEntity entity, Entity lookingEntity, double originalMultiplier) {
        LivingEvent.LivingVisibilityEvent event = new LivingEvent.LivingVisibilityEvent(entity, lookingEntity, originalMultiplier);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return Math.max(0.0, event.getVisibilityModifier());
    }

    public static Optional<BlockPos> isLivingOnLadder(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull LivingEntity entity) {
        boolean isSpectator;
        boolean bl = isSpectator = entity instanceof Player && entity.m_5833_();
        if (isSpectator) {
            return Optional.empty();
        }
        if (!((Boolean)ForgeConfig.SERVER.fullBoundingBoxLadders.get()).booleanValue()) {
            return state.isLadder((LevelReader)level, pos, entity) ? Optional.of(pos) : Optional.empty();
        }
        AABB bb = entity.m_20191_();
        int mX = Mth.m_14107_((double)bb.f_82288_);
        int mY = Mth.m_14107_((double)bb.f_82289_);
        int mZ = Mth.m_14107_((double)bb.f_82290_);
        int y2 = mY;
        while ((double)y2 < bb.f_82292_) {
            int x2 = mX;
            while ((double)x2 < bb.f_82291_) {
                int z2 = mZ;
                while ((double)z2 < bb.f_82293_) {
                    BlockPos tmp = new BlockPos(x2, y2, z2);
                    state = level.m_8055_(tmp);
                    if (state.isLadder((LevelReader)level, tmp, entity)) {
                        return Optional.of(tmp);
                    }
                    ++z2;
                }
                ++x2;
            }
            ++y2;
        }
        return Optional.empty();
    }

    public static void onLivingJump(LivingEntity entity) {
        MinecraftForge.EVENT_BUS.post((Event)new LivingEvent.LivingJumpEvent(entity));
    }

    @Nullable
    public static ItemEntity onPlayerTossEvent(@NotNull Player player, @NotNull ItemStack item, boolean includeName) {
        player.captureDrops((Collection)Lists.newArrayList());
        ItemEntity ret = player.m_7197_(item, false, includeName);
        player.captureDrops(null);
        if (ret == null) {
            return null;
        }
        ItemTossEvent event = new ItemTossEvent(ret, player);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return null;
        }
        if (!player.m_9236_().f_46443_) {
            player.m_20193_().m_7967_((Entity)event.getEntity());
        }
        return event.getEntity();
    }

    public static boolean onVanillaGameEvent(Level level, GameEvent vanillaEvent, Vec3 pos, GameEvent.Context context) {
        return !MinecraftForge.EVENT_BUS.post((Event)new VanillaGameEvent(level, vanillaEvent, pos, context));
    }

    private static String getRawText(Component message) {
        String string;
        ComponentContents componentContents = message.m_214077_();
        if (componentContents instanceof LiteralContents) {
            LiteralContents literalContents = (LiteralContents)componentContents;
            string = literalContents.f_237368_();
        } else {
            string = "";
        }
        return string;
    }

    @Nullable
    public static Component onServerChatSubmittedEvent(ServerPlayer player, String plain, Component decorated) {
        ServerChatEvent event = new ServerChatEvent(player, plain, decorated);
        return MinecraftForge.EVENT_BUS.post((Event)event) ? null : event.getMessage();
    }

    @NotNull
    public static ChatDecorator getServerChatSubmittedDecorator() {
        return (sender, message) -> CompletableFuture.supplyAsync(() -> {
            if (sender == null) {
                return message;
            }
            return ForgeHooks.onServerChatSubmittedEvent(sender, ForgeHooks.getRawText(message), message);
        }, Util.m_183991_());
    }

    public static Component newChatWithLinks(String string) {
        return ForgeHooks.newChatWithLinks(string, true);
    }

    public static Component newChatWithLinks(String string, boolean allowMissingHeader) {
        MutableComponent ichat = null;
        Matcher matcher = URL_PATTERN.matcher(string);
        int lastEnd = 0;
        while (matcher.find()) {
            MutableComponent link;
            Object url;
            block13: {
                int start = matcher.start();
                int end = matcher.end();
                String part = string.substring(lastEnd, start);
                if (part.length() > 0) {
                    if (ichat == null) {
                        ichat = Component.m_237113_((String)part);
                    } else {
                        ichat.m_130946_(part);
                    }
                }
                lastEnd = end;
                url = string.substring(start, end);
                link = Component.m_237113_((String)url);
                try {
                    if (new URI((String)url).getScheme() != null) break block13;
                    if (!allowMissingHeader) {
                        if (ichat == null) {
                            ichat = Component.m_237113_((String)url);
                            continue;
                        }
                        ichat.m_130946_((String)url);
                        continue;
                    }
                    url = "http://" + (String)url;
                }
                catch (URISyntaxException e) {
                    if (ichat == null) {
                        ichat = Component.m_237113_((String)url);
                        continue;
                    }
                    ichat.m_130946_((String)url);
                    continue;
                }
            }
            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, (String)url);
            link.m_6270_(link.m_7383_().m_131142_(click).m_131162_(Boolean.valueOf(true)).m_131148_(TextColor.m_131270_((ChatFormatting)ChatFormatting.BLUE)));
            if (ichat == null) {
                ichat = Component.m_237113_((String)"");
            }
            ichat.m_7220_((Component)link);
        }
        String end = string.substring(lastEnd);
        if (ichat == null) {
            ichat = Component.m_237113_((String)end);
        } else if (end.length() > 0) {
            ichat.m_7220_((Component)Component.m_237113_((String)string.substring(lastEnd)));
        }
        return ichat;
    }

    public static void dropXpForBlock(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack) {
        int silkTouchLevel;
        int fortuneLevel = stack.getEnchantmentLevel(Enchantments.f_44987_);
        int exp = state.getExpDrop((LevelReader)level, level.f_46441_, pos, fortuneLevel, silkTouchLevel = stack.getEnchantmentLevel(Enchantments.f_44985_));
        if (exp > 0) {
            state.m_60734_().m_49805_(level, pos, exp);
        }
    }

    public static int onBlockBreakEvent(Level level, GameType gameType, ServerPlayer entityPlayer, BlockPos pos) {
        boolean preCancelEvent = false;
        ItemStack itemstack = entityPlayer.m_21205_();
        if (!itemstack.m_41619_() && !itemstack.m_41720_().m_6777_(level.m_8055_(pos), level, pos, (Player)entityPlayer)) {
            preCancelEvent = true;
        }
        if (gameType.m_46407_()) {
            if (gameType == GameType.SPECTATOR) {
                preCancelEvent = true;
            }
            if (!(entityPlayer.m_36326_() || !itemstack.m_41619_() && itemstack.m_204128_(level.m_9598_().m_175515_(Registries.f_256747_), new BlockInWorld((LevelReader)level, pos, false)))) {
                preCancelEvent = true;
            }
        }
        if (level.m_7702_(pos) == null) {
            entityPlayer.f_8906_.m_9829_((Packet)new ClientboundBlockUpdatePacket(pos, level.m_6425_(pos).m_76188_()));
        }
        BlockState state = level.m_8055_(pos);
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, state, (Player)entityPlayer);
        event.setCanceled(preCancelEvent);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            Packet pkt;
            entityPlayer.f_8906_.m_9829_((Packet)new ClientboundBlockUpdatePacket((BlockGetter)level, pos));
            BlockEntity blockEntity = level.m_7702_(pos);
            if (blockEntity != null && (pkt = blockEntity.m_58483_()) != null) {
                entityPlayer.f_8906_.m_9829_(pkt);
            }
        }
        return event.isCanceled() ? -1 : event.getExpToDrop();
    }

    public static InteractionResult onPlaceItemIntoWorld(@NotNull UseOnContext context) {
        ItemStack itemstack = context.m_43722_();
        Level level = context.m_43725_();
        Player player = context.m_43723_();
        if (player != null && !player.m_150110_().f_35938_ && !itemstack.m_204121_(level.m_9598_().m_175515_(Registries.f_256747_), new BlockInWorld((LevelReader)level, context.m_8083_(), false))) {
            return InteractionResult.PASS;
        }
        Item item = itemstack.m_41720_();
        int size = itemstack.m_41613_();
        CompoundTag nbt = null;
        if (itemstack.m_41783_() != null) {
            nbt = itemstack.m_41783_().m_6426_();
        }
        if (!(itemstack.m_41720_() instanceof BucketItem)) {
            level.captureBlockSnapshots = true;
        }
        ItemStack copy = itemstack.m_41777_();
        InteractionResult ret = itemstack.m_41720_().m_6225_(context);
        if (itemstack.m_41619_()) {
            ForgeEventFactory.onPlayerDestroyItem(player, copy, context.m_43724_());
        }
        level.captureBlockSnapshots = false;
        if (ret.m_19077_()) {
            int newSize = itemstack.m_41613_();
            CompoundTag newNBT = null;
            if (itemstack.m_41783_() != null) {
                newNBT = itemstack.m_41783_().m_6426_();
            }
            List blockSnapshots = (List)level.capturedBlockSnapshots.clone();
            level.capturedBlockSnapshots.clear();
            itemstack.m_41764_(size);
            itemstack.m_41751_(nbt);
            Direction side = context.m_43719_();
            boolean eventResult = false;
            if (blockSnapshots.size() > 1) {
                eventResult = ForgeEventFactory.onMultiBlockPlace((Entity)player, blockSnapshots, side);
            } else if (blockSnapshots.size() == 1) {
                eventResult = ForgeEventFactory.onBlockPlace((Entity)player, (BlockSnapshot)blockSnapshots.get(0), side);
            }
            if (eventResult) {
                ret = InteractionResult.FAIL;
                for (BlockSnapshot blocksnapshot : Lists.reverse((List)blockSnapshots)) {
                    level.restoringBlockSnapshots = true;
                    blocksnapshot.restore(true, false);
                    level.restoringBlockSnapshots = false;
                }
            } else {
                itemstack.m_41764_(newSize);
                itemstack.m_41751_(newNBT);
                for (BlockSnapshot snap : blockSnapshots) {
                    int updateFlag = snap.getFlag();
                    BlockState oldBlock = snap.getReplacedBlock();
                    BlockState newBlock = level.m_8055_(snap.getPos());
                    newBlock.m_60696_(level, snap.getPos(), oldBlock, false);
                    level.markAndNotifyBlock(snap.getPos(), level.m_46745_(snap.getPos()), oldBlock, newBlock, updateFlag, 512);
                }
                if (player != null) {
                    player.m_36246_(Stats.f_12982_.m_12902_((Object)item));
                }
            }
        }
        level.capturedBlockSnapshots.clear();
        return ret;
    }

    public static boolean onAnvilChange(AnvilMenu container, @NotNull ItemStack left, @NotNull ItemStack right, Container outputSlot, String name, int baseCost, Player player) {
        AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost, player);
        if (MinecraftForge.EVENT_BUS.post((Event)e)) {
            return false;
        }
        if (e.getOutput().m_41619_()) {
            return true;
        }
        outputSlot.m_6836_(0, e.getOutput());
        container.setMaximumCost(e.getCost());
        container.f_39000_ = e.getMaterialCost();
        return false;
    }

    public static float onAnvilRepair(Player player, @NotNull ItemStack output, @NotNull ItemStack left, @NotNull ItemStack right) {
        AnvilRepairEvent e = new AnvilRepairEvent(player, left, right, output);
        MinecraftForge.EVENT_BUS.post((Event)e);
        return e.getBreakChance();
    }

    public static int onGrindstoneChange(@NotNull ItemStack top, @NotNull ItemStack bottom, Container outputSlot, int xp) {
        GrindstoneEvent.OnPlaceItem e = new GrindstoneEvent.OnPlaceItem(top, bottom, xp);
        if (MinecraftForge.EVENT_BUS.post((Event)e)) {
            outputSlot.m_6836_(0, ItemStack.f_41583_);
            return -1;
        }
        if (e.getOutput().m_41619_()) {
            return Integer.MIN_VALUE;
        }
        outputSlot.m_6836_(0, e.getOutput());
        return e.getXp();
    }

    public static boolean onGrindstoneTake(Container inputSlots, ContainerLevelAccess access, Function<Level, Integer> xpFunction) {
        access.m_39292_((l, p) -> {
            int xp = (Integer)xpFunction.apply((Level)l);
            GrindstoneEvent.OnTakeItem e = new GrindstoneEvent.OnTakeItem(inputSlots.m_8020_(0), inputSlots.m_8020_(1), xp);
            if (MinecraftForge.EVENT_BUS.post((Event)e)) {
                return;
            }
            if (l instanceof ServerLevel) {
                ExperienceOrb.m_147082_((ServerLevel)((ServerLevel)l), (Vec3)Vec3.m_82512_((Vec3i)p), (int)e.getXp());
            }
            l.m_46796_(1042, p, 0);
            inputSlots.m_6836_(0, e.getNewTopItem());
            inputSlots.m_6836_(1, e.getNewBottomItem());
            inputSlots.m_6596_();
        });
        return true;
    }

    public static void setCraftingPlayer(Player player) {
        craftingPlayer.set(player);
    }

    public static Player getCraftingPlayer() {
        return craftingPlayer.get();
    }

    @NotNull
    public static ItemStack getCraftingRemainingItem(@NotNull ItemStack stack) {
        if (stack.m_41720_().hasCraftingRemainingItem(stack)) {
            if (!(stack = stack.m_41720_().getCraftingRemainingItem(stack)).m_41619_() && stack.m_41763_() && stack.m_41773_() > stack.m_41776_()) {
                ForgeEventFactory.onPlayerDestroyItem(craftingPlayer.get(), stack, null);
                return ItemStack.f_41583_;
            }
            return stack;
        }
        return ItemStack.f_41583_;
    }

    public static boolean onPlayerAttackTarget(Player player, Entity target) {
        if (MinecraftForge.EVENT_BUS.post((Event)new AttackEntityEvent(player, target))) {
            return false;
        }
        ItemStack stack = player.m_21205_();
        return stack.m_41619_() || !stack.m_41720_().onLeftClickEntity(stack, player, target);
    }

    public static boolean onTravelToDimension(Entity entity, ResourceKey<Level> dimension) {
        EntityTravelToDimensionEvent event = new EntityTravelToDimensionEvent(entity, dimension);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return !event.isCanceled();
    }

    public static InteractionResult onInteractEntityAt(Player player, Entity entity, HitResult ray, InteractionHand hand) {
        Vec3 vec3d = ray.m_82450_().m_82546_(entity.m_20182_());
        return ForgeHooks.onInteractEntityAt(player, entity, vec3d, hand);
    }

    public static InteractionResult onInteractEntityAt(Player player, Entity entity, Vec3 vec3d, InteractionHand hand) {
        PlayerInteractEvent.EntityInteractSpecific evt = new PlayerInteractEvent.EntityInteractSpecific(player, hand, entity, vec3d);
        MinecraftForge.EVENT_BUS.post((Event)evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }

    public static InteractionResult onInteractEntity(Player player, Entity entity, InteractionHand hand) {
        PlayerInteractEvent.EntityInteract evt = new PlayerInteractEvent.EntityInteract(player, hand, entity);
        MinecraftForge.EVENT_BUS.post((Event)evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }

    public static InteractionResult onItemRightClick(Player player, InteractionHand hand) {
        PlayerInteractEvent.RightClickItem evt = new PlayerInteractEvent.RightClickItem(player, hand);
        MinecraftForge.EVENT_BUS.post((Event)evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }

    @Deprecated(since="1.20.1", forRemoval=true)
    public static PlayerInteractEvent.LeftClickBlock onLeftClickBlock(Player player, BlockPos pos, Direction face) {
        return ForgeHooks.onLeftClickBlock(player, pos, face, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK);
    }

    public static PlayerInteractEvent.LeftClickBlock onLeftClickBlock(Player player, BlockPos pos, Direction face, ServerboundPlayerActionPacket.Action action) {
        PlayerInteractEvent.LeftClickBlock evt = new PlayerInteractEvent.LeftClickBlock(player, pos, face, PlayerInteractEvent.LeftClickBlock.Action.convert(action));
        MinecraftForge.EVENT_BUS.post((Event)evt);
        return evt;
    }

    public static PlayerInteractEvent.LeftClickBlock onClientMineHold(Player player, BlockPos pos, Direction face) {
        PlayerInteractEvent.LeftClickBlock evt = new PlayerInteractEvent.LeftClickBlock(player, pos, face, PlayerInteractEvent.LeftClickBlock.Action.CLIENT_HOLD);
        MinecraftForge.EVENT_BUS.post((Event)evt);
        return evt;
    }

    public static PlayerInteractEvent.RightClickBlock onRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec) {
        PlayerInteractEvent.RightClickBlock evt = new PlayerInteractEvent.RightClickBlock(player, hand, pos, hitVec);
        MinecraftForge.EVENT_BUS.post((Event)evt);
        return evt;
    }

    public static void onEmptyClick(Player player, InteractionHand hand) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerInteractEvent.RightClickEmpty(player, hand));
    }

    public static void onEmptyLeftClick(Player player) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerInteractEvent.LeftClickEmpty(player));
    }

    @Nullable
    public static GameType onChangeGameType(Player player, GameType currentGameType, GameType newGameType) {
        if (currentGameType != newGameType) {
            PlayerEvent.PlayerChangeGameModeEvent evt = new PlayerEvent.PlayerChangeGameModeEvent(player, currentGameType, newGameType);
            MinecraftForge.EVENT_BUS.post((Event)evt);
            return evt.isCanceled() ? null : evt.getNewGameMode();
        }
        return newGameType;
    }

    private static LootTableContext getLootTableContext() {
        LootTableContext ctx = lootContext.get().peek();
        if (ctx == null) {
            throw new JsonParseException("Invalid call stack, could not grab json context!");
        }
        return ctx;
    }

    public static TriFunction<ResourceLocation, JsonElement, ResourceManager, Optional<LootTable>> getLootTableDeserializer(Gson gson, String directory) {
        return (location, data, resourceManager) -> {
            try {
                Resource resource = resourceManager.m_213713_(location.m_247449_(directory + "/" + location.m_135815_() + ".json")).orElse(null);
                boolean custom = resource == null || !resource.m_247137_();
                return Optional.ofNullable(ForgeHooks.loadLootTable(gson, location, data, custom));
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't parse element {}:{}", (Object)directory, location, (Object)exception);
                return Optional.empty();
            }
        };
    }

    public static LootTable loadLootTable(Gson gson, ResourceLocation name, JsonElement data, boolean custom) {
        LootTable ret;
        ArrayDeque que = lootContext.get();
        if (que == null) {
            que = Queues.newArrayDeque();
            lootContext.set(que);
        }
        try {
            que.push(new LootTableContext(name, custom));
            ret = (LootTable)gson.fromJson(data, LootTable.class);
            ret.setLootTableId(name);
        }
        catch (JsonParseException e) {
            throw e;
        }
        finally {
            que.pop();
        }
        if (!custom) {
            ret = ForgeEventFactory.loadLootTable(name, ret);
        }
        if (ret != null) {
            ret.freeze();
        }
        return ret;
    }

    public static String readPoolName(JsonObject json) {
        LootTableContext ctx = ForgeHooks.getLootTableContext();
        if (json.has("name")) {
            return GsonHelper.m_13906_((JsonObject)json, (String)"name");
        }
        if (ctx.custom) {
            return "custom#" + json.hashCode();
        }
        ++ctx.poolCount;
        return ctx.poolCount == 1 ? "main" : "pool" + (ctx.poolCount - 1);
    }

    public static FluidType getVanillaFluidType(Fluid fluid) {
        if (fluid == Fluids.f_76191_) {
            return ForgeMod.EMPTY_TYPE.get();
        }
        if (fluid == Fluids.f_76193_ || fluid == Fluids.f_76192_) {
            return ForgeMod.WATER_TYPE.get();
        }
        if (fluid == Fluids.f_76195_ || fluid == Fluids.f_76194_) {
            return ForgeMod.LAVA_TYPE.get();
        }
        if (ForgeMod.MILK.filter(milk -> milk == fluid).isPresent() || ForgeMod.FLOWING_MILK.filter(milk -> milk == fluid).isPresent()) {
            return ForgeMod.MILK_TYPE.get();
        }
        throw new RuntimeException("Mod fluids must override getFluidType.");
    }

    public static TagKey<Block> getTagFromVanillaTier(Tiers tier) {
        return switch (tier) {
            default -> throw new IncompatibleClassChangeError();
            case Tiers.WOOD -> Tags.Blocks.NEEDS_WOOD_TOOL;
            case Tiers.GOLD -> Tags.Blocks.NEEDS_GOLD_TOOL;
            case Tiers.STONE -> BlockTags.f_144286_;
            case Tiers.IRON -> BlockTags.f_144285_;
            case Tiers.DIAMOND -> BlockTags.f_144284_;
            case Tiers.NETHERITE -> Tags.Blocks.NEEDS_NETHERITE_TOOL;
        };
    }

    public static Collection<CreativeModeTab> onCheckCreativeTabs(CreativeModeTab ... vanillaTabs) {
        ArrayList<CreativeModeTab> tabs = new ArrayList<CreativeModeTab>(Arrays.asList(vanillaTabs));
        return tabs;
    }

    public static boolean onCropsGrowPre(Level level, BlockPos pos, BlockState state, boolean def) {
        BlockEvent.CropGrowEvent.Pre ev = new BlockEvent.CropGrowEvent.Pre(level, pos, state);
        MinecraftForge.EVENT_BUS.post((Event)ev);
        return ev.getResult() == Event.Result.ALLOW || ev.getResult() == Event.Result.DEFAULT && def;
    }

    public static void onCropsGrowPost(Level level, BlockPos pos, BlockState state) {
        MinecraftForge.EVENT_BUS.post((Event)new BlockEvent.CropGrowEvent.Post(level, pos, state, level.m_8055_(pos)));
    }

    @Nullable
    public static CriticalHitEvent getCriticalHit(Player player, Entity target, boolean vanillaCritical, float damageModifier) {
        CriticalHitEvent hitResult = new CriticalHitEvent(player, target, damageModifier, vanillaCritical);
        MinecraftForge.EVENT_BUS.post((Event)hitResult);
        if (hitResult.getResult() == Event.Result.ALLOW || vanillaCritical && hitResult.getResult() == Event.Result.DEFAULT) {
            return hitResult;
        }
        return null;
    }

    public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot equipmentSlot, Multimap<Attribute, AttributeModifier> attributes) {
        ItemAttributeModifierEvent event = new ItemAttributeModifierEvent(stack, equipmentSlot, attributes);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getModifiers();
    }

    public static ItemStack getProjectile(LivingEntity entity, ItemStack projectileWeaponItem, ItemStack projectile) {
        LivingGetProjectileEvent event = new LivingGetProjectileEvent(entity, projectileWeaponItem, projectile);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getProjectileItemStack();
    }

    @Nullable
    public static String getDefaultCreatorModId(@NotNull ItemStack itemStack) {
        String modId;
        Item item = itemStack.m_41720_();
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(item);
        String string = modId = registryName == null ? null : registryName.m_135827_();
        if ("minecraft".equals(modId)) {
            ResourceLocation resourceLocation;
            if (item instanceof EnchantedBookItem) {
                CompoundTag nbttagcompound;
                ResourceLocation resourceLocation2;
                ListTag enchantmentsNbt = EnchantedBookItem.m_41163_((ItemStack)itemStack);
                if (enchantmentsNbt.size() == 1 && (resourceLocation2 = ResourceLocation.m_135820_((String)(nbttagcompound = enchantmentsNbt.m_128728_(0)).m_128461_("id"))) != null && ForgeRegistries.ENCHANTMENTS.containsKey(resourceLocation2)) {
                    return resourceLocation2.m_135827_();
                }
            } else if (item instanceof PotionItem || item instanceof TippedArrowItem) {
                Potion potionType = PotionUtils.m_43579_((ItemStack)itemStack);
                ResourceLocation resourceLocation3 = ForgeRegistries.POTIONS.getKey(potionType);
                if (resourceLocation3 != null) {
                    return resourceLocation3.m_135827_();
                }
            } else if (item instanceof SpawnEggItem && (resourceLocation = ForgeRegistries.ENTITY_TYPES.getKey(((SpawnEggItem)item).m_43228_(null))) != null) {
                return resourceLocation.m_135827_();
            }
        }
        return modId;
    }

    public static boolean onFarmlandTrample(Level level, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
        if (entity.canTrample(state, pos, fallDistance)) {
            BlockEvent.FarmlandTrampleEvent event = new BlockEvent.FarmlandTrampleEvent(level, pos, state, fallDistance, entity);
            MinecraftForge.EVENT_BUS.post((Event)event);
            return !event.isCanceled();
        }
        return false;
    }

    public static int onNoteChange(Level level, BlockPos pos, BlockState state, int old, int _new) {
        NoteBlockEvent.Change event = new NoteBlockEvent.Change(level, pos, state, old, _new);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return -1;
        }
        return event.getVanillaNoteId();
    }

    public static boolean hasNoElements(Ingredient ingredient) {
        ItemStack[] items = ingredient.m_43908_();
        if (items.length == 0) {
            return true;
        }
        if (items.length == 1) {
            MutableComponent hoverName;
            Component component;
            ItemStack item = items[0];
            return item.m_41720_() == Items.f_42127_ && (component = item.m_41786_()) instanceof MutableComponent && (hoverName = (MutableComponent)component).getString().startsWith("Empty Tag: ");
        }
        return false;
    }

    @Deprecated(forRemoval=true, since="1.20.1")
    public static <T> void deserializeTagAdditions(List<TagEntry> list, JsonObject json, List<TagEntry> allList) {
    }

    @Nullable
    public static EntityDataSerializer<?> getSerializer(int id, CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> vanilla) {
        ForgeRegistry registry;
        EntityDataSerializer serializer = (EntityDataSerializer)vanilla.m_7942_(id);
        if (serializer == null && (registry = (ForgeRegistry)ForgeRegistries.ENTITY_DATA_SERIALIZERS.get()) != null) {
            serializer = (EntityDataSerializer)registry.getValue(id);
        }
        return serializer;
    }

    public static int getSerializerId(EntityDataSerializer<?> serializer, CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> vanilla) {
        ForgeRegistry registry;
        int id = vanilla.m_7447_(serializer);
        if (id < 0 && (registry = (ForgeRegistry)ForgeRegistries.ENTITY_DATA_SERIALIZERS.get()) != null) {
            id = registry.getID(serializer);
        }
        return id;
    }

    public static boolean canEntityDestroy(Level level, BlockPos pos, LivingEntity entity) {
        if (!level.m_46749_(pos)) {
            return false;
        }
        BlockState state = level.m_8055_(pos);
        return ForgeEventFactory.getMobGriefingEvent(level, (Entity)entity) && state.canEntityDestroy((BlockGetter)level, pos, (Entity)entity) && ForgeEventFactory.onEntityDestroyBlock(entity, pos, state);
    }

    public static int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        if (stack.m_41619_()) {
            return 0;
        }
        Item item = stack.m_41720_();
        int ret = stack.getBurnTime(recipeType);
        return ForgeEventFactory.getItemBurnTime(stack, ret == -1 ? VANILLA_BURNS.getOrDefault(ForgeRegistries.ITEMS.getDelegateOrThrow(item), 0) : ret, recipeType);
    }

    public static synchronized void updateBurns() {
        VANILLA_BURNS.clear();
        FurnaceBlockEntity.m_58423_().entrySet().forEach(e -> VANILLA_BURNS.put(ForgeRegistries.ITEMS.getDelegateOrThrow((Item)e.getKey()), (Integer)e.getValue()));
    }

    @Deprecated
    public static List<ItemStack> modifyLoot(List<ItemStack> list, LootContext context) {
        return ForgeHooks.modifyLoot(LootTableIdCondition.UNKNOWN_LOOT_TABLE, (ObjectArrayList<ItemStack>)ObjectArrayList.wrap((Object[])((ItemStack[])list.toArray())), context);
    }

    public static ObjectArrayList<ItemStack> modifyLoot(ResourceLocation lootTableId, ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        context.setQueriedLootTableId(lootTableId);
        LootModifierManager man = ForgeInternalHandler.getLootModifierManager();
        for (IGlobalLootModifier mod : man.getAllLootMods()) {
            generatedLoot = mod.apply(generatedLoot, context);
        }
        return generatedLoot;
    }

    public static List<String> getModPacks() {
        List<String> modpacks = ResourcePackLoader.getPackNames();
        if (modpacks.isEmpty()) {
            throw new IllegalStateException("Attempted to retrieve mod packs before they were loaded in!");
        }
        return modpacks;
    }

    public static List<String> getModPacksWithVanilla() {
        List<String> modpacks = ForgeHooks.getModPacks();
        modpacks.add("vanilla");
        return modpacks;
    }

    @Deprecated
    public static Map<EntityType<? extends LivingEntity>, AttributeSupplier> getAttributesView() {
        return Collections.unmodifiableMap(FORGE_ATTRIBUTES);
    }

    @Deprecated
    public static void modifyAttributes() {
        ModLoader.get().postEvent((Event)new EntityAttributeCreationEvent(FORGE_ATTRIBUTES));
        HashMap<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> finalMap = new HashMap<EntityType<? extends LivingEntity>, AttributeSupplier.Builder>();
        ModLoader.get().postEvent((Event)new EntityAttributeModificationEvent(finalMap));
        finalMap.forEach((k, v) -> {
            AttributeSupplier supplier = DefaultAttributes.m_22297_((EntityType)k);
            AttributeSupplier.Builder newBuilder = supplier != null ? new AttributeSupplier.Builder(supplier) : new AttributeSupplier.Builder();
            newBuilder.combine(v);
            FORGE_ATTRIBUTES.put((EntityType<? extends LivingEntity>)k, newBuilder.m_22265_());
        });
    }

    public static void onEntityEnterSection(Entity entity, long packedOldPos, long packedNewPos) {
        MinecraftForge.EVENT_BUS.post((Event)new EntityEvent.EnteringSection(entity, packedOldPos, packedNewPos));
    }

    public static ShieldBlockEvent onShieldBlock(LivingEntity blocker, DamageSource source, float blocked) {
        ShieldBlockEvent e = new ShieldBlockEvent(blocker, source, blocked);
        MinecraftForge.EVENT_BUS.post((Event)e);
        return e;
    }

    public static LivingSwapItemsEvent.Hands onLivingSwapHandItems(LivingEntity livingEntity) {
        LivingSwapItemsEvent.Hands event = new LivingSwapItemsEvent.Hands(livingEntity);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static void writeAdditionalLevelSaveData(WorldData worldData, CompoundTag levelTag) {
        CompoundTag fmlData = new CompoundTag();
        ListTag modList = new ListTag();
        ModList.get().getMods().forEach(mi -> {
            CompoundTag mod = new CompoundTag();
            mod.m_128359_("ModId", mi.getModId());
            mod.m_128359_("ModVersion", MavenVersionStringHelper.artifactVersionToString(mi.getVersion()));
            modList.add((Object)mod);
        });
        fmlData.m_128365_("LoadingModList", (Tag)modList);
        CompoundTag registries = new CompoundTag();
        fmlData.m_128365_("Registries", (Tag)registries);
        LOGGER.debug(WORLDPERSISTENCE, "Gathering id map for writing to world save {}", (Object)worldData.m_5462_());
        for (Map.Entry<ResourceLocation, ForgeRegistry.Snapshot> e : RegistryManager.ACTIVE.takeSnapshot(true).entrySet()) {
            registries.m_128365_(e.getKey().toString(), (Tag)e.getValue().write());
        }
        LOGGER.debug(WORLDPERSISTENCE, "ID Map collection complete {}", (Object)worldData.m_5462_());
        levelTag.m_128365_("fml", (Tag)fmlData);
    }

    @ApiStatus.Internal
    public static void readAdditionalLevelSaveData(CompoundTag rootTag, LevelStorageSource.LevelDirectory levelDirectory) {
        CompoundTag tag = rootTag.m_128469_("fml");
        if (tag.m_128441_("LoadingModList")) {
            ListTag modList = tag.m_128437_("LoadingModList", 10);
            HashMap<String, ArtifactVersion> mismatchedVersions = new HashMap<String, ArtifactVersion>(modList.size());
            HashMap<String, ArtifactVersion> missingVersions = new HashMap<String, ArtifactVersion>(modList.size());
            for (int i = 0; i < modList.size(); ++i) {
                CompoundTag mod = modList.m_128728_(i);
                String modId = mod.m_128461_("ModId");
                if (Objects.equals("minecraft", modId)) continue;
                String modVersion = mod.m_128461_("ModVersion");
                DefaultArtifactVersion previousVersion = new DefaultArtifactVersion(modVersion);
                ModList.get().getModContainerById(modId).ifPresentOrElse(container -> {
                    ArtifactVersion loadingVersion = container.getModInfo().getVersion();
                    if (!loadingVersion.equals(previousVersion)) {
                        mismatchedVersions.put(modId, (ArtifactVersion)previousVersion);
                    }
                }, () -> missingVersions.put(modId, (ArtifactVersion)previousVersion));
            }
            ModMismatchEvent mismatchEvent = new ModMismatchEvent(levelDirectory, mismatchedVersions, missingVersions);
            ModLoader.get().postEvent((Event)mismatchEvent);
            StringBuilder resolved = new StringBuilder("The following mods have version differences that were marked resolved:");
            StringBuilder unresolved = new StringBuilder("The following mods have version differences that were not resolved:");
            mismatchEvent.getResolved().forEachOrdered(res -> {
                String modid = res.modid();
                ModMismatchEvent.MismatchedVersionInfo diff = res.versionDifference();
                if (res.wasSelfResolved()) {
                    resolved.append(System.lineSeparator()).append(diff.isMissing() ? "%s (version %s -> MISSING, self-resolved)".formatted(modid, diff.oldVersion()) : "%s (version %s -> %s, self-resolved)".formatted(modid, diff.oldVersion(), diff.newVersion()));
                } else {
                    String resolver = res.resolver().getModId();
                    resolved.append(System.lineSeparator()).append(diff.isMissing() ? "%s (version %s -> MISSING, resolved by %s)".formatted(modid, diff.oldVersion(), resolver) : "%s (version %s -> %s, resolved by %s)".formatted(modid, diff.oldVersion(), diff.newVersion(), resolver));
                }
            });
            mismatchEvent.getUnresolved().forEachOrdered(unres -> {
                String modid = unres.modid();
                ModMismatchEvent.MismatchedVersionInfo diff = unres.versionDifference();
                unresolved.append(System.lineSeparator()).append(diff.isMissing() ? "%s (version %s -> MISSING)".formatted(modid, diff.oldVersion()) : "%s (version %s -> %s)".formatted(modid, diff.oldVersion(), diff.newVersion()));
            });
            if (mismatchEvent.anyResolved()) {
                resolved.append(System.lineSeparator()).append("Things may not work well.");
                LOGGER.debug(WORLDPERSISTENCE, resolved.toString());
            }
            if (mismatchEvent.anyUnresolved()) {
                unresolved.append(System.lineSeparator()).append("Things may not work well.");
                LOGGER.warn(WORLDPERSISTENCE, unresolved.toString());
            }
        }
        Multimap<ResourceLocation, ResourceLocation> failedElements = null;
        if (tag.m_128441_("Registries")) {
            HashMap<ResourceLocation, ForgeRegistry.Snapshot> snapshot = new HashMap<ResourceLocation, ForgeRegistry.Snapshot>();
            CompoundTag regs = tag.m_128469_("Registries");
            for (String key : regs.m_128431_()) {
                snapshot.put(new ResourceLocation(key), ForgeRegistry.Snapshot.read(regs.m_128469_(key)));
            }
            failedElements = GameData.injectSnapshot(snapshot, true, true);
        }
        if (failedElements != null && !failedElements.isEmpty()) {
            StringBuilder buf = new StringBuilder();
            buf.append("Forge Mod Loader could not load this save.\n\n").append("There are ").append(failedElements.size()).append(" unassigned registry entries in this save.\n").append("You will not be able to load until they are present again.\n\n");
            failedElements.asMap().forEach((name, entries) -> {
                buf.append("Missing ").append(name).append(":\n");
                entries.forEach(rl -> buf.append("    ").append(rl).append("\n"));
            });
            LOGGER.error(WORLDPERSISTENCE, buf.toString());
        }
    }

    public static String encodeLifecycle(Lifecycle lifecycle) {
        if (lifecycle == Lifecycle.stable()) {
            return "stable";
        }
        if (lifecycle == Lifecycle.experimental()) {
            return "experimental";
        }
        if (lifecycle instanceof Lifecycle.Deprecated) {
            Lifecycle.Deprecated dep = (Lifecycle.Deprecated)lifecycle;
            return "deprecated=" + dep.since();
        }
        throw new IllegalArgumentException("Unknown lifecycle.");
    }

    public static Lifecycle parseLifecycle(String lifecycle) {
        if (lifecycle.equals("stable")) {
            return Lifecycle.stable();
        }
        if (lifecycle.equals("experimental")) {
            return Lifecycle.experimental();
        }
        if (lifecycle.startsWith("deprecated=")) {
            return Lifecycle.deprecated((int)Integer.parseInt(lifecycle.substring(lifecycle.indexOf(61) + 1)));
        }
        throw new IllegalArgumentException("Unknown lifecycle.");
    }

    public static void saveMobEffect(CompoundTag nbt, String key, MobEffect effect) {
        ResourceLocation registryName = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        if (registryName != null) {
            nbt.m_128359_(key, registryName.toString());
        }
    }

    @Nullable
    public static MobEffect loadMobEffect(CompoundTag nbt, String key, @Nullable MobEffect fallback) {
        String registryName = nbt.m_128461_(key);
        if (Strings.isNullOrEmpty((String)registryName)) {
            return fallback;
        }
        try {
            return ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(registryName));
        }
        catch (ResourceLocationException e) {
            return fallback;
        }
    }

    public static boolean shouldSuppressEnderManAnger(EnderMan enderMan, Player player, ItemStack mask) {
        return mask.isEnderMask(player, enderMan) || MinecraftForge.EVENT_BUS.post((Event)new EnderManAngerEvent(enderMan, player));
    }

    @Nullable
    public static StructuresBecomeConfiguredFix.Conversion getStructureConversion(String originalBiome) {
        return (StructuresBecomeConfiguredFix.Conversion)((Map)FORGE_CONVERSION_MAP.get()).get(originalBiome);
    }

    public static boolean checkStructureNamespace(String biome) {
        @Nullable ResourceLocation biomeLocation = ResourceLocation.m_135820_((String)biome);
        return biomeLocation != null && !biomeLocation.m_135827_().equals("minecraft");
    }

    public static Map<PackType, Integer> readTypedPackFormats(JsonObject json) {
        ImmutableMap.Builder map = ImmutableMap.builder();
        for (PackType packType : PackType.values()) {
            String key = ForgeHooks.makePackFormatKey(packType);
            if (!json.has(key)) continue;
            map.put((Object)packType, (Object)GsonHelper.m_13927_((JsonObject)json, (String)key));
        }
        return map.buildOrThrow();
    }

    public static void writeTypedPackFormats(JsonObject json, PackMetadataSection section) {
        int packFormat = section.m_10374_();
        for (PackType packType : PackType.values()) {
            int format = section.getPackFormat(packType);
            if (format == packFormat) continue;
            json.addProperty(ForgeHooks.makePackFormatKey(packType), (Number)format);
        }
    }

    private static String makePackFormatKey(PackType packType) {
        return "forge:" + packType.name().toLowerCase(Locale.ROOT) + "_pack_format";
    }

    public static String prefixNamespace(ResourceLocation registryKey) {
        return registryKey.m_135827_().equals("minecraft") ? registryKey.m_135815_() : registryKey.m_135827_() + "/" + registryKey.m_135815_();
    }

    public static boolean canUseEntitySelectors(SharedSuggestionProvider provider) {
        if (provider.m_6761_(2)) {
            return true;
        }
        if (provider instanceof CommandSourceStack) {
            CommandSourceStack source = (CommandSourceStack)provider;
            CommandSource commandSource = source.f_81288_;
            if (commandSource instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer)commandSource;
                return PermissionAPI.getPermission(player, ForgeMod.USE_SELECTORS_PERMISSION, new PermissionDynamicContext[0]);
            }
        }
        return false;
    }

    @ApiStatus.Internal
    public static <T> HolderLookup.RegistryLookup<T> wrapRegistryLookup(final HolderLookup.RegistryLookup<T> lookup) {
        return new HolderLookup.RegistryLookup.Delegate<T>(){

            protected HolderLookup.RegistryLookup<T> m_254893_() {
                return lookup;
            }

            public Stream<HolderSet.Named<T>> m_214063_() {
                return Stream.empty();
            }

            public Optional<HolderSet.Named<T>> m_254901_(TagKey<T> key) {
                return Optional.of(HolderSet.m_255229_((HolderOwner)lookup, key));
            }
        };
    }

    public static void onLivingBreathe(LivingEntity entity, int consumeAirAmount, int refillAirAmount) {
        LivingDrownEvent drownEvent;
        boolean isAir = entity.getEyeInFluidType().isAir() || entity.m_9236_().m_8055_(BlockPos.m_274561_((double)entity.m_20185_(), (double)entity.m_20188_(), (double)entity.m_20189_())).m_60713_(Blocks.f_50628_);
        boolean canBreathe = !entity.canDrownInFluidType(entity.getEyeInFluidType()) || MobEffectUtil.m_19588_((LivingEntity)entity) || entity instanceof Player && ((Player)entity).m_150110_().f_35934_;
        LivingBreatheEvent breatheEvent = new LivingBreatheEvent(entity, isAir || canBreathe, consumeAirAmount, refillAirAmount, isAir);
        MinecraftForge.EVENT_BUS.post((Event)breatheEvent);
        if (breatheEvent.canBreathe()) {
            if (breatheEvent.canRefillAir()) {
                entity.m_20301_(Math.min(entity.m_20146_() + breatheEvent.getRefillAirAmount(), entity.m_6062_()));
            }
        } else {
            entity.m_20301_(entity.m_20146_() - breatheEvent.getConsumeAirAmount());
        }
        if (entity.m_20146_() <= 0 && !MinecraftForge.EVENT_BUS.post((Event)(drownEvent = new LivingDrownEvent(entity, entity.m_20146_() <= -20, 2.0f, 8))) && drownEvent.isDrowning()) {
            entity.m_20301_(0);
            Vec3 vec3 = entity.m_20184_();
            for (int i = 0; i < drownEvent.getBubbleCount(); ++i) {
                double d2 = entity.m_217043_().m_188500_() - entity.m_217043_().m_188500_();
                double d3 = entity.m_217043_().m_188500_() - entity.m_217043_().m_188500_();
                double d4 = entity.m_217043_().m_188500_() - entity.m_217043_().m_188500_();
                entity.m_9236_().m_7106_((ParticleOptions)ParticleTypes.f_123795_, entity.m_20185_() + d2, entity.m_20186_() + d3, entity.m_20189_() + d4, vec3.f_82479_, vec3.f_82480_, vec3.f_82481_);
            }
            if (drownEvent.getDamageAmount() > 0.0f) {
                entity.m_6469_(entity.m_269291_().m_269063_(), drownEvent.getDamageAmount());
            }
        }
        if (!isAir && !entity.m_9236_().f_46443_ && entity.m_20159_() && entity.m_20202_() != null && !entity.m_20202_().canBeRiddenUnderFluidType(entity.getEyeInFluidType(), (Entity)entity)) {
            entity.m_8127_();
        }
    }

    public static void onCreativeModeTabBuildContents(CreativeModeTab tab, ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.DisplayItemsGenerator originalGenerator, CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = new MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility>(ItemStackLinkedSet.f_260558_, (key, left, right) -> CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        originalGenerator.m_257865_(params, (stack, vis) -> {
            if (stack.m_41613_() != 1) {
                throw new IllegalArgumentException("The stack count must be 1");
            }
            entries.put(stack, vis);
        });
        ModLoader.get().postEvent((Event)new BuildCreativeModeTabContentsEvent(tab, tabKey, params, entries));
        for (Map.Entry<ItemStack, CreativeModeTab.TabVisibility> entry : entries) {
            output.m_246267_(entry.getKey(), entry.getValue());
        }
    }

    private static class LootTableContext {
        public final ResourceLocation name;
        public final boolean vanilla;
        public final boolean custom;
        public int poolCount = 0;

        private LootTableContext(ResourceLocation name, boolean custom) {
            this.name = name;
            this.custom = custom;
            this.vanilla = "minecraft".equals(this.name.m_135827_());
        }
    }

    @FunctionalInterface
    public static interface BiomeCallbackFunction {
        public Biome apply(Biome.ClimateSettings var1, BiomeSpecialEffects var2, BiomeGenerationSettings var3, MobSpawnSettings var4);
    }
}

