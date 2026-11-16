package org.espetro.mixin.client;

import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundEngine.class)
public interface SoundEngineStateAccessor {
    @Accessor("loaded")
    boolean espetro$isLoaded();
}
