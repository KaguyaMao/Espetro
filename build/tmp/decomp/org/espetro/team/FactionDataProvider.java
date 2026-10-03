/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

import org.espetro.team.FactionDataLoader;

public class FactionDataProvider {
    private static FactionDataLoader loader;

    public static FactionDataLoader getOrCreateLoader() {
        if (loader == null) {
            loader = new FactionDataLoader();
        }
        return loader;
    }
}

