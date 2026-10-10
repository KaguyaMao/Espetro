/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  net.minecraft.Util
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.fml.IExtensionPoint$DisplayTest
 *  net.minecraftforge.fml.ModList
 */
package net.minecraftforge.network;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkRegistry;

public record ServerStatusPing(Map<ResourceLocation, ChannelData> channels, Map<String, String> mods, int fmlNetworkVer, boolean truncated) {
    private static final Codec<ByteBuf> BYTE_BUF_CODEC = Codec.STRING.xmap(ServerStatusPing::decodeOptimized, ServerStatusPing::encodeOptimized);
    public static final Codec<ServerStatusPing> CODEC = RecordCodecBuilder.create(in -> in.group((App)Codec.INT.fieldOf("fmlNetworkVersion").forGetter(ServerStatusPing::getFMLNetworkVersion), (App)BYTE_BUF_CODEC.optionalFieldOf("d").forGetter(ping -> Optional.of(ping.toBuf())), (App)ChannelData.CODEC.listOf().optionalFieldOf("channels").forGetter(ping -> Optional.of(List.of())), (App)ModInfo.CODEC.listOf().optionalFieldOf("mods").forGetter(ping -> Optional.of(List.of())), (App)Codec.BOOL.optionalFieldOf("truncated").forGetter(ping -> Optional.of(ping.isTruncated()))).apply((Applicative)in, (fmlVer, buf, channels, mods, truncated) -> buf.map(byteBuf -> ServerStatusPing.deserializeOptimized(fmlVer, byteBuf)).orElseGet(() -> new ServerStatusPing(channels.orElseGet(List::of).stream().collect(Collectors.toMap(ChannelData::res, Function.identity())), mods.orElseGet(List::of).stream().collect(Collectors.toMap(ModInfo::modId, ModInfo::modmarker)), (int)fmlVer, truncated.orElse(false)))));
    private static final int VERSION_FLAG_IGNORESERVERONLY = 1;

