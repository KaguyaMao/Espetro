package org.espetro.stats;

import net.minecraftforge.common.ForgeConfigSpec;

/** Server-owned scoring units. Values are copied at the start of each round. */
public final class RoundScoreConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.IntValue INFANTRY = score("infantry_kill", 100);
    private static final ForgeConfigSpec.IntValue VEHICLE = score("vehicle_destroyed", 150);
    private static final ForgeConfigSpec.IntValue SUPPLY = score("supply_points", 20);
    private static final ForgeConfigSpec.IntValue SUPPLY_UNIT = unit("supply_units", 100);
    private static final ForgeConfigSpec.IntValue SHOVEL = score("shovel_points", 5);
    private static final ForgeConfigSpec.IntValue TRANSPORT = score("transport_points", 5);
    private static final ForgeConfigSpec.IntValue PASSENGERS = unit("transport_passengers", 100);
    private static final ForgeConfigSpec.IntValue DISTANCE = unit("transport_distance", 100);
    private static final ForgeConfigSpec.IntValue MIN_TRIP = unit("transport_min_distance", 20);
    private static final ForgeConfigSpec.IntValue REPEAT_TRIP = unit("transport_repeat_seconds", 120);
    private static final ForgeConfigSpec.IntValue SHOVEL_LIMIT = unit("shovel_max_actions_per_site_per_minute", 60);
    private static final ForgeConfigSpec.IntValue RESCUE = score("rescue_points", 100);
    private static final ForgeConfigSpec.IntValue CAPTURE = score("capture_points", 5);
    private static final ForgeConfigSpec.IntValue CAPTURE_TIME = unit("capture_seconds", 60);
    private static final ForgeConfigSpec.BooleanValue HIDDEN = BUILDER
        .comment("Hide vanilla death messages and Espetro death/destruction notices, including event details in settlement.")
        .define("hide_death_and_destruction", false);
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private RoundScoreConfig() {}

    public record Rules(int infantry, int vehicle, int supply, int supplyUnit, int shovel,
                        int transport, int passengers, int distance, int minTrip, int repeatTrip,
                        int shovelLimit, int rescue, int capture, int captureTime) {}

    static Rules snapshot() {
        return new Rules(INFANTRY.get(), VEHICLE.get(), SUPPLY.get(), SUPPLY_UNIT.get(), SHOVEL.get(),
            TRANSPORT.get(), PASSENGERS.get(), DISTANCE.get(), MIN_TRIP.get(), REPEAT_TRIP.get(),
            SHOVEL_LIMIT.get(), RESCUE.get(), CAPTURE.get(), CAPTURE_TIME.get());
    }

    public static boolean hidden() { return SPEC.isLoaded() && HIDDEN.get(); }

    private static ForgeConfigSpec.IntValue score(String name, int value) {
        return BUILDER.defineInRange(name, value, 0, 100_000);
    }

    private static ForgeConfigSpec.IntValue unit(String name, int value) {
        return BUILDER.defineInRange(name, value, 1, 100_000);
    }
}
