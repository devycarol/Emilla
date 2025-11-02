package net.emilla.struct.sort;

import net.emilla.util.Strings;

public interface Searchable<T extends Searchable<T>> extends Comparable<T> {

    String ordinal();

    @Override
    default int compareTo(T other) {
        return ordinal().compareToIgnoreCase(other.ordinal());
    }

    default boolean ordinalIs(String search) {
        return ordinal().equalsIgnoreCase(search);
    }

    default boolean ordinalContains(String search) {
        return Strings.containsIgnoreCase(ordinal(), search);
    }

}
