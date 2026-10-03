/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.decoration.ArmorStand
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.sighs.apricityui.world;

import com.sighs.apricityui.world.WorldWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="apricityui", value={Dist.CLIENT})
public class WorldWindowTestSpawner {
    private static final String TEST_DOC_PATH = "tests/world-window-acceptance.html";
    private static final int TEST_MAX_DISTANCE = 8;
    private static final float TEST_FOLLOW_FACTOR = 0.3f;
    private static WorldWindow lastWindow;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null || mc.f_91074_ == null) {
            return;
        }
        Vec3 playerPos = mc.f_91074_.m_20182_();
        AABB search = new AABB(playerPos, playerPos).m_82400_(64.0);
        ArmorStand target = null;
        for (ArmorStand stand : mc.f_91073_.m_45976_(ArmorStand.class, search)) {
            if (stand.m_7755_() == null || !"auitest".equals(stand.m_7755_().getString())) continue;
            target = stand;
            break;
        }
        if (target == null) {
            if (lastWindow != null) {
                WorldWindow.removeWindow(lastWindow);
                lastWindow = null;
            }
            return;
        }
        Vec3 base = target.m_20182_().m_82520_(0.0, 1.5, 0.0);
        if (lastWindow == null) {
            WorldWindow window = new WorldWindow(TEST_DOC_PATH, base, 8);
            window.setFollow(true);
            window.setFollowFactor(0.3f);
            window.setFacing(true);
            WorldWindow.addWindow(window);
            lastWindow = window;
        }
    }
}

