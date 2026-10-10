/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.kubejs.commander;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.Espetro;
import org.espetro.kubejs.commander.KubeCommanderSkillDefinition;
import org.espetro.team.CommanderSkillManager;

public class KubeCommanderSkillEvent {
    private final KubeCommanderSkillDefinition definition;
    @Nullable
    private final CommanderSkillManager.ArtillerySupportRequest request;
    private final ServerPlayer commander;
    private final String team;
    private final ServerLevel level;
    private final double x;
    private final double y;
    private final double z;
    private final BlockPos blockPos;
    private final boolean hasTarget;

    public KubeCommanderSkillEvent(KubeCommanderSkillDefinition definition, ServerPlayer commander, String team) {
        this.definition = definition;
        this.request = null;
        this.commander = commander;
        this.team = team;
        this.level = commander.m_284548_();
        this.x = commander.m_20185_();
        this.y = commander.m_20186_();
        this.z = commander.m_20189_();
        this.blockPos = commander.m_20183_();
        this.hasTarget = false;
    }

    public KubeCommanderSkillEvent(KubeCommanderSkillDefinition definition, CommanderSkillManager.ArtillerySupportRequest request, ServerPlayer commander, ServerLevel level, BlockPos blockPos) {
        this.definition = definition;
        this.request = request;
        this.commander = commander;
        this.team = request.team();
        this.level = level;
        this.x = request.x();
        this.y = request.y();
        this.z = request.z();
        this.blockPos = blockPos;
        this.hasTarget = true;
    }

    public KubeCommanderSkillDefinition getDefinition() {
        return this.definition;
    }

    public KubeCommanderSkillDefinition definition() {
        return this.definition;
    }

    @Nullable
    public CommanderSkillManager.ArtillerySupportRequest getRequest() {
        return this.request;
    }

    @Nullable
    public CommanderSkillManager.ArtillerySupportRequest request() {
        return this.request;
    }

    public String getSkillId() {
        return this.definition.id();
    }

    public String skillId() {
        return this.getSkillId();
    }

    public ServerPlayer getCommander() {
        return this.commander;
    }

    public ServerPlayer commander() {
        return this.commander;
    }

    public UUID getCommanderId() {
        return this.commander.m_20148_();
    }

    public UUID commanderId() {
        return this.getCommanderId();
    }

    public String getCommanderName() {
        return this.commander.m_7755_().getString();
    }

    public String commanderName() {
        return this.getCommanderName();
    }

    public String getTeam() {
        return this.team;
    }

    public String team() {
        return this.team;
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public ServerLevel level() {
        return this.level;
    }

    public MinecraftServer getServer() {
        return this.level.m_7654_();
    }

    public MinecraftServer server() {
        return this.getServer();
    }

    public String getDimensionId() {
        return this.level.m_46472_().m_135782_().toString();
    }

    public String dimensionId() {
        return this.getDimensionId();
    }

    public boolean hasTarget() {
        return this.hasTarget;
    }

    public double getX() {
        return this.x;
    }

    public double x() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double y() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double z() {
        return this.z;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public BlockPos blockPos() {
        return this.blockPos;
    }

    public int getBlockX() {
        return this.blockPos.m_123341_();
    }

    public int blockX() {
        return this.getBlockX();
    }

    public int getBlockY() {
        return this.blockPos.m_123342_();
    }

    public int blockY() {
        return this.getBlockY();
    }

    public int getBlockZ() {
        return this.blockPos.m_123343_();
    }

    public int blockZ() {
        return this.getBlockZ();
    }

    public Direction getFacing() {
        return this.commander.m_6350_();
    }

    public Direction facing() {
        return this.getFacing();
    }

    public int getFacingStepX() {
        return this.getFacing().m_122429_();
    }

    public int facingStepX() {
        return this.getFacingStepX();
    }

    public int getFacingStepZ() {
        return this.getFacing().m_122431_();
    }

    public int facingStepZ() {
        return this.getFacingStepZ();
    }

    public void tell(String message) {
        Espetro.sendToPlayer(this.commander, message);
    }

    public void broadcastTeam(String message) {
        if (this.team != null) {
            Espetro.broadcastToTeam(this.team, message);
        }
    }

    public void broadcastAll(String message) {
        Espetro.broadcastToAll(message);
    }

    @Nullable
    public ServerPlayer getOnlineCommander() {
        MinecraftServer server = Espetro.getServer();
        return server == null ? null : server.m_6846_().m_11259_(this.commander.m_20148_());
    }
}

