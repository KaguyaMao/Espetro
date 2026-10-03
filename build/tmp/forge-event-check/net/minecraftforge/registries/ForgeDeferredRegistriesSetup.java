/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.IEventBus
 */
package net.minecraftforge.registries;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeDeferredRegistriesSetup {
    private static boolean setup = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setup(IEventBus modEventBus) {
        Class<ForgeDeferredRegistriesSetup> clazz = ForgeDeferredRegistriesSetup.class;
        synchronized (ForgeDeferredRegistriesSetup.class) {
            if (setup) {
                throw new IllegalStateException("Setup has already been called!");
            }
            setup = true;
            // ** MonitorExit[var1_1] (shouldn't be in output)
            ForgeRegistries.DEFERRED_ENTITY_DATA_SERIALIZERS.register(modEventBus);
            ForgeRegistries.DEFERRED_GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
            ForgeRegistries.DEFERRED_BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
            ForgeRegistries.DEFERRED_FLUID_TYPES.register(modEventBus);
            ForgeRegistries.DEFERRED_STRUCTURE_MODIFIER_SERIALIZERS.register(modEventBus);
            ForgeRegistries.DEFERRED_HOLDER_SET_TYPES.register(modEventBus);
            ForgeRegistries.DEFERRED_DISPLAY_CONTEXTS.register(modEventBus);
            return;
        }
    }
}

