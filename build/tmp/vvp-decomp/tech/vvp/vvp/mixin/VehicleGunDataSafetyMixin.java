/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.DefaultGunData
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.data.gun.GunData$Companion
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  kotlin.jvm.functions.Function0
 *  net.minecraft.world.item.ItemStack
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package tech.vvp.vvp.mixin;

import com.atsuishio.superbwarfare.data.gun.DefaultGunData;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import kotlin.jvm.functions.Function0;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={VehicleEntity.class})
public class VehicleGunDataSafetyMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"vvp");

    @Redirect(method={"getGunDataMap"}, at=@At(value="INVOKE", target="Lcom/atsuishio/superbwarfare/data/gun/GunData$Companion;from(Lnet/minecraft/world/item/ItemStack;Lkotlin/jvm/functions/Function0;)Lcom/atsuishio/superbwarfare/data/gun/GunData;"), remap=false)
    private GunData vvp$safeGunDataFrom(GunData.Companion companion, ItemStack stack, Function0<DefaultGunData> supplier) {
        try {
            return companion.from(stack, supplier);
        }
        catch (Throwable error) {
            LOGGER.warn("Skipping broken vehicle weapon (missing ammo or invalid gun data): {}", (Object)error.toString());
            return null;
        }
    }
}

