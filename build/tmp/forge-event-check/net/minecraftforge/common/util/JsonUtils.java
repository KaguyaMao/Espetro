/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.reflect.TypeParameter
 *  com.google.common.reflect.TypeToken
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.TagParser
 *  net.minecraft.util.GsonHelper
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

public class JsonUtils {
    private static <E> TypeToken<List<E>> listOf(Type arg) {
        return new TypeToken<List<E>>(){}.where(new TypeParameter<E>(){}, TypeToken.of((Type)arg));
    }

    @Nullable
    public static CompoundTag readNBT(JsonObject json, String key) {
        if (GsonHelper.m_13900_((JsonObject)json, (String)key)) {
            try {
                return TagParser.m_129359_((String)GsonHelper.m_13906_((JsonObject)json, (String)key));
            }
            catch (CommandSyntaxException e) {
                throw new JsonSyntaxException("Malformed NBT tag", (Throwable)e);
            }
        }
        return null;
    }

    private static <E> TypeToken<Map<String, E>> mapOf(Type arg) {
        return new TypeToken<Map<String, E>>(){}.where(new TypeParameter<E>(){}, TypeToken.of((Type)arg));
    }

    public static enum ImmutableMapTypeAdapter implements JsonDeserializer<ImmutableMap<String, ?>>,
    JsonSerializer<ImmutableMap<String, ?>>
    {
        INSTANCE;


        public ImmutableMap<String, ?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            Type[] typeArguments = ((ParameterizedType)type).getActualTypeArguments();
            Type parameterizedType = JsonUtils.mapOf(typeArguments[1]).getType();
            Map map = (Map)context.deserialize(json, parameterizedType);
            return ImmutableMap.copyOf((Map)map);
        }

        public JsonElement serialize(ImmutableMap<String, ?> src, Type type, JsonSerializationContext context) {
            Type[] typeArguments = ((ParameterizedType)type).getActualTypeArguments();
            Type parameterizedType = JsonUtils.mapOf(typeArguments[1]).getType();
            return context.serialize(src, parameterizedType);
        }
    }

    public static enum ImmutableListTypeAdapter implements JsonDeserializer<ImmutableList<?>>,
    JsonSerializer<ImmutableList<?>>
    {
        INSTANCE;


        public ImmutableList<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            Type[] typeArguments = ((ParameterizedType)type).getActualTypeArguments();
            Type parametrizedType = JsonUtils.listOf(typeArguments[0]).getType();
            List list = (List)context.deserialize(json, parametrizedType);
            return ImmutableList.copyOf((Collection)list);
        }

        public JsonElement serialize(ImmutableList<?> src, Type type, JsonSerializationContext context) {
            Type[] typeArguments = ((ParameterizedType)type).getActualTypeArguments();
            Type parametrizedType = JsonUtils.listOf(typeArguments[0]).getType();
            return context.serialize(src, parametrizedType);
        }
    }
}

