/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.client.ItemDecoratorHandler
 *  net.minecraftforge.client.extensions.common.IClientItemExtensions
 *  net.minecraftforge.client.extensions.common.IClientItemExtensions$FontContext
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package com.sighs.apricityui.forge;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.spi.AuiItemRenderRequest;
import com.sighs.apricityui.spi.AuiItemRenderService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.ItemDecoratorHandler;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class ItemRenderService
implements AuiItemRenderService {
    public static final ItemRenderService INSTANCE = new ItemRenderService();

    private ItemRenderService() {
    }

    @Override
    public boolean isEmptyStack(Object stack) {
        ItemStack itemStack;
        return !(stack instanceof ItemStack) || (itemStack = (ItemStack)stack).m_41619_();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void render(AuiItemRenderRequest request) {
        boolean hasStack;
        Object object = request.stack();
        if (!(object instanceof ItemStack)) {
            return;
        }
        ItemStack stack = (ItemStack)object;
        Minecraft minecraft = Minecraft.m_91087_();
        PoseStack poseStack = request.poseStack();
        MultiBufferSource.BufferSource bufferSource = minecraft.m_91269_().m_110104_();
        boolean bl = hasStack = !stack.m_41619_();
        if (hasStack) {
            BakedModel model = minecraft.m_91291_().m_174264_(stack, (Level)minecraft.f_91073_, (LivingEntity)minecraft.f_91074_, request.seed());
            boolean flatLighting = !model.m_7547_();
            poseStack.m_85836_();
            try {
                poseStack.m_252880_(8.0f, 8.0f, Base.getGuiItemModelZ());
                poseStack.m_252931_(new Matrix4f().scaling(1.0f, -1.0f, 1.0f));
                poseStack.m_85841_(16.0f, 16.0f, 16.0f);
                if (flatLighting) {
                    Lighting.m_84930_();
                }
                minecraft.m_91291_().m_269128_(stack, ItemDisplayContext.GUI, 0xF000F0, OverlayTexture.f_118083_, poseStack, (MultiBufferSource)bufferSource, (Level)minecraft.f_91073_, request.seed());
                bufferSource.m_109911_();
            }
            finally {
                if (flatLighting) {
                    Lighting.m_84931_();
                }
                poseStack.m_85849_();
            }
        }
        if (request.decorations() && (hasStack || ItemRenderService.hasOverlayText(request.overlayText()))) {
            ItemRenderService.drawDecorations(poseStack, stack, bufferSource, request);
        }
    }

    private static boolean hasOverlayText(String text) {
        return text != null && !text.isBlank();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void drawDecorations(PoseStack poseStack, ItemStack stack, MultiBufferSource.BufferSource bufferSource, AuiItemRenderRequest request) {
        Font customFont;
        Minecraft minecraft = Minecraft.m_91087_();
        Font font = minecraft.f_91062_;
        if (!stack.m_41619_() && (customFont = IClientItemExtensions.of((ItemStack)stack).getFont(stack, IClientItemExtensions.FontContext.ITEM_COUNT)) != null) {
            font = customFont;
        }
        poseStack.m_85836_();
        poseStack.m_252880_(0.0f, request.decorationOffsetY(), Base.getGuiItemDecorationZ());
        try {
            float cooldown;
            if (!stack.m_41619_() && stack.m_150947_()) {
                int width = Math.max(0, Math.min(13, stack.m_150948_()));
                Graph.drawFillRect(poseStack.m_85850_().m_252922_(), 2.0f, 13.0f, 15.0f, 15.0f, -16777216);
                if (width > 0) {
                    Graph.drawFillRect(poseStack.m_85850_().m_252922_(), 2.0f, 13.0f, 2.0f + (float)width, 14.0f, 0xFF000000 | stack.m_150949_());
                }
            }
            float f = cooldown = stack.m_41619_() || minecraft.f_91074_ == null ? 0.0f : minecraft.f_91074_.m_36335_().m_41521_(stack.m_41720_(), minecraft.m_91296_());
            if (cooldown > 0.0f) {
                int top = Mth.m_14143_((float)(16.0f * (1.0f - cooldown)));
                int bottom = top + Mth.m_14167_((float)(16.0f * cooldown));
                Graph.drawFillRect(poseStack.m_85850_().m_252922_(), 0.0f, top, 16.0f, bottom, Integer.MAX_VALUE);
            }
            Graph.endBatch();
            String text = request.overlayText();
            if (text == null && !stack.m_41619_() && stack.m_41613_() != 1) {
                text = String.valueOf(stack.m_41613_());
            }
            if (ItemRenderService.hasOverlayText(text)) {
                font.m_271703_(text, 17.0f - (float)font.m_92895_(text), 9.0f, -1, true, poseStack.m_85850_().m_252922_(), (MultiBufferSource)bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);
                bufferSource.m_109911_();
            }
            if (!stack.m_41619_()) {
                GuiGraphics guiGraphics = new GuiGraphics(minecraft, bufferSource);
                guiGraphics.m_280168_().m_85850_().m_252922_().set((Matrix4fc)poseStack.m_85850_().m_252922_());
                guiGraphics.m_280168_().m_85850_().m_252943_().set((Matrix3fc)poseStack.m_85850_().m_252943_());
                ItemDecoratorHandler.of((ItemStack)stack).render(guiGraphics, font, stack, 0, 0);
                bufferSource.m_109911_();
            }
        }
        finally {
            poseStack.m_85849_();
        }
    }
}

