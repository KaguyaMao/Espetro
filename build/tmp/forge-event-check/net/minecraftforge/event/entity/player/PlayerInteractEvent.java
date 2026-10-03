/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.protocol.game.ServerboundPlayerActionPacket$Action
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event$Result
 *  net.minecraftforge.fml.LogicalSide
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.event.entity.player;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerInteractEvent
extends PlayerEvent {
    private final InteractionHand hand;
    private final BlockPos pos;
    @Nullable
    private final Direction face;
    private InteractionResult cancellationResult = InteractionResult.PASS;

    private PlayerInteractEvent(Player player, InteractionHand hand, BlockPos pos, @Nullable Direction face) {
        super((Player)Preconditions.checkNotNull((Object)player, (Object)"Null player in PlayerInteractEvent!"));
        this.hand = (InteractionHand)Preconditions.checkNotNull((Object)hand, (Object)"Null hand in PlayerInteractEvent!");
        this.pos = (BlockPos)Preconditions.checkNotNull((Object)pos, (Object)"Null position in PlayerInteractEvent!");
        this.face = face;
    }

    @NotNull
    public InteractionHand getHand() {
        return this.hand;
    }

    @NotNull
    public ItemStack getItemStack() {
        return this.getEntity().m_21120_(this.hand);
    }

    @NotNull
    public BlockPos getPos() {
        return this.pos;
    }

    @Nullable
    public Direction getFace() {
        return this.face;
    }

    public Level getLevel() {
        return this.getEntity().m_9236_();
    }

    public LogicalSide getSide() {
        return this.getLevel().f_46443_ ? LogicalSide.CLIENT : LogicalSide.SERVER;
    }

    public InteractionResult getCancellationResult() {
        return this.cancellationResult;
    }

    public void setCancellationResult(InteractionResult result) {
        this.cancellationResult = result;
    }

    public static class LeftClickEmpty
    extends PlayerInteractEvent {
        public LeftClickEmpty(Player player) {
            super(player, InteractionHand.MAIN_HAND, player.m_20183_(), null);
        }
    }

    @Cancelable
    public static class LeftClickBlock
    extends PlayerInteractEvent {
        private Event.Result useBlock = Event.Result.DEFAULT;
        private Event.Result useItem = Event.Result.DEFAULT;
        private final Action action;

        @Deprecated(since="1.20.1", forRemoval=true)
        public LeftClickBlock(Player player, BlockPos pos, Direction face) {
            this(player, pos, face, Action.START);
        }

        @ApiStatus.Internal
        public LeftClickBlock(Player player, BlockPos pos, Direction face, Action action) {
            super(player, InteractionHand.MAIN_HAND, pos, face);
            this.action = action;
        }

        public Event.Result getUseBlock() {
            return this.useBlock;
        }

        public Event.Result getUseItem() {
            return this.useItem;
        }

        @NotNull
        public Action getAction() {
            return this.action;
        }

        public void setUseBlock(Event.Result triggerBlock) {
            this.useBlock = triggerBlock;
        }

        public void setUseItem(Event.Result triggerItem) {
            this.useItem = triggerItem;
        }

        public void setCanceled(boolean canceled) {
            super.setCanceled(canceled);
            if (canceled) {
                this.useBlock = Event.Result.DENY;
                this.useItem = Event.Result.DENY;
            }
        }

        public static enum Action {
            START,
            STOP,
            ABORT,
            CLIENT_HOLD;


            public static Action convert(ServerboundPlayerActionPacket.Action action) {
                return switch (action) {
                    default -> START;
                    case ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK -> START;
                    case ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK -> STOP;
                    case ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK -> ABORT;
                };
            }
        }
    }

    public static class RightClickEmpty
    extends PlayerInteractEvent {
        public RightClickEmpty(Player player, InteractionHand hand) {
            super(player, hand, player.m_20183_(), null);
        }
    }

    @Cancelable
    public static class RightClickItem
    extends PlayerInteractEvent {
        public RightClickItem(Player player, InteractionHand hand) {
            super(player, hand, player.m_20183_(), null);
        }
    }

    @Cancelable
    public static class RightClickBlock
    extends PlayerInteractEvent {
        private Event.Result useBlock = Event.Result.DEFAULT;
        private Event.Result useItem = Event.Result.DEFAULT;
        private BlockHitResult hitVec;

        public RightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec) {
            super(player, hand, pos, hitVec.m_82434_());
            this.hitVec = hitVec;
        }

        public Event.Result getUseBlock() {
            return this.useBlock;
        }

        public Event.Result getUseItem() {
            return this.useItem;
        }

        public BlockHitResult getHitVec() {
            return this.hitVec;
        }

        public void setUseBlock(Event.Result triggerBlock) {
            this.useBlock = triggerBlock;
        }

        public void setUseItem(Event.Result triggerItem) {
            this.useItem = triggerItem;
        }

        public void setCanceled(boolean canceled) {
            super.setCanceled(canceled);
            if (canceled) {
                this.useBlock = Event.Result.DENY;
                this.useItem = Event.Result.DENY;
            }
        }
    }

    @Cancelable
    public static class EntityInteract
    extends PlayerInteractEvent {
        private final Entity target;

        public EntityInteract(Player player, InteractionHand hand, Entity target) {
            super(player, hand, target.m_20183_(), null);
            this.target = target;
        }

        public Entity getTarget() {
            return this.target;
        }
    }

    @Cancelable
    public static class EntityInteractSpecific
    extends PlayerInteractEvent {
        private final Vec3 localPos;
        private final Entity target;

        public EntityInteractSpecific(Player player, InteractionHand hand, Entity target, Vec3 localPos) {
            super(player, hand, target.m_20183_(), null);
            this.localPos = localPos;
            this.target = target;
        }

        public Vec3 getLocalPos() {
            return this.localPos;
        }

        public Entity getTarget() {
            return this.target;
        }
    }
}

