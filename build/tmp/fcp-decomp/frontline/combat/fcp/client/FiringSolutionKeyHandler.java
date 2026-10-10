/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.client.settings.IKeyConflictContext
 *  net.minecraftforge.client.settings.KeyConflictContext
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package frontline.combat.fcp.client;

import com.mojang.blaze3d.platform.InputConstants;
import frontline.combat.fcp.client.screen.FiringSolutionScreen;
import frontline.combat.fcp.entity.vehicle.IndirectFireVehicleBase;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="fcp", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class FiringSolutionKeyHandler {
    public static final KeyMapping OPEN_FIRE_CONTROL = new KeyMapping("key.fcp.open_fire_control", (IKeyConflictContext)KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, 80, "key.categories.fcp");

    private FiringSolutionKeyHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91074_ == null) {
            return;
        }
        while (OPEN_FIRE_CONTROL.m_90859_()) {
            IndirectFireVehicleBase vehicle;
            Entity entity;
            if (minecraft.f_91080_ != null || !((entity = minecraft.f_91074_.m_20202_()) instanceof IndirectFireVehicleBase) || (vehicle = (IndirectFireVehicleBase)entity).getSeatIndex((Entity)minecraft.f_91074_) != vehicle.getTurretControllerIndex()) continue;
            minecraft.m_91152_((Screen)new FiringSolutionScreen(vehicle, (Player)minecraft.f_91074_));
        }
    }

    @Mod.EventBusSubscriber(modid="fcp", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBusHandler {
        private ModBusHandler() {
        }

        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(OPEN_FIRE_CONTROL);
        }
    }
}

