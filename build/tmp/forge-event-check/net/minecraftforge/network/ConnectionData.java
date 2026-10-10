/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Multimap
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.fml.ModList
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.minecraftforge.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkRegistry;
import org.apache.commons.lang3.tuple.Pair;

public class ConnectionData {
    private ImmutableMap<String, Pair<String, String>> modData;
    private ImmutableMap<ResourceLocation, String> channels;

    ConnectionData(Map<String, Pair<String, String>> modData, Map<ResourceLocation, String> channels) {
        this.modData = ImmutableMap.copyOf(modData);
        this.channels = ImmutableMap.copyOf(channels);
    }

    public ImmutableList<String> getModList() {
        return this.modData.keySet().asList();
    }

    public ImmutableMap<String, Pair<String, String>> getModData() {
        return this.modData;
    }

    public ImmutableMap<ResourceLocation, String> getChannels() {
        return this.channels;
    }

    public record ModMismatchData(Map<ResourceLocation, String> mismatchedModData, Map<ResourceLocation, Pair<String, String>> presentModData, boolean mismatchedDataFromServer) {
        public static ModMismatchData channel(Map<ResourceLocation, String> mismatchedChannels, ConnectionData connectionData, boolean mismatchedDataFromServer) {
            Map<ResourceLocation, String> mismatchedChannelData = ModMismatchData.enhanceWithModVersion(mismatchedChannels, connectionData, mismatchedDataFromServer);
            Map<ResourceLocation, Pair<String, String>> presentChannelData = ModMismatchData.getPresentChannelData(mismatchedChannels.keySet(), connectionData, mismatchedDataFromServer);
            return new ModMismatchData(mismatchedChannelData, presentChannelData, mismatchedDataFromServer);
        }

        public static ModMismatchData registry(Multimap<ResourceLocation, ResourceLocation> mismatchedRegistryData, ConnectionData connectionData) {
            List<ResourceLocation> mismatchedRegistryMods = mismatchedRegistryData.values().stream().map(ResourceLocation::m_135827_).distinct().map(id -> new ResourceLocation(id, "")).toList();
            Map<ResourceLocation, String> mismatchedRegistryModData = mismatchedRegistryMods.stream().map(id -> ModList.get().getModContainerById(id.m_135827_()).map(modContainer -> Pair.of((Object)id, (Object)modContainer.getModInfo().getVersion().toString())).orElse(Pair.of((Object)id, (Object)NetworkRegistry.ABSENT.version()))).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            Map<ResourceLocation, Pair<String, String>> presentModData = ModMismatchData.getServerSidePresentModData(mismatchedRegistryModData.keySet(), connectionData);
            return new ModMismatchData(mismatchedRegistryModData, presentModData, false);
        }

        public boolean containsMismatches() {
            return this.mismatchedModData != null && !this.mismatchedModData.isEmpty();
        }

        private static Map<ResourceLocation, String> enhanceWithModVersion(Map<ResourceLocation, String> mismatchedChannels, ConnectionData connectionData, boolean mismatchedDataFromServer) {
            Map<Object, Object> mismatchedModVersions = mismatchedDataFromServer ? (connectionData != null ? connectionData.getModData().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (String)((Pair)e.getValue()).getRight())) : Map.of()) : ModList.get().getMods().stream().map(info -> Pair.of((Object)info.getModId(), (Object)info.getVersion().toString())).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            return mismatchedChannels.keySet().stream().map(channel -> Pair.of((Object)channel, (Object)(((String)mismatchedChannels.get(channel)).equals(NetworkRegistry.ABSENT.version()) ? NetworkRegistry.ABSENT.version() : mismatchedModVersions.getOrDefault(channel.m_135827_(), NetworkRegistry.ABSENT.version())))).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        }

        private static Map<ResourceLocation, Pair<String, String>> getPresentChannelData(Set<ResourceLocation> mismatchedChannelsFilter, ConnectionData connectionData, boolean mismatchedDataFromServer) {
            Object channelData = mismatchedDataFromServer ? NetworkRegistry.buildChannelVersions() : (connectionData != null ? connectionData.getChannels() : Map.of());
            return channelData.keySet().stream().filter(mismatchedChannelsFilter::contains).map(id -> ModMismatchData.getPresentModDataFromChannel(id, connectionData, mismatchedDataFromServer)).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        }

        private static Pair<ResourceLocation, Pair<String, String>> getPresentModDataFromChannel(ResourceLocation channel, ConnectionData connectionData, boolean mismatchedDataFromServer) {
            if (mismatchedDataFromServer) {
                return ModList.get().getModContainerById(channel.m_135827_()).map(modContainer -> Pair.of((Object)channel, (Object)Pair.of((Object)modContainer.getModInfo().getDisplayName(), (Object)modContainer.getModInfo().getVersion().toString()))).orElse(Pair.of((Object)channel, (Object)Pair.of((Object)channel.m_135827_(), (Object)"")));
            }
            Map modData = connectionData != null ? connectionData.getModData() : Map.of();
            Pair modDataFromChannel = modData.getOrDefault(channel.m_135827_(), Pair.of((Object)channel.m_135827_(), (Object)""));
            return Pair.of((Object)channel, (Object)(((String)modDataFromChannel.getLeft()).isEmpty() ? Pair.of((Object)channel.m_135827_(), (Object)((String)modDataFromChannel.getRight())) : modDataFromChannel));
        }

        private static Map<ResourceLocation, Pair<String, String>> getServerSidePresentModData(Set<ResourceLocation> mismatchedModsFilter, ConnectionData connectionData) {
            Map serverModData = connectionData != null ? connectionData.getModData() : Map.of();
            Set modIdFilter = mismatchedModsFilter.stream().map(ResourceLocation::m_135827_).collect(Collectors.toSet());
            return serverModData.entrySet().stream().filter(e -> modIdFilter.contains(e.getKey())).collect(Collectors.toMap(e -> new ResourceLocation((String)e.getKey(), ""), Map.Entry::getValue));
        }
    }
}

