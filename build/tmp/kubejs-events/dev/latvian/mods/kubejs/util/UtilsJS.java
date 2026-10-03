/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.mojang.brigadier.StringReader
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  dev.latvian.mods.rhino.BaseFunction
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.NativeJavaObject
 *  dev.latvian.mods.rhino.ScriptableObject
 *  dev.latvian.mods.rhino.Wrapper
 *  dev.latvian.mods.rhino.mod.util.Copyable
 *  dev.latvian.mods.rhino.mod.util.MinecraftRemapper
 *  dev.latvian.mods.rhino.mod.util.NBTUtils
 *  dev.latvian.mods.rhino.mod.util.RemappingHelper
 *  dev.latvian.mods.rhino.mod.util.color.Color
 *  dev.latvian.mods.rhino.mod.util.color.SimpleColorWithAlpha
 *  dev.latvian.mods.rhino.regexp.NativeRegExp
 *  net.minecraft.ResourceLocationException
 *  net.minecraft.advancements.critereon.MinMaxBounds$Doubles
 *  net.minecraft.commands.arguments.selector.EntitySelector
 *  net.minecraft.commands.arguments.selector.EntitySelectorParser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.EndTag
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.nbt.NumericTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.valueproviders.ClampedInt
 *  net.minecraft.util.valueproviders.ClampedNormalInt
 *  net.minecraft.util.valueproviders.ConstantInt
 *  net.minecraft.util.valueproviders.IntProvider
 *  net.minecraft.util.valueproviders.UniformInt
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.MobType
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.storage.loot.Deserializers
 *  net.minecraft.world.level.storage.loot.LootDataManager
 *  net.minecraft.world.level.storage.loot.LootParams$Builder
 *  net.minecraft.world.level.storage.loot.LootTable
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 *  net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator
 *  net.minecraft.world.level.storage.loot.providers.number.ConstantValue
 *  net.minecraft.world.level.storage.loot.providers.number.NumberProvider
 *  net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.util;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.BlockEvents;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.block.BlockModificationEventJS;
