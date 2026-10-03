/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  javax.annotation.Nullable
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.loading.FMLLoader
 *  net.minecraftforge.forgespi.language.IModInfo
 *  org.slf4j.Logger
 *  org.spongepowered.asm.mixin.extensibility.IMixinInfo
 *  org.spongepowered.asm.mixin.transformer.ClassInfo
 *  org.spongepowered.asm.mixin.transformer.meta.MixinMerged
 */
package net.minecraftforge.logging;

import com.mojang.logging.LogUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IModInfo;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;

public final class CrashReportAnalyser {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, IModInfo> PACKAGE_MOD_CACHE = new HashMap<String, IModInfo>();
    private static final Map<IModInfo, String[]> SUSPECTED_MODS = new HashMap<IModInfo, String[]>();

    private CrashReportAnalyser() {
    }

    public static String appendSuspectedMods(Throwable throwable, StackTraceElement[] uncategorizedStackTrace) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            CrashReportAnalyser.cacheModList();
            CrashReportAnalyser.analyseCrashReport(throwable, uncategorizedStackTrace);
            CrashReportAnalyser.buildSuspectedModsSection(stringBuilder);
        }
        catch (Throwable t) {
            LOGGER.error("Failed to append suspected mod(s) to crash report!", t);
        }
        return stringBuilder.toString();
    }

    private static void analyseCrashReport(Throwable throwable, StackTraceElement[] uncategorizedStackTrace) {
        CrashReportAnalyser.scanThrowable(throwable);
        CrashReportAnalyser.scanStacktrace(uncategorizedStackTrace);
    }

    private static void buildSuspectedModsSection(StringBuilder stringBuilder) {
        stringBuilder.append("Suspected Mod");
        stringBuilder.append(SUSPECTED_MODS.size() == 1 ? ": " : "s: ");
        if (SUSPECTED_MODS.isEmpty()) {
            stringBuilder.append("NONE\n");
        } else {
            SUSPECTED_MODS.forEach((iModInfo, position) -> {
                stringBuilder.append("\n\t").append(iModInfo.getDisplayName()).append(" (").append(iModInfo.getModId()).append("),").append(" Version: ").append(iModInfo.getVersion());
                iModInfo.getOwningFile().getConfig().getConfigElement(new String[]{"issueTrackerURL"}).ifPresent(issuesLink -> stringBuilder.append("\n\t\t").append("Issue tracker URL: ").append((String)issuesLink));
                stringBuilder.append("\n\t\t");
                for (String s : position) {
                    stringBuilder.append(s);
                }
                stringBuilder.append("\n");
            });
            SUSPECTED_MODS.clear();
        }
    }

    private static void scanThrowable(Throwable throwable) {
        CrashReportAnalyser.scanStacktrace(throwable.getStackTrace());
        if (throwable.getCause() != null && throwable.getCause() != throwable) {
            CrashReportAnalyser.scanThrowable(throwable.getCause());
        }
    }

    private static void scanStacktrace(StackTraceElement[] stackTrace) {
        for (StackTraceElement stackTraceElement : stackTrace) {
            CrashReportAnalyser.identifyByClass(stackTraceElement);
            CrashReportAnalyser.identifyByMixins(stackTraceElement);
        }
    }

    private static void cacheModList() {
        ModList modList = ModList.get();
        ModuleLayer gameLayer = FMLLoader.getGameLayer();
        if (modList != null) {
            modList.getMods().forEach(iModInfo -> {
                if (!iModInfo.getModId().equals("forge") && !iModInfo.getModId().equals("minecraft")) {
                    HashSet packages = new HashSet();
                    gameLayer.findModule(iModInfo.getModId()).ifPresent(module -> packages.addAll(module.getPackages()));
                    packages.forEach(s -> PACKAGE_MOD_CACHE.put((String)s, (IModInfo)iModInfo));
                }
            });
        }
    }

    private static void identifyByClass(StackTraceElement stackTraceElement) {
        CrashReportAnalyser.blameIfPresent(stackTraceElement);
    }

    private static void identifyByMixins(StackTraceElement stackTraceElement) {
        IMixinInfo mixinInfo = CrashReportAnalyser.getMixinInfo(stackTraceElement);
        if (mixinInfo != null) {
            String elementAsString = stackTraceElement.toString();
            String mixinClassName = mixinInfo.getClassName();
            List targetClasses = mixinInfo.getTargetClasses();
            CrashReportAnalyser.blameIfPresent(mixinClassName, "Mixin class: ", mixinClassName, "\n\t\tTarget", (targetClasses.size() == 1 ? ": " + (String)targetClasses.get(0) : "s: " + String.valueOf(targetClasses)).replaceAll("/", "."), "\n\t\tat ", elementAsString);
        }
    }

    private static void blameIfPresent(StackTraceElement stackTraceElement) {
        CrashReportAnalyser.blameIfPresent(stackTraceElement.getClassName(), "at ", stackTraceElement.toString());
    }

    private static void blameIfPresent(String className, String ... position) {
        String commonPackage = CrashReportAnalyser.findMatchingPackage(className);
        if (commonPackage != null) {
            SUSPECTED_MODS.putIfAbsent(PACKAGE_MOD_CACHE.get(commonPackage), position);
        }
    }

    @Nullable
    private static String findMatchingPackage(String className) {
        for (String s : PACKAGE_MOD_CACHE.keySet()) {
            if (!className.startsWith(s)) continue;
            return s;
        }
        return null;
    }

    @Nullable
    private static MixinMerged findMixinMerged(StackTraceElement element) {
        try {
            Method[] methods;
            Class<?> clazz = Class.forName(element.getClassName());
            for (Method method : methods = clazz.getDeclaredMethods()) {
                MixinMerged mixinMerged;
                if (!method.getName().equals(element.getMethodName()) || (mixinMerged = method.getAnnotation(MixinMerged.class)) == null) continue;
                return mixinMerged;
            }
        }
        catch (ClassNotFoundException | NoClassDefFoundError throwable) {
            // empty catch block
        }
        return null;
    }

    @Nullable
    private static IMixinInfo getMixinInfo(StackTraceElement element) {
        ClassInfo classInfo;
        MixinMerged mixinMerged = CrashReportAnalyser.findMixinMerged(element);
        if (mixinMerged != null && (classInfo = ClassInfo.forName((String)mixinMerged.mixin().replace('.', '/'))) != null) {
            try {
                Field mixinField = ClassInfo.class.getDeclaredField("mixin");
                mixinField.setAccessible(true);
                return (IMixinInfo)mixinField.get(classInfo);
            }
            catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}

