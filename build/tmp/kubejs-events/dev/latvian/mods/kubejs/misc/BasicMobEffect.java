/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeMap
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  org.jetbrains.annotations.NotNull
 */
package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.misc.MobEffectBuilder;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public class BasicMobEffect
extends MobEffect {
    private final MobEffectBuilder.EffectTickCallback effectTickCallback;
    private final Map<ResourceLocation, AttributeModifier> modifierMap;
    private final Map<Attribute, AttributeModifier> attributeMap;
    private boolean modified = false;
    private final ResourceLocation id;

    public BasicMobEffect(Builder builder) {
        super(builder.category, builder.color);
        this.effectTickCallback = builder.effectTick;
        this.modifierMap = builder.attributeModifiers;
        this.attributeMap = new HashMap<Attribute, AttributeModifier>();
        this.id = builder.id;
    }

    public void m_6742_(@NotNull LivingEntity livingEntity, int i) {
        try {
            this.effectTickCallback.applyEffectTick(livingEntity, i);
        }
        catch (Throwable e) {
            ScriptType.STARTUP.console.error("Error while ticking mob effect " + String.valueOf(this.id) + " for entity " + livingEntity.m_7755_().getString(), e);
        }
    }

    private void applyAttributeModifications() {
        if (!this.modified) {
            this.modifierMap.forEach((r, m) -> this.attributeMap.put(RegistryInfo.ATTRIBUTE.getValue((ResourceLocation)r), (AttributeModifier)m));
            this.modified = true;
        }
    }

    public Map<Attribute, AttributeModifier> m_19485_() {
        this.applyAttributeModifications();
        return this.attributeMap;
    }

    public void m_6386_(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
        this.applyAttributeModifications();
        for (Map.Entry<Attribute, AttributeModifier> entry : this.attributeMap.entrySet()) {
            AttributeInstance attributeInstance = attributeMap.m_22146_(entry.getKey());
            if (attributeInstance == null) continue;
            attributeInstance.m_22130_(entry.getValue());
        }
    }

    public void m_6385_(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
        this.applyAttributeModifications();
        for (Map.Entry<Attribute, AttributeModifier> attributeAttributeModifierEntry : this.attributeMap.entrySet()) {
            AttributeInstance attributeInstance = attributeMap.m_22146_(attributeAttributeModifierEntry.getKey());
            if (attributeInstance == null) continue;
            AttributeModifier attributeModifier = attributeAttributeModifierEntry.getValue();
            attributeInstance.m_22130_(attributeModifier);
            attributeInstance.m_22125_(new AttributeModifier(attributeModifier.m_22209_(), this.m_19481_() + " " + i, this.m_7048_(i, attributeModifier), attributeModifier.m_22217_()));
        }
    }

    public boolean m_6584_(int i, int j) {
        return this.effectTickCallback != null;
    }

    public static class Builder
    extends MobEffectBuilder {
        public Builder(ResourceLocation i) {
            super(i);
        }

        @Override
        public MobEffect createObject() {
            return new BasicMobEffect(this);
        }
    }
}

