/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.tags.IntrinsicHolderTagsProvider$IntrinsicTagAppender
 *  net.minecraft.data.tags.ItemTagsProvider
 *  net.minecraft.data.tags.TagsProvider$TagLookup
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.block.Block
 */
package net.minecraftforge.common.data;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public final class ForgeItemTagsProvider
extends ItemTagsProvider {
    public ForgeItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, "forge", existingFileHelper);
    }

    public void m_6577_(HolderLookup.Provider lookupProvider) {
        this.m_206421_(Tags.Blocks.BARRELS, Tags.Items.BARRELS);
        this.m_206421_(Tags.Blocks.BARRELS_WOODEN, Tags.Items.BARRELS_WOODEN);
        this.m_206424_(Tags.Items.BONES).m_255245_((Object)Items.f_42500_);
        this.m_206421_(Tags.Blocks.BOOKSHELVES, Tags.Items.BOOKSHELVES);
        this.m_206421_(Tags.Blocks.CHESTS, Tags.Items.CHESTS);
        this.m_206421_(Tags.Blocks.CHESTS_ENDER, Tags.Items.CHESTS_ENDER);
        this.m_206421_(Tags.Blocks.CHESTS_TRAPPED, Tags.Items.CHESTS_TRAPPED);
        this.m_206421_(Tags.Blocks.CHESTS_WOODEN, Tags.Items.CHESTS_WOODEN);
        this.m_206421_(Tags.Blocks.COBBLESTONE, Tags.Items.COBBLESTONE);
        this.m_206421_(Tags.Blocks.COBBLESTONE_NORMAL, Tags.Items.COBBLESTONE_NORMAL);
        this.m_206421_(Tags.Blocks.COBBLESTONE_INFESTED, Tags.Items.COBBLESTONE_INFESTED);
        this.m_206421_(Tags.Blocks.COBBLESTONE_MOSSY, Tags.Items.COBBLESTONE_MOSSY);
        this.m_206421_(Tags.Blocks.COBBLESTONE_DEEPSLATE, Tags.Items.COBBLESTONE_DEEPSLATE);
        this.m_206424_(Tags.Items.CROPS).addTags(new TagKey[]{Tags.Items.CROPS_BEETROOT, Tags.Items.CROPS_CARROT, Tags.Items.CROPS_NETHER_WART, Tags.Items.CROPS_POTATO, Tags.Items.CROPS_WHEAT});
        this.m_206424_(Tags.Items.CROPS_BEETROOT).m_255245_((Object)Items.f_42732_);
        this.m_206424_(Tags.Items.CROPS_CARROT).m_255245_((Object)Items.f_42619_);
        this.m_206424_(Tags.Items.CROPS_NETHER_WART).m_255245_((Object)Items.f_42588_);
        this.m_206424_(Tags.Items.CROPS_POTATO).m_255245_((Object)Items.f_42620_);
        this.m_206424_(Tags.Items.CROPS_WHEAT).m_255245_((Object)Items.f_42405_);
        this.m_206424_(Tags.Items.DUSTS).addTags(new TagKey[]{Tags.Items.DUSTS_GLOWSTONE, Tags.Items.DUSTS_PRISMARINE, Tags.Items.DUSTS_REDSTONE});
        this.m_206424_(Tags.Items.DUSTS_GLOWSTONE).m_255245_((Object)Items.f_42525_);
        this.m_206424_(Tags.Items.DUSTS_PRISMARINE).m_255245_((Object)Items.f_42695_);
        this.m_206424_(Tags.Items.DUSTS_REDSTONE).m_255245_((Object)Items.f_42451_);
        this.addColored(arg_0 -> ForgeItemTagsProvider.lambda$addTags$0(this.m_206424_(Tags.Items.DYES), arg_0), Tags.Items.DYES, "{color}_dye");
        this.m_206424_(Tags.Items.EGGS).m_255245_((Object)Items.f_42521_);
        this.m_206424_(Tags.Items.ENCHANTING_FUELS).m_206428_(Tags.Items.GEMS_LAPIS);
        this.m_206421_(Tags.Blocks.END_STONES, Tags.Items.END_STONES);
        this.m_206424_(Tags.Items.ENDER_PEARLS).m_255245_((Object)Items.f_42584_);
        this.m_206424_(Tags.Items.FEATHERS).m_255245_((Object)Items.f_42402_);
        this.m_206421_(Tags.Blocks.FENCE_GATES, Tags.Items.FENCE_GATES);
        this.m_206421_(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
        this.m_206421_(Tags.Blocks.FENCES, Tags.Items.FENCES);
        this.m_206421_(Tags.Blocks.FENCES_NETHER_BRICK, Tags.Items.FENCES_NETHER_BRICK);
        this.m_206421_(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
        this.m_206424_(Tags.Items.GEMS).addTags(new TagKey[]{Tags.Items.GEMS_AMETHYST, Tags.Items.GEMS_DIAMOND, Tags.Items.GEMS_EMERALD, Tags.Items.GEMS_LAPIS, Tags.Items.GEMS_PRISMARINE, Tags.Items.GEMS_QUARTZ});
        this.m_206424_(Tags.Items.GEMS_AMETHYST).m_255245_((Object)Items.f_151049_);
        this.m_206424_(Tags.Items.GEMS_DIAMOND).m_255245_((Object)Items.f_42415_);
        this.m_206424_(Tags.Items.GEMS_EMERALD).m_255245_((Object)Items.f_42616_);
        this.m_206424_(Tags.Items.GEMS_LAPIS).m_255245_((Object)Items.f_42534_);
        this.m_206424_(Tags.Items.GEMS_PRISMARINE).m_255245_((Object)Items.f_42696_);
        this.m_206424_(Tags.Items.GEMS_QUARTZ).m_255245_((Object)Items.f_42692_);
        this.m_206421_(Tags.Blocks.GLASS, Tags.Items.GLASS);
        this.m_206421_(Tags.Blocks.GLASS_TINTED, Tags.Items.GLASS_TINTED);
        this.m_206421_(Tags.Blocks.GLASS_SILICA, Tags.Items.GLASS_SILICA);
        this.copyColored(Tags.Blocks.GLASS, Tags.Items.GLASS);
        this.m_206421_(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
        this.copyColored(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
        this.m_206421_(Tags.Blocks.GRAVEL, Tags.Items.GRAVEL);
        this.m_206424_(Tags.Items.GUNPOWDER).m_255245_((Object)Items.f_42403_);
        this.m_206424_(Tags.Items.HEADS).m_255179_((Object[])new Item[]{Items.f_42682_, Items.f_42683_, Items.f_42680_, Items.f_42678_, Items.f_42679_, Items.f_42681_});
        this.m_206424_(Tags.Items.INGOTS).addTags(new TagKey[]{Tags.Items.INGOTS_BRICK, Tags.Items.INGOTS_COPPER, Tags.Items.INGOTS_GOLD, Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_NETHERITE, Tags.Items.INGOTS_NETHER_BRICK});
        this.m_206424_(Tags.Items.INGOTS_BRICK).m_255245_((Object)Items.f_42460_);
        this.m_206424_(Tags.Items.INGOTS_COPPER).m_255245_((Object)Items.f_151052_);
        this.m_206424_(Tags.Items.INGOTS_GOLD).m_255245_((Object)Items.f_42417_);
        this.m_206424_(Tags.Items.INGOTS_IRON).m_255245_((Object)Items.f_42416_);
        this.m_206424_(Tags.Items.INGOTS_NETHERITE).m_255245_((Object)Items.f_42418_);
        this.m_206424_(Tags.Items.INGOTS_NETHER_BRICK).m_255245_((Object)Items.f_42691_);
        this.m_206424_(Tags.Items.LEATHER).m_255245_((Object)Items.f_42454_);
        this.m_206424_(Tags.Items.MUSHROOMS).m_255179_((Object[])new Item[]{Items.f_41952_, Items.f_41953_});
        this.m_206424_(Tags.Items.NETHER_STARS).m_255245_((Object)Items.f_42686_);
        this.m_206421_(Tags.Blocks.NETHERRACK, Tags.Items.NETHERRACK);
        this.m_206424_(Tags.Items.NUGGETS).addTags(new TagKey[]{Tags.Items.NUGGETS_IRON, Tags.Items.NUGGETS_GOLD});
        this.m_206424_(Tags.Items.NUGGETS_IRON).m_255245_((Object)Items.f_42749_);
        this.m_206424_(Tags.Items.NUGGETS_GOLD).m_255245_((Object)Items.f_42587_);
        this.m_206421_(Tags.Blocks.OBSIDIAN, Tags.Items.OBSIDIAN);
        this.m_206421_(Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE, Tags.Items.ORE_BEARING_GROUND_DEEPSLATE);
        this.m_206421_(Tags.Blocks.ORE_BEARING_GROUND_NETHERRACK, Tags.Items.ORE_BEARING_GROUND_NETHERRACK);
        this.m_206421_(Tags.Blocks.ORE_BEARING_GROUND_STONE, Tags.Items.ORE_BEARING_GROUND_STONE);
        this.m_206421_(Tags.Blocks.ORE_RATES_DENSE, Tags.Items.ORE_RATES_DENSE);
        this.m_206421_(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Items.ORE_RATES_SINGULAR);
        this.m_206421_(Tags.Blocks.ORE_RATES_SPARSE, Tags.Items.ORE_RATES_SPARSE);
        this.m_206421_(Tags.Blocks.ORES, Tags.Items.ORES);
        this.m_206421_(Tags.Blocks.ORES_COAL, Tags.Items.ORES_COAL);
        this.m_206421_(Tags.Blocks.ORES_COPPER, Tags.Items.ORES_COPPER);
        this.m_206421_(Tags.Blocks.ORES_DIAMOND, Tags.Items.ORES_DIAMOND);
        this.m_206421_(Tags.Blocks.ORES_EMERALD, Tags.Items.ORES_EMERALD);
        this.m_206421_(Tags.Blocks.ORES_GOLD, Tags.Items.ORES_GOLD);
        this.m_206421_(Tags.Blocks.ORES_IRON, Tags.Items.ORES_IRON);
        this.m_206421_(Tags.Blocks.ORES_LAPIS, Tags.Items.ORES_LAPIS);
        this.m_206421_(Tags.Blocks.ORES_QUARTZ, Tags.Items.ORES_QUARTZ);
        this.m_206421_(Tags.Blocks.ORES_REDSTONE, Tags.Items.ORES_REDSTONE);
        this.m_206421_(Tags.Blocks.ORES_NETHERITE_SCRAP, Tags.Items.ORES_NETHERITE_SCRAP);
        this.m_206421_(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE);
        this.m_206421_(Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK);
        this.m_206421_(Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE);
        this.m_206424_(Tags.Items.RAW_MATERIALS).addTags(new TagKey[]{Tags.Items.RAW_MATERIALS_COPPER, Tags.Items.RAW_MATERIALS_GOLD, Tags.Items.RAW_MATERIALS_IRON});
        this.m_206424_(Tags.Items.RAW_MATERIALS_COPPER).m_255245_((Object)Items.f_151051_);
        this.m_206424_(Tags.Items.RAW_MATERIALS_GOLD).m_255245_((Object)Items.f_151053_);
        this.m_206424_(Tags.Items.RAW_MATERIALS_IRON).m_255245_((Object)Items.f_151050_);
        this.m_206424_(Tags.Items.RODS).addTags(new TagKey[]{Tags.Items.RODS_BLAZE, Tags.Items.RODS_WOODEN});
        this.m_206424_(Tags.Items.RODS_BLAZE).m_255245_((Object)Items.f_42585_);
        this.m_206424_(Tags.Items.RODS_WOODEN).m_255245_((Object)Items.f_42398_);
        this.m_206421_(Tags.Blocks.SAND, Tags.Items.SAND);
        this.m_206421_(Tags.Blocks.SAND_COLORLESS, Tags.Items.SAND_COLORLESS);
        this.m_206421_(Tags.Blocks.SAND_RED, Tags.Items.SAND_RED);
        this.m_206421_(Tags.Blocks.SANDSTONE, Tags.Items.SANDSTONE);
        this.m_206424_(Tags.Items.SEEDS).addTags(new TagKey[]{Tags.Items.SEEDS_BEETROOT, Tags.Items.SEEDS_MELON, Tags.Items.SEEDS_PUMPKIN, Tags.Items.SEEDS_WHEAT});
        this.m_206424_(Tags.Items.SEEDS_BEETROOT).m_255245_((Object)Items.f_42733_);
        this.m_206424_(Tags.Items.SEEDS_MELON).m_255245_((Object)Items.f_42578_);
        this.m_206424_(Tags.Items.SEEDS_PUMPKIN).m_255245_((Object)Items.f_42577_);
        this.m_206424_(Tags.Items.SEEDS_WHEAT).m_255245_((Object)Items.f_42404_);
        this.m_206424_(Tags.Items.SHEARS).m_255245_((Object)Items.f_42574_);
        this.m_206424_(Tags.Items.SLIMEBALLS).m_255245_((Object)Items.f_42518_);
        this.m_206421_(Tags.Blocks.STAINED_GLASS, Tags.Items.STAINED_GLASS);
        this.m_206421_(Tags.Blocks.STAINED_GLASS_PANES, Tags.Items.STAINED_GLASS_PANES);
        this.m_206421_(Tags.Blocks.STONE, Tags.Items.STONE);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_AMETHYST, Tags.Items.STORAGE_BLOCKS_AMETHYST);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_COAL, Tags.Items.STORAGE_BLOCKS_COAL);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_COPPER, Tags.Items.STORAGE_BLOCKS_COPPER);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_DIAMOND, Tags.Items.STORAGE_BLOCKS_DIAMOND);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_EMERALD, Tags.Items.STORAGE_BLOCKS_EMERALD);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_IRON, Tags.Items.STORAGE_BLOCKS_IRON);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_LAPIS, Tags.Items.STORAGE_BLOCKS_LAPIS);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_QUARTZ, Tags.Items.STORAGE_BLOCKS_QUARTZ);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_REDSTONE, Tags.Items.STORAGE_BLOCKS_REDSTONE);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER, Tags.Items.STORAGE_BLOCKS_RAW_COPPER);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD, Tags.Items.STORAGE_BLOCKS_RAW_GOLD);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_RAW_IRON, Tags.Items.STORAGE_BLOCKS_RAW_IRON);
        this.m_206421_(Tags.Blocks.STORAGE_BLOCKS_NETHERITE, Tags.Items.STORAGE_BLOCKS_NETHERITE);
        this.m_206424_(Tags.Items.STRING).m_255245_((Object)Items.f_42401_);
        this.m_206424_(Tags.Items.TOOLS_SHIELDS).m_255245_((Object)Items.f_42740_);
        this.m_206424_(Tags.Items.TOOLS_BOWS).m_255245_((Object)Items.f_42411_);
        this.m_206424_(Tags.Items.TOOLS_CROSSBOWS).m_255245_((Object)Items.f_42717_);
        this.m_206424_(Tags.Items.TOOLS_FISHING_RODS).m_255245_((Object)Items.f_42523_);
        this.m_206424_(Tags.Items.TOOLS_TRIDENTS).m_255245_((Object)Items.f_42713_);
        this.m_206424_(Tags.Items.TOOLS).addTags(new TagKey[]{ItemTags.f_271388_, ItemTags.f_271207_, ItemTags.f_271360_, ItemTags.f_271138_, ItemTags.f_271298_}).addTags(new TagKey[]{Tags.Items.TOOLS_SHIELDS, Tags.Items.TOOLS_BOWS, Tags.Items.TOOLS_CROSSBOWS, Tags.Items.TOOLS_FISHING_RODS, Tags.Items.TOOLS_TRIDENTS});
        this.m_206424_(Tags.Items.ARMORS_HELMETS).m_255179_((Object[])new Item[]{Items.f_42407_, Items.f_42354_, Items.f_42464_, Items.f_42468_, Items.f_42476_, Items.f_42472_, Items.f_42480_});
        this.m_206424_(Tags.Items.ARMORS_CHESTPLATES).m_255179_((Object[])new Item[]{Items.f_42408_, Items.f_42465_, Items.f_42469_, Items.f_42477_, Items.f_42473_, Items.f_42481_});
        this.m_206424_(Tags.Items.ARMORS_LEGGINGS).m_255179_((Object[])new Item[]{Items.f_42462_, Items.f_42466_, Items.f_42470_, Items.f_42478_, Items.f_42474_, Items.f_42482_});
        this.m_206424_(Tags.Items.ARMORS_BOOTS).m_255179_((Object[])new Item[]{Items.f_42463_, Items.f_42467_, Items.f_42471_, Items.f_42479_, Items.f_42475_, Items.f_42483_});
        this.m_206424_(Tags.Items.ARMORS).addTags(new TagKey[]{Tags.Items.ARMORS_HELMETS, Tags.Items.ARMORS_CHESTPLATES, Tags.Items.ARMORS_LEGGINGS, Tags.Items.ARMORS_BOOTS});
    }

    private void addColored(Consumer<TagKey<Item>> consumer, TagKey<Item> group, String pattern) {
        String prefix = group.f_203868_().m_135815_().toUpperCase(Locale.ENGLISH) + "_";
        for (DyeColor color : DyeColor.values()) {
            ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}", color.m_41065_()));
            TagKey<Item> tag = this.getForgeItemTag(prefix + color.m_41065_());
            Item item = ForgeRegistries.ITEMS.getValue(key);
            if (item == null || item == Items.f_41852_) {
                throw new IllegalStateException("Unknown vanilla item: " + key.toString());
            }
            this.m_206424_(tag).m_255245_((Object)item);
            consumer.accept(tag);
        }
    }

    private void copyColored(TagKey<Block> blockGroup, TagKey<Item> itemGroup) {
        String blockPre = blockGroup.f_203868_().m_135815_().toUpperCase(Locale.ENGLISH) + "_";
        String itemPre = itemGroup.f_203868_().m_135815_().toUpperCase(Locale.ENGLISH) + "_";
        for (DyeColor color : DyeColor.values()) {
            TagKey<Block> from = this.getForgeBlockTag(blockPre + color.m_41065_());
            TagKey<Item> to = this.getForgeItemTag(itemPre + color.m_41065_());
            this.m_206421_(from, to);
        }
        this.m_206421_(this.getForgeBlockTag(blockPre + "colorless"), this.getForgeItemTag(itemPre + "colorless"));
    }

    private TagKey<Block> getForgeBlockTag(String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey)Tags.Blocks.class.getDeclaredField(name).get(null);
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
        }
    }

    private TagKey<Item> getForgeItemTag(String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey)Tags.Items.class.getDeclaredField(name).get(null);
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(Tags.Items.class.getName() + " is missing tag name: " + name);
        }
    }

    public String m_6055_() {
        return "Forge Item Tags";
    }

    private static /* synthetic */ void lambda$addTags$0(IntrinsicHolderTagsProvider.IntrinsicTagAppender rec$, TagKey xva$0) {
        rec$.addTags(new TagKey[]{xva$0});
    }
}

