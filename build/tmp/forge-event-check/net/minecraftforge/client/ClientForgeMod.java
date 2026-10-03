/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package net.minecraftforge.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterNamedRenderTypesEvent;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.DynamicFluidContainerModel;
import net.minecraftforge.client.model.ElementsModel;
import net.minecraftforge.client.model.EmptyModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.SeparateTransformsModel;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD, modid="forge")
public class ClientForgeMod {
    @SubscribeEvent
    public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("empty", EmptyModel.LOADER);
        event.register("elements", ElementsModel.Loader.INSTANCE);
        event.register("obj", ObjLoader.INSTANCE);
        event.register("fluid_container", DynamicFluidContainerModel.Loader.INSTANCE);
        event.register("composite", CompositeModel.Loader.INSTANCE);
        event.register("item_layers", ItemLayerModel.Loader.INSTANCE);
        event.register("separate_transforms", SeparateTransformsModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((PreparableReloadListener)ObjLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void onRegisterNamedRenderTypes(RegisterNamedRenderTypesEvent event) {
        event.register("item_unlit", RenderType.m_110466_(), ForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get());
    }
}

