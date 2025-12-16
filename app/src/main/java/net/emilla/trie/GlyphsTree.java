package net.emilla.trie;

import java.util.function.IntFunction;

final class GlyphsTree<V> implements PhraseTree<V> {

    private final PrefixTree<Integer, V> mPrefixTree;

    /*internal*/ GlyphsTree(IntFunction<V[]> arrayGenerator) {
        mPrefixTree = new PrefixTree<Integer, V>(arrayGenerator);
    }

    @Override
    public void put(String phrase, V value, boolean takesLeftovers) {
        mPrefixTree.put(new Glyphs(phrase), value, takesLeftovers);
    }

    @Override
    public PrefixResult<V, String> get(String phrase) {
        return mPrefixTree.get(new Glyphs(phrase));
    }

}
