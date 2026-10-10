/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.util;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.util.Storage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public class LocalStorage
extends Storage {
    private static volatile File localStorageFilePath;

    public void save() {
        File storageFile = LocalStorage.resolveStorageFile();
        if (storageFile == null) {
            return;
        }
        try {
            boolean created;
            File parentDir = storageFile.getParentFile();
            if (parentDir != null && !parentDir.exists() && !(created = parentDir.mkdirs())) {
                ApricityUI.LOGGER.error("Failed to create config directory for LocalStorage: {}", (Object)parentDir.getAbsolutePath());
                return;
            }
            Class<?> compoundTagClass = Class.forName("net.minecraft.nbt.CompoundTag");
            Object tag = compoundTagClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            Method putString = compoundTagClass.getMethod("putString", String.class, String.class);
            for (Map.Entry entry : this.data.entrySet()) {
                putString.invoke(tag, entry.getKey(), entry.getValue());
            }
            Class<?> nbtIoClass = Class.forName("net.minecraft.nbt.NbtIo");
            Method writeCompressed = nbtIoClass.getMethod("writeCompressed", compoundTagClass, File.class);
            writeCompressed.invoke(null, tag, storageFile);
        }
        catch (ClassNotFoundException parentDir) {
        }
        catch (ReflectiveOperationException e) {
            ApricityUI.LOGGER.error("Failed to reflectively persist LocalStorage to {}", (Object)storageFile.getAbsolutePath(), (Object)e);
        }
        catch (Exception e) {
            ApricityUI.LOGGER.error("Failed to save LocalStorage data to {}", (Object)storageFile.getAbsolutePath(), (Object)e);
        }
    }

    public void load() {
        File storageFile = LocalStorage.resolveStorageFile();
        if (storageFile == null || !storageFile.isFile()) {
            return;
        }
        try {
            Class<?> compoundTagClass = Class.forName("net.minecraft.nbt.CompoundTag");
            Class<?> nbtIoClass = Class.forName("net.minecraft.nbt.NbtIo");
            Method readCompressed = nbtIoClass.getMethod("readCompressed", File.class);
            Object tag = readCompressed.invoke(null, storageFile);
            if (tag == null) {
                return;
            }
            Method getAllKeys = compoundTagClass.getMethod("getAllKeys", new Class[0]);
            Method getString = compoundTagClass.getMethod("getString", String.class);
            Object rawKeys = getAllKeys.invoke(tag, new Object[0]);
            this.data.clear();
            if (rawKeys instanceof Set) {
                Set keys = (Set)rawKeys;
                for (Object key : keys) {
                    if (key == null) continue;
                    String stringKey = String.valueOf(key);
                    Object value = getString.invoke(tag, stringKey);
                    this.data.put(stringKey, value == null ? "" : String.valueOf(value));
                }
            }
        }
        catch (ClassNotFoundException compoundTagClass) {
        }
        catch (ReflectiveOperationException e) {
            ApricityUI.LOGGER.error("Failed to reflectively load LocalStorage from {}", (Object)storageFile.getAbsolutePath(), (Object)e);
        }
        catch (Exception e) {
            this.save();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static File resolveStorageFile() {
        File cached = localStorageFilePath;
        if (cached != null) {
            return cached;
        }
        Class<LocalStorage> clazz = LocalStorage.class;
        synchronized (LocalStorage.class) {
            if (localStorageFilePath != null) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return localStorageFilePath;
            }
            try {
                Path configDir = AuiServices.client().getConfigDirectory();
                if (configDir == null) {
                    // ** MonitorExit[var1_1] (shouldn't be in output)
                    return null;
                }
                localStorageFilePath = configDir.resolve("apricityui").resolve("localStorage.nbt").toFile();
            }
            catch (Throwable ignored) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return null;
            }
            return localStorageFilePath;
        }
    }

    public static File getStorageFile() {
        return LocalStorage.resolveStorageFile();
    }

    @Override
    public void setItem(String key, String value) {
        if (key == null || key.isBlank()) {
            return;
        }
        super.setItem(key, value);
        this.save();
    }

    @Override
    public void removeItem(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        super.removeItem(key);
        this.save();
    }

    @Override
    public void clear() {
        super.clear();
        this.save();
    }
}

