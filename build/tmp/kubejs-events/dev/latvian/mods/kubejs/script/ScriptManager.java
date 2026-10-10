/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  dev.latvian.mods.rhino.ClassShutter
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.NativeJavaClass
 *  dev.latvian.mods.rhino.NativeObject
 *  dev.latvian.mods.rhino.Scriptable
 *  dev.latvian.mods.rhino.mod.util.MinecraftRemapper
 *  dev.latvian.mods.rhino.mod.util.RemappingHelper
 *  dev.latvian.mods.rhino.util.Remapper
 *  dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory
 *  dev.latvian.mods.rhino.util.wrap.TypeWrappers
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.script;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.CustomJavaToJsWrappersEvent;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.script.ScriptFile;
import dev.latvian.mods.kubejs.script.ScriptFileInfo;
import dev.latvian.mods.kubejs.script.ScriptPack;
import dev.latvian.mods.kubejs.script.ScriptPackInfo;
import dev.latvian.mods.kubejs.script.ScriptSource;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.LogType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.ClassShutter;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.NativeObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.mod.util.MinecraftRemapper;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import dev.latvian.mods.rhino.util.Remapper;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

public class ScriptManager
implements ClassShutter {
    private static final ThreadLocal<Context> CURRENT_CONTEXT = new ThreadLocal();
    public final ScriptType scriptType;
    public final Map<String, ScriptPack> packs;
    private final ClassFilter classFilter;
    public boolean firstLoad;
    public Context context;
    public Scriptable topLevelScope;
    private Map<String, Either<NativeJavaClass, Boolean>> javaClassCache;
    public boolean canListenEvents;

    @Nullable
    public static Context getCurrentContext() {
        return CURRENT_CONTEXT.get();
    }

    public ScriptManager(ScriptType t) {
        this.scriptType = t;
        this.packs = new LinkedHashMap<String, ScriptPack>();
        this.firstLoad = true;
        this.classFilter = KubeJSPlugins.createClassFilter(this.scriptType);
    }

    public void unload() {
        this.packs.clear();
        this.scriptType.unload();
        this.javaClassCache = null;
    }

    public void reload(@Nullable ResourceManager resourceManager) {
        KubeJSPlugins.forEachPlugin(KubeJSPlugin::clearCaches);
        this.unload();
        this.scriptType.console.writeToFile(LogType.INIT, "KubeJS " + KubeJS.thisMod.getVersion() + "; MC 2001 " + PlatformWrapper.getName());
        this.scriptType.console.writeToFile(LogType.INIT, "Loaded plugins:");
        for (KubeJSPlugin plugin : KubeJSPlugins.getAll()) {
            this.scriptType.console.writeToFile(LogType.INIT, "- " + plugin.getClass().getName());
        }
        this.loadFromDirectory();
        if (resourceManager != null) {
            this.loadFromResources(resourceManager);
        }
        this.load();
    }

    private void loadFile(ScriptPack pack, ScriptFileInfo fileInfo, ScriptSource source) {
        try {
            fileInfo.preload(source);
            String skip = fileInfo.skipLoading();
            if (skip.isEmpty()) {
                pack.scripts.add(new ScriptFile(pack, fileInfo, source));
            } else {
                this.scriptType.console.info("Skipped " + fileInfo.location + ": " + skip);
            }
        }
        catch (Throwable error) {
            this.scriptType.console.error("Failed to pre-load script file '" + fileInfo.location + "'", error);
        }
    }

    private void loadFromResources(ResourceManager resourceManager) {
        HashMap<String, List> packMap = new HashMap<String, List>();
        for (ResourceLocation resourceLocation : resourceManager.m_214159_("kubejs", s -> s.m_135815_().endsWith(".js") || s.m_135815_().endsWith(".ts") && !s.m_135815_().endsWith(".d.ts")).keySet()) {
            packMap.computeIfAbsent(resourceLocation.m_135827_(), s -> new ArrayList()).add(resourceLocation);
        }
        for (Map.Entry entry : packMap.entrySet()) {
            ScriptPack pack = new ScriptPack(this, new ScriptPackInfo((String)entry.getKey(), "kubejs/"));
            for (ResourceLocation id : (List)entry.getValue()) {
                pack.info.scripts.add(new ScriptFileInfo(pack.info, id.m_135815_().substring(7)));
            }
            for (ScriptFileInfo fileInfo : pack.info.scripts) {
                ScriptSource.FromResource scriptSource = info -> resourceManager.m_215593_(info.id);
                this.loadFile(pack, fileInfo, scriptSource);
            }
            pack.scripts.sort(null);
            this.packs.put(pack.info.namespace, pack);
        }
    }

    public void loadFromDirectory() {
        if (Files.notExists(this.scriptType.path, new LinkOption[0])) {
            try {
                Files.createDirectories(this.scriptType.path, new FileAttribute[0]);
            }
            catch (Exception ex) {
                this.scriptType.console.error("Failed to create script directory", ex);
            }
            try (OutputStream out = Files.newOutputStream(this.scriptType.path.resolve("example.js"), new OpenOption[0]);){
                out.write(("// priority: 0\n\n// Visit the wiki for more info - https://kubejs.com/\n\nconsole.info('Hello, World! (Loaded " + this.scriptType.name + " scripts)')\n\n").getBytes(StandardCharsets.UTF_8));
            }
            catch (Exception ex) {
                this.scriptType.console.error("Failed to write example.js", ex);
            }
        }
        ScriptPack pack = new ScriptPack(this, new ScriptPackInfo(this.scriptType.path.getFileName().toString(), ""));
        KubeJS.loadScripts(pack, this.scriptType.path, "");
        for (ScriptFileInfo fileInfo : pack.info.scripts) {
            ScriptSource.FromPath scriptSource = info -> this.scriptType.path.resolve(info.file);
            this.loadFile(pack, fileInfo, scriptSource);
        }
        pack.scripts.sort(null);
        this.packs.put(pack.info.namespace, pack);
    }

    public boolean isClassAllowed(String name) {
        return this.classFilter.isAllowed(name);
    }

    public void load() {
        MinecraftRemapper remapper = RemappingHelper.getMinecraftRemapper();
        long startAll = System.currentTimeMillis();
        this.context = Context.enter();
        this.topLevelScope = this.context.initStandardObjects();
        CURRENT_CONTEXT.set(this.context);
        this.context.setProperty("Type", (Object)this.scriptType);
        this.context.setProperty("Console", (Object)this.scriptType.console);
        this.context.setClassShutter((ClassShutter)this);
        this.context.setRemapper((Remapper)remapper);
        this.context.setApplicationClassLoader(KubeJS.class.getClassLoader());
        if (MiscPlatformHelper.get().isDataGen()) {
            this.firstLoad = false;
            this.scriptType.console.info("Skipping KubeJS script loading (DataGen)");
            return;
        }
        this.canListenEvents = true;
        TypeWrappers typeWrappers = this.context.getTypeWrappers();
        BindingsEvent bindingsEvent = new BindingsEvent(this, this.topLevelScope);
        CustomJavaToJsWrappersEvent customJavaToJsWrappersEvent = new CustomJavaToJsWrappersEvent(this);
        for (KubeJSPlugin plugin : KubeJSPlugins.getAll()) {
            plugin.registerTypeWrappers(this.scriptType, typeWrappers);
            plugin.registerBindings(bindingsEvent);
            plugin.registerCustomJavaToJsWrappers(customJavaToJsWrappersEvent);
        }
        KubeJSPlugins.addSidedBindings(bindingsEvent);
        for (ResourceKey reg : BuiltInRegistries.f_257047_.m_214010_()) {
            RegistryInfo<?> info = RegistryInfo.of(reg);
            if (!info.autoWrap || info.objectBaseClass == Object.class || info.objectBaseClass == null) continue;
            try {
                typeWrappers.register(info.objectBaseClass, (TypeWrapperFactory)UtilsJS.cast(info));
            }
            catch (IllegalArgumentException ignored) {
                this.scriptType.console.info("Skipped registry type wrapper for " + String.valueOf(info.key.m_135782_()));
            }
        }
        int i = 0;
        int t = 0;
        for (ScriptPack pack : this.packs.values()) {
            try {
                pack.scope = new NativeObject(this.context);
                pack.scope.setParentScope(this.topLevelScope);
                for (ScriptFile file : pack.scripts) {
                    ++t;
                    long start = System.currentTimeMillis();
                    try {
                        file.load();
                        ++i;
                        this.scriptType.console.info("Loaded script " + file.info.location + " in " + (double)(System.currentTimeMillis() - start) / 1000.0 + " s");
                    }
                    catch (Throwable ex) {
                        this.scriptType.console.error("", ex);
                    }
                }
            }
            catch (Throwable ex) {
                this.scriptType.console.error("Failed to read script pack " + pack.info.namespace, ex);
            }
        }
        this.scriptType.console.info("Loaded " + i + "/" + t + " KubeJS " + this.scriptType.name + " scripts in " + (double)(System.currentTimeMillis() - startAll) / 1000.0 + " s with " + this.scriptType.console.errors.size() + " errors and " + this.scriptType.console.warnings.size() + " warnings");
        this.firstLoad = false;
        this.canListenEvents = false;
    }

    public NativeJavaClass loadJavaClass(String name, boolean error) {
        NativeJavaClass l;
        Either either;
        if (name == null || name.equals("null") || name.isEmpty()) {
            if (error) {
                throw Context.reportRuntimeError((String)"Class name can't be empty!", (Context)this.context);
            }
            return null;
        }
        if (this.javaClassCache == null) {
            this.javaClassCache = new HashMap<String, Either<NativeJavaClass, Boolean>>();
        }
        if ((either = this.javaClassCache.get(name)) == null) {
            block13: {
                either = Either.right((Object)false);
                if (!this.isClassAllowed(name)) {
                    either = Either.right((Object)true);
                } else {
                    try {
                        either = Either.left((Object)new NativeJavaClass(this.context, this.topLevelScope, Class.forName(name)));
                        this.scriptType.console.info("Loaded Java class '%s'".formatted(name));
                    }
                    catch (Exception ignored1) {
                        String name1 = RemappingHelper.getMinecraftRemapper().getUnmappedClass(name);
                        if (name1.isEmpty()) break block13;
                        if (!this.isClassAllowed(name1)) {
                            either = Either.right((Object)true);
                        }
                        try {
                            either = Either.left((Object)new NativeJavaClass(this.context, this.topLevelScope, Class.forName(name1)));
                            this.scriptType.console.info("Loaded Java class '%s'".formatted(name));
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                }
            }
            this.javaClassCache.put(name, (Either<NativeJavaClass, Boolean>)either);
        }
        if ((l = (NativeJavaClass)either.left().orElse(null)) != null) {
            return l;
        }
        if (error) {
            Boolean found = either.right().orElse(false);
            throw Context.reportRuntimeError((String)(found != false ? "Failed to load Java class '%s': Class is not allowed by class filter!" : "Failed to load Java class '%s': Class could not be found!").formatted(name), (Context)this.context);
        }
        return null;
    }

    public boolean visibleToScripts(String fullClassName, int type) {
        return type != 2 || this.isClassAllowed(fullClassName);
    }
}