import dev.latvian.mods.kubejs.item.ItemModificationEventJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.JSObjectType;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.ScriptableObject;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.mod.util.Copyable;
import dev.latvian.mods.rhino.mod.util.MinecraftRemapper;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColorWithAlpha;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.ClampedInt;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class UtilsJS {
    public static final Random RANDOM = new Random();
    public static final Pattern REGEX_PATTERN = Pattern.compile("/(.*)/([a-z]*)");
    public static final ResourceLocation AIR_LOCATION = new ResourceLocation("minecraft:air");
    public static final Pattern SNAKE_CASE_SPLIT = Pattern.compile("[:_/]");
    public static final Set<String> ALWAYS_LOWER_CASE = new HashSet<String>(Arrays.asList("a", "an", "the", "of", "on", "in", "and", "or", "but", "for"));
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static MinecraftServer staticServer = null;
    public static RegistryAccess staticRegistryAccess = RegistryAccess.f_243945_;
    public static final ResourceLocation UNKNOWN_ID = new ResourceLocation("unknown", "unknown");
    public static final Predicate<Object> ALWAYS_TRUE = o -> true;
    public static final Pattern TEMPORAL_AMOUNT_PATTERN = Pattern.compile("(\\d+)\\s*(y|M|d|w|h|m|s|ms|ns|t)\\b?");
    private static Collection<BlockState> ALL_STATE_CACHE = null;
    private static final Map<String, EntitySelector> ENTITY_SELECTOR_CACHE = new HashMap<String, EntitySelector>();
    private static final EntitySelector ALL_ENTITIES_SELECTOR = new EntitySelector(Integer.MAX_VALUE, true, false, e -> true, MinMaxBounds.Doubles.f_154779_, Function.identity(), null, EntitySelectorParser.f_121199_, false, null, null, null, true);

    public static void tryIO(TryIO tryIO) {
        try {
            tryIO.run();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static <T> T cast(Object o) {
        return (T)o;
    }

    @Nullable
    public static Pattern parseRegex(Object o) {
        if (o instanceof CharSequence || o instanceof NativeRegExp) {
            return UtilsJS.regex(o.toString());
        }
        if (o instanceof Pattern) {
            Pattern pattern = (Pattern)o;
            return pattern;
        }
        return null;
    }

    @Nullable
    public static Pattern regex(String string) {
        if (string.length() < 3) {
            return null;
        }
        Matcher matcher = REGEX_PATTERN.matcher(string);
        if (matcher.matches()) {
            int flags = 0;
            String f = matcher.group(2);
            block9: for (int i = 0; i < f.length(); ++i) {
                switch (f.charAt(i)) {
                    case 'd': {
                        flags |= 1;
                        continue block9;
                    }
                    case 'i': {
                        flags |= 2;
                        continue block9;
                    }
                    case 'x': {
                        flags |= 4;
                        continue block9;
                    }
                    case 'm': {
                        flags |= 8;
                        continue block9;
                    }
                    case 's': {
                        flags |= 0x20;
                        continue block9;
                    }
                    case 'u': {
                        flags |= 0x40;
                        continue block9;
                    }
                    case 'U': {
                        flags |= 0x100;
                    }
                }
            }
            return Pattern.compile(matcher.group(1), flags);
        }
        return null;
    }

    public static String toRegexString(Pattern pattern) {
        StringBuilder sb = new StringBuilder("/");
        sb.append(pattern.pattern());
        sb.append('/');
        int flags = pattern.flags();
        if ((flags & 1) != 0) {
            sb.append('d');
        }
        if ((flags & 2) != 0) {
            sb.append('i');
        }
        if ((flags & 4) != 0) {
            sb.append('x');
        }
        if ((flags & 8) != 0) {
            sb.append('m');
        }
        if ((flags & 0x20) != 0) {
            sb.append('s');
        }
        if ((flags & 0x40) != 0) {
            sb.append('u');
        }
        if ((flags & 0x100) != 0) {
            sb.append('U');
        }
        return sb.toString();
    }

    public static void queueIO(Runnable runnable) {
        try {
            runnable.run();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Nullable
    public static Path getPath(Object o) {
        try {
            if (o instanceof Path) {
                return KubeJS.verifyFilePath((Path)o);
            }
            if (o == null || o.toString().isEmpty()) {
                return null;
            }
            return KubeJS.verifyFilePath(KubeJS.getGameDirectory().resolve(o.toString()));
        }
        catch (Exception ex) {
            return null;
        }
    }

    @Nullable
    public static File getFileFromPath(Object o) {
        try {
            if (o instanceof File) {
                return KubeJS.verifyFilePath(((File)o).toPath()).toFile();
            }
            if (o == null || o.toString().isEmpty()) {
                return null;
            }
            return KubeJS.verifyFilePath(KubeJS.getGameDirectory().resolve(o.toString())).toFile();
        }
        catch (Exception ex) {
            return null;
        }
    }

    @Nullable
    public static Object copy(@Nullable Object o) {
        if (o instanceof Copyable) {
            Copyable copyable = (Copyable)o;
            return copyable.copy();
        }
        if (o instanceof JsonElement) {
            JsonElement json = (JsonElement)o;
            return JsonIO.copy(json);
        }
        if (o instanceof Tag) {
            Tag tag = (Tag)o;
            return tag.m_6426_();
        }
        return o;
    }

    @Nullable
    public static Object wrap(@Nullable Object o, JSObjectType type) {
        if (o == null || o instanceof WrappedJS || o instanceof Tag || o instanceof Number || o instanceof Character || o instanceof String || o instanceof Enum || o.getClass().isPrimitive() && !o.getClass().isArray()) {
            return o;
        }
        if (o instanceof CharSequence || o instanceof ResourceLocation) {
            return o.toString();
        }
        if (o instanceof Wrapper) {
            Wrapper w = (Wrapper)o;
            return UtilsJS.wrap(w.unwrap(), type);
        }
        if (o instanceof Map) {
            return o;
        }
        if (o instanceof Iterable) {
            Iterable itr = (Iterable)o;
            if (!type.checkList()) {
                return null;
            }
            ArrayList list = new ArrayList();
            for (Object o1 : itr) {
                list.add(o1);
            }
            return list;
        }
        if (o.getClass().isArray()) {
            if (type.checkList()) {
                return ListJS.ofArray(o);
            }
            return null;
        }
        if (o instanceof JsonPrimitive) {
            JsonPrimitive json = (JsonPrimitive)o;
            return JsonIO.toPrimitive((JsonElement)json);
        }
        if (o instanceof JsonObject) {
            JsonObject json = (JsonObject)o;
            if (!type.checkMap()) {
                return null;
            }
            HashMap map = new HashMap(json.size());
            for (Map.Entry entry : json.entrySet()) {
                map.put((String)entry.getKey(), entry.getValue());
            }
            return map;
        }
        if (o instanceof JsonNull || o instanceof EndTag) {
            return null;
        }
        if (o instanceof CompoundTag) {
            CompoundTag tag = (CompoundTag)o;
            if (!type.checkMap()) {
                return null;
            }
            HashMap<String, Tag> map = new HashMap<String, Tag>(tag.m_128440_());
            for (String s : tag.m_128431_()) {
                map.put(s, tag.m_128423_(s));
            }
            return map;
        }
        if (o instanceof NumericTag) {
            NumericTag tag = (NumericTag)o;
            return tag.m_8103_();
        }
        if (o instanceof StringTag) {
            StringTag tag = (StringTag)o;
            return tag.m_7916_();
        }
        return o;
    }

    public static int parseInt(@Nullable Object object, int def) {
        if (object == null) {
            return def;
        }
        if (object instanceof Number) {
            Number num = (Number)object;
            return num.intValue();
        }
        try {
            String s = object.toString();
            if (s.isEmpty()) {
                return def;
            }
            return Integer.parseInt(s);
        }
        catch (Exception ex) {
            return def;
        }
    }

    public static long parseLong(@Nullable Object object, long def) {
        if (object == null) {
            return def;
        }
        if (object instanceof Number) {
            Number num = (Number)object;
            return num.longValue();
        }
        try {
            String s = object.toString();
            if (s.isEmpty()) {
                return def;
            }
            return Long.parseLong(s);
        }
        catch (Exception ex) {
            return def;
        }
    }

    public static double parseDouble(@Nullable Object object, double def) {
        if (object == null) {
            return def;
        }
        if (object instanceof Number) {
            Number num = (Number)object;
            return num.doubleValue();
        }
        try {
            String s = object.toString();
            if (s.isEmpty()) {
                return def;
            }
            return Double.parseDouble(String.valueOf(object));
        }
        catch (Exception ex) {
            return def;
        }
    }

    public static String getID(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return "minecraft:air";
        }
        if (s.indexOf(58) == -1) {
            return "minecraft:" + s;
        }
        return s;
    }

    public static ResourceLocation getMCID(@Nullable Context cx, @Nullable Object o) {
        String string;
        if (o == null) {
            return null;
        }
        if (o instanceof ResourceLocation) {
            ResourceLocation id = (ResourceLocation)o;
            return id;
        }
        if (o instanceof ResourceKey) {
            ResourceKey key = (ResourceKey)o;
            return key.m_135782_();
        }
        if (o instanceof JsonPrimitive) {
            JsonPrimitive p = (JsonPrimitive)o;
            string = p.getAsString();
        } else {
            string = o.toString();
        }
        String s = string;
        try {
            return new ResourceLocation(s);
        }
        catch (ResourceLocationException ex) {
            ConsoleJS.getCurrent(cx).error("Could not create ID from '%s'!".formatted(s), ex);
            return null;
        }
    }

    public static String getNamespace(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return "minecraft";
        }
        int i = s.indexOf(58);
        return i == -1 ? "minecraft" : s.substring(0, i);
    }

    public static String getPath(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return "air";
        }
        int i = s.indexOf(58);
        return i == -1 ? s : s.substring(i + 1);
    }

    public static BlockState parseBlockState(String string) {
        if (string.isEmpty()) {
            return Blocks.f_50016_.m_49966_();
        }
        int i = string.indexOf(91);
        boolean hasProperties = i >= 0 && string.indexOf(93) == string.length() - 1;
        BlockState state = RegistryInfo.BLOCK.getValue(new ResourceLocation(hasProperties ? string.substring(0, i) : string)).m_49966_();
        if (hasProperties) {
            for (String s : string.substring(i + 1, string.length() - 1).split(",")) {
                Optional o;
                Property p;
                String[] s1 = s.split("=", 2);
                if (s1.length != 2 || s1[0].isEmpty() || s1[1].isEmpty() || (p = state.m_60734_().m_49965_().m_61081_(s1[0])) == null || !(o = p.m_6215_(s1[1])).isPresent()) continue;
                state = (BlockState)state.m_61124_(p, (Comparable)UtilsJS.cast(o.get()));
            }
        }
        return state;
    }

    public static <T> Predicate<T> onMatchDo(Predicate<T> predicate, Consumer<T> onMatch) {
        return t -> {
            boolean match = predicate.test(t);
            if (match) {
                onMatch.accept(t);
            }
            return match;
        };
    }

    public static List<ItemStack> rollChestLoot(ResourceLocation id, @Nullable Entity entity) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        if (staticServer != null) {
            LootDataManager tables = staticServer.m_278653_();
            LootTable table = tables.m_278676_(id);
            LootParams.Builder builder = entity != null ? new LootParams.Builder((ServerLevel)entity.m_9236_()).m_287289_(LootContextParams.f_81455_, (Object)entity).m_287286_(LootContextParams.f_81460_, (Object)entity.m_20182_()) : new LootParams.Builder(staticServer.m_129783_()).m_287289_(LootContextParams.f_81455_, null).m_287286_(LootContextParams.f_81460_, (Object)Vec3.f_82478_);
            table.m_287228_(builder.m_287235_(LootContextParamSets.f_81411_), list::add);
        }
        return list;
    }

    public static void postModificationEvents() {
        BlockEvents.MODIFICATION.post(ScriptType.STARTUP, new BlockModificationEventJS());
        ItemEvents.MODIFICATION.post(ScriptType.STARTUP, new ItemModificationEventJS());
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            Class clz = (Class)type;
            return clz;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType)type;
            Type rawType = paramType.getRawType();
            if (rawType instanceof Class) {
                Class clz = (Class)rawType;
                return clz;
            }
        } else {
            if (type instanceof GenericArrayType) {
                GenericArrayType arrType = (GenericArrayType)type;
                Type componentType = arrType.getGenericComponentType();
                return Array.newInstance(UtilsJS.getRawType(componentType), 0).getClass();
            }
            if (type instanceof TypeVariable) {
                return Object.class;
            }
            if (type instanceof WildcardType) {
                WildcardType wildcard = (WildcardType)type;
                return UtilsJS.getRawType(wildcard.getUpperBounds()[0]);
            }
        }
        String className = type == null ? "null" : type.getClass().getName();
        throw new IllegalArgumentException("Expected a Class, ParameterizedType, GenericArrayType, TypeVariable or WildcardType, but <" + String.valueOf(type) + "> is of type " + className);
    }

    public static String toMappedTypeString(Type type) {
        MinecraftRemapper remapper = RemappingHelper.getMinecraftRemapper();
        if (type instanceof Class) {
            Class clz = (Class)type;
            String mapped = remapper.getMappedClass(clz);
            if (Strings.isNullOrEmpty((String)mapped)) {
                return clz.getSimpleName();
            }
            return mapped.substring(mapped.lastIndexOf(46) + 1);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType)type;
            StringBuilder sb = new StringBuilder();
            Type owner = paramType.getOwnerType();
            if (owner != null) {
                sb.append(UtilsJS.toMappedTypeString(owner));
                sb.append('.');
            }
            sb.append(UtilsJS.toMappedTypeString(UtilsJS.getRawType(paramType)));
            Type[] args = paramType.getActualTypeArguments();
            if (args.length > 0) {
                sb.append('<');
                for (int i = 0; i < args.length; ++i) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(UtilsJS.toMappedTypeString(args[i]));
                }
                sb.append('>');
            }
            return sb.toString();
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType arrType = (GenericArrayType)type;
            return UtilsJS.toMappedTypeString(arrType.getGenericComponentType()) + "[]";
        }
        if (type instanceof TypeVariable) {
            TypeVariable typeVar = (TypeVariable)type;
            StringBuilder sb = new StringBuilder(typeVar.getName());
            Type[] bounds = typeVar.getBounds();
            if (!(bounds.length <= 0 || bounds.length == 1 && Object.class.equals((Object)bounds[0]))) {
                sb.append(" extends ");
                for (int i = 0; i < bounds.length; ++i) {
                    if (i > 0) {
                        sb.append(" & ");
                    }
                    sb.append(UtilsJS.toMappedTypeString(bounds[i]));
                }
            }
            return sb.toString();
        }
        if (type instanceof WildcardType) {
            WildcardType wildcard = (WildcardType)type;
            StringBuilder sb = new StringBuilder().append("?");
            Type[] lowerBounds = wildcard.getLowerBounds();
            Type[] upperBounds = wildcard.getUpperBounds();
            if (lowerBounds.length > 1 || lowerBounds.length == 1 && lowerBounds[0] != null) {
                sb.append(" super ");
                for (int i = 0; i < lowerBounds.length; ++i) {
                    if (i > 0) {
                        sb.append(" & ");
                    }
                    sb.append(UtilsJS.toMappedTypeString(lowerBounds[i]));
                }
            } else if (upperBounds.length > 1 || upperBounds.length == 1 && !Object.class.equals((Object)upperBounds[0])) {
                sb.append(" extends ");
                for (int i = 0; i < upperBounds.length; ++i) {
                    if (i > 0) {
                        sb.append(" & ");
                    }
                    sb.append(UtilsJS.toMappedTypeString(upperBounds[i]));
                }
            }
            return sb.toString();
        }
        String className = type == null ? "null" : type.getClass().getName();
        throw new IllegalArgumentException("Expected a Class, ParameterizedType, GenericArrayType, TypeVariable or WildcardType, but <" + String.valueOf(type) + "> is of type " + className);
    }

    public static String snakeCaseToCamelCase(String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        String[] s = SNAKE_CASE_SPLIT.split(string, 0);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String value : s) {
            if (value.isEmpty()) continue;
            if (first) {
                first = false;
                sb.append(value);
                continue;
            }
            sb.append(Character.toUpperCase(value.charAt(0)));
            sb.append(value, 1, value.length());
        }
        return sb.toString();
    }

    public static String snakeCaseToTitleCase(String string) {
        StringJoiner joiner = new StringJoiner(" ");
        String[] split = string.split("_");
        for (int i = 0; i < split.length; ++i) {
            String s = split[i];
            String titleCase = UtilsJS.toTitleCase(s, i == 0);
            joiner.add(titleCase);
        }
        return joiner.toString();
    }

    public static IntProvider intProviderOf(Object o) {
        List l;
        if (o instanceof Number) {
            Number n = (Number)o;
            return ConstantInt.m_146483_((int)n.intValue());
        }
        if (o instanceof List && !(l = (List)o).isEmpty()) {
            Number min = (Number)l.get(0);
            Number max = l.size() >= 2 ? (Number)((Number)l.get(1)) : (Number)min;
            return UniformInt.m_146622_((int)min.intValue(), (int)max.intValue());
        }
        if (o instanceof Map) {
            Optional decoded;
            Map m = (Map)o;
            UniformInt intBounds = UtilsJS.parseIntBounds(m);
            if (intBounds != null) {
                return intBounds;
            }
            if (m.containsKey("clamped")) {
                IntProvider source = UtilsJS.intProviderOf(m.get("clamped"));
                UniformInt clampTo = UtilsJS.parseIntBounds(m);
                if (clampTo != null) {
                    return ClampedInt.m_146395_((IntProvider)source, (int)clampTo.m_142739_(), (int)clampTo.m_142737_());
                }
            } else if (m.containsKey("clamped_normal")) {
                UniformInt clampTo = UtilsJS.parseIntBounds(m);
                int mean = ((Number)m.get("mean")).intValue();
                int deviation = ((Number)m.get("deviation")).intValue();
                if (clampTo != null) {
                    return ClampedNormalInt.m_185879_((float)mean, (float)deviation, (int)clampTo.m_142739_(), (int)clampTo.m_142737_());
                }
            }
            if ((decoded = IntProvider.f_146531_.parse((DynamicOps)NbtOps.f_128958_, (Object)NBTUtils.toTagCompound((Object)m)).result()).isPresent()) {
                return (IntProvider)decoded.get();
            }
        }
        return ConstantInt.m_146483_((int)0);
    }

    private static UniformInt parseIntBounds(Map<String, Object> m) {
        Object object = m.get("bounds");
        if (object instanceof List) {
            List bounds = (List)object;
            return UniformInt.m_146622_((int)UtilsJS.parseInt(bounds.get(0), 0), (int)UtilsJS.parseInt(bounds.get(1), 0));
        }
        if (m.containsKey("min") && m.containsKey("max")) {
            return UniformInt.m_146622_((int)((Number)m.get("min")).intValue(), (int)((Number)m.get("max")).intValue());
        }
        if (m.containsKey("min_inclusive") && m.containsKey("max_inclusive")) {
            return UniformInt.m_146622_((int)((Number)m.get("min_inclusive")).intValue(), (int)((Number)m.get("max_inclusive")).intValue());
        }
        if (m.containsKey("value")) {
            int f = ((Number)m.get("value")).intValue();
            return UniformInt.m_146622_((int)f, (int)f);
        }
        return null;
    }

    public static NumberProvider numberProviderOf(Object o) {
        List l;
        if (o instanceof Number) {
            Number n = (Number)o;
            float f = n.floatValue();
            return UniformGenerator.m_165780_((float)f, (float)f);
        }
        if (o instanceof List && !(l = (List)o).isEmpty()) {
            Number min = (Number)l.get(0);
            Number max = l.size() >= 2 ? (Number)((Number)l.get(1)) : (Number)min;
            return UniformGenerator.m_165780_((float)min.floatValue(), (float)max.floatValue());
        }
        if (o instanceof Map) {
            Map m = (Map)o;
            if (m.containsKey("min") && m.containsKey("max")) {
                return UniformGenerator.m_165780_((float)((Number)m.get("min")).intValue(), (float)((Number)m.get("max")).floatValue());
            }
            if (m.containsKey("n") && m.containsKey("p")) {
                return BinomialDistributionGenerator.m_165659_((int)((Number)m.get("n")).intValue(), (float)((Number)m.get("p")).floatValue());
            }
            if (m.containsKey("value")) {
                float f = ((Number)m.get("value")).floatValue();
                return UniformGenerator.m_165780_((float)f, (float)f);
            }
        }
        return ConstantValue.m_165692_((float)0.0f);
    }

    public static JsonElement numberProviderJson(NumberProvider gen) {
        return Deserializers.m_78798_().create().toJsonTree((Object)gen);
    }

    public static Vec3 vec3Of(@Nullable Object o) {
        List list;
        if (o instanceof Vec3) {
            Vec3 vec = (Vec3)o;
            return vec;
        }
        if (o instanceof Entity) {
            Entity entity = (Entity)o;
            return entity.m_20182_();
        }
        if (o instanceof List && (list = (List)o).size() >= 3) {
            return new Vec3(UtilsJS.parseDouble(list.get(0), 0.0), UtilsJS.parseDouble(list.get(1), 0.0), UtilsJS.parseDouble(list.get(2), 0.0));
        }
        if (o instanceof BlockPos) {
            BlockPos pos = (BlockPos)o;
            return new Vec3((double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 0.5, (double)pos.m_123343_() + 0.5);
        }
        if (o instanceof BlockContainerJS) {
            BlockContainerJS block = (BlockContainerJS)o;
            return new Vec3((double)block.getPos().m_123341_() + 0.5, (double)block.getPos().m_123342_() + 0.5, (double)block.getPos().m_123343_() + 0.5);
        }
        return Vec3.f_82478_;
    }

    public static BlockPos blockPosOf(@Nullable Object o) {
        List list;
        if (o instanceof BlockPos) {
            BlockPos pos = (BlockPos)o;
            return pos;
        }
        if (o instanceof List && (list = (List)o).size() >= 3) {
            return new BlockPos(UtilsJS.parseInt(list.get(0), 0), UtilsJS.parseInt(list.get(1), 0), UtilsJS.parseInt(list.get(2), 0));
        }
        if (o instanceof BlockContainerJS) {
            BlockContainerJS block = (BlockContainerJS)o;
            return block.getPos();
        }
        if (o instanceof Vec3) {
            Vec3 vec = (Vec3)o;
            return BlockPos.m_274561_((double)vec.f_82479_, (double)vec.f_82480_, (double)vec.f_82481_);
        }
        return BlockPos.f_121853_;
    }

    public static Collection<BlockState> getAllBlockStates() {
        if (ALL_STATE_CACHE != null) {
            return ALL_STATE_CACHE;
        }
        HashSet states = new HashSet();
        for (Block block : RegistryInfo.BLOCK.getArchitecturyRegistrar()) {
            states.addAll(block.m_49965_().m_61056_());
        }
        ALL_STATE_CACHE = Collections.unmodifiableCollection(states);
        return ALL_STATE_CACHE;
    }

    public static String toTitleCase(String s) {
        return UtilsJS.toTitleCase(s, false);
    }

    public static String toTitleCase(String s, boolean ignoreSpecial) {
        if (s.isEmpty()) {
            return "";
        }
        if (!ignoreSpecial && ALWAYS_LOWER_CASE.contains(s)) {
            return s;
        }
        if (s.length() == 1) {
            return s.toUpperCase();
        }
        char[] chars = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String getMobTypeId(MobType type) {
        if (type == MobType.f_21641_) {
            return "undead";
        }
        if (type == MobType.f_21642_) {
            return "arthropod";
        }
        if (type == MobType.f_21643_) {
            return "illager";
        }
        if (type == MobType.f_21644_) {
            return "water";
        }
        return "unknown";
    }

    public static MobCategory mobCategoryByName(String s) {
        return MiscPlatformHelper.get().getMobCategory(s);
    }

    public static String stripIdForEvent(ResourceLocation id) {
        return UtilsJS.stripEventName(id.toString());
    }

    public static String getUniqueId(JsonElement json) {
        return UtilsJS.getUniqueId(json, Function.identity());
    }

    public static <T> String getUniqueId(T input, Codec<T> codec) {
        return UtilsJS.getUniqueId(input, (T o) -> (JsonElement)codec.encodeStart((DynamicOps)JsonOps.COMPRESSED, o).getOrThrow(false, str -> {
            throw new RuntimeException("Could not encode element to JSON: " + str);
        }));
    }

    private static <T> String getUniqueId(T input, Function<T, JsonElement> toJson) {
        return JsonIO.getJsonHashString(toJson.apply(input));
    }

    public static String stripEventName(String s) {
        return s.replaceAll("[/:]", ".").replace('-', '_');
    }

    public static EntitySelector entitySelector(@Nullable Object o) {
        if (o == null) {
            return ALL_ENTITIES_SELECTOR;
        }
        if (o instanceof EntitySelector) {
            EntitySelector s = (EntitySelector)o;
            return s;
        }
        String s = o.toString();
        if (s.isBlank()) {
            return ALL_ENTITIES_SELECTOR;
        }
        EntitySelector sel = ENTITY_SELECTOR_CACHE.get(s);
        if (sel == null) {
            sel = ALL_ENTITIES_SELECTOR;
            try {
                sel = new EntitySelectorParser(new StringReader(s), true).m_121377_();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        ENTITY_SELECTOR_CACHE.put(s, sel);
        return sel;
    }

    @Nullable
    public static CreativeModeTab findCreativeTab(ResourceLocation id) {
        return RegistryInfo.CREATIVE_MODE_TAB.getValue(id);
    }

    public static <T> T makeFunctionProxy(ScriptType type, Class<T> targetClass, BaseFunction function) {
        return UtilsJS.cast(NativeJavaObject.createInterfaceAdapter((Context)type.manager.get().context, targetClass, (ScriptableObject)function));
    }

    public static TemporalAmount getTemporalAmount(Object o) {
        if (o instanceof TemporalAmount) {
            TemporalAmount d = (TemporalAmount)o;
            return d;
        }
        if (o instanceof Number) {
            Number n = (Number)o;
            return Duration.ofMillis(n.longValue());
        }
        if (o instanceof CharSequence) {
            Matcher matcher = TEMPORAL_AMOUNT_PATTERN.matcher(o.toString());
            long millis = 0L;
            long nanos = 0L;
            long ticks = -1L;
            block24: while (matcher.find()) {
                double amount = Double.parseDouble(matcher.group(1));
                switch (matcher.group(2)) {
                    case "t": {
                        if (ticks == -1L) {
                            ticks = 0L;
                        }
                        ticks += (long)amount;
                        continue block24;
                    }
                    case "ns": {
                        nanos += (long)amount;
                        continue block24;
                    }
                    case "ms": {
                        millis += (long)amount;
                        continue block24;
                    }
                    case "s": {
                        millis += (long)(amount * 1000.0);
                        continue block24;
                    }
                    case "m": {
                        millis += (long)(amount * 60000.0);
                        continue block24;
                    }
                    case "h": {
                        millis += (long)(amount * 60000.0) * 60L;
                        continue block24;
                    }
                    case "d": {
                        millis += (long)(amount * 86400.0) * 1000L;
                        continue block24;
                    }
                    case "w": {
                        millis += (long)(amount * 86400.0) * 7000L;
                        continue block24;
                    }
                    case "M": {
                        millis += (long)(amount * 3.1556952E7 / 12.0) * 1000L;
                        continue block24;
                    }
                    case "y": {
                        millis += (long)(amount * 3.1556952E7) * 1000L;
                        continue block24;
                    }
                }
                throw new IllegalArgumentException("Invalid temporal unit: " + matcher.group(2));
            }
            if (ticks != -1L) {
                return new TickDuration(ticks + millis / 50L);
            }
            return Duration.ofMillis(millis).plusNanos(nanos);
        }
        throw new IllegalArgumentException("Invalid temporal amount: " + String.valueOf(o));
    }

    public static long getTickDuration(Object o) {
        if (o instanceof Number) {
            Number n = (Number)o;
            return n.longValue();
        }
        if (o instanceof JsonPrimitive) {
            JsonPrimitive json = (JsonPrimitive)o;
            return json.getAsLong();
        }
        TemporalAmount t = UtilsJS.getTemporalAmount(o);
        if (t instanceof TickDuration) {
            TickDuration d = (TickDuration)t;
            return d.ticks();
        }
        if (t instanceof Duration) {
            Duration d = (Duration)t;
            return d.toMillis() / 50L;
        }
        return 0L;
    }

    public static Duration getDuration(Object o) {
        TemporalAmount t = UtilsJS.getTemporalAmount(o);
        if (t instanceof Duration) {
            Duration d = (Duration)t;
            return d;
        }
        if (t instanceof TickDuration) {
            TickDuration d = (TickDuration)t;
            return Duration.ofMillis(d.ticks() * 50L);
        }
        Duration d = Duration.ZERO;
        for (TemporalUnit unit : t.getUnits()) {
            d = d.plus(t.get(unit), unit);
        }
        return d;
    }

    public static void writeColor(FriendlyByteBuf buf, Color color) {
        buf.writeInt(color.getArgbJS());
    }

    public static Color readColor(FriendlyByteBuf buf) {
        return new SimpleColorWithAlpha(buf.readInt());
    }

    public static void appendTimestamp(StringBuilder builder, Calendar calendar) {
        int h = calendar.get(11);
        int m = calendar.get(12);
        int s = calendar.get(13);
        if (h < 10) {
            builder.append('0');
        }
        builder.append(h);
        builder.append(':');
        if (m < 10) {
            builder.append('0');
        }
        builder.append(m);
        builder.append(':');
        if (s < 10) {
            builder.append('0');
        }
        builder.append(s);
    }

    @FunctionalInterface
    public static interface TryIO {
        public void run() throws IOException;
    }
}

