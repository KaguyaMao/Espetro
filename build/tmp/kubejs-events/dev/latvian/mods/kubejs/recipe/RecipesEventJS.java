/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  dev.architectury.platform.Platform
 *  dev.latvian.mods.rhino.WrappedException
 *  dev.latvian.mods.rhino.mod.util.JsonUtils
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  net.minecraft.ReportedException
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.Bootstrap
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeManager
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.recipe;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.event.EventExceptionHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientWithCustomPredicate;
import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.ErroredRecipeJS;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeResultCallback;
import dev.latvian.mods.kubejs.recipe.NamespaceFunction;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.filter.ConstantFilter;
import dev.latvian.mods.kubejs.recipe.filter.IDFilter;
import dev.latvian.mods.kubejs.recipe.filter.OrFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.schema.JsonRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeConstructor;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.WrappedException;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class RecipesEventJS
extends EventJS {
    public static final Pattern SKIP_ERROR = Pattern.compile("dev\\.latvian\\.mods\\.kubejs\\.recipe\\.RecipesEventJS\\.post");
    private static final Predicate<RecipeJS> RECIPE_NOT_REMOVED = r -> r != null && !r.removed;
    private static final EventExceptionHandler RECIPE_EXCEPTION_HANDLER = (event, handler, ex) -> {
        if (ex instanceof RecipeExceptionJS || ex instanceof JsonParseException) {
            ConsoleJS.SERVER.error("Error while processing recipe event handler: " + String.valueOf(handler), ex);
            return null;
        }
        return ex;
    };
    private static final BinaryOperator<Recipe<?>> MERGE_ORIGINAL = (a, b) -> {
        ConsoleJS.SERVER.warn("Duplicate original recipe for id " + String.valueOf(a.m_6423_()) + "!\nRecipe A: " + RecipesEventJS.recipeToString(a) + "\nRecipe B: " + RecipesEventJS.recipeToString(b) + "\nUsing last one encountered.");
        return b;
    };
    private static final BinaryOperator<Recipe<?>> MERGE_ADDED = (a, b) -> {
        ConsoleJS.SERVER.error("Duplicate added recipe for id " + String.valueOf(a.m_6423_()) + "!\nRecipe A: " + RecipesEventJS.recipeToString(a) + "\nRecipe B: " + RecipesEventJS.recipeToString(b) + "\nUsing last one encountered.");
        return b;
    };
    private static final Function<Recipe<?>, ResourceLocation> RECIPE_ID = Recipe::m_6423_;
    private static final Predicate<Recipe<?>> RECIPE_NON_NULL = Objects::nonNull;
    private static final Function<Recipe<?>, Recipe<?>> RECIPE_IDENTITY = Function.identity();
    @HideFromJS
    public static final Map<ResourceLocation, ModifyRecipeResultCallback> MODIFY_RESULT_CALLBACKS = new ConcurrentHashMap<ResourceLocation, ModifyRecipeResultCallback>();
    private static final ForkJoinPool PARALLEL_THREAD_POOL = new ForkJoinPool(Math.max(1, Runtime.getRuntime().availableProcessors() - 1), forkJoinPool -> {
        ForkJoinWorkerThread thread = new ForkJoinWorkerThread(forkJoinPool){};
        thread.setContextClassLoader(RecipesEventJS.class.getClassLoader());
        thread.setName(String.format("KubeJS Recipe Event Worker %d", thread.getPoolIndex()));
        return thread;
    }, (thread, ex) -> {
        RecipeExceptionJS rex1;
        RecipeExceptionJS rex;
        while (ex instanceof CompletionException | ex instanceof InvocationTargetException | ex instanceof WrappedException && ex.getCause() != null) {
            ex = ex.getCause();
        }
        if (ex instanceof ReportedException) {
            ReportedException crashed = (ReportedException)ex;
            Bootstrap.m_135875_((String)crashed.m_134761_().m_127526_());
            System.exit(-1);
        }
        ConsoleJS.SERVER.error("Error in thread %s while performing bulk recipe operation!".formatted(thread), ex);
        RecipeExceptionJS recipeExceptionJS = rex = ex instanceof RecipeExceptionJS ? (rex1 = (RecipeExceptionJS)ex) : new RecipeExceptionJS(null, ex).error();
        if (rex.error) {
            throw rex;
        }
    }, true);
    @HideFromJS
    public static Map<UUID, IngredientWithCustomPredicate> customIngredientMap = null;
    @HideFromJS
    public static RecipesEventJS instance;
    public final Map<ResourceLocation, RecipeJS> originalRecipes;
    public final Collection<RecipeJS> addedRecipes;
    public final AtomicInteger failedCount;
    public final Map<ResourceLocation, RecipeJS> takenIds;
    private final Map<String, Object> recipeFunctions;
    public final transient RecipeTypeFunction vanillaShaped;
    public final transient RecipeTypeFunction vanillaShapeless;
    public final RecipeTypeFunction shaped;
    public final RecipeTypeFunction shapeless;
    public final RecipeTypeFunction smelting;
    public final RecipeTypeFunction blasting;
    public final RecipeTypeFunction smoking;
    public final RecipeTypeFunction campfireCooking;
    public final RecipeTypeFunction stonecutting;
    public final RecipeTypeFunction smithing;
    public final RecipeTypeFunction smithingTrim;
    final RecipeSerializer<?> stageSerializer;

    private static String recipeToString(Recipe<?> recipe) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("type", RegistryInfo.RECIPE_SERIALIZER.getId(recipe.m_7707_()));
        try {
            ArrayList in = new ArrayList();
            for (Ingredient ingredient : recipe.m_7527_()) {
                ArrayList<String> list = new ArrayList<String>();
                for (ItemStack item : ingredient.m_43908_()) {
                    list.add(item.kjs$toItemString());
                }
                in.add(list);
            }
            map.put("in", in);
        }
        catch (Exception ex) {
            map.put("in_error", ex.toString());
        }
        try {
            ItemStack result = recipe.m_8043_(UtilsJS.staticRegistryAccess);
            map.put("out", (result == null ? ItemStack.f_41583_ : result).kjs$toItemString());
        }
        catch (Exception ex) {
            map.put("out_error", ex.toString());
        }
        return map.toString();
    }

    public RecipesEventJS() {
        ConsoleJS.SERVER.info("Initializing recipe event...");
        this.originalRecipes = new HashMap<ResourceLocation, RecipeJS>();
        this.addedRecipes = new ConcurrentLinkedQueue<RecipeJS>();
        this.recipeFunctions = new HashMap<String, Object>();
        this.takenIds = new ConcurrentHashMap<ResourceLocation, RecipeJS>();
        this.failedCount = new AtomicInteger(0);
        Map<String, RecipeNamespace> allNamespaces = RecipeNamespace.getAll();
        for (RecipeNamespace recipeNamespace : allNamespaces.values()) {
            HashMap<String, RecipeTypeFunction> nsMap = new HashMap<String, RecipeTypeFunction>();
            this.recipeFunctions.put(recipeNamespace.name, new NamespaceFunction(recipeNamespace, nsMap));
            for (Map.Entry entry : recipeNamespace.entrySet()) {
                RecipeTypeFunction func = new RecipeTypeFunction(this, (RecipeSchemaType)entry.getValue());
                nsMap.put(((RecipeSchemaType)entry.getValue()).id.m_135815_(), func);
                this.recipeFunctions.put(((RecipeSchemaType)entry.getValue()).id.toString(), func);
            }
        }
        this.vanillaShaped = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:crafting_shaped");
        this.vanillaShapeless = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:crafting_shapeless");
        this.shaped = CommonProperties.get().serverOnly ? this.vanillaShaped : (RecipeTypeFunction)this.recipeFunctions.get("kubejs:shaped");
        this.shapeless = CommonProperties.get().serverOnly ? this.vanillaShapeless : (RecipeTypeFunction)this.recipeFunctions.get("kubejs:shapeless");
        this.smelting = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:smelting");
        this.blasting = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:blasting");
        this.smoking = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:smoking");
        this.campfireCooking = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:campfire_cooking");
        this.stonecutting = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:stonecutting");
        this.smithing = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:smithing_transform");
        this.smithingTrim = (RecipeTypeFunction)this.recipeFunctions.get("minecraft:smithing_trim");
        for (Map.Entry entry : new ArrayList<Map.Entry<String, Object>>(this.recipeFunctions.entrySet())) {
            String s;
            if (!(entry.getValue() instanceof RecipeTypeFunction) || ((String)entry.getKey()).indexOf(58) == -1 || (s = UtilsJS.snakeCaseToCamelCase((String)entry.getKey())).equals(entry.getKey())) continue;
            this.recipeFunctions.put(s, entry.getValue());
        }
        for (Map.Entry entry : RecipeNamespace.getMappedRecipes().entrySet()) {
            Object type = this.recipeFunctions.get(((ResourceLocation)entry.getValue()).toString());
            if (!(type instanceof RecipeTypeFunction)) continue;
            this.recipeFunctions.put((String)entry.getKey(), type);
        }
        this.recipeFunctions.put("shaped", this.shaped);
        this.recipeFunctions.put("shapeless", this.shapeless);
        this.recipeFunctions.put("smelting", this.smelting);
        this.recipeFunctions.put("blasting", this.blasting);
        this.recipeFunctions.put("smoking", this.smoking);
        this.recipeFunctions.put("campfireCooking", this.campfireCooking);
        this.recipeFunctions.put("stonecutting", this.stonecutting);
        this.recipeFunctions.put("smithing", this.smithing);
        this.recipeFunctions.put("smithingTrim", this.smithingTrim);
        this.stageSerializer = RegistryInfo.RECIPE_SERIALIZER.getValue(new ResourceLocation("recipestages:stage"));
    }

    @HideFromJS
    public void post(RecipeManager recipeManager, Map<ResourceLocation, JsonElement> datapackRecipeMap) {
        RecipeTypeFunction type;
        ConsoleJS.SERVER.info("Processing recipes...");
        RecipeJS.itemErrors = false;
        TagContext.INSTANCE.setValue((Object)TagContext.fromLoadResult(KubeJSReloadListener.resources.f_206849_.m_203904_()));
        MODIFY_RESULT_CALLBACKS.clear();
        Stopwatch timer = Stopwatch.createStarted();
        JsonObject exportedRecipes = new JsonObject();
        for (Map.Entry<ResourceLocation, JsonElement> entry : datapackRecipeMap.entrySet()) {
            JsonObject json;
            String recipeIdAndType;
            ResourceLocation recipeId;
            block26: {
                recipeId = entry.getKey();
                recipeIdAndType = String.valueOf(recipeId) + "[unknown:type]";
                try {
                    if (recipeId == null || Platform.isForge() && recipeId.m_135815_().startsWith("_")) continue;
                    json = RecipePlatformHelper.get().checkConditions(GsonHelper.m_13918_((JsonElement)entry.getValue(), (String)"top element"));
                    if (json == null) {
                        if (!DevProperties.get().logSkippedRecipes) continue;
                        ConsoleJS.SERVER.info("Skipping recipe " + String.valueOf(recipeId) + ", conditions not met");
                        continue;
                    }
                    if (!json.has("type")) {
                        if (!DevProperties.get().logSkippedRecipes) continue;
                        ConsoleJS.SERVER.info("Skipping recipe " + String.valueOf(recipeId) + ", missing type");
                        continue;
                    }
                    if (DataExport.export == null) break block26;
                    exportedRecipes.add(recipeId.toString(), JsonUtils.copy((JsonElement)json));
                }
                catch (Exception ex) {
                    if (!DevProperties.get().logSkippedRecipes) continue;
                    ConsoleJS.SERVER.warn("Skipping recipe %s, failed to load: ".formatted(recipeId), ex);
                    continue;
                }
            }
            String typeStr = GsonHelper.m_13906_((JsonObject)json, (String)"type");
            recipeIdAndType = String.valueOf(recipeId) + "[" + typeStr + "]";
            type = this.getRecipeFunction(typeStr);
            if (type == null) {
                if (!DevProperties.get().logSkippedRecipes) continue;
                ConsoleJS.SERVER.info("Skipping recipe " + String.valueOf(recipeId) + ", unknown type: " + typeStr);
                continue;
            }
            try {
                RecipeJS recipe = type.schemaType.schema.deserialize(type, recipeId, json);
                recipe.afterLoaded();
                this.originalRecipes.put(recipeId, recipe);
                if (!ConsoleJS.SERVER.shouldPrintDebug()) continue;
                Recipe<?> originalRecipe = recipe.getOriginalRecipe();
                if (originalRecipe == null || SpecialRecipeSerializerManager.INSTANCE.isSpecial(originalRecipe)) {
                    ConsoleJS.SERVER.debug("Loaded recipe " + recipeIdAndType + ": <dynamic>");
                    continue;
                }
                ConsoleJS.SERVER.debug("Loaded recipe " + recipeIdAndType + ": " + recipe.getFromToString());
            }
            catch (Throwable ex) {
                if (DevProperties.get().logErroringRecipes || DevProperties.get().debugInfo) {
                    ConsoleJS.SERVER.warn("Failed to parse recipe '" + recipeIdAndType + "'! Falling back to vanilla", ex, SKIP_ERROR);
                }
                try {
                    this.originalRecipes.put(recipeId, JsonRecipeSchema.SCHEMA.deserialize(type, recipeId, json));
                }
                catch (JsonParseException | IllegalArgumentException | NullPointerException ex2) {
                    if (!DevProperties.get().logErroringRecipes && !DevProperties.get().debugInfo) continue;
                    ConsoleJS.SERVER.warn("Failed to parse recipe " + recipeIdAndType, ex2, SKIP_ERROR);
                }
                catch (Exception ex3) {
                    ConsoleJS.SERVER.warn("Failed to parse recipe " + recipeIdAndType, ex3, SKIP_ERROR);
                }
            }
        }
        this.takenIds.putAll(this.originalRecipes);
        ConsoleJS.SERVER.info("Found " + this.originalRecipes.size() + " recipes in " + String.valueOf(timer.stop()));
        timer.reset().start();
        ServerEvents.RECIPES.post(ScriptType.SERVER, this);
        int modifiedCount = 0;
        ConcurrentLinkedQueue<RecipeJS> removedRecipes = new ConcurrentLinkedQueue<RecipeJS>();
        for (RecipeJS r : this.originalRecipes.values()) {
            if (r.removed) {
                removedRecipes.add(r);
                continue;
            }
            if (!r.hasChanged()) continue;
            ++modifiedCount;
        }
        ConsoleJS.SERVER.info("Posted recipe events in " + String.valueOf(timer.stop()));
        timer.reset().start();
        this.addedRecipes.removeIf(RecipesEventJS::addedRecipeRemoveCheck);
        HashMap recipesByName = new HashMap(this.originalRecipes.size() + this.addedRecipes.size());
        try {
            recipesByName.putAll(RecipesEventJS.runInParallel(() -> this.originalRecipes.values().parallelStream().filter(RECIPE_NOT_REMOVED).map(this::createRecipe).filter(RECIPE_NON_NULL).collect(Collectors.toConcurrentMap(RECIPE_ID, RECIPE_IDENTITY, MERGE_ORIGINAL))));
        }
        catch (Throwable ex) {
            ConsoleJS.SERVER.error("Error creating datapack recipes", ex, SKIP_ERROR);
        }
        try {
            recipesByName.putAll(RecipesEventJS.runInParallel(() -> this.addedRecipes.parallelStream().map(this::createRecipe).filter(RECIPE_NON_NULL).collect(Collectors.toConcurrentMap(RECIPE_ID, RECIPE_IDENTITY, MERGE_ADDED))));
        }
        catch (Throwable ex) {
            ConsoleJS.SERVER.error("Error creating script recipes", ex, SKIP_ERROR);
        }
        KubeJSPlugins.forEachPlugin(p -> p.injectRuntimeRecipes(this, recipeManager, recipesByName));
        HashMap newRecipeMap = new HashMap();
        for (Map.Entry entry : recipesByName.entrySet()) {
            type = ((Recipe)entry.getValue()).m_6671_();
            Map recipes = newRecipeMap.computeIfAbsent((RecipeType<?>)type, (Function<RecipeType<?>, Map<ResourceLocation, Recipe<?>>>)((Function<RecipeType, Map>)k -> new HashMap()));
            recipes.put((ResourceLocation)entry.getKey(), (Recipe)entry.getValue());
        }
        RecipePlatformHelper.get().pingNewRecipes(newRecipeMap);
        recipeManager.f_199900_ = recipesByName;
        recipeManager.f_44007_ = newRecipeMap;
        ConsoleJS.SERVER.info("Added " + this.addedRecipes.size() + " recipes, removed " + removedRecipes.size() + " recipes, modified " + modifiedCount + " recipes, with " + this.failedCount.get() + " failed recipes in " + String.valueOf(timer.stop()));
        RecipeJS.itemErrors = false;
        if (DataExport.export != null) {
            for (RecipeJS r : removedRecipes) {
                DataExport.export.addJson("removed_recipes/" + r.getId() + ".json", (JsonElement)r.json);
            }
        }
        if (DevProperties.get().debugInfo) {
            ConsoleJS.SERVER.info("======== Debug output of all added recipes ========");
            for (RecipeJS r : this.addedRecipes) {
                ConsoleJS.SERVER.info(String.valueOf(r.getOrCreateId()) + ": " + String.valueOf(r.json));
            }
            ConsoleJS.SERVER.info("======== Debug output of all modified recipes ========");
            for (RecipeJS r : this.originalRecipes.values()) {
                if (r.removed || !r.hasChanged()) continue;
                ConsoleJS.SERVER.info(String.valueOf(r.getOrCreateId()) + ": " + String.valueOf(r.json) + " FROM " + String.valueOf(r.originalJson));
            }
            ConsoleJS.SERVER.info("======== Debug output of all removed recipes ========");
            for (RecipeJS r : removedRecipes) {
                ConsoleJS.SERVER.info(String.valueOf(r.getOrCreateId()) + ": " + String.valueOf(r.json));
            }
        }
    }

    @Nullable
    private Recipe<?> createRecipe(RecipeJS r) {
        try {
            Recipe<?> rec = r.createRecipe();
            String path = r.kjs$getMod() + "/" + r.getPath();
            if (!r.removed && DataExport.export != null) {
                DataExport.export.addJson("recipes/%s.json".formatted(path), (JsonElement)r.json);
                if (r.newRecipe) {
                    DataExport.export.addJson("added_recipes/%s.json".formatted(path), (JsonElement)r.json);
                }
            }
            return rec;
        }
        catch (Throwable ex) {
            ConsoleJS.SERVER.warn("Error parsing recipe " + String.valueOf(r) + ": " + String.valueOf(r.json), ex, SKIP_ERROR);
            this.failedCount.incrementAndGet();
            return null;
        }
    }

    private static boolean addedRecipeRemoveCheck(RecipeJS r) {
        return !r.newRecipe;
    }

    public Map<String, Object> getRecipes() {
        return this.recipeFunctions;
    }

    public RecipeJS addRecipe(RecipeJS r, boolean json) {
        if (r instanceof ErroredRecipeJS) {
            ConsoleJS.SERVER.warn("Tried to add errored recipe %s!".formatted(r));
            return r;
        }
        this.addedRecipes.add(r);
        if (DevProperties.get().logAddedRecipes) {
            ConsoleJS.SERVER.info("+ " + String.valueOf(r.getType()) + ": " + r.getFromToString() + (json ? " [json]" : ""));
        } else if (ConsoleJS.SERVER.shouldPrintDebug()) {
            ConsoleJS.SERVER.debug("+ " + String.valueOf(r.getType()) + ": " + r.getFromToString() + (json ? " [json]" : ""));
        }
        return r;
    }

    public RecipeFilter customFilter(Predicate<RecipeKJS> filter) {
        return filter::test;
    }

    public Stream<RecipeJS> recipeStream(RecipeFilter filter) {
        block5: {
            if (filter == ConstantFilter.FALSE) {
                return Stream.empty();
            }
            if (filter instanceof IDFilter) {
                IDFilter id = (IDFilter)filter;
                RecipeJS r = this.originalRecipes.get(id.id);
                return r == null || r.removed ? Stream.empty() : Stream.of(r);
            }
            if (filter instanceof OrFilter) {
                OrFilter or = (OrFilter)filter;
                if (or.list.isEmpty()) {
                    return Stream.empty();
                }
                for (RecipeFilter recipeFilter : or.list) {
                    if (recipeFilter instanceof IDFilter) continue;
                    break block5;
                }
                return or.list.stream().map(idf -> this.originalRecipes.get(((IDFilter)idf).id)).filter(RECIPE_NOT_REMOVED);
            }
        }
        return this.originalRecipes.values().stream().filter(new RecipeStreamFilter(filter));
    }

    @ApiStatus.Internal
    private Stream<RecipeJS> recipeStreamAsync(RecipeFilter filter) {
        Stream stream = this.recipeStream(filter);
        return CommonProperties.get().allowAsyncStreams ? (Stream)stream.parallel() : stream;
    }

    private void forEachRecipeAsync(RecipeFilter filter, Consumer<RecipeJS> consumer) {
        RecipesEventJS.runInParallel(() -> this.recipeStreamAsync(filter).forEach(consumer));
    }

    private <T> T reduceRecipesAsync(RecipeFilter filter, Function<Stream<RecipeJS>, T> function) {
        return (T)RecipesEventJS.runInParallel(() -> function.apply(this.recipeStreamAsync(filter)));
    }

    public void forEachRecipe(RecipeFilter filter, Consumer<RecipeJS> consumer) {
        this.recipeStream(filter).forEach(consumer);
    }

    public int countRecipes(RecipeFilter filter) {
        return this.reduceRecipesAsync(filter, s -> (int)s.count());
    }

    public boolean containsRecipe(RecipeFilter filter) {
        return this.reduceRecipesAsync(filter, s -> s.findAny().isPresent());
    }

    public Collection<RecipeJS> findRecipes(RecipeFilter filter) {
        return this.reduceRecipesAsync(filter, Stream::toList);
    }

    public Collection<ResourceLocation> findRecipeIds(RecipeFilter filter) {
        return this.reduceRecipesAsync(filter, s -> s.map(RecipeJS::getOrCreateId).toList());
    }

    public void remove(RecipeFilter filter) {
        if (filter instanceof IDFilter) {
            IDFilter id = (IDFilter)filter;
            RecipeJS r = this.originalRecipes.get(id.id);
            if (r != null) {
                r.remove();
            }
        } else {
            this.forEachRecipeAsync(filter, RecipeJS::remove);
        }
    }

    public void replaceInput(RecipeFilter filter, ReplacementMatch match, InputReplacement with) {
        String dstring = DevProperties.get().logModifiedRecipes || ConsoleJS.SERVER.shouldPrintDebug() ? ": IN " + String.valueOf(match) + " -> " + String.valueOf(with) : "";
        this.forEachRecipeAsync(filter, r -> {
            if (r.replaceInput(match, with)) {
                if (DevProperties.get().logModifiedRecipes) {
                    ConsoleJS.SERVER.info("~ " + String.valueOf(r) + dstring);
                } else if (ConsoleJS.SERVER.shouldPrintDebug()) {
                    ConsoleJS.SERVER.debug("~ " + String.valueOf(r) + dstring);
                }
            }
        });
    }

    public void replaceOutput(RecipeFilter filter, ReplacementMatch match, OutputReplacement with) {
        String dstring = DevProperties.get().logModifiedRecipes || ConsoleJS.SERVER.shouldPrintDebug() ? ": OUT " + String.valueOf(match) + " -> " + String.valueOf(with) : "";
        this.forEachRecipeAsync(filter, r -> {
            if (r.replaceOutput(match, with)) {
                if (DevProperties.get().logModifiedRecipes) {
                    ConsoleJS.SERVER.info("~ " + String.valueOf(r) + dstring);
                } else if (ConsoleJS.SERVER.shouldPrintDebug()) {
                    ConsoleJS.SERVER.debug("~ " + String.valueOf(r) + dstring);
                }
            }
        });
    }

    public RecipeTypeFunction getRecipeFunction(@Nullable String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        Object object = this.recipeFunctions.get(UtilsJS.getID(id));
        if (object instanceof RecipeTypeFunction) {
            RecipeTypeFunction fn = (RecipeTypeFunction)object;
            return fn;
        }
        return null;
    }

    public RecipeJS custom(JsonObject json) {
        try {
            if (json == null || !json.has("type")) {
                throw new RecipeExceptionJS("JSON must contain 'type'!");
            }
            RecipeTypeFunction type = this.getRecipeFunction(json.get("type").getAsString());
            if (type == null) {
                throw new RecipeExceptionJS("Unknown recipe type: " + json.get("type").getAsString());
            }
            RecipeJS recipe = type.schemaType.schema.deserialize(type, null, json);
            recipe.afterLoaded();
            return this.addRecipe(recipe, true);
        }
        catch (RecipeExceptionJS rex) {
            if (rex.error) {
                throw rex;
            }
            return new ErroredRecipeJS(this, "Failed to create custom JSON recipe from '%s'".formatted(json), rex, SKIP_ERROR);
        }
    }

    private void printTypes(Predicate<RecipeSchemaType> predicate) {
        int t = 0;
        IdentityHashMap<RecipeSchema, Set> map = new IdentityHashMap<RecipeSchema, Set>();
        for (RecipeNamespace recipeNamespace : RecipeNamespace.getAll().values()) {
            for (RecipeSchemaType type : recipeNamespace.values()) {
                if (!predicate.test(type)) continue;
                ++t;
                map.computeIfAbsent(type.schema, s -> new HashSet()).add(type.id);
            }
        }
        for (Map.Entry entry : map.entrySet()) {
            ConsoleJS.SERVER.info("- " + ((Set)entry.getValue()).stream().map(ResourceLocation::toString).collect(Collectors.joining(", ")));
            for (RecipeConstructor c : ((RecipeSchema)entry.getKey()).constructors().values()) {
                ConsoleJS.SERVER.info("  - " + String.valueOf(c));
            }
        }
        ConsoleJS.SERVER.info(t + " types");
    }

    public void printTypes() {
        ConsoleJS.SERVER.info("== All recipe types [used] ==");
        Set set = this.reduceRecipesAsync(ConstantFilter.TRUE, s -> s.map(r -> r.type.id).collect(Collectors.toSet()));
        this.printTypes(t -> set.contains(t.id));
    }

    public void printAllTypes() {
        ConsoleJS.SERVER.info("== All recipe types [available] ==");
        this.printTypes(t -> RegistryInfo.RECIPE_SERIALIZER.getValue(t.id) != null);
    }

    public void printExamples(String type) {
        List list = this.originalRecipes.values().stream().filter(recipeJS -> recipeJS.type.toString().equals(type)).collect(Collectors.toList());
        Collections.shuffle(list);
        ConsoleJS.SERVER.info("== Random examples of '" + type + "' ==");
        for (int i = 0; i < Math.min(list.size(), 5); ++i) {
            RecipeJS r = (RecipeJS)list.get(i);
            ConsoleJS.SERVER.info("- " + String.valueOf(r.getOrCreateId()) + ":\n" + JsonIO.toPrettyString((JsonElement)r.json));
        }
    }

    public synchronized ResourceLocation takeId(RecipeJS recipe, String prefix, String ids) {
        int i = 2;
        ResourceLocation id = new ResourceLocation(prefix + ids);
        while (this.takenIds.containsKey(id)) {
            id = new ResourceLocation(prefix + ids + "_" + i);
            ++i;
        }
        this.takenIds.put(id, recipe);
        return id;
    }

    public void setItemErrors(boolean b) {
        RecipeJS.itemErrors = b;
    }

    public void stage(RecipeFilter filter, String stage) {
        this.forEachRecipeAsync(filter, r -> r.stage(stage));
    }

    public static void runInParallel(Runnable runnable) {
        PARALLEL_THREAD_POOL.invoke(ForkJoinTask.adapt(runnable));
    }

    public static <T> T runInParallel(Callable<T> callable) {
        return PARALLEL_THREAD_POOL.invoke(ForkJoinTask.adapt(callable));
    }

    private record RecipeStreamFilter(RecipeFilter filter) implements Predicate<RecipeJS>
    {
        @Override
        public boolean test(RecipeJS r) {
            return r != null && !r.removed && this.filter.test(r);
        }
    }
}

