/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 */
package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Info(value="Invoked when a player right clicks on a block.\n")
public class BlockRightClickedEventJS
extends PlayerEventJS {
    private final Player player;
    private final InteractionHand hand;
    private final BlockPos pos;
    private final Direction direction;
    private BlockContainerJS block;
    private ItemStack item;

    public BlockRightClickedEventJS(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
        this.player = player;
        this.hand = hand;
        this.pos = pos;
        this.direction = direction;
    }

    @Override
    @Info(value="The player that right clicked the block.")
    public Player getEntity() {
        return this.player;
    }

    @Info(value="The block that was right clicked.")
    public BlockContainerJS getBlock() {
        if (this.block == null) {
            this.block = new BlockContainerJS(this.player.m_9236_(), this.pos);
        }
        return this.block;
    }

    @Info(value="The hand that was used to right click the block.")
    public InteractionHand getHand() {
        return this.hand;
    }

    @Info(value="The position of the block that was right clicked.")
    public ItemStack getItem() {
        if (this.item == null) {
            this.item = this.player.m_21120_(this.hand);
        }
        return this.item;
    }

    @Info(value="The face of the block being right clicked.")
    public Direction getFacing() {
        return this.direction;
    }
}

