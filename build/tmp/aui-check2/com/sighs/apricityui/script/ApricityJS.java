/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.kubejs.KubeJS
 *  dev.latvian.mods.kubejs.script.ScriptManager
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.Script
 *  dev.latvian.mods.rhino.Scriptable
 *  net.minecraftforge.fml.ModList
 */
package com.sighs.apricityui.script;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.parser.JS;
import com.sighs.apricityui.util.AuiLog;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Script;
import dev.latvian.mods.rhino.Scriptable;
import net.minecraftforge.fml.ModList;

public class ApricityJS {
    private static final Object GLOBAL_SCRIPT_LOCK = new Object();
    private static final String DOCUMENT_UUID_BINDING = "__auiDocumentUuid";
    private static String cachedGlobalCode;
    private static Script cachedGlobalScript;

    public static void eval(String code) {
        ApricityJS.eval(code, null, "<global>");
    }

    public static void eval(String code, Event event) {
        ApricityJS.eval(code, event, "<inline>");
    }

    public static void eval(String code, Event event, String source) {
        if (!ApricityJS.isKubeJsLoaded()) {
            return;
        }
        if (code == null || code.isBlank()) {
            ApricityUI.LOGGER.warn("[AUI JS] empty script skipped source={}", (Object)AuiLog.source(source));
            return;
        }
        if (event != null) {
            // empty if block
        }
        code = JS.rewriteForRhino(code);
        ScriptManager manager = KubeJS.getClientScriptManager();
        Context context = manager.context;
        Scriptable top = manager.topLevelScope;
        Object previousEvent = null;
        boolean hadEvent = false;
        if (event != null) {
            previousEvent = top.get(context, "event", top);
            hadEvent = previousEvent != Scriptable.NOT_FOUND;
            top.put(context, "event", top, (Object)event);
        }
        try {
            context.evaluateString(top, code, AuiLog.source(source), 1, null);
        }
        catch (RuntimeException exception) {
            ApricityUI.LOGGER.error("[AUI JS] script execution failed source={} event={} code={}", new Object[]{AuiLog.source(source), event == null ? "<none>" : event.type, AuiLog.compact(code), exception});
            throw exception;
        }
        finally {
            if (event != null) {
                if (hadEvent) {
                    top.put(context, "event", top, previousEvent);
                } else {
                    top.delete(context, "event");
                }
            }
        }
    }

    public static void evalGlobal(String code, String documentUuid) {
        if (!ApricityJS.isKubeJsLoaded()) {
            return;
        }
        if (code == null || code.isBlank()) {
            return;
        }
        ScriptManager manager = KubeJS.getClientScriptManager();
        Scriptable top = manager.topLevelScope;
        Context context = manager.context;
        Object previousUuid = top.get(context, DOCUMENT_UUID_BINDING, top);
        boolean hadUuid = previousUuid != Scriptable.NOT_FOUND;
        top.put(context, DOCUMENT_UUID_BINDING, top, (Object)(documentUuid == null ? "" : documentUuid));
        try {
            ApricityJS.compiledGlobalScript(context, code).exec(context, top);
        }
        catch (RuntimeException exception) {
            ApricityUI.LOGGER.error("[AUI JS] global script execution failed document={} code={}", new Object[]{documentUuid, AuiLog.compact(code), exception});
            throw exception;
        }
        finally {
            if (hadUuid) {
                top.put(context, DOCUMENT_UUID_BINDING, top, previousUuid);
            } else {
                top.delete(context, DOCUMENT_UUID_BINDING);
            }
        }
    }

    public static void reload() {
        if (!ApricityJS.isKubeJsLoaded()) {
            return;
        }
        ApricityJS.clearGlobalScriptCache();
        try {
            KubeJS.PROXY.reloadClientInternal();
        }
        catch (RuntimeException exception) {
            ApricityUI.LOGGER.error("[AUI JS] KubeJS client script reload failed", (Throwable)exception);
            throw exception;
        }
    }

    public static void warmUp() {
        if (!ApricityJS.isKubeJsLoaded()) {
            return;
        }
        ScriptManager manager = KubeJS.getClientScriptManager();
        String globalJs = Loader.readGlobalJS();
        if (globalJs == null || globalJs.isBlank()) {
            return;
        }
        ApricityJS.compiledGlobalScript(manager.context, globalJs);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Script compiledGlobalScript(Context context, String code) {
        String prepared = ApricityJS.prepareGlobalCode(code);
        Object object = GLOBAL_SCRIPT_LOCK;
        synchronized (object) {
            if (cachedGlobalScript == null || !prepared.equals(cachedGlobalCode)) {
                cachedGlobalScript = context.compileString(prepared, "global.js", 1, null);
                cachedGlobalCode = prepared;
            }
            return cachedGlobalScript;
        }
    }

    private static String prepareGlobalCode(String code) {
        return JS.rewriteForRhino(code).replace("\"__AUI_DOCUMENT_UUID__\"", DOCUMENT_UUID_BINDING).replace("'__AUI_DOCUMENT_UUID__'", DOCUMENT_UUID_BINDING).replace("__AUI_DOCUMENT_UUID__", DOCUMENT_UUID_BINDING);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void clearGlobalScriptCache() {
        Object object = GLOBAL_SCRIPT_LOCK;
        synchronized (object) {
            cachedGlobalCode = null;
            cachedGlobalScript = null;
        }
    }

    private static boolean isKubeJsLoaded() {
        try {
            return ModList.get() != null && ModList.get().isLoaded("kubejs");
        }
        catch (LinkageError | RuntimeException unavailableForgeRuntime) {
            return false;
        }
    }
}

