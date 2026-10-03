/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.npc.AbstractVillager
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.trading.MerchantOffer
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus;

public class TradeWithVillagerEvent
extends PlayerEvent {
    private final MerchantOffer offer;
    private final AbstractVillager abstractVillager;

    @ApiStatus.Internal
    public TradeWithVillagerEvent(Player player, MerchantOffer offer, AbstractVillager abstractVillager) {
        super(player);
        this.offer = offer;
        this.abstractVillager = abstractVillager;
    }

    public MerchantOffer getMerchantOffer() {
        return this.offer;
    }

    public AbstractVillager getAbstractVillager() {
        return this.abstractVillager;
    }
}

