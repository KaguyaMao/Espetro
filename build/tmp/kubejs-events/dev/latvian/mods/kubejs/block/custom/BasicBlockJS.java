/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Explosion
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.BlockRightClickedEventJS;
import dev.latvian.mods.kubejs.block.KubeJSBlockProperties;
import dev.latvian.mods.kubejs.block.PickBlockCallbackJS;
import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.AfterEntityFallenOnBlockCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.BlockExplodedCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.BlockStateMirrorCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.BlockStateModifyCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.BlockStateModifyPlacementCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.BlockStateRotateCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.CanBeReplacedCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.EntityFallenOnBlockCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.EntitySteppedOnBlockCallbackJS;
import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachment;
import dev.latvian.mods.kubejs.block.entity.BlockEntityJS;
import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BasicBlockJS
extends Block
implements BlockKJS,
SimpleWaterloggedBlock {
    public final BlockBuilder blockBuilder;
    public final VoxelShape shape;

    public BasicBlockJS(BlockBuilder p) {
        super(p.createProperties());
        this.blockBuilder = p;
        this.shape = BlockBuilder.createShape(p.customShape);
        BlockState blockState = (BlockState)this.f_49792_.m_61090_();
        if (this.blockBuilder.defaultStateModification != null) {
            BlockStateModifyCallbackJS callbackJS = new BlockStateModifyCallbackJS(blockState);
            if (this.safeCallback(this.blockBuilder.defaultStateModification, callbackJS, "Error while creating default blockState for block " + String.valueOf(p.id))) {
                this.m_49959_(callbackJS.getState());
            }
        } else if (this.blockBuilder.canBeWaterlogged()) {
            this.m_49959_((BlockState)blockState.m_61124_((Property)BlockStateProperties.f_61362_, (Comparable)Boolean.valueOf(false)));
        }
    }

    @Override
    public BlockBuilder kjs$getBlockBuilder() {
        return this.blockBuilder;
    }

    public MutableComponent m_49954_() {
        if (this.blockBuilder.displayName != null && this.blockBuilder.formattedDisplayName) {
            return Component.m_237113_((String)"").m_7220_(this.blockBuilder.displayName);
        }
        return super.m_49954_();
    }

    @Deprecated
    public VoxelShape m_5940_(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shape;
    }

    protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
        BlockBehaviour.Properties properties = this.f_60439_;
        if (properties instanceof KubeJSBlockProperties) {
            KubeJSBlockProperties kp = (KubeJSBlockProperties)properties;
            for (Property property : kp.blockBuilder.blockStateProperties) {
                builder.m_61104_(new Property[]{property});
            }
            kp.blockBuilder.blockStateProperties = Collections.unmodifiableSet(kp.blockBuilder.blockStateProperties);
        }
    }

    @Deprecated
    public FluidState m_5888_(BlockState state) {
        return state.m_61145_((Property)BlockStateProperties.f_61362_).orElse(false) != false ? Fluids.f_76193_.m_76068_(false) : super.m_5888_(state);
    }

    @Nullable
    public BlockState m_5573_(BlockPlaceContext context) {
        BlockStateModifyPlacementCallbackJS callbackJS;
        if (this.blockBuilder.placementStateModification != null && this.safeCallback(this.blockBuilder.placementStateModification, callbackJS = new BlockStateModifyPlacementCallbackJS(context, this), "Error while modifying BlockState placement of " + String.valueOf(this.blockBuilder.id))) {
            return callbackJS.getState();
        }
        if (!this.blockBuilder.canBeWaterlogged()) {
            return this.m_49966_();
        }
        return (BlockState)this.m_49966_().m_61124_((Property)BlockStateProperties.f_61362_, (Comparable)Boolean.valueOf(context.m_43725_().m_6425_(context.m_8083_()).m_76152_() == Fluids.f_76193_));
    }

    public boolean m_6864_(BlockState blockState, BlockPlaceContext context) {
        if (this.blockBuilder.canBeReplacedFunction != null) {
            CanBeReplacedCallbackJS callbackJS = new CanBeReplacedCallbackJS(context, blockState);
            return this.blockBuilder.canBeReplacedFunction.test(callbackJS);
        }
        return super.m_6864_(blockState, context);
    }

    @Deprecated
    public BlockState m_7417_(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        if (state.m_61145_((Property)BlockStateProperties.f_61362_).orElse(false).booleanValue()) {
            world.m_186469_(pos, (Fluid)Fluids.f_76193_, Fluids.f_76193_.m_6718_((LevelReader)world));
        }
        return state;
    }

    public boolean m_7420_(BlockState state, BlockGetter level, BlockPos pos) {
        return this.blockBuilder.transparent || state.m_61145_((Property)BlockStateProperties.f_61362_).orElse(false) == false;
    }

    @Deprecated
    public void m_213898_(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.blockBuilder.randomTickCallback != null) {
            RandomTickCallbackJS callback = new RandomTickCallbackJS(new BlockContainerJS((Level)level, pos), random);
            this.safeCallback(this.blockBuilder.randomTickCallback, callback, "Error while random ticking custom block ");
        }
    }

    public boolean m_6724_(BlockState state) {
        return this.blockBuilder.randomTickCallback != null;
    }

    @Deprecated
    public VoxelShape m_5909_(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return this.blockBuilder.transparent ? Shapes.m_83040_() : super.m_5909_(state, level, pos, ctx);
    }

    @Deprecated
    public float m_7749_(BlockState state, BlockGetter level, BlockPos pos) {
        return this.blockBuilder.transparent ? 1.0f : super.m_7749_(state, level, pos);
    }

    @Deprecated
    public boolean m_6104_(BlockState state, BlockState state2, Direction direction) {
        return this.blockBuilder.transparent ? state2.m_60713_((Block)this) || super.m_6104_(state, state2, direction) : super.m_6104_(state, state2, direction);
    }

    @Nullable
    private <T> boolean safeCallback(Consumer<T> consumer, T value, String errorMessage) {
        try {
            consumer.accept(value);
        }
        catch (Throwable e) {
            ScriptType.STARTUP.console.error(errorMessage, e);
            return false;
        }
        return true;
    }

    public boolean m_6044_(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        if (this.blockBuilder.canBeWaterlogged()) {
            return super.m_6044_(blockGetter, blockPos, blockState, fluid);
        }
        return false;
    }

    public boolean m_7361_(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        if (this.blockBuilder.canBeWaterlogged()) {
            return super.m_7361_(levelAccessor, blockPos, blockState, fluidState);
        }
        return false;
    }

    public ItemStack m_142598_(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        if (this.blockBuilder.canBeWaterlogged()) {
            return super.m_142598_(levelAccessor, blockPos, blockState);
        }
        return ItemStack.f_41583_;
    }

    public Optional<SoundEvent> m_142298_() {
        if (this.blockBuilder.canBeWaterlogged()) {
            return super.m_142298_();
        }
        return Optional.empty();
    }

    public void m_141947_(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (this.blockBuilder.stepOnCallback != null) {
            EntitySteppedOnBlockCallbackJS callbackJS = new EntitySteppedOnBlockCallbackJS(level, entity, blockPos, blockState);
            this.safeCallback(this.blockBuilder.stepOnCallback, callbackJS, "Error while an entity stepped on custom block ");
        } else {
            super.m_141947_(level, blockPos, blockState, entity);
        }
    }

    public void m_142072_(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float f) {
        if (this.blockBuilder.fallOnCallback != null) {
            EntityFallenOnBlockCallbackJS callbackJS = new EntityFallenOnBlockCallbackJS(level, entity, blockPos, blockState, f);
            this.safeCallback(this.blockBuilder.fallOnCallback, callbackJS, "Error while an entity fell on custom block ");
        } else {
            super.m_142072_(level, blockState, blockPos, entity, f);
        }
    }

    public void m_5548_(BlockGetter blockGetter, Entity entity) {
        if (this.blockBuilder.afterFallenOnCallback != null) {
            AfterEntityFallenOnBlockCallbackJS callbackJS = new AfterEntityFallenOnBlockCallbackJS(blockGetter, entity);
            this.safeCallback(this.blockBuilder.afterFallenOnCallback, callbackJS, "Error while bouncing entity from custom block ");
            if (!callbackJS.hasChangedVelocity()) {
                super.m_5548_(blockGetter, entity);
            }
        } else {
            super.m_5548_(blockGetter, entity);
        }
    }

    public void m_7592_(Level level, BlockPos blockPos, Explosion explosion) {
        if (this.blockBuilder.explodedCallback != null) {
            BlockExplodedCallbackJS callbackJS = new BlockExplodedCallbackJS(level, blockPos, explosion);
            this.safeCallback(this.blockBuilder.explodedCallback, callbackJS, "Error while exploding custom block ");
        } else {
            super.m_7592_(level, blockPos, explosion);
        }
    }

    public BlockState m_6843_(BlockState blockState, Rotation rotation) {
        BlockStateRotateCallbackJS callbackJS;
        if (this.blockBuilder.rotateStateModification != null && this.safeCallback(this.blockBuilder.rotateStateModification, callbackJS = new BlockStateRotateCallbackJS(blockState, rotation), "Error while rotating BlockState of ")) {
            return callbackJS.getState();
        }
        return super.m_6843_(blockState, rotation);
    }

    public BlockState m_6943_(BlockState blockState, Mirror mirror) {
        BlockStateMirrorCallbackJS callbackJS;
        if (this.blockBuilder.mirrorStateModification != null && this.safeCallback(this.blockBuilder.mirrorStateModification, callbackJS = new BlockStateMirrorCallbackJS(blockState, mirror), "Error while mirroring BlockState of ")) {
            return callbackJS.getState();
        }
        return super.m_6943_(blockState, mirror);
    }

    public InteractionResult m_6227_(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (this.blockBuilder.rightClick != null) {
            if (!level.m_5776_()) {
                this.blockBuilder.rightClick.accept(new BlockRightClickedEventJS(player, hand, pos, hit.m_82434_()));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public void m_6810_(BlockState state, Level level, BlockPos pos, BlockState newState, boolean bl) {
        if (!state.m_60713_(newState.m_60734_())) {
            BlockEntityAttachment[] blockEntityAttachmentArray = level.m_7702_(pos);
            if (blockEntityAttachmentArray instanceof BlockEntityJS) {
                BlockEntityJS entity = (BlockEntityJS)blockEntityAttachmentArray;
                if (level instanceof ServerLevel) {
                    for (BlockEntityAttachment attachment : entity.attachments) {
                        attachment.onRemove(newState);
                    }
                }
                level.m_46717_(pos, (Block)this);
            }
            super.m_6810_(state, level, pos, newState, bl);
        }
    }

    public void m_6402_(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        BlockEntity blockEntity;
        if (livingEntity != null && !level.m_5776_() && (blockEntity = level.m_7702_(blockPos)) instanceof BlockEntityJS) {
            BlockEntityJS e = (BlockEntityJS)blockEntity;
            e.placerId = livingEntity.m_20148_();
        }
    }

    public ItemStack m_7397_(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        if (this.blockBuilder.pickBlockCallback != null) {
            PickBlockCallbackJS callback = new PickBlockCallbackJS(blockGetter, blockPos, blockState);
            this.safeCallback(this.blockBuilder.pickBlockCallback, callback, "Error while getting pick block item ");
            if (callback.item != null) {
                return new ItemStack((ItemLike)callback.item);
            }
        }
        return super.m_7397_(blockGetter, blockPos, blockState);
    }

    public static class WithEntity
    extends BasicBlockJS
    implements EntityBlock {
        public WithEntity(BlockBuilder p) {
            super(p);
        }

        @Nullable
        public BlockEntity m_142194_(BlockPos blockPos, BlockState blockState) {
            return this.blockBuilder.blockEntityInfo.createBlockEntity(blockPos, blockState);
        }

        @Nullable
        public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
            return this.blockBuilder.blockEntityInfo.getTicker(level);
        }
    }

    public static class Builder
    extends BlockBuilder {
        public Builder(ResourceLocation i) {
            super(i);
        }

        @Override
        public Block createObject() {
            return this.blockEntityInfo != null ? new WithEntity(this) : new BasicBlockJS(this);
        }
    }
}

