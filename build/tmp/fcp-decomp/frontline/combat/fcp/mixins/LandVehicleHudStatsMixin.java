/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.RenderHelper
 *  com.atsuishio.superbwarfare.client.overlay.VehicleMainWeaponHudOverlay
 *  com.atsuishio.superbwarfare.client.overlay.weapon.LandVehicleHud
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils
 *  com.atsuishio.superbwarfare.event.ClientEventHandler
 *  com.atsuishio.superbwarfare.tools.FormatTool
 *  com.atsuishio.superbwarfare.tools.MathTool
 *  com.atsuishio.superbwarfare.tools.RangeTool
 *  com.atsuishio.superbwarfare.tools.TraceTool
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.Camera
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package frontline.combat.fcp.mixins;

import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.client.overlay.VehicleMainWeaponHudOverlay;
import com.atsuishio.superbwarfare.client.overlay.weapon.LandVehicleHud;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.tools.FormatTool;
import com.atsuishio.superbwarfare.tools.MathTool;
import com.atsuishio.superbwarfare.tools.RangeTool;
import com.atsuishio.superbwarfare.tools.TraceTool;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frontline.combat.fcp.entity.vehicle.IndirectFireVehicleBase;
import frontline.combat.fcp.firecontrol.FireControlComputation;
import frontline.combat.fcp.firecontrol.FireControlSolution;
import frontline.combat.fcp.firecontrol.FireControlStatus;
import java.util.Set;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LandVehicleHud.class}, remap=false)
public class LandVehicleHudStatsMixin {
    private static final Set<String> FCP_GUNNER_FRAME_VEHICLES = Set.of("fcp:bmp1", "fcp:bmp1p", "fcp:bmp1am", "fcp:bmp2", "fcp:bmp2d", "fcp:bmp2m", "fcp:bmp2_noatgm", "fcp:btr82", "fcp:lav25", "fcp:stryker_mgs", "fcp:t72av", "fcp:aavp", "fcp:matv_tow", "fcp:stryker_mortar", "fcp:ural_grad", "fcp:toyota_hilux_bmp", "fcp:toyota_hilux_spg9", "fcp:uaz_spg9", "fcp:toyota_hilux_mortar", "fcp:btr80", "fcp:btr80_cope", "fcp:btr82_cope");
    private static final Set<String> FCP_NO_FRAME_VEHICLES = Set.of("fcp:toyota_hilux_zu23");
    private static final Set<String> FCP_MORTAR_HUD_VEHICLES = Set.of("fcp:ural_grad", "fcp:stryker_mortar", "fcp:toyota_hilux_rocket_pod", "fcp:toyota_hilux_mortar");
    private static final ResourceLocation GUNNER_FRAME = new ResourceLocation("fcp", "textures/overlay/vehicle/frame/gunner.png");
    private static final ResourceLocation COMPASS = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/compass.png");
    private static final ResourceLocation LINE = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/line.png");
    private static final ResourceLocation ROLL_IND = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/helicopter/roll_ind.png");
    private static final ResourceLocation BODY = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/body.png");

    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void replaceLandHudForFcp(VehicleEntity vehicle, Player player, ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight, CallbackInfo ci) {
        String vehicleId = EntityType.m_20613_((EntityType)vehicle.m_6095_()).toString();
        boolean hasGunnerFrame = FCP_GUNNER_FRAME_VEHICLES.contains(vehicleId);
        boolean noFrame = FCP_NO_FRAME_VEHICLES.contains(vehicleId);
        boolean hasMortarHud = FCP_MORTAR_HUD_VEHICLES.contains(vehicleId);
        if (!(hasGunnerFrame || noFrame || hasMortarHud)) {
            return;
        }
        ci.cancel();
        if (vehicle.getSeatIndex((Entity)player) != vehicle.computed().getTurretControllerIndex()) {
            return;
        }
        Minecraft mc = gui.getMinecraft();
        int color = vehicle.getHudColor();
        PoseStack poseStack = guiGraphics.m_280168_();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::m_172817_);
        RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        if (mc.f_91066_.m_92176_() == CameraType.FIRST_PERSON || ClientEventHandler.zoomVehicle) {
            if (hasGunnerFrame) {
                float screenAspect = (float)screenWidth / (float)screenHeight;
                float targetAspect = 1.7777778f;
                if (screenAspect > targetAspect) {
                    float drawWidth = (float)screenHeight * targetAspect;
                    float drawX = ((float)screenWidth - drawWidth) / 2.0f;
                    int leftEnd = (int)Math.floor(drawX) + 1;
                    int rightStart = (int)Math.ceil(drawX + drawWidth) - 1;
                    guiGraphics.m_280509_(0, 0, leftEnd, screenHeight, -16777216);
                    guiGraphics.m_280509_(rightStart, 0, screenWidth, screenHeight, -16777216);
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
                    RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                    RenderHelper.preciseBlit((GuiGraphics)guiGraphics, (ResourceLocation)GUNNER_FRAME, (float)drawX, (float)0.0f, (float)10.0f, (float)0.0f, (float)0.0f, (float)drawWidth, (float)screenHeight, (float)drawWidth, (float)screenHeight);
                } else {
                    float zoomedWidth = (float)screenHeight * targetAspect;
                    float uOffset = (zoomedWidth - (float)screenWidth) / 2.0f;
                    RenderHelper.preciseBlit((GuiGraphics)guiGraphics, (ResourceLocation)GUNNER_FRAME, (float)0.0f, (float)0.0f, (float)10.0f, (float)uOffset, (float)0.0f, (float)screenWidth, (float)screenHeight, (float)zoomedWidth, (float)screenHeight);
                }
            }
            RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)LINE, (float)((float)screenWidth / 2.0f - 64.0f), (float)((float)screenHeight - 56.0f), (float)0.0f, (float)0.0f, (float)128.0f, (float)1.0f, (float)128.0f, (float)1.0f, (int)color);
            RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)COMPASS, (float)((float)screenWidth / 2.0f - 128.0f), (float)10.0f, (float)(128.0f + 1.4222223f * player.m_146908_()), (float)0.0f, (float)256.0f, (float)16.0f, (float)512.0f, (float)16.0f, (int)color);
            RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)ROLL_IND, (float)((float)screenWidth / 2.0f - 8.0f), (float)30.0f, (float)0.0f, (float)0.0f, (float)16.0f, (float)16.0f, (float)16.0f, (float)16.0f, (int)color);
            int turretHeal = (int)(100.0f - 100.0f * vehicle.getTurretHealth() / vehicle.getTurretMaxHealth());
            RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)LINE, (float)((float)screenWidth / 2.0f + 112.0f), (float)((float)screenHeight - 71.0f), (float)0.0f, (float)0.0f, (float)1.0f, (float)16.0f, (float)1.0f, (float)16.0f, (int)MathTool.getGradientColor((int)color, (int)0xFF0000, (int)turretHeal, (int)2));
            poseStack.m_85836_();
            poseStack.m_272245_(Axis.f_252403_.m_252977_(Mth.m_14179_((float)partialTick, (float)vehicle.getTurretYRotO(), (float)vehicle.getTurretYRot())), (float)screenWidth / 2.0f + 112.0f, (float)screenHeight - 56.0f, 0.0f);
            int bodyHeal = (int)(100.0f - 100.0f * vehicle.getHealth() / vehicle.getMaxHealth());
            RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)BODY, (float)((float)screenWidth / 2.0f + 96.0f), (float)((float)screenHeight - 72.0f), (float)0.0f, (float)0.0f, (float)32.0f, (float)32.0f, (float)32.0f, (float)32.0f, (int)MathTool.getGradientColor((int)color, (int)0xFF0000, (int)bodyHeal, (int)2));
            poseStack.m_85849_();
            if (hasMortarHud) {
                this.renderMortarInfo(vehicle, player, guiGraphics, mc, partialTick, screenWidth, screenHeight, color);
            }
            Camera camera = mc.f_91063_.m_109153_();
            Vec3 cameraPos = camera.m_90583_();
            Vec3 viewVec = new Vec3(camera.m_253058_());
            BlockHitResult result = player.m_9236_().m_45547_(new ClipContext(player.m_146892_(), player.m_146892_().m_82549_(player.m_20252_(1.0f).m_82490_(512.0)), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, (Entity)player));
            double blockRange = player.m_20299_(1.0f).m_82554_(result.m_82450_());
            Entity lookingEntity = TraceTool.cameraFindLookingEntity((Player)player, (Vec3)cameraPos, (Vec3)viewVec, (double)512.0);
            String rangeStr = lookingEntity != null ? FormatTool.format0D((double)player.m_20270_(lookingEntity), (String)" m") : (blockRange > 500.0 ? "---m" : FormatTool.format0D((double)blockRange, (String)" m"));
            int rangeWidth = mc.f_91062_.m_92895_(rangeStr);
            guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)rangeStr), screenWidth / 2 - rangeWidth / 2, screenHeight - 53, color, false);
            GunData gunData = vehicle.getGunData((Entity)player);
            if (gunData != null) {
                VehicleMainWeaponHudOverlay.renderWeaponInfoFirst((GuiGraphics)guiGraphics, (VehicleEntity)vehicle, (Player)player, (GunData)gunData, (Font)mc.f_91062_, (int)screenWidth, (int)screenHeight, (int)color);
            }
            VehicleMainWeaponHudOverlay.renderEnergyInfo((VehicleEntity)vehicle, (GuiGraphics)guiGraphics, (int)screenWidth, (int)screenHeight, (Font)mc.f_91062_);
        }
    }

    private void renderMortarInfo(VehicleEntity vehicle, Player player, GuiGraphics guiGraphics, Minecraft mc, float partialTick, int screenWidth, int screenHeight, int color) {
        IndirectFireVehicleBase indirect;
        if (vehicle instanceof IndirectFireVehicleBase && (indirect = (IndirectFireVehicleBase)vehicle).isFireControlActive()) {
            this.renderActiveFireControl(indirect, guiGraphics, mc, screenWidth, screenHeight, color);
            return;
        }
        double pitch = -vehicle.getTurretXRot();
        Vec3 shootVec = vehicle.getShootVec((Entity)player, partialTick);
        double displayYaw = -VehicleVecUtils.getYRotFromVector((Vec3)shootVec);
        double velocity = vehicle.getProjectileVelocity((Entity)player);
        double gravity = vehicle.getProjectileGravity((Entity)player);
        double range = gravity > 0.0 && velocity > 0.0 && pitch > 0.0 ? RangeTool.getRange((double)pitch, (double)velocity, (double)gravity) : 0.0;
        String rangeStr = range > 0.0 ? FormatTool.format0D((double)Math.max(0, (int)range), (String)"m") : "---m";
        int baseX = screenWidth / 2 - 90;
        int baseY = screenHeight / 2 - 26;
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.superbwarfare.mortar.pitch").m_7220_((Component)Component.m_237113_((String)FormatTool.format1D((double)pitch, (String)"\u00b0"))), baseX, baseY, color, false);
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.superbwarfare.mortar.yaw").m_7220_((Component)Component.m_237113_((String)FormatTool.format1D((double)displayYaw, (String)"\u00b0"))), baseX, baseY + 10, color, false);
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.superbwarfare.mortar.range").m_7220_((Component)Component.m_237113_((String)rangeStr)), baseX, baseY + 20, color, false);
    }

    private void renderActiveFireControl(IndirectFireVehicleBase vehicle, GuiGraphics guiGraphics, Minecraft mc, int screenWidth, int screenHeight, int color) {
        int baseX = screenWidth / 2 - 90;
        int baseY = screenHeight / 2 - 26;
        BlockPos target = vehicle.getFireControlTarget();
        FireControlComputation computation = vehicle.getFireControlComputation();
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.fcp.firing_solution.target").m_7220_((Component)Component.m_237113_((String)(target.m_123341_() + " / " + target.m_123342_() + " / " + target.m_123343_()))), baseX, baseY, color, false);
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.fcp.firing_solution.dispersion").m_7220_((Component)Component.m_237113_((String)(vehicle.getFireControlRadius() + "m"))), baseX, baseY + 10, color, false);
        if (computation.isSuccess()) {
            FireControlSolution solution = computation.solution();
            guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.superbwarfare.mortar.range").m_7220_((Component)Component.m_237113_((String)FormatTool.format0D((double)solution.range(), (String)"m"))), baseX, baseY + 20, color, false);
            guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.fcp.firing_solution.angles").m_7220_((Component)Component.m_237113_((String)(FormatTool.format1D((double)solution.pitch(), (String)"\u00b0") + " / " + FormatTool.format1D((double)solution.yaw(), (String)"\u00b0")))), baseX, baseY + 30, color, false);
        }
        FireControlStatus status = vehicle.getFireControlStatus();
        int statusColor = switch (status) {
            case FireControlStatus.READY -> -9972847;
            case FireControlStatus.MOVING, FireControlStatus.ALIGNING -> -14249;
            case FireControlStatus.INACTIVE -> -6773334;
            default -> -39330;
        };
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.fcp.firing_solution.status").m_7220_((Component)Component.m_237115_((String)status.translationKey())), baseX, baseY + 42, statusColor, false);
    }
}

