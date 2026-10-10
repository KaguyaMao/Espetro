/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.common.util;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class RecipeMatcher {
    public static <T> int[] findMatches(List<T> inputs, List<? extends Predicate<T>> tests) {
        int elements = inputs.size();
        if (elements != tests.size()) {
            return null;
        }
        int[] ret = new int[elements];
        for (int x = 0; x < elements; ++x) {
            ret[x] = -1;
        }
        BitSet data = new BitSet((elements + 2) * elements);
        for (int x = 0; x < elements; ++x) {
            int matched = 0;
            int offset = (x + 2) * elements;
            Predicate<T> test = tests.get(x);
            for (int y = 0; y < elements; ++y) {
                if (data.get(y) || !test.test(inputs.get(y))) continue;
                data.set(offset + y);
                ++matched;
            }
            if (matched == 0) {
                return null;
            }
            if (matched != true || RecipeMatcher.claim(ret, data, x, elements)) continue;
            return null;
        }
        if (data.nextClearBit(0) >= elements) {
            return ret;
        }
        if (RecipeMatcher.backtrack(data, ret, 0, elements)) {
            return ret;
        }
        return null;
    }

    private static boolean claim(int[] ret, BitSet data, int claimed, int elements) {
        LinkedList<Integer> pending = new LinkedList<Integer>();
        pending.add(claimed);
        while (pending.peek() != null) {
            int test = (Integer)pending.poll();
            int offset = (test + 2) * elements;
            int used = data.nextSetBit(offset) - offset;
            if (used >= elements || used < 0) {
                throw new IllegalStateException("What? We matched something, but it wasn't set in the range of this test! Test: " + test + " Used: " + used);
            }
            data.set(used);
            data.set(elements + test);
            ret[used] = test;
            for (int x = 0; x < elements; ++x) {
                offset = (x + 2) * elements;
                if (!data.get(offset + used) || data.get(elements + x)) continue;
                data.clear(offset + used);
                int count = 0;
                for (int y = offset; y < offset + elements; ++y) {
                    if (!data.get(y)) continue;
                    ++count;
                }
                if (count == 0) {
                    return false;
                }
                if (count != 1) continue;
                pending.add(x);
            }
        }
        return true;
    }

    private static boolean backtrack(BitSet data, int[] ret, int start, int elements) {
        int test = data.nextClearBit(elements + start) - elements;
        if (test >= elements) {
            return true;
        }
        if (test < 0) {
            throw new IllegalStateException("This should never happen, negative test in backtrack!");
        }
        int offset = (test + 2) * elements;
        for (int x = 0; x < elements; ++x) {
            if (!data.get(offset + x) || data.get(x)) continue;
            data.set(x);
            if (RecipeMatcher.backtrack(data, ret, test + 1, elements)) {
                ret[x] = test;
                return true;
            }
            data.clear(x);
        }
        return false;
    }
}

