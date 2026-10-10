/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.ReloadableServerResources
 *  net.minecraft.server.packs.FilePackResources
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.resources.CloseableResourceManager
 *  net.minecraft.server.packs.resources.MultiPackResourceManager
 *  net.minecraft.server.packs.resources.ResourceManager
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.item.ingredient.IngredientWithCustomPredicate;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.mods.kubejs.server.GeneratedServerResourcePack;
import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import dev.latvian.mods.kubejs.server.tag.PreTagEventJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

public class ServerScriptManager
extends ScriptManager {
    public static ServerScriptManager instance;
    public final MinecraftServer server;
    public final Map<ResourceKey<?>, PreTagEventJS> preTagEvents = new ConcurrentHashMap();

    public static ScriptManager getScriptManager() {
        return instance;
    }

    public ServerScriptManager(@Nullable MinecraftServer server) {
        super(ScriptType.SERVER);
        this.server = server;
        try {
            if (Files.notExists(KubeJSPaths.DATA, new LinkOption[0])) {
                Files.createDirectories(KubeJSPaths.DATA, new FileAttribute[0]);
            }
        }
        catch (Throwable ex) {
            throw new RuntimeException("KubeJS failed to register it's script loader!", ex);
        }
    }

    public void updateResources(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
        KubeJSReloadListener.resources = serverResources;
        KubeJSReloadListener.recipeContext = RecipePlatformHelper.get().createRecipeContext(serverResources);
        UtilsJS.staticRegistryAccess = registryAccess;
    }

    public MultiPackResourceManager wrapResourceManager(CloseableResourceManager original) {
        List list;
        VirtualKubeJSDataPack virtualDataPackLow = new VirtualKubeJSDataPack(false);
        VirtualKubeJSDataPack virtualDataPackHigh = new VirtualKubeJSDataPack(true);
        if (original instanceof MultiPackResourceManager) {
            File[] mp = (File[])original;
            list = mp.f_203795_;
        } else {
            list = original.m_7536_().toList();
        }
        LinkedList list2 = new LinkedList(list);
        list2.addFirst(virtualDataPackLow);
        list2.addLast(new GeneratedServerResourcePack());
        for (File file : Objects.requireNonNull(KubeJSPaths.DATA.toFile().listFiles())) {
            if (!file.isFile() || !file.getName().endsWith(".zip")) continue;
            list2.addLast(new FilePackResources(file.getName(), file, false));
        }
        list2.addLast(virtualDataPackHigh);
        MultiPackResourceManager wrappedResourceManager = new MultiPackResourceManager(PackType.SERVER_DATA, list2);
        ConsoleJS.SERVER.setCapturingErrors(true);
        this.reload((ResourceManager)wrappedResourceManager);
        ServerEvents.LOW_DATA.post(ScriptType.SERVER, new DataPackEventJS(virtualDataPackLow, wrappedResourceManager));
        ServerEvents.HIGH_DATA.post(ScriptType.SERVER, new DataPackEventJS(virtualDataPackHigh, wrappedResourceManager));
        ConsoleJS.SERVER.info("Scripts loaded");
        RecipesEventJS.customIngredientMap = new HashMap<UUID, IngredientWithCustomPredicate>();
        CustomIngredientAction.MAP.clear();
        SpecialRecipeSerializerManager.INSTANCE.reset();
        ServerEvents.SPECIAL_RECIPES.post(ScriptType.SERVER, SpecialRecipeSerializerManager.INSTANCE);
        KubeJSPlugins.forEachPlugin(KubeJSPlugin::onServerReload);
        PreTagEventJS.handle(this.preTagEvents);
        if (ServerEvents.RECIPES.hasListeners()) {
            RecipesEventJS.instance = new RecipesEventJS();
        }
        return wrappedResourceManager;
    }
}

