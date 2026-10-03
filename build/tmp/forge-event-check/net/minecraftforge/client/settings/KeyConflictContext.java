/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package net.minecraftforge.client.settings;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.IKeyConflictContext;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum KeyConflictContext implements IKeyConflictContext
{
    UNIVERSAL{

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return true;
        }
    }
    ,
    GUI{

        @Override
        public boolean isActive() {
            return Minecraft.m_91087_().f_91080_ != null;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return this == other;
        }
    }
    ,
    IN_GAME{

        @Override
        public boolean isActive() {
            return !GUI.isActive();
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return this == other;
        }
    };

}

