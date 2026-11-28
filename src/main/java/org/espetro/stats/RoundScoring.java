package org.espetro.stats;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.espetro.Espetro;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.network.RoundReport;
import org.espetro.network.VehicleSupplyActionPacket;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.vehicle.SbwVehicleSeatResolver;

import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/** Per-round server ledger. Only validated gameplay transactions call the award methods. */
public final class RoundScoring {
    private static RoundScoreConfig.Rules rules;
    private static final Map<UUID, Tally> SCORES = new HashMap<>();
    private static final Map<UUID, Cargo> CARGO = new HashMap<>();
    private static final Map<Work, NavigableMap<Integer, Integer>> WORK = new HashMap<>();
    private static final Map<WorkPlayer, WorkWindow> WORK_RATE = new HashMap<>();
    private static final Map<UUID, Trip> TRIPS = new HashMap<>();
    private static final Map<DropOff, Long> DROP_OFFS = new HashMap<>();

    private static final class Tally {
        long combat, shovel, rescue, capture, delivered, transportUnits, captureTicks;
        long lastCaptureTick = Long.MIN_VALUE;
    }
    private static final class Cargo {
        UUID departedDriver;
        UUID loadingDriver;
        boolean seenAtBase;
        Vec3 lastPosition;
        long lastDrivenTick;
        int ammo, construction;
    }
    private record Work(String dimension, long anchor, String type, boolean build) {}
    private record WorkPlayer(UUID player, String dimension, long anchor) {}
    private record WorkWindow(long start, int count) {}
    private record DropOff(UUID driver, UUID passenger, int x, int z) {}
    private static final class Trip {
        final UUID vehicle, driver;
        final Vec3 start;
        Vec3 last;
        long lastTick;
        boolean invalid;
        Trip(UUID vehicle, UUID driver, Vec3 start, long tick) {
            this.vehicle = vehicle;
            this.driver = driver;
            this.start = this.last = start;
            this.lastTick = tick;
        }
    }

    private RoundScoring() {}

    public static void begin() {
        reset();
        rules = RoundScoreConfig.snapshot();
    }

    public static void reset() {
        rules = null;
        SCORES.clear();
        CARGO.clear();
        WORK.clear();
        WORK_RATE.clear();
        TRIPS.clear();
        DROP_OFFS.clear();
    }

    private static boolean active(ServerPlayer player) {
        return player != null && rules != null && !player.isSpectator()
            && GameStateManager.getInstance().getCurrentPhase() == GamePhase.BATTLE
            && BattlefieldContext.isActiveBattlefield(player.serverLevel()) && team(player) != null;
    }

    private static String team(ServerPlayer player) {
        String team = Espetro.getPlayerTeam(player);
        return "ATTACK".equals(team) || "DEFEND".equals(team) ? team : null;
    }

    private static Tally tally(UUID player) { return SCORES.computeIfAbsent(player, unused -> new Tally()); }

    public static void infantry(ServerPlayer killer) {
        if (active(killer)) tally(killer.getUUID()).combat += rules.infantry();
    }

    public static void vehicle(Entity attacker, String vehicleTeam) {
        if (attacker instanceof ServerPlayer player && active(player)
            && ("ATTACK".equals(vehicleTeam) || "DEFEND".equals(vehicleTeam))
            && !vehicleTeam.equals(team(player))) {
            tally(player.getUUID()).combat += rules.vehicle();
        }
    }

    public static boolean canRescue(ServerPlayer patient, ServerPlayer helper) {
        return active(helper) && helper.isAlive() && patient != helper && patient.serverLevel() == helper.serverLevel()
            && team(helper).equals(team(patient));
    }

    public static void rescue(ServerPlayer patient, ServerPlayer helper) {
        if (canRescue(patient, helper)) tally(helper.getUUID()).rescue += rules.rescue();
    }

    public static void contested(ServerPlayer player, int elapsed) {
        if (elapsed <= 0 || !active(player) || !player.isAlive()
            || player.getPersistentData().getBoolean("realgrisly_downed")) return;
        Tally score = tally(player.getUUID());
        long tick = player.serverLevel().getGameTime();
        if (score.lastCaptureTick == tick) return; // Overlapping eligible points cannot double-count.
        score.lastCaptureTick = tick;
        score.captureTicks += elapsed;
        int unit = rules.captureTime() * 20;
        score.capture += (score.captureTicks / unit) * rules.capture();
        score.captureTicks %= unit;
    }

