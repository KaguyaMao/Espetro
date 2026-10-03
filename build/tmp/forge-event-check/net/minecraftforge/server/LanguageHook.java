/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.packs.resources.CloseableResourceManager
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.util.GsonHelper
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraftforge.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeI18n;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageHook {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static List<Map<String, String>> capturedTables = new ArrayList<Map<String, String>>(2);
    private static Map<String, String> modTable;

    public static void captureLanguageMap(Map<String, String> table) {
        capturedTables.add(table);
        if (modTable != null) {
            capturedTables.forEach(t -> t.putAll(modTable));
        }
    }

    private static void loadLocaleData(List<Resource> allResources) {
        allResources.forEach(res -> {
            try {
                LanguageHook.loadLocaleData(res.m_215507_());
            }
            catch (IOException iOException) {
                // empty catch block
            }
        });
    }

    private static void loadLocaleData(InputStream inputstream) {
        try {
            JsonElement jsonelement = (JsonElement)GSON.fromJson((Reader)new InputStreamReader(inputstream, StandardCharsets.UTF_8), JsonElement.class);
            JsonObject jsonobject = GsonHelper.m_13918_((JsonElement)jsonelement, (String)"strings");
            jsonobject.entrySet().forEach(entry -> {
                String s = PATTERN.matcher(GsonHelper.m_13805_((JsonElement)((JsonElement)entry.getValue()), (String)((String)entry.getKey()))).replaceAll("%$1s");
                modTable.put((String)entry.getKey(), s);
            });
        }
        finally {
            IOUtils.closeQuietly((InputStream)inputstream);
        }
    }

    private static void loadLanguage(String langName, MinecraftServer server) {
        String langFile = String.format(Locale.ROOT, "lang/%s.json", langName);
        CloseableResourceManager resourceManager = server.getServerResources().f_206584_();
        resourceManager.m_7187_().forEach(arg_0 -> LanguageHook.lambda$loadLanguage$3(langFile, (ResourceManager)resourceManager, arg_0));
    }

    public static void loadForgeAndMCLangs() {
        modTable = new HashMap<String, String>(5000);
        InputStream mc = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/minecraft/lang/en_us.json");
        InputStream forge = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/forge/lang/en_us.json");
        LanguageHook.loadLocaleData(mc);
        LanguageHook.loadLocaleData(forge);
        capturedTables.forEach(t -> t.putAll(modTable));
        ForgeI18n.loadLanguageData(modTable);
    }

    static void loadLanguagesOnServer(MinecraftServer server) {
        modTable = new HashMap<String, String>(5000);
        for (String lang : Arrays.asList("en_us")) {
            LanguageHook.loadLanguage(lang, server);
        }
        capturedTables.forEach(t -> t.putAll(modTable));
        ForgeI18n.loadLanguageData(modTable);
    }

    private static /* synthetic */ void lambda$loadLanguage$3(String langFile, ResourceManager resourceManager, String namespace) {
        try {
            ResourceLocation langResource = new ResourceLocation(namespace, langFile);
            LanguageHook.loadLocaleData(resourceManager.m_213829_(langResource));
        }
        catch (Exception exception) {
            LOGGER.warn("Skipped language file: {}:{}", (Object)namespace, (Object)langFile, (Object)exception);
        }
    }
}

