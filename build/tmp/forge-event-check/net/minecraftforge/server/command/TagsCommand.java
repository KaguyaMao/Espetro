/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.datafixers.util.Pair
 *  net.minecraft.ChatFormatting
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.SharedSuggestionProvider
 *  net.minecraft.commands.arguments.ResourceKeyArgument
 *  net.minecraft.commands.arguments.ResourceLocationArgument
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderSet$Named
 *  net.minecraft.core.Registry
 *  net.minecraft.core.RegistryAccess$RegistryEntry
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentUtils
 *  net.minecraft.network.chat.HoverEvent
 *  net.minecraft.network.chat.HoverEvent$Action
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.Mth
 */
package net.minecraftforge.server.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;

class TagsCommand {
    private static final long PAGE_SIZE = 8L;
    private static final ResourceKey<Registry<Registry<?>>> ROOT_REGISTRY_KEY = ResourceKey.m_135788_((ResourceLocation)new ResourceLocation("root"));
    private static final DynamicCommandExceptionType UNKNOWN_REGISTRY = new DynamicCommandExceptionType(key -> Component.m_237110_((String)"commands.forge.tags.error.unknown_registry", (Object[])new Object[]{key}));
    private static final Dynamic2CommandExceptionType UNKNOWN_TAG = new Dynamic2CommandExceptionType((tag, registry) -> Component.m_237110_((String)"commands.forge.tags.error.unknown_tag", (Object[])new Object[]{tag, registry}));
    private static final Dynamic2CommandExceptionType UNKNOWN_ELEMENT = new Dynamic2CommandExceptionType((tag, registry) -> Component.m_237110_((String)"commands.forge.tags.error.unknown_element", (Object[])new Object[]{tag, registry}));

