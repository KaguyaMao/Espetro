/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.api.radiamenu.icon;

import cc.sighs.auratip.api.radiamenu.icon.TextureIcon;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public interface IRadialIcon {
    public static final Map<ResourceLocation, Codec<? extends IRadialIcon>> BY_ID = new ConcurrentHashMap<ResourceLocation, Codec<? extends IRadialIcon>>();
    public static final Map<Class<?>, ResourceLocation> ID_BY_CLASS = new ConcurrentHashMap();
    public static final Codec<IRadialIcon> CODEC = new Codec<IRadialIcon>(){

        public <T> DataResult<Pair<IRadialIcon, T>> decode(DynamicOps<T> ops, T input) {
            DataResult stringResult = ops.getStringValue(input);
            if (stringResult.result().isPresent()) {
                return ResourceLocation.f_135803_.decode(ops, input).map(p -> p.mapFirst(loc -> new TextureIcon((ResourceLocation)loc, 1.0f)));
            }
            DataResult typeVal = ops.get(input, "type");
            if (typeVal.result().isEmpty()) {
                return DataResult.error(() -> "Cannot decode IRadialIcon: expected a ResourceLocation string or an object with a 'type' field");
            }
            DataResult typeResult = ops.getStringValue(typeVal.result().get());
            if (typeResult.result().isEmpty()) {
                return DataResult.error(() -> "IRadialIcon 'type' field must be a string");
            }
            String typeStr = (String)typeResult.result().get();
            ResourceLocation typeId = ResourceLocation.m_135820_((String)typeStr);
            if (typeId == null) {
                return DataResult.error(() -> "Invalid IRadialIcon type: '" + typeStr + "'. Must be a ResourceLocation like 'modid:path'.");
            }
            Codec<? extends IRadialIcon> codec = BY_ID.get(typeId);
            if (codec == null) {
                return DataResult.error(() -> "Unknown IRadialIcon type: '" + typeStr + "'. Registered types: " + String.valueOf(BY_ID.keySet()));
            }
            return codec.decode(ops, input);
        }

        public <T> DataResult<T> encode(IRadialIcon input, DynamicOps<T> ops, T prefix) {
            if (input instanceof TextureIcon) {
                TextureIcon ti = (TextureIcon)input;
                if (ti.scale() == 1.0f) {
                    return ResourceLocation.f_135803_.encode((Object)ti.id(), ops, prefix);
                }
                return TextureIcon.CODEC.encode((Object)ti, ops, prefix);
            }
            ResourceLocation typeId = input.type();
            Codec<? extends IRadialIcon> codec = BY_ID.get(typeId);
            if (codec == null) {
                return DataResult.error(() -> "Unregistered IRadialIcon type: " + String.valueOf(typeId));
            }
            return codec.encode((Object)input, ops, prefix);
        }
    };

    public void render(GuiGraphics var1, int var2, int var3, float var4, float var5);

    public Codec<? extends IRadialIcon> codec();

    public static <T extends IRadialIcon> void register(ResourceLocation type, Class<T> clazz, Codec<T> codec) {
        BY_ID.put(type, codec);
        ID_BY_CLASS.put(clazz, type);
    }

    default public ResourceLocation type() {
        ResourceLocation id = ID_BY_CLASS.get(this.getClass());
        if (id != null) {
            return id;
        }
        throw new IllegalStateException("Unregistered IRadialIcon: " + this.getClass().getName());
    }
}

