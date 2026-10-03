/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnegative
 *  net.minecraft.core.NonNullList
 *  net.minecraft.world.entity.projectile.FishingHook
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.eventbus.api.Cancelable
 */
package net.minecraftforge.event.entity.player;

import com.google.common.base.Preconditions;
import java.util.List;
import javax.annotation.Nonnegative;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class ItemFishedEvent
extends PlayerEvent {
    private final NonNullList<ItemStack> stacks = NonNullList.m_122779_();
    private final FishingHook hook;
    private int rodDamage;

    public ItemFishedEvent(List<ItemStack> stacks, int rodDamage, FishingHook hook) {
        super(hook.m_37168_());
        this.stacks.addAll(stacks);
        this.rodDamage = rodDamage;
        this.hook = hook;
    }

    public int getRodDamage() {
        return this.rodDamage;
    }

    public void damageRodBy(@Nonnegative int rodDamage) {
        Preconditions.checkArgument((rodDamage >= 0 ? 1 : 0) != 0);
        this.rodDamage = rodDamage;
    }

    public NonNullList<ItemStack> getDrops() {
        return this.stacks;
    }

    public FishingHook getHookEntity() {
        return this.hook;
    }
}

