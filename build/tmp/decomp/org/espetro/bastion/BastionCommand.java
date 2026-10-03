/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package org.espetro.bastion;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.Espetro;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionManager;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.network.BastionSelectionPacket;
import org.espetro.network.NetworkManager;
import org.espetro.network.UnifiedDeployScreenPacket;
import org.espetro.team.ClassCountManager;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.TeamPackManager;

public class BastionCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("bastion").requires(source -> source.m_6761_(0))).then(((LiteralArgumentBuilder)Commands.m_82127_("select").requires(source -> source.m_6761_(0))).then(Commands.m_82129_("bastionId", StringArgumentType.string()).executes(context -> {
            ServerPlayer player = ((CommandSourceStack)context.getSource()).m_230896_();
            if (player == null) {
                return 0;
            }
            String bastionIdStr = StringArgumentType.getString((CommandContext)context, (String)"bastionId");
            try {
                UUID bastionId = UUID.fromString(bastionIdStr);
                return BastionCommand.selectBastion(player, bastionId);
            }
            catch (IllegalArgumentException e) {
                player.m_213846_(Component.m_237113_("\u00a7c\u65e0\u6548\u7684\u5175\u7ad9ID\uff01"));
                return 0;
            }
        })))).then(((LiteralArgumentBuilder)Commands.m_82127_("deploy").requires(source -> source.m_6761_(0))).executes(context -> {
            ServerPlayer player = ((CommandSourceStack)context.getSource()).m_230896_();
            if (player == null) {
                return 0;
            }
            return BastionCommand.respawnAtDeployPoint(player);
        }))).then(((LiteralArgumentBuilder)Commands.m_82127_("list").requires(source -> source.m_6761_(0))).executes(context -> {
            ServerPlayer player = ((CommandSourceStack)context.getSource()).m_230896_();
            if (player == null) {
                return 0;
            }
            return BastionCommand.listBastions(player);
        }))).then(Commands.m_82127_("help").executes(context -> {
            ServerPlayer player = ((CommandSourceStack)context.getSource()).m_230896_();
            if (player == null) {
                return 0;
            }
            player.m_213846_(Component.m_237113_("\u00a76=== \u5175\u7ad9\u547d\u4ee4\u5e2e\u52a9 ==="));
            player.m_213846_(Component.m_237113_("\u00a7e/bastion list \u00a77- \u67e5\u770b\u53ef\u7528\u5175\u7ad9\u5217\u8868"));
            player.m_213846_(Component.m_237113_("\u00a7e/bastion select <id> \u00a77- \u5728\u6307\u5b9a\u5175\u7ad9\u6216\u961f\u4f0d\u96c6\u7ed3\u70b9\u590d\u6d3b"));
            player.m_213846_(Component.m_237113_("\u00a7e/bastion deploy \u00a77- \u5728\u539f\u90e8\u7f72\u70b9\u590d\u6d3b"));
            player.m_213846_(Component.m_237113_("\u00a77(\u70b9\u51fb\u804a\u5929\u4e2d\u7684\u94fe\u63a5\u53ef\u76f4\u63a5\u9009\u62e9)"));
            return 1;
        })));
    }

    private static int selectBastion(ServerPlayer player, UUID bastionId) {
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.BATTLE && phase != GamePhase.DEPLOYING) {
            player.m_213846_(Component.m_237113_("\u00a7c\u53ea\u80fd\u5728\u6218\u6597\u6216\u90e8\u7f72\u9636\u6bb5\u4f7f\u7528\u6b64\u547d\u4ee4\uff01"));
            return 0;
        }
        String factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_());
        if (factionId == null) {
            player.m_213846_(Component.m_237113_("\u00a7c\u8bf7\u5148\u9009\u62e9\u9635\u8425\uff01"));
            return 0;
        }
        boolean success = BastionSelectionPacket.handleBastionSelect(player, bastionId);
        if (success) {
            if (!BastionManager.getInstance().isWaitingForBastion(player.m_20148_())) {
                BastionCommand.onDeployComplete(player);
            } else {
                NetworkManager.syncUnifiedDeployScreen(player, -1);
            }
            return 1;
        }
        return 0;
    }

    private static int respawnAtDeployPoint(ServerPlayer player) {
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.BATTLE && phase != GamePhase.DEPLOYING) {
            player.m_213846_(Component.m_237113_("\u00a7c\u53ea\u80fd\u5728\u6218\u6597\u6216\u90e8\u7f72\u9636\u6bb5\u4f7f\u7528\u6b64\u547d\u4ee4\uff01"));
            return 0;
        }
        String factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_());
        if (factionId == null) {
            player.m_213846_(Component.m_237113_("\u00a7c\u8bf7\u5148\u9009\u62e9\u9635\u8425\uff01"));
            return 0;
        }
        if (!BastionManager.getInstance().isWaitingForBastion(player.m_20148_())) {
            player.m_213846_(Component.m_237113_("\u00a7c\u4f60\u4e0d\u5728\u7b49\u5f85\u590d\u6d3b\u72b6\u6001\uff01"));
            return 0;
        }
        BastionManager.DeployPoint deployPoint = BastionManager.getInstance().getPlayerDeployPoint(player.m_20148_());
        if (deployPoint == null) {
            player.m_213846_(Component.m_237113_("\u00a7c\u65e0\u6cd5\u627e\u5230\u539f\u90e8\u7f72\u70b9\uff01"));
            return 0;
        }
        if (BastionManager.getInstance().respawnAtDeployPoint(BattlefieldContext.requireBattlefield(player.f_8924_), player)) {
            BastionCommand.onDeployComplete(player);
            return 1;
        }
        return 0;
    }

    private static void onDeployComplete(ServerPlayer player) {
        GameStateManager.getInstance().onMidGameDeployComplete(player);
    }

    private static int listBastions(ServerPlayer player) {
        String factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_());
        if (factionId == null) {
            player.m_213846_(Component.m_237113_("\u00a7c\u8bf7\u5148\u9009\u62e9\u9635\u8425\uff01"));
            return 0;
        }
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            player.m_213846_(Component.m_237113_("\u00a7c\u65e0\u6cd5\u786e\u5b9a\u4f60\u7684\u961f\u4f0d\uff01"));
            return 0;
        }
        List<BastionData> bastions = BastionManager.getInstance().getTeamBastions(team);
        if (bastions.isEmpty()) {
            player.m_213846_(Component.m_237113_("\u00a7c\u5f53\u524d\u6ca1\u6709\u53ef\u7528\u7684\u5175\u7ad9\uff01"));
            return 0;
        }
        player.m_213846_(Component.m_237113_("\u00a76=== \u53ef\u7528\u5175\u7ad9\u5217\u8868 ==="));
        for (BastionData bastion : bastions) {
            BastionManager manager = BastionManager.getInstance();
            BlockPos pos = manager.getRecordedArmorStandPosition(bastion);
            if (pos == null) continue;
            MutableComponent clickable = Component.m_237113_("\u00a7a- \u00a7e" + bastion.getName() + " \u00a77(" + pos.m_123341_() + ", " + pos.m_123342_() + ", " + pos.m_123343_() + ")").m_130938_(style -> style.m_131142_(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bastion select " + bastion.getBastionId().toString())).m_131144_(new HoverEvent(HoverEvent.Action.f_130831_, Component.m_237113_("\u00a7e\u70b9\u51fb\u9009\u62e9\u6b64\u5175\u7ad9"))));
            player.m_213846_(clickable);
        }
        for (UnifiedDeployScreenPacket.BastionItem item : TeamPackManager.getInstance().getDeployItemsForPlayer(player)) {
            MutableComponent clickable = Component.m_237113_("\u00a7d- \u00a7f" + item.name + " \u00a77(" + item.pos + ")").m_130938_(style -> style.m_131142_(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bastion select " + item.id)).m_131144_(new HoverEvent(HoverEvent.Action.f_130831_, Component.m_237113_("\u00a7e\u70b9\u51fb\u9009\u62e9\u6b64\u961f\u4f0d\u96c6\u7ed3\u70b9"))));
            player.m_213846_(clickable);
        }
        return bastions.size();
    }
}

