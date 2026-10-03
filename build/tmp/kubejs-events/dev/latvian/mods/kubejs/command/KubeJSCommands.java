/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Strings
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.JavaMembers
 *  dev.latvian.mods.rhino.JavaMembers$FieldInfo
 *  dev.latvian.mods.rhino.JavaMembers$MethodInfo
 *  dev.latvian.mods.rhino.Scriptable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.SharedSuggestionProvider
 *  net.minecraft.commands.arguments.CompoundTagArgument
 *  net.minecraft.commands.arguments.DimensionArgument
 *  net.minecraft.commands.arguments.EntityArgument
 *  net.minecraft.commands.arguments.ObjectiveArgument
 *  net.minecraft.commands.arguments.ResourceLocationArgument
 *  net.minecraft.commands.arguments.ScoreHolderArgument
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderSet
 *  net.minecraft.core.HolderSet$Named
 *  net.minecraft.core.Position
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.HoverEvent
 *  net.minecraft.network.chat.HoverEvent$Action
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.ServerScoreboard
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.BucketItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.scores.Objective
 *  org.apache.commons.io.FileUtils
 */
package dev.latvian.mods.kubejs.command;

import com.google.common.base.Strings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.WithPersistentData;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.net.DisplayClientErrorsMessage;
import dev.latvian.mods.kubejs.net.DisplayServerErrorsMessage;
import dev.latvian.mods.kubejs.net.PaintMessage;
import dev.latvian.mods.kubejs.net.ReloadStartupScriptsMessage;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.ExportablePackResources;
import dev.latvian.mods.kubejs.server.CustomCommandEventJS;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.JavaMembers;
import dev.latvian.mods.rhino.Scriptable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.scores.Objective;
import org.apache.commons.io.FileUtils;

