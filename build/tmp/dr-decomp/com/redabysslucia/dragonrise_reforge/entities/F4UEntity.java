/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.event.ClientEventHandler
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.redabysslucia.dragonrise_reforge.entities.utils.FireLightVisionVehicle;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class F4UEntity
extends FireLightVisionVehicle {
    public F4UEntity(EntityType<F4UEntity> type, Level world) {
        super(type, world);
    }

    public double getMouseSensitivity() {
        return ClientEventHandler.zoomVehicle ? 0.1 : 0.25;
    }
}

