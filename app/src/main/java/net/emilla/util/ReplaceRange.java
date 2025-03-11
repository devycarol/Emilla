package net.emilla.util;

public record ReplaceRange(int start, int end) {

    public boolean singleItem() {
        return start == end;
    }
}
