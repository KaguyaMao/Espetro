/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package tech.vvp.vvp.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.entity.projectile.PantsirMissileEntity;

@Mod.EventBusSubscriber(modid="vvp")
public class MissileSpawnHandler {
    @SubscribeEvent
    public static void onMissileSpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof PantsirMissileEntity)) {
            return;
        }
        PantsirMissileEntity missile = (PantsirMissileEntity)entity;
        if (event.getLevel().m_5776_()) {
            return;
        }
        Vec3 velocity = missile.m_20184_();
        if (velocity.m_82556_() < 0.01) {
            return;
        }
        Vec3 direction = velocity.m_82541_();
        Vec3 currentPos = missile.m_20182_();
        Vec3 spawnPos = currentPos.m_82546_(direction.m_82490_(0.5));
        missile.m_6034_(spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_);
        missile.setInitialRotation(direction);
    }
}

