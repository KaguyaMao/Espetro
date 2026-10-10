/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.List;

public class WorldWorkerManager {
    private static List<IWorker> workers = new ArrayList<IWorker>();
    private static long startTime = -1L;
    private static int index = 0;

    public static void tick(boolean start) {
        if (start) {
            startTime = System.currentTimeMillis();
            return;
        }
        index = 0;
        IWorker task = WorldWorkerManager.getNext();
        if (task == null) {
            return;
        }
        long time = 50L - (System.currentTimeMillis() - startTime);
        if (time < 10L) {
            time = 10L;
        }
        time += System.currentTimeMillis();
        while (System.currentTimeMillis() < time && task != null) {
            boolean again = task.doWork();
            if (!task.hasWork()) {
                WorldWorkerManager.remove(task);
                task = WorldWorkerManager.getNext();
                continue;
            }
            if (again) continue;
            task = WorldWorkerManager.getNext();
        }
    }

    public static synchronized void addWorker(IWorker worker) {
        workers.add(worker);
    }

    private static synchronized IWorker getNext() {
        return workers.size() > index ? workers.get(index++) : null;
    }

    private static synchronized void remove(IWorker worker) {
        workers.remove(worker);
        --index;
    }

    public static synchronized void clear() {
        workers.clear();
    }

    public static interface IWorker {
        public boolean hasWork();

        public boolean doWork();
    }
}

