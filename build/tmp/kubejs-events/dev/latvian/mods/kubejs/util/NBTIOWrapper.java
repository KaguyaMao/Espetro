/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtIo
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.jetbrains.annotations.Nullable;

public interface NBTIOWrapper {
    @Nullable
    public static CompoundTag read(Path path) throws IOException {
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            return null;
        }
        return NbtIo.m_128939_((InputStream)Files.newInputStream(path, new OpenOption[0]));
    }

    public static void write(Path path, CompoundTag nbt) throws IOException {
        if (nbt == null) {
            Files.deleteIfExists(path);
            return;
        }
        NbtIo.m_128947_((CompoundTag)nbt, (OutputStream)Files.newOutputStream(path, new OpenOption[0]));
    }
}

