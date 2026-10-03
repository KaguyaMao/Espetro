/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity
 *  com.atsuishio.superbwarfare.tools.OBB
 *  com.atsuishio.superbwarfare.tools.OBB$Part
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.registries.ForgeRegistries
 */
package frontline.combat.fcp.entity.vehicle.Trailers;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.tools.OBB;
import frontline.combat.fcp.entity.vehicle.CamoVehicleBase;
import frontline.combat.fcp.entity.vehicle.Trailers.TrailerDriverData;
import frontline.combat.fcp.entity.vehicle.Trailers.TrailerTowedData;
import frontline.combat.fcp.init.TrailerDriverConfigs;
import frontline.combat.fcp.init.TrailerTowedConfigs;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractTrailerEntity
extends CamoVehicleBase {
    private static final EntityDataAccessor<Integer> DRIVER_ID = SynchedEntityData.m_135353_(AbstractTrailerEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Boolean> ATTACHED = SynchedEntityData.m_135353_(AbstractTrailerEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    private static final EntityDataAccessor<Float> HITCH_X = SynchedEntityData.m_135353_(AbstractTrailerEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> HITCH_Y = SynchedEntityData.m_135353_(AbstractTrailerEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> HITCH_Z = SynchedEntityData.m_135353_(AbstractTrailerEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> TOW_X = SynchedEntityData.m_135353_(AbstractTrailerEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> TOW_Y = SynchedEntityData.m_135353_(AbstractTrailerEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> TOW_Z = SynchedEntityData.m_135353_(AbstractTrailerEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> MAX_ART = SynchedEntityData.m_135353_(AbstractTrailerEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final double HITCH_QUERY_MARGIN = 10.0;
    private static final double MAX_HITCH_JUMP = 4.0;
    private static final double MAX_HITCH_JUMP_SQ = 16.0;
    private static final int MAX_GLITCH_TICKS = 5;
    private static final float MAX_YAW_STEP = 50.0f;
    private int hitchGlitchTicks = 0;
    private int yawGlitchTicks = 0;
    @Nullable
    private UUID driverUUID;
    private static final double OBB_CONTAINS_EPSILON = 0.01;

    protected AbstractTrailerEntity(EntityType<? extends GeoVehicleEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(DRIVER_ID, (Object)-1);
        this.f_19804_.m_135372_(ATTACHED, (Object)false);
        this.f_19804_.m_135372_(HITCH_X, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(HITCH_Y, (Object)Float.valueOf(0.5f));
        this.f_19804_.m_135372_(HITCH_Z, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(TOW_X, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(TOW_Y, (Object)Float.valueOf(0.5f));
        this.f_19804_.m_135372_(TOW_Z, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(MAX_ART, (Object)Float.valueOf(110.0f));
    }

    @Nullable
    public TrailerTowedData getTowedData() {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey((Object)this.m_6095_());
        return id == null ? null : TrailerTowedConfigs.get(id);
    }

    public boolean isAttached() {
        return (Boolean)this.f_19804_.m_135370_(ATTACHED);
    }

    public Vec3 getHitchOffset() {
        return new Vec3((double)((Float)this.f_19804_.m_135370_(HITCH_X)).floatValue(), (double)((Float)this.f_19804_.m_135370_(HITCH_Y)).floatValue(), (double)((Float)this.f_19804_.m_135370_(HITCH_Z)).floatValue());
    }

    public Vec3 getTowOffset() {
        return new Vec3((double)((Float)this.f_19804_.m_135370_(TOW_X)).floatValue(), (double)((Float)this.f_19804_.m_135370_(TOW_Y)).floatValue(), (double)((Float)this.f_19804_.m_135370_(TOW_Z)).floatValue());
    }

    public float getMaxArticulation() {
        return ((Float)this.f_19804_.m_135370_(MAX_ART)).floatValue();
    }

    public void m_6075_() {
        boolean attached = this.isAttached();
        if (attached) {
            this.f_19790_ = this.m_20185_();
            this.f_19791_ = this.m_20186_();
            this.f_19792_ = this.m_20189_();
            this.f_19859_ = this.m_146908_();
            this.f_19860_ = this.m_146909_();
        }
        super.m_6075_();
        if (!this.m_9236_().m_5776_() && !attached && this.f_19797_ % 20 == 0) {
            this.syncTowDataFromConfig();
        }
        if (!attached) {
            return;
        }
        this.flattenCosmeticRotation();
        Entity driver = this.resolveDriver();
        if (driver == null) {
            return;
        }
        this.applyHitchConstraint(driver);
    }

    private void flattenCosmeticRotation() {
        this.m_146926_(0.0f);
        this.f_19860_ = 0.0f;
        this.setZRot(0.0f);
        this.setPrevRoll(0.0f);
    }

    private void applyHitchConstraint(Entity driver) {
        float yawStep;
        double dz;
        double tongueZ;
        double jz;
        double antX = 0.0;
        double antZ = 0.0;
        if (driver.f_19797_ < this.f_19797_) {
            Vec3 dv = driver.m_20184_();
            antX = dv.f_82479_;
            antZ = dv.f_82481_;
        }
        double driverX = driver.m_20185_() + antX;
        double driverZ = driver.m_20189_() + antZ;
        double driverY = driver.m_20186_();
        double hitchX = ((Float)this.f_19804_.m_135370_(HITCH_X)).floatValue();
        double hitchY = ((Float)this.f_19804_.m_135370_(HITCH_Y)).floatValue();
        double hitchZ = ((Float)this.f_19804_.m_135370_(HITCH_Z)).floatValue();
        double towX = ((Float)this.f_19804_.m_135370_(TOW_X)).floatValue();
        double towY = ((Float)this.f_19804_.m_135370_(TOW_Y)).floatValue();
        double towZ = ((Float)this.f_19804_.m_135370_(TOW_Z)).floatValue();
        float maxArt = ((Float)this.f_19804_.m_135370_(MAX_ART)).floatValue();
        double thetaD = Math.toRadians(driver.m_146908_());
        double cosD = Math.cos(thetaD);
        double sinD = Math.sin(thetaD);
        double hx = driverX + (hitchX * cosD - hitchZ * sinD);
        double hz = driverZ + (hitchX * sinD + hitchZ * cosD);
        double hy = driverY + hitchY;
        double thetaCur = Math.toRadians(this.m_146908_());
        double cosC = Math.cos(thetaCur);
        double sinC = Math.sin(thetaCur);
        double tongueX = this.m_20185_() + (towX * cosC - towZ * sinC);
        double jx = hx - tongueX;
        if (jx * jx + (jz = hz - (tongueZ = this.m_20189_() + (towX * sinC + towZ * cosC))) * jz > 16.0 && this.hitchGlitchTicks < 5) {
            ++this.hitchGlitchTicks;
            this.m_20256_(Vec3.f_82478_);
            return;
        }
        this.hitchGlitchTicks = 0;
        double dx = hx - this.m_20185_();
        float yaw = dx * dx + (dz = hz - this.m_20189_()) * dz < 1.0E-6 ? this.m_146908_() : (float)Math.toDegrees(Math.atan2(-dx, dz));
        float rel = Mth.m_14177_((float)(yaw - driver.m_146908_()));
        if (rel > maxArt) {
            rel = maxArt;
        }
        if (rel < -maxArt) {
            rel = -maxArt;
        }
        if (Math.abs(yawStep = Mth.m_14177_((float)((yaw = Mth.m_14177_((float)(driver.m_146908_() + rel))) - this.m_146908_()))) > 50.0f && this.yawGlitchTicks < 5) {
            ++this.yawGlitchTicks;
            yaw = this.m_146908_();
        } else {
            this.yawGlitchTicks = 0;
        }
        double thetaT = Math.toRadians(yaw);
        double cosT = Math.cos(thetaT);
        double sinT = Math.sin(thetaT);
        double newX = hx - (towX * cosT - towZ * sinT);
        double newZ = hz - (towX * sinT + towZ * cosT);
        double newY = hy - towY;
        this.m_6034_(newX, newY, newZ);
        this.m_146922_(yaw);
        this.m_146926_(0.0f);
        this.m_20256_(Vec3.f_82478_);
    }

    public boolean attach(Entity driver) {
        if (driver == null) {
            return false;
        }
        if (this.m_9236_().m_5776_()) {
            return false;
        }
        ResourceLocation driverId = ForgeRegistries.ENTITY_TYPES.getKey((Object)driver.m_6095_());
        if (driverId == null) {
            return false;
        }
        TrailerDriverData drv = TrailerDriverConfigs.get(driverId);
        if (drv == null) {
            return false;
        }
        TrailerTowedData towed = this.getTowedData();
        if (towed == null) {
            return false;
        }
        if (!towed.canBeTowedBy(driverId)) {
            return false;
        }
        if (AbstractTrailerEntity.isHitchTaken(driver, this)) {
            return false;
        }
        this.driverUUID = driver.m_20148_();
        this.f_19804_.m_135381_(DRIVER_ID, (Object)driver.m_19879_());
        this.f_19804_.m_135381_(ATTACHED, (Object)true);
        this.f_19804_.m_135381_(HITCH_X, (Object)Float.valueOf((float)drv.hitchX()));
        this.f_19804_.m_135381_(HITCH_Y, (Object)Float.valueOf((float)drv.hitchY()));
        this.f_19804_.m_135381_(HITCH_Z, (Object)Float.valueOf((float)drv.hitchZ()));
        this.f_19804_.m_135381_(TOW_X, (Object)Float.valueOf((float)towed.towX()));
        this.f_19804_.m_135381_(TOW_Y, (Object)Float.valueOf((float)towed.towY()));
        this.f_19804_.m_135381_(TOW_Z, (Object)Float.valueOf((float)towed.towZ()));
        this.f_19804_.m_135381_(MAX_ART, (Object)Float.valueOf(towed.maxArticulation()));
        double thetaD = Math.toRadians(driver.m_146908_());
        double cosD = Math.cos(thetaD);
        double sinD = Math.sin(thetaD);
        double hx = driver.m_20185_() + (drv.hitchX() * cosD - drv.hitchZ() * sinD);
        double hz = driver.m_20189_() + (drv.hitchX() * sinD + drv.hitchZ() * cosD);
        double hy = driver.m_20186_() + drv.hitchY();
        float yaw = driver.m_146908_();
        double newX = hx - (towed.towX() * cosD - towed.towZ() * sinD);
        double newZ = hz - (towed.towX() * sinD + towed.towZ() * cosD);
        double newY = hy - towed.towY();
        this.m_6034_(newX, newY, newZ);
        this.m_146922_(yaw);
        this.f_19859_ = yaw;
        this.m_146926_(0.0f);
        this.m_20256_(Vec3.f_82478_);
        return true;
    }

    public void detach() {
        this.driverUUID = null;
        this.f_19804_.m_135381_(DRIVER_ID, (Object)-1);
        this.f_19804_.m_135381_(ATTACHED, (Object)false);
    }

    @Nullable
    public Entity getDriver() {
        return this.resolveDriver();
    }

    @Nullable
    private Entity resolveDriver() {
        if (!this.isAttached()) {
            return null;
        }
        Level level = this.m_9236_();
        if (level instanceof ServerLevel) {
            Entity e;
            ServerLevel sl = (ServerLevel)level;
            Entity entity = e = this.driverUUID == null ? null : sl.m_8791_(this.driverUUID);
            if (e == null) {
                this.detach();
                return null;
            }
            if (((Integer)this.f_19804_.m_135370_(DRIVER_ID)).intValue() != e.m_19879_()) {
                this.f_19804_.m_135381_(DRIVER_ID, (Object)e.m_19879_());
            }
            return e;
        }
        int id = (Integer)this.f_19804_.m_135370_(DRIVER_ID);
        if (id < 0) {
            return null;
        }
        return this.m_9236_().m_6815_(id);
    }

    public Vec3 getTongueWorldPos() {
        Vec3 tow = this.getTowOffset();
        double theta = Math.toRadians(this.m_146908_());
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        return new Vec3(this.m_20185_() + (tow.f_82479_ * cos - tow.f_82481_ * sin), this.m_20186_() + tow.f_82480_, this.m_20189_() + (tow.f_82479_ * sin + tow.f_82481_ * cos));
    }

    @Nullable
    public static Vec3 getHitchWorldPos(Entity driver) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey((Object)driver.m_6095_());
        if (id == null) {
            return null;
        }
        TrailerDriverData drv = TrailerDriverConfigs.get(id);
        if (drv == null) {
            return null;
        }
        double theta = Math.toRadians(driver.m_146908_());
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        return new Vec3(driver.m_20185_() + (drv.hitchX() * cos - drv.hitchZ() * sin), driver.m_20186_() + drv.hitchY(), driver.m_20189_() + (drv.hitchX() * sin + drv.hitchZ() * cos));
    }

    private static boolean isHitchTaken(Entity driver, @Nullable AbstractTrailerEntity ignore) {
        List trailers = driver.m_9236_().m_6443_(AbstractTrailerEntity.class, driver.m_20191_().m_82400_(10.0), t -> t != ignore && t.isAttached());
        for (AbstractTrailerEntity trailer : trailers) {
            if (trailer.getDriver() != driver) continue;
            return true;
        }
        return false;
    }

    private DriverSearch findNearestDriver() {
        TrailerTowedData towed = this.getTowedData();
        if (towed == null) {
            return new DriverSearch(null, false);
        }
        double radius = towed.attachSearchRadius();
        double radiusSq = radius * radius;
        Vec3 tongue = this.getTongueWorldPos();
        List candidates = this.m_9236_().m_6249_((Entity)this, new AABB(tongue, tongue).m_82400_(radius + 10.0), entity -> {
            if (entity == this) {
                return false;
            }
            if (!(entity instanceof GeoVehicleEntity)) {
                return false;
            }
            if (entity instanceof AbstractTrailerEntity) {
                return false;
            }
            if (entity instanceof Player) {
                return false;
            }
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey((Object)entity.m_6095_());
            return id != null && TrailerDriverConfigs.has(id) && towed.canBeTowedBy(id);
        });
        if (candidates.isEmpty()) {
            return new DriverSearch(null, false);
        }
        Entity best = null;
        double bestDistSq = Double.MAX_VALUE;
        boolean sawTakenHitch = false;
        for (Entity candidate : candidates) {
            double distSq;
            Vec3 hitch = AbstractTrailerEntity.getHitchWorldPos(candidate);
            if (hitch == null || (distSq = hitch.m_82557_(tongue)) > radiusSq) continue;
            if (AbstractTrailerEntity.isHitchTaken(candidate, this)) {
                sawTakenHitch = true;
                continue;
            }
            if (!(distSq < bestDistSq)) continue;
            bestDistSq = distSq;
            best = candidate;
        }
        return new DriverSearch(best, sawTakenHitch);
    }

    public void handleClientSync() {
        if (this.isAttached()) {
            return;
        }
        super.handleClientSync();
    }

    public void m_6453_(double x, double y, double z, float yaw, float pitch, int steps, boolean teleport) {
        if (this.isAttached()) {
            return;
        }
        super.m_6453_(x, y, z, yaw, pitch, steps, teleport);
    }

    private void syncTowDataFromConfig() {
        TrailerTowedData towed = this.getTowedData();
        if (towed == null) {
            return;
        }
        this.f_19804_.m_135381_(TOW_X, (Object)Float.valueOf((float)towed.towX()));
        this.f_19804_.m_135381_(TOW_Y, (Object)Float.valueOf((float)towed.towY()));
        this.f_19804_.m_135381_(TOW_Z, (Object)Float.valueOf((float)towed.towZ()));
        this.f_19804_.m_135381_(MAX_ART, (Object)Float.valueOf(towed.maxArticulation()));
    }

    protected double hitchZoneRadius() {
        return 1.0;
    }

    protected boolean isInHitchZone(Vec3 vec) {
        Vec3 world = this.m_20182_().m_82549_(vec);
        boolean hasZoneBox = false;
        for (OBB obb : this.getOBBs()) {
            if (obb.part != OBB.Part.INTERACTIVE) continue;
            hasZoneBox = true;
            Vec3 centre = new Vec3(obb.center.x, obb.center.y, obb.center.z);
            Vec3 probe = world;
            Vec3 inward = centre.m_82546_(world);
            if (inward.m_82556_() > 1.0E-9) {
                probe = world.m_82549_(inward.m_82541_().m_82490_(0.01));
            }
            if (!obb.contains(probe)) continue;
            return true;
        }
        if (hasZoneBox) {
            return false;
        }
        return this.isNearTonguePoint(vec);
    }

    private boolean isNearTonguePoint(Vec3 vec) {
        double theta = Math.toRadians(this.m_146908_());
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        double lx = vec.f_82479_ * cos + vec.f_82481_ * sin;
        double lz = -vec.f_82479_ * sin + vec.f_82481_ * cos;
        double ly = vec.f_82480_;
        Vec3 tow = this.getTowOffset();
        double dx = lx - tow.f_82479_;
        double dy = ly - tow.f_82480_;
        double dz = lz - tow.f_82481_;
        double r = this.hitchZoneRadius();
        return dx * dx + dy * dy + dz * dz <= r * r;
    }

    public InteractionResult m_7111_(Player player, Vec3 vec, InteractionHand hand) {
        if (!player.m_21120_(hand).m_41619_()) {
            return InteractionResult.PASS;
        }
        if (!this.isInHitchZone(vec)) {
            return InteractionResult.PASS;
        }
        if (this.m_9236_().m_5776_()) {
            return InteractionResult.SUCCESS;
        }
        if (this.isAttached()) {
            this.detach();
            AbstractTrailerEntity.say(player, "fcp.trailer.detached");
            return InteractionResult.SUCCESS;
        }
        DriverSearch search = this.findNearestDriver();
        Entity driver = search.driver();
        if (driver == null) {
            AbstractTrailerEntity.say(player, search.sawTakenHitch() ? "fcp.trailer.hitch_taken" : "fcp.trailer.no_vehicle_nearby");
            return InteractionResult.SUCCESS;
        }
        if (this.attach(driver)) {
            AbstractTrailerEntity.say(player, "fcp.trailer.attached");
        } else {
            AbstractTrailerEntity.say(player, "fcp.trailer.cannot_attach");
        }
        return InteractionResult.SUCCESS;
    }

    private static void say(Player player, String key) {
        player.m_5661_((Component)Component.m_237115_((String)key), true);
    }

    public boolean m_7337_(Entity other) {
        if (this.isAttached() && other.m_19879_() == ((Integer)this.f_19804_.m_135370_(DRIVER_ID)).intValue()) {
            return false;
        }
        return super.m_7337_(other);
    }

    public boolean m_6094_() {
        return !this.isAttached() && super.m_6094_();
    }

    public void m_7334_(Entity other) {
        if (this.isAttached()) {
            return;
        }
        super.m_7334_(other);
    }

    protected void m_7840_(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (!this.isAttached()) {
            super.m_7840_(y, onGround, state, pos);
        }
    }

    @Override
    public void m_7380_(CompoundTag compound) {
        super.m_7380_(compound);
        compound.m_128379_("TrailerAttached", this.isAttached());
        if (this.driverUUID != null) {
            compound.m_128362_("TrailerDriverUUID", this.driverUUID);
        }
        compound.m_128350_("HitchX", ((Float)this.f_19804_.m_135370_(HITCH_X)).floatValue());
        compound.m_128350_("HitchY", ((Float)this.f_19804_.m_135370_(HITCH_Y)).floatValue());
        compound.m_128350_("HitchZ", ((Float)this.f_19804_.m_135370_(HITCH_Z)).floatValue());
        compound.m_128350_("TowX", ((Float)this.f_19804_.m_135370_(TOW_X)).floatValue());
        compound.m_128350_("TowY", ((Float)this.f_19804_.m_135370_(TOW_Y)).floatValue());
        compound.m_128350_("TowZ", ((Float)this.f_19804_.m_135370_(TOW_Z)).floatValue());
        compound.m_128350_("MaxArt", ((Float)this.f_19804_.m_135370_(MAX_ART)).floatValue());
    }

    @Override
    public void m_7378_(CompoundTag compound) {
        super.m_7378_(compound);
        this.f_19804_.m_135381_(ATTACHED, (Object)compound.m_128471_("TrailerAttached"));
        UUID uUID = this.driverUUID = compound.m_128403_("TrailerDriverUUID") ? compound.m_128342_("TrailerDriverUUID") : null;
        if (compound.m_128441_("HitchX")) {
            this.f_19804_.m_135381_(HITCH_X, (Object)Float.valueOf(compound.m_128457_("HitchX")));
            this.f_19804_.m_135381_(HITCH_Y, (Object)Float.valueOf(compound.m_128457_("HitchY")));
            this.f_19804_.m_135381_(HITCH_Z, (Object)Float.valueOf(compound.m_128457_("HitchZ")));
            this.f_19804_.m_135381_(TOW_X, (Object)Float.valueOf(compound.m_128457_("TowX")));
            this.f_19804_.m_135381_(TOW_Y, (Object)Float.valueOf(compound.m_128457_("TowY")));
            this.f_19804_.m_135381_(TOW_Z, (Object)Float.valueOf(compound.m_128457_("TowZ")));
            this.f_19804_.m_135381_(MAX_ART, (Object)Float.valueOf(compound.m_128457_("MaxArt")));
        }
    }

    private record DriverSearch(@Nullable Entity driver, boolean sawTakenHitch) {
    }
}

