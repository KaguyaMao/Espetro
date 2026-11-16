package org.espetro.client.ui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/** Installs the shipped faction/map previews at the paths used by existing screens. */
public final class BundledPreviewInstaller {
    private static final String BASE = "espetro_previews/";
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BundledPreviewInstaller.class);
    private static boolean installed;

    private BundledPreviewInstaller() { }

    public static synchronized void installForClient(Path gameDirectory) {
        if (installed) return;
        try {
            BundledMenuInstaller.Result result = install(gameDirectory, name -> {
                String resource;
                if (name.equals("manifest.tsv")) {
                    resource = BASE + name;
                } else if (name.startsWith("files/")) {
                    resource = resourcePath(name.substring("files/".length()));
                } else {
                    throw new IOException("Invalid bundled preview resource: " + name);
                }
                InputStream input = BundledPreviewInstaller.class.getClassLoader().getResourceAsStream(resource);
                if (input == null) throw new IOException("Missing bundled preview resource: " + resource);
                return input;
            });
            installed = true;
            LOGGER.info("Espetro preview bundle: {} images updated; previous files saved at {}",
                result.updatedFiles(), result.backupDirectory());
        } catch (IOException | RuntimeException failure) {
            LOGGER.error("Unable to install Espetro preview images; previous images retained", failure);
        }
    }

    static BundledMenuInstaller.Result install(Path gameDirectory, BundledMenuInstaller.Resources resources)
            throws IOException {
        return BundledMenuInstaller.install(gameDirectory, resources, BundledPreviewInstaller::allowedPath);
    }

    static boolean allowedPath(String path) {
        if (path == null || !(path.startsWith("EsFactions/") || path.startsWith("EsWorld/"))) return false;
        // These are image directories, never an alternative configuration/world installer.
        return path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg");
    }

    static String resourcePath(String path) throws IOException {
        if (!allowedPath(path) || path.contains("\\") || path.contains(":") || path.contains("//")) {
            throw new IOException("Invalid bundled preview path: " + path);
        }
        for (String segment : path.split("/", -1)) {
            if (segment.isEmpty() || segment.equals(".") || segment.equals("..")) {
                throw new IOException("Invalid bundled preview path: " + path);
            }
        }
        if (path.startsWith("EsFactions/")) {
            // One physical copy also serves existing espetro:textures/gui/factions/... references.
            return "assets/espetro/textures/gui/factions/" + path.substring("EsFactions/".length());
        }
        // Keep original Unicode names outside the ResourceLocation namespace.
        return BASE + "files/" + path;
    }
}
