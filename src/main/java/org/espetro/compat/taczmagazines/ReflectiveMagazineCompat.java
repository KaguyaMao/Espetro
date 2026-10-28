package org.espetro.compat.taczmagazines;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

/** Reflection against the public common-side API shared by 0.1.6 and 0.2.x. */
final class ReflectiveMagazineCompat implements MagazineCompat {
    private static final String MAGAZINE_ITEM =
        "com.raiiiden.taczmagazines.item.MagazineItem";
    /** 与 {@code GunMagazineCapability.TAG_KEY} 一致：枪内弹匣的 NBT 键。 */
    private static final String STORED_MAGAZINE_TAG = "TaCZMag_StoredMagazine";
    /** Mirrors {@code com.tacz.guns.api.DefaultAssets.EMPTY_AMMO_ID} for TaCZ 1.1.x. */
    private static final ResourceLocation FALLBACK_EMPTY_AMMO =
        ResourceLocation.fromNamespaceAndPath("tacz", "empty");

    private final Class<?> magazineClass;
    private final Method family;
    private final Method capacity;
    private final Method ammoId;
    private final Method ammoCount;
    private final Method createFour;
    private final Method createThree;
    private final Method setAmmoId;
    private final Method setAmmoCount;
    private final ResourceLocation emptyAmmoId;

    ReflectiveMagazineCompat() throws ReflectiveOperationException {
        magazineClass = Class.forName(MAGAZINE_ITEM, false,
            ReflectiveMagazineCompat.class.getClassLoader());
        family = magazineClass.getMethod("getMagazineFamilyId", ItemStack.class);
        capacity = magazineClass.getMethod("getMaxCapacity", ItemStack.class);
        ammoId = magazineClass.getMethod("getAmmoId", ItemStack.class);
        ammoCount = magazineClass.getMethod("getAmmoCount", ItemStack.class);
        setAmmoId = magazineClass.getMethod("setAmmoId", ItemStack.class,
            ResourceLocation.class);
        setAmmoCount = magazineClass.getMethod("setAmmoCount", ItemStack.class, int.class);

        Method four = null;
        Method three = null;
        for (Method method : magazineClass.getMethods()) {
            if (!method.getName().equals("createMagazineByFamily")
                || !Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.getParameterCount() == 4) four = method;
            if (method.getParameterCount() == 3) three = method;
        }
        createFour = four;
        createThree = three;
        if (createFour == null && createThree == null) {
            throw new NoSuchMethodException("MagazineItem.createMagazineByFamily");
        }
        emptyAmmoId = resolveEmptyAmmoId();
    }

    private static ResourceLocation resolveEmptyAmmoId() {
        try {
            Class<?> assets = Class.forName("com.tacz.guns.api.DefaultAssets", false,
                ReflectiveMagazineCompat.class.getClassLoader());
            Object value = assets.getField("EMPTY_AMMO_ID").get(null);
            if (value instanceof ResourceLocation location) return location;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // A future TaCZ may move the constant; the literal matches every 1.1.x build.
        }
        return FALLBACK_EMPTY_AMMO;
    }

    @Override
    public boolean available() {
        return true;
    }

