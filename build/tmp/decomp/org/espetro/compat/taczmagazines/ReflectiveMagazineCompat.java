/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.compat.taczmagazines;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.espetro.compat.taczmagazines.MagazineCompat;

final class ReflectiveMagazineCompat
implements MagazineCompat {
    private static final String MAGAZINE_ITEM = "com.raiiiden.taczmagazines.item.MagazineItem";
    private final Class<?> magazineClass = Class.forName("com.raiiiden.taczmagazines.item.MagazineItem", false, ReflectiveMagazineCompat.class.getClassLoader());
    private final Method family = this.magazineClass.getMethod("getMagazineFamilyId", ItemStack.class);
    private final Method capacity = this.magazineClass.getMethod("getMaxCapacity", ItemStack.class);
    private final Method ammoId = this.magazineClass.getMethod("getAmmoId", ItemStack.class);
    private final Method ammoCount = this.magazineClass.getMethod("getAmmoCount", ItemStack.class);
    private final Method createFour;
    private final Method createThree;
    private final Method setAmmoId = this.magazineClass.getMethod("setAmmoId", ItemStack.class, ResourceLocation.class);
    private final Method setAmmoCount = this.magazineClass.getMethod("setAmmoCount", ItemStack.class, Integer.TYPE);

    ReflectiveMagazineCompat() throws ReflectiveOperationException {
        Method four = null;
        Method three = null;
        for (Method method : this.magazineClass.getMethods()) {
            if (!method.getName().equals("createMagazineByFamily") || !Modifier.isStatic(method.getModifiers())) continue;
            if (method.getParameterCount() == 4) {
                four = method;
            }
            if (method.getParameterCount() != 3) continue;
            three = method;
        }
        this.createFour = four;
        this.createThree = three;
        if (this.createFour == null && this.createThree == null) {
            throw new NoSuchMethodException("MagazineItem.createMagazineByFamily");
        }
    }

    @Override
    public boolean available() {
        return true;
    }

    @Override
    public Optional<MagazineCompat.Identity> identity(ItemStack stack) {
        if (stack == null || stack.m_41619_() || !this.magazineClass.isInstance(stack.m_41720_())) {
            return Optional.empty();
        }
        try {
            String familyId = (String)this.family.invoke(null, stack);
            int max = (Integer)this.capacity.invoke(null, stack);
            ResourceLocation ammunition = (ResourceLocation)this.invokeOnItem(this.ammoId, stack, new Object[0]);
            if (familyId == null || familyId.isBlank() || ammunition == null || max <= 0) {
                return Optional.empty();
            }
            return Optional.of(new MagazineCompat.Identity(familyId, ammunition, max));
        }
        catch (ClassCastException | ReflectiveOperationException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public int ammoCount(ItemStack stack) {
        if (this.identity(stack).isEmpty()) {
            return 0;
        }
        try {
            return Math.max(0, (Integer)this.invokeOnItem(this.ammoCount, stack, new Object[0]));
        }
        catch (ClassCastException | ReflectiveOperationException ignored) {
            return 0;
        }
    }

    @Override
    public ItemStack createFull(ItemStack template) {
        Optional<MagazineCompat.Identity> resolved = this.identity(template);
        if (resolved.isEmpty()) {
            return ItemStack.f_41583_;
        }
        MagazineCompat.Identity id = resolved.get();
        try {
            ItemStack created;
            if (this.createFour != null) {
                created = (ItemStack)this.createFour.invoke(null, template.m_41720_(), id.family(), id.capacity(), id.ammoId());
            } else {
                created = (ItemStack)this.createThree.invoke(null, template.m_41720_(), id.family(), id.capacity());
                this.invokeOnItem(this.setAmmoId, created, id.ammoId());
            }
            if (created == null || created.m_41619_()) {
                return ItemStack.f_41583_;
            }
            this.invokeOnItem(this.setAmmoCount, created, id.capacity());
            created.m_41764_(1);
            return created;
        }
        catch (ClassCastException | ReflectiveOperationException ignored) {
            return ItemStack.f_41583_;
        }
    }

    private Object invokeOnItem(Method method, ItemStack stack, Object ... extra) throws ReflectiveOperationException {
        Item receiver = Modifier.isStatic(method.getModifiers()) ? null : stack.m_41720_();
        Object[] args = new Object[1 + extra.length];
        args[0] = stack;
        System.arraycopy(extra, 0, args, 1, extra.length);
        return method.invoke(receiver, args);
    }
}

