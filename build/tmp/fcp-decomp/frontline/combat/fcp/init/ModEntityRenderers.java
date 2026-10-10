/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.EntityRenderersEvent$RegisterRenderers
 *  net.minecraftforge.client.event.RegisterGuiOverlaysEvent
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package frontline.combat.fcp.init;

import frontline.combat.fcp.client.overlay.FcpDriverOverlay;
import frontline.combat.fcp.client.overlay.FcpPilotOverlay;
import frontline.combat.fcp.client.renderer.Aavp.AAVPRenderer;
import frontline.combat.fcp.client.renderer.Bmp1.BMP1AMRenderer;
import frontline.combat.fcp.client.renderer.Bmp1.BMP1Renderer;
import frontline.combat.fcp.client.renderer.Bmp1.BMP1URenderer;
import frontline.combat.fcp.client.renderer.Bmp1p.BMP1PRenderer;
import frontline.combat.fcp.client.renderer.Bmp2.BMP2Renderer;
import frontline.combat.fcp.client.renderer.Bmp2Noatgm.BMP2NoAtgmRenderer;
import frontline.combat.fcp.client.renderer.Bmp2d.BMP2DRenderer;
import frontline.combat.fcp.client.renderer.Bmp2m.BMP2MRenderer;
import frontline.combat.fcp.client.renderer.Btr3e.BTR3ERenderer;
import frontline.combat.fcp.client.renderer.Btr4mv1.BTR4MV1Renderer;
import frontline.combat.fcp.client.renderer.Btr80.BTR80Renderer;
import frontline.combat.fcp.client.renderer.Btr80Cope.BTR80CopeRenderer;
import frontline.combat.fcp.client.renderer.Btr82.BTR82Renderer;
import frontline.combat.fcp.client.renderer.Btr82Cope.BTR82CopeRenderer;
import frontline.combat.fcp.client.renderer.Fmtv.FMTVRenderer;
import frontline.combat.fcp.client.renderer.GazTigr.GazTigrGLRenderer;
import frontline.combat.fcp.client.renderer.GazTigr.GazTigrMGRenderer;
import frontline.combat.fcp.client.renderer.GazTigr.GazTigrRWSRenderer;
import frontline.combat.fcp.client.renderer.GazTigr.GazTigrRenderer;
import frontline.combat.fcp.client.renderer.Huey.HueyDoorGunnerM134Renderer;
import frontline.combat.fcp.client.renderer.Huey.HueyDoorGunnerM60Renderer;
import frontline.combat.fcp.client.renderer.Huey.HueyRenderer;
import frontline.combat.fcp.client.renderer.Huey.HueyRocketsRenderer;
import frontline.combat.fcp.client.renderer.Huey.VenomRenderer;
import frontline.combat.fcp.client.renderer.Humvee.HumveeRenderer;
import frontline.combat.fcp.client.renderer.Humvee.HumveeTOWRenderer;
import frontline.combat.fcp.client.renderer.JohnDeere.JohnDeereRenderer;
import frontline.combat.fcp.client.renderer.JohnDeere.SeederRenderer;
import frontline.combat.fcp.client.renderer.Kamaz.KamazRenderer;
import frontline.combat.fcp.client.renderer.Lav.Lav25Renderer;
import frontline.combat.fcp.client.renderer.Littlebird.LittlebirdArmedRenderer;
import frontline.combat.fcp.client.renderer.Littlebird.LittlebirdRenderer;
import frontline.combat.fcp.client.renderer.Matv.MATV9In1Renderer;
import frontline.combat.fcp.client.renderer.Matv.MATVCrowsRenderer;
import frontline.combat.fcp.client.renderer.Matv.MATVRenderer;
import frontline.combat.fcp.client.renderer.Matv.MATVTOWRenderer;
import frontline.combat.fcp.client.renderer.MemeVehicles.BigBirdRenderer;
import frontline.combat.fcp.client.renderer.MemeVehicles.LaHumveeRenderer;
import frontline.combat.fcp.client.renderer.MemeVehicles.WolfRenderer;
import frontline.combat.fcp.client.renderer.Mi17.MI17Renderer;
import frontline.combat.fcp.client.renderer.Novator.NovatorRenderer;
import frontline.combat.fcp.client.renderer.Projectile.Hellfire.LockOnHellfireRenderer;
import frontline.combat.fcp.client.renderer.Projectile.Hellfire.WireGuidedHellfireRenderer;
import frontline.combat.fcp.client.renderer.Projectile.Malyutka.MalyutkaRenderer;
import frontline.combat.fcp.client.renderer.Projectile.Sidewinder.SidewinderRenderer;
import frontline.combat.fcp.client.renderer.Stryker.StrykerDragoonRenderer;
import frontline.combat.fcp.client.renderer.Stryker.StrykerM2Renderer;
import frontline.combat.fcp.client.renderer.Stryker.StrykerMGSRenderer;
import frontline.combat.fcp.client.renderer.Stryker.StrykerMk19Renderer;
import frontline.combat.fcp.client.renderer.Stryker.StrykerMortarRenderer;
import frontline.combat.fcp.client.renderer.Stryker.StrykerTowRenderer;
import frontline.combat.fcp.client.renderer.T72av.T72AVRenderer;
import frontline.combat.fcp.client.renderer.Toyota.ToyotaHiluxBMPRenderer;
import frontline.combat.fcp.client.renderer.Toyota.ToyotaHiluxMortarRenderer;
import frontline.combat.fcp.client.renderer.Toyota.ToyotaHiluxRenderer;
import frontline.combat.fcp.client.renderer.Toyota.ToyotaHiluxRocketPodRenderer;
import frontline.combat.fcp.client.renderer.Toyota.ToyotaHiluxSpg9Renderer;
import frontline.combat.fcp.client.renderer.Toyota.ToyotaHiluxZu23Renderer;
import frontline.combat.fcp.client.renderer.Trailers.ExampleTrailer.ExampleTrailerRenderer;
import frontline.combat.fcp.client.renderer.Uaz.UAZDSHKARenderer;
import frontline.combat.fcp.client.renderer.Uaz.UAZRenderer;
import frontline.combat.fcp.client.renderer.Uaz.UAZSPG9Renderer;
import frontline.combat.fcp.client.renderer.Ural.UralGradRenderer;
import frontline.combat.fcp.client.renderer.Ural.UralRenderer;
import frontline.combat.fcp.client.renderer.Viper.ViperRenderer;
import frontline.combat.fcp.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="fcp", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public class ModEntityRenderers {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer((EntityType)ModEntities.TOYOTA_HILUX.get(), ToyotaHiluxRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.TOYOTA_HILUX_ROCKET_POD.get(), ToyotaHiluxRocketPodRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.TOYOTA_HILUX_BMP.get(), ToyotaHiluxBMPRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.TOYOTA_HILUX_SPG9.get(), ToyotaHiluxSpg9Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.TOYOTA_HILUX_MORTAR.get(), ToyotaHiluxMortarRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.TOYOTA_HILUX_ZU23.get(), ToyotaHiluxZu23Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.UAZ.get(), UAZRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.UAZ_DSHKA.get(), UAZDSHKARenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.UAZ_SPG9.get(), UAZSPG9Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.STRYKER_MGS.get(), StrykerMGSRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.STRYKER_M2.get(), StrykerM2Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.STRYKER_DRAGOON.get(), StrykerDragoonRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.STRYKER_MK19.get(), StrykerMk19Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.STRYKER_TOW.get(), StrykerTowRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.STRYKER_MORTAR.get(), StrykerMortarRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.LITTLEBIRD.get(), LittlebirdRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.LITTLEBIRD_ARMED.get(), LittlebirdArmedRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BMP1.get(), BMP1Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BMP1U.get(), BMP1URenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BMP1AM.get(), BMP1AMRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BMP2.get(), BMP2Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BMP1P.get(), BMP1PRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BMP2D.get(), BMP2DRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BMP2M.get(), BMP2MRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BMP2_NOATGM.get(), BMP2NoAtgmRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.AAVP.get(), AAVPRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.LAV25.get(), Lav25Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.T72AV.get(), T72AVRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.URAL.get(), UralRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.URAL_GRAD.get(), UralGradRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.KAMAZ.get(), KamazRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.VIPER.get(), ViperRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.GAZ_TIGR.get(), GazTigrRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.GAZ_TIGR_RWS.get(), GazTigrRWSRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.GAZ_TIGR_MG.get(), GazTigrMGRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.GAZ_TIGR_GL.get(), GazTigrGLRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.HUEY.get(), HueyRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.HUEY_ROCKETS.get(), HueyRocketsRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.HUEY_DOOR_GUNNER_M60.get(), HueyDoorGunnerM60Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.HUEY_DOOR_GUNNER_M134.get(), HueyDoorGunnerM134Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.VENOM.get(), VenomRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.NOVATOR.get(), NovatorRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.MATV.get(), MATVRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.MATV_TOW.get(), MATVTOWRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.MATV_CROW.get(), MATVCrowsRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.MATV_9IN1.get(), MATV9In1Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.HUMVEE.get(), HumveeRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.HUMVEE_TOW.get(), HumveeTOWRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BTR82.get(), BTR82Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BTR3E.get(), BTR3ERenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BTR4MV1.get(), BTR4MV1Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BTR80.get(), BTR80Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BTR80_COPE.get(), BTR80CopeRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BTR82_COPE.get(), BTR82CopeRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.MI17.get(), MI17Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.FMTV.get(), FMTVRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.JOHN_DEERE.get(), JohnDeereRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.LOCK_ON_HELLFIRE.get(), LockOnHellfireRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.WIRE_GUIDED_HELLFIRE.get(), WireGuidedHellfireRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.SIDEWINDER.get(), SidewinderRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.MALYUTKA.get(), MalyutkaRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BIGBIRD.get(), BigBirdRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.LA_HUMVEE.get(), LaHumveeRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.T14_ARMATA.get(), WolfRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.EXAMPLE_TRAILER.get(), ExampleTrailerRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.SEEDER.get(), SeederRenderer::new);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelowAll("fcp_driver_hud", (IGuiOverlay)FcpDriverOverlay.INSTANCE);
        event.registerBelowAll("fcp_pilot_hud", (IGuiOverlay)FcpPilotOverlay.INSTANCE);
    }
}

