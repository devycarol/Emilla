package net.emilla.command;

import net.emilla.activity.AssistActivity;
import net.emilla.trie.TrieMap;

import java.util.ArrayList;
import java.util.Iterator;

public final class DuplicateYielder extends CommandYielder implements TrieMap.Duplicate<CommandYielder> {

    private final ArrayList<CommandYielder> mYielders = new ArrayList<>(2);
    private boolean mUsesInstruction = false;
    private boolean mPruned = true;

    private DuplicateCommand mCommand = null;

    public DuplicateYielder(CommandYielder a, CommandYielder b) {
        addYielders(a);
        addYielders(b);
    }

    private void addYielders(CommandYielder yielder) {
        if (yielder instanceof DuplicateYielder dupeYielder) {
            for (CommandYielder dupe : dupeYielder.mYielders) {
                mUsesInstruction = mUsesInstruction || dupe.isPrefixable();
                mPruned = mPruned && dupe.isPrefixable();
            }
            mYielders.addAll(dupeYielder.mYielders);
        } else {
            mUsesInstruction = mUsesInstruction || yielder.isPrefixable();
            mPruned = mPruned && yielder.isPrefixable();
            mYielders.add(yielder);
        }
    }

    @Override
    public boolean isPrefixable() {
        return mUsesInstruction;
    }

    @Override
    public CommandYielder prune() {
        if (mPruned) return this;

        for (int i = mYielders.size() - 1; i >= 0 ; --i) {
            if (!mYielders.get(i).isPrefixable()) {
                mYielders.remove(i);
                --i;
            }
        }

        mUsesInstruction = true;
        if (mYielders.size() == 1) return mYielders.get(0);
        // this method should never result in mYielders becoming an empty list.

        mPruned = true;
        return this;
    }

    @Override
    protected EmillaCommand makeCommand(AssistActivity act) {
        if (mCommand == null) {
            int dupeCount = mYielders.size();
            var cmds = new EmillaCommand[dupeCount];
            for (int i = 0; i < dupeCount; ++i) {
                cmds[i] = mYielders.get(i).makeCommand(act);
            }
            mCommand = new DuplicateCommand(act, cmds);
        }
        return mCommand;
    }

    @Override
    public Iterator<CommandYielder> iterator() {
        return mYielders.iterator();
    }

}
