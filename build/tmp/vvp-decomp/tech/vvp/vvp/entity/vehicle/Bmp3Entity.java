/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package tech.vvp.vvp.entity.vehicle;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import tech.vvp.vvp.entity.vehicle.VvpVehicleBase;

public class Bmp3Entity
extends VvpVehicleBase {
    public Bmp3Entity(EntityType<Bmp3Entity> type, Level world) {
        super(type, world);
    }

    public int getTrackAnimationLength() {
        return 104;
    }
}

