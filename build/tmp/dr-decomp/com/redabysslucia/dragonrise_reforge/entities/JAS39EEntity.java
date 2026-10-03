/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  com.atsuishio.superbwarfare.event.ClientEventHandler
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.redabysslucia.dragonrise_reforge.entities.utils.FireLightVisionVehicle;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class JAS39EEntity
extends FireLightVisionVehicle {
    public JAS39EEntity(EntityType<JAS39EEntity> type, Level world) {
        super(type, world);
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.25f) * damage * (this.getHealth() > 0.1f ? 0.4f : 0.05f));
    }

    public double getMouseSensitivity() {
        return ClientEventHandler.zoomVehicle ? 0.1 : 0.25;
    }
}

