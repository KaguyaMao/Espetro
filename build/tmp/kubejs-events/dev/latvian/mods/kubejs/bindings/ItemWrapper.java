/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  dev.latvian.mods.rhino.mod.util.JsonUtils
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.bindings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.FireworksJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

@Info(value="Various item related helper methods")
public interface ItemWrapper {
    public static final UUID KJS_BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public static final UUID KJS_BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    public static final UUID[] KJS_ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    @Info(value="Returns an ItemStack of the input")
    public static ItemStack of(ItemStack in) {
        return in;
    }

    @Info(value="Returns an ItemStack of the input, with the specified count")
    public static ItemStack of(ItemStack in, int count) {
        return in.kjs$withCount(count);
    }

    @Info(value="Returns an ItemStack of the input, with the specified NBT data")
    public static ItemStack of(ItemStack in, CompoundTag tag) {
        return in.kjs$withNBT(tag);
    }

    @Info(value="Returns an ItemStack of the input, with the specified count and NBT data")
    public static ItemStack of(ItemStack in, int count, CompoundTag nbt) {
        ItemStack is = in.kjs$withNBT(nbt);
        is.m_41764_(count);
        return is;
    }

    @Info(value="Returns an ItemStack of the input, with the specified NBT data")
    public static ItemStack withNBT(ItemStack in, CompoundTag nbt) {
        return in.kjs$withNBT(nbt);
    }

    @Info(value="Get a list of most items in the game. Items not in a creative tab are ignored")
    public static List<ItemStack> getList() {
        return ItemStackJS.getList();
    }

    @Info(value="Get a list of all the item ids in the game")
    public static List<String> getTypeList() {
        return ItemStackJS.getTypeList();
    }

    public static Map<ResourceLocation, Collection<ItemStack>> getTypeToStackMap() {
        return ItemStackJS.getTypeToStacks();
    }

    public static Collection<ItemStack> getVariants(ItemStack item) {
        return ItemWrapper.getTypeToStackMap().get(item.kjs$getIdLocation());
    }

    @Info(value="Get the item that represents air/an empty slot")
    public static ItemStack getEmpty() {
        return ItemStack.f_41583_;
    }

    @Info(value="Returns a Firework with the input properties")
    public static FireworksJS fireworks(Map<String, Object> properties) {
        return FireworksJS.of(properties);
    }

    @Info(value="Gets an Item from an item id")
    public static Item getItem(ResourceLocation id) {
        return RegistryInfo.ITEM.getValue(id);
    }

    @Info(value="Gets an items id from the Item")
    @Nullable
    public static ResourceLocation getId(Item item) {
        return RegistryInfo.ITEM.getId(item);
    }

    @Info(value="Checks if the provided item id exists in the registry")
    public static boolean exists(ResourceLocation id) {
        return RegistryInfo.ITEM.hasValue(id);
    }

    @Info(value="Checks if the passed in object is an ItemStack.\nNote that this does not mean it will not function as an ItemStack if passed to something that requests one.\n")
    public static boolean isItem(@Nullable Object o) {
        return o instanceof ItemStackJS;
    }

    public static ItemStack playerHead(String name) {
        ItemStack stack = new ItemStack((ItemLike)Items.f_42680_);
        stack.m_41784_().m_128359_("SkullOwner", name);
        return stack;
    }

    public static ItemStack playerHead(UUID uuid, String textureBase64) {
        ItemStack stack = new ItemStack((ItemLike)Items.f_42680_);
        CompoundTag so = stack.m_41698_("SkullOwner");
        so.m_128362_("Id", uuid);
        if (textureBase64 != null && !textureBase64.isBlank()) {
            CompoundTag properties = new CompoundTag();
            ListTag list = new ListTag();
            CompoundTag texture = new CompoundTag();
            texture.m_128359_("Value", textureBase64);
            list.add((Object)texture);
            properties.m_128365_("textures", (Tag)list);
            so.m_128365_("Properties", (Tag)properties);
        }
        return stack;
    }

    public static ItemStack playerHeadFromUrl(String url) {
        JsonObject root = new JsonObject();
        JsonObject textures = new JsonObject();
        JsonObject skin = new JsonObject();
        skin.addProperty("url", url);
        textures.add("SKIN", (JsonElement)skin);
        root.add("textures", (JsonElement)textures);
        byte[] bytes = JsonUtils.toString((JsonElement)root).getBytes(StandardCharsets.UTF_8);
        return ItemWrapper.playerHead(UUID.nameUUIDFromBytes(bytes), Base64.getEncoder().encodeToString(bytes));
    }

    public static ItemStack playerHeadFromSkinHash(String hash) {
        return ItemWrapper.playerHeadFromUrl("https://textures.minecraft.net/texture/" + hash);
    }
}

