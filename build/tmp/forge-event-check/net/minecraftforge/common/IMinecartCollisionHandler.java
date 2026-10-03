/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.phys.AABB
 */
package net.minecraftforge.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.phys.AABB;

public interface IMinecartCollisionHandler {
    public void onEntityCollision(AbstractMinecart var1, Entity var2);

    public AABB getCollisionBox(AbstractMinecart var1, Entity var2);

    public AABB getMinecartCollisionBox(AbstractMinecart var1);

    public AABB getBoundingBox(AbstractMinecart var1);
}

