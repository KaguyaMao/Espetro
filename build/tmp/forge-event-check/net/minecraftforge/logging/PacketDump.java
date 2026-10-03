/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraftforge.logging;

import io.netty.buffer.ByteBuf;

public class PacketDump {
    public static String getContentDump(ByteBuf buffer) {
        int j;
        int i;
        int currentLength = buffer.readableBytes();
        StringBuffer returnString = new StringBuffer(currentLength * 3 + currentLength + currentLength / 4 + 30);
        for (i = 0; i < currentLength; ++i) {
            if (i != 0 && i % 16 == 0) {
                returnString.append('\t');
                for (j = i - 16; j < i; ++j) {
                    if (buffer.getByte(j) < 32 || buffer.getByte(j) > 127) {
                        returnString.append('.');
                        continue;
                    }
                    returnString.append((char)buffer.getByte(j));
                }
                returnString.append("\n");
            }
            returnString.append(Integer.toString((buffer.getByte(i) & 0xF0) >> 4, 16) + Integer.toString((buffer.getByte(i) & 0xF) >> 0, 16));
            returnString.append(' ');
        }
        if (i != 0 && i % 16 != 0) {
            for (j = 0; j < (16 - i % 16) * 3; ++j) {
                returnString.append(' ');
            }
        }
        returnString.append('\t');
        for (j = i > 0 && i % 16 == 0 ? i - 16 : i - i % 16; i >= 0 && j < i; ++j) {
            if (buffer.getByte(j) < 32 || buffer.getByte(j) > 127) {
                returnString.append('.');
                continue;
            }
            returnString.append((char)buffer.getByte(j));
        }
        returnString.append('\n');
        returnString.append("Length: " + currentLength);
        return returnString.toString();
    }
}

