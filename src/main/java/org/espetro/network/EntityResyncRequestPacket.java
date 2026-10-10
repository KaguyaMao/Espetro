package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 客户端→服务端：请求重发某个实体的位置与数据。
 * <p>
 * 客户端在区块未 FULL 加载时收到 spawn 包，实体被 {@code ClientLevel.addEntity}
 * 丢弃后由 {@code ClientEntityRetryQueue} 暂存补加；补加期间服务端发送的位置增量包
 * （MoveEntity/Teleport/SetEntityData）因实体未注册被客户端丢弃，导致补加的实体
 * 停留在 spawn 包时刻的位置（或残留为服务端已移除的幽灵实体）。
 * <p>
 * 客户端补加成功后发送本包，服务端用绝对位置 teleport 包 + 实体数据包重同步；
 * 若实体已不存在则回发移除包，让客户端清掉幽灵实体。
 */
public class EntityResyncRequestPacket {

    private final int entityId;

    public EntityResyncRequestPacket(int entityId) {
        this.entityId = entityId;
    }

    public static EntityResyncRequestPacket read(FriendlyByteBuf buf) {
        return new EntityResyncRequestPacket(buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = ctx.get().getSender();
                if (player == null || player.connection == null) {
                    return;
                }
                Entity entity = findEntity(player, entityId);
                if (entity == null) {
                    // 服务端已无此实体：清掉客户端补加的幽灵实体
                    player.connection.send(
                        new net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket(entityId));
                    return;
                }
                // 绝对位置 + 全量数据重同步，纠正补加实体的过期位置/缺失数据
                player.connection.send(
                    new net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket(entity));
                java.util.List<net.minecraft.network.syncher.SynchedEntityData.DataValue<?>> data =
                    entity.getEntityData().getNonDefaultValues();
                player.connection.send(
                    new net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket(
                        entity.getId(), data == null ? java.util.List.of() : data));
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static Entity findEntity(ServerPlayer player, int entityId) {
        if (player.serverLevel() != null) {
            Entity direct = player.serverLevel().getEntity(entityId);
            if (direct != null) {
                return direct;
            }
        }
        for (ServerLevel level : player.server.getAllLevels()) {
            Entity found = level.getEntity(entityId);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
