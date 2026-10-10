/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 */
package org.espetro.vehicle;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.espetro.Espetro;
import org.espetro.team.SquadManager;
import org.espetro.vehicle.VehicleEventHandler;
import org.espetro.vehicle.VehicleSquadOwnership;

public class VehCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("veh").requires(source -> source.m_6761_(0))).then(Commands.m_82127_("pass").executes(ctx -> VehCommand.handlePass((CommandSourceStack)ctx.getSource())))).then(Commands.m_82127_("passno").executes(ctx -> VehCommand.handlePassNo((CommandSourceStack)ctx.getSource()))));
    }

    private static int handlePass(CommandSourceStack source) {
        Entity entity = source.m_81373_();
        if (!(entity instanceof ServerPlayer)) {
            source.m_81352_(Component.m_237113_("\u53ea\u6709\u73a9\u5bb6\u53ef\u4ee5\u4f7f\u7528\u6b64\u547d\u4ee4\u3002"));
            return 0;
        }
        ServerPlayer leader = (ServerPlayer)entity;
        SquadManager sm = SquadManager.getInstance();
        String team = Espetro.getPlayerTeam(leader);
        if (team == null) {
            source.m_81352_(Component.m_237113_("\u4f60\u4e0d\u5728\u4efb\u4f55\u9635\u8425\u4e2d\u3002"));
            return 0;
        }
        team = team.toUpperCase();
        if (!sm.isSquadLeader(leader.m_20148_())) {
            source.m_81352_(Component.m_237113_("\u53ea\u6709\u5c0f\u961f\u957f\u53ef\u4ee5\u4f7f\u7528\u6b64\u547d\u4ee4\u3002"));
            return 0;
        }
        int squadId = sm.getPlayerSquadId(leader.m_20148_());
        if (squadId == -1) {
            source.m_81352_(Component.m_237113_("\u4f60\u4e0d\u5728\u4efb\u4f55\u5c0f\u961f\u4e2d\u3002"));
            return 0;
        }
        VehicleEventHandler.PendingClaim claim = VehicleEventHandler.PENDING_CLAIMS.remove(squadId);
        if (claim == null) {
            source.m_81352_(Component.m_237113_("\u6ca1\u6709\u5f85\u5904\u7406\u7684\u8ba4\u9886\u7533\u8bf7\u3002"));
            return 0;
        }
        if (System.currentTimeMillis() > claim.expiryMs()) {
            leader.m_213846_(Component.m_237113_("\u00a7c\u8be5\u8ba4\u9886\u7533\u8bf7\u5df2\u8fc7\u671f\u3002"));
            return 0;
        }
        Entity vehicle = null;
        for (ServerLevel level : leader.m_20194_().m_129785_()) {
            Entity e = level.m_8791_(claim.vehicleUuid());
            if (e == null) continue;
            vehicle = e;
            break;
        }
        if (vehicle == null) {
            leader.m_213846_(Component.m_237113_("\u00a7c\u7533\u8bf7\u8ba4\u9886\u7684\u8f7d\u5177\u5df2\u4e0d\u5b58\u5728\u3002"));
            return 0;
        }
        VehicleSquadOwnership.setOwner(vehicle, squadId, team);
        leader.m_213846_(Component.m_237113_("\u00a7a\u901a\u8fc7\u7533\u8bf7"));
        ServerPlayer member = leader.m_284548_().m_7654_().m_6846_().m_11259_(claim.memberUuid());
        if (member != null) {
            member.m_213846_(Component.m_237113_("\u00a7a\u901a\u8fc7\u7533\u8bf7"));
        }
        return 1;
    }

    private static int handlePassNo(CommandSourceStack source) {
        Entity entity = source.m_81373_();
        if (!(entity instanceof ServerPlayer)) {
            source.m_81352_(Component.m_237113_("\u53ea\u6709\u73a9\u5bb6\u53ef\u4ee5\u4f7f\u7528\u6b64\u547d\u4ee4\u3002"));
            return 0;
        }
        ServerPlayer leader = (ServerPlayer)entity;
        SquadManager sm = SquadManager.getInstance();
        if (!sm.isSquadLeader(leader.m_20148_())) {
            source.m_81352_(Component.m_237113_("\u53ea\u6709\u5c0f\u961f\u957f\u53ef\u4ee5\u4f7f\u7528\u6b64\u547d\u4ee4\u3002"));
            return 0;
        }
        int squadId = sm.getPlayerSquadId(leader.m_20148_());
        if (squadId == -1) {
            source.m_81352_(Component.m_237113_("\u4f60\u4e0d\u5728\u4efb\u4f55\u5c0f\u961f\u4e2d\u3002"));
            return 0;
        }
        VehicleEventHandler.PendingClaim claim = VehicleEventHandler.PENDING_CLAIMS.remove(squadId);
        if (claim == null) {
            source.m_81352_(Component.m_237113_("\u6ca1\u6709\u5f85\u5904\u7406\u7684\u8ba4\u9886\u7533\u8bf7\u3002"));
            return 0;
        }
        leader.m_213846_(Component.m_237113_("\u00a7c\u5df2\u5426\u51b3\u8ba4\u9886\u7533\u8bf7"));
        return 1;
    }
}

