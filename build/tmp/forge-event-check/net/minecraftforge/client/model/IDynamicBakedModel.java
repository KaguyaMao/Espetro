/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Direction
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model;

import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IDynamicBakedModel
extends BakedModel {
    @NotNull
    default public List<BakedQuad> m_213637_(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
        return this.getQuads(state, side, rand, ModelData.EMPTY, null);
    }

    @NotNull
    public List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, @NotNull RandomSource var3, @NotNull ModelData var4, @Nullable RenderType var5);
}

