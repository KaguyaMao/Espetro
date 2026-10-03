/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.worldselection.PresetEditor
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.levelgen.presets.WorldPreset
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoader
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraftforge.client.event.RegisterPresetEditorsEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public final class PresetEditorManager {
    private static Map<ResourceKey<WorldPreset>, PresetEditor> editors = Map.of();

    private PresetEditorManager() {
    }

    @ApiStatus.Internal
    static void init() {
        HashMap<ResourceKey<WorldPreset>, PresetEditor> gatheredEditors = new HashMap<ResourceKey<WorldPreset>, PresetEditor>();
        PresetEditor.f_232950_.forEach((k, v) -> k.ifPresent(key -> gatheredEditors.put((ResourceKey<WorldPreset>)key, (PresetEditor)v)));
        RegisterPresetEditorsEvent event = new RegisterPresetEditorsEvent(gatheredEditors);
        ModLoader.get().postEventWrapContainerInModOrder((Event)event);
        editors = gatheredEditors;
    }

    @Nullable
    public static PresetEditor get(ResourceKey<WorldPreset> key) {
        return editors.get(key);
    }
}

