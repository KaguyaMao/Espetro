/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.init.ModItems
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.redabysslucia.dragonrise_reforge.network.message;

import com.atsuishio.superbwarfare.init.ModItems;
import com.redabysslucia.dragonrise_reforge.entities.special.R6DroneEntity;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class R6DroneControlMessage {
    private final short keys;

    public R6DroneControlMessage(short keys) {
        this.keys = keys;
    }

    public static void encode(R6DroneControlMessage msg, FriendlyByteBuf buf) {
        buf.writeShort((int)msg.keys);
    }

    public static R6DroneControlMessage decode(FriendlyByteBuf buf) {
        return new R6DroneControlMessage(buf.readShort());
    }

    public static void handle(R6DroneControlMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            ItemStack stack = player.m_21205_();
            if (!stack.m_150930_((Item)ModItems.MONITOR.get())) {
                return;
            }
            CompoundTag tag = stack.m_41784_();
            if (!tag.m_128471_("Using") || !tag.m_128471_("Linked")) {
                return;
            }
            String linked = tag.m_128461_("LinkedDrone");
            R6DroneEntity drone = R6DroneEntity.findDrone(player.m_9236_(), linked);
            if (drone != null) {
                drone.processInput(msg.keys);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

