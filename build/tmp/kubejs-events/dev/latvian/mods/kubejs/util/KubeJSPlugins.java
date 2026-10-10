/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.platform.Mod
 *  dev.architectury.platform.Platform
 */
package dev.latvian.mods.kubejs.util;

import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.kubejs.util.ModResourceBindings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class KubeJSPlugins {
    private static final List<KubeJSPlugin> LIST = new ArrayList<KubeJSPlugin>();
    private static final List<String> GLOBAL_CLASS_FILTER = new ArrayList<String>();
    private static final ModResourceBindings BINDINGS = new ModResourceBindings();

    public static void load(List<Mod> mods, boolean loadClientPlugins) {
        try {
            for (Mod mod : mods) {
                KubeJSPlugins.load(mod, loadClientPlugins);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to load KubeJS plugin", ex);
        }
    }

    private static void load(Mod mod, boolean loadClientPlugins) throws IOException {
        Optional pc;
        Optional pp = mod.findResource(new String[]{"kubejs.plugins.txt"});
        if (pp.isPresent()) {
            KubeJSPlugins.loadFromFile(Files.lines((Path)pp.get()), mod.getModId(), loadClientPlugins);
        }
        if ((pc = mod.findResource(new String[]{"kubejs.classfilter.txt"})).isPresent()) {
            GLOBAL_CLASS_FILTER.addAll(Files.readAllLines((Path)pc.get()));
        }
        BINDINGS.readBindings(mod);
    }

    private static void loadFromFile(Stream<String> contents, String source, boolean loadClientPlugins) {
        KubeJS.LOGGER.info("Found plugin source {}", (Object)source);
        contents.map(s -> s.split("#", 2)[0].trim()).filter(s -> !s.isBlank()).flatMap(s -> {
            String[] line = s.split(" ");
            for (int i = 1; i < line.length; ++i) {
                if (line[i].equalsIgnoreCase("client")) {
                    if (loadClientPlugins) continue;
                    if (DevProperties.get().logSkippedPlugins) {
                        KubeJS.LOGGER.warn("Plugin " + line[0] + " does not load on server side, skipping");
                    }
                    return Stream.empty();
                }
                if (Platform.isModLoaded((String)line[i])) continue;
                if (DevProperties.get().logSkippedPlugins) {
                    KubeJS.LOGGER.warn("Plugin " + line[0] + " does not have required mod " + line[i] + " loaded, skipping");
                }
                return Stream.empty();
            }
            try {
                return Stream.of(Class.forName(line[0]));
            }
            catch (Throwable t) {
                KubeJS.LOGGER.error("Failed to load plugin {} from source {}: {}", new Object[]{s, source, t});
                t.printStackTrace();
                return Stream.empty();
            }
        }).filter(KubeJSPlugin.class::isAssignableFrom).forEach(c -> {
            try {
                LIST.add((KubeJSPlugin)c.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
            }
            catch (Throwable t) {
                KubeJS.LOGGER.error("Failed to init KubeJS plugin {} from source {}: {}", new Object[]{c.getName(), source, t});
            }
        });
    }

    public static ClassFilter createClassFilter(ScriptType type) {
        ClassFilter filter = new ClassFilter();
        for (KubeJSPlugin plugin : LIST) {
            plugin.registerClasses(type, filter);
        }
        for (String s : GLOBAL_CLASS_FILTER) {
            if (s.length() < 2) continue;
            if (s.startsWith("+")) {
                filter.allow(s.substring(1));
                continue;
            }
            if (!s.startsWith("-")) continue;
            filter.deny(s.substring(1));
        }
        return filter;
    }

    public static void forEachPlugin(Consumer<KubeJSPlugin> callback) {
        LIST.forEach(callback);
    }

    public static <T> void forEachPlugin(T instance, BiConsumer<KubeJSPlugin, T> callback) {
        for (KubeJSPlugin item : LIST) {
            callback.accept(item, (KubeJSPlugin)instance);
        }
    }

    public static List<KubeJSPlugin> getAll() {
        return Collections.unmodifiableList(LIST);
    }

    public static void addSidedBindings(BindingsEvent event) {
        BINDINGS.addBindings(event);
    }
}

