package org.espetro.naval;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.espetro.bastion.BastionItems;

import java.util.List;

/**
 * {@code /espetro naval ...}：舰船结构编排（框选 → 标记 → 逐卷导出）。
 *
 * <pre>
 * /espetro naval                       自检（读 amphibious.json 并试加载舰船）
 * /espetro naval wand                  发选定棒
 * /espetro naval pos1|pos2 [x y z]     设角点（不带坐标 = 准星方块）
 * /espetro naval anchor [x y z]        设锚点（贴图时整船最小角对齐这里）
 * /espetro naval marker deck [defend]  在脚下放出生点标记
 * /espetro naval marker veh &lt;type&gt; &lt;i&gt; [defend]
 * /espetro naval marker clear          清掉选区内标记块
 * /espetro naval save &lt;name&gt; [carve] [noentities]
 * /espetro naval list | info &lt;name&gt; | delete &lt;name&gt;
 * /espetro naval clear                 清空选区
 * </pre>
 */
public final class NavalCommand {

    private NavalCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("naval")
            .executes(ctx -> {
                var server = ctx.getSource().getServer();
                for (String line : NavalDiagnostics.describe(server)) {
                    ctx.getSource().sendSystemMessage(Component.literal(line));
                }
                return 1;
            })
            .then(Commands.literal("wand").executes(ctx -> {
                ServerPlayer player = player(ctx);
                if (player == null) {
                    return 0;
                }
                if (!NavalAuthoringManager.hasPermission(player)) {
                    message(ctx, "§c需要管理员权限（等级 2）");
                    return 0;
                }
                ItemStack stack = new ItemStack(BastionItems.FORTIFICATION_WAND);
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                NavalAuthoringManager.getInstance().clear(player);
                NavalAuthoringManager.getInstance().enterMode(player);
                message(ctx, "§a已发放选定棒：左键=A，右键=B，潜行+右键=锚点，潜行+左键=清空");
                message(ctx, "§7（此时点击归舰船编排；/espetro naval cancel 退出该模式）");
                message(ctx, NavalAuthoringManager.getInstance().describe(player));
                return 1;
            }))
            .then(Commands.literal("cancel").executes(ctx -> {
                ServerPlayer player = player(ctx);
                if (player == null) {
                    return 0;
                }
                NavalAuthoringManager.getInstance().clear(player);
                NavalAuthoringManager.getInstance().exitMode(player);
                message(ctx, "§e已退出舰船编排模式（棒子点击重新归工事编辑器）");
                return 1;
            }))
            .then(Commands.literal("pos1")
                .executes(ctx -> setCorner(ctx, true, null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .executes(ctx -> setCorner(ctx, true, BlockPosArgument.getBlockPos(ctx, "pos")))))
            .then(Commands.literal("pos2")
                .executes(ctx -> setCorner(ctx, false, null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .executes(ctx -> setCorner(ctx, false, BlockPosArgument.getBlockPos(ctx, "pos")))))
            .then(Commands.literal("anchor")
                .executes(ctx -> setAnchor(ctx, null))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .executes(ctx -> setAnchor(ctx, BlockPosArgument.getBlockPos(ctx, "pos")))))
            .then(Commands.literal("clear").executes(ctx -> {
                ServerPlayer player = player(ctx);
                if (player == null) {
                    return 0;
                }
                NavalAuthoringManager.getInstance().clear(player);
                message(ctx, "§e选区已清空");
                return 1;
            }))
            .then(Commands.literal("marker")
                .then(Commands.literal("deck")
                    .executes(ctx -> marker(ctx, "attack_spawn")))
                .then(Commands.literal("veh")
                    .then(Commands.argument("type", StringArgumentType.word())
                        .then(Commands.argument("index", IntegerArgumentType.integer(0))
                            .executes(ctx -> vehMarker(ctx, false))
                            .then(Commands.literal("defend").executes(ctx -> vehMarker(ctx, true))))))
                .then(Commands.literal("clear").executes(ctx -> {
                    ServerPlayer player = player(ctx);
                    if (player == null) {
                        return 0;
                    }
                    message(ctx, NavalAuthoringManager.getInstance().clearMarkers(player.serverLevel(), player));
                    return 1;
                })))
            .then(Commands.literal("save")
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(ctx -> save(ctx, false, false))
                    .then(Commands.literal("carve").executes(ctx -> save(ctx, true, false)))
                    .then(Commands.literal("noentities").executes(ctx -> save(ctx, false, true)))))
            .then(Commands.literal("list").executes(ctx -> {
                for (String line : NavalAuthoringManager.getInstance().list()) {
                    message(ctx, line);
                }
                return 1;
            }))
            .then(Commands.literal("info")
                .then(Commands.argument("name", StringArgumentType.word()).executes(ctx -> {
                    String name = StringArgumentType.getString(ctx, "name");
                    for (String line : NavalAuthoringManager.getInstance().info(name)) {
                        message(ctx, line);
                    }
                    return 1;
                })))
            .then(Commands.literal("delete")
                .then(Commands.argument("name", StringArgumentType.word()).executes(ctx -> {
                    String name = StringArgumentType.getString(ctx, "name");
                    NavalAuthoringManager.Outcome outcome = NavalAuthoringManager.getInstance().delete(name);
                    for (String line : outcome.lines()) {
                        message(ctx, line);
                    }
                    return outcome.ok() ? 1 : 0;
                })));
    }

    private static int setCorner(CommandContext<CommandSourceStack> ctx, boolean first,
                                 BlockPos explicit) {
        ServerPlayer player = player(ctx);
        if (player == null) {
            return 0;
        }
        if (!checkPermission(ctx, player)) {
            return 0;
        }
        BlockPos pos = explicit == null ? lookingAt(player) : explicit;
        if (pos == null) {
            message(ctx, "§c准星没有指向方块，请直接给坐标");
            return 0;
        }
        message(ctx, NavalAuthoringManager.getInstance().setCorner(player, first, pos));
        return 1;
    }

    private static int setAnchor(CommandContext<CommandSourceStack> ctx, BlockPos explicit) {
        ServerPlayer player = player(ctx);
        if (player == null) {
            return 0;
        }
        if (!checkPermission(ctx, player)) {
            return 0;
        }
        BlockPos pos = explicit == null ? lookingAt(player) : explicit;
        if (pos == null) {
            message(ctx, "§c准星没有指向方块，请直接给坐标");
            return 0;
        }
        message(ctx, NavalAuthoringManager.getInstance().setAnchor(player, pos));
        return 1;
    }

    private static int marker(CommandContext<CommandSourceStack> ctx, String kind) {
        ServerPlayer player = player(ctx);
        if (player == null) {
            return 0;
        }
        if (!checkPermission(ctx, player)) {
            return 0;
        }
        message(ctx, NavalAuthoringManager.getInstance().placeMarker(player.serverLevel(), player, kind));
        return 1;
    }

    private static int vehMarker(CommandContext<CommandSourceStack> ctx, boolean defend) {
        ServerPlayer player = player(ctx);
        if (player == null) {
            return 0;
        }
        if (!checkPermission(ctx, player)) {
            return 0;
        }
        String type = StringArgumentType.getString(ctx, "type").toLowerCase(java.util.Locale.ROOT);
        int index = IntegerArgumentType.getInteger(ctx, "index");
        String kind = "veh:" + type + ":" + index + (defend ? ":defend" : "");
        message(ctx, NavalAuthoringManager.getInstance().placeMarker(player.serverLevel(), player, kind));
        return 1;
    }

    private static int save(CommandContext<CommandSourceStack> ctx, boolean carve,
                            boolean noEntities) {
        ServerPlayer player = player(ctx);
        if (player == null) {
            return 0;
        }
        if (!checkPermission(ctx, player)) {
            return 0;
        }
        String name = StringArgumentType.getString(ctx, "name");
        ServerLevel level = player.serverLevel();
        NavalAuthoringManager.Outcome outcome = NavalAuthoringManager.getInstance()
            .save(ctx.getSource().getServer(), level, player, name, !noEntities, carve, false);
        for (String line : outcome.lines()) {
            message(ctx, line);
        }
        if (outcome.ok()) {
            NavalAuthoringManager.getInstance().clear(player);
            NavalAuthoringManager.getInstance().exitMode(player);
        }
        return outcome.ok() ? 1 : 0;
    }

    private static boolean checkPermission(CommandContext<CommandSourceStack> ctx,
                                           ServerPlayer player) {
        if (NavalAuthoringManager.hasPermission(player)) {
            return true;
        }
        message(ctx, "§c需要管理员权限（等级 2）");
        return false;
    }

    private static ServerPlayer player(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) {
            ctx.getSource().sendSystemMessage(Component.literal("§c该命令必须由玩家执行"));
        }
        return player;
    }

    private static void message(CommandContext<CommandSourceStack> ctx, String text) {
        ctx.getSource().sendSystemMessage(Component.literal(text));
    }

    private static BlockPos lookingAt(ServerPlayer player) {
        HitResult hit = player.pick(64.0D, 0.0F, false);
        if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
            return blockHit.getBlockPos();
        }
        return player.blockPosition();
    }

    public static void send(CommandSourceStack source, List<String> lines) {
        for (String line : lines) {
            source.sendSystemMessage(Component.literal(line));
        }
    }
}
