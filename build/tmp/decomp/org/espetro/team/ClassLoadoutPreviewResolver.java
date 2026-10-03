/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 */
package org.espetro.team;

import com.mojang.brigadier.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.espetro.Espetro;
import org.espetro.team.ClassEquipment;
import org.espetro.team.FactionDataLoader;

public final class ClassLoadoutPreviewResolver {
    private static final Map<String, Preview> PREVIEW_CACHE = new LinkedHashMap<String, Preview>();
    private static final Set<String> WARNED_PARSE_KEYS = new HashSet<String>();
    private static final int MAX_CACHE_ENTRIES = 512;

    public static void clearCache() {
        PREVIEW_CACHE.clear();
        WARNED_PARSE_KEYS.clear();
    }

    private ClassLoadoutPreviewResolver() {
    }

    public static Preview resolve(MinecraftServer server, FactionDataLoader.ClassKitData kit, FactionDataLoader.ClassVariantData variant) {
        if (server == null) {
            return Preview.empty();
        }
        return ClassLoadoutPreviewResolver.resolve(server.m_206579_(), kit, variant);
    }

    public static Preview resolve(HolderLookup.Provider lookup, FactionDataLoader.ClassKitData kit, FactionDataLoader.ClassVariantData variant) {
        if (kit == null || variant == null) {
            return Preview.empty();
        }
        if (lookup == null) {
            return Preview.empty();
        }
        String cacheKey = ClassLoadoutPreviewResolver.cacheKey(kit, variant);
        Preview cached = PREVIEW_CACHE.get(cacheKey);
        if (cached != null) {
            return cached.copy();
        }
        Preview built = ClassLoadoutPreviewResolver.resolveUncached(lookup, kit, variant);
        if (PREVIEW_CACHE.size() >= 512) {
            PREVIEW_CACHE.clear();
        }
        PREVIEW_CACHE.put(cacheKey, built);
        return built.copy();
    }

    private static String cacheKey(FactionDataLoader.ClassKitData kit, FactionDataLoader.ClassVariantData variant) {
        String faction = kit.factionId != null ? kit.factionId : "";
        String classId = kit.id != null ? kit.id : "";
        String variantId = variant.id != null ? variant.id : "";
        return faction + "\u0000" + classId + "\u0000" + variantId;
    }

