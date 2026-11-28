package org.espetro.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.espetro.client.vehicle.VehicleInteractionKind;
import org.espetro.client.vehicle.VehicleInteractionState;
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

    private static final int PANEL_WIDTH = 116;
    private static final int PANEL_HEIGHT = 31;
    private static final int ICON_SIZE = 13;
    private static final int ICON_TEXTURE_SIZE = 128;
    private static final int PAD_X = 5;
    private static final int BADGE_RADIUS = 16;

    private static final double HINT_RANGE = 5.0;
    private static final int HINT_Y_OFFSET = 20;
    private static final int HINT_PILL_HEIGHT = 13;

    private static boolean registered;
    private static boolean centerHook;

    public static void register() {
        if (registered) return;
        registered = true;
        MinecraftForge.EVENT_BUS.register(VehicleSupplyHud.class);
        try {
            var api=org.esradial.client.RadialMenuClientApi.class;
            var hook=api.getMethod("registerCenterPresentation",net.minecraft.resources.ResourceLocation.class,
                java.util.function.Supplier.class,java.util.function.BiConsumer.class);
            hook.invoke(null,net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("espetro","vehicle_root"),
                (java.util.function.Supplier<String>) () -> "",
                (java.util.function.BiConsumer<GuiGraphics,Float>) VehicleSupplyHud::renderWheelCenter);
            centerHook=true;
        } catch(ReflectiveOperationException ignored) {}
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;

        GuiGraphics graphics = event.getGuiGraphics();

        float interactionProgress =
            VehicleInteractionState.progress(event.getPartialTick());

        if (mc.options.hideGui || mc.screen != null) return;
        if (VehicleWheelController.isVehicleRootActive()) {
            VehicleSupplySyncPacket supply = VehicleWheelController.getCachedSupply();
            if (supply != null) drawCapacityBar(graphics, mc, supply);
            if (!centerHook && VehicleWheelController.isCenterHovered()) renderWheelCenter(graphics,event.getPartialTick());
            return;
        }

        if (VehicleWheelController.isWheelActive()) return;

        if (interactionProgress >= 0f) {
            drawInteractionCenterProgress(graphics, mc, interactionProgress);
            return;
        }

        // 已经坐在载具/乘客位里时不再提示“按 X 来互动”。
        if (mc.player.getVehicle() != null) return;

        drawCrosshairHint(graphics, mc);
    }

    private static void renderWheelCenter(GuiGraphics graphics,float partialTick) {
        var mc=Minecraft.getInstance();
        if (mc.player==null || mc.options.hideGui || !VehicleWheelController.isCenterHovered()) return;
        float progress=VehicleInteractionState.progress(partialTick);
        VehicleInteractionKind kind=progress>=0 ? VehicleInteractionState.kind()
            : mc.player.getVehicle()!=null ? VehicleInteractionKind.DISMOUNT : VehicleInteractionKind.MOUNT;
        drawSeatBadge(graphics,mc,kind,Math.max(0f,progress));
    }

    /** Compact shared-capacity bar, immediately above the vehicle wheel. */
    private static void drawCapacityBar(GuiGraphics graphics, Minecraft mc, VehicleSupplySyncPacket supply) {
        int capacity = supply.getMaxCapacity();
        if (capacity <= 0) return;
        int ammo = Math.max(0, supply.getAmmo());
        int construction = supply.canCarryConstruction() ? Math.max(0, supply.getConstruction()) : 0;
        int w = graphics.guiWidth(), h = graphics.guiHeight();
        int cx = w / 2;
        int x = cx - PANEL_WIDTH / 2;
        int y = Math.max(2, h / 2 - VehicleWheelController.displayRadius(w, h) - PANEL_HEIGHT - 5);
        graphics.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, UiTheme.RADIAL_STOCK_PLATE);

        if (supply.canCarryConstruction()) {
            drawStockValue(graphics, mc, cx - PANEL_WIDTH / 4, y + 2, ammo, false);
            drawStockValue(graphics, mc, cx + PANEL_WIDTH / 4, y + 2, construction, true);
        } else {
            drawStockValue(graphics, mc, cx, y + 2, ammo, false);
        }
        int bar = PANEL_WIDTH - PAD_X * 2, bx = x + PAD_X;
        int ammoEnd = (int) Math.min(bar, (long) ammo * bar / capacity);
        int totalEnd = (int) Math.min(bar, ((long) ammo + construction) * bar / capacity);
        graphics.fill(bx, y + 17, bx + bar, y + 21, UiTheme.RADIAL_STOCK_TRACK);
        if (ammoEnd > 0) graphics.fill(bx, y + 17, bx + ammoEnd, y + 21, UiTheme.WORLD_NEGATIVE);
        if (totalEnd > ammoEnd) graphics.fill(bx + ammoEnd, y + 17, bx + totalEnd, y + 21, UiTheme.WORLD_WARNING);
        String maximum = String.format(java.util.Locale.ROOT, "%,d", capacity);
        UiPaint.textScaled(graphics, "0", bx, y + 23, UiTheme.WORLD_TEXT, .75f, true);
        UiPaint.textScaled(graphics, maximum, bx + bar - Math.round(mc.font.width(maximum) * .75f),
            y + 23, UiTheme.WORLD_TEXT, .75f, true);
    }

    private static void drawStockValue(GuiGraphics graphics, Minecraft mc, int center, int y, int value, boolean build) {
        String text = String.format(java.util.Locale.ROOT, "%,d", value);
        int left = center - (ICON_SIZE + 3 + mc.font.width(text)) / 2;
        if (build) SupplyIcons.construction(graphics, left, y, ICON_SIZE);
        else SupplyIcons.ammunition(graphics, left, y, ICON_SIZE);
        UiPaint.textShadowed(graphics, text, left + ICON_SIZE + 3, y + 2,
            build ? UiTheme.WORLD_WARNING : UiTheme.WORLD_NEGATIVE);
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
        drawSeatBadge(graphics, mc, VehicleInteractionState.kind(), fill);
    }

    private static void drawSeatBadge(GuiGraphics graphics, Minecraft mc, VehicleInteractionKind kind, float fill) {
        int cx = graphics.guiWidth() / 2, cy = graphics.guiHeight() / 2;
        drawArc(graphics,cx,cy,0,BADGE_RADIUS,1,UiTheme.RADIAL_SEAT_PLATE);
        drawArc(graphics,cx,cy,BADGE_RADIUS-1.65f,BADGE_RADIUS,1,UiTheme.RADIAL_STOCK_TRACK);
        if(fill>0) drawArc(graphics,cx,cy,BADGE_RADIUS-1.65f,BADGE_RADIUS,fill,UiTheme.WORLD_TEXT);

        int size = 18;
        graphics.blit(VehicleInteractionState.icon(), cx - size / 2, cy - size / 2, size, size,
            0f, 0f, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE);
        boolean switchSeat = kind == VehicleInteractionKind.SEAT_SWITCH;
        String prefix = switchSeat ? "" : "按住";
        String key = switchSeat ? "Ctrl + 数字" : VehicleWheelController.interactKeyName();
        String suffix = switchSeat ? "更换座位" : kind == VehicleInteractionKind.DISMOUNT ? "以离开载具" : "以进入载具";
        int keyWidth = mc.font.width(key) + 8, gap = 4;
        int prefixWidth = mc.font.width(prefix);
        int width = prefixWidth + keyWidth + mc.font.width(suffix) + gap * (prefix.isEmpty() ? 1 : 2);
        int x = cx - width / 2, y = cy + BADGE_RADIUS + 7;
        if (!prefix.isEmpty()) {
            UiPaint.textShadowed(graphics, prefix, x, y, UiTheme.WORLD_TEXT);
            x += prefixWidth + gap;
        }
        graphics.fill(x, y - 3, x + keyWidth, y + 11, UiTheme.RADIAL_SEAT_PLATE);
        graphics.renderOutline(x, y - 3, keyWidth, 14, UiTheme.WORLD_TEXT);
        graphics.drawCenteredString(mc.font, key, x + keyWidth / 2, y, UiTheme.WORLD_TEXT);
        UiPaint.textShadowed(graphics, suffix, x + keyWidth + gap, y, UiTheme.WORLD_TEXT);
    }

    private static void drawArc(GuiGraphics graphics,int cx,int cy,float inner,float outer,float fraction,int color) {
        graphics.flush();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        var buffer=Tesselator.getInstance().getBuilder();
        var pose=graphics.pose().last().pose();
        buffer.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_COLOR);
        float feather=(float)graphics.guiWidth()/Minecraft.getInstance().getWindow().getWidth();
        float[] radii={Math.max(0,inner-feather*.5f),inner+feather*.5f,outer-feather*.5f,outer+feather*.5f};
        if(inner==0)radii[1]=0;
        int[] alpha={inner==0?255:0,255,255,0};
        int steps=Math.max(2,(int)Math.ceil(128*fraction));
        double sweep=2*Math.PI*fraction, endFeather=Math.min(sweep*.35,feather/outer);
        for(int i=0;i<steps;i++){
            double a=sweep*i/steps,b=sweep*(i+1)/steps;
            float aa=fraction>=1?1:(float)Math.min(1,Math.min(a,sweep-a)/endFeather);
            float ba=fraction>=1?1:(float)Math.min(1,Math.min(b,sweep-b)/endFeather);
            for(int j=0;j<3;j++){
                arcVertex(buffer,pose,cx,cy,a,radii[j],color,Math.round(alpha[j]*aa));
                arcVertex(buffer,pose,cx,cy,b,radii[j],color,Math.round(alpha[j]*ba));
                arcVertex(buffer,pose,cx,cy,b,radii[j+1],color,Math.round(alpha[j+1]*ba));
                arcVertex(buffer,pose,cx,cy,a,radii[j+1],color,Math.round(alpha[j+1]*aa));
            }
        }
        try {BufferUploader.drawWithShader(buffer.end());}
        finally {RenderSystem.enableCull(); RenderSystem.disableBlend();}
    }

    private static void arcVertex(BufferBuilder buffer,org.joml.Matrix4f pose,int cx,int cy,double a,float r,int color,int alpha) {
        buffer.vertex(pose,cx+(float)Math.sin(a)*r,cy-(float)Math.cos(a)*r,0)
            .color((color>>>16)&255,(color>>>8)&255,color&255,(color>>>24)*alpha/255).endVertex();
    }
}
