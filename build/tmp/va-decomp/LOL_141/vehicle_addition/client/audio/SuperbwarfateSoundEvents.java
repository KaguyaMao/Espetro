/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package LOL_141.vehicle_addition.client.audio;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class SuperbwarfateSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.SOUND_EVENTS, (String)"vehicle_addition");
    public static final RegistryObject<SoundEvent> RADIO = SOUND_EVENTS.register("radio", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("vehicle_addition", "radio")));

    private SuperbwarfateSoundEvents() {
    }
}

