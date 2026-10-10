/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.PackOutput$Target
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.ButtonBlock
 *  net.minecraft.world.level.block.CrossCollisionBlock
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.FenceBlock
 *  net.minecraft.world.level.block.FenceGateBlock
 *  net.minecraft.world.level.block.IronBarsBlock
 *  net.minecraft.world.level.block.PipeBlock
 *  net.minecraft.world.level.block.PressurePlateBlock
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.StandingSignBlock
 *  net.minecraft.world.level.block.TrapDoorBlock
 *  net.minecraft.world.level.block.WallBlock
 *  net.minecraft.world.level.block.WallSignBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DoorHingeSide
 *  net.minecraft.world.level.block.state.properties.DoubleBlockHalf
 *  net.minecraft.world.level.block.state.properties.Half
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.SlabType
 *  net.minecraft.world.level.block.state.properties.StairsShape
 *  net.minecraft.world.level.block.state.properties.WallSide
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.VisibleForTesting
 */
package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.IGeneratedBlockState;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

public abstract class BlockStateProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    @VisibleForTesting
    protected final Map<Block, IGeneratedBlockState> registeredBlocks = new LinkedHashMap<Block, IGeneratedBlockState>();
    private final PackOutput output;
    private final String modid;
    private final BlockModelProvider blockModels;
    private final ItemModelProvider itemModels;
    private static final int DEFAULT_ANGLE_OFFSET = 180;
    public static final ImmutableMap<Direction, Property<WallSide>> WALL_PROPS = ImmutableMap.builder().put((Object)Direction.EAST, (Object)BlockStateProperties.f_61378_).put((Object)Direction.NORTH, (Object)BlockStateProperties.f_61379_).put((Object)Direction.SOUTH, (Object)BlockStateProperties.f_61380_).put((Object)Direction.WEST, (Object)BlockStateProperties.f_61381_).build();

    public BlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        this.output = output;
        this.modid = modid;
        this.blockModels = new BlockModelProvider(output, modid, exFileHelper){

            @Override
            public CompletableFuture<?> m_213708_(CachedOutput cache) {
                return CompletableFuture.allOf(new CompletableFuture[0]);
            }

            @Override
            protected void registerModels() {
            }
        };
        this.itemModels = new ItemModelProvider(output, modid, this.blockModels.existingFileHelper){

            @Override
            protected void registerModels() {
            }

            @Override
            public CompletableFuture<?> m_213708_(CachedOutput cache) {
                return CompletableFuture.allOf(new CompletableFuture[0]);
            }
        };
    }

    public CompletableFuture<?> m_213708_(CachedOutput cache) {
        this.models().clear();
        this.itemModels().clear();
        this.registeredBlocks.clear();
        this.registerStatesAndModels();
        CompletableFuture[] futures = new CompletableFuture[2 + this.registeredBlocks.size()];
        int i = 0;
        futures[i++] = this.models().generateAll(cache);
        futures[i++] = this.itemModels().generateAll(cache);
        for (Map.Entry<Block, IGeneratedBlockState> entry : this.registeredBlocks.entrySet()) {
            futures[i++] = this.saveBlockState(cache, entry.getValue().toJson(), entry.getKey());
        }
        return CompletableFuture.allOf(futures);
    }

    protected abstract void registerStatesAndModels();

    public VariantBlockStateBuilder getVariantBuilder(Block b) {
        if (this.registeredBlocks.containsKey(b)) {
            IGeneratedBlockState old = this.registeredBlocks.get(b);
            Preconditions.checkState((boolean)(old instanceof VariantBlockStateBuilder));
            return (VariantBlockStateBuilder)old;
        }
        VariantBlockStateBuilder ret = new VariantBlockStateBuilder(b);
        this.registeredBlocks.put(b, ret);
        return ret;
    }

    public MultiPartBlockStateBuilder getMultipartBuilder(Block b) {
        if (this.registeredBlocks.containsKey(b)) {
            IGeneratedBlockState old = this.registeredBlocks.get(b);
            Preconditions.checkState((boolean)(old instanceof MultiPartBlockStateBuilder));
            return (MultiPartBlockStateBuilder)old;
        }
        MultiPartBlockStateBuilder ret = new MultiPartBlockStateBuilder(b);
        this.registeredBlocks.put(b, ret);
        return ret;
    }

    public BlockModelProvider models() {
        return this.blockModels;
    }

    public ItemModelProvider itemModels() {
        return this.itemModels;
    }

    public ResourceLocation modLoc(String name) {
        return new ResourceLocation(this.modid, name);
    }

    public ResourceLocation mcLoc(String name) {
        return new ResourceLocation(name);
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    private String name(Block block) {
        return this.key(block).m_135815_();
    }

    public ResourceLocation blockTexture(Block block) {
        ResourceLocation name = this.key(block);
        return new ResourceLocation(name.m_135827_(), "block/" + name.m_135815_());
    }

    private ResourceLocation extend(ResourceLocation rl, String suffix) {
        return new ResourceLocation(rl.m_135827_(), rl.m_135815_() + suffix);
    }

    public ModelFile cubeAll(Block block) {
        return this.models().cubeAll(this.name(block), this.blockTexture(block));
    }

    public void simpleBlock(Block block) {
        this.simpleBlock(block, this.cubeAll(block));
    }

    public void simpleBlock(Block block, Function<ModelFile, ConfiguredModel[]> expander) {
        this.simpleBlock(block, expander.apply(this.cubeAll(block)));
    }

    public void simpleBlock(Block block, ModelFile model) {
        this.simpleBlock(block, new ConfiguredModel(model));
    }

    public void simpleBlockItem(Block block, ModelFile model) {
        ((ItemModelBuilder)this.itemModels().getBuilder(this.key(block).m_135815_())).parent(model);
    }

    public void simpleBlockWithItem(Block block, ModelFile model) {
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    public void simpleBlock(Block block, ConfiguredModel ... models) {
        this.getVariantBuilder(block).partialState().setModels(models);
    }

    public void axisBlock(RotatedPillarBlock block) {
        this.axisBlock(block, this.blockTexture((Block)block));
    }

    public void logBlock(RotatedPillarBlock block) {
        this.axisBlock(block, this.blockTexture((Block)block), this.extend(this.blockTexture((Block)block), "_top"));
    }

    public void axisBlock(RotatedPillarBlock block, ResourceLocation baseName) {
        this.axisBlock(block, this.extend(baseName, "_side"), this.extend(baseName, "_end"));
    }

    public void axisBlock(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end) {
        this.axisBlock(block, (ModelFile)this.models().cubeColumn(this.name((Block)block), side, end), (ModelFile)this.models().cubeColumnHorizontal(this.name((Block)block) + "_horizontal", side, end));
    }

    public void axisBlockWithRenderType(RotatedPillarBlock block, String renderType) {
        this.axisBlockWithRenderType(block, this.blockTexture((Block)block), renderType);
    }

    public void logBlockWithRenderType(RotatedPillarBlock block, String renderType) {
        this.axisBlockWithRenderType(block, this.blockTexture((Block)block), this.extend(this.blockTexture((Block)block), "_top"), renderType);
    }

    public void axisBlockWithRenderType(RotatedPillarBlock block, ResourceLocation baseName, String renderType) {
        this.axisBlockWithRenderType(block, this.extend(baseName, "_side"), this.extend(baseName, "_end"), renderType);
    }

    public void axisBlockWithRenderType(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end, String renderType) {
        this.axisBlock(block, (ModelFile)((BlockModelBuilder)this.models().cubeColumn(this.name((Block)block), side, end)).renderType(renderType), (ModelFile)((BlockModelBuilder)this.models().cubeColumnHorizontal(this.name((Block)block) + "_horizontal", side, end)).renderType(renderType));
    }

    public void axisBlockWithRenderType(RotatedPillarBlock block, ResourceLocation renderType) {
        this.axisBlockWithRenderType(block, this.blockTexture((Block)block), renderType);
    }

    public void logBlockWithRenderType(RotatedPillarBlock block, ResourceLocation renderType) {
        this.axisBlockWithRenderType(block, this.blockTexture((Block)block), this.extend(this.blockTexture((Block)block), "_top"), renderType);
    }

    public void axisBlockWithRenderType(RotatedPillarBlock block, ResourceLocation baseName, ResourceLocation renderType) {
        this.axisBlockWithRenderType(block, this.extend(baseName, "_side"), this.extend(baseName, "_end"), renderType);
    }

    public void axisBlockWithRenderType(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end, ResourceLocation renderType) {
        this.axisBlock(block, (ModelFile)((BlockModelBuilder)this.models().cubeColumn(this.name((Block)block), side, end)).renderType(renderType), (ModelFile)((BlockModelBuilder)this.models().cubeColumnHorizontal(this.name((Block)block) + "_horizontal", side, end)).renderType(renderType));
    }

    public void axisBlockWithRenderTypeAndFast(RotatedPillarBlock block, String renderType, String renderTypeFast) {
        this.axisBlockWithRenderTypeAndFast(block, this.blockTexture((Block)block), renderType, renderTypeFast);
    }

    public void logBlockWithRenderTypeAndFast(RotatedPillarBlock block, String renderType, String renderTypeFast) {
        this.axisBlockWithRenderTypeAndFast(block, this.blockTexture((Block)block), this.extend(this.blockTexture((Block)block), "_top"), renderType, renderTypeFast);
    }

    public void axisBlockWithRenderTypeAndFast(RotatedPillarBlock block, ResourceLocation baseName, String renderType, String renderTypeFast) {
        this.axisBlockWithRenderTypeAndFast(block, this.extend(baseName, "_side"), this.extend(baseName, "_end"), renderType, renderTypeFast);
    }

    public void axisBlockWithRenderTypeAndFast(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end, String renderType, String renderTypeFast) {
        this.axisBlock(block, (ModelFile)((BlockModelBuilder)this.models().cubeColumn(this.name((Block)block), side, end)).renderType(renderType, renderTypeFast), (ModelFile)((BlockModelBuilder)this.models().cubeColumnHorizontal(this.name((Block)block) + "_horizontal", side, end)).renderType(renderType, renderTypeFast));
    }

    public void axisBlockWithRenderTypeAndFast(RotatedPillarBlock block, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.axisBlockWithRenderTypeAndFast(block, this.blockTexture((Block)block), renderType, renderTypeFast);
    }

    public void logBlockWithRenderType(RotatedPillarBlock block, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.axisBlockWithRenderTypeAndFast(block, this.blockTexture((Block)block), this.extend(this.blockTexture((Block)block), "_top"), renderType, renderTypeFast);
    }

    public void axisBlockWithRenderTypeAndFast(RotatedPillarBlock block, ResourceLocation baseName, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.axisBlockWithRenderTypeAndFast(block, this.extend(baseName, "_side"), this.extend(baseName, "_end"), renderType, renderTypeFast);
    }

    public void axisBlockWithRenderTypeAndFast(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.axisBlock(block, (ModelFile)((BlockModelBuilder)this.models().cubeColumn(this.name((Block)block), side, end)).renderType(renderType, renderTypeFast), (ModelFile)((BlockModelBuilder)this.models().cubeColumnHorizontal(this.name((Block)block) + "_horizontal", side, end)).renderType(renderType, renderTypeFast));
    }

    public void axisBlock(RotatedPillarBlock block, ModelFile vertical, ModelFile horizontal) {
        this.getVariantBuilder((Block)block).partialState().with(RotatedPillarBlock.f_55923_, Direction.Axis.Y).modelForState().modelFile(vertical).addModel().partialState().with(RotatedPillarBlock.f_55923_, Direction.Axis.Z).modelForState().modelFile(horizontal).rotationX(90).addModel().partialState().with(RotatedPillarBlock.f_55923_, Direction.Axis.X).modelForState().modelFile(horizontal).rotationX(90).rotationY(90).addModel();
    }

    public void horizontalBlock(Block block, ResourceLocation side, ResourceLocation front, ResourceLocation top) {
        this.horizontalBlock(block, (ModelFile)this.models().orientable(this.name(block), side, front, top));
    }

    public void horizontalBlock(Block block, ModelFile model) {
        this.horizontalBlock(block, model, 180);
    }

    public void horizontalBlock(Block block, ModelFile model, int angleOffset) {
        this.horizontalBlock(block, (BlockState $) -> model, angleOffset);
    }

    public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        this.horizontalBlock(block, modelFunc, 180);
    }

    public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        this.getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile((ModelFile)modelFunc.apply((BlockState)state)).rotationY(((int)((Direction)state.m_61143_((Property)BlockStateProperties.f_61374_)).m_122435_() + angleOffset) % 360).build());
    }

    public void horizontalFaceBlock(Block block, ModelFile model) {
        this.horizontalFaceBlock(block, model, 180);
    }

    public void horizontalFaceBlock(Block block, ModelFile model, int angleOffset) {
        this.horizontalFaceBlock(block, (BlockState $) -> model, angleOffset);
    }

    public void horizontalFaceBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        this.horizontalFaceBlock(block, modelFunc, 180);
    }

    public void horizontalFaceBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        this.getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile((ModelFile)modelFunc.apply((BlockState)state)).rotationX(((AttachFace)state.m_61143_((Property)BlockStateProperties.f_61376_)).ordinal() * 90).rotationY(((int)((Direction)state.m_61143_((Property)BlockStateProperties.f_61374_)).m_122435_() + angleOffset + (state.m_61143_((Property)BlockStateProperties.f_61376_) == AttachFace.CEILING ? 180 : 0)) % 360).build());
    }

    public void directionalBlock(Block block, ModelFile model) {
        this.directionalBlock(block, model, 180);
    }

    public void directionalBlock(Block block, ModelFile model, int angleOffset) {
        this.directionalBlock(block, (BlockState $) -> model, angleOffset);
    }

    public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        this.directionalBlock(block, modelFunc, 180);
    }

    public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        this.getVariantBuilder(block).forAllStates(state -> {
            Direction dir = (Direction)state.m_61143_((Property)BlockStateProperties.f_61372_);
            return ConfiguredModel.builder().modelFile((ModelFile)modelFunc.apply((BlockState)state)).rotationX(dir == Direction.DOWN ? 180 : (dir.m_122434_().m_122479_() ? 90 : 0)).rotationY(dir.m_122434_().m_122478_() ? 0 : ((int)dir.m_122435_() + angleOffset) % 360).build();
        });
    }

    public void stairsBlock(StairBlock block, ResourceLocation texture) {
        this.stairsBlock(block, texture, texture, texture);
    }

    public void stairsBlock(StairBlock block, String name, ResourceLocation texture) {
        this.stairsBlock(block, name, texture, texture, texture);
    }

    public void stairsBlock(StairBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        this.stairsBlockInternal(block, this.key((Block)block).toString(), side, bottom, top);
    }

    public void stairsBlock(StairBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        this.stairsBlockInternal(block, name + "_stairs", side, bottom, top);
    }

    public void stairsBlockWithRenderType(StairBlock block, ResourceLocation texture, String renderType) {
        this.stairsBlockWithRenderType(block, texture, texture, texture, renderType);
    }

    public void stairsBlockWithRenderType(StairBlock block, String name, ResourceLocation texture, String renderType) {
        this.stairsBlockWithRenderType(block, name, texture, texture, texture, renderType);
    }

    public void stairsBlockWithRenderType(StairBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, String renderType) {
        this.stairsBlockInternalWithRenderType(block, this.key((Block)block).toString(), side, bottom, top, ResourceLocation.m_135820_((String)renderType));
    }

    public void stairsBlockWithRenderType(StairBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, String renderType) {
        this.stairsBlockInternalWithRenderType(block, name + "_stairs", side, bottom, top, ResourceLocation.m_135820_((String)renderType));
    }

    public void stairsBlockWithRenderType(StairBlock block, ResourceLocation texture, ResourceLocation renderType) {
        this.stairsBlockWithRenderType(block, texture, texture, texture, renderType);
    }

    public void stairsBlockWithRenderType(StairBlock block, String name, ResourceLocation texture, ResourceLocation renderType) {
        this.stairsBlockWithRenderType(block, name, texture, texture, texture, renderType);
    }

    public void stairsBlockWithRenderType(StairBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
        this.stairsBlockInternalWithRenderType(block, this.key((Block)block).toString(), side, bottom, top, renderType);
    }

    public void stairsBlockWithRenderType(StairBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
        this.stairsBlockInternalWithRenderType(block, name + "_stairs", side, bottom, top, renderType);
    }

    public void stairsBlockWithRenderTypeAndFast(StairBlock block, ResourceLocation texture, String renderType, String renderTypeFast) {
        this.stairsBlockWithRenderTypeAndFast(block, texture, texture, texture, renderType, renderTypeFast);
    }

    public void stairsBlockWithRenderTypeAndFast(StairBlock block, String name, ResourceLocation texture, String renderType, String renderTypeFast) {
        this.stairsBlockWithRenderTypeAndFast(block, name, texture, texture, texture, renderType, renderTypeFast);
    }

    public void stairsBlockWithRenderTypeAndFast(StairBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, String renderType, String renderTypeFast) {
        this.stairsBlockInternalWithRenderTypeAndFast(block, this.key((Block)block).toString(), side, bottom, top, ResourceLocation.m_135820_((String)renderType), ResourceLocation.m_135820_((String)renderTypeFast));
    }

    public void stairsBlockWithRenderTypeAndFast(StairBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, String renderType, String renderTypeFast) {
        this.stairsBlockInternalWithRenderTypeAndFast(block, name + "_stairs", side, bottom, top, ResourceLocation.m_135820_((String)renderType), ResourceLocation.m_135820_((String)renderTypeFast));
    }

    public void stairsBlockWithRenderTypeAndFast(StairBlock block, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.stairsBlockWithRenderTypeAndFast(block, texture, texture, texture, renderType, renderTypeFast);
    }

    public void stairsBlockWithRenderTypeAndFast(StairBlock block, String name, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.stairsBlockWithRenderTypeAndFast(block, name, texture, texture, texture, renderType, renderTypeFast);
    }

    public void stairsBlockWithRenderTypeAndFast(StairBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.stairsBlockInternalWithRenderTypeAndFast(block, this.key((Block)block).toString(), side, bottom, top, renderType, renderTypeFast);
    }

    public void stairsBlockWithRenderTypeAndFast(StairBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.stairsBlockInternalWithRenderTypeAndFast(block, name + "_stairs", side, bottom, top, renderType, renderTypeFast);
    }

    private void stairsBlockInternal(StairBlock block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        Object stairs = this.models().stairs(baseName, side, bottom, top);
        Object stairsInner = this.models().stairsInner(baseName + "_inner", side, bottom, top);
        Object stairsOuter = this.models().stairsOuter(baseName + "_outer", side, bottom, top);
        this.stairsBlock(block, (ModelFile)stairs, (ModelFile)stairsInner, (ModelFile)stairsOuter);
    }

    private void stairsBlockInternalWithRenderType(StairBlock block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
        Object stairs = ((BlockModelBuilder)this.models().stairs(baseName, side, bottom, top)).renderType(renderType);
        Object stairsInner = ((BlockModelBuilder)this.models().stairsInner(baseName + "_inner", side, bottom, top)).renderType(renderType);
        Object stairsOuter = ((BlockModelBuilder)this.models().stairsOuter(baseName + "_outer", side, bottom, top)).renderType(renderType);
        this.stairsBlock(block, (ModelFile)stairs, (ModelFile)stairsInner, (ModelFile)stairsOuter);
    }

    private void stairsBlockInternalWithRenderTypeAndFast(StairBlock block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        Object stairs = ((BlockModelBuilder)this.models().stairs(baseName, side, bottom, top)).renderType(renderType, renderTypeFast);
        Object stairsInner = ((BlockModelBuilder)this.models().stairsInner(baseName + "_inner", side, bottom, top)).renderType(renderType, renderTypeFast);
        Object stairsOuter = ((BlockModelBuilder)this.models().stairsOuter(baseName + "_outer", side, bottom, top)).renderType(renderType, renderTypeFast);
        this.stairsBlock(block, (ModelFile)stairs, (ModelFile)stairsInner, (ModelFile)stairsOuter);
    }

    public void stairsBlock(StairBlock block, ModelFile stairs, ModelFile stairsInner, ModelFile stairsOuter) {
        this.getVariantBuilder((Block)block).forAllStatesExcept(state -> {
            Direction facing = (Direction)state.m_61143_((Property)StairBlock.f_56841_);
            Half half = (Half)state.m_61143_((Property)StairBlock.f_56842_);
            StairsShape shape = (StairsShape)state.m_61143_((Property)StairBlock.f_56843_);
            int yRot = (int)facing.m_122427_().m_122435_();
            if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                yRot += 270;
            }
            if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
                yRot += 90;
            }
            boolean uvlock = (yRot %= 360) != 0 || half == Half.TOP;
            return ConfiguredModel.builder().modelFile(shape == StairsShape.STRAIGHT ? stairs : (shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter)).rotationX(half == Half.BOTTOM ? 0 : 180).rotationY(yRot).uvLock(uvlock).build();
        }, new Property[]{StairBlock.f_56844_});
    }

    public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation texture) {
        this.slabBlock(block, doubleslab, texture, texture, texture);
    }

    public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        this.slabBlock(block, (ModelFile)this.models().slab(this.name((Block)block), side, bottom, top), (ModelFile)this.models().slabTop(this.name((Block)block) + "_top", side, bottom, top), this.models().getExistingFile(doubleslab));
    }

    public void slabBlock(SlabBlock block, ModelFile bottom, ModelFile top, ModelFile doubleslab) {
        this.getVariantBuilder((Block)block).partialState().with(SlabBlock.f_56353_, SlabType.BOTTOM).addModels(new ConfiguredModel(bottom)).partialState().with(SlabBlock.f_56353_, SlabType.TOP).addModels(new ConfiguredModel(top)).partialState().with(SlabBlock.f_56353_, SlabType.DOUBLE).addModels(new ConfiguredModel(doubleslab));
    }

    public void buttonBlock(ButtonBlock block, ResourceLocation texture) {
        Object button = this.models().button(this.name((Block)block), texture);
        Object buttonPressed = this.models().buttonPressed(this.name((Block)block) + "_pressed", texture);
        this.buttonBlock(block, (ModelFile)button, (ModelFile)buttonPressed);
    }

    public void buttonBlock(ButtonBlock block, ModelFile button, ModelFile buttonPressed) {
        this.getVariantBuilder((Block)block).forAllStates(state -> {
            Direction facing = (Direction)state.m_61143_((Property)ButtonBlock.f_54117_);
            AttachFace face = (AttachFace)state.m_61143_((Property)ButtonBlock.f_53179_);
            boolean powered = (Boolean)state.m_61143_((Property)ButtonBlock.f_51045_);
            return ConfiguredModel.builder().modelFile(powered ? buttonPressed : button).rotationX(face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180)).rotationY((int)(face == AttachFace.CEILING ? facing : facing.m_122424_()).m_122435_()).uvLock(face == AttachFace.WALL).build();
        });
    }

    public void pressurePlateBlock(PressurePlateBlock block, ResourceLocation texture) {
        Object pressurePlate = this.models().pressurePlate(this.name((Block)block), texture);
        Object pressurePlateDown = this.models().pressurePlateDown(this.name((Block)block) + "_down", texture);
        this.pressurePlateBlock(block, (ModelFile)pressurePlate, (ModelFile)pressurePlateDown);
    }

    public void pressurePlateBlock(PressurePlateBlock block, ModelFile pressurePlate, ModelFile pressurePlateDown) {
        this.getVariantBuilder((Block)block).partialState().with(PressurePlateBlock.f_55249_, true).addModels(new ConfiguredModel(pressurePlateDown)).partialState().with(PressurePlateBlock.f_55249_, false).addModels(new ConfiguredModel(pressurePlate));
    }

    public void signBlock(StandingSignBlock signBlock, WallSignBlock wallSignBlock, ResourceLocation texture) {
        Object sign = this.models().sign(this.name((Block)signBlock), texture);
        this.signBlock(signBlock, wallSignBlock, (ModelFile)sign);
    }

    public void signBlock(StandingSignBlock signBlock, WallSignBlock wallSignBlock, ModelFile sign) {
        this.simpleBlock((Block)signBlock, sign);
        this.simpleBlock((Block)wallSignBlock, sign);
    }

    public void fourWayBlock(CrossCollisionBlock block, ModelFile post, ModelFile side) {
        MultiPartBlockStateBuilder builder = this.getMultipartBuilder((Block)block).part().modelFile(post).addModel().end();
        this.fourWayMultipart(builder, side);
    }

    public void fourWayMultipart(MultiPartBlockStateBuilder builder, ModelFile side) {
        PipeBlock.f_55154_.entrySet().forEach(e -> {
            Direction dir = (Direction)e.getKey();
            if (dir.m_122434_().m_122479_()) {
                builder.part().modelFile(side).rotationY(((int)dir.m_122435_() + 180) % 360).uvLock(true).addModel().condition((Property)e.getValue(), new Boolean[]{true});
            }
        });
    }

    public void fenceBlock(FenceBlock block, ResourceLocation texture) {
        String baseName = this.key((Block)block).toString();
        this.fourWayBlock((CrossCollisionBlock)block, (ModelFile)this.models().fencePost(baseName + "_post", texture), (ModelFile)this.models().fenceSide(baseName + "_side", texture));
    }

    public void fenceBlock(FenceBlock block, String name, ResourceLocation texture) {
        this.fourWayBlock((CrossCollisionBlock)block, (ModelFile)this.models().fencePost(name + "_fence_post", texture), (ModelFile)this.models().fenceSide(name + "_fence_side", texture));
    }

    public void fenceBlockWithRenderType(FenceBlock block, ResourceLocation texture, String renderType) {
        String baseName = this.key((Block)block).toString();
        this.fourWayBlock((CrossCollisionBlock)block, (ModelFile)((BlockModelBuilder)this.models().fencePost(baseName + "_post", texture)).renderType(renderType), (ModelFile)((BlockModelBuilder)this.models().fenceSide(baseName + "_side", texture)).renderType(renderType));
    }

    public void fenceBlockWithRenderType(FenceBlock block, String name, ResourceLocation texture, String renderType) {
        this.fourWayBlock((CrossCollisionBlock)block, (ModelFile)((BlockModelBuilder)this.models().fencePost(name + "_fence_post", texture)).renderType(renderType), (ModelFile)((BlockModelBuilder)this.models().fenceSide(name + "_fence_side", texture)).renderType(renderType));
    }

    public void fenceBlockWithRenderType(FenceBlock block, ResourceLocation texture, ResourceLocation renderType) {
        String baseName = this.key((Block)block).toString();
        this.fourWayBlock((CrossCollisionBlock)block, (ModelFile)((BlockModelBuilder)this.models().fencePost(baseName + "_post", texture)).renderType(renderType), (ModelFile)((BlockModelBuilder)this.models().fenceSide(baseName + "_side", texture)).renderType(renderType));
    }

    public void fenceBlockWithRenderType(FenceBlock block, String name, ResourceLocation texture, ResourceLocation renderType) {
        this.fourWayBlock((CrossCollisionBlock)block, (ModelFile)((BlockModelBuilder)this.models().fencePost(name + "_fence_post", texture)).renderType(renderType), (ModelFile)((BlockModelBuilder)this.models().fenceSide(name + "_fence_side", texture)).renderType(renderType));
    }

    public void fenceBlockWithRenderTypeAndFast(FenceBlock block, ResourceLocation texture, String renderType, String renderTypeFast) {
        String baseName = this.key((Block)block).toString();
        this.fourWayBlock((CrossCollisionBlock)block, (ModelFile)((BlockModelBuilder)this.models().fencePost(baseName + "_post", texture)).renderType(renderType, renderTypeFast), (ModelFile)((BlockModelBuilder)this.models().fenceSide(baseName + "_side", texture)).renderType(renderType, renderTypeFast));
    }

    public void fenceBlockWithRenderTypeAndFast(FenceBlock block, String name, ResourceLocation texture, String renderType, String renderTypeFast) {
        this.fourWayBlock((CrossCollisionBlock)block, (ModelFile)((BlockModelBuilder)this.models().fencePost(name + "_fence_post", texture)).renderType(renderType, renderTypeFast), (ModelFile)((BlockModelBuilder)this.models().fenceSide(name + "_fence_side", texture)).renderType(renderType, renderTypeFast));
    }

    public void fenceBlockWithRenderTypeAndFast(FenceBlock block, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        String baseName = this.key((Block)block).toString();
        this.fourWayBlock((CrossCollisionBlock)block, (ModelFile)((BlockModelBuilder)this.models().fencePost(baseName + "_post", texture)).renderType(renderType, renderTypeFast), (ModelFile)((BlockModelBuilder)this.models().fenceSide(baseName + "_side", texture)).renderType(renderType, renderTypeFast));
    }

    public void fenceBlockWithRenderTypeAndFast(FenceBlock block, String name, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.fourWayBlock((CrossCollisionBlock)block, (ModelFile)((BlockModelBuilder)this.models().fencePost(name + "_fence_post", texture)).renderType(renderType, renderTypeFast), (ModelFile)((BlockModelBuilder)this.models().fenceSide(name + "_fence_side", texture)).renderType(renderType, renderTypeFast));
    }

    public void fenceGateBlock(FenceGateBlock block, ResourceLocation texture) {
        this.fenceGateBlockInternal(block, this.key((Block)block).toString(), texture);
    }

    public void fenceGateBlock(FenceGateBlock block, String name, ResourceLocation texture) {
        this.fenceGateBlockInternal(block, name + "_fence_gate", texture);
    }

    public void fenceGateBlockWithRenderType(FenceGateBlock block, ResourceLocation texture, String renderType) {
        this.fenceGateBlockInternalWithRenderType(block, this.key((Block)block).toString(), texture, ResourceLocation.m_135820_((String)renderType));
    }

    public void fenceGateBlockWithRenderType(FenceGateBlock block, String name, ResourceLocation texture, String renderType) {
        this.fenceGateBlockInternalWithRenderType(block, name + "_fence_gate", texture, ResourceLocation.m_135820_((String)renderType));
    }

    public void fenceGateBlockWithRenderType(FenceGateBlock block, ResourceLocation texture, ResourceLocation renderType) {
        this.fenceGateBlockInternalWithRenderType(block, this.key((Block)block).toString(), texture, renderType);
    }

    public void fenceGateBlockWithRenderType(FenceGateBlock block, String name, ResourceLocation texture, ResourceLocation renderType) {
        this.fenceGateBlockInternalWithRenderType(block, name + "_fence_gate", texture, renderType);
    }

    public void fenceGateBlockWithRenderTypeAndFast(FenceGateBlock block, ResourceLocation texture, String renderType, String renderTypeFast) {
        this.fenceGateBlockInternalWithRenderTypeAndFast(block, this.key((Block)block).toString(), texture, ResourceLocation.m_135820_((String)renderType), ResourceLocation.m_135820_((String)renderTypeFast));
    }

    public void fenceGateBlockWithRenderTypeAndFast(FenceGateBlock block, String name, ResourceLocation texture, String renderType, String renderTypeFast) {
        this.fenceGateBlockInternalWithRenderTypeAndFast(block, name + "_fence_gate", texture, ResourceLocation.m_135820_((String)renderType), ResourceLocation.m_135820_((String)renderTypeFast));
    }

    public void fenceGateBlockWithRenderTypeAndFast(FenceGateBlock block, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.fenceGateBlockInternalWithRenderTypeAndFast(block, this.key((Block)block).toString(), texture, renderType, renderTypeFast);
    }

    public void fenceGateBlockWithRenderTypeAndFast(FenceGateBlock block, String name, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.fenceGateBlockInternalWithRenderTypeAndFast(block, name + "_fence_gate", texture, renderType, renderTypeFast);
    }

    private void fenceGateBlockInternal(FenceGateBlock block, String baseName, ResourceLocation texture) {
        Object gate = this.models().fenceGate(baseName, texture);
        Object gateOpen = this.models().fenceGateOpen(baseName + "_open", texture);
        Object gateWall = this.models().fenceGateWall(baseName + "_wall", texture);
        Object gateWallOpen = this.models().fenceGateWallOpen(baseName + "_wall_open", texture);
        this.fenceGateBlock(block, (ModelFile)gate, (ModelFile)gateOpen, (ModelFile)gateWall, (ModelFile)gateWallOpen);
    }

    private void fenceGateBlockInternalWithRenderType(FenceGateBlock block, String baseName, ResourceLocation texture, ResourceLocation renderType) {
        Object gate = ((BlockModelBuilder)this.models().fenceGate(baseName, texture)).renderType(renderType);
        Object gateOpen = ((BlockModelBuilder)this.models().fenceGateOpen(baseName + "_open", texture)).renderType(renderType);
        Object gateWall = ((BlockModelBuilder)this.models().fenceGateWall(baseName + "_wall", texture)).renderType(renderType);
        Object gateWallOpen = ((BlockModelBuilder)this.models().fenceGateWallOpen(baseName + "_wall_open", texture)).renderType(renderType);
        this.fenceGateBlock(block, (ModelFile)gate, (ModelFile)gateOpen, (ModelFile)gateWall, (ModelFile)gateWallOpen);
    }

    private void fenceGateBlockInternalWithRenderTypeAndFast(FenceGateBlock block, String baseName, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        Object gate = ((BlockModelBuilder)this.models().fenceGate(baseName, texture)).renderType(renderType, renderTypeFast);
        Object gateOpen = ((BlockModelBuilder)this.models().fenceGateOpen(baseName + "_open", texture)).renderType(renderType, renderTypeFast);
        Object gateWall = ((BlockModelBuilder)this.models().fenceGateWall(baseName + "_wall", texture)).renderType(renderType, renderTypeFast);
        Object gateWallOpen = ((BlockModelBuilder)this.models().fenceGateWallOpen(baseName + "_wall_open", texture)).renderType(renderType, renderTypeFast);
        this.fenceGateBlock(block, (ModelFile)gate, (ModelFile)gateOpen, (ModelFile)gateWall, (ModelFile)gateWallOpen);
    }

    public void fenceGateBlock(FenceGateBlock block, ModelFile gate, ModelFile gateOpen, ModelFile gateWall, ModelFile gateWallOpen) {
        this.getVariantBuilder((Block)block).forAllStatesExcept(state -> {
            ModelFile model = gate;
            if (((Boolean)state.m_61143_((Property)FenceGateBlock.f_53343_)).booleanValue()) {
                model = gateWall;
            }
            if (((Boolean)state.m_61143_((Property)FenceGateBlock.f_53341_)).booleanValue()) {
                model = model == gateWall ? gateWallOpen : gateOpen;
            }
            return ConfiguredModel.builder().modelFile(model).rotationY((int)((Direction)state.m_61143_((Property)FenceGateBlock.f_54117_)).m_122435_()).uvLock(true).build();
        }, new Property[]{FenceGateBlock.f_53342_});
    }

    public void wallBlock(WallBlock block, ResourceLocation texture) {
        this.wallBlockInternal(block, this.key((Block)block).toString(), texture);
    }

    public void wallBlock(WallBlock block, String name, ResourceLocation texture) {
        this.wallBlockInternal(block, name + "_wall", texture);
    }

    public void wallBlockWithRenderType(WallBlock block, ResourceLocation texture, String renderType) {
        this.wallBlockInternalWithRenderType(block, this.key((Block)block).toString(), texture, ResourceLocation.m_135820_((String)renderType));
    }

    public void wallBlockWithRenderType(WallBlock block, String name, ResourceLocation texture, String renderType) {
        this.wallBlockInternalWithRenderType(block, name + "_wall", texture, ResourceLocation.m_135820_((String)renderType));
    }

    public void wallBlockWithRenderType(WallBlock block, ResourceLocation texture, ResourceLocation renderType) {
        this.wallBlockInternalWithRenderType(block, this.key((Block)block).toString(), texture, renderType);
    }

    public void wallBlockWithRenderType(WallBlock block, String name, ResourceLocation texture, ResourceLocation renderType) {
        this.wallBlockInternalWithRenderType(block, name + "_wall", texture, renderType);
    }

    public void wallBlockWithRenderTypeAndFast(WallBlock block, ResourceLocation texture, String renderType, String renderTypeFast) {
        this.wallBlockInternalWithRenderTypeAndFast(block, this.key((Block)block).toString(), texture, ResourceLocation.m_135820_((String)renderType), ResourceLocation.m_135820_((String)renderTypeFast));
    }

    public void wallBlockWithRenderTypeAndFast(WallBlock block, String name, ResourceLocation texture, String renderType, String renderTypeFast) {
        this.wallBlockInternalWithRenderTypeAndFast(block, name + "_wall", texture, ResourceLocation.m_135820_((String)renderType), ResourceLocation.m_135820_((String)renderTypeFast));
    }

    public void wallBlockWithRenderTypeAndFast(WallBlock block, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.wallBlockInternalWithRenderTypeAndFast(block, this.key((Block)block).toString(), texture, renderType, renderTypeFast);
    }

    public void wallBlockWithRenderTypeAndFast(WallBlock block, String name, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.wallBlockInternalWithRenderTypeAndFast(block, name + "_wall", texture, renderType, renderTypeFast);
    }

    private void wallBlockInternal(WallBlock block, String baseName, ResourceLocation texture) {
        this.wallBlock(block, (ModelFile)this.models().wallPost(baseName + "_post", texture), (ModelFile)this.models().wallSide(baseName + "_side", texture), (ModelFile)this.models().wallSideTall(baseName + "_side_tall", texture));
    }

    private void wallBlockInternalWithRenderType(WallBlock block, String baseName, ResourceLocation texture, ResourceLocation renderType) {
        this.wallBlock(block, (ModelFile)((BlockModelBuilder)this.models().wallPost(baseName + "_post", texture)).renderType(renderType), (ModelFile)((BlockModelBuilder)this.models().wallSide(baseName + "_side", texture)).renderType(renderType), (ModelFile)((BlockModelBuilder)this.models().wallSideTall(baseName + "_side_tall", texture)).renderType(renderType));
    }

    private void wallBlockInternalWithRenderTypeAndFast(WallBlock block, String baseName, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.wallBlock(block, (ModelFile)((BlockModelBuilder)this.models().wallPost(baseName + "_post", texture)).renderType(renderType, renderTypeFast), (ModelFile)((BlockModelBuilder)this.models().wallSide(baseName + "_side", texture)).renderType(renderType, renderTypeFast), (ModelFile)((BlockModelBuilder)this.models().wallSideTall(baseName + "_side_tall", texture)).renderType(renderType, renderTypeFast));
    }

    public void wallBlock(WallBlock block, ModelFile post, ModelFile side, ModelFile sideTall) {
        MultiPartBlockStateBuilder builder = this.getMultipartBuilder((Block)block).part().modelFile(post).addModel().condition((Property)WallBlock.f_57949_, new Boolean[]{true}).end();
        WALL_PROPS.entrySet().stream().filter(e -> ((Direction)e.getKey()).m_122434_().m_122479_()).forEach(e -> {
            this.wallSidePart(builder, side, (Map.Entry<Direction, Property<WallSide>>)e, WallSide.LOW);
            this.wallSidePart(builder, sideTall, (Map.Entry<Direction, Property<WallSide>>)e, WallSide.TALL);
        });
    }

    private void wallSidePart(MultiPartBlockStateBuilder builder, ModelFile model, Map.Entry<Direction, Property<WallSide>> entry, WallSide height) {
        builder.part().modelFile(model).rotationY(((int)entry.getKey().m_122435_() + 180) % 360).uvLock(true).addModel().condition(entry.getValue(), (Comparable[])new WallSide[]{height});
    }

    public void paneBlock(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge) {
        this.paneBlockInternal(block, this.key((Block)block).toString(), pane, edge);
    }

    public void paneBlock(IronBarsBlock block, String name, ResourceLocation pane, ResourceLocation edge) {
        this.paneBlockInternal(block, name + "_pane", pane, edge);
    }

    public void paneBlockWithRenderType(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge, String renderType) {
        this.paneBlockInternalWithRenderType(block, this.key((Block)block).toString(), pane, edge, ResourceLocation.m_135820_((String)renderType));
    }

    public void paneBlockWithRenderType(IronBarsBlock block, String name, ResourceLocation pane, ResourceLocation edge, String renderType) {
        this.paneBlockInternalWithRenderType(block, name + "_pane", pane, edge, ResourceLocation.m_135820_((String)renderType));
    }

    public void paneBlockWithRenderType(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge, ResourceLocation renderType) {
        this.paneBlockInternalWithRenderType(block, this.key((Block)block).toString(), pane, edge, renderType);
    }

    public void paneBlockWithRenderType(IronBarsBlock block, String name, ResourceLocation pane, ResourceLocation edge, ResourceLocation renderType) {
        this.paneBlockInternalWithRenderType(block, name + "_pane", pane, edge, renderType);
    }

    public void paneBlockWithRenderTypeAndFast(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge, String renderType, String renderTypeFast) {
        this.paneBlockInternalWithRenderTypeAndFast(block, this.key((Block)block).toString(), pane, edge, ResourceLocation.m_135820_((String)renderType), ResourceLocation.m_135820_((String)renderTypeFast));
    }

    public void paneBlockWithRenderTypeAndFast(IronBarsBlock block, String name, ResourceLocation pane, ResourceLocation edge, String renderType, String renderTypeFast) {
        this.paneBlockInternalWithRenderTypeAndFast(block, name + "_pane", pane, edge, ResourceLocation.m_135820_((String)renderType), ResourceLocation.m_135820_((String)renderTypeFast));
    }

    public void paneBlockWithRenderTypeAndFast(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.paneBlockInternalWithRenderTypeAndFast(block, this.key((Block)block).toString(), pane, edge, renderType, renderTypeFast);
    }

    public void paneBlockWithRenderTypeAndFast(IronBarsBlock block, String name, ResourceLocation pane, ResourceLocation edge, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.paneBlockInternalWithRenderTypeAndFast(block, name + "_pane", pane, edge, renderType, renderTypeFast);
    }

    private void paneBlockInternal(IronBarsBlock block, String baseName, ResourceLocation pane, ResourceLocation edge) {
        Object post = this.models().panePost(baseName + "_post", pane, edge);
        Object side = this.models().paneSide(baseName + "_side", pane, edge);
        Object sideAlt = this.models().paneSideAlt(baseName + "_side_alt", pane, edge);
        Object noSide = this.models().paneNoSide(baseName + "_noside", pane);
        Object noSideAlt = this.models().paneNoSideAlt(baseName + "_noside_alt", pane);
        this.paneBlock(block, (ModelFile)post, (ModelFile)side, (ModelFile)sideAlt, (ModelFile)noSide, (ModelFile)noSideAlt);
    }

    private void paneBlockInternalWithRenderType(IronBarsBlock block, String baseName, ResourceLocation pane, ResourceLocation edge, ResourceLocation renderType) {
        Object post = ((BlockModelBuilder)this.models().panePost(baseName + "_post", pane, edge)).renderType(renderType);
        Object side = ((BlockModelBuilder)this.models().paneSide(baseName + "_side", pane, edge)).renderType(renderType);
        Object sideAlt = ((BlockModelBuilder)this.models().paneSideAlt(baseName + "_side_alt", pane, edge)).renderType(renderType);
        Object noSide = ((BlockModelBuilder)this.models().paneNoSide(baseName + "_noside", pane)).renderType(renderType);
        Object noSideAlt = ((BlockModelBuilder)this.models().paneNoSideAlt(baseName + "_noside_alt", pane)).renderType(renderType);
        this.paneBlock(block, (ModelFile)post, (ModelFile)side, (ModelFile)sideAlt, (ModelFile)noSide, (ModelFile)noSideAlt);
    }

    private void paneBlockInternalWithRenderTypeAndFast(IronBarsBlock block, String baseName, ResourceLocation pane, ResourceLocation edge, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        Object post = ((BlockModelBuilder)this.models().panePost(baseName + "_post", pane, edge)).renderType(renderType, renderTypeFast);
        Object side = ((BlockModelBuilder)this.models().paneSide(baseName + "_side", pane, edge)).renderType(renderType, renderTypeFast);
        Object sideAlt = ((BlockModelBuilder)this.models().paneSideAlt(baseName + "_side_alt", pane, edge)).renderType(renderType, renderTypeFast);
        Object noSide = ((BlockModelBuilder)this.models().paneNoSide(baseName + "_noside", pane)).renderType(renderType, renderTypeFast);
        Object noSideAlt = ((BlockModelBuilder)this.models().paneNoSideAlt(baseName + "_noside_alt", pane)).renderType(renderType, renderTypeFast);
        this.paneBlock(block, (ModelFile)post, (ModelFile)side, (ModelFile)sideAlt, (ModelFile)noSide, (ModelFile)noSideAlt);
    }

    public void paneBlock(IronBarsBlock block, ModelFile post, ModelFile side, ModelFile sideAlt, ModelFile noSide, ModelFile noSideAlt) {
        MultiPartBlockStateBuilder builder = this.getMultipartBuilder((Block)block).part().modelFile(post).addModel().end();
        PipeBlock.f_55154_.entrySet().forEach(e -> {
            Direction dir = (Direction)e.getKey();
            if (dir.m_122434_().m_122479_()) {
                boolean alt = dir == Direction.SOUTH;
                builder.part().modelFile(alt || dir == Direction.WEST ? sideAlt : side).rotationY(dir.m_122434_() == Direction.Axis.X ? 90 : 0).addModel().condition((Property)e.getValue(), new Boolean[]{true}).end().part().modelFile(alt || dir == Direction.EAST ? noSideAlt : noSide).rotationY(dir == Direction.WEST ? 270 : (dir == Direction.SOUTH ? 90 : 0)).addModel().condition((Property)e.getValue(), new Boolean[]{false});
            }
        });
    }

    public void doorBlock(DoorBlock block, ResourceLocation bottom, ResourceLocation top) {
        this.doorBlockInternal(block, this.key((Block)block).toString(), bottom, top);
    }

    public void doorBlock(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top) {
        this.doorBlockInternal(block, name + "_door", bottom, top);
    }

    public void doorBlockWithRenderType(DoorBlock block, ResourceLocation bottom, ResourceLocation top, String renderType) {
        this.doorBlockInternalWithRenderType(block, this.key((Block)block).toString(), bottom, top, ResourceLocation.m_135820_((String)renderType));
    }

    public void doorBlockWithRenderType(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top, String renderType) {
        this.doorBlockInternalWithRenderType(block, name + "_door", bottom, top, ResourceLocation.m_135820_((String)renderType));
    }

    public void doorBlockWithRenderType(DoorBlock block, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
        this.doorBlockInternalWithRenderType(block, this.key((Block)block).toString(), bottom, top, renderType);
    }

    public void doorBlockWithRenderType(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
        this.doorBlockInternalWithRenderType(block, name + "_door", bottom, top, renderType);
    }

    public void doorBlockWithRenderTypeAndFast(DoorBlock block, ResourceLocation bottom, ResourceLocation top, String renderType, String renderTypeFast) {
        this.doorBlockInternalWithRenderTypeAndFast(block, this.key((Block)block).toString(), bottom, top, ResourceLocation.m_135820_((String)renderType), ResourceLocation.m_135820_((String)renderTypeFast));
    }

    public void doorBlockWithRenderTypeAndFast(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top, String renderType, String renderTypeFast) {
        this.doorBlockInternalWithRenderTypeAndFast(block, name + "_door", bottom, top, ResourceLocation.m_135820_((String)renderType), ResourceLocation.m_135820_((String)renderTypeFast));
    }

    public void doorBlockWithRenderTypeAndFast(DoorBlock block, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.doorBlockInternalWithRenderTypeAndFast(block, this.key((Block)block).toString(), bottom, top, renderType, renderTypeFast);
    }

    public void doorBlockWithRenderTypeAndFast(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.doorBlockInternalWithRenderTypeAndFast(block, name + "_door", bottom, top, renderType, renderTypeFast);
    }

    private void doorBlockInternal(DoorBlock block, String baseName, ResourceLocation bottom, ResourceLocation top) {
        Object bottomLeft = this.models().doorBottomLeft(baseName + "_bottom_left", bottom, top);
        Object bottomLeftOpen = this.models().doorBottomLeftOpen(baseName + "_bottom_left_open", bottom, top);
        Object bottomRight = this.models().doorBottomRight(baseName + "_bottom_right", bottom, top);
        Object bottomRightOpen = this.models().doorBottomRightOpen(baseName + "_bottom_right_open", bottom, top);
        Object topLeft = this.models().doorTopLeft(baseName + "_top_left", bottom, top);
        Object topLeftOpen = this.models().doorTopLeftOpen(baseName + "_top_left_open", bottom, top);
        Object topRight = this.models().doorTopRight(baseName + "_top_right", bottom, top);
        Object topRightOpen = this.models().doorTopRightOpen(baseName + "_top_right_open", bottom, top);
        this.doorBlock(block, (ModelFile)bottomLeft, (ModelFile)bottomLeftOpen, (ModelFile)bottomRight, (ModelFile)bottomRightOpen, (ModelFile)topLeft, (ModelFile)topLeftOpen, (ModelFile)topRight, (ModelFile)topRightOpen);
    }

    private void doorBlockInternalWithRenderType(DoorBlock block, String baseName, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
        Object bottomLeft = ((BlockModelBuilder)this.models().doorBottomLeft(baseName + "_bottom_left", bottom, top)).renderType(renderType);
        Object bottomLeftOpen = ((BlockModelBuilder)this.models().doorBottomLeftOpen(baseName + "_bottom_left_open", bottom, top)).renderType(renderType);
        Object bottomRight = ((BlockModelBuilder)this.models().doorBottomRight(baseName + "_bottom_right", bottom, top)).renderType(renderType);
        Object bottomRightOpen = ((BlockModelBuilder)this.models().doorBottomRightOpen(baseName + "_bottom_right_open", bottom, top)).renderType(renderType);
        Object topLeft = ((BlockModelBuilder)this.models().doorTopLeft(baseName + "_top_left", bottom, top)).renderType(renderType);
        Object topLeftOpen = ((BlockModelBuilder)this.models().doorTopLeftOpen(baseName + "_top_left_open", bottom, top)).renderType(renderType);
        Object topRight = ((BlockModelBuilder)this.models().doorTopRight(baseName + "_top_right", bottom, top)).renderType(renderType);
        Object topRightOpen = ((BlockModelBuilder)this.models().doorTopRightOpen(baseName + "_top_right_open", bottom, top)).renderType(renderType);
        this.doorBlock(block, (ModelFile)bottomLeft, (ModelFile)bottomLeftOpen, (ModelFile)bottomRight, (ModelFile)bottomRightOpen, (ModelFile)topLeft, (ModelFile)topLeftOpen, (ModelFile)topRight, (ModelFile)topRightOpen);
    }

    private void doorBlockInternalWithRenderTypeAndFast(DoorBlock block, String baseName, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        Object bottomLeft = ((BlockModelBuilder)this.models().doorBottomLeft(baseName + "_bottom_left", bottom, top)).renderType(renderType, renderTypeFast);
        Object bottomLeftOpen = ((BlockModelBuilder)this.models().doorBottomLeftOpen(baseName + "_bottom_left_open", bottom, top)).renderType(renderType, renderTypeFast);
        Object bottomRight = ((BlockModelBuilder)this.models().doorBottomRight(baseName + "_bottom_right", bottom, top)).renderType(renderType, renderTypeFast);
        Object bottomRightOpen = ((BlockModelBuilder)this.models().doorBottomRightOpen(baseName + "_bottom_right_open", bottom, top)).renderType(renderType, renderTypeFast);
        Object topLeft = ((BlockModelBuilder)this.models().doorTopLeft(baseName + "_top_left", bottom, top)).renderType(renderType, renderTypeFast);
        Object topLeftOpen = ((BlockModelBuilder)this.models().doorTopLeftOpen(baseName + "_top_left_open", bottom, top)).renderType(renderType, renderTypeFast);
        Object topRight = ((BlockModelBuilder)this.models().doorTopRight(baseName + "_top_right", bottom, top)).renderType(renderType, renderTypeFast);
        Object topRightOpen = ((BlockModelBuilder)this.models().doorTopRightOpen(baseName + "_top_right_open", bottom, top)).renderType(renderType, renderTypeFast);
        this.doorBlock(block, (ModelFile)bottomLeft, (ModelFile)bottomLeftOpen, (ModelFile)bottomRight, (ModelFile)bottomRightOpen, (ModelFile)topLeft, (ModelFile)topLeftOpen, (ModelFile)topRight, (ModelFile)topRightOpen);
    }

    public void doorBlock(DoorBlock block, ModelFile bottomLeft, ModelFile bottomLeftOpen, ModelFile bottomRight, ModelFile bottomRightOpen, ModelFile topLeft, ModelFile topLeftOpen, ModelFile topRight, ModelFile topRightOpen) {
        this.getVariantBuilder((Block)block).forAllStatesExcept(state -> {
            boolean lower;
            int yRot = (int)((Direction)state.m_61143_((Property)DoorBlock.f_52726_)).m_122435_() + 90;
            boolean right = state.m_61143_((Property)DoorBlock.f_52728_) == DoorHingeSide.RIGHT;
            boolean open = (Boolean)state.m_61143_((Property)DoorBlock.f_52727_);
            boolean bl = lower = state.m_61143_((Property)DoorBlock.f_52730_) == DoubleBlockHalf.LOWER;
            if (open) {
                yRot += 90;
            }
            if (right && open) {
                yRot += 180;
            }
            yRot %= 360;
            ModelFile model = null;
            if (lower && right && open) {
                model = bottomRightOpen;
            } else if (lower && !right && open) {
                model = bottomLeftOpen;
            }
            if (lower && right && !open) {
                model = bottomRight;
            } else if (lower && !right && !open) {
                model = bottomLeft;
            }
            if (!lower && right && open) {
                model = topRightOpen;
            } else if (!lower && !right && open) {
                model = topLeftOpen;
            }
            if (!lower && right && !open) {
                model = topRight;
            } else if (!(lower || right || open)) {
                model = topLeft;
            }
            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
        }, new Property[]{DoorBlock.f_52729_});
    }

    public void trapdoorBlock(TrapDoorBlock block, ResourceLocation texture, boolean orientable) {
        this.trapdoorBlockInternal(block, this.key((Block)block).toString(), texture, orientable);
    }

    public void trapdoorBlock(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable) {
        this.trapdoorBlockInternal(block, name + "_trapdoor", texture, orientable);
    }

    public void trapdoorBlockWithRenderType(TrapDoorBlock block, ResourceLocation texture, boolean orientable, String renderType) {
        this.trapdoorBlockInternalWithRenderType(block, this.key((Block)block).toString(), texture, orientable, ResourceLocation.m_135820_((String)renderType));
    }

    public void trapdoorBlockWithRenderType(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable, String renderType) {
        this.trapdoorBlockInternalWithRenderType(block, name + "_trapdoor", texture, orientable, ResourceLocation.m_135820_((String)renderType));
    }

    public void trapdoorBlockWithRenderType(TrapDoorBlock block, ResourceLocation texture, boolean orientable, ResourceLocation renderType) {
        this.trapdoorBlockInternalWithRenderType(block, this.key((Block)block).toString(), texture, orientable, renderType);
    }

    public void trapdoorBlockWithRenderType(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable, ResourceLocation renderType) {
        this.trapdoorBlockInternalWithRenderType(block, name + "_trapdoor", texture, orientable, renderType);
    }

    public void trapdoorBlockWithRenderTypeAndFast(TrapDoorBlock block, ResourceLocation texture, boolean orientable, String renderType, String renderTypeFast) {
        this.trapdoorBlockInternalWithRenderTypeAndFast(block, this.key((Block)block).toString(), texture, orientable, ResourceLocation.m_135820_((String)renderType), ResourceLocation.m_135820_((String)renderTypeFast));
    }

    public void trapdoorBlockWithRenderTypeAndFast(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable, String renderType, String renderTypeFast) {
        this.trapdoorBlockInternalWithRenderTypeAndFast(block, name + "_trapdoor", texture, orientable, ResourceLocation.m_135820_((String)renderType), ResourceLocation.m_135820_((String)renderTypeFast));
    }

    public void trapdoorBlockWithRenderTypeAndFast(TrapDoorBlock block, ResourceLocation texture, boolean orientable, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.trapdoorBlockInternalWithRenderTypeAndFast(block, this.key((Block)block).toString(), texture, orientable, renderType, renderTypeFast);
    }

    public void trapdoorBlockWithRenderTypeAndFast(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        this.trapdoorBlockInternalWithRenderTypeAndFast(block, name + "_trapdoor", texture, orientable, renderType, renderTypeFast);
    }

    private void trapdoorBlockInternal(TrapDoorBlock block, String baseName, ResourceLocation texture, boolean orientable) {
        Object bottom = orientable ? this.models().trapdoorOrientableBottom(baseName + "_bottom", texture) : this.models().trapdoorBottom(baseName + "_bottom", texture);
        Object top = orientable ? this.models().trapdoorOrientableTop(baseName + "_top", texture) : this.models().trapdoorTop(baseName + "_top", texture);
        Object open = orientable ? this.models().trapdoorOrientableOpen(baseName + "_open", texture) : this.models().trapdoorOpen(baseName + "_open", texture);
        this.trapdoorBlock(block, (ModelFile)bottom, (ModelFile)top, (ModelFile)open, orientable);
    }

    private void trapdoorBlockInternalWithRenderType(TrapDoorBlock block, String baseName, ResourceLocation texture, boolean orientable, ResourceLocation renderType) {
        Object bottom = orientable ? ((BlockModelBuilder)this.models().trapdoorOrientableBottom(baseName + "_bottom", texture)).renderType(renderType) : ((BlockModelBuilder)this.models().trapdoorBottom(baseName + "_bottom", texture)).renderType(renderType);
        Object top = orientable ? ((BlockModelBuilder)this.models().trapdoorOrientableTop(baseName + "_top", texture)).renderType(renderType) : ((BlockModelBuilder)this.models().trapdoorTop(baseName + "_top", texture)).renderType(renderType);
        Object open = orientable ? ((BlockModelBuilder)this.models().trapdoorOrientableOpen(baseName + "_open", texture)).renderType(renderType) : ((BlockModelBuilder)this.models().trapdoorOpen(baseName + "_open", texture)).renderType(renderType);
        this.trapdoorBlock(block, (ModelFile)bottom, (ModelFile)top, (ModelFile)open, orientable);
    }

    private void trapdoorBlockInternalWithRenderTypeAndFast(TrapDoorBlock block, String baseName, ResourceLocation texture, boolean orientable, ResourceLocation renderType, ResourceLocation renderTypeFast) {
        Object bottom = orientable ? ((BlockModelBuilder)this.models().trapdoorOrientableBottom(baseName + "_bottom", texture)).renderType(renderType, renderTypeFast) : ((BlockModelBuilder)this.models().trapdoorBottom(baseName + "_bottom", texture)).renderType(renderType, renderTypeFast);
        Object top = orientable ? ((BlockModelBuilder)this.models().trapdoorOrientableTop(baseName + "_top", texture)).renderType(renderType, renderTypeFast) : ((BlockModelBuilder)this.models().trapdoorTop(baseName + "_top", texture)).renderType(renderType, renderTypeFast);
        Object open = orientable ? ((BlockModelBuilder)this.models().trapdoorOrientableOpen(baseName + "_open", texture)).renderType(renderType, renderTypeFast) : ((BlockModelBuilder)this.models().trapdoorOpen(baseName + "_open", texture)).renderType(renderType, renderTypeFast);
        this.trapdoorBlock(block, (ModelFile)bottom, (ModelFile)top, (ModelFile)open, orientable);
    }

    public void trapdoorBlock(TrapDoorBlock block, ModelFile bottom, ModelFile top, ModelFile open, boolean orientable) {
        this.getVariantBuilder((Block)block).forAllStatesExcept(state -> {
            int xRot = 0;
            int yRot = (int)((Direction)state.m_61143_((Property)TrapDoorBlock.f_54117_)).m_122435_() + 180;
            boolean isOpen = (Boolean)state.m_61143_((Property)TrapDoorBlock.f_57514_);
            if (orientable && isOpen && state.m_61143_((Property)TrapDoorBlock.f_57515_) == Half.TOP) {
                xRot += 180;
                yRot += 180;
            }
            if (!orientable && !isOpen) {
                yRot = 0;
            }
            return ConfiguredModel.builder().modelFile(isOpen ? open : (state.m_61143_((Property)TrapDoorBlock.f_57515_) == Half.TOP ? top : bottom)).rotationX(xRot).rotationY(yRot %= 360).build();
        }, new Property[]{TrapDoorBlock.f_57516_, TrapDoorBlock.f_57517_});
    }

    private CompletableFuture<?> saveBlockState(CachedOutput cache, JsonObject stateJson, Block owner) {
        ResourceLocation blockName = (ResourceLocation)Preconditions.checkNotNull((Object)this.key(owner));
        Path outputPath = this.output.m_247566_(PackOutput.Target.RESOURCE_PACK).resolve(blockName.m_135827_()).resolve("blockstates").resolve(blockName.m_135815_() + ".json");
        return DataProvider.m_253162_((CachedOutput)cache, (JsonElement)stateJson, (Path)outputPath);
    }

    @NotNull
    public String m_6055_() {
        return "Block States: " + this.modid;
    }

    public static class ConfiguredModelList {
        private final List<ConfiguredModel> models;

        private ConfiguredModelList(List<ConfiguredModel> models) {
            Preconditions.checkArgument((!models.isEmpty() ? 1 : 0) != 0);
            this.models = models;
        }

        public ConfiguredModelList(ConfiguredModel model) {
            this((List<ConfiguredModel>)ImmutableList.of((Object)model));
        }

        public ConfiguredModelList(ConfiguredModel ... models) {
            this(Arrays.asList(models));
        }

        public JsonElement toJSON() {
            if (this.models.size() == 1) {
                return this.models.get(0).toJSON(false);
            }
            JsonArray ret = new JsonArray();
            for (ConfiguredModel m : this.models) {
                ret.add((JsonElement)m.toJSON(true));
            }
            return ret;
        }

        public ConfiguredModelList append(ConfiguredModel ... models) {
            return new ConfiguredModelList((List<ConfiguredModel>)ImmutableList.builder().addAll(this.models).add((Object[])models).build());
        }
    }
}

