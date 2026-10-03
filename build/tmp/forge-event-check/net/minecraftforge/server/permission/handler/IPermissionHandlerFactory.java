/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.server.permission.handler;

import java.util.Collection;
import net.minecraftforge.server.permission.handler.IPermissionHandler;
import net.minecraftforge.server.permission.nodes.PermissionNode;

@FunctionalInterface
public interface IPermissionHandlerFactory {
    public IPermissionHandler create(Collection<PermissionNode<?>> var1);
}

