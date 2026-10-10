/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.BaseFunction
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.NativeJavaObject
 *  dev.latvian.mods.rhino.ScriptableObject
 *  dev.latvian.mods.rhino.mod.util.color.Color
 *  dev.latvian.mods.rhino.mod.util.color.SimpleColor
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.toasts.Toast
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.bindings.TextWrapper;
import dev.latvian.mods.kubejs.client.NotificationToast;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.ScriptableObject;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NotificationBuilder {
    public static final Component[] NO_TEXT = new Component[0];
    public static final Duration DEFAULT_DURATION = Duration.ofSeconds(5L);
    public static final Color DEFAULT_BORDER_COLOR = new SimpleColor(4663636);
    public static final Color DEFAULT_BACKGROUND_COLOR = new SimpleColor(2364213);
    private static final int FLAG_ICON = 1;
    private static final int FLAG_TEXT_SHADOW = 2;
    private static final int FLAG_DURATION = 4;
    public Duration duration;
    public Component text;
    public transient int iconType;
    public transient String icon;
    public int iconSize;
    public Color outlineColor;
    public Color borderColor;
    public Color backgroundColor;
    public boolean textShadow;

    public static NotificationBuilder of(Context cx, Object object) {
        if (object instanceof NotificationBuilder) {
            NotificationBuilder b = (NotificationBuilder)object;
            return b;
        }
        if (object instanceof Map) {
            Map map = (Map)object;
            return null;
        }
        if (object instanceof BaseFunction) {
            BaseFunction func = (BaseFunction)object;
            Consumer consumer = (Consumer)NativeJavaObject.createInterfaceAdapter((Context)cx, Consumer.class, (ScriptableObject)func);
            NotificationBuilder b = new NotificationBuilder();
            consumer.accept(b);
            return b;
        }
        NotificationBuilder b = new NotificationBuilder();
        b.text = TextWrapper.of(object);
        return b;
    }

    public static NotificationBuilder make(Consumer<NotificationBuilder> consumer) {
        NotificationBuilder b = new NotificationBuilder();
        consumer.accept(b);
        return b;
    }

    public NotificationBuilder() {
        this.duration = DEFAULT_DURATION;
        this.text = Component.m_237119_();
        this.iconType = 0;
        this.icon = "";
        this.iconSize = 16;
        this.outlineColor = SimpleColor.BLACK;
        this.borderColor = DEFAULT_BORDER_COLOR;
        this.backgroundColor = DEFAULT_BACKGROUND_COLOR;
        this.textShadow = true;
    }

    public NotificationBuilder(FriendlyByteBuf buf) {
        int flags = buf.m_130242_();
        this.text = buf.m_130238_();
        Duration duration = this.duration = (flags & 4) != 0 ? Duration.ofMillis(buf.m_130258_()) : DEFAULT_DURATION;
        if ((flags & 1) != 0) {
            this.iconType = buf.m_130242_();
            this.icon = buf.m_130277_();
            this.iconSize = buf.readByte();
        } else {
            this.iconType = 0;
            this.icon = "";
            this.iconSize = 16;
        }
        this.outlineColor = UtilsJS.readColor(buf);
        this.borderColor = UtilsJS.readColor(buf);
        this.backgroundColor = UtilsJS.readColor(buf);
        this.textShadow = (flags & 2) != 0;
    }

    public void write(FriendlyByteBuf buf) {
        int flags = 0;
        if (this.iconType != 0) {
            flags |= 1;
        }
        if (this.textShadow) {
            flags |= 2;
        }
        if (this.duration != DEFAULT_DURATION) {
            flags |= 4;
        }
        buf.m_130130_(flags);
        buf.m_130083_(this.text);
        if (this.duration != DEFAULT_DURATION) {
            buf.m_130103_(this.duration.toMillis());
        }
        if (this.iconType != 0) {
            buf.m_130130_(this.iconType);
            buf.m_130070_(this.icon);
            buf.writeByte(this.iconSize);
        }
        UtilsJS.writeColor(buf, this.outlineColor);
        UtilsJS.writeColor(buf, this.borderColor);
        UtilsJS.writeColor(buf, this.backgroundColor);
    }

    public void setIcon(String icon) {
        this.icon = icon;
        this.iconType = 1;
    }

    public void setItemIcon(ItemStack stack) {
        this.icon = stack.kjs$getId();
        if (stack.m_41613_() > 1) {
            this.icon = stack.m_41613_() + "x " + this.icon;
        }
        if (stack.m_41783_() != null) {
            this.icon = this.icon + " " + String.valueOf(stack.m_41783_());
        }
        this.iconType = 2;
    }

    public void setAtlasIcon(String icon) {
        this.icon = icon;
        this.iconType = 3;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void show() {
        Minecraft mc = Minecraft.m_91087_();
        mc.m_91300_().m_94922_((Toast)new NotificationToast(mc, this));
    }
}

