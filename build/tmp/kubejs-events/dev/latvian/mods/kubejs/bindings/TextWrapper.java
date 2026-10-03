/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  dev.latvian.mods.rhino.mod.wrapper.ColorWrapper
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.ComponentContents
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.contents.ScoreContents
 *  net.minecraft.network.chat.contents.SelectorContents
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.bindings;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.JSObjectType;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

@Info(value="The hub for all things text components. Format text to your hearts content!")
public class TextWrapper {
    @Info(value="Returns a Component of the input")
    public static MutableComponent of(@Nullable Object o) {
        Map map;
        if ((o = UtilsJS.wrap(o, JSObjectType.ANY)) == null) {
            return Component.m_237113_((String)"null");
        }
        if (o instanceof MutableComponent) {
            MutableComponent component = (MutableComponent)o;
            return component;
        }
        if (o instanceof Component) {
            Component component = (Component)o;
            return component.m_6881_();
        }
        if (o instanceof CharSequence || o instanceof Number || o instanceof Character) {
            return TextWrapper.ofString(o.toString());
        }
        if (o instanceof Enum) {
            Enum e = (Enum)o;
            return TextWrapper.ofString(e.name());
        }
        if (o instanceof StringTag) {
            StringTag tag = (StringTag)o;
            String s = tag.m_7916_();
            if (s.startsWith("{") && s.endsWith("}")) {
                try {
                    return Component.Serializer.m_130701_((String)s);
                }
                catch (JsonParseException ex) {
                    return Component.m_237113_((String)("Error: " + String.valueOf((Object)ex)));
                }
            }
            return TextWrapper.ofString(s);
        }
        if (o instanceof Map && ((map = (Map)o).containsKey("text") || map.containsKey("translate"))) {
            Object a;
            MutableComponent text;
            if (map.containsKey("text")) {
                text = TextWrapper.ofString(map.get("text").toString());
            } else {
                Object[] with;
                Object v = map.get("with");
                if (v instanceof Collection) {
                    a = (Collection)v;
                    with = new Object[a.size()];
                    int i = 0;
                    Iterator iterator = a.iterator();
                    while (iterator.hasNext()) {
                        Object e1 = iterator.next();
                        with[i] = e1;
                        if (with[i] instanceof MapJS || with[i] instanceof ListJS) {
                            with[i] = TextWrapper.of(e1);
                        }
                        ++i;
                    }
                } else {
                    with = new Object[]{};
                }
                text = Component.m_237110_((String)map.get("translate").toString(), (Object[])with);
            }
            if (map.containsKey("color")) {
                text.kjs$color(ColorWrapper.of(map.get("color")));
            }
            text.kjs$bold((Boolean)map.getOrDefault("bold", null));
            text.kjs$italic((Boolean)map.getOrDefault("italic", null));
            text.kjs$underlined((Boolean)map.getOrDefault("underlined", null));
            text.kjs$strikethrough((Boolean)map.getOrDefault("strikethrough", null));
            text.kjs$obfuscated((Boolean)map.getOrDefault("obfuscated", null));
            text.kjs$insertion((String)map.getOrDefault("insertion", null));
            text.kjs$font(map.containsKey("font") ? new ResourceLocation(map.get("font").toString()) : null);
            text.kjs$click(map.containsKey("click") ? TextWrapper.clickEventOf(map.get("click")) : null);
            text.kjs$hover(map.containsKey("hover") ? TextWrapper.of(map.get("hover")) : null);
            a = map.get("extra");
            if (a instanceof Iterable) {
                Iterable itr = (Iterable)a;
                for (Object e : itr) {
                    text.m_7220_((Component)TextWrapper.of(e));
                }
            }
            return text;
        }
        if (o instanceof Iterable) {
            Iterable list = (Iterable)o;
            MutableComponent text = Component.m_237119_();
            for (Object e1 : list) {
                text.m_7220_((Component)TextWrapper.of(e1));
            }
            return text;
        }
        return TextWrapper.ofString(o.toString());
    }

    @Info(value="Returns a plain component of the string, or empty if it is an empty string")
    public static MutableComponent ofString(String s) {
        return s.isEmpty() ? Component.m_237119_() : Component.m_237113_((String)s);
    }

    @Info(value="Checks if the passed in component, and all its children are empty")
    public static boolean isEmpty(Component component) {
        return component.m_214077_() == ComponentContents.f_237124_ && component.m_7360_().isEmpty();
    }

