package net.emilla.util.trie;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*internal*/ abstract class TrieNode<K, V extends TrieMap.Value<V>> {

    V val;
    private Map<K, TrieNode<K, V>> children;

    final Map<K, TrieNode<K, V>> children() {
        return children == null ? children = newMap() : children;
    }

    final boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    abstract Map<K, TrieNode<K, V>> newMap();

    final boolean canYieldValue(Iterator<K> iterator) {
        return val != null && (val.isPrefixable() || !iterator.hasNext());
    }

    final V restrainedValue(Iterator<K> iterator) {
        if (val instanceof TrieMap.Duplicate<?> dupeVal && iterator.hasNext()) {
            return (V) dupeVal.prune();
        }
        return val;
    }

    /**
     * A list of all values under this node. Duplicates are split into their individual values.
     *
     * @return All the node's child values, including its own value, in depth-first order.
     */
    final List<V> values() {
        return valuesRec(this, new ArrayList<>(children.size() + 1));
    }

    private static <K, V extends TrieMap.Value<V>> List<V> valuesRec(
        TrieNode<K, V> node,
        List<V> values
    ) {
        if (node.hasChildren()) {
            if (node.val != null) addValues(values, node.val);
            for (TrieNode<K, V> next : node.children.values()) {
                valuesRec(next, values);
            }
        } else addValues(values, node.val);
        // a leaf node always has a value.

        return values;
    }

    private static <V extends TrieMap.Value<V>> void addValues(List<V> values, V value) {
        if (value instanceof TrieMap.Duplicate<?>) {
            for (V dupe : (TrieMap.Duplicate<V>) value) {
                values.add(dupe);
            }
        } else values.add(value);
    }
}

/**
 * <p>
 * A prefix tree for storing values mapped by multi-item keys called 'phrases'.</p>
 * <p>
 * This data structure is suitable for efficiently determining if a value starts with any of a large
 * set of arbitrary-length prefixes, such as command names.</p>
 *
 * @param <K> the type yielded by the 'phrase' construct used to create unique value entries.
 * @param <V> the type to be stored.
 */
public abstract class TrieMap<K, V extends TrieMap.Value<V>> {

    /**
     * <p>
     * A multi-item key used to map values in the trie.</p>
     * <p>
     * A "phrase" should be iterable for the trie's 'key' type and capable of using its iterator
     * construct to determine contents remaining in the phrase after a prefix value is retrieved.</p>
     *
     * @param <K> the key-type used to create value entries in the trie.
     * @param <R> the type used for yielding the phrase's remaining contents upon the retrieval of a
     *            prefix value.
     */
    public interface Phrase<K, R> extends Iterable<K> {

        /**
         * Sets the internal position of the phrase using its iterator, allowing it to designate the
         * iterator's remaining contents as items to be used after the prefix.
         *
         * @param iterator the phrase's iterator after being used for prefix retrieval in the trie.
         */
        void setPosition(Iterator<K> iterator);

        /**
         * <p>
         * Whether the phrase has additional contents beyond the prefix found by
         * {@link TrieMap#get(Phrase)}.</p>
         * <p>
         * This method is made usable by the {@code setPosition()} method.</p>
         *
         * @return true if the phrase has remaining contents after the prefix, false otherwise.
         */
        boolean hasRemainingContents();

        /**
         * <p>
         * The contents remaining in the phrase after the prefix found by {@link TrieMap#get(Phrase)}.</p>
         * <p>
         * This method is made usable by the {@code setPosition()} method.</p>
         *
         * @return the phrase's contents following the prefix.
         */
        R remainingContents();
    }

    /**
     * <p>
     * A value that can be stored in a TrieMap needs two properties:</p>
     * <ol>
     *   <li>Whether it can be a prefix in wherever context it's in,</li>
     *   <li>Handling of duplicate entries.</li>
     * </ol>
     * <p>
     * An example of a prefixable value is a command that takes instruction input, while an example
     * of a non-prefixable value is a command that takes no input and only performs a single action.</p>
     * <p>
     * Values need to determine whether they need to support disambiguation of duplicate phrase
     * entries. If they don't, they can simply return the self or the other value in the
     * {@code duplicate()} method. Otherwise, the {@code duplicate()} method should return an item
     * container to be disambiguated upon retrieval from the trie.</p>
     *
     * @param <V>
     */
    public interface Value<V extends Value<V>> {

