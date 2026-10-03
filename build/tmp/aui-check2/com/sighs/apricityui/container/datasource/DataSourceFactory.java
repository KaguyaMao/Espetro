/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.items.ItemStackHandler
 */
package com.sighs.apricityui.container.datasource;

import com.sighs.apricityui.config.ApricitySavedData;
import com.sighs.apricityui.container.bind.ContainerBindType;
import com.sighs.apricityui.container.datasource.BlockEntityDataSource;
import com.sighs.apricityui.container.datasource.ContainerDataSource;
import com.sighs.apricityui.container.datasource.EntityDataSource;
import com.sighs.apricityui.container.datasource.SavedDataDataSource;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.items.ItemStackHandler;

public final class DataSourceFactory {
    public static ContainerDataSource resolve(ServerPlayer player, String containerId, ContainerBindType bindType, Map<String, String> args, int capacity) {
        if (player == null || bindType == null) {
            return null;
        }
        if (ContainerBindType.isPlayer(bindType)) {
            return null;
        }
        return switch (bindType) {
            case ContainerBindType.SAVED_DATA -> DataSourceFactory.resolveSavedData(player, containerId, args, capacity);
            case ContainerBindType.BLOCK_ENTITY -> DataSourceFactory.resolveBlockEntity(player, containerId, args, capacity);
            case ContainerBindType.ENTITY -> DataSourceFactory.resolveEntity(player, containerId, args, capacity);
            default -> null;
        };
    }

    private static ContainerDataSource resolveSavedData(ServerPlayer player, String containerId, Map<String, String> args, int capacity) {
        if (player.m_20194_() == null) {
            return null;
        }
        String dataName = DataSourceFactory.getArg(args, "data_name", "apricityui_data");
        String inventoryKey = containerId != null && !containerId.isBlank() ? containerId : "__default__";
        int normalizedCapacity = Math.max(1, capacity);
        ApricitySavedData savedData = ApricitySavedData.get(player.m_20194_(), dataName);
        ItemStackHandler handler = savedData.getOrCreate(inventoryKey, normalizedCapacity);
        return new SavedDataDataSource(ContainerBindType.SAVED_DATA, savedData, inventoryKey, handler);
    }

    private static ContainerDataSource resolveBlockEntity(ServerPlayer player, String containerId, Map<String, String> args, int capacity) {
        BlockPos pos = DataSourceFactory.parseBlockPos(args);
        if (pos == null) {
            return null;
        }
        return BlockEntityDataSource.resolve(player, pos, capacity);
    }

    private static ContainerDataSource resolveEntity(ServerPlayer player, String containerId, Map<String, String> args, int capacity) {
        Integer entityId = DataSourceFactory.parseIntArg(args, "entity_id");
        if (entityId == null) {
            return null;
        }
        return EntityDataSource.resolve(player, entityId, capacity);
    }

    private static BlockPos parseBlockPos(Map<String, String> args) {
        Integer x = DataSourceFactory.parseIntArg(args, "x");
        Integer y = DataSourceFactory.parseIntArg(args, "y");
        Integer z = DataSourceFactory.parseIntArg(args, "z");
        if (x == null || y == null || z == null) {
            return null;
        }
        return new BlockPos(x.intValue(), y.intValue(), z.intValue());
    }

    private static Integer parseIntArg(Map<String, String> args, String key) {
        if (args == null || key == null) {
            return null;
        }
        String value = args.get(key);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String getArg(Map<String, String> args, String key, String fallback) {
        if (args == null || key == null) {
            return fallback;
        }
        String value = args.get(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}

