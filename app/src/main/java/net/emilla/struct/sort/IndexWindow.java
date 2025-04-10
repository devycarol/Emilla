package net.emilla.struct.sort;

public final class IndexWindow {

    public final int start;
    public final int last;
    public final int end;

    /*internal*/ IndexWindow(int start, int last) {
        this.start = start;
        this.last = last;
        this.end = last + 1;
    }

    public int size() {
        return end - start;
    }
}
