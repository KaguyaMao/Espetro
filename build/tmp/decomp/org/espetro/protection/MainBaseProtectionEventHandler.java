/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package org.espetro.protection;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.protection.MainBaseProtection;

@Mod.EventBusSubscriber(modid="espetro")
public final class MainBaseProtectionEventHandler {
    private MainBaseProtectionEventHandler() {
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (MainBaseProtection.isProtected(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (MainBaseProtection.isProtected(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (MainBaseProtection.isProtected(event.getEntity())) {
            event.setCanceled(true);
        }
    }
}

