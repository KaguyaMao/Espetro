/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.MinecraftServer$ReloadableResources
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.MinecraftServerKJS;
import dev.latvian.mods.kubejs.gui.chest.CustomChestMenu;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ScheduledServerEvent;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.ScheduledEvents;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@RemapPrefixForJS(value="kjs$")
@Mixin(value={MinecraftServer.class})
public abstract class MinecraftServerMixin
implements MinecraftServerKJS {
    @Unique
    private final CompoundTag kjs$persistentData = new CompoundTag();
    @Unique
    private ScheduledEvents kjs$scheduledEvents;
    @Unique
    private ServerLevel kjs$overworld;
    @Unique
    private AttachedData<MinecraftServer> kjs$attachedData;
    @Unique
    private final Map<UUID, Map<Integer, ItemStack>> kjs$restoreInventories = new HashMap<UUID, Map<Integer, ItemStack>>(1);

    @Shadow
    protected abstract boolean m_7038_() throws IOException;

    @Shadow
    public abstract void m_129929_();

    @Override
    @Accessor(value="resources")
    public abstract MinecraftServer.ReloadableResources kjs$getReloadableResources();

    @Inject(method={"<init>"}, at={@At(value="RETURN")})
    private void kjs$init(CallbackInfo ci) {
        CompletableFuture.runAsync(() -> this.kjs$afterResourcesLoaded(false), (Executor)this.kjs$self());
    }

    @Override
    public CompoundTag kjs$getPersistentData() {
        return this.kjs$persistentData;
    }

    @Override
    public AttachedData<MinecraftServer> kjs$getData() {
        if (this.kjs$attachedData == null) {
            this.kjs$attachedData = new AttachedData<MinecraftServer>(this.kjs$self());
            KubeJSPlugins.forEachPlugin(this.kjs$attachedData, KubeJSPlugin::attachServerData);
        }
        return this.kjs$attachedData;
    }

    @Override
    public ServerLevel kjs$getOverworld() {
        if (this.kjs$overworld == null) {
            this.kjs$overworld = this.kjs$self().m_129783_();
        }
        return this.kjs$overworld;
    }

    @Inject(method={"tickServer"}, at={@At(value="RETURN")})
    private void kjs$postTickServer(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        if (this.kjs$scheduledEvents != null) {
            this.kjs$scheduledEvents.tickAll(this.kjs$getOverworld().m_46467_());
        }
        if (!this.kjs$restoreInventories.isEmpty()) {
            for (ServerPlayer player : this.kjs$self().m_6846_().m_11314_()) {
                Map<Integer, ItemStack> map = this.kjs$restoreInventories.get(player.m_20148_());
                if (map == null || !player.m_6084_() || player.m_9232_() || player.f_36096_ instanceof CustomChestMenu) continue;
                this.kjs$restoreInventories.remove(player.m_20148_());
                NonNullList playerItems = player.m_150109_().f_35974_;
                for (int i = 0; i < playerItems.size(); ++i) {
                    playerItems.set(i, (Object)map.getOrDefault(i, ItemStack.f_41583_));
                }
            }
        }
        if (ServerEvents.TICK.hasListeners()) {
            ServerEvents.TICK.post(ScriptType.SERVER, new ServerEventJS(this.kjs$self()));
        }
    }

    @Override
    public ScheduledEvents kjs$getScheduledEvents() {
        if (this.kjs$scheduledEvents == null) {
            this.kjs$scheduledEvents = ScheduledServerEvent.make(this.kjs$self());
        }
        return this.kjs$scheduledEvents;
    }

    @Override
    public Map<UUID, Map<Integer, ItemStack>> kjs$restoreInventories() {
        return this.kjs$restoreInventories;
    }

    @Shadow
    @RemapForJS(value="isDedicated")
    public abstract boolean m_6982_();

    @Shadow
    @RemapForJS(value="stop")
    public abstract void m_7041_();

    @Inject(method={"reloadResources"}, at={@At(value="TAIL")})
    private void kjs$endResourceReload(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        CompletableFuture.runAsync(() -> this.kjs$afterResourcesLoaded(true), (Executor)this.kjs$self());
    }
}

