/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.contents.TranslatableContents
 */
package net.minecraftforge.internal;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.common.ForgeI18n;

public class TextComponentMessageFormatHandler {
    public static int handle(TranslatableContents parent, Consumer<FormattedText> addChild, Object[] formatArgs, String format) {
        try {
            boolean onlyMissingQuotes;
            String formattedString = ForgeI18n.parseFormat(format, formatArgs);
            if (format.indexOf(39) != -1 && (onlyMissingQuotes = format.chars().filter(ch -> formattedString.indexOf((char)ch) == -1).allMatch(ch -> ch == 39))) {
                return 0;
            }
            MutableComponent component = Component.m_237113_((String)formattedString);
            addChild.accept((FormattedText)component);
            return format.length();
        }
        catch (IllegalArgumentException ex) {
            return 0;
        }
    }
}

