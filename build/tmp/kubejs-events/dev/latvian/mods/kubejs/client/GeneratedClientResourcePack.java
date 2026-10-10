/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  dev.architectury.platform.Platform
 *  dev.latvian.mods.rhino.mod.util.JsonUtils
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.FilePackResources
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.server.packs.PackType
 */
package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.client.GenerateClientAssetsEventJS;
import dev.latvian.mods.kubejs.client.LangEventJS;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.script.data.GeneratedResourcePack;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

public class GeneratedClientResourcePack
extends GeneratedResourcePack {
    public final Minecraft client;

    public static List<PackResources> inject(Minecraft client, List<PackResources> packs) {
        if (KubeJS.instance != null) {
            packs = new ArrayList<PackResources>(packs);
            int i = packs.size();
            for (int j = 1; j < packs.size(); ++j) {
                if (!(packs.get(j) instanceof FilePackResources)) continue;
                i = j;
                break;
            }
            ArrayList<GeneratedClientResourcePack> injected = new ArrayList<GeneratedClientResourcePack>(2);
            injected.add(new GeneratedClientResourcePack(client));
            for (File file : Objects.requireNonNull(KubeJSPaths.ASSETS.toFile().listFiles())) {
                if (!file.isFile() || !file.getName().endsWith(".zip")) continue;
                injected.add((GeneratedClientResourcePack)new FilePackResources(file.getName(), file, false));
            }
            packs.addAll(i, injected);
        }
        return packs;
    }

    public GeneratedClientResourcePack(Minecraft client) {
        super(PackType.CLIENT_RESOURCES);
        this.client = client;
        this.getGenerated();
    }

    @Override
    public void generate(Map<ResourceLocation, GeneratedData> map) {
        AssetJsonGenerator generator = new AssetJsonGenerator(map);
        for (BuilderBase<?> builder : RegistryInfo.ALL_BUILDERS) {
            builder.generateAssetJsons(generator);
        }
        KubeJSPlugins.forEachPlugin(generator, KubeJSPlugin::generateAssetJsons);
        HashMap<LangEventJS.Key, String> langMap = new HashMap<LangEventJS.Key, String>();
        HashMap<String, LangEventJS> langEvents = new HashMap<String, LangEventJS>();
        LangEventJS enUsLangEvent = langEvents.computeIfAbsent("en_us", s -> new LangEventJS((String)s, (Map<LangEventJS.Key, String>)langMap));
        if (Platform.isModLoaded((String)"jade")) {
            for (PlatformWrapper.ModInfo modInfo : PlatformWrapper.getMods().values()) {
                if (modInfo.getCustomName().isEmpty()) continue;
                enUsLangEvent.add("kubejs", "jade.modName." + modInfo.getId(), modInfo.getCustomName());
            }
        }
        for (BuilderBase builderBase : RegistryInfo.ALL_BUILDERS) {
            builderBase.generateLang(enUsLangEvent);
        }
        KubeJSPlugins.forEachPlugin(enUsLangEvent, KubeJSPlugin::generateLang);
        ClientEvents.HIGH_ASSETS.post(ScriptType.CLIENT, new GenerateClientAssetsEventJS(generator));
        for (Object object : ClientEvents.LANG.findUniqueExtraIds(ScriptType.CLIENT)) {
            String l = String.valueOf(object);
            if (LangEventJS.PATTERN.matcher(l).matches()) {
                ClientEvents.LANG.post((ScriptTypeHolder)ScriptType.CLIENT, (Object)l, langEvents.computeIfAbsent(l, k -> new LangEventJS((String)k, (Map<LangEventJS.Key, String>)langMap)));
                continue;
            }
            ConsoleJS.CLIENT.error("Invalid language key: " + l);
        }
        try {
            for (Path path : Files.list(KubeJSPaths.ASSETS).filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).toList()) {
                String ns = path.getFileName().toString();
                Path langDir = path.resolve("lang");
                if (!Files.exists(langDir, new LinkOption[0]) || !Files.isDirectory(langDir, new LinkOption[0])) continue;
                for (Path path2 : Files.list(langDir).filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).filter(Files::isReadable).toList()) {
                    String fileName = path2.getFileName().toString();
                    if (!fileName.endsWith(".json")) continue;
                    try {
                        BufferedReader reader = Files.newBufferedReader(path2);
                        try {
                            JsonObject json = (JsonObject)JsonUtils.GSON.fromJson((Reader)reader, JsonObject.class);
                            String lang = fileName.substring(0, fileName.length() - 5);
                            for (Map.Entry entry : json.entrySet()) {
                                langMap.put(new LangEventJS.Key(ns, lang, (String)entry.getKey()), ((JsonElement)entry.getValue()).getAsString());
                            }
                        }
                        finally {
                            if (reader == null) continue;
                            reader.close();
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        HashMap<String, Map> finalMap = new HashMap<String, Map>();
        for (Map.Entry entry : langMap.entrySet()) {
            Map ns = finalMap.computeIfAbsent(((LangEventJS.Key)entry.getKey()).namespace(), s -> new HashMap());
            JsonObject lang = ns.computeIfAbsent(((LangEventJS.Key)entry.getKey()).lang(), s -> new JsonObject());
            lang.addProperty(((LangEventJS.Key)entry.getKey()).key(), (String)entry.getValue());
        }
        for (Map.Entry e1 : finalMap.entrySet()) {
            for (Map.Entry e2 : ((Map)e1.getValue()).entrySet()) {
                generator.json(new ResourceLocation((String)e1.getKey() + ":lang/" + (String)e2.getKey()), (JsonElement)e2.getValue());
            }
        }
    }

    @Override
    protected boolean forgetFile(String path) {
        return super.forgetFile(path);
    }

    @Override
    protected boolean skipFile(GeneratedData data) {
        return data.id().m_135815_().startsWith("lang/");
    }
}

