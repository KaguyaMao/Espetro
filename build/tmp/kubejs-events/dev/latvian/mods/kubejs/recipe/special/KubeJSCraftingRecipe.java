/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.utils.GameInstance
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.inventory.CraftingMenu
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.recipe.special;

import dev.architectury.utils.GameInstance;
import dev.latvian.mods.kubejs.core.CraftingContainerKJS;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeCraftingGrid;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeResultCallback;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.jetbrains.annotations.Nullable;

public interface KubeJSCraftingRecipe
extends CraftingRecipe {
    public List<IngredientAction> kjs$getIngredientActions();

    @Nullable
    public ModifyRecipeResultCallback kjs$getModifyResult();

    public String kjs$getStage();

    default public NonNullList<ItemStack> kjs$getRemainingItems(CraftingContainer container) {
        NonNullList list = NonNullList.m_122780_((int)container.m_6643_(), (Object)ItemStack.f_41583_);
        for (int i = 0; i < list.size(); ++i) {
            list.set(i, (Object)IngredientAction.getRemaining(container, i, this.kjs$getIngredientActions()));
        }
        return list;
    }

    default public ItemStack kjs$assemble(CraftingContainer container, RegistryAccess registryAccess) {
        Player player;
        if (!(this.kjs$getStage().isEmpty() || (player = KubeJSCraftingRecipe.getPlayer(((CraftingContainerKJS)container).kjs$getMenu())) != null && player.kjs$getStages().has(this.kjs$getStage()))) {
            return ItemStack.f_41583_;
        }
        ModifyRecipeResultCallback modifyResult = this.kjs$getModifyResult();
        ItemStack result = this.m_8043_(registryAccess);
        ItemStack itemStack = result = result == null || result.m_41619_() ? ItemStack.f_41583_ : result.m_41777_();
        if (modifyResult != null) {
            return modifyResult.modify(new ModifyRecipeCraftingGrid(container), result);
        }
        return result;
    }

    @Nullable
    private static Player getPlayer(AbstractContainerMenu menu) {
        if (menu instanceof CraftingMenu) {
            CraftingMenu craft = (CraftingMenu)menu;
            return craft.f_39351_;
        }
        if (menu instanceof InventoryMenu) {
            InventoryMenu inv = (InventoryMenu)menu;
            return inv.f_39703_;
        }
        MinecraftServer server = GameInstance.getServer();
        if (server != null) {
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                if (player.f_36096_ != menu || !menu.m_6875_((Player)player)) continue;
                return player;
            }
        }
        return null;
    }
}

