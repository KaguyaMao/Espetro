/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.ModelBakery
 *  net.minecraft.client.resources.model.ModelManager
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public abstract class ModelEvent
extends Event {
    @ApiStatus.Internal
    protected ModelEvent() {
    }

    public static class RegisterGeometryLoaders
    extends ModelEvent
    implements IModBusEvent {
        private final Map<ResourceLocation, IGeometryLoader<?>> loaders;

        @ApiStatus.Internal
        public RegisterGeometryLoaders(Map<ResourceLocation, IGeometryLoader<?>> loaders) {
            this.loaders = loaders;
        }

        public void register(String name, IGeometryLoader<?> loader) {
            ResourceLocation key = new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name);
            Preconditions.checkArgument((!this.loaders.containsKey(key) ? 1 : 0) != 0, (Object)("Geometry loader already registered: " + String.valueOf(key)));
            this.loaders.put(key, loader);
        }
    }

    public static class RegisterAdditional
    extends ModelEvent
    implements IModBusEvent {
        private final Set<ResourceLocation> models;

        @ApiStatus.Internal
        public RegisterAdditional(Set<ResourceLocation> models) {
            this.models = models;
        }

        public void register(ResourceLocation model) {
            this.models.add(model);
        }
    }

    public static class BakingCompleted
    extends ModelEvent
    implements IModBusEvent {
        private final ModelManager modelManager;
        private final Map<ResourceLocation, BakedModel> models;
        private final ModelBakery modelBakery;

        @ApiStatus.Internal
        public BakingCompleted(ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) {
            this.modelManager = modelManager;
            this.models = models;
            this.modelBakery = modelBakery;
        }

        public ModelManager getModelManager() {
            return this.modelManager;
        }

        public Map<ResourceLocation, BakedModel> getModels() {
            return this.models;
        }

        public ModelBakery getModelBakery() {
            return this.modelBakery;
        }
    }

    public static class ModifyBakingResult
    extends ModelEvent
    implements IModBusEvent {
        private final Map<ResourceLocation, BakedModel> models;
        private final ModelBakery modelBakery;

        @ApiStatus.Internal
        public ModifyBakingResult(Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) {
            this.models = models;
            this.modelBakery = modelBakery;
        }

        public Map<ResourceLocation, BakedModel> getModels() {
            return this.models;
        }

        public ModelBakery getModelBakery() {
            return this.modelBakery;
        }
    }
}

