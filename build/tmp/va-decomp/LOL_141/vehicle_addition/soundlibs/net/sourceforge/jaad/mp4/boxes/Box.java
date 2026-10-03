/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes;

import java.util.List;

public interface Box {
    public Box getParent();

    public long getSize();

    public long getType();

    public long getOffset();

    public String getName();

    public boolean hasChildren();

    public boolean hasChild(long var1);

    public List<Box> getChildren();

    public List<Box> getChildren(long var1);

    public Box getChild(long var1);
}

