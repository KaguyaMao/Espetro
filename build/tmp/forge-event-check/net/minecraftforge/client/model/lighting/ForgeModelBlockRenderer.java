/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.color.block.BlockColors
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.ModelBlockRenderer
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package net.minecraftforge.client.model.lighting;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.lighting.FlatQuadLighter;
import net.minecraftforge.client.model.lighting.QuadLighter;
import net.minecraftforge.client.model.lighting.SmoothQuadLighter;
import net.minecraftforge.common.ForgeConfig;

public class ForgeModelBlockRenderer
extends ModelBlockRenderer {
    private static final Direction[] SIDES = Direction.values();
    private final ThreadLocal<QuadLighter> flatLighter = ThreadLocal.withInitial(() -> new FlatQuadLighter(colors));
    private final ThreadLocal<QuadLighter> smoothLighter = ThreadLocal.withInitial(() -> new SmoothQuadLighter(colors));

    public ForgeModelBlockRenderer(BlockColors colors) {
        super(colors);
    }

    public void tesselateWithoutAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer vertexConsumer, boolean checkSides, RandomSource rand, long seed, int packedOverlay, ModelData modelData, RenderType renderType) {
        if (((Boolean)ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get()).booleanValue()) {
            ForgeModelBlockRenderer.render(vertexConsumer, this.flatLighter.get(), level, model, state, pos, poseStack, checkSides, rand, seed, packedOverlay, modelData, renderType);
        } else {
            super.tesselateWithoutAO(level, model, state, pos, poseStack, vertexConsumer, checkSides, rand, seed, packedOverlay, modelData, renderType);
        }
    }

    public void tesselateWithAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer vertexConsumer, boolean checkSides, RandomSource rand, long seed, int packedOverlay, ModelData modelData, RenderType renderType) {
        if (((Boolean)ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get()).booleanValue()) {
            ForgeModelBlockRenderer.render(vertexConsumer, this.smoothLighter.get(), level, model, state, pos, poseStack, checkSides, rand, seed, packedOverlay, modelData, renderType);
        } else {
            super.tesselateWithAO(level, model, state, pos, poseStack, vertexConsumer, checkSides, rand, seed, packedOverlay, modelData, renderType);
        }
    }

    public static boolean render(VertexConsumer vertexConsumer, QuadLighter lighter, BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, boolean checkSides, RandomSource rand, long seed, int packedOverlay, ModelData modelData, RenderType renderType) {
        ForgeModelBlockRenderer renderer = (ForgeModelBlockRenderer)Minecraft.m_91087_().m_91289_().m_110937_();
        PoseStack.Pose pose = poseStack.m_85850_();
        boolean empty = true;
        boolean smoothLighter = lighter instanceof SmoothQuadLighter;
        QuadLighter flatLighter = null;
        rand.m_188584_(seed);
        List quads = model.getQuads(state, null, rand, modelData, renderType);
        if (!quads.isEmpty()) {
            empty = false;
            lighter.setup(level, pos, state);
            for (BakedQuad quad : quads) {
                if (smoothLighter && !quad.hasAmbientOcclusion()) {
                    if (flatLighter == null) {
                        flatLighter = renderer.flatLighter.get();
                        flatLighter.setup(level, pos, state);
                    }
                    flatLighter.process(vertexConsumer, pose, quad, packedOverlay);
                    continue;
                }
                lighter.process(vertexConsumer, pose, quad, packedOverlay);
            }
        }
        for (Direction side : SIDES) {
            if (checkSides && !Block.m_152444_((BlockState)state, (BlockGetter)level, (BlockPos)pos, (Direction)side, (BlockPos)pos.m_121945_(side))) continue;
            rand.m_188584_(seed);
            quads = model.getQuads(state, side, rand, modelData, renderType);
            if (quads.isEmpty()) continue;
            if (empty) {
                empty = false;
                lighter.setup(level, pos, state);
            }
            for (BakedQuad quad : quads) {
                if (smoothLighter && !quad.hasAmbientOcclusion()) {
                    if (flatLighter == null) {
                        flatLighter = renderer.flatLighter.get();
                        flatLighter.setup(level, pos, state);
                    }
                    flatLighter.process(vertexConsumer, pose, quad, packedOverlay);
                    continue;
                }
                lighter.process(vertexConsumer, pose, quad, packedOverlay);
            }
        }
        lighter.reset();
        if (flatLighter != null) {
            flatLighter.reset();
        }
        return !empty;
    }
}

