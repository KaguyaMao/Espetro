/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.data.api.DataValidator
 *  cc.sighs.oelib.data.api.DataValidator$ValidationResult
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.data.validator;

import cc.sighs.auratip.data.RadialMenuData;
import cc.sighs.oelib.data.api.DataValidator;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class RadialMenuDataValidator
implements DataValidator<RadialMenuData> {
    public DataValidator.ValidationResult validate(RadialMenuData data, ResourceLocation source) {
        if (data.id() == null) {
            return DataValidator.ValidationResult.failure((String)("Radial menu has no id in " + String.valueOf(source)));
        }
        if (data.menuSettings() == null) {
            return DataValidator.ValidationResult.failure((String)("Radial menu has no menu_settings in " + String.valueOf(source)));
        }
        RadialMenuData.MenuSettings settings = data.menuSettings();
        if (settings.innerRadius() <= 0 || settings.outerRadius() <= 0) {
            return DataValidator.ValidationResult.failure((String)("inner_radius and outer_radius must be > 0 in " + String.valueOf(source)));
        }
        if (settings.innerRadius() >= settings.outerRadius()) {
            return DataValidator.ValidationResult.failure((String)("inner_radius must be < outer_radius in " + String.valueOf(source)));
        }
        if (settings.animationSpeed() <= 0.0f) {
            return DataValidator.ValidationResult.failure((String)("animation_speed must be > 0 in " + String.valueOf(source)));
        }
        List<RadialMenuData.Slot> slots = data.slots();
        if (slots == null || slots.isEmpty()) {
            return DataValidator.ValidationResult.failure((String)("Radial menu has no slots in " + String.valueOf(source)));
        }
        for (int i = 0; i < slots.size(); ++i) {
            RadialMenuData.Slot slot = slots.get(i);
            if (slot.name() == null || slot.name().isBlank()) {
                return DataValidator.ValidationResult.failure((String)("Slot " + i + " has empty name in " + String.valueOf(source)));
            }
            if (slot.icon() == null) {
                return DataValidator.ValidationResult.failure((String)("Slot " + i + " has no icon in " + String.valueOf(source)));
            }
            if (slot.action() != null) continue;
            return DataValidator.ValidationResult.failure((String)("Slot " + i + " has no action in " + String.valueOf(source)));
        }
        return DataValidator.ValidationResult.success();
    }
}

