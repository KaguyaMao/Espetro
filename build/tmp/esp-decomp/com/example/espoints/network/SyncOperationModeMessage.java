/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package com.example.espoints.network;

import com.example.espoints.client.ClientBattleState;
import com.example.espoints.network.NetworkHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class SyncOperationModeMessage {
    private final boolean operationModeRunning;
    private final int currentBatch;
    private final int totalBatches;
    private final String endBehavior;
    private final Map<String, String> teamRoles;
    private final Map<String, Integer> teamReinforcements;
    private final Map<String, Integer> teamInitialReinforcements;

    public SyncOperationModeMessage(boolean operationModeRunning, int currentBatch, int totalBatches, String endBehavior, Map<String, String> teamRoles, Map<String, Integer> teamReinforcements, Map<String, Integer> teamInitialReinforcements) {
        this.operationModeRunning = operationModeRunning;
        this.currentBatch = currentBatch;
        this.totalBatches = totalBatches;
        this.endBehavior = endBehavior;
        this.teamRoles = new HashMap<String, String>(teamRoles);
        this.teamReinforcements = new HashMap<String, Integer>(teamReinforcements);
        this.teamInitialReinforcements = new HashMap<String, Integer>(teamInitialReinforcements);
    }

    public static void encode(SyncOperationModeMessage msg, FriendlyByteBuf buf) {
        SyncOperationModeMessage.validateHeader(msg.currentBatch, msg.totalBatches, msg.endBehavior);
        SyncOperationModeMessage.checkedSize(msg.teamRoles.size(), "team roles");
        SyncOperationModeMessage.checkedSize(msg.teamReinforcements.size(), "reinforcements");
        SyncOperationModeMessage.checkedSize(msg.teamInitialReinforcements.size(), "initial reinforcements");
        buf.writeBoolean(msg.operationModeRunning);
        buf.writeInt(msg.currentBatch);
        buf.writeInt(msg.totalBatches);
        buf.m_130072_(msg.endBehavior, 16);
        buf.writeInt(msg.teamRoles.size());
        for (Map.Entry<String, String> entry : msg.teamRoles.entrySet()) {
            buf.m_130072_(entry.getKey(), 32);
            buf.m_130072_(entry.getValue(), 16);
        }
        buf.writeInt(msg.teamReinforcements.size());
        for (Map.Entry<String, Object> entry : msg.teamReinforcements.entrySet()) {
            buf.m_130072_(entry.getKey(), 32);
            SyncOperationModeMessage.checkedReinforcements((Integer)entry.getValue());
            buf.writeInt(((Integer)entry.getValue()).intValue());
        }
        buf.writeInt(msg.teamInitialReinforcements.size());
        for (Map.Entry<String, Object> entry : msg.teamInitialReinforcements.entrySet()) {
            buf.m_130072_(entry.getKey(), 32);
            SyncOperationModeMessage.checkedReinforcements((Integer)entry.getValue());
            buf.writeInt(((Integer)entry.getValue()).intValue());
        }
    }

    public static SyncOperationModeMessage decode(FriendlyByteBuf buf) {
        boolean operationModeRunning = buf.readBoolean();
        int currentBatch = buf.readInt();
        int totalBatches = buf.readInt();
        String endBehavior = buf.m_130136_(16);
        SyncOperationModeMessage.validateHeader(currentBatch, totalBatches, endBehavior);
        int teamRolesSize = SyncOperationModeMessage.checkedSize(buf.readInt(), "team roles");
        HashMap<String, String> teamRoles = new HashMap<String, String>();
        for (int i = 0; i < teamRolesSize; ++i) {
            String team = buf.m_130136_(32);
            String role = buf.m_130136_(16);
            teamRoles.put(team, role);
        }
        int teamReinforcementsSize = SyncOperationModeMessage.checkedSize(buf.readInt(), "reinforcements");
        HashMap<String, Integer> teamReinforcements = new HashMap<String, Integer>();
        for (int i = 0; i < teamReinforcementsSize; ++i) {
            String team = buf.m_130136_(32);
            int reinforcements = buf.readInt();
            SyncOperationModeMessage.checkedReinforcements(reinforcements);
            teamReinforcements.put(team, reinforcements);
        }
        int teamInitialReinforcementsSize = SyncOperationModeMessage.checkedSize(buf.readInt(), "initial reinforcements");
        HashMap<String, Integer> teamInitialReinforcements = new HashMap<String, Integer>();
        for (int i = 0; i < teamInitialReinforcementsSize; ++i) {
            String team = buf.m_130136_(32);
            int initialReinforcements = buf.readInt();
            SyncOperationModeMessage.checkedReinforcements(initialReinforcements);
            teamInitialReinforcements.put(team, initialReinforcements);
        }
        return new SyncOperationModeMessage(operationModeRunning, currentBatch, totalBatches, endBehavior, teamRoles, teamReinforcements, teamInitialReinforcements);
    }

    public static void handle(SyncOperationModeMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                ClientBattleState.get().applyOperation(msg.operationModeRunning, msg.currentBatch, msg.totalBatches, msg.endBehavior, msg.teamRoles, msg.teamReinforcements, msg.teamInitialReinforcements);
            }
        });
        context.setPacketHandled(true);
    }

    private static int checkedSize(int size, String field) {
        if (size < 0 || size > 8) {
            throw new IllegalArgumentException("Invalid " + field + " count: " + size);
        }
        return size;
    }

    private static void validateHeader(int currentBatch, int totalBatches, String endBehavior) {
        if (currentBatch < 1 || currentBatch > 64 || totalBatches < 0 || totalBatches > 64 || !"terminate".equalsIgnoreCase(endBehavior) && !"loop".equalsIgnoreCase(endBehavior)) {
            throw new IllegalArgumentException("Invalid operation mode header");
        }
    }

    private static void checkedReinforcements(Integer value) {
        if (value == null || value < 0 || value > 10000000) {
            throw new IllegalArgumentException("Invalid reinforcement value");
        }
    }

    public static void broadcastToAll(boolean operationModeRunning, int currentBatch, int totalBatches, String endBehavior, Map<String, String> teamRoles, Map<String, Integer> teamReinforcements, Map<String, Integer> teamInitialReinforcements) {
        NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), (Object)new SyncOperationModeMessage(operationModeRunning, currentBatch, totalBatches, endBehavior, teamRoles, teamReinforcements, teamInitialReinforcements));
    }

    public static void sendToPlayer(ServerPlayer player, boolean operationModeRunning, int currentBatch, int totalBatches, String endBehavior, Map<String, String> teamRoles, Map<String, Integer> teamReinforcements, Map<String, Integer> teamInitialReinforcements) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncOperationModeMessage(operationModeRunning, currentBatch, totalBatches, endBehavior, teamRoles, teamReinforcements, teamInitialReinforcements));
    }
}

