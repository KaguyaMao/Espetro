/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.SpawnPlacements$Type
 *  net.minecraft.world.entity.boss.enderdragon.EnderDragon
 *  net.minecraft.world.entity.boss.wither.WitherBoss
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.WitherSkull
 *  net.minecraft.world.item.AxeItem
 *  net.minecraft.world.item.HoneycombItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.ShovelItem
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.CollisionGetter
 *  net.minecraft.world.level.Explosion
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.SignalGetter
 *  net.minecraft.world.level.block.BeaconBeamBlock
 *  net.minecraft.world.level.block.BedBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.FarmBlock
 *  net.minecraft.world.level.block.FenceGateBlock
 *  net.minecraft.world.level.block.FireBlock
 *  net.minecraft.world.level.block.HalfTransparentBlock
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.LadderBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.ObserverBlock
 *  net.minecraft.world.level.block.RepeaterBlock
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.TrapDoorBlock
 *  net.minecraft.world.level.block.WeatheringCopper
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.MapColor
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.level.pathfinder.BlockPathTypes
 *  net.minecraft.world.level.pathfinder.WalkNodeEvaluator
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.fml.loading.FMLEnvironment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.extensions;

import com.google.common.collect.BiMap;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

public interface IForgeBlock {
    private Block self() {
        return (Block)this;
    }

