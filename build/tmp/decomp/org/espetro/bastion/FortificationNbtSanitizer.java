/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.bastion;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

final class FortificationNbtSanitizer {
    private static final Set<String> BLOCK_ENTITY_TYPES = Set.of("minecraft:banner", "minecraft:sign", "minecraft:hanging_sign", "minecraft:skull", "minecraft:decorated_pot");
    private static final Set<String> ENTITY_TYPES = Set.of("minecraft:armor_stand", "minecraft:item_frame", "minecraft:glow_item_frame", "minecraft:painting", "minecraft:text_display", "minecraft:item_display", "minecraft:block_display", "minecraft:interaction");
    private static final Set<String> COMMON_ENTITY_VISUAL = Set.of("CustomName", "CustomNameVisible", "Silent", "NoGravity", "Invulnerable", "Glowing", "Tags");
    private static final Set<String> ARMOR_STAND_VISUAL = Set.of("Pose", "Small", "ShowArms", "NoBasePlate", "Marker", "Invisible", "DisabledSlots");
    private static final Set<String> DISPLAY_VISUAL = Set.of("transformation", "billboard", "brightness", "view_range", "shadow_radius", "shadow_strength", "width", "height", "glow_color_override", "text", "line_width", "background", "text_opacity", "style_flags", "item", "block_state", "interpolation_duration", "start_interpolation");
    private static final Set<String> ITEM_FRAME_VISUAL = Set.of("Item", "ItemRotation", "Invisible", "Fixed", "Facing");
    private static final Set<String> PAINTING_VISUAL = Set.of("variant", "facing", "Facing");
    private static final Set<String> BLOCK_ENTITY_COMMON = Set.of("CustomName");
    private static final Set<String> BANNER_VISUAL = Set.of("Patterns", "Base", "CustomName");
    private static final Set<String> SIGN_VISUAL = Set.of("front_text", "back_text", "is_waxed", "Color", "GlowingText", "Text1", "Text2", "Text3", "Text4", "CustomName");
    private static final Set<String> SKULL_VISUAL = Set.of("SkullOwner", "ExtraType", "note_block_sound", "CustomName");
    private static final Set<String> POT_VISUAL = Set.of("sherds", "CustomName");

    private FortificationNbtSanitizer() {
    }

    @Nullable
    static CompoundTag sanitizeBlockEntity(@Nullable CompoundTag input, int maxBytes) {
        if (input == null) {
            return null;
        }
        String id = FortificationNbtSanitizer.canonicalId(input.m_128461_("id"));
        if (!BLOCK_ENTITY_TYPES.contains(id)) {
            throw new IllegalArgumentException("\u4e0d\u5141\u8bb8\u7684\u65b9\u5757\u5b9e\u4f53\u7c7b\u578b: " + id);
        }
        Set<String> allowed = switch (id) {
            case "minecraft:banner" -> BANNER_VISUAL;
            case "minecraft:sign", "minecraft:hanging_sign" -> SIGN_VISUAL;
            case "minecraft:skull" -> SKULL_VISUAL;
            case "minecraft:decorated_pot" -> POT_VISUAL;
            default -> BLOCK_ENTITY_COMMON;
        };
        CompoundTag output = FortificationNbtSanitizer.copyAllowed(input, allowed);
        output.m_128359_("id", id);
        FortificationNbtSanitizer.ensureSize(output, maxBytes, "\u65b9\u5757\u5b9e\u4f53 " + id);
        return output;
    }

