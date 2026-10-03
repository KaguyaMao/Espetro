/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.vehicle.DefaultVehicleData
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.tools.OBB
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.joml.Quaterniond
 *  org.joml.Vector3d
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package LOL_141.vehicle_addition.compat;

import LOL_141.vehicle_addition.compat.GroundTerrainCompat;
import com.atsuishio.superbwarfare.data.vehicle.DefaultVehicleData;
import com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.OBB;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VehicleTerrainCompatHelper {
    public static final String SBW_MOD_ID = "superbwarfare";
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleTerrainCompatHelper.class);
    private static final WeakHashMap<VehicleEntity, GroundTerrainCompat.AngularState> STATES = new WeakHashMap();
    private static final Set<String> CLEARED_TYPES = new HashSet<String>();
    private static Field terrainCompatField;
    private static Field obbCenterF;
    private static Field obbExtentsF;
    private static Field obbRotF;

    private VehicleTerrainCompatHelper() {
    }

    public static boolean isSbwVehicle(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (!ModList.get().isLoaded(SBW_MOD_ID)) {
            return false;
        }
        return entity instanceof VehicleEntity;
    }

    public static void apply(Entity entity) {
        if (entity == null) {
            return;
        }
        if (!ModList.get().isLoaded(SBW_MOD_ID)) {
            return;
        }
        if (!(entity instanceof VehicleEntity)) {
            return;
        }
        VehicleEntity self = (VehicleEntity)entity;
        VehicleTerrainCompatHelper.applyToVehicle(self);
    }

    private static void applyToVehicle(final VehicleEntity self) {
        VehicleType type = self.getVehicleType();
        if (type == null) {
            return;
        }
        switch (type) {
            case TANK: 
            case APC: 
            case AA: 
            case CAR: 
            case ARTILLERY: 
            case SPECIAL: {
                break;
            }
            default: {
                return;
            }
        }
        if (self.isWreck()) {
            return;
        }
        VehicleTerrainCompatHelper.clearTerrainCompat(self);
        OBB collObb = self.getCollisionOBB();
        if (collObb == null) {
            return;
        }
        Vector3d obbCenter = (Vector3d)VehicleTerrainCompatHelper.obbCenterField(collObb);
        Vector3d obbExtents = (Vector3d)VehicleTerrainCompatHelper.obbExtentsField(collObb);
        Quaterniond obbRot = (Quaterniond)VehicleTerrainCompatHelper.obbRotField(collObb);
        if (obbCenter == null || obbExtents == null || obbRot == null) {
            return;
        }
        if (obbExtents.x <= 0.0 || obbExtents.z <= 0.0) {
            return;
        }
        DefaultVehicleData computed = self.computed();
        float rotateRate = computed.getTerrainCompatRotateRate();
        GroundTerrainCompat.AngularState state = STATES.computeIfAbsent(self, k -> new GroundTerrainCompat.AngularState());
        GroundTerrainCompat.applyAutoTerrainCompact(self.m_9236_(), self.m_20182_(), new Vec3(obbCenter.x, obbCenter.y, obbCenter.z), new Vec3(obbExtents.x, obbExtents.y, obbExtents.z), obbRot, rotateRate, state, new GroundTerrainCompat.PoseAccess(){

            @Override
            public float xRot() {
                return self.m_146909_();
            }

            @Override
            public float yRot() {
                return self.m_146908_();
            }

            @Override
            public float roll() {
                return self.getRoll(1.0f);
            }

            @Override
            public void setXRot(float value) {
                self.m_146926_(value);
                self.f_19860_ = value;
                self.setFakePitch(0.0f);
                self.setFakePitchO(0.0f);
                self.setPitchAngle(0.0f);
            }

            @Override
            public void setYRot(float value) {
                self.m_146922_(value);
            }

            @Override
            public void setZRot(float value) {
                self.setZRot(value);
                self.setPrevRoll(value);
                self.setFakeRoll(0.0f);
                self.setFakeRollO(0.0f);
                self.setRollAngle(0.0f);
            }

            @Override
            public void adjustY(double dy) {
                Vec3 pos = self.m_20182_();
                self.m_6034_(pos.f_82479_, pos.f_82480_ + dy, pos.f_82481_);
            }

            @Override
            public void setPosition(Vec3 pos) {
                self.m_6034_(pos.f_82479_, pos.f_82480_, pos.f_82481_);
            }

            @Override
            public Vec3 deltaMovement() {
                return self.m_20184_();
            }

            @Override
            public void deltaMovement(double x, double y, double z) {
                self.m_20256_(new Vec3(x, y, z));
            }

            @Override
            public Entity debugEntity() {
                return self;
            }
        });
    }

    private static void clearTerrainCompat(VehicleEntity self) {
        try {
            ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey((Object)self.m_6095_());
            if (key == null) {
                return;
            }
            String id = key.toString();
            if (!CLEARED_TYPES.add(id)) {
                return;
            }
            Object dataMap = Class.forName("com.atsuishio.superbwarfare.data.CustomData").getField("VEHICLE_DATA").get(null);
            Object data = ((Map)dataMap).get(id);
            if (data instanceof DefaultVehicleData) {
                Object list;
                DefaultVehicleData dvd = (DefaultVehicleData)data;
                if (terrainCompatField == null) {
                    terrainCompatField = DefaultVehicleData.class.getDeclaredField("terrainCompat");
                    terrainCompatField.setAccessible(true);
                }
                if ((list = terrainCompatField.get(dvd)) instanceof List) {
                    List l = (List)list;
                    l.clear();
                    LOGGER.info("[VehicleTerrain] cleared SBW terrainCompat for {}", (Object)id);
                }
            }
        }
        catch (Throwable t) {
            LOGGER.warn("[VehicleTerrain] failed to clear terrainCompat: {}", (Object)t.toString());
        }
    }

    private static Object obbCenterField(Object obb) {
        try {
            if (obbCenterF == null) {
                obbCenterF = obb.getClass().getDeclaredField("center");
                obbCenterF.setAccessible(true);
            }
            return obbCenterF.get(obb);
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static Object obbExtentsField(Object obb) {
        try {
            if (obbExtentsF == null) {
                obbExtentsF = obb.getClass().getDeclaredField("extents");
                obbExtentsF.setAccessible(true);
            }
            return obbExtentsF.get(obb);
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static Object obbRotField(Object obb) {
        try {
            if (obbRotF == null) {
                obbRotF = obb.getClass().getDeclaredField("rotation");
                obbRotF.setAccessible(true);
            }
            return obbRotF.get(obb);
        }
        catch (Throwable t) {
            return null;
        }
    }
}

