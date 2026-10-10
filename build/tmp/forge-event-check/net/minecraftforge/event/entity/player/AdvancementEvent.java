/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.advancements.AdvancementProgress
 *  net.minecraft.world.entity.player.Player
 */
package net.minecraftforge.event.entity.player;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class AdvancementEvent
extends PlayerEvent {
    private final Advancement advancement;

    public AdvancementEvent(Player player, Advancement advancement) {
        super(player);
        this.advancement = advancement;
    }

    public Advancement getAdvancement() {
        return this.advancement;
    }

    public static class AdvancementProgressEvent
    extends AdvancementEvent {
        private final AdvancementProgress advancementProgress;
        private final String criterionName;
        private final ProgressType progressType;

        public AdvancementProgressEvent(Player player, Advancement progressed, AdvancementProgress advancementProgress, String criterionName, ProgressType progressType) {
            super(player, progressed);
            this.advancementProgress = advancementProgress;
            this.criterionName = criterionName;
            this.progressType = progressType;
        }

        @Override
        public Advancement getAdvancement() {
            return super.getAdvancement();
        }

        public AdvancementProgress getAdvancementProgress() {
            return this.advancementProgress;
        }

        public String getCriterionName() {
            return this.criterionName;
        }

        public ProgressType getProgressType() {
            return this.progressType;
        }

        public static enum ProgressType {
            GRANT,
            REVOKE;

        }
    }

    public static class AdvancementEarnEvent
    extends AdvancementEvent {
        public AdvancementEarnEvent(Player player, Advancement earned) {
            super(player, earned);
        }

        @Override
        public Advancement getAdvancement() {
            return super.getAdvancement();
        }
    }
}

