/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.tools.OBB
 *  com.atsuishio.superbwarfare.tools.OBB$Part
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 */
package com.redabysslucia.dragonrise_reforge.entities.utils;

import com.atsuishio.superbwarfare.tools.OBB;
import com.redabysslucia.dragonrise_reforge.entities.utils.DragonriseVehicleBase;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

public abstract class FireLightVisionVehicle
extends DragonriseVehicleBase {
    private static final Map<FireLightVisionVehicle, LightInfo> lightInfoMap = new HashMap<FireLightVisionVehicle, LightInfo>();
    private int lightRemovalTimer = 0;

    public void vehicleShoot(LivingEntity living, String weaponName, Vec3 targetPos) {
        super.vehicleShoot(living, weaponName, targetPos);
        this.handleTurretFireLight(1);
        this.lightRemovalTimer = 1;
    }

    public void vehicleShoot(LivingEntity living, UUID uuid, Vec3 targetPos) {
        super.vehicleShoot(living, uuid, targetPos);
        this.handleTurretFireLight(1);
        this.lightRemovalTimer = 1;
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.lightRemovalTimer > 0) {
            --this.lightRemovalTimer;
            if (this.lightRemovalTimer == 0) {
                this.handleTurretFireLight(0);
            }
        }
    }

    protected void handleTurretFireLight(int shootTimer) {
        Level level = this.m_9236_();
        if (!level.m_5776_()) {
            if (shootTimer > 0) {
                for (OBB obb : this.getOBBs()) {
                    if (obb.part != OBB.Part.TURRET) continue;
                    Vec3 obbCenter = OBB.vector3dToVec3((Vector3d)obb.center);
                    Quaterniond rotation = obb.rotation();
                    Vector3d northDirection = new Vector3d(0.0, 0.0, 1.0);
                    northDirection.rotate((Quaterniondc)rotation);
                    double offsetX = northDirection.x * 2.0;
                    double offsetY = 0.0;
                    double offsetZ = northDirection.z * 2.0;
                    double finalX = obbCenter.f_82479_ + offsetX;
                    double finalY = obbCenter.f_82480_ + offsetY;
                    double finalZ = obbCenter.f_82481_ + offsetZ;
                    BlockPos pos = new BlockPos((int)Math.floor(finalX), (int)Math.floor(finalY), (int)Math.floor(finalZ));
                    if (level.m_46859_(pos)) {
                        level.m_7731_(pos, (BlockState)Blocks.f_152480_.m_49966_().m_61124_((Property)BlockStateProperties.f_61422_, (Comparable)Integer.valueOf(10)), 3);
                        lightInfoMap.put(this, new LightInfo(pos, level.m_46467_()));
                    }
                    break;
                }
            } else {
                LightInfo lightInfo = lightInfoMap.get((Object)this);
                if (lightInfo != null) {
                    level.m_7471_(lightInfo.pos, false);
                    lightInfoMap.remove((Object)this);
                }
            }
        }
    }

    public FireLightVisionVehicle(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private static class LightInfo {
        public final BlockPos pos;
        public final long placementTime;

        public LightInfo(BlockPos pos, long placementTime) {
            this.pos = pos;
            this.placementTime = placementTime;
        }
    }
}

