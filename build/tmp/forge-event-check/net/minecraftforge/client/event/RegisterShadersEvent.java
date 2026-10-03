/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public class RegisterShadersEvent
extends Event
implements IModBusEvent {
    private final ResourceProvider resourceProvider;
    private final List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderList;

    @ApiStatus.Internal
    public RegisterShadersEvent(ResourceProvider resourceProvider, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderList) {
        this.resourceProvider = resourceProvider;
        this.shaderList = shaderList;
    }

    public ResourceProvider getResourceProvider() {
        return this.resourceProvider;
    }

    public void registerShader(ShaderInstance shaderInstance, Consumer<ShaderInstance> onLoaded) {
        this.shaderList.add((Pair<ShaderInstance, Consumer<ShaderInstance>>)Pair.of((Object)shaderInstance, onLoaded));
    }
}

