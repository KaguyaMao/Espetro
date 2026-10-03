/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleOptions$Deserializer
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.registries.ForgeRegistries
 */
package tech.vvp.vvp.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;
import tech.vvp.vvp.init.ModParticleTypes;

public record VvpMuzzleParticleOption(int color, int life, float fade, int animationSpeed, float baseScale, float targetScale, int frameCount, int layer, boolean lingerSmoke, int movementDuration, int attachVehicleId, int attachSeatIndex) implements ParticleOptions
{
    public static final int NO_ATTACH = -1;
    public static final int LAYER_SMOKE = 0;
    public static final int LAYER_BANG_STATIC = 1;
    public static final int LAYER_BANG_SPARK = 2;
    public static final int LAYER_BLOOM = 3;
    public static final Codec<VvpMuzzleParticleOption> CODEC = RecordCodecBuilder.create(builder -> builder.group((App)Codec.INT.fieldOf("color").forGetter(VvpMuzzleParticleOption::color), (App)Codec.INT.fieldOf("life").forGetter(VvpMuzzleParticleOption::life), (App)Codec.FLOAT.fieldOf("fade").forGetter(VvpMuzzleParticleOption::fade), (App)Codec.INT.fieldOf("animationSpeed").forGetter(VvpMuzzleParticleOption::animationSpeed), (App)Codec.FLOAT.fieldOf("baseScale").forGetter(VvpMuzzleParticleOption::baseScale), (App)Codec.FLOAT.fieldOf("targetScale").forGetter(VvpMuzzleParticleOption::targetScale), (App)Codec.INT.fieldOf("frameCount").forGetter(VvpMuzzleParticleOption::frameCount), (App)Codec.INT.fieldOf("layer").forGetter(VvpMuzzleParticleOption::layer), (App)Codec.BOOL.fieldOf("lingerSmoke").forGetter(VvpMuzzleParticleOption::lingerSmoke), (App)Codec.INT.optionalFieldOf("movementDuration", (Object)0).forGetter(VvpMuzzleParticleOption::movementDuration), (App)Codec.INT.optionalFieldOf("attachVehicleId", (Object)-1).forGetter(VvpMuzzleParticleOption::attachVehicleId), (App)Codec.INT.optionalFieldOf("attachSeatIndex", (Object)0).forGetter(VvpMuzzleParticleOption::attachSeatIndex)).apply((Applicative)builder, VvpMuzzleParticleOption::new));
    public static final ParticleOptions.Deserializer<VvpMuzzleParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<VvpMuzzleParticleOption>(){

        public VvpMuzzleParticleOption fromCommand(ParticleType<VvpMuzzleParticleOption> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int color = reader.readInt();
            reader.expect(' ');
            int life = reader.readInt();
            reader.expect(' ');
            float fade = reader.readFloat();
            reader.expect(' ');
            int animationSpeed = reader.readInt();
            reader.expect(' ');
            float baseScale = reader.readFloat();
            reader.expect(' ');
            float targetScale = reader.readFloat();
            reader.expect(' ');
            int frameCount = reader.readInt();
            reader.expect(' ');
            int layer = reader.readInt();
            reader.expect(' ');
            boolean lingerSmoke = reader.readBoolean();
            reader.expect(' ');
            int movementDuration = reader.readInt();
            reader.expect(' ');
            int attachVehicleId = reader.readInt();
            reader.expect(' ');
            int attachSeatIndex = reader.readInt();
            return new VvpMuzzleParticleOption(color, life, fade, animationSpeed, baseScale, targetScale, frameCount, layer, lingerSmoke, movementDuration, attachVehicleId, attachSeatIndex);
        }

        public VvpMuzzleParticleOption fromNetwork(ParticleType<VvpMuzzleParticleOption> type, FriendlyByteBuf buffer) {
            return new VvpMuzzleParticleOption(buffer.readInt(), buffer.readInt(), buffer.readFloat(), buffer.readInt(), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), buffer.readInt(), buffer.readBoolean(), buffer.m_130242_(), buffer.m_130242_(), buffer.m_130242_());
        }
    };

    public VvpMuzzleParticleOption(float r, float g, float b, int life, float fade, int animationSpeed, float baseScale, float targetScale, int frameCount, int layer) {
        this(r, g, b, life, fade, animationSpeed, baseScale, targetScale, frameCount, layer, false, 0);
    }

    public VvpMuzzleParticleOption(float r, float g, float b, int life, float fade, int animationSpeed, float baseScale, float targetScale, int frameCount, int layer, boolean lingerSmoke) {
        this(r, g, b, life, fade, animationSpeed, baseScale, targetScale, frameCount, layer, lingerSmoke, 0);
    }

    public VvpMuzzleParticleOption(float r, float g, float b, int life, float fade, int animationSpeed, float baseScale, float targetScale, int frameCount, int layer, boolean lingerSmoke, int movementDuration) {
        this((int)(r * 255.0f) << 16 | (int)(g * 255.0f) << 8 | (int)(b * 255.0f), life, fade, animationSpeed, baseScale, targetScale, frameCount, layer, lingerSmoke, movementDuration, -1, 0);
    }

    public boolean hasBarrelAttach() {
        return this.attachVehicleId >= 0 && this.layer == 0 && this.lingerSmoke;
    }

    public VvpMuzzleParticleOption withBarrelAttach(int vehicleId, int seatIndex) {
        return new VvpMuzzleParticleOption(this.color, this.life, this.fade, this.animationSpeed, this.baseScale, this.targetScale, this.frameCount, this.layer, this.lingerSmoke, this.movementDuration, vehicleId, seatIndex);
    }

    public float red() {
        return (float)(this.color >> 16 & 0xFF) / 255.0f;
    }

    public float green() {
        return (float)(this.color >> 8 & 0xFF) / 255.0f;
    }

    public float blue() {
        return (float)(this.color & 0xFF) / 255.0f;
    }

    public ParticleType<?> m_6012_() {
        return switch (this.layer) {
            case 3 -> (ParticleType)ModParticleTypes.MUZZLE_BLOOM.get();
            case 1, 2 -> (ParticleType)ModParticleTypes.MUZZLE_BANG.get();
            default -> (ParticleType)ModParticleTypes.MUZZLE_SMOKE.get();
        };
    }

    public void m_7711_(FriendlyByteBuf buffer) {
        buffer.writeInt(this.color);
        buffer.writeInt(this.life);
        buffer.writeFloat(this.fade);
        buffer.writeInt(this.animationSpeed);
        buffer.writeFloat(this.baseScale);
        buffer.writeFloat(this.targetScale);
        buffer.writeInt(this.frameCount);
        buffer.writeInt(this.layer);
        buffer.writeBoolean(this.lingerSmoke);
        buffer.m_130130_(this.movementDuration);
        buffer.m_130130_(this.attachVehicleId);
        buffer.m_130130_(this.attachSeatIndex);
    }

    public String m_5942_() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this.m_6012_()) + " [" + this.color + ", " + this.life + ", " + this.fade + ", " + this.animationSpeed + ", " + this.baseScale + ", " + this.targetScale + ", " + this.frameCount + ", " + this.layer + ", " + this.lingerSmoke + ", " + this.movementDuration + ", " + this.attachVehicleId + ", " + this.attachSeatIndex + "]";
    }
}

