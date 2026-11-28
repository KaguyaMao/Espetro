package org.espetro.compat;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public final class ConstructionTools {
    private ConstructionTools() {}

    public static boolean isTool(ItemStack stack) {
        if (stack.is(Items.IRON_SHOVEL)) return true;
        var id=ForgeRegistries.ITEMS.getKey(stack.getItem());
        return id!=null && "superbwarfare".equals(id.getNamespace()) && "military_shovel".equals(id.getPath());
    }
}
