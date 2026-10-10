/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package LOL_141.vehicle_addition.compat;

import LOL_141.vehicle_addition.compat.VehicleStabilizerHandler;
import LOL_141.vehicle_addition.compat.VehicleTerrainCompatHelper;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="vehicle_addition")
public final class VehicleTerrainEventHandler {
    private static final Map<Level, Set<Entity>> VEHICLES = new IdentityHashMap<Level, Set<Entity>>();

    private VehicleTerrainEventHandler() {
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity == null || entity.m_9236_().f_46443_) {
            return;
        }
        if (!VehicleTerrainCompatHelper.isSbwVehicle(entity)) {
            return;
        }
        VEHICLES.computeIfAbsent(entity.m_9236_(), k -> Collections.newSetFromMap(new IdentityHashMap())).add(entity);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Iterator<Map.Entry<Level, Set<Entity>>> levelIt = VEHICLES.entrySet().iterator();
        while (levelIt.hasNext()) {
            Map.Entry<Level, Set<Entity>> entry = levelIt.next();
            Set<Entity> set = entry.getValue();
            if (set == null || set.isEmpty()) {
                levelIt.remove();
                continue;
            }
            Iterator<Entity> it = set.iterator();
            while (it.hasNext()) {
                Entity entity = it.next();
                if (entity.m_213877_()) {
                    it.remove();
                    continue;
                }
                VehicleTerrainCompatHelper.apply(entity);
                VehicleStabilizerHandler.tickShake(entity);
            }
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.m_91104_()) {
            return;
        }
        ClientLevel level = mc.f_91073_;
        if (level == null) {
            return;
        }
        double s = 1.0E7;
        for (Entity entity : level.m_45976_(Entity.class, new AABB(-s, -s, -s, s, s, s))) {
            VehicleTerrainCompatHelper.apply(entity);
            VehicleStabilizerHandler.tickShake(entity);
        }
    }
}

