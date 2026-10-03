/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils
 *  com.atsuishio.superbwarfare.item.misc.FiringParametersItem
 *  com.atsuishio.superbwarfare.tools.FormatTool
 *  com.atsuishio.superbwarfare.tools.RangeTool
 *  com.atsuishio.superbwarfare.tools.TraceTool
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.commands.arguments.EntityAnchorArgument$Anchor
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 */
package tech.vvp.vvp.client.overlay;

import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils;
import com.atsuishio.superbwarfare.item.misc.FiringParametersItem;
import com.atsuishio.superbwarfare.tools.FormatTool;
import com.atsuishio.superbwarfare.tools.RangeTool;
import com.atsuishio.superbwarfare.tools.TraceTool;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tech.vvp.vvp.entity.vehicle.D30Entity;

@OnlyIn(value=Dist.CLIENT)
public class D30InfoOverlay
implements IGuiOverlay {
    public static final String ID = "vvp_d30_info";
    private static final float PROJECTILE_VELOCITY = 15.0f;
    private static final float GRAVITY = 0.05f;

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        LocalPlayer player = mc.f_91074_;
        if (player == null) {
            return;
        }
        Entity lookingEntity = TraceTool.findLookingEntity((Entity)player, (double)30.0);
        if (!(lookingEntity instanceof D30Entity)) {
            return;
        }
        D30Entity d30 = (D30Entity)lookingEntity;
        float pitch = ((Float)d30.m_20088_().m_135370_(D30Entity.TARGET_PITCH)).floatValue();
        float yaw = ((Float)d30.m_20088_().m_135370_(D30Entity.TARGET_YAW)).floatValue();
        boolean loaded = (Boolean)d30.m_20088_().m_135370_(D30Entity.LOADED);
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.superbwarfare.mortar.pitch").m_7220_((Component)Component.m_237113_((String)FormatTool.format2D((double)(-pitch), (String)"\u00b0"))), screenWidth / 2 - 130, screenHeight / 2 - 26, -1, false);
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.superbwarfare.mortar.yaw").m_7220_((Component)Component.m_237113_((String)FormatTool.format2D((double)yaw, (String)"\u00b0"))), screenWidth / 2 - 130, screenHeight / 2 - 16, -1, false);
        double range = RangeTool.getRange((double)(-pitch), (double)15.0, (double)0.05f);
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.superbwarfare.mortar.range").m_7220_((Component)Component.m_237113_((String)FormatTool.format1D((double)Math.max((int)range, 0), (String)"m"))), screenWidth / 2 - 130, screenHeight / 2 - 6, -1, false);
        int color = loaded ? -11141291 : -43691;
        String status = loaded ? "[LOADED]" : "[EMPTY]";
        guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)status), screenWidth / 2 - 130, screenHeight / 2 + 6, color, false);
        ItemStack stack = player.m_21206_();
        if (player.m_21205_().m_41720_() instanceof FiringParametersItem) {
            stack = player.m_21205_();
        }
        if (stack.m_41720_() instanceof FiringParametersItem) {
            double targetX = stack.m_41784_().m_128459_("TargetX");
            double targetY = stack.m_41784_().m_128459_("TargetY") - 1.0;
            double targetZ = stack.m_41784_().m_128459_("TargetZ");
            boolean isDepressed = stack.m_41784_().m_128471_("IsDepressed");
            Vec3 targetPos = new Vec3(targetX, targetY, targetZ);
            double dx = targetPos.f_82479_ - d30.getShootPos().f_82479_;
            double dy = targetPos.f_82480_ - d30.getShootPos().f_82480_;
            double dz = targetPos.f_82481_ - d30.getShootPos().f_82481_;
            double horizontalDist = Math.sqrt(dx * dx + dz * dz);
            double v = 15.0;
            double g = 0.05f;
            double discriminant = v * v * v * v - g * (g * horizontalDist * horizontalDist + 2.0 * dy * v * v);
            Vec3 launchVector = null;
            if (discriminant >= 0.0) {
                double angle1 = Math.atan((v * v + Math.sqrt(discriminant)) / (g * horizontalDist));
                double angle2 = Math.atan((v * v - Math.sqrt(discriminant)) / (g * horizontalDist));
                double angle = isDepressed ? Math.min(angle1, angle2) : Math.max(angle1, angle2);
                double horizontalVel = v * Math.cos(angle);
                double verticalVel = v * Math.sin(angle);
                launchVector = new Vec3(dx / horizontalDist * horizontalVel, verticalVel, dz / horizontalDist * horizontalVel);
            }
            Vec3 vec3 = EntityAnchorArgument.Anchor.EYES.m_90377_((Entity)d30);
            double d0 = (targetPos.f_82479_ - vec3.f_82479_) * 0.2;
            double d2 = (targetPos.f_82481_ - vec3.f_82481_) * 0.2;
            double targetYaw = Mth.m_14177_((float)((float)(Mth.m_14136_((double)d2, (double)d0) * 57.2957763671875) - 90.0f));
            if (launchVector == null) {
                guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.superbwarfare.mortar.out_of_range").m_130940_(ChatFormatting.RED), screenWidth / 2 + 90, screenHeight / 2 - 26, -1, false);
                return;
            }
            float angle = (float)VehicleVecUtils.getXRotFromVector(launchVector);
            guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.superbwarfare.target.pitch").m_7220_((Component)Component.m_237113_((String)FormatTool.format2D((double)angle, (String)"\u00b0"))), screenWidth / 2 + 90, screenHeight / 2 - 26, -1, false);
            guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.superbwarfare.target.yaw").m_7220_((Component)Component.m_237113_((String)FormatTool.format2D((double)targetYaw, (String)"\u00b0"))), screenWidth / 2 + 90, screenHeight / 2 - 16, -1, false);
            guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237115_((String)"tips.superbwarfare.mortar.target_pos").m_7220_((Component)Component.m_237113_((String)(FormatTool.format0D((double)targetX) + " " + FormatTool.format0D((double)targetY) + " " + FormatTool.format0D((double)targetZ)))), screenWidth / 2 + 90, screenHeight / 2 - 6, -1, false);
            if (angle < -70.0f || angle > 7.0f) {
                guiGraphics.m_280614_(mc.f_91062_, (Component)Component.m_237110_((String)"tips.superbwarfare.mortar.warn", (Object[])new Object[]{d30.m_5446_()}).m_130940_(ChatFormatting.RED), screenWidth / 2 + 90, screenHeight / 2 + 4, -1, false);
            }
        }
    }
}

