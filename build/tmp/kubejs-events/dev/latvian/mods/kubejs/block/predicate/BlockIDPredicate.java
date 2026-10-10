/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.latvian.mods.kubejs.block.predicate;

import dev.latvian.mods.kubejs.block.predicate.BlockPredicate;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockIDPredicate
implements BlockPredicate {
    private final ResourceLocation id;
    private final Map<String, String> properties;
    private Block cachedBlock;
    private List<PropertyObject> cachedProperties;

    public BlockIDPredicate(ResourceLocation i) {
        this.id = i;
        this.properties = new HashMap<String, String>();
    }

    public String toString() {
        if (this.properties.isEmpty()) {
            return this.id.toString();
        }
        StringBuilder sb = new StringBuilder(this.id.toString());
        sb.append('[');
        boolean first = true;
        for (Map.Entry<String, String> entry : this.properties.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
        }
        sb.append(']');
        return sb.toString();
    }

    public BlockIDPredicate with(String key, String value) {
        this.properties.put(key, value);
        this.cachedBlock = null;
        this.cachedProperties = null;
        return this;
    }

    private Block getBlock() {
        if (this.cachedBlock == null) {
            this.cachedBlock = RegistryInfo.BLOCK.getValue(this.id);
            if (this.cachedBlock == null) {
                this.cachedBlock = Blocks.f_50016_;
            }
        }
        return this.cachedBlock;
    }

    public List<PropertyObject> getBlockProperties() {
        if (this.cachedProperties == null) {
            this.cachedProperties = new LinkedList<PropertyObject>();
            HashMap<String, Property> map = new HashMap<String, Property>();
            for (Property property : this.getBlock().m_49966_().m_61147_()) {
                map.put(property.m_61708_(), property);
            }
            for (Map.Entry entry : this.properties.entrySet()) {
                Optional o;
                Property property = (Property)map.get(entry.getKey());
                if (property == null || !(o = property.m_6215_((String)entry.getValue())).isPresent()) continue;
                PropertyObject po = new PropertyObject(property, o.get());
                this.cachedProperties.add(po);
            }
        }
        return this.cachedProperties;
    }

    public BlockState getBlockState() {
        BlockState state = this.getBlock().m_49966_();
        for (PropertyObject object : this.getBlockProperties()) {
            state = (BlockState)state.m_61124_(object.property, (Comparable)UtilsJS.cast(object.value));
        }
        return state;
    }

    @Override
    public boolean check(BlockContainerJS b) {
        return this.getBlock() != Blocks.f_50016_ && this.checkState(b.getBlockState());
    }

    public boolean checkState(BlockState state) {
        if (state.m_60734_() != this.getBlock()) {
            return false;
        }
        if (this.properties.isEmpty()) {
            return true;
        }
        for (PropertyObject object : this.getBlockProperties()) {
            if (state.m_61143_(object.property).equals(object.value)) continue;
            return false;
        }
        return true;
    }

    public record PropertyObject(Property<?> property, Object value) {
    }
}

