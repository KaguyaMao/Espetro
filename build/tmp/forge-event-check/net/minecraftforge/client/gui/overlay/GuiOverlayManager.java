/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoader
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.gui.overlay;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public final class GuiOverlayManager {
    private static ImmutableList<NamedGuiOverlay> OVERLAYS;
    private static ImmutableMap<ResourceLocation, NamedGuiOverlay> OVERLAYS_BY_NAME;

    public static ImmutableList<NamedGuiOverlay> getOverlays() {
        return OVERLAYS;
    }

    @Nullable
    public static NamedGuiOverlay findOverlay(ResourceLocation id) {
        return (NamedGuiOverlay)OVERLAYS_BY_NAME.get((Object)id);
    }

    @ApiStatus.Internal
    public static void init() {
        HashMap<ResourceLocation, IGuiOverlay> overlays = new HashMap<ResourceLocation, IGuiOverlay>();
        ArrayList<ResourceLocation> orderedOverlays = new ArrayList<ResourceLocation>();
        GuiOverlayManager.preRegisterVanillaOverlays(overlays, orderedOverlays);
        RegisterGuiOverlaysEvent event = new RegisterGuiOverlaysEvent(overlays, orderedOverlays);
        ModLoader.get().postEventWrapContainerInModOrder((Event)event);
        OVERLAYS = (ImmutableList)orderedOverlays.stream().map(id -> new NamedGuiOverlay((ResourceLocation)id, (IGuiOverlay)overlays.get(id))).collect(ImmutableList.toImmutableList());
        OVERLAYS_BY_NAME = (ImmutableMap)OVERLAYS.stream().collect(ImmutableMap.toImmutableMap(NamedGuiOverlay::id, Function.identity()));
        GuiOverlayManager.assignVanillaOverlayTypes();
    }

    private static void preRegisterVanillaOverlays(HashMap<ResourceLocation, IGuiOverlay> overlays, ArrayList<ResourceLocation> orderedOverlays) {
        for (VanillaGuiOverlay entry : VanillaGuiOverlay.values()) {
            overlays.put(entry.id(), entry.overlay);
            orderedOverlays.add(entry.id());
        }
    }

    private static void assignVanillaOverlayTypes() {
        for (VanillaGuiOverlay entry : VanillaGuiOverlay.values()) {
            entry.type = (NamedGuiOverlay)OVERLAYS_BY_NAME.get((Object)entry.id());
        }
    }

    private GuiOverlayManager() {
    }
}

