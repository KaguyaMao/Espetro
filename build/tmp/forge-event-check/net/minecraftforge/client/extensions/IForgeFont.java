/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.network.chat.FormattedText
 */
package net.minecraftforge.client.extensions;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;

public interface IForgeFont {
    public static final FormattedText ELLIPSIS = FormattedText.m_130775_((String)"...");

    public Font self();

    default public FormattedText ellipsize(FormattedText text, int maxWidth) {
        Font self = this.self();
        int strWidth = self.m_92852_(text);
        int ellipsisWidth = self.m_92852_(ELLIPSIS);
        if (strWidth > maxWidth) {
            if (ellipsisWidth >= maxWidth) {
                return self.m_92854_(text, maxWidth);
            }
            return FormattedText.m_130773_((FormattedText[])new FormattedText[]{self.m_92854_(text, maxWidth - ellipsisWidth), ELLIPSIS});
        }
        return text;
    }
}

