/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.screens.VehicleAssemblingScreen
 *  com.atsuishio.superbwarfare.init.ModItems
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.client.extensions.common.IClientItemExtensions
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  software.bernie.geckolib.animatable.GeoItem
 */
package com.redabysslucia.dragonrise_reforge.mixin;

import com.atsuishio.superbwarfare.client.screens.VehicleAssemblingScreen;
import com.atsuishio.superbwarfare.init.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import software.bernie.geckolib.animatable.GeoItem;

@Mixin(value={VehicleAssemblingScreen.class})
public abstract class VehicleAssemblingScreenMixin {
    @Redirect(method={"renderDefaultItemModel"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;I)V"))
    private void redirectRenderStatic(ItemRenderer instance, ItemStack stack, ItemDisplayContext displayContext, int packedLight, int packedOverlay, PoseStack poseStack, MultiBufferSource bufferSource, Level level, int seed) {
        IClientItemExtensions extensions;
        BlockEntityWithoutLevelRenderer ber;
        if (stack.m_41720_() instanceof GeoItem && !stack.m_150930_((Item)ModItems.CONTAINER.get()) && (ber = (extensions = IClientItemExtensions.of((ItemStack)stack)).getCustomRenderer()) != null) {
            ber.m_108829_(stack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);
            return;
        }
        instance.m_269128_(stack, displayContext, packedLight, packedOverlay, poseStack, bufferSource, level, seed);
    }
}

