/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.common.ticket;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ticket.SimpleTicket;
import org.jetbrains.annotations.NotNull;

public class AABBTicket
extends SimpleTicket<Vec3> {
    @NotNull
    public final AABB axisAlignedBB;

    public AABBTicket(@NotNull AABB axisAlignedBB) {
        this.axisAlignedBB = axisAlignedBB;
    }

    @Override
    public boolean matches(Vec3 toMatch) {
        return this.axisAlignedBB.m_82390_(toMatch);
    }
}

