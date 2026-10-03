/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.ShapelessRecipe
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.recipe.special;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeResultCallback;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.recipe.special.KubeJSCraftingRecipe;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;

public class ShapelessKubeJSRecipe
extends ShapelessRecipe
implements KubeJSCraftingRecipe {
    private final List<IngredientAction> ingredientActions;
    private final ModifyRecipeResultCallback modifyResult;
    private final String stage;

    public ShapelessKubeJSRecipe(ShapelessRecipe original, List<IngredientAction> ingredientActions, @Nullable ModifyRecipeResultCallback modifyResult, String stage) {
        super(original.m_6423_(), original.m_6076_(), original.m_245232_(), original.f_44243_, original.m_7527_());
        this.ingredientActions = ingredientActions;
        this.modifyResult = modifyResult;
        this.stage = stage;
    }

    public RecipeSerializer<?> m_7707_() {
        return KubeJSRecipeEventHandler.SHAPELESS.get();
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

    public static class SerializerKJS
    implements RecipeSerializer<ShapelessKubeJSRecipe> {
        private static final RecipeSerializer<ShapelessRecipe> SHAPELESS = (RecipeSerializer)UtilsJS.cast(RegistryInfo.RECIPE_SERIALIZER.getValue(new ResourceLocation("crafting_shapeless")));

        public ShapelessKubeJSRecipe fromJson(ResourceLocation id, JsonObject json) {
            ShapelessRecipe shapelessRecipe = (ShapelessRecipe)SHAPELESS.m_6729_(id, json);
            List<IngredientAction> ingredientActions = IngredientAction.parseList(json.get("kubejs:actions"));
            ModifyRecipeResultCallback modifyResult = null;
            if (json.has("kubejs:modify_result")) {
                modifyResult = RecipesEventJS.MODIFY_RESULT_CALLBACKS.get(id);
            }
            String stage = GsonHelper.m_13851_((JsonObject)json, (String)"kubejs:stage", (String)"");
            return new ShapelessKubeJSRecipe(shapelessRecipe, ingredientActions, modifyResult, stage);
        }

        public ShapelessKubeJSRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            ShapelessRecipe shapelessRecipe = (ShapelessRecipe)SHAPELESS.m_8005_(id, buf);
            byte flags = buf.readByte();
            List<IngredientAction> ingredientActions = (flags & 1) != 0 ? IngredientAction.readList(buf) : List.of();
            String stage = (flags & 2) != 0 ? buf.m_130277_() : "";
            return new ShapelessKubeJSRecipe(shapelessRecipe, ingredientActions, null, stage);
        }

        public void toNetwork(FriendlyByteBuf buf, ShapelessKubeJSRecipe r) {
            SHAPELESS.m_6178_(buf, (Recipe)r);
            int flags = 0;
            if (r.ingredientActions != null && !r.ingredientActions.isEmpty()) {
                flags |= 1;
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

