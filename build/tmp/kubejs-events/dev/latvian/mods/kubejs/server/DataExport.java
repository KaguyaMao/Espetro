/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  dev.architectury.platform.Mod
 *  dev.architectury.platform.Platform
 *  dev.architectury.registry.registries.Registrar
 *  dev.latvian.mods.rhino.mod.util.JsonUtils
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  net.minecraft.Util
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.core.Registry
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.Registrar;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DataExport {
    @HideFromJS
    public static DataExport export = null;
    public CommandSourceStack source;
    private final Map<String, Callable<byte[]>> exportedFiles = new ConcurrentHashMap<String, Callable<byte[]>>();

    public static void exportData() {
        if (export != null) {
            try {
                export.exportData0();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            export = null;
        }
    }

    private static <T> void addRegistry(JsonObject o, String name, Registrar<T> r) {
        JsonArray a = new JsonArray();
        for (ResourceLocation id : r.getIds()) {
            a.add(id.toString());
        }
        o.add(name, (JsonElement)a);
    }

    public void add(String path, Callable<byte[]> data) {
        try {
            this.exportedFiles.put(path, data);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addString(String path, String data) {
        this.add(path, () -> data.getBytes(StandardCharsets.UTF_8));
    }

    public void addJson(String path, JsonElement json) {
        this.add(path, () -> JsonUtils.toPrettyString((JsonElement)json).getBytes(StandardCharsets.UTF_8));
    }

    private void appendLine(StringBuilder sb, Calendar calendar, ConsoleLine line) {
        calendar.setTimeInMillis(line.timestamp);
        sb.append('[');
        UtilsJS.appendTimestamp(sb, calendar);
        sb.append(']');
        sb.append(' ');
        sb.append('[');
        sb.append((Object)line.type);
        sb.append(']');
        sb.append(' ');
        if (line.type.equals("ERROR")) {
            sb.append('!');
            sb.append(' ');
        }
        sb.append(line.getText());
        sb.append('\n');
    }

    private void exportData0() throws Exception {
        this.source.m_5894_().m_206193_().forEach(reg -> {
            ResourceKey key = reg.f_206233_();
            Registry registry = reg.f_206234_();
            JsonObject j = new JsonObject();
            for (Map.Entry entry : registry.m_6579_()) {
                j.addProperty(((ResourceKey)entry.getKey()).m_135782_().toString(), entry.getValue() == null ? "null" : entry.getValue().getClass().getName());
            }
            this.addJson("registries/" + key.m_135782_().m_135815_() + ".json", (JsonElement)j);
        });
        StringBuilder logStringBuilder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        for (ConsoleLine consoleLine : ConsoleJS.SERVER.errors) {
            this.appendLine(logStringBuilder, calendar, consoleLine);
        }
        if (logStringBuilder.length() > 0) {
            logStringBuilder.setLength(logStringBuilder.length() - 1);
            this.addString("errors.log", logStringBuilder.toString());
        }
        logStringBuilder.setLength(0);
        for (ConsoleLine consoleLine : ConsoleJS.SERVER.warnings) {
            this.appendLine(logStringBuilder, calendar, consoleLine);
        }
        if (logStringBuilder.length() > 0) {
            logStringBuilder.setLength(logStringBuilder.length() - 1);
            this.addString("warnings.log", logStringBuilder.toString());
        }
        JsonArray modArr = new JsonArray();
        for (Mod mod : Platform.getMods()) {
            JsonObject o = new JsonObject();
            o.addProperty("id", mod.getModId().trim());
            o.addProperty("name", mod.getName().trim());
            o.addProperty("version", mod.getVersion().trim());
            o.addProperty("description", mod.getDescription().trim());
            o.addProperty("authors", String.join((CharSequence)", ", mod.getAuthors()).trim());
            o.addProperty("homepage", mod.getHomepage().orElse("").trim());
            o.addProperty("sources", mod.getSources().orElse("").trim());
            o.addProperty("issue_tracker", mod.getIssueTracker().orElse("").trim());
            o.addProperty("license", mod.getLicense() == null ? "" : String.join((CharSequence)", ", mod.getLicense()).trim());
            o.entrySet().removeIf(e -> {
                JsonPrimitive p;
                Object patt4433$temp = e.getValue();
                return patt4433$temp instanceof JsonPrimitive && (p = (JsonPrimitive)patt4433$temp).isString() && p.getAsString().isEmpty();
            });
            modArr.add((JsonElement)o);
        }
        this.addJson("mods.json", (JsonElement)modArr);
        KubeJSPlugins.forEachPlugin(this, KubeJSPlugin::exportServerData);
        JsonArray jsonArray = new JsonArray();
        this.exportedFiles.keySet().stream().sorted(String.CASE_INSENSITIVE_ORDER).forEach(arg_0 -> ((JsonArray)jsonArray).add(arg_0));
        this.addJson("index.json", (JsonElement)jsonArray);
        Files.walk(KubeJSPaths.EXPORT, new FileVisitOption[0]).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        Files.createDirectory(KubeJSPaths.EXPORT, new FileAttribute[0]);
        CompletableFuture[] arr = new CompletableFuture[this.exportedFiles.size()];
        int i = 0;
        for (Map.Entry<String, Callable<byte[]>> entry : this.exportedFiles.entrySet()) {
            arr[i++] = CompletableFuture.runAsync(() -> {
                try {
                    Path path = KubeJSPaths.EXPORT.resolve(((String)entry.getKey()).replace(':', '/'));
                    Path parent = path.getParent();
                    if (Files.notExists(parent, new LinkOption[0])) {
                        Files.createDirectories(parent, new FileAttribute[0]);
                    }
                    if (Files.notExists(path, new LinkOption[0])) {
                        Files.createFile(path, new FileAttribute[0]);
                    }
                    Files.write(path, (byte[])((Callable)entry.getValue()).call(), new OpenOption[0]);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }, Util.m_183991_());
        }
        CompletableFuture.allOf(arr).join();
        if (this.source.m_81377_().m_129792_()) {
            this.source.m_288197_(() -> Component.m_237113_((String)"Done! Export in local/kubejs/export").kjs$clickOpenFile(KubeJSPaths.EXPORT.toAbsolutePath().toString()), false);
        } else {
            this.source.m_288197_(() -> Component.m_237113_((String)"Done! Export in local/kubejs/export"), false);
        }
    }
}

