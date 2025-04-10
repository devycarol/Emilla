package net.emilla.command;

import android.content.res.Resources;

import androidx.annotation.ArrayRes;

import net.emilla.lang.Lang;
import net.emilla.lang.Words;
import net.emilla.struct.trie.HashTrieMap;
import net.emilla.struct.trie.TrieMap;

public final class ActionMap<A extends Enum<A>> {

    private final TrieMap<String, ActionYielder<A>> mTrieMap = new HashTrieMap<>();
    private final A mDefaultAction;

    public ActionMap(A defaultAction) {
        mDefaultAction = defaultAction;
    }

    public void put(Resources res, A action, @ArrayRes int names, boolean usesInstruction) {
        var yielder = new ActionYielder<>(action, usesInstruction);
        for (String name : res.getStringArray(names)) {
            mTrieMap.put(Lang.words(name), yielder);
        }
    }

    public Subcommand<A> get(String instruction) {
        Words words = Lang.words(instruction);
        ActionYielder<A> get = mTrieMap.get(words);
        return get == null ? new Subcommand<>(mDefaultAction, instruction) : get.action(words);
    }
}
