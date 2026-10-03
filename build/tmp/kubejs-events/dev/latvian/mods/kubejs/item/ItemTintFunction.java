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
 *  dev.latvian.mods.rhino.mod.wrapper.ColorWrapper
 *  it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.MapItem
 *  net.minecraft.world.item.alchemy.PotionUtils
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.ScriptableObject;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ItemTintFunction {
    public static final ItemTintFunction BLOCK = (stack, index) -> {
        BlockItem block;
        BlockState s;
        BlockBuilder internal;
        Item patt1330$temp = stack.m_41720_();
        if (patt1330$temp instanceof BlockItem && (internal = (s = (block = (BlockItem)patt1330$temp).m_40614_().m_49966_()).m_60734_().kjs$getBlockBuilder()) != null && internal.tint != null) {
            return internal.tint.getColor(s, null, null, index);
        }
        return null;
    };
    public static final ItemTintFunction POTION = (stack, index) -> new SimpleColor(PotionUtils.m_43575_((ItemStack)stack));
    public static final ItemTintFunction MAP = (stack, index) -> new SimpleColor(MapItem.m_42918_((ItemStack)stack));
    public static final ItemTintFunction DISPLAY_COLOR_NBT = (stack, index) -> {
        CompoundTag tag = stack.m_41737_("display");
        if (tag != null && tag.m_128425_("color", 99)) {
            return new SimpleColor(tag.m_128451_("color"));
        }
        return null;
    };

    public Color getColor(ItemStack var1, int var2);

    @Nullable
    public static ItemTintFunction of(Context cx, Object o) {
        if (o == null || Undefined.isUndefined((Object)o)) {
            return null;
        }
        if (o instanceof ItemTintFunction) {
            ItemTintFunction f = (ItemTintFunction)o;
            return f;
        }
        if (o instanceof List) {
            List list = (List)o;
            Mapped map = new Mapped();
            for (int i = 0; i < list.size(); ++i) {
                ItemTintFunction f = ItemTintFunction.of(cx, list.get(i));
                if (f == null) continue;
                map.map.put(i, (Object)f);
            }
            return map;
        }
        if (o instanceof CharSequence) {
            ItemTintFunction f;
            switch (o.toString()) {
                case "block": {
                    ItemTintFunction itemTintFunction = BLOCK;
                    break;
                }
                case "potion": {
                    ItemTintFunction itemTintFunction = POTION;
                    break;
                }
                case "map": {
                    ItemTintFunction itemTintFunction = MAP;
                    break;
                }
                case "display_color_nbt": {
                    ItemTintFunction itemTintFunction = DISPLAY_COLOR_NBT;
                    break;
                }
                default: {
                    ItemTintFunction itemTintFunction = f = null;
                }
            }
            if (f != null) {
                return f;
            }
        } else if (o instanceof BaseFunction) {
            BaseFunction function = (BaseFunction)o;
            return (ItemTintFunction)NativeJavaObject.createInterfaceAdapter((Context)cx, ItemTintFunction.class, (ScriptableObject)function);
        }
        return new Fixed(ColorWrapper.of((Object)o));
    }

    public static class Mapped
    implements ItemTintFunction {
        public final Int2ObjectMap<ItemTintFunction> map = new Int2ObjectArrayMap(1);

        @Override
        public Color getColor(ItemStack stack, int index) {
            ItemTintFunction f = (ItemTintFunction)this.map.get(index);
            return f == null ? null : f.getColor(stack, index);
        }
    }

    public record Fixed(Color color) implements ItemTintFunction
    {
        @Override
        public Color getColor(ItemStack stack, int index) {
            return this.color;
        }
    }
}

