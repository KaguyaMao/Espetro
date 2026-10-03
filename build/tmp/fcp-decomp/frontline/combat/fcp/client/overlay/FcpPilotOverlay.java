/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.RenderHelper
 *  com.atsuishio.superbwarfare.client.overlay.VehicleHudOverlay
 *  com.atsuishio.superbwarfare.client.overlay.VehicleMainWeaponHudOverlay
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils
 *  com.atsuishio.superbwarfare.init.ModKeyMappings
 *  com.atsuishio.superbwarfare.tools.FormatTool
 *  com.atsuishio.superbwarfare.tools.MathTool
 *  com.atsuishio.superbwarfare.tools.VectorToolKt
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 */
package frontline.combat.fcp.client.overlay;

import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.client.overlay.VehicleHudOverlay;
import com.atsuishio.superbwarfare.client.overlay.VehicleMainWeaponHudOverlay;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils;
import com.atsuishio.superbwarfare.init.ModKeyMappings;
import com.atsuishio.superbwarfare.tools.FormatTool;
import com.atsuishio.superbwarfare.tools.MathTool;
import com.atsuishio.superbwarfare.tools.VectorToolKt;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Map;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(value=Dist.CLIENT)
public class FcpPilotOverlay
implements IGuiOverlay {
    public static final FcpPilotOverlay INSTANCE = new FcpPilotOverlay();
    public static final String ID = "fcp_pilot_hud";
    private static final ResourceLocation HELI_BASE = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/helicopter/heli_base.png");
    private static final ResourceLocation HELI_DRIVER_ANGLE = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/helicopter/heli_driver_angle.png");
    private static final ResourceLocation ROLL_IND = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/helicopter/roll_ind.png");
    private static final ResourceLocation HELI_POWER_RULER = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/helicopter/heli_power_ruler.png");
    private static final ResourceLocation HELI_POWER = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/helicopter/heli_power.png");
    private static final ResourceLocation HELI_VY_MOVE = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/helicopter/heli_vy_move.png");
    private static final ResourceLocation SPEED_FRAME = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/helicopter/speed_frame.png");
    private static final ResourceLocation CROSSHAIR_IND = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/helicopter/crosshair_ind.png");
    private static final ResourceLocation COMPASS = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/compass.png");
    private static final ResourceLocation HUD_LINE = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/aircraft/hud_line.png");
    private static final Map<String, List<Integer>> PILOT_OVERLAY_VEHICLES = Map.ofEntries(Map.entry("littlebird", List.of(Integer.valueOf(0), Integer.valueOf(1))), Map.entry("littlebird_armed", List.of(Integer.valueOf(1))), Map.entry("venom", List.of(Integer.valueOf(0), Integer.valueOf(1))), Map.entry("huey", List.of(Integer.valueOf(0), Integer.valueOf(1))), Map.entry("huey_rockets", List.of(Integer.valueOf(1))), Map.entry("huey_door_gunner_m60", List.of(Integer.valueOf(0), Integer.valueOf(2))), Map.entry("huey_door_gunner_m134", List.of(Integer.valueOf(2))), Map.entry("mi17", List.of(Integer.valueOf(0), Integer.valueOf(1))));
    private float scopeScale = 1.0f;
    private float lerpVy = 1.0f;
    private float lerpPower = 0.0f;

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null || player.m_5833_() || mc.f_91066_.f_92062_) {
            return;
        }
        if (mc.f_91066_.m_92176_() != CameraType.FIRST_PERSON) {
            return;
        }
        Entity vehicle = player.m_20202_();
        if (!(vehicle instanceof VehicleEntity)) {
            return;
        }
        VehicleEntity ve = (VehicleEntity)vehicle;
        if (!ve.computed().getHudType().equals("@Helicopter")) {
            return;
        }
        List<Integer> hudSeats = PILOT_OVERLAY_VEHICLES.get(EntityType.m_20613_((EntityType)ve.m_6095_()).m_135815_());
        if (hudSeats == null) {
            return;
        }
        int seatIndex = ve.getSeatIndex((Entity)player);
        if (!hudSeats.contains(seatIndex)) {
            return;
        }
        int color = ve.getHudColor();
        PoseStack poseStack = guiGraphics.m_280168_();
        GunData data = ve.getGunData(seatIndex);
        poseStack.m_85836_();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::m_172817_);
        RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.scopeScale = Mth.m_14179_((float)partialTick, (float)this.scopeScale, (float)1.0f);
        float f = Math.min(screenWidth, screenHeight);
        float f1 = Math.min((float)screenWidth / f, (float)screenHeight / f) * this.scopeScale;
        float i = Mth.m_14143_((float)(f * f1));
        float j = Mth.m_14143_((float)(f * f1));
        float k = ((float)screenWidth - i) / 2.0f;
        float l = ((float)screenHeight - j) / 2.0f;
        Vec3 shootPos = ve.getShootPosForHud((Entity)player, partialTick);
        Vec3 shootDir = ve.getShootDirectionForHud((Entity)player, partialTick);
        double dis = this.computeAimDistance(ve, (Player)player, shootPos, shootDir, partialTick);
        Vec3 pos = shootPos.m_82549_(shootDir.m_82490_(dis));
        Vec3 screenPos = VectorToolKt.worldToScreen((Vec3)pos);
        float x = (float)screenPos.f_82479_;
        float y = (float)screenPos.f_82480_;
        double speed = ve.m_20184_().m_82553_() * 72.0;
        this.lerpVy = Mth.m_14179_((float)(0.021f * partialTick), (float)this.lerpVy, (float)((float)(ve.m_20184_().m_7098_() * 20.0)));
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)HELI_BASE, (float)k, (float)l, (float)0.0f, (float)0.0f, (float)i, (float)j, (float)i, (float)j, (int)color);
        float diffY = -Mth.m_14179_((float)partialTick, (float)ve.getTurretYRotO(), (float)ve.getTurretYRot()) * 0.3f;
        float diffX = (float)(Mth.m_14175_((double)(-VehicleVecUtils.getXRotFromVector((Vec3)ve.getBarrelVector(partialTick)) - (double)Mth.m_14179_((float)partialTick, (float)ve.f_19860_, (float)ve.m_146909_()))) * (double)0.072f);
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)HELI_DRIVER_ANGLE, (float)(k + diffY), (float)(l + diffX), (float)0.0f, (float)0.0f, (float)i, (float)j, (float)i, (float)j, (int)color);
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)COMPASS, (float)((float)screenWidth / 2.0f - 128.0f), (float)6.0f, (float)(128.0f + 1.4222223f * ve.m_146908_()), (float)0.0f, (float)256.0f, (float)16.0f, (float)512.0f, (float)16.0f, (int)color);
        poseStack.m_85836_();
        poseStack.m_272245_(Axis.f_252403_.m_252977_(-ve.getRoll(partialTick)), (float)screenWidth / 2.0f, (float)screenHeight / 2.0f, 0.0f);
        float pitch = ve.getPitch(partialTick);
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)HUD_LINE, (float)((float)screenWidth / 2.0f - 144.0f), (float)((float)screenHeight / 2.0f - 128.0f), (float)0.0f, (float)(722.5f + 4.725f * pitch), (float)288.0f, (float)256.0f, (float)288.0f, (float)1701.0f, (int)color);
        poseStack.m_85849_();
        poseStack.m_85836_();
        poseStack.m_272245_(Axis.f_252403_.m_252977_(ve.getRoll(partialTick)), (float)screenWidth / 2.0f, (float)screenHeight / 2.0f - 56.0f, 0.0f);
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)ROLL_IND, (float)((float)screenWidth / 2.0f - 8.0f), (float)((float)screenHeight / 2.0f - 88.0f), (float)0.0f, (float)0.0f, (float)16.0f, (float)16.0f, (float)16.0f, (float)16.0f, (int)color);
        poseStack.m_85849_();
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)HELI_POWER_RULER, (float)((float)screenWidth / 2.0f + 100.0f), (float)((float)screenHeight / 2.0f - 64.0f), (float)0.0f, (float)0.0f, (float)64.0f, (float)128.0f, (float)64.0f, (float)128.0f, (int)color);
        float power = ve.getPower();
        this.lerpPower = Mth.m_14179_((float)(0.5f * partialTick), (float)this.lerpPower, (float)power);
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)HELI_POWER, (float)((float)screenWidth / 2.0f + 130.0f), (float)((float)screenHeight / 2.0f - 64.0f + 124.0f - this.lerpPower * 980.0f), (float)0.0f, (float)0.0f, (float)4.0f, (float)(this.lerpPower * 980.0f), (float)4.0f, (float)(this.lerpPower * 980.0f), (int)color);
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)HELI_VY_MOVE, (float)((float)screenWidth / 2.0f + 138.0f), (float)((float)screenHeight / 2.0f - 3.0f - Mth.m_14036_((float)(this.lerpVy * 3.0f), (float)-24.0f, (float)24.0f) * 2.5f), (float)0.0f, (float)0.0f, (float)8.0f, (float)8.0f, (float)8.0f, (float)8.0f, (int)color);
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)FormatTool.format0D((double)this.lerpVy, (String)"m/s")), screenWidth / 2 + 146, (int)((double)((float)screenHeight / 2.0f - 3.0f) - (double)Mth.m_14036_((float)(this.lerpVy * 3.0f), (float)-24.0f, (float)24.0f) * 2.5), this.lerpVy < -12.0f ? -65536 : color, false);
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)FormatTool.format0D((double)ve.m_20186_())), screenWidth / 2 + 104, screenHeight / 2, color, false);
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)SPEED_FRAME, (float)((float)screenWidth / 2.0f - 144.0f), (float)((float)screenHeight / 2.0f - 6.0f), (float)0.0f, (float)0.0f, (float)50.0f, (float)18.0f, (float)50.0f, (float)18.0f, (int)color);
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)FormatTool.format0D((double)speed, (String)"km/h")), screenWidth / 2 - 140, screenHeight / 2, color, false);
        if (ve.hasDecoy()) {
            if (ve.getDecoyReady()) {
                guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.superbwarfare.flare.ready").m_7220_((Component)Component.m_237113_((String)(" [" + ModKeyMappings.RELEASE_DECOY.getKey().m_84875_().getString() + "]"))), screenWidth / 2 - 160, screenHeight / 2 - 50, color, false);
            } else {
                guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.superbwarfare.flare.reloading"), screenWidth / 2 - 160, screenHeight / 2 - 50, 0xFF0000, false);
            }
        }
        if (data != null) {
            Component ammo = ve.firstPersonAmmoComponent(data, (Player)player);
            int heat = ve.getWeaponHeat((LivingEntity)player);
            guiGraphics.m_280614_(mc.f_91062_, ammo, screenWidth / 2 - 160, screenHeight / 2 - 59, MathTool.getGradientColor((int)color, (int)0xFF0000, (int)heat, (int)2), false);
        }
        VehicleMainWeaponHudOverlay.renderEnergyInfo((VehicleEntity)ve, (GuiGraphics)guiGraphics, (int)screenWidth, (int)screenHeight, (Font)mc.f_91062_);
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)CROSSHAIR_IND, (float)(x - 8.0f), (float)(y - 8.0f), (float)0.0f, (float)0.0f, (float)16.0f, (float)16.0f, (float)16.0f, (float)16.0f, (int)color);
        VehicleHudOverlay.renderKillIndicatorDynamic((GuiGraphics)guiGraphics, (float)(x - 7.5f + (float)(2.0 * (Math.random() - 0.5))), (float)(y - 7.5f + (float)(2.0 * (Math.random() - 0.5))));
        poseStack.m_85849_();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableDepthTest();
    }

    private double computeAimDistance(VehicleEntity ve, Player player, Vec3 shootPos, Vec3 shootDir, float partialTick) {
        double dis = 512.0;
        BlockHitResult result = player.m_9236_().m_45547_(new ClipContext(shootPos, shootPos.m_82549_(shootDir.m_82490_(512.0)), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, (Entity)player));
        dis = shootPos.m_82554_(result.m_82450_());
        Entity looking = ve.getPlayerLookAtEntityOnVehicle((Entity)player, 512.0, partialTick);
        if (looking != null) {
            dis = shootPos.m_82554_(looking.m_20182_());
        }
        return dis;
    }
}

