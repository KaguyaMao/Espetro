/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package org.espetro.kubejs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.minecraftforge.fml.loading.FMLPaths;
import org.espetro.Espetro;

public final class EspetroKubeJSDefaultScripts {
    private static final String RESOURCE_ROOT = "/espetro_kubejs/";

    private EspetroKubeJSDefaultScripts() {
    }

    public static void ensureDefaultScripts() {
        EspetroKubeJSDefaultScripts.install("startup_scripts/00_espetro_drone_detection.js");
        EspetroKubeJSDefaultScripts.install("startup_scripts/00_espetro_artillery_155.js");
        EspetroKubeJSDefaultScripts.install("server_scripts/00_espetro_drone_detection.js");
        EspetroKubeJSDefaultScripts.install("server_scripts/00_espetro_artillery_155.js");
    }

    private static void install(String relativePath) {
        Path target = FMLPaths.GAMEDIR.get().resolve("kubejs").resolve(relativePath);
        try {
            if (Files.exists(target, new LinkOption[0])) {
                return;
            }
            String source = EspetroKubeJSDefaultScripts.readBundledScript(relativePath);
            Files.createDirectories(target.getParent(), new FileAttribute[0]);
            Files.writeString(target, (CharSequence)source, StandardCharsets.UTF_8, new OpenOption[0]);
            Espetro.LOGGER.info("\u5df2\u5199\u5165\u9ed8\u8ba4 KubeJS \u6307\u6325\u5b98\u6280\u80fd\u811a\u672c: {}", (Object)target);
        }
        catch (IOException e) {
            Espetro.LOGGER.warn("\u65e0\u6cd5\u5199\u5165\u9ed8\u8ba4 KubeJS \u6307\u6325\u5b98\u6280\u80fd\u811a\u672c: {}", (Object)target, (Object)e);
        }
    }

    private static String readBundledScript(String relativePath) throws IOException {
        String resourcePath = RESOURCE_ROOT + relativePath;
        try (InputStream stream = EspetroKubeJSDefaultScripts.class.getResourceAsStream(resourcePath);){
            if (stream == null) {
                throw new IOException("\u7f3a\u5c11\u5185\u7f6e\u811a\u672c\u8d44\u6e90: " + resourcePath);
            }
            String string = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return string;
        }
    }
}

