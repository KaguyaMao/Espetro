/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.world.entity.Entity
 */
package net.minecraftforge.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;

public abstract class PartEntity<T extends Entity>
extends Entity {
    private final T parent;

    public PartEntity(T parent) {
        super(parent.m_6095_(), parent.m_9236_());
        this.parent = parent;
    }

    public T getParent() {
        return this.parent;
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        throw new UnsupportedOperationException();
    }
}

