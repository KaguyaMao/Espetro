/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.GLFW
 */
package com.sighs.apricityui.event;

import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.render.Operation;
import org.lwjgl.glfw.GLFW;

public class KeyEvent
extends Event {
    public final int keyCode;
    public final int scanCode;
    public final int modifiers;
    public final String key;
    public final String code;
    public final boolean repeat;
    public final Source source;
    public boolean altKey;
    public boolean shiftKey;
    public boolean controlKey;
    public boolean metaKey;

    public boolean getCtrlKey() {
        return this.controlKey;
    }

    public KeyEvent(Element target, String type, int keyCode, int scanCode, int modifiers, boolean repeat, Source source) {
        super(target, type, true);
        this.keyCode = keyCode;
        this.scanCode = scanCode;
        this.modifiers = modifiers;
        this.repeat = repeat;
        this.key = KeyEvent.resolveKey(keyCode, scanCode);
        this.code = KeyEvent.resolveCode(keyCode);
        this.source = source == null ? Source.INPUT_EVENT : source;
        this.altKey = (modifiers & 4) != 0 || KeyEvent.isModifierPressed("key.keyboard.left.alt") || KeyEvent.isModifierPressed("key.keyboard.right.alt");
        this.shiftKey = (modifiers & 1) != 0 || KeyEvent.isModifierPressed("key.keyboard.left.shift") || KeyEvent.isModifierPressed("key.keyboard.right.shift");
        this.controlKey = (modifiers & 2) != 0 || KeyEvent.isModifierPressed("key.keyboard.left.control") || KeyEvent.isModifierPressed("key.keyboard.right.control");
        this.metaKey = (modifiers & 8) != 0 || KeyEvent.isModifierPressed("key.keyboard.left.win") || KeyEvent.isModifierPressed("key.keyboard.right.win");
    }

    public static KeyEvent triggerEvent(Document document, String type, int keyCode, int scanCode, int modifiers, boolean repeat, Source source) {
        if (document == null) {
            return null;
        }
        Element target = document.getFocusedElement();
        if (target == null) {
            target = document.getPressedElement();
        }
        if (target == null) {
            target = document.body;
        }
        if (target == null) {
            return null;
        }
        KeyEvent event = new KeyEvent(target, type, keyCode, scanCode, modifiers, repeat, source);
        event.setTrusted(true);
        Event.tiggerEvent(event);
        return event;
    }

    public static KeyEvent triggerEvent(Document document, String type, int keyCode, boolean repeat) {
        return KeyEvent.triggerEvent(document, type, keyCode, 0, 0, repeat, Source.INPUT_EVENT);
    }

    private static String resolveKey(int keyCode, int scanCode) {
        return switch (keyCode) {
            case 257 -> "Enter";
            case 256 -> "Escape";
            case 259 -> "Backspace";
            case 258 -> "Tab";
            case 32 -> " ";
            case 263 -> "ArrowLeft";
            case 262 -> "ArrowRight";
            case 265 -> "ArrowUp";
            case 264 -> "ArrowDown";
            case 261 -> "Delete";
            case 268 -> "Home";
            case 269 -> "End";
            case 266 -> "PageUp";
            case 267 -> "PageDown";
            case 340, 344 -> "Shift";
            case 341, 345 -> "Control";
            case 342, 346 -> "Alt";
            case 343, 347 -> "Meta";
            default -> {
                String glfwName = GLFW.glfwGetKeyName((int)keyCode, (int)scanCode);
                if (glfwName == null || glfwName.isBlank()) {
                    yield "Unidentified";
                }
                if (glfwName.length() == 1) {
                    yield glfwName.toLowerCase();
                }
                yield glfwName;
            }
        };
    }

    private static String resolveCode(int keyCode) {
        return switch (keyCode) {
            case 65 -> "KeyA";
            case 66 -> "KeyB";
            case 67 -> "KeyC";
            case 68 -> "KeyD";
            case 69 -> "KeyE";
            case 70 -> "KeyF";
            case 71 -> "KeyG";
            case 72 -> "KeyH";
            case 73 -> "KeyI";
            case 74 -> "KeyJ";
            case 75 -> "KeyK";
            case 76 -> "KeyL";
            case 77 -> "KeyM";
            case 78 -> "KeyN";
            case 79 -> "KeyO";
            case 80 -> "KeyP";
            case 81 -> "KeyQ";
            case 82 -> "KeyR";
            case 83 -> "KeyS";
            case 84 -> "KeyT";
            case 85 -> "KeyU";
            case 86 -> "KeyV";
            case 87 -> "KeyW";
            case 88 -> "KeyX";
            case 89 -> "KeyY";
            case 90 -> "KeyZ";
            case 48 -> "Digit0";
            case 49 -> "Digit1";
            case 50 -> "Digit2";
            case 51 -> "Digit3";
            case 52 -> "Digit4";
            case 53 -> "Digit5";
            case 54 -> "Digit6";
            case 55 -> "Digit7";
            case 56 -> "Digit8";
            case 57 -> "Digit9";
            case 257 -> "Enter";
            case 256 -> "Escape";
            case 259 -> "Backspace";
            case 258 -> "Tab";
            case 32 -> "Space";
            case 263 -> "ArrowLeft";
            case 262 -> "ArrowRight";
            case 265 -> "ArrowUp";
            case 264 -> "ArrowDown";
            case 261 -> "Delete";
            case 340 -> "ShiftLeft";
            case 344 -> "ShiftRight";
            case 341 -> "ControlLeft";
            case 345 -> "ControlRight";
            case 342 -> "AltLeft";
            case 346 -> "AltRight";
            case 343 -> "MetaLeft";
            case 347 -> "MetaRight";
            default -> "Unidentified";
        };
    }

    private static boolean isModifierPressed(String key) {
        try {
            return Operation.isKeyPressed(key);
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    @Override
    public KeyEvent clone() {
        Element element;
        Object object = this.target;
        KeyEvent copy = new KeyEvent(object instanceof Element ? (element = (Element)object) : null, this.type, this.keyCode, this.scanCode, this.modifiers, this.repeat, this.source);
        this.copyTo(copy);
        copy.altKey = this.altKey;
        copy.shiftKey = this.shiftKey;
        copy.controlKey = this.controlKey;
        copy.metaKey = this.metaKey;
        return copy;
    }

    public static enum Source {
        INPUT_EVENT,
        SCREEN_EVENT;

    }
}

