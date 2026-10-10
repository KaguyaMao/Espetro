/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.commands.arguments.selector.EntitySelector
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.core.DataSenderKJS;
import dev.latvian.mods.kubejs.core.MessageSenderKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public class EntityArrayList
extends ArrayList<Entity>
implements MessageSenderKJS,
DataSenderKJS {
    public final Level level;

    public EntityArrayList(Level l, int size) {
        super(size);
        this.level = l;
    }

    public EntityArrayList(Level l, Iterable<? extends Entity> entities) {
        int n;
        if (entities instanceof Collection) {
            Collection c = (Collection)entities;
            n = c.size();
        } else {
            n = 4;
        }
        this(l, n);
        this.addAllIterable(entities);
    }

    public void addAllIterable(Iterable<? extends Entity> entities) {
        if (entities instanceof Collection) {
            Collection c = (Collection)entities;
            this.addAll(c);
        } else {
            for (Entity entity : entities) {
                this.add(entity);
            }
        }
    }

    @Override
    public Component kjs$getName() {
        return Component.m_237113_((String)"EntityList");
    }

    @Override
    public Component kjs$getDisplayName() {
        return Component.m_237113_((String)this.toString()).kjs$lightPurple();
    }

    @Override
    public void kjs$tell(Component message) {
        for (Entity entity : this) {
            entity.kjs$tell(message);
        }
    }

    @Override
    public void kjs$setStatusMessage(Component message) {
        for (Entity entity : this) {
            entity.kjs$setStatusMessage(message);
        }
    }

    @Override
    public int kjs$runCommand(String command) {
        int m = 0;
        for (Entity entity : this) {
            m = Math.max(m, entity.kjs$runCommand(command));
        }
        return m;
    }

    @Override
    public int kjs$runCommandSilent(String command) {
        int m = 0;
        for (Entity entity : this) {
            m = Math.max(m, entity.kjs$runCommandSilent(command));
        }
        return m;
    }

    public void kill() {
        for (Entity entity : this) {
            entity.m_6074_();
        }
    }

    public void playSound(SoundEvent id, float volume, float pitch) {
        for (Entity entity : this) {
            entity.m_9236_().m_6263_(null, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), id, entity.m_5720_(), volume, pitch);
        }
    }

    public void playSound(SoundEvent id) {
        this.playSound(id, 1.0f, 1.0f);
    }

    public EntityArrayList filter(Predicate<Entity> filter) {
        if (this.isEmpty()) {
            return this;
        }
        EntityArrayList list = new EntityArrayList(this.level, this.size());
        for (Entity entity : this) {
            if (!filter.test(entity)) continue;
            list.add(entity);
        }
        return list;
    }

    public EntityArrayList filterSelector(EntitySelector selector) {
        return this.filter(selector.f_121114_);
    }

    @Override
    public void kjs$sendData(String channel, @Nullable CompoundTag data) {
        for (Entity entity : this) {
            if (!(entity instanceof Player)) continue;
            Player player = (Player)entity;
            player.kjs$sendData(channel, data);
        }
    }

    @Override
    @Nullable
    public Entity getFirst() {
        return this.isEmpty() ? null : (Entity)this.get(0);
    }
}

