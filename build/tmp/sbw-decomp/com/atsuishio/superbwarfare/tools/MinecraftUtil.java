/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.JvmName
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.PacketDistributor$PacketTarget
 *  net.minecraftforge.registries.RegistryObject
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atsuishio.superbwarfare.tools;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.network.NetworkRegistry;
import com.atsuishio.superbwarfare.tools.FormatTool;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 0, 0}, k=2, xi=48, d1={"\u0000\u00a0\u0001\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\r\u0010\u0018\u001a\u00020\u0019*\u00020\u001aH\u0086\u0002\u001a\r\u0010\u001b\u001a\u00020\u0019*\u00020\u001aH\u0086\u0002\u001a\r\u0010\u001c\u001a\u00020\u0019*\u00020\u001aH\u0086\u0002\u001a\u0015\u0010\u001d\u001a\u00020\u001e*\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H\u0086\u0002\u001a\u0015\u0010\u001d\u001a\u00020\u001e*\u00020\u001e2\u0006\u0010\u001f\u001a\u00020!H\u0086\u0002\u001a\u001d\u0010\"\u001a\u00020\u0015*\u0004\u0018\u00010#\u0082\u0002\u000e\n\f\b\u0000\u0012\u0002\u0018\u0001\u001a\u0004\b\u0003\u0010\u0000\u001a\f\u0010$\u001a\u00020!*\u0004\u0018\u00010%\u001a\u0017\u0010&\u001a\u00020\u0015*\u00020'2\b\u0010(\u001a\u0004\u0018\u00010'H\u0086\u0004\u001a\u0016\u0010)\u001a\u00020\u00152\u0006\u0010*\u001a\u00020'2\u0006\u0010+\u001a\u00020'\u001a\f\u0010,\u001a\u00020\u0015*\u00020'H\u0002\u001a\u0012\u0010-\u001a\u00020.*\u00020#2\u0006\u0010/\u001a\u000200\u001a\u0016\u00101\u001a\u00020.2\u0006\u00102\u001a\u00020#2\u0006\u0010/\u001a\u000200\u001a\u0016\u00101\u001a\u00020.2\u0006\u00103\u001a\u0002042\u0006\u0010/\u001a\u000200\u001a\u000e\u00105\u001a\u00020.2\u0006\u0010/\u001a\u000200\u001a\u000e\u00106\u001a\u00020.2\u0006\u0010/\u001a\u000200\u001a\u0016\u00107\u001a\u00020.2\u0006\u00108\u001a\u0002092\u0006\u0010/\u001a\u000200\u001a\u0012\u0010:\u001a\u00020.*\u0002092\u0006\u0010/\u001a\u000200\u001a\u001d\u0010;\u001a\u00020\u0015\"\b\b\u0000\u0010<*\u00020=2\u0006\u0010>\u001a\u0002H<\u00a2\u0006\u0002\u0010?\u001a$\u0010@\u001a\u00020.2\u0006\u0010A\u001a\u00020\u00192\u000e\b\u0004\u0010B\u001a\b\u0012\u0004\u0012\u00020.0CH\u0086\b\u00f8\u0001\u0000\u001a/\u0010D\u001a\u00020\u0015*\u00020'2\u001e\u0010E\u001a\u0010\u0012\f\b\u0001\u0012\b\u0012\u0004\u0012\u00020H0G0F\"\b\u0012\u0004\u0012\u00020H0G\u00a2\u0006\u0002\u0010I\u001a#\u0010D\u001a\u00020\u0015*\u00020'2\u0012\u0010J\u001a\n\u0012\u0006\b\u0001\u0012\u00020H0F\"\u00020H\u00a2\u0006\u0002\u0010K\"\u0011\u0010\u0000\u001a\u00020\u00018G\u00a2\u0006\u0006\u001a\u0004\b\u0002\u0010\u0003\"\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u00058G\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\"\u0013\u0010\b\u001a\u0004\u0018\u00010\t8G\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000b\"\u0011\u0010\f\u001a\u00020\r8G\u00a2\u0006\u0006\u001a\u0004\b\u000e\u0010\u000f\"\u0011\u0010\u0010\u001a\u00020\u00118G\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013\"\u0011\u0010\u0014\u001a\u00020\u00158G\u00a2\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017\u0082\u0002\u0007\n\u0005\b\u009920\u0001\u00a8\u0006L"}, d2={"mc", "Lnet/minecraft/client/Minecraft;", "getMc", "()Lnet/minecraft/client/Minecraft;", "localPlayer", "Lnet/minecraft/client/player/LocalPlayer;", "getLocalPlayer", "()Lnet/minecraft/client/player/LocalPlayer;", "clientLevel", "Lnet/minecraft/client/multiplayer/ClientLevel;", "getClientLevel", "()Lnet/minecraft/client/multiplayer/ClientLevel;", "font", "Lnet/minecraft/client/gui/Font;", "getFont", "()Lnet/minecraft/client/gui/Font;", "options", "Lnet/minecraft/client/Options;", "getOptions", "()Lnet/minecraft/client/Options;", "notInGame", "", "getNotInGame", "()Z", "component1", "", "Lnet/minecraft/core/BlockPos;", "component2", "component3", "plus", "Lnet/minecraft/network/chat/MutableComponent;", "other", "Lnet/minecraft/network/chat/Component;", "", "isNullOrSpector", "Lnet/minecraft/world/entity/player/Player;", "toFormattedString", "Lnet/minecraft/world/phys/Vec3;", "sameWith", "Lnet/minecraft/world/item/ItemStack;", "that", "isSameItemStack", "a", "b", "hasEmptyTag", "sendPacket", "", "packet", "", "sendPacketTo", "player", "target", "Lnet/minecraftforge/network/PacketDistributor$PacketTarget;", "sendPacketToAll", "sendPacketToServer", "sendPacketToTrackingEntity", "entity", "Lnet/minecraft/world/entity/Entity;", "sendPacketToTrackingThis", "postEvent", "T", "Lnet/minecraftforge/eventbus/api/Event;", "event", "(Lnet/minecraftforge/eventbus/api/Event;)Z", "queueClientWorkIfDelayed", "delay", "block", "Lkotlin/Function0;", "is", "itemsRegistry", "", "Lnet/minecraftforge/registries/RegistryObject;", "Lnet/minecraft/world/item/Item;", "(Lnet/minecraft/world/item/ItemStack;[Lnet/minecraftforge/registries/RegistryObject;)Z", "items", "(Lnet/minecraft/world/item/ItemStack;[Lnet/minecraft/world/item/Item;)Z", "superbwarfare"})
@JvmName(name="MinecraftUtil")
@SourceDebugExtension(value={"SMAP\nMinecraftUtil.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MinecraftUtil.kt\ncom/atsuishio/superbwarfare/tools/MinecraftUtil\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,157:1\n1#2:158\n12511#3,2:159\n12511#3,2:161\n*S KotlinDebug\n*F\n+ 1 MinecraftUtil.kt\ncom/atsuishio/superbwarfare/tools/MinecraftUtil\n*L\n152#1:159,2\n156#1:161,2\n*E\n"})
public final class MinecraftUtil {
    @OnlyIn(value=Dist.CLIENT)
    @NotNull
    public static final Minecraft getMc() {
        Minecraft minecraft = Minecraft.m_91087_();
        Intrinsics.checkNotNullExpressionValue((Object)minecraft, (String)"getInstance(...)");
        return minecraft;
    }

