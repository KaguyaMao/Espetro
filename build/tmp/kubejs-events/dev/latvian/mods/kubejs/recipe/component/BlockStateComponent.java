/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.commands.arguments.blocks.BlockStateParser
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.Map;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record BlockStateComponent(ComponentRole crole, boolean preferObjectForm) implements RecipeComponent<BlockState>
{
    public static final RecipeComponent<BlockState> INPUT = new BlockStateComponent(ComponentRole.INPUT, true);
    public static final RecipeComponent<BlockState> OUTPUT = new BlockStateComponent(ComponentRole.OUTPUT, true);
    public static final RecipeComponent<BlockState> BLOCK = new BlockStateComponent(ComponentRole.OTHER, true);
    public static final RecipeComponent<BlockState> INPUT_STRING = new BlockStateComponent(ComponentRole.INPUT, false);
    public static final RecipeComponent<BlockState> OUTPUT_STRING = new BlockStateComponent(ComponentRole.OUTPUT, false);
    public static final RecipeComponent<BlockState> BLOCK_STRING = new BlockStateComponent(ComponentRole.OTHER, false);

    @Override
    public ComponentRole role() {
        return this.crole;
    }

    @Override
    public String componentType() {
        return "block_state";
    }

    @Override
    public Class<?> componentClass() {
        return BlockState.class;
    }

    @Override
    public JsonElement write(RecipeJS recipe, BlockState value) {
        if (this.preferObjectForm) {
            return (JsonElement)BlockState.f_61039_.encode((Object)value, (DynamicOps)JsonOps.INSTANCE, (Object)new JsonObject()).getOrThrow(true, message -> {
                throw new RecipeExceptionJS("Failed to write blockstate to object form: " + message);
            });
        }
        return new JsonPrimitive(BlockStateParser.m_116769_((BlockState)value));
    }

    @Override
    public BlockState read(RecipeJS recipe, Object from) {
        if (from instanceof BlockState) {
            BlockState s = (BlockState)from;
            return s;
        }
        if (from instanceof Block) {
            Block b = (Block)from;
            return b.m_49966_();
        }
        if (from instanceof JsonPrimitive) {
            JsonPrimitive json = (JsonPrimitive)from;
            return UtilsJS.parseBlockState(json.getAsString());
        }
        Map<?, ?> map = MapJS.of(from);
        if (map == null) {
            return UtilsJS.parseBlockState(String.valueOf(from));
        }
        return (BlockState)BlockState.f_61039_.parse((DynamicOps)JsonOps.INSTANCE, (Object)JsonIO.GSON.toJsonTree(from)).getOrThrow(true, message -> {
            throw new RecipeExceptionJS("Failed to parse blockstate: " + message);
        });
    }

    @Override
    public boolean isInput(RecipeJS recipe, BlockState value, ReplacementMatch match) {
        BlockStatePredicate m2;
        return this.crole.isInput() && match instanceof BlockStatePredicate && (m2 = (BlockStatePredicate)match).test(value);
    }

    @Override
    public boolean isOutput(RecipeJS recipe, BlockState value, ReplacementMatch match) {
        BlockStatePredicate m2;
        return this.crole.isOutput() && match instanceof BlockStatePredicate && (m2 = (BlockStatePredicate)match).test(value);
    }

    @Override
    public String checkEmpty(RecipeKey<BlockState> key, BlockState value) {
        if (value.m_60734_() == Blocks.f_50016_) {
            return "Block '" + key.name + "' can't be empty!";
        }
        return "";
    }

    @Override
    public String toString() {
        return this.componentType();
    }
}

