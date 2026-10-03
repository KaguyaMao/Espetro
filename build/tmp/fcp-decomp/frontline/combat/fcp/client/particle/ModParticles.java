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
package frontline.combat.fcp.client.particle;

import frontline.combat.fcp.client.particle.FCPMuzzleParticle;
import frontline.combat.fcp.init.ModParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="fcp", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public class ModParticles {
    @SubscribeEvent
    public static void registerProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet((ParticleType)ModParticleTypes.MUZZLE_SMOKE.get(), FCPMuzzleParticle.Provider::new);
        event.registerSpriteSet((ParticleType)ModParticleTypes.MUZZLE_BLOOM.get(), FCPMuzzleParticle.Provider::new);
        event.registerSpriteSet((ParticleType)ModParticleTypes.MUZZLE_FLASH.get(), FCPMuzzleParticle.Provider::new);
        event.registerSpriteSet((ParticleType)ModParticleTypes.MUZZLE_BANG.get(), FCPMuzzleParticle.Provider::new);
        event.registerSpriteSet((ParticleType)ModParticleTypes.MUZZLE_SPARK.get(), FCPMuzzleParticle.Provider::new);
    }
}

