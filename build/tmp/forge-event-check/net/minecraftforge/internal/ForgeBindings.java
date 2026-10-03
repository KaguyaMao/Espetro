/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.fml.I18NParser
 *  net.minecraftforge.fml.IBindingsProvider
 *  net.minecraftforge.fml.config.IConfigEvent$ConfigConfig
 */
package net.minecraftforge.internal;

import java.util.function.Supplier;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.I18NParser;
import net.minecraftforge.fml.IBindingsProvider;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class ForgeBindings
implements IBindingsProvider {
    public Supplier<IEventBus> getForgeBusSupplier() {
        return () -> MinecraftForge.EVENT_BUS;
    }

    public Supplier<I18NParser> getMessageParser() {
        return () -> new I18NParser(){

            public String parseMessage(String i18nMessage, Object ... args) {
                return ForgeI18n.parseMessage(i18nMessage, args);
            }

            public String stripControlCodes(String toStrip) {
                return ForgeI18n.stripControlCodes(toStrip);
            }
        };
    }

    public Supplier<IConfigEvent.ConfigConfig> getConfigConfiguration() {
        return () -> new IConfigEvent.ConfigConfig(ModConfigEvent.Loading::new, ModConfigEvent.Reloading::new, ModConfigEvent.Unloading::new);
    }
}

