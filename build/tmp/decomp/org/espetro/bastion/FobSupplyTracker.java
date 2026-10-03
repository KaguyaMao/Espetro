/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.bastion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.FortificationManager;
import org.espetro.bastion.OutpostSupplyTracker;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.network.FobSupplySyncPacket;
import org.espetro.network.NetworkManager;

@Mod.EventBusSubscriber(modid="espetro", bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class FobSupplyTracker {
    private static final int MOVEMENT_CHECK_INTERVAL = 5;
    private static final Map<UUID, BlockPos> lastPositions = new HashMap<UUID, BlockPos>();
    private static final Map<UUID, String> lastSignatures = new HashMap<UUID, String>();
    private static final Map<UUID, Set<UUID>> playerRadios = new HashMap<UUID, Set<UUID>>();
    private static final Map<UUID, Set<UUID>> radioSubscribers = new HashMap<UUID, Set<UUID>>();

    private FobSupplyTracker() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        ServerPlayer player;
        block5: {
            block4: {
                Player player2;
                if (event.phase != TickEvent.Phase.END || !((player2 = event.player) instanceof ServerPlayer)) break block4;
                player = (ServerPlayer)player2;
                if (player.f_19797_ % 5 == 0) break block5;
            }
            return;
        }
        BlockPos current = player.m_20183_();
        BlockPos previous = lastPositions.put(player.m_20148_(), current.m_7949_());
        if (previous == null || !previous.equals(current)) {
            FobSupplyTracker.syncPlayer(player, false);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            FobSupplyTracker.syncPlayer(player2, true);
        }
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            lastPositions.remove(player2.m_20148_());
            FobSupplyTracker.syncPlayer(player2, true);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        FobSupplyTracker.clearPlayer(event.getEntity().m_20148_());
    }

    public static void notifySupplyChanged(BastionData radio) {
        if (radio == null || !radio.isRadio()) {
            return;
        }
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        Set<UUID> subscribers = radioSubscribers.get(radio.getBastionId());
        if (subscribers == null || subscribers.isEmpty()) {
            ServerLevel level = radio.getLevel();
            for (ServerPlayer player : level.m_6907_()) {
                if (!radio.getTeam().equals(Espetro.getPlayerTeam(player))) continue;
                double radius = LogisticsConfig.get().radioBuildRadius;
                if (!(radio.getPosition().m_123331_(player.m_20183_()) <= radius * radius)) continue;
                FobSupplyTracker.syncPlayer(player, true);
            }
            return;
        }
        for (UUID playerId : new ArrayList<UUID>(subscribers)) {
            ServerPlayer player = server.m_6846_().m_11259_(playerId);
            if (player == null) {
                FobSupplyTracker.clearPlayer(playerId);
                continue;
            }
            FobSupplyTracker.syncPlayer(player, true);
        }
    }

    public static void notifyConstructionProgressChanged(ServerLevel level, BlockPos anchor, String team) {
        if (level == null || anchor == null || team == null) {
            return;
        }
        double radius = LogisticsConfig.get().radioBuildRadius;
        double radiusSq = radius * radius;
        for (ServerPlayer player : level.m_6907_()) {
            if (!team.equals(Espetro.getPlayerTeam(player)) || !(player.m_20183_().m_123331_(anchor) <= radiusSq)) continue;
            FobSupplyTracker.syncPlayer(player, true);
        }
    }

    public static void clearPlayer(UUID playerId) {
        lastPositions.remove(playerId);
        lastSignatures.remove(playerId);
        Set<UUID> radios = playerRadios.remove(playerId);
        if (radios == null) {
            return;
        }
        for (UUID radioId : radios) {
            Set<UUID> subscribers = radioSubscribers.get(radioId);
            if (subscribers == null) continue;
            subscribers.remove(playerId);
            if (!subscribers.isEmpty()) continue;
            radioSubscribers.remove(radioId);
        }
    }

    public static void clearAll() {
        lastPositions.clear();
        lastSignatures.clear();
        playerRadios.clear();
        radioSubscribers.clear();
        OutpostSupplyTracker.clearAll();
    }

    private static void syncPlayer(ServerPlayer player, boolean force) {
        Level level;
        String team = Espetro.getPlayerTeam(player);
        if (team == null || !((level = player.m_9236_()) instanceof ServerLevel)) {
            FobSupplyTracker.updateMembership(player.m_20148_(), Set.of());
            FobSupplyTracker.sendOut(player, force);
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        List<BastionData> radios = BastionManager.getInstance().findCoveringRadios(level2, player.m_20183_(), team);
        HashSet<UUID> ids = new HashSet<UUID>();
        int construction = 0;
        int ammunition = 0;
        for (BastionData radio : radios) {
            ids.add(radio.getBastionId());
            construction += radio.getConstructionSupplies();
            ammunition += radio.getAmmunitionSupplies();
        }
        FobSupplyTracker.updateMembership(player.m_20148_(), ids);
        FortificationManager.RadioConstructionProgress radioProgress = FortificationManager.getInstance().getRadioConstructionProgress(level2, player.m_20183_(), team);
        if (radios.isEmpty() && radioProgress == null) {
            FobSupplyTracker.sendOut(player, force);
            return;
        }
        LogisticsConfig.LogisticsSettings config = LogisticsConfig.get();
        int maxConstruction = Math.max(1, config.maxConstruction);
        int maxAmmunition = Math.max(1, config.maxAmmunition);
        int radioHealth = 0;
        int radioMaxHealth = 1;
        if (radioProgress != null) {
            radioHealth = radioProgress.progress();
            radioMaxHealth = Math.max(1, radioProgress.required());
        } else {
            BastionData healthRadio = BastionManager.getInstance().findNearestRadio(level2, player.m_20183_(), team, LogisticsConfig.get().radioBuildRadius);
            if (healthRadio != null) {
                radioHealth = (int)Math.ceil(healthRadio.getCoreHealth());
                radioMaxHealth = Math.max(1, BastionManager.getInstance().getArmorStandHealth());
            }
        }
        String signature = "1|" + construction + "|" + ammunition + "|" + maxConstruction + "|" + maxAmmunition + "|" + radioHealth + "|" + radioMaxHealth;
        if (!force && signature.equals(lastSignatures.get(player.m_20148_()))) {
            return;
        }
        lastSignatures.put(player.m_20148_(), signature);
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new FobSupplySyncPacket(true, construction, ammunition, maxConstruction, maxAmmunition, radioHealth, radioMaxHealth));
    }

    private static void updateMembership(UUID playerId, Set<UUID> next) {
        Set previous = playerRadios.put(playerId, new HashSet<UUID>(next));
        if (previous != null) {
            for (UUID removed : previous) {
                Set<UUID> subscribers;
                if (next.contains(removed) || (subscribers = radioSubscribers.get(removed)) == null) continue;
                subscribers.remove(playerId);
                if (!subscribers.isEmpty()) continue;
                radioSubscribers.remove(removed);
            }
        }
        for (UUID radioId : next) {
            radioSubscribers.computeIfAbsent(radioId, ignored -> new HashSet()).add(playerId);
        }
    }

    private static void sendOut(ServerPlayer player, boolean force) {
        if (!force && "0".equals(lastSignatures.get(player.m_20148_()))) {
            return;
        }
        lastSignatures.put(player.m_20148_(), "0");
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)FobSupplySyncPacket.outOfRange());
    }
}

