package org.espetro.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.espetro.bastion.BastionItems;
import org.espetro.bastion.FortificationAuthoringManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * {@code /espetro fort ...}：工事可视化编辑器指令（**仅管理员**，permission 2）。
 *
 * <p>与 {@link EspetroCommand} 共用 {@code espetro} 根字面量，Brigadier 会自动合并子树。</p>
 */
public final class FortCommand {

    private static final Set<String> ROLES =
        Set.of("commander", "squad_leader", "fireteam_leader");

    private FortCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("espetro")
            .requires(source -> source.hasPermission(FortificationAuthoringManager.PERMISSION_LEVEL))
            .then(Commands.literal("fort")
                .executes(ctx -> {
                    reply(ctx, manager().usage());
                    return 1;
                })
                .then(Commands.literal("help").executes(ctx -> {
                    reply(ctx, manager().usage());
                    return 1;
                }))
                .then(Commands.literal("wand").executes(FortCommand::giveWand))
                .then(Commands.literal("info").executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayer();
                    reply(ctx, manager().describe(player));
                    return 1;
                }))
                .then(Commands.literal("clear").executes(ctx -> {
                    manager().clear(player(ctx));
                    manager().syncTo(player(ctx));
                    reply(ctx, "§e选区已清空。");
                    return 1;
                }))
                .then(Commands.literal("list").executes(ctx -> {
                    reply(ctx, manager().list());
                    return 1;
                }))
                .then(Commands.literal("reload").executes(ctx -> {
                    // 允许控制台执行（无需玩家），方便服务器侧运维
                    reply(ctx, manager().reload(ctx.getSource().getServer()));
                    return 1;
                }))
                .then(Commands.literal("delete")
                    .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            reply(ctx, manager().delete(
                                StringArgumentType.getString(ctx, "name")));
                            return 1;
                        })))
                .then(corner("pos1", true))
                .then(corner("pos2", false))
                .then(anchor())
                .then(Commands.literal("save")
                    .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> save(ctx, ""))
                        .then(Commands.argument("options", StringArgumentType.greedyString())
                            .executes(ctx -> save(ctx,
                                StringArgumentType.getString(ctx, "options"))))))
            ));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> corner(
        String name, boolean first) {
        return Commands.literal(name)
            .executes(ctx -> setCorner(ctx, first, null, null, null))
            .then(Commands.argument("x", IntegerArgumentType.integer())
                .then(Commands.argument("y", IntegerArgumentType.integer())
                    .then(Commands.argument("z", IntegerArgumentType.integer())
                        .executes(ctx -> setCorner(ctx, first,
                            IntegerArgumentType.getInteger(ctx, "x"),
                            IntegerArgumentType.getInteger(ctx, "y"),
                            IntegerArgumentType.getInteger(ctx, "z"))))));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> anchor() {
        return Commands.literal("anchor")
            .executes(ctx -> setAnchor(ctx, null, null, null))
            .then(Commands.argument("x", IntegerArgumentType.integer())
                .then(Commands.argument("y", IntegerArgumentType.integer())
                    .then(Commands.argument("z", IntegerArgumentType.integer())
                        .executes(ctx -> setAnchor(ctx,
                            IntegerArgumentType.getInteger(ctx, "x"),
                            IntegerArgumentType.getInteger(ctx, "y"),
                            IntegerArgumentType.getInteger(ctx, "z"))))));
    }

    private static int setCorner(CommandContext<CommandSourceStack> ctx, boolean first,
                                 Integer x, Integer y, Integer z) {
        ServerPlayer player = player(ctx);
        BlockPos pos = FortificationAuthoringManager.resolveTarget(player, x, y, z);
        if (pos == null) {
            reply(ctx, "§c没有瞄准任何方块（也可以直接给 x y z）。");
            return 0;
        }
        reply(ctx, manager().setCorner(player, first, pos));
        manager().syncTo(player);
        return 1;
    }

    private static int setAnchor(CommandContext<CommandSourceStack> ctx,
                                 Integer x, Integer y, Integer z) {
        ServerPlayer player = player(ctx);
        BlockPos pos = FortificationAuthoringManager.resolveTarget(player, x, y, z);
        if (pos == null) {
            reply(ctx, "§c没有瞄准任何方块（也可以直接给 x y z）。");
            return 0;
        }
        reply(ctx, manager().setAnchor(player, pos));
        manager().syncTo(player);
        return 1;
    }

    private static int giveWand(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = player(ctx);
        ItemStack stack = new ItemStack(BastionItems.FORTIFICATION_WAND);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        player.getInventory().setChanged();
        reply(ctx, "§a已发放工事选定棒：§7左键=角A，右键=角B，潜行+右键=锚点，潜行+左键=清空。");
        return 1;
    }

    private static int save(CommandContext<CommandSourceStack> ctx, String rawOptions) {
        ServerPlayer player = player(ctx);
        String name = StringArgumentType.getString(ctx, "name");
        FortificationAuthoringManager.SaveOptions options;
        try {
            options = parseOptions(name, rawOptions);
        } catch (IllegalArgumentException e) {
            reply(ctx, "§c参数错误：" + e.getMessage());
            return 0;
        }
        reply(ctx, manager().save(player, name, options));
        return 1;
    }

    /** 解析 {@code save <name> --flag value ...} 形式的参数串。 */
    static FortificationAuthoringManager.SaveOptions parseOptions(String name, String raw) {
        FortificationAuthoringManager.SaveOptions defaults =
            FortificationAuthoringManager.SaveOptions.defaults(name);
        if (raw == null || raw.isBlank()) return defaults;
        String displayName = defaults.displayName();
        String icon = defaults.icon();
        int cost = defaults.cost();
        int progress = defaults.progress();
        boolean radioRange = defaults.radioRange();
        List<String> roles = defaults.roles();
        boolean includeEntities = defaults.includeEntities();
        boolean carve = defaults.carve();
        boolean writeDefinition = defaults.writeDefinition();
        boolean force = defaults.force();
        List<Integer> damageable = new ArrayList<>(defaults.damageableEntities());
        boolean instant = defaults.instant();

        String[] tokens = raw.trim().split("\\s+");
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            switch (token) {
                case "--name" -> displayName = value(tokens, ++i, token);
                case "--icon" -> icon = value(tokens, ++i, token);
                case "--cost" -> cost = intValue(value(tokens, ++i, token), token);
                case "--progress" -> progress = intValue(value(tokens, ++i, token), token);
                case "--radio-range" -> radioRange = boolValue(value(tokens, ++i, token), token);
                case "--roles" -> {
                    List<String> parsed = new ArrayList<>();
                    for (String role : value(tokens, ++i, token).split(",")) {
                        String normalized = role.trim().toLowerCase(Locale.ROOT);
                        if (!ROLES.contains(normalized)) {
                            throw new IllegalArgumentException("未知角色 " + role
                                + "（可用：commander,squad_leader,fireteam_leader）");
                        }
                        if (!parsed.contains(normalized)) parsed.add(normalized);
                    }
                    if (parsed.isEmpty()) throw new IllegalArgumentException("--roles 不能为空");
                    roles = List.copyOf(parsed);
                }
                case "--damageable-entities" -> {
                    damageable.clear();
                    for (String index : value(tokens, ++i, token).split(",")) {
                        damageable.add(intValue(index.trim(), token));
                    }
                }
                case "--no-entities" -> includeEntities = false;
                case "--carve" -> carve = true;
                case "--instant" -> instant = true;
                case "--no-define" -> writeDefinition = false;
                case "--force" -> force = true;
                default -> throw new IllegalArgumentException("未知参数 " + token);
            }
        }
        if (cost < 0 || cost > 1_000_000) throw new IllegalArgumentException("--cost 越界");
        if (progress < 1 || progress > 1_000_000) throw new IllegalArgumentException("--progress 越界");
        return new FortificationAuthoringManager.SaveOptions(displayName, icon, cost, progress,
            radioRange, roles, includeEntities, carve, List.copyOf(damageable), writeDefinition,
            force, instant);
    }

    private static String value(String[] tokens, int index, String flag) {
        if (index >= tokens.length) throw new IllegalArgumentException(flag + " 缺少取值");
        return tokens[index];
    }

    private static int intValue(String raw, String flag) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(flag + " 需要整数，收到 " + raw);
        }
    }

    private static boolean boolValue(String raw, String flag) {
        if ("true".equalsIgnoreCase(raw)) return true;
        if ("false".equalsIgnoreCase(raw)) return false;
        throw new IllegalArgumentException(flag + " 需要 true/false，收到 " + raw);
    }

    private static FortificationAuthoringManager manager() {
        return FortificationAuthoringManager.getInstance();
    }

    private static ServerPlayer player(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) throw new IllegalStateException("该指令只能由玩家执行");
        return player;
    }

    private static void reply(CommandContext<CommandSourceStack> ctx, String message) {
        ctx.getSource().sendSystemMessage(Component.literal(message));
    }
}
