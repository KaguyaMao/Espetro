/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.scores.Team
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.event.RenderLivingEvent$Post
 *  net.minecraftforge.client.event.RenderNameTagEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  org.joml.Matrix4f
 */
package com.example.espoints.client;

import com.example.espoints.util.EspetroTeamBridge;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@OnlyIn(value=Dist.CLIENT)
@Mod.EventBusSubscriber(modid="espoints", value={Dist.CLIENT})
public class PlayerTeamIndicator {
    private static final float OFFSET_Y = 1.0f;
    private static final float TRIANGLE_SIZE = 2.0f;

    @SubscribeEvent
    public static void onRenderNameTag(RenderNameTagEvent event) {
        if (event.getEntity() instanceof Player) {
            // empty if block
        }
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderLivingEvent.Post<?, ?> event) {
    }

    private static boolean isInventoryRender() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91080_ != null) {
            String screenName = mc.f_91080_.getClass().getName();
            return screenName.contains("InventoryScreen") || screenName.contains("CreativeModeInventoryScreen");
        }
        return false;
    }

    private static boolean isFriendlyPlayer(Team localTeam, Team targetTeam, Player targetPlayer) {
        LocalPlayer localPlayer = Minecraft.m_91087_().f_91074_;
        return localPlayer != null && EspetroTeamBridge.isSameTeam(EspetroTeamBridge.getPlayerTeam((Player)localPlayer), EspetroTeamBridge.getPlayerTeam(targetPlayer));
    }

    private static int getTeamIndicatorColor(Team localTeam, Team targetTeam, Player targetPlayer) {
        if (PlayerTeamIndicator.isFriendlyPlayer(localTeam, targetTeam, targetPlayer)) {
            return -11141291;
        }
        if (EspetroTeamBridge.getPlayerTeam(targetPlayer) == null) {
            return -5592406;
        }
        return -43691;
    }

    private static void renderInvertedTriangle(PoseStack poseStack, MultiBufferSource buffer, int color) {
        Matrix4f matrix4f = poseStack.m_85850_().m_252922_();
        VertexConsumer vertexConsumer = buffer.m_6299_(RenderType.m_110504_());
        float centerX = 0.0f;
        float topY = -4.0f;
        float bottomY = 0.0f;
        float sideX = 2.0f;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        int a = color >> 24 & 0xFF;
        vertexConsumer.m_252986_(matrix4f, centerX, topY, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
        vertexConsumer.m_252986_(matrix4f, -sideX, bottomY, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
        vertexConsumer.m_252986_(matrix4f, -sideX, bottomY, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
        vertexConsumer.m_252986_(matrix4f, sideX, bottomY, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
        vertexConsumer.m_252986_(matrix4f, sideX, bottomY, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
        vertexConsumer.m_252986_(matrix4f, centerX, topY, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
    }

    private static void renderInvertedArrow(PoseStack poseStack, MultiBufferSource buffer, int color) {
        Matrix4f matrix4f = poseStack.m_85850_().m_252922_();
        VertexConsumer vertexConsumer = buffer.m_6299_(RenderType.m_110504_());
        float centerX = 0.0f;
        float topY = -4.0f;
        float bottomY = 0.0f;
        float sideX = 2.0f;
        float arrowLength = 6.0f;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        int a = color >> 24 & 0xFF;
        vertexConsumer.m_252986_(matrix4f, centerX, topY, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
        vertexConsumer.m_252986_(matrix4f, -sideX, bottomY, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
        vertexConsumer.m_252986_(matrix4f, centerX, topY, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
        vertexConsumer.m_252986_(matrix4f, sideX, bottomY, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
        vertexConsumer.m_252986_(matrix4f, -sideX, bottomY, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
        vertexConsumer.m_252986_(matrix4f, sideX, bottomY, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
        vertexConsumer.m_252986_(matrix4f, centerX, bottomY, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
        vertexConsumer.m_252986_(matrix4f, centerX, arrowLength, 0.0f).m_6122_(r, g, b, a).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 0.0f, 1.0f).m_5752_();
    }
}

