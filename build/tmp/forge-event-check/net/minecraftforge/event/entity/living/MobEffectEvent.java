/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event$HasResult
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.event.entity.living;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MobEffectEvent
extends LivingEvent {
    @Nullable
    protected final MobEffectInstance effectInstance;

    public MobEffectEvent(LivingEntity living, MobEffectInstance effectInstance) {
        super(living);
        this.effectInstance = effectInstance;
    }

    @Nullable
    public MobEffectInstance getEffectInstance() {
        return this.effectInstance;
    }

    public static class Expired
    extends MobEffectEvent {
        public Expired(LivingEntity living, MobEffectInstance effectInstance) {
            super(living, effectInstance);
        }
    }

    public static class Added
    extends MobEffectEvent {
        private final MobEffectInstance oldEffectInstance;
        private final Entity source;

        public Added(LivingEntity living, MobEffectInstance oldEffectInstance, MobEffectInstance newEffectInstance, Entity source) {
            super(living, newEffectInstance);
            this.oldEffectInstance = oldEffectInstance;
            this.source = source;
        }

        @Override
        @NotNull
        public MobEffectInstance getEffectInstance() {
            return super.getEffectInstance();
        }

        @Nullable
        public MobEffectInstance getOldEffectInstance() {
            return this.oldEffectInstance;
        }

        @Nullable
        public Entity getEffectSource() {
            return this.source;
        }
    }

    @Event.HasResult
    public static class Applicable
    extends MobEffectEvent {
        public Applicable(LivingEntity living, @NotNull MobEffectInstance effectInstance) {
            super(living, effectInstance);
        }

        @Override
        @NotNull
        public MobEffectInstance getEffectInstance() {
            return super.getEffectInstance();
        }
    }

    @Cancelable
    public static class Remove
    extends MobEffectEvent {
        private final MobEffect effect;

        public Remove(LivingEntity living, MobEffect effect) {
            super(living, living.m_21124_(effect));
            this.effect = effect;
        }

        public Remove(LivingEntity living, MobEffectInstance effectInstance) {
            super(living, effectInstance);
            this.effect = effectInstance.m_19544_();
        }

        public MobEffect getEffect() {
            return this.effect;
        }

        @Override
        @Nullable
        public MobEffectInstance getEffectInstance() {
            return super.getEffectInstance();
        }
    }
}

