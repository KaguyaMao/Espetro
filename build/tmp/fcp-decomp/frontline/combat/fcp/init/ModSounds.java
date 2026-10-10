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
package frontline.combat.fcp.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.SOUND_EVENTS, (String)"fcp");
    public static final RegistryObject<SoundEvent> SPRAY = ModSounds.register("spray");
    public static final RegistryObject<SoundEvent> BMP1_FIRE_1P = ModSounds.register("bmp1_fire_1p");
    public static final RegistryObject<SoundEvent> BMP1_FIRE_3P = ModSounds.register("bmp1_fire_3p");
    public static final RegistryObject<SoundEvent> BMP1_FIRE_3P_FAR = ModSounds.register("bmp1_fire_3p_far");
    public static final RegistryObject<SoundEvent> BMP1_FIRE_3P_EXTRAFAR = ModSounds.register("bmp1_fire_3p_extrafar");
    public static final RegistryObject<SoundEvent> BMP1_MALYUTKA_FIRE_1P = ModSounds.register("bmp1_malyutka_fire_1p");
    public static final RegistryObject<SoundEvent> BMP1_MALYUTKA_FIRE_3P = ModSounds.register("bmp1_malyutka_fire_3p");
    public static final RegistryObject<SoundEvent> BMP1_MALYUTKA_FIRE_3P_FAR = ModSounds.register("bmp1_malyutka_fire_3p_far");
    public static final RegistryObject<SoundEvent> BMP1_CANNON_RELOAD = ModSounds.register("bmp1_cannon_reload");
    public static final RegistryObject<SoundEvent> BMP1_MALYUTKA_RELOAD = ModSounds.register("bmp1_malyutka_reload");
    public static final RegistryObject<SoundEvent> BMP1_ENGINE = ModSounds.register("bmp1_engine");
    public static final RegistryObject<SoundEvent> BMP1_INTO_CANNON = ModSounds.register("bmp1_into_cannon");
    public static final RegistryObject<SoundEvent> BMP1_INTO_MALYUTKA = ModSounds.register("bmp1_into_malyutka");
    public static final RegistryObject<SoundEvent> COAX_EQUIP = ModSounds.register("coax_equip");
    public static final RegistryObject<SoundEvent> RUSSIAN_COAX_1P = ModSounds.register("russian_coax_1p");
    public static final RegistryObject<SoundEvent> RUSSIAN_COAX_3P = ModSounds.register("russian_coax_3p");
    public static final RegistryObject<SoundEvent> RUSSIAN_COAX_3P_FAR = ModSounds.register("russian_coax_3p_far");
    public static final RegistryObject<SoundEvent> TOYOTA_ENGINE = ModSounds.register("toyota_engine");
    public static final RegistryObject<SoundEvent> SPG9_FIRE_1P = ModSounds.register("spg9_fire_1p");
    public static final RegistryObject<SoundEvent> SPG9_FIRE_3P = ModSounds.register("spg9_fire_3p");
    public static final RegistryObject<SoundEvent> SPG9_RELOAD = ModSounds.register("spg9_reload");
    public static final RegistryObject<SoundEvent> LITTLEBIRD_ENGINE_IDLE = ModSounds.register("littlebird_engine_idle");
    public static final RegistryObject<SoundEvent> LITTLEBIRD_ENGINE_START = ModSounds.register("littlebird_engine_start");
    public static final RegistryObject<SoundEvent> M134_FIRE_1P = ModSounds.register("m134_fire_1p");
    public static final RegistryObject<SoundEvent> M134_FIRE_3P = ModSounds.register("m134_fire_3p");
    public static final RegistryObject<SoundEvent> M134_FIRE_3P_FAR = ModSounds.register("m134_fire_3p_far");
    public static final RegistryObject<SoundEvent> STRYKER_ENGINE = ModSounds.register("stryker_engine");
    public static final RegistryObject<SoundEvent> STRYKER_MGS_FIRE_1P = ModSounds.register("stryker_mgs_fire_1p");
    public static final RegistryObject<SoundEvent> STRYKER_MGS_FIRE_3P = ModSounds.register("stryker_mgs_fire_3p");
    public static final RegistryObject<SoundEvent> STRYKER_MGS_FIRE_3P_FAR = ModSounds.register("stryker_mgs_fire_3p_far");
    public static final RegistryObject<SoundEvent> STRYKER_MGS_FIRE_3P_EXTRAFAR = ModSounds.register("stryker_mgs_fire_3p_extrafar");
    public static final RegistryObject<SoundEvent> STRYKER_MGS_RELOAD = ModSounds.register("stryker_mgs_reload");
    public static final RegistryObject<SoundEvent> M2_FIRE_1P = ModSounds.register("m2_fire_1p");
    public static final RegistryObject<SoundEvent> M2_FIRE_3P = ModSounds.register("m2_fire_3p");
    public static final RegistryObject<SoundEvent> M2_FIRE_3P_FAR = ModSounds.register("m2_fire_3p_far");
    public static final RegistryObject<SoundEvent> M2_RELOAD = ModSounds.register("m2_reload");
    public static final RegistryObject<SoundEvent> LAV25_FIRE_1P = ModSounds.register("lav25_fire_1p");
    public static final RegistryObject<SoundEvent> LAV25_FIRE_3P = ModSounds.register("lav25_fire_3p");
    public static final RegistryObject<SoundEvent> LAV25_FIRE_3P_FAR = ModSounds.register("lav25_fire_3p_far");
    public static final RegistryObject<SoundEvent> LAV25_RELOAD = ModSounds.register("lav25_engine");
    public static final RegistryObject<SoundEvent> TIGR_ENGINE = ModSounds.register("tigr_engine");
    public static final RegistryObject<SoundEvent> MK19_FIRE_1P = ModSounds.register("mk19_fire_1p");
    public static final RegistryObject<SoundEvent> MK19_FIRE_3P = ModSounds.register("mk19_fire_3p");
    public static final RegistryObject<SoundEvent> MK19_FIRE_3P_FAR = ModSounds.register("mk19_fire_3p_far");
    public static final RegistryObject<SoundEvent> MK19_FIRE_3P_VERY_FAR = ModSounds.register("mk19_fire_3p_very_far");
    public static final RegistryObject<SoundEvent> MK19_RELOAD = ModSounds.register("mk19_engine");
    public static final RegistryObject<SoundEvent> WE_GOT_HIM = ModSounds.register("we_got_him");
    public static final RegistryObject<SoundEvent> BOMB_IRAN = ModSounds.register("bomb_iran");
    public static final RegistryObject<SoundEvent> GOD_SYRIA_AND_BASHAR = ModSounds.register("god_syria_and_bashar");
    public static final RegistryObject<SoundEvent> FUNKY_TOWN = ModSounds.register("funky_town");
    public static final RegistryObject<SoundEvent> ERIKA_TRAP_REMIX = ModSounds.register("erika_trap_remix");
    public static final RegistryObject<SoundEvent> ERIKA = ModSounds.register("erika");

    private static RegistryObject<SoundEvent> register(String name) {
        return REGISTRY.register(name, () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("fcp", name)));
    }

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}

