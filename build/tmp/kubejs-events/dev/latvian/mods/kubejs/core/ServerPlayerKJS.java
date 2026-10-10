/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.registry.menu.ExtendedMenuProvider
 *  dev.architectury.registry.menu.MenuRegistry
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.advancements.AdvancementProgress
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.players.StoredUserEntry
 *  net.minecraft.server.players.UserBanListEntry
 *  net.minecraft.stats.StatsCounter
 *  net.minecraft.util.Mth
 *  net.minecraft.world.Container
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleContainer
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.GameType
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.core.PlayerKJS;
import dev.latvian.mods.kubejs.gui.KubeJSGUI;
import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import dev.latvian.mods.kubejs.gui.chest.ChestMenuData;
import dev.latvian.mods.kubejs.gui.chest.CustomChestMenu;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.net.NotificationMessage;
import dev.latvian.mods.kubejs.net.PaintMessage;
import dev.latvian.mods.kubejs.net.SendDataFromServerMessage;
import dev.latvian.mods.kubejs.player.AdvancementJS;
import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.util.NotificationBuilder;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Consumer;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface ServerPlayerKJS
extends PlayerKJS {
    default public ServerPlayer kjs$self() {
        return (ServerPlayer)this;
    }

    @Override
    default public void kjs$sendData(String channel, @Nullable CompoundTag data) {
        if (!channel.isEmpty()) {
            new SendDataFromServerMessage(channel, data).sendTo(this.kjs$self());
        }
    }

    @Override
    default public void kjs$paint(CompoundTag renderer) {
        new PaintMessage(renderer).sendTo(this.kjs$self());
    }

    @Override
    default public PlayerStatsJS kjs$getStats() {
        return new PlayerStatsJS((Player)this.kjs$self(), (StatsCounter)this.kjs$self().m_8951_());
    }

    @Override
    default public boolean kjs$isMiningBlock() {
        return this.kjs$self().f_8941_.f_9249_;
    }

    @Override
    default public void kjs$setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        PlayerKJS.super.kjs$setPositionAndRotation(x, y, z, yaw, pitch);
        this.kjs$self().f_8906_.m_9774_(x, y, z, yaw, pitch);
    }

    default public void kjs$setCreativeMode(boolean mode) {
        this.kjs$self().m_143403_(mode ? GameType.CREATIVE : GameType.SURVIVAL);
    }

    default public boolean kjs$isOp() {
        return this.kjs$self().f_8924_.m_6846_().m_11303_(this.kjs$self().m_36316_());
    }

    default public void kjs$kick(Component reason) {
        this.kjs$self().f_8906_.m_9942_(reason);
    }

    default public void kjs$kick() {
        this.kjs$kick((Component)Component.m_237115_((String)"multiplayer.disconnect.kicked"));
    }

    default public void kjs$ban(String banner, String reason, long expiresInMillis) {
        Date date = new Date();
        UserBanListEntry userlistbansentry = new UserBanListEntry(this.kjs$self().m_36316_(), date, banner, new Date(date.getTime() + (expiresInMillis <= 0L ? 315569260000L : expiresInMillis)), reason);
        this.kjs$self().f_8924_.m_6846_().m_11295_().m_11381_((StoredUserEntry)userlistbansentry);
        this.kjs$kick((Component)Component.m_237115_((String)"multiplayer.disconnect.banned"));
    }

    default public boolean kjs$isAdvancementDone(ResourceLocation id) {
        AdvancementJS a = this.kjs$self().f_8924_.kjs$getAdvancement(id);
        return a != null && this.kjs$self().m_8960_().m_135996_(a.advancement).m_8193_();
    }

    default public void kjs$unlockAdvancement(ResourceLocation id) {
        AdvancementJS a = this.kjs$self().f_8924_.kjs$getAdvancement(id);
        if (a != null) {
            AdvancementProgress advancementprogress = this.kjs$self().m_8960_().m_135996_(a.advancement);
            for (String s : advancementprogress.m_8219_()) {
                this.kjs$self().m_8960_().m_135988_(a.advancement, s);
            }
        }
    }

    default public void kjs$revokeAdvancement(ResourceLocation id) {
        AdvancementProgress advancementprogress;
        AdvancementJS a = this.kjs$self().f_8924_.kjs$getAdvancement(id);
        if (a != null && (advancementprogress = this.kjs$self().m_8960_().m_135996_(a.advancement)).m_8206_()) {
            for (String s : advancementprogress.m_8220_()) {
                this.kjs$self().m_8960_().m_135998_(a.advancement, s);
            }
        }
    }

    @Override
    default public void kjs$setSelectedSlot(int index) {
        int p = this.kjs$getSelectedSlot();
        PlayerKJS.super.kjs$setSelectedSlot(index);
        int n = this.kjs$getSelectedSlot();
        if (p != n && this.kjs$self().f_8906_ != null) {
            this.kjs$self().f_8906_.m_9829_((Packet)new ClientboundSetCarriedItemPacket(n));
        }
    }

    @Override
    default public void kjs$setMouseItem(ItemStack item) {
        PlayerKJS.super.kjs$setMouseItem(item);
        if (this.kjs$self().f_8906_ != null) {
            this.kjs$self().f_36095_.m_38946_();
        }
    }

    @Nullable
    default public BlockContainerJS kjs$getSpawnLocation() {
        BlockPos pos = this.kjs$self().m_8961_();
        return pos == null ? null : new BlockContainerJS(this.kjs$getLevel(), pos);
    }

    default public void kjs$setSpawnLocation(BlockContainerJS c) {
        this.kjs$self().m_9158_(c.minecraftLevel.m_46472_(), c.getPos(), 0.0f, true, false);
    }

    @Override
    default public void kjs$notify(NotificationBuilder builder) {
        new NotificationMessage(builder).sendTo(this.kjs$self());
    }

    default public void kjs$openGUI(Consumer<KubeJSGUI> gui) {
        final KubeJSGUI data = new KubeJSGUI();
        gui.accept(data);
        MenuRegistry.openExtendedMenu((ServerPlayer)this.kjs$self(), (ExtendedMenuProvider)new ExtendedMenuProvider(){

            public void saveExtraData(FriendlyByteBuf buf) {
                data.write(buf);
            }

            public Component m_5446_() {
                return data.title;
            }

            public AbstractContainerMenu m_7208_(int i, Inventory inventory, Player player) {
                return new KubeJSMenu(i, inventory, data);
            }
        });
    }

    default public void kjs$openInventoryGUI(InventoryKJS inventory, Component title) {
        this.kjs$openGUI(gui -> {
            gui.title = title;
            gui.setInventory(inventory);
        });
    }

    default public Container kjs$captureInventory(boolean autoRestore) {
        NonNullList playerItems = this.kjs$self().m_150109_().f_35974_;
        SimpleContainer captured = new SimpleContainer(playerItems.size());
        HashMap<Integer, ItemStack> map = new HashMap<Integer, ItemStack>();
        for (int i = 0; i < playerItems.size(); ++i) {
            ItemStack c = (ItemStack)playerItems.set(i, (Object)ItemStack.f_41583_);
            if (c.m_41619_()) continue;
            if (autoRestore) {
                map.put(i, c);
            }
            captured.m_6836_(i, c.m_41777_());
        }
        if (autoRestore && !map.isEmpty()) {
            this.kjs$self().m_20194_().kjs$restoreInventories().put(this.kjs$self().m_20148_(), map);
        }
        return captured;
    }

    /*
     * Enabled aggressive block sorting
     */
    default public void kjs$openChestGUI(final Component title, int rows, Consumer<ChestMenuData> gui) {
        CustomChestMenu open;
        final ChestMenuData data = new ChestMenuData(this.kjs$self(), title, Mth.m_14045_((int)rows, (int)1, (int)6));
        gui.accept(data);
        AbstractContainerMenu abstractContainerMenu = this.kjs$self().f_36096_;
        if (abstractContainerMenu instanceof CustomChestMenu) {
            open = (CustomChestMenu)abstractContainerMenu;
            data.capturedInventory = open.data.capturedInventory;
        } else {
            data.capturedInventory = this.kjs$captureInventory(true);
        }
        abstractContainerMenu = this.kjs$self().f_36096_;
        if (abstractContainerMenu instanceof CustomChestMenu) {
            open = (CustomChestMenu)abstractContainerMenu;
            if (open.data.rows == data.rows && open.data.title.equals(title)) {
                open.data = data;
                data.sync();
                return;
            }
        }
        data.sync();
        this.kjs$self().m_5893_(new MenuProvider(){

            public Component m_5446_() {
                return title;
            }

            public AbstractContainerMenu m_7208_(int i, Inventory inventory, Player player) {
                return new CustomChestMenu(i, data);
            }
        });
    }
}

