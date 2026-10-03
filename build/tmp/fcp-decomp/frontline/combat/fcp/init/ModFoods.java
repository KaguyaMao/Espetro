/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.food.FoodProperties$Builder
 */
package frontline.combat.fcp.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties DELICIOUS_SNACK = new FoodProperties.Builder().m_38765_().m_38760_(5).m_38758_(0.6f).m_38767_();
    public static final FoodProperties REDBULL = new FoodProperties.Builder().m_38765_().m_38760_(5).m_38758_(0.6f).effect(() -> new MobEffectInstance(MobEffects.f_19620_, 60, 0), 1.0f).m_38767_();
}

