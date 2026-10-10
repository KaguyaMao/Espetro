/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.commands.CommandSource
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.network.ServerGamePacketListenerImpl
 */
package net.minecraftforge.server.command;

import java.util.Locale;
import net.minecraft.commands.CommandSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.network.ConnectionType;
import net.minecraftforge.network.NetworkHooks;

public class TextComponentHelper {
    private TextComponentHelper() {
    }

    public static MutableComponent createComponentTranslation(CommandSource source, String translation, Object ... args) {
        if (TextComponentHelper.isVanillaClient(source)) {
            return Component.m_237113_((String)String.format(Locale.ENGLISH, Language.m_128107_().m_6834_(translation), args));
        }
        return Component.m_237110_((String)translation, (Object[])args);
    }

    private static boolean isVanillaClient(CommandSource sender) {
        if (sender instanceof ServerPlayer) {
            ServerPlayer playerMP = (ServerPlayer)sender;
            ServerGamePacketListenerImpl channel = playerMP.f_8906_;
            return NetworkHooks.getConnectionType(() -> channel.f_9742_) == ConnectionType.VANILLA;
        }
        return false;
    }
}

