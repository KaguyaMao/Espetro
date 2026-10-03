/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.api.IEnvironment
 *  cpw.mods.modlauncher.api.ITransformationService
 *  cpw.mods.modlauncher.api.ITransformer
 */
package LOL_141.vehicle_addition.coremod;

import LOL_141.vehicle_addition.coremod.SbwTerrainCompactTransformer;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import java.util.List;
import java.util.Set;

public final class SbwCoremodService
implements ITransformationService {
    public String name() {
        return "vehicle_addition_sbw_coremod";
    }

    public void initialize(IEnvironment environment) {
    }

    public void onLoad(IEnvironment environment, Set<String> servicesToScan) {
    }

    public List<ITransformer> transformers() {
        return List.of(new SbwTerrainCompactTransformer());
    }
}

