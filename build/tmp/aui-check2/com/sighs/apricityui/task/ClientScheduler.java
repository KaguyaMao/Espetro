/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.task;

import com.sighs.apricityui.ApricityUI;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ClientScheduler {
    private static final Timer TIMER = new Timer("ApricityUI-Timer", true);

    public static Cancellable setTimeout(int ms, Consumer<Cancellable> action) {
        Task task = new Task(action, false);
        TIMER.schedule((TimerTask)task, ms);
        return task;
    }

    public static Cancellable setInterval(int ms, Consumer<Cancellable> action) {
        Task task = new Task(action, true);
        TIMER.schedule((TimerTask)task, ms, (long)ms);
        return task;
    }

    private static void runOnClientThread(Runnable action) {
        if (action == null) {
            return;
        }
        try {
            Class<?> minecraftClass = Class.forName("net.minecraft.client.Minecraft");
            Method getInstance = minecraftClass.getMethod("getInstance", new Class[0]);
            Object minecraft = getInstance.invoke(null, new Object[0]);
            if (minecraft != null) {
                Method execute = minecraftClass.getMethod("execute", Runnable.class);
                execute.invoke(minecraft, action);
                return;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        action.run();
    }

    private static final class Task
    extends TimerTask
    implements Cancellable {
        private final Consumer<Cancellable> action;
        private final boolean repeat;

        private Task(Consumer<Cancellable> action, boolean repeat) {
            this.action = action;
            this.repeat = repeat;
        }

        @Override
        public void run() {
            try {
                ClientScheduler.runOnClientThread(() -> this.action.accept(this));
            }
            catch (Exception e) {
                ApricityUI.LOGGER.error("[AUI Scheduler] client timer task failed repeat={}", (Object)this.repeat, (Object)e);
            }
            if (!this.repeat) {
                this.cancel();
            }
        }
    }

    @FunctionalInterface
    public static interface Cancellable {
        public boolean cancel();
    }
}

