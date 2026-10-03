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
package com.redabysslucia.dragonrise_reforge.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.SOUND_EVENTS, (String)"dragonrise_reforge");
    public static final RegistryObject<SoundEvent> TERRORIST_IDLE = REGISTRY.register("terrorist_idle", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("dragonrise_reforge", "terrorist_idle")));
    public static final RegistryObject<SoundEvent> TERRORIST_STEP = REGISTRY.register("terrorist_step", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("dragonrise_reforge", "terrorist_step")));
    public static final RegistryObject<SoundEvent> TERRORIST_HURT = REGISTRY.register("terrorist_hurt", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("dragonrise_reforge", "terrorist_hurt")));
    public static final RegistryObject<SoundEvent> TERRORIST_DEATH = REGISTRY.register("terrorist_death", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("dragonrise_reforge", "terrorist_death")));
    public static final RegistryObject<SoundEvent> TERRORIST_SHOOT = REGISTRY.register("terrorist_shoot", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("dragonrise_reforge", "terrorist_shoot")));
    public static final RegistryObject<SoundEvent> SUPPLY_STATION_CHARGING = REGISTRY.register("supply_station_charging", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("dragonrise_reforge", "supply_station_charging")));
    public static final RegistryObject<SoundEvent> SUPPLY_STATION_COMPLETE = REGISTRY.register("supply_station_complete", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("dragonrise_reforge", "supply_station_complete")));
    public static final RegistryObject<SoundEvent> R6_DRONE_MOVING = REGISTRY.register("r6_drone_moving", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("dragonrise_reforge", "r6_drone_moving")));
    public static final RegistryObject<SoundEvent> R6_DRONE_FAST = REGISTRY.register("r6_drone_fast", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("dragonrise_reforge", "r6_drone_fast")));
    public static final RegistryObject<SoundEvent> R6_DRONE_JUMP = REGISTRY.register("r6_drone_jump", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("dragonrise_reforge", "r6_drone_jump")));
    public static final RegistryObject<SoundEvent> R6_DRONE_DOWN = REGISTRY.register("r6_drone_down", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("dragonrise_reforge", "r6_drone_down")));
    private static final String[] VEHICLE_SOUNDS;

    static {
        for (String soundName : VEHICLE_SOUNDS = new String[]{"cyborg_tank_engine", "cyborg_tank_fire_1p", "cyborg_tank_fire_3p", "cyborg_tank_fire_3p_far", "cyborg_tank_fire_3p_very_far", "cyborg_tank_fire_coax", "cyborg_tank_fire_coax_far", "cyborg_tank_fire_station", "cyborg_tank_fire_station_far", "cyborg_tank_reload", "fire_sound_57mm", "fire_sound_57mm_far", "hand_reload3s", "hand_reload5s", "jet_engine", "m1a2_engine", "m1a2_fire_1p", "m1a2_fire_3p", "m1a2_fire_3p_far", "m1a2_reload", "m2a3_engine_loop", "m2a3fire", "m2a3firefar", "mgfire1", "mgfire11", "plane_loop", "t80_engine", "t80_fire_1p", "t80_fire_3p", "t80_fire_3p_far", "t80_reload", "tank_engine_loop3", "type100_engine", "us_105_shoot", "us_37_shoot", "us_76_shoot", "wheel_loop1", "zbd04a_100mm_fire_1p", "zbd04a_100mm_fire_3p", "zbd04a_100mm_fire_3p_far", "zbd04a_100mm_reload", "zbd04a_30mm_fire_1p", "zbd04a_30mm_fire_3p", "zbd04a_30mm_fire_3p_far", "ztz59_fire_1p", "ztz59_fire_3p_far", "ztz99a_engine", "ztz99a_fire_1p", "ztz99a_fire_3p", "ztz99a_fire_3p_far", "ztz99a_reload"}) {
            REGISTRY.register(soundName, () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("dragonrise_reforge", soundName)));
        }
    }
}

