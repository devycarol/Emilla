package net.emilla.util;

@FunctionalInterface
public interface Searchable<T extends Searchable<T>> extends Comparable<T> {

    String ordinal();

    @Override
    default int compareTo(T that) {
        return this.ordinal().compareToIgnoreCase(that.ordinal());
    }

    default boolean ordinalContains(String search) {
        return Strings.containsIgnoreCase(ordinal(), search);
    }
}
