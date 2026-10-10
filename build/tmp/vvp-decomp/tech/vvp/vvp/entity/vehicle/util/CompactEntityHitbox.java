/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 */
package tech.vvp.vvp.entity.vehicle.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import tech.vvp.vvp.entity.vehicle.util.ICompactEntityHitbox;

public final class CompactEntityHitbox {
    public static final float DEFAULT_VISUAL_HEIGHT = 3.5f;
    private static final Map<EntityType<?>, Float> VISUAL_HEIGHTS = new HashMap();

    private CompactEntityHitbox() {
    }

    public static void registerVisualHeight(EntityType<?> type, float height) {
        VISUAL_HEIGHTS.put(type, Float.valueOf(height));
    }

    public static float visualHeight(EntityType<?> type) {
        Float height = VISUAL_HEIGHTS.get(type);
        return height != null ? height.floatValue() : 3.5f;
    }

    public static float displayBbHeight(Entity entity) {
        if (entity instanceof ICompactEntityHitbox) {
            ICompactEntityHitbox compact = (ICompactEntityHitbox)entity;
            return compact.getVisualBbHeight();
        }
        return entity.m_20206_();
    }
}

