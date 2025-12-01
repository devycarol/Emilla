package net.emilla.trie;

import java.util.function.IntFunction;

public sealed interface PhraseTree<V> permits WordsTree, GlyphsTree {

    void put(String phrase, V value, boolean takesLeftovers);
    PrefixResult<V, String> get(String phrase);

    static <V> PhraseTree<V> of(
        boolean wordsAreSpaceSeparated,
        IntFunction<V[]> arrayGenerator
    ) {
        return wordsAreSpaceSeparated
            ? new WordsTree<V>(arrayGenerator)
            : new GlyphsTree<V>(arrayGenerator);
    }

}
