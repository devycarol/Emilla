package net.emilla.util;

public enum Exceptions {
    ;

    public static IndexOutOfBoundsException iob(int index) {
        return new IndexOutOfBoundsException("Index out of range: " + index);
    }

}
