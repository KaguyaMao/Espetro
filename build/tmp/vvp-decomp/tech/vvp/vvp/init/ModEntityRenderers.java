/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.projectile.BasicProjectileRenderer
 *  net.minecraft.world.entity.EntityType
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.EntityRenderersEvent$RegisterRenderers
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package tech.vvp.vvp.init;

import com.atsuishio.superbwarfare.client.renderer.projectile.BasicProjectileRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.client.renderer.entity.vehicle.Ags30Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.Ah64Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.AjaxRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.BMPT3KRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.Bmp2BakhcaRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.Bmp2MRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.Bmp2Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.Bmp3Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.BradleyRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.BrmRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.Btr3Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.BushmasterRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.CV90Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.CentauroRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.ChallengerRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.ChryzantemaRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.CobraRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.D30Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.FMTVRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.GazTigrRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.HumveeRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.KornetRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.Leopard2A4Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.Leopard2A7VRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.M142HimarsRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.M1A2HVRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.M1A2SepIIRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.Mi24Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.Mi28Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.Mi8Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.Nh90Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.OplotRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.PantsirS1Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.PautinaRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.PumaRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.StrykerRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.Stryker_M1296Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.T72B3MRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.T90MRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.TerminatorRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.UralRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.VartaPTRKRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.VartaRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.btr4Renderer;
import tech.vvp.vvp.init.ModEntities;

@Mod.EventBusSubscriber(modid="vvp", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public class ModEntityRenderers {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer((EntityType)ModEntities.BTR_4.get(), btr4Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BRADLEY.get(), BradleyRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.TERMINATOR.get(), TerminatorRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.T90_M.get(), T90MRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BRM.get(), BrmRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BMP_3.get(), Bmp3Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.CHRYZANTEMA.get(), ChryzantemaRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.PUMA.get(), PumaRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.STRYKER.get(), StrykerRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.STRYKER_M1296.get(), Stryker_M1296Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.FMTV.get(), FMTVRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.GAZ_TIGR.get(), GazTigrRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.HUMVEE_MK19.get(), HumveeRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.MI_28.get(), Mi28Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.MI_24.get(), Mi24Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.LEOPARD_2A7V.get(), Leopard2A7VRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.LEOPARD_2A4.get(), Leopard2A4Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.AH_64.get(), Ah64Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.CHALLENGER.get(), ChallengerRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BMP_2.get(), Bmp2Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.T72_B3M.get(), T72B3MRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BMP_2M.get(), Bmp2MRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.URAL.get(), UralRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.VARTA.get(), VartaRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.PANTSIR_S1.get(), PantsirS1Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.KORNET.get(), KornetRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.AGS_30.get(), Ags30Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.COBRA.get(), CobraRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.CENTAURO.get(), CentauroRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BMPT_3K.get(), BMPT3KRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.AJAX.get(), AjaxRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.OPLOT.get(), OplotRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BTR_3.get(), Btr3Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.PAUTINA.get(), PautinaRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.CV_90.get(), CV90Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.M1A2_HV.get(), M1A2HVRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.M1A2_SEP_II.get(), M1A2SepIIRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.VARTA_PTRK.get(), VartaPTRKRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BMP_2_BAKHCHA.get(), Bmp2BakhcaRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.PANTSIR_MISSILE.get(), BasicProjectileRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.MI_8.get(), Mi8Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.D30.get(), D30Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.NH_90.get(), Nh90Renderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.M142_HIMARS.get(), M142HimarsRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.BUSHMASTER.get(), BushmasterRenderer::new);
    }
}

