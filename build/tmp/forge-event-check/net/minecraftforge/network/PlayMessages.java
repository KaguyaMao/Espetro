/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.MenuScreens
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.MenuAccess
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package net.minecraftforge.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkEvent;

public class PlayMessages {

    public static class OpenContainer {
        private final int id;
        private final int windowId;
        private final Component name;
        private final FriendlyByteBuf additionalData;

        OpenContainer(MenuType<?> id, int windowId, Component name, FriendlyByteBuf additionalData) {
            this(BuiltInRegistries.f_256818_.m_7447_(id), windowId, name, additionalData);
        }

        private OpenContainer(int id, int windowId, Component name, FriendlyByteBuf additionalData) {
            this.id = id;
            this.windowId = windowId;
            this.name = name;
            this.additionalData = additionalData;
        }

        public static void encode(OpenContainer msg, FriendlyByteBuf buf) {
            buf.m_130130_(msg.id);
            buf.m_130130_(msg.windowId);
            buf.m_130083_(msg.name);
            buf.m_130087_(msg.additionalData.m_130052_());
        }

        public static OpenContainer decode(FriendlyByteBuf buf) {
            return new OpenContainer(buf.m_130242_(), buf.m_130242_(), buf.m_130238_(), new FriendlyByteBuf(Unpooled.wrappedBuffer((byte[])buf.m_130101_(32600))));
        }

