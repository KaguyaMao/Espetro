/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package org.espetro.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.espetro.Espetro;
import org.espetro.bastion.BastionManager;
import org.espetro.dimension.BattlefieldWorldManager;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.mapconfig.ExternalConfigBootstrap;
import org.espetro.team.ClassCountManager;
import org.espetro.team.ClassEquipment;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.GameStateManager;
import org.espetro.team.PartyManager;
import org.espetro.team.SpawnPointConfig;
import org.espetro.team.TeamManager;
import org.espetro.team.TeamPackManager;
import org.espetro.team.TroopCountManager;
import org.espetro.tutorial.TutorialManager;

public class EspetroCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("espetro").requires(source -> source.m_6761_(2))).then(Commands.m_82127_("reload").executes(ctx -> {
            Espetro.reloadAllConfigs();
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7e[Espetro] \u5916\u90e8\u5730\u56fe/\u7f16\u5236/EsConfig \u4e0d\u652f\u6301\u70ed\u91cd\u8f7d\uff1b\u4fee\u6539\u5c06\u5728\u91cd\u542f\u540e\u751f\u6548\u3002"));
            return 1;
        }))).then(Commands.m_82127_("prestart").executes(ctx -> {
            boolean started = GameStateManager.getInstance().prestart(((CommandSourceStack)ctx.getSource()).m_81377_());
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_(started ? "\u00a7a[Espetro] \u5df2\u5f00\u59cb\u5730\u56fe\u6295\u7968\u3002" : "\u00a7c[Espetro] \u65e0\u6cd5\u5f00\u59cb\uff1a\u5fc5\u987b\u5904\u4e8e\u4e3b\u57ce\u3001\u81f3\u5c11\u4e00\u4eba\u5728\u7ebf\u4e14\u5b58\u5728\u6709\u6548\u5730\u56fe/\u7f16\u5236\u3002"));
            return started ? 1 : 0;
        }))).then(Commands.m_82127_("stop").executes(ctx -> {
            CommandSourceStack source = (CommandSourceStack)ctx.getSource();
            boolean stopping = GameStateManager.getInstance().forceStopGame(source.m_81377_(), result -> source.m_243053_(Component.m_237113_((String)(result.success() ? "\u00a7a[Espetro] \u5f3a\u5236\u7ed3\u675f\u5b8c\u6210\uff1a\u73a9\u5bb6\u5df2\u56de\u5230\u4e3b\u57ce\uff0c\u6218\u573a\u5b58\u6863\u526f\u672c\u5df2\u5220\u9664\u3002" : "\u00a7c[Espetro] \u73a9\u5bb6\u5df2\u56de\u5230\u4e3b\u57ce\uff0c\u4f46\u6218\u573a\u5b58\u6863\u526f\u672c\u5220\u9664\u5931\u8d25\uff1a" + result.error()))));
            source.m_243053_(Component.m_237113_(stopping ? "\u00a7e[Espetro] \u6b63\u5728\u5f3a\u5236\u7ed3\u675f\u6e38\u620f\u5e76\u5c06\u6240\u6709\u73a9\u5bb6\u9001\u56de\u4e3b\u57ce\u2026\u2026" : "\u00a7c[Espetro] \u5df2\u6709\u4e00\u4e2a\u5f3a\u5236\u7ed3\u675f\u6d41\u7a0b\u6b63\u5728\u6267\u884c\u3002"));
            return stopping ? 1 : 0;
        }))).then(Commands.m_82127_("end").then(Commands.m_82129_("winner", StringArgumentType.word()).executes(ctx -> {
            String winner = StringArgumentType.getString((CommandContext)ctx, (String)"winner");
            boolean ended = GameStateManager.getInstance().endRound(winner);
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_(ended ? "\u00a7a[Espetro] \u5df2\u7ed3\u675f\u672c\u56de\u5408\u3002" : "\u00a7c[Espetro] \u4ec5\u6218\u6597\u9636\u6bb5\u53ef\u7ed3\u675f\uff0cwinner \u5fc5\u987b\u662f attack/defend/draw\u3002"));
            return ended ? 1 : 0;
        })))).then(Commands.m_82127_("maps").executes(ctx -> {
            List<ActiveMapConfig> maps = ExternalConfigBootstrap.getAllMaps();
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76========== \u5730\u56fe\u6ce8\u518c\u72b6\u6001 =========="));
            for (ActiveMapConfig map : maps) {
                String status;
                boolean formationsPlayable = map.usable && FactionDataProvider.getOrCreateLoader().isMapPlayable(map);
                String string = status = formationsPlayable ? "\u00a7a\u53ef\u7528" : "\u00a7c\u62d2\u7edd";
                Object reason = !map.rejectionReasons.isEmpty() ? " \u00a77- " + String.join((CharSequence)"; ", map.rejectionReasons) : (!formationsPlayable ? " \u00a77- \u81f3\u5c11\u9700\u8981\u4e24\u4e2a faction_id \u4e0d\u540c\u4e14\u4e0e VehSpawn \u517c\u5bb9\u7684\u7f16\u5236" : "");
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_(status + " \u00a7f" + map.displayName + " \u00a77[" + map.dimensionId + "]" + (String)reason));
            }
            for (String error : ExternalConfigBootstrap.getBootstrapErrors()) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c" + error));
            }
            return maps.size();
        }))).then(Commands.m_82127_("reset").executes(ctx -> {
            MinecraftServer server = ((CommandSourceStack)ctx.getSource()).m_81377_();
            ClassCountManager.getInstance().resetAll();
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                ClassEquipment.clearEquipment(player);
            }
            GameStateManager.getInstance().resetGame();
            BastionManager.getInstance().reset();
            TeamPackManager.getInstance().reset();
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76[Espetro] \u5df2\u91cd\u7f6e\u6240\u6709\u73a9\u5bb6\u72b6\u6001\u548c\u804c\u4e1a\u4eba\u6570"));
            return 1;
        }))).then(Commands.m_82127_("start").executes(ctx -> {
            boolean started = GameStateManager.getInstance().prestart(((CommandSourceStack)ctx.getSource()).m_81377_());
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_(started ? "\u00a7a[Espetro] /start \u517c\u5bb9\u522b\u540d\u5df2\u5f00\u59cb\u5730\u56fe\u6295\u7968\u3002" : "\u00a7c[Espetro] \u5f53\u524d\u65e0\u6cd5\u5f00\u59cb\u3002"));
            return started ? 1 : 0;
        }))).then(Commands.m_82127_("status").executes(ctx -> {
            MinecraftServer server = ((CommandSourceStack)ctx.getSource()).m_81377_();
            GameStateManager gameState = GameStateManager.getInstance();
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76========== \u5bf9\u5c40\u72b6\u6001 =========="));
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7e\u9636\u6bb5: \u00a7f" + gameState.getCurrentPhase().name() + " \u00a77(" + gameState.getCurrentPhase().getDisplayName() + ")"));
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7e\u5728\u7ebf\u73a9\u5bb6: \u00a7f" + server.m_7416_() + " \u4eba"));
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7e\u5df2\u9009\u8fb9: \u00a7f" + gameState.getTeamSelectedCount() + " \u4eba"));
            BattlefieldWorldManager.StartupPreparationResult startup = BattlefieldWorldManager.getInstance().getStartupPreparation();
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7e\u6218\u573a\u542f\u52a8\u95e8\u7981: \u00a7f" + startup.status() + " \u00a77prepared=" + startup.preparedCount() + (String)(startup.error() == null ? "" : " \u00a7c" + startup.error())));
            BattlefieldContext.get().ifPresent(map -> ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7e\u6218\u573a: \u00a7f" + map.displayName + " \u00a77[" + map.dimensionId + "]")));
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76================================"));
            return 1;
        }))).then(Commands.m_82127_("teams").executes(ctx -> {
            MinecraftServer server = ((CommandSourceStack)ctx.getSource()).m_81377_();
            int attackCount = TeamManager.getTeamSize(server, "espetro_attack");
            int defendCount = TeamManager.getTeamSize(server, "espetro_defend");
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76========== \u961f\u4f0d\u4fe1\u606f =========="));
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u8fdb\u653b\u65b9: \u00a7f" + attackCount + " \u4eba"));
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a79\u9632\u5b88\u65b9: \u00a7f" + defendCount + " \u4eba"));
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76=============================="));
            return 1;
        }))).then(Commands.m_82127_("factions").executes(ctx -> {
            FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76========== \u53ef\u7528\u9635\u8425 =========="));
            for (FactionDataLoader.FactionData faction : loader.getAllFactions()) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_(faction.icon + " \u00a7e" + faction.name + " \u00a77- " + faction.description));
            }
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76================================"));
            return 1;
        }))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("spawnpoint").executes(ctx -> {
            GameStateManager gameState = GameStateManager.getInstance();
            Map<String, SpawnPointConfig.SpawnPoint> spawnPoints = gameState.getAllSpawnPoints();
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76========== \u590d\u6d3b\u70b9\u914d\u7f6e =========="));
            for (Map.Entry<String, SpawnPointConfig.SpawnPoint> entry : spawnPoints.entrySet()) {
                SpawnPointConfig.SpawnPoint sp = entry.getValue();
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7e" + entry.getKey() + ": \u00a77x=" + sp.x + " y=" + sp.y + " z=" + sp.z + " yaw=" + sp.yaw));
            }
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76================================"));
            return 1;
        })).then(Commands.m_82127_("set").then(Commands.m_82129_("team", StringArgumentType.string()).then(Commands.m_82129_("x", IntegerArgumentType.integer()).then(Commands.m_82129_("y", IntegerArgumentType.integer()).then(((RequiredArgumentBuilder)Commands.m_82129_("z", IntegerArgumentType.integer()).executes(ctx -> {
            String team = StringArgumentType.getString((CommandContext)ctx, (String)"team").toUpperCase();
            if (!team.equals("ATTACK") && !team.equals("DEFEND")) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u961f\u4f0d\u5fc5\u987b\u662f ATTACK \u6216 DEFEND"));
                return 0;
            }
            int x = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"x");
            int y = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"y");
            int z = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"z");
            float yaw = team.equals("ATTACK") ? 0.0f : 180.0f;
            GameStateManager.getInstance().setTeamSpawnPoint(team, x, y, z, yaw);
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7a\u5df2\u8bbe\u7f6e " + team + " \u590d\u6d3b\u70b9: (" + x + ", " + y + ", " + z + ")"));
            return 1;
        })).then(Commands.m_82129_("yaw", FloatArgumentType.floatArg()).executes(ctx -> {
            String team = StringArgumentType.getString((CommandContext)ctx, (String)"team").toUpperCase();
            if (!team.equals("ATTACK") && !team.equals("DEFEND")) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u961f\u4f0d\u5fc5\u987b\u662f ATTACK \u6216 DEFEND"));
                return 0;
            }
            int x = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"x");
            int y = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"y");
            int z = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"z");
            float yaw = FloatArgumentType.getFloat((CommandContext)ctx, (String)"yaw");
            GameStateManager.getInstance().setTeamSpawnPoint(team, x, y, z, yaw);
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7a\u5df2\u8bbe\u7f6e " + team + " \u590d\u6d3b\u70b9: (" + x + ", " + y + ", " + z + ") yaw=" + yaw));
            return 1;
        })))))))).then(Commands.m_82127_("here").then(Commands.m_82129_("team", StringArgumentType.string()).executes(ctx -> {
            String team = StringArgumentType.getString((CommandContext)ctx, (String)"team").toUpperCase();
            if (!team.equals("ATTACK") && !team.equals("DEFEND")) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u961f\u4f0d\u5fc5\u987b\u662f ATTACK \u6216 DEFEND"));
                return 0;
            }
            Entity patt14259$temp = ((CommandSourceStack)ctx.getSource()).m_81373_();
            if (!(patt14259$temp instanceof ServerPlayer)) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u6b64\u547d\u4ee4\u9700\u8981\u5728\u6e38\u620f\u4e2d\u6267\u884c"));
                return 0;
            }
            ServerPlayer player = (ServerPlayer)patt14259$temp;
            int x = (int)player.m_20185_();
            int y = (int)player.m_20186_();
            int z = (int)player.m_20189_();
            float yaw = player.m_146908_();
            float pitch = player.m_146909_();
            GameStateManager.getInstance().setTeamSpawnPoint(team, x, y, z, yaw);
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7a\u5df2\u8bbe\u7f6e " + team + " \u590d\u6d3b\u70b9\u4e3a\u5f53\u524d\u4f4d\u7f6e: (" + x + ", " + y + ", " + z + ") yaw=" + yaw));
            return 1;
        }))))).then(Commands.m_82127_("setclass").then(Commands.m_82129_("faction", StringArgumentType.string()).then(((RequiredArgumentBuilder)Commands.m_82129_("class", StringArgumentType.string()).then(Commands.m_82129_("player", StringArgumentType.string()).executes(ctx -> {
            String factionId = StringArgumentType.getString((CommandContext)ctx, (String)"faction");
            String classId = StringArgumentType.getString((CommandContext)ctx, (String)"class");
            String playerName = StringArgumentType.getString((CommandContext)ctx, (String)"player");
            FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
            FactionDataLoader.ClassKitData kit = loader.getClassKit(classId);
            if (kit == null) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u65e0\u6548\u7684\u804c\u4e1a: " + classId));
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a77\u53ef\u7528\u804c\u4e1a: " + String.join((CharSequence)", ", loader.getClassIdsForFaction(factionId))));
                return 0;
            }
            if (!factionId.equals(kit.factionId)) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u804c\u4e1a " + classId + " \u4e0d\u5c5e\u4e8e\u7f16\u5236 " + factionId));
                return 0;
            }
            if (kit.variants == null || kit.variants.size() != 1) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u804c\u4e1a " + classId + " \u6709\u591a\u4e2a\u88c5\u5907\u53d8\u4f53\uff0c\u8bf7\u4f7f\u7528 /espetro setclass " + factionId + " " + classId + " variant <\u53d8\u4f53ID> " + playerName));
                return 0;
            }
            MinecraftServer server = ((CommandSourceStack)ctx.getSource()).m_81377_();
            ServerPlayer player = server.m_6846_().m_11255_(playerName);
            if (player == null) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u73a9\u5bb6\u4e0d\u5728\u7ebf: " + playerName));
                return 0;
            }
            ClassEquipment.equipPlayer(player, factionId, classId);
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7a\u5df2\u4e3a " + playerName + " \u88c5\u5907: " + kit.name));
            return 1;
        }))).then(Commands.m_82127_("variant").then(Commands.m_82129_("variant", StringArgumentType.string()).then(Commands.m_82129_("player", StringArgumentType.string()).executes(ctx -> {
            String factionId = StringArgumentType.getString((CommandContext)ctx, (String)"faction");
            String classId = StringArgumentType.getString((CommandContext)ctx, (String)"class");
            String variantId = StringArgumentType.getString((CommandContext)ctx, (String)"variant");
            String playerName = StringArgumentType.getString((CommandContext)ctx, (String)"player");
            FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
            FactionDataLoader.ClassKitData kit = loader.getClassKit(classId);
            FactionDataLoader.ClassVariantData variant = loader.getClassVariant(classId, variantId);
            if (kit == null || !factionId.equals(kit.factionId) || variant == null) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u65e0\u6548\u7684\u804c\u4e1a\u88c5\u5907\u53d8\u4f53: " + factionId + "/" + classId + "/" + variantId));
                return 0;
            }
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81377_().m_6846_().m_11255_(playerName);
            if (player == null) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u73a9\u5bb6\u4e0d\u5728\u7ebf: " + playerName));
                return 0;
            }
            ClassEquipment.equipPlayer(player, factionId, classId, variantId);
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7a\u5df2\u4e3a " + playerName + " \u88c5\u5907: " + kit.name + " / " + variant.name));
            return 1;
        })))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("troops").executes(ctx -> {
            int attack = TroopCountManager.getInstance().getAttackTroops();
            int defend = TroopCountManager.getInstance().getDefendTroops();
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76========== \u5f53\u524d\u5175\u529b =========="));
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u25a0 \u8fdb\u653b\u65b9: \u00a7f" + attack));
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a79\u25a0 \u9632\u5b88\u65b9: \u00a7f" + defend));
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76================================"));
            return 1;
        })).then(Commands.m_82127_("set").then(Commands.m_82129_("team", StringArgumentType.string()).then(Commands.m_82129_("value", IntegerArgumentType.integer()).executes(ctx -> {
            String team = StringArgumentType.getString((CommandContext)ctx, (String)"team").toUpperCase();
            int value = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"value");
            if (!team.equals("ATTACK") && !team.equals("DEFEND")) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u961f\u4f0d\u5fc5\u987b\u662f ATTACK \u6216 DEFEND"));
                return 0;
            }
            if (value < 0) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u5175\u529b\u503c\u4e0d\u80fd\u4e3a\u8d1f\u6570"));
                return 0;
            }
            if ("ATTACK".equals(team)) {
                TroopCountManager.getInstance().setAttackTroops(value);
                Espetro.broadcastToAll("\u00a76[\u7ba1\u7406] \u653b\u65b9\u5175\u529b\u5df2\u8bbe\u7f6e\u4e3a: \u00a7c" + value);
            } else {
                TroopCountManager.getInstance().setDefendTroops(value);
                Espetro.broadcastToAll("\u00a76[\u7ba1\u7406] \u5b88\u65b9\u5175\u529b\u5df2\u8bbe\u7f6e\u4e3a: \u00a79" + value);
            }
            return 1;
        }))))).then(Commands.m_82127_("add").then(Commands.m_82129_("team", StringArgumentType.string()).then(Commands.m_82129_("value", IntegerArgumentType.integer()).executes(ctx -> {
            String team = StringArgumentType.getString((CommandContext)ctx, (String)"team").toUpperCase();
            int value = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"value");
            if (!team.equals("ATTACK") && !team.equals("DEFEND")) {
                ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7c\u961f\u4f0d\u5fc5\u987b\u662f ATTACK \u6216 DEFEND"));
                return 0;
            }
            if ("ATTACK".equals(team)) {
                int current = TroopCountManager.getInstance().getAttackTroops();
                TroopCountManager.getInstance().setAttackTroops(current + value);
                Espetro.broadcastToTeam("ATTACK", "\u00a76[\u7ba1\u7406] \u653b\u65b9\u5175\u529b\u589e\u52a0\u4e86 " + value + " (\u5f53\u524d: \u00a7c" + (current + value) + "\u00a76)");
            } else {
                int current = TroopCountManager.getInstance().getDefendTroops();
                TroopCountManager.getInstance().setDefendTroops(current + value);
                Espetro.broadcastToTeam("DEFEND", "\u00a76[\u7ba1\u7406] \u5b88\u65b9\u5175\u529b\u589e\u52a0\u4e86 " + value + " (\u5f53\u524d: \u00a79" + (current + value) + "\u00a76)");
            }
            return 1;
        }))))).then(Commands.m_82127_("reset").executes(ctx -> {
            TroopCountManager.getInstance().initializeTroops();
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7a\u5175\u529b\u5df2\u91cd\u7f6e\u4e3a\u521d\u59cb\u503c: \u653b\u65b9280 | \u5b88\u65b91200"));
            return 1;
        })))).then(Commands.m_82127_("party").then(Commands.m_82127_("maxsize").then(Commands.m_82129_("size", IntegerArgumentType.integer((int)1, (int)100)).executes(ctx -> {
            int size = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"size");
            PartyManager.setMaxPartySize(size);
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a7a\u7ec4\u961f\u4e0a\u9650\u5df2\u8bbe\u7f6e\u4e3a " + size + " \u4eba\u3002"));
            return 1;
        })))));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("espetro").requires(source -> source.m_6761_(2))).executes(ctx -> {
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76[Espetro] \u6218\u672f\u5c0f\u961f\u6a21\u7ec4 v1.0"));
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a77\u4f7f\u7528 /espetro factions \u67e5\u770b\u53ef\u7528\u9635\u8425"));
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a77\u4f7f\u7528 /espetro troops \u67e5\u770b/\u7ba1\u7406\u5175\u529b"));
            return 1;
        }));
        dispatcher.register((LiteralArgumentBuilder)Commands.m_82127_("espetro").then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("tutorial").executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            TutorialManager.getInstance().reopen(player);
            return 1;
        })).then(Commands.m_82127_("next").executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            TutorialManager.getInstance().handleAction(player, TutorialManager.Action.NEXT, null);
            return 1;
        }))).then(Commands.m_82127_("skip").executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            TutorialManager.getInstance().skipAll(player);
            return 1;
        }))).then(Commands.m_82127_("dismiss").executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            TutorialManager.getInstance().handleAction(player, TutorialManager.Action.DISMISS, null);
            return 1;
        }))).then(Commands.m_82127_("status").executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            String status = TutorialManager.getInstance().statusLine(player);
            ((CommandSourceStack)ctx.getSource()).m_243053_(Component.m_237113_("\u00a76[Espetro \u6559\u7a0b] \u00a7f" + status));
            return 1;
        }))));
    }
}

