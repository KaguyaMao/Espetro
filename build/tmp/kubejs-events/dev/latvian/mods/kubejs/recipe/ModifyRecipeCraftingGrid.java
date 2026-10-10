/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.inventory.CraftingMenu
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.core.CraftingContainerKJS;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class ModifyRecipeCraftingGrid {
    private final CraftingContainer container;

    public ModifyRecipeCraftingGrid(CraftingContainer c) {
        this.container = c;
    }

    public ItemStack get(int index) {
        return this.container.m_8020_(index).m_41777_();
    }

    public List<ItemStack> findAll(Ingredient ingredient) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (int i = 0; i < this.container.m_6643_(); ++i) {
            ItemStack stack = this.container.m_8020_(i);
            if (!ingredient.test(stack)) continue;
            list.add(stack.m_41777_());
        }
        return list;
    }

    public List<ItemStack> findAll() {
        return this.findAll(IngredientPlatformHelper.get().wildcard());
    }

    public ItemStack find(Ingredient ingredient, int skip) {
        for (int i = 0; i < this.container.m_6643_(); ++i) {
            ItemStack stack = this.container.m_8020_(i);
            if (!ingredient.test(stack)) continue;
            if (skip > 0) {
                --skip;
                continue;
            }
            return stack.m_41777_();
        }
        return ItemStack.f_41583_;
    }

    public ItemStack find(Ingredient ingredient) {
        return this.find(ingredient, 0);
    }

    public int getWidth() {
        return this.container.m_39347_();
    }

    public int getHeight() {
        return this.container.m_39346_();
    }

    @Nullable
    public AbstractContainerMenu getMenu() {
        return ((CraftingContainerKJS)this.container).kjs$getMenu();
    }

    @Nullable
    public Player getPlayer() {
        AbstractContainerMenu abstractContainerMenu = this.getMenu();
        if (abstractContainerMenu instanceof CraftingMenu) {
            CraftingMenu menu = (CraftingMenu)abstractContainerMenu;
            if (menu.f_39351_ != null) {
                return menu.f_39351_;
            }
        }
        if ((abstractContainerMenu = this.getMenu()) instanceof InventoryMenu) {
            InventoryMenu menu = (InventoryMenu)abstractContainerMenu;
            if (menu.f_39703_ != null) {
                return menu.f_39703_;
            }
        }
        return null;
    }
}

