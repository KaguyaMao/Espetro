/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.reflect.TypeToken
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonSyntaxException
 *  net.minecraftforge.fml.loading.FMLLoader
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

public final class UsernameCache {
    private static Map<UUID, String> map = new HashMap<UUID, String>();
    private static final Path saveFile = FMLLoader.getGamePath().resolve("usernamecache.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger(UsernameCache.class);
    private static final Marker USRCACHE = MarkerManager.getMarker((String)"USERNAMECACHE");

    private UsernameCache() {
    }

    protected static void setUsername(UUID uuid, String username) {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(username);
        if (username.equals(map.get(uuid))) {
            return;
        }
        map.put(uuid, username);
        UsernameCache.save();
    }

    protected static boolean removeUsername(UUID uuid) {
        Objects.requireNonNull(uuid);
        if (map.remove(uuid) != null) {
            UsernameCache.save();
            return true;
        }
        return false;
    }

    @Nullable
    public static String getLastKnownUsername(UUID uuid) {
        Objects.requireNonNull(uuid);
        return map.get(uuid);
    }

    public static boolean containsUUID(UUID uuid) {
        Objects.requireNonNull(uuid);
        return map.containsKey(uuid);
    }

    public static Map<UUID, String> getMap() {
        return ImmutableMap.copyOf(map);
    }

    protected static void save() {
        new SaveThread(gson.toJson(map)).start();
    }

    protected static void load() {
        if (!Files.exists(saveFile, new LinkOption[0])) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(saveFile, Charsets.UTF_8);){
            Type type = new TypeToken<Map<UUID, String>>(){}.getType();
            map = (Map)gson.fromJson((Reader)reader, type);
        }
        catch (JsonSyntaxException | IOException e) {
            LOGGER.error(USRCACHE, "Could not parse username cache file as valid json, deleting file {}", (Object)saveFile, (Object)e);
            try {
                Files.delete(saveFile);
            }
            catch (IOException e1) {
                LOGGER.error(USRCACHE, "Could not delete file {}", (Object)saveFile.toString());
            }
        }
        finally {
            if (map == null) {
                map = new HashMap<UUID, String>();
            }
        }
    }

    private static class SaveThread
    extends Thread {
        private final String data;

        public SaveThread(String data) {
            this.data = data;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                Path path = saveFile;
                synchronized (path) {
                    Files.write(saveFile, this.data.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
                }
            }
            catch (IOException e) {
                LOGGER.error(USRCACHE, "Failed to save username cache to file!", (Throwable)e);
            }
        }
    }
}

