/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.AABB
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package frontline.combat.fcp.mixins;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={EntityRenderDispatcher.class})
public class HideVehicleHitboxMixin {
    @Redirect(method={"renderHitbox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;F)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderLineBox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/AABB;FFFF)V"))
    private static void fcp$hideVehicleWhiteBox(PoseStack pose, VertexConsumer buffer, AABB box, float red, float green, float blue, float alpha, PoseStack pPoseStack, VertexConsumer pBuffer, Entity entity, float pPartialTicks) {
        VehicleEntity vehicle;
        if (entity instanceof VehicleEntity && !(vehicle = (VehicleEntity)entity).enableAABB()) {
            return;
        }
        LevelRenderer.m_109646_((PoseStack)pose, (VertexConsumer)buffer, (AABB)box, (float)red, (float)green, (float)blue, (float)alpha);
    }
}

