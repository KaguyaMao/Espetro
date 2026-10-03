/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.brigadier.StringReader
 *  dev.latvian.mods.rhino.mod.util.CountingMap
 *  net.minecraft.Util
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.stats.Stat
 *  net.minecraft.stats.Stats
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package dev.latvian.mods.kubejs.bindings;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.mod.util.CountingMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import net.minecraft.Util;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Info(value="A collection of utilities")
public interface UtilsWrapper {
    public static final DustParticleOptions ERROR_PARTICLE = new DustParticleOptions(new Vector3f(0.0f, 0.0f, 0.0f), 1.0f);

    @Info(value="Get the server. Null if there is no server (startup or client)")
    public static MinecraftServer getServer() {
        return UtilsJS.staticServer;
    }

    @Info(value="Immediately run the passed runnable function in a try-catch block, and log the exception if it throws")
    public static void queueIO(Runnable runnable) {
        UtilsJS.queueIO(runnable);
    }

    @Info(value="Get a Random, for generating random numbers. Note this will always return the same Random instance")
    public static Random getRandom() {
        return UtilsJS.RANDOM;
    }

    @Info(value="Get a new random with the specified seed")
    public static Random newRandom(long seed) {
        return new Random(seed);
    }

    @Info(value="Get an immutable empty list")
    public static <T> List<T> emptyList() {
        return List.of();
    }

    @Info(value="Get an immutable empty map")
    public static <K, V> Map<K, V> emptyMap() {
        return Map.of();
    }

    @Info(value="Returns a new mutable list")
    public static List<?> newList() {
        return new ArrayList();
    }

    @Info(value="Returns a new mutable map")
    public static Map<?, ?> newMap() {
        return new LinkedHashMap();
    }

    @Info(value="Returns a new counting map")
    public static CountingMap newCountingMap() {
        return new CountingMap();
    }

