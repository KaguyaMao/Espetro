/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.eventbus.api.Cancelable
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.ApiStatus;

public class LivingSwapItemsEvent
extends LivingEvent {
    @ApiStatus.Internal
    public LivingSwapItemsEvent(LivingEntity entity) {
        super(entity);
    }

    @Cancelable
    public static class Hands
    extends LivingSwapItemsEvent {
        private ItemStack toMainHand;
        private ItemStack toOffHand;

        @ApiStatus.Internal
        public Hands(LivingEntity entity) {
            super(entity);
            this.toMainHand = entity.m_21206_();
            this.toOffHand = entity.m_21205_();
        }

        public ItemStack getItemSwappedToMainHand() {
            return this.toMainHand;
        }

        public ItemStack getItemSwappedToOffHand() {
            return this.toOffHand;
        }

        public void setItemSwappedToMainHand(ItemStack item) {
            this.toMainHand = item;
        }

        public void setItemSwappedToOffHand(ItemStack item) {
            this.toOffHand = item;
        }
    }
}

