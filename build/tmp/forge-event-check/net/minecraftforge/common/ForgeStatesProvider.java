/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.IModLoadingState
 *  net.minecraftforge.fml.IModStateProvider
 *  net.minecraftforge.fml.ModLoadingPhase
 *  net.minecraftforge.fml.ModLoadingState
 */
package net.minecraftforge.common;

import java.util.List;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.IModLoadingState;
import net.minecraftforge.fml.IModStateProvider;
import net.minecraftforge.fml.ModLoadingPhase;
import net.minecraftforge.fml.ModLoadingState;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.registries.RegistryManager;

public class ForgeStatesProvider
implements IModStateProvider {
    final ModLoadingState CREATE_REGISTRIES = ModLoadingState.withInline((String)"CREATE_REGISTRIES", (String)"CONSTRUCT", (ModLoadingPhase)ModLoadingPhase.GATHER, ml -> RegistryManager.postNewRegistryEvent());
    final ModLoadingState OBJECT_HOLDERS = ModLoadingState.withInline((String)"OBJECT_HOLDERS", (String)"CREATE_REGISTRIES", (ModLoadingPhase)ModLoadingPhase.GATHER, ml -> ObjectHolderRegistry.findObjectHolders());
    final ModLoadingState INJECT_CAPABILITIES = ModLoadingState.withInline((String)"INJECT_CAPABILITIES", (String)"OBJECT_HOLDERS", (ModLoadingPhase)ModLoadingPhase.GATHER, ml -> CapabilityManager.INSTANCE.injectCapabilities(ml.getAllScanData()));
    final ModLoadingState UNFREEZE = ModLoadingState.withInline((String)"UNFREEZE_DATA", (String)"INJECT_CAPABILITIES", (ModLoadingPhase)ModLoadingPhase.GATHER, ml -> GameData.unfreezeData());
    final ModLoadingState LOAD_REGISTRIES = ModLoadingState.withInline((String)"LOAD_REGISTRIES", (String)"UNFREEZE_DATA", (ModLoadingPhase)ModLoadingPhase.GATHER, ml -> GameData.postRegisterEvents());
    final ModLoadingState FREEZE = ModLoadingState.withInline((String)"FREEZE_DATA", (String)"COMPLETE", (ModLoadingPhase)ModLoadingPhase.COMPLETE, ml -> GameData.freezeData());
    final ModLoadingState NETLOCK = ModLoadingState.withInline((String)"NETWORK_LOCK", (String)"FREEZE_DATA", (ModLoadingPhase)ModLoadingPhase.COMPLETE, ml -> NetworkRegistry.lock());

    public List<IModLoadingState> getAllStates() {
        return List.of(this.CREATE_REGISTRIES, this.OBJECT_HOLDERS, this.INJECT_CAPABILITIES, this.UNFREEZE, this.LOAD_REGISTRIES, this.FREEZE, this.NETLOCK);
    }
}

