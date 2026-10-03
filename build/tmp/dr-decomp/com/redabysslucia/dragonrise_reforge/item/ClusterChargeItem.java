/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.item;

import com.redabysslucia.dragonrise_reforge.entities.special.ClusterChargeEntity;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class ClusterChargeItem
extends Item {
    public ClusterChargeItem(Item.Properties properties) {
        super(properties);
    }

    public void m_7373_(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add((Component)Component.m_237115_((String)"des.dragonrise_reforge.cluster_charge").m_130940_(ChatFormatting.GRAY));
    }

    public InteractionResult m_6225_(UseOnContext context) {
        BlockPos pos = context.m_8083_();
        Direction direction = context.m_43719_();
        BlockPos relative = pos.m_121945_(direction);
        Player player = context.m_43723_();
        ItemStack stack = context.m_43722_();
        if (player != null && !this.mayPlace(player, direction, stack, relative)) {
            return InteractionResult.FAIL;
        }
        if (direction == Direction.DOWN) {
            return InteractionResult.FAIL;
        }
        Level level = context.m_43725_();
        ClusterChargeEntity entity = new ClusterChargeEntity((LivingEntity)player, level, relative, direction, this.getCornerFromHit(direction, pos, context.m_43720_()));
        if (entity.m_7088_()) {
            if (!level.m_5776_()) {
                entity.m_7084_();
                level.m_220400_((Entity)player, GameEvent.f_157810_, entity.m_20182_());
                level.m_7967_((Entity)entity);
            }
            stack.m_41774_(1);
            return InteractionResult.m_19078_((boolean)level.m_5776_());
        }
        return InteractionResult.CONSUME;
    }

    public boolean mayPlace(Player player, Direction direction, ItemStack stack, BlockPos pos) {
        return !player.m_9236_().m_151570_(pos) && player.m_36204_(pos, direction, stack);
    }

    public int getCornerFromHit(Direction face, BlockPos pos, Vec3 hitVec) {
        boolean left;
        double x = hitVec.f_82479_;
        double y = hitVec.f_82480_;
        double z = hitVec.f_82481_;
        boolean top = y > (double)pos.m_123342_() + 0.5;
        switch (face) {
            case WEST: {
                boolean bl;
                if (z < (double)pos.m_123343_() + 0.5) {
                    bl = true;
                    break;
                }
                bl = false;
                break;
            }
            case EAST: {
                boolean bl;
                if (z > (double)pos.m_123343_() + 0.5) {
                    bl = true;
                    break;
                }
                bl = false;
                break;
            }
            case SOUTH: {
                boolean bl;
                if (x < (double)pos.m_123341_() + 0.5) {
                    bl = true;
                    break;
                }
                bl = false;
                break;
            }
            case NORTH: {
                boolean bl;
                if (x > (double)pos.m_123341_() + 0.5) {
                    bl = true;
                    break;
                }
                bl = false;
                break;
            }
            default: {
                boolean bl = left = false;
            }
        }
        if (left && top) {
            return 0;
        }
        if (left) {
            return 1;
        }
        if (!top) {
            return 2;
        }
        return 3;
    }
}

