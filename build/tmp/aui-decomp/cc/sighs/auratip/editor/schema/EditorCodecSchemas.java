/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 */
package cc.sighs.auratip.editor.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public final class EditorCodecSchemas {
    private EditorCodecSchemas() {
    }

    public static JsonObject buildAll() {
        JsonObject out = new JsonObject();
        out.add("tip", (JsonElement)EditorCodecSchemas.tip());
        out.add("radial", (JsonElement)EditorCodecSchemas.radial());
        return out;
    }

    private static JsonObject tip() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("id", EditorCodecSchemas.rl(), false, null, "tip.id", "tip.id.desc"), EditorCodecSchemas.f("trigger", EditorCodecSchemas.tipTrigger(), false, null, "tip.trigger", "tip.trigger.desc"), EditorCodecSchemas.f("visual_settings", EditorCodecSchemas.tipVisualSettings(), false, null, "tip.visual_settings", "tip.visual_settings.desc"), EditorCodecSchemas.f("behavior", EditorCodecSchemas.tipBehavior(), false, null, "tip.behavior", "tip.behavior.desc"), EditorCodecSchemas.f("pages", EditorCodecSchemas.array(EditorCodecSchemas.tipPage()), false, null, "tip.pages", "tip.pages.desc"));
    }

    private static JsonObject tipTrigger() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("type", EditorCodecSchemas.rl(), false, (JsonElement)EditorCodecSchemas.rlVal("auratip:first_join_world"), "tip.trigger.type", "tip.trigger.type.desc"), EditorCodecSchemas.f("mode", EditorCodecSchemas.enumOf("once", "repeatable"), true, (JsonElement)EditorCodecSchemas.str("once"), "tip.trigger.mode", "tip.trigger.mode.desc"), EditorCodecSchemas.f("cooldown", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(0.0), "tip.trigger.cooldown", "tip.trigger.cooldown.desc"));
    }

    private static JsonObject tipVisualSettings() {
        JsonObject bgDefault = new JsonObject();
        bgDefault.addProperty("type", "gradient");
        JsonArray colors = new JsonArray();
        colors.add("#FFE0F7FF");
        colors.add("#FFB3E5FC");
        bgDefault.add("colors", (JsonElement)colors);
        bgDefault.addProperty("border_radius", (Number)8);
        bgDefault.addProperty("rounded", Boolean.valueOf(true));
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("animation_style", EditorCodecSchemas.rl(), true, (JsonElement)EditorCodecSchemas.rlVal("auratip:fade_and_slide"), "tip.visual.animation_style", "tip.visual.animation_style.desc"), EditorCodecSchemas.f("animation_params", EditorCodecSchemas.dynamicMap(), true, (JsonElement)new JsonObject(), "tip.visual.animation_params", "tip.visual.animation_params.desc"), EditorCodecSchemas.f("animation_speed", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(1.0), "tip.visual.animation_speed", "tip.visual.animation_speed.desc"), EditorCodecSchemas.f("animation_from", EditorCodecSchemas.position(), true, (JsonElement)EditorCodecSchemas.str("BOTTOM_CENTER"), "tip.visual.animation_from", "tip.visual.animation_from.desc"), EditorCodecSchemas.f("animation_to", EditorCodecSchemas.position(), true, (JsonElement)EditorCodecSchemas.str("BOTTOM_CENTER"), "tip.visual.animation_to", "tip.visual.animation_to.desc"), EditorCodecSchemas.f("hover_animation_style", EditorCodecSchemas.rl(), true, (JsonElement)EditorCodecSchemas.rlVal("auratip:none"), "tip.visual.hover_animation_style", "tip.visual.hover_animation_style.desc"), EditorCodecSchemas.f("hover_animation_params", EditorCodecSchemas.dynamicMap(), true, (JsonElement)new JsonObject(), "tip.visual.hover_animation_params", "tip.visual.hover_animation_params.desc"), EditorCodecSchemas.f("hover_animation_speed", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(1.0), "tip.visual.hover_animation_speed", "tip.visual.hover_animation_speed.desc"), EditorCodecSchemas.f("hover_only_on_hover", EditorCodecSchemas.bool(), true, (JsonElement)EditorCodecSchemas.boolVal(false), "tip.visual.hover_only_on_hover", "tip.visual.hover_only_on_hover.desc"), EditorCodecSchemas.f("background", EditorCodecSchemas.tipBackground(), true, (JsonElement)bgDefault, "tip.visual.background", "tip.visual.background.desc"), EditorCodecSchemas.f("theme_color", EditorCodecSchemas.string(), true, (JsonElement)EditorCodecSchemas.str("#FFFFFFFF"), "tip.visual.theme_color", "tip.visual.theme_color.desc"), EditorCodecSchemas.f("stripe_width", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(4.0), "tip.visual.stripe_width", "tip.visual.stripe_width.desc"), EditorCodecSchemas.f("stripe_length_factor", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(1.0), "tip.visual.stripe_length_factor", "tip.visual.stripe_length_factor.desc"), EditorCodecSchemas.f("width", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(280.0), "tip.visual.width", "tip.visual.width.desc"), EditorCodecSchemas.f("height", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(180.0), "tip.visual.height", "tip.visual.height.desc"), EditorCodecSchemas.f("position", EditorCodecSchemas.position(), true, (JsonElement)EditorCodecSchemas.str("BOTTOM_CENTER"), "tip.visual.position", "tip.visual.position.desc"), EditorCodecSchemas.f("layout", EditorCodecSchemas.tipLayout(), true, (JsonElement)EditorCodecSchemas.layoutDefault(), "tip.visual.layout", "tip.visual.layout.desc"));
    }

    private static JsonObject tipBackground() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("type", EditorCodecSchemas.enumOf("gradient", "solid", "image"), true, (JsonElement)EditorCodecSchemas.str("gradient"), "tip.visual.background.type", "tip.visual.background.type.desc"), EditorCodecSchemas.f("colors", EditorCodecSchemas.array(EditorCodecSchemas.string()), true, (JsonElement)EditorCodecSchemas.listStr("#FFE0F7FF", "#FFB3E5FC"), "tip.visual.background.colors", "tip.visual.background.colors.desc"), EditorCodecSchemas.f("border_radius", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(8.0), "tip.visual.background.border_radius", "tip.visual.background.border_radius.desc"), EditorCodecSchemas.f("rounded", EditorCodecSchemas.bool(), true, (JsonElement)EditorCodecSchemas.boolVal(true), "tip.visual.background.rounded", "tip.visual.background.rounded.desc"), EditorCodecSchemas.f("image_path", EditorCodecSchemas.string(), true, (JsonElement)EditorCodecSchemas.str("minecraft:textures/block/stone.png"), "tip.visual.background.image_path", "tip.visual.background.image_path.desc"), EditorCodecSchemas.f("shadow", EditorCodecSchemas.tipShadowConfig(), true, (JsonElement)new JsonObject(), "tip.visual.background.shadow", "tip.visual.background.shadow.desc"));
    }

    private static JsonObject tipBehavior() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("default_duration", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(200.0), "tip.behavior.default_duration", "tip.behavior.default_duration.desc"), EditorCodecSchemas.f("pause_timer_on_hover", EditorCodecSchemas.bool(), true, (JsonElement)EditorCodecSchemas.boolVal(true), "tip.behavior.pause_timer_on_hover", "tip.behavior.pause_timer_on_hover.desc"), EditorCodecSchemas.f("closable_by_key", EditorCodecSchemas.string(), true, (JsonElement)EditorCodecSchemas.str("key.keyboard.escape"), "tip.behavior.closable_by_key", "tip.behavior.closable_by_key.desc"), EditorCodecSchemas.f("allow_paging", EditorCodecSchemas.bool(), true, (JsonElement)EditorCodecSchemas.boolVal(true), "tip.behavior.allow_paging", "tip.behavior.allow_paging.desc"), EditorCodecSchemas.f("show_close_button", EditorCodecSchemas.bool(), true, (JsonElement)EditorCodecSchemas.boolVal(true), "tip.behavior.show_close_button", "tip.behavior.show_close_button.desc"), EditorCodecSchemas.f("show_page_indicator", EditorCodecSchemas.bool(), true, (JsonElement)EditorCodecSchemas.boolVal(true), "tip.behavior.show_page_indicator", "tip.behavior.show_page_indicator.desc"));
    }

    private static JsonObject tipPage() {
        JsonObject base = new JsonObject();
        base.addProperty("page_index", (Number)0);
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("page_index", EditorCodecSchemas.number(), false, (JsonElement)EditorCodecSchemas.num(0.0), "tip.page.page_index", "tip.page.page_index.desc"), EditorCodecSchemas.f("title", EditorCodecSchemas.textElement(), true, (JsonElement)EditorCodecSchemas.defaultTextElement("Title"), "tip.page.title", "tip.page.title.desc"), EditorCodecSchemas.f("subtitle", EditorCodecSchemas.textElement(), true, (JsonElement)EditorCodecSchemas.defaultTextElement("Subtitle"), "tip.page.subtitle", "tip.page.subtitle.desc"), EditorCodecSchemas.f("content", EditorCodecSchemas.textElement(), true, (JsonElement)EditorCodecSchemas.defaultTextElement("Content"), "tip.page.content", "tip.page.content.desc"), EditorCodecSchemas.f("image", EditorCodecSchemas.tipImage(), true, (JsonElement)EditorCodecSchemas.defaultImage(), "tip.page.image", "tip.page.image.desc"), EditorCodecSchemas.f("badge", EditorCodecSchemas.tipBadge(), true, (JsonElement)EditorCodecSchemas.badgeDefault(), "tip.page.badge", "tip.page.badge.desc")).deepCopy();
    }

    private static JsonObject tipImage() {
        JsonArray size = new JsonArray();
        size.add((Number)64);
        size.add((Number)64);
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("path", EditorCodecSchemas.string(), false, (JsonElement)EditorCodecSchemas.str("minecraft:textures/item/apple.png"), "tip.page.image.path", "tip.page.image.path.desc"), EditorCodecSchemas.f("position", EditorCodecSchemas.position(), true, (JsonElement)EditorCodecSchemas.str("TOP_CENTER"), "tip.page.image.position", "tip.page.image.position.desc"), EditorCodecSchemas.f("size", EditorCodecSchemas.fixedIntArray2(), true, (JsonElement)size, "tip.page.image.size", "tip.page.image.size.desc"), EditorCodecSchemas.f("scale", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(1.0), "tip.page.image.scale", "tip.page.image.scale.desc"));
    }

    private static JsonObject position() {
        JsonObject preset = new JsonObject();
        preset.addProperty("label", "preset");
        preset.add("type", (JsonElement)EditorCodecSchemas.enumOf("TOP_LEFT", "TOP_CENTER", "TOP_RIGHT", "LEFT_CENTER", "CENTER", "RIGHT_CENTER", "BOTTOM_LEFT", "BOTTOM_CENTER", "BOTTOM_RIGHT"));
        JsonObject abs = new JsonObject();
        abs.addProperty("label", "absolute");
        abs.add("type", (JsonElement)EditorCodecSchemas.fixedIntArray2());
        JsonArray variants = new JsonArray();
        variants.add((JsonElement)preset);
        variants.add((JsonElement)abs);
        JsonObject out = new JsonObject();
        out.addProperty("kind", "either");
        out.add("variants", (JsonElement)variants);
        return out;
    }

    private static JsonObject textElement() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("text", EditorCodecSchemas.component(), false, (JsonElement)EditorCodecSchemas.componentLiteral("Text"), "text_element.text", "text_element.text.desc"), EditorCodecSchemas.f("scale", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(1.0), "text_element.scale", "text_element.scale.desc"), EditorCodecSchemas.f("line_spacing", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(0.0), "text_element.line_spacing", "text_element.line_spacing.desc"), EditorCodecSchemas.f("divider", EditorCodecSchemas.divider(), true, (JsonElement)EditorCodecSchemas.defaultDivider(), "text_element.divider", "text_element.divider.desc"));
    }

    private static JsonObject divider() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("thickness", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(1.0), "divider.thickness", "divider.thickness.desc"), EditorCodecSchemas.f("margin_top", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(4.0), "divider.margin_top", "divider.margin_top.desc"), EditorCodecSchemas.f("margin_bottom", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(4.0), "divider.margin_bottom", "divider.margin_bottom.desc"), EditorCodecSchemas.f("length", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(1.0), "divider.length", "divider.length.desc"), EditorCodecSchemas.f("color", EditorCodecSchemas.string(), true, (JsonElement)EditorCodecSchemas.str(""), "divider.color", "divider.color.desc"));
    }

    private static JsonObject radial() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("id", EditorCodecSchemas.rl(), false, (JsonElement)EditorCodecSchemas.rlVal("auratip:menu"), "radial.id", "radial.id.desc"), EditorCodecSchemas.f("menu_settings", EditorCodecSchemas.radialSettings(), false, null, "radial.menu_settings", "radial.menu_settings.desc"), EditorCodecSchemas.f("slots", EditorCodecSchemas.array(EditorCodecSchemas.radialSlot()), false, null, "radial.slots", "radial.slots.desc"));
    }

    private static JsonObject radialSettings() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("inner_radius", EditorCodecSchemas.number(), false, (JsonElement)EditorCodecSchemas.num(55.0), "radial.menu_settings.inner_radius", "radial.menu_settings.inner_radius.desc"), EditorCodecSchemas.f("outer_radius", EditorCodecSchemas.number(), false, (JsonElement)EditorCodecSchemas.num(100.0), "radial.menu_settings.outer_radius", "radial.menu_settings.outer_radius.desc"), EditorCodecSchemas.f("animation_speed", EditorCodecSchemas.number(), false, (JsonElement)EditorCodecSchemas.num(1.0), "radial.menu_settings.animation_speed", "radial.menu_settings.animation_speed.desc"), EditorCodecSchemas.f("ring_color", EditorCodecSchemas.string(), true, (JsonElement)EditorCodecSchemas.str("#77FFFFFF"), "radial.menu_settings.ring_color", "radial.menu_settings.ring_color.desc"), EditorCodecSchemas.f("ring_colors", EditorCodecSchemas.array(EditorCodecSchemas.string()), true, (JsonElement)EditorCodecSchemas.listStr("#77FFFFFF", "#33FFFFFF"), "radial.menu_settings.ring_colors", "radial.menu_settings.ring_colors.desc"), EditorCodecSchemas.f("close_key", EditorCodecSchemas.string(), true, (JsonElement)EditorCodecSchemas.str("key.keyboard.escape"), "radial.menu_settings.close_key", "radial.menu_settings.close_key.desc"));
    }

    private static JsonObject radialSlot() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("name", EditorCodecSchemas.string(), false, (JsonElement)EditorCodecSchemas.str("Slot"), "radial.slot.name", "radial.slot.name.desc"), EditorCodecSchemas.f("icon", EditorCodecSchemas.radialIcon(), false, (JsonElement)EditorCodecSchemas.rlVal("minecraft:textures/item/paper.png"), "radial.slot.icon", "radial.slot.icon.desc"), EditorCodecSchemas.f("action", EditorCodecSchemas.action(), false, (JsonElement)EditorCodecSchemas.actionDefaultRunCommand(), "radial.slot.action", "radial.slot.action.desc"), EditorCodecSchemas.f("text", EditorCodecSchemas.component(), true, (JsonElement)EditorCodecSchemas.componentLiteral("Slot"), "radial.slot.text", "radial.slot.text.desc"), EditorCodecSchemas.f("highlight_color", EditorCodecSchemas.string(), true, (JsonElement)EditorCodecSchemas.str("#FFFFFF"), "radial.slot.highlight_color", "radial.slot.highlight_color.desc"), EditorCodecSchemas.f("close_after_action", EditorCodecSchemas.bool(), true, (JsonElement)EditorCodecSchemas.boolVal(true), "radial.slot.close_after_action", "radial.slot.close_after_action.desc"), EditorCodecSchemas.f("base_color", EditorCodecSchemas.string(), true, (JsonElement)EditorCodecSchemas.str("#40FFFFFF"), "radial.slot.base_color", "radial.slot.base_color.desc"));
    }

    private static JsonObject radialIcon() {
        JsonObject texture = new JsonObject();
        texture.addProperty("label", "texture");
        texture.add("type", (JsonElement)EditorCodecSchemas.textureIconType());
        texture.add("default", (JsonElement)EditorCodecSchemas.textureIconDefault());
        JsonObject item = new JsonObject();
        item.addProperty("label", "item");
        item.add("type", (JsonElement)EditorCodecSchemas.itemIconType());
        item.add("default", (JsonElement)EditorCodecSchemas.itemIconDefault());
        JsonArray variants = new JsonArray();
        variants.add((JsonElement)texture);
        variants.add((JsonElement)item);
        JsonObject out = new JsonObject();
        out.addProperty("kind", "either");
        out.add("variants", (JsonElement)variants);
        return out;
    }

    private static JsonObject textureIconType() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("type", EditorCodecSchemas.enumOf("auratip:texture"), false, (JsonElement)EditorCodecSchemas.str("auratip:texture"), "radial.slot.icon.texture.type", "radial.slot.icon.texture.type.desc"), EditorCodecSchemas.f("id", EditorCodecSchemas.rl(), false, (JsonElement)EditorCodecSchemas.rlVal("minecraft:textures/item/paper.png"), "radial.slot.icon.texture.id", "radial.slot.icon.texture.id.desc"), EditorCodecSchemas.f("scale", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(1.0), "radial.slot.icon.texture.scale", "radial.slot.icon.texture.scale.desc"));
    }

    private static JsonObject textureIconDefault() {
        JsonObject o = new JsonObject();
        o.addProperty("type", "auratip:texture");
        o.addProperty("id", "minecraft:textures/item/paper.png");
        o.addProperty("scale", (Number)1.0);
        return o;
    }

    private static JsonObject itemIconType() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("type", EditorCodecSchemas.enumOf("auratip:item"), false, (JsonElement)EditorCodecSchemas.str("auratip:item"), "radial.slot.icon.item.type", "radial.slot.icon.item.type.desc"), EditorCodecSchemas.f("stack", EditorCodecSchemas.itemStackFields(), false, null, "radial.slot.icon.item.stack", "radial.slot.icon.item.stack.desc"), EditorCodecSchemas.f("scale", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(1.0), "radial.slot.icon.item.scale", "radial.slot.icon.item.scale.desc"));
    }

    private static JsonObject itemStackFields() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("id", EditorCodecSchemas.rl(), false, (JsonElement)EditorCodecSchemas.rlVal("minecraft:chest"), "radial.slot.icon.item.id", "radial.slot.icon.item.id.desc"), EditorCodecSchemas.f("Count", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(1.0), "radial.slot.icon.item.Count", "radial.slot.icon.item.Count.desc"), EditorCodecSchemas.f("tag", EditorCodecSchemas.dynamicMap(), true, (JsonElement)new JsonObject(), "radial.slot.icon.item.tag", "radial.slot.icon.item.tag.desc"));
    }

    private static JsonObject itemIconDefault() {
        JsonObject o = new JsonObject();
        JsonObject st = new JsonObject();
        st.addProperty("id", "minecraft:chest");
        st.addProperty("Count", (Number)1);
        st.add("tag", (JsonElement)new JsonObject());
        o.add("stack", (JsonElement)st);
        o.addProperty("scale", (Number)1.0);
        return o;
    }

    private static JsonObject action() {
        JsonObject out = new JsonObject();
        out.addProperty("kind", "action");
        return out;
    }

    private static JsonObject f(String name, JsonObject type, boolean optional, JsonElement def, String labelKey, String descKey) {
        JsonObject out = new JsonObject();
        out.addProperty("name", name);
        out.add("type", (JsonElement)type);
        if (labelKey != null && !labelKey.isBlank()) {
            out.addProperty("label_key", labelKey);
        }
        if (descKey != null && !descKey.isBlank()) {
            out.addProperty("desc_key", descKey);
        }
        if (optional) {
            out.addProperty("optional", Boolean.valueOf(true));
        }
        if (def != null) {
            out.add("default", def);
        }
        return out;
    }

    private static JsonObject obj(JsonObject ... fields) {
        JsonObject out = new JsonObject();
        out.addProperty("kind", "object");
        JsonArray arr = new JsonArray();
        for (JsonObject f : fields) {
            arr.add((JsonElement)f);
        }
        out.add("fields", (JsonElement)arr);
        return out;
    }

    private static JsonObject array(JsonObject itemType) {
        JsonObject out = new JsonObject();
        out.addProperty("kind", "array");
        out.add("item", (JsonElement)itemType);
        return out;
    }

    private static JsonObject fixedIntArray2() {
        JsonObject out = new JsonObject();
        out.addProperty("kind", "fixed_int_array2");
        return out;
    }

    private static JsonObject dynamicMap() {
        JsonObject out = new JsonObject();
        out.addProperty("kind", "map_dynamic");
        return out;
    }

    private static JsonObject string() {
        JsonObject out = new JsonObject();
        out.addProperty("kind", "string");
        return out;
    }

    private static JsonObject number() {
        JsonObject out = new JsonObject();
        out.addProperty("kind", "number");
        return out;
    }

    private static JsonObject bool() {
        JsonObject out = new JsonObject();
        out.addProperty("kind", "boolean");
        return out;
    }

    private static JsonObject rl() {
        JsonObject out = new JsonObject();
        out.addProperty("kind", "resource_location");
        return out;
    }

    private static JsonObject component() {
        JsonObject out = new JsonObject();
        out.addProperty("kind", "component");
        return out;
    }

    private static JsonObject enumOf(String ... values) {
        JsonObject out = new JsonObject();
        out.addProperty("kind", "enum");
        JsonArray opts = new JsonArray();
        for (String v : values) {
            opts.add(v);
        }
        out.add("options", (JsonElement)opts);
        return out;
    }

    private static JsonPrimitive str(String s) {
        return new JsonPrimitive(s == null ? "" : s);
    }

    private static JsonPrimitive boolVal(boolean b) {
        return new JsonPrimitive(Boolean.valueOf(b));
    }

    private static JsonPrimitive num(double v) {
        return new JsonPrimitive((Number)v);
    }

    private static JsonPrimitive rlVal(String s) {
        return new JsonPrimitive(s);
    }

    private static JsonArray listStr(String ... values) {
        JsonArray arr = new JsonArray();
        for (String v : values) {
            arr.add(v);
        }
        return arr;
    }

    private static JsonObject componentLiteral(String text) {
        JsonObject o = new JsonObject();
        o.addProperty("text", text == null ? "" : text);
        return o;
    }

    private static JsonObject defaultDivider() {
        JsonObject o = new JsonObject();
        o.addProperty("thickness", (Number)1);
        o.addProperty("margin_top", (Number)4);
        o.addProperty("margin_bottom", (Number)4);
        o.addProperty("length", (Number)1.0);
        o.addProperty("color", "");
        return o;
    }

    private static JsonObject defaultTextElement(String text) {
        JsonObject o = new JsonObject();
        o.add("text", (JsonElement)EditorCodecSchemas.componentLiteral(text));
        o.addProperty("scale", (Number)1.0);
        o.addProperty("line_spacing", (Number)0);
        return o;
    }

    private static JsonObject defaultImage() {
        JsonObject o = new JsonObject();
        o.addProperty("path", "minecraft:textures/item/apple.png");
        o.addProperty("position", "TOP_CENTER");
        JsonArray size = new JsonArray();
        size.add((Number)64);
        size.add((Number)64);
        o.add("size", (JsonElement)size);
        o.addProperty("scale", (Number)1.0);
        return o;
    }

    private static JsonObject actionDefaultRunCommand() {
        JsonObject o = new JsonObject();
        o.addProperty("type", "auratip:run_command");
        o.addProperty("command", "/say hello");
        return o;
    }

    private static JsonObject tipLayout() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("padding", EditorCodecSchemas.padding(), true, (JsonElement)EditorCodecSchemas.paddingDefault(), "tip.visual.layout.padding", "tip.visual.layout.padding.desc"), EditorCodecSchemas.f("element_spacing", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(4.0), "tip.visual.layout.element_spacing", "tip.visual.layout.element_spacing.desc"));
    }

    private static JsonObject padding() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("top", EditorCodecSchemas.number(), false, (JsonElement)EditorCodecSchemas.num(12.0), "tip.visual.layout.padding.top", "tip.visual.layout.padding.top.desc"), EditorCodecSchemas.f("right", EditorCodecSchemas.number(), false, (JsonElement)EditorCodecSchemas.num(12.0), "tip.visual.layout.padding.right", "tip.visual.layout.padding.right.desc"), EditorCodecSchemas.f("bottom", EditorCodecSchemas.number(), false, (JsonElement)EditorCodecSchemas.num(12.0), "tip.visual.layout.padding.bottom", "tip.visual.layout.padding.bottom.desc"), EditorCodecSchemas.f("left", EditorCodecSchemas.number(), false, (JsonElement)EditorCodecSchemas.num(12.0), "tip.visual.layout.padding.left", "tip.visual.layout.padding.left.desc"));
    }

    private static JsonObject tipShadowConfig() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("enabled", EditorCodecSchemas.bool(), false, (JsonElement)EditorCodecSchemas.boolVal(false), "tip.visual.background.shadow.enabled", "tip.visual.background.shadow.enabled.desc"), EditorCodecSchemas.f("color", EditorCodecSchemas.string(), true, (JsonElement)EditorCodecSchemas.str("#8C000000"), "tip.visual.background.shadow.color", "tip.visual.background.shadow.color.desc"), EditorCodecSchemas.f("offset_x", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(2.0), "tip.visual.background.shadow.offset_x", "tip.visual.background.shadow.offset_x.desc"), EditorCodecSchemas.f("offset_y", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(2.0), "tip.visual.background.shadow.offset_y", "tip.visual.background.shadow.offset_y.desc"), EditorCodecSchemas.f("size", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(4.0), "tip.visual.background.shadow.size", "tip.visual.background.shadow.size.desc"));
    }

    private static JsonObject tipBadge() {
        return EditorCodecSchemas.obj(EditorCodecSchemas.f("text", EditorCodecSchemas.textElement(), false, (JsonElement)EditorCodecSchemas.defaultTextElement("Badge"), "tip.page.badge.text", "tip.page.badge.text.desc"), EditorCodecSchemas.f("background_color", EditorCodecSchemas.string(), true, (JsonElement)EditorCodecSchemas.str("#CC000000"), "tip.page.badge.background_color", "tip.page.badge.background_color.desc"), EditorCodecSchemas.f("radius", EditorCodecSchemas.number(), true, (JsonElement)EditorCodecSchemas.num(4.0), "tip.page.badge.radius", "tip.page.badge.radius.desc"), EditorCodecSchemas.f("position", EditorCodecSchemas.position(), true, (JsonElement)EditorCodecSchemas.str("BOTTOM_RIGHT"), "tip.page.badge.position", "tip.page.badge.position.desc"));
    }

    private static JsonObject paddingDefault() {
        JsonObject o = new JsonObject();
        o.addProperty("top", (Number)12);
        o.addProperty("right", (Number)12);
        o.addProperty("bottom", (Number)12);
        o.addProperty("left", (Number)12);
        return o;
    }

    private static JsonObject badgeDefault() {
        JsonObject o = new JsonObject();
        o.add("text", (JsonElement)EditorCodecSchemas.componentLiteral("Badge"));
        o.addProperty("background_color", "#CC000000");
        o.addProperty("radius", (Number)4);
        o.addProperty("position", "BOTTOM_RIGHT");
        return o;
    }

    private static JsonObject layoutDefault() {
        JsonObject o = new JsonObject();
        o.add("padding", (JsonElement)EditorCodecSchemas.paddingDefault());
        o.addProperty("element_spacing", (Number)4);
        return o;
    }
}