        public static void handle(OpenContainer msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                try {
                    MenuScreens.getScreenFactory(msg.getType(), (Minecraft)Minecraft.m_91087_(), (int)msg.getWindowId(), (Component)msg.getName()).ifPresent(f -> {
                        AbstractContainerMenu c = msg.getType().create(msg.getWindowId(), Minecraft.m_91087_().f_91074_.m_150109_(), msg.getAdditionalData());
                        Screen s = f.m_96214_(c, Minecraft.m_91087_().f_91074_.m_150109_(), msg.getName());
                        Minecraft.m_91087_().f_91074_.f_36096_ = ((MenuAccess)s).m_6262_();
                        Minecraft.m_91087_().m_91152_(s);
                    });
                }
                finally {
                    msg.getAdditionalData().release();
                }
            });
            ctx.get().setPacketHandled(true);
        }

        public final MenuType<?> getType() {
            return (MenuType)BuiltInRegistries.f_256818_.m_7942_(this.id);
        }

        public int getWindowId() {
            return this.windowId;
        }

        public Component getName() {
            return this.name;
        }

        public FriendlyByteBuf getAdditionalData() {
            return this.additionalData;
        }
    }

    public static class SpawnEntity {
        private final Entity entity;
        private final int typeId;
        private final int entityId;
        private final UUID uuid;
        private final double posX;
        private final double posY;
        private final double posZ;
        private final byte pitch;
        private final byte yaw;
        private final byte headYaw;
        private final int velX;
        private final int velY;
        private final int velZ;
        private final FriendlyByteBuf buf;

        SpawnEntity(Entity e) {
            this.entity = e;
            this.typeId = BuiltInRegistries.f_256780_.m_7447_((Object)e.m_6095_());
            this.entityId = e.m_19879_();
            this.uuid = e.m_20148_();
            this.posX = e.m_20185_();
            this.posY = e.m_20186_();
            this.posZ = e.m_20189_();
            this.pitch = (byte)Mth.m_14143_((float)(e.m_146909_() * 256.0f / 360.0f));
            this.yaw = (byte)Mth.m_14143_((float)(e.m_146908_() * 256.0f / 360.0f));
            this.headYaw = (byte)(e.m_6080_() * 256.0f / 360.0f);
            Vec3 vec3d = e.m_20184_();
            double d1 = Mth.m_14008_((double)vec3d.f_82479_, (double)-3.9, (double)3.9);
            double d2 = Mth.m_14008_((double)vec3d.f_82480_, (double)-3.9, (double)3.9);
            double d3 = Mth.m_14008_((double)vec3d.f_82481_, (double)-3.9, (double)3.9);
            this.velX = (int)(d1 * 8000.0);
            this.velY = (int)(d2 * 8000.0);
            this.velZ = (int)(d3 * 8000.0);
            this.buf = null;
        }

        private SpawnEntity(int typeId, int entityId, UUID uuid, double posX, double posY, double posZ, byte pitch, byte yaw, byte headYaw, int velX, int velY, int velZ, FriendlyByteBuf buf) {
            this.entity = null;
            this.typeId = typeId;
            this.entityId = entityId;
            this.uuid = uuid;
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.pitch = pitch;
            this.yaw = yaw;
            this.headYaw = headYaw;
            this.velX = velX;
            this.velY = velY;
            this.velZ = velZ;
            this.buf = buf;
        }

        public static void encode(SpawnEntity msg, FriendlyByteBuf buf) {
            buf.m_130130_(msg.typeId);
            buf.writeInt(msg.entityId);
            buf.writeLong(msg.uuid.getMostSignificantBits());
            buf.writeLong(msg.uuid.getLeastSignificantBits());
            buf.writeDouble(msg.posX);
            buf.writeDouble(msg.posY);
            buf.writeDouble(msg.posZ);
            buf.writeByte((int)msg.pitch);
            buf.writeByte((int)msg.yaw);
            buf.writeByte((int)msg.headYaw);
            buf.writeShort(msg.velX);
            buf.writeShort(msg.velY);
            buf.writeShort(msg.velZ);
            Entity entity = msg.entity;
            if (entity instanceof IEntityAdditionalSpawnData) {
                IEntityAdditionalSpawnData entityAdditionalSpawnData = (IEntityAdditionalSpawnData)entity;
                FriendlyByteBuf spawnDataBuffer = new FriendlyByteBuf(Unpooled.buffer());
                entityAdditionalSpawnData.writeSpawnData(spawnDataBuffer);
                buf.m_130130_(spawnDataBuffer.readableBytes());
                buf.writeBytes((ByteBuf)spawnDataBuffer);
                spawnDataBuffer.release();
            } else {
                buf.m_130130_(0);
            }
        }

        public static SpawnEntity decode(FriendlyByteBuf buf) {
            return new SpawnEntity(buf.m_130242_(), buf.readInt(), new UUID(buf.readLong(), buf.readLong()), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readByte(), buf.readByte(), buf.readByte(), buf.readShort(), buf.readShort(), buf.readShort(), SpawnEntity.readSpawnDataPacket(buf));
        }

        private static FriendlyByteBuf readSpawnDataPacket(FriendlyByteBuf buf) {
            int count = buf.m_130242_();
            if (count > 0) {
                FriendlyByteBuf spawnDataBuffer = new FriendlyByteBuf(Unpooled.buffer());
                spawnDataBuffer.writeBytes((ByteBuf)buf, count);
                return spawnDataBuffer;
            }
            return new FriendlyByteBuf(Unpooled.buffer());
        }

        public static void handle(SpawnEntity msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                try {
                    EntityType type = (EntityType)BuiltInRegistries.f_256780_.m_7942_(msg.typeId);
                    Optional<Level> world = LogicalSidedProvider.CLIENTWORLD.get(((NetworkEvent.Context)ctx.get()).getDirection().getReceptionSide());
                    Entity e = world.map(w -> type.customClientSpawn(msg, w)).orElse(null);
                    if (e == null) {
                        return;
                    }
                    e.m_217006_(msg.posX, msg.posY, msg.posZ);
                    e.m_19890_(msg.posX, msg.posY, msg.posZ, (float)(msg.yaw * 360) / 256.0f, (float)(msg.pitch * 360) / 256.0f);
                    e.m_5616_((float)(msg.headYaw * 360) / 256.0f);
                    e.m_5618_((float)(msg.headYaw * 360) / 256.0f);
                    e.m_20234_(msg.entityId);
                    e.m_20084_(msg.uuid);
                    world.filter(ClientLevel.class::isInstance).ifPresent(w -> ((ClientLevel)w).m_104627_(msg.entityId, e));
                    e.m_6001_((double)msg.velX / 8000.0, (double)msg.velY / 8000.0, (double)msg.velZ / 8000.0);
                    if (e instanceof IEntityAdditionalSpawnData) {
                        IEntityAdditionalSpawnData entityAdditionalSpawnData = (IEntityAdditionalSpawnData)e;
                        entityAdditionalSpawnData.readSpawnData(msg.buf);
                    }
                }
                finally {
                    msg.buf.release();
                }
            });
            ctx.get().setPacketHandled(true);
        }

        public Entity getEntity() {
            return this.entity;
        }

        public int getTypeId() {
            return this.typeId;
        }

        public int getEntityId() {
            return this.entityId;
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public double getPosX() {
            return this.posX;
        }

        public double getPosY() {
            return this.posY;
        }

        public double getPosZ() {
            return this.posZ;
        }

        public byte getPitch() {
            return this.pitch;
        }

        public byte getYaw() {
            return this.yaw;
        }

        public byte getHeadYaw() {
            return this.headYaw;
        }

        public int getVelX() {
            return this.velX;
        }

        public int getVelY() {
            return this.velY;
        }

        public int getVelZ() {
            return this.velZ;
        }

        public FriendlyByteBuf getAdditionalData() {
            return this.buf;
        }
    }
}

