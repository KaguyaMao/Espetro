/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.server.ServerLifecycleHooks
 */
package org.espetro.team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.espetro.Espetro;
import org.espetro.team.ClassAttributeBonusPolicy;
import org.espetro.team.ClassCountManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;

public class ClassEquipment {
    private static final Set<UUID> EQUIPMENT_MUTATION_PLAYERS = new HashSet<UUID>();
    private static final Map<UUID, String> LAST_EQUIPPED_KEY = new HashMap<UUID, String>();
    static final UUID CLASS_HEALTH_BONUS_ID = UUID.fromString("dd348d6d-91e3-4f54-aa7a-cd6847dad14a");
    static final UUID CLASS_SPEED_BONUS_ID = UUID.fromString("2a836d60-7e0b-4518-81f9-4f038fe51a35");

    public static boolean needsLoadout(Player player) {
        if (player == null) {
            return false;
        }
        if (!(player.m_6844_(EquipmentSlot.HEAD).m_41619_() && player.m_6844_(EquipmentSlot.CHEST).m_41619_() && player.m_6844_(EquipmentSlot.LEGS).m_41619_() && player.m_6844_(EquipmentSlot.FEET).m_41619_())) {
            return false;
        }
        int nonEmpty = 0;
        for (int i = 0; i < player.m_150109_().m_6643_(); ++i) {
            if (player.m_150109_().m_8020_(i).m_41619_() || ++nonEmpty < 4) continue;
            return false;
        }
        return true;
    }

    public static void ensureEquippedIfNeeded(ServerPlayer player) {
        boolean classChanged;
        if (player == null) {
            return;
        }
        ClassCountManager counts = ClassCountManager.getInstance();
        String classId = counts.getPlayerClass(player.m_20148_());
        String variantId = counts.getPlayerVariant(player.m_20148_());
        String factionId = counts.getPlayerFaction(player.m_20148_());
        if (classId == null || variantId == null || factionId == null) {
            return;
        }
        String currentKey = factionId + ":" + classId + ":" + variantId;
        String lastKey = LAST_EQUIPPED_KEY.get(player.m_20148_());
        boolean bl = classChanged = lastKey != null && !currentKey.equals(lastKey);
        if (!classChanged && !ClassEquipment.needsLoadout(player)) {
            return;
        }
        ClassEquipment.equipPlayer(player, factionId, classId, variantId);
    }

    public static void clearEquipment(Player player) {
        ClassEquipment.beginEquipmentMutation(player);
        try {
            ClassEquipment.clearClassBonuses(player);
            player.m_150109_().m_6211_();
            player.m_8061_(EquipmentSlot.HEAD, ItemStack.f_41583_);
            player.m_8061_(EquipmentSlot.CHEST, ItemStack.f_41583_);
            player.m_8061_(EquipmentSlot.LEGS, ItemStack.f_41583_);
            player.m_8061_(EquipmentSlot.FEET, ItemStack.f_41583_);
            player.m_8061_(EquipmentSlot.MAINHAND, ItemStack.f_41583_);
            player.m_8061_(EquipmentSlot.OFFHAND, ItemStack.f_41583_);
            player.m_150109_().m_6596_();
            if (player instanceof ServerPlayer) {
                ServerPlayer sp = (ServerPlayer)player;
                sp.f_36095_.m_142503_(ItemStack.f_41583_);
                sp.f_36096_.m_142503_(ItemStack.f_41583_);
                sp.f_36095_.m_38946_();
                sp.f_36096_.m_38946_();
            }
        }
        finally {
            ClassEquipment.endEquipmentMutation(player);
        }
    }

    public static boolean isEquipmentMutation(ServerPlayer player) {
        return player != null && EQUIPMENT_MUTATION_PLAYERS.contains(player.m_20148_());
    }

