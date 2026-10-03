/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.client.renderer.texture.atlas.SpriteSource
 *  net.minecraft.client.renderer.texture.atlas.SpriteSources
 *  net.minecraft.data.PackOutput
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackType
 */
package net.minecraftforge.common.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;

public abstract class SpriteSourceProvider
extends JsonCodecProvider<List<SpriteSource>> {
    protected static final ResourceLocation BLOCKS_ATLAS = new ResourceLocation("blocks");
    protected static final ResourceLocation BANNER_PATTERNS_ATLAS = new ResourceLocation("banner_patterns");
    protected static final ResourceLocation BEDS_ATLAS = new ResourceLocation("beds");
    protected static final ResourceLocation CHESTS_ATLAS = new ResourceLocation("chests");
    protected static final ResourceLocation SHIELD_PATTERNS_ATLAS = new ResourceLocation("shield_patterns");
    protected static final ResourceLocation SHULKER_BOXES_ATLAS = new ResourceLocation("shulker_boxes");
    protected static final ResourceLocation SIGNS_ATLAS = new ResourceLocation("signs");
    protected static final ResourceLocation MOB_EFFECTS_ATLAS = new ResourceLocation("mob_effects");
    protected static final ResourceLocation PAINTINGS_ATLAS = new ResourceLocation("paintings");
    protected static final ResourceLocation PARTICLES_ATLAS = new ResourceLocation("particles");
    private final Map<ResourceLocation, SourceList> atlases = new HashMap<ResourceLocation, SourceList>();

    public SpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper, String modid) {
        super(output, fileHelper, modid, (DynamicOps<JsonElement>)JsonOps.INSTANCE, PackType.CLIENT_RESOURCES, "atlases", SpriteSources.f_260551_, Map.of());
    }

    @Override
    protected final void gather(BiConsumer<ResourceLocation, List<SpriteSource>> consumer) {
        this.addSources();
        this.atlases.forEach((atlas, srcList) -> consumer.accept((ResourceLocation)atlas, srcList.sources));
    }

    protected abstract void addSources();

    protected final SourceList atlas(ResourceLocation atlas) {
        return this.atlases.computeIfAbsent(atlas, $ -> new SourceList());
    }

    protected static final class SourceList {
        private final List<SpriteSource> sources = new ArrayList<SpriteSource>();

        protected SourceList() {
        }

        public SourceList addSource(SpriteSource source) {
            this.sources.add(source);
            return this;
        }
    }
}

