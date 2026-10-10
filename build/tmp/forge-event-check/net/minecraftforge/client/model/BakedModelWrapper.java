/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.ItemOverrides
 *  net.minecraft.client.renderer.block.model.ItemTransforms
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BakedModelWrapper<T extends BakedModel>
implements BakedModel {
    protected final T originalModel;

    public BakedModelWrapper(T originalModel) {
        this.originalModel = originalModel;
    }

    public List<BakedQuad> m_213637_(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return this.originalModel.m_213637_(state, side, rand);
    }

    public boolean m_7541_() {
        return this.originalModel.m_7541_();
    }

    public boolean useAmbientOcclusion(BlockState state) {
        return this.originalModel.useAmbientOcclusion(state);
    }

    public boolean useAmbientOcclusion(BlockState state, RenderType renderType) {
        return this.originalModel.useAmbientOcclusion(state, renderType);
    }

    public boolean m_7539_() {
        return this.originalModel.m_7539_();
    }

    public boolean m_7547_() {
        return this.originalModel.m_7547_();
    }

    public boolean m_7521_() {
        return this.originalModel.m_7521_();
    }

    public TextureAtlasSprite m_6160_() {
        return this.originalModel.m_6160_();
    }

    public ItemTransforms m_7442_() {
        return this.originalModel.m_7442_();
    }

    public ItemOverrides m_7343_() {
        return this.originalModel.m_7343_();
    }

    public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        return this.originalModel.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
    }

    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        return this.originalModel.getParticleIcon(data);
    }

    @NotNull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        return this.originalModel.getQuads(state, side, rand, extraData, renderType);
    }

    @NotNull
    public ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        return this.originalModel.getModelData(level, pos, state, modelData);
    }

    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return this.originalModel.getRenderTypes(state, rand, data);
    }

    public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
        return this.originalModel.getRenderTypes(itemStack, fabulous);
    }

    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
        return this.originalModel.getRenderPasses(itemStack, fabulous);
    }
}

