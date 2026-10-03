/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.Mod
 *  com.atsuishio.superbwarfare.client.particle.CustomCloudOption
 *  com.atsuishio.superbwarfare.config.server.ExplosionConfig
 *  com.atsuishio.superbwarfare.entity.projectile.DestroyableProjectile
 *  com.atsuishio.superbwarfare.init.ModDamageTypes
 *  com.atsuishio.superbwarfare.init.ModItems
 *  com.atsuishio.superbwarfare.init.ModParticleTypes
 *  com.atsuishio.superbwarfare.init.ModSounds
 *  com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage
 *  com.atsuishio.superbwarfare.tools.CustomExplosion$Builder
 *  com.atsuishio.superbwarfare.tools.ParticleTool
 *  com.atsuishio.superbwarfare.tools.SoundTool
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.Level$ExplosionInteraction
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  software.bernie.geckolib.animatable.GeoEntity
 *  software.bernie.geckolib.core.animatable.GeoAnimatable
 *  software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
 *  software.bernie.geckolib.core.animation.AnimatableManager$ControllerRegistrar
 *  software.bernie.geckolib.util.GeckoLibUtil
 */
package com.redabysslucia.dragonrise_reforge.entities.projectile;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.particle.CustomCloudOption;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.DestroyableProjectile;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModParticleTypes;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.SoundTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class NukerBombEntity
extends DestroyableProjectile
implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable)this);

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public NukerBombEntity(EntityType<? extends NukerBombEntity> type, Level level) {
        super(type, level);
        this.f_19811_ = true;
        this.setExplosionRadius(300.0f);
        this.setExplosionDamage(20000.0f);
    }

    public boolean m_6469_(@NotNull DamageSource source, float amount) {
        NukerBombEntity nuke;
        Entity entity = source.m_7640_();
        if (entity instanceof NukerBombEntity && (nuke = (NukerBombEntity)entity).m_19749_() == this.m_19749_()) {
            return false;
        }
        return super.m_6469_(source, amount);
    }

    @NotNull
    protected Item m_7881_() {
        return (Item)ModItems.MEDIUM_AERIAL_BOMB.get();
    }

    public void m_8060_(@NotNull BlockHitResult hit) {
        super.m_8060_(hit);
        Level level = this.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            this.causeNuclearExplosion(serverLevel);
        }
        this.m_146870_();
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.f_19797_ > 600 || ((Float)this.f_19804_.m_135370_(HEALTH)).floatValue() <= 0.0f) {
            Level level = this.m_9236_();
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                this.causeNuclearExplosion(serverLevel);
            }
            this.m_146870_();
        }
    }

    private void causeNuclearExplosion(ServerLevel serverLevel) {
        double x = this.m_20185_();
        double y = this.m_20186_();
        double z = this.m_20189_();
        Vec3 pos = this.m_20182_();
        BlockPos center = BlockPos.m_274561_((double)x, (double)y, (double)z);
        new CustomExplosion.Builder((Entity)this).damageSource(ModDamageTypes.causeCustomExplosionDamage((RegistryAccess)serverLevel.m_9598_(), (Entity)this, (Entity)this.m_19749_())).damage(this.getExplosionDamageValue() * 2.0f).radius(this.getExplosionRadiusValue()).position(pos).withParticleType(null).keepBlock().explode();
        if (((Boolean)ExplosionConfig.EXPLOSION_DESTROY.get()).booleanValue()) {
            serverLevel.m_254849_(this.m_19749_(), x, y, z, 30.0f, Level.ExplosionInteraction.BLOCK);
            Mod.queueServerWork((int)2, () -> serverLevel.m_254849_(this.m_19749_(), x + 25.0, y, z, 20.0f, Level.ExplosionInteraction.BLOCK));
            Mod.queueServerWork((int)3, () -> serverLevel.m_254849_(this.m_19749_(), x - 25.0, y, z, 20.0f, Level.ExplosionInteraction.BLOCK));
            Mod.queueServerWork((int)4, () -> serverLevel.m_254849_(this.m_19749_(), x, y, z + 25.0, 20.0f, Level.ExplosionInteraction.BLOCK));
            Mod.queueServerWork((int)5, () -> serverLevel.m_254849_(this.m_19749_(), x, y, z - 25.0, 20.0f, Level.ExplosionInteraction.BLOCK));
        }
        this.createCrater(serverLevel, center, 50);
        if (((Boolean)ExplosionConfig.EXPLOSION_DESTROY.get()).booleanValue()) {
            for (int wave = 0; wave < 8; ++wave) {
                int w = wave;
                int inner = 50 + w * 20;
                int outer = 80 + w * 20;
                Mod.queueServerWork((int)(35 + wave * 8), () -> this.destroyBlocksInRing(serverLevel, center, inner, outer));
            }
        }
        this.spawnNuclearExplosionParticles(serverLevel, x, y, z);
        this.applyRadiation(serverLevel, x, y, z, 150);
    }

    private void applyRadiation(ServerLevel level, double x, double y, double z, int radius) {
        this.applyRadiationEffects(level, x, y, z, radius, 1200, 3);
        for (int i = 0; i < 30; ++i) {
            int tick = i;
            Mod.queueServerWork((int)(i * 20), () -> {
                int currentRadius = radius - tick * 3;
                int intensity = Math.max(0, 2 - tick / 10);
                if (currentRadius > 30) {
                    this.applyRadiationEffects(level, x, y, z, currentRadius, 400, intensity);
                    if (tick % 2 == 0) {
                        ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)ParticleTypes.f_123748_, (double)x, (double)(y + 2.0), (double)z, (int)50, (double)((double)currentRadius * 0.7), (double)3.0, (double)((double)currentRadius * 0.7), (double)0.01, (boolean)true);
                        ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)ParticleTypes.f_175833_, (double)x, (double)(y + 30.0), (double)z, (int)30, (double)((double)currentRadius * 0.5), (double)20.0, (double)((double)currentRadius * 0.5), (double)0.005, (boolean)true);
                    }
                }
            });
        }
    }

    private void applyRadiationEffects(ServerLevel level, double x, double y, double z, int radius, int duration, int amplifier) {
        AABB area = new AABB(x - (double)radius, y - (double)(radius / 2), z - (double)radius, x + (double)radius, y + (double)radius, z + (double)radius);
        for (Entity entity : level.m_45933_(null, area)) {
            double maxDistSq;
            double distance;
            if (!(entity instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity)entity;
            if (entity == this.m_19749_() || !((distance = entity.m_20275_(x, y, z)) < (maxDistSq = (double)(radius * radius)))) continue;
            float intensity = 1.0f - (float)(distance / maxDistSq);
            int effectDuration = (int)((float)duration * intensity);
            int effectAmplifier = (int)((float)amplifier * intensity);
            living.m_7292_(new MobEffectInstance(MobEffects.f_19615_, effectDuration, effectAmplifier, false, true));
            living.m_7292_(new MobEffectInstance(MobEffects.f_19614_, effectDuration, effectAmplifier, false, true));
            living.m_7292_(new MobEffectInstance(MobEffects.f_19612_, effectDuration * 2, effectAmplifier + 1, false, true));
            living.m_7292_(new MobEffectInstance(MobEffects.f_19613_, effectDuration, effectAmplifier, false, true));
            living.m_7292_(new MobEffectInstance(MobEffects.f_19597_, effectDuration / 2, effectAmplifier, false, true));
            if (!(intensity > 0.5f)) continue;
            living.m_7292_(new MobEffectInstance(MobEffects.f_19604_, 200, 0, false, true));
        }
    }

    private void createCrater(ServerLevel level, BlockPos center, int radius) {
        int minY = -radius;
        int maxY = radius / 2;
        for (int layer = 0; layer <= maxY - minY; ++layer) {
            int dy;
            int finalDy = dy = minY + layer;
            int delay = layer / 3;
            Mod.queueServerWork((int)delay, () -> this.createCraterLayer(level, center, radius, finalDy));
        }
    }

    private void createCraterLayer(ServerLevel level, BlockPos center, int radius, int dy) {
        int blocksProcessed = 0;
        int maxBlocksPerLayer = 3000;
        double yRatio = (double)Math.abs(dy) / (double)radius;
        int horizontalRadius = (int)((double)radius * Math.sqrt(1.0 - yRatio * yRatio));
        if (horizontalRadius <= 0) {
            return;
        }
        for (int dx = -horizontalRadius; dx <= horizontalRadius && blocksProcessed < maxBlocksPerLayer; ++dx) {
            for (int dz = -horizontalRadius; dz <= horizontalRadius && blocksProcessed < maxBlocksPerLayer; ++dz) {
                BlockPos pos;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (!(distance <= (double)radius) || level.m_8055_(pos = center.m_7918_(dx, dy, dz)).m_60795_() || !(level.m_8055_(pos).m_60800_((BlockGetter)level, pos) >= 0.0f)) continue;
                ++blocksProcessed;
                double distRatio = distance / (double)radius;
                if (distRatio < 0.5) {
                    level.m_7731_(pos, Blocks.f_50016_.m_49966_(), 2);
                    continue;
                }
                if (distRatio < 0.75) {
                    if (level.f_46441_.m_188501_() < 0.8f) {
                        level.m_7731_(pos, Blocks.f_50016_.m_49966_(), 2);
                        continue;
                    }
                    if (dy < 0) continue;
                    level.m_7731_(pos, Blocks.f_50083_.m_49966_(), 2);
                    continue;
                }
                float destroyChance = 0.5f - (float)(distRatio - 0.75) * 2.0f;
                if (level.f_46441_.m_188501_() < destroyChance) {
                    level.m_7731_(pos, Blocks.f_50016_.m_49966_(), 2);
                    continue;
                }
                if (dy < 0 || !(level.f_46441_.m_188501_() < 0.4f)) continue;
                level.m_7731_(pos, Blocks.f_50083_.m_49966_(), 2);
            }
        }
    }

    private void destroyBlocksInRing(ServerLevel level, BlockPos center, int innerRadius, int outerRadius) {
        int minY = -innerRadius / 4;
        int maxY = innerRadius / 3;
        int taskId = 0;
        for (int quadrant = 0; quadrant < 4; ++quadrant) {
            int layer = minY;
            while (layer <= maxY) {
                int q = quadrant;
                int dy = layer++;
                Mod.queueServerWork((int)taskId++, () -> this.destroyRingQuadrant(level, center, innerRadius, outerRadius, dy, q));
            }
        }
    }

    private void destroyRingQuadrant(ServerLevel level, BlockPos center, int innerRadius, int outerRadius, int dy, int quadrant) {
        int dxEnd;
        int dzEnd = switch (quadrant) {
            case 0 -> {
                int dxStart = 0;
                dxEnd = outerRadius;
                int dzStart = 0;
                yield outerRadius;
            }
            case 1 -> {
                int dxStart = -outerRadius;
                dxEnd = 0;
                int dzStart = 0;
                yield outerRadius;
            }
            case 2 -> {
                int dxStart = -outerRadius;
                dxEnd = 0;
                int dzStart = -outerRadius;
                yield 0;
            }
            default -> {
                int dxStart = 0;
                dxEnd = outerRadius;
                int dzStart = -outerRadius;
                yield 0;
            }
        };
        for (int dx = dxStart; dx <= dxEnd; ++dx) {
            for (int dz = dzStart; dz <= dzEnd; ++dz) {
                BlockPos pos;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (!(distance >= (double)innerRadius) || !(distance <= (double)outerRadius) || level.m_8055_(pos = center.m_7918_(dx, dy, dz)).m_60795_() || !(level.m_8055_(pos).m_60800_((BlockGetter)level, pos) >= 0.0f)) continue;
                float distRatio = (float)(distance - (double)innerRadius) / (float)(outerRadius - innerRadius);
                float chance = 0.85f - distRatio * 0.5f;
                if (level.f_46441_.m_188501_() < chance) {
                    level.m_7731_(pos, Blocks.f_50016_.m_49966_(), 2);
                    continue;
                }
                if (dy < 0 || !(level.f_46441_.m_188501_() < 0.35f)) continue;
                level.m_7731_(pos, Blocks.f_50083_.m_49966_(), 2);
            }
        }
    }

    private void spawnNuclearExplosionParticles(ServerLevel level, double x, double y, double z) {
        int i;
        SoundTool.playDistantSound((ServerLevel)level, (SoundEvent)((SoundEvent)ModSounds.HUGE_EXPLOSION_CLOSE.get()), (Vec3)new Vec3(x, y, z), (float)32.0f, (float)1.0f, null);
        SoundTool.playDistantSound((ServerLevel)level, (SoundEvent)((SoundEvent)ModSounds.HUGE_EXPLOSION_FAR.get()), (Vec3)new Vec3(x, y, z), (float)96.0f, (float)1.0f, null);
        SoundTool.playDistantSound((ServerLevel)level, (SoundEvent)((SoundEvent)ModSounds.HUGE_EXPLOSION_VERY_FAR.get()), (Vec3)new Vec3(x, y, z), (float)600.0f, (float)1.0f, null);
        ShakeClientMessage.sendToNearbyPlayers((Level)level, (double)x, (double)y, (double)z, (double)1500.0, (double)100.0, (double)80.0);
        for (i = 1; i <= 5; ++i) {
            int delay = i * 40;
            float intensity = 60 - i * 10;
            Mod.queueServerWork((int)delay, () -> ShakeClientMessage.sendToNearbyPlayers((Level)level, (double)x, (double)y, (double)z, (double)1200.0, (double)intensity, (double)30.0));
        }
        ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)ParticleTypes.f_123747_, (double)x, (double)(y + 10.0), (double)z, (int)1500, (double)40.0, (double)40.0, (double)40.0, (double)1.0, (boolean)true);
        ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)ParticleTypes.f_123813_, (double)x, (double)(y + 15.0), (double)z, (int)500, (double)25.0, (double)25.0, (double)25.0, (double)1.0, (boolean)true);
        ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)((SimpleParticleType)ModParticleTypes.FIRE_STAR.get()), (double)x, (double)(y + 10.0), (double)z, (int)5000, (double)0.0, (double)0.0, (double)0.0, (double)6.0, (boolean)true);
        for (i = 0; i < 1000; ++i) {
            Vec3 v = new Vec3(1.0, 0.0, 0.0).m_82524_((float)i * 0.00628f);
            ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(1.0f, 1.0f, 1.0f, 200, 25.0f, 0.0f, false, false), (double)x, (double)(y + 8.0), (double)z, (int)0, (double)v.f_82479_, (double)v.f_82480_, (double)v.f_82481_, (double)200.0, (boolean)true);
            ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(1.0f, 1.0f, 0.95f, 180, 20.0f, 0.0f, false, false), (double)x, (double)(y + 4.0), (double)z, (int)0, (double)v.f_82479_, (double)v.f_82480_, (double)v.f_82481_, (double)220.0, (boolean)true);
        }
        for (int wave = 0; wave < 150; ++wave) {
            int w = wave;
            Mod.queueServerWork((int)(wave * 2), () -> {
                for (int i = 0; i < 500; ++i) {
                    float angle = (float)i * 0.01257f;
                    Vec3 v = new Vec3(1.0, 0.0, 0.0).m_82524_(angle);
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(1.0f, 1.0f, 0.95f, 250, 30.0f, 0.0f, false, false), (double)x, (double)(y + 15.0), (double)z, (int)0, (double)v.f_82479_, (double)v.f_82480_, (double)v.f_82481_, (double)(60 + w * 5), (boolean)true);
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(0.95f, 0.9f, 0.85f, 280, 25.0f, 0.0f, false, false), (double)x, (double)(y + 8.0), (double)z, (int)0, (double)v.f_82479_, (double)v.f_82480_, (double)v.f_82481_, (double)(55.0 + (double)w * 4.5), (boolean)true);
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(0.85f, 0.8f, 0.75f, 300, 20.0f, 0.0f, false, false), (double)x, (double)(y + 2.0), (double)z, (int)0, (double)v.f_82479_, (double)v.f_82480_, (double)v.f_82481_, (double)(50 + w * 4), (boolean)true);
                }
                ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(0.8f, 0.75f, 0.7f, 350, 35.0f, 0.0f, false, false), (double)x, (double)(y + 20.0), (double)z, (int)250, (double)(10.0 + (double)w * 1.5), (double)15.0, (double)(10.0 + (double)w * 1.5), (double)0.03, (boolean)true);
                ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(0.7f, 0.65f, 0.6f, 400, 18.0f, 0.0f, false, false), (double)x, (double)(y + 1.0), (double)z, (int)150, (double)(12 + w * 2), (double)2.0, (double)(12 + w * 2), (double)0.025, (boolean)true);
            });
        }
        for (i = 0; i < 600; ++i) {
            int tick = i;
            Mod.queueServerWork((int)i, () -> {
                if (tick < 150) {
                    float progress = (float)tick / 150.0f;
                    float brightness = 1.0f - progress * 0.4f;
                    float height = (float)tick * 1.5f;
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(brightness, brightness * 0.7f, brightness * 0.2f, 350, 16.0f, 0.0f, true, true), (double)x, (double)(y + (double)height), (double)z, (int)150, (double)(10.0f - progress * 4.0f), (double)4.0, (double)(10.0f - progress * 4.0f), (double)0.025, (boolean)true);
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)((SimpleParticleType)ModParticleTypes.FIRE_STAR.get()), (double)x, (double)(y + (double)height * 0.8), (double)z, (int)200, (double)0.0, (double)0.0, (double)0.0, (double)2.5, (boolean)true);
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(1.0f, 0.5f, 0.1f, 300, 14.0f, 0.0f, true, true), (double)x, (double)(y + (double)height * 0.9), (double)z, (int)100, (double)(8.0f - progress * 3.0f), (double)3.0, (double)(8.0f - progress * 3.0f), (double)0.02, (boolean)true);
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(0.45f, 0.4f, 0.35f, 450, 18.0f, 0.0f, true, true), (double)x, (double)(y + (double)height * 0.6), (double)z, (int)90, (double)(6.0f + progress * 4.0f), (double)2.0, (double)(6.0f + progress * 4.0f), (double)0.015, (boolean)true);
                }
                if (tick >= 80 && tick < 400) {
                    int k = tick - 80;
                    float capHeight = 150.0f + Math.min((float)k * 0.3f, 50.0f);
                    float fireIntensity = Math.max(0.0f, 1.0f - (float)k / 400.0f);
                    if (tick < 300) {
                        ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(fireIntensity, fireIntensity * 0.6f, fireIntensity * 0.15f, 400, 20.0f, 0.0f, true, true), (double)x, (double)(y + (double)capHeight), (double)z, (int)(50 + k / 4), (double)(5.0 + (double)k * 0.08), (double)(6.0 + (double)k * 0.05), (double)(5.0 + (double)k * 0.08), (double)0.008, (boolean)true);
                        if (tick < 200) {
                            ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)((SimpleParticleType)ModParticleTypes.FIRE_STAR.get()), (double)x, (double)(y + (double)capHeight), (double)z, (int)100, (double)0.0, (double)0.0, (double)0.0, (double)2.0, (boolean)true);
                        }
                    }
                    float orangeLevel = Math.max(0.25f, 0.95f - (float)k / 500.0f);
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(orangeLevel, orangeLevel * 0.45f, 0.08f, 450, 18.0f, 0.0f, true, true), (double)x, (double)(y + (double)capHeight - 12.0), (double)z, (int)(40 + k / 4), (double)(6.0 + (double)k * 0.1), (double)(5.0 + (double)k * 0.04), (double)(6.0 + (double)k * 0.1), (double)0.007, (boolean)true);
                    float smokeAlpha = Math.max(0.15f, 0.5f - (float)k / 800.0f);
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(smokeAlpha + 0.1f, smokeAlpha, smokeAlpha - 0.05f, 550, 24.0f, 0.0f, true, true), (double)x, (double)(y + (double)capHeight + 15.0), (double)z, (int)(35 + k / 4), (double)(8.0 + (double)k * 0.15), (double)(8.0 + (double)k * 0.08), (double)(8.0 + (double)k * 0.15), (double)0.006, (boolean)true);
                }
                if (tick >= 50 && tick < 350) {
                    int stemTick = tick - 50;
                    float stemFire = Math.max(0.0f, 1.0f - (float)stemTick / 200.0f);
                    if (tick < 200) {
                        ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(stemFire, stemFire * 0.5f, 0.1f, 350, 12.0f, 0.0f, true, true), (double)x, (double)(y + 70.0), (double)z, (int)60, (double)8.0, (double)40.0, (double)8.0, (double)0.005, (boolean)true);
                    }
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(0.5f, 0.45f, 0.4f, 500, 14.0f, 0.0f, true, true), (double)x, (double)(y + 70.0), (double)z, (int)55, (double)8.0, (double)50.0, (double)8.0, (double)0.004, (boolean)true);
                }
                if (tick < 200) {
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(0.6f, 0.55f, 0.5f, 400, 8.0f, 0.0f, false, false), (double)x, (double)(y + 1.0), (double)z, (int)(8 + tick / 3), (double)((double)tick * 0.8), (double)0.5, (double)((double)tick * 0.8), (double)(3.0E-4 * (double)tick), (boolean)true);
                }
                if (tick % 8 == 0 && tick < 120) {
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)ParticleTypes.f_123813_, (double)x, (double)(y + (double)tick * 1.2), (double)z, (int)60, (double)15.0, (double)10.0, (double)15.0, (double)1.0, (boolean)true);
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)((SimpleParticleType)ModParticleTypes.FIRE_STAR.get()), (double)x, (double)(y + (double)tick), (double)z, (int)600, (double)0.0, (double)0.0, (double)0.0, (double)4.5, (boolean)true);
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)ParticleTypes.f_123747_, (double)x, (double)(y + (double)tick), (double)z, (int)40, (double)10.0, (double)8.0, (double)10.0, (double)1.0, (boolean)true);
                }
                if (tick >= 200) {
                    if (tick % 3 == 0) {
                        ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(0.55f, 0.5f, 0.45f, 550, 10.0f, 0.0f, false, false), (double)x, (double)(y + 180.0), (double)z, (int)40, (double)60.0, (double)50.0, (double)60.0, (double)0.002, (boolean)true);
                    }
                    if (tick % 4 == 0) {
                        ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(0.6f, 0.55f, 0.5f, 500, 6.0f, 0.0f, false, false), (double)x, (double)(y + 4.0), (double)z, (int)30, (double)50.0, (double)1.0, (double)50.0, (double)0.004, (boolean)true);
                    }
                }
                if (tick >= 150 && tick < 550) {
                    float glowFade = 1.0f - (float)(tick - 150) / 500.0f;
                    float capY = 180.0f;
                    if (glowFade > 0.15f) {
                        ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(glowFade * 0.8f, glowFade * 0.4f, 0.08f, 600, 26.0f, 0.0f, true, true), (double)x, (double)(y + (double)capY), (double)z, (int)25, (double)40.0, (double)25.0, (double)40.0, (double)0.003, (boolean)true);
                    }
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(0.45f * glowFade, 0.4f * glowFade, 0.35f * glowFade, 650, 28.0f, 0.0f, true, true), (double)x, (double)(y + (double)capY + 10.0), (double)z, (int)22, (double)45.0, (double)28.0, (double)45.0, (double)0.003, (boolean)true);
                }
            });
        }
        for (i = 0; i < 200; ++i) {
            int tick = i;
            Mod.queueServerWork((int)(600 + i * 2), () -> {
                float fade = 1.0f - (float)tick / 250.0f;
                ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)ParticleTypes.f_123777_, (double)x, (double)(y + 50.0 + (double)(tick / 3)), (double)z, (int)30, (double)25.0, (double)60.0, (double)25.0, (double)0.008, (boolean)true);
                if (tick % 4 == 0 && tick < 100) {
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)ParticleTypes.f_123756_, (double)x, (double)(y + 1.0), (double)z, (int)20, (double)35.0, (double)0.5, (double)35.0, (double)0.02, (boolean)true);
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)ParticleTypes.f_123744_, (double)x, (double)(y + 2.0), (double)z, (int)15, (double)30.0, (double)1.5, (double)30.0, (double)0.025, (boolean)true);
                }
                if (tick < 80) {
                    float redGlow = 0.5f - (float)tick / 200.0f;
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(redGlow, redGlow * 0.3f, 0.05f, 400, 20.0f, 0.0f, false, false), (double)x, (double)(y + 120.0), (double)z, (int)50, (double)80.0, (double)30.0, (double)80.0, (double)0.001, (boolean)true);
                }
                if (tick >= 100) {
                    ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)new CustomCloudOption(0.4f * fade, 0.35f * fade, 0.3f * fade, 500, 15.0f, 0.0f, true, true), (double)x, (double)(y + 100.0), (double)z, (int)15, (double)50.0, (double)40.0, (double)50.0, (double)0.002, (boolean)true);
                }
            });
        }
    }

    @NotNull
    public SoundEvent getSound() {
        return (SoundEvent)ModSounds.SHELL_FLY.get();
    }

    public float getVolume() {
        return 1.0f;
    }
}

