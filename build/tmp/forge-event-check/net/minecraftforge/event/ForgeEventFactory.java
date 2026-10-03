/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.advancements.AdvancementProgress
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands$CommandSelection
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.ReloadableServerResources
 *  net.minecraft.server.level.ChunkHolder
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.players.PlayerList
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.random.WeightedRandomList
 *  net.minecraft.world.Container
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LightningBolt
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.SpawnGroupData
 *  net.minecraft.world.entity.animal.Animal
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.monster.Zombie
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.player.Player$BedSleepingProblem
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.entity.projectile.ThrownEnderpearl
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.BaseSpawner
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Explosion
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.LevelSimulatedReader
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.SpawnData
 *  net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.levelgen.feature.ConfiguredFeature
 *  net.minecraft.world.level.portal.PortalShape
 *  net.minecraft.world.level.storage.PlayerDataStorage
 *  net.minecraft.world.level.storage.ServerLevelData
 *  net.minecraft.world.level.storage.loot.LootTable
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.eventbus.api.Event$Result
 *  net.minecraftforge.fml.LogicalSide
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.event;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.living.ZombieEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PermissionsChangedEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.level.AlterGroundEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkTicketLevelUpdatedEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgeEventFactory {
    public static boolean onMultiBlockPlace(@Nullable Entity entity, List<BlockSnapshot> blockSnapshots, Direction direction) {
        BlockSnapshot snap = blockSnapshots.get(0);
        BlockState placedAgainst = snap.getLevel().m_8055_(snap.getPos().m_121945_(direction.m_122424_()));
        BlockEvent.EntityMultiPlaceEvent event = new BlockEvent.EntityMultiPlaceEvent(blockSnapshots, placedAgainst, entity);
        return MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static boolean onBlockPlace(@Nullable Entity entity, @NotNull BlockSnapshot blockSnapshot, @NotNull Direction direction) {
        BlockState placedAgainst = blockSnapshot.getLevel().m_8055_(blockSnapshot.getPos().m_121945_(direction.m_122424_()));
        BlockEvent.EntityPlaceEvent event = new BlockEvent.EntityPlaceEvent(blockSnapshot, placedAgainst, entity);
        return MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static BlockEvent.NeighborNotifyEvent onNeighborNotify(Level level, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate) {
        BlockEvent.NeighborNotifyEvent event = new BlockEvent.NeighborNotifyEvent(level, pos, state, notifiedSides, forceRedstoneUpdate);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static boolean doPlayerHarvestCheck(Player player, BlockState state, boolean success) {
        PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, state, success);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.canHarvest();
    }

    public static float getBreakSpeed(Player player, BlockState state, float original, BlockPos pos) {
        PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, state, original, pos);
        return MinecraftForge.EVENT_BUS.post((Event)event) ? -1.0f : event.getNewSpeed();
    }

    public static void onPlayerDestroyItem(Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerDestroyItemEvent(player, stack, hand));
    }

    @ApiStatus.Internal
    public static boolean checkSpawnPlacements(EntityType<?> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random, boolean defaultResult) {
        MobSpawnEvent.SpawnPlacementCheck event = new MobSpawnEvent.SpawnPlacementCheck(entityType, level, spawnType, pos, random, defaultResult);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getResult() == Event.Result.DEFAULT ? defaultResult : event.getResult() == Event.Result.ALLOW;
    }

    public static boolean checkSpawnPosition(Mob mob, ServerLevelAccessor level, MobSpawnType spawnType) {
        MobSpawnEvent.PositionCheck event = new MobSpawnEvent.PositionCheck(mob, level, spawnType, null);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.getResult() == Event.Result.DEFAULT) {
            return mob.m_5545_((LevelAccessor)level, spawnType) && mob.m_6914_((LevelReader)level);
        }
        return event.getResult() == Event.Result.ALLOW;
    }

    public static boolean checkSpawnPositionSpawner(Mob mob, ServerLevelAccessor level, MobSpawnType spawnType, SpawnData spawnData, BaseSpawner spawner) {
        MobSpawnEvent.PositionCheck event = new MobSpawnEvent.PositionCheck(mob, level, spawnType, null);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.getResult() == Event.Result.DEFAULT) {
            return (spawnData.m_186574_().isPresent() || mob.m_5545_((LevelAccessor)level, spawnType)) && mob.m_6914_((LevelReader)level);
        }
        return event.getResult() == Event.Result.ALLOW;
    }

    @Nullable
    public static SpawnGroupData onFinalizeSpawn(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag) {
        MobSpawnEvent.FinalizeSpawn event = new MobSpawnEvent.FinalizeSpawn(mob, level, mob.m_20185_(), mob.m_20186_(), mob.m_20189_(), difficulty, spawnType, spawnData, spawnTag, null);
        boolean cancel = MinecraftForge.EVENT_BUS.post((Event)event);
        if (!cancel) {
            return mob.m_6518_(level, event.getDifficulty(), event.getSpawnType(), event.getSpawnData(), event.getSpawnTag());
        }
        return null;
    }

    @Nullable
    public static MobSpawnEvent.FinalizeSpawn onFinalizeSpawnSpawner(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag, BaseSpawner spawner) {
        MobSpawnEvent.FinalizeSpawn event = new MobSpawnEvent.FinalizeSpawn(mob, level, mob.m_20185_(), mob.m_20186_(), mob.m_20189_(), difficulty, MobSpawnType.SPAWNER, spawnData, spawnTag, spawner);
        boolean cancel = MinecraftForge.EVENT_BUS.post((Event)event);
        return cancel ? null : event;
    }

    public static Event.Result canEntityDespawn(Mob entity, ServerLevelAccessor level) {
        MobSpawnEvent.AllowDespawn event = new MobSpawnEvent.AllowDespawn(entity, level);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getResult();
    }

    public static int getItemBurnTime(@NotNull ItemStack itemStack, int burnTime, @Nullable RecipeType<?> recipeType) {
        FurnaceFuelBurnTimeEvent event = new FurnaceFuelBurnTimeEvent(itemStack, burnTime, recipeType);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getBurnTime();
    }

    public static int getExperienceDrop(LivingEntity entity, Player attackingPlayer, int originalExperience) {
        LivingExperienceDropEvent event = new LivingExperienceDropEvent(entity, attackingPlayer, originalExperience);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return 0;
        }
        return event.getDroppedExperience();
    }

    public static int getMaxSpawnPackSize(Mob entity) {
        LivingPackSizeEvent maxCanSpawnEvent = new LivingPackSizeEvent(entity);
        MinecraftForge.EVENT_BUS.post((Event)maxCanSpawnEvent);
        return maxCanSpawnEvent.getResult() == Event.Result.ALLOW ? maxCanSpawnEvent.getMaxPackSize() : entity.m_5792_();
    }

    public static Component getPlayerDisplayName(Player player, Component username) {
        PlayerEvent.NameFormat event = new PlayerEvent.NameFormat(player, username);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getDisplayname();
    }

    public static Component getPlayerTabListDisplayName(Player player) {
        PlayerEvent.TabListNameFormat event = new PlayerEvent.TabListNameFormat(player);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getDisplayName();
    }

    public static BlockState fireFluidPlaceBlockEvent(LevelAccessor level, BlockPos pos, BlockPos liquidPos, BlockState state) {
        BlockEvent.FluidPlaceBlockEvent event = new BlockEvent.FluidPlaceBlockEvent(level, pos, liquidPos, state);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getNewState();
    }

    public static ItemTooltipEvent onItemTooltip(ItemStack itemStack, @Nullable Player entityPlayer, List<Component> list, TooltipFlag flags) {
        ItemTooltipEvent event = new ItemTooltipEvent(itemStack, entityPlayer, list, flags);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static ZombieEvent.SummonAidEvent fireZombieSummonAid(Zombie zombie, Level level, int x, int y, int z, LivingEntity attacker, double summonChance) {
        ZombieEvent.SummonAidEvent summonEvent = new ZombieEvent.SummonAidEvent(zombie, level, x, y, z, attacker, summonChance);
        MinecraftForge.EVENT_BUS.post((Event)summonEvent);
        return summonEvent;
    }

    public static boolean onEntityStruckByLightning(Entity entity, LightningBolt bolt) {
        return MinecraftForge.EVENT_BUS.post((Event)new EntityStruckByLightningEvent(entity, bolt));
    }

    public static int onItemUseStart(LivingEntity entity, ItemStack item, int duration) {
        LivingEntityUseItemEvent.Start event = new LivingEntityUseItemEvent.Start(entity, item, duration);
        return MinecraftForge.EVENT_BUS.post((Event)event) ? -1 : event.getDuration();
    }

    public static int onItemUseTick(LivingEntity entity, ItemStack item, int duration) {
        LivingEntityUseItemEvent.Tick event = new LivingEntityUseItemEvent.Tick(entity, item, duration);
        return MinecraftForge.EVENT_BUS.post((Event)event) ? -1 : event.getDuration();
    }

    public static boolean onUseItemStop(LivingEntity entity, ItemStack item, int duration) {
        return MinecraftForge.EVENT_BUS.post((Event)new LivingEntityUseItemEvent.Stop(entity, item, duration));
    }

    public static ItemStack onItemUseFinish(LivingEntity entity, ItemStack item, int duration, ItemStack result) {
        LivingEntityUseItemEvent.Finish event = new LivingEntityUseItemEvent.Finish(entity, item, duration, result);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getResultStack();
    }

    public static void onStartEntityTracking(Entity entity, Player player) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerEvent.StartTracking(player, entity));
    }

    public static void onStopEntityTracking(Entity entity, Player player) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerEvent.StopTracking(player, entity));
    }

    public static void firePlayerLoadingEvent(Player player, File playerDirectory, String uuidString) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerEvent.LoadFromFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerSavingEvent(Player player, File playerDirectory, String uuidString) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerEvent.SaveToFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerLoadingEvent(Player player, PlayerDataStorage playerFileData, String uuidString) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerEvent.LoadFromFile(player, playerFileData.getPlayerDataFolder(), uuidString));
    }

    @Nullable
    public static BlockState onToolUse(BlockState originalState, UseOnContext context, ToolAction toolAction, boolean simulate) {
        BlockEvent.BlockToolModificationEvent event = new BlockEvent.BlockToolModificationEvent(originalState, context, toolAction, simulate);
        return MinecraftForge.EVENT_BUS.post((Event)event) ? null : event.getFinalState();
    }

    public static int onApplyBonemeal(@NotNull Player player, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ItemStack stack) {
        BonemealEvent event = new BonemealEvent(player, level, pos, state, stack);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return -1;
        }
        if (event.getResult() == Event.Result.ALLOW) {
            if (!level.f_46443_) {
                stack.m_41774_(1);
            }
            return 1;
        }
        return 0;
    }

    @Nullable
    public static InteractionResultHolder<ItemStack> onBucketUse(@NotNull Player player, @NotNull Level level, @NotNull ItemStack stack, @Nullable HitResult target) {
        FillBucketEvent event = new FillBucketEvent(player, stack, level, target);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return new InteractionResultHolder(InteractionResult.FAIL, (Object)stack);
        }
        if (event.getResult() == Event.Result.ALLOW) {
            if (player.m_150110_().f_35937_) {
                return new InteractionResultHolder(InteractionResult.SUCCESS, (Object)stack);
            }
            stack.m_41774_(1);
            if (stack.m_41619_()) {
                return new InteractionResultHolder(InteractionResult.SUCCESS, (Object)event.getFilledBucket());
            }
            if (!player.m_150109_().m_36054_(event.getFilledBucket())) {
                player.m_36176_(event.getFilledBucket(), false);
            }
            return new InteractionResultHolder(InteractionResult.SUCCESS, (Object)stack);
        }
        return null;
    }

    public static PlayLevelSoundEvent.AtEntity onPlaySoundAtEntity(Entity entity, Holder<SoundEvent> name, SoundSource category, float volume, float pitch) {
        PlayLevelSoundEvent.AtEntity event = new PlayLevelSoundEvent.AtEntity(entity, name, category, volume, pitch);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static PlayLevelSoundEvent.AtPosition onPlaySoundAtPosition(Level level, double x, double y, double z, Holder<SoundEvent> name, SoundSource category, float volume, float pitch) {
        PlayLevelSoundEvent.AtPosition event = new PlayLevelSoundEvent.AtPosition(level, new Vec3(x, y, z), name, category, volume, pitch);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static int onItemExpire(ItemEntity entity, @NotNull ItemStack item) {
        if (item.m_41619_()) {
            return -1;
        }
        ItemExpireEvent event = new ItemExpireEvent(entity, item.m_41619_() ? 6000 : item.m_41720_().getEntityLifespan(item, entity.m_9236_()));
        if (!MinecraftForge.EVENT_BUS.post((Event)event)) {
            return -1;
        }
        return event.getExtraLife();
    }

    public static int onItemPickup(ItemEntity entityItem, Player player) {
        EntityItemPickupEvent event = new EntityItemPickupEvent(player, entityItem);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return -1;
        }
        return event.getResult() == Event.Result.ALLOW ? 1 : 0;
    }

    public static boolean canMountEntity(Entity entityMounting, Entity entityBeingMounted, boolean isMounting) {
        boolean isCanceled = MinecraftForge.EVENT_BUS.post((Event)new EntityMountEvent(entityMounting, entityBeingMounted, entityMounting.m_9236_(), isMounting));
        if (isCanceled) {
            entityMounting.m_19890_(entityMounting.m_20185_(), entityMounting.m_20186_(), entityMounting.m_20189_(), entityMounting.f_19859_, entityMounting.f_19860_);
            return false;
        }
        return true;
    }

    public static boolean onAnimalTame(Animal animal, Player tamer) {
        return MinecraftForge.EVENT_BUS.post((Event)new AnimalTameEvent(animal, tamer));
    }

    public static Player.BedSleepingProblem onPlayerSleepInBed(Player player, Optional<BlockPos> pos) {
        PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(player, pos);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getResultStatus();
    }

    public static void onPlayerWakeup(Player player, boolean wakeImmediately, boolean updateLevel) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerWakeUpEvent(player, wakeImmediately, updateLevel));
    }

    public static void onPlayerFall(Player player, float distance, float multiplier) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerFlyableFallEvent(player, distance, multiplier));
    }

    public static boolean onPlayerSpawnSet(Player player, ResourceKey<Level> levelKey, BlockPos pos, boolean forced) {
        return MinecraftForge.EVENT_BUS.post((Event)new PlayerSetSpawnEvent(player, levelKey, pos, forced));
    }

    public static void onPlayerClone(Player player, Player oldPlayer, boolean wasDeath) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerEvent.Clone(player, oldPlayer, wasDeath));
    }

    public static boolean onExplosionStart(Level level, Explosion explosion) {
        return MinecraftForge.EVENT_BUS.post((Event)new ExplosionEvent.Start(level, explosion));
    }

    public static void onExplosionDetonate(Level level, Explosion explosion, List<Entity> list, double diameter) {
        MinecraftForge.EVENT_BUS.post((Event)new ExplosionEvent.Detonate(level, explosion, list));
    }

    public static boolean onCreateWorldSpawn(Level level, ServerLevelData settings) {
        return MinecraftForge.EVENT_BUS.post((Event)new LevelEvent.CreateSpawnPosition((LevelAccessor)level, settings));
    }

    public static float onLivingHeal(LivingEntity entity, float amount) {
        LivingHealEvent event = new LivingHealEvent(entity, amount);
        return MinecraftForge.EVENT_BUS.post((Event)event) ? 0.0f : event.getAmount();
    }

    public static boolean onPotionAttemptBrew(NonNullList<ItemStack> stacks) {
        NonNullList tmp = NonNullList.m_122780_((int)stacks.size(), (Object)ItemStack.f_41583_);
        for (int x = 0; x < tmp.size(); ++x) {
            tmp.set(x, (Object)((ItemStack)stacks.get(x)).m_41777_());
        }
        PotionBrewEvent.Pre event = new PotionBrewEvent.Pre((NonNullList<ItemStack>)tmp);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            boolean changed = false;
            for (int x = 0; x < stacks.size(); ++x) {
                changed |= ItemStack.m_41728_((ItemStack)((ItemStack)tmp.get(x)), (ItemStack)((ItemStack)stacks.get(x)));
                stacks.set(x, (Object)event.getItem(x));
            }
            if (changed) {
                ForgeEventFactory.onPotionBrewed(stacks);
            }
            return true;
        }
        return false;
    }

    public static void onPotionBrewed(NonNullList<ItemStack> brewingItemStacks) {
        MinecraftForge.EVENT_BUS.post((Event)new PotionBrewEvent.Post(brewingItemStacks));
    }

    public static void onPlayerBrewedPotion(Player player, ItemStack stack) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerBrewedPotionEvent(player, stack));
    }

    @Nullable
    public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider) {
        return ForgeEventFactory.gatherCapabilities(type, provider, null);
    }

    @Nullable
    public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider, @Nullable ICapabilityProvider parent) {
        return ForgeEventFactory.gatherCapabilities(new AttachCapabilitiesEvent<T>(type, provider), parent);
    }

    @Nullable
    private static CapabilityDispatcher gatherCapabilities(AttachCapabilitiesEvent<?> event, @Nullable ICapabilityProvider parent) {
        MinecraftForge.EVENT_BUS.post(event);
        return !event.getCapabilities().isEmpty() || parent != null ? new CapabilityDispatcher(event.getCapabilities(), event.getListeners(), parent) : null;
    }

    public static boolean fireSleepingLocationCheck(LivingEntity player, BlockPos sleepingLocation) {
        SleepingLocationCheckEvent evt = new SleepingLocationCheckEvent(player, sleepingLocation);
        MinecraftForge.EVENT_BUS.post((Event)evt);
        Event.Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Event.Result.DEFAULT) {
            return player.m_21257_().map(pos -> {
                BlockState state = player.m_9236_().m_8055_(pos);
                return state.m_60734_().isBed(state, (BlockGetter)player.m_9236_(), pos, (Entity)player);
            }).orElse(false);
        }
        return canContinueSleep == Event.Result.ALLOW;
    }

    public static boolean fireSleepingTimeCheck(Player player, Optional<BlockPos> sleepingLocation) {
        SleepingTimeCheckEvent evt = new SleepingTimeCheckEvent(player, sleepingLocation);
        MinecraftForge.EVENT_BUS.post((Event)evt);
        Event.Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Event.Result.DEFAULT) {
            return !player.m_9236_().m_46461_();
        }
        return canContinueSleep == Event.Result.ALLOW;
    }

    public static InteractionResultHolder<ItemStack> onArrowNock(ItemStack item, Level level, Player player, InteractionHand hand, boolean hasAmmo) {
        ArrowNockEvent event = new ArrowNockEvent(player, item, hand, level, hasAmmo);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return new InteractionResultHolder(InteractionResult.FAIL, (Object)item);
        }
        return event.getAction();
    }

    public static int onArrowLoose(ItemStack stack, Level level, Player player, int charge, boolean hasAmmo) {
        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, level, charge, hasAmmo);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return -1;
        }
        return event.getCharge();
    }

    public static ProjectileImpactEvent.ImpactResult onProjectileImpactResult(Projectile projectile, HitResult ray) {
        ProjectileImpactEvent event = new ProjectileImpactEvent(projectile, ray);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return ProjectileImpactEvent.ImpactResult.SKIP_ENTITY;
        }
        return event.getImpactResult();
    }

    @Nullable
    public static ProjectileImpactEvent.ImpactResult onProjectileImpactResultNullable(Projectile projectile, HitResult ray) {
        ProjectileImpactEvent event = new ProjectileImpactEvent(projectile, ray);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return null;
        }
        return event.getImpactResult();
    }

    public static boolean onProjectileImpact(Projectile projectile, HitResult ray) {
        return ForgeEventFactory.onProjectileImpactResult(projectile, ray) != ProjectileImpactEvent.ImpactResult.DEFAULT;
    }

    public static LootTable loadLootTable(ResourceLocation name, LootTable table) {
        LootTableLoadEvent event = new LootTableLoadEvent(name, table);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return LootTable.f_79105_;
        }
        return event.getTable();
    }

    public static boolean canCreateFluidSource(Level level, BlockPos pos, BlockState state, boolean def) {
        BlockEvent.CreateFluidSourceEvent evt = new BlockEvent.CreateFluidSourceEvent(level, pos, state);
        MinecraftForge.EVENT_BUS.post((Event)evt);
        Event.Result result = evt.getResult();
        return result == Event.Result.DEFAULT ? def : result == Event.Result.ALLOW;
    }

    public static Optional<PortalShape> onTrySpawnPortal(LevelAccessor level, BlockPos pos, Optional<PortalShape> size) {
        if (!size.isPresent()) {
            return size;
        }
        return !MinecraftForge.EVENT_BUS.post((Event)new BlockEvent.PortalSpawnEvent(level, pos, level.m_8055_(pos), size.get())) ? size : Optional.empty();
    }

    public static int onEnchantmentLevelSet(Level level, BlockPos pos, int enchantRow, int power, ItemStack itemStack, int enchantmentLevel) {
        EnchantmentLevelSetEvent e = new EnchantmentLevelSetEvent(level, pos, enchantRow, power, itemStack, enchantmentLevel);
        MinecraftForge.EVENT_BUS.post((Event)e);
        return e.getEnchantLevel();
    }

    public static boolean onEntityDestroyBlock(LivingEntity entity, BlockPos pos, BlockState state) {
        return !MinecraftForge.EVENT_BUS.post((Event)new LivingDestroyBlockEvent(entity, pos, state));
    }

    public static boolean getMobGriefingEvent(Level level, @Nullable Entity entity) {
        if (entity == null) {
            return level.m_46469_().m_46207_(GameRules.f_46132_);
        }
        EntityMobGriefingEvent event = new EntityMobGriefingEvent(entity);
        MinecraftForge.EVENT_BUS.post((Event)event);
        Event.Result result = event.getResult();
        return result == Event.Result.DEFAULT ? level.m_46469_().m_46207_(GameRules.f_46132_) : result == Event.Result.ALLOW;
    }

    public static SaplingGrowTreeEvent blockGrowFeature(LevelAccessor level, RandomSource randomSource, BlockPos pos, @Nullable Holder<ConfiguredFeature<?, ?>> holder) {
        SaplingGrowTreeEvent event = new SaplingGrowTreeEvent(level, randomSource, pos, holder);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static BlockState alterGround(LevelSimulatedReader level, RandomSource random, BlockPos pos, BlockState altered) {
        AlterGroundEvent event = new AlterGroundEvent(level, random, pos, altered);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getNewAlteredState();
    }

    public static void fireChunkTicketLevelUpdated(ServerLevel level, long chunkPos, int oldTicketLevel, int newTicketLevel, @Nullable ChunkHolder chunkHolder) {
        if (oldTicketLevel != newTicketLevel) {
            MinecraftForge.EVENT_BUS.post((Event)new ChunkTicketLevelUpdatedEvent(level, chunkPos, oldTicketLevel, newTicketLevel, chunkHolder));
        }
    }

    public static void fireChunkWatch(ServerPlayer entity, LevelChunk chunk, ServerLevel level) {
        MinecraftForge.EVENT_BUS.post((Event)new ChunkWatchEvent.Watch(entity, chunk, level));
    }

    public static void fireChunkUnWatch(ServerPlayer entity, ChunkPos chunkpos, ServerLevel level) {
        MinecraftForge.EVENT_BUS.post((Event)new ChunkWatchEvent.UnWatch(entity, chunkpos, level));
    }

    public static boolean onPistonMovePre(Level level, BlockPos pos, Direction direction, boolean extending) {
        return MinecraftForge.EVENT_BUS.post((Event)new PistonEvent.Pre(level, pos, direction, extending ? PistonEvent.PistonMoveType.EXTEND : PistonEvent.PistonMoveType.RETRACT));
    }

    public static boolean onPistonMovePost(Level level, BlockPos pos, Direction direction, boolean extending) {
        return MinecraftForge.EVENT_BUS.post((Event)new PistonEvent.Post(level, pos, direction, extending ? PistonEvent.PistonMoveType.EXTEND : PistonEvent.PistonMoveType.RETRACT));
    }

    public static long onSleepFinished(ServerLevel level, long newTime, long minTime) {
        SleepFinishedTimeEvent event = new SleepFinishedTimeEvent(level, newTime, minTime);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getNewTime();
    }

    public static List<PreparableReloadListener> onResourceReload(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
        AddReloadListenerEvent event = new AddReloadListenerEvent(serverResources, registryAccess);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getListeners();
    }

    public static void onCommandRegister(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection environment, CommandBuildContext context) {
        RegisterCommandsEvent event = new RegisterCommandsEvent(dispatcher, environment, context);
        MinecraftForge.EVENT_BUS.post((Event)event);
    }

    @Deprecated(forRemoval=true, since="1.20.1")
    public static EntityEvent.Size getEntitySizeForge(Entity entity, Pose pose, EntityDimensions size, float eyeHeight) {
        EntityEvent.Size evt = new EntityEvent.Size(entity, pose, size, eyeHeight);
        MinecraftForge.EVENT_BUS.post((Event)evt);
        return evt;
    }

    @Deprecated(forRemoval=true, since="1.20.1")
    public static EntityEvent.Size getEntitySizeForge(Entity entity, Pose pose, EntityDimensions oldSize, EntityDimensions newSize, float newEyeHeight) {
        EntityEvent.Size evt = new EntityEvent.Size(entity, pose, oldSize, newSize, entity.m_20192_(), newEyeHeight);
        MinecraftForge.EVENT_BUS.post((Event)evt);
        return evt;
    }

    public static boolean canLivingConvert(LivingEntity entity, EntityType<? extends LivingEntity> outcome, Consumer<Integer> timer) {
        return !MinecraftForge.EVENT_BUS.post((Event)new LivingConversionEvent.Pre(entity, outcome, timer));
    }

    public static void onLivingConvert(LivingEntity entity, LivingEntity outcome) {
        MinecraftForge.EVENT_BUS.post((Event)new LivingConversionEvent.Post(entity, outcome));
    }

    public static EntityTeleportEvent.TeleportCommand onEntityTeleportCommand(Entity entity, double targetX, double targetY, double targetZ) {
        EntityTeleportEvent.TeleportCommand event = new EntityTeleportEvent.TeleportCommand(entity, targetX, targetY, targetZ);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static EntityTeleportEvent.SpreadPlayersCommand onEntityTeleportSpreadPlayersCommand(Entity entity, double targetX, double targetY, double targetZ) {
        EntityTeleportEvent.SpreadPlayersCommand event = new EntityTeleportEvent.SpreadPlayersCommand(entity, targetX, targetY, targetZ);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static EntityTeleportEvent.EnderEntity onEnderTeleport(LivingEntity entity, double targetX, double targetY, double targetZ) {
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity(entity, targetX, targetY, targetZ);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    @ApiStatus.Internal
    public static EntityTeleportEvent.EnderPearl onEnderPearlLand(ServerPlayer entity, double targetX, double targetY, double targetZ, ThrownEnderpearl pearlEntity, float attackDamage, HitResult hitResult) {
        EntityTeleportEvent.EnderPearl event = new EntityTeleportEvent.EnderPearl(entity, targetX, targetY, targetZ, pearlEntity, attackDamage, hitResult);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static EntityTeleportEvent.ChorusFruit onChorusFruitTeleport(LivingEntity entity, double targetX, double targetY, double targetZ) {
        EntityTeleportEvent.ChorusFruit event = new EntityTeleportEvent.ChorusFruit(entity, targetX, targetY, targetZ);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static boolean onPermissionChanged(GameProfile gameProfile, int newLevel, PlayerList playerList) {
        int oldLevel = playerList.m_7873_().m_129944_(gameProfile);
        ServerPlayer player = playerList.m_11259_(gameProfile.getId());
        if (newLevel != oldLevel && player != null) {
            return MinecraftForge.EVENT_BUS.post((Event)new PermissionsChangedEvent(player, newLevel, oldLevel));
        }
        return false;
    }

    public static void firePlayerChangedDimensionEvent(Player player, ResourceKey<Level> fromDim, ResourceKey<Level> toDim) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerEvent.PlayerChangedDimensionEvent(player, fromDim, toDim));
    }

    public static void firePlayerLoggedIn(Player player) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerEvent.PlayerLoggedInEvent(player));
    }

    public static void firePlayerLoggedOut(Player player) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerEvent.PlayerLoggedOutEvent(player));
    }

    public static void firePlayerRespawnEvent(Player player, boolean endConquered) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerEvent.PlayerRespawnEvent(player, endConquered));
    }

    public static void firePlayerItemPickupEvent(Player player, ItemEntity item, ItemStack clone) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerEvent.ItemPickupEvent(player, item, clone));
    }

    public static void firePlayerCraftingEvent(Player player, ItemStack crafted, Container craftMatrix) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerEvent.ItemCraftedEvent(player, crafted, craftMatrix));
    }

    public static void firePlayerSmeltedEvent(Player player, ItemStack smelted) {
        MinecraftForge.EVENT_BUS.post((Event)new PlayerEvent.ItemSmeltedEvent(player, smelted));
    }

    public static void onRenderTickStart(float timer) {
        MinecraftForge.EVENT_BUS.post((Event)new TickEvent.RenderTickEvent(TickEvent.Phase.START, timer));
    }

    public static void onRenderTickEnd(float timer) {
        MinecraftForge.EVENT_BUS.post((Event)new TickEvent.RenderTickEvent(TickEvent.Phase.END, timer));
    }

    public static void onPlayerPreTick(Player player) {
        MinecraftForge.EVENT_BUS.post((Event)new TickEvent.PlayerTickEvent(TickEvent.Phase.START, player));
    }

    public static void onPlayerPostTick(Player player) {
        MinecraftForge.EVENT_BUS.post((Event)new TickEvent.PlayerTickEvent(TickEvent.Phase.END, player));
    }

    public static void onPreLevelTick(Level level, BooleanSupplier haveTime) {
        MinecraftForge.EVENT_BUS.post((Event)new TickEvent.LevelTickEvent(level.f_46443_ ? LogicalSide.CLIENT : LogicalSide.SERVER, TickEvent.Phase.START, level, haveTime));
    }

    public static void onPostLevelTick(Level level, BooleanSupplier haveTime) {
        MinecraftForge.EVENT_BUS.post((Event)new TickEvent.LevelTickEvent(level.f_46443_ ? LogicalSide.CLIENT : LogicalSide.SERVER, TickEvent.Phase.END, level, haveTime));
    }

    public static void onPreClientTick() {
        MinecraftForge.EVENT_BUS.post((Event)new TickEvent.ClientTickEvent(TickEvent.Phase.START));
    }

    public static void onPostClientTick() {
        MinecraftForge.EVENT_BUS.post((Event)new TickEvent.ClientTickEvent(TickEvent.Phase.END));
    }

    public static void onPreServerTick(BooleanSupplier haveTime, MinecraftServer server) {
        MinecraftForge.EVENT_BUS.post((Event)new TickEvent.ServerTickEvent(TickEvent.Phase.START, haveTime, server));
    }

    public static void onPostServerTick(BooleanSupplier haveTime, MinecraftServer server) {
        MinecraftForge.EVENT_BUS.post((Event)new TickEvent.ServerTickEvent(TickEvent.Phase.END, haveTime, server));
    }

    public static WeightedRandomList<MobSpawnSettings.SpawnerData> getPotentialSpawns(LevelAccessor level, MobCategory category, BlockPos pos, WeightedRandomList<MobSpawnSettings.SpawnerData> oldList) {
        LevelEvent.PotentialSpawns event = new LevelEvent.PotentialSpawns(level, category, pos, oldList);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return WeightedRandomList.m_146332_();
        }
        return WeightedRandomList.m_146328_(event.getSpawnerDataList());
    }

    @ApiStatus.Internal
    public static void onAdvancementEarnedEvent(Player player, Advancement earned) {
        MinecraftForge.EVENT_BUS.post((Event)new AdvancementEvent.AdvancementEarnEvent(player, earned));
    }

    @ApiStatus.Internal
    public static void onAdvancementProgressedEvent(Player player, Advancement progressed, AdvancementProgress advancementProgress, String criterion, AdvancementEvent.AdvancementProgressEvent.ProgressType progressType) {
        MinecraftForge.EVENT_BUS.post((Event)new AdvancementEvent.AdvancementProgressEvent(player, progressed, advancementProgress, criterion, progressType));
    }
}

