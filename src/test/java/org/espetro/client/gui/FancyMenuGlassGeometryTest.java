package org.espetro.client.gui;

import org.joml.Matrix4f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FancyMenuGlassGeometryTest {
    @Test
    void auiSlideAndHudScaleAreAppliedExactlyOnce() {
        Matrix4f pose = new Matrix4f().translate(12.0F, -35.0F, 200.0F).scale(1.5F, 1.5F, 1.0F);
        FancyMenuGlass.Area area = FancyMenuGlass.screenArea(pose, 20, 40, 100, 24, 6.0F);
        assertEquals(42.0F, area.x());
        assertEquals(25.0F, area.y());
        assertEquals(150.0F, area.width());
        assertEquals(36.0F, area.height());
        assertEquals(9.0F, area.radius());
    }

    @Test
    void reflectedPoseKeepsPositiveScreenBounds() {
        FancyMenuGlass.Area area = FancyMenuGlass.screenArea(
            new Matrix4f().translate(200, 100, 0).scale(-2.0F, 1.0F, 1.0F), 10, 20, 30, 40, 8);
        assertEquals(120.0F, area.x());
        assertEquals(120.0F, area.y());
        assertEquals(60.0F, area.width());
        assertEquals(40.0F, area.height());
        assertEquals(8.0F, area.radius());
    }

    @Test
    void unsupportedTransformsAndInvalidAreasFallBack() {
        assertNull(FancyMenuGlass.screenArea(new Matrix4f().rotateZ(0.25F), 0, 0, 50, 20, 4));
        assertNull(FancyMenuGlass.screenArea(new Matrix4f().scale(0), 0, 0, 50, 20, 4));
        assertNull(FancyMenuGlass.screenArea(new Matrix4f(), 0, 0, 0, 20, 4));
        assertNull(FancyMenuGlass.screenArea(new Matrix4f(), 0, 0, 50, 20, Float.NaN));
        assertNull(FancyMenuGlass.screenArea(new Matrix4f().translate(Float.NaN, 0, 0), 0, 0, 50, 20, 4));
    }
}
