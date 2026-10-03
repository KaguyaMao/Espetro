/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.LivingEntity
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.Nullable;

public class LootingLevelEvent
extends LivingEvent {
    @Nullable
    private final DamageSource damageSource;
    private int lootingLevel;

    public LootingLevelEvent(LivingEntity entity, @Nullable DamageSource damageSource, int lootingLevel) {
        super(entity);
        this.damageSource = damageSource;
        this.lootingLevel = lootingLevel;
    }

    @Nullable
    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public int getLootingLevel() {
        return this.lootingLevel;
    }

    public void setLootingLevel(int lootingLevel) {
        this.lootingLevel = lootingLevel;
    }
}

