/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.Sheets
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public final class RenderTypeHelper {
    @NotNull
    public static RenderType getEntityRenderType(RenderType chunkRenderType, boolean cull) {
        if (chunkRenderType != RenderType.m_110466_()) {
            return Sheets.m_110790_();
        }
        return cull || !Minecraft.m_91085_() ? Sheets.m_110792_() : Sheets.m_110791_();
    }

    @NotNull
    public static RenderType getMovingBlockRenderType(RenderType renderType) {
        if (renderType == RenderType.m_110466_()) {
            return RenderType.m_110469_();
        }
        return renderType;
    }

    @NotNull
    public static RenderType getFallbackItemRenderType(ItemStack stack, BakedModel model, boolean cull) {
        Item item = stack.m_41720_();
        if (item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            ChunkRenderTypeSet renderTypes = model.getRenderTypes(blockItem.m_40614_().m_49966_(), RandomSource.m_216335_((long)42L), ModelData.EMPTY);
            if (renderTypes.contains(RenderType.m_110466_())) {
                return RenderTypeHelper.getEntityRenderType(RenderType.m_110466_(), cull);
            }
            return Sheets.m_110790_();
        }
        return cull ? Sheets.m_110792_() : Sheets.m_110791_();
    }

    private RenderTypeHelper() {
    }
}

