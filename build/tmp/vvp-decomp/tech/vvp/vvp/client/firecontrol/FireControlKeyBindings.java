/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraftforge.client.settings.IKeyConflictContext
 *  net.minecraftforge.client.settings.KeyConflictContext
 */
package tech.vvp.vvp.client.firecontrol;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;

public final class FireControlKeyBindings {
    public static final String CATEGORY = "key.categories.vvp";
    public static final KeyMapping OPEN_FDC_MAP = new KeyMapping("key.vvp.himars_fdc_map", (IKeyConflictContext)KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, 73, "key.categories.vvp");

    private FireControlKeyBindings() {
    }
}

