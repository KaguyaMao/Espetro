/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.unit.FixedNumberUnit
 *  dev.latvian.mods.unit.Unit
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.client.painter;

import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObject;
import dev.latvian.mods.kubejs.client.painter.screen.ScreenPainterObject;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.Nullable;

public class PainterObjectStorage {
    private static final ScreenPainterObject[] NO_SCREEN_OBJECTS = new ScreenPainterObject[0];
    public final Painter painter;
    private final Map<String, PainterObject> objects = new LinkedHashMap<String, PainterObject>();

    public PainterObjectStorage(Painter p) {
        this.painter = p;
    }

    @Nullable
    public PainterObject getObject(String key) {
        return this.objects.get(key);
    }

    public Collection<PainterObject> getObjects() {
        return this.objects.isEmpty() ? List.of() : this.objects.values();
    }

    public void handle(CompoundTag root) {
        CompoundTag tag;
        if (root.m_128441_("bulk")) {
            ListTag bulk = root.m_128437_("bulk", 10);
            for (int i = 0; i < bulk.size(); ++i) {
                this.handle(bulk.m_128728_(i));
            }
            return;
        }
        Object i = root.m_128423_("*");
        if (i instanceof CompoundTag) {
            tag = (CompoundTag)i;
            if (tag.m_128471_("remove")) {
                this.objects.clear();
            } else {
                i = this.objects.values().iterator();
                while (i.hasNext()) {
                    PainterObject o = (PainterObject)i.next();
                    o.update(tag);
                }
            }
        }
        if ((i = root.m_128423_("$")) instanceof CompoundTag) {
            tag = (CompoundTag)i;
            for (String k : tag.m_128431_()) {
                if (tag.m_128425_(k, 99)) {
                    this.painter.setVariable(k, (Unit)FixedNumberUnit.of((double)tag.m_128457_(k)));
                    continue;
                }
                this.painter.setVariable(k, this.painter.unitOf(ConsoleJS.CLIENT, (Object)tag.m_128423_(k)));
            }
        }
        for (String key : root.m_128431_()) {
            if (key.equals("*") || key.equals("$")) continue;
            CompoundTag tag2 = root.m_128469_(key);
            PainterObject o = this.objects.get(key);
            if (o != null) {
                o.update(tag2);
                continue;
            }
            if (key.indexOf(32) != -1) {
                ConsoleJS.CLIENT.error("Painter id can't contain spaces!");
                continue;
            }
            String type = tag2.m_128461_("type");
            PainterObject o1 = this.painter.make(type);
            if (o1 != null) {
                o1.id = key;
                o1.parent = this;
                o1.update(tag2);
                this.objects.put(key, o1);
                continue;
            }
            ConsoleJS.CLIENT.error("Unknown Painter type: " + type);
        }
    }

    public void clear() {
        this.objects.clear();
    }

    public ScreenPainterObject[] createScreenObjects() {
        return this.objects.isEmpty() ? NO_SCREEN_OBJECTS : (ScreenPainterObject[])this.objects.values().stream().filter(o -> o instanceof ScreenPainterObject).map(o -> (ScreenPainterObject)o).toArray(ScreenPainterObject[]::new);
    }

    public void remove(String id) {
        this.objects.remove(id);
    }
}

