/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.Multimap
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  dev.latvian.mods.rhino.mod.util.color.Color
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.ArmorMaterials
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.Tier
 *  net.minecraft.world.item.Tiers
 *  net.minecraft.world.item.UseAnim
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.item.FoodBuilder;
import dev.latvian.mods.kubejs.item.ItemTintFunction;
import dev.latvian.mods.kubejs.item.KubeJSItemProperties;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.mod.util.color.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class ItemBuilder
extends BuilderBase<Item> {
    public static final Map<String, Tier> TOOL_TIERS = new HashMap<String, Tier>();
    public static final Map<String, ArmorMaterial> ARMOR_TIERS = new HashMap<String, ArmorMaterial>();
    public transient int maxStackSize = 64;
    public transient int maxDamage = 0;
    public transient int burnTime = 0;
    private ResourceLocation containerItem = null;
    public transient Function<ItemStack, Collection<ItemStack>> subtypes = null;
    public transient Rarity rarity = Rarity.COMMON;
    public transient boolean fireResistant = false;
    public transient boolean glow = false;
    public final transient List<Component> tooltip = new ArrayList<Component>();
    @Nullable
    public transient ItemTintFunction tint;
    public transient FoodBuilder foodBuilder = null;
    public transient Function<ItemStack, Color> barColor;
    public transient ToIntFunction<ItemStack> barWidth;
    public transient NameCallback nameGetter;
    public transient Multimap<ResourceLocation, AttributeModifier> attributes;
    public transient UseAnim anim = null;
    public transient ToIntFunction<ItemStack> useDuration = null;
    public transient UseCallback use = null;
    public transient FinishUsingCallback finishUsing = null;
    public transient ReleaseUsingCallback releaseUsing = null;
    public transient Predicate<HurtEnemyContext> hurtEnemy = null;
    public String texture;
    public String parentModel = "";
    public JsonObject textureJson = new JsonObject();
    public JsonObject modelJson = null;

    public static ArmorMaterial toArmorMaterial(Object o) {
        if (o instanceof ArmorMaterial) {
            ArmorMaterial armorMaterial = (ArmorMaterial)o;
            return armorMaterial;
        }
        String asString = String.valueOf(o);
        ArmorMaterial armorMaterial = ARMOR_TIERS.get(asString);
        if (armorMaterial != null) {
            return armorMaterial;
        }
        String withKube = KubeJS.appendModId(asString);
        return ARMOR_TIERS.getOrDefault(withKube, (ArmorMaterial)ArmorMaterials.IRON);
    }

    public static Tier toToolTier(Object o) {
        if (o instanceof Tier) {
            Tier tier = (Tier)o;
            return tier;
        }
        String asString = String.valueOf(o);
        Tier toolTier = TOOL_TIERS.get(asString);
        if (toolTier != null) {
            return toolTier;
        }
        String withKube = KubeJS.appendModId(asString);
        return TOOL_TIERS.getOrDefault(withKube, (Tier)Tiers.IRON);
    }

    public ItemBuilder(ResourceLocation i) {
        super(i);
        this.attributes = ArrayListMultimap.create();
    }

    @Override
    public final RegistryInfo getRegistryType() {
        return RegistryInfo.ITEM;
    }

    @Override
    public Item transformObject(Item obj) {
        obj.kjs$setItemBuilder(this);
        return obj;
    }

    @Override
    public void generateDataJsons(DataJsonGenerator generator) {
    }

    @Override
    public void generateAssetJsons(AssetJsonGenerator generator) {
        if (this.modelJson != null) {
            generator.json(AssetJsonGenerator.asItemModelLocation(this.id), (JsonElement)this.modelJson);
            return;
        }
        generator.itemModel(this.id, m -> {
            if (!this.parentModel.isEmpty()) {
                m.parent(this.parentModel);
            } else {
                m.parent("minecraft:item/generated");
            }
            if (this.textureJson.size() == 0) {
                this.texture(this.newID("item/", "").toString());
            }
            m.textures(this.textureJson);
        });
    }

    @Info(value="Sets the item's max stack size. Default is 64.")
    public ItemBuilder maxStackSize(int v) {
        this.maxStackSize = v;
        return this;
    }

    @Info(value="Makes the item not stackable, equivalent to setting the item's max stack size to 1.")
    public ItemBuilder unstackable() {
        return this.maxStackSize(1);
    }

    @Info(value="Sets the item's max damage. Default is 0 (No durability).")
    public ItemBuilder maxDamage(int v) {
        this.maxDamage = v;
        return this;
    }

    @Info(value="Sets the item's burn time. Default is 0 (Not a fuel).")
    public ItemBuilder burnTime(int v) {
        this.burnTime = v;
        return this;
    }

    @Info(value="Sets the item's container item, e.g. a bucket for a milk bucket.")
    public ItemBuilder containerItem(ResourceLocation id) {
        this.containerItem = id;
        return this;
    }

    @Info(value="Adds subtypes to the item. The function should return a collection of item stacks, each with a different subtype.\n\nEach subtype will appear as a separate item in JEI and the creative inventory.\n")
    public ItemBuilder subtypes(Function<ItemStack, Collection<ItemStack>> fn) {
        this.subtypes = fn;
        return this;
    }

    @Info(value="Sets the item's rarity.")
    public ItemBuilder rarity(Rarity v) {
        this.rarity = v;
        return this;
    }

    @Info(value="Makes the item glow like enchanted, even if it's not enchanted.")
    public ItemBuilder glow(boolean v) {
        this.glow = v;
        return this;
    }

    @Info(value="Adds a tooltip to the item.")
    public ItemBuilder tooltip(Component text) {
        this.tooltip.add(text);
        return this;
    }

    @Deprecated
    public ItemBuilder group(@Nullable String g) {
        ConsoleJS.STARTUP.error("Item builder .group() is no longer supported, use StartupEvents.modifyCreativeTab!");
        return this;
    }

    @Info(value="Colorizes item's texture of the given index. Index is used when you have multiple layers, e.g. a crushed ore (of rock + ore).")
    public ItemBuilder color(int index, ItemTintFunction color) {
        if (!(this.tint instanceof ItemTintFunction.Mapped)) {
            this.tint = new ItemTintFunction.Mapped();
        }
        ((ItemTintFunction.Mapped)this.tint).map.put(index, (Object)color);
        return this;
    }

    @Info(value="Colorizes item's texture of the given index. Useful for coloring items, like GT ores ore dusts.")
    public ItemBuilder color(ItemTintFunction callback) {
        this.tint = callback;
        return this;
    }

    @Info(value="Sets the item's texture (layer0).")
    public ItemBuilder texture(String tex) {
        this.textureJson.addProperty("layer0", tex);
        return this;
    }

    @Info(value="Sets the item's texture by given key.")
    public ItemBuilder texture(String key, String tex) {
        this.textureJson.addProperty(key, tex);
        return this;
    }

    @Info(value="Directlys set the item's texture json.")
    public ItemBuilder textureJson(JsonObject json) {
        this.textureJson = json;
        return this;
    }

    @Info(value="Directly set the item's model json.")
    public ItemBuilder modelJson(JsonObject json) {
        this.modelJson = json;
        return this;
    }

    @Info(value="Sets the item's model (parent).")
    public ItemBuilder parentModel(String m) {
        this.parentModel = m;
        return this;
    }

    @Info(value="Determines the color of the item's durability bar. Defaulted to vanilla behavior.")
    public ItemBuilder barColor(Function<ItemStack, Color> barColor) {
        this.barColor = barColor;
        return this;
    }

    @Info(value="Determines the width of the item's durability bar. Defaulted to vanilla behavior.\n\nThe function should return a value between 0 and 13 (max width of the bar).\n")
    public ItemBuilder barWidth(ToIntFunction<ItemStack> barWidth) {
        this.barWidth = barWidth;
        return this;
    }

    @Info(value="Sets the item's name dynamically.\n")
    public ItemBuilder name(NameCallback name) {
        this.nameGetter = name;
        return this;
    }

    @Info(value="Set the food properties of the item.\n")
    public ItemBuilder food(Consumer<FoodBuilder> b) {
        this.foodBuilder = new FoodBuilder();
        b.accept(this.foodBuilder);
        return this;
    }

    @Info(value="Makes the item fire resistant like netherite tools (or not).")
    public ItemBuilder fireResistant(boolean isFireResistant) {
        this.fireResistant = isFireResistant;
        return this;
    }

    @Info(value="Makes the item fire resistant like netherite tools.")
    public ItemBuilder fireResistant() {
        return this.fireResistant(true);
    }

    public Item.Properties createItemProperties() {
        Item item;
        KubeJSItemProperties properties = new KubeJSItemProperties(this);
        if (this.maxDamage > 0) {
            properties.m_41503_(this.maxDamage);
        } else {
            properties.m_41487_(this.maxStackSize);
        }
        properties.m_41497_(this.rarity);
        Item item2 = item = this.containerItem == null ? Items.f_41852_ : ItemWrapper.getItem(this.containerItem);
        if (item != Items.f_41852_) {
            properties.m_41495_(item);
        }
        if (this.foodBuilder != null) {
            properties.m_41489_(this.foodBuilder.build());
        }
        if (this.fireResistant) {
            properties.m_41486_();
        }
        return properties;
    }

    @Info(value="Adds an attribute modifier to the item.\n\nAn attribute modifier is something like a damage boost or a speed boost.\nOn tools, they're applied when the item is held, on armor, they're\napplied when the item is worn.\n", params={@Param(name="attribute", value="The resource location of the attribute, e.g. 'generic.attack_damage'"), @Param(name="identifier", value="A unique identifier for the modifier. Modifiers are considered the same if they have the same identifier."), @Param(name="d", value="The amount of the modifier."), @Param(name="operation", value="The operation to apply the modifier with. Can be ADDITION, MULTIPLY_BASE, or MULTIPLY_TOTAL.")})
    public ItemBuilder modifyAttribute(ResourceLocation attribute, String identifier, double d, AttributeModifier.Operation operation) {
        this.attributes.put((Object)attribute, (Object)new AttributeModifier(new UUID(identifier.hashCode(), identifier.hashCode()), identifier, d, operation));
        return this;
    }

    @Info(value="Determines the animation of the item when used, e.g. eating food.")
    public ItemBuilder useAnimation(UseAnim animation) {
        this.anim = animation;
        return this;
    }

    @Info(value="The duration when the item is used.\n\nFor example, when eating food, this is the time it takes to eat the food.\nThis can change the eating speed, or be used for other things (like making a custom bow).\n")
    public ItemBuilder useDuration(ToIntFunction<ItemStack> useDuration) {
        this.useDuration = useDuration;
        return this;
    }

    @Info(value="Determines if player will start using the item.\n\nFor example, when eating food, returning true will make the player start eating the food.\n")
    public ItemBuilder use(UseCallback use) {
        this.use = use;
        return this;
    }

    @Info(value="When players finish using the item.\n\nThis is called only when `useDuration` ticks have passed.\n\nFor example, when eating food, this is called when the player has finished eating the food, so hunger is restored.\n")
    public ItemBuilder finishUsing(FinishUsingCallback finishUsing) {
        this.finishUsing = finishUsing;
        return this;
    }

    @Info(value="When players did not finish using the item but released the right mouse button halfway through.\n\nAn example is the bow, where the arrow is shot when the player releases the right mouse button.\n\nTo ensure the bow won't finish using, Minecraft sets the `useDuration` to a very high number (1h).\n")
    public ItemBuilder releaseUsing(ReleaseUsingCallback releaseUsing) {
        this.releaseUsing = releaseUsing;
        return this;
    }

    @Info(value="Gets called when the item is used to hurt an entity.\n\nFor example, when using a sword to hit a mob, this is called.\n")
    public ItemBuilder hurtEnemy(Predicate<HurtEnemyContext> context) {
        this.hurtEnemy = context;
        return this;
    }

    static {
        for (Tiers tiers : Tiers.values()) {
            TOOL_TIERS.put(tiers.toString().toLowerCase(), (Tier)tiers);
        }
        for (Tiers tiers : ArmorMaterials.values()) {
            ARMOR_TIERS.put(tiers.toString().toLowerCase(), (ArmorMaterial)tiers);
        }
    }

    @FunctionalInterface
    public static interface UseCallback {
        public boolean use(Level var1, Player var2, InteractionHand var3);
    }

    @FunctionalInterface
    public static interface FinishUsingCallback {
        public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3);
    }

    @FunctionalInterface
    public static interface ReleaseUsingCallback {
        public void releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4);
    }

    @FunctionalInterface
    public static interface NameCallback {
        public Component apply(ItemStack var1);
    }

    public record HurtEnemyContext(ItemStack getItem, LivingEntity getTarget, LivingEntity getAttacker) {
    }
}

