/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.Util
 *  net.minecraft.client.renderer.RenderType
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public class ChunkRenderTypeSet
implements Iterable<RenderType> {
    private static final List<RenderType> CHUNK_RENDER_TYPES_LIST = RenderType.m_110506_();
    private static final RenderType[] CHUNK_RENDER_TYPES = CHUNK_RENDER_TYPES_LIST.toArray(new RenderType[0]);
    private static final ChunkRenderTypeSet NONE = new None();
    private static final ChunkRenderTypeSet ALL = new All();
    private final BitSet bits;

    public static ChunkRenderTypeSet none() {
        return NONE;
    }

    public static ChunkRenderTypeSet all() {
        return ALL;
    }

    public static ChunkRenderTypeSet of(RenderType ... renderTypes) {
        return ChunkRenderTypeSet.of(Arrays.asList(renderTypes));
    }

    public static ChunkRenderTypeSet of(Collection<RenderType> renderTypes) {
        if (renderTypes.isEmpty()) {
            return ChunkRenderTypeSet.none();
        }
        return ChunkRenderTypeSet.of(renderTypes);
    }

    private static ChunkRenderTypeSet of(Iterable<RenderType> renderTypes) {
        BitSet bits = new BitSet();
        for (RenderType renderType : renderTypes) {
            int index = renderType.getChunkLayerId();
            Preconditions.checkArgument((index >= 0 ? 1 : 0) != 0, (Object)("Attempted to create chunk render type set with a non-chunk render type: " + String.valueOf(renderType)));
            bits.set(index);
        }
        return new ChunkRenderTypeSet(bits);
    }

    public static ChunkRenderTypeSet union(ChunkRenderTypeSet ... sets) {
        return ChunkRenderTypeSet.union(Arrays.asList(sets));
    }

    public static ChunkRenderTypeSet union(Collection<ChunkRenderTypeSet> sets) {
        if (sets.isEmpty()) {
            return ChunkRenderTypeSet.none();
        }
        return ChunkRenderTypeSet.union(sets);
    }

    public static ChunkRenderTypeSet union(Iterable<ChunkRenderTypeSet> sets) {
        BitSet bits = new BitSet();
        for (ChunkRenderTypeSet set : sets) {
            bits.or(set.bits);
        }
        return new ChunkRenderTypeSet(bits);
    }

    public static ChunkRenderTypeSet intersection(ChunkRenderTypeSet ... sets) {
        return ChunkRenderTypeSet.intersection(Arrays.asList(sets));
    }

    public static ChunkRenderTypeSet intersection(Collection<ChunkRenderTypeSet> sets) {
        if (sets.isEmpty()) {
            return ChunkRenderTypeSet.all();
        }
        return ChunkRenderTypeSet.intersection(sets);
    }

    public static ChunkRenderTypeSet intersection(Iterable<ChunkRenderTypeSet> sets) {
        BitSet bits = new BitSet();
        bits.set(0, CHUNK_RENDER_TYPES.length);
        for (ChunkRenderTypeSet set : sets) {
            bits.and(set.bits);
        }
        return new ChunkRenderTypeSet(bits);
    }

    private ChunkRenderTypeSet(BitSet bits) {
        this.bits = bits;
    }

    public boolean isEmpty() {
        return this.bits.isEmpty();
    }

    public boolean contains(RenderType renderType) {
        int id = renderType.getChunkLayerId();
        return id >= 0 && this.bits.get(id);
    }

    @Override
    @NotNull
    public Iterator<RenderType> iterator() {
        return new IteratorImpl();
    }

    public List<RenderType> asList() {
        return ImmutableList.copyOf((Iterable)this);
    }

    private final class IteratorImpl
    implements Iterator<RenderType> {
        private int index;

        private IteratorImpl() {
            this.index = ChunkRenderTypeSet.this.bits.nextSetBit(0);
        }

        @Override
        public boolean hasNext() {
            return this.index >= 0;
        }

        @Override
        public RenderType next() {
            RenderType renderType = CHUNK_RENDER_TYPES[this.index];
            this.index = ChunkRenderTypeSet.this.bits.nextSetBit(this.index + 1);
            return renderType;
        }
    }

    private static final class None
    extends ChunkRenderTypeSet {
        private None() {
            super(new BitSet());
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean contains(RenderType renderType) {
            return false;
        }

        @Override
        @NotNull
        public Iterator<RenderType> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public List<RenderType> asList() {
            return List.of();
        }
    }

    private static final class All
    extends ChunkRenderTypeSet {
        private All() {
            super((BitSet)Util.m_137469_((Object)new BitSet(), bits -> bits.set(0, CHUNK_RENDER_TYPES.length)));
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(RenderType renderType) {
            return renderType.getChunkLayerId() >= 0;
        }

        @Override
        @NotNull
        public Iterator<RenderType> iterator() {
            return CHUNK_RENDER_TYPES_LIST.iterator();
        }

        @Override
        public List<RenderType> asList() {
            return CHUNK_RENDER_TYPES_LIST;
        }
    }
}

