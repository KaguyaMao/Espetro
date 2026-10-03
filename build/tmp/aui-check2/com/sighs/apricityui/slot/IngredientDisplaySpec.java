/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.sighs.apricityui.slot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public record IngredientDisplaySpec(List<ItemStack> candidates, boolean cycleEnabled, long cycleIntervalMs) {
    public static final long DEFAULT_CYCLE_INTERVAL_MS = 1000L;
    public static final IngredientDisplaySpec EMPTY = new IngredientDisplaySpec(List.of(), false, 1000L);

    public IngredientDisplaySpec {
        ArrayList<ItemStack> safeCandidates = new ArrayList<ItemStack>();
        if (candidates != null) {
            for (ItemStack stack : candidates) {
                if (stack == null || stack.m_41619_()) continue;
                safeCandidates.add(stack.m_41777_());
            }
        }
        candidates = Collections.unmodifiableList(safeCandidates);
        cycleIntervalMs = Math.max(200L, cycleIntervalMs);
    }

    public boolean hasCandidates() {
        return !this.candidates.isEmpty();
    }
}

