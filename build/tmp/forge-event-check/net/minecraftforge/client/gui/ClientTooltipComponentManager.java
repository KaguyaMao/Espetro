/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
 *  net.minecraft.world.inventory.tooltip.TooltipComponent
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoader
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.gui;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.function.Function;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public final class ClientTooltipComponentManager {
    private static ImmutableMap<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> FACTORIES;

    @Nullable
    public static ClientTooltipComponent createClientTooltipComponent(TooltipComponent component) {
        Function factory = (Function)FACTORIES.get(component.getClass());
        return factory != null ? (ClientTooltipComponent)factory.apply(component) : null;
    }

    @ApiStatus.Internal
    public static void init() {
        HashMap<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> factories = new HashMap<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>>();
        RegisterClientTooltipComponentFactoriesEvent event = new RegisterClientTooltipComponentFactoriesEvent(factories);
        ModLoader.get().postEventWrapContainerInModOrder((Event)event);
        FACTORIES = ImmutableMap.copyOf(factories);
    }

    private ClientTooltipComponentManager() {
    }
}

