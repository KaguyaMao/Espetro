/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.protection;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.espetro.Espetro;
import org.espetro.config.GameConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.team.SpawnPointConfig;
import org.espetro.vehicle.VehicleManager;

public final class MainBaseProtection {
    private static final String TEAM_TAG_PREFIX = "espetro_team_";

    private MainBaseProtection() {
    }

    public static boolean isProtected(@Nullable Entity entity) {
        String team;
        ServerLevel level;
        Level level2;
        if (entity == null || entity.m_9236_().f_46443_ || !((level2 = entity.m_9236_()) instanceof ServerLevel) || !BattlefieldContext.isActiveBattlefield(level = (ServerLevel)level2)) {
            return false;
        }
        double radius = GameConfig.getMainBaseInvulnerabilityRadius();
        if (radius <= 0.0) {
            return false;
        }
        if (entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)entity;
            v0 = MainBaseProtection.normalizeTeam(Espetro.getPlayerTeam(player));
        } else {
            v0 = team = MainBaseProtection.resolveVehicleTeam(entity);
        }
        if (team == null) {
            return false;
        }
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint(team);
        if (spawn == null) {
            return false;
        }
        return MainBaseProtection.isWithinHorizontalRadius(entity.m_20185_(), entity.m_20189_(), spawn.x, spawn.z, radius);
    }

    static boolean isWithinHorizontalRadius(double x, double z, double centerX, double centerZ, double radius) {
        if (radius <= 0.0) {
            return false;
        }
        double deltaX = x - centerX;
        double deltaZ = z - centerZ;
        return deltaX * deltaX + deltaZ * deltaZ <= radius * radius;
    }

    @Nullable
    private static String resolveVehicleTeam(Entity entity) {
        ServerPlayer player;
        String controllerTeam;
        VehicleManager vehicles = VehicleManager.getInstance();
        String tracked = MainBaseProtection.normalizeTeam(vehicles.getTrackedVehicleTeam(entity.m_20148_()));
        if (tracked != null) {
            return tracked;
        }
        String persistent = null;
        if (entity.getPersistentData().m_128425_("espetro_vehicle_team", 8)) {
            persistent = MainBaseProtection.normalizeTeam(entity.getPersistentData().m_128461_("espetro_vehicle_team"));
        }
        if (persistent != null) {
            return persistent;
        }
        for (String tag : entity.m_19880_()) {
            String fromTag;
            if (!tag.startsWith(TEAM_TAG_PREFIX) || (fromTag = MainBaseProtection.normalizeTeam(tag.substring(TEAM_TAG_PREFIX.length()))) == null) continue;
            return fromTag;
        }
        LivingEntity controller = entity.m_6688_();
        if (controller instanceof ServerPlayer && (controllerTeam = MainBaseProtection.normalizeTeam(Espetro.getPlayerTeam(player = (ServerPlayer)controller))) != null) {
            return controllerTeam;
        }
        return MainBaseProtection.resolvePassengerTeam(entity);
    }

    @Nullable
    private static String resolvePassengerTeam(Entity vehicle) {
        for (Entity passenger : vehicle.m_20197_()) {
            ServerPlayer player;
            String team;
            if (passenger instanceof ServerPlayer && (team = MainBaseProtection.normalizeTeam(Espetro.getPlayerTeam(player = (ServerPlayer)passenger))) != null) {
                return team;
            }
            String nested = MainBaseProtection.resolvePassengerTeam(passenger);
            if (nested == null) continue;
            return nested;
        }
        return null;
    }

    @Nullable
    private static String normalizeTeam(@Nullable String team) {
        if (team == null || team.isBlank()) {
            return null;
        }
        String normalized = team.trim().toUpperCase(Locale.ROOT);
        return "ATTACK".equals(normalized) || "DEFEND".equals(normalized) ? normalized : null;
    }
}

