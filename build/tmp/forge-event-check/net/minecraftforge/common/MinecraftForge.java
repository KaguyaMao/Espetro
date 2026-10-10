/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.eventbus.api.BusBuilder
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.loading.FMLEnvironment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 */
package net.minecraftforge.common;

import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.ForgeInternalHandler;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.DualStackUtils;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MinecraftForge {
    public static final IEventBus EVENT_BUS = BusBuilder.builder().startShutdown().useModLauncher().build();
    static final ForgeInternalHandler INTERNAL_HANDLER = new ForgeInternalHandler();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FORGE = MarkerManager.getMarker((String)"FORGE");

    public static void initialize() {
        LOGGER.info(FORGE, "MinecraftForge v{} Initialized", (Object)ForgeVersion.getVersion());
        UsernameCache.load();
        TierSortingRegistry.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientCommandHandler.init();
        }
        DualStackUtils.initialise();
    }

    public static void registerConfigScreen(Function<Screen, Screen> screenFunction) {
        MinecraftForge.registerConfigScreen((Minecraft mcClient, Screen modsScreen) -> (Screen)screenFunction.apply((Screen)modsScreen));
    }

    public static void registerConfigScreen(BiFunction<Minecraft, Screen, Screen> screenFunction) {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(screenFunction));
    }
}