    public ServerStatusPing() {
        this(NetworkRegistry.buildChannelVersionsForListPing(), (Map)Util.m_137469_(new HashMap(), map -> ModList.get().forEachModContainer((modid, mc) -> map.put(modid, mc.getCustomExtension(IExtensionPoint.DisplayTest.class).map(IExtensionPoint.DisplayTest::suppliedVersion).map(Supplier::get).orElse("OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31")))), 3, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerStatusPing)) {
            return false;
        }
        ServerStatusPing that = (ServerStatusPing)o;
        return this.fmlNetworkVer == that.fmlNetworkVer && this.channels.equals(that.channels) && this.mods.equals(that.mods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.channels, this.mods, this.fmlNetworkVer);
    }

    private List<Map.Entry<ResourceLocation, ChannelData>> getChannelsForMod(String modId) {
        return this.channels.entrySet().stream().filter(c -> ((ResourceLocation)c.getKey()).m_135827_().equals(modId)).toList();
    }

    private List<Map.Entry<ResourceLocation, ChannelData>> getNonModChannels() {
        return this.channels.entrySet().stream().filter(c -> !this.mods.containsKey(((ResourceLocation)c.getKey()).m_135827_())).toList();
    }

    public ByteBuf toBuf() {
        boolean reachedSizeLimit = false;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(false);
        buf.writeShort(this.mods.size());
        int writtenCount = 0;
        for (Map.Entry<String, String> modEntry : this.mods.entrySet()) {
            boolean isIgnoreServerOnly = modEntry.getValue().equals("OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31");
            List<Map.Entry<ResourceLocation, ChannelData>> channelsForMod = this.getChannelsForMod(modEntry.getKey());
            int channelSizeAndVersionFlag = channelsForMod.size() << 1;
            if (isIgnoreServerOnly) {
                channelSizeAndVersionFlag |= 1;
            }
            buf.m_130130_(channelSizeAndVersionFlag);
            buf.m_130070_(modEntry.getKey());
            if (!isIgnoreServerOnly) {
                buf.m_130070_(modEntry.getValue());
            }
            for (Map.Entry<ResourceLocation, ChannelData> entry : channelsForMod) {
                buf.m_130070_(entry.getKey().m_135815_());
                buf.m_130070_(entry.getValue().version());
                buf.writeBoolean(entry.getValue().required());
            }
            ++writtenCount;
            if (buf.readableBytes() < 60000) continue;
            reachedSizeLimit = true;
            break;
        }
        if (!reachedSizeLimit) {
            List<Map.Entry<ResourceLocation, ChannelData>> nonModChannels = this.getNonModChannels();
            buf.m_130130_(nonModChannels.size());
            for (Map.Entry<ResourceLocation, ChannelData> entry : nonModChannels) {
                buf.m_130085_(entry.getKey());
                buf.m_130070_(entry.getValue().version());
                buf.writeBoolean(entry.getValue().required());
            }
        } else {
            buf.setShort(1, writtenCount);
            buf.m_130130_(0);
        }
        buf.setBoolean(0, reachedSizeLimit);
        return buf;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static ServerStatusPing deserializeOptimized(int fmlNetworkVersion, ByteBuf bbuf) {
        HashMap<ResourceLocation, ChannelData> channels;
        HashMap<String, String> mods;
        boolean truncated;
        FriendlyByteBuf buf = new FriendlyByteBuf(bbuf);
        try {
            truncated = buf.readBoolean();
            int modsSize = buf.readUnsignedShort();
            mods = new HashMap<String, String>();
            channels = new HashMap<ResourceLocation, ChannelData>();
            for (int i = 0; i < modsSize; ++i) {
                int channelSizeAndVersionFlag = buf.m_130242_();
                int channelSize = channelSizeAndVersionFlag >>> 1;
                boolean isIgnoreServerOnly = (channelSizeAndVersionFlag & 1) != 0;
                String modId = buf.m_130277_();
                String modVersion = isIgnoreServerOnly ? "OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31" : buf.m_130277_();
                for (int i1 = 0; i1 < channelSize; ++i1) {
                    String channelName = buf.m_130277_();
                    String channelVersion = buf.m_130277_();
                    boolean requiredOnClient = buf.readBoolean();
                    ResourceLocation id = new ResourceLocation(modId, channelName);
                    channels.put(id, new ChannelData(id, channelVersion, requiredOnClient));
                }
                mods.put(modId, modVersion);
            }
            int nonModChannelCount = buf.m_130242_();
            for (int i = 0; i < nonModChannelCount; ++i) {
                ResourceLocation channelName = buf.m_130281_();
                String channelVersion = buf.m_130277_();
                boolean requiredOnClient = buf.readBoolean();
                channels.put(channelName, new ChannelData(channelName, channelVersion, requiredOnClient));
            }
        }
        finally {
            buf.release();
        }
        return new ServerStatusPing(channels, mods, fmlNetworkVersion, truncated);
    }

    private static String encodeOptimized(ByteBuf buf) {
        char c;
        int byteLength = buf.readableBytes();
        StringBuilder sb = new StringBuilder();
        sb.append((char)(byteLength & Short.MAX_VALUE));
        sb.append((char)(byteLength >>> 15 & Short.MAX_VALUE));
        int buffer = 0;
        int bitsInBuf = 0;
        while (buf.isReadable()) {
            if (bitsInBuf >= 15) {
                c = (char)(buffer & Short.MAX_VALUE);
                sb.append(c);
                buffer >>>= 15;
                bitsInBuf -= 15;
            }
            short b = buf.readUnsignedByte();
            buffer |= b << bitsInBuf;
            bitsInBuf += 8;
        }
        buf.release();
        if (bitsInBuf > 0) {
            c = (char)(buffer & Short.MAX_VALUE);
            sb.append(c);
        }
        return sb.toString();
    }

    private static ByteBuf decodeOptimized(String s) {
        char size0 = s.charAt(0);
        char size1 = s.charAt(1);
        int size = size0 | size1 << 15;
        ByteBuf buf = Unpooled.buffer((int)size);
        int buffer = 0;
        int bitsInBuf = 0;
        for (int stringIndex = 2; stringIndex < s.length(); ++stringIndex) {
            while (bitsInBuf >= 8) {
                buf.writeByte(buffer);
                buffer >>>= 8;
                bitsInBuf -= 8;
            }
            char c = s.charAt(stringIndex);
            buffer |= (c & Short.MAX_VALUE) << bitsInBuf;
            bitsInBuf += 15;
        }
        while (buf.readableBytes() < size) {
            buf.writeByte(buffer);
            buffer >>>= 8;
            bitsInBuf -= 8;
        }
        return buf;
    }

    public Map<ResourceLocation, ChannelData> getRemoteChannels() {
        return this.channels;
    }

    public Map<String, String> getRemoteModData() {
        return this.mods;
    }

    public int getFMLNetworkVersion() {
        return this.fmlNetworkVer;
    }

    public boolean isTruncated() {
        return this.truncated;
    }

    public record ChannelData(ResourceLocation res, String version, boolean required) {
        public static final Codec<ChannelData> CODEC = RecordCodecBuilder.create(in -> in.group((App)ResourceLocation.f_135803_.fieldOf("res").forGetter(ChannelData::res), (App)Codec.STRING.fieldOf("version").forGetter(ChannelData::version), (App)Codec.BOOL.fieldOf("required").forGetter(ChannelData::required)).apply((Applicative)in, ChannelData::new));
    }

    public record ModInfo(String modId, String modmarker) {
        public static final Codec<ModInfo> CODEC = RecordCodecBuilder.create(in -> in.group((App)Codec.STRING.fieldOf("modId").forGetter(ModInfo::modId), (App)Codec.STRING.fieldOf("modmarker").forGetter(ModInfo::modmarker)).apply((Applicative)in, ModInfo::new));
    }
}

