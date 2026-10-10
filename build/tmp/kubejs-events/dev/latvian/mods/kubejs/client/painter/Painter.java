/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  dev.latvian.mods.unit.FixedNumberUnit
 *  dev.latvian.mods.unit.MutableNumberUnit
 *  dev.latvian.mods.unit.Unit
 *  dev.latvian.mods.unit.UnitContext
 *  dev.latvian.mods.unit.UnitVariables
 *  dev.latvian.mods.unit.VariableSet
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.StringTag
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.client.painter;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.client.ClientEventJS;
import dev.latvian.mods.kubejs.client.painter.PainterFactory;
import dev.latvian.mods.kubejs.client.painter.PainterObject;
import dev.latvian.mods.kubejs.client.painter.PainterObjectStorage;
import dev.latvian.mods.kubejs.client.painter.screen.AtlasTextureObject;
import dev.latvian.mods.kubejs.client.painter.screen.GradientObject;
import dev.latvian.mods.kubejs.client.painter.screen.ItemObject;
import dev.latvian.mods.kubejs.client.painter.screen.LineObject;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenEventJS;
import dev.latvian.mods.kubejs.client.painter.screen.RectangleObject;
import dev.latvian.mods.kubejs.client.painter.screen.ScreenGroup;
import dev.latvian.mods.kubejs.client.painter.screen.ScreenPainterObject;
import dev.latvian.mods.kubejs.client.painter.screen.TextObject;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.MutableNumberUnit;
import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitContext;
import dev.latvian.mods.unit.UnitVariables;
import dev.latvian.mods.unit.VariableSet;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import org.jetbrains.annotations.Nullable;

