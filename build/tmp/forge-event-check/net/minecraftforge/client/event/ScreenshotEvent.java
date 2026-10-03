/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.network.chat.Component
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

@Cancelable
public class ScreenshotEvent
extends Event {
    public static final Component DEFAULT_CANCEL_REASON = Component.m_237113_((String)"Screenshot canceled");
    private final NativeImage image;
    private File screenshotFile;
    private Component resultMessage = null;

    @ApiStatus.Internal
    public ScreenshotEvent(NativeImage image, File screenshotFile) {
        this.image = image;
        this.screenshotFile = screenshotFile;
        try {
            this.screenshotFile = screenshotFile.getCanonicalFile();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public NativeImage getImage() {
        return this.image;
    }

    public File getScreenshotFile() {
        return this.screenshotFile;
    }

    public void setScreenshotFile(File screenshotFile) {
        this.screenshotFile = screenshotFile;
    }

    public Component getResultMessage() {
        return this.resultMessage;
    }

    public void setResultMessage(Component resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Component getCancelMessage() {
        return this.getResultMessage() != null ? this.getResultMessage() : DEFAULT_CANCEL_REASON;
    }
}

