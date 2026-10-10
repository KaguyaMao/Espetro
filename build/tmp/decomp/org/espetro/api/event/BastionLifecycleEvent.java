/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraftforge.eventbus.api.Event
 */
package org.espetro.api.event;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.StructureKind;

public abstract class BastionLifecycleEvent
extends Event {
    private final UUID bastionId;
    private final String name;
    private final String team;
    private final StructureKind kind;
    @Nullable
    private final ServerLevel level;
    @Nullable
    private final BlockPos position;

    protected BastionLifecycleEvent(BastionData bastion) {
        this.bastionId = bastion.getBastionId();
        this.name = bastion.getName();
        this.team = bastion.getTeam();
        this.kind = bastion.getKind();
        this.level = bastion.getLevel();
        this.position = bastion.getPosition();
    }

    public UUID bastionId() {
        return this.bastionId;
    }

    public String name() {
        return this.name;
    }

    public String team() {
        return this.team;
    }

    public StructureKind kind() {
        return this.kind;
    }

    @Nullable
    public ServerLevel level() {
        return this.level;
    }

    @Nullable
    public BlockPos position() {
        return this.position;
    }

    public static final class Destroyed
    extends BastionLifecycleEvent {
        @Nullable
        private final Entity attacker;
        private final boolean deductedManpower;
        private final int manpowerPenalty;

        public Destroyed(BastionData bastion, @Nullable Entity attacker, boolean deductedManpower, int manpowerPenalty) {
            super(bastion);
            this.attacker = attacker;
            this.deductedManpower = deductedManpower;
            this.manpowerPenalty = manpowerPenalty;
        }

        @Nullable
        public Entity attacker() {
            return this.attacker;
        }

        public boolean deductedManpower() {
            return this.deductedManpower;
        }

        public int manpowerPenalty() {
            return this.manpowerPenalty;
        }
    }

    public static final class Built
    extends BastionLifecycleEvent {
        public Built(BastionData bastion) {
            super(bastion);
        }
    }
}

