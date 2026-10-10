/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.AbstractCookingRecipe
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeManager
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.ShapedRecipe
 *  net.minecraft.world.item.crafting.SmithingRecipe
 *  net.minecraft.world.item.crafting.StonecutterRecipe
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RecipesUpdatedEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.sighs.apricityui.dom.expander;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.dom.SlotContentRules;
import com.sighs.apricityui.element.Item;
import com.sighs.apricityui.element.Slot;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.slot.IngredientExpressionCompiler;
import com.sighs.apricityui.slot.ItemStackExpressionCompiler;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public final class RecipeExpander {
    public static void expand(Document document) {
        if (document == null) {
            return;
        }
        ArrayList<Element> snapshot = new ArrayList<Element>(document.getElements());
        for (Element element : snapshot) {
            if (!(element instanceof com.sighs.apricityui.element.Recipe)) continue;
            com.sighs.apricityui.element.Recipe recipe = (com.sighs.apricityui.element.Recipe)element;
            RecipeExpander.expandSingleRecipe(document, recipe);
        }
    }

    private static void expandSingleRecipe(Document document, com.sighs.apricityui.element.Recipe recipe) {
        boolean changed = recipe.clearGeneratedRecipeSlots();
        RecipeResolver.DeclaredType declaredType = RecipeResolver.DeclaredType.fromRaw(recipe.getAttribute("type"));
        if (declaredType == null) {
            String message = "Recipe preview skipped: missing/invalid type";
            ApricityUI.LOGGER.warn("{}, type={}", (Object)message, (Object)recipe.getAttribute("type"));
            changed |= RecipeExpander.setAttributeIfChanged(recipe, "data-recipe-type", "");
            changed |= RecipeExpander.setAttributeIfChanged(recipe, "data-recipe-layout", "");
            if (changed |= RecipeExpander.setAttributeIfChanged(recipe, "data-recipe-error", message)) {
                document.markDirty(recipe, 4);
            }
            return;
        }
        changed |= RecipeExpander.setAttributeIfChanged(recipe, "data-recipe-type", declaredType.id());
        ResourceLocation recipeId = recipe.parseRecipeIdFromInnerText();
        if (recipeId == null) {
            String message = "Recipe preview skipped: invalid recipe id in innerText";
            ApricityUI.LOGGER.warn("{}, innerText={}", (Object)message, (Object)recipe.innerText);
            changed |= RecipeExpander.setAttributeIfChanged(recipe, "data-recipe-layout", "");
            if (changed |= RecipeExpander.setAttributeIfChanged(recipe, "data-recipe-error", message)) {
                document.markDirty(recipe, 4);
            }
            return;
        }
        RecipeResolver.ResolveResult preview = RecipeResolver.resolve(recipeId, declaredType);
        changed |= RecipeExpander.setAttributeIfChanged(recipe, "data-recipe-layout", preview.layoutKind().name().toLowerCase(Locale.ROOT));
        if (preview.absoluteEntries().isEmpty() && preview.listEntries().isEmpty()) {
            if (changed |= RecipeExpander.setAttributeIfChanged(recipe, "data-recipe-error", preview.message())) {
                document.markDirty(recipe, 4);
            }
            return;
        }
        changed |= RecipeExpander.setAttributeIfChanged(recipe, "data-recipe-error", "");
        EnumMap<RecipeResolver.PreviewRole, Integer> roleOrderCursor = new EnumMap<RecipeResolver.PreviewRole, Integer>(RecipeResolver.PreviewRole.class);
        int appendedCount = 0;
        for (RecipeResolver.PreviewEntry entry : preview.absoluteEntries()) {
            RecipeExpander.appendPreviewSlot(document, recipe, entry, RecipeExpander.nextRoleIndex(roleOrderCursor, entry.role()), "absolute");
            ++appendedCount;
        }
        if (preview.layoutKind() == RecipeResolver.LayoutKind.STONECUTTING) {
            int visibleCount = Math.min(3, preview.listEntries().size());
            for (int index = 0; index < visibleCount; ++index) {
                RecipeResolver.PreviewEntry entry = preview.listEntries().get(index);
                RecipeExpander.appendPreviewSlot(document, recipe, entry, RecipeExpander.nextRoleIndex(roleOrderCursor, entry.role()), "list");
                ++appendedCount;
            }
        }
        if (appendedCount > 0) {
            changed = true;
        }
        if (changed) {
            document.markDirty(recipe, 4);
        }
    }

    private static int nextRoleIndex(EnumMap<RecipeResolver.PreviewRole, Integer> roleOrderCursor, RecipeResolver.PreviewRole role) {
        int next = roleOrderCursor.getOrDefault((Object)role, 0);
        roleOrderCursor.put(role, next + 1);
        return next;
    }

    private static String toRoleClass(RecipeResolver.PreviewRole role) {
        if (role == RecipeResolver.PreviewRole.FUEL) {
            return "aui-recipe-fuel";
        }
        if (role == RecipeResolver.PreviewRole.OUTPUT) {
            return "aui-recipe-output";
        }
        return "aui-recipe-input";
    }

    private static String buildPreviewSlotClassName(RecipeResolver.PreviewRole role) {
        String roleName = role.name().toLowerCase(Locale.ROOT);
        LinkedHashSet<Object> classNames = new LinkedHashSet<Object>();
        classNames.add("recipe-slot");
        classNames.add("recipe-slot-" + roleName);
        classNames.add("aui-recipe-slot");
        classNames.add(RecipeExpander.toRoleClass(role));
        classNames.add("aui-recipe-" + roleName);
        return String.join((CharSequence)" ", classNames);
    }

    private static void appendPreviewSlot(Document document, com.sighs.apricityui.element.Recipe recipe, RecipeResolver.PreviewEntry entry, int roleIndex, String group) {
        if (entry == null) {
            return;
        }
        Slot slot = new Slot(document);
        String roleName = entry.role().name().toLowerCase(Locale.ROOT);
        slot.applyRecipeSlotMeta(RecipeExpander.buildPreviewSlotClassName(entry.role()), "recipe-slot");
        slot.setAttributesBatch(Map.of("data-role", roleName, "data-i", String.valueOf(Math.max(0, roleIndex)), "data-group", group == null ? "absolute" : group, "interactive", "0", "pointer", "0", "style", "--aui-slot-interactive:0;"), true);
        RecipeExpander.appendPreviewContent(document, slot, entry);
        recipe.append(slot);
    }

    private static void appendPreviewContent(Document document, Slot slot, RecipeResolver.PreviewEntry entry) {
        String expression;
        String string = expression = entry.slotExpression() == null ? "minecraft:air" : entry.slotExpression();
        if (RecipeExpander.usesIngredientContent(entry)) {
            com.sighs.apricityui.element.Ingredient ingredient = new com.sighs.apricityui.element.Ingredient(document);
            ingredient.setTextContent(expression);
            SlotContentRules.ensureControlledItem(ingredient);
            slot.appendChild(ingredient);
            return;
        }
        Item item = new Item(document);
        item.setTextContent(expression);
        slot.appendChild(item);
    }

    private static boolean usesIngredientContent(RecipeResolver.PreviewEntry entry) {
        return entry.role() != RecipeResolver.PreviewRole.OUTPUT && !"minecraft:air".equals(ItemStackExpressionCompiler.normalize(entry.slotExpression()));
    }

    private static boolean setAttributeIfChanged(com.sighs.apricityui.element.Recipe recipe, String key, String value) {
        String normalized;
        String string = normalized = value == null ? "" : value;
        if (Objects.equals(recipe.getAttribute(key), normalized)) {
            return false;
        }
        recipe.setAttribute(key, normalized);
        return true;
    }

    private static final class RecipeResolver {
        public static final int STONECUTTING_LIST_VISIBLE_ROWS = 3;
        private static final String AIR_ITEM_LITERAL = "minecraft:air";
        private static final int MAX_CACHE_SIZE = 256;
        private static final LinkedHashMap<RecipeCacheKey, ResolveResult> CACHE = new LinkedHashMap<RecipeCacheKey, ResolveResult>(64, 0.75f, true){

            @Override
            protected boolean removeEldestEntry(Map.Entry<RecipeCacheKey, ResolveResult> eldest) {
                return this.size() > 256;
            }
        };

        private RecipeResolver() {
        }

        public static synchronized ResolveResult resolve(ResourceLocation recipeId, DeclaredType declaredType) {
            if (recipeId == null || declaredType == null) {
                return ResolveResult.empty("Recipe cache key is invalid");
            }
            RecipeCacheKey cacheKey = new RecipeCacheKey(recipeId, declaredType);
            ResolveResult cached = CACHE.get(cacheKey);
            if (cached != null) {
                return cached;
            }
            ResolveResult resolved = RecipeResolver.buildPreview(recipeId, declaredType);
            CACHE.put(cacheKey, resolved);
            return resolved;
        }

        public static synchronized void clearCache() {
            CACHE.clear();
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private static ResolveResult buildPreview(ResourceLocation recipeId, DeclaredType declaredType) {
            Minecraft minecraft = Minecraft.m_91087_();
            if (minecraft.f_91073_ == null) {
                return ResolveResult.empty("Client level is not available");
            }
            RecipeManager recipeManager = minecraft.f_91073_.m_7465_();
            if (recipeManager == null) {
                return ResolveResult.empty("Recipe manager is not available");
            }
            Optional recipeOptional = recipeManager.m_44043_(recipeId);
            if (recipeOptional.isEmpty()) {
                return ResolveResult.empty("Recipe not found");
            }
            Recipe recipe = (Recipe)recipeOptional.get();
            if (!declaredType.matches(recipe)) {
                return ResolveResult.empty("Recipe type mismatch: declared=%s, actual=%s".formatted(declaredType.id(), recipe.getClass().getSimpleName()));
            }
            RegistryAccess registryAccess = minecraft.f_91073_.m_9598_();
            ArrayList<PreviewEntry> absoluteEntries = new ArrayList<PreviewEntry>();
            ArrayList<PreviewEntry> listEntries = new ArrayList<PreviewEntry>();
            LayoutKind layoutKind = declaredType.layoutKind();
            switch (declaredType) {
                case CRAFTING_SHAPED: {
                    if (!(recipe instanceof ShapedRecipe)) {
                        return ResolveResult.empty("Declared crafting_shaped but recipe is not ShapedRecipe");
                    }
                    ShapedRecipe shapedRecipe = (ShapedRecipe)recipe;
                    RecipeResolver.buildShapedCraftingEntries(shapedRecipe, absoluteEntries, registryAccess);
                    return new ResolveResult(layoutKind, List.copyOf(absoluteEntries), List.copyOf(listEntries), "");
                }
                case CRAFTING_SHAPELESS: {
                    if (!(recipe instanceof CraftingRecipe)) return ResolveResult.empty("Declared crafting_shapeless but recipe is not shapeless crafting");
                    CraftingRecipe craftingRecipe = (CraftingRecipe)recipe;
                    if (recipe instanceof ShapedRecipe) {
                        return ResolveResult.empty("Declared crafting_shapeless but recipe is not shapeless crafting");
                    }
                    RecipeResolver.buildShapelessCraftingEntries(craftingRecipe, absoluteEntries, registryAccess);
                    return new ResolveResult(layoutKind, List.copyOf(absoluteEntries), List.copyOf(listEntries), "");
                }
                case SMELTING: 
                case BLASTING: 
                case SMOKING: 
                case CAMPFIRE_COOKING: {
                    if (!(recipe instanceof AbstractCookingRecipe)) {
                        return ResolveResult.empty("Declared cooking family but recipe is not AbstractCookingRecipe");
                    }
                    AbstractCookingRecipe cookingRecipe = (AbstractCookingRecipe)recipe;
                    RecipeResolver.buildCookingEntries(cookingRecipe, absoluteEntries, registryAccess);
                    return new ResolveResult(layoutKind, List.copyOf(absoluteEntries), List.copyOf(listEntries), "");
                }
                case STONECUTTING: {
                    if (!(recipe instanceof StonecutterRecipe)) {
                        return ResolveResult.empty("Declared stonecutting but recipe is not StonecutterRecipe");
                    }
                    StonecutterRecipe stonecutterRecipe = (StonecutterRecipe)recipe;
                    RecipeResolver.buildStonecuttingEntries(stonecutterRecipe, recipeManager, absoluteEntries, listEntries, registryAccess);
                    return new ResolveResult(layoutKind, List.copyOf(absoluteEntries), List.copyOf(listEntries), "");
                }
                case SMITHING: {
                    if (!(recipe instanceof SmithingRecipe)) {
                        return ResolveResult.empty("Declared smithing but recipe is not SmithingRecipe");
                    }
                    SmithingRecipe smithingRecipe = (SmithingRecipe)recipe;
                    RecipeResolver.buildSmithingEntries(smithingRecipe, absoluteEntries, registryAccess);
                    return new ResolveResult(layoutKind, List.copyOf(absoluteEntries), List.copyOf(listEntries), "");
                }
                case FALLBACK: {
                    RecipeResolver.buildFallbackEntries(recipe, absoluteEntries, registryAccess);
                }
            }
            return new ResolveResult(layoutKind, List.copyOf(absoluteEntries), List.copyOf(listEntries), "");
        }

        private static void buildShapedCraftingEntries(ShapedRecipe recipe, List<PreviewEntry> output, RegistryAccess registryAccess) {
            int width = Math.max(1, recipe.m_44220_());
            int height = Math.max(1, recipe.m_44221_());
            NonNullList ingredients = recipe.m_7527_();
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 3; ++col) {
                    Ingredient ingredient = Ingredient.f_43901_;
                    int shapedIndex = row * width + col;
                    if (row < height && col < width && shapedIndex >= 0 && shapedIndex < ingredients.size()) {
                        ingredient = (Ingredient)ingredients.get(shapedIndex);
                    }
                    output.add(RecipeResolver.toIngredientEntryOrAir(ingredient, PreviewRole.INPUT));
                }
            }
            ItemStack result = recipe.m_8043_(registryAccess);
            output.add(RecipeResolver.toEntryOrAir(result, PreviewRole.OUTPUT));
        }

        private static void buildShapelessCraftingEntries(CraftingRecipe recipe, List<PreviewEntry> output, RegistryAccess registryAccess) {
            NonNullList ingredients = recipe.m_7527_();
            for (int slot = 0; slot < 9; ++slot) {
                Ingredient ingredient = slot < ingredients.size() ? (Ingredient)ingredients.get(slot) : Ingredient.f_43901_;
                output.add(RecipeResolver.toIngredientEntryOrAir(ingredient, PreviewRole.INPUT));
            }
            ItemStack result = recipe.m_8043_(registryAccess);
            output.add(RecipeResolver.toEntryOrAir(result, PreviewRole.OUTPUT));
        }

        private static void buildCookingEntries(AbstractCookingRecipe recipe, List<PreviewEntry> output, RegistryAccess registryAccess) {
            PreviewEntry inputEntry;
            NonNullList ingredients = recipe.m_7527_();
            if (!ingredients.isEmpty() && (inputEntry = RecipeResolver.toIngredientEntry((Ingredient)ingredients.get(0), PreviewRole.INPUT)) != null) {
                output.add(inputEntry);
            }
            PreviewEntry fuelEntry = RecipeResolver.toLiteralEntry(IngredientExpressionCompiler.furnaceFuelTagLiteral(), PreviewRole.FUEL);
            output.add(fuelEntry);
            ItemStack result = recipe.m_8043_(registryAccess);
            PreviewEntry resultEntry = RecipeResolver.toEntry(result, PreviewRole.OUTPUT);
            if (resultEntry != null) {
                output.add(resultEntry);
            }
        }

        private static void buildStonecuttingEntries(StonecutterRecipe recipe, RecipeManager recipeManager, List<PreviewEntry> absoluteOutput, List<PreviewEntry> listOutput, RegistryAccess registryAccess) {
            ItemStack result;
            ArrayList<ItemStack> outputs;
            PreviewEntry inputEntry;
            NonNullList ingredients = recipe.m_7527_();
            if (!ingredients.isEmpty() && (inputEntry = RecipeResolver.toIngredientEntry((Ingredient)ingredients.get(0), PreviewRole.INPUT)) != null) {
                absoluteOutput.add(inputEntry);
            }
            if ((outputs = RecipeResolver.collectStonecuttingOutputs(recipe, recipeManager, registryAccess)).isEmpty() && !(result = recipe.m_8043_(registryAccess)).m_41619_()) {
                outputs.add(result.m_41777_());
            }
            for (ItemStack stack : outputs) {
                PreviewEntry outputEntry = RecipeResolver.toEntry(stack, PreviewRole.OUTPUT);
                if (outputEntry == null) continue;
                listOutput.add(outputEntry);
            }
        }

        private static ArrayList<ItemStack> collectStonecuttingOutputs(StonecutterRecipe recipe, RecipeManager recipeManager, RegistryAccess registryAccess) {
            ArrayList<ItemStack> result = new ArrayList<ItemStack>();
            NonNullList ingredients = recipe.m_7527_();
            if (ingredients.isEmpty()) {
                return result;
            }
            Ingredient selectedIngredient = (Ingredient)ingredients.get(0);
            ItemStack selectedInput = RecipeResolver.pickDisplayStack(selectedIngredient);
            if (selectedInput.m_41619_()) {
                return result;
            }
            HashSet<ResourceLocation> dedup = new HashSet<ResourceLocation>();
            List candidates = recipeManager.m_44013_(RecipeType.f_44112_);
            for (StonecutterRecipe candidateRecipe : candidates) {
                ResourceLocation itemId;
                ItemStack candidateResult;
                Ingredient candidateIngredient;
                NonNullList candidateIngredients = candidateRecipe.m_7527_();
                if (candidateIngredients.isEmpty() || !(candidateIngredient = (Ingredient)candidateIngredients.get(0)).test(selectedInput) && !selectedIngredient.test(RecipeResolver.pickDisplayStack(candidateIngredient)) || (candidateResult = candidateRecipe.m_8043_(registryAccess)).m_41619_() || (itemId = BuiltInRegistries.f_257033_.m_7981_((Object)candidateResult.m_41720_())) == null || !dedup.add(itemId)) continue;
                result.add(candidateResult.m_41777_());
            }
            result.sort((a, b) -> {
                ResourceLocation ida = BuiltInRegistries.f_257033_.m_7981_((Object)a.m_41720_());
                ResourceLocation idb = BuiltInRegistries.f_257033_.m_7981_((Object)b.m_41720_());
                String sa = ida == null ? "" : ida.toString();
                String sb = idb == null ? "" : idb.toString();
                return sa.compareTo(sb);
            });
            return result;
        }

        private static void buildSmithingEntries(SmithingRecipe recipe, List<PreviewEntry> output, RegistryAccess registryAccess) {
            NonNullList ingredients = recipe.m_7527_();
            int limit = Math.min(3, ingredients.size());
            for (int index = 0; index < limit; ++index) {
                Ingredient ingredient = (Ingredient)ingredients.get(index);
                PreviewRole role = switch (index) {
                    case 0 -> PreviewRole.TEMPLATE;
                    case 1 -> PreviewRole.INPUT;
                    case 2 -> PreviewRole.ADDITION;
                    default -> PreviewRole.INPUT;
                };
                PreviewEntry entry = RecipeResolver.toIngredientEntry(ingredient, role);
                if (entry == null) continue;
                output.add(entry);
            }
            ItemStack result = recipe.m_8043_(registryAccess);
            PreviewEntry resultEntry = RecipeResolver.toEntry(result, PreviewRole.OUTPUT);
            if (resultEntry != null) {
                output.add(resultEntry);
            }
        }

        private static void buildFallbackEntries(Recipe<?> recipe, List<PreviewEntry> output, RegistryAccess registryAccess) {
            ItemStack result;
            PreviewEntry outputEntry;
            NonNullList ingredients = recipe.m_7527_();
            int visualIndex = 0;
            for (Ingredient ingredient : ingredients) {
                PreviewEntry inputEntry = RecipeResolver.toIngredientEntry(ingredient, PreviewRole.INPUT);
                if (inputEntry == null) continue;
                output.add(inputEntry);
                if (++visualIndex < 8) continue;
                break;
            }
            if ((outputEntry = RecipeResolver.toEntry(result = recipe.m_8043_(registryAccess), PreviewRole.OUTPUT)) != null) {
                output.add(outputEntry);
            }
        }

        private static ItemStack pickDisplayStack(Ingredient ingredient) {
            if (ingredient == null) {
                return ItemStack.f_41583_;
            }
            ItemStack[] options = ingredient.m_43908_();
            if (options == null) {
                return ItemStack.f_41583_;
            }
            for (ItemStack candidate : options) {
                if (candidate == null || candidate.m_41619_()) continue;
                return candidate.m_41777_();
            }
            return ItemStack.f_41583_;
        }

        private static PreviewEntry toEntry(ItemStack stack, PreviewRole role) {
            if (stack == null || stack.m_41619_()) {
                return null;
            }
            ResourceLocation itemId = BuiltInRegistries.f_257033_.m_7981_((Object)stack.m_41720_());
            if (itemId == null) {
                return null;
            }
            int count = Math.max(1, stack.m_41613_());
            String expression = ItemStackExpressionCompiler.withCount(itemId.toString(), count);
            return new PreviewEntry(role, expression);
        }

        private static PreviewEntry toLiteralEntry(String slotExpression, PreviewRole role) {
            if (slotExpression == null || slotExpression.isBlank()) {
                return null;
            }
            return new PreviewEntry(role, slotExpression);
        }

        private static PreviewEntry toEntryOrAir(ItemStack stack, PreviewRole role) {
            PreviewEntry direct = RecipeResolver.toEntry(stack, role);
            if (direct != null) {
                return direct;
            }
            return RecipeResolver.toLiteralEntry(AIR_ITEM_LITERAL, role);
        }

        private static PreviewEntry toIngredientEntry(Ingredient ingredient, PreviewRole role) {
            String expression = RecipeResolver.toIngredientExpression(ingredient);
            if (expression.isBlank()) {
                return null;
            }
            return new PreviewEntry(role, expression);
        }

        private static PreviewEntry toIngredientEntryOrAir(Ingredient ingredient, PreviewRole role) {
            PreviewEntry direct = RecipeResolver.toIngredientEntry(ingredient, role);
            if (direct != null) {
                return direct;
            }
            return RecipeResolver.toLiteralEntry(AIR_ITEM_LITERAL, role);
        }

        private static String toIngredientExpression(Ingredient ingredient) {
            if (ingredient == null || ingredient.m_43947_()) {
                return "";
            }
            try {
                return ingredient.m_43942_().toString();
            }
            catch (Exception exception) {
                ApricityUI.LOGGER.warn("[AUI Recipe] failed to serialize ingredient={}", (Object)ingredient, (Object)exception);
                return "";
            }
        }

        public record ResolveResult(LayoutKind layoutKind, List<PreviewEntry> absoluteEntries, List<PreviewEntry> listEntries, String message) {
            public static ResolveResult empty(String message) {
                return new ResolveResult(LayoutKind.FALLBACK, List.of(), List.of(), message == null ? "" : message);
            }
        }

        private record RecipeCacheKey(ResourceLocation recipeId, DeclaredType declaredType) {
        }

        /*
         * Uses 'sealed' constructs - enablewith --sealed true
         */
        public static enum DeclaredType {
            CRAFTING_SHAPED("crafting_shaped", LayoutKind.CRAFTING_SHAPED){

                @Override
                boolean matches(Recipe<?> recipe) {
                    return recipe instanceof ShapedRecipe;
                }
            }
            ,
            CRAFTING_SHAPELESS("crafting_shapeless", LayoutKind.CRAFTING_SHAPELESS){

                @Override
                boolean matches(Recipe<?> recipe) {
                    return recipe instanceof CraftingRecipe && !(recipe instanceof ShapedRecipe);
                }
            }
            ,
            SMELTING("smelting", LayoutKind.SMELTING_FAMILY){

                @Override
                boolean matches(Recipe<?> recipe) {
                    return recipe instanceof AbstractCookingRecipe && recipe.m_6671_() == RecipeType.f_44108_;
                }
            }
            ,
            BLASTING("blasting", LayoutKind.SMELTING_FAMILY){

                @Override
                boolean matches(Recipe<?> recipe) {
                    return recipe instanceof AbstractCookingRecipe && recipe.m_6671_() == RecipeType.f_44109_;
                }
            }
            ,
            SMOKING("smoking", LayoutKind.SMELTING_FAMILY){

                @Override
                boolean matches(Recipe<?> recipe) {
                    return recipe instanceof AbstractCookingRecipe && recipe.m_6671_() == RecipeType.f_44110_;
                }
            }
            ,
            CAMPFIRE_COOKING("campfire_cooking", LayoutKind.SMELTING_FAMILY){

                @Override
                boolean matches(Recipe<?> recipe) {
                    return recipe instanceof AbstractCookingRecipe && recipe.m_6671_() == RecipeType.f_44111_;
                }
            }
            ,
            STONECUTTING("stonecutting", LayoutKind.STONECUTTING){

                @Override
                boolean matches(Recipe<?> recipe) {
                    return recipe instanceof StonecutterRecipe;
                }
            }
            ,
            SMITHING("smithing", LayoutKind.SMITHING){

                @Override
                boolean matches(Recipe<?> recipe) {
                    return recipe instanceof SmithingRecipe;
                }
            }
            ,
            FALLBACK("fallback", LayoutKind.FALLBACK){

                @Override
                boolean matches(Recipe<?> recipe) {
                    return recipe != null;
                }
            };

            private final String id;
            private final LayoutKind layoutKind;

            private DeclaredType(String id, LayoutKind layoutKind) {
                this.id = id;
                this.layoutKind = layoutKind;
            }

            public String id() {
                return this.id;
            }

            public LayoutKind layoutKind() {
                return this.layoutKind;
            }

            abstract boolean matches(Recipe<?> var1);

            public static DeclaredType fromRaw(String raw) {
                if (raw == null || raw.isBlank()) {
                    return null;
                }
                String normalized = raw.trim().toLowerCase(Locale.ROOT);
                for (DeclaredType value : DeclaredType.values()) {
                    if (!value.id.equals(normalized)) continue;
                    return value;
                }
                return null;
            }
        }

        public static enum LayoutKind {
            CRAFTING_SHAPED,
            CRAFTING_SHAPELESS,
            SMELTING_FAMILY,
            STONECUTTING,
            SMITHING,
            FALLBACK;

        }

        public static enum PreviewRole {
            INPUT,
            FUEL,
            TEMPLATE,
            ADDITION,
            OUTPUT;

        }

        public record PreviewEntry(PreviewRole role, String slotExpression) {
        }

        @Mod.EventBusSubscriber(modid="apricityui", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.FORGE)
        public static class ForgeEvents {
            @SubscribeEvent
            public static void onRecipesUpdated(RecipesUpdatedEvent event) {
                RecipeResolver.clearCache();
                IngredientExpressionCompiler.clearTagCache();
            }
        }
    }
}

