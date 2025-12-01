package net.emilla.trie;

/*internal*/ final class PrefixValue<V> {

    public final V value;
    public final boolean takesLeftovers;

    /*internal*/ PrefixValue(V value, boolean takesLeftovers) {
        this.value = value;
        this.takesLeftovers = takesLeftovers;
    }

}
