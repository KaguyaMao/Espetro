/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.platform.Mod
 *  dev.architectury.platform.Platform
 *  dev.architectury.utils.Env
 */
package dev.latvian.mods.kubejs.script;

import architectury_inject_KubeJS120_common_fed970bf692e482b87fece206d70e7a8_a84d70e99521d46269779bd2e09f32f2e9e510acf818188eef03ca9da5863685kubejs200165build26devjar.PlatformMethods;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PlatformWrapper {
    private static Map<String, ModInfo> allMods;

    public static String getName() {
        if (PlatformWrapper.isDevelopmentEnvironment()) {
            if (PlatformWrapper.isForge()) {
                return "forge";
            }
            if (PlatformWrapper.isFabric()) {
                return "fabric";
            }
            return "unknown (userdev?)";
        }
        return PlatformMethods.getCurrentTarget();
    }

    public static boolean isForge() {
        return Platform.isForge();
    }

    public static boolean isFabric() {
        return Platform.isFabric();
    }

    public static String getMcVersion() {
        return "1.20.1";
    }

    public static Set<String> getList() {
        return PlatformWrapper.getMods().keySet();
    }

    public static String getModVersion() {
        return KubeJS.thisMod.getVersion();
    }

    public static boolean isLoaded(String modId) {
        return PlatformWrapper.getMods().containsKey(modId);
    }

    public static ModInfo getInfo(String modID) {
        return PlatformWrapper.getMods().computeIfAbsent(modID, ModInfo::new);
    }

    public static Map<String, ModInfo> getMods() {
        if (allMods == null) {
            allMods = new LinkedHashMap<String, ModInfo>();
            for (Mod mod : Platform.getMods()) {
                ModInfo info = new ModInfo(mod.getModId());
                info.name = mod.getName();
                info.version = mod.getVersion();
                allMods.put(info.id, info);
            }
        }
        return allMods;
    }

    public static boolean isDevelopmentEnvironment() {
        return Platform.isDevelopmentEnvironment();
    }

    public static boolean isClientEnvironment() {
        return Platform.getEnvironment() == Env.CLIENT;
    }

    public static void setModName(String modId, String name) {
        PlatformWrapper.getInfo(modId).setName(name);
    }

    public static int getMinecraftVersion() {
        return 2001;
    }

    public static String getMinecraftVersionString() {
        return "1.20.1";
    }

    public static boolean isGeneratingData() {
        return MiscPlatformHelper.get().isDataGen();
    }

    public static void breakpoint(Object ... args) {
        KubeJS.LOGGER.info(Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(", ")));
    }

    public static class ModInfo {
        private final String id;
        private String name;
        private String version;
        private String customName;

        public ModInfo(String i) {
            this.name = this.id = i;
            this.version = "0.0.0";
            this.customName = "";
        }

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String n) {
            this.name = n;
            this.customName = n;
            MiscPlatformHelper.get().setModName(this, this.name);
        }

        public String getVersion() {
            return this.version;
        }

        public String getCustomName() {
            return this.customName;
        }
    }
}

