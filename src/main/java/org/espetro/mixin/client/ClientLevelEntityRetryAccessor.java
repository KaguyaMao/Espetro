package org.espetro.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 暴露 {@link ClientLevel} 私有的 {@code addEntity(int, Entity)}
 * （srg 名 m_104739_，原版 spawn 包处理路径），
 * 供 {@code ClientEntityRetryQueue} 在区块 FULL 后按原版路径补加实体。
 */
@Mixin(ClientLevel.class)
public interface ClientLevelEntityRetryAccessor {

    @Invoker("addEntity")
    void espetro$callAddEntity(int id, Entity entity);
}
