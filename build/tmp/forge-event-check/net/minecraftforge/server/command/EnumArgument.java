/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.SharedSuggestionProvider
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo$Template
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 */
package net.minecraftforge.server.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public class EnumArgument<T extends Enum<T>>
implements ArgumentType<T> {
    private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType((found, constants) -> Component.m_237110_((String)"commands.forge.arguments.enum.invalid", (Object[])new Object[]{constants, found}));
    private final Class<T> enumClass;

    public static <R extends Enum<R>> EnumArgument<R> enumArgument(Class<R> enumClass) {
        return new EnumArgument<R>(enumClass);
    }

    private EnumArgument(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    public T parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        try {
            return Enum.valueOf(this.enumClass, name);
        }
        catch (IllegalArgumentException e) {
            throw INVALID_ENUM.createWithContext((ImmutableStringReader)reader, (Object)name, (Object)Arrays.toString(Arrays.stream((Enum[])this.enumClass.getEnumConstants()).map(Enum::name).toArray()));
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.m_82981_(Stream.of((Enum[])this.enumClass.getEnumConstants()).map(Enum::name), (SuggestionsBuilder)builder);
    }

    public Collection<String> getExamples() {
        return Stream.of((Enum[])this.enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.toList());
    }

    public static class Info<T extends Enum<T>>
    implements ArgumentTypeInfo<EnumArgument<T>, Template> {
        public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
            buffer.m_130070_(template.enumClass.getName());
        }

        public Template deserializeFromNetwork(FriendlyByteBuf buffer) {
            try {
                String name = buffer.m_130277_();
                return new Template(Class.forName(name));
            }
            catch (ClassNotFoundException e) {
                return null;
            }
        }

        public void serializeToJson(Template template, JsonObject json) {
            json.addProperty("enum", template.enumClass.getName());
        }

        public Template unpack(EnumArgument<T> argument) {
            return new Template(argument.enumClass);
        }

        public class Template
        implements ArgumentTypeInfo.Template<EnumArgument<T>> {
            final Class<T> enumClass;

            Template(Class<T> enumClass) {
                this.enumClass = enumClass;
            }

            public EnumArgument<T> instantiate(CommandBuildContext p_223435_) {
                return new EnumArgument(this.enumClass);
            }

            public ArgumentTypeInfo<EnumArgument<T>, ?> m_213709_() {
                return Info.this;
            }
        }
    }
}

