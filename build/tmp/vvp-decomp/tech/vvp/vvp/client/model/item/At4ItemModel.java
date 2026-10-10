/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.animation.AnimationHelper
 *  com.atsuishio.superbwarfare.client.model.item.CustomGunModel
 *  com.atsuishio.superbwarfare.client.overlay.CrossHairOverlay
 *  com.atsuishio.superbwarfare.event.ClientEventHandler
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  software.bernie.geckolib.core.animatable.model.CoreGeoBone
 *  software.bernie.geckolib.core.animation.AnimationProcessor
 *  software.bernie.geckolib.core.animation.AnimationState
 */
package tech.vvp.vvp.client.model.item;

import com.atsuishio.superbwarfare.client.animation.AnimationHelper;
import com.atsuishio.superbwarfare.client.model.item.CustomGunModel;
import com.atsuishio.superbwarfare.client.overlay.CrossHairOverlay;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.animation.AnimationState;
import tech.vvp.vvp.item.gun.At4Item;

public class At4ItemModel
extends CustomGunModel<At4Item> {
    public ResourceLocation getAnimationResource(At4Item animatable) {
        return new ResourceLocation("vvp", "animations/at4.animation.json");
    }

    public ResourceLocation getModelResource(At4Item animatable) {
        return new ResourceLocation("vvp", "geo/at4.geo.json");
    }

    public ResourceLocation getTextureResource(At4Item animatable) {
        return new ResourceLocation("vvp", "textures/item/at4.png");
    }

    public ResourceLocation getLODModelResource(At4Item animatable) {
        return new ResourceLocation("vvp", "geo/at4.geo.json");
    }

    public ResourceLocation getLODTextureResource(At4Item animatable) {
        return new ResourceLocation("vvp", "textures/item/at4.png");
    }

    public void setCustomAnimations(At4Item animatable, long instanceId, AnimationState<At4Item> animationState) {
        LocalPlayer player = Minecraft.m_91087_().f_91074_;
        if (player == null) {
            return;
        }
        ItemStack stack = player.m_21205_();
        if (this.shouldCancelRender(stack, animationState)) {
            return;
        }
        CoreGeoBone gun = this.getAnimationProcessor().getBone("bone");
        CoreGeoBone at4 = this.getAnimationProcessor().getBone("at4");
        if (gun == null || at4 == null) {
            return;
        }
        double zt = ClientEventHandler.zoomTime;
        double zp = ClientEventHandler.zoomPos;
        double zpz = ClientEventHandler.zoomPosZ;
        ClientEventHandler.handleShootAnimation((CoreGeoBone)at4, (float)1.0f, (float)-0.4f, (float)1.2f, (float)1.3f, (float)1.0f, (float)1.0f, (float)0.5f, (float)0.7f);
        CrossHairOverlay.gunRot = at4.getRotZ();
        gun.setPosX(0.91f * (float)zp);
        gun.setPosY(-0.04f * (float)zp - (float)((double)0.2f * zpz));
        gun.setPosZ(2.0f * (float)zp + (float)((double)0.15f * zpz));
        gun.setRotZ(0.45f * (float)zp + (float)((double)0.02f * zpz));
        gun.setScaleZ(1.0f - 0.5f * (float)zp);
        ClientEventHandler.gunRootMove((AnimationProcessor)this.getAnimationProcessor(), (float)0.0f, (float)0.0f, (float)0.0f, (boolean)true);
        CoreGeoBone camera = this.getAnimationProcessor().getBone("camera");
        CoreGeoBone main = this.getAnimationProcessor().getBone("0");
        if (camera == null || main == null) {
            return;
        }
        float numR = (float)(1.0 - 0.82 * zt);
        float numP = (float)(1.0 - 0.78 * zt);
        AnimationHelper.handleReloadShakeAnimation((ItemStack)stack, (CoreGeoBone)main, (CoreGeoBone)camera, (float)numR, (float)numP);
        ClientEventHandler.handleReloadShake((double)(57.295776f * camera.getRotX()), (double)(57.295776f * camera.getRotY()), (double)(57.295776f * camera.getRotZ()));
    }
}