    @Info(value="Returns a ClickEvent of the input")
    public static ClickEvent clickEventOf(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof ClickEvent) {
            ClickEvent ce = (ClickEvent)o;
            return ce;
        }
        JsonObject json = MapJS.json(o);
        if (json != null) {
            String action = GsonHelper.m_13906_((JsonObject)json, (String)"action");
            String value = GsonHelper.m_13906_((JsonObject)json, (String)"value");
            return new ClickEvent(Objects.requireNonNull(ClickEvent.Action.m_130645_((String)action), "Invalid click event action %s!".formatted(action)), value);
        }
        String s = o.toString();
        String[] split = s.split(":", 2);
        return switch (split[0]) {
            case "command" -> new ClickEvent(ClickEvent.Action.RUN_COMMAND, split[1]);
            case "suggest_command" -> new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, split[1]);
            case "copy" -> new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, split[1]);
            case "file" -> new ClickEvent(ClickEvent.Action.OPEN_FILE, split[1]);
            default -> {
                ClickEvent.Action action = ClickEvent.Action.m_130645_((String)split[0]);
                if (action != null) {
                    yield new ClickEvent(action, split[1]);
                }
                yield new ClickEvent(ClickEvent.Action.OPEN_URL, s);
            }
        };
    }

    @Info(value="Returns a colorful representation of the input nbt. Useful for displaying NBT to the player")
    public static Component prettyPrintNbt(Tag tag) {
        return NbtUtils.m_178061_((Tag)tag);
    }

    @Info(value="Joins all components in the list with the separator component")
    public static MutableComponent join(MutableComponent separator, Iterable<? extends Component> texts) {
        MutableComponent joined = Component.m_237119_();
        boolean first = true;
        for (Component component : texts) {
            if (first) {
                first = false;
            } else if (!TextWrapper.isEmpty((Component)separator)) {
                joined.m_7220_((Component)separator);
            }
            joined.m_7220_(component);
        }
        return joined;
    }

    @Info(value="Returns an empty component")
    public static MutableComponent empty() {
        return Component.m_237119_();
    }

    @Info(value="Joins all components")
    public static MutableComponent join(Component ... texts) {
        return TextWrapper.join(Component.m_237119_(), Arrays.asList(texts));
    }

    @Info(value="Returns a plain component of the passed in string, even if empty")
    public static MutableComponent string(String text) {
        return Component.m_237113_((String)text);
    }

    @Info(value="Returns a plain component of the input")
    public static MutableComponent literal(String text) {
        return Component.m_237113_((String)text);
    }

    @Info(value="Returns a translatable component of the input key")
    public static MutableComponent translate(String key) {
        return Component.m_237115_((String)key);
    }

    @Info(value="Returns a translatable component of the input key, with args of the objects")
    public static MutableComponent translate(String key, Object ... objects) {
        return Component.m_237110_((String)key, (Object[])objects);
    }

    @Info(value="Returns a translatable component of the input key")
    public static MutableComponent translatable(String key) {
        return Component.m_237115_((String)key);
    }

    @Info(value="Returns a translatable component of the input key, with args of the objects")
    public static MutableComponent translatable(String key, Object ... objects) {
        return Component.m_237110_((String)key, (Object[])objects);
    }

    @Info(value="Returns a keybinding component of the input keybinding descriptor")
    public static MutableComponent keybind(String keybind) {
        return Component.m_237117_((String)keybind);
    }

    @Info(value="Returns a score component of the input objective, for the provided selector")
    public static MutableComponent score(String selector, String objective) {
        return MutableComponent.m_237204_((ComponentContents)new ScoreContents(selector, objective));
    }

    @Info(value="Returns a component displaying all entities matching the input selector")
    public static MutableComponent selector(String selector) {
        return MutableComponent.m_237204_((ComponentContents)new SelectorContents(selector, Optional.empty()));
    }

    @Info(value="Returns a component displaying all entities matching the input selector, with a custom separator")
    public static MutableComponent selector(String selector, Component separator) {
        return MutableComponent.m_237204_((ComponentContents)new SelectorContents(selector, Optional.of(separator)));
    }

    @Info(value="Returns a component of the input, colored black")
    public static MutableComponent black(Object text) {
        return TextWrapper.of(text).kjs$black();
    }

    @Info(value="Returns a component of the input, colored dark blue")
    public static MutableComponent darkBlue(Object text) {
        return TextWrapper.of(text).kjs$darkBlue();
    }

    @Info(value="Returns a component of the input, colored dark green")
    public static MutableComponent darkGreen(Object text) {
        return TextWrapper.of(text).kjs$darkGreen();
    }

    @Info(value="Returns a component of the input, colored dark aqua")
    public static MutableComponent darkAqua(Object text) {
        return TextWrapper.of(text).kjs$darkAqua();
    }

    @Info(value="Returns a component of the input, colored dark red")
    public static MutableComponent darkRed(Object text) {
        return TextWrapper.of(text).kjs$darkRed();
    }

    @Info(value="Returns a component of the input, colored dark purple")
    public static MutableComponent darkPurple(Object text) {
        return TextWrapper.of(text).kjs$darkPurple();
    }

    @Info(value="Returns a component of the input, colored gold")
    public static MutableComponent gold(Object text) {
        return TextWrapper.of(text).kjs$gold();
    }

    @Info(value="Returns a component of the input, colored gray")
    public static MutableComponent gray(Object text) {
        return TextWrapper.of(text).kjs$gray();
    }

    @Info(value="Returns a component of the input, colored dark gray")
    public static MutableComponent darkGray(Object text) {
        return TextWrapper.of(text).kjs$darkGray();
    }

    @Info(value="Returns a component of the input, colored blue")
    public static MutableComponent blue(Object text) {
        return TextWrapper.of(text).kjs$blue();
    }

    @Info(value="Returns a component of the input, colored green")
    public static MutableComponent green(Object text) {
        return TextWrapper.of(text).kjs$green();
    }

    @Info(value="Returns a component of the input, colored aqua")
    public static MutableComponent aqua(Object text) {
        return TextWrapper.of(text).kjs$aqua();
    }

    @Info(value="Returns a component of the input, colored red")
    public static MutableComponent red(Object text) {
        return TextWrapper.of(text).kjs$red();
    }

    @Info(value="Returns a component of the input, colored light purple")
    public static MutableComponent lightPurple(Object text) {
        return TextWrapper.of(text).kjs$lightPurple();
    }

    @Info(value="Returns a component of the input, colored yellow")
    public static MutableComponent yellow(Object text) {
        return TextWrapper.of(text).kjs$yellow();
    }

    @Info(value="Returns a component of the input, colored white")
    public static MutableComponent white(Object text) {
        return TextWrapper.of(text).kjs$white();
    }
}

