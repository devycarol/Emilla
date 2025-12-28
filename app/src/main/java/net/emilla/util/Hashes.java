package net.emilla.util;

public enum Hashes {
    ;

    public static int one(Object o) {
        return one(o.hashCode());
    }

    private static int one(int i) {
        return 0x1F + i;
    }

}
