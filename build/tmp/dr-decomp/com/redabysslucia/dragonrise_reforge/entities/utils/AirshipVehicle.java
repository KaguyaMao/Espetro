/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.Mod
 *  com.atsuishio.superbwarfare.data.DataLoader
 *  com.atsuishio.superbwarfare.data.vehicle.DefaultVehicleData
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.redabysslucia.dragonrise_reforge.entities.utils;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.data.DataLoader;
import com.atsuishio.superbwarfare.data.vehicle.DefaultVehicleData;
import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.redabysslucia.dragonrise_reforge.utils.AirshipInfo;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AirshipVehicle
extends VehicleEntity {
    private EngineInfo engineCache;

    public void travel() {
        DefaultVehicleData computed = this.computed();
        if (this.engineCache == null) {
            JsonObject engineInfo = computed.getEngineInfo();
            try {
                this.engineCache = (EngineInfo)DataLoader.GSON.fromJson((JsonElement)engineInfo, AirshipInfo.class);
            }
            catch (Exception e) {
                Mod.LOGGER.error("Failed to parse engine info for vehicle {}, {}", (Object)this, (Object)e);
            }
        } else {
            this.engineCache.work((VehicleEntity)this);
        }
    }

    public AirshipVehicle(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public void m_8119_() {
        super.m_8119_();
    }
}

