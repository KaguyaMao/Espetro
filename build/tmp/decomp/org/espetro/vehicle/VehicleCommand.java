/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package org.espetro.vehicle;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.espetro.team.ClassCountManager;
import org.espetro.vehicle.VehicleManager;

public class VehicleCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("vehicle").requires(source -> source.m_6761_(0))).then(Commands.m_82127_("list").executes(VehicleCommand::listVehicles)));
    }

    private static int listVehicles(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = (CommandSourceStack)ctx.getSource();
        Entity entity = source.m_81373_();
        if (!(entity instanceof ServerPlayer)) {
            source.m_81352_(Component.m_237113_("\u00a7c\u53ea\u6709\u73a9\u5bb6\u53ef\u4ee5\u6267\u884c\u6b64\u547d\u4ee4\uff01"));
            return 0;
        }
        ServerPlayer player = (ServerPlayer)entity;
        String factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_());
        if (factionId == null) {
            source.m_81352_(Component.m_237113_("\u00a7c\u4f60\u6ca1\u6709\u9009\u62e9\u7f16\u5236\uff01"));
            return 0;
        }
        source.m_243053_(Component.m_237113_("\u00a76\u2550\u2550\u2550 \u8f7d\u5177\u72b6\u6001 \u2550\u2550\u2550").m_130948_(Style.f_131099_.m_131148_(TextColor.m_131266_(0xFFAA00)).m_131136_(true)));
        source.m_243053_(Component.m_237113_("\u00a77\u7f16\u5236: " + factionId));
        List<String> status = VehicleManager.getInstance().getFactionVehicleStatus(factionId);
        if (status.isEmpty()) {
            source.m_243053_(Component.m_237113_("\u00a77\u5f53\u524d\u7f16\u5236\u6ca1\u6709\u53ef\u90e8\u7f72\u7684\u8f7d\u5177\u3002"));
        } else {
            for (String line : status) {
                source.m_243053_(Component.m_237113_("  " + line));
            }
        }
        return 1;
    }
}

