/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package dev.latvian.mods.kubejs.util;

import java.util.function.BiConsumer;
import org.slf4j.Logger;

public enum LogType {
    INIT("INIT", Logger::info),
    DEBUG("DEBUG", Logger::debug),
    INFO("INFO", Logger::info),
    WARN("WARN", Logger::warn),
    ERROR("ERROR", Logger::error);

    public static final LogType[] VALUES;
    public final String name;
    public final BiConsumer<Logger, String> callback;

    private LogType(String name, BiConsumer<Logger, String> callback) {
        this.name = name;
        this.callback = callback;
    }

    static {
        VALUES = LogType.values();
    }
}

