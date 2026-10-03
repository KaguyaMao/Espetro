/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.platform.Platform
 *  net.minecraft.server.packs.PackType
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package dev.latvian.mods.kubejs;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.CommonProperties;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.server.packs.PackType;
import org.apache.commons.lang3.mutable.MutableBoolean;

public interface KubeJSPaths {
    public static final MutableBoolean FIRST_RUN = new MutableBoolean(false);
    public static final Path DIRECTORY = KubeJSPaths.dir(Platform.getGameFolder().resolve("kubejs"), true);
    public static final Path DATA = KubeJSPaths.dir(DIRECTORY.resolve("data"));
    public static final Path ASSETS = KubeJSPaths.dir(DIRECTORY.resolve("assets"));
    public static final Path STARTUP_SCRIPTS = DIRECTORY.resolve("startup_scripts");
    public static final Path SERVER_SCRIPTS = DIRECTORY.resolve("server_scripts");
    public static final Path CLIENT_SCRIPTS = DIRECTORY.resolve("client_scripts");
    public static final Path CONFIG = KubeJSPaths.dir(DIRECTORY.resolve("config"));
    public static final Path COMMON_PROPERTIES = CONFIG.resolve("common.properties");
    public static final Path CLIENT_PROPERTIES = CONFIG.resolve("client.properties");
    public static final Path CONFIG_DEV_PROPERTIES = CONFIG.resolve("dev.properties");
    public static final Path PACKICON = CONFIG.resolve("packicon.png");
    public static final Path README = DIRECTORY.resolve("README.txt");
    public static final Path LOCAL = KubeJSPaths.dir(Platform.getGameFolder().resolve("local").resolve("kubejs"));
    public static final Path LOCAL_CACHE = KubeJSPaths.dir(LOCAL.resolve("cache"));
    public static final Path LOCAL_DEV_PROPERTIES = LOCAL.resolve("dev.properties");
    public static final Path EXPORT = KubeJSPaths.dir(LOCAL.resolve("export"));
    public static final Path EXPORTED_PACKS = KubeJSPaths.dir(LOCAL.resolve("exported_packs"));

    public static Path dir(Path dir, boolean markFirstRun) {
        if (Files.notExists(dir, new LinkOption[0])) {
            try {
                Files.createDirectories(dir, new FileAttribute[0]);
                if (markFirstRun) {
                    FIRST_RUN.setTrue();
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return dir;
    }

    public static Path dir(Path dir) {
        return KubeJSPaths.dir(dir, false);
    }

    public static Path get(PackType type) {
        return type == PackType.CLIENT_RESOURCES ? ASSETS : DATA;
    }

    public static Path getLocalDevProperties() {
        return CommonProperties.get().saveDevPropertiesInConfig ? CONFIG_DEV_PROPERTIES : LOCAL_DEV_PROPERTIES;
    }
}

