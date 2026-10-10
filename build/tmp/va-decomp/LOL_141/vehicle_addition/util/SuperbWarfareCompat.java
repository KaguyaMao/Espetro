/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.fml.ModList
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package LOL_141.vehicle_addition.util;

import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SuperbWarfareCompat {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SBW_VEHICLE = "com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity";
    private static final String WYZJ_VEHICLE = "org.ywzj.vehicle.entity.vehicle.AbstractVehicle";

    private SuperbWarfareCompat() {
    }

    public static boolean isRidingVehicle(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (ModList.get().isLoaded("superbwarfare") && SuperbWarfareCompat.isInstance(entity, SBW_VEHICLE)) {
            return true;
        }
        return ModList.get().isLoaded("ywzj_vehicle") && SuperbWarfareCompat.isInstance(entity, WYZJ_VEHICLE);
    }

    public static boolean isMainDriver(Player player) {
        if (player == null) {
            return false;
        }
        Entity vehicle = player.m_20202_();
        if (vehicle == null) {
            return false;
        }
        if (ModList.get().isLoaded("ywzj_vehicle") && SuperbWarfareCompat.isInstance(vehicle, WYZJ_VEHICLE)) {
            try {
                Object driver = vehicle.getClass().getMethod("getDriver", new Class[0]).invoke(vehicle, new Object[0]);
                return driver == player;
            }
            catch (Exception e) {
                LOGGER.warn("Failed to check wyzj driver", (Throwable)e);
                return false;
            }
        }
        if (ModList.get().isLoaded("superbwarfare") && SuperbWarfareCompat.isInstance(vehicle, SBW_VEHICLE)) {
            try {
                Object index = vehicle.getClass().getMethod("getSeatIndex", Entity.class).invoke(vehicle, player);
                if (index instanceof Number) {
                    Number number = (Number)index;
                    return number.intValue() == 0;
                }
            }
            catch (Exception e) {
                LOGGER.warn("Failed to check sbw seat index", (Throwable)e);
            }
            return false;
        }
        LivingEntity controlling = vehicle.m_6688_();
        if (controlling == player) {
            return true;
        }
        if (controlling == null) {
            List passengers = vehicle.m_20197_();
            return !passengers.isEmpty() && passengers.get(0) == player;
        }
        return false;
    }

    private static boolean isInstance(Object obj, String className) {
        try {
            return Class.forName(className).isInstance(obj);
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}

