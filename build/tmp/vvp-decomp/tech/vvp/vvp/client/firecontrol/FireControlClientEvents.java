/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.Input
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.InputEvent$InteractionKeyMappingTriggered
 *  net.minecraftforge.client.event.InputEvent$Key
 *  net.minecraftforge.client.event.InputEvent$MouseScrollingEvent
 *  net.minecraftforge.client.event.MovementInputUpdateEvent
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.client.event.RenderGuiOverlayEvent$Pre
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 */
package tech.vvp.vvp.client.firecontrol;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.client.firecontrol.FireControlClientState;
import tech.vvp.vvp.client.firecontrol.FireControlKeyBindings;
import tech.vvp.vvp.client.firecontrol.FireControlScreenTexture;
import tech.vvp.vvp.entity.vehicle.HimarsEntity;
import tech.vvp.vvp.firecontrol.HimarsBallisticsUtil;
import tech.vvp.vvp.network.VVPNetwork;
import tech.vvp.vvp.network.message.HimarsFdcCancelMessage;
import tech.vvp.vvp.network.message.HimarsFdcDesignateMessage;
import tech.vvp.vvp.network.message.HimarsFdcFireMessage;

@Mod.EventBusSubscriber(modid="vvp", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public class FireControlClientEvents {
    public static final ResourceLocation SCREEN_TEXTURE_ID = VVP.loc("fire_control_screen");
    private static FireControlScreenTexture screenTexture;
    private static final float PAN_SPEED = 5.5f;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Minecraft mc = Minecraft.m_91087_();
            screenTexture = new FireControlScreenTexture();
            mc.m_91097_().m_118495_(SCREEN_TEXTURE_ID, (AbstractTexture)screenTexture);
        });
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(FireControlKeyBindings.OPEN_FDC_MAP);
    }

    @Mod.EventBusSubscriber(modid="vvp", value={Dist.CLIENT})
    public static class Runtime {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.m_91087_();
            LocalPlayer player = mc.f_91074_;
            if (player == null || mc.f_91080_ != null) {
                return;
            }
            if (FireControlKeyBindings.OPEN_FDC_MAP.m_90859_()) {
                Runtime.toggleTablet((Player)player);
                return;
            }
            if (!FireControlClientState.isMapVisible()) {
                return;
            }
            if (FireControlKeyBindings.OPEN_FDC_MAP.m_90832_(event.getKey(), event.getScanCode())) {
                return;
            }
            if (event.getKey() == 32 && event.getAction() == 1) {
                Runtime.designateTarget((Player)player);
            }
            if ((event.getKey() == 257 || event.getKey() == 335) && event.getAction() == 1) {
                Runtime.fireTarget((Player)player);
            }
        }

        @SubscribeEvent
        public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
            if (!FireControlClientState.isMapVisible()) {
                return;
            }
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91074_ == null || mc.f_91080_ != null) {
                return;
            }
            double delta = event.getScrollDelta();
            if (delta > 0.0) {
                FireControlClientState.setZoom(FireControlClientState.getZoom() * 1.12);
            } else if (delta < 0.0) {
                FireControlClientState.setZoom(FireControlClientState.getZoom() / 1.12);
            }
            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onMouseClick(InputEvent.InteractionKeyMappingTriggered event) {
            if (!FireControlClientState.isMapVisible() || !event.isAttack()) {
                return;
            }
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91074_ == null || mc.f_91080_ != null) {
                return;
            }
            event.setCanceled(true);
            Runtime.designateTarget((Player)mc.f_91074_);
        }

        @SubscribeEvent
        public static void onMovementInput(MovementInputUpdateEvent event) {
            if (!FireControlClientState.isMapVisible()) {
                return;
            }
            Input input = event.getInput();
            float forward = input.f_108567_;
            float left = input.f_108566_;
            if (forward != 0.0f || left != 0.0f) {
                FireControlClientState.panMap(-left * 5.5f, -forward * 5.5f);
            }
            input.f_108567_ = 0.0f;
            input.f_108566_ = 0.0f;
            input.f_108568_ = false;
            input.f_108569_ = false;
            input.f_108570_ = false;
            input.f_108571_ = false;
            input.f_108572_ = false;
            input.f_108573_ = false;
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END || screenTexture == null) {
                return;
            }
            Minecraft mc = Minecraft.m_91087_();
            LocalPlayer player = mc.f_91074_;
            if (player == null) {
                FireControlClientState.forceOff();
                return;
            }
            FireControlClientState.tickBrightness();
            if (FireControlClientState.isScreenLit()) {
                if (FireControlClientState.isTabletOpen()) {
                    HimarsEntity himars = FireControlClientState.getBoundHimars();
                    if (himars == null) {
                        FireControlClientState.debugMsg("Shutdown: boundHimars is null");
                        FireControlClientState.beginShutdown();
                    } else {
                        Entity entity = player.m_20202_();
                        if (!(entity instanceof HimarsEntity)) {
                            FireControlClientState.debugMsg("Shutdown: player not in HimarsEntity");
                            FireControlClientState.beginShutdown();
                        } else {
                            HimarsEntity current = (HimarsEntity)entity;
                            if (current.m_19879_() != himars.m_19879_()) {
                                FireControlClientState.debugMsg("Shutdown: vehicle mismatch (current=" + current.m_19879_() + ", bound=" + himars.m_19879_() + ")");
                                FireControlClientState.beginShutdown();
                            } else if (current.getSeatIndex((Entity)player) != 2) {
                                FireControlClientState.debugMsg("Shutdown: not operator seat (seat=" + current.getSeatIndex((Entity)player) + ")");
                                FireControlClientState.beginShutdown();
                            }
                        }
                    }
                }
                FireControlClientState.tickPower();
                screenTexture.update();
            }
        }

        @SubscribeEvent
        public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
            ResourceLocation overlayId;
            HimarsEntity himars;
            Minecraft mc = Minecraft.m_91087_();
            LocalPlayer player = mc.f_91074_;
            if (player != null && player.m_20202_() instanceof HimarsEntity && (himars = (HimarsEntity)player.m_20202_()).getSeatIndex((Entity)player) == 2 && "superbwarfare".equals((overlayId = event.getOverlay().id()).m_135827_())) {
                event.setCanceled(true);
            }
        }

        private static void toggleTablet(Player player) {
            Entity entity = player.m_20202_();
            if (!(entity instanceof HimarsEntity)) {
                player.m_5661_((Component)Component.m_237115_((String)"message.vvp.fdc.not_in_himars"), true);
                return;
            }
            HimarsEntity himars = (HimarsEntity)entity;
            if (himars.getSeatIndex((Entity)player) != 2) {
                player.m_5661_((Component)Component.m_237115_((String)"message.vvp.fdc.not_operator"), true);
                return;
            }
            if (FireControlClientState.getBoundHimars() != null && FireControlClientState.getBoundHimars().m_19879_() != himars.m_19879_()) {
                FireControlClientState.debugMsg("Toggle: bound vehicle mismatch (resetting state)");
                FireControlClientState.forceOff();
            }
            if (FireControlClientState.isScreenLit()) {
                FireControlClientState.beginShutdown();
                VVPNetwork.VVP_HANDLER.sendToServer((Object)new HimarsFdcCancelMessage());
                return;
            }
            FireControlClientState.beginBoot(himars);
        }

        private static void designateTarget(Player player) {
            if (!FireControlClientState.isMapVisible()) {
                return;
            }
            Entity entity = player.m_20202_();
            if (!(entity instanceof HimarsEntity)) {
                return;
            }
            HimarsEntity himars = (HimarsEntity)entity;
            if (!himars.isStationaryForFire()) {
                player.m_5661_((Component)Component.m_237115_((String)"message.vvp.fdc.must_be_stationary"), true);
                return;
            }
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91073_ == null) {
                return;
            }
            int blockX = (int)Math.floor(FireControlClientState.getCrosshairWorldX());
            int blockZ = (int)Math.floor(FireControlClientState.getCrosshairWorldZ());
            int blockY = FireControlScreenTexture.resolveTargetSurfaceY(mc.f_91073_, blockX, blockZ);
            Vec3 origin = HimarsBallisticsUtil.resolveShootOrigin(himars);
            HimarsBallisticsUtil.FireSolution solution = HimarsBallisticsUtil.solve(himars, origin, (double)blockX + 0.5, blockY, (double)blockZ + 0.5);
            FireControlClientState.setTarget(blockX, blockY, blockZ, solution);
            if (!solution.inArc()) {
                player.m_5661_((Component)Component.m_237115_((String)"label.vvp.fdc.out_of_arc"), true);
                return;
            }
            VVPNetwork.VVP_HANDLER.sendToServer((Object)new HimarsFdcDesignateMessage(blockX, blockY, blockZ));
            player.m_5661_((Component)Component.m_237115_((String)"message.vvp.fdc.target_marked"), true);
        }

        private static void fireTarget(Player player) {
            if (!FireControlClientState.isMapVisible()) {
                return;
            }
            if (!(player.m_20202_() instanceof HimarsEntity)) {
                return;
            }
            if (!FireControlClientState.hasTarget()) {
                player.m_5661_((Component)Component.m_237115_((String)"label.vvp.fdc.no_target"), true);
                return;
            }
            HimarsBallisticsUtil.FireSolution solution = FireControlClientState.getSolution();
            if (solution == null || !solution.inArc()) {
                player.m_5661_((Component)Component.m_237115_((String)"label.vvp.fdc.out_of_arc"), true);
                return;
            }
            VVPNetwork.VVP_HANDLER.sendToServer((Object)new HimarsFdcFireMessage());
        }
    }
}