    static CompoundTag sanitizeEntity(CompoundTag input, int depth, int maxDepth, int maxBytes) {
        if (depth > maxDepth) {
            throw new IllegalArgumentException("\u5b9e\u4f53\u4e58\u5ba2\u6df1\u5ea6\u8d85\u8fc7 " + maxDepth);
        }
        String id = FortificationNbtSanitizer.canonicalId(input.m_128461_("id"));
        if ("minecraft:player".equals(id) || !ENTITY_TYPES.contains(id)) {
            throw new IllegalArgumentException("\u4e0d\u5141\u8bb8\u7684\u7ed3\u6784\u5b9e\u4f53\u7c7b\u578b: " + id);
        }
        CompoundTag output = FortificationNbtSanitizer.copyAllowed(input, COMMON_ENTITY_VISUAL);
        Set<String> specific = switch (id) {
            case "minecraft:armor_stand" -> ARMOR_STAND_VISUAL;
            case "minecraft:item_frame", "minecraft:glow_item_frame" -> ITEM_FRAME_VISUAL;
            case "minecraft:painting" -> PAINTING_VISUAL;
            case "minecraft:text_display", "minecraft:item_display", "minecraft:block_display" -> DISPLAY_VISUAL;
            default -> Set.of("width", "height", "response");
        };
        FortificationNbtSanitizer.mergeAllowed(input, output, specific);
        output.m_128359_("id", id);
        if (input.m_128425_("Passengers", 9)) {
            ListTag cleanPassengers = new ListTag();
            ListTag passengers = input.m_128437_("Passengers", 10);
            for (Tag passenger : passengers) {
                cleanPassengers.add(FortificationNbtSanitizer.sanitizeEntity((CompoundTag)passenger, depth + 1, maxDepth, maxBytes));
            }
            if (!cleanPassengers.isEmpty()) {
                output.m_128365_("Passengers", cleanPassengers);
            }
        }
        FortificationNbtSanitizer.ensureSize(output, maxBytes, "\u5b9e\u4f53 " + id);
        return output;
    }

    static CompoundTag sanitizeRootEntity(CompoundTag input, ResourceLocation expectedType, int maxDepth, int maxBytes) {
        String actual = FortificationNbtSanitizer.canonicalId(input.m_128461_("id"));
        if (!expectedType.toString().equals(actual) || "minecraft:player".equals(actual)) {
            throw new IllegalArgumentException("\u72ec\u7acb\u5b9e\u4f53 id \u4e0e\u914d\u7f6e\u4e0d\u5339\u914d: " + actual);
        }
        CompoundTag output = FortificationNbtSanitizer.copyAllowed(input, COMMON_ENTITY_VISUAL);
        output.m_128359_("id", actual);
        if (input.m_128425_("Passengers", 9)) {
            ListTag passengers = input.m_128437_("Passengers", 10);
            ListTag clean = new ListTag();
            for (Tag passenger : passengers) {
                clean.add(FortificationNbtSanitizer.sanitizeEntity((CompoundTag)passenger, 1, maxDepth, maxBytes));
            }
            if (!clean.isEmpty()) {
                output.m_128365_("Passengers", clean);
            }
        }
        FortificationNbtSanitizer.ensureSize(output, maxBytes, "\u72ec\u7acb\u5b9e\u4f53 " + actual);
        return output;
    }

    static ResourceLocation requireEntityType(CompoundTag tag) {
        ResourceLocation id = ResourceLocation.m_135820_(tag.m_128461_("id"));
        if (id == null) {
            throw new IllegalArgumentException("\u5b9e\u4f53\u7f3a\u5c11\u5408\u6cd5 id");
        }
        return id;
    }

    private static CompoundTag copyAllowed(CompoundTag input, Set<String> allowed) {
        CompoundTag output = new CompoundTag();
        FortificationNbtSanitizer.mergeAllowed(input, output, allowed);
        return output;
    }

    private static void mergeAllowed(CompoundTag input, CompoundTag output, Set<String> allowed) {
        for (String key : allowed) {
            Tag value = input.m_128423_(key);
            if (value == null) continue;
            output.m_128365_(key, value.m_6426_());
        }
    }

    private static String canonicalId(String raw) {
        ResourceLocation parsed = ResourceLocation.m_135820_(raw);
        if (parsed == null) {
            throw new IllegalArgumentException("\u7f3a\u5c11\u5408\u6cd5\u7c7b\u578b id");
        }
        return parsed.toString();
    }

    private static void ensureSize(CompoundTag tag, int maxBytes, String what) {
        int bytes = tag.toString().getBytes(StandardCharsets.UTF_8).length;
        if (bytes > maxBytes) {
            throw new IllegalArgumentException(what + " NBT \u8d85\u8fc7 " + maxBytes + " bytes");
        }
    }
}

