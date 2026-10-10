/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 */
package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@FunctionalInterface
public interface CreativeTabIconSupplier {
    public static final CreativeTabIconSupplier DEFAULT = () -> ItemStack.f_41583_;

    public ItemStack getIcon();

    public record Wrapper(CreativeTabIconSupplier supplier) implements Supplier<ItemStack>
    {
        @Override
        public ItemStack get() {
            try {
                ItemStack i = ItemStackJS.of(this.supplier.getIcon());
                return i.m_41619_() ? Items.f_42493_.m_7968_() : i;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return Items.f_42493_.m_7968_();
            }
        }
    }
}

