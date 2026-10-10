/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.log.TransformingThrowablePatternConverter
 *  joptsimple.internal.Strings
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.SystemReport
 *  net.minecraftforge.fml.CrashReportCallables
 *  net.minecraftforge.fml.ISystemReportExtender
 *  net.minecraftforge.fml.LoadingFailedException
 *  net.minecraftforge.forgespi.language.IModFileInfo
 *  net.minecraftforge.forgespi.language.IModInfo
 *  org.apache.logging.log4j.Logger
 */
package net.minecraftforge.logging;

import cpw.mods.modlauncher.log.TransformingThrowablePatternConverter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import joptsimple.internal.Strings;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.SystemReport;
import net.minecraftforge.fml.CrashReportCallables;
import net.minecraftforge.fml.ISystemReportExtender;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.forge.snapshots.ForgeSnapshotsMod;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.Logger;

public class CrashReportExtender {
    public static void extendSystemReport(SystemReport systemReport) {
        for (ISystemReportExtender call : CrashReportCallables.allCrashCallables()) {
            if (!call.isActive()) continue;
            systemReport.m_143522_(call.getLabel(), (Supplier)call);
        }
    }

    public static void addCrashReportHeader(StringBuilder stringbuilder, CrashReport crashReport) {
        ForgeSnapshotsMod.addCrashReportHeader(stringbuilder, crashReport);
    }

    public static String generateEnhancedStackTrace(Throwable throwable) {
        return CrashReportExtender.generateEnhancedStackTrace(throwable, true);
    }

    public static String generateEnhancedStackTrace(StackTraceElement[] stacktrace) {
        Throwable t = new Throwable();
        t.setStackTrace(stacktrace);
        return CrashReportExtender.generateEnhancedStackTrace(t, false);
    }

    public static String generateEnhancedStackTrace(Throwable throwable, boolean header) {
        String s = TransformingThrowablePatternConverter.generateEnhancedStackTrace((Throwable)throwable);
        return header ? s : s.substring(s.indexOf(Strings.LINE_SEPARATOR));
    }

    public static File dumpModLoadingCrashReport(Logger logger, LoadingFailedException error, File topLevelDir) {
        CrashReport crashReport = CrashReport.m_127521_((Throwable)new Exception("Mod Loading has failed"), (String)"Mod loading error has occurred");
        error.getErrors().forEach(mle -> {
            Throwable cause;
            Optional<IModInfo> modInfo = Optional.ofNullable(mle.getModInfo());
            CrashReportCategory category = crashReport.m_127514_(modInfo.map(iModInfo -> "MOD " + iModInfo.getModId()).orElse("NO MOD INFO AVAILABLE"));
            int depth = 0;
            for (cause = mle.getCause(); cause != null && cause.getCause() != null && cause.getCause() != cause; cause = cause.getCause()) {
                category.m_128159_("Caused by " + depth++, (Object)(String.valueOf(cause) + CrashReportExtender.generateEnhancedStackTrace(cause.getStackTrace()).replaceAll(Strings.LINE_SEPARATOR + "\t", "\n\t\t")));
            }
            if (cause != null) {
                category.applyStackTrace(cause);
            }
            category.m_128165_("Mod File", () -> modInfo.map(IModInfo::getOwningFile).map(t -> t.getFile().getFilePath().toUri().getPath()).orElse("NO FILE INFO"));
            category.m_128165_("Failure message", () -> mle.getCleanMessage().replace("\n", "\n\t\t"));
            category.m_128165_("Mod Version", () -> modInfo.map(IModInfo::getVersion).map(Object::toString).orElse("NO MOD INFO AVAILABLE"));
            category.m_128165_("Mod Issue URL", () -> modInfo.map(IModInfo::getOwningFile).map(IModFileInfo.class::cast).flatMap(mfi -> mfi.getConfig().getConfigElement(new String[]{"issueTrackerURL"})).orElse("NOT PROVIDED"));
            category.m_128159_("Exception message", (Object)Objects.toString(cause, "MISSING EXCEPTION MESSAGE"));
        });
        File file1 = new File(topLevelDir, "crash-reports");
        File file2 = new File(file1, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-fml.txt");
        if (crashReport.m_127512_(file2)) {
            logger.fatal("Crash report saved to {}", (Object)file2);
        } else {
            logger.fatal("Failed to save crash report");
        }
        System.out.print(crashReport.m_127526_());
        return file2;
    }
}

