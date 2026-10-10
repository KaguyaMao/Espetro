/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.BaseFunction
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.NativeJavaObject
 *  dev.latvian.mods.rhino.ScriptableObject
 *  dev.latvian.mods.rhino.Undefined
 *  dev.latvian.mods.rhino.mod.util.color.Color
 *  dev.latvian.mods.rhino.mod.util.color.SimpleColor
 *  dev.latvian.mods.rhino.mod.util.color.SimpleColorWithAlpha
 *  dev.latvian.mods.rhino.mod.wrapper.ColorWrapper
 *  it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  net.minecraft.client.renderer.BiomeColors
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.FoliageColor
 *  net.minecraft.world.level.GrassColor
 *  net.minecraft.world.level.block.RedStoneWireBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.ScriptableObject;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import dev.latvian.mods.rhino.mod.util.color.SimpleColorWithAlpha;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface BlockTintFunction {
    public static final BlockTintFunction GRASS = (s, l, p, i) -> new SimpleColor(l == null || p == null ? GrassColor.m_46415_((double)0.5, (double)1.0) : BiomeColors.m_108793_((BlockAndTintGetter)l, (BlockPos)p));
    public static final Color DEFAULT_FOLIAGE_COLOR = new SimpleColor(FoliageColor.m_46113_());
    public static final BlockTintFunction FOLIAGE = (s, l, p, i) -> l == null || p == null ? DEFAULT_FOLIAGE_COLOR : new SimpleColor(BiomeColors.m_108804_((BlockAndTintGetter)l, (BlockPos)p));
    public static final Fixed EVERGREEN_FOLIAGE = new Fixed((Color)new SimpleColor(FoliageColor.m_46106_()));
    public static final Fixed BIRCH_FOLIAGE = new Fixed((Color)new SimpleColor(FoliageColor.m_46112_()));
    public static final Fixed MANGROVE_FOLIAGE = new Fixed((Color)new SimpleColor(FoliageColor.m_220346_()));
    public static final BlockTintFunction WATER = (s, l, p, i) -> l == null || p == null ? null : new SimpleColorWithAlpha(BiomeColors.m_108811_((BlockAndTintGetter)l, (BlockPos)p));
    public static final Color[] REDSTONE_COLORS = new Color[16];
    public static final BlockTintFunction REDSTONE = (state, level, pos, index) -> {
        if (REDSTONE_COLORS[0] == null) {
            for (int i = 0; i < REDSTONE_COLORS.length; ++i) {
                BlockTintFunction.REDSTONE_COLORS[i] = new SimpleColor(RedStoneWireBlock.m_55606_((int)i));
            }
        }
        return REDSTONE_COLORS[(Integer)state.m_61143_((Property)BlockStateProperties.f_61426_)];
    };

    public Color getColor(BlockState var1, @Nullable BlockAndTintGetter var2, @Nullable BlockPos var3, int var4);

    @Nullable
    public static BlockTintFunction of(Context cx, Object o) {
        if (o == null || Undefined.isUndefined((Object)o)) {
            return null;
        }
        if (o instanceof BlockTintFunction) {
            BlockTintFunction f = (BlockTintFunction)o;
            return f;
        }
        if (o instanceof List) {
            List list = (List)o;
            Mapped map = new Mapped();
            for (int i = 0; i < list.size(); ++i) {
                BlockTintFunction f = BlockTintFunction.of(cx, list.get(i));
                if (f == null) continue;
                map.map.put(i, (Object)f);
            }
            return map;
        }
        if (o instanceof CharSequence) {
            BlockTintFunction f;
            switch (o.toString()) {
                case "grass": {
                    BlockTintFunction blockTintFunction = GRASS;
                    break;
                }
                case "foliage": {
                    BlockTintFunction blockTintFunction = FOLIAGE;
                    break;
                }
                case "evergreen_foliage": {
                    BlockTintFunction blockTintFunction = EVERGREEN_FOLIAGE;
                    break;
                }
                case "birch_foliage": {
                    BlockTintFunction blockTintFunction = BIRCH_FOLIAGE;
                    break;
                }
                case "mangrove_foliage": {
                    BlockTintFunction blockTintFunction = MANGROVE_FOLIAGE;
                    break;
                }
                case "water": {
                    BlockTintFunction blockTintFunction = WATER;
                    break;
                }
                case "redstone": {
                    BlockTintFunction blockTintFunction = REDSTONE;
                    break;
                }
                default: {
                    BlockTintFunction blockTintFunction = f = null;
                }
            }
            if (f != null) {
                return f;
            }
        } else if (o instanceof BaseFunction) {
            BaseFunction function = (BaseFunction)o;
            return (BlockTintFunction)NativeJavaObject.createInterfaceAdapter((Context)cx, BlockTintFunction.class, (ScriptableObject)function);
        }
        return new Fixed(ColorWrapper.of((Object)o));
    }

    public static class Mapped
    implements BlockTintFunction {
        public final Int2ObjectMap<BlockTintFunction> map = new Int2ObjectArrayMap(1);

        @Override
        public Color getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int index) {
            BlockTintFunction f = (BlockTintFunction)this.map.get(index);
            return f == null ? null : f.getColor(state, level, pos, index);
        }
    }

    public record Fixed(Color color) implements BlockTintFunction
    {
        @Override
        public Color getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int index) {
            return this.color;
        }
    }
}