    @Info(value="Returns a ResourceLocation with the specified namepsace and path")
    public static ResourceLocation id(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    @Info(value="Typewraps the input string to a ResourceLocation. Format should be namespace:path")
    public static ResourceLocation id(ResourceLocation id) {
        return id;
    }

    @Info(value="Returns a regex pattern of the input")
    public static Pattern regex(Object s) {
        Pattern pattern = UtilsJS.parseRegex(s);
        return pattern == null ? Pattern.compile(s.toString()) : pattern;
    }

    @Info(value="Returns a regex pattern of the input with the specified flags")
    public static Pattern regex(String pattern, int flags) {
        return Pattern.compile(pattern, flags);
    }

    @Info(value="Tries to parse the first parameter as an integer, and returns that. The second parameter is returned if parsing fails")
    public static int parseInt(@Nullable Object object, int def) {
        return UtilsJS.parseInt(object, def);
    }

    @Info(value="Tries to parse the first parameter as a double and returns that. The second parameter is returned if parsing fails")
    public static double parseDouble(@Nullable Object object, double def) {
        return UtilsJS.parseDouble(object, def);
    }

    @Info(value="Returns a Stat of the passed in ResourceLocation.\nNote that this requires the same ResourceLocation to get the same stat, so should not be used unless you want to make your own stat, and are storing an actual ResourceLocation somewhere to access it.\n")
    public static Stat<ResourceLocation> getStat(ResourceLocation id) {
        return Stats.f_12988_.m_12902_((Object)id);
    }

    @Info(value="Gets a SoundEvent from the id")
    @Nullable
    public static SoundEvent getSound(ResourceLocation id) {
        return RegistryInfo.SOUND_EVENT.getValue(id);
    }

    @Info(value="Gets a random object from the list using the passed in random")
    public static Object randomOf(Random random, Collection<Object> objects) {
        if (objects.isEmpty()) {
            return null;
        }
        if (objects instanceof List) {
            List list = (List)objects;
            return list.get(random.nextInt(objects.size()));
        }
        return new ArrayList<Object>(objects).get(random.nextInt(objects.size()));
    }

    @Info(value="Gets the current system time, in milliseconds")
    public static long getSystemTime() {
        return System.currentTimeMillis();
    }

    @Info(value="Returns the results of rolling the specified loot table (it does not have to be a chest loot table)")
    public static List<ItemStack> rollChestLoot(ResourceLocation id) {
        return UtilsWrapper.rollChestLoot(id, null);
    }

    @Info(value="Returns the results of rolling the specified loot table with the entity as a parameter (it does not have to be a chest loot table)")
    public static List<ItemStack> rollChestLoot(ResourceLocation id, @Nullable Entity entity) {
        return UtilsJS.rollChestLoot(id, entity);
    }

    @Info(value="Returns a copy the object if possible, or the object itself if not")
    @Nullable
    public static Object copy(@Nullable Object o) {
        return UtilsJS.copy(o);
    }

    @Info(value="Checks if the passed in object is an instance of WrappedJS")
    public static boolean isWrapped(@Nullable Object o) {
        return o instanceof WrappedJS;
    }

    @Info(value="Capitalises the first letter of the string unless it is \"a\", \"an\", \"the\", \"of\", \"on\", \"in\", \"and\", \"or\", \"but\" or \"for\"")
    public static String toTitleCase(String s) {
        return UtilsJS.toTitleCase(s);
    }

    @Info(value="Capitalises the first letter of the string. If ignoreSpecial is true, it will also capitalise articles and prepositions")
    public static String toTitleCase(String s, boolean ignoreSpecial) {
        return UtilsJS.toTitleCase(s, ignoreSpecial);
    }

    @Info(value="Gets the specified registry")
    public static RegistryInfo<?> getRegistry(ResourceLocation id) {
        return RegistryInfo.of(ResourceKey.m_135788_((ResourceLocation)id));
    }

    @Info(value="Gets all ids from the registry with the specified id")
    public static List<ResourceLocation> getRegistryIds(ResourceLocation id) {
        Set<Map.Entry<ResourceKey<?>, ?>> entries = UtilsWrapper.getRegistry(id).entrySet();
        ArrayList<ResourceLocation> list = new ArrayList<ResourceLocation>(entries.size());
        for (Map.Entry<ResourceKey<?>, ?> entry : entries) {
            list.add(entry.getKey().m_135782_());
        }
        return list;
    }

    @Info(value="Returns a lazy value with the supplier function as its value factory")
    public static <T> Lazy<T> lazy(Supplier<T> supplier) {
        return Lazy.of(supplier);
    }

    @Info(value="Returns a lazy value with the supplier function as its value factory, that will expire after the specified number of milliseconds")
    public static <T> Lazy<T> expiringLazy(Supplier<T> supplier, long time) {
        return Lazy.of(supplier, time);
    }

    @Info(value="Returns the creative tab associated with the id")
    @Nullable
    public static CreativeModeTab findCreativeTab(ResourceLocation id) {
        return UtilsJS.findCreativeTab(id);
    }

    @Info(value="Runs the provided runnable function in KubeJS' background thread and returns its CompletableFuture")
    public static CompletableFuture<Void> runAsync(Runnable task) {
        return CompletableFuture.runAsync(task, Util.m_183991_());
    }

    @Info(value="Runs the provided supplier function in KubeJS' background thread and returns its CompletableFuture")
    public static CompletableFuture<Object> supplyAsync(Supplier<Object> task) {
        return CompletableFuture.supplyAsync(task, Util.m_183991_());
    }

    @Info(value="Returns the provided snake_case_string in camelCase")
    public static String snakeCaseToCamelCase(String string) {
        return UtilsJS.snakeCaseToCamelCase(string);
    }

    @Info(value="Returns the provided snake_case_string in Title Case")
    public static String snakeCaseToTitleCase(String string) {
        return UtilsJS.snakeCaseToTitleCase(string);
    }

    public static ParticleOptions particleOptions(Object o) {
        if (o instanceof ParticleOptions) {
            ParticleOptions po = (ParticleOptions)o;
            return po;
        }
        if (o != null) {
            try {
                String string;
                if (o instanceof JsonElement) {
                    JsonElement j = (JsonElement)o;
                    string = j.getAsString();
                } else {
                    string = o.toString();
                }
                StringReader reader = new StringReader(string);
                ParticleType particle = RegistryInfo.PARTICLE_TYPE.getValue(ResourceLocation.m_135818_((StringReader)reader));
                return particle.m_123743_().m_5739_(particle, reader);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return ERROR_PARTICLE;
    }

    @Info(value="Parses a block state from the input string. May throw for invalid inputs!")
    public static BlockState parseBlockState(Object o) {
        if (o instanceof BlockState) {
            BlockState bs = (BlockState)o;
            return bs;
        }
        return o == null ? Blocks.f_50016_.m_49966_() : UtilsJS.parseBlockState(o.toString());
    }
}

