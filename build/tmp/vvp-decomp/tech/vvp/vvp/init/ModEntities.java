/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.EntityType$EntityFactory
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package tech.vvp.vvp.init;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import tech.vvp.vvp.entity.projectile.PantsirMissileEntity;
import tech.vvp.vvp.entity.vehicle.Ags30Entity;
import tech.vvp.vvp.entity.vehicle.Ah64Entity;
import tech.vvp.vvp.entity.vehicle.AjaxEntity;
import tech.vvp.vvp.entity.vehicle.BMPT3KEntity;
import tech.vvp.vvp.entity.vehicle.Bmp2BakhchaEntity;
import tech.vvp.vvp.entity.vehicle.Bmp2Entity;
import tech.vvp.vvp.entity.vehicle.Bmp2MEntity;
import tech.vvp.vvp.entity.vehicle.Bmp3Entity;
import tech.vvp.vvp.entity.vehicle.BradleyEntity;
import tech.vvp.vvp.entity.vehicle.BrmEntity;
import tech.vvp.vvp.entity.vehicle.Btr3Entity;
import tech.vvp.vvp.entity.vehicle.Btr4Entity;
import tech.vvp.vvp.entity.vehicle.BushmasterEntity;
import tech.vvp.vvp.entity.vehicle.CV90Entity;
import tech.vvp.vvp.entity.vehicle.CentauroEntity;
import tech.vvp.vvp.entity.vehicle.ChallengerEntity;
import tech.vvp.vvp.entity.vehicle.ChryzantemaEntity;
import tech.vvp.vvp.entity.vehicle.CobraEntity;
import tech.vvp.vvp.entity.vehicle.D30Entity;
import tech.vvp.vvp.entity.vehicle.FMTVEntity;
import tech.vvp.vvp.entity.vehicle.GazTigrEntity;
import tech.vvp.vvp.entity.vehicle.HimarsEntity;
import tech.vvp.vvp.entity.vehicle.HumveeEntity;
import tech.vvp.vvp.entity.vehicle.KornetEntity;
import tech.vvp.vvp.entity.vehicle.Leopard2A4Entity;
import tech.vvp.vvp.entity.vehicle.Leopard2A7VEntity;
import tech.vvp.vvp.entity.vehicle.M1A2HVEntity;
import tech.vvp.vvp.entity.vehicle.M1A2SepIIEntity;
import tech.vvp.vvp.entity.vehicle.Mi24Entity;
import tech.vvp.vvp.entity.vehicle.Mi28Entity;
import tech.vvp.vvp.entity.vehicle.Mi8Entity;
import tech.vvp.vvp.entity.vehicle.Nh90Entity;
import tech.vvp.vvp.entity.vehicle.OplotEntity;
import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;
import tech.vvp.vvp.entity.vehicle.PautinaEntity;
import tech.vvp.vvp.entity.vehicle.PumaEntity;
import tech.vvp.vvp.entity.vehicle.StrykerEntity;
import tech.vvp.vvp.entity.vehicle.Stryker_M1296Entity;
import tech.vvp.vvp.entity.vehicle.T72B3MEntity;
import tech.vvp.vvp.entity.vehicle.T90MEntity;
import tech.vvp.vvp.entity.vehicle.TerminatorEntity;
import tech.vvp.vvp.entity.vehicle.UralEntity;
import tech.vvp.vvp.entity.vehicle.VartaEntity;
import tech.vvp.vvp.entity.vehicle.VartaPTRKEntity;
import tech.vvp.vvp.entity.vehicle.util.CompactEntityHitbox;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ENTITY_TYPES, (String)"vvp");
    private static final float IFV_W = 3.6f;
    private static final float IFV_H = 2.15f;
    private static final float TANK_W = 4.62f;
    private static final float TANK_H = 2.0f;
    private static final float HELI_W = 3.375f;
    private static final float HELI_H = 2.5f;
    private static final float TRUCK_W = 2.8f;
    private static final float TRUCK_H = 2.5f;
    private static final float VIS_IFV = 3.5f;
    private static final float VIS_BMP2 = 3.0f;
    private static final float VIS_TANK = 4.0f;
    private static final float VIS_MBT = 5.0f;
    private static final float VIS_HELI = 4.0f;
    private static final float VIS_TRUCK = 4.5f;
    public static final RegistryObject<EntityType<Btr4Entity>> BTR_4 = ModEntities.registerVehicle("btr_4", 3.6f, 2.15f, 3.5f, ModEntities.builder(Btr4Entity::new));
    public static final RegistryObject<EntityType<Btr3Entity>> BTR_3 = ModEntities.registerVehicle("btr_3", 3.6f, 2.15f, 3.5f, ModEntities.builder(Btr3Entity::new));
    public static final RegistryObject<EntityType<BradleyEntity>> BRADLEY = ModEntities.registerVehicle("bradley", 3.6f, 2.3f, 3.5f, ModEntities.builder(BradleyEntity::new));
    public static final RegistryObject<EntityType<BrmEntity>> BRM = ModEntities.registerVehicle("brm", 3.6f, 2.15f, 3.5f, ModEntities.builder(BrmEntity::new));
    public static final RegistryObject<EntityType<Bmp3Entity>> BMP_3 = ModEntities.registerVehicle("bmp_3", 3.6f, 2.15f, 3.5f, ModEntities.builder(Bmp3Entity::new));
    public static final RegistryObject<EntityType<Bmp2Entity>> BMP_2 = ModEntities.registerVehicle("bmp_2", 3.6f, 2.1f, 3.0f, ModEntities.builder(Bmp2Entity::new));
    public static final RegistryObject<EntityType<Bmp2BakhchaEntity>> BMP_2_BAKHCHA = ModEntities.registerVehicle("bmp_2_bakhcha", 3.6f, 2.1f, 3.0f, ModEntities.builder(Bmp2BakhchaEntity::new));
    public static final RegistryObject<EntityType<Bmp2MEntity>> BMP_2M = ModEntities.registerVehicle("bmp_2m", 3.6f, 2.1f, 3.0f, ModEntities.builder(Bmp2MEntity::new));
    public static final RegistryObject<EntityType<ChryzantemaEntity>> CHRYZANTEMA = ModEntities.registerVehicle("chryzantema", 3.6f, 2.15f, 3.5f, ModEntities.builder(ChryzantemaEntity::new));
    public static final RegistryObject<EntityType<CV90Entity>> CV_90 = ModEntities.registerVehicle("cv_90", 3.6f, 2.15f, 3.5f, ModEntities.builder(CV90Entity::new));
    public static final RegistryObject<EntityType<StrykerEntity>> STRYKER = ModEntities.registerVehicle("stryker", 3.6f, 2.15f, 3.5f, ModEntities.builder(StrykerEntity::new));
    public static final RegistryObject<EntityType<Stryker_M1296Entity>> STRYKER_M1296 = ModEntities.registerVehicle("stryker_m1296", 3.6f, 2.15f, 3.5f, ModEntities.builder(Stryker_M1296Entity::new));
    public static final RegistryObject<EntityType<TerminatorEntity>> TERMINATOR = ModEntities.registerVehicle("terminator", 3.6f, 2.15f, 3.5f, ModEntities.builder(TerminatorEntity::new));
    public static final RegistryObject<EntityType<BMPT3KEntity>> BMPT_3K = ModEntities.registerVehicle("bmpt_3k", 3.6f, 2.15f, 3.5f, ModEntities.builder(BMPT3KEntity::new));
    public static final RegistryObject<EntityType<T90MEntity>> T90_M = ModEntities.registerVehicle("t90_m", 4.62f, 2.0f, 4.0f, ModEntities.builder(T90MEntity::new));
    public static final RegistryObject<EntityType<M1A2HVEntity>> M1A2_HV = ModEntities.registerVehicle("m1a2_hv", 4.62f, 2.0f, 5.0f, ModEntities.builder(M1A2HVEntity::new));
    public static final RegistryObject<EntityType<M1A2SepIIEntity>> M1A2_SEP_II = ModEntities.registerVehicle("m1a2_sep_ii", 4.62f, 2.0f, 5.0f, ModEntities.builder(M1A2SepIIEntity::new));
    public static final RegistryObject<EntityType<PumaEntity>> PUMA = ModEntities.registerVehicle("puma", 3.6f, 2.3f, 3.5f, ModEntities.builder(PumaEntity::new));
    public static final RegistryObject<EntityType<FMTVEntity>> FMTV = ModEntities.registerVehicle("fmtv", 2.8f, 2.5f, 4.5f, ModEntities.builder(FMTVEntity::new));
    public static final RegistryObject<EntityType<Mi28Entity>> MI_28 = ModEntities.registerVehicle("mi_28", 3.375f, 3.375f, 4.0f, ModEntities.builder(Mi28Entity::new));
    public static final RegistryObject<EntityType<Mi24Entity>> MI_24 = ModEntities.registerVehicle("mi_24", 3.375f, 2.5f, 4.0f, ModEntities.builder(Mi24Entity::new));
    public static final RegistryObject<EntityType<Leopard2A7VEntity>> LEOPARD_2A7V = ModEntities.registerVehicle("leopard_2a7v", 4.62f, 2.0f, 4.0f, ModEntities.builder(Leopard2A7VEntity::new));
    public static final RegistryObject<EntityType<Leopard2A4Entity>> LEOPARD_2A4 = ModEntities.registerVehicle("leopard_2a4", 4.62f, 2.0f, 4.0f, ModEntities.builder(Leopard2A4Entity::new));
    public static final RegistryObject<EntityType<Ah64Entity>> AH_64 = ModEntities.registerVehicle("ah_64", 3.375f, 2.5f, 4.0f, ModEntities.builder(Ah64Entity::new));
    public static final RegistryObject<EntityType<GazTigrEntity>> GAZ_TIGR = ModEntities.registerVehicle("gaz_tigr", 2.4f, 2.0f, 2.0f, ModEntities.builder(GazTigrEntity::new));
    public static final RegistryObject<EntityType<HumveeEntity>> HUMVEE_MK19 = ModEntities.registerVehicle("humvee_mk19", 2.4f, 2.0f, 2.0f, ModEntities.builder(HumveeEntity::new));
    public static final RegistryObject<EntityType<ChallengerEntity>> CHALLENGER = ModEntities.registerVehicle("challenger", 4.62f, 2.0f, 4.0f, ModEntities.builder(ChallengerEntity::new));
    public static final RegistryObject<EntityType<T72B3MEntity>> T72_B3M = ModEntities.registerVehicle("t72_b3m", 4.62f, 2.0f, 4.0f, ModEntities.builder(T72B3MEntity::new));
    public static final RegistryObject<EntityType<UralEntity>> URAL = ModEntities.registerVehicle("ural", 2.8f, 3.0f, 4.5f, ModEntities.builder(UralEntity::new));
    public static final RegistryObject<EntityType<VartaEntity>> VARTA = ModEntities.registerVehicle("varta", 3.0f, 2.3f, 3.5f, ModEntities.builder(VartaEntity::new));
    public static final RegistryObject<EntityType<VartaPTRKEntity>> VARTA_PTRK = ModEntities.registerVehicle("varta_ptrk", 3.0f, 2.3f, 3.5f, ModEntities.builder(VartaPTRKEntity::new));
    public static final RegistryObject<EntityType<PantsirS1Entity>> PANTSIR_S1 = ModEntities.registerVehicle("pantsir_s1", 3.5f, 2.5f, 4.0f, ModEntities.builder(PantsirS1Entity::new));
    public static final RegistryObject<EntityType<Ags30Entity>> AGS_30 = ModEntities.registerVehicle("ags_30", 0.8f, 1.2f, 1.5f, ModEntities.builder(Ags30Entity::new).setTrackingRange(256));
    public static final RegistryObject<EntityType<KornetEntity>> KORNET = ModEntities.registerVehicle("kornet", 0.6f, 1.35f, 2.0f, ModEntities.builder(KornetEntity::new));
    public static final RegistryObject<EntityType<CobraEntity>> COBRA = ModEntities.registerVehicle("cobra", 3.375f, 2.5f, 4.0f, ModEntities.builder(CobraEntity::new));
    public static final RegistryObject<EntityType<CentauroEntity>> CENTAURO = ModEntities.registerVehicle("centauro", 4.62f, 2.2f, 4.0f, ModEntities.builder(CentauroEntity::new));
    public static final RegistryObject<EntityType<PantsirMissileEntity>> PANTSIR_MISSILE = ModEntities.register("pantsir_missile", EntityType.Builder.m_20704_(PantsirMissileEntity::new, (MobCategory)MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(256).setUpdateInterval(1).m_20716_().m_20719_().m_20699_(0.5f, 0.5f));
    public static final RegistryObject<EntityType<AjaxEntity>> AJAX = ModEntities.registerVehicle("ajax", 3.6f, 2.0f, 3.0f, ModEntities.builder(AjaxEntity::new));
    public static final RegistryObject<EntityType<Mi8Entity>> MI_8 = ModEntities.registerVehicle("mi_8", 3.375f, 2.5f, 4.0f, ModEntities.builder(Mi8Entity::new));
    public static final RegistryObject<EntityType<D30Entity>> D30 = ModEntities.registerVehicle("d30", 2.5f, 1.6f, 2.0f, ModEntities.builder(D30Entity::new).setTrackingRange(256));
    public static final RegistryObject<EntityType<Nh90Entity>> NH_90 = ModEntities.registerVehicle("nh_90", 3.375f, 2.5f, 4.0f, ModEntities.builder(Nh90Entity::new));
    public static final RegistryObject<EntityType<OplotEntity>> OPLOT = ModEntities.registerVehicle("oplot", 4.62f, 2.0f, 4.0f, ModEntities.builder(OplotEntity::new));
    public static final RegistryObject<EntityType<PautinaEntity>> PAUTINA = ModEntities.registerVehicle("pautina", 3.6f, 2.15f, 4.0f, ModEntities.builder(PautinaEntity::new));
    public static final RegistryObject<EntityType<HimarsEntity>> M142_HIMARS = ModEntities.registerVehicle("m142_himars", 2.8f, 2.5f, 4.5f, ModEntities.builder(HimarsEntity::new));
    public static final RegistryObject<EntityType<BushmasterEntity>> BUSHMASTER = ModEntities.registerVehicle("bushmaster", 2.8f, 2.5f, 4.5f, ModEntities.builder(BushmasterEntity::new));

    private static <T extends Entity> EntityType.Builder<T> builder(EntityType.EntityFactory<T> factory) {
        return EntityType.Builder.m_20704_(factory, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_();
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerVehicle(String name, float collisionWidth, float collisionHeight, float visualHeight, EntityType.Builder<T> entityTypeBuilder) {
        return ENTITY_TYPES.register(name, () -> {
            EntityType type = entityTypeBuilder.m_20699_(collisionWidth, collisionHeight).m_20712_(name);
            CompactEntityHitbox.registerVisualHeight(type, visualHeight);
            return type;
        });
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return ENTITY_TYPES.register(name, () -> entityTypeBuilder.m_20712_(name));
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}

