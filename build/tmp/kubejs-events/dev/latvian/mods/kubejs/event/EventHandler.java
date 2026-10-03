/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.BaseFunction
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.RhinoException
 *  dev.latvian.mods.rhino.Scriptable
 *  dev.latvian.mods.rhino.Wrapper
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.event.EventExceptionHandler;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandlerContainer;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.IEventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.RhinoException;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public final class EventHandler
extends BaseFunction {
    public final EventGroup group;
    public final String name;
    public final ScriptTypePredicate scriptTypePredicate;
    public final Supplier<Class<? extends EventJS>> eventType;
    private boolean hasResult;
    public transient Extra extra;
    private EventHandlerContainer[] eventContainers;
    private Map<Object, EventHandlerContainer[]> extraEventContainers;

    EventHandler(EventGroup g, String n, ScriptTypePredicate st, Supplier<Class<? extends EventJS>> e) {
        this.group = g;
        this.name = n;
        this.scriptTypePredicate = st;
        this.eventType = e;
        this.hasResult = false;
        this.extra = null;
        this.eventContainers = null;
        this.extraEventContainers = null;
    }

    public EventHandler hasResult() {
        this.hasResult = true;
        return this;
    }

    public boolean getHasResult() {
        return this.hasResult;
    }

    @HideFromJS
    public EventHandler extra(Extra extra) {
        this.extra = extra;
        return this;
    }

    @HideFromJS
    public void clear(ScriptType type) {
        if (this.eventContainers != null) {
            this.eventContainers[type.ordinal()] = null;
            if (EventHandlerContainer.isEmpty(this.eventContainers)) {
                this.eventContainers = null;
            }
        }
        if (this.extraEventContainers != null) {
            Iterator<Map.Entry<Object, EventHandlerContainer[]>> entries = this.extraEventContainers.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<Object, EventHandlerContainer[]> entry = entries.next();
                entry.getValue()[type.ordinal()] = null;
                if (!EventHandlerContainer.isEmpty(entry.getValue())) continue;
                entries.remove();
            }
            if (this.extraEventContainers.isEmpty()) {
                this.extraEventContainers = null;
            }
        }
    }

    public boolean hasListeners() {
        return this.eventContainers != null || this.extraEventContainers != null;
    }

    public boolean hasListeners(Object extraId) {
        return this.eventContainers != null || this.extraEventContainers != null && this.extraEventContainers.containsKey(extraId);
    }

    public void listen(ScriptType type, @Nullable Object extraId, IEventHandler handler) {
        EventHandlerContainer[] map;
        if (!type.manager.get().canListenEvents) {
            throw new IllegalStateException("Event handler '" + String.valueOf((Object)this) + "' can only be registered during script loading!");
        }
        if (!this.scriptTypePredicate.test(type)) {
            throw new UnsupportedOperationException("Tried to register event handler '" + String.valueOf((Object)this) + "' for invalid script type " + String.valueOf(type) + "! Valid script types: " + String.valueOf(this.scriptTypePredicate.getValidTypes()));
        }
        if (extraId != null && this.extra != null) {
            extraId = Wrapper.unwrapped((Object)extraId);
            extraId = this.extra.transformer.transform(extraId);
        }
        if (this.extra != null && this.extra.required && extraId == null) {
            throw new IllegalArgumentException("Event handler '" + String.valueOf((Object)this) + "' requires extra id!");
        }
        if (this.extra == null && extraId != null) {
            throw new IllegalArgumentException("Event handler '" + String.valueOf((Object)this) + "' doesn't support extra id!");
        }
        if (this.extra != null && extraId != null && !this.extra.validator.test(extraId)) {
            throw new IllegalArgumentException("Event handler '" + String.valueOf((Object)this) + "' doesn't accept id '" + String.valueOf(this.extra.toString.transform(extraId)) + "'!");
        }
        int[] line = new int[1];
        String source = Context.getSourcePositionFromStack((Context)type.manager.get().context, (int[])line);
        if (extraId == null) {
            if (this.eventContainers == null) {
                this.eventContainers = new EventHandlerContainer[ScriptType.VALUES.length];
            }
            map = this.eventContainers;
        } else {
            if (this.extraEventContainers == null) {
                Map<Object, Object> map2 = this.extraEventContainers = this.extra.identity ? new LinkedHashMap() : new HashMap();
            }
            if ((map = this.extraEventContainers.get(extraId)) == null) {
                map = new EventHandlerContainer[ScriptType.VALUES.length];
                this.extraEventContainers.put(extraId, map);
            }
        }
        int index = type.ordinal();
        if (map[index] == null) {
            map[index] = new EventHandlerContainer(extraId, handler, source, line[0]);
        } else {
            map[index].add(extraId, handler, source, line[0]);
        }
    }

    @HideFromJS
    public void listenJava(ScriptType type, @Nullable Object extraId, IEventHandler handler) {
        boolean b = type.manager.get().canListenEvents;
        type.manager.get().canListenEvents = true;
        this.listen(type, extraId, handler);
        type.manager.get().canListenEvents = b;
    }

    public EventResult post(EventJS event) {
        ScriptTypePredicate scriptTypePredicate = this.scriptTypePredicate;
        if (scriptTypePredicate instanceof ScriptTypeHolder) {
            ScriptTypeHolder type = (ScriptTypeHolder)((Object)scriptTypePredicate);
            return this.post(type, null, event);
        }
        throw new IllegalStateException("You must specify which script type to post event to");
    }

    public EventResult post(ScriptTypeHolder scriptType, EventJS event) {
        return this.post(scriptType, null, event);
    }

    public EventResult post(ScriptTypeHolder scriptType, EventJS event, EventExceptionHandler exh) {
        return this.post(scriptType, null, event, exh);
    }

    public EventResult post(EventJS event, @Nullable Object extraId) {
        ScriptTypePredicate scriptTypePredicate = this.scriptTypePredicate;
        if (scriptTypePredicate instanceof ScriptTypeHolder) {
            ScriptTypeHolder type = (ScriptTypeHolder)((Object)scriptTypePredicate);
            return this.post(type, extraId, event);
        }
        throw new IllegalStateException("You must specify which script type to post event to");
    }

    public EventResult post(EventJS event, @Nullable Object extraId, EventExceptionHandler exh) {
        ScriptTypePredicate scriptTypePredicate = this.scriptTypePredicate;
        if (scriptTypePredicate instanceof ScriptTypeHolder) {
            ScriptTypeHolder type = (ScriptTypeHolder)((Object)scriptTypePredicate);
            return this.post(type, extraId, event, exh);
        }
        throw new IllegalStateException("You must specify which script type to post event to");
    }

    public EventResult post(ScriptTypeHolder type, @Nullable Object extraId, EventJS event) {
        return this.post(type, extraId, event, null);
    }

    public EventResult post(ScriptTypeHolder type, @Nullable Object extraId, EventJS event, EventExceptionHandler exh) {
        if (!this.hasListeners()) {
            return EventResult.PASS;
        }
        ScriptType scriptType = type.kjs$getScriptType();
        if (extraId != null && this.extra != null) {
            extraId = Wrapper.unwrapped((Object)extraId);
            extraId = this.extra.transformer.transform(extraId);
        }
        if (this.extra != null && this.extra.required && extraId == null) {
            throw new IllegalArgumentException("Event handler '" + String.valueOf((Object)this) + "' requires extra id!");
        }
        if (this.extra == null && extraId != null) {
            throw new IllegalArgumentException("Event handler '" + String.valueOf((Object)this) + "' doesn't support extra id " + String.valueOf(extraId) + "!");
        }
        EventResult eventResult = EventResult.PASS;
        try {
            EventHandlerContainer[] extraContainers;
            EventHandlerContainer[] eventHandlerContainerArray = extraContainers = this.extraEventContainers == null ? null : this.extraEventContainers.get(extraId);
            if (extraContainers != null) {
                this.postToHandlers(scriptType, extraContainers, event, exh);
                if (!scriptType.isStartup()) {
                    this.postToHandlers(ScriptType.STARTUP, extraContainers, event, exh);
                }
            }
            if (this.eventContainers != null) {
                this.postToHandlers(scriptType, this.eventContainers, event, exh);
                if (!scriptType.isStartup()) {
                    this.postToHandlers(ScriptType.STARTUP, this.eventContainers, event, exh);
                }
            }
        }
        catch (EventExit exit) {
            if (exit.result.type() == EventResult.Type.ERROR) {
                if (DevProperties.get().debugInfo) {
                    ((Throwable)exit.result.value()).printStackTrace();
                }
                scriptType.console.error("Error in '" + String.valueOf((Object)this) + "'", (Throwable)exit.result.value());
            } else {
                eventResult = exit.result;
                if (!this.getHasResult()) {
                    scriptType.console.error("Error in '" + String.valueOf((Object)this) + "'", new IllegalStateException("Event returned result when it's not cancellable"));
                }
            }
        }
        catch (RhinoException error) {
            scriptType.console.error("Error in '" + String.valueOf((Object)this) + "'", error);
        }
        event.afterPosted(eventResult);
        return eventResult;
    }

    private void postToHandlers(ScriptType type, EventHandlerContainer[] containers, EventJS event, @Nullable EventExceptionHandler exh) throws EventExit {
        EventHandlerContainer handler = containers[type.ordinal()];
        if (handler != null) {
            handler.handle(event, exh);
        }
    }

    public String toString() {
        return String.valueOf(this.group) + "." + this.name;
    }

    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        ScriptType type = (ScriptType)cx.getProperty("Type", null);
        if (type == null) {
            throw new IllegalStateException("Unknown script type!");
        }
        try {
            if (args.length == 1) {
                this.listen(type, null, (IEventHandler)Context.jsToJava((Context)cx, (Object)args[0], IEventHandler.class));
            } else if (args.length == 2) {
                IEventHandler handler = (IEventHandler)Context.jsToJava((Context)cx, (Object)args[1], IEventHandler.class);
                for (Object o : ListJS.orSelf(args[0])) {
                    this.listen(type, o, handler);
                }
            }
        }
        catch (Exception ex) {
            type.console.error(ex.getLocalizedMessage());
        }
        return null;
    }

    public void forEachListener(ScriptType type, Consumer<EventHandlerContainer> callback) {
        if (this.eventContainers != null) {
            EventHandlerContainer c = this.eventContainers[type.ordinal()];
            while (c != null) {
                callback.accept(c);
                c = c.child;
            }
        }
        if (this.extraEventContainers != null) {
            for (Map.Entry<Object, EventHandlerContainer[]> entry : this.extraEventContainers.entrySet()) {
                EventHandlerContainer c = entry.getValue()[type.ordinal()];
                while (c != null) {
                    callback.accept(c);
                    c = c.child;
                }
            }
        }
    }

    public Set<Object> findUniqueExtraIds(ScriptType type) {
        if (!this.hasListeners()) {
            return Set.of();
        }
        LinkedHashSet<Object> set = new LinkedHashSet<Object>();
        this.forEachListener(type, c -> {
            if (c.extraId != null) {
                set.add(c.extraId);
            }
        });
        return set;
    }
}

