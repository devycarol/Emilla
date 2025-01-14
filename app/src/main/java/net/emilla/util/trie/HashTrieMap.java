package net.emilla.util.trie;

import java.util.HashMap;
import java.util.Map;

public final class HashTrieMap<K, V extends TrieMap.Value<V>> extends TrieMap<K, V> {

    private static class HashTrieNode<K, V extends TrieMap.Value<V>> extends TrieNode<K, V> {

        @Override
        Map<K, TrieNode<K, V>> newMap() {
            return new HashMap<>();
        }
    }

    @Override
    TrieNode<K, V> newNode() {
        return new HashTrieNode<>();
    }
}
