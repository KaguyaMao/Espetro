/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.init.ModDamageTypes
 *  com.atsuishio.superbwarfare.tools.ParticleTool
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.entity.EntityTypeTest
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package frontline.combat.fcp.mixins;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import frontline.combat.fcp.client.particle.FCPMuzzleParticleOption;
import java.util.List;
import java.util.Map;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={VehicleEntity.class}, remap=false)
public abstract class VehicleBackblastMixin {
    private static final Map<String, Blast> FCP_BACKBLAST_VEHICLES = Map.of("fcp:ural_grad", new Blast(3.15, 1.0, false), "fcp:toyota_hilux_rocket_pod", new Blast(1.5879, 0.4, true), "fcp:stryker_tow", new Blast(1.275, 0.4, true));

    @Inject(method={"afterShoot"}, at={@At(value="TAIL")}, remap=false)
    private void fcp$backblast(GunData gunData, Vec3 shootVec, CallbackInfo ci) {
        VehicleEntity self = (VehicleEntity)this;
        String vehicleId = EntityType.m_20613_((EntityType)self.m_6095_()).toString();
        Blast cfg = FCP_BACKBLAST_VEHICLES.get(vehicleId);
        if (cfg == null) {
            return;
        }
        Level level = self.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        Vec3 barrelVector = self.getBarrelVector(1.0f);
        Vec3 back = barrelVector.m_82490_(-1.0);
        int turretIndex = self.computed().getTurretControllerIndex();
        Entity gunner = self.getNthEntity(turretIndex);
        AABB box = new AABB(self.m_20191_().m_82399_(), self.m_20191_().m_82399_()).m_82400_(0.75).m_82383_(barrelVector.m_82490_(-2.0)).m_82369_(barrelVector.m_82490_(-5.0));
        List caught = level2.m_142425_(EntityTypeTest.m_156916_(Entity.class), box, e -> e != self && e.m_20202_() != self);
        for (Entity entity : caught) {
            float dist = entity.m_20270_((Entity)self);
            entity.m_6469_(ModDamageTypes.causeBurnDamage((RegistryAccess)level2.m_9598_(), (Entity)gunner), 30.0f - 2.0f * dist);
            double force = 4.0 - 0.7 * (double)dist;
            entity.m_5997_(-force * barrelVector.f_82479_, -force * barrelVector.f_82480_, -force * barrelVector.f_82481_);
        }
        Vec3 muzzle = self.getShootPos(turretIndex, 1.0f);
        Vec3 breech = muzzle.m_82549_(back.m_82490_(cfg.tubeLength()));
        RandomSource rand = self.getRandom();
        double fs = cfg.flashScale();
        Vec3 b0 = breech;
        level2.m_8767_((ParticleOptions)new FCPMuzzleParticleOption(1.0f, 1.0f, 0.95f, 8, 0.86f, 1, (float)(16.0 * fs), (float)(1.5 * fs), 1, 3), b0.f_82479_, b0.f_82480_, b0.f_82481_, 0, 0.0, 0.0, 0.0, 1.0);
        Vec3 b1 = breech.m_82549_(back.m_82490_(1.0 * fs));
        level2.m_8767_((ParticleOptions)new FCPMuzzleParticleOption(1.0f, 0.9f, 0.55f, 9, 0.87f, 1, (float)(13.0 * fs), (float)(1.2 * fs), 1, 3), b1.f_82479_, b1.f_82480_, b1.f_82481_, 0, 0.0, 0.0, 0.0, 1.0);
        Vec3 b2 = breech.m_82549_(back.m_82490_(2.2 * fs));
        level2.m_8767_((ParticleOptions)new FCPMuzzleParticleOption(1.0f, 0.62f, 0.28f, 10, 0.88f, 1, (float)(9.0 * fs), (float)(1.0 * fs), 1, 3), b2.f_82479_, b2.f_82480_, b2.f_82481_, 0, 0.0, 0.0, 0.0, 1.0);
        level2.m_8767_((ParticleOptions)new FCPMuzzleParticleOption(1.0f, 1.0f, 1.0f, 12, 0.88f, 2, (float)(6.0 * fs), (float)(8.0 * fs), 9, 1), breech.f_82479_, breech.f_82480_, breech.f_82481_, 0, 0.0, 0.0, 0.0, 1.0);
        for (int i = 0; i < 12; ++i) {
            Vec3 jet = back.m_82490_((0.35 + rand.m_188500_() * 0.45) * fs).m_82520_((rand.m_188500_() - 0.5) * 0.18, (rand.m_188500_() - 0.5) * 0.18, (rand.m_188500_() - 0.5) * 0.18);
            level2.m_8767_((ParticleOptions)new FCPMuzzleParticleOption(1.0f, 0.95f, 0.75f, 8, 0.86f, 1, (float)(2.6 * fs), 0.02f, 9, 2), breech.f_82479_, breech.f_82480_, breech.f_82481_, 0, jet.f_82479_, jet.f_82480_, jet.f_82481_, 1.0);
        }
        if (cfg.rpgSmoke()) {
            Vec3 puff = breech.m_82549_(back.m_82490_(0.4));
            ParticleTool.sendParticle((ServerLevel)level2, (ParticleOptions)ParticleTypes.f_123796_, (double)puff.f_82479_, (double)puff.f_82480_, (double)puff.f_82481_, (int)30, (double)0.4, (double)0.4, (double)0.4, (double)0.005, (boolean)true);
        } else {
            Vec3 core = breech.m_82549_(back.m_82490_(0.5));
            ParticleTool.sendParticle((ServerLevel)level2, (ParticleOptions)ParticleTypes.f_123777_, (double)core.f_82479_, (double)core.f_82480_, (double)core.f_82481_, (int)10, (double)0.22, (double)0.22, (double)0.22, (double)0.01, (boolean)true);
            Vec3 mid = breech.m_82549_(back.m_82490_(1.5));
            ParticleTool.sendParticle((ServerLevel)level2, (ParticleOptions)ParticleTypes.f_123777_, (double)mid.f_82479_, (double)mid.f_82480_, (double)mid.f_82481_, (int)6, (double)0.35, (double)0.35, (double)0.35, (double)0.015, (boolean)true);
            Vec3 tail = breech.m_82549_(back.m_82490_(2.6));
            ParticleTool.sendParticle((ServerLevel)level2, (ParticleOptions)ParticleTypes.f_123777_, (double)tail.f_82479_, (double)tail.f_82480_, (double)tail.f_82481_, (int)4, (double)0.5, (double)0.5, (double)0.5, (double)0.02, (boolean)true);
        }
    }

    private record Blast(double tubeLength, double flashScale, boolean rpgSmoke) {
    }
}

