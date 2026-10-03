/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterParticleProvidersEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package tech.vvp.vvp.client.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.client.particle.VvpMuzzleParticle;
import tech.vvp.vvp.init.ModParticleTypes;

@Mod.EventBusSubscriber(modid="vvp", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public class ModParticles {
    @SubscribeEvent
    public static void registerProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet((ParticleType)ModParticleTypes.MUZZLE_SMOKE.get(), VvpMuzzleParticle.Provider::new);
        event.registerSpriteSet((ParticleType)ModParticleTypes.MUZZLE_BLOOM.get(), VvpMuzzleParticle.Provider::new);
        event.registerSpriteSet((ParticleType)ModParticleTypes.MUZZLE_FLASH.get(), VvpMuzzleParticle.Provider::new);
        event.registerSpriteSet((ParticleType)ModParticleTypes.MUZZLE_BANG.get(), VvpMuzzleParticle.Provider::new);
        event.registerSpriteSet((ParticleType)ModParticleTypes.MUZZLE_SPARK.get(), VvpMuzzleParticle.Provider::new);
    }
}

