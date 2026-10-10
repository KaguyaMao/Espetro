/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.Undefined
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.MapColor
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.rhino.Undefined;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record MapColorHelper(int id, String name, MapColor color, Vector3f rgb) implements Function<BlockState, MapColor>
{
    public static final Map<String, MapColorHelper> NAME_MAP = new HashMap<String, MapColorHelper>(64);
    public static final Map<Integer, MapColorHelper> ID_MAP = new HashMap<Integer, MapColorHelper>(64);
    public static final MapColorHelper NONE = MapColorHelper.add("none", MapColor.f_283808_);

    private static MapColorHelper add(String id, MapColor color) {
        float r = (float)(color.f_283871_ >> 16 & 0xFF) / 255.0f;
        float g = (float)(color.f_283871_ >> 8 & 0xFF) / 255.0f;
        float b = (float)(color.f_283871_ & 0xFF) / 255.0f;
        MapColorHelper helper = new MapColorHelper(color.f_283805_, id, color, new Vector3f(r, g, b));
        NAME_MAP.put(id, helper);
        ID_MAP.put(color.f_283805_, helper);
        return helper;
    }

    public static MapColor of(Object o) {
        if (o == null || Undefined.isUndefined((Object)o)) {
            return MapColor.f_283808_;
        }
        if (o instanceof MapColor) {
            MapColor c = (MapColor)o;
            return c;
        }
        if (o instanceof CharSequence) {
            CharSequence s = (CharSequence)o;
            if (s.isEmpty()) {
                return MapColor.f_283808_;
            }
            if (s.charAt(0) == '#') {
                return MapColorHelper.findClosest((int)Integer.decode((String)s.toString()).intValue()).color;
            }
            return MapColorHelper.NAME_MAP.getOrDefault((Object)s.toString().toLowerCase(), (MapColorHelper)MapColorHelper.NONE).color;
        }
        if (o instanceof Number) {
            Number n = (Number)o;
            return MapColorHelper.findClosest((int)n.intValue()).color;
        }
        if (o instanceof DyeColor) {
            DyeColor c = (DyeColor)o;
            return c.m_284406_();
        }
        return MapColor.f_283808_;
    }

    public static MapColorHelper reverse(MapColor c) {
        return ID_MAP.getOrDefault(c.f_283805_, NONE);
    }

    public static MapColorHelper findClosest(int rgbi) {
        Vector3f rgb = new Vector3f((float)(rgbi >> 16 & 0xFF) / 255.0f, (float)(rgbi >> 8 & 0xFF) / 255.0f, (float)(rgbi & 0xFF) / 255.0f);
        MapColorHelper closest = null;
        float lastDist = Float.MAX_VALUE;
        for (MapColorHelper helper : NAME_MAP.values()) {
            float dist;
            if (helper.color == MapColor.f_283808_ || !((dist = helper.rgb.distanceSquared((Vector3fc)rgb)) < lastDist)) continue;
            closest = helper;
            lastDist = dist;
        }
        return closest == null ? NONE : closest;
    }

    @Override
    public MapColor apply(BlockState blockState) {
        return this.color;
    }

    static {
        MapColorHelper.add("grass", MapColor.f_283824_);
        MapColorHelper.add("sand", MapColor.f_283761_);
        MapColorHelper.add("wool", MapColor.f_283930_);
        MapColorHelper.add("fire", MapColor.f_283816_);
        MapColorHelper.add("ice", MapColor.f_283828_);
        MapColorHelper.add("metal", MapColor.f_283906_);
        MapColorHelper.add("plant", MapColor.f_283915_);
        MapColorHelper.add("snow", MapColor.f_283811_);
        MapColorHelper.add("clay", MapColor.f_283744_);
        MapColorHelper.add("dirt", MapColor.f_283762_);
        MapColorHelper.add("stone", MapColor.f_283947_);
        MapColorHelper.add("water", MapColor.f_283864_);
        MapColorHelper.add("wood", MapColor.f_283825_);
        MapColorHelper.add("quartz", MapColor.f_283942_);
        MapColorHelper.add("color_orange", MapColor.f_283750_);
        MapColorHelper.add("color_magenta", MapColor.f_283931_);
        MapColorHelper.add("color_light_blue", MapColor.f_283869_);
        MapColorHelper.add("color_yellow", MapColor.f_283832_);
        MapColorHelper.add("color_light_green", MapColor.f_283916_);
        MapColorHelper.add("color_pink", MapColor.f_283765_);
        MapColorHelper.add("color_gray", MapColor.f_283818_);
        MapColorHelper.add("color_light_gray", MapColor.f_283779_);
        MapColorHelper.add("color_cyan", MapColor.f_283772_);
        MapColorHelper.add("color_purple", MapColor.f_283889_);
        MapColorHelper.add("color_blue", MapColor.f_283743_);
        MapColorHelper.add("color_brown", MapColor.f_283748_);
        MapColorHelper.add("color_green", MapColor.f_283784_);
        MapColorHelper.add("color_red", MapColor.f_283913_);
        MapColorHelper.add("color_black", MapColor.f_283927_);
        MapColorHelper.add("gold", MapColor.f_283757_);
        MapColorHelper.add("diamond", MapColor.f_283821_);
        MapColorHelper.add("lapis", MapColor.f_283933_);
        MapColorHelper.add("emerald", MapColor.f_283812_);
        MapColorHelper.add("podzol", MapColor.f_283819_);
        MapColorHelper.add("nether", MapColor.f_283820_);
        MapColorHelper.add("terracotta_white", MapColor.f_283919_);
        MapColorHelper.add("terracotta_orange", MapColor.f_283895_);
        MapColorHelper.add("terracotta_magenta", MapColor.f_283850_);
        MapColorHelper.add("terracotta_light_blue", MapColor.f_283791_);
        MapColorHelper.add("terracotta_yellow", MapColor.f_283843_);
        MapColorHelper.add("terracotta_light_green", MapColor.f_283778_);
        MapColorHelper.add("terracotta_pink", MapColor.f_283870_);
        MapColorHelper.add("terracotta_gray", MapColor.f_283861_);
        MapColorHelper.add("terracotta_light_gray", MapColor.f_283907_);
        MapColorHelper.add("terracotta_cyan", MapColor.f_283846_);
        MapColorHelper.add("terracotta_purple", MapColor.f_283892_);
        MapColorHelper.add("terracotta_blue", MapColor.f_283908_);
        MapColorHelper.add("terracotta_brown", MapColor.f_283774_);
        MapColorHelper.add("terracotta_green", MapColor.f_283856_);
        MapColorHelper.add("terracotta_red", MapColor.f_283798_);
        MapColorHelper.add("terracotta_black", MapColor.f_283771_);
        MapColorHelper.add("crimson_nylium", MapColor.f_283909_);
        MapColorHelper.add("crimson_stem", MapColor.f_283804_);
        MapColorHelper.add("crimson_hyphae", MapColor.f_283883_);
        MapColorHelper.add("warped_nylium", MapColor.f_283745_);
        MapColorHelper.add("warped_stem", MapColor.f_283749_);
        MapColorHelper.add("warped_hyphae", MapColor.f_283807_);
        MapColorHelper.add("warped_wart_block", MapColor.f_283898_);
        MapColorHelper.add("deepslate", MapColor.f_283875_);
        MapColorHelper.add("raw_iron", MapColor.f_283877_);
        MapColorHelper.add("glow_lichen", MapColor.f_283769_);
    }
}

