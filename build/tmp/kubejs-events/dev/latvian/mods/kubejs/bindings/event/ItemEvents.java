/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 */
package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.item.FoodEatenEventJS;
import dev.latvian.mods.kubejs.item.ItemClickedEventJS;
import dev.latvian.mods.kubejs.item.ItemCraftedEventJS;
import dev.latvian.mods.kubejs.item.ItemDroppedEventJS;
import dev.latvian.mods.kubejs.item.ItemEntityInteractedEventJS;
import dev.latvian.mods.kubejs.item.ItemModelPropertiesEventJS;
import dev.latvian.mods.kubejs.item.ItemModificationEventJS;
import dev.latvian.mods.kubejs.item.ItemPickedUpEventJS;
import dev.latvian.mods.kubejs.item.ItemSmeltedEventJS;
import dev.latvian.mods.kubejs.item.ItemTooltipEventJS;
import dev.latvian.mods.kubejs.item.custom.ItemArmorTierRegistryEventJS;
import dev.latvian.mods.kubejs.item.custom.ItemToolTierRegistryEventJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public interface ItemEvents {
    public static final EventGroup GROUP = EventGroup.of("ItemEvents");
    public static final Extra SUPPORTS_ITEM = new Extra().transformer(ItemEvents::transformItem).toString(o -> ((Item)o).kjs$getId()).identity().describeType(context -> context.javaType(Item.class));
    public static final EventHandler MODIFICATION = GROUP.startup("modification", () -> ItemModificationEventJS.class);
    public static final EventHandler TOOL_TIER_REGISTRY = GROUP.startup("toolTierRegistry", () -> ItemToolTierRegistryEventJS.class);
    public static final EventHandler ARMOR_TIER_REGISTRY = GROUP.startup("armorTierRegistry", () -> ItemArmorTierRegistryEventJS.class);
    public static final EventHandler RIGHT_CLICKED = GROUP.common("rightClicked", () -> ItemClickedEventJS.class).extra(SUPPORTS_ITEM).hasResult();
    public static final EventHandler CAN_PICK_UP = GROUP.common("canPickUp", () -> ItemPickedUpEventJS.class).extra(SUPPORTS_ITEM).hasResult();
    public static final EventHandler PICKED_UP = GROUP.common("pickedUp", () -> ItemPickedUpEventJS.class).extra(SUPPORTS_ITEM);
    public static final EventHandler DROPPED = GROUP.common("dropped", () -> ItemDroppedEventJS.class).extra(SUPPORTS_ITEM).hasResult();
    public static final EventHandler ENTITY_INTERACTED = GROUP.common("entityInteracted", () -> ItemEntityInteractedEventJS.class).extra(SUPPORTS_ITEM).hasResult();
    public static final EventHandler CRAFTED = GROUP.common("crafted", () -> ItemCraftedEventJS.class).extra(SUPPORTS_ITEM);
    public static final EventHandler SMELTED = GROUP.common("smelted", () -> ItemSmeltedEventJS.class).extra(SUPPORTS_ITEM);
    public static final EventHandler FOOD_EATEN = GROUP.common("foodEaten", () -> FoodEatenEventJS.class).extra(SUPPORTS_ITEM).hasResult();
    public static final EventHandler TOOLTIP = GROUP.client("tooltip", () -> ItemTooltipEventJS.class);
    public static final EventHandler MODEL_PROPERTIES = GROUP.startup("modelProperties", () -> ItemModelPropertiesEventJS.class);
    public static final EventHandler FIRST_RIGHT_CLICKED = GROUP.common("firstRightClicked", () -> ItemClickedEventJS.class).extra(SUPPORTS_ITEM);
    public static final EventHandler FIRST_LEFT_CLICKED = GROUP.common("firstLeftClicked", () -> ItemClickedEventJS.class).extra(SUPPORTS_ITEM);

    private static Object transformItem(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof ItemLike) {
            ItemLike item = (ItemLike)o;
            return item.m_5456_();
        }
        ResourceLocation id = ResourceLocation.m_135820_((String)o.toString());
        Item item = id == null ? null : ItemWrapper.getItem(id);
        return item == Items.f_41852_ ? null : item;
    }
}

