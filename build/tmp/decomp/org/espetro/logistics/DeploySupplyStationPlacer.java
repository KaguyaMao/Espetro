/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.logistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.espetro.Espetro;
import org.espetro.logistics.LogisticsBlocks;
import org.espetro.logistics.SupplySourceBlockEntity;
import org.espetro.team.SpawnPointConfig;

public final class DeploySupplyStationPlacer {
    public static final String TAG = "espetro_deploy_supply_station";
    public static final String LABEL = "\u8865\u7ed9\u7ad9";
    private static final double SIDE_OFFSET = 2.0;
    private static final int Y_OFFSET = 0;
    private static final double LABEL_Y_ABOVE_BLOCK = 0.85;
    private static final Map<String, List<PlacedStation>> PLACED = new HashMap<String, List<PlacedStation>>();

    private DeploySupplyStationPlacer() {
    }

    public static int placeAtSpawnPoints(ServerLevel level) {
        if (level == null || LogisticsBlocks.SUPPLY_SOURCE == null) {
            return 0;
        }
        DeploySupplyStationPlacer.clear(level);
        int placed = 0;
        for (String team : new String[]{"ATTACK", "DEFEND"}) {
            SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint(team);
            if (spawn == null || !DeploySupplyStationPlacer.placeOne(level, spawn, team)) continue;
            ++placed;
        }
        Espetro.LOGGER.info("\u539f\u90e8\u7f72\u70b9\u8865\u7ed9\u7ad9\u9884\u653e\u5b8c\u6210: {} \u4e2a (\u7ef4\u5ea6 {})", (Object)placed, (Object)level.m_46472_().m_135782_());
        return placed;
    }

    public static int clear(@Nullable ServerLevel level) {
        if (level == null) {
            return 0;
        }
        String dimension = level.m_46472_().m_135782_().toString();
        List<PlacedStation> stations = PLACED.remove(dimension);
        if (stations == null || stations.isEmpty()) {
            return 0;
        }
        int removed = 0;
        for (PlacedStation station : stations) {
            Entity label;
            if (level.m_46805_(station.blockPos()) && DeploySupplyStationPlacer.isAutoSupplyBlock(level, station.blockPos())) {
                level.m_7731_(station.blockPos(), Blocks.f_50016_.m_49966_(), 3);
                ++removed;
            }
            if ((label = level.m_8791_(station.labelId())) == null || label.m_213877_()) continue;
            label.m_146870_();
        }
        return removed;
    }

    private static boolean placeOne(ServerLevel level, SpawnPointConfig.SpawnPoint spawn, String team) {
        UUID labelId;
        BlockPos pos = DeploySupplyStationPlacer.resolveOffset(spawn);
        if (!level.m_46805_(pos)) {
            Espetro.LOGGER.warn("\u90e8\u7f72\u70b9\u8865\u7ed9\u7ad9\u533a\u5757\u5c1a\u672a\u9884\u8f7d\uff0c\u8df3\u8fc7 {} ({})", (Object)pos, (Object)team);
            return false;
        }
        BlockState state = LogisticsBlocks.SUPPLY_SOURCE.m_49966_();
        if (!level.m_7731_(pos, state, 3)) {
            Espetro.LOGGER.warn("\u65e0\u6cd5\u653e\u7f6e\u90e8\u7f72\u70b9\u8865\u7ed9\u7ad9 at {} ({})", (Object)pos, (Object)team);
            return false;
        }
        BlockEntity be = level.m_7702_(pos);
        if (be instanceof SupplySourceBlockEntity) {
            SupplySourceBlockEntity supply = (SupplySourceBlockEntity)be;
            supply.setSourceId("default");
        }
        if ((labelId = DeploySupplyStationPlacer.spawnLabel(level, pos, team)) == null) {
            Espetro.LOGGER.warn("\u90e8\u7f72\u70b9\u8865\u7ed9\u7ad9\u6807\u9898\u751f\u6210\u5931\u8d25 at {} ({})", (Object)pos, (Object)team);
            labelId = UUID.randomUUID();
        }
        PLACED.computeIfAbsent(level.m_46472_().m_135782_().toString(), ignored -> new ArrayList()).add(new PlacedStation(pos.m_7949_(), labelId));
        return true;
    }

    public static BlockPos resolveOffset(SpawnPointConfig.SpawnPoint spawn) {
        float yawRad = spawn.yaw * ((float)Math.PI / 180);
        double rightX = -Mth.m_14089_(yawRad);
        double rightZ = -Mth.m_14031_(yawRad);
        int x = Mth.m_14107_(spawn.x + rightX * 2.0);
        int y = Mth.m_14107_(spawn.y) + 0;
        int z = Mth.m_14107_(spawn.z + rightZ * 2.0);
        return new BlockPos(x, y, z);
    }

    @Nullable
    private static UUID spawnLabel(ServerLevel level, BlockPos blockPos, String team) {
        ArmorStand stand = EntityType.f_20529_.m_20615_(level);
        if (stand == null) {
            return null;
        }
        double x = (double)blockPos.m_123341_() + 0.5;
        double y = (double)blockPos.m_123342_() + 1.0 + 0.85;
        double z = (double)blockPos.m_123343_() + 0.5;
        stand.m_6034_(x, y, z);
        stand.m_6593_(Component.m_237113_(LABEL));
        stand.m_20340_(true);
        stand.m_6842_(true);
        stand.m_20242_(true);
        stand.m_20225_(true);
        stand.m_20331_(true);
        CompoundTag flags = new CompoundTag();
        stand.m_20240_(flags);
        flags.m_128379_("Marker", true);
        flags.m_128379_("Invisible", true);
        flags.m_128379_("NoGravity", true);
        flags.m_128379_("Silent", true);
        flags.m_128379_("Invulnerable", true);
        flags.m_128379_("CustomNameVisible", true);
        stand.m_20258_(flags);
        stand.m_6034_(x, y, z);
        stand.m_6593_(Component.m_237113_(LABEL));
        stand.m_20340_(true);
        stand.m_20049_(TAG);
        stand.m_20049_("espetro_deploy_supply_station_team_" + team);
        if (!level.m_7967_(stand)) {
            stand.m_146870_();
            return null;
        }
        return stand.m_20148_();
    }

    private static boolean isAutoSupplyBlock(ServerLevel level, BlockPos pos) {
        if (LogisticsBlocks.SUPPLY_SOURCE == null) {
            return false;
        }
        return level.m_8055_(pos).m_60713_(LogisticsBlocks.SUPPLY_SOURCE);
    }

    private record PlacedStation(BlockPos blockPos, UUID labelId) {
    }
}

