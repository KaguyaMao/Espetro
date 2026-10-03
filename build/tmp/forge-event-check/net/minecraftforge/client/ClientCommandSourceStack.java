/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.client.Minecraft
 *  net.minecraft.commands.CommandSource
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.crafting.RecipeManager
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec2
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.scores.Scoreboard
 */
package net.minecraftforge.client;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;

public class ClientCommandSourceStack
extends CommandSourceStack {
    public ClientCommandSourceStack(CommandSource source, Vec3 position, Vec2 rotation, int permission, String plainTextName, Component displayName, Entity executing) {
        super(source, position, rotation, null, permission, plainTextName, displayName, null, executing);
    }

    public void m_288197_(Supplier<Component> message, boolean sendToAdmins) {
        Minecraft.m_91087_().f_91074_.m_213846_(message.get());
    }

    public Collection<String> m_5983_() {
        return Minecraft.m_91087_().f_91073_.m_6188_().m_83488_();
    }

    public Collection<String> m_5982_() {
        return Minecraft.m_91087_().m_91403_().m_105142_().stream().map(player -> player.m_105312_().getName()).collect(Collectors.toList());
    }

    public Stream<ResourceLocation> m_6860_() {
        return Minecraft.m_91087_().m_91403_().m_105141_().m_44073_();
    }

    public Set<ResourceKey<Level>> m_6553_() {
        return Minecraft.m_91087_().m_91403_().m_105151_();
    }

    public RegistryAccess m_5894_() {
        return Minecraft.m_91087_().m_91403_().m_105152_();
    }

    public Scoreboard getScoreboard() {
        return Minecraft.m_91087_().f_91073_.m_6188_();
    }

    public Advancement getAdvancement(ResourceLocation id) {
        return Minecraft.m_91087_().m_91403_().m_105145_().m_104396_().m_139337_(id);
    }

    public RecipeManager getRecipeManager() {
        return Minecraft.m_91087_().m_91403_().m_105141_();
    }

    public Level getUnsidedLevel() {
        return Minecraft.m_91087_().f_91073_;
    }

    public MinecraftServer m_81377_() {
        throw new UnsupportedOperationException("Attempted to get server in client command");
    }

    public ServerLevel m_81372_() {
        throw new UnsupportedOperationException("Attempted to get server level in client command");
    }
}

