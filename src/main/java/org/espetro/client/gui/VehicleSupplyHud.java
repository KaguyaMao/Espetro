package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.espetro.network.VehicleSupplySyncPacket;

/**
 * 载具 HUD 渲染器：
 * <ul>
 *   <li>容量条：轮盘打开时渲染在屏幕顶部</li>
 *   <li>准心提示：准心对准5格内己方载具时显示"按住 F 来交互"</li>
 * </ul>
 */
public final class VehicleSupplyHud {

    private static final int BAR_WIDTH = 200;
    private static final int BAR_HEIGHT = 5;
    private static final int PANEL_WIDTH = 220;
    private static final int PANEL_HEIGHT = 34;
    private static final int TOP_MARGIN = 4;
    private static final int ICON_SIZE = 10;
    private static final int ICON_TEXTURE_SIZE = 128;
    /** 卡片内边距：左右各 10px，标题/进度条/数值行按 4px 网格排。 */
    private static final int PAD_X = 10;
    private static final int LABEL_Y = 4;
    private static final int BAR_Y = 14;
    private static final int ICON_ROW_Y = 22;

    private static final double HINT_RANGE = 5.0;
    private static final int HINT_Y_OFFSET = 20;
    private static final int HINT_PILL_HEIGHT = 13;

    private static boolean registered;

    public static void register() {
        if (registered) return;
        registered = true;
        MinecraftForge.EVENT_BUS.register(VehicleSupplyHud.class);
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;

        GuiGraphics graphics = event.getGuiGraphics();

        float interactionProgress =
            org.espetro.client.vehicle.VehicleInteractionState.progress();

        if (VehicleWheelController.isWheelActive()) {
            VehicleSupplySyncPacket supply = VehicleWheelController.getCachedSupply();
            if (supply != null) drawCapacityBar(graphics, mc, supply);
            // EsRadial owns the ring and transfer progress. Keep only the center icon/text here.
            if (interactionProgress >= 0f) {
                drawInteractionCenterProgress(graphics, mc, interactionProgress);
            } else if (!VehicleWheelController.isHolding() && VehicleWheelController.isCenterHovered()) {
                drawCenterMountHint(graphics, mc);
            }
            return;
        }

        if (interactionProgress >= 0f) {
            drawInteractionCenterProgress(graphics, mc, interactionProgress);
            return;
        }

        // 已经坐在载具/乘客位里时不再提示“按 X 来互动”。
        if (mc.player.getVehicle() != null) return;

        drawCrosshairHint(graphics, mc);
    }

    // ==================== 容量条（屏幕顶部居中面板） ====================

    private static void drawCapacityBar(GuiGraphics graphics, Minecraft mc, VehicleSupplySyncPacket supply) {
        int max = supply.getMaxCapacity();
        if (max <= 0) return;
        int ammo = supply.getAmmo();
        int constr = supply.canCarryConstruction() ? supply.getConstruction() : 0;

        int screenW = mc.getWindow().getGuiScaledWidth();
        int px = (screenW - PANEL_WIDTH) / 2;
        int py = TOP_MARGIN;

        // 顶部居中的无框玻璃卡。
        UiPaint.card(graphics, px, py, PANEL_WIDTH, PANEL_HEIGHT);

        // 标题微标签 + 右侧容量读数。
        UiPaint.textScaled(graphics, "载具补给", px + PAD_X, py + LABEL_Y,
            UiTheme.TEXT_THIRD, UiTheme.TYPE_MICRO, false);
        String capacityText = (ammo + constr) + " / " + max;
        int capacityWidth = Math.round(mc.font.width(capacityText) * UiTheme.TYPE_MICRO);
        UiPaint.textScaled(graphics, capacityText,
            px + PANEL_WIDTH - PAD_X - capacityWidth, py + LABEL_Y,
            UiTheme.TEXT_SECOND, UiTheme.TYPE_MICRO, false);

        // 容量条：内凹槽 + 渐变填充 + 高光走 UiPaint.progress；建材段再以黄色叠在弹药段右侧。
        int barX = px + PAD_X;
        int barY = py + BAR_Y;
        int barW = BAR_WIDTH;
        int ammoW = (int) ((long) ammo * barW / max);
        int constrW = (int) ((long) constr * barW / max);
        int filled = Math.min(barW, ammoW + constrW);
        UiPaint.progress(graphics, barX, barY, barW, BAR_HEIGHT,
            filled / (float) barW, UiTheme.ATTACK);
        if (constrW > 0) {
            int start = Math.min(barX + ammoW, barX + barW);
            int width = Math.min(constrW, barX + barW - start);
            if (width > 0) {
                // 建材段沿用弹药填充的内缩与厚度（槽内 2px 边距、高度 - 4），
                // 不再整块盖住凹槽，避免建材条看起来比弹药条粗。
                int innerY = barY + 2;
                int innerH = Math.max(1, BAR_HEIGHT - 4);
                int x1 = Math.max(barX + 2, start);
                int x2 = Math.min(barX + barW - 2, start + width);
                if (x2 > x1) {
                    graphics.fill(x1, innerY, x2, innerY + innerH, UiTheme.WARNING);
                }
            }
        }

        // 图标 + 数值行（容量条下方，数值用标题字号 + 投影）。
        int iconRowY = py + ICON_ROW_Y;
        int textY = iconRowY;

        if (supply.canCarryConstruction()) {
            // 补给载具：弹药左，建材右
            int ammoIconX = px + 35;
            SupplyIcons.ammunition(graphics, ammoIconX, iconRowY, ICON_SIZE);
            UiPaint.textScaled(graphics, String.valueOf(ammo),
                ammoIconX + ICON_SIZE + 4, textY, UiTheme.TEXT, UiTheme.TYPE_HEADING, true);

            String constrText = String.valueOf(constr);
            int constrTextW = Math.round(mc.font.width(constrText) * UiTheme.TYPE_HEADING);
            int constrIconX = px + PANEL_WIDTH - 35 - ICON_SIZE - 4 - constrTextW;
            SupplyIcons.construction(graphics, constrIconX, iconRowY, ICON_SIZE);
            UiPaint.textScaled(graphics, constrText,
                constrIconX + ICON_SIZE + 4, textY, UiTheme.TEXT, UiTheme.TYPE_HEADING, true);
        } else {
            // 战斗载具：弹药居中
            String ammoText = String.valueOf(ammo);
            int ammoTextW = Math.round(mc.font.width(ammoText) * UiTheme.TYPE_HEADING);
            int totalW = ICON_SIZE + 4 + ammoTextW;
            int startX = px + (PANEL_WIDTH - totalW) / 2;
            SupplyIcons.ammunition(graphics, startX, iconRowY, ICON_SIZE);
            UiPaint.textScaled(graphics, ammoText,
                startX + ICON_SIZE + 4, textY, UiTheme.TEXT, UiTheme.TYPE_HEADING, true);
        }
    }

