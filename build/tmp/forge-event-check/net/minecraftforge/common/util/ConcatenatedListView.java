/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.common.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class ConcatenatedListView<T>
implements List<T> {
    private final List<List<T>> lists;

    @SafeVarargs
    public static <T> ConcatenatedListView<T> of(List<T> ... lists) {
        return new ConcatenatedListView<T>(List.of(lists));
    }

    public static <T> List<T> of(List<? extends List<? extends T>> members) {
        if (members.isEmpty()) {
            return List.of();
        }
        if (members.size() == 1) {
            return Collections.unmodifiableList(members.get(0));
        }
        List<List<T>> typed = members;
        return new ConcatenatedListView<T>(typed);
    }

    private ConcatenatedListView(List<List<T>> lists) {
        this.lists = lists;
    }

    @Override
    public int size() {
        int size = 0;
        for (List<T> list : this.lists) {
            size += list.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (List<T> list : this.lists) {
            if (list.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        for (List<T> list : this.lists) {
            if (!list.contains(o)) continue;
            return true;
        }
        return false;
    }

    @Override
    public T get(int index) {
        for (List<T> list : this.lists) {
            int size = list.size();
            if (index < size) {
                return list.get(index);
            }
            index -= size;
        }
        throw new IndexOutOfBoundsException(index);
    }

    @Override
    public int indexOf(Object o) {
        int offset = 0;
        for (List<T> list : this.lists) {
            int foundIndex = list.indexOf(o);
            if (foundIndex >= 0) {
                return offset + foundIndex;
            }
            offset += list.size();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int offset = 0;
        for (List list : Lists.reverse(this.lists)) {
            int foundIndex = list.lastIndexOf(o);
            if (foundIndex >= 0) {
                return offset + foundIndex;
            }
            offset += list.size();
        }
        return -1;
    }

    @Override
    @NotNull
    public Iterator<T> iterator() {
        return Iterables.unmodifiableIterable((Iterable)Iterables.concat(this.lists)).iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return Iterables.unmodifiableIterable((Iterable)Iterables.concat(this.lists)).spliterator();
    }

    private <C extends Collection<T>> C concatenate(Supplier<C> collectionFactory) {
        Collection concat = (Collection)collectionFactory.get();
        for (List<T> list : this.lists) {
            concat.addAll(list);
        }
        return (C)concat;
    }

    @Override
    @NotNull
    public Object[] toArray() {
        return this.concatenate(ArrayList::new).toArray();
    }

    @Override
    @NotNull
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return this.concatenate(ArrayList::new).toArray(a);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.concatenate(HashSet::new).containsAll(c);
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }
}

