/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.logistics.resupply;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record ResupplySourceRef(Kind kind, BlockPos blockPos, UUID entityId) {
    public ResupplySourceRef {
        kind = Objects.requireNonNull(kind, "kind");
        blockPos = blockPos == null ? BlockPos.f_121853_ : blockPos.m_7949_();
        entityId = entityId == null ? new UUID(0L, 0L) : entityId;
    }

    public static ResupplySourceRef radio(BlockPos pos) {
        return new ResupplySourceRef(Kind.RADIO, pos, null);
    }

    public static ResupplySourceRef vehicle(UUID id) {
        return new ResupplySourceRef(Kind.VEHICLE, BlockPos.f_121853_, id);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130068_(this.kind);
        if (this.kind == Kind.RADIO) {
            buf.m_130064_(this.blockPos);
        } else {
            buf.m_130077_(this.entityId);
        }
    }

    public static ResupplySourceRef read(FriendlyByteBuf buf) {
        Kind kind = buf.m_130066_(Kind.class);
        return kind == Kind.RADIO ? ResupplySourceRef.radio(buf.m_130135_()) : ResupplySourceRef.vehicle(buf.m_130259_());
    }

    public static enum Kind {
        RADIO,
        VEHICLE;

    }
}

