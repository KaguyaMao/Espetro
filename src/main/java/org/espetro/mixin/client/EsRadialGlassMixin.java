package org.espetro.mixin.client;

import org.espetro.client.gui.RadialGlassStyle;
import org.espetro.client.gui.UiTheme;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * EsRadial 0.3.2 exposes ringColors but hard-codes its hovered/blank sector fills.
 * Replace only those canvas background constants, leaving icons, rims and progress colors intact.
 */
@Mixin(targets = "org.esradial.client.AuiRadialRenderer", remap = false)
public abstract class EsRadialGlassMixin {
    @ModifyConstant(method = "drawRing(D)V", remap = false, require = 1,
        constant = @Constant(stringValue = "rgba(36,41,43,0.37)"))
    private String espetro$blankInner(String original) {
        return RadialGlassStyle.css(UiTheme.RADIAL_BACKGROUND);
    }

    @ModifyConstant(method = "drawRing(D)V", remap = false, require = 1,
        constant = @Constant(stringValue = "rgba(50,56,58,0.43)"))
    private String espetro$blankOuter(String original) {
        return RadialGlassStyle.css(UiTheme.RADIAL_BACKGROUND_OUTER);
    }

    // This same fill appears once in the layout editor and once for the hovered sector.
    @ModifyConstant(method = "drawRing(D)V", remap = false, require = 2,
        constant = @Constant(stringValue = "rgba(222,228,230,0.24)"))
    private String espetro$hoveredFill(String original) {
        return RadialGlassStyle.css(UiTheme.RADIAL_HOVER);
    }

    @ModifyConstant(method = "drawRing(D)V", remap = false, require = 1,
        constant = @Constant(stringValue = "rgba(130,50,45,0.28)"))
    private String espetro$disabledFill(String original) {
        return RadialGlassStyle.css(UiTheme.RADIAL_DISABLED);
    }

    @ModifyConstant(method = "drawRing(D)V", remap = false, require = 1,
        constant = @Constant(stringValue = "rgba(231,239,233,0.28)"))
    private String espetro$progressTrack(String original) {
        return RadialGlassStyle.css(UiTheme.RADIAL_CENTER);
    }
}
