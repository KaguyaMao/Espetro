/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.google.gson.annotations.SerializedName
 */
package com.redabysslucia.dragonrise_reforge.utils;

import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.google.gson.annotations.SerializedName;
import com.redabysslucia.dragonrise_reforge.utils.AirshipEngineUtils;

public class AirshipInfo
extends EngineInfo {
    @SerializedName(value="SteeringSpeed")
    public float steeringSpeed = 0.1f;
    @SerializedName(value="MaxForwardSpeedRate")
    public float maxForwardSpeedRate = 0.2f;
    @SerializedName(value="MaxBackwardSpeedRate")
    public float maxBackwardSpeedRate = -0.1f;
    @SerializedName(value="LiftSpeedRate")
    public float liftSpeedRate = 0.2f;
    @SerializedName(value="SinkSpeedRate")
    public float sinkSpeedRate = -0.1f;
    @SerializedName(value="DragHorizontal")
    public float dragHorizontal = 0.02f;
    @SerializedName(value="DragVertical")
    public float dragVertical = 0.01f;

    public void work(VehicleEntity vehicle) {
        AirshipEngineUtils.airshipEngine(vehicle, this);
    }
}

