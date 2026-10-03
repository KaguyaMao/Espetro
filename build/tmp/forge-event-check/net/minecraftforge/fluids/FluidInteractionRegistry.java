/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 */
package net.minecraftforge.fluids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidType;

public final class FluidInteractionRegistry {
    private static final Map<FluidType, List<InteractionInformation>> INTERACTIONS = new HashMap<FluidType, List<InteractionInformation>>();

    public static synchronized void addInteraction(FluidType source, InteractionInformation interaction) {
        INTERACTIONS.computeIfAbsent(source, s -> new ArrayList()).add(interaction);
    }

    public static boolean canInteract(Level level, BlockPos pos) {
        FluidState state = level.m_6425_(pos);
        for (Direction direction : LiquidBlock.f_181233_) {
            BlockPos relativePos = pos.m_121945_(direction.m_122424_());
            List interactions = INTERACTIONS.getOrDefault(state.getFluidType(), Collections.emptyList());
            for (InteractionInformation interaction : interactions) {
                if (!interaction.predicate().test(level, pos, relativePos, state)) continue;
                interaction.interaction().interact(level, pos, relativePos, state);
                return true;
            }
        }
        return false;
    }

    static {
        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new InteractionInformation(ForgeMod.WATER_TYPE.get(), fluidState -> fluidState.m_76170_() ? Blocks.f_50080_.m_49966_() : Blocks.f_50652_.m_49966_()));
        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new InteractionInformation((level, currentPos, relativePos, currentState) -> level.m_8055_(currentPos.m_7495_()).m_60713_(Blocks.f_50136_) && level.m_8055_(relativePos).m_60713_(Blocks.f_50568_), Blocks.f_50137_.m_49966_()));
    }

    public record InteractionInformation(HasFluidInteraction predicate, FluidInteraction interaction) {
        public InteractionInformation(FluidType type, BlockState state) {
            this(type, (FluidState fluidState) -> state);
        }

        public InteractionInformation(HasFluidInteraction predicate, BlockState state) {
            this(predicate, (FluidState fluidState) -> state);
        }

        public InteractionInformation(FluidType type, Function<FluidState, BlockState> getState) {
            this((Level level, BlockPos currentPos, BlockPos relativePos, FluidState currentState) -> level.m_6425_(relativePos).getFluidType() == type, getState);
        }

        public InteractionInformation(HasFluidInteraction predicate, Function<FluidState, BlockState> getState) {
            this(predicate, (Level level, BlockPos currentPos, BlockPos relativePos, FluidState currentState) -> {
                level.m_46597_(currentPos, ForgeEventFactory.fireFluidPlaceBlockEvent((LevelAccessor)level, currentPos, currentPos, (BlockState)getState.apply(currentState)));
                level.m_46796_(1501, currentPos, 0);
            });
        }
    }

    @FunctionalInterface
    public static interface HasFluidInteraction {
        public boolean test(Level var1, BlockPos var2, BlockPos var3, FluidState var4);
    }

    @FunctionalInterface
    public static interface FluidInteraction {
        public void interact(Level var1, BlockPos var2, BlockPos var3, FluidState var4);
    }
}

