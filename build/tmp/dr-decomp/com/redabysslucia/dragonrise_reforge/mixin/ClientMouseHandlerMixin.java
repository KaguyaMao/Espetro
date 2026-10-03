/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.event.ClientMouseHandler
 *  com.atsuishio.superbwarfare.init.ModItems
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.redabysslucia.dragonrise_reforge.mixin;

import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModItems;
import com.redabysslucia.dragonrise_reforge.entities.special.R6DroneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ClientMouseHandler.class}, remap=false)
public class ClientMouseHandlerMixin {
    @Inject(method={"changeSensitivity"}, at={@At(value="HEAD")}, cancellable=true)
    private static void dr$keepSensitivityForR6Drone(double original, CallbackInfoReturnable<Double> cir) {
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null) {
            return;
        }
        ItemStack stack = player.m_21205_();
        if (!stack.m_150930_((Item)ModItems.MONITOR.get())) {
            return;
        }
        CompoundTag tag = stack.m_41784_();
        if (!tag.m_128471_("Using") || !tag.m_128471_("Linked")) {
            return;
        }
        if (R6DroneEntity.findDrone(player.m_9236_(), tag.m_128461_("LinkedDrone")) != null) {
            cir.setReturnValue((Object)original);
        }
    }
}

