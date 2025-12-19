package net.emilla.command;

import android.content.res.Resources;

import androidx.annotation.Nullable;

import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.lang.Lang;
import net.emilla.trie.PhraseTree;
import net.emilla.trie.PrefixResult;

public final class CommandMap {

    private final PhraseTree<CommandYielder> mPhraseTree;
    private final CommandYielder mDefaultYielder;

    @internal CommandMap(Resources res, CommandYielder defaultYielder) {
        mPhraseTree = Lang.phraseTree(res, CommandYielder[]::new);
        mDefaultYielder = defaultYielder;
    }

    @internal void put(String command, CommandYielder yielder) {
        mPhraseTree.put(command, yielder, yielder.usesInstruction());
    }

    @Nullable
    public EmillaCommand get(AssistActivity act, String fullCommand) {
        PrefixResult<CommandYielder, String> get = mPhraseTree.get(fullCommand);
        return PrefixResult.toEmillaCommand(act, get);
    }

    public EmillaCommand getDefault(AssistActivity act) {
        return mDefaultYielder.command(act);
    }

    public EmillaCommand getDefault(AssistActivity act, String fullCommand) {
        return mDefaultYielder.command(act, fullCommand);
    }

}
