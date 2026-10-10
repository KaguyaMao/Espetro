/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.RegisterClientReloadListenersEvent
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.client.event.RenderGuiEvent$Post
 *  net.minecraftforge.client.event.RenderNameTagEvent
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  org.lwjgl.glfw.GLFW
 */
package org.espetro;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.espetro.Espetro;
import org.espetro.bastion.BastionItems;
import org.espetro.bastion.RadioBlock;
import org.espetro.client.EquipZoneRenderer;
import org.espetro.client.FortificationPlacementController;
import org.espetro.client.LeaderOverheadRenderer;
import org.espetro.client.TeammateNameTagRenderer;
import org.espetro.client.audio.ClientFormationAudioManager;
import org.espetro.client.aui.AuiRadial;
import org.espetro.client.aui.AuiTips;
import org.espetro.client.gui.AuraTipAboveScreen;
import org.espetro.client.gui.AuraTipRadialController;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.EspetroHudOverlay;
import org.espetro.client.gui.FobSupplyHud;
import org.espetro.client.gui.RadioRadialController;
import org.espetro.client.gui.ResupplyRadialController;
import org.espetro.client.gui.TutorialClientController;
import org.espetro.client.gui.TutorialHudOverlay;
import org.espetro.client.gui.TutorialOverlay;
import org.espetro.client.gui.VanillaHudLayout;
import org.espetro.client.gui.VehicleSupplyHud;
import org.espetro.client.gui.VehicleWheelController;
import org.espetro.client.vehicle.DismountGate;
import org.espetro.client.vehicle.SeatSwitchGate;
import org.espetro.client.vehicle.VehicleMountClientGate;
import org.espetro.network.NetworkManager;
import org.lwjgl.glfw.GLFW;

public class EspetroClient {
    private static boolean tutorialExitMouseWasDown;

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EspetroClient::registerKeyBindings);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EspetroClient::registerReloadListeners);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EspetroClient::onClientSetup);
        MinecraftForge.EVENT_BUS.addListener(EspetroClient::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(VanillaHudLayout::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(VanillaHudLayout::onRenderOverlayPre);
        MinecraftForge.EVENT_BUS.addListener(EspetroClient::onRenderOverlay);
        MinecraftForge.EVENT_BUS.addListener(EspetroClient::onRenderNameTag);
        AuraTipRadialController.initialize();
        RadioRadialController.initialize();
        VehicleWheelController.initialize();
        ResupplyRadialController.initialize();
        VehicleSupplyHud.register();
        VehicleMountClientGate.register();
        DismountGate.register();
        SeatSwitchGate.register();
        FobSupplyHud.register();
        MinecraftForge.EVENT_BUS.addListener(AuraTipAboveScreen::onScreenRenderPost);
        MinecraftForge.EVENT_BUS.addListener(EquipZoneRenderer::onRenderLevel);
        MinecraftForge.EVENT_BUS.addListener(EspetroClient::onRightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(LeaderOverheadRenderer::onRenderLevelStage);
        MinecraftForge.EVENT_BUS.addListener(FortificationPlacementController::onInteraction);
        MinecraftForge.EVENT_BUS.addListener(FortificationPlacementController::render);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer((Block)BastionItems.ON_BUILDING_BLOCK, (RenderType)RenderType.m_110463_()));
    }

    private static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        KeyMapping keyTeam = new KeyMapping("key.espetro.team", 75, "key.categories.espetro");
        KeyMapping keyClass = new KeyMapping("key.espetro.class", 74, "key.categories.espetro");
        KeyMapping keyRadial = new KeyMapping("key.espetro.radial", 342, "key.categories.espetro");
        event.register(keyTeam);
        event.register(keyClass);
        event.register(keyRadial);
        Espetro.KEY_TEAM = keyTeam;
        Espetro.KEY_CLASS = keyClass;
        Espetro.KEY_RADIAL = keyRadial;
    }

    private static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(resourceManager -> FobSupplyHud.onResourceReload());
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        KeyMapping key;
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null) {
            return;
        }
        Object object = Espetro.KEY_RADIAL;
        KeyMapping radialKey = object instanceof KeyMapping ? (key = (KeyMapping)object) : null;
        AuraTipRadialController.tick(mc, radialKey);
        RadioRadialController.tick(mc);
        VehicleWheelController.tick(mc);
        ResupplyRadialController.tick();
        AuiRadial.tickInput(mc);
        AuiTips.tick();
        FortificationPlacementController.tick(mc);
        TutorialOverlay.tick();
        ClientFormationAudioManager.tick(mc);
        if (TutorialClientController.isActive() && mc.f_91080_ == null) {
            boolean down;
            boolean bl = down = GLFW.glfwGetMouseButton((long)mc.m_91268_().m_85439_(), (int)0) == 1;
            if (down && !tutorialExitMouseWasDown) {
                double scale = mc.m_91268_().m_85449_();
                double mx = mc.f_91067_.m_91589_() / scale;
                double my = mc.f_91067_.m_91594_() / scale;
                TutorialHudOverlay.mouseClicked(mx, my, 0);
            }
            tutorialExitMouseWasDown = down;
        } else {
            tutorialExitMouseWasDown = false;
        }
        if (mc.f_91074_ == null) {
            return;
        }
        if (Espetro.KEY_TEAM != null && ((KeyMapping)Espetro.KEY_TEAM).m_90859_() && mc.f_91080_ == null) {
            NetworkManager.requestGameState();
        }
        if (Espetro.KEY_CLASS != null && ((KeyMapping)Espetro.KEY_CLASS).m_90859_()) {
            if (mc.f_91080_ != null) {
                return;
            }
            ClientGameState.tryOpenJKeyScreen();
        }
    }

    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().m_5776_()) {
            return;
        }
        BlockState state = event.getLevel().m_8055_(event.getPos());
        if (!(state.m_60734_() instanceof RadioBlock)) {
            return;
        }
        if (FortificationPlacementController.isPreviewing() || event.getEntity().m_21205_().m_41720_() == Items.f_42384_) {
            return;
        }
    }

    private static void onRenderOverlay(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null || mc.f_91073_ == null) {
            return;
        }
        if (mc.f_91080_ == null) {
            EspetroHudOverlay.render(event.getGuiGraphics(), mc, event.getPartialTick());
        }
        AuiRadial.render(event.getGuiGraphics(), event.getPartialTick());
        TutorialHudOverlay.render(event.getGuiGraphics(), mc, event.getPartialTick());
    }

    private static void onRenderNameTag(RenderNameTagEvent event) {
        TeammateNameTagRenderer.onRenderNameTag(event);
    }
}

