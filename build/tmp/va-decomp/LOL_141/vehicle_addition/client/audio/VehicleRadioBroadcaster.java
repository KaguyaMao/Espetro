/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package LOL_141.vehicle_addition.client.audio;

import LOL_141.vehicle_addition.client.audio.RadioSoundInstance;
import LOL_141.vehicle_addition.network.NetworkHandler;
import LOL_141.vehicle_addition.network.VehicleRadioPacket;
import LOL_141.vehicle_addition.radio.RadioManager;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(value=Dist.CLIENT)
public final class VehicleRadioBroadcaster {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Integer, ActiveSound> ACTIVE = new HashMap<Integer, ActiveSound>();

    private VehicleRadioBroadcaster() {
    }

    public static void broadcast(int vehicleId, String url, long startTime) {
        NetworkHandler.sendToServer(new VehicleRadioPacket(vehicleId, url, startTime));
    }

    public static void onPacket(int vehicleId, String url, long startTime) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null) {
            return;
        }
        if (mc.f_91074_ != null && mc.f_91074_.m_20202_() != null && mc.f_91074_.m_20202_().m_19879_() == vehicleId && RadioManager.isController()) {
            return;
        }
        if (url == null || url.isEmpty()) {
            VehicleRadioBroadcaster.stopVehicle(vehicleId);
            return;
        }
        ActiveSound current = ACTIVE.get(vehicleId);
        if (current != null && url.equals(current.url) && startTime == current.startTime) {
            return;
        }
        VehicleRadioBroadcaster.stopVehicle(vehicleId);
        Entity vehicle = mc.f_91073_.m_6815_(vehicleId);
        if (vehicle == null) {
            LOGGER.warn("Vehicle radio: entity {} not loaded, skip", (Object)vehicleId);
            return;
        }
        try {
            long elapsed;
            RadioSoundInstance instance = RadioSoundInstance.fromUrlPositional(url, (float)vehicle.m_20185_(), (float)vehicle.m_20186_(), (float)vehicle.m_20189_(), true);
            if (startTime > 0L && (elapsed = System.currentTimeMillis() - startTime) > 0L) {
                instance.setSkipMs(elapsed);
            }
            mc.m_91106_().m_120367_((SoundInstance)instance);
            ACTIVE.put(vehicleId, new ActiveSound(url, startTime, instance));
            LOGGER.info("Vehicle radio playing for vehicle {}: {} (skip {} ms)", (Object)vehicleId, (Object)url, (Object)(startTime > 0L ? System.currentTimeMillis() - startTime : 0L));
        }
        catch (MalformedURLException e) {
            LOGGER.error("Invalid vehicle radio url: {}", (Object)url, (Object)e);
        }
    }

    private static void stopVehicle(int vehicleId) {
        ActiveSound current = ACTIVE.remove(vehicleId);
        if (current != null) {
            current.instance.setCancelled(true);
            Minecraft.m_91087_().m_91106_().m_120399_((SoundInstance)current.instance);
        }
    }

    public static void tick() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null) {
            VehicleRadioBroadcaster.clear();
            return;
        }
        ACTIVE.entrySet().removeIf(entry -> {
            Entity vehicle = mc.f_91073_.m_6815_(((Integer)entry.getKey()).intValue());
            if (vehicle == null) {
                ((ActiveSound)entry.getValue()).instance.setCancelled(true);
                mc.m_91106_().m_120399_((SoundInstance)((ActiveSound)entry.getValue()).instance);
                return true;
            }
            ((ActiveSound)entry.getValue()).instance.setPosition((float)vehicle.m_20185_(), (float)vehicle.m_20186_(), (float)vehicle.m_20189_());
            return false;
        });
    }

    public static void clear() {
        Minecraft mc = Minecraft.m_91087_();
        for (ActiveSound sound : ACTIVE.values()) {
            sound.instance.setCancelled(true);
            mc.m_91106_().m_120399_((SoundInstance)sound.instance);
        }
        ACTIVE.clear();
    }

    private record ActiveSound(String url, long startTime, RadioSoundInstance instance) {
    }
}

