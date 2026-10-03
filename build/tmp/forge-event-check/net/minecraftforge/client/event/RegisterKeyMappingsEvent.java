/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Options
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.apache.commons.lang3.ArrayUtils
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;

public class RegisterKeyMappingsEvent
extends Event
implements IModBusEvent {
    private final Options options;

    @ApiStatus.Internal
    public RegisterKeyMappingsEvent(Options options) {
        this.options = options;
    }

    public void register(KeyMapping key) {
        this.options.f_92059_ = (KeyMapping[])ArrayUtils.add((Object[])this.options.f_92059_, (Object)key);
    }
}