    @OnlyIn(value=Dist.CLIENT)
    @Nullable
    public static final LocalPlayer getLocalPlayer() {
        return MinecraftUtil.getMc().f_91074_;
    }

    @OnlyIn(value=Dist.CLIENT)
    @Nullable
    public static final ClientLevel getClientLevel() {
        return MinecraftUtil.getMc().f_91073_;
    }

    @OnlyIn(value=Dist.CLIENT)
    @NotNull
    public static final Font getFont() {
        Font font = MinecraftUtil.getMc().f_91062_;
        Intrinsics.checkNotNullExpressionValue((Object)font, (String)"font");
        return font;
    }

    @OnlyIn(value=Dist.CLIENT)
    @NotNull
    public static final Options getOptions() {
        Options options = MinecraftUtil.getMc().f_91066_;
        Intrinsics.checkNotNullExpressionValue((Object)options, (String)"options");
        return options;
    }

    @OnlyIn(value=Dist.CLIENT)
    public static final boolean getNotInGame() {
        if (MinecraftUtil.getMc().f_91074_ == null) {
            return true;
        }
        if (MinecraftUtil.getMc().m_91265_() != null) {
            return true;
        }
        if (MinecraftUtil.getMc().f_91080_ != null) {
            return true;
        }
        if (!MinecraftUtil.getMc().f_91067_.m_91600_()) {
            return true;
        }
        return !MinecraftUtil.getMc().m_91302_();
    }

