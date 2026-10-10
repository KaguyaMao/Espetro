/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import com.google.common.base.Strings;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

@Cancelable
public class ClientChatEvent
extends Event {
    private String message;
    private final String originalMessage;

    @ApiStatus.Internal
    public ClientChatEvent(String message) {
        this.setMessage(message);
        this.message = this.originalMessage = Strings.nullToEmpty((String)message);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = Strings.nullToEmpty((String)message);
    }

    public String getOriginalMessage() {
        return this.originalMessage;
    }
}

