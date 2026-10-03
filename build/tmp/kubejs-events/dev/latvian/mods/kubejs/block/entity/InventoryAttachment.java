/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.Container
 *  net.minecraft.world.Containers
 *  net.minecraft.world.SimpleContainer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachment;
import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachmentType;
import dev.latvian.mods.kubejs.block.entity.BlockEntityJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.typings.desc.PrimitiveDescJS;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class InventoryAttachment
extends SimpleContainer
implements BlockEntityAttachment {
    public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType("inventory", TypeDescJS.object().add("xsize", TypeDescJS.NUMBER, false).add("ysize", TypeDescJS.NUMBER, false).add("inputFilter", new PrimitiveDescJS("Ingredient"), true), map -> {
        int width = ((Number)map.get("width")).intValue();
        int height = ((Number)map.get("height")).intValue();
        Ingredient inputFilter = map.containsKey("inputFilter") ? IngredientJS.of(map.get("inputFilter")) : null;
        return entity -> new InventoryAttachment(entity, width, height, inputFilter);
    });
    public final int width;
    public final int height;
    public final BlockEntityJS blockEntity;
    public final Ingredient inputFilter;

    public InventoryAttachment(BlockEntityJS blockEntity, int width, int height, @Nullable Ingredient inputFilter) {
        super(width * height);
        this.width = width;
        this.height = height;
        this.blockEntity = blockEntity;
        this.inputFilter = inputFilter;
    }

    public void m_6596_() {
        super.m_6596_();
        this.blockEntity.save();
    }

    @Override
    public CompoundTag writeAttachment() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (int i = 0; i < this.m_6643_(); ++i) {
            ItemStack stack = this.m_8020_(i);
            if (stack.m_41619_()) continue;
            CompoundTag itemTag = new CompoundTag();
            itemTag.m_128344_("Slot", (byte)i);
            stack.m_41739_(itemTag);
            list.add((Object)itemTag);
        }
        tag.m_128365_("items", (Tag)list);
        return tag;
    }

    @Override
    public void readAttachment(CompoundTag tag) {
        for (int i = 0; i < this.m_6643_(); ++i) {
            this.m_8016_(i);
        }
        ListTag list = tag.m_128437_("items", 10);
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag itemTag = list.m_128728_(i);
            byte slot = itemTag.m_128445_("Slot");
            if (slot < 0 || slot >= this.m_6643_()) continue;
            this.m_6836_(slot, ItemStack.m_41712_((CompoundTag)itemTag));
        }
    }

    @Override
    public void onRemove(BlockState newState) {
        Containers.m_19002_((Level)this.blockEntity.m_58904_(), (BlockPos)this.blockEntity.m_58899_(), (Container)this);
    }

    public boolean m_19183_(ItemStack itemStack) {
        return (this.inputFilter == null || this.inputFilter.test(itemStack)) && super.m_19183_(itemStack);
    }

    public boolean m_7013_(int i, ItemStack itemStack) {
        return (this.inputFilter == null || this.inputFilter.test(itemStack)) && super.m_7013_(i, itemStack);
    }

    public boolean m_6542_(Player player) {
        return !this.blockEntity.m_58901_();
    }

    public int kjs$getWidth() {
        return this.width;
    }

    public int kjs$getHeight() {
        return this.height;
    }
}

