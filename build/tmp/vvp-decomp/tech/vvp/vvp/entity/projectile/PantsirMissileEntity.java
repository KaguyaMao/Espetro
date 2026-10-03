/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.projectile.Ru9m336MissileEntity
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 */
package tech.vvp.vvp.entity.projectile;

import com.atsuishio.superbwarfare.entity.projectile.Ru9m336MissileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;

public class PantsirMissileEntity
extends Ru9m336MissileEntity {
    private int launcherEntityId = -1;

    public PantsirMissileEntity(EntityType<? extends PantsirMissileEntity> type, Level level) {
        super(type, level);
    }

    public void m_8119_() {
        Entity entity;
        Entity owner;
        super.m_8119_();
        if (this.f_19797_ == 1 && this.launcherEntityId == -1 && (owner = this.m_19749_()) != null && (entity = owner.m_20202_()) instanceof PantsirS1Entity) {
            PantsirS1Entity pantsir = (PantsirS1Entity)entity;
            this.launcherEntityId = pantsir.m_19879_();
        }
    }

    public void m_7378_(@NotNull CompoundTag compound) {
        super.m_7378_(compound);
        this.launcherEntityId = compound.m_128451_("LauncherId");
    }

    public void m_7380_(@NotNull CompoundTag compound) {
        super.m_7380_(compound);
        compound.m_128405_("LauncherId", this.launcherEntityId);
    }

    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(this.launcherEntityId);
    }

    public void readSpawnData(FriendlyByteBuf buffer) {
        this.launcherEntityId = buffer.readInt();
    }

    public int getLauncherId() {
        return this.launcherEntityId;
    }

    public void setInitialRotation(Vec3 direction) {
        double horizontalDist = Math.sqrt(direction.f_82479_ * direction.f_82479_ + direction.f_82481_ * direction.f_82481_);
        float yaw = (float)(-Math.atan2(direction.f_82479_, direction.f_82481_) * 180.0 / Math.PI);
        float pitch = (float)(-Math.atan2(direction.f_82480_, horizontalDist) * 180.0 / Math.PI);
        this.m_146922_(yaw);
        this.m_146926_(pitch);
        this.f_19859_ = yaw;
        this.f_19860_ = pitch;
    }

    public float getVolume() {
        return 0.5f;
    }
}

