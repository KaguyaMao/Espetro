/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.core.animatable.GeoAnimatable
 *  software.bernie.geckolib.model.GeoModel
 *  software.bernie.geckolib.renderer.GeoArmorRenderer
 */
package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import tech.vvp.vvp.client.renderer.armor.ukr_v2_helmetModel;
import tech.vvp.vvp.item.armor.ukr_v2_helmet;

public class ukr_v2_helmetRenderer
extends GeoArmorRenderer<ukr_v2_helmet> {
    public ukr_v2_helmetRenderer() {
        super((GeoModel)new ukr_v2_helmetModel());
    }

    public RenderType getRenderType(ukr_v2_helmet animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.m_110473_((ResourceLocation)this.getTextureLocation((GeoAnimatable)animatable));
    }
}

