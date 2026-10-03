/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  io.netty.handler.codec.DecoderException
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.handler.codec.DecoderException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;
import org.espetro.logistics.resupply.ResupplySourceRef;

public record ResupplyCatalogPacket(UUID token, long catalogRevision, long stateRevision, ResupplySourceRef source, int balance, List<Entry> entries) {
    public static final int MAX_ENTRIES = 64;
    public static final int MAX_TEXT = 512;
    public static final int MAX_ITEM_TAG_CHARS = Short.MAX_VALUE;
    public static final int MAX_RESOURCE_LOCATION = 256;
    public static final int MAX_VALUE = 1000000;

    public ResupplyCatalogPacket {
        List<Object> list = entries = entries == null ? List.of() : List.copyOf(entries);
        if (entries.size() > 64) {
            throw new IllegalArgumentException("too many entries");
        }
    }

    public static ResupplyCatalogPacket read(FriendlyByteBuf buf) {
        UUID token = buf.m_130259_();
        long catalogue = buf.readLong();
        long state = buf.readLong();
        ResupplySourceRef source = ResupplySourceRef.read(buf);
        int balance = buf.m_130242_();
        int count = buf.m_130242_();
        if (count < 0 || count > 64) {
            throw new DecoderException("invalid resupply entry count " + count);
        }
        ArrayList<Entry> entries = new ArrayList<Entry>(count);
        for (int i = 0; i < count; ++i) {
            entries.add(Entry.read(buf));
        }
        return new ResupplyCatalogPacket(token, catalogue, state, source, balance, entries);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130077_(this.token);
        buf.writeLong(this.catalogRevision);
        buf.writeLong(this.stateRevision);
        this.source.write(buf);
        buf.m_130130_(Math.max(0, this.balance));
        buf.m_130130_(this.entries.size());
        for (Entry entry : this.entries) {
            entry.write(buf);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleResupplyCatalog", ResupplyCatalogPacket.class).invoke(null, this);
            }
            catch (ReflectiveOperationException error) {
                Espetro.LOGGER.error("\u5904\u7406\u8865\u7ed9\u76ee\u5f55\u5931\u8d25", (Throwable)error);
            }
        });
        context.setPacketHandled(true);
    }

    public record Entry(int index, ItemStack icon, String configuredId, int count, int max, int ammoCost, int current, boolean selectable, String reason) {
        public Entry {
            icon = icon == null ? ItemStack.f_41583_ : icon.m_41777_();
            configuredId = Entry.bounded(configuredId);
            reason = Entry.bounded(reason);
        }

        private static String bounded(String value) {
            String text = value == null ? "" : value;
            return text.length() <= 512 ? text : text.substring(0, 512);
        }

        void write(FriendlyByteBuf buf) {
            String tag;
            buf.m_130130_(this.index);
            ResourceLocation itemId = BuiltInRegistries.f_257033_.m_7981_(this.icon.m_41720_());
            buf.m_130072_(itemId.toString(), 256);
            String string = tag = this.icon.m_41782_() ? this.icon.m_41783_().toString() : "";
            if (tag.length() > Short.MAX_VALUE) {
                throw new IllegalArgumentException("resupply item tag too large");
            }
            buf.m_130072_(tag, Short.MAX_VALUE);
            buf.m_130072_(this.configuredId, 512);
            buf.m_130130_(this.count);
            buf.m_130130_(this.max);
            buf.m_130130_(this.ammoCost);
            buf.m_130130_(this.current);
            buf.writeBoolean(this.selectable);
            buf.m_130072_(this.reason, 512);
        }

        static Entry read(FriendlyByteBuf buf) {
            int index = buf.m_130242_();
            ResourceLocation itemId = ResourceLocation.m_135820_(buf.m_130136_(256));
            if (itemId == null) {
                throw new DecoderException("invalid resupply item id");
            }
            Item item = BuiltInRegistries.f_257033_.m_6612_(itemId).orElse(null);
            if (item == null) {
                throw new DecoderException("unknown resupply item id");
            }
            ItemStack icon = new ItemStack(item);
            String tag = buf.m_130136_(Short.MAX_VALUE);
            if (!tag.isEmpty()) {
                try {
                    icon.m_41751_(TagParser.m_129359_(tag));
                }
                catch (CommandSyntaxException error) {
                    throw new DecoderException("invalid resupply item tag", (Throwable)error);
                }
            }
            String configuredId = buf.m_130136_(512);
            int count = Entry.readBoundedValue(buf, "count");
            int max = Entry.readBoundedValue(buf, "max");
            int cost = Entry.readBoundedValue(buf, "ammo cost");
            int current = Entry.readBoundedValue(buf, "current");
            return new Entry(index, icon, configuredId, count, max, cost, current, buf.readBoolean(), buf.m_130136_(512));
        }

        private static int readBoundedValue(FriendlyByteBuf buf, String field) {
            int value = buf.m_130242_();
            if (value < 0 || value > 1000000) {
                throw new DecoderException("invalid resupply " + field);
            }
            return value;
        }
    }
}

