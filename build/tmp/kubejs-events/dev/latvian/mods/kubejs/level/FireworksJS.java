/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.mod.wrapper.ColorWrapper
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.entity.projectile.FireworkRocketEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 */
package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.core.FireworkRocketEntityKJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class FireworksJS {
    public int flight = 2;
    public int lifetime = -1;
    public final List<Explosion> explosions = new ArrayList<Explosion>();

    public static FireworksJS of(Object o) {
        Map<?, ?> properties = MapJS.of(o);
        FireworksJS fireworks = new FireworksJS();
        if (properties == null) {
            return fireworks;
        }
        Object obj = properties.get("flight");
        if (obj instanceof Number) {
            Number flight = (Number)obj;
            fireworks.flight = flight.intValue();
        }
        if ((obj = properties.get("lifetime")) instanceof Number) {
            Number lifetime = (Number)obj;
            fireworks.lifetime = lifetime.intValue();
        }
        if (properties.containsKey("explosions")) {
            for (Object o1 : ListJS.orSelf(properties.get("explosions"))) {
                Map<?, ?> m = MapJS.of(o1);
                if (m == null) continue;
                Explosion e = new Explosion();
                Object obj2 = m.get("shape");
                if (obj2 instanceof String) {
                    String shape = (String)obj2;
                    e.shape = Shape.get(shape);
                }
                if ((obj2 = m.get("flicker")) instanceof Boolean) {
                    Boolean flicker = (Boolean)obj2;
                    e.flicker = flicker;
                }
                if ((obj2 = m.get("trail")) instanceof Boolean) {
                    Boolean trail = (Boolean)obj2;
                    e.trail = trail;
                }
                if (m.containsKey("colors")) {
                    for (Object o2 : ListJS.orSelf(m.get("colors"))) {
                        e.colors.add(ColorWrapper.of(o2).getFireworkColorJS());
                    }
                }
                if (m.containsKey("fadeColors")) {
                    for (Object o2 : ListJS.orSelf(m.get("fadeColors"))) {
                        e.fadeColors.add(ColorWrapper.of(o2).getFireworkColorJS());
                    }
                }
                if (e.colors.isEmpty()) {
                    e.colors.add(ColorWrapper.YELLOW_DYE.getFireworkColorJS());
                }
                fireworks.explosions.add(e);
            }
        }
        if (fireworks.explosions.isEmpty()) {
            Explosion e = new Explosion();
            e.colors.add(ColorWrapper.YELLOW_DYE.getFireworkColorJS());
            fireworks.explosions.add(e);
        }
        return fireworks;
    }

    public FireworkRocketEntity createFireworkRocket(Level w, double x, double y, double z) {
        ItemStack stack = new ItemStack((ItemLike)Items.f_42688_);
        CompoundTag nbt = new CompoundTag();
        nbt.m_128405_("Flight", this.flight);
        ListTag list = new ListTag();
        for (Explosion e : this.explosions) {
            CompoundTag nbt1 = new CompoundTag();
            nbt1.m_128405_("Type", e.shape.type);
            nbt1.m_128379_("Flicker", e.flicker);
            nbt1.m_128379_("Trail", e.trail);
            nbt1.m_128385_("Colors", e.colors.toIntArray());
            nbt1.m_128385_("FadeColors", e.fadeColors.toIntArray());
            list.add((Object)nbt1);
        }
        nbt.m_128365_("Explosions", (Tag)list);
        stack.m_41700_("Fireworks", (Tag)nbt);
        FireworkRocketEntity rocket = new FireworkRocketEntity(w, x, y, z, stack);
        if (this.lifetime != -1) {
            ((FireworkRocketEntityKJS)rocket).setLifetimeKJS(this.lifetime);
        }
        rocket.m_6842_(true);
        return rocket;
    }

    public static class Explosion {
        public Shape shape = Shape.SMALL_BALL;
        public boolean flicker = false;
        public boolean trail = false;
        public final IntOpenHashSet colors = new IntOpenHashSet();
        public final IntOpenHashSet fadeColors = new IntOpenHashSet();
    }

    public static enum Shape {
        SMALL_BALL("small_ball", 0),
        LARGE_BALL("large_ball", 1),
        STAR("star", 2),
        CREEPER("creeper", 3),
        BURST("burst", 4);

        public static final Shape[] VALUES;
        private final String name;
        public final int type;

        private Shape(String n2, int t) {
            this.name = n2;
            this.type = t;
        }

        public static Shape get(String name) {
            for (Shape s : VALUES) {
                if (!s.name.equals(name)) continue;
                return s;
            }
            return SMALL_BALL;
        }

        static {
            VALUES = Shape.values();
        }
    }
}

