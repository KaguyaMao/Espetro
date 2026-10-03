package org.espetro.bastion;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Converts untrusted structure/entity NBT into a small visual-only DTO tag.
 *
 * <p>模板文件本身由管理员导出时是**无损**的；本类只在编译（加载）阶段做过滤。
 * 过滤强度由 {@link FortificationConfig.EntityPolicy} 决定：</p>
 * <ul>
 *   <li>{@code filter}（默认）：不允许的类型/字段被剔除并记入 {@code warnings}，
 *       不会让整份工事 registry 冻结失败。</li>
 *   <li>{@code reject}：直接抛异常（旧行为）。</li>
 * </ul>
 */
final class FortificationNbtSanitizer {

    private static final Set<String> BLOCK_ENTITY_TYPES = Set.of(
        "minecraft:banner", "minecraft:sign", "minecraft:hanging_sign",
        "minecraft:skull", "minecraft:decorated_pot"
    );
    private static final Set<String> ENTITY_TYPES = Set.of(
        "minecraft:armor_stand", "minecraft:item_frame", "minecraft:glow_item_frame",
        "minecraft:painting", "minecraft:text_display", "minecraft:item_display",
        "minecraft:block_display", "minecraft:interaction"
    );
    private static final Set<String> COMMON_ENTITY_VISUAL = Set.of(
        "CustomName", "CustomNameVisible", "Silent", "NoGravity", "Invulnerable",
        "Glowing", "Tags"
    );
    private static final Set<String> ARMOR_STAND_VISUAL = Set.of(
        "Pose", "Small", "ShowArms", "NoBasePlate", "Marker", "Invisible",
        "DisabledSlots"
    );
    private static final Set<String> DISPLAY_VISUAL = Set.of(
        "transformation", "billboard", "brightness", "view_range", "shadow_radius",
        "shadow_strength", "width", "height", "glow_color_override", "text",
        "line_width", "background", "text_opacity", "style_flags", "item", "block_state",
        "interpolation_duration", "start_interpolation"
    );
    private static final Set<String> ITEM_FRAME_VISUAL = Set.of(
        "Item", "ItemRotation", "Invisible", "Fixed", "Facing"
    );
    private static final Set<String> PAINTING_VISUAL = Set.of("variant", "facing", "Facing");
    private static final Set<String> BLOCK_ENTITY_COMMON = Set.of("CustomName");
    private static final Set<String> BANNER_VISUAL = Set.of("Patterns", "Base", "CustomName");
    private static final Set<String> SIGN_VISUAL = Set.of(
        "front_text", "back_text", "is_waxed", "Color", "GlowingText",
        "Text1", "Text2", "Text3", "Text4", "CustomName"
    );
    private static final Set<String> SKULL_VISUAL = Set.of(
        "SkullOwner", "ExtraType", "note_block_sound", "CustomName"
    );
    private static final Set<String> POT_VISUAL = Set.of("sherds", "CustomName");

    private FortificationNbtSanitizer() {
    }

    /** 内置允许的方块实体类型（供管理员指令展示）。 */
    static Set<String> baseBlockEntityTypes() {
        return BLOCK_ENTITY_TYPES;
    }

    /** 内置允许的结构实体类型（供管理员指令展示）。 */
    static Set<String> baseEntityTypes() {
        return ENTITY_TYPES;
    }

    static boolean allowsBlockEntity(String id, FortificationConfig.EntityPolicy policy) {
        if (id == null) return false;
        return BLOCK_ENTITY_TYPES.contains(id)
            || (policy != null && policy.extraBlockEntityTypes().contains(id));
    }

    static boolean allowsEntity(String id, FortificationConfig.EntityPolicy policy) {
        if (id == null) return false;
        if ("minecraft:player".equals(id)) {
            return policy != null && policy.allowPlayerEntities();
        }
        return ENTITY_TYPES.contains(id)
            || (policy != null && policy.extraEntityTypes().contains(id));
    }

    /**
     * 方块实体 NBT 过滤。
     *
     * @return 过滤后的 NBT；类型不允许且策略为 filter 时返回 {@code null}
     *         （方块本体保留，只是不再带 NBT）
     */
    @Nullable
    static CompoundTag sanitizeBlockEntity(@Nullable CompoundTag input,
                                           FortificationConfig.EntityPolicy policy,
                                           int maxBytes, List<String> warnings) {
        if (input == null) return null;
        String id = canonicalId(input.getString("id"));
        if (!allowsBlockEntity(id, policy)) {
            if (strict(policy)) throw new IllegalArgumentException("不允许的方块实体类型: " + id);
            warnings.add("方块实体 " + id + " 的 NBT 已被 entity_policy 剔除（方块本体保留）");
            return null;
        }
        Set<String> allowed = switch (id) {
            case "minecraft:banner" -> BANNER_VISUAL;
            case "minecraft:sign", "minecraft:hanging_sign" -> SIGN_VISUAL;
            case "minecraft:skull" -> SKULL_VISUAL;
            case "minecraft:decorated_pot" -> POT_VISUAL;
            default -> BLOCK_ENTITY_COMMON;
        };
        CompoundTag output = copyAllowed(input, withExtras(allowed, id, policy));
        output.putString("id", id);
        ensureSize(output, maxBytes, "方块实体 " + id);
        return output;
    }

