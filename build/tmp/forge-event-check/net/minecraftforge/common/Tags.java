/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.material.Fluid
 */
package net.minecraftforge.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class Tags {
    public static void init() {
        Blocks.init();
        EntityTypes.init();
        Items.init();
        Fluids.init();
        Biomes.init();
    }

    public static class Blocks {
        public static final TagKey<Block> BARRELS = Blocks.tag("barrels");
        public static final TagKey<Block> BARRELS_WOODEN = Blocks.tag("barrels/wooden");
        public static final TagKey<Block> BOOKSHELVES = Blocks.tag("bookshelves");
        public static final TagKey<Block> CHESTS = Blocks.tag("chests");
        public static final TagKey<Block> CHESTS_ENDER = Blocks.tag("chests/ender");
        public static final TagKey<Block> CHESTS_TRAPPED = Blocks.tag("chests/trapped");
        public static final TagKey<Block> CHESTS_WOODEN = Blocks.tag("chests/wooden");
        public static final TagKey<Block> CHORUS_ADDITIONALLY_GROWS_ON = Blocks.tag("chorus_additionally_grows_on");
        public static final TagKey<Block> COBBLESTONE = Blocks.tag("cobblestone");
        public static final TagKey<Block> COBBLESTONE_NORMAL = Blocks.tag("cobblestone/normal");
        public static final TagKey<Block> COBBLESTONE_INFESTED = Blocks.tag("cobblestone/infested");
        public static final TagKey<Block> COBBLESTONE_MOSSY = Blocks.tag("cobblestone/mossy");
        public static final TagKey<Block> COBBLESTONE_DEEPSLATE = Blocks.tag("cobblestone/deepslate");
        public static final TagKey<Block> END_STONES = Blocks.tag("end_stones");
        public static final TagKey<Block> ENDERMAN_PLACE_ON_BLACKLIST = Blocks.tag("enderman_place_on_blacklist");
        public static final TagKey<Block> FENCE_GATES = Blocks.tag("fence_gates");
        public static final TagKey<Block> FENCE_GATES_WOODEN = Blocks.tag("fence_gates/wooden");
        public static final TagKey<Block> FENCES = Blocks.tag("fences");
        public static final TagKey<Block> FENCES_NETHER_BRICK = Blocks.tag("fences/nether_brick");
        public static final TagKey<Block> FENCES_WOODEN = Blocks.tag("fences/wooden");
        public static final TagKey<Block> GLASS = Blocks.tag("glass");
        public static final TagKey<Block> GLASS_BLACK = Blocks.tag("glass/black");
        public static final TagKey<Block> GLASS_BLUE = Blocks.tag("glass/blue");
        public static final TagKey<Block> GLASS_BROWN = Blocks.tag("glass/brown");
        public static final TagKey<Block> GLASS_COLORLESS = Blocks.tag("glass/colorless");
        public static final TagKey<Block> GLASS_CYAN = Blocks.tag("glass/cyan");
        public static final TagKey<Block> GLASS_GRAY = Blocks.tag("glass/gray");
        public static final TagKey<Block> GLASS_GREEN = Blocks.tag("glass/green");
        public static final TagKey<Block> GLASS_LIGHT_BLUE = Blocks.tag("glass/light_blue");
        public static final TagKey<Block> GLASS_LIGHT_GRAY = Blocks.tag("glass/light_gray");
        public static final TagKey<Block> GLASS_LIME = Blocks.tag("glass/lime");
        public static final TagKey<Block> GLASS_MAGENTA = Blocks.tag("glass/magenta");
        public static final TagKey<Block> GLASS_ORANGE = Blocks.tag("glass/orange");
        public static final TagKey<Block> GLASS_PINK = Blocks.tag("glass/pink");
        public static final TagKey<Block> GLASS_PURPLE = Blocks.tag("glass/purple");
        public static final TagKey<Block> GLASS_RED = Blocks.tag("glass/red");
        public static final TagKey<Block> GLASS_SILICA = Blocks.tag("glass/silica");
        public static final TagKey<Block> GLASS_TINTED = Blocks.tag("glass/tinted");
        public static final TagKey<Block> GLASS_WHITE = Blocks.tag("glass/white");
        public static final TagKey<Block> GLASS_YELLOW = Blocks.tag("glass/yellow");
        public static final TagKey<Block> GLASS_PANES = Blocks.tag("glass_panes");
        public static final TagKey<Block> GLASS_PANES_BLACK = Blocks.tag("glass_panes/black");
        public static final TagKey<Block> GLASS_PANES_BLUE = Blocks.tag("glass_panes/blue");
        public static final TagKey<Block> GLASS_PANES_BROWN = Blocks.tag("glass_panes/brown");
        public static final TagKey<Block> GLASS_PANES_COLORLESS = Blocks.tag("glass_panes/colorless");
        public static final TagKey<Block> GLASS_PANES_CYAN = Blocks.tag("glass_panes/cyan");
        public static final TagKey<Block> GLASS_PANES_GRAY = Blocks.tag("glass_panes/gray");
        public static final TagKey<Block> GLASS_PANES_GREEN = Blocks.tag("glass_panes/green");
        public static final TagKey<Block> GLASS_PANES_LIGHT_BLUE = Blocks.tag("glass_panes/light_blue");
        public static final TagKey<Block> GLASS_PANES_LIGHT_GRAY = Blocks.tag("glass_panes/light_gray");
        public static final TagKey<Block> GLASS_PANES_LIME = Blocks.tag("glass_panes/lime");
        public static final TagKey<Block> GLASS_PANES_MAGENTA = Blocks.tag("glass_panes/magenta");
        public static final TagKey<Block> GLASS_PANES_ORANGE = Blocks.tag("glass_panes/orange");
        public static final TagKey<Block> GLASS_PANES_PINK = Blocks.tag("glass_panes/pink");
        public static final TagKey<Block> GLASS_PANES_PURPLE = Blocks.tag("glass_panes/purple");
        public static final TagKey<Block> GLASS_PANES_RED = Blocks.tag("glass_panes/red");
        public static final TagKey<Block> GLASS_PANES_WHITE = Blocks.tag("glass_panes/white");
        public static final TagKey<Block> GLASS_PANES_YELLOW = Blocks.tag("glass_panes/yellow");
        public static final TagKey<Block> GRAVEL = Blocks.tag("gravel");
        public static final TagKey<Block> NETHERRACK = Blocks.tag("netherrack");
        public static final TagKey<Block> OBSIDIAN = Blocks.tag("obsidian");
        public static final TagKey<Block> ORE_BEARING_GROUND_DEEPSLATE = Blocks.tag("ore_bearing_ground/deepslate");
        public static final TagKey<Block> ORE_BEARING_GROUND_NETHERRACK = Blocks.tag("ore_bearing_ground/netherrack");
        public static final TagKey<Block> ORE_BEARING_GROUND_STONE = Blocks.tag("ore_bearing_ground/stone");
        public static final TagKey<Block> ORE_RATES_DENSE = Blocks.tag("ore_rates/dense");
        public static final TagKey<Block> ORE_RATES_SINGULAR = Blocks.tag("ore_rates/singular");
        public static final TagKey<Block> ORE_RATES_SPARSE = Blocks.tag("ore_rates/sparse");
        public static final TagKey<Block> ORES = Blocks.tag("ores");
        public static final TagKey<Block> ORES_COAL = Blocks.tag("ores/coal");
        public static final TagKey<Block> ORES_COPPER = Blocks.tag("ores/copper");
        public static final TagKey<Block> ORES_DIAMOND = Blocks.tag("ores/diamond");
        public static final TagKey<Block> ORES_EMERALD = Blocks.tag("ores/emerald");
        public static final TagKey<Block> ORES_GOLD = Blocks.tag("ores/gold");
        public static final TagKey<Block> ORES_IRON = Blocks.tag("ores/iron");
        public static final TagKey<Block> ORES_LAPIS = Blocks.tag("ores/lapis");
        public static final TagKey<Block> ORES_NETHERITE_SCRAP = Blocks.tag("ores/netherite_scrap");
        public static final TagKey<Block> ORES_QUARTZ = Blocks.tag("ores/quartz");
        public static final TagKey<Block> ORES_REDSTONE = Blocks.tag("ores/redstone");
        public static final TagKey<Block> ORES_IN_GROUND_DEEPSLATE = Blocks.tag("ores_in_ground/deepslate");
        public static final TagKey<Block> ORES_IN_GROUND_NETHERRACK = Blocks.tag("ores_in_ground/netherrack");
        public static final TagKey<Block> ORES_IN_GROUND_STONE = Blocks.tag("ores_in_ground/stone");
        public static final TagKey<Block> SAND = Blocks.tag("sand");
        public static final TagKey<Block> SAND_COLORLESS = Blocks.tag("sand/colorless");
        public static final TagKey<Block> SAND_RED = Blocks.tag("sand/red");
        public static final TagKey<Block> SANDSTONE = Blocks.tag("sandstone");
        public static final TagKey<Block> STAINED_GLASS = Blocks.tag("stained_glass");
        public static final TagKey<Block> STAINED_GLASS_PANES = Blocks.tag("stained_glass_panes");
        public static final TagKey<Block> STONE = Blocks.tag("stone");
        public static final TagKey<Block> STORAGE_BLOCKS = Blocks.tag("storage_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_AMETHYST = Blocks.tag("storage_blocks/amethyst");
        public static final TagKey<Block> STORAGE_BLOCKS_COAL = Blocks.tag("storage_blocks/coal");
        public static final TagKey<Block> STORAGE_BLOCKS_COPPER = Blocks.tag("storage_blocks/copper");
        public static final TagKey<Block> STORAGE_BLOCKS_DIAMOND = Blocks.tag("storage_blocks/diamond");
        public static final TagKey<Block> STORAGE_BLOCKS_EMERALD = Blocks.tag("storage_blocks/emerald");
        public static final TagKey<Block> STORAGE_BLOCKS_GOLD = Blocks.tag("storage_blocks/gold");
        public static final TagKey<Block> STORAGE_BLOCKS_IRON = Blocks.tag("storage_blocks/iron");
        public static final TagKey<Block> STORAGE_BLOCKS_LAPIS = Blocks.tag("storage_blocks/lapis");
        public static final TagKey<Block> STORAGE_BLOCKS_NETHERITE = Blocks.tag("storage_blocks/netherite");
        public static final TagKey<Block> STORAGE_BLOCKS_QUARTZ = Blocks.tag("storage_blocks/quartz");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_COPPER = Blocks.tag("storage_blocks/raw_copper");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_GOLD = Blocks.tag("storage_blocks/raw_gold");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_IRON = Blocks.tag("storage_blocks/raw_iron");
        public static final TagKey<Block> STORAGE_BLOCKS_REDSTONE = Blocks.tag("storage_blocks/redstone");
        public static final TagKey<Block> NEEDS_WOOD_TOOL = Blocks.tag("needs_wood_tool");
        public static final TagKey<Block> NEEDS_GOLD_TOOL = Blocks.tag("needs_gold_tool");
        public static final TagKey<Block> NEEDS_NETHERITE_TOOL = Blocks.tag("needs_netherite_tool");

        private static void init() {
        }

        private static TagKey<Block> tag(String name) {
            return BlockTags.create((ResourceLocation)new ResourceLocation("forge", name));
        }
    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> BOSSES = EntityTypes.tag("bosses");

        private static void init() {
        }

        private static TagKey<EntityType<?>> tag(String name) {
            return TagKey.m_203882_((ResourceKey)Registries.f_256939_, (ResourceLocation)new ResourceLocation("forge", name));
        }
    }

    public static class Items {
        public static final TagKey<Item> BARRELS = Items.tag("barrels");
        public static final TagKey<Item> BARRELS_WOODEN = Items.tag("barrels/wooden");
        public static final TagKey<Item> BONES = Items.tag("bones");
        public static final TagKey<Item> BOOKSHELVES = Items.tag("bookshelves");
        public static final TagKey<Item> CHESTS = Items.tag("chests");
        public static final TagKey<Item> CHESTS_ENDER = Items.tag("chests/ender");
        public static final TagKey<Item> CHESTS_TRAPPED = Items.tag("chests/trapped");
        public static final TagKey<Item> CHESTS_WOODEN = Items.tag("chests/wooden");
        public static final TagKey<Item> COBBLESTONE = Items.tag("cobblestone");
        public static final TagKey<Item> COBBLESTONE_NORMAL = Items.tag("cobblestone/normal");
        public static final TagKey<Item> COBBLESTONE_INFESTED = Items.tag("cobblestone/infested");
        public static final TagKey<Item> COBBLESTONE_MOSSY = Items.tag("cobblestone/mossy");
        public static final TagKey<Item> COBBLESTONE_DEEPSLATE = Items.tag("cobblestone/deepslate");
        public static final TagKey<Item> CROPS = Items.tag("crops");
        public static final TagKey<Item> CROPS_BEETROOT = Items.tag("crops/beetroot");
        public static final TagKey<Item> CROPS_CARROT = Items.tag("crops/carrot");
        public static final TagKey<Item> CROPS_NETHER_WART = Items.tag("crops/nether_wart");
        public static final TagKey<Item> CROPS_POTATO = Items.tag("crops/potato");
        public static final TagKey<Item> CROPS_WHEAT = Items.tag("crops/wheat");
        public static final TagKey<Item> DUSTS = Items.tag("dusts");
        public static final TagKey<Item> DUSTS_PRISMARINE = Items.tag("dusts/prismarine");
        public static final TagKey<Item> DUSTS_REDSTONE = Items.tag("dusts/redstone");
        public static final TagKey<Item> DUSTS_GLOWSTONE = Items.tag("dusts/glowstone");
        public static final TagKey<Item> DYES = Items.tag("dyes");
        public static final TagKey<Item> DYES_BLACK = DyeColor.BLACK.getTag();
        public static final TagKey<Item> DYES_RED = DyeColor.RED.getTag();
        public static final TagKey<Item> DYES_GREEN = DyeColor.GREEN.getTag();
        public static final TagKey<Item> DYES_BROWN = DyeColor.BROWN.getTag();
        public static final TagKey<Item> DYES_BLUE = DyeColor.BLUE.getTag();
        public static final TagKey<Item> DYES_PURPLE = DyeColor.PURPLE.getTag();
        public static final TagKey<Item> DYES_CYAN = DyeColor.CYAN.getTag();
        public static final TagKey<Item> DYES_LIGHT_GRAY = DyeColor.LIGHT_GRAY.getTag();
        public static final TagKey<Item> DYES_GRAY = DyeColor.GRAY.getTag();
        public static final TagKey<Item> DYES_PINK = DyeColor.PINK.getTag();
        public static final TagKey<Item> DYES_LIME = DyeColor.LIME.getTag();
        public static final TagKey<Item> DYES_YELLOW = DyeColor.YELLOW.getTag();
        public static final TagKey<Item> DYES_LIGHT_BLUE = DyeColor.LIGHT_BLUE.getTag();
        public static final TagKey<Item> DYES_MAGENTA = DyeColor.MAGENTA.getTag();
        public static final TagKey<Item> DYES_ORANGE = DyeColor.ORANGE.getTag();
        public static final TagKey<Item> DYES_WHITE = DyeColor.WHITE.getTag();
        public static final TagKey<Item> EGGS = Items.tag("eggs");
        public static final TagKey<Item> ENCHANTING_FUELS = Items.tag("enchanting_fuels");
        public static final TagKey<Item> END_STONES = Items.tag("end_stones");
        public static final TagKey<Item> ENDER_PEARLS = Items.tag("ender_pearls");
        public static final TagKey<Item> FEATHERS = Items.tag("feathers");
        public static final TagKey<Item> FENCE_GATES = Items.tag("fence_gates");
        public static final TagKey<Item> FENCE_GATES_WOODEN = Items.tag("fence_gates/wooden");
        public static final TagKey<Item> FENCES = Items.tag("fences");
        public static final TagKey<Item> FENCES_NETHER_BRICK = Items.tag("fences/nether_brick");
        public static final TagKey<Item> FENCES_WOODEN = Items.tag("fences/wooden");
        public static final TagKey<Item> GEMS = Items.tag("gems");
        public static final TagKey<Item> GEMS_DIAMOND = Items.tag("gems/diamond");
        public static final TagKey<Item> GEMS_EMERALD = Items.tag("gems/emerald");
        public static final TagKey<Item> GEMS_AMETHYST = Items.tag("gems/amethyst");
        public static final TagKey<Item> GEMS_LAPIS = Items.tag("gems/lapis");
        public static final TagKey<Item> GEMS_PRISMARINE = Items.tag("gems/prismarine");
        public static final TagKey<Item> GEMS_QUARTZ = Items.tag("gems/quartz");
        public static final TagKey<Item> GLASS = Items.tag("glass");
        public static final TagKey<Item> GLASS_BLACK = Items.tag("glass/black");
        public static final TagKey<Item> GLASS_BLUE = Items.tag("glass/blue");
        public static final TagKey<Item> GLASS_BROWN = Items.tag("glass/brown");
        public static final TagKey<Item> GLASS_COLORLESS = Items.tag("glass/colorless");
        public static final TagKey<Item> GLASS_CYAN = Items.tag("glass/cyan");
        public static final TagKey<Item> GLASS_GRAY = Items.tag("glass/gray");
        public static final TagKey<Item> GLASS_GREEN = Items.tag("glass/green");
        public static final TagKey<Item> GLASS_LIGHT_BLUE = Items.tag("glass/light_blue");
        public static final TagKey<Item> GLASS_LIGHT_GRAY = Items.tag("glass/light_gray");
        public static final TagKey<Item> GLASS_LIME = Items.tag("glass/lime");
        public static final TagKey<Item> GLASS_MAGENTA = Items.tag("glass/magenta");
        public static final TagKey<Item> GLASS_ORANGE = Items.tag("glass/orange");
        public static final TagKey<Item> GLASS_PINK = Items.tag("glass/pink");
        public static final TagKey<Item> GLASS_PURPLE = Items.tag("glass/purple");
        public static final TagKey<Item> GLASS_RED = Items.tag("glass/red");
        public static final TagKey<Item> GLASS_SILICA = Items.tag("glass/silica");
        public static final TagKey<Item> GLASS_TINTED = Items.tag("glass/tinted");
        public static final TagKey<Item> GLASS_WHITE = Items.tag("glass/white");
        public static final TagKey<Item> GLASS_YELLOW = Items.tag("glass/yellow");
        public static final TagKey<Item> GLASS_PANES = Items.tag("glass_panes");
        public static final TagKey<Item> GLASS_PANES_BLACK = Items.tag("glass_panes/black");
        public static final TagKey<Item> GLASS_PANES_BLUE = Items.tag("glass_panes/blue");
        public static final TagKey<Item> GLASS_PANES_BROWN = Items.tag("glass_panes/brown");
        public static final TagKey<Item> GLASS_PANES_COLORLESS = Items.tag("glass_panes/colorless");
        public static final TagKey<Item> GLASS_PANES_CYAN = Items.tag("glass_panes/cyan");
        public static final TagKey<Item> GLASS_PANES_GRAY = Items.tag("glass_panes/gray");
        public static final TagKey<Item> GLASS_PANES_GREEN = Items.tag("glass_panes/green");
        public static final TagKey<Item> GLASS_PANES_LIGHT_BLUE = Items.tag("glass_panes/light_blue");
        public static final TagKey<Item> GLASS_PANES_LIGHT_GRAY = Items.tag("glass_panes/light_gray");
        public static final TagKey<Item> GLASS_PANES_LIME = Items.tag("glass_panes/lime");
        public static final TagKey<Item> GLASS_PANES_MAGENTA = Items.tag("glass_panes/magenta");
        public static final TagKey<Item> GLASS_PANES_ORANGE = Items.tag("glass_panes/orange");
        public static final TagKey<Item> GLASS_PANES_PINK = Items.tag("glass_panes/pink");
        public static final TagKey<Item> GLASS_PANES_PURPLE = Items.tag("glass_panes/purple");
        public static final TagKey<Item> GLASS_PANES_RED = Items.tag("glass_panes/red");
        public static final TagKey<Item> GLASS_PANES_WHITE = Items.tag("glass_panes/white");
        public static final TagKey<Item> GLASS_PANES_YELLOW = Items.tag("glass_panes/yellow");
        public static final TagKey<Item> GRAVEL = Items.tag("gravel");
        public static final TagKey<Item> GUNPOWDER = Items.tag("gunpowder");
        public static final TagKey<Item> HEADS = Items.tag("heads");
        public static final TagKey<Item> INGOTS = Items.tag("ingots");
        public static final TagKey<Item> INGOTS_BRICK = Items.tag("ingots/brick");
        public static final TagKey<Item> INGOTS_COPPER = Items.tag("ingots/copper");
        public static final TagKey<Item> INGOTS_GOLD = Items.tag("ingots/gold");
        public static final TagKey<Item> INGOTS_IRON = Items.tag("ingots/iron");
        public static final TagKey<Item> INGOTS_NETHERITE = Items.tag("ingots/netherite");
        public static final TagKey<Item> INGOTS_NETHER_BRICK = Items.tag("ingots/nether_brick");
        public static final TagKey<Item> LEATHER = Items.tag("leather");
        public static final TagKey<Item> MUSHROOMS = Items.tag("mushrooms");
        public static final TagKey<Item> NETHER_STARS = Items.tag("nether_stars");
        public static final TagKey<Item> NETHERRACK = Items.tag("netherrack");
        public static final TagKey<Item> NUGGETS = Items.tag("nuggets");
        public static final TagKey<Item> NUGGETS_GOLD = Items.tag("nuggets/gold");
        public static final TagKey<Item> NUGGETS_IRON = Items.tag("nuggets/iron");
        public static final TagKey<Item> OBSIDIAN = Items.tag("obsidian");
        public static final TagKey<Item> ORE_BEARING_GROUND_DEEPSLATE = Items.tag("ore_bearing_ground/deepslate");
        public static final TagKey<Item> ORE_BEARING_GROUND_NETHERRACK = Items.tag("ore_bearing_ground/netherrack");
        public static final TagKey<Item> ORE_BEARING_GROUND_STONE = Items.tag("ore_bearing_ground/stone");
        public static final TagKey<Item> ORE_RATES_DENSE = Items.tag("ore_rates/dense");
        public static final TagKey<Item> ORE_RATES_SINGULAR = Items.tag("ore_rates/singular");
        public static final TagKey<Item> ORE_RATES_SPARSE = Items.tag("ore_rates/sparse");
        public static final TagKey<Item> ORES = Items.tag("ores");
        public static final TagKey<Item> ORES_COAL = Items.tag("ores/coal");
        public static final TagKey<Item> ORES_COPPER = Items.tag("ores/copper");
        public static final TagKey<Item> ORES_DIAMOND = Items.tag("ores/diamond");
        public static final TagKey<Item> ORES_EMERALD = Items.tag("ores/emerald");
        public static final TagKey<Item> ORES_GOLD = Items.tag("ores/gold");
        public static final TagKey<Item> ORES_IRON = Items.tag("ores/iron");
        public static final TagKey<Item> ORES_LAPIS = Items.tag("ores/lapis");
        public static final TagKey<Item> ORES_NETHERITE_SCRAP = Items.tag("ores/netherite_scrap");
        public static final TagKey<Item> ORES_QUARTZ = Items.tag("ores/quartz");
        public static final TagKey<Item> ORES_REDSTONE = Items.tag("ores/redstone");
        public static final TagKey<Item> ORES_IN_GROUND_DEEPSLATE = Items.tag("ores_in_ground/deepslate");
        public static final TagKey<Item> ORES_IN_GROUND_NETHERRACK = Items.tag("ores_in_ground/netherrack");
        public static final TagKey<Item> ORES_IN_GROUND_STONE = Items.tag("ores_in_ground/stone");
        public static final TagKey<Item> RAW_MATERIALS = Items.tag("raw_materials");
        public static final TagKey<Item> RAW_MATERIALS_COPPER = Items.tag("raw_materials/copper");
        public static final TagKey<Item> RAW_MATERIALS_GOLD = Items.tag("raw_materials/gold");
        public static final TagKey<Item> RAW_MATERIALS_IRON = Items.tag("raw_materials/iron");
        public static final TagKey<Item> RODS = Items.tag("rods");
        public static final TagKey<Item> RODS_BLAZE = Items.tag("rods/blaze");
        public static final TagKey<Item> RODS_WOODEN = Items.tag("rods/wooden");
        public static final TagKey<Item> SAND = Items.tag("sand");
        public static final TagKey<Item> SAND_COLORLESS = Items.tag("sand/colorless");
        public static final TagKey<Item> SAND_RED = Items.tag("sand/red");
        public static final TagKey<Item> SANDSTONE = Items.tag("sandstone");
        public static final TagKey<Item> SEEDS = Items.tag("seeds");
        public static final TagKey<Item> SEEDS_BEETROOT = Items.tag("seeds/beetroot");
        public static final TagKey<Item> SEEDS_MELON = Items.tag("seeds/melon");
        public static final TagKey<Item> SEEDS_PUMPKIN = Items.tag("seeds/pumpkin");
        public static final TagKey<Item> SEEDS_WHEAT = Items.tag("seeds/wheat");
        public static final TagKey<Item> SHEARS = Items.tag("shears");
        public static final TagKey<Item> SLIMEBALLS = Items.tag("slimeballs");
        public static final TagKey<Item> STAINED_GLASS = Items.tag("stained_glass");
        public static final TagKey<Item> STAINED_GLASS_PANES = Items.tag("stained_glass_panes");
        public static final TagKey<Item> STONE = Items.tag("stone");
        public static final TagKey<Item> STORAGE_BLOCKS = Items.tag("storage_blocks");
        public static final TagKey<Item> STORAGE_BLOCKS_AMETHYST = Items.tag("storage_blocks/amethyst");
        public static final TagKey<Item> STORAGE_BLOCKS_COAL = Items.tag("storage_blocks/coal");
        public static final TagKey<Item> STORAGE_BLOCKS_COPPER = Items.tag("storage_blocks/copper");
        public static final TagKey<Item> STORAGE_BLOCKS_DIAMOND = Items.tag("storage_blocks/diamond");
        public static final TagKey<Item> STORAGE_BLOCKS_EMERALD = Items.tag("storage_blocks/emerald");
        public static final TagKey<Item> STORAGE_BLOCKS_GOLD = Items.tag("storage_blocks/gold");
        public static final TagKey<Item> STORAGE_BLOCKS_IRON = Items.tag("storage_blocks/iron");
        public static final TagKey<Item> STORAGE_BLOCKS_LAPIS = Items.tag("storage_blocks/lapis");
        public static final TagKey<Item> STORAGE_BLOCKS_NETHERITE = Items.tag("storage_blocks/netherite");
        public static final TagKey<Item> STORAGE_BLOCKS_QUARTZ = Items.tag("storage_blocks/quartz");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_COPPER = Items.tag("storage_blocks/raw_copper");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_GOLD = Items.tag("storage_blocks/raw_gold");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_IRON = Items.tag("storage_blocks/raw_iron");
        public static final TagKey<Item> STORAGE_BLOCKS_REDSTONE = Items.tag("storage_blocks/redstone");
        public static final TagKey<Item> STRING = Items.tag("string");
        public static final TagKey<Item> TOOLS = Items.tag("tools");
        public static final TagKey<Item> TOOLS_SHIELDS = Items.tag("tools/shields");
        public static final TagKey<Item> TOOLS_BOWS = Items.tag("tools/bows");
        public static final TagKey<Item> TOOLS_CROSSBOWS = Items.tag("tools/crossbows");
        public static final TagKey<Item> TOOLS_FISHING_RODS = Items.tag("tools/fishing_rods");
        public static final TagKey<Item> TOOLS_TRIDENTS = Items.tag("tools/tridents");
        public static final TagKey<Item> ARMORS = Items.tag("armors");
        public static final TagKey<Item> ARMORS_HELMETS = Items.tag("armors/helmets");
        public static final TagKey<Item> ARMORS_CHESTPLATES = Items.tag("armors/chestplates");
        public static final TagKey<Item> ARMORS_LEGGINGS = Items.tag("armors/leggings");
        public static final TagKey<Item> ARMORS_BOOTS = Items.tag("armors/boots");

        private static void init() {
        }

        private static TagKey<Item> tag(String name) {
            return ItemTags.create((ResourceLocation)new ResourceLocation("forge", name));
        }
    }

    public static class Fluids {
        public static final TagKey<Fluid> MILK = Fluids.tag("milk");
        public static final TagKey<Fluid> GASEOUS = Fluids.tag("gaseous");

        private static void init() {
        }

        private static TagKey<Fluid> tag(String name) {
            return FluidTags.create((ResourceLocation)new ResourceLocation("forge", name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> IS_HOT = Biomes.tag("is_hot");
        public static final TagKey<Biome> IS_HOT_OVERWORLD = Biomes.tag("is_hot/overworld");
        public static final TagKey<Biome> IS_HOT_NETHER = Biomes.tag("is_hot/nether");
        public static final TagKey<Biome> IS_HOT_END = Biomes.tag("is_hot/end");
        public static final TagKey<Biome> IS_COLD = Biomes.tag("is_cold");
        public static final TagKey<Biome> IS_COLD_OVERWORLD = Biomes.tag("is_cold/overworld");
        public static final TagKey<Biome> IS_COLD_NETHER = Biomes.tag("is_cold/nether");
        public static final TagKey<Biome> IS_COLD_END = Biomes.tag("is_cold/end");
        public static final TagKey<Biome> IS_SPARSE = Biomes.tag("is_sparse");
        public static final TagKey<Biome> IS_SPARSE_OVERWORLD = Biomes.tag("is_sparse/overworld");
        public static final TagKey<Biome> IS_SPARSE_NETHER = Biomes.tag("is_sparse/nether");
        public static final TagKey<Biome> IS_SPARSE_END = Biomes.tag("is_sparse/end");
        public static final TagKey<Biome> IS_DENSE = Biomes.tag("is_dense");
        public static final TagKey<Biome> IS_DENSE_OVERWORLD = Biomes.tag("is_dense/overworld");
        public static final TagKey<Biome> IS_DENSE_NETHER = Biomes.tag("is_dense/nether");
        public static final TagKey<Biome> IS_DENSE_END = Biomes.tag("is_dense/end");
        public static final TagKey<Biome> IS_WET = Biomes.tag("is_wet");
        public static final TagKey<Biome> IS_WET_OVERWORLD = Biomes.tag("is_wet/overworld");
        public static final TagKey<Biome> IS_WET_NETHER = Biomes.tag("is_wet/nether");
        public static final TagKey<Biome> IS_WET_END = Biomes.tag("is_wet/end");
        public static final TagKey<Biome> IS_DRY = Biomes.tag("is_dry");
        public static final TagKey<Biome> IS_DRY_OVERWORLD = Biomes.tag("is_dry/overworld");
        public static final TagKey<Biome> IS_DRY_NETHER = Biomes.tag("is_dry/nether");
        public static final TagKey<Biome> IS_DRY_END = Biomes.tag("is_dry/end");
        public static final TagKey<Biome> IS_CONIFEROUS = Biomes.tag("is_coniferous");
        public static final TagKey<Biome> IS_SPOOKY = Biomes.tag("is_spooky");
        public static final TagKey<Biome> IS_DEAD = Biomes.tag("is_dead");
        public static final TagKey<Biome> IS_LUSH = Biomes.tag("is_lush");
        public static final TagKey<Biome> IS_MUSHROOM = Biomes.tag("is_mushroom");
        public static final TagKey<Biome> IS_MAGICAL = Biomes.tag("is_magical");
        public static final TagKey<Biome> IS_RARE = Biomes.tag("is_rare");
        public static final TagKey<Biome> IS_PLATEAU = Biomes.tag("is_plateau");
        public static final TagKey<Biome> IS_MODIFIED = Biomes.tag("is_modified");
        public static final TagKey<Biome> IS_WATER = Biomes.tag("is_water");
        public static final TagKey<Biome> IS_DESERT = Biomes.tag("is_desert");
        public static final TagKey<Biome> IS_PLAINS = Biomes.tag("is_plains");
        public static final TagKey<Biome> IS_SWAMP = Biomes.tag("is_swamp");
        public static final TagKey<Biome> IS_SANDY = Biomes.tag("is_sandy");
        public static final TagKey<Biome> IS_SNOWY = Biomes.tag("is_snowy");
        public static final TagKey<Biome> IS_WASTELAND = Biomes.tag("is_wasteland");
        public static final TagKey<Biome> IS_VOID = Biomes.tag("is_void");
        public static final TagKey<Biome> IS_UNDERGROUND = Biomes.tag("is_underground");
        public static final TagKey<Biome> IS_CAVE = Biomes.tag("is_cave");
        public static final TagKey<Biome> IS_PEAK = Biomes.tag("is_peak");
        public static final TagKey<Biome> IS_SLOPE = Biomes.tag("is_slope");
        public static final TagKey<Biome> IS_MOUNTAIN = Biomes.tag("is_mountain");

        private static void init() {
        }

        private static TagKey<Biome> tag(String name) {
            return TagKey.m_203882_((ResourceKey)Registries.f_256952_, (ResourceLocation)new ResourceLocation("forge", name));
        }
    }
}