    public static void equipPlayer(Player player, String classId) {
        FactionDataLoader.ClassKitData kit;
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer sp = (ServerPlayer)player;
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            loader.ensureLoaded(server.m_177941_());
        }
        if ((kit = loader.getClassKit(classId)) == null) {
            Espetro.LOGGER.warn("\u672a\u627e\u5230\u804c\u4e1a\u914d\u7f6e: {}", (Object)classId);
            return;
        }
        if (kit.variants == null || kit.variants.size() != 1) {
            Espetro.LOGGER.warn("\u804c\u4e1a {} \u6709\u591a\u4e2a\u88c5\u5907\u53d8\u4f53\uff0c\u5fc5\u987b\u6307\u5b9a variantId", (Object)classId);
            return;
        }
        ClassEquipment.equipFromVariant(sp, kit, kit.variants.values().iterator().next());
    }

    public static void equipPlayer(Player player, String factionId, String classId) {
        FactionDataLoader.ClassKitData kit;
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer sp = (ServerPlayer)player;
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            loader.ensureLoaded(server.m_177941_());
        }
        if ((kit = loader.getClassKit(classId)) == null || factionId == null || !factionId.equals(kit.factionId) || kit.variants == null || kit.variants.size() != 1) {
            Espetro.LOGGER.warn("\u672a\u627e\u5230\u552f\u4e00\u804c\u4e1a\u88c5\u5907\u53d8\u4f53: {} / {}", (Object)factionId, (Object)classId);
            return;
        }
        ClassEquipment.equipFromVariant(sp, kit, kit.variants.values().iterator().next());
    }

    public static void equipPlayer(Player player, String factionId, String classId, String variantId) {
        FactionDataLoader.ClassKitData kit;
        FactionDataLoader.ClassVariantData variant;
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer sp = (ServerPlayer)player;
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            loader.ensureLoaded(server.m_177941_());
        }
        FactionDataLoader.ClassVariantData classVariantData = variant = (kit = loader.getClassKit(classId)) != null ? kit.getVariant(variantId) : null;
        if (kit == null || variant == null || factionId == null || !factionId.equals(kit.factionId)) {
            Espetro.LOGGER.warn("\u672a\u627e\u5230\u804c\u4e1a\u88c5\u5907\u53d8\u4f53: {} / {} / {}", new Object[]{factionId, classId, variantId});
            return;
        }
        ClassEquipment.equipFromVariant(sp, kit, variant);
    }

    private static void equipFromVariant(ServerPlayer player, FactionDataLoader.ClassKitData kit, FactionDataLoader.ClassVariantData variant) {
        ClassEquipment.beginEquipmentMutation(player);
        try {
            ClassEquipment.equipFromVariantInternal(player, kit, variant);
        }
        finally {
            ClassEquipment.endEquipmentMutation(player);
        }
    }

    private static void equipFromVariantInternal(ServerPlayer player, FactionDataLoader.ClassKitData kit, FactionDataLoader.ClassVariantData variant) {
        player.m_150109_().m_6211_();
        boolean hasCommands = variant.commands != null && variant.commands.length > 0;
        boolean hasConfiguredEquipment = ClassEquipment.hasConfiguredEquipment(variant);
        ClassEquipment.applyBonus(player, kit);
        if (!hasCommands && !hasConfiguredEquipment) {
            Espetro.LOGGER.warn("\u804c\u4e1a {} \u53d8\u4f53 {} \u65e0 commands/equipment \u914d\u7f6e", (Object)kit.id, (Object)variant.id);
            return;
        }
        MinecraftServer server = player.m_20194_();
        if (server == null) {
            return;
        }
        String playerName = player.m_7755_().getString();
        ClassEquipment.equipConfiguredEquipment(player, kit, variant, server, playerName);
        if (hasCommands) {
            for (String args : variant.commands) {
                if (args == null || args.isBlank()) continue;
                String itemArgs = ClassEquipment.normalizeItemArgs(args);
                if (ClassEquipment.shouldAutoEquipWearables(variant) && ClassEquipment.handleWearableCommand(player, server, playerName, itemArgs)) continue;
                String fullCmd = "give " + playerName + " " + itemArgs;
                ClassEquipment.executeCommand(server, fullCmd, "give");
            }
        }
        if (ClassEquipment.shouldAutoEquipWearables(variant)) {
            ClassEquipment.equipWearableItems(player);
        }
        LAST_EQUIPPED_KEY.put(player.m_20148_(), kit.factionId + ":" + kit.id + ":" + variant.id);
    }

    private static void beginEquipmentMutation(Player player) {
        if (player instanceof ServerPlayer) {
            EQUIPMENT_MUTATION_PLAYERS.add(player.m_20148_());
        }
    }

    private static void endEquipmentMutation(Player player) {
        if (player instanceof ServerPlayer) {
            EQUIPMENT_MUTATION_PLAYERS.remove(player.m_20148_());
        }
    }

    private static void equipConfiguredEquipment(ServerPlayer player, FactionDataLoader.ClassKitData kit, FactionDataLoader.ClassVariantData variant, MinecraftServer server, String playerName) {
        Map<String, String> equipment = ClassEquipment.collectConfiguredEquipment(variant);
        if (equipment.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : equipment.entrySet()) {
            String slotName = ClassEquipment.normalizeEquipmentSlotName(entry.getKey());
            String itemArgs = ClassEquipment.normalizeItemArgs(entry.getValue());
            if (slotName == null) {
                Espetro.LOGGER.warn("\u804c\u4e1a {} \u7684 equipment \u69fd\u4f4d\u65e0\u6548: {}", (Object)kit.id, (Object)entry.getKey());
                continue;
            }
            if (itemArgs.isBlank()) continue;
            String fullCmd = "item replace entity " + playerName + " " + slotName + " with " + itemArgs;
            ClassEquipment.executeCommand(server, fullCmd, "item replace");
        }
        player.m_150109_().m_6596_();
        player.f_36095_.m_38946_();
        player.f_36096_.m_38946_();
    }

    private static boolean handleWearableCommand(ServerPlayer player, MinecraftServer server, String playerName, String itemArgs) {
        EquipmentSlot slot = ClassEquipment.getWearableSlotFromItemArgs(itemArgs);
        if (slot == null) {
            return false;
        }
        if (!player.m_6844_(slot).m_41619_()) {
            Espetro.LOGGER.debug("\u8df3\u8fc7\u91cd\u590d\u53ef\u7a7f\u6234\u88c5\u5907: {} -> {}", (Object)itemArgs, (Object)slot.m_20751_());
            return true;
        }
        String slotName = ClassEquipment.toCommandSlotName(slot);
        if (slotName == null) {
            return false;
        }
        String fullCmd = "item replace entity " + playerName + " " + slotName + " with " + itemArgs;
        ClassEquipment.executeCommand(server, fullCmd, "item replace");
        player.m_150109_().m_6596_();
        player.f_36095_.m_38946_();
        player.f_36096_.m_38946_();
        return true;
    }

    private static void equipWearableItems(ServerPlayer player) {
        boolean changed = false;
        for (int i = 0; i < player.m_150109_().f_35974_.size(); ++i) {
            EquipmentSlot slot;
            ItemStack stack = player.m_150109_().f_35974_.get(i);
            if (stack.m_41619_() || !(slot = LivingEntity.m_147233_(stack)).m_254934_() || !player.m_6844_(slot).m_41619_()) continue;
            ItemStack wearable = stack.m_41620_(1);
            player.m_8061_(slot, wearable);
            if (stack.m_41619_()) {
                player.m_150109_().f_35974_.set(i, ItemStack.f_41583_);
            }
            changed = true;
        }
        if (changed) {
            player.m_150109_().m_6596_();
            player.f_36095_.m_38946_();
            player.f_36096_.m_38946_();
        }
    }

    private static boolean hasConfiguredEquipment(FactionDataLoader.ClassVariantData variant) {
        return variant.equipment != null && !variant.equipment.isEmpty() || variant.wearableEquipment != null && !variant.wearableEquipment.isEmpty();
    }

    private static boolean shouldAutoEquipWearables(FactionDataLoader.ClassVariantData variant) {
        return variant.autoEquipWearables == null || variant.autoEquipWearables != false;
    }

    private static Map<String, String> collectConfiguredEquipment(FactionDataLoader.ClassVariantData variant) {
        LinkedHashMap<String, String> equipment = new LinkedHashMap<String, String>();
        if (variant.equipment != null) {
            equipment.putAll(variant.equipment);
        }
        if (variant.wearableEquipment != null) {
            equipment.putAll(variant.wearableEquipment);
        }
        return equipment;
    }

    static String normalizeEquipmentSlotName(String slotName) {
        String key;
        if (slotName == null) {
            return null;
        }
        return switch (key = slotName.trim().toLowerCase(Locale.ROOT).replace('-', '_')) {
            case "head", "helmet", "armor_head", "armor.head" -> "armor.head";
            case "chest", "chestplate", "body", "armor_chest", "armor.chest" -> "armor.chest";
            case "legs", "leggings", "armor_legs", "armor.legs" -> "armor.legs";
            case "feet", "boots", "armor_feet", "armor.feet" -> "armor.feet";
            case "mainhand", "main_hand", "weapon", "weapon_mainhand", "weapon.mainhand" -> "weapon.mainhand";
            case "offhand", "off_hand", "shield", "weapon_offhand", "weapon.offhand" -> "weapon.offhand";
            default -> null;
        };
    }

    static String toCommandSlotName(EquipmentSlot slot) {
        return switch (slot) {
            default -> throw new IncompatibleClassChangeError();
            case EquipmentSlot.HEAD -> "armor.head";
            case EquipmentSlot.CHEST -> "armor.chest";
            case EquipmentSlot.LEGS -> "armor.legs";
            case EquipmentSlot.FEET -> "armor.feet";
            case EquipmentSlot.MAINHAND -> "weapon.mainhand";
            case EquipmentSlot.OFFHAND -> "weapon.offhand";
        };
    }

    static EquipmentSlot resolveEquipmentSlot(String slotName) {
        String normalized = ClassEquipment.normalizeEquipmentSlotName(slotName);
        if (normalized == null) {
            return null;
        }
        return switch (normalized) {
            case "armor.head" -> EquipmentSlot.HEAD;
            case "armor.chest" -> EquipmentSlot.CHEST;
            case "armor.legs" -> EquipmentSlot.LEGS;
            case "armor.feet" -> EquipmentSlot.FEET;
            case "weapon.mainhand" -> EquipmentSlot.MAINHAND;
            case "weapon.offhand" -> EquipmentSlot.OFFHAND;
            default -> null;
        };
    }

    static EquipmentSlot getWearableSlotFromItemArgs(String itemArgs) {
        if (itemArgs == null || itemArgs.isBlank()) {
            return null;
        }
        String itemId = ClassEquipment.extractItemId(itemArgs);
        if (itemId.isBlank()) {
            return null;
        }
        ResourceLocation itemLocation = ResourceLocation.m_135820_(itemId);
        if (itemLocation == null) {
            Espetro.LOGGER.warn("\u65e0\u6cd5\u89e3\u6790\u88c5\u5907\u7269\u54c1ID: {}", (Object)itemId);
            return null;
        }
        Item item = BuiltInRegistries.f_257033_.m_7745_(itemLocation);
        if (item == Items.f_41852_) {
            return null;
        }
        EquipmentSlot slot = LivingEntity.m_147233_(new ItemStack(item));
        return slot.m_254934_() ? slot : null;
    }

    static String extractItemId(String itemArgs) {
        String trimmed = itemArgs.trim();
        int end = trimmed.length();
        for (int i = 0; i < trimmed.length(); ++i) {
            char c = trimmed.charAt(i);
            if (!Character.isWhitespace(c) && c != '{' && c != '[') continue;
            end = i;
            break;
        }
        return trimmed.substring(0, end);
    }

    static String normalizeItemArgs(String args) {
        if (args == null) {
            return "";
        }
        String trimmed = args.trim();
        if (trimmed.regionMatches(true, 0, "with ", 0, 5)) {
            return trimmed.substring(5).trim();
        }
        return trimmed;
    }

    private static void executeCommand(MinecraftServer server, String fullCmd, String commandName) {
        try {
            server.m_129892_().m_230957_(server.m_129893_().m_81324_(), fullCmd);
            Espetro.LOGGER.debug("\u6267\u884c: {}", (Object)fullCmd);
        }
        catch (Exception e) {
            Espetro.LOGGER.error("[!] {}\u6307\u4ee4\u5931\u8d25: {}", (Object)commandName, (Object)fullCmd);
            Espetro.LOGGER.error("[!] \u9519\u8bef: {}", (Object)e.getMessage());
        }
    }

    public static void applyClassBonuses(Player player, FactionDataLoader.ClassKitData kit) {
        ClassEquipment.applyBonus(player, kit);
    }

    public static void clearClassBonuses(Player player) {
        if (player == null) {
            return;
        }
        LAST_EQUIPPED_KEY.remove(player.m_20148_());
        float previousHealth = player.m_21223_();
        ClassEquipment.removeModifier(player.m_21051_(Attributes.f_22276_), CLASS_HEALTH_BONUS_ID);
        ClassEquipment.removeModifier(player.m_21051_(Attributes.f_22279_), CLASS_SPEED_BONUS_ID);
        if (player.m_21223_() > player.m_21233_()) {
            player.m_21153_(Math.min(previousHealth, player.m_21233_()));
        }
    }

    private static void applyBonus(Player player, FactionDataLoader.ClassKitData kit) {
        if (player == null || kit == null) {
            return;
        }
        float previousHealth = player.m_21223_();
        AttributeInstance maxHealth = player.m_21051_(Attributes.f_22276_);
        AttributeInstance movementSpeed = player.m_21051_(Attributes.f_22279_);
        ClassEquipment.removeModifier(maxHealth, CLASS_HEALTH_BONUS_ID);
        ClassEquipment.removeModifier(movementSpeed, CLASS_SPEED_BONUS_ID);
        double healthBonus = ClassAttributeBonusPolicy.healthAmount(kit.healthBonus);
        if (maxHealth != null && healthBonus != 0.0) {
            maxHealth.m_22118_(new AttributeModifier(CLASS_HEALTH_BONUS_ID, "Espetro class health bonus", healthBonus, AttributeModifier.Operation.ADDITION));
        }
        double speedBonus = ClassAttributeBonusPolicy.speedMultiplier(kit.speedBonus);
        if (movementSpeed != null && speedBonus != 0.0) {
            movementSpeed.m_22118_(new AttributeModifier(CLASS_SPEED_BONUS_ID, "Espetro class speed bonus", speedBonus, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        player.m_21153_(ClassAttributeBonusPolicy.clampCurrentHealth(previousHealth, player.m_21233_()));
        Espetro.LOGGER.debug("\u5e94\u7528\u804c\u4e1a\u5c5e\u6027: {} -> \u751f\u547d +{}, \u901f\u5ea6 {}%", new Object[]{kit.name, healthBonus, speedBonus * 100.0});
    }

    private static void removeModifier(AttributeInstance attribute, UUID id) {
        if (attribute != null && attribute.m_22111_(id) != null) {
            attribute.m_22120_(id);
        }
    }
}

