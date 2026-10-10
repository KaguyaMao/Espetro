/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.tags.IntrinsicHolderTagsProvider$IntrinsicTagAppender
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.data;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class ForgeBlockTagsProvider
extends BlockTagsProvider {
    public ForgeBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    public void m_6577_(HolderLookup.Provider p_256380_) {
        this.m_206424_(Tags.Blocks.BARRELS).m_206428_(Tags.Blocks.BARRELS_WOODEN);
        this.m_206424_(Tags.Blocks.BARRELS_WOODEN).m_255245_((Object)Blocks.f_50618_);
        this.m_206424_(Tags.Blocks.BOOKSHELVES).m_255245_((Object)Blocks.f_50078_);
        this.m_206424_(Tags.Blocks.CHESTS).addTags(new TagKey[]{Tags.Blocks.CHESTS_ENDER, Tags.Blocks.CHESTS_TRAPPED, Tags.Blocks.CHESTS_WOODEN});
        this.m_206424_(Tags.Blocks.CHESTS_ENDER).m_255245_((Object)Blocks.f_50265_);
        this.m_206424_(Tags.Blocks.CHESTS_TRAPPED).m_255245_((Object)Blocks.f_50325_);
        this.m_206424_(Tags.Blocks.CHESTS_WOODEN).m_255179_((Object[])new Block[]{Blocks.f_50087_, Blocks.f_50325_});
        this.m_206424_(Tags.Blocks.CHORUS_ADDITIONALLY_GROWS_ON).addTags(new TagKey[]{Tags.Blocks.END_STONES});
        this.m_206424_(Tags.Blocks.COBBLESTONE).addTags(new TagKey[]{Tags.Blocks.COBBLESTONE_NORMAL, Tags.Blocks.COBBLESTONE_INFESTED, Tags.Blocks.COBBLESTONE_MOSSY, Tags.Blocks.COBBLESTONE_DEEPSLATE});
        this.m_206424_(Tags.Blocks.COBBLESTONE_NORMAL).m_255245_((Object)Blocks.f_50652_);
        this.m_206424_(Tags.Blocks.COBBLESTONE_INFESTED).m_255245_((Object)Blocks.f_50227_);
        this.m_206424_(Tags.Blocks.COBBLESTONE_MOSSY).m_255245_((Object)Blocks.f_50079_);
        this.m_206424_(Tags.Blocks.COBBLESTONE_DEEPSLATE).m_255245_((Object)Blocks.f_152551_);
        this.m_206424_(Tags.Blocks.END_STONES).m_255245_((Object)Blocks.f_50259_);
        this.m_206424_(Tags.Blocks.ENDERMAN_PLACE_ON_BLACKLIST);
        this.m_206424_(Tags.Blocks.FENCE_GATES).addTags(new TagKey[]{Tags.Blocks.FENCE_GATES_WOODEN});
        this.m_206424_(Tags.Blocks.FENCE_GATES_WOODEN).m_255179_((Object[])new Block[]{Blocks.f_50192_, Blocks.f_50474_, Blocks.f_50475_, Blocks.f_50476_, Blocks.f_50477_, Blocks.f_50478_, Blocks.f_50665_, Blocks.f_50666_, Blocks.f_220850_, Blocks.f_244313_, Blocks.f_271274_});
        this.m_206424_(Tags.Blocks.FENCES).addTags(new TagKey[]{Tags.Blocks.FENCES_NETHER_BRICK, Tags.Blocks.FENCES_WOODEN});
        this.m_206424_(Tags.Blocks.FENCES_NETHER_BRICK).m_255245_((Object)Blocks.f_50198_);
        this.m_206424_(Tags.Blocks.FENCES_WOODEN).m_206428_(BlockTags.f_13098_);
        this.m_206424_(Tags.Blocks.GLASS).addTags(new TagKey[]{Tags.Blocks.GLASS_COLORLESS, Tags.Blocks.STAINED_GLASS, Tags.Blocks.GLASS_TINTED});
        this.m_206424_(Tags.Blocks.GLASS_COLORLESS).m_255245_((Object)Blocks.f_50058_);
        this.m_206424_(Tags.Blocks.GLASS_SILICA).m_255179_((Object[])new Block[]{Blocks.f_50058_, Blocks.f_50215_, Blocks.f_50211_, Blocks.f_50212_, Blocks.f_50209_, Blocks.f_50207_, Blocks.f_50213_, Blocks.f_50203_, Blocks.f_50208_, Blocks.f_50205_, Blocks.f_50202_, Blocks.f_50148_, Blocks.f_50206_, Blocks.f_50210_, Blocks.f_50214_, Blocks.f_50147_, Blocks.f_50204_});
        this.m_206424_(Tags.Blocks.GLASS_TINTED).m_255245_((Object)Blocks.f_152498_);
        this.addColored(arg_0 -> ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.m_206424_(Tags.Blocks.STAINED_GLASS)).m_255245_(arg_0), Tags.Blocks.GLASS, "{color}_stained_glass");
        this.m_206424_(Tags.Blocks.GLASS_PANES).addTags(new TagKey[]{Tags.Blocks.GLASS_PANES_COLORLESS, Tags.Blocks.STAINED_GLASS_PANES});
        this.m_206424_(Tags.Blocks.GLASS_PANES_COLORLESS).m_255245_((Object)Blocks.f_50185_);
        this.addColored(arg_0 -> ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.m_206424_(Tags.Blocks.STAINED_GLASS_PANES)).m_255245_(arg_0), Tags.Blocks.GLASS_PANES, "{color}_stained_glass_pane");
        this.m_206424_(Tags.Blocks.GRAVEL).m_255245_((Object)Blocks.f_49994_);
        this.m_206424_(Tags.Blocks.NETHERRACK).m_255245_((Object)Blocks.f_50134_);
        this.m_206424_(Tags.Blocks.OBSIDIAN).m_255245_((Object)Blocks.f_50080_);
        this.m_206424_(Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE).m_255245_((Object)Blocks.f_152550_);
        this.m_206424_(Tags.Blocks.ORE_BEARING_GROUND_NETHERRACK).m_255245_((Object)Blocks.f_50134_);
        this.m_206424_(Tags.Blocks.ORE_BEARING_GROUND_STONE).m_255245_((Object)Blocks.f_50069_);
        this.m_206424_(Tags.Blocks.ORE_RATES_DENSE).m_255179_((Object[])new Block[]{Blocks.f_152505_, Blocks.f_152506_, Blocks.f_152472_, Blocks.f_152473_, Blocks.f_50059_, Blocks.f_50173_});
        this.m_206424_(Tags.Blocks.ORE_RATES_SINGULAR).m_255179_((Object[])new Block[]{Blocks.f_50722_, Blocks.f_49997_, Blocks.f_152469_, Blocks.f_152474_, Blocks.f_152479_, Blocks.f_152467_, Blocks.f_152468_, Blocks.f_50089_, Blocks.f_50264_, Blocks.f_49995_, Blocks.f_49996_, Blocks.f_50331_});
        this.m_206424_(Tags.Blocks.ORE_RATES_SPARSE).m_255245_((Object)Blocks.f_49998_);
        this.m_206424_(Tags.Blocks.ORES).addTags(new TagKey[]{Tags.Blocks.ORES_COAL, Tags.Blocks.ORES_COPPER, Tags.Blocks.ORES_DIAMOND, Tags.Blocks.ORES_EMERALD, Tags.Blocks.ORES_GOLD, Tags.Blocks.ORES_IRON, Tags.Blocks.ORES_LAPIS, Tags.Blocks.ORES_REDSTONE, Tags.Blocks.ORES_QUARTZ, Tags.Blocks.ORES_NETHERITE_SCRAP});
        this.m_206424_(Tags.Blocks.ORES_COAL).m_206428_(BlockTags.f_144262_);
        this.m_206424_(Tags.Blocks.ORES_COPPER).m_206428_(BlockTags.f_144264_);
        this.m_206424_(Tags.Blocks.ORES_DIAMOND).m_206428_(BlockTags.f_144259_);
        this.m_206424_(Tags.Blocks.ORES_EMERALD).m_206428_(BlockTags.f_144263_);
        this.m_206424_(Tags.Blocks.ORES_GOLD).m_206428_(BlockTags.f_13043_);
        this.m_206424_(Tags.Blocks.ORES_IRON).m_206428_(BlockTags.f_144258_);
        this.m_206424_(Tags.Blocks.ORES_LAPIS).m_206428_(BlockTags.f_144261_);
        this.m_206424_(Tags.Blocks.ORES_QUARTZ).m_255245_((Object)Blocks.f_50331_);
        this.m_206424_(Tags.Blocks.ORES_REDSTONE).m_206428_(BlockTags.f_144260_);
        this.m_206424_(Tags.Blocks.ORES_NETHERITE_SCRAP).m_255245_((Object)Blocks.f_50722_);
        this.m_206424_(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).m_255179_((Object[])new Block[]{Blocks.f_152469_, Blocks.f_152506_, Blocks.f_152474_, Blocks.f_152479_, Blocks.f_152467_, Blocks.f_152468_, Blocks.f_152472_, Blocks.f_152473_});
        this.m_206424_(Tags.Blocks.ORES_IN_GROUND_NETHERRACK).m_255179_((Object[])new Block[]{Blocks.f_49998_, Blocks.f_50331_});
        this.m_206424_(Tags.Blocks.ORES_IN_GROUND_STONE).m_255179_((Object[])new Block[]{Blocks.f_49997_, Blocks.f_152505_, Blocks.f_50089_, Blocks.f_50264_, Blocks.f_49995_, Blocks.f_49996_, Blocks.f_50059_, Blocks.f_50173_});
        this.m_206424_(Tags.Blocks.SAND).addTags(new TagKey[]{Tags.Blocks.SAND_COLORLESS, Tags.Blocks.SAND_RED});
        this.m_206424_(Tags.Blocks.SAND_COLORLESS).m_255245_((Object)Blocks.f_49992_);
        this.m_206424_(Tags.Blocks.SAND_RED).m_255245_((Object)Blocks.f_49993_);
        this.m_206424_(Tags.Blocks.SANDSTONE).m_255179_((Object[])new Block[]{Blocks.f_50062_, Blocks.f_50064_, Blocks.f_50063_, Blocks.f_50471_, Blocks.f_50394_, Blocks.f_50396_, Blocks.f_50395_, Blocks.f_50473_});
        this.m_206424_(Tags.Blocks.STONE).m_255179_((Object[])new Block[]{Blocks.f_50334_, Blocks.f_50228_, Blocks.f_50122_, Blocks.f_50226_, Blocks.f_50069_, Blocks.f_50387_, Blocks.f_50281_, Blocks.f_50175_, Blocks.f_152550_, Blocks.f_152555_, Blocks.f_152596_, Blocks.f_152496_});
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS).addTags(new TagKey[]{Tags.Blocks.STORAGE_BLOCKS_AMETHYST, Tags.Blocks.STORAGE_BLOCKS_COAL, Tags.Blocks.STORAGE_BLOCKS_COPPER, Tags.Blocks.STORAGE_BLOCKS_DIAMOND, Tags.Blocks.STORAGE_BLOCKS_EMERALD, Tags.Blocks.STORAGE_BLOCKS_GOLD, Tags.Blocks.STORAGE_BLOCKS_IRON, Tags.Blocks.STORAGE_BLOCKS_LAPIS, Tags.Blocks.STORAGE_BLOCKS_QUARTZ, Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER, Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD, Tags.Blocks.STORAGE_BLOCKS_RAW_IRON, Tags.Blocks.STORAGE_BLOCKS_REDSTONE, Tags.Blocks.STORAGE_BLOCKS_NETHERITE});
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_AMETHYST).m_255245_((Object)Blocks.f_152490_);
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_COAL).m_255245_((Object)Blocks.f_50353_);
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_COPPER).m_255245_((Object)Blocks.f_152504_);
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_DIAMOND).m_255245_((Object)Blocks.f_50090_);
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_EMERALD).m_255245_((Object)Blocks.f_50268_);
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_GOLD).m_255245_((Object)Blocks.f_50074_);
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_IRON).m_255245_((Object)Blocks.f_50075_);
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_LAPIS).m_255245_((Object)Blocks.f_50060_);
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_QUARTZ).m_255245_((Object)Blocks.f_50333_);
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER).m_255245_((Object)Blocks.f_152599_);
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD).m_255245_((Object)Blocks.f_152600_);
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_RAW_IRON).m_255245_((Object)Blocks.f_152598_);
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_REDSTONE).m_255245_((Object)Blocks.f_50330_);
        this.m_206424_(Tags.Blocks.STORAGE_BLOCKS_NETHERITE).m_255245_((Object)Blocks.f_50721_);
    }

    private void addColored(Consumer<Block> consumer, TagKey<Block> group, String pattern) {
        String prefix = group.f_203868_().m_135815_().toUpperCase(Locale.ENGLISH) + "_";
        for (DyeColor color : DyeColor.values()) {
            ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}", color.m_41065_()));
            TagKey<Block> tag = this.getForgeTag(prefix + color.m_41065_());
            Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null || block == Blocks.f_50016_) {
                throw new IllegalStateException("Unknown vanilla block: " + key.toString());
            }
            this.m_206424_(tag).m_255245_((Object)block);
            consumer.accept(block);
        }
    }

    private TagKey<Block> getForgeTag(String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey)Tags.Blocks.class.getDeclaredField(name).get(null);
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
        }
    }

    public String m_6055_() {
        return "Forge Block Tags";
    }
}

