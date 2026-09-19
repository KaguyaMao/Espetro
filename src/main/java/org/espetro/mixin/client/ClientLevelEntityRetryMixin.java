package org.espetro.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import org.espetro.client.vehicle.ClientEntityRetryQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 拦截 {@link ClientLevel#addEntity(int, Entity)}（1.20.1 实际签名为
 * {@code addEntity(int, Entity)} 且返回 void）。
 * <p>当实体所在区块尚未 FULL 加载时，原版会静默丢弃实体且服务端不会重发 spawn 包，
 * 导致开局刷新的载具/补给站在客户端永久缺失。这里在调用前检查区块状态：
 * 未就绪则取消本次添加，将实体交给 {@link ClientEntityRetryQueue} 暂存，
 * 等区块加载完成后自动补加。
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelEntityRetryMixin {

    @Inject(method = "addEntity(ILnet/minecraft/world/entity/Entity;)V",
        at = @At("HEAD"), cancellable = true, require = 1)
    private void espetro$deferEntityIfChunkNotReady(int id, Entity entity, CallbackInfo ci) {
        if (entity == null || entity instanceof Player) {
            // 玩家（含本地玩家 LocalPlayer）与服务器其他玩家一律走原版路径：
            // 原版 addEntity 从不因区块状态丢弃实体，玩家加入世界必须立即生效，
            // 否则玩家不在实体 tick 列表中，将无法移动/使用物品。
            return;
        }
        ClientLevel level = (ClientLevel) (Object) this;
        ChunkAccess chunk = level.getChunk(
            entity.getBlockX() >> 4, entity.getBlockZ() >> 4, ChunkStatus.FULL, false);
        if (chunk instanceof LevelChunk && chunk.getStatus().isOrAfter(ChunkStatus.FULL)) {
            return; // 区块已 FULL 加载，正常走原版逻辑
        }
        // 区块未就绪：取消本次添加，暂存等待补加
        ci.cancel();
        ClientEntityRetryQueue.stash(entity);
    }
}
