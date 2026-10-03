/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.event.RegisterClientReloadListenersEvent
 *  net.minecraftforge.client.event.RegisterGuiOverlaysEvent
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.redabysslucia.dragonrise_reforge.client;

import com.redabysslucia.dragonrise_reforge.Dragonrise_reforge;
import com.redabysslucia.dragonrise_reforge.client.overlay.CannonBallisticOverlay;
import com.redabysslucia.dragonrise_reforge.client.overlay.M270BallisticOverlay;
import com.redabysslucia.dragonrise_reforge.client.overlay.SupplyProgressOverlay;
import com.redabysslucia.dragonrise_reforge.client.overlay.VehicleBackgroundOverlay;
import com.redabysslucia.dragonrise_reforge.resource.model.ArmorModelReloadListener;
import com.redabysslucia.dragonrise_reforge.resource.model.BlockModelReloadListener;
import com.redabysslucia.dragonrise_reforge.resource.model.EntityModelReloadListener;
import com.redabysslucia.dragonrise_reforge.resource.model.ItemModelReloadListener;
import com.redabysslucia.dragonrise_reforge.resource.model.ProjectileModelReloadListener;
import com.redabysslucia.dragonrise_reforge.resource.model.VehicleLODModelReloadListener;
import com.redabysslucia.dragonrise_reforge.resource.model.VehicleModelReloadListener;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(value=Dist.CLIENT)
@Mod.EventBusSubscriber(modid="dragonrise_reforge", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public class ClientEventHandler {
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelowAll("dragonrise_reforge_vehicle_background", (IGuiOverlay)new VehicleBackgroundOverlay());
        event.registerBelowAll("dragonrise_reforge_supply_progress", (IGuiOverlay)new SupplyProgressOverlay());
        event.registerBelowAll("dragonrise_reforge_m270_ballistic", (IGuiOverlay)new M270BallisticOverlay());
        event.registerBelowAll("dragonrise_reforge_cannon_ballistic", (IGuiOverlay)new CannonBallisticOverlay());
        Dragonrise_reforge.LOGGER.info("Dragonrise overlays registered");
    }

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((PreparableReloadListener)VehicleModelReloadListener.INSTANCE);
        event.registerReloadListener((PreparableReloadListener)VehicleLODModelReloadListener.INSTANCE);
        event.registerReloadListener((PreparableReloadListener)ArmorModelReloadListener.INSTANCE);
        event.registerReloadListener((PreparableReloadListener)EntityModelReloadListener.INSTANCE);
        event.registerReloadListener((PreparableReloadListener)ItemModelReloadListener.INSTANCE);
        event.registerReloadListener((PreparableReloadListener)ProjectileModelReloadListener.INSTANCE);
        event.registerReloadListener((PreparableReloadListener)BlockModelReloadListener.INSTANCE);
        Dragonrise_reforge.LOGGER.info("Dragonrise SBM model reload listeners registered");
    }
}

