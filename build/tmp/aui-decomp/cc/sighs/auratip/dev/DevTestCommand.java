/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.registry.extra.CommandRegister
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.Commands$CommandSelection
 *  net.minecraft.commands.arguments.ResourceLocationArgument
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 */
package cc.sighs.auratip.dev;

import cc.sighs.auratip.api.tip.TipBuilder;
import cc.sighs.auratip.api.tip.TipRegistry;
import cc.sighs.auratip.api.tip.TipServer;
import cc.sighs.auratip.data.TipData;
import cc.sighs.oelib.registry.extra.CommandRegister;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class DevTestCommand {
    private DevTestCommand() {
    }

    public static void register() {
        CommandRegister.registerServer(DevTestCommand::registerCommand);
    }

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"auratipdev").requires(source -> source.m_6761_(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"tip").then(Commands.m_82127_((String)"trigger").then(Commands.m_82129_((String)"type", (ArgumentType)ResourceLocationArgument.m_106984_()).executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            ResourceLocation type = ResourceLocationArgument.m_107011_((CommandContext)ctx, (String)"type");
            TipServer.trigger(type, player, DevTestCommand.buildVariables(player));
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)("TipServer.trigger: " + String.valueOf(type))), true);
            return 1;
        })))).then(Commands.m_82127_((String)"trigger_id").then(Commands.m_82129_((String)"id", (ArgumentType)ResourceLocationArgument.m_106984_()).executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            ResourceLocation id = ResourceLocationArgument.m_107011_((CommandContext)ctx, (String)"id");
            TipServer.triggerById(id, player, DevTestCommand.buildVariables(player));
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)("TipServer.triggerById: " + String.valueOf(id))), true);
            return 1;
        })))).then(Commands.m_82127_((String)"show").executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            ResourceLocation id = new ResourceLocation("auratip", "dev_show_direct");
            TipData tip = new TipBuilder(id).triggerRepeatable(new ResourceLocation("auratip", "unused_trigger"), 0).visual(v -> v.animationStyle(new ResourceLocation("auratip", "fade_and_slide")).hoverAnimationStyle(new ResourceLocation("auratip", "none")).size(220, 62).positionPreset("BOTTOM_RIGHT")).behavior(b -> b.duration(160)).page(0, p -> p.title((Component)Component.m_237113_((String)"TipServer.show \u6f14\u793a"), 0.8f, 0).content((Component)Component.m_237113_((String)"\u8fd9\u662f\u76f4\u63a5 show \u7684 TipData\uff0c\u4e0d\u8d70 trigger \u5339\u914d/\u51b7\u5374\u3002\n\u73a9\u5bb6: ${player}"), 0.65f, 1)).build();
            TipServer.show(player, tip, DevTestCommand.buildVariables(player));
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)("TipServer.show: " + String.valueOf(tip.id()))), true);
            return 1;
        }))).then(Commands.m_82127_((String)"runtime_count").executes(ctx -> {
            int count = TipRegistry.getTips().size();
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)("TipRegistry runtime tips: " + count)), false);
            return 1;
        }))));
    }

    private static Map<String, Object> buildVariables(ServerPlayer player) {
        return Map.of("player", player.m_5446_(), "x", player.m_146903_(), "y", player.m_146904_(), "z", player.m_146907_());
    }
}

