/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  io.netty.buffer.Unpooled
 *  io.netty.util.Attribute
 *  net.minecraft.ResourceLocationException
 *  net.minecraft.network.Connection
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.commons.lang3.tuple.Pair
 *  org.slf4j.Logger
 */
package net.minecraftforge.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import io.netty.util.Attribute;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ICustomPacket;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

public class MCRegisterPacketHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MCRegisterPacketHandler INSTANCE = new MCRegisterPacketHandler();

    public void addChannels(Set<ResourceLocation> locations, Connection manager) {
        MCRegisterPacketHandler.getFrom((Connection)manager).locations.addAll(locations);
    }

    void registerListener(NetworkEvent evt) {
        ChannelList channelList = MCRegisterPacketHandler.getFrom(evt);
        channelList.updateFrom(evt.getSource(), evt.getPayload(), NetworkEvent.RegistrationChangeType.REGISTER);
        evt.getSource().get().setPacketHandled(true);
    }

    void unregisterListener(NetworkEvent evt) {
        ChannelList channelList = MCRegisterPacketHandler.getFrom(evt);
        channelList.updateFrom(evt.getSource(), evt.getPayload(), NetworkEvent.RegistrationChangeType.UNREGISTER);
        evt.getSource().get().setPacketHandled(true);
    }

    private static ChannelList getFrom(Connection manager) {
        return MCRegisterPacketHandler.fromAttr((Attribute<ChannelList>)manager.channel().attr(NetworkConstants.FML_MC_REGISTRY));
    }

    private static ChannelList getFrom(NetworkEvent event) {
        return MCRegisterPacketHandler.fromAttr(event.getSource().get().attr(NetworkConstants.FML_MC_REGISTRY));
    }

    private static ChannelList fromAttr(Attribute<ChannelList> attr) {
        attr.setIfAbsent((Object)new ChannelList());
        return (ChannelList)attr.get();
    }

    public void sendRegistry(Connection manager, NetworkDirection dir) {
        FriendlyByteBuf pb = new FriendlyByteBuf(Unpooled.buffer());
        pb.writeBytes(MCRegisterPacketHandler.getFrom(manager).toByteArray());
        ICustomPacket iPacketICustomPacket = dir.buildPacket((Pair<FriendlyByteBuf, Integer>)Pair.of((Object)pb, (Object)0), NetworkConstants.MC_REGISTER_RESOURCE);
        manager.m_129512_(iPacketICustomPacket.getThis());
    }

    public static class ChannelList {
        private Set<ResourceLocation> locations = new HashSet<ResourceLocation>();
        private Set<ResourceLocation> remoteLocations = Set.of();

        public void updateFrom(Supplier<NetworkEvent.Context> source, FriendlyByteBuf buffer, NetworkEvent.RegistrationChangeType changeType) {
            byte[] data = new byte[Math.max(buffer.readableBytes(), 0)];
            buffer.readBytes(data);
            Set<ResourceLocation> oldLocations = this.locations;
            this.locations = this.bytesToResLocation(data);
            this.remoteLocations = Set.copyOf(this.locations);
            oldLocations.addAll(this.locations);
            oldLocations.stream().map(NetworkRegistry::findTarget).filter(Optional::isPresent).map(Optional::get).forEach(t -> t.dispatchEvent(new NetworkEvent.ChannelRegistrationChangeEvent(source, changeType)));
        }

        byte[] toByteArray() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (ResourceLocation rl : this.locations) {
                try {
                    bos.write(rl.toString().getBytes(StandardCharsets.UTF_8));
                    bos.write(0);
                }
                catch (IOException iOException) {}
            }
            return bos.toByteArray();
        }

        private Set<ResourceLocation> bytesToResLocation(byte[] all) {
            HashSet<ResourceLocation> rl = new HashSet<ResourceLocation>();
            int last = 0;
            for (int cur = 0; cur < all.length; ++cur) {
                if (all[cur] != 0) continue;
                String s = new String(all, last, cur - last, StandardCharsets.UTF_8);
                try {
                    rl.add(new ResourceLocation(s));
                }
                catch (ResourceLocationException ex) {
                    LOGGER.warn("Invalid channel name received: {}. Ignoring", (Object)s);
                }
                last = cur + 1;
            }
            return rl;
        }

        public Set<ResourceLocation> getRemoteLocations() {
            return this.remoteLocations;
        }
    }
}

