/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionManager;
import org.espetro.config.GameConfig;
import org.espetro.network.ClassCountSyncPacket;
import org.espetro.network.NetworkManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.TeamPackManager;

public class BastionSelectionPacket {
    private final List<BastionInfo> bastions;

    public BastionSelectionPacket(List<BastionInfo> bastions) {
        this.bastions = bastions;
    }

    public BastionSelectionPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        this.bastions = new ArrayList<BastionInfo>();
        for (int i = 0; i < size; ++i) {
            UUID id = buf.m_130259_();
            String name = buf.m_130277_();
            String team = buf.m_130277_();
            this.bastions.add(new BastionInfo(id, name, team));
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.bastions.size());
        for (BastionInfo info : this.bastions) {
            buf.m_130077_(info.id);
            buf.m_130070_(info.name);
            buf.m_130070_(info.team);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {});
        ctx.get().setPacketHandled(true);
    }

    public static void sendBastionSelectionMessage(ServerPlayer player) {
        NetworkManager.sendDeployPointSelectScreen(player);
    }

    public static boolean handleBastionSelect(ServerPlayer player, UUID bastionId) {
        String factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_());
        if (factionId == null) {
            return false;
        }
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return false;
        }
        String classId = ClassCountManager.getInstance().getPlayerClass(player.m_20148_());
        if (classId == null || classId.isEmpty()) {
            player.m_213846_(Component.m_237113_("\u00a7c\u8bf7\u5148\u9009\u62e9\u804c\u4e1a\u540e\u518d\u9009\u62e9\u90e8\u7f72\u70b9\uff01"));
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new ClassCountSyncPacket("\u00a7c\u8bf7\u5148\u9009\u62e9\u804c\u4e1a\u540e\u518d\u9009\u62e9\u90e8\u7f72\u70b9\uff01", true));
            return false;
        }
        BastionData bastion = BastionManager.getInstance().getBastion(bastionId);
        if (bastion == null || !bastion.isActive() || !team.equals(bastion.getTeam())) {
            return TeamPackManager.getInstance().respawnAtTeamPack(player, bastionId);
        }
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.BATTLE && phase != GamePhase.DEPLOYING) {
            player.m_213846_(Component.m_237113_("\u00a7c\u53ea\u80fd\u5728\u6218\u6597\u6216\u90e8\u7f72\u9636\u6bb5\u590d\u6d3b\uff01"));
            return false;
        }
        if (!BastionManager.getInstance().isWaitingForBastion(player.m_20148_())) {
            player.m_213846_(Component.m_237113_("\u00a7c\u4f60\u5df2\u7ecf\u5b8c\u6210\u4e86\u590d\u6d3b\u9009\u62e9\uff01"));
            return false;
        }
        if (!BastionManager.getInstance().isHabOperational(bastion, true)) {
            String status = BastionManager.getInstance().getFobStatus(bastion);
            player.m_213846_(Component.m_237113_("\u00a7c\u65e0\u6cd5\u5728\u8be5\u5175\u7ad9\u90e8\u7f72\uff1a\u00a7e" + status + "\u00a7c\uff08\u9700\u5df1\u65b9 Radio \u8986\u76d6\u4e14\u672a\u88ab\u654c\u65b9\u538b\u5236\uff09"));
            return false;
        }
        TeamPackManager.getInstance().cancelPendingRespawn(player.m_20148_());
        BastionManager manager = BastionManager.getInstance();
        boolean queued = manager.teleportPlayerToHabAsync(player, bastion, success -> {
            if (!success.booleanValue()) {
                player.m_213846_(Component.m_237113_("\u00a7c\u5175\u7ad9\u533a\u5757\u52a0\u8f7d\u5931\u8d25\u3001\u8d85\u65f6\u6216\u5df2\u5931\u6548\uff0c\u8bf7\u91cd\u65b0\u9009\u62e9\u90e8\u7f72\u70b9\u3002"));
                NetworkManager.sendUnifiedDeployScreen(player, -1);
                return;
            }
            BastionSelectionPacket.completeHabDeployment(player, bastion, manager);
        });
        if (!queued) {
            if (manager.isHabTeleportPending(player.m_20148_())) {
                player.m_213846_(Component.m_237113_("\u00a7e\u6b63\u5728\u51c6\u5907\u8be5\u5175\u7ad9\uff0c\u8bf7\u7a0d\u5019\u3002"));
                return true;
            }
            player.m_213846_(Component.m_237113_("\u00a7c\u8be5\u5175\u7ad9\u7f3a\u5c11\u8bb0\u5f55\u5750\u6807\u6216\u5df2\u5931\u6548\uff0c\u65e0\u6cd5\u90e8\u7f72\uff01"));
        }
        return queued;
    }

    private static void completeHabDeployment(ServerPlayer player, BastionData bastion, BastionManager manager) {
        player.f_8924_.execute(() -> manager.clearWaiting(player.m_20148_()));
        player.m_143403_(GameType.SURVIVAL);
        player.m_21219_();
        int invincibilityTicks = GameConfig.getRespawnInvincibilityTicks();
        player.m_7292_(new MobEffectInstance(MobEffects.f_19606_, invincibilityTicks, 127, false, false, false));
        GameStateManager.getInstance().applyBattlefieldMiningRestriction(player);
        GameStateManager.getInstance().onMidGameDeployComplete(player);
        player.m_213846_(Component.m_237113_("\u00a7a\u5df2\u5728 \u00a7e" + bastion.getName() + " \u00a7a\u590d\u6d3b\uff01"));
    }

    public static class BastionInfo {
        public final UUID id;
        public final String name;
        public final String team;

        public BastionInfo(UUID id, String name, String team) {
            this.id = id;
            this.name = name;
            this.team = team;
        }
    }
}

