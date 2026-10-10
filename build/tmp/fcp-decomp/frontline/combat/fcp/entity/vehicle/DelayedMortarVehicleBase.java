/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.data.gun.GunProp
 *  com.atsuishio.superbwarfare.data.gun.ShootParameters
 *  com.atsuishio.superbwarfare.data.gun.SoundInfo
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package frontline.combat.fcp.entity.vehicle;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.data.gun.ShootParameters;
import com.atsuishio.superbwarfare.data.gun.SoundInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import frontline.combat.fcp.entity.vehicle.IndirectFireVehicleBase;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class DelayedMortarVehicleBase
extends IndirectFireVehicleBase {
    private static final int MORTAR_FIRE_DELAY = 20;
    private int mortarFireTime;
    private LivingEntity mortarShooter;
    private UUID mortarUuid;
    private Vec3 mortarTargetPos;

    protected DelayedMortarVehicleBase(EntityType<? extends VehicleEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void vehicleShoot(LivingEntity shooter, UUID uuid, Vec3 targetPos) {
        if (this.m_9236_().m_5776_() || this.isWreck()) {
            super.vehicleShoot(shooter, uuid, targetPos);
            return;
        }
        GunData data = this.getGunData(this.getSeatIndex((Entity)shooter));
        if (data == null || this.mortarFireTime != 0 || !data.canShoot(this.getAmmoSupplier())) {
            return;
        }
        if (!this.canStartIndirectShot(shooter)) {
            return;
        }
        this.mortarFireTime = 20;
        this.mortarShooter = shooter;
        this.mortarUuid = uuid;
        this.mortarTargetPos = targetPos;
        SoundInfo soundInfo = (SoundInfo)data.get(GunProp.SOUND_INFO);
        if (soundInfo != null && soundInfo.vehicleReload != null) {
            this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), soundInfo.vehicleReload, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        this.playShootSound3p(shooter, this.getSeatIndex((Entity)shooter));
    }

    @Override
    public void m_6075_() {
        super.m_6075_();
        if (this.mortarFireTime > 0 && --this.mortarFireTime == 0) {
            this.launchMortarRound();
        }
    }

    private void launchMortarRound() {
        ServerLevel server;
        block7: {
            block6: {
                Level level = this.m_9236_();
                if (!(level instanceof ServerLevel)) break block6;
                server = (ServerLevel)level;
                if (this.mortarShooter != null) break block7;
            }
            this.clearQueuedShot();
            return;
        }
        if (this.isFireControlActive()) {
            this.fireIndirectRound(this.mortarShooter, this.mortarUuid, false);
            this.clearQueuedShot();
            return;
        }
        int seatIndex = this.getSeatIndex((Entity)this.mortarShooter);
        GunData data = this.getGunData(seatIndex);
        if (data != null) {
            Vec3 direction = this.getShootVec((Entity)this.mortarShooter, 1.0f);
            Vec3 position = this.getShootPos((Entity)this.mortarShooter, 1.0f);
            if (direction != null && position != null) {
                this.modifyGunData(seatIndex, current -> {
                    if (current.canShoot(this.getAmmoSupplier())) {
                        current.shoot(new ShootParameters(this.getAmmoSupplier(), (Entity)this.mortarShooter, server, position, direction, current, ((Double)current.get(GunProp.SPREAD)).doubleValue(), true, this.mortarUuid, this.mortarTargetPos));
                    }
                });
                this.afterShoot(this.getGunData(seatIndex), direction);
            }
        }
        this.clearQueuedShot();
    }

    private void clearQueuedShot() {
        this.mortarShooter = null;
        this.mortarUuid = null;
        this.mortarTargetPos = null;
    }
}

