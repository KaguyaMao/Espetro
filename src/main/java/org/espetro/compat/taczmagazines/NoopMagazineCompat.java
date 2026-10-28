package org.espetro.compat.taczmagazines;

import net.minecraft.world.item.ItemStack;

import java.util.Optional;

final class NoopMagazineCompat implements MagazineCompat {
    static final NoopMagazineCompat INSTANCE = new NoopMagazineCompat();

    private NoopMagazineCompat() {
    }

    @Override
    public boolean available() {
        return false;
    }

    @Override
    public Optional<Identity> identity(ItemStack stack) {
        return Optional.empty();
    }

    @Override
    public int ammoCount(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean setAmmoCount(ItemStack stack, int count) {
        return false;
    }

    @Override
    public boolean sameMagazineType(Identity candidate, Identity required) {
        return candidate.equals(required);
    }

    @Override
    public boolean bindAmmo(ItemStack stack, net.minecraft.resources.ResourceLocation ammoId) {
        return false;
    }

    @Override
    public ItemStack createFull(ItemStack template) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack storedMagazine(ItemStack gun) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean setStoredMagazine(ItemStack gun, ItemStack magazine) {
        return false;
    }
}
