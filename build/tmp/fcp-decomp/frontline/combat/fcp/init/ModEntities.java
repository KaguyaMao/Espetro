/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package frontline.combat.fcp.init;

import frontline.combat.fcp.entity.projectile.Hellfire.LockOnHellfireEntity;
import frontline.combat.fcp.entity.projectile.Hellfire.WireGuidedHellfireEntity;
import frontline.combat.fcp.entity.projectile.Malyutka.MalyutkaEntity;
import frontline.combat.fcp.entity.projectile.Sidewinder.SidewinderEntity;
import frontline.combat.fcp.entity.vehicle.Aavp.AAVPEntity;
import frontline.combat.fcp.entity.vehicle.Bmp1.BMP1AMEntity;
import frontline.combat.fcp.entity.vehicle.Bmp1.BMP1Entity;
import frontline.combat.fcp.entity.vehicle.Bmp1.BMP1UEntity;
import frontline.combat.fcp.entity.vehicle.Bmp1p.BMP1PEntity;
import frontline.combat.fcp.entity.vehicle.Bmp2.BMP2Entity;
import frontline.combat.fcp.entity.vehicle.Bmp2Noatgm.BMP2NoAtgmEntity;
import frontline.combat.fcp.entity.vehicle.Bmp2d.BMP2DEntity;
import frontline.combat.fcp.entity.vehicle.Bmp2m.BMP2MEntity;
import frontline.combat.fcp.entity.vehicle.Btr3e.BTR3EEntity;
import frontline.combat.fcp.entity.vehicle.Btr4mv1.BTR4MV1Entity;
import frontline.combat.fcp.entity.vehicle.Btr80.BTR80Entity;
import frontline.combat.fcp.entity.vehicle.Btr80Cope.BTR80CopeEntity;
import frontline.combat.fcp.entity.vehicle.Btr82.BTR82Entity;
import frontline.combat.fcp.entity.vehicle.Btr82Cope.BTR82CopeEntity;
import frontline.combat.fcp.entity.vehicle.Fmtv.FMTVEntity;
import frontline.combat.fcp.entity.vehicle.GazTigr.GazTigrEntity;
import frontline.combat.fcp.entity.vehicle.GazTigr.GazTigrGLEntity;
import frontline.combat.fcp.entity.vehicle.GazTigr.GazTigrMGEntity;
import frontline.combat.fcp.entity.vehicle.GazTigr.GazTigrRWSEntity;
import frontline.combat.fcp.entity.vehicle.Huey.HueyDoorGunnerM134Entity;
import frontline.combat.fcp.entity.vehicle.Huey.HueyDoorGunnerM60Entity;
import frontline.combat.fcp.entity.vehicle.Huey.HueyEntity;
import frontline.combat.fcp.entity.vehicle.Huey.HueyRocketsEntity;
import frontline.combat.fcp.entity.vehicle.Huey.VenomEntity;
import frontline.combat.fcp.entity.vehicle.Humvee.HumveeEntity;
import frontline.combat.fcp.entity.vehicle.Humvee.HumveeTOWEntity;
import frontline.combat.fcp.entity.vehicle.JohnDeere.JohnDeereEntity;
import frontline.combat.fcp.entity.vehicle.JohnDeere.SeederEntity;
import frontline.combat.fcp.entity.vehicle.Kamaz.KamazEntity;
import frontline.combat.fcp.entity.vehicle.Lav.Lav25Entity;
import frontline.combat.fcp.entity.vehicle.Littlebird.LittlebirdArmedEntity;
import frontline.combat.fcp.entity.vehicle.Littlebird.LittlebirdEntity;
import frontline.combat.fcp.entity.vehicle.Matv.MATV9In1Entity;
import frontline.combat.fcp.entity.vehicle.Matv.MATVCrowsEntity;
import frontline.combat.fcp.entity.vehicle.Matv.MATVEntity;
import frontline.combat.fcp.entity.vehicle.Matv.MATVTOWEntity;
import frontline.combat.fcp.entity.vehicle.MemeVehicles.BigBirdEntity;
import frontline.combat.fcp.entity.vehicle.MemeVehicles.LaHumveeEntity;
import frontline.combat.fcp.entity.vehicle.MemeVehicles.WolfEntity;
import frontline.combat.fcp.entity.vehicle.Mi17.MI17Entity;
import frontline.combat.fcp.entity.vehicle.Novator.NovatorEntity;
import frontline.combat.fcp.entity.vehicle.Stryker.StrykerDragoonEntity;
import frontline.combat.fcp.entity.vehicle.Stryker.StrykerM2Entity;
import frontline.combat.fcp.entity.vehicle.Stryker.StrykerMGSEntity;
import frontline.combat.fcp.entity.vehicle.Stryker.StrykerMk19Entity;
import frontline.combat.fcp.entity.vehicle.Stryker.StrykerMortarEntity;
import frontline.combat.fcp.entity.vehicle.Stryker.StrykerTowEntity;
import frontline.combat.fcp.entity.vehicle.T72av.T72AVEntity;
import frontline.combat.fcp.entity.vehicle.Toyota.ToyotaHiluxBMPEntity;
import frontline.combat.fcp.entity.vehicle.Toyota.ToyotaHiluxEntity;
import frontline.combat.fcp.entity.vehicle.Toyota.ToyotaHiluxMortarEntity;
import frontline.combat.fcp.entity.vehicle.Toyota.ToyotaHiluxRocketPodEntity;
import frontline.combat.fcp.entity.vehicle.Toyota.ToyotaHiluxSpg9Entity;
import frontline.combat.fcp.entity.vehicle.Toyota.ToyotaHiluxZu23Entity;
import frontline.combat.fcp.entity.vehicle.Trailers.ExampleTrailer.ExampleTrailerEntity;
import frontline.combat.fcp.entity.vehicle.Uaz.UAZDSHKAEntity;
import frontline.combat.fcp.entity.vehicle.Uaz.UAZEntity;
import frontline.combat.fcp.entity.vehicle.Uaz.UAZSPG9Entity;
import frontline.combat.fcp.entity.vehicle.Ural.UralEntity;
import frontline.combat.fcp.entity.vehicle.Ural.UralGradEntity;
import frontline.combat.fcp.entity.vehicle.Viper.ViperEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ENTITY_TYPES, (String)"fcp");
    public static final RegistryObject<EntityType<ToyotaHiluxEntity>> TOYOTA_HILUX = ModEntities.register("toyota_hilux", EntityType.Builder.m_20704_(ToyotaHiluxEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<ToyotaHiluxRocketPodEntity>> TOYOTA_HILUX_ROCKET_POD = ModEntities.register("toyota_hilux_rocket_pod", EntityType.Builder.m_20704_(ToyotaHiluxRocketPodEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<ToyotaHiluxBMPEntity>> TOYOTA_HILUX_BMP = ModEntities.register("toyota_hilux_bmp", EntityType.Builder.m_20704_(ToyotaHiluxBMPEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<ToyotaHiluxSpg9Entity>> TOYOTA_HILUX_SPG9 = ModEntities.register("toyota_hilux_spg9", EntityType.Builder.m_20704_(ToyotaHiluxSpg9Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<ToyotaHiluxMortarEntity>> TOYOTA_HILUX_MORTAR = ModEntities.register("toyota_hilux_mortar", EntityType.Builder.m_20704_(ToyotaHiluxMortarEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<ToyotaHiluxZu23Entity>> TOYOTA_HILUX_ZU23 = ModEntities.register("toyota_hilux_zu23", EntityType.Builder.m_20704_(ToyotaHiluxZu23Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<UAZEntity>> UAZ = ModEntities.register("uaz", EntityType.Builder.m_20704_(UAZEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(2.0f, 2.0f));
    public static final RegistryObject<EntityType<UAZDSHKAEntity>> UAZ_DSHKA = ModEntities.register("uaz_dshka", EntityType.Builder.m_20704_(UAZDSHKAEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(2.0f, 2.0f));
    public static final RegistryObject<EntityType<UAZSPG9Entity>> UAZ_SPG9 = ModEntities.register("uaz_spg9", EntityType.Builder.m_20704_(UAZSPG9Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(2.0f, 2.0f));
    public static final RegistryObject<EntityType<StrykerMGSEntity>> STRYKER_MGS = ModEntities.register("stryker_mgs", EntityType.Builder.m_20704_(StrykerMGSEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<StrykerM2Entity>> STRYKER_M2 = ModEntities.register("stryker_m2", EntityType.Builder.m_20704_(StrykerM2Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<StrykerDragoonEntity>> STRYKER_DRAGOON = ModEntities.register("stryker_dragoon", EntityType.Builder.m_20704_(StrykerDragoonEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<StrykerMk19Entity>> STRYKER_MK19 = ModEntities.register("stryker_mk19", EntityType.Builder.m_20704_(StrykerMk19Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<StrykerTowEntity>> STRYKER_TOW = ModEntities.register("stryker_tow", EntityType.Builder.m_20704_(StrykerTowEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<StrykerMortarEntity>> STRYKER_MORTAR = ModEntities.register("stryker_mortar", EntityType.Builder.m_20704_(StrykerMortarEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<LittlebirdEntity>> LITTLEBIRD = ModEntities.register("littlebird", EntityType.Builder.m_20704_(LittlebirdEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(2.0f, 2.0f));
    public static final RegistryObject<EntityType<LittlebirdArmedEntity>> LITTLEBIRD_ARMED = ModEntities.register("littlebird_armed", EntityType.Builder.m_20704_(LittlebirdArmedEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(2.0f, 2.0f));
    public static final RegistryObject<EntityType<BMP1Entity>> BMP1 = ModEntities.register("bmp1", EntityType.Builder.m_20704_(BMP1Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<BMP1UEntity>> BMP1U = ModEntities.register("bmp1u", EntityType.Builder.m_20704_(BMP1UEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<BMP1AMEntity>> BMP1AM = ModEntities.register("bmp1am", EntityType.Builder.m_20704_(BMP1AMEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<BMP2Entity>> BMP2 = ModEntities.register("bmp2", EntityType.Builder.m_20704_(BMP2Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<BMP1PEntity>> BMP1P = ModEntities.register("bmp1p", EntityType.Builder.m_20704_(BMP1PEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<BMP2DEntity>> BMP2D = ModEntities.register("bmp2d", EntityType.Builder.m_20704_(BMP2DEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<BMP2MEntity>> BMP2M = ModEntities.register("bmp2m", EntityType.Builder.m_20704_(BMP2MEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<BMP2NoAtgmEntity>> BMP2_NOATGM = ModEntities.register("bmp2_noatgm", EntityType.Builder.m_20704_(BMP2NoAtgmEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<AAVPEntity>> AAVP = ModEntities.register("aavp", EntityType.Builder.m_20704_(AAVPEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<Lav25Entity>> LAV25 = ModEntities.register("lav25", EntityType.Builder.m_20704_(Lav25Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<T72AVEntity>> T72AV = ModEntities.register("t72av", EntityType.Builder.m_20704_(T72AVEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<UralEntity>> URAL = ModEntities.register("ural", EntityType.Builder.m_20704_(UralEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<UralGradEntity>> URAL_GRAD = ModEntities.register("ural_grad", EntityType.Builder.m_20704_(UralGradEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<KamazEntity>> KAMAZ = ModEntities.register("kamaz", EntityType.Builder.m_20704_(KamazEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<ViperEntity>> VIPER = ModEntities.register("viper", EntityType.Builder.m_20704_(ViperEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<GazTigrEntity>> GAZ_TIGR = ModEntities.register("gaz_tigr", EntityType.Builder.m_20704_(GazTigrEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<GazTigrRWSEntity>> GAZ_TIGR_RWS = ModEntities.register("gaz_tigr_rws", EntityType.Builder.m_20704_(GazTigrRWSEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<GazTigrMGEntity>> GAZ_TIGR_MG = ModEntities.register("gaz_tigr_mg", EntityType.Builder.m_20704_(GazTigrMGEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<GazTigrGLEntity>> GAZ_TIGR_GL = ModEntities.register("gaz_tigr_gl", EntityType.Builder.m_20704_(GazTigrGLEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<HueyEntity>> HUEY = ModEntities.register("huey", EntityType.Builder.m_20704_(HueyEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<HueyRocketsEntity>> HUEY_ROCKETS = ModEntities.register("huey_rockets", EntityType.Builder.m_20704_(HueyRocketsEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<HueyDoorGunnerM60Entity>> HUEY_DOOR_GUNNER_M60 = ModEntities.register("huey_door_gunner_m60", EntityType.Builder.m_20704_(HueyDoorGunnerM60Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<HueyDoorGunnerM134Entity>> HUEY_DOOR_GUNNER_M134 = ModEntities.register("huey_door_gunner_m134", EntityType.Builder.m_20704_(HueyDoorGunnerM134Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<VenomEntity>> VENOM = ModEntities.register("venom", EntityType.Builder.m_20704_(VenomEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<NovatorEntity>> NOVATOR = ModEntities.register("novator", EntityType.Builder.m_20704_(NovatorEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<MATVEntity>> MATV = ModEntities.register("matv", EntityType.Builder.m_20704_(MATVEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<MATVTOWEntity>> MATV_TOW = ModEntities.register("matv_tow", EntityType.Builder.m_20704_(MATVTOWEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<MATVCrowsEntity>> MATV_CROW = ModEntities.register("matv_crow", EntityType.Builder.m_20704_(MATVCrowsEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<MATV9In1Entity>> MATV_9IN1 = ModEntities.register("matv_9in1", EntityType.Builder.m_20704_(MATV9In1Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<HumveeEntity>> HUMVEE = ModEntities.register("humvee", EntityType.Builder.m_20704_(HumveeEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<HumveeTOWEntity>> HUMVEE_TOW = ModEntities.register("humvee_tow", EntityType.Builder.m_20704_(HumveeTOWEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<BTR3EEntity>> BTR3E = ModEntities.register("btr3e", EntityType.Builder.m_20704_(BTR3EEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<BTR4MV1Entity>> BTR4MV1 = ModEntities.register("btr4mv1", EntityType.Builder.m_20704_(BTR4MV1Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<BTR82Entity>> BTR82 = ModEntities.register("btr82", EntityType.Builder.m_20704_(BTR82Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<BTR80Entity>> BTR80 = ModEntities.register("btr80", EntityType.Builder.m_20704_(BTR80Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<BTR80CopeEntity>> BTR80_COPE = ModEntities.register("btr80_cope", EntityType.Builder.m_20704_(BTR80CopeEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<BTR82CopeEntity>> BTR82_COPE = ModEntities.register("btr82_cope", EntityType.Builder.m_20704_(BTR82CopeEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<FMTVEntity>> FMTV = ModEntities.register("fmtv", EntityType.Builder.m_20704_(FMTVEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<LockOnHellfireEntity>> LOCK_ON_HELLFIRE = ModEntities.register("lock_on_hellfire", EntityType.Builder.m_20704_(LockOnHellfireEntity::new, (MobCategory)MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(256).setUpdateInterval(1).m_20716_().m_20719_().m_20699_(0.5f, 0.5f));
    public static final RegistryObject<EntityType<WireGuidedHellfireEntity>> WIRE_GUIDED_HELLFIRE = ModEntities.register("wire_guided_hellfire", EntityType.Builder.m_20704_(WireGuidedHellfireEntity::new, (MobCategory)MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(256).setUpdateInterval(1).m_20716_().m_20719_().m_20699_(0.5f, 0.5f));
    public static final RegistryObject<EntityType<SidewinderEntity>> SIDEWINDER = ModEntities.register("sidewinder", EntityType.Builder.m_20704_(SidewinderEntity::new, (MobCategory)MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(256).setUpdateInterval(1).m_20716_().m_20719_().m_20699_(0.5f, 0.5f));
    public static final RegistryObject<EntityType<MalyutkaEntity>> MALYUTKA = ModEntities.register("malyutka", EntityType.Builder.m_20704_(MalyutkaEntity::new, (MobCategory)MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(256).setUpdateInterval(1).m_20716_().m_20719_().m_20699_(0.5f, 0.5f));
    public static final RegistryObject<EntityType<BigBirdEntity>> BIGBIRD = ModEntities.register("bigbird", EntityType.Builder.m_20704_(BigBirdEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(2.0f, 2.0f));
    public static final RegistryObject<EntityType<LaHumveeEntity>> LA_HUMVEE = ModEntities.register("la_humvee", EntityType.Builder.m_20704_(LaHumveeEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<WolfEntity>> T14_ARMATA = ModEntities.register("t14_armata", EntityType.Builder.m_20704_(WolfEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(0.5f, 0.5f));
    public static final RegistryObject<EntityType<MI17Entity>> MI17 = ModEntities.register("mi17", EntityType.Builder.m_20704_(MI17Entity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(3.0f, 2.0f));
    public static final RegistryObject<EntityType<JohnDeereEntity>> JOHN_DEERE = ModEntities.register("john_deere", EntityType.Builder.m_20704_(JohnDeereEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(2.0f, 3.0f));
    public static final RegistryObject<EntityType<SeederEntity>> SEEDER = ModEntities.register("seeder", EntityType.Builder.m_20704_(SeederEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(4.0f, 3.0f));
    public static final RegistryObject<EntityType<ExampleTrailerEntity>> EXAMPLE_TRAILER = ModEntities.register("example_trailer", EntityType.Builder.m_20704_(ExampleTrailerEntity::new, (MobCategory)MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).m_20719_().m_20699_(5.0f, 3.0f));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return ENTITY_TYPES.register(name, () -> entityTypeBuilder.m_20712_("fcp:" + name));
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}

