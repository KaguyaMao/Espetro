/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.Registry
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.forgespi.language.IModInfo
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.network;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class HandshakeMessages {

    public static class S2CChannelMismatchData
    extends LoginIndexedMessage {
        private final Map<ResourceLocation, String> mismatchedChannelData;

        public S2CChannelMismatchData(Map<ResourceLocation, String> mismatchedChannelData) {
            this.mismatchedChannelData = mismatchedChannelData;
        }

        public static S2CChannelMismatchData decode(FriendlyByteBuf input) {
            Map mismatchedMods = input.m_236847_(i -> new ResourceLocation(i.m_130136_(256)), i -> i.m_130136_(256));
            return new S2CChannelMismatchData(mismatchedMods);
        }

        public void encode(FriendlyByteBuf output) {
            output.m_236831_(this.mismatchedChannelData, (o, r) -> o.m_130072_(r.toString(), 256), (o, v) -> o.m_130072_(v, 256));
        }

        public Map<ResourceLocation, String> getMismatchedChannelData() {
            return this.mismatchedChannelData;
        }
    }

    public static class S2CConfigData
    extends LoginIndexedMessage {
        private final String fileName;
        private final byte[] fileData;

        public S2CConfigData(String configFileName, byte[] configFileData) {
            this.fileName = configFileName;
            this.fileData = configFileData;
        }

        void encode(FriendlyByteBuf buffer) {
            buffer.m_130070_(this.fileName);
            buffer.m_130087_(this.fileData);
        }

        public static S2CConfigData decode(FriendlyByteBuf buffer) {
            return new S2CConfigData(buffer.m_130136_(Short.MAX_VALUE), buffer.m_130052_());
        }

        public String getFileName() {
            return this.fileName;
        }

        public byte[] getBytes() {
            return this.fileData;
        }
    }

    public static class S2CRegistry
    extends LoginIndexedMessage {
        private ResourceLocation registryName;
        @Nullable
        private ForgeRegistry.Snapshot snapshot;

        public S2CRegistry(ResourceLocation name, @Nullable ForgeRegistry.Snapshot snapshot) {
            this.registryName = name;
            this.snapshot = snapshot;
        }

        void encode(FriendlyByteBuf buffer) {
            buffer.m_130085_(this.registryName);
            buffer.writeBoolean(this.hasSnapshot());
            if (this.hasSnapshot()) {
                buffer.writeBytes((ByteBuf)this.snapshot.getPacketData());
            }
        }

        public static S2CRegistry decode(FriendlyByteBuf buffer) {
            ResourceLocation name = buffer.m_130281_();
            ForgeRegistry.Snapshot snapshot = null;
            if (buffer.readBoolean()) {
                snapshot = ForgeRegistry.Snapshot.read(buffer);
            }
            return new S2CRegistry(name, snapshot);
        }

        public ResourceLocation getRegistryName() {
            return this.registryName;
        }

        public boolean hasSnapshot() {
            return this.snapshot != null;
        }

        @Nullable
        public ForgeRegistry.Snapshot getSnapshot() {
            return this.snapshot;
        }
    }

    public static class C2SAcknowledge
    extends LoginIndexedMessage {
        public void encode(FriendlyByteBuf buf) {
        }

        public static C2SAcknowledge decode(FriendlyByteBuf buf) {
            return new C2SAcknowledge();
        }
    }

    public static class C2SModListReply
    extends LoginIndexedMessage {
        private List<String> mods;
        private Map<ResourceLocation, String> channels;
        private Map<ResourceLocation, String> registries;

        public C2SModListReply() {
            this.mods = ModList.get().getMods().stream().map(IModInfo::getModId).collect(Collectors.toList());
            this.channels = NetworkRegistry.buildChannelVersions();
            this.registries = Maps.newHashMap();
        }

        private C2SModListReply(List<String> mods, Map<ResourceLocation, String> channels, Map<ResourceLocation, String> registries) {
            this.mods = mods;
            this.channels = channels;
            this.registries = registries;
        }

        public static C2SModListReply decode(FriendlyByteBuf input) {
            ArrayList<String> mods = new ArrayList<String>();
            int len = input.m_130242_();
            for (int x = 0; x < len; ++x) {
                mods.add(input.m_130136_(256));
            }
            HashMap<ResourceLocation, String> channels = new HashMap<ResourceLocation, String>();
            len = input.m_130242_();
            for (int x = 0; x < len; ++x) {
                channels.put(input.m_130281_(), input.m_130136_(256));
            }
            HashMap<ResourceLocation, String> registries = new HashMap<ResourceLocation, String>();
            len = input.m_130242_();
            for (int x = 0; x < len; ++x) {
                registries.put(input.m_130281_(), input.m_130136_(256));
            }
            return new C2SModListReply(mods, channels, registries);
        }

        public void encode(FriendlyByteBuf output) {
            output.m_130130_(this.mods.size());
            this.mods.forEach(m -> output.m_130072_(m, 256));
            output.m_130130_(this.channels.size());
            this.channels.forEach((k, v) -> {
                output.m_130085_(k);
                output.m_130072_(v, 256);
            });
            output.m_130130_(this.registries.size());
            this.registries.forEach((k, v) -> {
                output.m_130085_(k);
                output.m_130072_(v, 256);
            });
        }

        public List<String> getModList() {
            return this.mods;
        }

        public Map<ResourceLocation, String> getRegistries() {
            return this.registries;
        }

        public Map<ResourceLocation, String> getChannels() {
            return this.channels;
        }
    }

    public static class S2CModData
    extends LoginIndexedMessage {
        private final Map<String, Pair<String, String>> mods;

        public S2CModData() {
            this.mods = ModList.get().getMods().stream().collect(Collectors.toMap(IModInfo::getModId, info -> Pair.of((Object)info.getDisplayName(), (Object)info.getVersion().toString())));
        }

        private S2CModData(Map<String, Pair<String, String>> mods) {
            this.mods = mods;
        }

        public static S2CModData decode(FriendlyByteBuf input) {
            Map mods = input.m_236847_(o -> o.m_130136_(256), o -> Pair.of((Object)o.m_130136_(256), (Object)o.m_130136_(256)));
            return new S2CModData(mods);
        }

        public void encode(FriendlyByteBuf output) {
            output.m_236831_(this.mods, (o, s) -> o.m_130072_(s, 256), (o, p) -> {
                o.m_130072_((String)p.getLeft(), 256);
                o.m_130072_((String)p.getRight(), 256);
            });
        }

        public Map<String, Pair<String, String>> getMods() {
            return this.mods;
        }
    }

    public static class S2CModList
    extends LoginIndexedMessage {
        private List<String> mods;
        private Map<ResourceLocation, String> channels;
        private List<ResourceLocation> registries;
        private final List<ResourceKey<? extends Registry<?>>> dataPackRegistries;

        public S2CModList() {
            this.mods = ModList.get().getMods().stream().map(IModInfo::getModId).collect(Collectors.toList());
            this.channels = NetworkRegistry.buildChannelVersions();
            this.registries = RegistryManager.getRegistryNamesForSyncToClient();
            this.dataPackRegistries = List.copyOf(DataPackRegistriesHooks.getSyncedCustomRegistries());
        }

        private S2CModList(List<String> mods, Map<ResourceLocation, String> channels, List<ResourceLocation> registries, List<ResourceKey<? extends Registry<?>>> dataPackRegistries) {
            this.mods = mods;
            this.channels = channels;
            this.registries = registries;
            this.dataPackRegistries = dataPackRegistries;
        }

        public static S2CModList decode(FriendlyByteBuf input) {
            ArrayList<String> mods = new ArrayList<String>();
            int len = input.m_130242_();
            for (int x = 0; x < len; ++x) {
                mods.add(input.m_130136_(256));
            }
            HashMap<ResourceLocation, String> channels = new HashMap<ResourceLocation, String>();
            len = input.m_130242_();
            for (int x = 0; x < len; ++x) {
                channels.put(input.m_130281_(), input.m_130136_(256));
            }
            ArrayList<ResourceLocation> registries = new ArrayList<ResourceLocation>();
            len = input.m_130242_();
            for (int x = 0; x < len; ++x) {
                registries.add(input.m_130281_());
            }
            List dataPackRegistries = (List)input.m_236838_(ArrayList::new, buf -> ResourceKey.m_135788_((ResourceLocation)buf.m_130281_()));
            return new S2CModList(mods, channels, registries, dataPackRegistries);
        }

        public void encode(FriendlyByteBuf output) {
            output.m_130130_(this.mods.size());
            this.mods.forEach(m -> output.m_130072_(m, 256));
            output.m_130130_(this.channels.size());
            this.channels.forEach((k, v) -> {
                output.m_130085_(k);
                output.m_130072_(v, 256);
            });
            output.m_130130_(this.registries.size());
            this.registries.forEach(arg_0 -> ((FriendlyByteBuf)output).m_130085_(arg_0));
            Set<ResourceKey<? extends Registry<?>>> dataPackRegistries = DataPackRegistriesHooks.getSyncedCustomRegistries();
            output.m_236828_(dataPackRegistries, (buf, key) -> buf.m_130085_(key.m_135782_()));
        }

        public List<String> getModList() {
            return this.mods;
        }

        public List<ResourceLocation> getRegistries() {
            return this.registries;
        }

        public Map<ResourceLocation, String> getChannels() {
            return this.channels;
        }

        public List<ResourceKey<? extends Registry<?>>> getCustomDataPackRegistries() {
            return this.dataPackRegistries;
        }
    }

    static class LoginIndexedMessage
    implements IntSupplier {
        private int loginIndex;

        LoginIndexedMessage() {
        }

        void setLoginIndex(int loginIndex) {
            this.loginIndex = loginIndex;
        }

        int getLoginIndex() {
            return this.loginIndex;
        }

        @Override
        public int getAsInt() {
            return this.getLoginIndex();
        }
    }
}

