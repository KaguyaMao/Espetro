/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.constants.VanillaTypes
 *  mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter
 *  mezz.jei.api.ingredients.subtypes.UidContext
 *  mezz.jei.api.registration.ISubtypeRegistration
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 */
package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.event.EventJS;
import java.util.function.Function;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class JEISubtypesEventJS
extends EventJS {
    private final ISubtypeRegistration registration;

    public JEISubtypesEventJS(ISubtypeRegistration r) {
        this.registration = r;
    }

    public void registerInterpreter(Item item, Interpreter interpreter) {
        this.registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, (Object)item, (stack, context) -> {
            Object o = interpreter.apply(stack);
            return o == null ? "" : o.toString();
        });
    }

    public void useNBT(Ingredient items) {
        this.registration.useNbtForSubtypes(items.kjs$getItemTypes().toArray(new Item[0]));
    }

    public void useNBTKey(Ingredient items, String key) {
        NBTKeyInterpreter in = new NBTKeyInterpreter(key);
        for (Item item : items.kjs$getItemTypes()) {
            this.registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, (Object)item, (IIngredientSubtypeInterpreter)in);
        }
    }

    @FunctionalInterface
    public static interface Interpreter
    extends Function<ItemStack, Object> {
    }

    private static class NBTKeyInterpreter
    implements IIngredientSubtypeInterpreter<ItemStack> {
        private final String key;

        private NBTKeyInterpreter(String k) {
            this.key = k;
        }

        public String apply(ItemStack stack, UidContext context) {
            CompoundTag nbt = stack.m_41783_();
            if (nbt == null || !nbt.m_128441_(this.key)) {
                return "";
            }
            return String.valueOf(nbt.m_128423_(this.key));
        }
    }
}

