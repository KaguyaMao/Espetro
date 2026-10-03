/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.worldselection.PresetEditor
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.levelgen.presets.WorldPreset
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import java.util.Map;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

public class RegisterPresetEditorsEvent
extends Event
implements IModBusEvent {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<ResourceKey<WorldPreset>, PresetEditor> editors;

    @ApiStatus.Internal
    public RegisterPresetEditorsEvent(Map<ResourceKey<WorldPreset>, PresetEditor> editors) {
        this.editors = editors;
    }

    public void register(ResourceKey<WorldPreset> key, PresetEditor editor) {
        PresetEditor old = this.editors.put(key, editor);
        if (old != null) {
            LOGGER.debug("PresetEditor {} overridden by mod {}", (Object)key.m_135782_(), (Object)ModLoadingContext.get().getActiveNamespace());
        }
    }
}

