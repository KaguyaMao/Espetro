/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 */
package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

public class LivingGetProjectileEvent
extends LivingEvent {
    private final ItemStack projectileWeaponItemStack;
    private ItemStack projectileItemStack;

    public LivingGetProjectileEvent(LivingEntity livingEntity, ItemStack projectileWeaponItemStack, ItemStack ammo) {
        super(livingEntity);
        this.projectileWeaponItemStack = projectileWeaponItemStack;
        this.projectileItemStack = ammo;
    }

    public ItemStack getProjectileWeaponItemStack() {
        return this.projectileWeaponItemStack;
    }

    public ItemStack getProjectileItemStack() {
        return this.projectileItemStack;
    }

    public void setProjectileItemStack(ItemStack projectileItemStack) {
        this.projectileItemStack = projectileItemStack;
    }
}