    TagsCommand() {
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.m_82127_((String)"tags").requires(cs -> cs.m_6761_(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.m_82129_((String)"registry", (ArgumentType)ResourceKeyArgument.m_212386_(ROOT_REGISTRY_KEY)).suggests(TagsCommand::suggestRegistries).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"list").executes(ctx -> TagsCommand.listTags((CommandContext<CommandSourceStack>)ctx, 1))).then(Commands.m_82129_((String)"page", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(ctx -> TagsCommand.listTags((CommandContext<CommandSourceStack>)ctx, IntegerArgumentType.getInteger((CommandContext)ctx, (String)"page")))))).then(Commands.m_82127_((String)"get").then(((RequiredArgumentBuilder)Commands.m_82129_((String)"tag", (ArgumentType)ResourceLocationArgument.m_106984_()).suggests(TagsCommand.suggestFromRegistry(r -> r.m_203613_().map(TagKey::f_203868_)::iterator)).executes(ctx -> TagsCommand.listTagElements((CommandContext<CommandSourceStack>)ctx, 1))).then(Commands.m_82129_((String)"page", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(ctx -> TagsCommand.listTagElements((CommandContext<CommandSourceStack>)ctx, IntegerArgumentType.getInteger((CommandContext)ctx, (String)"page"))))))).then(Commands.m_82127_((String)"query").then(((RequiredArgumentBuilder)Commands.m_82129_((String)"element", (ArgumentType)ResourceLocationArgument.m_106984_()).suggests(TagsCommand.suggestFromRegistry(Registry::m_6566_)).executes(ctx -> TagsCommand.queryElementTags((CommandContext<CommandSourceStack>)ctx, 1))).then(Commands.m_82129_((String)"page", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(ctx -> TagsCommand.queryElementTags((CommandContext<CommandSourceStack>)ctx, IntegerArgumentType.getInteger((CommandContext)ctx, (String)"page")))))));
    }

    private static int listTags(CommandContext<CommandSourceStack> ctx, int page) throws CommandSyntaxException {
        ResourceKey<Registry<?>> registryKey = TagsCommand.getResourceKey(ctx, "registry", ROOT_REGISTRY_KEY).orElseThrow();
        Registry registry = (Registry)((CommandSourceStack)ctx.getSource()).m_81377_().m_206579_().m_6632_(registryKey).orElseThrow(() -> UNKNOWN_REGISTRY.create((Object)registryKey.m_135782_()));
        long tagCount = registry.m_203612_().count();
        ((CommandSourceStack)ctx.getSource()).m_288197_(() -> TagsCommand.createMessage(Component.m_237110_((String)"commands.forge.tags.registry_key", (Object[])new Object[]{Component.m_237113_((String)registryKey.m_135782_().toString()).m_130940_(ChatFormatting.GOLD)}), "commands.forge.tags.tag_count", "commands.forge.tags.copy_tag_names", tagCount, page, ChatFormatting.DARK_GREEN, () -> registry.m_203612_().map(Pair::getSecond).map(s -> (String)s.m_203440_().map(k -> k.f_203868_().toString(), Object::toString))), false);
        return (int)tagCount;
    }

    private static int listTagElements(CommandContext<CommandSourceStack> ctx, int page) throws CommandSyntaxException {
        ResourceKey<Registry<?>> registryKey = TagsCommand.getResourceKey(ctx, "registry", ROOT_REGISTRY_KEY).orElseThrow();
        Registry registry = (Registry)((CommandSourceStack)ctx.getSource()).m_81377_().m_206579_().m_6632_(registryKey).orElseThrow(() -> UNKNOWN_REGISTRY.create((Object)registryKey.m_135782_()));
        ResourceLocation tagLocation = ResourceLocationArgument.m_107011_(ctx, (String)"tag");
        TagKey tagKey = TagKey.m_203882_((ResourceKey)((ResourceKey)TagsCommand.cast(registryKey)), (ResourceLocation)tagLocation);
        HolderSet.Named tag = (HolderSet.Named)registry.m_203431_((TagKey)TagsCommand.cast(tagKey)).orElseThrow(() -> UNKNOWN_TAG.create((Object)tagKey.f_203868_(), (Object)registryKey.m_135782_()));
        ((CommandSourceStack)ctx.getSource()).m_288197_(() -> TagsCommand.createMessage(Component.m_237110_((String)"commands.forge.tags.tag_key", (Object[])new Object[]{Component.m_237113_((String)tagKey.f_203867_().m_135782_().toString()).m_130940_(ChatFormatting.GOLD), Component.m_237113_((String)tagKey.f_203868_().toString()).m_130940_(ChatFormatting.DARK_GREEN)}), "commands.forge.tags.element_count", "commands.forge.tags.copy_element_names", tag.m_203632_(), page, ChatFormatting.YELLOW, () -> tag.m_203614_().map(s -> (String)s.m_203439_().map(k -> k.m_135782_().toString(), Object::toString))), false);
        return tag.m_203632_();
    }

    private static int queryElementTags(CommandContext<CommandSourceStack> ctx, int page) throws CommandSyntaxException {
        ResourceKey<Registry<?>> registryKey = TagsCommand.getResourceKey(ctx, "registry", ROOT_REGISTRY_KEY).orElseThrow();
        Registry registry = (Registry)((CommandSourceStack)ctx.getSource()).m_81377_().m_206579_().m_6632_(registryKey).orElseThrow(() -> UNKNOWN_REGISTRY.create((Object)registryKey.m_135782_()));
        ResourceLocation elementLocation = ResourceLocationArgument.m_107011_(ctx, (String)"element");
        ResourceKey elementKey = ResourceKey.m_135785_((ResourceKey)((ResourceKey)TagsCommand.cast(registryKey)), (ResourceLocation)elementLocation);
        Holder elementHolder = (Holder)registry.m_203636_((ResourceKey)TagsCommand.cast(elementKey)).orElseThrow(() -> UNKNOWN_ELEMENT.create((Object)elementLocation, (Object)registryKey.m_135782_()));
        long containingTagsCount = elementHolder.m_203616_().count();
        ((CommandSourceStack)ctx.getSource()).m_288197_(() -> TagsCommand.createMessage(Component.m_237110_((String)"commands.forge.tags.element", (Object[])new Object[]{Component.m_237113_((String)registryKey.m_135782_().toString()).m_130940_(ChatFormatting.GOLD), Component.m_237113_((String)elementLocation.toString()).m_130940_(ChatFormatting.YELLOW)}), "commands.forge.tags.containing_tag_count", "commands.forge.tags.copy_tag_names", containingTagsCount, page, ChatFormatting.DARK_GREEN, () -> elementHolder.m_203616_().map(k -> k.f_203868_().toString())), false);
        return (int)containingTagsCount;
    }

    private static MutableComponent createMessage(MutableComponent header, String containsText, String copyHoverText, long count, long currentPage, ChatFormatting elementColor, Supplier<Stream<String>> names) {
        String allElementNames = names.get().sorted().collect(Collectors.joining("\n"));
        long totalPages = (count - 1L) / 8L + 1L;
        long actualPage = (long)Mth.m_14036_((float)currentPage, (float)1.0f, (float)totalPages);
        MutableComponent containsComponent = Component.m_237110_((String)containsText, (Object[])new Object[]{count});
        if (count > 0L) {
            containsComponent = ComponentUtils.m_130748_((Component)containsComponent.m_130938_(s -> s.m_131140_(ChatFormatting.GREEN).m_131142_(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, allElementNames)).m_131144_(new HoverEvent(HoverEvent.Action.f_130831_, (Object)Component.m_237115_((String)copyHoverText)))));
            containsComponent = Component.m_237110_((String)"commands.forge.tags.page_info", (Object[])new Object[]{containsComponent, actualPage, totalPages});
        }
        MutableComponent tagElements = Component.m_237113_((String)"").m_7220_((Component)containsComponent);
        names.get().sorted().skip(8L * (actualPage - 1L)).limit(8L).map(Component::m_237113_).map(t -> t.m_130940_(elementColor)).map(t -> Component.m_237115_((String)"\n - ").m_7220_((Component)t)).forEach(arg_0 -> ((MutableComponent)tagElements).m_7220_(arg_0));
        return header.m_130946_("\n").m_7220_((Component)tagElements);
    }

    private static CompletableFuture<Suggestions> suggestRegistries(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        ((CommandSourceStack)ctx.getSource()).m_5894_().m_206193_().map(RegistryAccess.RegistryEntry::f_206233_).map(ResourceKey::m_135782_).map(ResourceLocation::toString).forEach(arg_0 -> ((SuggestionsBuilder)builder).suggest(arg_0));
        return builder.buildFuture();
    }

    private static SuggestionProvider<CommandSourceStack> suggestFromRegistry(Function<Registry<?>, Iterable<ResourceLocation>> namesFunction) {
        return (ctx, builder) -> TagsCommand.getResourceKey((CommandContext<CommandSourceStack>)ctx, "registry", ROOT_REGISTRY_KEY).flatMap(key -> ((CommandSourceStack)ctx.getSource()).m_5894_().m_6632_(key).map(registry -> {
            SharedSuggestionProvider.m_82926_((Iterable)((Iterable)namesFunction.apply((Registry<?>)registry)), (SuggestionsBuilder)builder);
            return builder.buildFuture();
        })).orElseGet(() -> ((SuggestionsBuilder)builder).buildFuture());
    }

    private static <T> Optional<ResourceKey<T>> getResourceKey(CommandContext<CommandSourceStack> ctx, String name, ResourceKey<Registry<T>> registryKey) {
        ResourceKey key = (ResourceKey)ctx.getArgument(name, ResourceKey.class);
        return key.m_195975_(registryKey);
    }

    private static <O> O cast(Object input) {
        return (O)input;
    }
}

