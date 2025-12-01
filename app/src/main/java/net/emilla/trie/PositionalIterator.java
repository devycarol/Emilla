package net.emilla.trie;

import androidx.annotation.Nullable;

import java.util.Iterator;

public interface PositionalIterator<E, L> extends Iterator<E> {
    int position();
    @Nullable
    L leftoversFrom(int position);
}
