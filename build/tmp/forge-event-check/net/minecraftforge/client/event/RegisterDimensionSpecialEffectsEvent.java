/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.DimensionSpecialEffects
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import java.util.Map;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public class RegisterDimensionSpecialEffectsEvent
extends Event
implements IModBusEvent {
    private final Map<ResourceLocation, DimensionSpecialEffects> effects;

    @ApiStatus.Internal
    public RegisterDimensionSpecialEffectsEvent(Map<ResourceLocation, DimensionSpecialEffects> effects) {
        this.effects = effects;
    }

    public void register(ResourceLocation dimensionType, DimensionSpecialEffects effects) {
        this.effects.put(dimensionType, effects);
    }
}

