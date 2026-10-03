/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.common.util;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface NonNullConsumer<T> {
    public void accept(@NotNull T var1);
}

