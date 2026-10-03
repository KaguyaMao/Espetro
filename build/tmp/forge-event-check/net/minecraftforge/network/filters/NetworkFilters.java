/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelPipeline
 *  net.minecraft.network.Connection
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraftforge.network.filters;

import com.google.common.collect.ImmutableMap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.network.Connection;
import net.minecraftforge.network.filters.ForgeConnectionNetworkFilter;
import net.minecraftforge.network.filters.VanillaConnectionNetworkFilter;
import net.minecraftforge.network.filters.VanillaPacketFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkFilters {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, Function<Connection, VanillaPacketFilter>> instances = ImmutableMap.of((Object)"forge:vanilla_filter", manager -> new VanillaConnectionNetworkFilter(), (Object)"forge:forge_fixes", ForgeConnectionNetworkFilter::new);

    public static void injectIfNecessary(Connection manager) {
        ChannelPipeline pipeline = manager.channel().pipeline();
        if (pipeline.get("packet_handler") == null) {
            return;
        }
        instances.forEach((key, filterFactory) -> {
            VanillaPacketFilter filter = (VanillaPacketFilter)((Object)((Object)filterFactory.apply(manager)));
            if (filter.isNecessary(manager)) {
                pipeline.addBefore("packet_handler", key, (ChannelHandler)filter);
                LOGGER.debug("Injected {} into {}", (Object)filter, (Object)manager);
            }
        });
    }

    private NetworkFilters() {
    }
}