    public static void shovel(ServerPlayer player, BlockPos anchor, String type, boolean build, int before, int after) {
        if (!active(player) || before == after) return;
        String dimension = player.serverLevel().dimension().location().toString();
        long pos = anchor.asLong(), tick = player.serverLevel().getGameTime();
        var key = new WorkPlayer(player.getUUID(), dimension, pos);
        var window = WORK_RATE.get(key);
        int count = window == null || tick - window.start() >= 1200 ? 0 : window.count();
        if (count >= rules.shovelLimit()) return;
        int low = Math.min(before, after), high = Math.max(before, after);
        var ranges = WORK.computeIfAbsent(new Work(dimension, pos, type, build), unused -> new TreeMap<>());
        if (!claimWorkRange(ranges, low, high)) return;
        WORK_RATE.put(key, new WorkWindow(count == 0 ? tick : window.start(), count + 1));
        tally(player.getUUID()).shovel += rules.shovel();
    }

    static boolean claimWorkRange(NavigableMap<Integer, Integer> ranges, int low, int high) {
        var previous = ranges.floorEntry(low);
        if (previous != null && previous.getValue() >= high) return false;
        if (previous != null && previous.getValue() >= low) low = previous.getKey();
        var next = ranges.ceilingEntry(low);
        while (next != null && next.getKey() <= high) {
            high = Math.max(high, next.getValue());
            ranges.remove(next.getKey());
            next = ranges.ceilingEntry(low);
        }
        ranges.put(low, high);
        return true;
    }

    public static void load(ServerPlayer player, UUID vehicle, boolean ammo, int added, boolean mainBase) {
        if (!mainBase || added <= 0 || !active(player)) return;
        var cargo = CARGO.computeIfAbsent(vehicle, unused -> new Cargo());
        if (!player.getUUID().equals(cargo.loadingDriver)) {
            cargo.ammo = cargo.construction = 0;
            cargo.loadingDriver = player.getUUID();
        }
        cargo.departedDriver = null;
        cargo.seenAtBase = false;
        cargo.lastPosition = null;
        if (ammo) cargo.ammo += added;
        else cargo.construction += added;
    }

    public static void discardCargo(UUID vehicle, boolean ammo, int removed) {
        if (vehicle != null) unload(null, vehicle, ammo, removed, 0);
    }

    public static void unload(ServerPlayer player, UUID vehicle, boolean ammo, int removed, int accepted) {
        var cargo = CARGO.get(vehicle);
        if (cargo == null || removed <= 0) return;
        int tracked = ammo ? cargo.ammo : cargo.construction;
        int credited = Math.min(tracked, removed);
        if (ammo) cargo.ammo -= credited;
        else cargo.construction -= credited;
        if (accepted > 0 && active(player) && player.getUUID().equals(cargo.departedDriver)) {
            tally(player.getUUID()).delivered += Math.min(credited, accepted);
        }
        if (cargo.ammo == 0 && cargo.construction == 0) CARGO.remove(vehicle);
    }

    public static void forgetVehicle(UUID vehicle) { CARGO.remove(vehicle); }

