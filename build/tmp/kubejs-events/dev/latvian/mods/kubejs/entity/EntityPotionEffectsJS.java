/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.LivingEntity
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.entity;

import java.util.Collection;
import java.util.Map;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class EntityPotionEffectsJS {
    private final LivingEntity entity;

    public EntityPotionEffectsJS(LivingEntity e) {
        this.entity = e;
    }

    public void clear() {
        this.entity.m_21219_();
    }

    public Collection<MobEffectInstance> getActive() {
        return this.entity.m_21220_();
    }

    public Map<MobEffect, MobEffectInstance> getMap() {
        return this.entity.m_21221_();
    }

    public boolean isActive(MobEffect mobEffect) {
        return mobEffect != null && this.entity.m_21023_(mobEffect);
    }

    public int getDuration(MobEffect mobEffect) {
        if (mobEffect != null) {
            MobEffectInstance i = (MobEffectInstance)this.entity.m_21221_().get(mobEffect);
            return i == null ? 0 : i.m_19557_();
        }
        return 0;
    }

    @Nullable
    public MobEffectInstance getActive(MobEffect mobEffect) {
        return mobEffect == null ? null : this.entity.m_21124_(mobEffect);
    }

    public void add(MobEffect mobEffect) {
        this.add(mobEffect, 200);
    }

    public void add(MobEffect mobEffect, int duration) {
        this.add(mobEffect, duration, 0);
    }

    public void add(MobEffect mobEffect, int duration, int amplifier) {
        this.add(mobEffect, duration, amplifier, false, true);
    }

    public void add(MobEffect mobEffect, int duration, int amplifier, boolean ambient, boolean showParticles) {
        if (mobEffect != null) {
            this.entity.m_7292_(new MobEffectInstance(mobEffect, duration, amplifier, ambient, showParticles));
        }
    }

    public boolean isApplicable(MobEffectInstance effect) {
        return this.entity.m_7301_(effect);
    }
}

