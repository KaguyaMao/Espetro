/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.server.permission.nodes;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContextKey;
import net.minecraftforge.server.permission.nodes.PermissionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PermissionNode<T> {
    private final String nodeName;
    private final PermissionType<T> type;
    private final PermissionResolver<T> defaultResolver;
    private final PermissionDynamicContextKey<?>[] dynamics;
    @Nullable
    private Component readableName;
    @Nullable
    private Component description;

    public PermissionNode(ResourceLocation nodeName, PermissionType<T> type, PermissionResolver<T> defaultResolver, PermissionDynamicContextKey ... dynamics) {
        this(nodeName.m_135827_(), nodeName.m_135815_(), type, defaultResolver, dynamics);
    }

    public PermissionNode(String modID, String nodeName, PermissionType<T> type, PermissionResolver<T> defaultResolver, PermissionDynamicContextKey ... dynamics) {
        this(modID + "." + nodeName, type, defaultResolver, dynamics);
    }

    private PermissionNode(String nodeName, PermissionType<T> type, PermissionResolver<T> defaultResolver, PermissionDynamicContextKey ... dynamics) {
        this.nodeName = nodeName;
        this.type = type;
        this.dynamics = dynamics;
        this.defaultResolver = defaultResolver;
    }

    public PermissionNode setInformation(@NotNull Component readableName, @NotNull Component description) {
        Preconditions.checkNotNull((Object)readableName, (String)"Readable name for PermissionNodes must not be null %s", (Object)this.nodeName);
        Preconditions.checkNotNull((Object)description, (String)"Description for PermissionNodes must not be null %s", (Object)this.nodeName);
        this.readableName = readableName;
        this.description = description;
        return this;
    }

    public String getNodeName() {
        return this.nodeName;
    }

    public PermissionType<T> getType() {
        return this.type;
    }

    public PermissionDynamicContextKey<?>[] getDynamics() {
        return this.dynamics;
    }

    public PermissionResolver<T> getDefaultResolver() {
        return this.defaultResolver;
    }

    @Nullable
    public Component getReadableName() {
        return this.readableName;
    }

    @Nullable
    public Component getDescription() {
        return this.description;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermissionNode)) {
            return false;
        }
        PermissionNode otherNode = (PermissionNode)o;
        return this.nodeName.equals(otherNode.nodeName) && this.type.equals(otherNode.type);
    }

    public int hashCode() {
        return Objects.hash(this.nodeName, this.type);
    }

    @FunctionalInterface
    public static interface PermissionResolver<T> {
        public T resolve(@Nullable ServerPlayer var1, UUID var2, PermissionDynamicContext<?> ... var3);
    }
}

