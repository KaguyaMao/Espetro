/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Hash$Strategy
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.util;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class MutableHashedLinkedMap<K, V>
implements Iterable<Map.Entry<K, V>> {
    public static final Hash.Strategy<? super Object> BASIC = new BasicStrategy();
    public static final Hash.Strategy<? super Object> IDENTITY = new IdentityStrategy();
    private final Hash.Strategy<? super K> strategy;
    private final Map<K, Entry> entries;
    private final MergeFunction<K, V> merge;
    private Entry head = null;
    private Entry last = null;
    private transient int changes = 0;

    public MutableHashedLinkedMap() {
        this(BASIC);
    }

    public MutableHashedLinkedMap(Hash.Strategy<? super K> strategy) {
        this(strategy, (k, v1, v2) -> v2);
    }

    public MutableHashedLinkedMap(Hash.Strategy<? super K> strategy, MergeFunction<K, V> merge) {
        this.strategy = strategy;
        this.entries = new Object2ObjectOpenCustomHashMap(strategy);
        this.merge = merge;
    }

    @Nullable
    public V put(K key, V value) {
        Entry l;
        Entry old = this.entries.get(key);
        if (old != null) {
            Object ret = old.value;
            old.value = this.merge.apply(key, ret, value);
            return ret;
        }
        ++this.changes;
        Entry self = new Entry(key, value);
        self.previous = l = this.last;
        if (l == null) {
            this.head = self;
        } else {
            l.next = self;
        }
        this.last = self;
        this.entries.put(key, self);
        return null;
    }

    public boolean contains(K key) {
        return this.entries.containsKey(key);
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    @Nullable
    public V remove(K key) {
        Entry ret = this.entries.remove(key);
        if (ret == null) {
            return null;
        }
        this.remove(ret);
        return ret.getValue();
    }

    @Nullable
    public V get(K key) {
        Entry entry = this.entries.get(key);
        return entry == null ? null : (V)entry.getValue();
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new Iterator<Map.Entry<K, V>>(){
            private Entry current;
            private Entry last;
            private int expectedChanges;
            {
                this.current = MutableHashedLinkedMap.this.head;
                this.last = null;
                this.expectedChanges = MutableHashedLinkedMap.this.changes;
            }

            @Override
            public boolean hasNext() {
                return this.current != null;
            }

            @Override
            public Map.Entry<K, V> next() {
                if (MutableHashedLinkedMap.this.changes != this.expectedChanges) {
                    throw new ConcurrentModificationException();
                }
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.current;
                this.current = this.current.next;
                return this.last;
            }

            @Override
            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException("Invalid remove() call, must call next() first");
                }
                if (MutableHashedLinkedMap.this.changes != this.expectedChanges) {
                    throw new ConcurrentModificationException();
                }
                Entry removed = MutableHashedLinkedMap.this.entries.remove(this.last.getKey());
                if (removed != this.last) {
                    throw new ConcurrentModificationException();
                }
                ++this.expectedChanges;
                MutableHashedLinkedMap.this.remove(this.last);
                this.last = null;
            }
        };
    }

    @Nullable
    public V putFirst(K key, V value) {
        if (this.head != null) {
            return this.putBefore(this.head.getKey(), key, value);
        }
        return this.put(key, value);
    }

    @Nullable
    public V putAfter(K after, K key, V value) {
        Entry target = this.entries.get(after);
        if (target == null) {
            return this.put(key, value);
        }
        V ret = null;
        Entry entry = this.entries.get(key);
        if (entry != null) {
            ret = entry.value;
            entry.value = this.merge.apply(key, ret, value);
            this.remove(entry);
        } else {
            entry = new Entry(key, value);
            this.entries.put(key, entry);
        }
        ++this.changes;
        entry.previous = target;
        if (target.next == null) {
            this.last = target;
        } else {
            target.next.previous = entry;
        }
        entry.next = target.next;
        target.next = entry;
        return ret;
    }

    @Nullable
    public V putBefore(K before, K key, V value) {
        Entry target = this.entries.get(before);
        if (target == null) {
            return this.put(key, value);
        }
        V ret = null;
        Entry entry = this.entries.get(key);
        if (entry != null) {
            ret = entry.value;
            entry.value = this.merge.apply(key, ret, value);
            this.remove(entry);
        } else {
            entry = new Entry(key, value);
            this.entries.put(key, entry);
        }
        ++this.changes;
        entry.previous = target.previous;
        if (target.previous == null) {
            this.head = entry;
        } else {
            target.previous.next = entry;
        }
        entry.next = target;
        target.previous = entry;
        return ret;
    }

    private void remove(Entry e) {
        ++this.changes;
        Entry previous = e.previous;
        if (this.head == e) {
            this.head = e.next;
        } else if (e.previous != null) {
            e.previous.next = e.next;
            e.previous = null;
        }
        if (this.last == e) {
            this.last = previous;
        } else if (e.next != null) {
            e.next.previous = previous;
            e.next = null;
        }
    }

    public static interface MergeFunction<Key, Value> {
        public Value apply(Key var1, Value var2, Value var3);
    }

    private class Entry
    implements Map.Entry<K, V> {
        private final K key;
        private V value;
        private Entry previous;
        private Entry next;

        private Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            Object old = this.value;
            this.value = value;
            return old;
        }

        public String toString() {
            return "Entry[" + String.valueOf(this.key) + ", " + String.valueOf(this.value) + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return (this.key == null ? e.getKey() == null : this.key.equals(e.getKey())) && (this.value == null ? e.getValue() == null : this.value.equals(e.getValue()));
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : MutableHashedLinkedMap.this.strategy.hashCode(this.key)) ^ (this.value == null ? 0 : this.value.hashCode());
        }
    }

    private static class BasicStrategy
    implements Hash.Strategy<Object> {
        private BasicStrategy() {
        }

        public int hashCode(Object o) {
            return Objects.hashCode(o);
        }

        public boolean equals(Object a, Object b) {
            return Objects.equals(a, b);
        }
    }

    private static class IdentityStrategy
    implements Hash.Strategy<Object> {
        private IdentityStrategy() {
        }

        public int hashCode(Object o) {
            return System.identityHashCode(o);
        }

        public boolean equals(Object a, Object b) {
            return a == b;
        }
    }
}

