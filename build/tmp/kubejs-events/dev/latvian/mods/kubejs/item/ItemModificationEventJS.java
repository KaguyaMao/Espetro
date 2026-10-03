/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.crafting.Ingredient
 */
package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

@Info(value="Invoked after all items are registered to modify them.\n")
public class ItemModificationEventJS
extends EventJS {
    @Info(value="Modifies items matching the given ingredient.\n\n**NOTE**: tag ingredients are not supported at this time.\n")
    public void modify(Ingredient in, Consumer<Item> c) {
        for (Item item : in.kjs$getItemTypes()) {
            c.accept(item);
        }
    }
}

