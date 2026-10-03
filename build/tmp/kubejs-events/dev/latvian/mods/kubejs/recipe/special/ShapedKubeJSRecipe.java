/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.ShapedRecipe
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.recipe.special;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeResultCallback;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.recipe.special.KubeJSCraftingRecipe;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.List;
import java.util.Map;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ShapedKubeJSRecipe
extends ShapedRecipe
implements KubeJSCraftingRecipe {
    private final boolean mirror;
    private final List<IngredientAction> ingredientActions;
    private final ModifyRecipeResultCallback modifyResult;
    private final String stage;

    public ShapedKubeJSRecipe(ResourceLocation id, String group, CraftingBookCategory category, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result, boolean mirror, List<IngredientAction> ingredientActions, @Nullable ModifyRecipeResultCallback modifyResult, String stage) {
        super(id, group, category, width, height, ingredients, result);
        this.mirror = mirror;
        this.ingredientActions = ingredientActions;
        this.modifyResult = modifyResult;
        this.stage = stage;
    }

    public RecipeSerializer<?> m_7707_() {
        return KubeJSRecipeEventHandler.SHAPED.get();
    }

    @Override
    public List<IngredientAction> kjs$getIngredientActions() {
        return this.ingredientActions;
    }

    @Override
    @Nullable
    public ModifyRecipeResultCallback kjs$getModifyResult() {
        return this.modifyResult;
    }

    @Override
    public String kjs$getStage() {
        return this.stage;
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        return this.kjs$getRemainingItems(container);
    }

    public ItemStack m_5874_(CraftingContainer container, RegistryAccess registryAccess) {
        return this.kjs$assemble(container, registryAccess);
    }

    public boolean m_5818_(CraftingContainer craftingContainer, Level level) {
        for (int i = 0; i <= craftingContainer.m_39347_() - this.f_44146_; ++i) {
            for (int j = 0; j <= craftingContainer.m_39346_() - this.f_44147_; ++j) {
                if (this.mirror && this.m_44170_(craftingContainer, i, j, true)) {
                    return true;
                }
                if (!this.m_44170_(craftingContainer, i, j, false)) continue;
                return true;
            }
        }
        return false;
    }

    public static class SerializerKJS
    implements RecipeSerializer<ShapedKubeJSRecipe> {
        private static final RecipeSerializer<ShapedRecipe> SHAPED = (RecipeSerializer)UtilsJS.cast(RegistryInfo.RECIPE_SERIALIZER.getValue(new ResourceLocation("crafting_shaped")));

        public ShapedKubeJSRecipe fromJson(ResourceLocation id, JsonObject json) {
            ShapedRecipe shapedRecipe = (ShapedRecipe)SHAPED.m_6729_(id, json);
            boolean mirror = GsonHelper.m_13855_((JsonObject)json, (String)"kubejs:mirror", (boolean)true);
            boolean shrink = GsonHelper.m_13855_((JsonObject)json, (String)"kubejs:shrink", (boolean)true);
            Map key = ShapedRecipe.m_44210_((JsonObject)GsonHelper.m_13930_((JsonObject)json, (String)"key"));
            String[] pattern = ShapedRecipe.m_44196_((JsonArray)GsonHelper.m_13933_((JsonObject)json, (String)"pattern"));
            if (shrink) {
                pattern = ShapedRecipe.m_44186_((String[])pattern);
            }
            int w = pattern[0].length();
            int h = pattern.length;
            NonNullList ingredients = ShapedRecipe.m_44202_((String[])pattern, (Map)key, (int)w, (int)h);
            List<IngredientAction> ingredientActions = IngredientAction.parseList(json.get("kubejs:actions"));
            ModifyRecipeResultCallback modifyResult = null;
            if (json.has("kubejs:modify_result")) {
                modifyResult = RecipesEventJS.MODIFY_RESULT_CALLBACKS.get(id);
            }
            String stage = GsonHelper.m_13851_((JsonObject)json, (String)"kubejs:stage", (String)"");
            return new ShapedKubeJSRecipe(id, shapedRecipe.m_6076_(), shapedRecipe.m_245232_(), w, h, (NonNullList<Ingredient>)ingredients, shapedRecipe.f_44149_, mirror, ingredientActions, modifyResult, stage);
        }

        public ShapedKubeJSRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            ShapedRecipe shapedRecipe = (ShapedRecipe)SHAPED.m_8005_(id, buf);
            byte flags = buf.readByte();
            String group = shapedRecipe.m_6076_();
            CraftingBookCategory category = shapedRecipe.m_245232_();
            int width = shapedRecipe.m_44220_();
            int height = shapedRecipe.m_44221_();
            NonNullList ingredients = shapedRecipe.m_7527_();
            ItemStack result = shapedRecipe.f_44149_;
            List<IngredientAction> ingredientActions = (flags & 1) != 0 ? IngredientAction.readList(buf) : List.of();
            String stage = (flags & 2) != 0 ? buf.m_130277_() : "";
            boolean mirror = (flags & 4) != 0;
            return new ShapedKubeJSRecipe(id, group, category, width, height, (NonNullList<Ingredient>)ingredients, result, mirror, ingredientActions, null, stage);
        }

        public void toNetwork(FriendlyByteBuf buf, ShapedKubeJSRecipe r) {
            SHAPED.m_6178_(buf, (Recipe)r);
            int flags = 0;
            if (r.ingredientActions != null && !r.ingredientActions.isEmpty()) {
                flags |= 1;
            }
            if (r.mirror) {
                flags |= 4;
            }
            if (!r.stage.isEmpty()) {
                flags |= 2;
            }
            buf.writeByte(flags);
            if (r.ingredientActions != null && !r.ingredientActions.isEmpty()) {
                IngredientAction.writeList(buf, r.ingredientActions);
            }
            if (!r.stage.isEmpty()) {
                buf.m_130070_(r.stage);
            }
        }
    }
}

