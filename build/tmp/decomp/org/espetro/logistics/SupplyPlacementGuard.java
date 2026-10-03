/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickItem
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package org.espetro.logistics;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.logistics.ConstructionMaterialItem;
import org.espetro.logistics.SupplyManager;
import org.espetro.logistics.SupplyType;

@Mod.EventBusSubscriber(modid="espetro")
public final class SupplyPlacementGuard {
    private SupplyPlacementGuard() {
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.m_41619_()) {
            return;
        }
        if (stack.m_41720_() instanceof ConstructionMaterialItem || SupplyManager.getInstance().getSupplyType(stack) == SupplyType.CONSTRUCTION) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        if (stack.m_41619_()) {
            return;
        }
        if (stack.m_41720_() instanceof ConstructionMaterialItem || SupplyManager.getInstance().getSupplyType(stack) == SupplyType.CONSTRUCTION) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }
}

