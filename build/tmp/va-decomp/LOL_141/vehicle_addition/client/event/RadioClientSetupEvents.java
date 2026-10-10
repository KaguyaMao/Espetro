/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 */
package LOL_141.vehicle_addition.client.event;

import LOL_141.vehicle_addition.client.api.AudioStreamHandlerManager;
import LOL_141.vehicle_addition.init.ModKeyMappings;
import LOL_141.vehicle_addition.radio.NetEaseSongManager;
import LOL_141.vehicle_addition.radio.RadioConfig;
import LOL_141.vehicle_addition.radio.RadioStationManager;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid="vehicle_addition", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public final class RadioClientSetupEvents {
    private RadioClientSetupEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            RadioConfig.load();
            AudioStreamHandlerManager.init();
            RadioStationManager.load();
            NetEaseSongManager.load();
        });
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register((KeyMapping)ModKeyMappings.RADIO_CONFIG.get());
    }
}