public class KubeJSCommands {
    private static final char UNICODE_TICK = '\u2714';
    private static final char UNICODE_CROSS = '\u2718';
    public static final DynamicCommandExceptionType NO_REGISTRY = new DynamicCommandExceptionType(id -> Component.m_237113_((String)("No builtin or static registry found for " + String.valueOf(id))));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        com.google.common.base.Predicate spOrOP = source -> source.m_81377_().m_129792_() || source.m_6761_(2);
        LiteralCommandNode cmd = dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"kubejs").then(Commands.m_82127_((String)"help").executes(context -> KubeJSCommands.help((CommandSourceStack)context.getSource())))).then(Commands.m_82127_((String)"custom_command").then(Commands.m_82129_((String)"id", (ArgumentType)StringArgumentType.word()).suggests((ctx, builder) -> SharedSuggestionProvider.m_82981_(ServerEvents.CUSTOM_COMMAND.findUniqueExtraIds(ScriptType.SERVER).stream().map(String::valueOf), (SuggestionsBuilder)builder)).executes(context -> KubeJSCommands.customCommand((CommandSourceStack)context.getSource(), StringArgumentType.getString((CommandContext)context, (String)"id")))))).then(Commands.m_82127_((String)"hand").executes(context -> KubeJSCommands.hand(((CommandSourceStack)context.getSource()).m_81375_(), InteractionHand.MAIN_HAND)))).then(Commands.m_82127_((String)"offhand").executes(context -> KubeJSCommands.hand(((CommandSourceStack)context.getSource()).m_81375_(), InteractionHand.OFF_HAND)))).then(Commands.m_82127_((String)"inventory").executes(context -> KubeJSCommands.inventory(((CommandSourceStack)context.getSource()).m_81375_())))).then(Commands.m_82127_((String)"hotbar").executes(context -> KubeJSCommands.hotbar(((CommandSourceStack)context.getSource()).m_81375_())))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"errors").then(((LiteralArgumentBuilder)Commands.m_82127_((String)"startup").requires((Predicate)spOrOP)).executes(context -> KubeJSCommands.errors((CommandSourceStack)context.getSource(), ScriptType.STARTUP)))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"server").requires((Predicate)spOrOP)).executes(context -> KubeJSCommands.errors((CommandSourceStack)context.getSource(), ScriptType.SERVER)))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"client").requires(source -> true)).executes(context -> KubeJSCommands.errors((CommandSourceStack)context.getSource(), ScriptType.CLIENT))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"reload").then(((LiteralArgumentBuilder)Commands.m_82127_((String)"config").requires((Predicate)spOrOP)).executes(context -> KubeJSCommands.reloadConfig((CommandSourceStack)context.getSource())))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"startup_scripts").requires((Predicate)spOrOP)).executes(context -> KubeJSCommands.reloadStartup((CommandSourceStack)context.getSource())))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"server_scripts").requires((Predicate)spOrOP)).executes(context -> KubeJSCommands.reloadServer((CommandSourceStack)context.getSource())))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"client_scripts").requires(source -> true)).executes(context -> KubeJSCommands.reloadClient((CommandSourceStack)context.getSource())))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"textures").requires(source -> true)).executes(context -> KubeJSCommands.reloadTextures((CommandSourceStack)context.getSource())))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"lang").requires(source -> true)).executes(context -> KubeJSCommands.reloadLang((CommandSourceStack)context.getSource()))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"export").requires((Predicate)spOrOP)).executes(context -> KubeJSCommands.export((CommandSourceStack)context.getSource()))).then(Commands.m_82127_((String)"pack_zips").executes(context -> KubeJSCommands.exportPacks((CommandSourceStack)context.getSource(), true)))).then(Commands.m_82127_((String)"pack_folders").executes(context -> KubeJSCommands.exportPacks((CommandSourceStack)context.getSource(), false))))).then(Commands.m_82127_((String)"list_tag").then(((RequiredArgumentBuilder)Commands.m_82129_((String)"registry", (ArgumentType)ResourceLocationArgument.m_106984_()).suggests((ctx, builder) -> SharedSuggestionProvider.m_82981_(((CommandSourceStack)ctx.getSource()).m_5894_().m_206193_().map(entry -> entry.f_206233_().m_135782_().toString()), (SuggestionsBuilder)builder)).executes(ctx -> KubeJSCommands.listTagsFor((CommandSourceStack)ctx.getSource(), KubeJSCommands.registry((CommandContext<CommandSourceStack>)ctx, "registry")))).then(Commands.m_82129_((String)"tag", (ArgumentType)ResourceLocationArgument.m_106984_()).suggests((ctx, builder) -> SharedSuggestionProvider.m_82981_(KubeJSCommands.allTags((CommandSourceStack)ctx.getSource(), KubeJSCommands.registry((CommandContext<CommandSourceStack>)ctx, "registry")).map(TagKey::f_203868_).map(ResourceLocation::toString), (SuggestionsBuilder)builder)).executes(ctx -> KubeJSCommands.tagObjects((CommandSourceStack)ctx.getSource(), TagKey.m_203882_(KubeJSCommands.registry((CommandContext<CommandSourceStack>)ctx, "registry"), (ResourceLocation)ResourceLocationArgument.m_107011_((CommandContext)ctx, (String)"tag")))))))).then(Commands.m_82127_((String)"dump_registry").then(Commands.m_82129_((String)"registry", (ArgumentType)ResourceLocationArgument.m_106984_()).suggests((ctx, builder) -> SharedSuggestionProvider.m_82981_(((CommandSourceStack)ctx.getSource()).m_5894_().m_206193_().map(entry -> entry.f_206233_().m_135782_().toString()), (SuggestionsBuilder)builder)).executes(ctx -> KubeJSCommands.dumpRegistry((CommandSourceStack)ctx.getSource(), KubeJSCommands.registry((CommandContext<CommandSourceStack>)ctx, "registry")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"stages").requires((Predicate)spOrOP)).then(Commands.m_82127_((String)"add").then(Commands.m_82129_((String)"player", (ArgumentType)EntityArgument.m_91470_()).then(Commands.m_82129_((String)"stage", (ArgumentType)StringArgumentType.string()).executes(context -> KubeJSCommands.addStage((CommandSourceStack)context.getSource(), EntityArgument.m_91477_((CommandContext)context, (String)"player"), StringArgumentType.getString((CommandContext)context, (String)"stage"))))))).then(Commands.m_82127_((String)"remove").then(Commands.m_82129_((String)"player", (ArgumentType)EntityArgument.m_91470_()).then(Commands.m_82129_((String)"stage", (ArgumentType)StringArgumentType.string()).executes(context -> KubeJSCommands.removeStage((CommandSourceStack)context.getSource(), EntityArgument.m_91477_((CommandContext)context, (String)"player"), StringArgumentType.getString((CommandContext)context, (String)"stage"))))))).then(Commands.m_82127_((String)"clear").then(Commands.m_82129_((String)"player", (ArgumentType)EntityArgument.m_91470_()).executes(context -> KubeJSCommands.clearStages((CommandSourceStack)context.getSource(), EntityArgument.m_91477_((CommandContext)context, (String)"player")))))).then(Commands.m_82127_((String)"list").then(Commands.m_82129_((String)"player", (ArgumentType)EntityArgument.m_91470_()).executes(context -> KubeJSCommands.listStages((CommandSourceStack)context.getSource(), EntityArgument.m_91477_((CommandContext)context, (String)"player"))))))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"painter").requires((Predicate)spOrOP)).then(Commands.m_82129_((String)"player", (ArgumentType)EntityArgument.m_91470_()).then(Commands.m_82129_((String)"object", (ArgumentType)CompoundTagArgument.m_87657_()).executes(context -> KubeJSCommands.painter((CommandSourceStack)context.getSource(), EntityArgument.m_91477_((CommandContext)context, (String)"player"), CompoundTagArgument.m_87660_((CommandContext)context, (String)"object"))))))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"generate_typings").requires((Predicate)spOrOP)).executes(context -> KubeJSCommands.generateTypings((CommandSourceStack)context.getSource())))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"packmode").requires((Predicate)spOrOP)).executes(context -> KubeJSCommands.packmode((CommandSourceStack)context.getSource(), ""))).then(Commands.m_82129_((String)"name", (ArgumentType)StringArgumentType.word()).executes(context -> KubeJSCommands.packmode((CommandSourceStack)context.getSource(), StringArgumentType.getString((CommandContext)context, (String)"name")))))).then(Commands.m_82127_((String)"dump_internals").then(((LiteralArgumentBuilder)Commands.m_82127_((String)"events").requires((Predicate)spOrOP)).executes(context -> KubeJSCommands.dumpEvents((CommandSourceStack)context.getSource()))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"persistent_data").requires((Predicate)spOrOP)).then(KubeJSCommands.addPersistentDataCommands(Commands.m_82127_((String)"server"), ctx -> Set.of(((CommandSourceStack)ctx.getSource()).m_81377_())))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"dimension").then(KubeJSCommands.addPersistentDataCommands(Commands.m_82127_((String)"*"), ctx -> (Collection)((CommandSourceStack)ctx.getSource()).m_81377_().m_129785_()))).then(KubeJSCommands.addPersistentDataCommands(Commands.m_82129_((String)"dimension", (ArgumentType)DimensionArgument.m_88805_()), ctx -> Set.of(DimensionArgument.m_88808_((CommandContext)ctx, (String)"dimension")))))).then(Commands.m_82127_((String)"entity").then(KubeJSCommands.addPersistentDataCommands(Commands.m_82129_((String)"entity", (ArgumentType)EntityArgument.m_91460_()), ctx -> EntityArgument.m_91461_((CommandContext)ctx, (String)"entity"))))));
        dispatcher.register((LiteralArgumentBuilder)Commands.m_82127_((String)"kjs").redirect((CommandNode)cmd));
    }

    private static int dumpEvents(CommandSourceStack source) {
        Map<String, EventGroup> groups = EventGroup.getGroups();
        Path output = KubeJSPaths.LOCAL.resolve("event_groups");
        for (Map.Entry<String, EventGroup> entry : groups.entrySet()) {
            String groupName = entry.getKey();
            EventGroup group = entry.getValue();
            Path groupFolder = output.resolve(groupName);
            try {
                Files.createDirectories(groupFolder, new FileAttribute[0]);
                FileUtils.cleanDirectory((File)groupFolder.toFile());
            }
            catch (IOException e) {
                ConsoleJS.SERVER.error("Failed to create folder for event group " + groupName, e);
                source.m_81352_((Component)Component.m_237113_((String)("Failed to create folder for event group " + groupName)));
                return 0;
            }
            for (Map.Entry<String, EventHandler> handlerEntry : group.getHandlers().entrySet()) {
                String handlerName = handlerEntry.getKey();
                EventHandler handler = handlerEntry.getValue();
                Path handlerFile = groupFolder.resolve(handlerName + ".md");
                String fullName = "%s.%s".formatted(groupName, handlerName);
                Class<? extends EventJS> eventType = handler.eventType.get();
                StringBuilder builder = new StringBuilder();
                builder.append("# ").append(fullName).append("\n\n");
                builder.append("## Basic info\n\n");
                builder.append("- Valid script types: ").append(handler.scriptTypePredicate.getValidTypes()).append("\n\n");
                builder.append("- Has result? ").append(handler.getHasResult() ? (char)'\u2714' : '\u2718').append("\n\n");
                builder.append("- Event class: ");
                if (eventType.getPackageName().startsWith("dev.latvian.mods.kubejs")) {
                    builder.append('[').append(UtilsJS.toMappedTypeString(eventType)).append(']').append('(').append("https://github.com/KubeJS-Mods/KubeJS/tree/").append(2001).append("/common/src/main/java/").append(eventType.getPackageName().replace('.', '/')).append('/').append(eventType.getSimpleName()).append(".java").append(')');
                } else {
                    builder.append(UtilsJS.toMappedTypeString(eventType)).append(" (third-party)");
                }
                builder.append("\n\n");
                Info classInfo = eventType.getAnnotation(Info.class);
                if (classInfo != null) {
                    builder.append("```\n").append(classInfo.value()).append("```");
                    builder.append("\n\n");
                }
                ScriptManager scriptManager = ScriptType.SERVER.manager.get();
                Context cx = scriptManager.context;
                JavaMembers members = JavaMembers.lookupClass((Context)cx, (Scriptable)scriptManager.topLevelScope, eventType, null, (boolean)false);
                boolean hasDocumentedMembers = false;
                StringBuilder documentedMembers = new StringBuilder("### Documented members:\n\n");
                builder.append("### Available fields:\n\n");
                builder.append("| Name | Type | Static? |\n");
                builder.append("| ---- | ---- | ------- |\n");
                for (JavaMembers.FieldInfo field : members.getAccessibleFields(cx, false)) {
                    if (field.field.getDeclaringClass() == Object.class) continue;
                    String typeName = UtilsJS.toMappedTypeString(field.field.getGenericType());
                    builder.append("| ").append(field.name).append(" | ").append(typeName).append(" | ");
                    builder.append(Modifier.isStatic(field.field.getModifiers()) ? (char)'\u2714' : '\u2718').append(" |\n");
                    Info info = field.field.getAnnotation(Info.class);
                    if (info == null) continue;
                    hasDocumentedMembers = true;
                    documentedMembers.append("- `").append(typeName).append(' ').append(field.name).append("`\n");
                    documentedMembers.append("```\n");
                    String desc = info.value();
                    documentedMembers.append(desc);
                    if (!desc.endsWith("\n")) {
                        documentedMembers.append("\n");
                    }
                    documentedMembers.append("```\n\n");
                }
                builder.append("\n").append("Note: Even if no fields are listed above, some methods are still available as fields through *beans*.\n\n");
                builder.append("### Available methods:\n\n");
                builder.append("| Name | Parameters | Return type | Static? |\n");
                builder.append("| ---- | ---------- | ----------- | ------- |\n");
                for (JavaMembers.MethodInfo method : members.getAccessibleMethods(cx, false)) {
                    int i;
                    if (method.hidden || method.method.getDeclaringClass() == Object.class) continue;
                    builder.append("| ").append(method.name).append(" | ");
                    Type[] params = method.method.getGenericParameterTypes();
                    CharSequence[] paramTypes = new String[params.length];
                    for (int i2 = 0; i2 < params.length; ++i2) {
                        paramTypes[i2] = UtilsJS.toMappedTypeString(params[i2]);
                    }
                    builder.append(String.join((CharSequence)", ", paramTypes)).append(" | ");
                    String returnType = UtilsJS.toMappedTypeString(method.method.getGenericReturnType());
                    builder.append(" | ").append(returnType).append(" | ");
                    builder.append(Modifier.isStatic(method.method.getModifiers()) ? (char)'\u2714' : '\u2718').append(" |\n");
                    Info info = method.method.getAnnotation(Info.class);
                    if (info == null) continue;
                    hasDocumentedMembers = true;
                    documentedMembers.append("- ").append('`');
                    if (Modifier.isStatic(method.method.getModifiers())) {
                        documentedMembers.append("static ");
                    }
                    documentedMembers.append(returnType).append(' ').append(method.name).append('(');
                    Param[] namedParams = info.params();
                    String[] paramNames = new String[params.length];
                    CharSequence[] signature = new String[params.length];
                    for (i = 0; i < params.length; ++i) {
                        String name1;
                        Object name = "var" + i;
                        if (namedParams.length > i && !Strings.isNullOrEmpty((String)(name1 = namedParams[i].name()))) {
                            name = name1;
                        }
                        paramNames[i] = name;
                        signature[i] = (String)paramTypes[i] + " " + (String)name;
                    }
                    documentedMembers.append(String.join((CharSequence)", ", signature)).append(')').append('`').append("\n");
                    if (params.length > 0) {
                        documentedMembers.append("\n  Parameters:\n");
                        for (i = 0; i < params.length; ++i) {
                            documentedMembers.append("  - ").append(paramNames[i]).append(": ").append((String)paramTypes[i]).append((String)(namedParams.length > i ? "- " + namedParams[i].value() : "")).append("\n");
                        }
                        documentedMembers.append("\n");
                    }
                    documentedMembers.append("```\n");
                    String desc = info.value();
                    documentedMembers.append(desc);
                    if (!desc.endsWith("\n")) {
                        documentedMembers.append("\n");
                    }
                    documentedMembers.append("```\n\n");
                }
                builder.append("\n\n");
                if (hasDocumentedMembers) {
                    builder.append((CharSequence)documentedMembers).append("\n\n");
                }
                builder.append("### Example script:\n\n");
                builder.append("```js\n");
                builder.append(fullName).append('(');
                if (handler.extra != null) {
                    builder.append(handler.extra.required ? "extra_id, " : "/* extra_id (optional), */ ");
                }
                builder.append("(event) => {\n");
                builder.append("\t// This space (un)intentionally left blank\n");
                builder.append("});\n");
                builder.append("```\n\n");
                try {
                    Files.writeString(handlerFile, (CharSequence)builder.toString(), new OpenOption[0]);
                }
                catch (IOException e) {
                    ConsoleJS.SERVER.error("Failed to write file for event handler " + fullName, e);
                    source.m_81352_((Component)Component.m_237113_((String)("Failed to write file for event handler " + fullName)));
                    return 0;
                }
            }
        }
        source.m_243053_((Component)Component.m_237113_((String)("Successfully dumped event groups to " + String.valueOf(output))));
        return 1;
    }

    private static <T> ResourceKey<Registry<T>> registry(CommandContext<CommandSourceStack> ctx, String arg) {
        return ResourceKey.m_135788_((ResourceLocation)ResourceLocationArgument.m_107011_(ctx, (String)arg));
    }

    private static <T> Stream<TagKey<T>> allTags(CommandSourceStack source, ResourceKey<Registry<T>> registry) throws CommandSyntaxException {
        return ((Registry)source.m_5894_().m_6632_(registry).orElseThrow(() -> NO_REGISTRY.create((Object)registry.m_135782_()))).m_203613_();
    }

    private static Component copy(String s, ChatFormatting col, String info) {
        return KubeJSCommands.copy((Component)Component.m_237113_((String)s).m_130940_(col), info);
    }

    private static Component copy(Component c, String info) {
        return Component.m_237113_((String)"- ").m_130940_(ChatFormatting.GRAY).m_130948_(Style.f_131099_.m_131142_(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, c.getString()))).m_130948_(Style.f_131099_.m_131144_(new HoverEvent(HoverEvent.Action.f_130831_, (Object)Component.m_237113_((String)(info + " (Click to copy)"))))).m_7220_(c);
    }

    private static void link(CommandSourceStack source, ChatFormatting color, String name, String url) {
        source.m_243053_((Component)Component.m_237113_((String)"\u2022 ").m_7220_((Component)Component.m_237113_((String)name).m_130940_(color).m_130938_(style -> style.m_131142_(new ClickEvent(ClickEvent.Action.OPEN_URL, url)))));
    }

    private static int help(CommandSourceStack source) {
        KubeJSCommands.link(source, ChatFormatting.GOLD, "Wiki", "https://kubejs.com/?" + KubeJS.QUERY);
        KubeJSCommands.link(source, ChatFormatting.GREEN, "Support", "https://kubejs.com/support?" + KubeJS.QUERY);
        KubeJSCommands.link(source, ChatFormatting.BLUE, "Changelog", "https://kubejs.com/changelog?" + KubeJS.QUERY);
        return 1;
    }

    private static int customCommand(CommandSourceStack source, String id) {
        if (ServerEvents.CUSTOM_COMMAND.hasListeners()) {
            EventResult result = ServerEvents.CUSTOM_COMMAND.post(new CustomCommandEventJS((Level)source.m_81372_(), source.m_81373_(), BlockPos.m_274446_((Position)source.m_81371_()), id), id);
            if (result.type() == EventResult.Type.ERROR) {
                source.m_81352_((Component)Component.m_237113_((String)result.value().toString()));
                return 0;
            }
            return 1;
        }
        return 0;
    }

    private static int hand(ServerPlayer player, InteractionHand hand) {
        Integer size;
        String id;
        Item block;
        TagKey tag2;
        player.m_213846_((Component)Component.m_237113_((String)"Item in hand:"));
        ItemStack stack = player.m_21120_(hand);
        Holder holder = stack.m_220173_();
        player.m_213846_(KubeJSCommands.copy(ItemStackJS.toItemString(stack), ChatFormatting.GREEN, "Item ID"));
        List itemTags = holder.m_203616_().toList();
        for (TagKey tag2 : itemTags) {
            String id2 = "'#%s'".formatted(tag2.f_203868_());
            Integer size2 = BuiltInRegistries.f_257033_.m_203431_(tag2).map(HolderSet::m_203632_).orElse(0);
            player.m_213846_(KubeJSCommands.copy(id2, ChatFormatting.YELLOW, "Item Tag [" + size2 + " items]"));
        }
        player.m_213846_(KubeJSCommands.copy("'@" + stack.kjs$getMod() + "'", ChatFormatting.AQUA, "Mod [" + IngredientPlatformHelper.get().mod(stack.kjs$getMod()).kjs$getStacks().size() + " items]"));
        tag2 = stack.m_41720_();
        if (tag2 instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)tag2;
            player.m_213846_((Component)Component.m_237113_((String)"Held block:"));
            block = blockItem.m_40614_();
            Holder.Reference blockHolder = block.m_204297_();
            player.m_213846_(KubeJSCommands.copy(block.kjs$getId(), ChatFormatting.GREEN, "Block ID"));
            List blockTags = blockHolder.m_203616_().toList();
            for (TagKey tag3 : blockTags) {
                id = "'#%s'".formatted(tag3.f_203868_());
                size = BuiltInRegistries.f_256975_.m_203431_(tag3).map(HolderSet::m_203632_).orElse(0);
                player.m_213846_(KubeJSCommands.copy(id, ChatFormatting.YELLOW, "Block Tag [" + size + " items]"));
            }
        }
        if ((block = stack.m_41720_()) instanceof BucketItem) {
            BucketItem bucket = (BucketItem)block;
            player.m_213846_((Component)Component.m_237113_((String)"Held fluid:"));
            Fluid fluid = bucket.arch$getFluid();
            Holder.Reference fluidHolder = fluid.m_205069_();
            player.m_213846_(KubeJSCommands.copy(fluidHolder.m_205785_().m_135782_().toString(), ChatFormatting.GREEN, "Fluid ID"));
            List fluidTags = fluidHolder.m_203616_().toList();
            for (TagKey tag3 : fluidTags) {
                id = "'#%s'".formatted(tag3.f_203868_());
                size = BuiltInRegistries.f_257020_.m_203431_(tag3).map(HolderSet::m_203632_).orElse(0);
                player.m_213846_(KubeJSCommands.copy(id, ChatFormatting.YELLOW, "Fluid Tag [" + size + " items]"));
            }
        }
        return 1;
    }

    private static int inventory(ServerPlayer player) {
        return KubeJSCommands.dump((List<ItemStack>)player.m_150109_().f_35974_, player, "Inventory");
    }

    private static int hotbar(ServerPlayer player) {
        return KubeJSCommands.dump(player.m_150109_().f_35974_.subList(0, 9), player, "Hotbar");
    }

    private static int dump(List<ItemStack> stacks, ServerPlayer player, String name) {
        List<String> dump = stacks.stream().filter(is -> !is.m_41619_()).map(ItemStackJS::toItemString).toList();
        player.m_213846_(KubeJSCommands.copy(dump.toString(), ChatFormatting.WHITE, name + " Item List"));
        return 1;
    }

    private static int errors(CommandSourceStack source, ScriptType type) throws CommandSyntaxException {
        if (type == ScriptType.CLIENT) {
            ServerPlayer player = source.m_81375_();
            new DisplayClientErrorsMessage().sendTo(player);
            return 1;
        }
        if (type.console.errors.isEmpty() && type.console.warnings.isEmpty()) {
            source.m_243053_((Component)Component.m_237113_((String)"No errors or warnings found!").m_130940_(ChatFormatting.GREEN));
            return 0;
        }
        if (source.m_81377_().m_129792_()) {
            KubeJS.PROXY.openErrors(type);
            return 1;
        }
        ServerPlayer player = source.m_81375_();
        ArrayList<ConsoleLine> errors = new ArrayList<ConsoleLine>(type.console.errors);
        ArrayList<ConsoleLine> warnings = new ArrayList<ConsoleLine>(type.console.warnings);
        player.m_240418_((Component)Component.m_237113_((String)"You need KubeJS on client side!").m_130940_(ChatFormatting.RED), true);
        new DisplayServerErrorsMessage(type, errors, warnings).sendTo(player);
        return 1;
    }

    private static int reloadConfig(CommandSourceStack source) {
        KubeJS.PROXY.reloadConfig();
        source.m_243053_((Component)Component.m_237113_((String)"Done!"));
        return 1;
    }

    private static int reloadStartup(CommandSourceStack source) {
        KubeJS.getStartupScriptManager().reload(null);
        source.m_243053_((Component)Component.m_237113_((String)"Done!"));
        new ReloadStartupScriptsMessage(source.m_81377_().m_6982_()).sendToAll(source.m_81377_());
        return 1;
    }

    private static int reloadServer(CommandSourceStack source) {
        ServerScriptManager.instance.reload((ResourceManager)source.m_81377_().kjs$getReloadableResources().f_206584_());
        source.m_288197_(() -> Component.m_237113_((String)"Done! To reload recipes, tags, loot tables and other datapack things, run ").m_7220_((Component)Component.m_237113_((String)"'/reload'").kjs$clickRunCommand("/reload").kjs$hover((Component)Component.m_237113_((String)"Click to run"))), false);
        return 1;
    }

    private static int reloadClient(CommandSourceStack source) {
        KubeJS.PROXY.reloadClientInternal();
        source.m_243053_((Component)Component.m_237113_((String)"Done! To reload textures, models and other assets, press F3 + T"));
        return 1;
    }

    private static int reloadTextures(CommandSourceStack source) {
        KubeJS.PROXY.reloadTextures();
        return 1;
    }

    private static int reloadLang(CommandSourceStack source) {
        KubeJS.PROXY.reloadLang();
        return 1;
    }

    private static int export(CommandSourceStack source) {
        if (DataExport.export != null) {
            return 0;
        }
        DataExport.export = new DataExport();
        DataExport.export.source = source;
        source.m_288197_(() -> Component.m_237113_((String)"Reloading server and exporting data..."), true);
        source.m_81377_().kjs$runCommand("reload");
        return 1;
    }

    private static void afterReload(CommandSourceStack source) {
        source.m_288197_(() -> Component.m_237113_((String)"Reloaded!"), true);
    }

    private static int exportPacks(CommandSourceStack source, boolean exportZip) {
        ArrayList<ExportablePackResources> packs = new ArrayList<ExportablePackResources>();
        for (Object pack : source.m_81377_().m_177941_().m_7536_().toList()) {
            if (!(pack instanceof ExportablePackResources)) continue;
            ExportablePackResources e = (ExportablePackResources)pack;
            packs.add(e);
        }
        KubeJS.PROXY.export(packs);
        int success = 0;
        for (ExportablePackResources pack : packs) {
            try {
                if (exportZip) {
                    path = KubeJSPaths.EXPORTED_PACKS.resolve(pack.m_5542_() + ".zip");
                    Files.deleteIfExists(path);
                    try (FileSystem fs = FileSystems.newFileSystem(path, Map.of("create", true));){
                        pack.export(fs.getPath(".", new String[0]));
                    }
                } else {
                    path = KubeJSPaths.EXPORTED_PACKS.resolve(pack.m_5542_());
                    if (Files.exists(path, new LinkOption[0])) {
                        Files.walk(path, new FileVisitOption[0]).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                    }
                    Files.createDirectories(path, new FileAttribute[0]);
                    pack.export(path);
                }
                source.m_288197_(() -> Component.m_237113_((String)"Successfully exported ").m_130940_(ChatFormatting.GREEN).m_7220_((Component)Component.m_237113_((String)pack.m_5542_()).m_130940_(ChatFormatting.BLUE)), false);
                ++success;
            }
            catch (IOException e) {
                e.printStackTrace();
                source.m_81352_((Component)Component.m_237113_((String)"Failed to export %s!".formatted(pack)).m_130938_(style -> style.m_131140_(ChatFormatting.RED).m_131144_(new HoverEvent(HoverEvent.Action.f_130831_, (Object)Component.m_237113_((String)e.getMessage())))));
            }
        }
        int success1 = success;
        if (source.m_81377_().m_129792_() && !source.m_81377_().m_6992_()) {
            source.m_288197_(() -> Component.m_237113_((String)("Exported " + success1 + " packs")).kjs$clickOpenFile(KubeJSPaths.EXPORTED_PACKS.toAbsolutePath().toString()), false);
        } else {
            source.m_288197_(() -> Component.m_237113_((String)("Exported " + success1 + " packs")), false);
        }
        return success;
    }

    private static int outputRecipes(ServerPlayer player) {
        player.m_213846_((Component)Component.m_237113_((String)"WIP!"));
        return 1;
    }

    private static int inputRecipes(ServerPlayer player) {
        player.m_213846_((Component)Component.m_237113_((String)"WIP!"));
        return 1;
    }

    private static int checkRecipeConflicts(ServerPlayer player) {
        player.m_213846_((Component)Component.m_237113_((String)"WIP!"));
        return 1;
    }

    private static <T> int listTagsFor(CommandSourceStack source, ResourceKey<Registry<T>> registry) throws CommandSyntaxException {
        Stream<TagKey<TagKey>> tags = KubeJSCommands.allTags(source, registry);
        source.m_243053_((Component)Component.m_237119_());
        source.m_243053_((Component)Component.m_237113_((String)("List of all Tags for " + String.valueOf(registry.m_135782_()) + ":")));
        source.m_243053_((Component)Component.m_237119_());
        long size = tags.map(TagKey::f_203868_).map(tag -> Component.m_237113_((String)"- %s".formatted(tag)).m_130948_(Style.f_131099_.m_131142_(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kubejs list_tag %s %s".formatted(registry.m_135782_(), tag))).m_131144_(new HoverEvent(HoverEvent.Action.f_130831_, (Object)Component.m_237113_((String)"[Show all entries for %s]".formatted(tag)))))).mapToLong(msg -> {
            source.m_243053_((Component)msg);
            return 1L;
        }).sum();
        source.m_243053_((Component)Component.m_237119_());
        source.m_243053_((Component)Component.m_237113_((String)"Total: %d tags".formatted(size)));
        source.m_243053_((Component)Component.m_237113_((String)"(Click on any of the above tags to list their contents!)"));
        source.m_243053_((Component)Component.m_237119_());
        return 1;
    }

    private static <T> int tagObjects(CommandSourceStack source, TagKey<T> key) throws CommandSyntaxException {
        Registry registry = (Registry)source.m_5894_().m_6632_(key.f_203867_()).orElseThrow(() -> NO_REGISTRY.create((Object)key.f_203867_().m_135782_()));
        Optional tag = registry.m_203431_(key);
        if (tag.isEmpty()) {
            source.m_81352_((Component)Component.m_237113_((String)"Tag not found or empty!"));
            return 0;
        }
        source.m_243053_((Component)Component.m_237119_());
        source.m_243053_((Component)Component.m_237113_((String)("Contents of #" + String.valueOf(key.f_203868_()) + " [" + String.valueOf(key.f_203867_().m_135782_()) + "]:")));
        source.m_243053_((Component)Component.m_237119_());
        HolderSet.Named items = (HolderSet.Named)tag.get();
        for (Holder holder : items) {
            String id = (String)holder.m_203439_().map(o -> o.m_135782_().toString(), o -> String.valueOf(o) + " (unknown ID)");
            source.m_243053_((Component)Component.m_237113_((String)("- " + id)));
        }
        source.m_243053_((Component)Component.m_237119_());
        source.m_243053_((Component)Component.m_237113_((String)("Total: " + items.m_203632_() + " elements")));
        source.m_243053_((Component)Component.m_237119_());
        return 1;
    }

    private static <T> int dumpRegistry(CommandSourceStack source, ResourceKey<Registry<T>> registry) throws CommandSyntaxException {
        Stream ids = ((Registry)source.m_5894_().m_6632_(registry).orElseThrow(() -> NO_REGISTRY.create((Object)registry.m_135782_()))).m_203611_();
        source.m_243053_((Component)Component.m_237119_());
        source.m_243053_((Component)Component.m_237113_((String)("List of all entries for registry " + String.valueOf(registry.m_135782_()) + ":")));
        source.m_243053_((Component)Component.m_237119_());
        long size = ids.map(holder -> {
            ResourceLocation id = holder.m_205785_().m_135782_();
            return Component.m_237113_((String)"- %s".formatted(id)).m_130948_(Style.f_131099_.m_131144_(new HoverEvent(HoverEvent.Action.f_130831_, (Object)Component.m_237113_((String)"%s [%s]".formatted(holder.m_203334_(), holder.m_203334_().getClass().getName())))));
        }).mapToLong(msg -> {
            source.m_243053_((Component)msg);
            return 1L;
        }).sum();
        source.m_243053_((Component)Component.m_237119_());
        source.m_243053_((Component)Component.m_237113_((String)"Total: %d entries".formatted(size)));
        source.m_243053_((Component)Component.m_237119_());
        return 1;
    }

    private static int addStage(CommandSourceStack source, Collection<ServerPlayer> players, String stage) {
        for (ServerPlayer p : players) {
            if (!p.kjs$getStages().add(stage)) continue;
            source.m_288197_(() -> Component.m_237113_((String)("Added '" + stage + "' stage for " + p.m_6302_())), true);
        }
        return 1;
    }

    private static int removeStage(CommandSourceStack source, Collection<ServerPlayer> players, String stage) {
        for (ServerPlayer p : players) {
            if (!p.kjs$getStages().remove(stage)) continue;
            source.m_288197_(() -> Component.m_237113_((String)("Removed '" + stage + "' stage for " + p.m_6302_())), true);
        }
        return 1;
    }

    private static int clearStages(CommandSourceStack source, Collection<ServerPlayer> players) {
        for (ServerPlayer p : players) {
            if (!p.kjs$getStages().clear()) continue;
            source.m_288197_(() -> Component.m_237113_((String)("Cleared stages for " + p.m_6302_())), true);
        }
        return 1;
    }

    private static int listStages(CommandSourceStack source, Collection<ServerPlayer> players) {
        for (ServerPlayer p : players) {
            source.m_243053_((Component)Component.m_237113_((String)(p.m_6302_() + " stages:")));
            p.kjs$getStages().getAll().stream().sorted().forEach(s -> source.m_243053_((Component)Component.m_237113_((String)("- " + s))));
        }
        return 1;
    }

    private static int painter(CommandSourceStack source, Collection<ServerPlayer> players, CompoundTag object) {
        new PaintMessage(object).sendTo(players);
        return 1;
    }

    private static int generateTypings(CommandSourceStack source) {
        if (!source.m_81377_().m_129792_()) {
            source.m_81352_((Component)Component.m_237113_((String)"You can only run this command in singleplayer!"));
            return 0;
        }
        KubeJS.PROXY.generateTypings(source);
        return 1;
    }

    private static int packmode(CommandSourceStack source, String packmode) {
        if (packmode.isEmpty()) {
            source.m_288197_(() -> Component.m_237113_((String)("Current packmode: " + CommonProperties.get().packMode)), false);
        } else {
            CommonProperties.get().setPackMode(packmode);
            source.m_288197_(() -> Component.m_237113_((String)("Set packmode to: " + packmode)), true);
        }
        return 1;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> addPersistentDataCommands(ArgumentBuilder<CommandSourceStack, ?> cmd, PersistentDataFactory factory) {
        cmd.then(((LiteralArgumentBuilder)Commands.m_82127_((String)"get").then(Commands.m_82127_((String)"*").executes(ctx -> {
            Collection<? extends WithPersistentData> objects = factory.getAll((CommandContext<CommandSourceStack>)ctx);
            for (WithPersistentData withPersistentData : objects) {
                Component dataStr = NbtUtils.m_178061_((Tag)withPersistentData.kjs$getPersistentData());
                ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)"").m_7220_((Component)Component.m_237113_((String)"").m_130940_(ChatFormatting.YELLOW).m_7220_(o.kjs$getDisplayName())).m_130946_(": ").m_7220_(dataStr), false);
            }
            return objects.size();
        }))).then(Commands.m_82129_((String)"key", (ArgumentType)StringArgumentType.string()).executes(ctx -> {
            Collection<? extends WithPersistentData> objects = factory.getAll((CommandContext<CommandSourceStack>)ctx);
            String key = StringArgumentType.getString((CommandContext)ctx, (String)"key");
            for (WithPersistentData withPersistentData : objects) {
                CompoundTag data = key.equals("*") ? withPersistentData.kjs$getPersistentData() : withPersistentData.kjs$getPersistentData().m_128423_(key);
                Component dataStr = data == null ? Component.m_237113_((String)"null").m_130940_(ChatFormatting.RED) : NbtUtils.m_178061_((Tag)data);
                ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)"").m_7220_((Component)Component.m_237113_((String)"").m_130940_(ChatFormatting.YELLOW).m_7220_(o.kjs$getDisplayName())).m_130946_(": ").m_7220_(dataStr), false);
            }
            return objects.size();
        })));
        cmd.then(Commands.m_82127_((String)"merge").then(Commands.m_82129_((String)"nbt", (ArgumentType)CompoundTagArgument.m_87657_()).executes(ctx -> {
            Collection<? extends WithPersistentData> objects = factory.getAll((CommandContext<CommandSourceStack>)ctx);
            CompoundTag tag = CompoundTagArgument.m_87660_((CommandContext)ctx, (String)"nbt");
            for (WithPersistentData withPersistentData : objects) {
                withPersistentData.kjs$getPersistentData().m_128391_(tag);
                ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)"").m_7220_((Component)Component.m_237113_((String)"").m_130940_(ChatFormatting.YELLOW).m_7220_(o.kjs$getDisplayName())).m_130946_(" updated"), false);
            }
            return objects.size();
        })));
        cmd.then(((LiteralArgumentBuilder)Commands.m_82127_((String)"remove").then(Commands.m_82127_((String)"*").executes(ctx -> {
            Collection<? extends WithPersistentData> objects = factory.getAll((CommandContext<CommandSourceStack>)ctx);
            for (WithPersistentData withPersistentData : objects) {
                withPersistentData.kjs$getPersistentData().m_128431_().removeIf(UtilsJS.ALWAYS_TRUE);
            }
            return objects.size();
        }))).then(Commands.m_82129_((String)"key", (ArgumentType)StringArgumentType.string()).executes(ctx -> {
            Collection<? extends WithPersistentData> objects = factory.getAll((CommandContext<CommandSourceStack>)ctx);
            String key = StringArgumentType.getString((CommandContext)ctx, (String)"key");
            for (WithPersistentData withPersistentData : objects) {
                withPersistentData.kjs$getPersistentData().m_128473_(key);
            }
            return objects.size();
        })));
        cmd.then(((LiteralArgumentBuilder)Commands.m_82127_((String)"scoreboard").then(Commands.m_82127_((String)"import").then(Commands.m_82129_((String)"key", (ArgumentType)StringArgumentType.string()).then(Commands.m_82129_((String)"target", (ArgumentType)ScoreHolderArgument.m_108217_()).suggests(ScoreHolderArgument.f_108210_).then(Commands.m_82129_((String)"objective", (ArgumentType)ObjectiveArgument.m_101957_()).executes(ctx -> {
            Objective objective;
            ServerScoreboard scoreboard = ((CommandSourceStack)ctx.getSource()).m_81377_().m_129896_();
            Collection<? extends WithPersistentData> objects = factory.getAll((CommandContext<CommandSourceStack>)ctx);
            String key = StringArgumentType.getString((CommandContext)ctx, (String)"key");
            String target = ScoreHolderArgument.m_108223_((CommandContext)ctx, (String)"target");
            int score = scoreboard.m_83461_(target, objective = ObjectiveArgument.m_101960_((CommandContext)ctx, (String)"objective")) ? scoreboard.m_83471_(target, objective).m_83400_() : 0;
            for (WithPersistentData withPersistentData : objects) {
                withPersistentData.kjs$getPersistentData().m_128405_(key, score);
            }
            return objects.size();
        })))))).then(Commands.m_82127_((String)"export").then(Commands.m_82129_((String)"key", (ArgumentType)StringArgumentType.string()).then(Commands.m_82129_((String)"targets", (ArgumentType)ScoreHolderArgument.m_108239_()).suggests(ScoreHolderArgument.f_108210_).then(Commands.m_82129_((String)"objective", (ArgumentType)ObjectiveArgument.m_101957_()).executes(ctx -> {
            ServerScoreboard scoreboard = ((CommandSourceStack)ctx.getSource()).m_81377_().m_129896_();
            WithPersistentData object = factory.getOne((CommandContext<CommandSourceStack>)ctx);
            String key = StringArgumentType.getString((CommandContext)ctx, (String)"key");
            Collection targets = ScoreHolderArgument.m_108243_((CommandContext)ctx, (String)"targets");
            Objective objective = ObjectiveArgument.m_101960_((CommandContext)ctx, (String)"objective");
            int score = object.kjs$getPersistentData().m_128451_(key);
            for (String target : targets) {
                scoreboard.m_83471_(target, objective).m_83402_(score);
            }
            return 1;
        }))))));
        return cmd;
    }

    @FunctionalInterface
    private static interface PersistentDataFactory {
        public static final SimpleCommandExceptionType EMPTY_LIST = new SimpleCommandExceptionType((Message)Component.m_237113_((String)"Expected at least one target"));

        public Collection<? extends WithPersistentData> apply(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;

        default public Collection<? extends WithPersistentData> getAll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
            Collection<? extends WithPersistentData> list = this.apply(ctx);
            if (list.isEmpty()) {
                throw EMPTY_LIST.create();
            }
            return list;
        }

        default public WithPersistentData getOne(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
            Collection<? extends WithPersistentData> list = this.apply(ctx);
            if (list.isEmpty()) {
                throw EMPTY_LIST.create();
            }
            return list.iterator().next();
        }
    }
}

