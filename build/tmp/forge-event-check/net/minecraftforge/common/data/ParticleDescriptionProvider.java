/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.Util
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.PackOutput$PathProvider
 *  net.minecraft.data.PackOutput$Target
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackType
 *  org.jetbrains.annotations.VisibleForTesting
 */
package net.minecraftforge.common.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.VisibleForTesting;

public abstract class ParticleDescriptionProvider
implements DataProvider {
    private final PackOutput.PathProvider particlesPath;
    @VisibleForTesting
    protected final ExistingFileHelper fileHelper;
    @VisibleForTesting
    protected final Map<ResourceLocation, List<String>> descriptions;

    protected ParticleDescriptionProvider(PackOutput output, ExistingFileHelper fileHelper) {
        this.particlesPath = output.m_245269_(PackOutput.Target.RESOURCE_PACK, "particles");
        this.fileHelper = fileHelper;
        this.descriptions = new HashMap<ResourceLocation, List<String>>();
    }

    protected abstract void addDescriptions();

    protected void sprite(ParticleType<?> type, ResourceLocation texture) {
        this.spriteSet(type, texture, new ResourceLocation[0]);
    }

    protected void spriteSet(ParticleType<?> type, final ResourceLocation baseName, final int numOfTextures, final boolean reverse) {
        Preconditions.checkArgument((numOfTextures > 0 ? 1 : 0) != 0, (Object)"The number of textures to generate must be positive");
        this.spriteSet(type, () -> new Iterator<ResourceLocation>(){
            private int counter = 0;

            @Override
            public boolean hasNext() {
                return this.counter < numOfTextures;
            }

            @Override
            public ResourceLocation next() {
                ResourceLocation texture = baseName.m_266382_("_" + (reverse ? numOfTextures - this.counter - 1 : this.counter));
                ++this.counter;
                return texture;
            }
        });
    }

    protected void spriteSet(ParticleType<?> type, ResourceLocation texture, ResourceLocation ... textures) {
        this.spriteSet(type, Stream.concat(Stream.of(texture), Arrays.stream(textures))::iterator);
    }

    protected void spriteSet(ParticleType<?> type, Iterable<ResourceLocation> textures) {
        ResourceLocation particle = (ResourceLocation)Preconditions.checkNotNull((Object)ForgeRegistries.PARTICLE_TYPES.getKey(type), (Object)"The particle type is not registered");
        ArrayList<String> desc = new ArrayList<String>();
        for (ResourceLocation texture : textures) {
            Preconditions.checkArgument((boolean)this.fileHelper.exists(texture, PackType.CLIENT_RESOURCES, ".png", "textures/particle"), (String)"Texture '%s' does not exist in any known resource pack", (Object)texture);
            desc.add(texture.toString());
        }
        Preconditions.checkArgument((desc.size() > 0 ? 1 : 0) != 0, (String)"The particle type '%s' must have one texture", (Object)particle);
        if (this.descriptions.putIfAbsent(particle, desc) != null) {
            throw new IllegalArgumentException(String.format("The particle type '%s' already has a description associated with it", particle));
        }
    }

    public CompletableFuture<?> m_213708_(CachedOutput cache) {
        this.addDescriptions();
        return CompletableFuture.allOf((CompletableFuture[])this.descriptions.entrySet().stream().map(entry -> {
            JsonArray textures = new JsonArray();
            ((List)entry.getValue()).forEach(arg_0 -> ((JsonArray)textures).add(arg_0));
            return DataProvider.m_253162_((CachedOutput)cache, (JsonElement)((JsonElement)Util.m_137469_((Object)new JsonObject(), obj -> obj.add("textures", (JsonElement)textures))), (Path)this.particlesPath.m_245731_((ResourceLocation)entry.getKey()));
        }).toArray(CompletableFuture[]::new));
    }

    public String m_6055_() {
        return "Particle Descriptions";
    }
}

