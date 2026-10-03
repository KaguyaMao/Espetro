/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.tags.BiomeTagsProvider
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.tags.BiomeTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biomes
 */
package net.minecraftforge.common.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class ForgeBiomeTagsProvider
extends BiomeTagsProvider {
    public ForgeBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    protected void m_6577_(HolderLookup.Provider lookupProvider) {
        this.tag((ResourceKey<Biome>)Biomes.f_48202_, Tags.Biomes.IS_PLAINS);
        this.tag((ResourceKey<Biome>)Biomes.f_48203_, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_DRY_OVERWORLD, Tags.Biomes.IS_SANDY, Tags.Biomes.IS_DESERT);
        this.tag((ResourceKey<Biome>)Biomes.f_48206_, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_CONIFEROUS);
        this.tag((ResourceKey<Biome>)Biomes.f_48207_, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_SWAMP);
        this.tag((ResourceKey<Biome>)Biomes.f_48209_, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY_NETHER);
        this.tag((ResourceKey<Biome>)Biomes.f_48210_, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY_END);
        this.tag((ResourceKey<Biome>)Biomes.f_48211_, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY);
        this.tag((ResourceKey<Biome>)Biomes.f_48212_, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY);
        this.tag((ResourceKey<Biome>)Biomes.f_186761_, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_WASTELAND, Tags.Biomes.IS_PLAINS);
        this.tag((ResourceKey<Biome>)Biomes.f_48215_, Tags.Biomes.IS_MUSHROOM, Tags.Biomes.IS_RARE);
        this.tag((ResourceKey<Biome>)Biomes.f_48222_, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_DENSE_OVERWORLD);
        this.tag((ResourceKey<Biome>)Biomes.f_186769_, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_RARE);
        this.tag((ResourceKey<Biome>)Biomes.f_48217_, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_SANDY);
        this.tag((ResourceKey<Biome>)Biomes.f_48148_, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY);
        this.tag((ResourceKey<Biome>)Biomes.f_48151_, Tags.Biomes.IS_SPOOKY, Tags.Biomes.IS_DENSE_OVERWORLD);
        this.tag((ResourceKey<Biome>)Biomes.f_48152_, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_CONIFEROUS, Tags.Biomes.IS_SNOWY);
        this.tag((ResourceKey<Biome>)Biomes.f_186763_, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_CONIFEROUS);
        this.tag((ResourceKey<Biome>)Biomes.f_186767_, Tags.Biomes.IS_SPARSE_OVERWORLD);
        this.tag((ResourceKey<Biome>)Biomes.f_48157_, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD);
        this.tag((ResourceKey<Biome>)Biomes.f_48158_, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_RARE, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_PLATEAU);
        this.tag((ResourceKey<Biome>)Biomes.f_48159_, Tags.Biomes.IS_SANDY, Tags.Biomes.IS_DRY_OVERWORLD);
        this.tag((ResourceKey<Biome>)Biomes.f_186753_, Tags.Biomes.IS_SANDY, Tags.Biomes.IS_DRY_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_SLOPE, Tags.Biomes.IS_PLATEAU);
        this.tag((ResourceKey<Biome>)Biomes.f_186754_, Tags.Biomes.IS_PLAINS, Tags.Biomes.IS_PLATEAU, Tags.Biomes.IS_SLOPE);
        this.tag((ResourceKey<Biome>)Biomes.f_186755_, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_CONIFEROUS, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_SLOPE);
        this.tag((ResourceKey<Biome>)Biomes.f_186756_, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_SLOPE);
        this.tag((ResourceKey<Biome>)Biomes.f_186758_, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_PEAK);
        this.tag((ResourceKey<Biome>)Biomes.f_186757_, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_PEAK);
        this.tag((ResourceKey<Biome>)Biomes.f_186759_, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_PEAK);
        this.tag((ResourceKey<Biome>)Biomes.f_48162_, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY_END);
        this.tag((ResourceKey<Biome>)Biomes.f_48163_, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY_END);
        this.tag((ResourceKey<Biome>)Biomes.f_48164_, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY_END);
        this.tag((ResourceKey<Biome>)Biomes.f_48165_, Tags.Biomes.IS_COLD_END, Tags.Biomes.IS_DRY_END);
        this.tag((ResourceKey<Biome>)Biomes.f_48166_, Tags.Biomes.IS_HOT_OVERWORLD);
        this.tag((ResourceKey<Biome>)Biomes.f_48168_, Tags.Biomes.IS_COLD_OVERWORLD);
        this.tag((ResourceKey<Biome>)Biomes.f_48171_, Tags.Biomes.IS_COLD_OVERWORLD);
        this.tag((ResourceKey<Biome>)Biomes.f_48172_, Tags.Biomes.IS_COLD_OVERWORLD);
        this.tag((ResourceKey<Biome>)Biomes.f_48173_, Tags.Biomes.IS_VOID);
        this.tag((ResourceKey<Biome>)Biomes.f_48176_, Tags.Biomes.IS_PLAINS, Tags.Biomes.IS_RARE);
        this.tag((ResourceKey<Biome>)Biomes.f_186766_, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_RARE);
        this.tag((ResourceKey<Biome>)Biomes.f_48179_, Tags.Biomes.IS_RARE);
        this.tag((ResourceKey<Biome>)Biomes.f_48182_, Tags.Biomes.IS_COLD_OVERWORLD, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_RARE);
        this.tag((ResourceKey<Biome>)Biomes.f_186762_, Tags.Biomes.IS_DENSE_OVERWORLD, Tags.Biomes.IS_RARE);
        this.tag((ResourceKey<Biome>)Biomes.f_186764_, Tags.Biomes.IS_DENSE_OVERWORLD, Tags.Biomes.IS_RARE);
        this.tag((ResourceKey<Biome>)Biomes.f_186768_, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_DRY_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_RARE);
        this.tag((ResourceKey<Biome>)Biomes.f_48194_, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_DRY_OVERWORLD, Tags.Biomes.IS_SPARSE_OVERWORLD, Tags.Biomes.IS_RARE);
        this.tag((ResourceKey<Biome>)Biomes.f_48197_, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_RARE);
        this.tag((ResourceKey<Biome>)Biomes.f_151785_, Tags.Biomes.IS_CAVE, Tags.Biomes.IS_LUSH, Tags.Biomes.IS_WET_OVERWORLD);
        this.tag((ResourceKey<Biome>)Biomes.f_151784_, Tags.Biomes.IS_CAVE, Tags.Biomes.IS_SPARSE_OVERWORLD);
        this.tag((ResourceKey<Biome>)Biomes.f_48199_, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY_NETHER);
        this.tag((ResourceKey<Biome>)Biomes.f_48200_, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY_NETHER);
        this.tag((ResourceKey<Biome>)Biomes.f_48201_, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY_NETHER);
        this.tag((ResourceKey<Biome>)Biomes.f_48175_, Tags.Biomes.IS_HOT_NETHER, Tags.Biomes.IS_DRY_NETHER);
        this.tag((ResourceKey<Biome>)Biomes.f_220595_, Tags.Biomes.IS_WET_OVERWORLD, Tags.Biomes.IS_HOT_OVERWORLD, Tags.Biomes.IS_SWAMP);
        this.tag((ResourceKey<Biome>)Biomes.f_220594_, Tags.Biomes.IS_CAVE, Tags.Biomes.IS_RARE, Tags.Biomes.IS_SPOOKY);
        this.m_206424_(Tags.Biomes.IS_HOT).m_206428_(Tags.Biomes.IS_HOT_OVERWORLD).m_206428_(Tags.Biomes.IS_HOT_NETHER).m_176841_(Tags.Biomes.IS_HOT_END.f_203868_());
        this.m_206424_(Tags.Biomes.IS_COLD).m_206428_(Tags.Biomes.IS_COLD_OVERWORLD).m_176841_(Tags.Biomes.IS_COLD_NETHER.f_203868_()).m_206428_(Tags.Biomes.IS_COLD_END);
        this.m_206424_(Tags.Biomes.IS_SPARSE).m_206428_(Tags.Biomes.IS_SPARSE_OVERWORLD).m_176841_(Tags.Biomes.IS_SPARSE_NETHER.f_203868_()).m_176841_(Tags.Biomes.IS_SPARSE_END.f_203868_());
        this.m_206424_(Tags.Biomes.IS_DENSE).m_206428_(Tags.Biomes.IS_DENSE_OVERWORLD).m_176841_(Tags.Biomes.IS_DENSE_NETHER.f_203868_()).m_176841_(Tags.Biomes.IS_DENSE_END.f_203868_());
        this.m_206424_(Tags.Biomes.IS_WET).m_206428_(Tags.Biomes.IS_WET_OVERWORLD).m_176841_(Tags.Biomes.IS_WET_NETHER.f_203868_()).m_176841_(Tags.Biomes.IS_WET_END.f_203868_());
        this.m_206424_(Tags.Biomes.IS_DRY).m_206428_(Tags.Biomes.IS_DRY_OVERWORLD).m_206428_(Tags.Biomes.IS_DRY_NETHER).m_206428_(Tags.Biomes.IS_DRY_END);
        this.m_206424_(Tags.Biomes.IS_WATER).m_206428_(BiomeTags.f_207603_).m_206428_(BiomeTags.f_207605_);
        this.m_206424_(Tags.Biomes.IS_MOUNTAIN).m_206428_(Tags.Biomes.IS_PEAK).m_206428_(Tags.Biomes.IS_SLOPE);
        this.m_206424_(Tags.Biomes.IS_UNDERGROUND).m_206428_(Tags.Biomes.IS_CAVE);
    }

    @SafeVarargs
    private void tag(ResourceKey<Biome> biome, TagKey<Biome> ... tags) {
        for (TagKey<Biome> key : tags) {
            this.m_206424_(key).m_255204_(biome);
        }
    }

    public String m_6055_() {
        return "Forge Biome Tags";
    }
}

