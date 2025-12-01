package net.emilla.trie;

import java.util.Iterator;
import java.util.function.IntFunction;

/*internal*/ final class PrefixTree<K, V> {

    private final PrefixNode<K, V> mRoot;

    /*internal*/ PrefixTree(IntFunction<V[]> arrayGenerator) {
        mRoot = new PrefixNode<K, V>(arrayGenerator);
    }

    public void put(Iterator<K> keySequence, V value, boolean takesLeftovers) {
        PrefixNode<K, V> current = mRoot;

        while (keySequence.hasNext()) {
            K key = keySequence.next();

            PrefixNode<K, V> child = current.child(key);
            if (child == null) {
                child = current.createChild(key);
            }

            current = child;
        }

        current.add(value, takesLeftovers);
    }

    public <L> PrefixResult<V, L> get(PositionalIterator<K, L> keySequence) {
        PrefixNode<K, V> currentNode = mRoot;
        V[] values = null;
        int leftoverPosition = 0;

        boolean contentsRemain = keySequence.hasNext();
        while (contentsRemain) {
            K key = keySequence.next();
            contentsRemain = keySequence.hasNext();

            PrefixNode<K, V> child = currentNode.child(key);
            if (child == null) {
                break;
            }
            currentNode = child;

            V[] foundValues = currentNode.values(contentsRemain);
            if (foundValues != null) {
                values = foundValues;
                leftoverPosition = keySequence.position();
            }
        }

        L leftovers = keySequence.leftoversFrom(leftoverPosition);
        return new PrefixResult<V, L>(values, leftovers, leftoverPosition);
    }

}
