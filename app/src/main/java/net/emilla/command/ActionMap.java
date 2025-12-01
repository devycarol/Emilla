package net.emilla.command;

import android.content.res.Resources;

import androidx.annotation.ArrayRes;

import net.emilla.lang.Lang;
import net.emilla.trie.PhraseTree;
import net.emilla.trie.PrefixResult;

import java.util.function.IntFunction;

public final class ActionMap<A extends Enum<A>> {

    private final PhraseTree<A> mPhraseTree;
    private final A mDefaultAction;

    public ActionMap(Resources res, A defaultAction, IntFunction<A[]> arrayGenerator) {
        mPhraseTree = Lang.phraseTree(res, arrayGenerator);
        mDefaultAction = defaultAction;
    }

    public void put(Resources res, A action, @ArrayRes int names, boolean usesInstruction) {
        for (String name : res.getStringArray(names)) {
            mPhraseTree.put(name, action, usesInstruction);
        }
    }

    public Subcommand<A> get(String instruction) {
        PrefixResult<A, String> get = mPhraseTree.get(instruction);
        A action = get.value();

        return action != null
            ? new Subcommand<A>(action, get.leftovers)
            : new Subcommand<A>(mDefaultAction, instruction);
    }

}
