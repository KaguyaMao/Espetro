/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.forgespi.language.IModInfo
 */
package net.minecraftforge.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

class ModListCommand {
    ModListCommand() {
    }

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.m_82127_((String)"mods").requires(cs -> cs.m_6761_(0))).executes(ctx -> {
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237110_((String)"commands.forge.mods.list", (Object[])new Object[]{ModList.get().applyForEachModFile(modFile -> String.format(Locale.ROOT, "%s %s : %s (%s) - %d", modFile.getProvider().name().replace(' ', '_'), modFile.getFileName(), ((IModInfo)modFile.getModInfos().get(0)).getModId(), ((IModInfo)modFile.getModInfos().get(0)).getVersion(), modFile.getModInfos().size())).collect(Collectors.joining("\n\u2022 ", "\n\u2022 ", ""))}), false);
            return 0;
        });
    }
}

