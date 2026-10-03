/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.CreativeModeTab$DisplayItemsGenerator
 *  net.minecraft.world.item.CreativeModeTab$ItemDisplayParameters
 *  net.minecraft.world.item.CreativeModeTab$Output
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 */
package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import java.util.Arrays;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@FunctionalInterface
public interface CreativeTabContentSupplier {
    public static final CreativeTabContentSupplier DEFAULT = showRestrictedItems -> new ItemStack[0];

    public ItemStack[] getContent(boolean var1);

    public record Wrapper(CreativeTabContentSupplier supplier) implements CreativeModeTab.DisplayItemsGenerator
    {
        public void m_257865_(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
            List<Object> items = List.of();
            try {
                items = Arrays.stream(this.supplier.getContent(itemDisplayParameters.f_268429_())).map(ItemStackJS::of).filter(is -> !is.m_41619_()).toList();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            if (items.isEmpty()) {
                output.m_246342_(Items.f_42516_.m_7968_().kjs$withName((Component)Component.m_237113_((String)"Use .content(showRestrictedItems => ['kubejs:example']) to add more items!")));
            } else {
                for (ItemStack item : items) {
                    output.m_246342_(item);
                }
            }
        }
    }
}

