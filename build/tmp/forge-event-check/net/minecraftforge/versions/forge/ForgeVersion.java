/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.Logging
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.VersionChecker
 *  net.minecraftforge.fml.VersionChecker$CheckResult
 *  net.minecraftforge.fml.VersionChecker$Status
 *  net.minecraftforge.fml.loading.FMLLoader
 *  net.minecraftforge.fml.loading.JarVersionLookupHandler
 *  net.minecraftforge.forgespi.language.IModInfo
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.versions.forge;

import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.JarVersionLookupHandler;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ForgeVersion {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "forge";
    private static final String forgeVersion;
    private static final String forgeSpec;
    private static final String forgeGroup;

    public static String getVersion() {
        return forgeVersion;
    }

    public static VersionChecker.Status getStatus() {
        return VersionChecker.getResult((IModInfo)((IModInfo)ModList.get().getModFileById(MOD_ID).getMods().get(0))).status();
    }

    @Nullable
    public static String getTarget() {
        VersionChecker.CheckResult res = VersionChecker.getResult((IModInfo)((IModInfo)ModList.get().getModFileById(MOD_ID).getMods().get(0)));
        return res.target() == null ? "" : res.target().toString();
    }

    public static String getSpec() {
        return forgeSpec;
    }

    public static String getGroup() {
        return forgeGroup;
    }

    static {
        LOGGER.debug(Logging.CORE, "Forge Version package {} from {}", (Object)ForgeVersion.class.getPackage(), (Object)ForgeVersion.class.getClassLoader());
        String vers = JarVersionLookupHandler.getImplementationVersion(ForgeVersion.class).orElse(FMLLoader.versionInfo().forgeVersion());
        if (vers == null) {
            throw new RuntimeException("Missing forge version, cannot continue");
        }
        String spec = JarVersionLookupHandler.getSpecificationVersion(ForgeVersion.class).orElse(System.getenv("FORGE_SPEC"));
        if (spec == null) {
            throw new RuntimeException("Missing forge spec, cannot continue");
        }
        String group = JarVersionLookupHandler.getImplementationTitle(ForgeVersion.class).orElse(FMLLoader.versionInfo().forgeGroup());
        if (group == null) {
            group = "net.minecraftforge";
        }
        forgeVersion = vers;
        forgeSpec = spec;
        forgeGroup = group;
        LOGGER.debug(Logging.CORE, "Found Forge version {}", (Object)forgeVersion);
        LOGGER.debug(Logging.CORE, "Found Forge spec {}", (Object)forgeSpec);
        LOGGER.debug(Logging.CORE, "Found Forge group {}", (Object)forgeGroup);
    }
}