public class Painter
implements UnitVariables {
    public static final Painter INSTANCE = new Painter("global");
    public final String id;
    private final Object lock;
    private final Map<String, PainterFactory> objectRegistry;
    private final PainterObjectStorage storage;
    private ScreenPainterObject[] screenObjects;
    public final UnitContext unitContext;
    private final VariableSet variables;
    public final MutableNumberUnit deltaUnit;
    public final MutableNumberUnit screenWidthUnit;
    public final MutableNumberUnit screenHeightUnit;
    public final MutableNumberUnit mouseXUnit;
    public final MutableNumberUnit mouseYUnit;
    public final MutableNumberUnit defaultLineSizeUnit;

    public Painter(String id) {
        this.id = id;
        this.lock = new Object();
        this.objectRegistry = new HashMap<String, PainterFactory>();
        this.storage = new PainterObjectStorage(this);
        this.screenObjects = null;
        this.unitContext = UnitContext.DEFAULT.sub();
        this.variables = new VariableSet();
        this.deltaUnit = this.variables.setMutable("$D", 1.0);
        this.variables.set("$SX", 0.0);
        this.variables.set("$SY", 0.0);
        this.screenWidthUnit = this.variables.setMutable("$SW", 1.0);
        this.screenHeightUnit = this.variables.setMutable("$SH", 1.0);
        this.mouseXUnit = this.variables.setMutable("$MX", 0.0);
        this.mouseYUnit = this.variables.setMutable("$MY", 0.0);
        this.defaultLineSizeUnit = this.variables.setMutable("$LINE", 2.5);
        this.variables.set("$delta", (Unit)this.deltaUnit);
        this.variables.set("$screenW", (Unit)this.screenWidthUnit);
        this.variables.set("$screenH", (Unit)this.screenHeightUnit);
        this.variables.set("$mouseX", (Unit)this.mouseXUnit);
        this.variables.set("$mouseY", (Unit)this.mouseYUnit);
    }

    public Unit unitOf(Context cx, Object o) {
        return this.unitOf(ConsoleJS.getCurrent(cx), o);
    }

    public Unit unitOf(ConsoleJS console, Object o) {
        if (o instanceof Unit) {
            Unit unit = (Unit)o;
            return unit;
        }
        if (o instanceof Number) {
            Number number = (Number)o;
            return FixedNumberUnit.of((double)number.floatValue());
        }
        try {
            if (o instanceof String) {
                return this.unitContext.parse(o.toString());
            }
            if (o instanceof StringTag) {
                StringTag tag = (StringTag)o;
                return this.unitContext.parse(tag.m_7916_());
            }
        }
        catch (Exception ex) {
            console.error("Failed to parse Unit: " + String.valueOf(ex));
        }
        return FixedNumberUnit.ZERO;
    }

    @HideFromJS
    public void registerObject(String name, PainterFactory supplier) {
        this.objectRegistry.put(name, supplier);
    }

    public void registerBuiltinObjects() {
        this.registerObject("screen_group", ScreenGroup::new);
        this.registerObject("rectangle", RectangleObject::new);
        this.registerObject("text", TextObject::new);
        this.registerObject("atlas_texture", AtlasTextureObject::new);
        this.registerObject("gradient", GradientObject::new);
        this.registerObject("item", ItemObject::new);
        this.registerObject("line", LineObject::new);
    }

    @Nullable
    public PainterObject make(String type) {
        PainterFactory supplier = this.objectRegistry.get(type);
        return supplier == null ? null : supplier.create(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public PainterObject getObject(String key) {
        Object object = this.lock;
        synchronized (object) {
            return this.storage.getObject(key);
        }
    }

    public void paint(CompoundTag root) {
        Minecraft.m_91087_().execute(() -> {
            Object object = this.lock;
            synchronized (object) {
                this.storage.handle(root);
                this.screenObjects = null;
                if (ClientEvents.PAINTER_UPDATED.hasListeners()) {
                    ClientEvents.PAINTER_UPDATED.post(ScriptType.CLIENT, new ClientEventJS());
                }
            }
        });
    }

    public void clear() {
        Minecraft.m_91087_().execute(() -> {
            Object object = this.lock;
            synchronized (object) {
                this.storage.clear();
                this.screenObjects = null;
                if (ClientEvents.PAINTER_UPDATED.hasListeners()) {
                    ClientEvents.PAINTER_UPDATED.post(ScriptType.CLIENT, new ClientEventJS());
                }
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @HideFromJS
    public ScreenPainterObject[] getScreenObjects() {
        if (this.screenObjects == null) {
            Object object = this.lock;
            synchronized (object) {
                this.screenObjects = this.storage.createScreenObjects();
            }
        }
        return this.screenObjects;
    }

    public void setVariable(String key, Unit variable) {
        this.variables.set(key, variable);
    }

    public VariableSet getVariables() {
        return this.variables;
    }

    public void inGameScreenDraw(GuiGraphics graphics, float delta) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null || mc.f_91066_.f_92063_ || mc.f_91080_ != null) {
            return;
        }
        if (!ClientEvents.PAINT_SCREEN.hasListeners() && this.getScreenObjects().length == 0) {
            return;
        }
        RenderSystem.enableDepthTest();
        PaintScreenEventJS event = new PaintScreenEventJS(mc, graphics, this, delta);
        this.deltaUnit.set((double)delta);
        this.screenWidthUnit.set((double)event.width);
        this.screenHeightUnit.set((double)event.height);
        this.mouseXUnit.set((double)event.width / 2.0);
        this.mouseYUnit.set((double)event.height / 2.0);
        this.defaultLineSizeUnit.set(Math.max(2.5, (double)event.mc.m_91268_().m_85441_() / 1920.0 * 2.5));
        ClientEvents.PAINT_SCREEN.post(ScriptType.CLIENT, event);
        for (ScreenPainterObject object : this.getScreenObjects()) {
            if (!object.visible.getBoolean((UnitVariables)event) || !object.draw.ingame()) continue;
            object.preDraw(event);
        }
        for (ScreenPainterObject object : this.getScreenObjects()) {
            if (!object.visible.getBoolean((UnitVariables)event) || !object.draw.ingame()) continue;
            object.draw(event);
        }
    }

    public void guiScreenDraw(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        if (!ClientEvents.PAINT_SCREEN.hasListeners() && this.getScreenObjects().length == 0) {
            return;
        }
        PaintScreenEventJS event = new PaintScreenEventJS(mc, screen, graphics, this, mouseX, mouseY, delta);
        this.deltaUnit.set((double)delta);
        this.screenWidthUnit.set((double)event.width);
        this.screenHeightUnit.set((double)event.height);
        this.mouseXUnit.set((double)mouseX);
        this.mouseYUnit.set((double)mouseY);
        this.defaultLineSizeUnit.set(Math.max(2.5, (double)event.mc.m_91268_().m_85441_() / 1920.0 * 2.5));
        event.resetShaderColor();
        ClientEvents.PAINT_SCREEN.post(ScriptType.CLIENT, event);
        for (ScreenPainterObject object : this.getScreenObjects()) {
            if (!object.visible.getBoolean((UnitVariables)event) || !object.draw.gui()) continue;
            object.preDraw(event);
        }
        for (ScreenPainterObject object : this.getScreenObjects()) {
            if (!object.visible.getBoolean((UnitVariables)event) || !object.draw.gui()) continue;
            object.draw(event);
        }
    }
}

