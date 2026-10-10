/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.LivingEntity
 */
package net.minecraftforge.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface IArmPoseTransformer {
    public void applyTransform(HumanoidModel<?> var1, LivingEntity var2, HumanoidArm var3);
}

