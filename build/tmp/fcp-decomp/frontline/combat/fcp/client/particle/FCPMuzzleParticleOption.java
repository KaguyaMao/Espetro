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
package frontline.combat.fcp.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import frontline.combat.fcp.init.ModParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

public record FCPMuzzleParticleOption(int color, int life, float fade, int animationSpeed, float baseScale, float targetScale, int frameCount, int layer, boolean lingerSmoke, int movementDuration, int attachVehicleId, int attachSeatIndex) implements ParticleOptions
{
    public static final int NO_ATTACH = -1;
    public static final int LAYER_SMOKE = 0;
    public static final int LAYER_BANG_STATIC = 1;
    public static final int LAYER_BANG_SPARK = 2;
    public static final int LAYER_BLOOM = 3;
    public static final Codec<FCPMuzzleParticleOption> CODEC = RecordCodecBuilder.create(builder -> builder.group((App)Codec.INT.fieldOf("color").forGetter(FCPMuzzleParticleOption::color), (App)Codec.INT.fieldOf("life").forGetter(FCPMuzzleParticleOption::life), (App)Codec.FLOAT.fieldOf("fade").forGetter(FCPMuzzleParticleOption::fade), (App)Codec.INT.fieldOf("animationSpeed").forGetter(FCPMuzzleParticleOption::animationSpeed), (App)Codec.FLOAT.fieldOf("baseScale").forGetter(FCPMuzzleParticleOption::baseScale), (App)Codec.FLOAT.fieldOf("targetScale").forGetter(FCPMuzzleParticleOption::targetScale), (App)Codec.INT.fieldOf("frameCount").forGetter(FCPMuzzleParticleOption::frameCount), (App)Codec.INT.fieldOf("layer").forGetter(FCPMuzzleParticleOption::layer), (App)Codec.BOOL.fieldOf("lingerSmoke").forGetter(FCPMuzzleParticleOption::lingerSmoke), (App)Codec.INT.optionalFieldOf("movementDuration", (Object)0).forGetter(FCPMuzzleParticleOption::movementDuration), (App)Codec.INT.optionalFieldOf("attachVehicleId", (Object)-1).forGetter(FCPMuzzleParticleOption::attachVehicleId), (App)Codec.INT.optionalFieldOf("attachSeatIndex", (Object)0).forGetter(FCPMuzzleParticleOption::attachSeatIndex)).apply((Applicative)builder, FCPMuzzleParticleOption::new));
    public static final ParticleOptions.Deserializer<FCPMuzzleParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<FCPMuzzleParticleOption>(){

        public FCPMuzzleParticleOption fromCommand(ParticleType<FCPMuzzleParticleOption> type, StringReader reader) throws CommandSyntaxException {
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
            return new FCPMuzzleParticleOption(color, life, fade, animationSpeed, baseScale, targetScale, frameCount, layer, lingerSmoke, movementDuration, attachVehicleId, attachSeatIndex);
        }

        public FCPMuzzleParticleOption fromNetwork(ParticleType<FCPMuzzleParticleOption> type, FriendlyByteBuf buffer) {
            return new FCPMuzzleParticleOption(buffer.readInt(), buffer.readInt(), buffer.readFloat(), buffer.readInt(), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), buffer.readInt(), buffer.readBoolean(), buffer.m_130242_(), buffer.m_130242_(), buffer.m_130242_());
        }
    };

    public FCPMuzzleParticleOption(float r, float g, float b, int life, float fade, int animationSpeed, float baseScale, float targetScale, int frameCount, int layer) {
        this(r, g, b, life, fade, animationSpeed, baseScale, targetScale, frameCount, layer, false, 0);
    }

    public FCPMuzzleParticleOption(float r, float g, float b, int life, float fade, int animationSpeed, float baseScale, float targetScale, int frameCount, int layer, boolean lingerSmoke) {
        this(r, g, b, life, fade, animationSpeed, baseScale, targetScale, frameCount, layer, lingerSmoke, 0);
    }

    public FCPMuzzleParticleOption(float r, float g, float b, int life, float fade, int animationSpeed, float baseScale, float targetScale, int frameCount, int layer, boolean lingerSmoke, int movementDuration) {
        this((int)(r * 255.0f) << 16 | (int)(g * 255.0f) << 8 | (int)(b * 255.0f), life, fade, animationSpeed, baseScale, targetScale, frameCount, layer, lingerSmoke, movementDuration, -1, 0);
    }

    public boolean hasBarrelAttach() {
        return this.attachVehicleId >= 0 && this.layer == 0 && this.lingerSmoke;
    }

    public FCPMuzzleParticleOption withBarrelAttach(int vehicleId, int seatIndex) {
        return new FCPMuzzleParticleOption(this.color, this.life, this.fade, this.animationSpeed, this.baseScale, this.targetScale, this.frameCount, this.layer, this.lingerSmoke, this.movementDuration, vehicleId, seatIndex);
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
        return String.valueOf(ForgeRegistries.PARTICLE_TYPES.getKey(this.m_6012_())) + " [" + this.color + ", " + this.life + ", " + this.fade + ", " + this.animationSpeed + ", " + this.baseScale + ", " + this.targetScale + ", " + this.frameCount + ", " + this.layer + ", " + this.lingerSmoke + ", " + this.movementDuration + ", " + this.attachVehicleId + ", " + this.attachSeatIndex + "]";
    }
}

