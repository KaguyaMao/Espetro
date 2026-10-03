/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.data.api.DataValidator
 *  cc.sighs.oelib.data.api.DataValidator$ValidationResult
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.data.validator;

import cc.sighs.auratip.data.TipData;
import cc.sighs.oelib.data.api.DataValidator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public class TipDataValidator
implements DataValidator<TipData> {
    public DataValidator.ValidationResult validate(TipData data, ResourceLocation source) {
        TipData.Position p;
        TipData.Position p2;
        TipData.Position pos;
        if (data.id() == null) {
            return DataValidator.ValidationResult.failure((String)("Tip id is empty in " + String.valueOf(source)));
        }
        List<TipData.Page> pages = data.pages();
        if (pages == null || pages.isEmpty()) {
            return DataValidator.ValidationResult.failure((String)("Tip " + String.valueOf(data.id()) + " has no pages in " + String.valueOf(source)));
        }
        HashSet<Integer> indices = new HashSet<Integer>();
        for (TipData.Page page : pages) {
            if (!indices.add(page.pageIndex())) {
                return DataValidator.ValidationResult.failure((String)("Duplicate page_index " + page.pageIndex() + " in tip " + String.valueOf(data.id()) + " at " + String.valueOf(source)));
            }
            boolean hasContent = page.title().isPresent() || page.subtitle().isPresent() || page.content().isPresent() || page.image().isPresent();
            if (hasContent) continue;
            return DataValidator.ValidationResult.failure((String)("Page " + page.pageIndex() + " of tip " + String.valueOf(data.id()) + " has no content in " + String.valueOf(source)));
        }
        TipData.VisualSettings visual = data.visualSettings();
        if (visual == null) {
            return DataValidator.ValidationResult.failure((String)("visual_settings is missing in tip " + String.valueOf(data.id()) + " at " + String.valueOf(source)));
        }
        if (visual.width() <= 0 || visual.height() <= 0) {
            return DataValidator.ValidationResult.failure((String)("width and height must be > 0 in tip " + String.valueOf(data.id()) + " at " + String.valueOf(source)));
        }
        TipData.VisualSettings.Background background = visual.background();
        if (background != null) {
            List<String> colors;
            if (background.borderRadius() < 0) {
                return DataValidator.ValidationResult.failure((String)("background.border_radius must be >= 0 in tip " + String.valueOf(data.id()) + " at " + String.valueOf(source)));
            }
            if (!(background.type() != TipData.VisualSettings.BackgroundType.GRADIENT && background.type() != TipData.VisualSettings.BackgroundType.SOLID || (colors = background.colors()) != null && !colors.isEmpty())) {
                return DataValidator.ValidationResult.failure((String)(background.type().name().toLowerCase(Locale.ROOT) + " background requires at least one color in tip " + String.valueOf(data.id()) + " at " + String.valueOf(source)));
            }
        }
        if ((pos = visual.position()) != null && pos.absolute() && (pos.x() < 0 || pos.y() < 0)) {
            return DataValidator.ValidationResult.failure((String)("visual position coordinates must be >= 0 in tip " + String.valueOf(data.id()) + " at " + String.valueOf(source)));
        }
        Optional<TipData.Position> animationFrom = visual.animationFrom();
        if (animationFrom.isPresent() && animationFrom.get().absolute() && ((p2 = animationFrom.get()).x() < 0 || p2.y() < 0)) {
            return DataValidator.ValidationResult.failure((String)("animation_from coordinates must be >= 0 in tip " + String.valueOf(data.id()) + " at " + String.valueOf(source)));
        }
        Optional<TipData.Position> animationTo = visual.animationTo();
        if (animationTo.isPresent() && animationTo.get().absolute() && ((p = animationTo.get()).x() < 0 || p.y() < 0)) {
            return DataValidator.ValidationResult.failure((String)("animation_to coordinates must be >= 0 in tip " + String.valueOf(data.id()) + " at " + String.valueOf(source)));
        }
        if (visual.hoverAnimationSpeed() < 0.0f) {
            return DataValidator.ValidationResult.failure((String)("hover_animation_speed must be >= 0 in tip " + String.valueOf(data.id()) + " at " + String.valueOf(source)));
        }
        TipData.Behavior behavior = data.behavior();
        if (behavior.defaultDuration() < -1) {
            return DataValidator.ValidationResult.failure((String)("default_duration must be >= -1 in tip " + String.valueOf(data.id()) + " at " + String.valueOf(source)));
        }
        if (behavior.closableByKey().isPresent() && behavior.closableByKey().get().isBlank()) {
            return DataValidator.ValidationResult.failure((String)("closable_by_key is blank in tip " + String.valueOf(data.id()) + " at " + String.valueOf(source)));
        }
        return DataValidator.ValidationResult.success();
    }
}