    // ==================== 准心提示 ====================

    private static void drawCrosshairHint(GuiGraphics graphics, Minecraft mc) {
        Entity target = findLookedAtVehicle(mc);
        if (target == null) return;
        Entity root = target.getRootVehicle();
        if (root == null) return;
        if (!org.espetro.client.vehicle.ClientFriendlyVehicles.contains(root.getUUID())) return;
        int cx = mc.getWindow().getGuiScaledWidth() / 2;
        int cy = mc.getWindow().getGuiScaledHeight() / 2;
        String hint = "按 " + VehicleWheelController.interactKeyName() + " 来互动";
        int textWidth = mc.font.width(hint);
        int hintY = cy + HINT_Y_OFFSET;
        // 无框玻璃提示保证在亮色天空与雪地上仍可读。
        UiPaint.glassPanel(graphics, cx - textWidth / 2 - 6, hintY - 3,
            textWidth + 12, HINT_PILL_HEIGHT, UiTheme.GLASS_PANEL);
        UiPaint.textShadowed(graphics, hint, cx - textWidth / 2, hintY, UiTheme.GOLD);
    }

    /** 准心射线取 5 格内第一个可交互实体（载具本体或其座位上的乘员）。 */
    private static Entity findLookedAtVehicle(Minecraft mc) {
        if (mc.player == null || mc.level == null) return null;
        Vec3 eye = mc.player.getEyePosition(1.0F);
        Vec3 look = mc.player.getLookAngle();
        Vec3 end = eye.add(look.scale(HINT_RANGE));
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
            mc.player, eye, end,
            mc.player.getBoundingBox().expandTowards(look.scale(HINT_RANGE)).inflate(1.0D),
            entity -> entity != mc.player && entity.isPickable(),
            HINT_RANGE * HINT_RANGE);
        return hit == null ? null : hit.getEntity();
    }

    // ==================== 圆形进度条 ====================

    private static void drawInteractionCenterProgress(GuiGraphics graphics, Minecraft mc, float fill) {
        int cx = mc.getWindow().getGuiScaledWidth() / 2;
        int cy = mc.getWindow().getGuiScaledHeight() / 2;
        var icon = org.espetro.client.vehicle.VehicleInteractionState.icon();
        int size = 18;
        // 轻量图标底，保留交互状态图标。
        UiPaint.circle(graphics, cx, cy - 6, size / 2 + 2, UiTheme.ICON_PLATE);
        graphics.blit(icon, cx - size / 2, cy - size / 2 - 6, size, size,
            0f, 0f, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE,
            ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE);
        String label = org.espetro.client.vehicle.VehicleInteractionState.label();
        String percentText = Math.round(fill * 100f) + "%";
        int labelWidth = mc.font.width(label);
        int percentWidth = mc.font.width(percentText);
        int totalWidth = labelWidth + 4 + percentWidth;
        int textX = cx - totalWidth / 2;
        int textY = cy + 16;
        // 文本底衬：半透明深底，保证在亮色天空/雪地上也清晰。
        UiPaint.glassPanel(graphics, textX - 6, textY - 3, totalWidth + 12,
            HINT_PILL_HEIGHT, UiTheme.GLASS_PANEL);
        UiPaint.textShadowed(graphics, label, textX, textY, UiTheme.TEXT);
        UiPaint.textShadowed(graphics, percentText, textX + labelWidth + 4, textY, UiTheme.GOLD);
    }

    private static void drawCenterMountHint(GuiGraphics graphics, Minecraft mc) {
        int cx = mc.getWindow().getGuiScaledWidth() / 2;
        int cy = mc.getWindow().getGuiScaledHeight() / 2;
        net.minecraft.resources.ResourceLocation mountIcon =
            org.espetro.client.vehicle.VehicleInteractionState.icon();
        UiPaint.circle(graphics, cx, cy - 6, 11, UiTheme.ICON_PLATE);
        graphics.blit(mountIcon, cx - 9, cy - 15, 18, 18,
            0f, 0f, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE,
            ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE);
        String text = "上车";
        int textWidth = mc.font.width(text);
        int textY = cy + 16;
        UiPaint.glassPanel(graphics, cx - textWidth / 2 - 6, textY - 3, textWidth + 12,
            HINT_PILL_HEIGHT, UiTheme.GLASS_PANEL);
        UiPaint.textShadowed(graphics, text, cx - textWidth / 2, textY, UiTheme.TEXT_SECOND);
    }

}
