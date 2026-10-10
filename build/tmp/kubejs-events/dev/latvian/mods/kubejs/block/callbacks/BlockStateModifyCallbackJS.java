/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.latvian.mods.kubejs.block.callbacks;

import com.google.common.collect.ImmutableMap;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockStateModifyCallbackJS {
    private BlockState state;

    public BlockStateModifyCallbackJS(BlockState state) {
        this.state = state;
    }

    @Info(value="Cycles the property")
    public <T extends Comparable<T>> BlockStateModifyCallbackJS cycle(Property<T> property) {
        this.state = (BlockState)this.state.m_61122_(property);
        return this;
    }

    @Info(value="Gets the state. If it has been modified, gets the new state")
    public BlockState getState() {
        return this.state;
    }

    public String toString() {
        return this.state.toString();
    }

    @Info(value="Get the properties this block has that can be changed")
    public Collection<Property<?>> getProperties() {
        return this.state.m_61147_();
    }

    @Info(value="Checks if this block has the specified property")
    public <T extends Comparable<T>> boolean hasProperty(Property<T> property) {
        return this.state.m_61138_(property);
    }

    @Info(value="Gets the value of the passed in property")
    public <T extends Comparable<T>> T getValue(Property<T> property) {
        return (T)this.state.m_61143_(property);
    }

    @Info(value="Gets the value of the pased in property")
    public <T extends Comparable<T>> T get(Property<T> property) {
        return (T)this.state.m_61143_(property);
    }

    @Info(value="Gets the value of the passed in property as an Optional. If the property does not exist in this block the Optional will be empty")
    public <T extends Comparable<T>> Optional<T> getOptionalValue(Property<T> property) {
        return this.state.m_61145_(property);
    }

    @Info(value="Sets the value of the specified property")
    public <T extends Comparable<T>, V extends T> BlockStateModifyCallbackJS setValue(Property<T> property, V comparable) {
        this.state = (BlockState)this.state.m_61124_(property, comparable);
        return this;
    }

    @Info(value="Sets the value of the specified boolean property")
    public BlockStateModifyCallbackJS set(BooleanProperty property, boolean value) {
        this.state = (BlockState)this.state.m_61124_((Property)property, (Comparable)Boolean.valueOf(value));
        return this;
    }

    @Info(value="Sets the value of the specified integer property")
    public BlockStateModifyCallbackJS set(IntegerProperty property, Integer value) {
        this.state = (BlockState)this.state.m_61124_((Property)property, (Comparable)value);
        return this;
    }

    @Info(value="Sets the value of the specified enum property")
    public <T extends Enum<T>> BlockStateModifyCallbackJS set(EnumProperty<T> property, String value) {
        this.state = (BlockState)this.state.m_61124_(property, (Comparable)((Object)((Enum)property.m_6215_(value).get())));
        return this;
    }

    public BlockStateModifyCallbackJS populateNeighbours(Map<Map<Property<?>, Comparable<?>>, BlockState> map) {
        this.state.m_61133_(map);
        return this;
    }

    @Info(value="Get a map of this blocks properties to it's value")
    public ImmutableMap<Property<?>, Comparable<?>> getValues() {
        return this.state.m_61148_();
    }

    @Info(value="Rotate the block using the specified Rotation")
    public BlockStateModifyCallbackJS rotate(Rotation rotation) {
        this.state = this.state.m_60717_(rotation);
        return this;
    }

    @Info(value="Mirror the block using the specified Mirror")
    public BlockStateModifyCallbackJS mirror(Mirror mirror) {
        this.state = this.state.m_60715_(mirror);
        return this;
    }

    @Info(value="Updates the shape of this block. Mostly used in waterloggable blocks to update the water flow")
    public BlockStateModifyCallbackJS updateShape(Direction direction, BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        this.state = this.state.m_60728_(direction, blockState, levelAccessor, blockPos, blockPos2);
        return this;
    }
}

