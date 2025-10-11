package net.emilla.struct.trie;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class SortedTrieMap<K extends Comparable<K>, V extends TrieMap.Value<V>>
    extends TrieMap<K, V> {

    private static final class SortedTrieNode<K extends Comparable<K>, V extends Value<V>>
        extends TrieNode<K, V> {

        @Override
        Map<K, TrieNode<K, V>> newMap() {
            return new TreeMap<K, TrieNode<K, V>>();
        }
    }

    @Override
    TrieNode<K, V> newNode() {
        return new SortedTrieNode<K, V>();
    }

    /// Sorted list of elements that start with a given prefix.
    ///
    /// @param prefix phrase to get prefixed values of.
    /// @return list of elements that start with `prefix` in sorted depth-first order, or `null` if
    /// no such values exist.
    @Nullable
    public List<V> elementsWithPrefix(Phrase<K, ?> prefix) {
        TrieNode<K, V> current = mRoot;
        for (K item : prefix) {
            TrieNode<K, V> get = current.children().get(item);
            if (get == null) return null;
            current = get;
        }
        return current.values();
    }
}
