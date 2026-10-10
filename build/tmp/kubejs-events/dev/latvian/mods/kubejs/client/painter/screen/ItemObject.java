/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  dev.latvian.mods.unit.FixedBooleanUnit
 *  dev.latvian.mods.unit.FixedNumberUnit
 *  dev.latvian.mods.unit.Unit
 *  dev.latvian.mods.unit.UnitVariables
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 */
package dev.latvian.mods.kubejs.client.painter.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.kubejs.client.painter.screen.BoxObject;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenEventJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.unit.FixedBooleanUnit;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class ItemObject
extends BoxObject {
    public ItemStack itemStack = ItemStack.f_41583_;
    public Unit overlay = FixedBooleanUnit.TRUE;
    public String customText = "";
    public Unit rotation = FixedNumberUnit.ZERO;

    public ItemObject(Painter painter) {
        super(painter);
        this.z = FixedNumberUnit.of((double)100.0);
    }

    @Override
    protected void load(PainterObjectProperties properties) {
        super.load(properties);
        if (properties.hasAny("item")) {
            this.itemStack = ItemStackJS.of(properties.tag.m_128423_("item"));
        }
        this.overlay = properties.getUnit("overlay", this.overlay);
        this.customText = properties.getString("customText", this.customText);
        this.rotation = properties.getUnit("rotation", this.rotation);
    }

    @Override
    public void draw(PaintScreenEventJS event) {
        if (this.itemStack.m_41619_()) {
            return;
        }
        float aw = this.w.getFloat((UnitVariables)event);
        float ah = this.h.getFloat((UnitVariables)event);
        float ax = event.alignX(this.x.getFloat((UnitVariables)event), aw, this.alignX);
        float ay = event.alignY(this.y.getFloat((UnitVariables)event), ah, this.alignY);
        float az = this.z.getFloat((UnitVariables)event);
        event.push();
        event.translate(ax, ay, az);
        if (this.rotation != FixedNumberUnit.ZERO) {
            event.rotateRad(this.rotation.getFloat((UnitVariables)event));
        }
        event.scale(aw / 16.0f, ah / 16.0f, 1.0f);
        event.blend(true);
        ItemObject.drawItem(event.matrices, this.itemStack, 0, this.overlay.getBoolean((UnitVariables)event), this.customText.isEmpty() ? null : this.customText);
        event.pop();
    }

    public static void drawItem(PoseStack poseStack, ItemStack stack, int hash, boolean renderOverlay, @Nullable String text) {
        boolean flatLight;
        if (stack.m_41619_()) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        ItemRenderer itemRenderer = mc.m_91291_();
        BakedModel bakedModel = itemRenderer.m_174264_(stack, null, (LivingEntity)mc.f_91074_, hash);
        Minecraft.m_91087_().m_91097_().m_118506_(InventoryMenu.f_39692_).m_117960_(false, false);
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)InventoryMenu.f_39692_);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.m_85836_();
        modelViewStack.m_252931_(poseStack.m_85850_().m_252922_());
        modelViewStack.m_85841_(1.0f, -1.0f, 1.0f);
        modelViewStack.m_85841_(16.0f, 16.0f, 16.0f);
        RenderSystem.applyModelViewMatrix();
        MultiBufferSource.BufferSource bufferSource = Minecraft.m_91087_().m_91269_().m_110104_();
        boolean bl = flatLight = !bakedModel.m_7547_();
        if (flatLight) {
            Lighting.m_84930_();
        }
        itemRenderer.m_115143_(stack, ItemDisplayContext.GUI, false, new PoseStack(), (MultiBufferSource)bufferSource, 0xF000F0, OverlayTexture.f_118083_, bakedModel);
        bufferSource.m_109911_();
        RenderSystem.enableDepthTest();
        if (flatLight) {
            Lighting.m_84931_();
        }
        modelViewStack.m_85849_();
        RenderSystem.applyModelViewMatrix();
        if (renderOverlay) {
            float cooldown;
            Tesselator t = Tesselator.m_85913_();
            Font font = mc.f_91062_;
            if (stack.m_41613_() != 1 || text != null) {
                String s = text == null ? String.valueOf(stack.m_41613_()) : text;
                poseStack.m_85836_();
                poseStack.m_85837_(9.0 - (double)font.m_92895_(s), 1.0, 20.0);
                font.m_271703_(s, 0.0f, 0.0f, 0xFFFFFF, true, poseStack.m_85850_().m_252922_(), (MultiBufferSource)bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);
                bufferSource.m_109911_();
                poseStack.m_85849_();
            }
            if (stack.m_150947_()) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableBlend();
                int barWidth = stack.m_150948_();
                int barColor = stack.m_150949_();
                ItemObject.draw(poseStack, t, -6, 5, 13, 2, 0, 0, 0, 255);
                ItemObject.draw(poseStack, t, -6, 5, barWidth, 1, barColor >> 16 & 0xFF, barColor >> 8 & 0xFF, barColor & 0xFF, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
            }
            float f = cooldown = mc.f_91074_ == null ? 0.0f : mc.f_91074_.m_36335_().m_41521_(stack.m_41720_(), mc.m_91296_());
            if (cooldown > 0.0f) {
                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                ItemObject.draw(poseStack, t, -8, Mth.m_14143_((float)(16.0f * (1.0f - cooldown))) - 8, 16, Mth.m_14167_((float)(16.0f * cooldown)), 255, 255, 255, 127);
                RenderSystem.enableDepthTest();
            }
        }
    }

    private static void draw(PoseStack matrixStack, Tesselator t, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        if (width <= 0 || height <= 0) {
            return;
        }
        RenderSystem.setShader(GameRenderer::m_172811_);
        Matrix4f m = matrixStack.m_85850_().m_252922_();
        BufferBuilder renderer = t.m_85915_();
        renderer.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        renderer.m_252986_(m, (float)x, (float)y, 0.0f).m_6122_(red, green, blue, alpha).m_5752_();
        renderer.m_252986_(m, (float)x, (float)(y + height), 0.0f).m_6122_(red, green, blue, alpha).m_5752_();
        renderer.m_252986_(m, (float)(x + width), (float)(y + height), 0.0f).m_6122_(red, green, blue, alpha).m_5752_();
        renderer.m_252986_(m, (float)(x + width), (float)y, 0.0f).m_6122_(red, green, blue, alpha).m_5752_();
        t.m_85914_();
    }
}

