/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoader
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ItemDecoratorHandler {
    private final List<IItemDecorator> itemDecorators;
    private static Map<Item, ItemDecoratorHandler> DECORATOR_LOOKUP = ImmutableMap.of();
    private static final ItemDecoratorHandler EMPTY = new ItemDecoratorHandler();

    private ItemDecoratorHandler() {
        this.itemDecorators = ImmutableList.of();
    }

    private ItemDecoratorHandler(List<IItemDecorator> itemDecorators) {
        this.itemDecorators = ImmutableList.copyOf(itemDecorators);
    }

    public static void init() {
        HashMap<Item, List<IItemDecorator>> decorators = new HashMap<Item, List<IItemDecorator>>();
        RegisterItemDecorationsEvent event = new RegisterItemDecorationsEvent(decorators);
        ModLoader.get().postEventWrapContainerInModOrder((Event)event);
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        decorators.forEach((item, itemDecorators) -> builder.put(item, (Object)new ItemDecoratorHandler((List<IItemDecorator>)itemDecorators)));
        DECORATOR_LOOKUP = builder.build();
    }

    public static ItemDecoratorHandler of(ItemStack stack) {
        return DECORATOR_LOOKUP.getOrDefault(stack.m_41720_(), EMPTY);
    }

    public void render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        this.resetRenderState();
        for (IItemDecorator itemDecorator : this.itemDecorators) {
            if (!itemDecorator.render(guiGraphics, font, stack, xOffset, yOffset)) continue;
            this.resetRenderState();
        }
    }

    private void resetRenderState() {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }
}