    private static Preview resolveUncached(HolderLookup.Provider lookup, FactionDataLoader.ClassKitData kit, FactionDataLoader.ClassVariantData variant) {
        ItemStack mainHand;
        ParsedItem parsed;
        ItemStack head = ItemStack.f_41583_;
        ItemStack chest = ItemStack.f_41583_;
        ItemStack legs = ItemStack.f_41583_;
        ItemStack feet = ItemStack.f_41583_;
        ItemStack offHand = ItemStack.f_41583_;
        ItemStack mainHandExplicit = ItemStack.f_41583_;
        SimpleTempInventory tempInventory = new SimpleTempInventory();
        boolean autoEquipWearables = variant.autoEquipWearables == null || variant.autoEquipWearables != false;
        Map<String, String> equipmentMap = ClassLoadoutPreviewResolver.collectConfiguredEquipment(variant);
        for (Map.Entry<String, String> entry : equipmentMap.entrySet()) {
            String slotName = entry.getKey();
            String itemArgs = ClassEquipment.normalizeItemArgs(entry.getValue());
            if (itemArgs.isBlank()) continue;
            EquipmentSlot slot = ClassEquipment.resolveEquipmentSlot(slotName);
            if (slot == null) {
                Espetro.LOGGER.warn("[\u9884\u89c8\u89e3\u6790] \u804c\u4e1a {} \u53d8\u4f53 {} \u7684 equipment \u69fd\u4f4d\u65e0\u6548: {}", new Object[]{kit.id, variant.id, slotName});
                continue;
            }
            parsed = ClassLoadoutPreviewResolver.parseItem(lookup, itemArgs, kit, variant);
            if (parsed == null || parsed.stack.m_41619_()) continue;
            switch (slot) {
                case HEAD: {
                    head = parsed.stack.m_41777_();
                    break;
                }
                case CHEST: {
                    chest = parsed.stack.m_41777_();
                    break;
                }
                case LEGS: {
                    legs = parsed.stack.m_41777_();
                    break;
                }
                case FEET: {
                    feet = parsed.stack.m_41777_();
                    break;
                }
                case OFFHAND: {
                    offHand = parsed.stack.m_41777_();
                    break;
                }
                case MAINHAND: {
                    mainHandExplicit = parsed.stack.m_41777_();
                }
            }
        }
        if (variant.commands != null) {
            for (String raw : variant.commands) {
                EquipmentSlot armorSlot;
                String args;
                if (raw == null || (args = ClassEquipment.normalizeItemArgs(raw)).isBlank() || (parsed = ClassLoadoutPreviewResolver.parseItem(lookup, args, kit, variant)) == null || parsed.stack.m_41619_()) continue;
                ItemStack stack = parsed.stack;
                if (autoEquipWearables && (armorSlot = ClassLoadoutPreviewResolver.armorSlotFor(stack)) != null) {
                    if (!ClassLoadoutPreviewResolver.slotIsEmpty(armorSlot, head, chest, legs, feet)) continue;
                    ItemStack equipped = stack.m_41777_();
                    switch (armorSlot) {
                        case HEAD: {
                            head = equipped;
                            break;
                        }
                        case CHEST: {
                            chest = equipped;
                            break;
                        }
                        case LEGS: {
                            legs = equipped;
                            break;
                        }
                        case FEET: {
                            feet = equipped;
                            break;
                        }
                    }
                    continue;
                }
                tempInventory.add(stack);
            }
        }
        if (autoEquipWearables) {
            for (int i = 0; i < tempInventory.slots.size(); ++i) {
                EquipmentSlot armorSlot;
                ItemStack stack = tempInventory.slots.get(i);
                if (stack.m_41619_() || (armorSlot = ClassLoadoutPreviewResolver.armorSlotFor(stack)) == null || !ClassLoadoutPreviewResolver.slotIsEmpty(armorSlot, head, chest, legs, feet)) continue;
                ItemStack single = stack.m_41620_(1);
                switch (armorSlot) {
                    case HEAD: {
                        head = single;
                        break;
                    }
                    case CHEST: {
                        chest = single;
                        break;
                    }
                    case LEGS: {
                        legs = single;
                        break;
                    }
                    case FEET: {
                        feet = single;
                        break;
                    }
                }
                if (stack.m_41619_()) {
                    tempInventory.slots.set(i, ItemStack.f_41583_);
                    continue;
                }
                tempInventory.slots.set(i, stack);
            }
        }
        if ((mainHand = mainHandExplicit).m_41619_()) {
            mainHand = tempInventory.hotbarSlot0().m_41777_();
        }
        return new Preview(head, chest, legs, feet, mainHand, offHand);
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

    private static ParsedItem parseItem(HolderLookup.Provider lookup, String itemArgs, FactionDataLoader.ClassKitData kit, FactionDataLoader.ClassVariantData variant) {
        try {
            HolderLookup.RegistryLookup<Item> itemLookup = lookup.m_255025_(Registries.f_256913_);
            StringReader reader = new StringReader(itemArgs);
            ItemParser.ItemResult result = ItemParser.m_235305_(itemLookup, reader);
            Holder<Item> holder = result.f_235328_();
            if (holder == null) {
                return null;
            }
            ItemStack stack = new ItemStack(holder);
            CompoundTag nbt = result.f_235329_();
            if (nbt != null) {
                stack.m_41751_(nbt);
            }
            reader.skipWhitespace();
            int count = 1;
            if (reader.canRead()) {
                count = reader.readInt();
            }
            if (count < 1) {
                count = 1;
            }
            stack.m_41764_(count);
            return new ParsedItem(stack);
        }
        catch (Exception e) {
            String warnKey = (kit != null ? kit.id : "?") + "/" + (variant != null ? variant.id : "?") + ":" + itemArgs;
            if (WARNED_PARSE_KEYS.add(warnKey)) {
                Espetro.LOGGER.warn("[\u9884\u89c8\u89e3\u6790] \u804c\u4e1a {} \u53d8\u4f53 {} \u7269\u54c1\u53c2\u6570\u89e3\u6790\u5931\u8d25: {} ({})", new Object[]{kit.id, variant.id, itemArgs, e.getMessage()});
            }
            return null;
        }
    }

    private static EquipmentSlot armorSlotFor(ItemStack stack) {
        EquipmentSlot slot = LivingEntity.m_147233_(stack);
        return slot.m_254934_() ? slot : null;
    }

    private static boolean slotIsEmpty(EquipmentSlot slot, ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
        return switch (slot) {
            case EquipmentSlot.HEAD -> head.m_41619_();
            case EquipmentSlot.CHEST -> chest.m_41619_();
            case EquipmentSlot.LEGS -> legs.m_41619_();
            case EquipmentSlot.FEET -> feet.m_41619_();
            default -> false;
        };
    }

    public static final class Preview {
        public final ItemStack head;
        public final ItemStack chest;
        public final ItemStack legs;
        public final ItemStack feet;
        public final ItemStack mainHand;
        public final ItemStack offHand;

        public Preview(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet, ItemStack mainHand, ItemStack offHand) {
            this.head = head == null ? ItemStack.f_41583_ : head;
            this.chest = chest == null ? ItemStack.f_41583_ : chest;
            this.legs = legs == null ? ItemStack.f_41583_ : legs;
            this.feet = feet == null ? ItemStack.f_41583_ : feet;
            this.mainHand = mainHand == null ? ItemStack.f_41583_ : mainHand;
            this.offHand = offHand == null ? ItemStack.f_41583_ : offHand;
        }

        public static Preview empty() {
            return new Preview(ItemStack.f_41583_, ItemStack.f_41583_, ItemStack.f_41583_, ItemStack.f_41583_, ItemStack.f_41583_, ItemStack.f_41583_);
        }

        public Preview copy() {
            return new Preview(this.head.m_41619_() ? ItemStack.f_41583_ : this.head.m_41777_(), this.chest.m_41619_() ? ItemStack.f_41583_ : this.chest.m_41777_(), this.legs.m_41619_() ? ItemStack.f_41583_ : this.legs.m_41777_(), this.feet.m_41619_() ? ItemStack.f_41583_ : this.feet.m_41777_(), this.mainHand.m_41619_() ? ItemStack.f_41583_ : this.mainHand.m_41777_(), this.offHand.m_41619_() ? ItemStack.f_41583_ : this.offHand.m_41777_());
        }

        public ItemStack bySlot(EquipmentSlot slot) {
            return switch (slot) {
                default -> throw new IncompatibleClassChangeError();
                case EquipmentSlot.HEAD -> this.head;
                case EquipmentSlot.CHEST -> this.chest;
                case EquipmentSlot.LEGS -> this.legs;
                case EquipmentSlot.FEET -> this.feet;
                case EquipmentSlot.MAINHAND -> this.mainHand;
                case EquipmentSlot.OFFHAND -> this.offHand;
            };
        }
    }

    private static final class SimpleTempInventory {
        private static final int SLOT_COUNT = 36;
        private final List<ItemStack> slots = new ArrayList<ItemStack>(36);

        SimpleTempInventory() {
            for (int i = 0; i < 36; ++i) {
                this.slots.add(ItemStack.f_41583_);
            }
        }

        void add(ItemStack stack) {
            int max;
            ItemStack existing;
            int i;
            if (stack.m_41619_()) {
                return;
            }
            for (i = 0; i < 36 && !stack.m_41619_(); ++i) {
                int room;
                existing = this.slots.get(i);
                if (existing.m_41619_() || !ItemStack.m_150942_(existing, stack) || (room = (max = Math.min(existing.m_41741_(), stack.m_41741_())) - existing.m_41613_()) <= 0) continue;
                int moved = Math.min(room, stack.m_41613_());
                existing.m_41769_(moved);
                stack.m_41774_(moved);
            }
            for (i = 0; i < 36 && !stack.m_41619_(); ++i) {
                existing = this.slots.get(i);
                if (!existing.m_41619_()) continue;
                max = stack.m_41741_();
                int moved = Math.min(max, stack.m_41613_());
                ItemStack placed = stack.m_255036_(moved);
                this.slots.set(i, placed);
                stack.m_41774_(moved);
            }
        }

        ItemStack hotbarSlot0() {
            return this.slots.get(0);
        }

        public String toString() {
            int nonEmpty = 0;
            for (ItemStack s : this.slots) {
                if (s.m_41619_()) continue;
                ++nonEmpty;
            }
            return String.format(Locale.ROOT, "SimpleTempInventory{nonEmpty=%d}", nonEmpty);
        }
    }

    private static final class ParsedItem {
        final ItemStack stack;

        ParsedItem(ItemStack stack) {
            this.stack = stack;
        }
    }
}

