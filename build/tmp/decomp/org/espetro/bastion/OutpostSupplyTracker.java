/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
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
import org.espetro.logistics.LogisticsConfig;
import org.espetro.network.NetworkManager;
import org.espetro.network.OutpostSupplySyncPacket;
import org.espetro.team.OutpostManager;

@Mod.EventBusSubscriber(modid="espetro", bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class OutpostSupplyTracker {
    private static final int MOVEMENT_CHECK_INTERVAL = 5;
    private static final double OUTPOST_RANGE = 6.0;
    private static final Map<UUID, String> lastSignatures = new HashMap<UUID, String>();

    private OutpostSupplyTracker() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        ServerPlayer player;
        block3: {
            block2: {
                Player player2;
                if (event.phase != TickEvent.Phase.END || !((player2 = event.player) instanceof ServerPlayer)) break block2;
                player = (ServerPlayer)player2;
                if (player.f_19797_ % 5 == 0) break block3;
            }
            return;
        }
        OutpostSupplyTracker.syncPlayer(player, false);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            OutpostSupplyTracker.syncPlayer(player2, true);
        }
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            OutpostSupplyTracker.syncPlayer(player2, true);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        lastSignatures.remove(event.getEntity().m_20148_());
    }

    public static void clearAll() {
        lastSignatures.clear();
    }

    private static void syncPlayer(ServerPlayer player, boolean force) {
        ServerLevel level;
        String team;
        block7: {
            block6: {
                Level level2;
                team = Espetro.getPlayerTeam(player);
                if (team == null || !"DEFEND".equals(team) || !((level2 = player.m_9236_()) instanceof ServerLevel)) break block6;
                level = (ServerLevel)level2;
                if (OutpostManager.getInstance().isAvailable()) break block7;
            }
            OutpostSupplyTracker.sendOut(player, force);
            return;
        }
        OutpostManager.Outpost outpost = OutpostSupplyTracker.findNearestOutpost(player);
        if (outpost == null) {
            OutpostSupplyTracker.sendOut(player, force);
            return;
        }
        BlockPos outpostPos = BlockPos.m_274561_(outpost.x, outpost.y, outpost.z);
        double radioRadius = LogisticsConfig.get().radioBuildRadius;
        BastionData radio = BastionManager.getInstance().findNearestRadio(level, outpostPos, team, radioRadius);
        if (radio == null) {
            OutpostSupplyTracker.sendOut(player, force);
            return;
        }
        int health = (int)Math.ceil(radio.getCoreHealth());
        int maxHealth = Math.max(1, BastionManager.getInstance().getArmorStandHealth());
        int ammunition = radio.getAmmunitionSupplies();
        int construction = radio.getConstructionSupplies();
        boolean habEnabled = OutpostSupplyTracker.hasEnabledHab(level, radio, team);
        String signature = "1|" + health + "|" + maxHealth + "|" + ammunition + "|" + construction + "|" + habEnabled;
        if (!force && signature.equals(lastSignatures.get(player.m_20148_()))) {
            return;
        }
        lastSignatures.put(player.m_20148_(), signature);
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new OutpostSupplySyncPacket(true, health, maxHealth, ammunition, construction, habEnabled));
    }

    @Nullable
    private static OutpostManager.Outpost findNearestOutpost(ServerPlayer player) {
        BlockPos playerPos = player.m_20183_();
        double best = 36.0;
        OutpostManager.Outpost nearest = null;
        for (OutpostManager.Outpost outpost : OutpostManager.getInstance().getOutposts()) {
            BlockPos pos = BlockPos.m_274561_(outpost.x, outpost.y, outpost.z);
            double distance = playerPos.m_123331_(pos);
            if (!(distance <= best)) continue;
            best = distance;
            nearest = outpost;
        }
        return nearest;
    }

    private static boolean hasEnabledHab(ServerLevel level, BastionData radio, String team) {
        double radius = LogisticsConfig.get().radioBuildRadius;
        double radiusSq = radius * radius;
        for (BastionData bastion : BastionManager.getInstance().getAllBastions()) {
            if (!bastion.isActive() || !bastion.isHab() || !team.equals(bastion.getTeam()) || bastion.getLevel() != level || radio.getPosition().m_123331_(bastion.getPosition()) > radiusSq || !BastionManager.getInstance().isHabOperational(bastion, false)) continue;
            return true;
        }
        return false;
    }

    private static void sendOut(ServerPlayer player, boolean force) {
        if (!force && "0".equals(lastSignatures.get(player.m_20148_()))) {
            return;
        }
        lastSignatures.put(player.m_20148_(), "0");
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)OutpostSupplySyncPacket.outOfRange());
    }
}