    /**
     * 结构实体 NBT 过滤。
     *
     * @return 过滤后的 NBT；类型不允许且策略为 filter 时返回 {@code null}（该实体整体剔除）
     */
    @Nullable
    static CompoundTag sanitizeEntity(CompoundTag input, int depth, int maxDepth, int maxBytes,
                                      FortificationConfig.EntityPolicy policy,
                                      List<String> warnings) {
        if (depth > maxDepth) throw new IllegalArgumentException("实体乘客深度超过 " + maxDepth);
        String id = canonicalId(input.getString("id"));
        if (!allowsEntity(id, policy)) {
            if (strict(policy)) throw new IllegalArgumentException("不允许的结构实体类型: " + id);
            warnings.add("实体 " + id + " 已被 entity_policy 剔除");
            return null;
        }
        CompoundTag output = copyAllowed(input, withExtras(COMMON_ENTITY_VISUAL, id, policy));
        Set<String> specific = switch (id) {
            case "minecraft:armor_stand" -> ARMOR_STAND_VISUAL;
            case "minecraft:item_frame", "minecraft:glow_item_frame" -> ITEM_FRAME_VISUAL;
            case "minecraft:painting" -> PAINTING_VISUAL;
            case "minecraft:text_display", "minecraft:item_display", "minecraft:block_display" -> DISPLAY_VISUAL;
            default -> Set.of("width", "height", "response");
        };
        mergeAllowed(input, output, withExtras(specific, id, policy));
        output.putString("id", id);
        if (input.contains("Passengers", Tag.TAG_LIST)) {
            ListTag cleanPassengers = new ListTag();
            ListTag passengers = input.getList("Passengers", Tag.TAG_COMPOUND);
            for (Tag passenger : passengers) {
                CompoundTag cleaned = sanitizeEntity((CompoundTag) passenger, depth + 1,
                    maxDepth, maxBytes, policy, warnings);
                if (cleaned != null) cleanPassengers.add(cleaned);
            }
            if (!cleanPassengers.isEmpty()) output.put("Passengers", cleanPassengers);
        }
        ensureSize(output, maxBytes, "实体 " + id);
        return output;
    }

    /** Independent entity fortifications may use a behavior-owned mod entity type. */
    static CompoundTag sanitizeRootEntity(CompoundTag input, ResourceLocation expectedType,
                                          int maxDepth, int maxBytes) {
        String actual = canonicalId(input.getString("id"));
        if (!expectedType.toString().equals(actual) || "minecraft:player".equals(actual)) {
            throw new IllegalArgumentException("独立实体 id 与配置不匹配: " + actual);
        }
        CompoundTag output = copyAllowed(input, COMMON_ENTITY_VISUAL);
        output.putString("id", actual);
        if (input.contains("Passengers", Tag.TAG_LIST)) {
            ListTag passengers = input.getList("Passengers", Tag.TAG_COMPOUND);
            ListTag clean = new ListTag();
            for (Tag passenger : passengers) {
                clean.add(sanitizeEntity((CompoundTag) passenger, 1, maxDepth, maxBytes,
                    new FortificationConfig.EntityPolicy(), new ArrayList<>()));
            }
            if (!clean.isEmpty()) output.put("Passengers", clean);
        }
        ensureSize(output, maxBytes, "独立实体 " + actual);
        return output;
    }

    static ResourceLocation requireEntityType(CompoundTag tag) {
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
        if (id == null) throw new IllegalArgumentException("实体缺少合法 id");
        return id;
    }

    private static boolean strict(@Nullable FortificationConfig.EntityPolicy policy) {
        return policy != null && policy.strict();
    }

    private static Set<String> withExtras(Set<String> base, String id,
                                          @Nullable FortificationConfig.EntityPolicy policy) {
        List<String> extras = policy == null ? List.of() : policy.extraFieldsFor(id);
        if (extras.isEmpty()) return base;
        Set<String> merged = new LinkedHashSet<>(base);
        merged.addAll(extras);
        return merged;
    }

    private static CompoundTag copyAllowed(CompoundTag input, Set<String> allowed) {
        CompoundTag output = new CompoundTag();
        mergeAllowed(input, output, allowed);
        return output;
    }

    private static void mergeAllowed(CompoundTag input, CompoundTag output, Set<String> allowed) {
        for (String key : allowed) {
            Tag value = input.get(key);
            if (value != null) output.put(key, value.copy());
        }
    }

    private static String canonicalId(String raw) {
        ResourceLocation parsed = ResourceLocation.tryParse(raw);
        if (parsed == null) throw new IllegalArgumentException("缺少合法类型 id");
        return parsed.toString();
    }

    private static void ensureSize(CompoundTag tag, int maxBytes, String what) {
        int bytes = tag.toString().getBytes(StandardCharsets.UTF_8).length;
        if (bytes > maxBytes) throw new IllegalArgumentException(what + " NBT 超过 " + maxBytes + " bytes");
    }
}
