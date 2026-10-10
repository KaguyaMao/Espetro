/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.client.particle.TextureSheetParticle
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package frontline.combat.fcp.client.particle;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import frontline.combat.fcp.client.particle.BarrelMuzzleTracking;
import frontline.combat.fcp.client.particle.FCPMuzzleParticleOption;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FCPMuzzleParticle
extends TextureSheetParticle {
    private static final float SIZE_UNIT = 0.2f;
    private final SpriteSet sprites;
    private final float fade;
    private final int animationSpeed;
    private final int frameCount;
    private final int layer;
    private final float startSize;
    private final float endSize;
    private final float targetR;
    private final float targetG;
    private final float targetB;
    private final boolean lingerSmoke;
    private final int movementDuration;
    private final double initialXd;
    private final double initialYd;
    private final double initialZd;
    private final int attachVehicleId;
    private final int attachSeatIndex;
    private double lastMuzzleX = Double.NaN;
    private double lastMuzzleY = Double.NaN;
    private double lastMuzzleZ = Double.NaN;

    protected FCPMuzzleParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprites, FCPMuzzleParticleOption options) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.fade = options.fade();
        this.animationSpeed = Math.max(1, options.animationSpeed());
        this.frameCount = Math.max(1, options.frameCount());
        this.layer = options.layer();
        this.lingerSmoke = options.lingerSmoke();
        this.movementDuration = options.movementDuration();
        this.targetR = options.red();
        this.targetG = options.green();
        this.targetB = options.blue();
        this.startSize = 0.2f * options.baseScale();
        this.endSize = 0.2f * options.targetScale();
        this.initialXd = vx;
        this.initialYd = vy;
        this.initialZd = vz;
        this.attachVehicleId = options.attachVehicleId();
        this.attachSeatIndex = options.attachSeatIndex();
        this.f_107663_ = this.startSize;
        this.f_107225_ = Math.max(1, options.life());
        this.f_107219_ = false;
        this.f_107215_ = vx;
        this.f_107216_ = vy;
        this.f_107217_ = vz;
        this.f_107231_ = this.layer == 0 ? (float)(this.f_107223_.m_188500_() * Math.PI * 2.0) : 0.0f;
        switch (this.layer) {
            case 3: {
                this.f_107226_ = 0.0f;
                this.f_107227_ = this.targetR;
                this.f_107228_ = this.targetG;
                this.f_107229_ = this.targetB;
                this.f_107230_ = 0.95f;
                this.m_108337_(sprites.m_5819_(0, 1));
                break;
            }
            case 1: {
                this.f_107226_ = 0.0f;
                this.f_107227_ = 1.0f;
                this.f_107228_ = 1.0f;
                this.f_107229_ = 1.0f;
                this.f_107230_ = 1.0f;
                this.m_108337_(sprites.m_5819_(this.f_107223_.m_188503_(this.frameCount), this.frameCount));
                break;
            }
            case 2: {
                this.f_107226_ = 0.0f;
                this.f_107227_ = 1.0f;
                this.f_107228_ = 0.95f;
                this.f_107229_ = 0.8f;
                this.f_107230_ = 1.0f;
                this.m_108337_(sprites.m_5819_(this.f_107223_.m_188503_(this.frameCount), this.frameCount));
                break;
            }
            case 0: {
                this.f_107226_ = 0.0f;
                this.f_107227_ = this.targetR;
                this.f_107228_ = this.targetG;
                this.f_107229_ = this.targetB;
                this.f_107230_ = this.lingerSmoke ? 0.24f : 0.18f;
                this.m_108337_(sprites.m_5819_(this.f_107223_.m_188503_(this.frameCount), this.frameCount));
                break;
            }
            default: {
                this.f_107226_ = 0.0f;
                this.f_107230_ = 1.0f;
                this.m_108337_(sprites.m_5819_(0, 1));
            }
        }
        this.m_107250_(this.f_107663_, this.f_107663_);
    }

    protected int m_6355_(float partialTick) {
        if (this.layer == 0) {
            return super.m_6355_(partialTick);
        }
        return 0xF000F0;
    }

    public ParticleRenderType m_7556_() {
        return ParticleRenderType.f_107431_;
    }

    public void m_5989_() {
        this.f_107209_ = this.f_107212_;
        this.f_107210_ = this.f_107213_;
        this.f_107211_ = this.f_107214_;
        if (this.f_107224_++ >= this.f_107225_) {
            this.m_107274_();
            return;
        }
        this.applyBarrelMovement();
        if (this.layer == 0) {
            if (this.lingerSmoke) {
                this.f_107215_ *= 0.9;
                this.f_107216_ = this.f_107216_ * 0.9 + 0.004;
                this.f_107217_ *= 0.9;
            } else if (this.movementDuration > 0 && this.f_107224_ <= this.movementDuration) {
                factor = (float)(this.movementDuration - this.f_107224_) / (float)this.movementDuration;
                this.clampVelocityToInitial(factor);
            }
        } else if (this.movementDuration > 0 && this.f_107224_ <= this.movementDuration) {
            factor = (float)(this.movementDuration - this.f_107224_) / (float)this.movementDuration;
            this.clampVelocityToInitial(factor);
        } else if (this.layer == 2) {
            this.f_107215_ *= 0.96;
            this.f_107216_ *= 0.96;
            this.f_107217_ *= 0.96;
        }
        this.m_6257_(this.f_107215_, this.f_107216_, this.f_107217_);
        float progress = (float)this.f_107224_ / (float)this.f_107225_;
        this.f_107663_ = Mth.m_14179_((float)progress, (float)this.startSize, (float)this.endSize);
        this.m_107250_(this.f_107663_, this.f_107663_);
        if (this.frameCount > 1 && this.layer == 0) {
            int frame = (this.f_107224_ / this.animationSpeed % this.frameCount + this.frameCount) % this.frameCount;
            this.m_108337_(this.sprites.m_5819_(frame, this.frameCount));
        }
        if (this.layer == 3 || this.layer == 1 || this.layer == 2) {
            this.f_107230_ *= this.fade;
        } else if (this.layer == 0) {
            float fadeStart;
            if (this.f_107224_ < 2) {
                this.f_107230_ *= (float)this.f_107224_ / 2.0f;
            }
            float f = fadeStart = this.lingerSmoke ? 0.45f : 0.25f;
            if (progress > fadeStart) {
                float tail = (progress - fadeStart) / (1.0f - fadeStart);
                this.f_107230_ *= 1.0f - tail * (this.lingerSmoke ? 0.04f : 0.025f);
            }
            this.f_107230_ *= this.fade;
        }
    }

    private void applyBarrelMovement() {
        if (this.attachVehicleId < 0 || this.layer != 0) {
            return;
        }
        Entity entity = this.f_107208_.m_6815_(this.attachVehicleId);
        if (!(entity instanceof VehicleEntity)) {
            return;
        }
        VehicleEntity vehicle = (VehicleEntity)entity;
        double[] last = new double[]{this.lastMuzzleX, this.lastMuzzleY, this.lastMuzzleZ};
        double[] pos = new double[]{this.f_107212_, this.f_107213_, this.f_107214_};
        BarrelMuzzleTracking.applyFollowDelta(vehicle, this.attachSeatIndex, last, pos, 0.4f);
        this.f_107212_ = pos[0];
        this.f_107213_ = pos[1];
        this.f_107214_ = pos[2];
        this.lastMuzzleX = last[0];
        this.lastMuzzleY = last[1];
        this.lastMuzzleZ = last[2];
    }

    private void clampVelocityToInitial(float factor) {
        double maxX = Math.abs(this.initialXd * (double)factor);
        double maxY = Math.abs(this.initialYd * (double)factor);
        double maxZ = Math.abs(this.initialZd * (double)factor);
        if (Math.abs(this.f_107215_) > maxX) {
            this.f_107215_ = Math.signum(this.f_107215_) * maxX;
        }
        if (Math.abs(this.f_107216_) > maxY) {
            this.f_107216_ = Math.signum(this.f_107216_) * maxY;
        }
        if (Math.abs(this.f_107217_) > maxZ) {
            this.f_107217_ = Math.signum(this.f_107217_) * maxZ;
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public static class Provider
    implements ParticleProvider<FCPMuzzleParticleOption> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(FCPMuzzleParticleOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new FCPMuzzleParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites, type);
        }
    }
}

