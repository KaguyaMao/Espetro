/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FlowingFluid
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.fluids;

import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ForgeFlowingFluid
extends FlowingFluid {
    private final Supplier<? extends FluidType> fluidType;
    private final Supplier<? extends Fluid> flowing;
    private final Supplier<? extends Fluid> still;
    @Nullable
    private final Supplier<? extends Item> bucket;
    @Nullable
    private final Supplier<? extends LiquidBlock> block;
    private final int slopeFindDistance;
    private final int levelDecreasePerBlock;
    private final float explosionResistance;
    private final int tickRate;

    protected ForgeFlowingFluid(Properties properties) {
        this.fluidType = properties.fluidType;
        this.flowing = properties.flowing;
        this.still = properties.still;
        this.bucket = properties.bucket;
        this.block = properties.block;
        this.slopeFindDistance = properties.slopeFindDistance;
        this.levelDecreasePerBlock = properties.levelDecreasePerBlock;
        this.explosionResistance = properties.explosionResistance;
        this.tickRate = properties.tickRate;
    }

    public FluidType getFluidType() {
        return this.fluidType.get();
    }

    public Fluid m_5615_() {
        return this.flowing.get();
    }

    public Fluid m_5613_() {
        return this.still.get();
    }

    protected boolean m_6760_(Level level) {
        return false;
    }

    public boolean canConvertToSource(FluidState state, Level level, BlockPos pos) {
        return this.getFluidType().canConvertToSource(state, (LevelReader)level, pos);
    }

    protected void m_7456_(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.m_155947_() ? worldIn.m_7702_(pos) : null;
        Block.m_49892_((BlockState)state, (LevelAccessor)worldIn, (BlockPos)pos, (BlockEntity)blockEntity);
    }

    protected int m_6719_(LevelReader worldIn) {
        return this.slopeFindDistance;
    }

    protected int m_6713_(LevelReader worldIn) {
        return this.levelDecreasePerBlock;
    }

    public Item m_6859_() {
        return this.bucket != null ? this.bucket.get() : Items.f_41852_;
    }

    protected boolean m_5486_(FluidState state, BlockGetter level, BlockPos pos, Fluid fluidIn, Direction direction) {
        return direction == Direction.DOWN && !this.m_6212_(fluidIn);
    }

    public int m_6718_(LevelReader level) {
        return this.tickRate;
    }

    protected float m_6752_() {
        return this.explosionResistance;
    }

    protected BlockState m_5804_(FluidState state) {
        if (this.block != null) {
            return (BlockState)this.block.get().m_49966_().m_61124_((Property)LiquidBlock.f_54688_, (Comparable)Integer.valueOf(ForgeFlowingFluid.m_76092_((FluidState)state)));
        }
        return Blocks.f_50016_.m_49966_();
    }

    public boolean m_6212_(Fluid fluidIn) {
        return fluidIn == this.still.get() || fluidIn == this.flowing.get();
    }

    @NotNull
    public Optional<SoundEvent> m_142520_() {
        return Optional.ofNullable(this.getFluidType().getSound(SoundActions.BUCKET_FILL));
    }

    public static class Properties {
        private Supplier<? extends FluidType> fluidType;
        private Supplier<? extends Fluid> still;
        private Supplier<? extends Fluid> flowing;
        private Supplier<? extends Item> bucket;
        private Supplier<? extends LiquidBlock> block;
        private int slopeFindDistance = 4;
        private int levelDecreasePerBlock = 1;
        private float explosionResistance = 1.0f;
        private int tickRate = 5;

        public Properties(Supplier<? extends FluidType> fluidType, Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing) {
            this.fluidType = fluidType;
            this.still = still;
            this.flowing = flowing;
        }

        public Properties bucket(Supplier<? extends Item> bucket) {
            this.bucket = bucket;
            return this;
        }

        public Properties block(Supplier<? extends LiquidBlock> block) {
            this.block = block;
            return this;
        }

        public Properties slopeFindDistance(int slopeFindDistance) {
            this.slopeFindDistance = slopeFindDistance;
            return this;
        }

        public Properties levelDecreasePerBlock(int levelDecreasePerBlock) {
            this.levelDecreasePerBlock = levelDecreasePerBlock;
            return this;
        }

        public Properties explosionResistance(float explosionResistance) {
            this.explosionResistance = explosionResistance;
            return this;
        }

        public Properties tickRate(int tickRate) {
            this.tickRate = tickRate;
            return this;
        }
    }

    public static class Source
    extends ForgeFlowingFluid {
        public Source(Properties properties) {
            super(properties);
        }

        public int m_7430_(FluidState state) {
            return 8;
        }

        public boolean m_7444_(FluidState state) {
            return true;
        }
    }

    public static class Flowing
    extends ForgeFlowingFluid {
        public Flowing(Properties properties) {
            super(properties);
            this.m_76142_((FluidState)((FluidState)this.m_76144_().m_61090_()).m_61124_((Property)f_75948_, (Comparable)Integer.valueOf(7)));
        }

        protected void m_7180_(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.m_7180_(builder);
            builder.m_61104_(new Property[]{f_75948_});
        }

        public int m_7430_(FluidState state) {
            return (Integer)state.m_61143_((Property)f_75948_);
        }

        public boolean m_7444_(FluidState state) {
            return false;
        }
    }
}

