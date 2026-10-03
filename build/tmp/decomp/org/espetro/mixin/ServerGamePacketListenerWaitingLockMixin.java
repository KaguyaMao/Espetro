/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.espetro.mixin;

import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import org.espetro.bastion.BastionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ServerGamePacketListenerImpl.class})
public abstract class ServerGamePacketListenerWaitingLockMixin {
    @Shadow
    public ServerPlayer f_9743_;
    @Shadow
    private Vec3 f_9766_;
    @Unique
    private long espetro$lastWaitingCorrectionTick = Long.MIN_VALUE;

    @Shadow
    public abstract void m_9774_(double var1, double var3, double var5, float var7, float var8);

    @Inject(method={"handleAcceptTeleportPacket"}, at={@At(value="HEAD")})
    private void espetro$ensureAwaitingBeforeAccept(ServerboundAcceptTeleportationPacket packet, CallbackInfo ci) {
        if (this.f_9766_ == null && this.f_9743_ != null) {
            this.f_9766_ = new Vec3(this.f_9743_.m_20185_(), this.f_9743_.m_20186_(), this.f_9743_.m_20189_());
        }
    }

    @Inject(method={"handleMovePlayer"}, at={@At(value="HEAD")}, cancellable=true)
    private void espetro$rejectWaitingMovement(ServerboundMovePlayerPacket packet, CallbackInfo callback) {
        BastionManager manager = BastionManager.getInstance();
        Vec3 lock = manager.getPlayerLockPosition(this.f_9743_.m_20148_());
        if (lock == null && !manager.isWaitingForBastion(this.f_9743_.m_20148_())) {
            return;
        }
        if (this.f_9766_ == null) {
            this.f_9766_ = new Vec3(this.f_9743_.m_20185_(), this.f_9743_.m_20186_(), this.f_9743_.m_20189_());
        }
        this.f_9743_.m_20256_(Vec3.f_82478_);
        this.f_9743_.f_19789_ = 0.0f;
        if (lock != null) {
            double requestedX = packet.m_134129_(this.f_9743_.m_20185_());
            double requestedY = packet.m_134140_(this.f_9743_.m_20186_());
            double requestedZ = packet.m_134146_(this.f_9743_.m_20189_());
            double dx = requestedX - lock.f_82479_;
            double dy = requestedY - lock.f_82480_;
            double dz = requestedZ - lock.f_82481_;
            long tick = this.f_9743_.f_8924_.m_129921_();
            if ((dx * dx + dy * dy + dz * dz > 0.25 || this.f_9743_.m_20238_(lock) > 0.25) && (this.espetro$lastWaitingCorrectionTick == Long.MIN_VALUE || tick - this.espetro$lastWaitingCorrectionTick >= 10L)) {
                this.espetro$lastWaitingCorrectionTick = tick;
                this.m_9774_(lock.f_82479_, lock.f_82480_, lock.f_82481_, this.f_9743_.m_146908_(), this.f_9743_.m_146909_());
            }
        }
        callback.cancel();
    }
}

