package net.emilla.struct.trie;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*internal*/ abstract class TrieNode<K, V extends TrieMap.Value<V>> {

    /*internal*/ V mVal = null;
    private Map<K, TrieNode<K, V>> mChildren = null;

    final Map<K, TrieNode<K, V>> children() {
        return mChildren == null ? mChildren = newMap() : mChildren;
    }

    final boolean hasChildren() {
        return mChildren != null && !mChildren.isEmpty();
    }

    abstract Map<K, TrieNode<K, V>> newMap();

    final boolean canYieldValue(Iterator<K> iterator) {
        return mVal != null && (mVal.isPrefixable() || !iterator.hasNext());
    }

    final V restrainedValue(Iterator<K> iterator) {
        if (mVal instanceof TrieMap.Duplicate<?> dupeVal && iterator.hasNext()) {
            return (V) dupeVal.prune();
        }
        return mVal;
    }

    /// A list of all values under this node. Duplicates are split into their individual values.
    ///
    /// @return All the node's child values, including its own value, in depth-first order.
    final List<V> values() {
        return valuesRec(this, new ArrayList<V>(mChildren.size() + 1));
    }

    private static <K, V extends TrieMap.Value<V>> List<V> valuesRec(
        TrieNode<K, V> node,
        List<V> values
    ) {
        if (node.hasChildren()) {
            if (node.mVal != null) {
                addValues(values, node.mVal);
            }
            for (TrieNode<K, V> next : node.mChildren.values()) {
                valuesRec(next, values);
            }
        } else {
            addValues(values, node.mVal);
        }
        // a leaf node always has a value.

        return values;
    }

    private static <V extends TrieMap.Value<V>> void addValues(List<V> values, V value) {
        if (value instanceof TrieMap.Duplicate<?>) {
            for (V dupe : (TrieMap.Duplicate<V>) value) {
                values.add(dupe);
            }
        } else {
            values.add(value);
        }
    }
}

/// A prefix tree for storing values mapped by multi-item keys called 'phrases'.
///
/// This data structure is suitable for efficiently determining if a value starts with any of a large
/// set of arbitrary-length prefixes, such as command names.
///
/// @param <K> the type yielded by the 'phrase' construct used to create unique value entries.
/// @param <V> the type to be stored.
public abstract class TrieMap<K, V extends TrieMap.Value<V>> {

    /// A multi-item key used to map values in the trie.
    ///
    /// A "phrase" should be iterable for the trie's 'key' type and capable of using its iterator
    /// construct to determine contents remaining in the phrase after a prefix value is retrieved.
    ///
    /// @param <K> the key-type used to create value entries in the trie.
    /// @param <R> the type used for yielding the phrase's remaining contents upon the retrieval of
    /// a prefix value.
    public interface Phrase<K, R> extends Iterable<K> {

        /// Sets the internal position of the phrase using its iterator, allowing it to designate
        /// the iterator's remaining contents as items to be used after the prefix.
        ///
        /// @param iterator the phrase's iterator after being used for prefix retrieval in the trie.
        void setPosition(Iterator<K> iterator);

        /// Whether the phrase has additional contents beyond the prefix found by [#get(Phrase)].
        ///
        /// This method is made usable by the `setPosition()` method.
        ///
        /// @return true if the phrase has remaining contents after the prefix, false otherwise.
        boolean hasRemainingContents();

        /// The contents remaining in the phrase after the prefix found by [#get(Phrase)].
        ///
        /// This method is made usable by the `setPosition()` method.
        ///
        /// @return the phrase's contents following the prefix.
        R remainingContents();
    }

    /// A value that can be stored in a TrieMap needs two properties:
    /// 1. Whether it can be a prefix.
    /// 2. Handling of duplicate entries.
    ///
    /// An example of a prefixable value is a command that takes instruction input, while an example
    /// of a non-prefixable value is a command that takes no input and only performs a single action.
    ///
    /// Values need to determine whether they need to support disambiguation of duplicate phrase
    /// entries. If they don't, they can simply return the self or the other value in the
    /// `duplicate()` method. Otherwise, the `duplicate()` method should return an item container to
    /// be disambiguated upon retrieval from the trie.
    ///
    /// @param <V>
    public interface Value<V extends Value<V>> {

        /// Whether this value is applicable as a prefix in its context of use.
        ///
        /// For instance, a website with search functionality is helpful as a prefix whereas a
        /// website without search functionality is not.
        ///
        /// @return true if this value can be used as a prefix, false if it can't.
        boolean isPrefixable();
        /// The value to put in the trie when attempting to add another with the same phrase key.
        ///
        /// This can be an item container for disambiguation, the self for preventing overwrites, or
        /// the other value simply replace values.
        ///
        /// @param value duplicate value trying to be inserted in the trie.
        /// @return value to put in the trie when a phrase collision occurs.
        V duplicate(V value);
    }

