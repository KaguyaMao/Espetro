/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.particle.FireworkParticles$Starter
 *  net.minecraft.world.item.FireworkRocketItem$Shape
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.world.item.FireworkRocketItem;
import org.jetbrains.annotations.Nullable;

public class FireworkShapeFactoryRegistry {
    private static final Map<FireworkRocketItem.Shape, Factory> factories = new HashMap<FireworkRocketItem.Shape, Factory>();

    public static void register(FireworkRocketItem.Shape shape, Factory factory) {
        factories.put(shape, factory);
    }

    @Nullable
    public static Factory get(FireworkRocketItem.Shape shape) {
        return factories.get(shape);
    }

    public static interface Factory {
        public void build(FireworkParticles.Starter var1, boolean var2, boolean var3, int[] var4, int[] var5);
    }
}

