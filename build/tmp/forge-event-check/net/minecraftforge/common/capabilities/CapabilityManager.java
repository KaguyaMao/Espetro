/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.Logging
 *  net.minecraftforge.fml.ModLoader
 *  net.minecraftforge.forgespi.language.ModFileScanData
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.objectweb.asm.Type
 */
package net.minecraftforge.common.capabilities;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

public enum CapabilityManager {
    INSTANCE;

    static final Logger LOGGER;
    private static final Type AUTO_REGISTER;
    private final IdentityHashMap<String, Capability<?>> providers = new IdentityHashMap();

    public static <T> Capability<T> get(CapabilityToken<T> type) {
        return INSTANCE.get(type.getType(), false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    <T> Capability<T> get(String realName, boolean registering) {
        Capability cap;
        Object object = this.providers;
        synchronized (object) {
            realName = realName.intern();
            cap = this.providers.computeIfAbsent(realName, Capability::new);
        }
        if (registering) {
            object = cap;
            synchronized (object) {
                if (cap.isRegistered()) {
                    LOGGER.error(Logging.CAPABILITIES, "Cannot register capability implementation multiple times : {}", (Object)realName);
                    throw new IllegalArgumentException("Cannot register a capability implementation multiple times : " + realName);
                }
                cap.onRegister();
            }
        }
        return cap;
    }

    public void injectCapabilities(List<ModFileScanData> data) {
        List<Type> autos = data.stream().flatMap(e -> e.getAnnotations().stream()).filter(a -> AUTO_REGISTER.equals((Object)a.annotationType())).map(a -> a.clazz()).distinct().sorted(Comparator.comparing(Type::toString)).toList();
        for (Type auto : autos) {
            LOGGER.debug(Logging.CAPABILITIES, "Attempting to automatically register: " + String.valueOf(auto));
            this.get(auto.getInternalName(), true);
        }
        RegisterCapabilitiesEvent event = new RegisterCapabilitiesEvent();
        ModLoader.get().postEvent((Event)event);
    }

    static {
        LOGGER = LogManager.getLogger();
        AUTO_REGISTER = Type.getType(AutoRegisterCapability.class);
    }
}

