/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableCollection
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package LOL_141.vehicle_addition.client.api;

import LOL_141.vehicle_addition.client.api.IAudioStreamHandler;
import LOL_141.vehicle_addition.client.api.implement.CnrM3u8Handler;
import LOL_141.vehicle_addition.client.api.implement.DirectHttpHandler;
import LOL_141.vehicle_addition.client.api.implement.M3u8Handler;
import LOL_141.vehicle_addition.client.api.implement.NetEaseHttpHandler;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AudioStreamHandlerManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static List<IAudioStreamHandler> HANDLERS = Lists.newArrayList();

    private AudioStreamHandlerManager() {
    }

    public static void init() {
        AudioStreamHandlerManager.registerHandler(new CnrM3u8Handler());
        AudioStreamHandlerManager.registerHandler(new M3u8Handler());
        AudioStreamHandlerManager.registerHandler(new DirectHttpHandler());
        AudioStreamHandlerManager.registerHandler(new NetEaseHttpHandler());
        HANDLERS.sort((h1, h2) -> Integer.compare(h2.getPriority(), h1.getPriority()));
        HANDLERS = ImmutableList.copyOf(HANDLERS);
    }

    public static void registerHandler(IAudioStreamHandler handler) {
        if (HANDLERS instanceof ImmutableCollection) {
            LOGGER.error("Failed to register audio stream handler {}, you should register it before the load complete event", (Object)handler.getClass().getName());
            return;
        }
        HANDLERS.add(handler);
    }

    public static AudioInputStream handle(URL url) throws UnsupportedAudioFileException, IOException {
        return AudioStreamHandlerManager.handle(url, 0L);
    }

    public static AudioInputStream handle(URL url, long startOffset) throws UnsupportedAudioFileException, IOException {
        for (IAudioStreamHandler handler : HANDLERS) {
            if (!handler.canHandle(url)) continue;
            return handler.handle(url, startOffset);
        }
        throw new UnsupportedAudioFileException("No handler found for URL: " + String.valueOf(url));
    }
}

