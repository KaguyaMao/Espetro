/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.commands.arguments.selector.EntitySelector
 *  net.minecraft.commands.arguments.selector.EntitySelectorParser
 */
package net.minecraftforge.common.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.HashMap;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraftforge.common.command.IEntitySelectorType;

public class EntitySelectorManager {
    private static final HashMap<String, IEntitySelectorType> REGISTRY = new HashMap();

    public static void register(String token, IEntitySelectorType type) {
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Token must not be empty");
        }
        if (Arrays.asList("p", "a", "r", "s", "e").contains(token)) {
            throw new IllegalArgumentException("Token clashes with vanilla @" + token);
        }
        for (char c : token.toCharArray()) {
            if (StringReader.isAllowedInUnquotedString((char)c)) continue;
            throw new IllegalArgumentException("Token must only contain allowed characters");
        }
        REGISTRY.put(token, type);
    }

    public static EntitySelector parseSelector(EntitySelectorParser parser) throws CommandSyntaxException {
        if (parser.m_121346_().canRead()) {
            int i = parser.m_121346_().getCursor();
            String token = parser.m_121346_().readUnquotedString();
            IEntitySelectorType type = REGISTRY.get(token);
            if (type != null) {
                return type.build(parser);
            }
            parser.m_121346_().setCursor(i);
        }
        return null;
    }

    public static void fillSelectorSuggestions(SuggestionsBuilder suggestionBuilder) {
        REGISTRY.forEach((token, type) -> suggestionBuilder.suggest("@" + token, (Message)type.getSuggestionTooltip()));
    }
}

