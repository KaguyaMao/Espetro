/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.RenderHelper
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.tools.FormatTool
 *  com.atsuishio.superbwarfare.tools.MathTool
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 */
package frontline.combat.fcp.client.overlay;

import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.FormatTool;
import com.atsuishio.superbwarfare.tools.MathTool;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Set;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(value=Dist.CLIENT)
public class FcpDriverOverlay
implements IGuiOverlay {
    public static final FcpDriverOverlay INSTANCE = new FcpDriverOverlay();
    public static final String ID = "fcp_driver_hud";
    private static final ResourceLocation DRIVER_FRAME = new ResourceLocation("fcp", "textures/overlay/vehicle/frame/driver.png");
    private static final ResourceLocation COMPASS = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/compass.png");
    private static final ResourceLocation ROLL_IND = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/helicopter/roll_ind.png");
    private static final ResourceLocation LINE = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/line.png");
    private static final ResourceLocation BODY = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/body.png");
    private static final ResourceLocation LEFT_WHEEL = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/left_wheel.png");
    private static final ResourceLocation RIGHT_WHEEL = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/right_wheel.png");
    private static final ResourceLocation ENGINE = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/engine.png");
    private static final Set<String> DRIVER_OVERLAY_VEHICLES = Set.of("fcp:bmp1", "fcp:bmp1u", "fcp:bmp1p", "fcp:bmp1am", "fcp:bmp2", "fcp:bmp2d", "fcp:bmp2m", "fcp:bmp2_noatgm", "fcp:btr82", "fcp:lav25", "fcp:stryker_m2", "fcp:stryker_mgs", "fcp:t72av", "fcp:aavp", "fcp:stryker_mk19", "fcp:stryker_tow", "fcp:stryker_dragoon", "fcp:stryker_mortar", "fcp:btr80", "fcp:btr80_cope", "fcp:btr82_cope");

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
        if (!ve.computed().getHudType().equals("@Land")) {
            return;
        }
        int seatIndex = ve.getSeatIndex((Entity)player);
        if (seatIndex != 0) {
            return;
        }
        if (seatIndex == ve.computed().getTurretControllerIndex()) {
            return;
        }
        String vehicleId = EntityType.m_20613_((EntityType)ve.m_6095_()).toString();
        if (!DRIVER_OVERLAY_VEHICLES.contains(vehicleId)) {
            return;
        }
        int color = ve.getHudColor();
        PoseStack poseStack = guiGraphics.m_280168_();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        float targetAspect = 1.7777778f;
        float screenAspect = (float)screenWidth / (float)screenHeight;
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
            RenderHelper.preciseBlit((GuiGraphics)guiGraphics, (ResourceLocation)DRIVER_FRAME, (float)drawX, (float)0.0f, (float)10.0f, (float)0.0f, (float)0.0f, (float)drawWidth, (float)screenHeight, (float)drawWidth, (float)screenHeight);
        } else {
            float zoomedWidth = (float)screenHeight * targetAspect;
            float uOffset = (zoomedWidth - (float)screenWidth) / 2.0f;
            RenderHelper.preciseBlit((GuiGraphics)guiGraphics, (ResourceLocation)DRIVER_FRAME, (float)0.0f, (float)0.0f, (float)10.0f, (float)uOffset, (float)0.0f, (float)screenWidth, (float)screenHeight, (float)zoomedWidth, (float)screenHeight);
        }
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask((boolean)true);
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)COMPASS, (float)((float)screenWidth / 2.0f - 128.0f), (float)10.0f, (float)(128.0f + 1.4222223f * player.m_146908_()), (float)0.0f, (float)256.0f, (float)16.0f, (float)512.0f, (float)16.0f, (int)color);
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)ROLL_IND, (float)((float)screenWidth / 2.0f - 8.0f), (float)30.0f, (float)0.0f, (float)0.0f, (float)16.0f, (float)16.0f, (float)16.0f, (float)16.0f, (int)color);
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)FormatTool.format0D((double)(ve.getAbsoluteSpeed() * 72.0), (String)" KM/H")), screenWidth / 2 + 160, screenHeight / 2 - 48, color, false);
        int bodyHeal = (int)(100.0f - 100.0f * ve.getHealth() / ve.getMaxHealth());
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)FormatTool.format0D((double)(100 - bodyHeal), (String)"")), screenWidth / 2 - 165, screenHeight / 2 - 46, MathTool.getGradientColor((int)color, (int)0xFF0000, (int)bodyHeal, (int)2), false);
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)BODY, (float)((float)screenWidth / 2.0f + 96.0f), (float)((float)screenHeight - 72.0f), (float)0.0f, (float)0.0f, (float)32.0f, (float)32.0f, (float)32.0f, (float)32.0f, (int)MathTool.getGradientColor((int)color, (int)0xFF0000, (int)bodyHeal, (int)2));
        int leftWheelHeal = (int)(100.0f - 100.0f * ve.getLeftWheelHealth() / ve.getWheelMaxHealth());
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)LEFT_WHEEL, (float)((float)screenWidth / 2.0f + 96.0f), (float)((float)screenHeight - 72.0f), (float)0.0f, (float)0.0f, (float)32.0f, (float)32.0f, (float)32.0f, (float)32.0f, (int)MathTool.getGradientColor((int)color, (int)0xFF0000, (int)leftWheelHeal, (int)2));
        int rightWheelHeal = (int)(100.0f - 100.0f * ve.getRightWheelHealth() / ve.getWheelMaxHealth());
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)RIGHT_WHEEL, (float)((float)screenWidth / 2.0f + 96.0f), (float)((float)screenHeight - 72.0f), (float)0.0f, (float)0.0f, (float)32.0f, (float)32.0f, (float)32.0f, (float)32.0f, (int)MathTool.getGradientColor((int)color, (int)0xFF0000, (int)rightWheelHeal, (int)2));
        int engineHeal = (int)(100.0f - 100.0f * ve.getMainEngineHealth() / ve.getEngineMaxHealth());
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)ENGINE, (float)((float)screenWidth / 2.0f + 96.0f), (float)((float)screenHeight - 72.0f), (float)0.0f, (float)0.0f, (float)32.0f, (float)32.0f, (float)32.0f, (float)32.0f, (int)MathTool.getGradientColor((int)color, (int)0xFF0000, (int)engineHeal, (int)2));
        poseStack.m_85836_();
        poseStack.m_272245_(Axis.f_252403_.m_252977_(-Mth.m_14179_((float)partialTick, (float)ve.getTurretYRotO(), (float)ve.getTurretYRot())), (float)screenWidth / 2.0f + 112.0f, (float)screenHeight - 56.0f, 0.0f);
        int turretHeal = (int)(100.0f - 100.0f * ve.getTurretHealth() / ve.getTurretMaxHealth());
        RenderHelper.blit((PoseStack)poseStack, (ResourceLocation)LINE, (float)((float)screenWidth / 2.0f + 112.0f), (float)((float)screenHeight - 71.0f), (float)0.0f, (float)0.0f, (float)1.0f, (float)16.0f, (float)1.0f, (float)16.0f, (int)MathTool.getGradientColor((int)color, (int)0xFF0000, (int)turretHeal, (int)2));
        poseStack.m_85849_();
    }
}