    @Override
    public ItemStack storedMagazine(ItemStack gun) {
        if (gun == null || gun.isEmpty() || gun.getTag() == null) return ItemStack.EMPTY;
        if (!gun.getTag().contains(STORED_MAGAZINE_TAG, net.minecraft.nbt.Tag.TAG_COMPOUND)) {
            return ItemStack.EMPTY;
        }
        try {
            net.minecraft.nbt.CompoundTag tag = gun.getTag().getCompound(STORED_MAGAZINE_TAG);
            if (tag.isEmpty()) return ItemStack.EMPTY;
            ItemStack stored = ItemStack.of(tag);
            return stored != null && magazineClass.isInstance(stored.getItem())
                ? stored : ItemStack.EMPTY;
        } catch (RuntimeException ignored) {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean setStoredMagazine(ItemStack gun, ItemStack magazine) {
        if (gun == null || gun.isEmpty() || magazine == null || magazine.isEmpty()) return false;
        try {
            net.minecraft.nbt.CompoundTag stored = magazine.save(new net.minecraft.nbt.CompoundTag());
            gun.getOrCreateTag().put(STORED_MAGAZINE_TAG, stored);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    @Override
    public Optional<Identity> identity(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !magazineClass.isInstance(stack.getItem())) {
            return Optional.empty();
        }
        try {
            String familyId = (String) family.invoke(null, stack);
            int max = (Integer) capacity.invoke(null, stack);
            ResourceLocation ammunition = (ResourceLocation) invokeOnItem(ammoId, stack);
            if (familyId == null || familyId.isBlank() || ammunition == null || max <= 0) {
                return Optional.empty();
            }
            return Optional.of(new Identity(familyId, ammunition, max));
        } catch (ReflectiveOperationException | ClassCastException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public int ammoCount(ItemStack stack) {
        if (identity(stack).isEmpty()) return 0;
        try {
            return Math.max(0, (Integer) invokeOnItem(ammoCount, stack));
        } catch (ReflectiveOperationException | ClassCastException ignored) {
            return 0;
        }
    }

    @Override
    public boolean setAmmoCount(ItemStack stack, int count) {
        Optional<Identity> resolved = identity(stack);
        if (resolved.isEmpty()) return false;
        int clamped = Math.max(0, Math.min(resolved.get().capacity(), count));
        try {
            invokeOnItem(setAmmoCount, stack, clamped);
            return true;
        } catch (ReflectiveOperationException | ClassCastException ignored) {
            return false;
        }
    }

    @Override
    public boolean sameMagazineType(Identity candidate, Identity required) {
        if (candidate == null || required == null) return false;
        if (!candidate.family().equals(required.family())
            || candidate.capacity() != required.capacity()) {
            return false;
        }
        // An emptied magazine lost its AmmoId (tacz:empty) and may be refilled with
        // the catalog's ammo again; a magazine that still holds a different caliber
        // must not be touched.
        return emptyAmmoId.equals(candidate.ammoId()) || candidate.ammoId().equals(required.ammoId());
    }

    @Override
    public boolean bindAmmo(ItemStack stack, ResourceLocation target) {
        if (target == null || target.equals(emptyAmmoId) || identity(stack).isEmpty()) {
            return false;
        }
        try {
            invokeOnItem(setAmmoId, stack, target);
            Object written = invokeOnItem(ammoId, stack);
            return target.equals(written);
        } catch (ReflectiveOperationException | ClassCastException ignored) {
            return false;
        }
    }

    @Override
    public ItemStack createFull(ItemStack template) {
        Optional<Identity> resolved = identity(template);
        if (resolved.isEmpty()) return ItemStack.EMPTY;
        Identity id = resolved.get();
        try {
            ItemStack created;
            if (createFour != null) {
                created = (ItemStack) createFour.invoke(null, template.getItem(), id.family(),
                    id.capacity(), id.ammoId());
            } else {
                created = (ItemStack) createThree.invoke(null, template.getItem(), id.family(),
                    id.capacity());
                invokeOnItem(setAmmoId, created, id.ammoId());
            }
            if (created == null || created.isEmpty()) return ItemStack.EMPTY;
            invokeOnItem(setAmmoCount, created, id.capacity());
            created.setCount(1);
            return created;
        } catch (ReflectiveOperationException | ClassCastException ignored) {
            return ItemStack.EMPTY;
        }
    }

    private Object invokeOnItem(Method method, ItemStack stack, Object... extra)
            throws ReflectiveOperationException {
        Object receiver = Modifier.isStatic(method.getModifiers()) ? null : stack.getItem();
        Object[] args = new Object[1 + extra.length];
        args[0] = stack;
        System.arraycopy(extra, 0, args, 1, extra.length);
        return method.invoke(receiver, args);
    }
}
