/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.Mod
 *  com.atsuishio.superbwarfare.data.DataLoader
 *  com.atsuishio.superbwarfare.data.vehicle.DefaultVehicleData
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo$Aircraft
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo$Helicopter
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo$Ship
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo$Tom6
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo$Track
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo$Wheel
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo$WheelChair
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  org.joml.Math
 */
package com.redabysslucia.dragonrise_reforge.entities.utils;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.data.DataLoader;
import com.atsuishio.superbwarfare.data.vehicle.DefaultVehicleData;
import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo;
import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.redabysslucia.dragonrise_reforge.entities.utils.DragonriseVehicleBase;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Math;

public abstract class VariableEngineVehicle
extends DragonriseVehicleBase {
    private EngineInfo variableEngineCache;
    private List<EngineType> engineTypeList;
    private EngineType currentEngineType;
    private int engineTypeIndex = 0;

    public VariableEngineVehicle(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public void m_8119_() {
        super.m_8119_();
    }

    public void setEngineTypeList(List<EngineType> engineTypeList) {
        this.engineTypeList = engineTypeList;
        this.currentEngineType = engineTypeList.get(engineTypeList.size() - 1);
    }

    public void toggleChangeMode() {
        this.engineTypeIndex = (this.engineTypeIndex + 1) % this.engineTypeList.size();
        EngineType newMode = this.engineTypeList.get(this.engineTypeIndex);
        this.updateEngineCache(newMode);
    }

    public void updateEngineCache(EngineType pEngineType) {
        DefaultVehicleData computed = this.computed();
        JsonObject engineInfo = computed.getEngineInfo();
        try {
            if (this.currentEngineType == EngineType.HELICOPTER) {
                this.f_19804_.m_135381_(POWER, (Object)Float.valueOf(((Float)this.f_19804_.m_135370_(POWER)).floatValue() / 0.12f));
            }
            this.variableEngineCache = switch (pEngineType) {
                case EngineType.WHEEL -> (EngineInfo.Wheel)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.Wheel.class);
                case EngineType.TRACK -> (EngineInfo.Track)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.Track.class);
                case EngineType.HELICOPTER -> {
                    this.f_19804_.m_135381_(POWER, (Object)Float.valueOf(((Float)this.f_19804_.m_135370_(POWER)).floatValue() * 0.12f));
                    yield (EngineInfo.Helicopter)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.Helicopter.class);
                }
                case EngineType.SHIP -> (EngineInfo.Ship)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.Ship.class);
                case EngineType.AIRCRAFT -> (EngineInfo.Aircraft)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.Aircraft.class);
                case EngineType.WHEELCHAIR -> (EngineInfo.WheelChair)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.WheelChair.class);
                case EngineType.TOM6 -> (EngineInfo.Tom6)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.Tom6.class);
                default -> null;
            };
            this.currentEngineType = pEngineType;
        }
        catch (Exception e) {
            Mod.LOGGER.error("Failed to parse engine info for vehicle {}, {}", (Object)this, (Object)e);
        }
    }

    public void travel() {
        DefaultVehicleData computed = this.computed();
        EngineType engineType = computed.getEngineType();
        if (engineType == EngineType.EMPTY) {
            return;
        }
        if (engineType == EngineType.FIXED) {
            this.fixedEngine();
            return;
        }
        if (this.variableEngineCache == null) {
            JsonObject engineInfo = computed.getEngineInfo();
            try {
                this.variableEngineCache = switch (engineType) {
                    case EngineType.WHEEL -> (EngineInfo.Wheel)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.Wheel.class);
                    case EngineType.TRACK -> (EngineInfo.Track)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.Track.class);
                    case EngineType.HELICOPTER -> (EngineInfo.Helicopter)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.Helicopter.class);
                    case EngineType.SHIP -> (EngineInfo.Ship)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.Ship.class);
                    case EngineType.AIRCRAFT -> (EngineInfo.Aircraft)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.Aircraft.class);
                    case EngineType.WHEELCHAIR -> (EngineInfo.WheelChair)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.WheelChair.class);
                    case EngineType.TOM6 -> (EngineInfo.Tom6)DataLoader.GSON.fromJson((JsonElement)engineInfo, EngineInfo.Tom6.class);
                    default -> null;
                };
            }
            catch (Exception e) {
                Mod.LOGGER.error("Failed to parse engine info for vehicle {}, {}", (Object)this, (Object)e);
            }
        } else {
            this.variableEngineCache.work((VehicleEntity)this);
        }
    }

    public float getEngineSoundVolume() {
        EngineType engineType = this.currentEngineType;
        if (engineType != EngineType.EMPTY && engineType != EngineType.FIXED && engineType != null) {
            EngineInfo engineInfo = this.variableEngineCache;
            if (engineInfo == null) {
                return 0.0f;
            }
            return switch (engineType) {
                case EngineType.TRACK -> Math.max((float)Mth.m_14154_((float)((Float)this.f_19804_.m_135370_(POWER)).floatValue()), (float)Mth.m_14154_((float)(1.4f * ((Float)this.f_19804_.m_135370_(DELTA_ROT)).floatValue()))) * engineInfo.getEngineSoundVolume();
                case EngineType.HELICOPTER -> ((Float)this.f_19804_.m_135370_(POWER)).floatValue() / 0.12f * engineInfo.getEngineSoundVolume();
                default -> Mth.m_14154_((float)((Float)this.f_19804_.m_135370_(POWER)).floatValue()) * engineInfo.getEngineSoundVolume();
            };
        }
        return 0.0f;
    }
}

