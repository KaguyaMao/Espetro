/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.Main
 */
package net.minecraftforge.gametest;

import net.minecraft.server.Main;

public class GameTestMain {
    public static void main(String[] args) {
        System.setProperty("forge.enableGameTest", "true");
        System.setProperty("forge.gameTestServer", "true");
        Main.main((String[])args);
    }
}

