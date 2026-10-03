/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraftforge.items.ItemStackHandler
 */
package com.sighs.apricityui.config;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.items.ItemStackHandler;

public class ApricitySavedData
extends SavedData {
    private static final String INVENTORIES_KEY = "inventories";
    private final LinkedHashMap<String, ItemStackHandler> inventories = new LinkedHashMap();

    public static ApricitySavedData get(MinecraftServer server, String dataName) {
        return (ApricitySavedData)server.m_129783_().m_8895_().m_164861_(ApricitySavedData::load, ApricitySavedData::new, dataName);
    }

    public static ApricitySavedData load(CompoundTag tag) {
        ApricitySavedData data = new ApricitySavedData();
        CompoundTag allInventories = tag.m_128469_(INVENTORIES_KEY);
        for (String key : allInventories.m_128431_()) {
            CompoundTag serialized = allInventories.m_128469_(key);
            int slotCount = Math.max(1, serialized.m_128451_("Size"));
            ItemStackHandler handler = data.createTrackedHandler(slotCount);
            handler.deserializeNBT(serialized);
            data.inventories.put(key, handler);
        }
        return data;
    }

    public ItemStackHandler getOrCreate(String inventoryKey, int slotCount) {
        String key = this.normalizeInventoryKey(inventoryKey);
        int normalizedSlotCount = Math.max(1, slotCount);
        ItemStackHandler existing = this.inventories.get(key);
        if (existing == null) {
            ItemStackHandler created = this.createTrackedHandler(normalizedSlotCount);
            this.inventories.put(key, created);
            this.m_77762_();
            return created;
        }
        if (existing.getSlots() == normalizedSlotCount) {
            return existing;
        }
        ItemStackHandler resized = this.createTrackedHandler(normalizedSlotCount);
        int copyCount = Math.min(existing.getSlots(), normalizedSlotCount);
        for (int i = 0; i < copyCount; ++i) {
            ItemStack stack = existing.getStackInSlot(i);
            if (stack.m_41619_()) continue;
            resized.setStackInSlot(i, stack.m_41777_());
        }
        this.inventories.put(key, resized);
        this.m_77762_();
        return resized;
    }

    @Nonnull
    public CompoundTag m_7176_(@Nonnull CompoundTag tag) {
        CompoundTag allInventories = new CompoundTag();
        for (Map.Entry<String, ItemStackHandler> entry : this.inventories.entrySet()) {
            allInventories.m_128365_(entry.getKey(), (Tag)entry.getValue().serializeNBT());
        }
        tag.m_128365_(INVENTORIES_KEY, (Tag)allInventories);
        return tag;
    }

    private String normalizeInventoryKey(String inventoryKey) {
        if (inventoryKey == null || inventoryKey.trim().isEmpty()) {
            return "__default__";
        }
        return inventoryKey.trim();
    }

    private ItemStackHandler createTrackedHandler(int slotCount) {
        int normalized = Math.max(1, slotCount);
        return new ItemStackHandler(normalized){

            protected void onContentsChanged(int slot) {
                ApricitySavedData.this.m_77762_();
            }
        };
    }
}

