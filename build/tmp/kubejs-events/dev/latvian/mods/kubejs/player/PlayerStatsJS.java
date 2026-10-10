/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.stats.Stat
 *  net.minecraft.stats.Stats
 *  net.minecraft.stats.StatsCounter
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 */
package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PlayerStatsJS {
    public final Player player;
    private final StatsCounter statFile;

    public PlayerStatsJS(Player p, StatsCounter s) {
        this.player = p;
        this.statFile = s;
    }

    public static Stat<?> statOf(Object o) {
        if (o instanceof Stat) {
            Stat s = (Stat)o;
            return s;
        }
        if (o instanceof ResourceLocation) {
            ResourceLocation rl = (ResourceLocation)o;
            return Stats.f_12988_.m_12902_((Object)rl);
        }
        if (o instanceof CharSequence) {
            CharSequence cs = (CharSequence)o;
            return Stats.f_12988_.m_12902_((Object)new ResourceLocation(cs.toString()));
        }
        return null;
    }

    public int get(Stat<?> stat) {
        return this.statFile.m_13015_(stat);
    }

    @HideFromJS
    public int get(ResourceLocation rl) {
        return this.get(Stats.f_12988_.m_12902_((Object)rl));
    }

    public int getPlayTime() {
        return this.get(Stats.f_144255_);
    }

    public int getTimeSinceDeath() {
        return this.get(Stats.f_12991_);
    }

    public int getTimeSinceRest() {
        return this.get(Stats.f_12992_);
    }

    public int getTimeCrouchTime() {
        return this.get(Stats.f_12993_);
    }

    public int getJumps() {
        return this.get(Stats.f_12926_);
    }

    public int getWalkDistance() {
        return this.get(Stats.f_12994_);
    }

    public int getSprintDistance() {
        return this.get(Stats.f_12996_);
    }

    public int getSwimDistance() {
        return this.get(Stats.f_12924_);
    }

    public int getCrouchDistance() {
        return this.get(Stats.f_12995_);
    }

    public int getDamageDealt() {
        return this.get(Stats.f_12928_);
    }

    public int getDamageDealt_absorbed() {
        return this.get(Stats.f_12929_);
    }

    public int getDamageDealt_resisted() {
        return this.get(Stats.f_12930_);
    }

    public int getDamageTaken() {
        return this.get(Stats.f_12931_);
    }

    public int getDamageBlocked_by_shield() {
        return this.get(Stats.f_12932_);
    }

    public int getDamageAbsorbed() {
        return this.get(Stats.f_12933_);
    }

    public int getDamageResisted() {
        return this.get(Stats.f_12934_);
    }

    public int getDeaths() {
        return this.get(Stats.f_12935_);
    }

    public int getMobKills() {
        return this.get(Stats.f_12936_);
    }

    public int getAnimalsBred() {
        return this.get(Stats.f_12937_);
    }

    public int getPlayerKills() {
        return this.get(Stats.f_12938_);
    }

    public int getFishCaught() {
        return this.get(Stats.f_12939_);
    }

    public void set(Stat<?> stat, int value) {
        this.statFile.m_6085_(this.player, stat, value);
    }

    public void add(Stat<?> stat, int value) {
        this.statFile.m_13023_(this.player, stat, value);
    }

    public int getBlocksMined(Block block) {
        return this.statFile.m_13015_(Stats.f_12949_.m_12902_((Object)block));
    }

    public int getItemsCrafted(Item item) {
        return this.statFile.m_13015_(Stats.f_12981_.m_12902_((Object)item));
    }

    public int getItemsUsed(Item item) {
        return this.statFile.m_13015_(Stats.f_12982_.m_12902_((Object)item));
    }

    public int getItemsBroken(Item item) {
        return this.statFile.m_13015_(Stats.f_12983_.m_12902_((Object)item));
    }

    public int getItemsPickedUp(Item item) {
        return this.statFile.m_13015_(Stats.f_12984_.m_12902_((Object)item));
    }

    public int getItemsDropped(Item item) {
        return this.statFile.m_13015_(Stats.f_12985_.m_12902_((Object)item));
    }

    public int getKilled(EntityType<?> entity) {
        return this.statFile.m_13015_(Stats.f_12986_.m_12902_(entity));
    }

    public int getKilledBy(EntityType<?> entity) {
        return this.statFile.m_13015_(Stats.f_12987_.m_12902_(entity));
    }
}

