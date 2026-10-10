/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.compat.taczmagazines;

import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface MagazineCompat {
    public boolean available();

    public Optional<Identity> identity(ItemStack var1);

    public int ammoCount(ItemStack var1);

    public ItemStack createFull(ItemStack var1);

    public record Identity(String family, ResourceLocation ammoId, int capacity) {
        public Identity {
            String string = family = family == null ? "" : family;
            if (ammoId == null) {
                ammoId = ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"air");
            }
            capacity = Math.max(0, capacity);
        }
    }
}

