/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EquipmentSlot$Type
 *  net.minecraft.world.entity.LivingEntity
 */
package net.minecraftforge.items.wrapper;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.items.wrapper.EntityEquipmentInvWrapper;

public class EntityHandsInvWrapper
extends EntityEquipmentInvWrapper {
    public EntityHandsInvWrapper(LivingEntity entity) {
        super(entity, EquipmentSlot.Type.HAND);
    }
}

