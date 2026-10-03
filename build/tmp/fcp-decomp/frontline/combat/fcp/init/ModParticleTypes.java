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
package frontline.combat.fcp.init;

import com.mojang.serialization.Codec;
import frontline.combat.fcp.client.particle.FCPMuzzleParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.PARTICLE_TYPES, (String)"fcp");
    public static final RegistryObject<ParticleType<FCPMuzzleParticleOption>> MUZZLE_SMOKE = PARTICLE_TYPES.register("muzzle_smoke", () -> ModParticleTypes.create(FCPMuzzleParticleOption.CODEC, FCPMuzzleParticleOption.DESERIALIZER));
    public static final RegistryObject<ParticleType<FCPMuzzleParticleOption>> MUZZLE_BLOOM = PARTICLE_TYPES.register("muzzle_bloom", () -> ModParticleTypes.create(FCPMuzzleParticleOption.CODEC, FCPMuzzleParticleOption.DESERIALIZER));
    public static final RegistryObject<ParticleType<FCPMuzzleParticleOption>> MUZZLE_FLASH = PARTICLE_TYPES.register("muzzle_flash", () -> ModParticleTypes.create(FCPMuzzleParticleOption.CODEC, FCPMuzzleParticleOption.DESERIALIZER));
    public static final RegistryObject<ParticleType<FCPMuzzleParticleOption>> MUZZLE_BANG = PARTICLE_TYPES.register("muzzle_bang", () -> ModParticleTypes.create(FCPMuzzleParticleOption.CODEC, FCPMuzzleParticleOption.DESERIALIZER));
    public static final RegistryObject<ParticleType<FCPMuzzleParticleOption>> MUZZLE_SPARK = PARTICLE_TYPES.register("muzzle_spark", () -> ModParticleTypes.create(FCPMuzzleParticleOption.CODEC, FCPMuzzleParticleOption.DESERIALIZER));

    private static <T extends ParticleOptions> ParticleType<T> create(final Codec<T> codec, ParticleOptions.Deserializer<T> deserializer) {
        return new ParticleType<T>(true, deserializer){

            public Codec<T> m_7652_() {
                return codec;
            }
        };
    }
}

