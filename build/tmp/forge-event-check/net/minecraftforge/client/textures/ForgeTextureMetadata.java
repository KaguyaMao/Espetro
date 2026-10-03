/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.metadata.MetadataSectionSerializer
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.util.GsonHelper
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.textures;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.textures.ITextureAtlasSpriteLoader;
import net.minecraftforge.client.textures.TextureAtlasSpriteLoaderManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ForgeTextureMetadata {
    public static final ForgeTextureMetadata EMPTY = new ForgeTextureMetadata(null);
    public static final MetadataSectionSerializer<ForgeTextureMetadata> SERIALIZER = new Serializer();
    @Nullable
    private final ITextureAtlasSpriteLoader loader;

    public static ForgeTextureMetadata forResource(Resource resource) throws IOException {
        Optional metadata = resource.m_215509_().m_214059_(SERIALIZER);
        return metadata.isEmpty() ? EMPTY : (ForgeTextureMetadata)metadata.get();
    }

    public ForgeTextureMetadata(@Nullable ITextureAtlasSpriteLoader loader) {
        this.loader = loader;
    }

    @Nullable
    public ITextureAtlasSpriteLoader getLoader() {
        return this.loader;
    }

    private static final class Serializer
    implements MetadataSectionSerializer<ForgeTextureMetadata> {
        private Serializer() {
        }

        @NotNull
        public String m_7991_() {
            return "forge";
        }

        @NotNull
        public ForgeTextureMetadata fromJson(JsonObject json) {
            ITextureAtlasSpriteLoader loader;
            if (json.has("loader")) {
                ResourceLocation loaderName = new ResourceLocation(GsonHelper.m_13906_((JsonObject)json, (String)"loader"));
                loader = TextureAtlasSpriteLoaderManager.get(loaderName);
                if (loader == null) {
                    throw new JsonSyntaxException("Unknown TextureAtlasSpriteLoader " + String.valueOf(loaderName));
                }
            } else {
                loader = null;
            }
            return new ForgeTextureMetadata(loader);
        }
    }
}

