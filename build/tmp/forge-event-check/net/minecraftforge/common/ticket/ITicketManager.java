/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.common.ticket;

import net.minecraftforge.common.ticket.SimpleTicket;

public interface ITicketManager<T> {
    public void add(SimpleTicket<T> var1);

    public void remove(SimpleTicket<T> var1);
}

