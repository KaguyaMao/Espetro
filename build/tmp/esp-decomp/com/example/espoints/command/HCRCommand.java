/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.coordinates.BlockPosArgument
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 */
package com.example.espoints.command;

import com.example.espoints.api.HCRAPI;
import com.example.espoints.capturepoint.CapturePointManager;
import com.example.espoints.command.TeamfightPresetManager;
import com.example.espoints.config.MapPlayerDisplayConfig;
import com.example.espoints.config.TeamfightJsonConfig;
import com.example.espoints.network.PlayLowReinforcementAudioMessage;
import com.example.espoints.network.SyncMapPlayerDisplayMessage;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class HCRCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"hcrpi").then(Commands.m_82127_((String)"help").executes(HCRCommand::executeHelp))).then(Commands.m_82127_((String)"send").then(Commands.m_82129_((String)"player", (ArgumentType)StringArgumentType.word()).suggests(HCRCommand::suggestExistingPlayers).then(Commands.m_82129_((String)"borderColor", (ArgumentType)StringArgumentType.word()).suggests(HCRCommand::suggestBorderColors).then(Commands.m_82129_((String)"content", (ArgumentType)StringArgumentType.greedyString()).executes(HCRCommand::executeSend)))))).then(Commands.m_82127_((String)"playsound").then(Commands.m_82129_((String)"soundName", (ArgumentType)StringArgumentType.word()).suggests(HCRCommand::suggestSoundNames).executes(HCRCommand::executePlaySound)))).then(Commands.m_82127_((String)"mapctrl").then(Commands.m_82129_((String)"state", (ArgumentType)StringArgumentType.word()).suggests(HCRCommand::suggestBooleanValues).executes(HCRCommand::executeMapCtrl)))).then(Commands.m_82127_((String)"reload").executes(HCRCommand::executeReload))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"teamfight").requires(source -> HCRCommand.hasPermission(source, 2))).then(Commands.m_82127_((String)"create").then(Commands.m_82129_((String)"batch", (ArgumentType)IntegerArgumentType.integer((int)1)).then(Commands.m_82129_((String)"name", (ArgumentType)StringArgumentType.word()).then(Commands.m_82129_((String)"pos1", (ArgumentType)BlockPosArgument.m_118239_()).then(Commands.m_82129_((String)"pos2", (ArgumentType)BlockPosArgument.m_118239_()).executes(HCRCommand::executeTeamfightCreate))))))).then(Commands.m_82127_((String)"list").executes(HCRCommand::executeTeamfightList))).then(Commands.m_82127_((String)"del").then(Commands.m_82129_((String)"name", (ArgumentType)StringArgumentType.word()).executes(HCRCommand::executeTeamfightDel)))).then(Commands.m_82127_((String)"clear").executes(HCRCommand::executeTeamfightClear))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"start").executes(HCRCommand::executeTeamfightStartFromConfig)).then(Commands.m_82129_((String)"totalBatches", (ArgumentType)IntegerArgumentType.integer((int)1)).then(Commands.m_82129_((String)"endBehavior", (ArgumentType)StringArgumentType.word()).suggests(HCRCommand::suggestEndBehavior).executes(HCRCommand::executeTeamfightStart))))).then(Commands.m_82127_((String)"stop").executes(HCRCommand::executeTeamfightStop))).then(Commands.m_82127_((String)"nextbatch").executes(HCRCommand::executeTeamfightNextBatch))).then(Commands.m_82127_((String)"save").then(Commands.m_82129_((String)"preset", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(HCRCommand::executeTeamfightSave)))).then(Commands.m_82127_((String)"load").then(Commands.m_82129_((String)"preset", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(HCRCommand::executeTeamfightLoad)))).then(Commands.m_82127_((String)"loadconfig").executes(HCRCommand::executeTeamfightLoadConfig))).then(Commands.m_82127_((String)"saveconfig").executes(HCRCommand::executeTeamfightSaveConfig)))).executes(HCRCommand::executeHelp));
    }

    private static int executeHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        source.m_288197_(() -> Component.m_237113_((String)"HCR\u636e\u70b9\u4e89\u593a\u6a21\u7ec4\u547d\u4ee4\u5e2e\u52a9"), false);
        source.m_288197_(() -> Component.m_237113_((String)"/hcrpi teamfight create <\u6279\u6b21> <\u540d\u79f0> <x1> <y1> <z1> <x2> <y2> <z2> - \u6dfb\u52a0\u884c\u52a8\u8ba1\u5212\u636e\u70b9"), false);
        source.m_288197_(() -> Component.m_237113_((String)"/hcrpi teamfight list - \u5217\u51fa\u884c\u52a8\u8ba1\u5212\u636e\u70b9"), false);
        source.m_288197_(() -> Component.m_237113_((String)"/hcrpi teamfight del <\u540d\u79f0> - \u5220\u9664\u884c\u52a8\u8ba1\u5212\u636e\u70b9"), false);
        source.m_288197_(() -> Component.m_237113_((String)"/hcrpi teamfight clear - \u6e05\u7a7a\u884c\u52a8\u8ba1\u5212\u636e\u70b9"), false);
        source.m_288197_(() -> Component.m_237113_((String)"/hcrpi teamfight start <\u603b\u6279\u6b21> <terminate|loop> - \u542f\u52a8\u884c\u52a8\uff0c\u961f\u4f0d\u56fa\u5b9a\u4f7f\u7528 Espetro \u8fdb\u653b\u65b9/\u9632\u5b88\u65b9"), false);
        source.m_288197_(() -> Component.m_237113_((String)"/hcrpi teamfight stop - \u505c\u6b62\u884c\u52a8"), false);
        source.m_288197_(() -> Component.m_237113_((String)"/hcrpi teamfight nextbatch - \u63a8\u8fdb\u5230\u4e0b\u4e00\u6279\u6b21"), false);
        source.m_288197_(() -> Component.m_237113_((String)"/hcrpi teamfight save/load <\u5e8f\u53f7> - \u4fdd\u5b58\u6216\u52a0\u8f7d\u884c\u52a8\u9884\u8bbe"), false);
        source.m_288197_(() -> Component.m_237113_((String)"/hcrpi teamfight loadconfig/saveconfig - \u6062\u590d\u5f53\u524d\u5730\u56fe\u5feb\u7167\u6216\u5bfc\u51fa\u8fd0\u884c\u72b6\u6001"), false);
        source.m_288197_(() -> Component.m_237113_((String)"/hcrpi send <\u73a9\u5bb6> <\u8fb9\u6846\u989c\u8272> <\u5185\u5bb9> - \u5411\u6307\u5b9a\u73a9\u5bb6\u53d1\u9001\u6d88\u606f\u5f39\u7a97"), false);
        source.m_288197_(() -> Component.m_237113_((String)"/hcrpi playsound <\u97f3\u6548\u540d> - \u76f4\u63a5\u64ad\u653e\u6307\u5b9a\u97f3\u6548\uff0c\u4f8b\u5982\uff1a/hcrpi playsound lastStandBGM"), false);
        source.m_288197_(() -> Component.m_237113_((String)"/hcrpi mapctrl <true|false> - \u63a7\u5236\u6218\u672f\u5730\u56fe\u73a9\u5bb6\u4f4d\u7f6e\u663e\u793a"), false);
        return 1;
    }

    private static int executePlaySound(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        String soundName = StringArgumentType.getString(context, (String)"soundName");
        if (!soundName.equalsIgnoreCase("lastStandBGM")) {
            source.m_81352_((Component)Component.m_237113_((String)"\u4e0d\u652f\u6301\u7684\u97f3\u6548\u540d\u79f0\uff01\u5f53\u524d\u53ea\u652f\u6301\uff1alastStandBGM"));
            return 0;
        }
        PlayLowReinforcementAudioMessage.broadcastToAll(true);
        source.m_288197_(() -> Component.m_237113_((String)("\u5df2\u5411\u6240\u6709\u73a9\u5bb6\u53d1\u9001\u97f3\u9891\u64ad\u653e\u6307\u4ee4\uff1a" + soundName)), true);
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestSoundNames(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        builder.suggest("lastStandBGM", (Message)Component.m_237113_((String)"\u80cc\u6c34\u4e00\u6218\u80cc\u666f\u97f3\u4e50"));
        return builder.buildFuture();
    }

    private static boolean hasPermission(CommandSourceStack source, int level) {
        if (!(source.m_81373_() instanceof ServerPlayer)) {
            return true;
        }
        ServerPlayer player = (ServerPlayer)source.m_81373_();
        return source.m_6761_(level);
    }

    private static int executeTeamfightCreate(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        int batch = IntegerArgumentType.getInteger(context, (String)"batch");
        String name = StringArgumentType.getString(context, (String)"name");
        BlockPos pos1 = BlockPosArgument.m_264582_(context, (String)"pos1");
        BlockPos pos2 = BlockPosArgument.m_264582_(context, (String)"pos2");
        CapturePointManager manager = CapturePointManager.getInstance();
        if (!manager.isValidPointName(name)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u521b\u5efa\u5931\u8d25\uff0c\u636e\u70b9\u540d\u79f0\u5fc5\u987b\u4e3a\u5355\u4e2a\u5927\u5199\u5b57\u6bcd\uff08A-Z\uff09"));
            return 0;
        }
        if (!manager.isValidCoordinates(pos1, pos2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u521b\u5efa\u5931\u8d25\uff0c\u4e24\u70b9\u9700\u6784\u6210\u6709\u6548\u957f\u65b9\u4f53\u533a\u57df"));
            return 0;
        }
        boolean success = manager.addPlannedCapturePoint(name, pos1, pos2, batch);
        if (success) {
            source.m_288197_(() -> Component.m_237113_((String)("\u636e\u70b9\u3010" + name + "\u3011\uff08\u6279\u6b21 " + batch + "\uff09\u5df2\u6dfb\u52a0\u5230\u8ba1\u5212\uff0c\u533a\u57df\uff1a(" + pos1.m_123341_() + "," + pos1.m_123342_() + "," + pos1.m_123343_() + ")-(" + pos2.m_123341_() + "," + pos2.m_123342_() + "," + pos2.m_123343_() + ")")), true);
            return 1;
        }
        source.m_81352_((Component)Component.m_237113_((String)("\u521b\u5efa\u5931\u8d25\uff0c\u636e\u70b9\u540d\u79f0\u3010" + name + "\u3011\u5df2\u5b58\u5728\u4e8e\u8ba1\u5212\u4e2d")));
        return 0;
    }

    private static int executeTeamfightList(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        CapturePointManager manager = CapturePointManager.getInstance();
        List<String> plannedPointsInfo = manager.getPlannedPointsInfo();
        if (plannedPointsInfo.isEmpty()) {
            source.m_288197_(() -> Component.m_237113_((String)"\u5f53\u524d\u65e0\u8ba1\u5212\u636e\u70b9"), false);
            return 1;
        }
        source.m_288197_(() -> Component.m_237113_((String)"\u884c\u52a8\u6a21\u5f0f\u8ba1\u5212\u636e\u70b9\u5217\u8868\uff1a"), false);
        for (String info : plannedPointsInfo) {
            source.m_288197_(() -> Component.m_237113_((String)info), false);
        }
        return 1;
    }

    private static int executeTeamfightDel(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        String name = StringArgumentType.getString(context, (String)"name");
        CapturePointManager manager = CapturePointManager.getInstance();
        boolean success = manager.removePlannedCapturePoint(name);
        if (success) {
            source.m_288197_(() -> Component.m_237113_((String)("\u8ba1\u5212\u636e\u70b9\u3010" + name + "\u3011\u5df2\u6210\u529f\u79fb\u9664")), true);
            return 1;
        }
        source.m_81352_((Component)Component.m_237113_((String)("\u5220\u9664\u5931\u8d25\uff0c\u672a\u627e\u5230\u540d\u79f0\u4e3a\u3010" + name + "\u3011\u7684\u8ba1\u5212\u636e\u70b9")));
        return 0;
    }

    private static int executeTeamfightClear(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        CapturePointManager manager = CapturePointManager.getInstance();
        manager.clearPlannedCapturePoints();
        source.m_288197_(() -> Component.m_237113_((String)"\u6240\u6709\u8ba1\u5212\u636e\u70b9\u5df2\u6e05\u7a7a"), true);
        return 1;
    }

    private static int executeTeamfightStart(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        CapturePointManager manager = CapturePointManager.getInstance();
        manager.bindEspetroTeams();
        int totalBatches = IntegerArgumentType.getInteger(context, (String)"totalBatches");
        String endBehavior = StringArgumentType.getString(context, (String)"endBehavior");
        if (!endBehavior.equalsIgnoreCase("terminate") && !endBehavior.equalsIgnoreCase("loop")) {
            source.m_81352_((Component)Component.m_237113_((String)"\u65e0\u6548\u7684\u7ed3\u675f\u884c\u4e3a\uff01\u8bf7\u4f7f\u7528 'terminate'(\u7ec8\u6b62) \u6216 'loop'(\u5faa\u73af)"));
            return 0;
        }
        manager.startOperationMode(totalBatches, endBehavior);
        source.m_288197_(() -> Component.m_237113_((String)("\u884c\u52a8\u5df2\u542f\u52a8\uff01\u5f53\u524d\u6279\u6b21\uff1a1, \u603b\u6279\u6570\uff1a" + totalBatches + ", \u7ed3\u675f\u884c\u4e3a\uff1a" + endBehavior)), true);
        return 1;
    }

    private static int executeTeamfightStartFromConfig(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        CapturePointManager manager = CapturePointManager.getInstance();
        manager.bindEspetroTeams();
        int totalBatches = Math.max(manager.getTotalBatches(), manager.calculateTotalBatches());
        if (totalBatches <= 0) {
            totalBatches = manager.calculateTotalBatches();
        }
        if (totalBatches <= 0 || manager.getPlannedPointsInfo().isEmpty()) {
            source.m_81352_((Component)Component.m_237113_((String)"\u542f\u52a8\u5931\u8d25\uff1a\u5f53\u524d Espetro \u5730\u56fe\u6ca1\u6709\u53ef\u7528\u7684\u8ba1\u5212\u636e\u70b9"));
            return 0;
        }
        String endBehavior = manager.getEndBehavior();
        if (endBehavior == null || !endBehavior.equalsIgnoreCase("terminate") && !endBehavior.equalsIgnoreCase("loop")) {
            endBehavior = "terminate";
        }
        manager.startOperationMode(totalBatches, endBehavior);
        String finalEndBehavior = endBehavior;
        int finalTotalBatches = totalBatches;
        source.m_288197_(() -> Component.m_237113_((String)("\u5df2\u6309\u5f53\u524d\u5730\u56fe\u542f\u52a8\u884c\u52a8\uff01\u5f53\u524d\u6279\u6b21\uff1a1, \u603b\u6279\u6570\uff1a" + finalTotalBatches + ", \u7ed3\u675f\u884c\u4e3a\uff1a" + finalEndBehavior)), true);
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestEndBehavior(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        builder.suggest("terminate");
        builder.suggest("loop");
        return builder.buildFuture();
    }

    private static int executeTeamfightStop(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        CapturePointManager manager = CapturePointManager.getInstance();
        manager.stopOperationMode();
        source.m_288197_(() -> Component.m_237113_((String)"\u884c\u52a8\u5df2\u505c\u6b62\uff01"), true);
        return 1;
    }

    private static int executeTeamfightNextBatch(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        CapturePointManager manager = CapturePointManager.getInstance();
        boolean success = manager.nextBatch();
        if (success) {
            source.m_288197_(() -> Component.m_237113_((String)("\u5df2\u8fdb\u5165\u4e0b\u4e00\u6279\u6b21\uff01\u5f53\u524d\u6279\u6b21\uff1a" + manager.getCurrentBatch())), true);
        } else {
            source.m_81352_((Component)Component.m_237113_((String)"\u6ca1\u6709\u66f4\u591a\u6279\u6b21\u4e86\uff01"));
        }
        return 1;
    }

    private static int executeTeamfightSave(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        int presetId = IntegerArgumentType.getInteger(context, (String)"preset");
        boolean success = TeamfightPresetManager.savePreset(presetId);
        if (success) {
            source.m_288197_(() -> Component.m_237113_((String)("\u884c\u52a8\u653b\u9632\u6a21\u5f0f\u9884\u8bbe " + presetId + " \u5df2\u6210\u529f\u4fdd\u5b58\uff01")), true);
        } else {
            source.m_81352_((Component)Component.m_237113_((String)"\u4fdd\u5b58\u9884\u8bbe\u5931\u8d25\uff01\u8bf7\u67e5\u770b\u670d\u52a1\u5668\u65e5\u5fd7\u83b7\u53d6\u8be6\u7ec6\u4fe1\u606f"));
        }
        return 1;
    }

    private static int executeTeamfightLoad(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        int presetId = IntegerArgumentType.getInteger(context, (String)"preset");
        boolean success = TeamfightPresetManager.loadPreset(presetId);
        if (success) {
            source.m_288197_(() -> Component.m_237113_((String)("\u884c\u52a8\u653b\u9632\u6a21\u5f0f\u9884\u8bbe " + presetId + " \u5df2\u6210\u529f\u52a0\u8f7d\uff01")), true);
        } else {
            source.m_81352_((Component)Component.m_237113_((String)"\u52a0\u8f7d\u9884\u8bbe\u5931\u8d25\uff01\u9884\u8bbe\u6587\u4ef6\u53ef\u80fd\u4e0d\u5b58\u5728\u6216\u683c\u5f0f\u9519\u8bef"));
        }
        return 1;
    }

    private static int executeTeamfightLoadConfig(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        TeamfightJsonConfig.LoadResult result = TeamfightJsonConfig.loadConfig(true);
        if (!result.isSuccess()) {
            source.m_81352_((Component)Component.m_237113_((String)("\u52a0\u8f7d\u884c\u52a8\u6a21\u5f0fJSON\u914d\u7f6e\u5931\u8d25\uff1a" + result.getMessage())));
            return 0;
        }
        source.m_288197_(() -> Component.m_237113_((String)("\u884c\u52a8\u6a21\u5f0fJSON\u914d\u7f6e\u5df2\u52a0\u8f7d\uff1a" + String.valueOf(result.getPath()) + "\uff0c\u8ba1\u5212\u636e\u70b9 " + result.getPlannedPointCount() + " \u4e2a\uff0c\u603b\u6279\u6b21 " + result.getTotalBatches() + "\uff0c\u7ed3\u675f\u884c\u4e3a " + result.getEndBehavior())), true);
        return 1;
    }

    private static int executeTeamfightSaveConfig(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        TeamfightJsonConfig.LoadResult result = TeamfightJsonConfig.saveCurrentConfig();
        if (!result.isSuccess()) {
            source.m_81352_((Component)Component.m_237113_((String)("\u4fdd\u5b58\u884c\u52a8\u6a21\u5f0fJSON\u914d\u7f6e\u5931\u8d25\uff1a" + result.getMessage())));
            return 0;
        }
        source.m_288197_(() -> Component.m_237113_((String)("\u5f53\u524d\u636e\u70b9\u72b6\u6001\u5df2\u5bfc\u51fa\uff1a" + String.valueOf(result.getPath()) + "\uff0c\u8ba1\u5212\u636e\u70b9 " + result.getPlannedPointCount() + " \u4e2a\uff0c\u603b\u6279\u6b21 " + result.getTotalBatches() + "\uff0c\u7ed3\u675f\u884c\u4e3a " + result.getEndBehavior())), true);
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestExistingPlayers(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        MinecraftServer server = ((CommandSourceStack)context.getSource()).m_81377_();
        if (server == null) {
            return builder.buildFuture();
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            builder.suggest(player.m_36316_().getName());
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestBorderColors(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        builder.suggest("FF0000", (Message)Component.m_237113_((String)"\u7ea2\u8272"));
        builder.suggest("FF5500", (Message)Component.m_237113_((String)"\u6a59\u8272"));
        builder.suggest("FFFF00", (Message)Component.m_237113_((String)"\u9ec4\u8272"));
        builder.suggest("00FF00", (Message)Component.m_237113_((String)"\u7eff\u8272"));
        builder.suggest("00FF55", (Message)Component.m_237113_((String)"\u6d45\u7eff\u8272"));
        builder.suggest("00AA00", (Message)Component.m_237113_((String)"\u6df1\u7eff\u8272"));
        builder.suggest("0000FF", (Message)Component.m_237113_((String)"\u84dd\u8272"));
        builder.suggest("5500FF", (Message)Component.m_237113_((String)"\u7d2b\u8272"));
        builder.suggest("0055FF", (Message)Component.m_237113_((String)"\u6df1\u84dd\u8272"));
        builder.suggest("FF00FF", (Message)Component.m_237113_((String)"\u7c89\u8272"));
        builder.suggest("FFFFFF", (Message)Component.m_237113_((String)"\u767d\u8272"));
        builder.suggest("888888", (Message)Component.m_237113_((String)"\u7070\u8272"));
        builder.suggest("00FFFF", (Message)Component.m_237113_((String)"\u9752\u8272"));
        return builder.buildFuture();
    }

    private static int executeSend(CommandContext<CommandSourceStack> context) {
        int borderColor;
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        String playerName = StringArgumentType.getString(context, (String)"player");
        String borderColorHex = StringArgumentType.getString(context, (String)"borderColor");
        String content = StringArgumentType.getString(context, (String)"content");
        MinecraftServer server = source.m_81377_();
        if (server == null) {
            source.m_81352_((Component)Component.m_237113_((String)"\u65e0\u6cd5\u83b7\u53d6\u670d\u52a1\u5668\u5b9e\u4f8b"));
            return 0;
        }
        ServerPlayer targetPlayer = server.m_6846_().m_11255_(playerName);
        if (targetPlayer == null) {
            source.m_81352_((Component)Component.m_237113_((String)("\u672a\u627e\u5230\u73a9\u5bb6\uff1a" + playerName)));
            return 0;
        }
        if (!borderColorHex.matches("[0-9A-Fa-f]{6}")) {
            source.m_81352_((Component)Component.m_237113_((String)"\u65e0\u6548\u7684\u8fb9\u6846\u989c\u8272\u683c\u5f0f\uff0c\u5fc5\u987b\u662f6\u4f4d\u5341\u516d\u8fdb\u5236\u5b57\u7b26\uff08\u4e0d\u5e26#\uff09\uff0c\u4f8b\u5982\uff1aFF5500"));
            return 0;
        }
        try {
            borderColor = Integer.parseInt(borderColorHex, 16);
            borderColor = 0xFF000000 | borderColor;
        }
        catch (NumberFormatException e) {
            source.m_81352_((Component)Component.m_237113_((String)"\u65e0\u6548\u7684\u8fb9\u6846\u989c\u8272\u503c"));
            return 0;
        }
        HCRAPI.showMessage(targetPlayer.m_20148_(), content, 4000L, 150, 40, -16777216, -1, borderColor, 2);
        source.m_288197_(() -> Component.m_237113_((String)("\u5df2\u5411\u73a9\u5bb6" + playerName + "\u53d1\u9001\u6d88\u606f\uff1a" + content)), true);
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestBooleanValues(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        ArrayList<String> booleanValues = new ArrayList<String>();
        booleanValues.add("true");
        booleanValues.add("false");
        for (String value : booleanValues) {
            if (!value.startsWith(builder.getRemaining())) continue;
            builder.suggest(value);
        }
        return builder.buildFuture();
    }

    private static int executeMapCtrl(CommandContext<CommandSourceStack> context) {
        boolean newState;
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        String stateStr = (String)context.getArgument("state", String.class);
        if (stateStr.equalsIgnoreCase("true")) {
            newState = true;
        } else if (stateStr.equalsIgnoreCase("false")) {
            newState = false;
        } else {
            source.m_81352_((Component)Component.m_237113_((String)"\u65e0\u6548\u7684\u72b6\u6001\u503c\uff0c\u53ea\u80fd\u662ftrue\u6216false"));
            return 0;
        }
        MapPlayerDisplayConfig config = MapPlayerDisplayConfig.getInstance();
        config.setShowPlayerLocations(newState);
        SyncMapPlayerDisplayMessage.broadcastToAll();
        source.m_288197_(() -> Component.m_237113_((String)("\u5df2" + (newState ? "\u5f00\u542f" : "\u5173\u95ed") + "\u5730\u56fe\u73a9\u5bb6\u4f4d\u7f6e\u663e\u793a")), true);
        return 1;
    }

    private static int executeReload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (!HCRCommand.hasPermission(source, 2)) {
            source.m_81352_((Component)Component.m_237113_((String)"\u6743\u9650\u4e0d\u8db3\uff0c\u97002\u7ea7\u7ba1\u7406\u5458\u6743\u9650"));
            return 0;
        }
        MapPlayerDisplayConfig.getInstance().loadConfig();
        TeamfightJsonConfig.LoadResult teamfightConfigResult = TeamfightJsonConfig.loadConfig(true);
        SyncMapPlayerDisplayMessage.broadcastToAll();
        String teamfightConfigMessage = teamfightConfigResult.isSuccess() ? "\uff1b\u5f53\u524d\u5730\u56fe\u636e\u70b9\u5df2\u6062\u590d\uff0c\u8ba1\u5212\u636e\u70b9 " + teamfightConfigResult.getPlannedPointCount() + " \u4e2a" : "\uff1b\u5f53\u524d\u5730\u56fe\u636e\u70b9\u672a\u6062\u590d\uff1a" + teamfightConfigResult.getMessage();
        source.m_288197_(() -> Component.m_237113_((String)("\u5df2\u91cd\u65b0\u52a0\u8f7d\u914d\u7f6e\u6587\u4ef6\uff0c\u5f53\u524d\u5730\u56fe\u73a9\u5bb6\u4f4d\u7f6e\u663e\u793a\uff1a" + MapPlayerDisplayConfig.getInstance().isShowPlayerLocations() + teamfightConfigMessage + "\uff1b\u6218\u672f\u5730\u56fe\u7531\u5f53\u524d Espetro \u5730\u56fe\u63d0\u4f9b")), true);
        return 1;
    }
}

