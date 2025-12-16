package net.emilla.trie;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.Stream;

final class PrefixNode<K, V> {

    private final IntFunction<V[]> mArrayGenerator;

    @Nullable
    private PrefixValue<V>[] mValues = null;
    @Nullable
    private Map<K, PrefixNode<K, V>> mChildren = null;

    /*internal*/ PrefixNode(IntFunction<V[]> arrayGenerator) {
        mArrayGenerator = arrayGenerator;
    }

    public PrefixNode<K, V> createChild(K key) {
        if (mChildren == null) {
            mChildren = new HashMap<K, PrefixNode<K, V>>(1);
        }

        var child = new PrefixNode<K, V>(mArrayGenerator);
        mChildren.put(key, child);

        return child;
    }

    @Nullable
    public PrefixNode<K, V> child(K key) {
        if (mChildren == null) {
            return null;
        }
        return mChildren.get(key);
    }

    @SuppressWarnings("unchecked")
    public void add(V value, boolean takesLeftovers) {
        if (mValues == null) {
            mValues = (PrefixValue<V>[]) new PrefixValue<?>[1];
            mValues[0] = new PrefixValue<V>(value, takesLeftovers);
        } else {
            int valueCount = mValues.length;
            mValues = Arrays.copyOf(mValues, valueCount + 1);
            mValues[valueCount] = new PrefixValue<V>(value, takesLeftovers);
        }
    }

    @Nullable
    public V[] values(boolean contentsRemain) {
        if (mValues == null) {
            return null;
        }

        var valueStream = Stream.<PrefixValue<V>>of(mValues);
        if (contentsRemain) {
            valueStream = valueStream.filter(value -> value.takesLeftovers);
        }

        V[] values = valueStream
            .map(value -> value.value)
            .toArray(mArrayGenerator);

        return values.length > 0
            ? values
            : null;
    }

}
