/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.event.entity.EntityLeaveLevelEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package frontline.combat.fcp.event;

import frontline.combat.fcp.entity.vehicle.Trailers.AbstractTrailerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="fcp")
public class TrailerEventHandler {
    private static final double SEARCH_RADIUS = 32.0;

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        TrailerEventHandler.detachTrailersFor((Entity)event.getEntity());
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        TrailerEventHandler.detachTrailersFor(event.getEntity());
    }

    private static void detachTrailersFor(Entity leaving) {
        Level level = leaving.m_9236_();
        if (level.m_5776_()) {
            return;
        }
        level.m_6443_(AbstractTrailerEntity.class, leaving.m_20191_().m_82400_(32.0), trailer -> {
            Entity driver = trailer.getDriver();
            return driver != null && driver.m_20148_().equals(leaving.m_20148_());
        }).forEach(AbstractTrailerEntity::detach);
    }
}

