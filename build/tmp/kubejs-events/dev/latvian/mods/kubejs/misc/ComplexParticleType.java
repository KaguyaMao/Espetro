/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleOptions$Deserializer
 *  net.minecraft.core.particles.ParticleType
 */
package dev.latvian.mods.kubejs.misc;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class ComplexParticleType
extends ParticleType<ParticleOptions> {
    public ComplexParticleType(boolean bl, ParticleOptions.Deserializer<ParticleOptions> deserializer) {
        super(bl, deserializer);
    }

    public Codec<ParticleOptions> m_7652_() {
        return null;
    }
}

