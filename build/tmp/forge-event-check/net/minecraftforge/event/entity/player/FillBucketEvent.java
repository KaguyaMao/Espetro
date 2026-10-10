/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.HitResult
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event$HasResult
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Cancelable
@Event.HasResult
public class FillBucketEvent
extends PlayerEvent {
    private final ItemStack current;
    private final Level level;
    @Nullable
    private final HitResult target;
    private ItemStack result;

    public FillBucketEvent(Player player, @NotNull ItemStack current, Level level, @Nullable HitResult target) {
        super(player);
        this.current = current;
        this.level = level;
        this.target = target;
    }

    @NotNull
    public ItemStack getEmptyBucket() {
        return this.current;
    }

    public Level getLevel() {
        return this.level;
    }

    @Nullable
    public HitResult getTarget() {
        return this.target;
    }

    @NotNull
    public ItemStack getFilledBucket() {
        return this.result;
    }

    public void setFilledBucket(@NotNull ItemStack bucket) {
        this.result = bucket;
    }
}

