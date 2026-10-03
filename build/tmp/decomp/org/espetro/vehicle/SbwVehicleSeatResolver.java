/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.vehicle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class SbwVehicleSeatResolver {
    private static final String VEHICLE_CLASS_NAME = "com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity";
    private static volatile VehicleAccess vehicleAccess;

    private SbwVehicleSeatResolver() {
    }

    @Nullable
    public static SeatState resolveCurrent(@Nullable ServerPlayer player) {
        if (player == null || !player.m_20159_()) {
            return null;
        }
        Entity vehicle = player.m_20201_();
        if (vehicle == player) {
            return null;
        }
        int seat = SbwVehicleSeatResolver.getSeatIndex(vehicle, player);
        if (seat < 0) {
            return null;
        }
        return new SeatState(SbwVehicleSeatResolver.getKind(vehicle), seat, vehicle);
    }

    public static boolean isSupportedVehicle(@Nullable Entity vehicle) {
        VehicleAccess access = SbwVehicleSeatResolver.access();
        return vehicle != null && access != null && access.vehicleClass.isInstance(vehicle);
    }

    public static Kind getKind(@Nullable Entity vehicle) {
        VehicleAccess access = SbwVehicleSeatResolver.access();
        if (vehicle == null || access == null || !access.vehicleClass.isInstance(vehicle)) {
            return Kind.OTHER;
        }
        try {
            String type;
            String string;
            Object rawType = access.getVehicleType.invoke(vehicle, new Object[0]);
            if (rawType instanceof Enum) {
                Enum value = (Enum)rawType;
                string = value.name();
            } else {
                string = String.valueOf(rawType);
            }
            return switch (type = string) {
                case "TANK" -> Kind.TANK;
                case "APC" -> Kind.IFV;
                case "HELICOPTER" -> Kind.HELICOPTER;
                default -> Kind.OTHER;
            };
        }
        catch (IllegalAccessException | InvocationTargetException ignored) {
            return Kind.OTHER;
        }
    }

    public static int getSeatIndex(@Nullable Entity vehicle, @Nullable Entity passenger) {
        VehicleAccess access = SbwVehicleSeatResolver.access();
        if (vehicle == null || passenger == null || access == null || !access.vehicleClass.isInstance(vehicle)) {
            return -1;
        }
        try {
            int n;
            Object result = access.getSeatIndex.invoke(vehicle, passenger);
            if (result instanceof Number) {
                Number number = (Number)result;
                n = number.intValue();
            } else {
                n = -1;
            }
            return n;
        }
        catch (IllegalAccessException | InvocationTargetException ignored) {
            return -1;
        }
    }

    @Nullable
    public static List<?> getOrderedSeatOccupants(@Nullable Entity vehicle) {
        VehicleAccess access = SbwVehicleSeatResolver.access();
        if (vehicle == null || access == null || !access.vehicleClass.isInstance(vehicle)) {
            return null;
        }
        try {
            List passengers;
            Object rawPassengers = access.getOrderedPassengers.invoke(vehicle, new Object[0]);
            return rawPassengers instanceof List ? (passengers = (List)rawPassengers) : null;
        }
        catch (IllegalAccessException | InvocationTargetException ignored) {
            return null;
        }
    }

    public static boolean overrideNextMountSeat(@Nullable Entity vehicle, @Nullable ServerPlayer passenger, int seatIndex) {
        VehicleAccess access = SbwVehicleSeatResolver.access();
        if (vehicle == null || passenger == null || seatIndex < 0 || access == null || !access.vehicleClass.isInstance(vehicle)) {
            return false;
        }
        try {
            Function function;
            Object current = access.getEntityIndexOverride.invoke(vehicle, new Object[0]);
            Function original = current instanceof Function ? (function = (Function)current) : null;
            OneShotSeatOverride override = new OneShotSeatOverride(access, vehicle, passenger, seatIndex, original);
            access.setEntityIndexOverride.invoke(vehicle, override);
            passenger.f_8924_.m_6937_(new TickTask(passenger.f_8924_.m_129921_(), override::restoreIfCurrent));
            return true;
        }
        catch (IllegalAccessException | RuntimeException | InvocationTargetException ignored) {
            return false;
        }
    }

    public static int predictMountSeat(@Nullable Entity vehicle, @Nullable Entity passenger) {
        VehicleAccess access = SbwVehicleSeatResolver.access();
        if (vehicle == null || passenger == null || access == null || !access.vehicleClass.isInstance(vehicle)) {
            return -1;
        }
        try {
            Number number;
            Function rawFunction;
            Function function;
            Object result;
            Object override = access.getEntityIndexOverride.invoke(vehicle, new Object[0]);
            if (override instanceof Function && (result = (function = (rawFunction = (Function)override)).apply(passenger)) instanceof Number && (number = (Number)result).intValue() != -1) {
                return number.intValue();
            }
            Object rawPassengers = access.getOrderedPassengers.invoke(vehicle, new Object[0]);
            if (!(rawPassengers instanceof List)) {
                return -1;
            }
            List passengers = (List)rawPassengers;
            for (int index = 0; index < passengers.size(); ++index) {
                if (passengers.get(index) != null) continue;
                return index;
            }
        }
        catch (IllegalAccessException | RuntimeException | InvocationTargetException ignored) {
            return -1;
        }
        return -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private static VehicleAccess access() {
        VehicleAccess cached = vehicleAccess;
        if (cached != null) {
            return cached.available ? cached : null;
        }
        Class<SbwVehicleSeatResolver> clazz = SbwVehicleSeatResolver.class;
        synchronized (SbwVehicleSeatResolver.class) {
            cached = vehicleAccess;
            if (cached == null) {
                try {
                    Class<?> vehicleClass = Class.forName(VEHICLE_CLASS_NAME, false, SbwVehicleSeatResolver.class.getClassLoader());
                    cached = new VehicleAccess(vehicleClass, vehicleClass.getMethod("getVehicleType", new Class[0]), vehicleClass.getMethod("getSeatIndex", Entity.class), vehicleClass.getMethod("getOrderedPassengers", new Class[0]), vehicleClass.getMethod("getEntityIndexOverride", new Class[0]), vehicleClass.getMethod("setEntityIndexOverride", Function.class), true);
                }
                catch (LinkageError | ReflectiveOperationException ignored) {
                    cached = VehicleAccess.UNAVAILABLE;
                }
                vehicleAccess = cached;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return cached.available ? cached : null;
        }
    }

    public record SeatState(Kind kind, int seatIndex, Entity vehicle) {
    }

    public static enum Kind {
        TANK,
        IFV,
        HELICOPTER,
        OTHER;

    }

    private record VehicleAccess(Class<?> vehicleClass, Method getVehicleType, Method getSeatIndex, Method getOrderedPassengers, Method getEntityIndexOverride, Method setEntityIndexOverride, boolean available) {
        private static final VehicleAccess UNAVAILABLE = new VehicleAccess(Object.class, null, null, null, null, null, false);
    }

    private static final class OneShotSeatOverride
    implements Function<Entity, Integer> {
        private final VehicleAccess access;
        private final Entity vehicle;
        private final Entity passenger;
        private final int seatIndex;
        private final Function<Entity, ?> original;

        private OneShotSeatOverride(VehicleAccess access, Entity vehicle, Entity passenger, int seatIndex, @Nullable Function<Entity, ?> original) {
            this.access = access;
            this.vehicle = vehicle;
            this.passenger = passenger;
            this.seatIndex = seatIndex;
            this.original = original;
        }

        @Override
        public Integer apply(Entity candidate) {
            this.restoreIfCurrent();
            if (candidate == this.passenger) {
                return this.seatIndex;
            }
            if (this.original == null) {
                return -1;
            }
            try {
                Integer n;
                Object result = this.original.apply(candidate);
                if (result instanceof Number) {
                    Number number = (Number)result;
                    n = number.intValue();
                } else {
                    n = -1;
                }
                return n;
            }
            catch (RuntimeException ignored) {
                return -1;
            }
        }

        private void restoreIfCurrent() {
            try {
                Object current = this.access.getEntityIndexOverride.invoke(this.vehicle, new Object[0]);
                if (current == this) {
                    this.access.setEntityIndexOverride.invoke(this.vehicle, this.original);
                }
            }
            catch (IllegalAccessException | RuntimeException | InvocationTargetException exception) {
                // empty catch block
            }
        }
    }
}

