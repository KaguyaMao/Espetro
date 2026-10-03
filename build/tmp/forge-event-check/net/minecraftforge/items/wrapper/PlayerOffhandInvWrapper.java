/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Inventory
 */
package net.minecraftforge.items.wrapper;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class PlayerOffhandInvWrapper
extends RangedWrapper {
    public PlayerOffhandInvWrapper(Inventory inv) {
        super(new InvWrapper((Container)inv), inv.f_35974_.size() + inv.f_35975_.size(), inv.f_35974_.size() + inv.f_35975_.size() + inv.f_35976_.size());
    }
}

