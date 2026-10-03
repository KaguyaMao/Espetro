/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 */
package net.minecraftforge.server.permission.handler;

import java.util.Set;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;

public interface IPermissionHandler {
    public ResourceLocation getIdentifier();

    public Set<PermissionNode<?>> getRegisteredNodes();

    public <T> T getPermission(ServerPlayer var1, PermissionNode<T> var2, PermissionDynamicContext<?> ... var3);

    public <T> T getOfflinePermission(UUID var1, PermissionNode<T> var2, PermissionDynamicContext<?> ... var3);
}

