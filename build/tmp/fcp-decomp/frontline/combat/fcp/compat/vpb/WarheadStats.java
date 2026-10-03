/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.tools.ParticleTool$ParticleType
 *  javax.annotation.Nullable
 */
package frontline.combat.fcp.compat.vpb;

import com.atsuishio.superbwarfare.tools.ParticleTool;
import javax.annotation.Nullable;

public final class WarheadStats {
    public final float directDamage;
    public final float explosionDamage;
    public final float explosionRadius;
    @Nullable
    public final ParticleTool.ParticleType explosionParticle;
    public final int fireTime;
    public final boolean destroyBlocks;

    public WarheadStats(float directDamage, float explosionDamage, float explosionRadius, @Nullable ParticleTool.ParticleType explosionParticle, int fireTime, boolean destroyBlocks) {
        this.directDamage = directDamage;
        this.explosionDamage = explosionDamage;
        this.explosionRadius = explosionRadius;
        this.explosionParticle = explosionParticle;
        this.fireTime = fireTime;
        this.destroyBlocks = destroyBlocks;
    }

    public boolean hasExplosion() {
        return this.explosionDamage > 0.0f && this.explosionRadius > 0.0f;
    }

    public boolean hasDirectHit() {
        return this.directDamage > 0.0f;
    }

    public ParticleTool.ParticleType resolveExplosionParticle() {
        return this.explosionParticle != null ? this.explosionParticle : WarheadStats.particleTypeForRadius(this.explosionRadius);
    }

    public static ParticleTool.ParticleType particleTypeForRadius(float radius) {
        if (radius < 2.0f) {
            return ParticleTool.ParticleType.MINI;
        }
        if (radius < 4.0f) {
            return ParticleTool.ParticleType.SMALL;
        }
        if (radius < 7.0f) {
            return ParticleTool.ParticleType.MEDIUM;
        }
        if (radius < 10.0f) {
            return ParticleTool.ParticleType.LARGE;
        }
        if (radius < 20.0f) {
            return ParticleTool.ParticleType.HUGE;
        }
        return ParticleTool.ParticleType.GIANT;
    }

    public String toString() {
        return "WarheadStats{direct=" + this.directDamage + ", explosion=" + this.explosionDamage + ", radius=" + this.explosionRadius + ", particle=" + String.valueOf(this.explosionParticle != null ? this.explosionParticle : "auto") + ", fireTime=" + this.fireTime + ", destroyBlocks=" + this.destroyBlocks + "}";
    }
}

