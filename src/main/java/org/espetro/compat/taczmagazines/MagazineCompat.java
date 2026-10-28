package org.espetro.compat.taczmagazines;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Optional TaCZ Magazines bridge.  No optional-mod type is allowed in this API,
 * so the JVM can verify Espetro when taczmagazines is absent.
 */
public interface MagazineCompat {
    record Identity(String family, ResourceLocation ammoId, int capacity) {
        public Identity {
            family = family == null ? "" : family;
            if (ammoId == null) {
                ammoId = ResourceLocation.fromNamespaceAndPath("minecraft", "air");
            }
            capacity = Math.max(0, capacity);
        }
    }

    boolean available();

    Optional<Identity> identity(ItemStack stack);

    int ammoCount(ItemStack stack);

    /**
     * Refills an existing magazine in place.  Used by the resupply menu so a
     * partially spent magazine is topped up instead of being replaced (which
     * used to delete the player's old magazine together with its rounds).
     *
     * @return true when the stack really is a magazine and the count was written
     */
    boolean setAmmoCount(ItemStack stack, int count);

    /**
     * Compares a magazine found in an inventory against the one the catalog asks for.
     * TaCZ Magazines wipes {@code AmmoId} as soon as a magazine is emptied, so an
     * empty magazine no longer carries an ammo id; it still belongs to the same
     * family and capacity and must keep counting as fillable, otherwise a player who
     * fired a magazine dry can never resupply it again.
     */
    boolean sameMagazineType(Identity candidate, Identity required);

    /** Writes the ammo id back onto an emptied magazine (part of refilling it). */
    boolean bindAmmo(ItemStack stack, ResourceLocation ammoId);

    ItemStack createFull(ItemStack template);

    /**
     * 取出枪械自身携带的弹匣（TaCZ Magazines 把弹匣存在枪的 NBT
     * {@code TaCZMag_StoredMagazine} 里，不占背包格）。
     *
     * <p>打空后弹匣仍留在枪里，因此「补给弹匣」必须能看到它，否则玩家把弹打光后
     * 背包里没有任何可填对象，就再也补不上弹。</p>
     *
     * @return 弹匣副本；枪里没有弹匣或不是本模组的枪时返回 {@link ItemStack#EMPTY}
     */
    ItemStack storedMagazine(ItemStack gun);

    /** 把填好的弹匣写回枪械 NBT；返回是否写入成功。 */
    boolean setStoredMagazine(ItemStack gun, ItemStack magazine);
}
