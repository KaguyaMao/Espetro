/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.random.WeightedEntry$IntrusiveBase
 *  net.minecraft.util.random.WeightedRandom
 *  net.minecraft.world.entity.EntityType
 */
package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.EntityType;

public class DungeonHooks {
    private static ArrayList<DungeonMob> dungeonMobs = new ArrayList();

    public static float addDungeonMob(EntityType<?> type, int rarity) {
        if (rarity <= 0) {
            throw new IllegalArgumentException("Rarity must be greater then zero");
        }
        Iterator<DungeonMob> itr = dungeonMobs.iterator();
        while (itr.hasNext()) {
            DungeonMob mob = itr.next();
            if (type != mob.type) continue;
            itr.remove();
            rarity = mob.m_142631_().m_146281_() + rarity;
            break;
        }
        dungeonMobs.add(new DungeonMob(rarity, type));
        return rarity;
    }

    public static int removeDungeonMob(EntityType<?> name) {
        for (DungeonMob mob : dungeonMobs) {
            if (name != mob.type) continue;
            dungeonMobs.remove((Object)mob);
            return mob.m_142631_().m_146281_();
        }
        return 0;
    }

    public static EntityType<?> getRandomDungeonMob(RandomSource rand) {
        DungeonMob mob = (DungeonMob)((Object)WeightedRandom.m_216822_((RandomSource)rand, dungeonMobs).orElseThrow());
        return mob.type;
    }

    static {
        DungeonHooks.addDungeonMob(EntityType.f_20524_, 100);
        DungeonHooks.addDungeonMob(EntityType.f_20501_, 200);
        DungeonHooks.addDungeonMob(EntityType.f_20479_, 100);
    }

    public static class DungeonMob
    extends WeightedEntry.IntrusiveBase {
        public final EntityType<?> type;

        public DungeonMob(int weight, EntityType<?> type) {
            super(weight);
            this.type = type;
        }

        public boolean equals(Object target) {
            return target instanceof DungeonMob && this.type.equals(((DungeonMob)((Object)target)).type);
        }
    }
}

