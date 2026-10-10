/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.client.model.HumanoidModel$ArmPose
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.extensions.common;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IClientItemExtensions {
    public static final IClientItemExtensions DEFAULT = new IClientItemExtensions(){};

    public static IClientItemExtensions of(ItemStack stack) {
        return IClientItemExtensions.of(stack.m_41720_());
    }

    public static IClientItemExtensions of(Item item) {
        IClientItemExtensions e;
        Object object = item.getRenderPropertiesInternal();
        return object instanceof IClientItemExtensions ? (e = (IClientItemExtensions)object) : DEFAULT;
    }

    @Nullable
    default public Font getFont(ItemStack stack, FontContext context) {
        return null;
    }

    @Nullable
    default public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
        return null;
    }

    default public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
        return false;
    }

    @NotNull
    default public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        return original;
    }

    @NotNull
    default public Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        HumanoidModel<?> replacement = this.getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original);
        if (replacement != original) {
            ForgeHooksClient.copyModelProperties(original, replacement);
            return replacement;
        }
        return original;
    }

    default public void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick) {
    }

    default public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return Minecraft.m_91087_().m_91291_().getBlockEntityRenderer();
    }

    public static enum FontContext {
        ITEM_COUNT,
        TOOLTIP,
        SELECTED_ITEM_NAME;

    }
}

