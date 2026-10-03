/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 */
package net.minecraftforge.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

class DimensionsCommand {
    DimensionsCommand() {
    }

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.m_82127_((String)"dimensions").requires(cs -> cs.m_6761_(0))).executes(ctx -> {
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237115_((String)"commands.forge.dimensions.list"), true);
            Registry reg = ((CommandSourceStack)ctx.getSource()).m_5894_().m_175515_(Registries.f_256787_);
            HashMap<ResourceLocation, List> types = new HashMap<ResourceLocation, List>();
            for (ServerLevel dim : ((CommandSourceStack)ctx.getSource()).m_81377_().m_129785_()) {
                types.computeIfAbsent(reg.m_7981_((Object)dim.m_6042_()), k -> new ArrayList()).add(dim.m_46472_().m_135782_());
            }
            types.keySet().stream().sorted().forEach(key -> ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)(String.valueOf(key) + ": " + ((List)types.get(key)).stream().map(ResourceLocation::toString).sorted().collect(Collectors.joining(", ")))), false));
            return 0;
        });
    }
}

