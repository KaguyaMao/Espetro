/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleOptions$Deserializer
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package tech.vvp.vvp.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import tech.vvp.vvp.client.particle.VvpMuzzleParticleOption;

public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.PARTICLE_TYPES, (String)"vvp");
    public static final RegistryObject<ParticleType<VvpMuzzleParticleOption>> MUZZLE_SMOKE = PARTICLE_TYPES.register("muzzle_smoke", () -> ModParticleTypes.create(VvpMuzzleParticleOption.CODEC, VvpMuzzleParticleOption.DESERIALIZER));
    public static final RegistryObject<ParticleType<VvpMuzzleParticleOption>> MUZZLE_BLOOM = PARTICLE_TYPES.register("muzzle_bloom", () -> ModParticleTypes.create(VvpMuzzleParticleOption.CODEC, VvpMuzzleParticleOption.DESERIALIZER));
    public static final RegistryObject<ParticleType<VvpMuzzleParticleOption>> MUZZLE_FLASH = PARTICLE_TYPES.register("muzzle_flash", () -> ModParticleTypes.create(VvpMuzzleParticleOption.CODEC, VvpMuzzleParticleOption.DESERIALIZER));
    public static final RegistryObject<ParticleType<VvpMuzzleParticleOption>> MUZZLE_BANG = PARTICLE_TYPES.register("muzzle_bang", () -> ModParticleTypes.create(VvpMuzzleParticleOption.CODEC, VvpMuzzleParticleOption.DESERIALIZER));
    public static final RegistryObject<ParticleType<VvpMuzzleParticleOption>> MUZZLE_SPARK = PARTICLE_TYPES.register("muzzle_spark", () -> ModParticleTypes.create(VvpMuzzleParticleOption.CODEC, VvpMuzzleParticleOption.DESERIALIZER));

    private static <T extends ParticleOptions> ParticleType<T> create(final Codec<T> codec, ParticleOptions.Deserializer<T> deserializer) {
        return new ParticleType<T>(true, deserializer){

            public Codec<T> m_7652_() {
                return codec;
            }
        };
    }
}

