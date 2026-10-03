/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 */
package net.minecraftforge.client.model.renderable;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.renderable.IRenderable;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class CompositeRenderable
implements IRenderable<Transforms> {
    private final List<Component> components = new ArrayList<Component>();

    private CompositeRenderable() {
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, float partialTick, Transforms context) {
        for (Component component : this.components) {
            component.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, context);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private static class Component {
        private final String name;
        private final List<Component> children = new ArrayList<Component>();
        private final List<Mesh> meshes = new ArrayList<Mesh>();

        public Component(String name) {
            this.name = name;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, Transforms context) {
            Matrix4f matrix = context.getTransform(this.name);
            if (matrix != null) {
                poseStack.m_85836_();
                poseStack.m_252931_(matrix);
            }
            for (Component part : this.children) {
                part.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, context);
            }
            for (Mesh mesh : this.meshes) {
                mesh.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay);
            }
            if (matrix != null) {
                poseStack.m_85849_();
            }
        }
    }

    public static class Transforms {
        public static final Transforms EMPTY = new Transforms((ImmutableMap<String, Matrix4f>)ImmutableMap.of());
        private final ImmutableMap<String, Matrix4f> parts;

        public static Transforms of(ImmutableMap<String, Matrix4f> parts) {
            return new Transforms(parts);
        }

        private Transforms(ImmutableMap<String, Matrix4f> parts) {
            this.parts = parts;
        }

        @Nullable
        public Matrix4f getTransform(String part) {
            return (Matrix4f)this.parts.get((Object)part);
        }
    }

    public static class Builder {
        private final CompositeRenderable renderable = new CompositeRenderable();

        private Builder() {
        }

        public PartBuilder<Builder> child(String name) {
            Component child = new Component(name);
            this.renderable.components.add(child);
            return new PartBuilder<Builder>(this, child);
        }

        public CompositeRenderable get() {
            return this.renderable;
        }
    }

    public static class PartBuilder<T> {
        private final T parent;
        private final Component component;

        private PartBuilder(T parent, Component component) {
            this.parent = parent;
            this.component = component;
        }

        public PartBuilder<PartBuilder<T>> child(String name) {
            Component child = new Component(this.component.name + "/" + name);
            this.component.children.add(child);
            return new PartBuilder<PartBuilder<T>>(this, child);
        }

        public PartBuilder<T> addMesh(ResourceLocation texture, List<BakedQuad> quads) {
            Mesh mesh = new Mesh(texture);
            mesh.quads.addAll(quads);
            this.component.meshes.add(mesh);
            return this;
        }

        public T end() {
            return this.parent;
        }
    }

    private static class Mesh {
        private final ResourceLocation texture;
        private final List<BakedQuad> quads = new ArrayList<BakedQuad>();

        public Mesh(ResourceLocation texture) {
            this.texture = texture;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay) {
            VertexConsumer consumer = bufferSource.m_6299_(textureRenderTypeLookup.get(this.texture));
            for (BakedQuad quad : this.quads) {
                consumer.putBulkData(poseStack.m_85850_(), quad, 1.0f, 1.0f, 1.0f, 1.0f, lightmap, overlay, true);
            }
        }
    }
}

