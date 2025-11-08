package net.emilla.command.core;

import net.emilla.activity.AssistActivity;
import net.emilla.command.CommandYielder;
import net.emilla.command.EmillaCommand;

/*internal*/ final class CoreYielder extends CommandYielder {

    private final CoreEntry mCoreEntry;

    /*internal*/ CoreYielder(CoreEntry coreEntry) {
        mCoreEntry = coreEntry;
    }

    @Override
    public boolean isPrefixable() {
        return mCoreEntry.usesInstruction;
    }

    @Override
    protected EmillaCommand makeCommand(AssistActivity act) {
        return mCoreEntry.mMaker.make(act);
    }

}
