package net.emilla.command;

import net.emilla.lang.Words;
import net.emilla.util.trie.TrieMap;

public final class ActionYielder<A extends Enum<A>> implements TrieMap.Value<ActionYielder<A>> {

    public final A action;
    private final boolean mUsesInstruction;

    public ActionYielder(A action, boolean usesInstruction) {
        this.action = action;
        mUsesInstruction = usesInstruction;
    }

    @Override
    public boolean isPrefixable() {
        return mUsesInstruction;
    }

    @Override
    public ActionYielder<A> duplicate(ActionYielder<A> discarded) {
        return this;
    }

    public Subcommand<A> action(Words words) {
        return new Subcommand<>(action, words.remainingContents());
    }
}
