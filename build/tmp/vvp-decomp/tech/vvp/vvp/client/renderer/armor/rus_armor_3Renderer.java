/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.cache.object.GeoBone
 *  software.bernie.geckolib.core.animatable.GeoAnimatable
 *  software.bernie.geckolib.model.GeoModel
 *  software.bernie.geckolib.renderer.GeoArmorRenderer
 */
package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import tech.vvp.vvp.client.renderer.armor.rus_armor_3Model;
import tech.vvp.vvp.item.armor.rus_armor_3;

public class rus_armor_3Renderer
extends GeoArmorRenderer<rus_armor_3> {
    public rus_armor_3Renderer() {
        super((GeoModel)new rus_armor_3Model());
        this.body = new GeoBone(null, "armorBody", Boolean.valueOf(false), Double.valueOf(0.0), Boolean.valueOf(false), Boolean.valueOf(false));
        this.rightArm = new GeoBone(null, "armorRightArm", Boolean.valueOf(false), Double.valueOf(0.0), Boolean.valueOf(false), Boolean.valueOf(false));
        this.leftArm = new GeoBone(null, "armorLeftArm", Boolean.valueOf(false), Double.valueOf(0.0), Boolean.valueOf(false), Boolean.valueOf(false));
        this.leftLeg = new GeoBone(null, "armorLeftLeg", Boolean.valueOf(false), Double.valueOf(0.0), Boolean.valueOf(false), Boolean.valueOf(false));
        this.leftBoot = new GeoBone(null, "armorLeftBoot", Boolean.valueOf(false), Double.valueOf(0.0), Boolean.valueOf(false), Boolean.valueOf(false));
        this.rightLeg = new GeoBone(null, "armorRightLeg", Boolean.valueOf(false), Double.valueOf(0.0), Boolean.valueOf(false), Boolean.valueOf(false));
        this.rightBoot = new GeoBone(null, "armorRightBoot", Boolean.valueOf(false), Double.valueOf(0.0), Boolean.valueOf(false), Boolean.valueOf(false));
    }

    public RenderType getRenderType(rus_armor_3 animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.m_110473_((ResourceLocation)this.getTextureLocation((GeoAnimatable)animatable));
    }
}

