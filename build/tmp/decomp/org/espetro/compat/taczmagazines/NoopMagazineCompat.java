/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.compat.taczmagazines;

import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import org.espetro.compat.taczmagazines.MagazineCompat;

final class NoopMagazineCompat
implements MagazineCompat {
    static final NoopMagazineCompat INSTANCE = new NoopMagazineCompat();

    private NoopMagazineCompat() {
    }

    @Override
    public boolean available() {
        return false;
    }

    @Override
    public Optional<MagazineCompat.Identity> identity(ItemStack stack) {
        return Optional.empty();
    }

    @Override
    public int ammoCount(ItemStack stack) {
        return 0;
    }

    @Override
    public ItemStack createFull(ItemStack template) {
        return ItemStack.f_41583_;
    }
}

