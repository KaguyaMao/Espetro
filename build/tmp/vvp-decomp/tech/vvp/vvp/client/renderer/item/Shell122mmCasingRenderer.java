/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.bernie.geckolib.model.GeoModel
 *  software.bernie.geckolib.renderer.GeoItemRenderer
 */
package tech.vvp.vvp.client.renderer.item;

import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import tech.vvp.vvp.client.model.item.Shell122mmCasingModel;
import tech.vvp.vvp.item.Shell122mmCasingItem;

public class Shell122mmCasingRenderer
extends GeoItemRenderer<Shell122mmCasingItem> {
    public Shell122mmCasingRenderer() {
        super((GeoModel)new Shell122mmCasingModel());
    }
}

