/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.stats.Stats
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.shapes.Shapes
 *  org.jetbrains.annotations.NotNull
 */
package com.redabysslucia.dragonrise_reforge.item;

import com.redabysslucia.dragonrise_reforge.entities.atmg.HJ8Entity;
import com.redabysslucia.dragonrise_reforge.init.ModEntities;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;

public class HJ8Deployer
extends Item {
    public HJ8Deployer() {
        super(new Item.Properties().m_41497_(Rarity.EPIC));
    }

    @NotNull
    public InteractionResult m_6225_(UseOnContext pContext) {
        Level level = pContext.m_43725_();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = pContext.m_43722_();
        BlockPos clickedPos = pContext.m_8083_();
        Direction direction = pContext.m_43719_();
        Player player = pContext.m_43723_();
        if (player == null) {
            return InteractionResult.PASS;
        }
        BlockState blockstate = level.m_8055_(clickedPos);
        BlockPos pos = blockstate.m_60812_((BlockGetter)level, clickedPos).m_83281_() ? clickedPos : clickedPos.m_121945_(direction);
        HJ8Entity HJ8Entity2 = new HJ8Entity((EntityType<HJ8Entity>)((EntityType)ModEntities.HJ8.get()), level);
        HJ8Entity2.m_6034_((double)pos.m_123341_() + 0.5, pos.m_123342_() + 1, (double)pos.m_123343_() + 0.5);
        double yOffset = this.getYOffset((LevelReader)level, pos, !Objects.equals(clickedPos, pos) && direction == Direction.UP, HJ8Entity2.m_20191_());
        HJ8Entity2.m_6027_((double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + yOffset, (double)pos.m_123343_() + 0.5);
        level.m_7967_((Entity)HJ8Entity2);
        if (!player.m_150110_().f_35937_) {
            stack.m_41774_(1);
        }
        level.m_142346_((Entity)pContext.m_43723_(), GameEvent.f_157810_, clickedPos);
        return InteractionResult.CONSUME;
    }

    public double getYOffset(LevelReader pLevel, BlockPos pPos, boolean pShouldOffsetYMore, AABB pBox) {
        AABB aabb = new AABB(pPos);
        if (pShouldOffsetYMore) {
            aabb = aabb.m_82363_(0.0, -1.0, 0.0);
        }
        Iterable iterable = pLevel.m_186431_(null, aabb);
        return 1.0 + Shapes.m_193135_((Direction.Axis)Direction.Axis.Y, (AABB)pBox, (Iterable)iterable, (double)(pShouldOffsetYMore ? -2.0 : -1.0));
    }

    @ParametersAreNonnullByDefault
    @NotNull
    public InteractionResultHolder<ItemStack> m_7203_(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.m_21120_(pHand);
        BlockHitResult blockhitresult = HJ8Deployer.m_41435_((Level)pLevel, (Player)pPlayer, (ClipContext.Fluid)ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.m_6662_() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.m_19098_((Object)itemstack);
        }
        if (!(pLevel instanceof ServerLevel)) {
            return InteractionResultHolder.m_19090_((Object)itemstack);
        }
        BlockPos blockpos = blockhitresult.m_82425_();
        if (!(pLevel.m_8055_(blockpos).m_60734_() instanceof LiquidBlock)) {
            return InteractionResultHolder.m_19098_((Object)itemstack);
        }
        if (pLevel.m_7966_(pPlayer, blockpos) && pPlayer.m_36204_(blockpos, blockhitresult.m_82434_(), itemstack)) {
            HJ8Entity HJ8Entity2 = new HJ8Entity((EntityType<HJ8Entity>)((EntityType)ModEntities.HJ8.get()), pLevel);
            HJ8Entity2.m_6034_((double)blockpos.m_123341_() + 0.5, blockpos.m_123342_(), (double)blockpos.m_123343_() + 0.5);
            pLevel.m_7967_((Entity)HJ8Entity2);
            if (!pPlayer.m_150110_().f_35937_) {
                itemstack.m_41774_(1);
            }
            pPlayer.m_36246_(Stats.f_12982_.m_12902_((Object)this));
            pLevel.m_220400_((Entity)pPlayer, GameEvent.f_157810_, HJ8Entity2.m_20182_());
            return InteractionResultHolder.m_19096_((Object)itemstack);
        }
        return InteractionResultHolder.m_19100_((Object)itemstack);
    }
}

