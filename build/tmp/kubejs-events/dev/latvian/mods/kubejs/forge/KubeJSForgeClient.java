/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.color.block.BlockColor
 *  net.minecraft.client.color.item.ItemColor
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.material.FlowingFluid
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraftforge.client.event.RegisterColorHandlersEvent$Block
 *  net.minecraftforge.client.event.RegisterColorHandlersEvent$Item
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 */
package dev.latvian.mods.kubejs.forge;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.client.BlockTintFunctionWrapper;
import dev.latvian.mods.kubejs.client.ItemTintFunctionWrapper;
import dev.latvian.mods.kubejs.fluid.FluidBucketItemBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class KubeJSForgeClient {
    public KubeJSForgeClient() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::setupClient);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::blockColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::itemColors);
    }

    private void setupClient(FMLClientSetupEvent event) {
        BuilderBase b;
        KubeJS.PROXY.clientSetup();
        for (BuilderBase<Block> builderBase : RegistryInfo.BLOCK) {
            if (!(builderBase instanceof BlockBuilder)) continue;
            b = (BlockBuilder)builderBase;
            switch (((BlockBuilder)b).renderType) {
                case "cutout": {
                    ItemBlockRenderTypes.setRenderLayer((Block)((Block)b.get()), (RenderType)RenderType.m_110463_());
                    break;
                }
                case "cutout_mipped": {
                    ItemBlockRenderTypes.setRenderLayer((Block)((Block)b.get()), (RenderType)RenderType.m_110457_());
                    break;
                }
                case "translucent": {
                    ItemBlockRenderTypes.setRenderLayer((Block)((Block)b.get()), (RenderType)RenderType.m_110466_());
                }
            }
        }
        for (BuilderBase<Block> builderBase : RegistryInfo.FLUID) {
            if (!(builderBase instanceof FluidBuilder)) continue;
            b = (FluidBuilder)builderBase;
            switch (((FluidBuilder)b).renderType) {
                case "cutout": {
                    ItemBlockRenderTypes.setRenderLayer((Fluid)((FlowingFluid)b.get()).m_5613_(), (RenderType)RenderType.m_110463_());
                    ItemBlockRenderTypes.setRenderLayer((Fluid)((FlowingFluid)b.get()).m_5615_(), (RenderType)RenderType.m_110463_());
                    break;
                }
                case "cutout_mipped": {
                    ItemBlockRenderTypes.setRenderLayer((Fluid)((FlowingFluid)b.get()).m_5613_(), (RenderType)RenderType.m_110457_());
                    ItemBlockRenderTypes.setRenderLayer((Fluid)((FlowingFluid)b.get()).m_5615_(), (RenderType)RenderType.m_110457_());
                    break;
                }
                case "translucent": {
                    ItemBlockRenderTypes.setRenderLayer((Fluid)((FlowingFluid)b.get()).m_5613_(), (RenderType)RenderType.m_110466_());
                    ItemBlockRenderTypes.setRenderLayer((Fluid)((FlowingFluid)b.get()).m_5615_(), (RenderType)RenderType.m_110466_());
                }
            }
        }
    }

    private void blockColors(RegisterColorHandlersEvent.Block event) {
        for (BuilderBase<Block> builderBase : RegistryInfo.BLOCK) {
            if (!(builderBase instanceof BlockBuilder)) continue;
            BlockBuilder b = (BlockBuilder)builderBase;
            if (b.tint == null) continue;
            event.register((BlockColor)new BlockTintFunctionWrapper(b.tint), new Block[]{(Block)b.get()});
        }
    }

    private void itemColors(RegisterColorHandlersEvent.Item event) {
        for (BuilderBase<Item> builderBase : RegistryInfo.ITEM) {
            ItemBuilder b;
            if (builderBase instanceof ItemBuilder) {
                b = (ItemBuilder)builderBase;
                if (b.tint != null) {
                    event.register((ItemColor)new ItemTintFunctionWrapper(b.tint), new ItemLike[]{(ItemLike)b.get()});
                }
            }
            if (!(builderBase instanceof FluidBucketItemBuilder)) continue;
            b = (FluidBucketItemBuilder)builderBase;
            if (((FluidBucketItemBuilder)b).fluidBuilder.bucketColor == -1) continue;
            event.register((arg_0, arg_1) -> KubeJSForgeClient.lambda$itemColors$0((FluidBucketItemBuilder)b, arg_0, arg_1), new ItemLike[]{(ItemLike)b.get()});
        }
    }

    private static /* synthetic */ int lambda$itemColors$0(FluidBucketItemBuilder b, ItemStack stack, int index) {
        return index == 1 ? b.fluidBuilder.bucketColor : -1;
    }
}

