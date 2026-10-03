/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleOptions$Deserializer
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.misc.BasicParticleType;
import dev.latvian.mods.kubejs.misc.ComplexParticleType;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;

public class ParticleTypeBuilder
extends BuilderBase<ParticleType<?>> {
    public transient boolean overrideLimiter = false;
    public transient ParticleOptions.Deserializer deserializer;

    public ParticleTypeBuilder(ResourceLocation i) {
        super(i);
    }

    @Override
    public final RegistryInfo getRegistryType() {
        return RegistryInfo.PARTICLE_TYPE;
    }

    @Override
    public ParticleType<?> createObject() {
        if (this.deserializer != null) {
            return new ComplexParticleType(this.overrideLimiter, (ParticleOptions.Deserializer<ParticleOptions>)this.deserializer);
        }
        return new BasicParticleType(this.overrideLimiter);
    }

    public ParticleTypeBuilder overrideLimiter(boolean o) {
        this.overrideLimiter = o;
        return this;
    }

    public ParticleTypeBuilder deserializer(ParticleOptions.Deserializer d) {
        this.deserializer = d;
        return this;
    }
}

