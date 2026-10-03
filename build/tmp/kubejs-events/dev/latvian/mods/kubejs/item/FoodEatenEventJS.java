/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 */
package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.entity.EntityEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Info(value="Invoked when an entity eats food.\n")
public class FoodEatenEventJS
extends EntityEventJS {
    private final Entity entity;
    private final ItemStack item;

    public FoodEatenEventJS(LivingEntity e, ItemStack is) {
        this.entity = e;
        this.item = is;
    }

    @Override
    @Info(value="The entity that ate the food.")
    public Entity getEntity() {
        return this.entity;
    }

    @Info(value="The food that was eaten.")
    public ItemStack getItem() {
        return this.item;
    }
}

