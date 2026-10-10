/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package tech.vvp.vvp.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import tech.vvp.vvp.VVP;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.SOUND_EVENTS, (String)"vvp");
    public static final RegistryObject<SoundEvent> DOOR = ModSounds.register("door");
    public static final RegistryObject<SoundEvent> SPRAY = ModSounds.register("spray");
    public static final RegistryObject<SoundEvent> REMONT = ModSounds.register("remont");
    public static final RegistryObject<SoundEvent> WHEEL_STEP = ModSounds.register("wheel_step");
    public static final RegistryObject<SoundEvent> RADIOHEAD = ModSounds.register("radiohead");
    public static final RegistryObject<SoundEvent> ROCKET_SOUND = ModSounds.register("rocket_sound");
    public static final RegistryObject<SoundEvent> YX_100_FAR = ModSounds.register("yx_100_far");
    public static final RegistryObject<SoundEvent> YX_100_VERYFAR = ModSounds.register("yx_100_veryfar");
    public static final RegistryObject<SoundEvent> PLANETA_ENGINE = ModSounds.register("planeta_engine");
    public static final RegistryObject<SoundEvent> BTR_80A_ENGINE = ModSounds.register("btr_80a_engine");
    public static final RegistryObject<SoundEvent> STRYKER_ENGINE = ModSounds.register("stryker_engine");
    public static final RegistryObject<SoundEvent> F35_ENGINE = ModSounds.register("f35_engine");
    public static final RegistryObject<SoundEvent> MI24_ENGINE = ModSounds.register("mi24_engine");
    public static final RegistryObject<SoundEvent> HUMVEE_ENGINE = ModSounds.register("humvee_engine");
    public static final RegistryObject<SoundEvent> COBRA_ENGINE = ModSounds.register("cobra_engine");
    public static final RegistryObject<SoundEvent> VAZIK_ENGINE = ModSounds.register("vazik_engine");
    public static final RegistryObject<SoundEvent> SU25_ENGINE = ModSounds.register("su25_engine");
    public static final RegistryObject<SoundEvent> F16_ENGINE = ModSounds.register("f16_engine");
    public static final RegistryObject<SoundEvent> BMP_IDLE = ModSounds.register("bmp_idle");
    public static final RegistryObject<SoundEvent> BMP_START = ModSounds.register("bmp_start");
    public static final RegistryObject<SoundEvent> UH60_IDLE = ModSounds.register("uh60_idle");
    public static final RegistryObject<SoundEvent> UH60_START = ModSounds.register("uh60_start");
    public static final RegistryObject<SoundEvent> MI8_IDLE = ModSounds.register("mi8_idle");
    public static final RegistryObject<SoundEvent> MI8_START = ModSounds.register("mi8_start");
    public static final RegistryObject<SoundEvent> T72_ENGINE_IDLE = ModSounds.register("t72_engine_idle");
    public static final RegistryObject<SoundEvent> M2_1P = ModSounds.register("m2_1p");
    public static final RegistryObject<SoundEvent> M2_3P = ModSounds.register("m2_3p");
    public static final RegistryObject<SoundEvent> M2_FAR = ModSounds.register("m2_far");
    public static final RegistryObject<SoundEvent> M2_VERYFAR = ModSounds.register("m2_veryfar");
    public static final RegistryObject<SoundEvent> TOW_1P = ModSounds.register("tow_1p");
    public static final RegistryObject<SoundEvent> TOW_3P = ModSounds.register("tow_3p");
    public static final RegistryObject<SoundEvent> TOW_FAR = ModSounds.register("tow_far");
    public static final RegistryObject<SoundEvent> TOW_RELOAD = ModSounds.register("tow_reload");
    public static final RegistryObject<SoundEvent> BUSHMASTER_1P = ModSounds.register("bushmaster_1p");
    public static final RegistryObject<SoundEvent> BUSHMASTER_3P = ModSounds.register("bushmaster_3p");
    public static final RegistryObject<SoundEvent> BUSHMASTER_FAR = ModSounds.register("bushmaster_far");
    public static final RegistryObject<SoundEvent> BUSHMASTER_VERYFAR = ModSounds.register("bushmaster_veryfar");
    public static final RegistryObject<SoundEvent> M1128_1P = ModSounds.register("m1128_1p");
    public static final RegistryObject<SoundEvent> M1128_3P = ModSounds.register("m1128_3p");
    public static final RegistryObject<SoundEvent> M1128_FAR = ModSounds.register("m1128_far");
    public static final RegistryObject<SoundEvent> M1128_VERYFAR = ModSounds.register("m1128_veryfar");
    public static final RegistryObject<SoundEvent> M1128_RELOAD = ModSounds.register("m1128_reload");
    public static final RegistryObject<SoundEvent> PUSHKA_2A72_1P = ModSounds.register("pushka_2a72_1p");
    public static final RegistryObject<SoundEvent> PUSHKA_2A72_3P = ModSounds.register("pushka_2a72_3p");
    public static final RegistryObject<SoundEvent> PUSHKA_2A72_FAR = ModSounds.register("pushka_2a72_far");
    public static final RegistryObject<SoundEvent> PUSHKA_2A72_VERYFAR = ModSounds.register("pushka_2a72_veryfar");
    public static final RegistryObject<SoundEvent> A42_1P = ModSounds.register("2a42_1p");
    public static final RegistryObject<SoundEvent> A42_3P = ModSounds.register("2a42_3p");
    public static final RegistryObject<SoundEvent> A42_FAR = ModSounds.register("2a42_far");
    public static final RegistryObject<SoundEvent> A42_VERYFAR = ModSounds.register("2a42_veryfar");
    public static final RegistryObject<SoundEvent> ABRAMS_1P = ModSounds.register("abrams_1p");
    public static final RegistryObject<SoundEvent> ABRAMS_3P = ModSounds.register("abrams_3p");
    public static final RegistryObject<SoundEvent> ABRAMS_FAR = ModSounds.register("abrams_far");
    public static final RegistryObject<SoundEvent> ABRAMS_VERYFAR = ModSounds.register("abrams_veryfar");
    public static final RegistryObject<SoundEvent> ABRAMS_COAX_1P = ModSounds.register("abrams_coax_1p");
    public static final RegistryObject<SoundEvent> ABRAMS_COAX_3P = ModSounds.register("abrams_coax_3p");
    public static final RegistryObject<SoundEvent> ABRAMS_COAX_FAR = ModSounds.register("abrams_coax_far");
    public static final RegistryObject<SoundEvent> ABRAMS_COAX_VERYFAR = ModSounds.register("abrams_coax_veryfar");
    public static final RegistryObject<SoundEvent> HK_GMG_1P = ModSounds.register("hk_gmg_1p");
    public static final RegistryObject<SoundEvent> HK_GMG_3P = ModSounds.register("hk_gmg_3p");
    public static final RegistryObject<SoundEvent> HK_GMG_RELOAD = ModSounds.register("hk_gmg_reload");
    public static final RegistryObject<SoundEvent> T90_AUTORELOAD = ModSounds.register("t90_autoreload");
    public static final RegistryObject<SoundEvent> T72_AUTORELOAD = ModSounds.register("t72_autoreload");
    public static final RegistryObject<SoundEvent> T64_RELOAD = ModSounds.register("t64_reload");
    public static final RegistryObject<SoundEvent> PKT_FIRE = ModSounds.register("pkt_fire");
    public static final RegistryObject<SoundEvent> MK30_2_ABM_FIRE = ModSounds.register("mk30_2_abm_fire");
    public static final RegistryObject<SoundEvent> INTO_ATGM = ModSounds.register("into_atgm");
    public static final RegistryObject<SoundEvent> INTO_AUTOCANNON = ModSounds.register("into_autocannon");
    public static final RegistryObject<SoundEvent> INTO_COAX = ModSounds.register("into_coax");
    public static final RegistryObject<SoundEvent> INTO_MAIN_CANNON = ModSounds.register("into_main_cannon");

    private static RegistryObject<SoundEvent> register(String name) {
        return REGISTRY.register(name, () -> SoundEvent.m_262824_((ResourceLocation)VVP.loc(name)));
    }

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}

