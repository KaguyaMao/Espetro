/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 */
package cc.sighs.auratip.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.Component;

public final class ComponentSerialization {
    public static final Codec<Component> COMPONENT_CODEC = Codec.PASSTHROUGH.xmap(dynamic -> {
        JsonElement element = (JsonElement)dynamic.convert((DynamicOps)JsonOps.INSTANCE).getValue();
        return Component.Serializer.m_130691_((JsonElement)element);
    }, component -> new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)Component.Serializer.m_130716_((Component)component)));

    private ComponentSerialization() {
    }

    public record Divider(int thickness, int marginTop, int marginBottom, float length, String color) {
        public static final Codec<Divider> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Codec.INT.optionalFieldOf("thickness", (Object)1).forGetter(Divider::thickness), (App)Codec.INT.optionalFieldOf("margin_top", (Object)4).forGetter(Divider::marginTop), (App)Codec.INT.optionalFieldOf("margin_bottom", (Object)4).forGetter(Divider::marginBottom), (App)Codec.FLOAT.optionalFieldOf("length", (Object)Float.valueOf(1.0f)).forGetter(Divider::length), (App)Codec.STRING.optionalFieldOf("color").xmap(opt -> opt.orElse(""), value -> value == null || value.isBlank() ? Optional.empty() : Optional.of(value)).forGetter(Divider::color)).apply((Applicative)inst, Divider::new));

        public Divider {
            if (thickness <= 0) {
                thickness = 1;
            }
            if (marginTop < 0) {
                marginTop = 0;
            }
            if (marginBottom < 0) {
                marginBottom = 0;
            }
            if (length <= 0.0f) {
                length = 1.0f;
            }
            if (color == null) {
                color = "";
            }
        }
    }

    public record TextElement(Component text, float scale, int lineSpacing, Optional<Divider> divider) {
        public static final Codec<TextElement> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)COMPONENT_CODEC.fieldOf("text").forGetter(TextElement::text), (App)Codec.FLOAT.optionalFieldOf("scale", (Object)Float.valueOf(1.0f)).forGetter(TextElement::scale), (App)Codec.INT.optionalFieldOf("line_spacing", (Object)0).forGetter(TextElement::lineSpacing), (App)Divider.CODEC.optionalFieldOf("divider").forGetter(TextElement::divider)).apply((Applicative)inst, TextElement::new));

        public TextElement {
            if (scale <= 0.0f) {
                scale = 1.0f;
            }
            if (lineSpacing < 0) {
                lineSpacing = 0;
            }
        }
    }
}

