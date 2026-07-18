package net.emilla.util;

import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public enum ArrayHelp {;
    public static <E> int[] mapToInt(E[] array, ToIntFunction<E> mapper) {
        var map = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            map[i] = mapper.applyAsInt(array[i]);
        }
        return map;
    }

    public static <E> E[] map(
        int[] array,
        IntFunction<E> mapper,
        IntFunction<E[]> generator
    ) {
        var map = generator.apply(array.length);
        for (int i = 0; i < array.length; ++i) {
            map[i] = mapper.apply(array[i]);
        }
        return map;
    }
}
