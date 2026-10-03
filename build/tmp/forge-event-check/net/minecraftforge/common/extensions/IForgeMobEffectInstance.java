/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.ItemStack
 */
package net.minecraftforge.common.extensions;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public interface IForgeMobEffectInstance {
    public List<ItemStack> getCurativeItems();

    default public boolean isCurativeItem(ItemStack stack) {
        return this.getCurativeItems().stream().anyMatch(e -> ItemStack.m_41656_((ItemStack)e, (ItemStack)stack));
    }

    public void setCurativeItems(List<ItemStack> var1);

    default public void addCurativeItem(ItemStack stack) {
        if (!this.isCurativeItem(stack)) {
            this.getCurativeItems().add(stack);
        }
    }

    default public void writeCurativeItems(CompoundTag nbt) {
        ListTag list = new ListTag();
        this.getCurativeItems().forEach(s -> list.add((Object)s.m_41739_(new CompoundTag())));
        nbt.m_128365_("CurativeItems", (Tag)list);
    }
}

