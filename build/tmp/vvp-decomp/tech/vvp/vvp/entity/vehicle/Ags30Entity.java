/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.data.gun.GunData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tech.vvp.vvp.entity.vehicle.VvpVehicleBase;
import tech.vvp.vvp.init.ModItems;
import tech.vvp.vvp.init.ModSounds;

public class Ags30Entity
extends VvpVehicleBase {
    public Ags30Entity(EntityType<Ags30Entity> type, Level world) {
        super(type, world);
    }

    @NotNull
    public List<ItemStack> getRetrieveItems() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack((ItemLike)ModItems.AGS_30_ITEM.get()));
        return list;
    }

    @NotNull
    public InteractionResult m_6096_(Player player, @NotNull InteractionHand hand) {
        if (player.m_6144_()) {
            this.retrieve(player);
            return InteractionResult.SUCCESS;
        }
        GunData gunData = this.getGunData(0);
        if (gunData == null) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = player.m_21120_(hand);
        if (stack.m_150930_((Item)ModItems.ITEM_40_MM.get())) {
            if (!this.m_9236_().f_46443_) {
                for (int i = 0; i < 30; ++i) {
                    this.modifyGunData(0, data -> data.reloadAmmo((Entity)player));
                }
                if (!player.m_7500_()) {
                    stack.m_41774_(1);
                }
                this.m_9236_().m_5594_(null, this.m_20097_(), (SoundEvent)ModSounds.HK_GMG_RELOAD.get(), SoundSource.PLAYERS, 1.0f, this.f_19796_.m_188501_() * 0.1f + 0.9f);
            }
            return InteractionResult.SUCCESS;
        }
        if (gunData.hasEnoughAmmoToShoot((Entity)player)) {
            return super.m_6096_(player, hand);
        }
        if (!gunData.selectedAmmoConsumer().isAmmoItem(stack)) {
            return super.m_6096_(player, hand);
        }
        if (!this.m_9236_().f_46443_) {
            this.modifyGunData(0, data -> data.reloadAmmo((Entity)player));
            this.m_9236_().m_5594_(null, this.m_20097_(), (SoundEvent)ModSounds.HK_GMG_RELOAD.get(), SoundSource.PLAYERS, 1.0f, this.f_19796_.m_188501_() * 0.1f + 0.9f);
        }
        return InteractionResult.SUCCESS;
    }

    private void retrieve(Player player) {
        if (this.m_9236_().f_46443_) {
            return;
        }
        for (ItemStack stack : this.getRetrieveItems()) {
            ItemStack copy = stack.m_41777_();
            if (player.m_36356_(copy)) continue;
            player.m_36176_(copy, false);
        }
        this.m_20153_();
        this.m_146870_();
    }

    public boolean banHand(LivingEntity entity) {
        return true;
    }
}

