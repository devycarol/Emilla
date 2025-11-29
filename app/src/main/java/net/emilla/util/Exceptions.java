package net.emilla.util;

public final class Exceptions {

    public static IndexOutOfBoundsException iob(int index) {
        return new IndexOutOfBoundsException("Index out of range: " + index);
    }

    private Exceptions() {}

}
