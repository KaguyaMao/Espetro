/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.script.ScriptType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KubeJSBackgroundThread
extends Thread {
    public static boolean running = true;

    public KubeJSBackgroundThread() {
        super("KubeJS Background Thread");
    }

    @Override
    public void run() {
        ScriptType[] types;
        for (ScriptType type : types = ScriptType.values()) {
            type.executor = Executors.newSingleThreadExecutor();
        }
        while (running) {
            try {
                Thread.sleep(3000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (ScriptType type : types) {
                type.console.flush(false);
            }
        }
        for (ScriptType type : types) {
            boolean b;
            type.console.flush(false);
            ((ExecutorService)type.executor).shutdown();
            try {
                b = ((ExecutorService)type.executor).awaitTermination(3L, TimeUnit.SECONDS);
            }
            catch (InterruptedException var3) {
                b = false;
            }
            if (b) continue;
            ((ExecutorService)type.executor).shutdownNow();
        }
    }
}