    public static void tick(MinecraftServer server) {
        if (rules == null || GameStateManager.getInstance().getCurrentPhase() != GamePhase.BATTLE) return;
        long tick = server.getTickCount();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!active(player) || !player.isAlive() || player.getPersistentData().getBoolean("realgrisly_downed")) {
                TRIPS.remove(player.getUUID());
                continue;
            }
            Entity vehicle = player.isPassenger() ? player.getRootVehicle() : null;
            Entity controller = vehicle == null ? null : vehicle.getControllingPassenger();
            int seat = SbwVehicleSeatResolver.getSeatIndex(vehicle, player);
            if (seat >= 0) {
                var ordered = SbwVehicleSeatResolver.getOrderedSeatOccupants(vehicle);
                if (ordered != null && !ordered.isEmpty()) {
                    controller = ordered.get(0) instanceof Entity driver ? driver : null;
                }
            }
            if (vehicle != null && (seat == 0 || controller == player)) {
                var cargo = CARGO.get(vehicle.getUUID());
                if (cargo != null) {
                    if (!player.getUUID().equals(cargo.loadingDriver)) {
                        cargo.departedDriver = null;
                        cargo.seenAtBase = false;
                        cargo.lastPosition = null;
                    } else if (VehicleSupplyActionPacket.isAtMainBase(player.serverLevel(), vehicle.blockPosition(), team(player))) {
                        cargo.departedDriver = null;
                        cargo.seenAtBase = true;
                        cargo.lastPosition = vehicle.position();
                        cargo.lastDrivenTick = tick;
                    } else if (cargo.seenAtBase) {
                        if (cargo.lastPosition == null || tick - cargo.lastDrivenTick > 1
                            || cargo.lastPosition.distanceToSqr(vehicle.position()) > 40 * 40) {
                            cargo.departedDriver = null;
                            cargo.seenAtBase = false;
                        } else {
                            cargo.departedDriver = player.getUUID();
                            cargo.lastPosition = vehicle.position();
                            cargo.lastDrivenTick = tick;
                        }
                    }
                }
            }
            if (vehicle == null || seat == 0 || controller == player) {
                Trip old = TRIPS.remove(player.getUUID());
                if (vehicle == null && old != null) finishTrip(server, player, old, tick);
                continue;
            }
            if (!(controller instanceof ServerPlayer driver) || !team(player).equals(team(driver))) {
                TRIPS.remove(player.getUUID());
                continue;
            }
            var trip = TRIPS.get(player.getUUID());
            if (trip == null || !trip.vehicle.equals(vehicle.getUUID()) || !trip.driver.equals(driver.getUUID())) {
                TRIPS.put(player.getUUID(), new Trip(vehicle.getUUID(), driver.getUUID(), vehicle.position(), tick));
            } else {
                if (trip.last.distanceToSqr(vehicle.position()) > 40 * 40) trip.invalid = true;
                trip.last = vehicle.position();
                trip.lastTick = tick;
            }
        }
        TRIPS.values().removeIf(trip -> tick - trip.lastTick > 2);
        if (tick % 200 == 0) DROP_OFFS.values().removeIf(time -> tick - time >= rules.repeatTrip() * 20L);
    }

    private static void finishTrip(MinecraftServer server, ServerPlayer passenger, Trip trip, long tick) {
        if (trip.invalid || tick - trip.lastTick != 1 || passenger.position().distanceToSqr(trip.last) > 12 * 12) return;
        double distance = Math.hypot(trip.last.x - trip.start.x, trip.last.z - trip.start.z);
        if (distance < rules.minTrip()) return;
        ServerPlayer driver = server.getPlayerList().getPlayer(trip.driver);
        if (!active(driver) || !team(passenger).equals(team(driver)) || passenger.serverLevel() != driver.serverLevel()) return;
        int sector = rules.minTrip();
        int x = Math.floorDiv(passenger.getBlockX(), sector), z = Math.floorDiv(passenger.getBlockZ(), sector);
        for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
            var previous = DROP_OFFS.get(new DropOff(trip.driver, passenger.getUUID(), x + dx, z + dz));
            if (previous != null && tick - previous < rules.repeatTrip() * 20L) return;
        }
        DROP_OFFS.put(new DropOff(trip.driver, passenger.getUUID(), x, z), tick);
        tally(trip.driver).transportUnits += distanceUnits(distance, rules.distance());
    }

    static long distanceUnits(double distance, int unit) { return (long) Math.ceil(distance / unit); }

    static long pointsForUnits(long units, int points, int denominator) {
        return Math.round(units * (double) points / denominator);
    }

    public static boolean friendlyDowning(ServerPlayer patient, Entity attacker) {
        return attacker instanceof ServerPlayer player
            && team(patient) != null && team(patient).equals(team(player));
    }

    public static RoundReport.Score score(UUID player) {
        Tally t = SCORES.get(player);
        if (t == null || rules == null) return RoundReport.Score.ZERO;
        long supply = pointsForUnits(t.delivered, rules.supply(), rules.supplyUnit());
        long transport = pointsForUnits(t.transportUnits, rules.transport(), rules.passengers());
        return new RoundReport.Score(t.combat, t.shovel + supply + transport, t.rescue, t.capture);
    }
}
