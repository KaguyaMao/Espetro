/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.data.api.DataDriven
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.data;

import cc.sighs.auratip.api.radiamenu.icon.IRadialIcon;
import cc.sighs.auratip.data.action.Action;
import cc.sighs.auratip.data.validator.RadialMenuDataValidator;
import cc.sighs.auratip.util.ComponentSerialization;
import cc.sighs.oelib.data.api.DataDriven;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@DataDriven(modid="auratip", folder="radial_menu", syncToClient=true, supportArray=true, validator=RadialMenuDataValidator.class)
public record RadialMenuData(ResourceLocation id, MenuSettings menuSettings, List<Slot> slots) {
    public static final Codec<RadialMenuData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ResourceLocation.f_135803_.fieldOf("id").forGetter(RadialMenuData::id), (App)MenuSettings.CODEC.fieldOf("menu_settings").forGetter(RadialMenuData::menuSettings), (App)Slot.CODEC.listOf().fieldOf("slots").forGetter(RadialMenuData::slots)).apply((Applicative)instance, RadialMenuData::new));

    public RadialMenuData {
        id = Objects.requireNonNull(id, "id");
        menuSettings = Objects.requireNonNull(menuSettings, "menuSettings");
        slots = List.copyOf((Collection)Objects.requireNonNull(slots, "slots"));
    }

    public record MenuSettings(int innerRadius, int outerRadius, float animationSpeed, Optional<ResourceLocation> centerIcon, Optional<String> ringColor, Optional<List<String>> ringColors, Optional<String> closeKey) {
        public static final Codec<MenuSettings> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Codec.INT.fieldOf("inner_radius").forGetter(MenuSettings::innerRadius), (App)Codec.INT.fieldOf("outer_radius").forGetter(MenuSettings::outerRadius), (App)Codec.FLOAT.fieldOf("animation_speed").forGetter(MenuSettings::animationSpeed), (App)ResourceLocation.f_135803_.optionalFieldOf("center_icon").forGetter(MenuSettings::centerIcon), (App)Codec.STRING.optionalFieldOf("ring_color").forGetter(MenuSettings::ringColor), (App)Codec.list((Codec)Codec.STRING).optionalFieldOf("ring_colors").forGetter(MenuSettings::ringColors), (App)Codec.STRING.optionalFieldOf("close_key").forGetter(MenuSettings::closeKey)).apply((Applicative)inst, MenuSettings::new));

        public MenuSettings {
            centerIcon = centerIcon == null ? Optional.empty() : centerIcon;
            ringColor = ringColor == null ? Optional.empty() : ringColor;
            ringColors = ringColors == null ? Optional.empty() : ringColors.map(List::copyOf);
            closeKey = closeKey == null ? Optional.empty() : closeKey;
        }
    }

    public record Slot(String name, IRadialIcon icon, Action action, Optional<Component> text, Optional<String> highlightColor, boolean closeAfterAction, Optional<String> baseColor) {
        public static final Codec<Slot> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Codec.STRING.fieldOf("name").forGetter(Slot::name), (App)IRadialIcon.CODEC.fieldOf("icon").forGetter(Slot::icon), (App)Action.CODEC.fieldOf("action").forGetter(Slot::action), (App)ComponentSerialization.COMPONENT_CODEC.optionalFieldOf("text").forGetter(Slot::text), (App)Codec.STRING.optionalFieldOf("highlight_color").forGetter(Slot::highlightColor), (App)Codec.BOOL.optionalFieldOf("close_after_action", (Object)true).forGetter(Slot::closeAfterAction), (App)Codec.STRING.optionalFieldOf("base_color").forGetter(Slot::baseColor)).apply((Applicative)inst, Slot::new));

        public Slot {
            name = Objects.requireNonNull(name, "name");
            icon = Objects.requireNonNull(icon, "icon");
            action = Objects.requireNonNull(action, "action");
            text = text == null ? Optional.empty() : text;
            highlightColor = highlightColor == null ? Optional.empty() : highlightColor;
            baseColor = baseColor == null ? Optional.empty() : baseColor;
        }

        public Slot(String name, IRadialIcon icon, Action action, Optional<Component> text, Optional<String> highlightColor) {
            this(name, icon, action, text, highlightColor, true, Optional.empty());
        }
    }
}

