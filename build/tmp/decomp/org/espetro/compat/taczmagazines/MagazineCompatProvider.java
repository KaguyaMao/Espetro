/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.ModList
 */
package org.espetro.compat.taczmagazines;

import net.minecraftforge.fml.ModList;
import org.espetro.Espetro;
import org.espetro.compat.taczmagazines.MagazineCompat;
import org.espetro.compat.taczmagazines.NoopMagazineCompat;
import org.espetro.compat.taczmagazines.ReflectiveMagazineCompat;

public final class MagazineCompatProvider {
    private static volatile MagazineCompat instance;

    private MagazineCompatProvider() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MagazineCompat get() {
        MagazineCompat current = instance;
        if (current != null) {
            return current;
        }
        Class<MagazineCompatProvider> clazz = MagazineCompatProvider.class;
        synchronized (MagazineCompatProvider.class) {
            current = instance;
            if (current != null) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return current;
            }
            if (!ModList.get().isLoaded("taczmagazines")) {
                current = NoopMagazineCompat.INSTANCE;
            } else {
                try {
                    current = new ReflectiveMagazineCompat();
                    Espetro.LOGGER.info("TaCZ Magazines compatibility enabled");
                }
                catch (LinkageError | ReflectiveOperationException error) {
                    Espetro.LOGGER.error("TaCZ Magazines is loaded but its public API is incompatible", error);
                    current = NoopMagazineCompat.INSTANCE;
                }
            }
            instance = current;
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return current;
        }
    }

    public static void resetForTests() {
        instance = null;
    }
}

