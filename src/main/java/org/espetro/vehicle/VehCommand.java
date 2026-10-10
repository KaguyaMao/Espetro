package org.espetro.vehicle;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.espetro.Espetro;
import org.espetro.team.SquadManager;

import java.util.UUID;

/**
 * 载具认领命令（小队长用，无需 OP）
 * <pre>
 * /veh pass  [载具UUID]  — 通过认领申请（不带 UUID 时取本小队最早的一条）
 * /veh passno[载具UUID]  — 否决认领申请（不带 UUID 时取本小队最早的一条）
 * </pre>
 * 聊天栏里队长收到的 [通过]/[否决] 按钮会自动带上载具 UUID，
 * 因此按载具排队的多条申请可以分别处理。
 */
public class VehCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("veh")
                .requires(source -> source.hasPermission(0))
                .then(Commands.literal("pass")
                    .executes(ctx -> handlePass(ctx.getSource(), null))
                    .then(Commands.argument("vehicle", StringArgumentType.word())
                        .executes(ctx -> handlePass(ctx.getSource(),
                            StringArgumentType.getString(ctx, "vehicle"))))
                )
                .then(Commands.literal("passno")
                    .executes(ctx -> handlePassNo(ctx.getSource(), null))
                    .then(Commands.argument("vehicle", StringArgumentType.word())
                        .executes(ctx -> handlePassNo(ctx.getSource(),
                            StringArgumentType.getString(ctx, "vehicle"))))
                )
        );
    }

    private static int handlePass(CommandSourceStack source, String vehicleArg) {
        if (!(source.getEntity() instanceof ServerPlayer leader)) {
            source.sendFailure(Component.literal("只有玩家可以使用此命令。"));
            return 0;
        }

        SquadManager sm = SquadManager.getInstance();
        String team = Espetro.getPlayerTeam(leader);
        if (team == null) {
            source.sendFailure(Component.literal("你不在任何阵营中。"));
            return 0;
        }
        team = team.toUpperCase();

        if (!sm.isSquadLeader(leader.getUUID())) {
            source.sendFailure(Component.literal("只有小队长可以使用此命令。"));
            return 0;
        }

        int squadId = sm.getPlayerSquadId(leader.getUUID());
        if (squadId == SquadManager.NO_SQUAD) {
            source.sendFailure(Component.literal("你不在任何小队中。"));
            return 0;
        }

        UUID vehicleId = parseVehicleId(source, vehicleArg);
        if (vehicleId == null && vehicleArg != null) return 0;

        VehicleEventHandler.PendingClaim claim =
            VehicleEventHandler.pollClaim(vehicleId, squadId);
        if (claim == null) {
            source.sendFailure(Component.literal(vehicleArg != null
                ? "该载具没有待处理的认领申请（可能已过期）。"
                : "没有待处理的认领申请。"));
            return 0;
        }

        // 查找载具实体
        Entity vehicle = null;
        for (var level : leader.getServer().getAllLevels()) {
            Entity e = level.getEntity(claim.vehicleUuid());
            if (e != null) {
                vehicle = e;
                break;
            }
        }
        if (vehicle == null) {
            leader.sendSystemMessage(Component.literal("§c申请认领的载具已不存在。"));
            return 0;
        }

        // 归属阵营用队长自己的阵营（申请本身已限定在本小队内）
        VehicleSquadOwnership.setOwner(vehicle, squadId, team);

        // 通过 → 告知双方
        leader.sendSystemMessage(Component.literal("§a通过申请"));
        ServerPlayer member = leader.serverLevel().getServer().getPlayerList()
            .getPlayer(claim.memberUuid());
        if (member != null) {
            member.sendSystemMessage(Component.literal("§a通过申请"));
        }
        return 1;
    }

    private static int handlePassNo(CommandSourceStack source, String vehicleArg) {
        if (!(source.getEntity() instanceof ServerPlayer leader)) {
            source.sendFailure(Component.literal("只有玩家可以使用此命令。"));
            return 0;
        }

        SquadManager sm = SquadManager.getInstance();

        if (!sm.isSquadLeader(leader.getUUID())) {
            source.sendFailure(Component.literal("只有小队长可以使用此命令。"));
            return 0;
        }

        int squadId = sm.getPlayerSquadId(leader.getUUID());
        if (squadId == SquadManager.NO_SQUAD) {
            source.sendFailure(Component.literal("你不在任何小队中。"));
            return 0;
        }

        UUID vehicleId = parseVehicleId(source, vehicleArg);
        if (vehicleId == null && vehicleArg != null) return 0;

        VehicleEventHandler.PendingClaim claim =
            VehicleEventHandler.pollClaim(vehicleId, squadId);
        if (claim == null) {
            source.sendFailure(Component.literal(vehicleArg != null
                ? "该载具没有待处理的认领申请（可能已过期）。"
                : "没有待处理的认领申请。"));
            return 0;
        }

        leader.sendSystemMessage(Component.literal("§c已否决认领申请"));
        // 否决也告知申请人，避免队员以为申请还挂着
        ServerPlayer member = leader.serverLevel().getServer().getPlayerList()
            .getPlayer(claim.memberUuid());
        if (member != null) {
            member.sendSystemMessage(Component.literal("§c队长否决了你对该载具的认领申请"));
        }
        return 1;
    }

    /** 解析可选参数里的载具 UUID；参数非法时报错并返回 null。 */
    private static UUID parseVehicleId(CommandSourceStack source, String vehicleArg) {
        if (vehicleArg == null || vehicleArg.isBlank()) return null;
        try {
            return UUID.fromString(vehicleArg.trim());
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.literal("载具标识无效：" + vehicleArg));
            return null;
        }
    }
}