    public static final int component1(@NotNull BlockPos $this$component1) {
        Intrinsics.checkNotNullParameter((Object)$this$component1, (String)"<this>");
        return $this$component1.m_123341_();
    }

    public static final int component2(@NotNull BlockPos $this$component2) {
        Intrinsics.checkNotNullParameter((Object)$this$component2, (String)"<this>");
        return $this$component2.m_123342_();
    }

    public static final int component3(@NotNull BlockPos $this$component3) {
        Intrinsics.checkNotNullParameter((Object)$this$component3, (String)"<this>");
        return $this$component3.m_123343_();
    }

    @NotNull
    public static final MutableComponent plus(@NotNull MutableComponent $this$plus, @NotNull Component other) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)other, (String)"other");
        MutableComponent mutableComponent = $this$plus.m_7220_(other);
        Intrinsics.checkNotNullExpressionValue((Object)mutableComponent, (String)"append(...)");
        return mutableComponent;
    }

    @NotNull
    public static final MutableComponent plus(@NotNull MutableComponent $this$plus, @NotNull String other) {
        Intrinsics.checkNotNullParameter((Object)$this$plus, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)other, (String)"other");
        MutableComponent mutableComponent = $this$plus.m_7220_((Component)Component.m_237113_((String)other));
        Intrinsics.checkNotNullExpressionValue((Object)mutableComponent, (String)"append(...)");
        return mutableComponent;
    }

    public static final boolean isNullOrSpector(@Nullable Player $this$isNullOrSpector) {
        return $this$isNullOrSpector == null || $this$isNullOrSpector.m_5833_();
    }

    @NotNull
    public static final String toFormattedString(@Nullable Vec3 $this$toFormattedString) {
        if ($this$toFormattedString == null) {
            return "[ ---, ---, --- ]";
        }
        return "[ " + FormatTool.format0D$default($this$toFormattedString.f_82479_, null, 2, null) + ", " + FormatTool.format0D$default($this$toFormattedString.f_82480_, null, 2, null) + ", " + FormatTool.format0D$default($this$toFormattedString.f_82481_, null, 2, null) + " ]";
    }

    public static final boolean sameWith(@NotNull ItemStack $this$sameWith, @Nullable ItemStack that) {
        Object object;
        Object object2;
        Intrinsics.checkNotNullParameter((Object)$this$sameWith, (String)"<this>");
        if (that == null) {
            return false;
        }
        if ($this$sameWith.m_41720_() != that.m_41720_()) {
            return false;
        }
        CompoundTag compoundTag = $this$sameWith.m_41783_();
        if (compoundTag != null) {
            CompoundTag compoundTag2;
            CompoundTag it = compoundTag2 = compoundTag;
            boolean bl = false;
            object2 = !it.m_128456_() ? compoundTag2 : null;
        } else {
            object2 = null;
        }
        CompoundTag thisTag = object2;
        CompoundTag compoundTag3 = that.m_41783_();
        if (compoundTag3 != null) {
            CompoundTag compoundTag4;
            CompoundTag it = compoundTag4 = compoundTag3;
            boolean bl = false;
            object = !it.m_128456_() ? compoundTag4 : null;
        } else {
            object = null;
        }
        CompoundTag thatTag = object;
        return Intrinsics.areEqual((Object)thisTag, thatTag);
    }

    public static final boolean isSameItemStack(@NotNull ItemStack a, @NotNull ItemStack b) {
        Intrinsics.checkNotNullParameter((Object)a, (String)"a");
        Intrinsics.checkNotNullParameter((Object)b, (String)"b");
        return MinecraftUtil.sameWith(a, b);
    }

    private static final boolean hasEmptyTag(ItemStack $this$hasEmptyTag) {
        CompoundTag compoundTag = $this$hasEmptyTag.m_41783_();
        return compoundTag != null ? compoundTag.m_128456_() : false;
    }

    public static final void sendPacket(@NotNull Player $this$sendPacket, @NotNull Object packet) {
        Intrinsics.checkNotNullParameter((Object)$this$sendPacket, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)packet, (String)"packet");
        MinecraftUtil.sendPacketTo($this$sendPacket, packet);
    }

    public static final void sendPacketTo(@NotNull Player player, @NotNull Object packet) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Intrinsics.checkNotNullParameter((Object)packet, (String)"packet");
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        if (packet instanceof Packet) {
            ((ServerPlayer)player).f_8906_.m_9829_((Packet)packet);
        } else {
            NetworkRegistry.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> MinecraftUtil.sendPacketTo$lambda$2(player)), packet);
        }
    }

    public static final void sendPacketTo(@NotNull PacketDistributor.PacketTarget target, @NotNull Object packet) {
        Intrinsics.checkNotNullParameter((Object)target, (String)"target");
        Intrinsics.checkNotNullParameter((Object)packet, (String)"packet");
        NetworkRegistry.PACKET_HANDLER.send(target, packet);
    }

    public static final void sendPacketToAll(@NotNull Object packet) {
        Intrinsics.checkNotNullParameter((Object)packet, (String)"packet");
        NetworkRegistry.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), packet);
    }

    public static final void sendPacketToServer(@NotNull Object packet) {
        Intrinsics.checkNotNullParameter((Object)packet, (String)"packet");
        NetworkRegistry.PACKET_HANDLER.sendToServer(packet);
    }

    public static final void sendPacketToTrackingEntity(@NotNull Entity entity, @NotNull Object packet) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        Intrinsics.checkNotNullParameter((Object)packet, (String)"packet");
        NetworkRegistry.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> MinecraftUtil.sendPacketToTrackingEntity$lambda$3(entity)), packet);
    }

    public static final void sendPacketToTrackingThis(@NotNull Entity $this$sendPacketToTrackingThis, @NotNull Object packet) {
        Intrinsics.checkNotNullParameter((Object)$this$sendPacketToTrackingThis, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)packet, (String)"packet");
        MinecraftUtil.sendPacketToTrackingEntity($this$sendPacketToTrackingThis, packet);
    }

    public static final <T extends Event> boolean postEvent(@NotNull T event) {
        Intrinsics.checkNotNullParameter(event, (String)"event");
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static final void queueClientWorkIfDelayed(int delay, @NotNull Function0<Unit> block) {
        Intrinsics.checkNotNullParameter(block, (String)"block");
        boolean $i$f$queueClientWorkIfDelayed = false;
        if (delay > 0) {
            Mod.Companion.queueClientWork(delay, new Runnable(block){
                final /* synthetic */ Function0<Unit> $block;
                {
                    this.$block = $block;
                }

                public final void run() {
                    this.$block.invoke();
                }
            });
        } else {
            block.invoke();
        }
    }

    public static final boolean is(@NotNull ItemStack $this$is, RegistryObject<Item> ... itemsRegistry) {
        boolean bl;
        block1: {
            Intrinsics.checkNotNullParameter((Object)$this$is, (String)"<this>");
            Intrinsics.checkNotNullParameter(itemsRegistry, (String)"itemsRegistry");
            RegistryObject<Item>[] $this$any$iv = itemsRegistry;
            boolean $i$f$any = false;
            int n = $this$any$iv.length;
            for (int i = 0; i < n; ++i) {
                RegistryObject<Item> element$iv;
                RegistryObject<Item> it = element$iv = $this$any$iv[i];
                boolean bl2 = false;
                if (!$this$is.m_150930_((Item)it.get())) continue;
                bl = true;
                break block1;
            }
            bl = false;
        }
        return bl;
    }

    public static final boolean is(@NotNull ItemStack $this$is, Item ... items) {
        boolean bl;
        block1: {
            Intrinsics.checkNotNullParameter((Object)$this$is, (String)"<this>");
            Intrinsics.checkNotNullParameter((Object)items, (String)"items");
            Item[] $this$any$iv = items;
            boolean $i$f$any = false;
            int n = $this$any$iv.length;
            for (int i = 0; i < n; ++i) {
                Item element$iv;
                Item it = element$iv = $this$any$iv[i];
                boolean bl2 = false;
                if (!$this$is.m_150930_(it)) continue;
                bl = true;
                break block1;
            }
            bl = false;
        }
        return bl;
    }

    private static final ServerPlayer sendPacketTo$lambda$2(Player $player) {
        Intrinsics.checkNotNullParameter((Object)$player, (String)"$player");
        return (ServerPlayer)$player;
    }

    private static final Entity sendPacketToTrackingEntity$lambda$3(Entity $entity) {
        Intrinsics.checkNotNullParameter((Object)$entity, (String)"$entity");
        return $entity;
    }
}
