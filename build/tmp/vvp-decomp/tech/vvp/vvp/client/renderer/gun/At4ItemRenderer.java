/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.bernie.geckolib.model.GeoModel
 *  software.bernie.geckolib.renderer.GeoItemRenderer
 */
package tech.vvp.vvp.client.renderer.gun;

import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import tech.vvp.vvp.client.model.item.At4ItemModel;
import tech.vvp.vvp.item.gun.At4Item;

public class At4ItemRenderer
extends GeoItemRenderer<At4Item> {
    public At4ItemRenderer() {
        super((GeoModel)new At4ItemModel());
    }
}

