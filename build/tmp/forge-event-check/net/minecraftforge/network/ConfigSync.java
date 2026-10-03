/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.fml.config.ConfigTracker
 *  net.minecraftforge.fml.config.ModConfig
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.minecraftforge.network;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.HandshakeMessages;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigSync {
    public static final ConfigSync INSTANCE = new ConfigSync(ConfigTracker.INSTANCE);
    private final ConfigTracker tracker;

    private ConfigSync(ConfigTracker tracker) {
        this.tracker = tracker;
    }

    public List<Pair<String, HandshakeMessages.S2CConfigData>> syncConfigs(boolean isLocal) {
        Map<String, byte[]> configData = ((Set)this.tracker.configSets().get(ModConfig.Type.SERVER)).stream().collect(Collectors.toMap(ModConfig::getFileName, mc -> {
            try {
                return Files.readAllBytes(mc.getFullPath());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        return configData.entrySet().stream().map(e -> Pair.of((Object)("Config " + (String)e.getKey()), (Object)new HandshakeMessages.S2CConfigData((String)e.getKey(), (byte[])e.getValue()))).collect(Collectors.toList());
    }

    public void receiveSyncedConfig(HandshakeMessages.S2CConfigData s2CConfigData, Supplier<NetworkEvent.Context> contextSupplier) {
        if (!Minecraft.m_91087_().m_91090_()) {
            Optional.ofNullable((ModConfig)this.tracker.fileMap().get(s2CConfigData.getFileName())).ifPresent(mc -> mc.acceptSyncedConfig(s2CConfigData.getBytes()));
        }
    }
}

