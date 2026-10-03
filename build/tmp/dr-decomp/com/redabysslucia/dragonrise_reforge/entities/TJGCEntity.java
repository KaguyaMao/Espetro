/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  com.atsuishio.superbwarfare.event.ClientEventHandler
 *  com.atsuishio.superbwarfare.init.ModDamageTypes
 *  com.atsuishio.superbwarfare.init.ModSounds
 *  com.atsuishio.superbwarfare.network.NetworkRegistry
 *  com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage
 *  com.atsuishio.superbwarfare.tools.DamageHandler
 *  com.atsuishio.superbwarfare.tools.ParticleTool
 *  com.atsuishio.superbwarfare.tools.SeekTool$Builder
 *  net.minecraft.core.Holder
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundSoundPacket
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.network.PacketDistributor
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.NetworkRegistry;
import com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.SeekTool;
import com.redabysslucia.dragonrise_reforge.entities.utils.VariableEngineVehicle;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

public class TJGCEntity
extends VariableEngineVehicle {
    public TJGCEntity(EntityType<TJGCEntity> type, Level world) {
        super(type, world);
        this.setEngineTypeList(List.of(EngineType.AIRCRAFT, EngineType.HELICOPTER));
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.25f) * damage * (this.getHealth() > 0.1f ? 0.4f : 0.05f));
    }

    public void hitBlock(Vec3 pos, GunData gunData, Entity shooter) {
        Level level = this.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            if (gunData.compute().getExplosionRadius() > 0.0) {
                this.findNearEntity(pos, gunData, shooter);
                ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123810_, (double)pos.f_82479_, (double)pos.f_82480_, (double)pos.f_82481_, (int)24, (double)0.0, (double)0.0, (double)0.0, (double)0.2, (boolean)true);
                ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123756_, (double)pos.f_82479_, (double)pos.f_82480_, (double)pos.f_82481_, (int)8, (double)0.0, (double)0.0, (double)0.0, (double)0.4, (boolean)true);
            } else {
                ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123810_, (double)pos.f_82479_, (double)pos.f_82480_, (double)pos.f_82481_, (int)4, (double)0.0, (double)0.0, (double)0.0, (double)0.05, (boolean)true);
                ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123756_, (double)pos.f_82479_, (double)pos.f_82480_, (double)pos.f_82481_, (int)2, (double)0.0, (double)0.0, (double)0.0, (double)0.15, (boolean)true);
            }
        }
    }

    public void hitEntity(Vec3 pos, GunData gunData, Entity shooter) {
        Level level = this.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            if (gunData.compute().getExplosionRadius() > 0.0) {
                this.findNearEntity(pos, gunData, shooter);
                ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123810_, (double)pos.f_82479_, (double)pos.f_82480_, (double)pos.f_82481_, (int)24, (double)0.0, (double)0.0, (double)0.0, (double)0.2, (boolean)true);
                ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123756_, (double)pos.f_82479_, (double)pos.f_82480_, (double)pos.f_82481_, (int)8, (double)0.0, (double)0.0, (double)0.0, (double)0.4, (boolean)true);
            } else {
                ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123810_, (double)pos.f_82479_, (double)pos.f_82480_, (double)pos.f_82481_, (int)4, (double)0.0, (double)0.0, (double)0.0, (double)0.05, (boolean)true);
                ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123756_, (double)pos.f_82479_, (double)pos.f_82480_, (double)pos.f_82481_, (int)2, (double)0.0, (double)0.0, (double)0.0, (double)0.15, (boolean)true);
            }
        }
    }

    public void findNearEntity(Vec3 vec, GunData gunData, Entity shooter) {
        double aoeDamage = gunData.compute().getExplosionDamage();
        double range = gunData.compute().getExplosionRadius();
        Level level = this.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            List entities = new SeekTool.Builder((Entity)this).withinRange(vec, range).notItsVehicle().baseFilter().smokeFilter().noVehicle().differentTeam().build();
            for (Entity e : entities) {
                double dis = vec.m_82554_(e.m_146892_());
                float i = 0.0f;
                while ((double)i < dis) {
                    Vec3 toVec = vec.m_82505_(e.m_146892_()).m_82541_();
                    Vec3 pos = vec.m_82549_(toVec.m_82490_((double)i));
                    ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123810_, (double)pos.f_82479_, (double)pos.f_82480_, (double)pos.f_82481_, (int)1, (double)0.0, (double)0.0, (double)0.0, (double)0.0, (boolean)true);
                    i += 0.2f;
                }
                ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123756_, (double)e.m_20185_(), (double)e.m_20188_(), (double)e.m_20189_(), (int)4, (double)0.0, (double)0.0, (double)0.0, (double)0.15, (boolean)true);
                float damage = (float)(aoeDamage - Mth.m_14008_((double)(dis / range), (double)0.0, (double)0.75) * aoeDamage);
                DamageHandler.doDamage((Entity)e, (DamageSource)ModDamageTypes.causeLaserDamage((RegistryAccess)this.m_9236_().m_9598_(), (Entity)this, (Entity)shooter), (float)damage);
                if (!(shooter instanceof ServerPlayer)) continue;
                ServerPlayer player = (ServerPlayer)shooter;
                Holder holder = Holder.m_205709_((Object)((SoundEvent)ModSounds.INDICATION.get()));
                player.f_8906_.m_9829_((Packet)new ClientboundSoundPacket(holder, SoundSource.PLAYERS, player.m_20185_(), player.m_20186_(), player.m_20189_(), 1.0f, 1.0f, player.m_9236_().f_46441_.m_188505_()));
                NetworkRegistry.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), (Object)new ClientIndicatorMessage(0, 5));
            }
        }
    }

    public float getWheelMaxHealth() {
        return 100.0f;
    }

    public float getEngineMaxHealth() {
        return 150.0f;
    }

    @OnlyIn(value=Dist.CLIENT)
    public Component firstPersonAmmoComponent(GunData data, Player player) {
        String name = data.compute().getName();
        if (name == null || name.isBlank()) {
            return Component.m_237119_();
        }
        return Component.m_237110_((String)name, (Object[])new Object[]{(int)(25.0 + data.heat.get()) + " \u00b0C"});
    }

    public boolean shouldShowMissileOn(VehicleEntity vehicle, int ... missileWeaponIndices) {
        for (Entity passenger : vehicle.m_20197_()) {
            GunData gunData;
            int seatIndex = vehicle.getSeatIndex(passenger);
            if (seatIndex < 0) continue;
            int currentWeaponIndex = vehicle.getSelectedWeapon(seatIndex);
            boolean matches = false;
            for (int index : missileWeaponIndices) {
                if (currentWeaponIndex != index) continue;
                matches = true;
                break;
            }
            if (!matches || (gunData = vehicle.getGunData(seatIndex)) == null || gunData.ammo.get() <= 0 && gunData.backupAmmoCount.get() <= 0) continue;
            return true;
        }
        return false;
    }

    public double getMouseSensitivity() {
        return ClientEventHandler.zoomVehicle ? 0.1 : 0.25;
    }
}

