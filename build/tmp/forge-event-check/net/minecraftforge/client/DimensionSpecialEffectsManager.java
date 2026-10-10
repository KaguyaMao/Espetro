/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.client.renderer.DimensionSpecialEffects
 *  net.minecraft.client.renderer.DimensionSpecialEffects$EndEffects
 *  net.minecraft.client.renderer.DimensionSpecialEffects$NetherEffects
 *  net.minecraft.client.renderer.DimensionSpecialEffects$OverworldEffects
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.dimension.BuiltinDimensionTypes
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoader
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;

public final class DimensionSpecialEffectsManager {
    private static ImmutableMap<ResourceLocation, DimensionSpecialEffects> EFFECTS;
    private static DimensionSpecialEffects DEFAULT_EFFECTS;

    public static DimensionSpecialEffects getForType(ResourceLocation type) {
        return (DimensionSpecialEffects)EFFECTS.getOrDefault((Object)type, (Object)DEFAULT_EFFECTS);
    }

    @ApiStatus.Internal
    public static void init() {
        HashMap<ResourceLocation, DimensionSpecialEffects> effects = new HashMap<ResourceLocation, DimensionSpecialEffects>();
        DEFAULT_EFFECTS = DimensionSpecialEffectsManager.preRegisterVanillaEffects(effects);
        RegisterDimensionSpecialEffectsEvent event = new RegisterDimensionSpecialEffectsEvent(effects);
        ModLoader.get().postEventWrapContainerInModOrder((Event)event);
        EFFECTS = ImmutableMap.copyOf(effects);
    }

    private static DimensionSpecialEffects preRegisterVanillaEffects(Map<ResourceLocation, DimensionSpecialEffects> effects) {
        DimensionSpecialEffects.OverworldEffects overworldEffects = new DimensionSpecialEffects.OverworldEffects();
        effects.put(BuiltinDimensionTypes.f_223542_, (DimensionSpecialEffects)overworldEffects);
        effects.put(BuiltinDimensionTypes.f_223543_, (DimensionSpecialEffects)new DimensionSpecialEffects.NetherEffects());
        effects.put(BuiltinDimensionTypes.f_223544_, (DimensionSpecialEffects)new DimensionSpecialEffects.EndEffects());
        return overworldEffects;
    }

    private DimensionSpecialEffectsManager() {
    }
}

