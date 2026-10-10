/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.common.ticket;

import java.util.Collection;
import net.minecraftforge.common.ticket.ITicketManager;
import net.minecraftforge.common.ticket.SimpleTicket;

public interface ITicketGetter<T>
extends ITicketManager<T> {
    public Collection<SimpleTicket<T>> getTickets();
}

