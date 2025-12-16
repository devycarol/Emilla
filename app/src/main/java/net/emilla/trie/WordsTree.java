package net.emilla.trie;

import java.util.function.IntFunction;

final class WordsTree<V> implements PhraseTree<V> {

    private final PrefixTree<String, V> mPrefixTree;

    /*internal*/ WordsTree(IntFunction<V[]> arrayGenerator) {
        mPrefixTree = new PrefixTree<String, V>(arrayGenerator);
    }

    @Override
    public void put(String phrase, V value, boolean takesLeftovers) {
        mPrefixTree.put(new Words(phrase), value, takesLeftovers);
    }

    @Override
    public PrefixResult<V, String> get(String phrase) {
        return mPrefixTree.get(new Words(phrase));
    }

}