    default public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return this.self().m_49958_();
    }

    default public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.m_60791_();
    }

    default public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return state.m_204336_(BlockTags.f_13082_);
    }

    default public boolean makesOpenTrapdoorAboveClimbable(BlockState state, LevelReader level, BlockPos pos, BlockState trapdoorState) {
        return state.m_60734_() instanceof LadderBlock && state.m_61143_((Property)LadderBlock.f_54337_) == trapdoorState.m_61143_((Property)TrapDoorBlock.f_54117_);
    }

    default public boolean isBurning(BlockState state, BlockGetter level, BlockPos pos) {
        return this == Blocks.f_50083_ || this == Blocks.f_49991_;
    }

    default public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        return ForgeHooks.isCorrectToolForDrops(state, player);
    }

    default public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        this.self().m_5707_(level, pos, state, player);
        return level.m_7731_(pos, fluid.m_76188_(), level.f_46443_ ? 11 : 3);
    }

    default public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, @Nullable Entity player) {
        return this.self() instanceof BedBlock;
    }

    default public Optional<Vec3> getRespawnPosition(BlockState state, EntityType<?> type, LevelReader levelReader, BlockPos pos, float orientation, @Nullable LivingEntity entity) {
        Level level;
        if (this.isBed(state, (BlockGetter)levelReader, pos, (Entity)entity) && levelReader instanceof Level && BedBlock.m_49488_((Level)(level = (Level)levelReader))) {
            return BedBlock.m_260958_(type, (CollisionGetter)levelReader, (BlockPos)pos, (Direction)((Direction)state.m_61143_((Property)BedBlock.f_54117_)), (float)orientation);
        }
        return Optional.empty();
    }

    default public boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
        return state.m_60643_(level, pos, entityType);
    }

    default public void setBedOccupied(BlockState state, Level level, BlockPos pos, LivingEntity sleeper, boolean occupied) {
        level.m_7731_(pos, (BlockState)state.m_61124_((Property)BedBlock.f_49441_, (Comparable)Boolean.valueOf(occupied)), 3);
    }

    default public Direction getBedDirection(BlockState state, LevelReader level, BlockPos pos) {
        return (Direction)state.m_61143_((Property)HorizontalDirectionalBlock.f_54117_);
    }

    default public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return this.self().m_7325_();
    }

    default public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return this.self().m_7397_(level, pos, state);
    }

    default public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return false;
    }

    default public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        return false;
    }

    public boolean canSustainPlant(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4, IPlantable var5);

    default public boolean onTreeGrow(BlockState state, LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config) {
        return false;
    }

    default public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.m_60713_(Blocks.f_50093_)) {
            return (Integer)state.m_61143_((Property)FarmBlock.f_53243_) > 0;
        }
        return false;
    }

    default public boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
        return state.m_60734_() == Blocks.f_50377_ || state.m_60734_() == Blocks.f_50378_ || state.m_60734_() == Blocks.f_50386_ || state.m_60734_() == Blocks.f_50379_;
    }

    default public boolean isPortalFrame(BlockState state, BlockGetter level, BlockPos pos) {
        return state.m_60713_(Blocks.f_50080_);
    }

    default public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return 0;
    }

    default public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.m_60717_(direction);
    }

    default public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
        return state.m_204336_(BlockTags.f_278384_) ? 1.0f : 0.0f;
    }

    default public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
    }

    default public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return state.m_60796_((BlockGetter)level, pos);
    }

    default public boolean getWeakChanges(BlockState state, LevelReader level, BlockPos pos) {
        return false;
    }

    default public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return this.self().m_49962_(state);
    }

    @Nullable
    default public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        if (this.self() instanceof BeaconBeamBlock) {
            return ((BeaconBeamBlock)this.self()).m_7988_().m_41068_();
        }
        return null;
    }

    default public BlockState getStateAtViewpoint(BlockState state, BlockGetter level, BlockPos pos, Vec3 viewpoint) {
        return state;
    }

    @Nullable
    default public BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return state.m_60734_() == Blocks.f_49991_ ? BlockPathTypes.LAVA : (state.isBurning(level, pos) ? BlockPathTypes.DAMAGE_FIRE : null);
    }

    @Nullable
    default public BlockPathTypes getAdjacentBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, BlockPathTypes originalType) {
        if (state.m_60713_(Blocks.f_50685_)) {
            return BlockPathTypes.DANGER_OTHER;
        }
        if (WalkNodeEvaluator.m_77622_((BlockState)state)) {
            return BlockPathTypes.DANGER_FIRE;
        }
        return null;
    }

    default public boolean isSlimeBlock(BlockState state) {
        return state.m_60734_() == Blocks.f_50374_;
    }

    default public boolean isStickyBlock(BlockState state) {
        return state.m_60734_() == Blocks.f_50374_ || state.m_60734_() == Blocks.f_50719_;
    }

    default public boolean canStickTo(BlockState state, BlockState other) {
        if (state.m_60734_() == Blocks.f_50719_ && other.m_60734_() == Blocks.f_50374_) {
            return false;
        }
        if (state.m_60734_() == Blocks.f_50374_ && other.m_60734_() == Blocks.f_50719_) {
            return false;
        }
        return state.isStickyBlock() || other.isStickyBlock();
    }

    default public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return ((FireBlock)Blocks.f_50083_).m_221164_(state);
    }

    default public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getFlammability(level, pos, direction) > 0;
    }

    default public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
    }

    default public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return ((FireBlock)Blocks.f_50083_).m_221166_(state);
    }

    default public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction direction) {
        return state.m_204336_(level.m_6042_().f_63836_());
    }

    default public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        if (entity instanceof EnderDragon) {
            return !this.self().m_49966_().m_204336_(BlockTags.f_13069_);
        }
        if (entity instanceof WitherBoss || entity instanceof WitherSkull) {
            return state.m_60795_() || WitherBoss.m_31491_((BlockState)state);
        }
        return true;
    }

    default public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return state.m_60734_().m_6903_(explosion);
    }

    default public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        level.m_7731_(pos, Blocks.f_50016_.m_49966_(), 3);
        this.self().m_7592_(level, pos, explosion);
    }

    default public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
        return state.m_204336_(BlockTags.f_13039_) || state.m_204336_(BlockTags.f_13032_) || this.self() instanceof FenceGateBlock;
    }

    default public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
        return state.m_60734_() instanceof HalfTransparentBlock || state.m_60734_() instanceof LeavesBlock;
    }

    @Nullable
    default public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        ItemStack itemStack = context.m_43722_();
        if (!itemStack.canPerformAction(toolAction)) {
            return null;
        }
        if (ToolActions.AXE_STRIP == toolAction) {
            return AxeItem.getAxeStrippingState((BlockState)state);
        }
        if (ToolActions.AXE_SCRAPE == toolAction) {
            return WeatheringCopper.m_154899_((BlockState)state).orElse(null);
        }
        if (ToolActions.AXE_WAX_OFF == toolAction) {
            return Optional.ofNullable((Block)((BiMap)HoneycombItem.f_150864_.get()).get((Object)state.m_60734_())).map(block -> block.m_152465_(state)).orElse(null);
        }
        if (ToolActions.SHOVEL_FLATTEN == toolAction) {
            return ShovelItem.getShovelPathingState((BlockState)state);
        }
        if (ToolActions.HOE_TILL == toolAction) {
            Block block2 = state.m_60734_();
            if (block2 == Blocks.f_152549_) {
                if (!simulate && !context.m_43725_().f_46443_) {
                    Block.m_152435_((Level)context.m_43725_(), (BlockPos)context.m_8083_(), (Direction)context.m_43719_(), (ItemStack)new ItemStack((ItemLike)Items.f_151017_));
                }
                return Blocks.f_50493_.m_49966_();
            }
            if ((block2 == Blocks.f_50440_ || block2 == Blocks.f_152481_ || block2 == Blocks.f_50493_ || block2 == Blocks.f_50546_) && context.m_43725_().m_8055_(context.m_8083_().m_7494_()).m_60795_()) {
                return block2 == Blocks.f_50546_ ? Blocks.f_50493_.m_49966_() : Blocks.f_50093_.m_49966_();
            }
        }
        return null;
    }

    default public boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return state.m_60713_(Blocks.f_50616_);
    }

    default public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        if (state.m_60713_(Blocks.f_50088_)) {
            return true;
        }
        if (state.m_60713_(Blocks.f_50146_)) {
            Direction facing = (Direction)state.m_61143_((Property)RepeaterBlock.f_54117_);
            return facing == direction || facing.m_122424_() == direction;
        }
        if (state.m_60713_(Blocks.f_50455_)) {
            return direction == state.m_61143_((Property)ObserverBlock.f_52588_);
        }
        return state.m_60803_() && direction != null;
    }

    default public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        return false;
    }

    default public boolean supportsExternalFaceHiding(BlockState state) {
        if (FMLEnvironment.dist.isClient()) {
            return !ForgeHooksClient.isBlockInSolidLayer(state);
        }
        return true;
    }

    default public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
    }

    default public boolean canBeHydrated(BlockState state, BlockGetter getter, BlockPos pos, FluidState fluid, BlockPos fluidPos) {
        return fluid.canHydrate(getter, fluidPos, state, pos);
    }

    default public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor) {
        return defaultColor;
    }

    default public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        return state;
    }

    @Nullable
    default public PushReaction getPistonPushReaction(BlockState state) {
        return null;
    }
}