        /**
         * <p>
         * Whether this value is applicable as a prefix in its context of use.</p>
         * <p>
         * For instance, a website with search functionality is helpful as a prefix whereas a
         * website without search functionality is not.</p>
         *
         * @return true if this value can be used as a prefix, false if it can't.
         */
        boolean isPrefixable();
        /**
         * <p>
         * The value to put in the trie when attempting to add another with the same phrase key.</p>
         * <p>
         * This can be an item container for disambiguation, the self for preventing overwrites, or
         * the other value simply replace values.</p>
         *
         * @param value duplicate value trying to be inserted in the trie.
         * @return value to put in the trie when a phrase collision occurs.
         */
        V duplicate(V value);
    }

    /**
     * <p>
     * A container for duplicate trie values. The user can expect assistance disambiguating the
     * duplicate entries down the line.</p>
     * <p>
     * It's optional to use duplicate containers in the TrieMap, and you may instead opt to have
     * values yield themself in their {@code duplicate()} method. This will have the effect of
     * preventing duplicate inserts altogether.</p>
     *
     * @param <V> the value type stored in this container.
     */
    public interface Duplicate<V extends Value<V>> extends Value<V>, Iterable<V> {

        /**
         * Removes any duplicate entries that can't be prefixes.
         *
         * @return the duplicate container without any non-prefixable values, or a standalone value
         *         if there was just one prefixable value.
         */
        V prune();
    }

    final TrieNode<K, V> root = newNode();

    abstract TrieNode<K, V> newNode();

    /**
     * <p>
     * Inserts {@code value} into the trie with {@code phrase} as its prefix key.</p>
     * <p>
     * If the phrase key already exists in the trie, the value's 'duplicate' behavior will be
     * invoked for the entry value. Whether the value type actually supports disambiguation is
     * determined by its {@code duplicate()} method.</p>
     *
     * @param phrase the value's prefix key, whose values will be used for retrieval.
     * @param value the corresponding value to put in the trie.
     */
    public final void put(Phrase<K, ?> phrase, V value) {
        TrieNode<K, V> current = root;

        for (K item : phrase) {
            Map<K, TrieNode<K, V>> children = current.children();

            TrieNode<K, V> get = children.get(item);
            if (get == null) children.put(item, current = newNode());
            else current = get;
        }

        if (current.val == null) current.val = value;
        else current.val = current.val.duplicate(value);
    }

    /**
     * <p>
     * Retrieves the value associated with longest entry in the trie that prefixes {@code phrase},
     * or {@code null} if no such value exists.</p>
     * <p>
     * This will <i>not</i> yield a value designated as non-prefixable unless the given phrase is an
     * exact match for the entry.</p>
     *
     * @param phrase may be modified by this method! Generally, the phrase's {@code setPosition()}
     *               method will be called any time a constituent prefix is identified in the trie.
     *               This is to notify the phrase of contents following the prefix, such as a
     *               command instruction.
     * @return the value associated with the longest prefix of {@code phrase} contained within the
     *         trie, or {@code null} if no such prefix was found.
     */
    @Nullable
    public final V get(Phrase<K, ?> phrase) {
        TrieNode<K, V> current = root;
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

    /**
     * Retrieves the value associated with {@code phrase}, or {@code null} if no such value exists.
     *
     * @param phrase phrase to query the trie for a mapped value.
     * @return the value associated with {@code phrase}, or {@code null} if no value is found.
     */
    @Nullable
    public final V getExact(Phrase<K, ?> phrase) {
        TrieNode<K, V> current = root;
        for (K item : phrase) {
            TrieNode<K, V> get = current.children().get(item);
            if (get == null) return null;
            current = get;
        }
        return current.val;
    }

    /**
     * Sometimes, values in the trie will be shadowed by others that start with their item sequence.
     * This method tells you how many of the trie's values exist as prefixes for the given phrase.
     * Duplicate value entries are not counted more than once.
     *
     * @param phrase is evaluated for the number of trie values it has as prefixes.
     * @return the count of the trie's values existing in `phrase` as nested prefixes.
     */
    public final int count(Phrase<K, ?> phrase) {
        TrieNode<K, V> current = root;
        int count = 0;

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

    /**
     * <p>
     * Whether the trie contains the given phrase as a prefix.</p>
     * <p>
     * Note that this doesn't necessarily mean the prefix is directly associated with a value, only
     * that an entry starts with the phrase.</p>
     *
     * @param prefix phrase to determine if the trie contains it as a prefix.
     * @return true if the prefix is part of an entry in the trie, false otherwise.
     */
    public final boolean containsPrefix(Phrase<K, ?> prefix) {
        TrieNode<K, V> current = root;
        for (K item : prefix) {
            TrieNode<K, V> get = current.children().get(item);
            if (get == null) return false;
            current = get;
        }
        return true;
    }
}
