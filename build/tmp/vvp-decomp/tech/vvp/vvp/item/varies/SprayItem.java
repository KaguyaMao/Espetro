/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package tech.vvp.vvp.item.varies;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.entity.vehicle.ICamoVehicle;

public class SprayItem
extends Item {
    public SprayItem() {
        super(new Item.Properties().m_41487_(1));
    }

    @ParametersAreNonnullByDefault
    public void m_7373_(ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add((Component)Component.m_237115_((String)"des.vvp.spray").m_130940_(ChatFormatting.GRAY));
        pTooltipComponents.add((Component)Component.m_237115_((String)"des.vvp.spray.usage").m_130940_(ChatFormatting.DARK_GRAY));
    }

    public InteractionResult m_6880_(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof ICamoVehicle) {
            ICamoVehicle camoVehicle = (ICamoVehicle)target;
            if (!player.m_9236_().f_46443_) {
                camoVehicle.cycleCamo();
                String[] camoNames = camoVehicle.getCamoNames();
                int camoType = camoVehicle.getCamoType();
                String camoName = camoType >= 0 && camoType < camoNames.length ? camoNames[camoType] : "Unknown";
                player.m_5661_((Component)Component.m_237110_((String)"message.vvp.camo_changed", (Object[])new Object[]{camoName}).m_130940_(ChatFormatting.GREEN), true);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}

