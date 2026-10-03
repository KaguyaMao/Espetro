/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonPrimitive
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record BlockComponent(ComponentRole crole) implements RecipeComponent<Block>
{
    public static final RecipeComponent<Block> INPUT = new BlockComponent(ComponentRole.INPUT);
    public static final RecipeComponent<Block> OUTPUT = new BlockComponent(ComponentRole.OUTPUT);
    public static final RecipeComponent<Block> BLOCK = new BlockComponent(ComponentRole.OTHER);

    @Override
    public ComponentRole role() {
        return this.crole;
    }

    @Override
    public String componentType() {
        return "block";
    }

    @Override
    public Class<?> componentClass() {
        return Block.class;
    }

    public JsonPrimitive write(RecipeJS recipe, Block value) {
        return new JsonPrimitive(String.valueOf(RegistryInfo.BLOCK.getId(value)));
    }

    @Override
    public Block read(RecipeJS recipe, Object from) {
        if (from instanceof Block) {
            Block b = (Block)from;
            return b;
        }
        if (from instanceof BlockState) {
            BlockState s = (BlockState)from;
            return s.m_60734_();
        }
        if (from instanceof JsonPrimitive) {
            JsonPrimitive json = (JsonPrimitive)from;
            return UtilsJS.parseBlockState(json.getAsString()).m_60734_();
        }
        return UtilsJS.parseBlockState(String.valueOf(from)).m_60734_();
    }

    @Override
    public boolean isInput(RecipeJS recipe, Block value, ReplacementMatch match) {
        BlockStatePredicate m2;
        return this.crole.isInput() && match instanceof BlockStatePredicate && (m2 = (BlockStatePredicate)match).testBlock(value);
    }

    @Override
    public boolean isOutput(RecipeJS recipe, Block value, ReplacementMatch match) {
        BlockStatePredicate m2;
        return this.crole.isOutput() && match instanceof BlockStatePredicate && (m2 = (BlockStatePredicate)match).testBlock(value);
    }

    @Override
    public String checkEmpty(RecipeKey<Block> key, Block value) {
        if (value == Blocks.f_50016_) {
            return "Block '" + key.name + "' can't be empty!";
        }
        return "";
    }

    @Override
    public String toString() {
        return this.componentType();
    }
}

