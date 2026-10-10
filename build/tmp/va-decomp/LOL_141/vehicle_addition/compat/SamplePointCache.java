/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 */
package LOL_141.vehicle_addition.compat;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class SamplePointCache {
    public static final ConcurrentHashMap<Entity, SampleSet> ALL = new ConcurrentHashMap();

    private SamplePointCache() {
    }

    public static void put(Entity vehicle, List<Vec3> points, List<Boolean> isPit, List<Integer> supportIdx, List<Vec3> embedded) {
        SampleSet set = new SampleSet();
        set.vehicle = vehicle;
        set.points = points;
        set.isPit = isPit;
        set.supportIdx = supportIdx;
        set.embedded = embedded;
        ALL.put(vehicle, set);
    }

    public static void sweep() {
        ALL.entrySet().removeIf(e -> e.getKey() == null || ((Entity)e.getKey()).m_213877_());
    }

    public static final class SampleSet {
        public Entity vehicle;
        public List<Vec3> points;
        public List<Boolean> isPit;
        public List<Integer> supportIdx;
        public List<Vec3> embedded;
    }
}

