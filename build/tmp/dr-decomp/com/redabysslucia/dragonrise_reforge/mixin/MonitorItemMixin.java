/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.init.ModItems
 *  com.atsuishio.superbwarfare.item.misc.MonitorItem
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.redabysslucia.dragonrise_reforge.mixin;

import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.item.misc.MonitorItem;
import com.redabysslucia.dragonrise_reforge.entities.special.R6DroneEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={MonitorItem.class})
public class MonitorItemMixin {
    @Inject(method={"inventoryTick"}, at={@At(value="HEAD")}, cancellable=true)
    private void dr$keepUsingForR6Drone(ItemStack stack, Level level, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (!selected) {
            return;
        }
        if (!stack.m_150930_((Item)ModItems.MONITOR.get())) {
            return;
        }
        CompoundTag tag = stack.m_41783_();
        if (tag == null || !tag.m_128471_("Using")) {
            return;
        }
        String linked = tag.m_128461_("LinkedDrone");
        if (R6DroneEntity.findDrone(level, linked) != null) {
            ci.cancel();
        }
    }
}

