/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.registry.client.level.entity.EntityRendererRegistry
 *  dev.architectury.registry.client.rendering.BlockEntityRendererRegistry
 *  dev.architectury.registry.menu.MenuRegistry
 *  dev.architectury.registry.menu.MenuRegistry$ScreenFactory
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
 *  net.minecraft.client.renderer.entity.EntityRendererProvider
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.level.block.entity.BlockEntityType
 */
package dev.latvian.mods.kubejs.client;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.latvian.mods.kubejs.client.ClientEventJS;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ClientInitEventJS
extends ClientEventJS {
    public void registerBlockEntityRenderer(BlockEntityType<?> type, BlockEntityRendererProvider renderer) {
        BlockEntityRendererRegistry.register(type, (BlockEntityRendererProvider)renderer);
    }

    public void registerEntityRenderer(EntityType<?> type, EntityRendererProvider renderer) {
        EntityRendererRegistry.register(() -> type, (EntityRendererProvider)renderer);
    }

    public void registerMenuScreen(MenuType<?> type, MenuRegistry.ScreenFactory screenFactory) {
        MenuRegistry.registerScreenFactory(type, (MenuRegistry.ScreenFactory)screenFactory);
    }
}

