/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.SkullModelBase
 *  net.minecraft.client.model.geom.EntityModelSet
 *  net.minecraft.client.model.geom.ModelLayerLocation
 *  net.minecraft.client.model.geom.builders.LayerDefinition
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderers
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.EntityRenderers
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.SkullBlock$Type
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.event;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public abstract class EntityRenderersEvent
extends Event
implements IModBusEvent {
    @ApiStatus.Internal
    protected EntityRenderersEvent() {
    }

    public static class CreateSkullModels
    extends EntityRenderersEvent {
        private final ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder;
        private final EntityModelSet entityModelSet;

        @ApiStatus.Internal
        public CreateSkullModels(ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder, EntityModelSet entityModelSet) {
            this.builder = builder;
            this.entityModelSet = entityModelSet;
        }

        public EntityModelSet getEntityModelSet() {
            return this.entityModelSet;
        }

        public void registerSkullModel(SkullBlock.Type type, SkullModelBase model) {
            this.builder.put((Object)type, (Object)model);
        }
    }

    public static class AddLayers
    extends EntityRenderersEvent {
        private final Map<EntityType<?>, EntityRenderer<?>> renderers;
        private final Map<String, EntityRenderer<? extends Player>> skinMap;
        private final EntityRendererProvider.Context context;

        @ApiStatus.Internal
        public AddLayers(Map<EntityType<?>, EntityRenderer<?>> renderers, Map<String, EntityRenderer<? extends Player>> playerRenderers, EntityRendererProvider.Context context) {
            this.renderers = renderers;
            this.skinMap = playerRenderers;
            this.context = context;
        }

        public Set<String> getSkins() {
            return this.skinMap.keySet();
        }

        @Nullable
        public <R extends EntityRenderer<? extends Player>> R getPlayerSkin(String skinName) {
            return (R)this.skinMap.get(skinName);
        }

        @Nullable
        public <R extends LivingEntityRenderer<? extends Player, ? extends EntityModel<? extends Player>>> R getSkin(String skinName) {
            LivingEntityRenderer renderer;
            EntityRenderer<? extends Player> entityRenderer = this.skinMap.get(skinName);
            return (R)(entityRenderer instanceof LivingEntityRenderer ? (renderer = (LivingEntityRenderer)entityRenderer) : null);
        }

        @Nullable
        public <T extends Entity, R extends EntityRenderer<T>> R getEntityRenderer(EntityType<? extends T> entityType) {
            return (R)this.renderers.get(entityType);
        }

        @Nullable
        public <T extends LivingEntity, R extends LivingEntityRenderer<T, ? extends EntityModel<T>>> R getRenderer(EntityType<? extends T> entityType) {
            LivingEntityRenderer renderer;
            EntityRenderer<?> entityRenderer = this.renderers.get(entityType);
            return (R)(entityRenderer instanceof LivingEntityRenderer ? (renderer = (LivingEntityRenderer)entityRenderer) : null);
        }

        public EntityModelSet getEntityModels() {
            return this.context.m_174027_();
        }

        public EntityRendererProvider.Context getContext() {
            return this.context;
        }
    }

    public static class RegisterRenderers
    extends EntityRenderersEvent {
        @ApiStatus.Internal
        public RegisterRenderers() {
        }

        public <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider) {
            EntityRenderers.m_174036_(entityType, entityRendererProvider);
        }

        public <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider) {
            BlockEntityRenderers.m_173590_(blockEntityType, blockEntityRendererProvider);
        }
    }

    public static class RegisterLayerDefinitions
    extends EntityRenderersEvent {
        @ApiStatus.Internal
        public RegisterLayerDefinitions() {
        }

        public void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
            ForgeHooksClient.registerLayerDefinition(layerLocation, supplier);
        }
    }
}

