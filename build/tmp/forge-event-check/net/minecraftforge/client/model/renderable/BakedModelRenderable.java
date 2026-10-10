/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.Unit
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector4f
 */
package net.minecraftforge.client.model.renderable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.renderable.IRenderable;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

public class BakedModelRenderable
implements IRenderable<Context> {
    private final BakedModel model;

    public static BakedModelRenderable of(ResourceLocation model) {
        return BakedModelRenderable.of(Minecraft.m_91087_().m_91304_().getModel(model));
    }

    public static BakedModelRenderable of(BakedModel model) {
        return new BakedModelRenderable(model);
    }

    private BakedModelRenderable(BakedModel model) {
        this.model = model;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, float partialTick, Context context) {
        VertexConsumer buffer = bufferSource.m_6299_(textureRenderTypeLookup.get(InventoryMenu.f_39692_));
        Vector4f tint = context.tint();
        RandomSource randomSource = context.randomSource();
        for (Direction direction : context.faces()) {
            randomSource.m_188584_(context.seed());
            for (BakedQuad quad : this.model.getQuads(context.state(), direction, randomSource, context.data(), null)) {
                buffer.putBulkData(poseStack.m_85850_(), quad, tint.x(), tint.y(), tint.z(), tint.w(), lightmap, overlay, true);
            }
        }
    }

    @Override
    public IRenderable<Unit> withContext(ModelData modelData) {
        return this.withContext(new Context(modelData));
    }

    public IRenderable<ModelData> withModelDataContext() {
        return (poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, partialTick, context) -> this.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, partialTick, new Context((ModelData)context));
    }

    public record Context(@Nullable BlockState state, Direction[] faces, RandomSource randomSource, long seed, ModelData data, Vector4f tint) {
        private static final Direction[] ALL_FACES_AND_NULL = Arrays.copyOf(Direction.values(), Direction.values().length + 1);
        private static final Vector4f WHITE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

        public Context(ModelData data) {
            this(null, ALL_FACES_AND_NULL, RandomSource.m_216327_(), 42L, data, WHITE);
        }
    }
}