    /// A container for duplicate trie values. The user can expect assistance disambiguating the
    /// duplicate entries down the line.
    ///
    /// It's optional to use duplicate containers in the TrieMap, and you may instead opt to have
    /// values yield themself in their `duplicate()` method. This will have the effect of preventing
    /// duplicate inserts altogether.
    ///
    /// @param <V> the value type stored in this container.
    public interface Duplicate<V extends Value<V>> extends Value<V>, Iterable<V> {

        /// Removes any duplicate entries that can't be prefixes.
        ///
        /// @return the duplicate container without any non-prefixable values, or a standalone value
        /// if there was just one prefixable value.
        V prune();
    }

    /*internal*/ final TrieNode<K, V> mRoot = newNode();

    abstract TrieNode<K, V> newNode();

    /// Inserts `value` into the trie with `phrase` as its prefix key.
    ///
    /// If the phrase key already exists in the trie, the value's 'duplicate' behavior will be
    /// invoked for the entry value. Whether the value type actually supports disambiguation is
    /// determined by its `duplicate()` method.
    ///
    /// @param phrase the value's prefix key, whose values will be used for retrieval.
    /// @param value the corresponding value to put in the trie.
    public final void put(Phrase<? extends K, ?> phrase, V value) {
        TrieNode<K, V> current = mRoot;

        for (K item : phrase) {
            Map<K, TrieNode<K, V>> children = current.children();

            TrieNode<K, V> get = children.get(item);
            if (get == null) {
                children.put(item, current = newNode());
            } else {
                current = get;
            }
        }

        if (current.mVal == null) {
            current.mVal = value;
        } else {
            current.mVal = current.mVal.duplicate(value);
        }
    }

    /// Retrieves the value associated with longest entry in the trie that prefixes `phrase`, or
    /// `null` if no such value exists.
    ///
    /// This will *not* yield a value designated as non-prefixable unless the given phrase is an
    /// exact match for the entry.
    ///
    /// @param phrase may be modified by this method! Generally, the phrase's `setPosition()` method
    /// will be called any time a constituent prefix is identified in the trie. This is to notify
    /// the phrase of contents following the prefix, such as a command instruction.
    /// @return the value associated with the longest prefix of `phrase` contained within the true,
    /// or `null` if no such prefix was found.
    @Nullable
    public final V get(Phrase<K, ?> phrase) {
        TrieNode<K, V> current = mRoot;
        V currentVal = null;

        Iterator<K> iterator = phrase.iterator();
        while (iterator.hasNext()) {
            K item = iterator.next();

            TrieNode<K, V> get = current.children().get(item);
            if (get == null) break;
            current = get;
            if (current.canYieldValue(iterator)) {
                currentVal = current.restrainedValue(iterator);
                phrase.setPosition(iterator);
            }
        }

        if (currentVal instanceof Duplicate<?> dupeVal && phrase.hasRemainingContents()) {
            return (V) dupeVal.prune();
        }
        return currentVal;
    }

    /// Retrieves the value associated with `phrase`, or `null` if no such value exists.
    ///
    /// @param phrase phrase to query the trie for a mapped value.
    /// @return the value associated with `phrase`, or `null` if no value is found.
    @Nullable
    public final V getExact(Phrase<? extends K, ?> phrase) {
        TrieNode<K, V> current = mRoot;
        for (K item : phrase) {
            TrieNode<K, V> get = current.children().get(item);
            if (get == null) return null;
            current = get;
        }
        return current.mVal;
    }

    /// Sometimes, values in the trie will be shadowed by others that start with their item sequence.
    /// This method tells you how many of the trie's values exist as prefixes for the given phrase.
    /// Duplicate value entries are not counted more than once.
    ///
    /// @param phrase is evaluated for the number of trie values it has as prefixes.
    /// @return the count of the trie's values existing in `phrase` as nested prefixes.
    public final int count(Phrase<K, ?> phrase) {
        int count = 0;

        TrieNode<K, V> current = mRoot;

        Iterator<K> iterator = phrase.iterator();
        while (iterator.hasNext()) {
            K item = iterator.next();

            TrieNode<K, V> get = current.children().get(item);
            if (get == null) return count;
            current = get;

            if (current.canYieldValue(iterator)) ++count;
        }

        return count;
    }

    /// Whether the trie contains the given phrase as a prefix.
    ///
    /// Note that this doesn't necessarily mean the prefix is directly associated with a value, only
    /// that an entry starts with the phrase.
    ///
    /// @param prefix phrase to determine if the trie contains it as a prefix.
    /// @return true if the prefix is part of an entry in the trie, false otherwise.
    public final boolean containsPrefix(Phrase<? extends K, ?> prefix) {
        TrieNode<K, V> current = mRoot;
        for (K item : prefix) {
            TrieNode<K, V> get = current.children().get(item);
            if (get == null) return false;
            current = get;
        }
        return true;
    }
}
