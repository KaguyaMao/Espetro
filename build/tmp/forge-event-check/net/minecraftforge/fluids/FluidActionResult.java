/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.fluids;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FluidActionResult {
    public static final FluidActionResult FAILURE = new FluidActionResult(false, ItemStack.f_41583_);
    public final boolean success;
    @NotNull
    public final ItemStack result;

    public FluidActionResult(@NotNull ItemStack result) {
        this(true, result);
    }

    private FluidActionResult(boolean success, @NotNull ItemStack result) {
        this.success = success;
        this.result = result;
    }

    public boolean isSuccess() {
        return this.success;
    }

    @NotNull
    public ItemStack getResult() {
        return this.result;
    }
}

