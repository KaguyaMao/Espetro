/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.ChunkPos
 */
package net.minecraftforge.common.ticket;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.ticket.ITicketGetter;
import net.minecraftforge.common.ticket.SimpleTicket;

public class ChunkTicketManager<T>
implements ITicketGetter<T> {
    private final Set<SimpleTicket<T>> tickets = Collections.newSetFromMap(new WeakHashMap());
    public final ChunkPos pos;

    public ChunkTicketManager(ChunkPos pos) {
        this.pos = pos;
    }

    @Override
    public void add(SimpleTicket<T> ticket) {
        this.tickets.add(ticket);
    }

    @Override
    public void remove(SimpleTicket<T> ticket) {
        this.tickets.remove(ticket);
    }

    @Override
    public Collection<SimpleTicket<T>> getTickets() {
        return this.tickets;
    }
}

