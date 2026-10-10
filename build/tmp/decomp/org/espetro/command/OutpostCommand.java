/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package org.espetro.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.Espetro;
import org.espetro.team.GameStateManager;
import org.espetro.team.OutpostManager;
import org.espetro.team.TeamDisplayNames;

public class OutpostCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("outpost").then(Commands.m_82127_("deploy").then(Commands.m_82129_("index", IntegerArgumentType.integer((int)1)).executes(ctx -> OutpostCommand.deployOutpost(((CommandSourceStack)ctx.getSource()).m_230896_(), IntegerArgumentType.getInteger((CommandContext)ctx, (String)"index") - 1))))).then(Commands.m_82127_("list").executes(ctx -> OutpostCommand.listOutposts(((CommandSourceStack)ctx.getSource()).m_230896_())))).then(Commands.m_82127_("redeploy").executes(ctx -> OutpostCommand.startRedeploy(((CommandSourceStack)ctx.getSource()).m_230896_())));
    }

    private static int deployOutpost(ServerPlayer player, int index) {
        if (player == null) {
            return 0;
        }
        String error = OutpostManager.getInstance().tryDeploy(player, index);
        if (error != null) {
            player.m_213846_(Component.m_237113_(error));
            return 0;
        }
        GameStateManager.getInstance().onMidGameDeployComplete(player);
        return 1;
    }

    private static int startRedeploy(ServerPlayer player) {
        if (player == null) {
            return 0;
        }
        String error = OutpostManager.getInstance().tryStartRedeploy(player);
        if (error != null) {
            player.m_213846_(Component.m_237113_(error));
            return 0;
        }
        return 1;
    }

    private static int listOutposts(ServerPlayer player) {
        if (player == null) {
            return 0;
        }
        if (TeamDisplayNames.isSymmetricMode()) {
            player.m_213846_(Component.m_237113_("\u00a7c\u5f53\u524d\u6a21\u5f0f\u53cc\u65b9\u90fd\u4e0d\u53ef\u4f7f\u7528\u524d\u54e8\u57fa\u5730\uff01"));
            return 0;
        }
        if (!"DEFEND".equals(Espetro.getPlayerTeam(player))) {
            player.m_213846_(Component.m_237113_("\u00a7c\u53ea\u6709\u9632\u5b88\u65b9\u53ef\u4ee5\u67e5\u770b\u524d\u54e8\u57fa\u5730\uff01"));
            return 0;
        }
        List<OutpostManager.Outpost> outposts = OutpostManager.getInstance().getOutposts();
        if (outposts.isEmpty()) {
            player.m_213846_(Component.m_237113_("\u00a77\u5f53\u524d\u6ca1\u6709\u914d\u7f6e\u524d\u54e8\u57fa\u5730"));
            return 0;
        }
        if (!OutpostManager.getInstance().isAvailable()) {
            player.m_213846_(Component.m_237113_("\u00a7c\u524d\u54e8\u57fa\u5730\u5f53\u524d\u4e0d\u53ef\u7528\uff08\u4ec5\u90e8\u7f72\u9636\u6bb5\u53ef\u7528\uff09"));
            return 0;
        }
        player.m_213846_(Component.m_237113_("\u00a76=== \u53ef\u7528\u524d\u54e8\u57fa\u5730 ==="));
        for (int i = 0; i < outposts.size(); ++i) {
            OutpostManager.Outpost op = outposts.get(i);
            player.m_213846_(Component.m_237113_("\u00a7e" + (i + 1) + ". \u00a7f" + op.name + " \u00a77(" + op.getPosString() + ")"));
        }
        return 1;
    }
}

